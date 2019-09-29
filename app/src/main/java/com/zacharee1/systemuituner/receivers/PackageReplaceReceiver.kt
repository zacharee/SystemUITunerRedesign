package com.zacharee1.systemuituner.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.zacharee1.systemuituner.util.prefs
import com.zacharee1.systemuituner.util.startForceADBService
import com.zacharee1.systemuituner.util.startSafeModeService

class PackageReplaceReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_MY_PACKAGE_REPLACED) {
            if (context.prefs.safeMode) context.startSafeModeService()
            if (context.prefs.forceEnableAdb) context.startForceADBService()
        }
    }
}
