package com.janseon.helper.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import java.util.ArrayList;

/**
 * 
 * @Description context 广播管理
 * @author 健兴
 * @version 1.0
 * @date 2014-8-13
 * @Copyright: Copyright (c) 2014 Shenzhen Inser Technology Co., Ltd. Inc. All
 *             rights reserved.
 */
public class BroadcastReceiverHelper {

	// /////////////////////////////注册广播
	private ArrayList<BroadcastReceiver> mRreceivers;

	public void registerReceiver(Context context, BroadcastReceiver receiver, String... actions) {
		registerReceiver(context, false, receiver, actions);
	}

	public void registerReceiver(Context context, BroadcastReceiver[] receivers, String... actions) {
		if (mRreceivers == null) {
			mRreceivers = new ArrayList<BroadcastReceiver>();
		}
		int length = receivers.length;
		for (int i = 0; i < length; i++) {
			String action = actions[i];
			IntentFilter filter = new IntentFilter();
			filter.addAction(action);
			context.registerReceiver(receivers[i], filter);
			mRreceivers.add(receivers[i]);
		}
	}

	public void registerReceiver(Context context, boolean firstRun, BroadcastReceiver receiver, String... actions) {
		if (mRreceivers == null) {
			mRreceivers = new ArrayList<BroadcastReceiver>();
		}
		mRreceivers.add(receiver);
		IntentFilter filter = new IntentFilter();
		for (String action : actions) {
			filter.addAction(action);
		}
		context.registerReceiver(receiver, filter);
		if (firstRun) {
			Intent intent = new Intent(actions[0]);
			receiver.onReceive(context, intent);
		}
	}

	public void unReceiver(Context context, BroadcastReceiver... rs) {
		if (mRreceivers != null) {
			ArrayList<BroadcastReceiver> removeRreceivers = new ArrayList<BroadcastReceiver>();
			for (BroadcastReceiver r : rs) {
				if (mRreceivers.contains(r)) {
					context.unregisterReceiver(r);
					removeRreceivers.add(r);
				}
			}
			mRreceivers.removeAll(removeRreceivers);
		}
	}

	public void onDestroy(Context context) {
		if (mRreceivers != null) {
			for (BroadcastReceiver receiver : mRreceivers) {
				context.unregisterReceiver(receiver);
			}
			mRreceivers.clear();
		}
	}
}
