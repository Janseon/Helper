package com.janseon.helper.activityresult;

import android.app.Activity;
import android.content.Intent;
import android.util.SparseArray;

import java.util.ArrayList;

/**
 * 
 * @Description Activity 返回的回调
 * @author 健兴
 * @version 1.0
 * @date 2014-8-13
 * @Copyright: Copyright (c) 2014 Shenzhen Inser Technology Co., Ltd. Inc. All
 *             rights reserved.
 */
public class ActivityResultHelper {

	private SparseArray<ActivityResult> mActivityResults;
	private ArrayList<ActivityResult> mComActivityResults;

	public void addActivityResult(ActivityResult activityResult) {
		if (mActivityResults == null) {
			mActivityResults = new SparseArray<ActivityResult>(4);
		}
		mActivityResults.put(activityResult.mRequestCode, activityResult);
	}

	public void addComActivityResults(ActivityResult activityResult) {
		if (mComActivityResults == null) {
			mComActivityResults = new ArrayList<ActivityResult>(4);
		}
		mComActivityResults.add(activityResult);
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (Activity.RESULT_OK == resultCode) {
			if (mActivityResults != null) {
				ActivityResult activityResult = mActivityResults.get(requestCode);
				if (activityResult != null) {
					mActivityResults.remove(requestCode);
					activityResult.onActivityResult(requestCode, resultCode, data);
				}
			}
		}
		if (mComActivityResults != null) {
			for (ActivityResult activityResult : mComActivityResults) {
				activityResult.onActivityResult(requestCode, resultCode, data);
			}
		}
	}
}
