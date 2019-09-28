package com.zacharee1.systemuituner.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.zacharee1.systemuituner.util.prefs
import com.zacharee1.systemuituner.util.startForceADBService
import com.zacharee1.systemuituner.util.startSafeModeService

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        if (action != null
                && (action == Intent.ACTION_BOOT_COMPLETED
                        || action == Intent.ACTION_REBOOT
                        || action == "android.intent.action.QUICKBOOT_POWERON"
                        || action == "com.htc.intent.action.QUICKBOOT_POWERON")) {
            if (context.prefs.safeMode) context.startSafeModeService()
            if (context.prefs.forceEnableAdb) context.startForceADBService()
        }
    }
}
