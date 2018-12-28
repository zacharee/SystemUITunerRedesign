package com.zacharee1.systemuituner.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.zacharee1.systemuituner.services.SafeModeService
import com.zacharee1.systemuituner.util.prefs

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        if (action != null
                && context.prefs.safeMode
                && (action == Intent.ACTION_BOOT_COMPLETED
                        || action == Intent.ACTION_REBOOT
                        || action == "android.intent.action.QUICKBOOT_POWERON"
                        || action == "com.htc.intent.action.QUICKBOOT_POWERON")) {
            ContextCompat.startForegroundService(context, Intent(context, SafeModeService::class.java))
        }
    }
}
