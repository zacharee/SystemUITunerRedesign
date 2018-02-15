package com.zacharee1.systemuituner.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.preference.PreferenceManager
import android.util.Log
import com.zacharee1.systemuituner.misc.MiscStrings
import com.zacharee1.systemuituner.util.SettingsUtils
import java.util.*

class TaskerReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.e("DATA", "RECEIVED")
        Log.e("DATA", intent.dataString)

        if ((intent.action == MiscStrings.ACTION_SETTINGS_GLOBAL ||
                        intent.action == MiscStrings.ACTION_SETTINGS_SECURE ||
                        intent.action == MiscStrings.ACTION_SETTINGS_SYSTEM) && PreferenceManager.getDefaultSharedPreferences(context).getBoolean("tasker_support_enabled", false)) {

            val dataString = intent.dataString

            //expecting dataString with format "SETTING:key/value"
            val nameVal = dataString!!.split("[:]".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
            if (nameVal.size < 2) nameVal[1] = ""
            val keyVal = nameVal[1]

            val keyValPair = ArrayList(Arrays.asList<String>(*keyVal.split("[/]".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()))
            if (keyValPair.size < 2) keyValPair[1] = ""

            when (intent.action) {
                MiscStrings.ACTION_SETTINGS_GLOBAL -> SettingsUtils.writeGlobal(context, keyValPair[0], keyValPair[1])
                MiscStrings.ACTION_SETTINGS_SECURE -> SettingsUtils.writeSecure(context, keyValPair[0], keyValPair[1])
                MiscStrings.ACTION_SETTINGS_SYSTEM -> SettingsUtils.writeSystem(context, keyValPair[0], keyValPair[1])
            }
        }
    }
}
