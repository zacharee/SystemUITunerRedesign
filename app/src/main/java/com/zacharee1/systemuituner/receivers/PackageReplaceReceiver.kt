package com.zacharee1.systemuituner.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.zacharee1.systemuituner.services.SafeModeService
import com.zacharee1.systemuituner.util.prefs

class PackageReplaceReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_MY_PACKAGE_REPLACED
                && context.prefs.safeMode) {
            ContextCompat.startForegroundService(context, Intent(context, SafeModeService::class.java))
        }
    }
}
