package com.zacharee1.systemuituner.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.preference.PreferenceManager
import android.support.v4.content.ContextCompat
import com.zacharee1.systemuituner.services.SafeModeService

class PackageReplaceReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_MY_PACKAGE_REPLACED
                && PreferenceManager.getDefaultSharedPreferences(context).getBoolean("safe_mode", false)) {
            ContextCompat.startForegroundService(context, Intent(context, SafeModeService::class.java))
        }
    }
}
