package com.janseon.helper.activityresult;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;

public abstract class ActivityResult {

	public static final int REQUEST_CODE_DEFAUTE = 10000;
	private static final String KEY_Result = "Key_ActivityResult";

	public final int mRequestCode;

	public ActivityResult() {
		this.mRequestCode = REQUEST_CODE_DEFAUTE;
	}

	public ActivityResult(int requestCode) {
		this.mRequestCode = requestCode;
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		onActivityResult(data);
	}

	public void onActivityResult(Intent data) {
	};

	public static void onFinishResult(Activity activiy) {
		activiy.setResult(Activity.RESULT_OK);
		activiy.finish();
	}

	public static void onFinishResult(Activity activiy, Intent data) {
		activiy.setResult(Activity.RESULT_OK, data);
		activiy.finish();
	}

	public static void onFinishResult(Activity activiy, String value) {
		Intent data = new Intent();
		data.putExtra(KEY_Result, value);
		activiy.setResult(Activity.RESULT_OK, data);
		activiy.finish();
	}

	public static void onFinishResult(Activity activiy, Bundle bundle) {
		Intent data = new Intent();
		data.putExtras(bundle);
		activiy.setResult(Activity.RESULT_OK, data);
		activiy.finish();
	}

	public static void onFinishResult(Activity activiy, int value) {
		Intent data = new Intent();
		data.putExtra(KEY_Result, value);
		activiy.setResult(Activity.RESULT_OK, data);
		activiy.finish();
	}

	public static void onFinishResult(Activity activiy, Parcelable value) {
		Intent data = new Intent();
		data.putExtra(KEY_Result, value);
		activiy.setResult(Activity.RESULT_OK, data);
		activiy.finish();
	}

	public static String getDataString(Intent data) {
		return data.getStringExtra(KEY_Result);
	}

	public static int getDataInt(Intent data) {
		return data.getIntExtra(KEY_Result, -1);
	}

	public static int getDataInt(Intent data, int defaultValue) {
		return data.getIntExtra(KEY_Result, defaultValue);
	}

	public static <T extends Parcelable> T getDataParcelable(Intent data) {
		return data.getParcelableExtra(KEY_Result);
	}
}
