package com.zacharee1.systemuituner.util

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.preference.PreferenceFragment
import android.preference.SwitchPreference
import android.provider.Settings
import android.text.TextUtils
import com.zacharee1.systemuituner.activites.info.SettingWriteFailed
import java.util.*

object SettingsUtils {
    fun writeGlobal(context: Context?, key: String?, value: Any?): Boolean {
        if (key == null) return false
        return try {
            Settings.Global.putString(context?.contentResolver, key, value?.toString())
        } catch (e: Exception) {
            val baseCommand = if (value != null) "settings put global $key $value" else "settings delete global $key"
            return if (SuUtils.testSudo()) {
                SuUtils.sudo(baseCommand)
                true
            } else {
                launchErrorActivity(context, baseCommand)
                false
            }
        }

    }

    fun writeSecure(context: Context?, key: String?, value: Any?): Boolean {
        if (key == null) return false
        return try {
            Settings.Secure.putString(context?.contentResolver, key, value?.toString())
        } catch (e: Exception) {
            val baseCommand = if (value != null) "settings put secure $key $value" else "settings delete secure $key"
            return if (SuUtils.testSudo()) {
                SuUtils.sudo(baseCommand)
                true
            } else {
                launchErrorActivity(context, baseCommand)
                false
            }
        }

    }

    fun writeSystem(context: Context?, key: String?, value: Any?): Boolean {
        if (key == null) return false
        return try {
            Settings.System.putString(context?.contentResolver, key, value?.toString())
        } catch (e: Exception) {
            val baseCommand = if (value != null) "settings put system $key $value" else "settings delete system $key"
            return if (SuUtils.testSudo()) {
                SuUtils.sudo(baseCommand)
                true
            } else {
                launchErrorActivity(context, baseCommand)
                false
            }
        }

    }

    private fun launchErrorActivity(context: Context?, baseCommand: String) {
        val adbCommand = "adb shell $baseCommand"
        val intent = Intent(context, SettingWriteFailed::class.java)
        intent.action = Intent.ACTION_VIEW
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.putExtra("command", adbCommand)
        context?.startActivity(intent)
    }

    fun hasSpecificPerm(context: Context?, permission: String): Boolean {
        return context?.checkCallingOrSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
    }

    fun changeBlacklist(key: String?, remove: Boolean, context: Context?): Boolean {
        return if (key != null) {
            var currentBL: String = Settings.Secure.getString(context?.contentResolver, "icon_blacklist") ?: ""
            val blItems = TreeSet(currentBL.split(","))
            val keyItems = TreeSet(key.split(","))

            keyItems
                    .filter { if (remove) blItems.contains(it) else !blItems.contains(it) }
                    .forEach { if (remove) blItems.remove(it) else blItems.add(it) }

            currentBL = TextUtils.join(",", blItems) ?: ""

            SettingsUtils.writeSecure(context, "icon_blacklist", currentBL)
        } else false
    }

    fun shouldSetSwitchChecked(fragment: PreferenceFragment) {
        var blString: String? = Settings.Secure.getString(fragment.activity.contentResolver, "icon_blacklist")
        if (blString == null) blString = ""

        val blItems = TreeSet(blString.split(","))

        for (i in 0 until fragment.preferenceScreen.rootAdapter.count) {
            val o = fragment.preferenceScreen.rootAdapter.getItem(i)

            if (o is SwitchPreference) {
                o.isChecked = true

                if (!blString.isEmpty()) {
                    val key = o.key

                    if (key != null) {
                        val keyItems = TreeSet(key.split(","))

                        keyItems
                                .filter { blItems.contains(it) }
                                .forEach { o.isChecked = false }
                    }
                }
            }
        }
    }
}
