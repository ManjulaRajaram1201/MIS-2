package com.mis.common;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;

/**
 * @author PRANESH class to kill all activities
 */

public abstract class AppBaseActivity extends AppCompatActivity {
	public static final String FINISH_ALL_ACTIVITIES_ACTIVITY_ACTION = "com.mis.controller.FINISH_ALL_ACTIVITIES_ACTIVITY_ACTION";
	private BaseActivityReceiver baseActivityReceiver = new BaseActivityReceiver();
	public static final IntentFilter INTENT_FILTER = createIntentFilter();

	private static IntentFilter createIntentFilter() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(FINISH_ALL_ACTIVITIES_ACTIVITY_ACTION);
		return filter;
	}

	protected void registerBaseActivityReceiver() {
		registerReceiver(baseActivityReceiver, INTENT_FILTER);
	}

	protected void unRegisterBaseActivityReceiver() {
		unregisterReceiver(baseActivityReceiver);
	}

	public class BaseActivityReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction()
					.equals(FINISH_ALL_ACTIVITIES_ACTIVITY_ACTION)) {
				finish();
			}
		}
	}

	protected void closeAllActivities() {
		sendBroadcast(new Intent(FINISH_ALL_ACTIVITIES_ACTIVITY_ACTION));
	}

}