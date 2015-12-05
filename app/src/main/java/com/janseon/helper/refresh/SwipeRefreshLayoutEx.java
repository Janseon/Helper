package com.janseon.helper.refresh;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.Transformation;


import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class SwipeRefreshLayoutEx extends android.support.v4.widget.SwipeRefreshLayout implements OnOverScrollListener {

	private static final float MAX_SWIPE_DISTANCE_FACTOR = .6f;
	private static final float ACCELERATE_INTERPOLATION_FACTOR = 1.5f;
	private static final int REFRESH_TRIGGER_DISTANCE = 160;

	private Class<android.support.v4.widget.SwipeRefreshLayout> cls = android.support.v4.widget.SwipeRefreshLayout.class;
	private Field mDistanceToTriggerSyncField;
	private Method setTriggerPercentageMethod;
	private Method updateContentOffsetTopMethod;

	private Method startRefreshMethod;

	private AccelerateInterpolator mAccelerateInterpolator = new AccelerateInterpolator(ACCELERATE_INTERPOLATION_FACTOR);
	private MotionEvent mDownEvent;
	private float mReturnDistance = -1;

	private Runnable mCancel;

	public SwipeRefreshLayoutEx(Context context, AttributeSet attrs) {
		super(context, attrs);
		try {
			Field field = cls.getDeclaredField("mCancel");
			field.setAccessible(true); // 抑制Java对修饰符的检查
			mCancel = (Runnable) field.get(this);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		try {
			mDistanceToTriggerSyncField = cls.getDeclaredField("mDistanceToTriggerSync");
			mDistanceToTriggerSyncField.setAccessible(true); // 抑制Java对修饰符的检查
			mDistanceToTriggerSyncField.set(this, Integer.MAX_VALUE);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		try {
			setTriggerPercentageMethod = cls.getDeclaredMethod("setTriggerPercentage", new Class[] { float.class });
			setTriggerPercentageMethod.setAccessible(true); // 抑制Java的访问控制检查

			updateContentOffsetTopMethod = cls.getDeclaredMethod("updateContentOffsetTop", new Class[] { int.class });
			updateContentOffsetTopMethod.setAccessible(true); // 抑制Java的访问控制检查

			startRefreshMethod = cls.getDeclaredMethod("startRefresh");
			startRefreshMethod.setAccessible(true); // 抑制Java的访问控制检查
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mDownEvent = MotionEvent.obtain(ev);
			break;
		}
		return super.onInterceptTouchEvent(ev);
	}

	@SuppressLint("Recycle")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (mReturnDistance == -1) {
			if (getParent() != null && ((View) getParent()).getHeight() > 0) {
				final DisplayMetrics metrics = getResources().getDisplayMetrics();
				mReturnDistance = (int) Math.min(((View) getParent()).getHeight() * MAX_SWIPE_DISTANCE_FACTOR, REFRESH_TRIGGER_DISTANCE * metrics.density);
			}
		}
		boolean handled = super.onTouchEvent(event);
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mDownEvent = MotionEvent.obtain(event);
			break;
		case MotionEvent.ACTION_MOVE:
			resetPercentage(event.getY() - mDownEvent.getY());
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			final float eventY = event.getY();
			float yDiff = eventY - mDownEvent.getY();
			if (startRefreshMethod != null && yDiff > mReturnDistance) {
				try {
					startRefreshMethod.invoke(this);
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			} else {
				mCancel.run();
			}
			break;
		}
		return handled;
	}

	private void resetPercentage(float yDiff) {
		if (mCancel != null) {
			removeCallbacks(mCancel);
		}
		if (setTriggerPercentageMethod != null) {
			try {
				float currPercentage = mAccelerateInterpolator.getInterpolation(yDiff / mReturnDistance);
				if (currPercentage > 1f) {
					currPercentage = 1f;
				}
				// DLog.i("onTopOverScroll", "currPercentage=" +
				// currPercentage);
				setTriggerPercentageMethod.invoke(this, currPercentage);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}

	private void reupdateContentOffsetTop(int yDiff) {
		if (updateContentOffsetTopMethod != null) {
			try {
				updateContentOffsetTopMethod.invoke(this, yDiff);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onTopOverScroll(int maxOverScrollY) {
		// DLog.i("onTopOverScroll", "maxOverScrollY=" + maxOverScrollY);
		if (!animationRunning && maxOverScrollY > 0) {
			int yDiff = Math.max(maxOverScrollY, REFRESH_TRIGGER_DISTANCE / 3);
			yDiff = Math.min(yDiff, REFRESH_TRIGGER_DISTANCE * 2);
			mAnimateFormTo.setFromTo(0, yDiff);
		}
		//
		// resetPercentage(yDiff);
		// reupdateContentOffsetTop(yDiff);
	}

	private static final int Duration = 350;
	AnimateFromTo mAnimateFormTo = new AnimateFromTo();
	boolean animationRunning = false;
	Interpolator mInterpolator = new DecelerateAccelerateInterpolator();

	class AnimateFromTo extends Animation {
		int mFrom;
		int mTo;
		View mTarget;

		AnimateFromTo() {
			super();
			setDuration(Duration);
			//
		}

		AnimateFromTo(int from, int to) {
			super();
			setDuration(300);
			setFromTo(from, to);
		}

		void setFromTo(int from, int to) {
			if (mTarget == null && getChildCount() > 0) {
				mTarget = getChildAt(0);
			}
			if (mTarget == null) {
				return;
			}
			mFrom = from;
			mTo = to;
			animationRunning = true;
			setInterpolator(mInterpolator);
			mTarget.startAnimation(this);
		}

		@Override
		public void applyTransformation(float interpolatedTime, Transformation t) {
			int targetTop = 0;
			if (mFrom != mTo) {
				if (interpolatedTime < 0.5f) {
					targetTop = (mFrom + (int) ((mTo - mFrom) * 2 * interpolatedTime));
				} else {
					targetTop = (mFrom + (int) ((mTo - mFrom) * 2 * (1 - interpolatedTime)));
				}
			}

			int offset = targetTop - mTarget.getTop();
			final int currentTop = mTarget.getTop();
			if (offset + currentTop < 0) {
				offset = 0 - currentTop;
			}
			// DLog.i("applyTransformation", "offset=" + offset);
			resetPercentage(offset);
			reupdateContentOffsetTop(offset);
			if (interpolatedTime >= 1) {
				animationRunning = false;
			}
		}
	};

	private class DecelerateAccelerateInterpolator implements Interpolator {

		public float getInterpolation(float input) {
			float interpolation = (float) (Math.sin(input * Math.PI) / 2.0f);
			if (input > 0.5f) {
				interpolation = 1 - interpolation;
			}
			// DLog.i("getInterpolation", "interpolation=" + interpolation);
			return interpolation;
		}
	}

	// private class DecelerateAccelerateInterpolator implements Interpolator {
	// DecelerateInterpolator mDeceleratenterpolator = new
	// DecelerateInterpolator();
	// AccelerateInterpolator mAccelerateInterpolator = new
	// AccelerateInterpolator();
	//
	// public DecelerateAccelerateInterpolator() {
	// }
	//
	// public float getInterpolation(float input) {
	// float interpolation = (input < 0.5f ?
	// mDeceleratenterpolator.getInterpolation(input * 2) :
	// mAccelerateInterpolator.getInterpolation(input * 2)) / 2;
	// DLog.i("getInterpolation", "input=" + input + ",interpolation=" +
	// interpolation);
	// return interpolation;
	// }
	// }
}

interface OnOverScrollListener {
	public void onTopOverScroll(int dY);

	// public void onBottomOverScroll(int dY);
}