package com.zacharee1.systemuituner.misc;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

import com.zacharee1.systemuituner.activites.SettingsActivity;

public class RecreateHandler
{
    private static BroadcastReceiver mMessageReceiver;

    public static void onCreate(final Activity activity) {
        mMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                activity.recreate();
            }
        };

        LocalBroadcastManager.getInstance(activity).registerReceiver(mMessageReceiver,
                new IntentFilter(SettingsActivity.RECREATE_ACTIVITY));
    }

    public static void onDestroy(Activity context) {
        if (mMessageReceiver != null) LocalBroadcastManager.getInstance(context).unregisterReceiver(mMessageReceiver);
    }
}
