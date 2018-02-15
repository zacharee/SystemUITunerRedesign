package com.zacharee1.systemuituner.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.preference.PreferenceManager
import com.zacharee1.systemuituner.services.SafeModeService

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

        if (action != null &&
                sharedPreferences.getBoolean("safe_mode", false) && (action == Intent.ACTION_BOOT_COMPLETED ||
                        action == Intent.ACTION_REBOOT ||
                        action == "android.intent.action.QUICKBOOT_POWERON" ||
                        action == "com.htc.intent.action.QUICKBOOT_POWERON")) {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.N_MR1) {
                context.startService(Intent(context, SafeModeService::class.java))
            } else {
                context.startForegroundService(Intent(context, SafeModeService::class.java))
            }
        }
    }
}
