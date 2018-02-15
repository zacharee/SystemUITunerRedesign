package com.zacharee1.systemuituner.handlers

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.support.v4.content.LocalBroadcastManager

import com.zacharee1.systemuituner.activites.settings.SettingsActivity

object RecreateHandler {
    private var mMessageReceiver: BroadcastReceiver? = null

    fun onCreate(activity: Activity) {
        mMessageReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                activity.recreate()
            }
        }

        LocalBroadcastManager.getInstance(activity).registerReceiver(mMessageReceiver!!,
                IntentFilter(SettingsActivity.RECREATE_ACTIVITY))
    }

    fun onDestroy(context: Activity) {
        if (mMessageReceiver != null) LocalBroadcastManager.getInstance(context).unregisterReceiver(mMessageReceiver!!)
    }
}
