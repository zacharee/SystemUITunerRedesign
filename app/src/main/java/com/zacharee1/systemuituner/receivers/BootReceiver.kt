package com.zacharee1.systemuituner.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.preference.PreferenceManager
import android.support.v4.content.ContextCompat
import com.zacharee1.systemuituner.services.SafeModeService

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

        if (action != null
                && sharedPreferences.getBoolean("safe_mode", false)
                && (action == Intent.ACTION_BOOT_COMPLETED
                        || action == Intent.ACTION_REBOOT
                        || action == "android.intent.action.QUICKBOOT_POWERON"
                        || action == "com.htc.intent.action.QUICKBOOT_POWERON")) {
            ContextCompat.startForegroundService(context, Intent(context, SafeModeService::class.java))
        }
    }
}
