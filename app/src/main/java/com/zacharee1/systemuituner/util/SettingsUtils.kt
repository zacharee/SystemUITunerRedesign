package com.zacharee1.systemuituner.util

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.preference.SwitchPreference
import android.provider.Settings
import android.text.TextUtils
import com.zacharee1.systemuituner.activites.info.SettingWriteFailed
import com.zacharee1.systemuituner.fragments.ItemDetailFragment
import java.util.*

object SettingsUtils {
    fun writeGlobal(context: Context?, key: String, value: String?): Boolean {
        return try {
            Settings.Global.putString(context?.contentResolver, key, value)
            true
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

    fun writeSecure(context: Context?, key: String, value: String?): Boolean {
        return try {
            Settings.Secure.putString(context?.contentResolver, key, value)
            true
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

    fun writeSystem(context: Context, key: String, value: String?): Boolean {
        return try {
            Settings.System.putString(context.contentResolver, key, value)
            true
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
        val adbCommand = "adb shell " + baseCommand
        val intent = Intent(context, SettingWriteFailed::class.java)
        intent.action = Intent.ACTION_VIEW
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.putExtra("command", adbCommand)
        context?.startActivity(intent)
    }

    fun hasPerms(context: Context?): Boolean {
        try {
            val packageInfo = context?.packageManager?.getPackageInfo(context.packageName, PackageManager.GET_PERMISSIONS)
            val perms = ArrayList(Arrays.asList(*packageInfo?.requestedPermissions))

            for (permission in perms) {
                if (!hasSpecificPerm(context, permission)) return false
            }
        } catch (e: Exception) {
            return false
        }

        return true
    }

    fun hasSpecificPerm(context: Context?, permission: String): Boolean {
        return context?.checkCallingOrSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
    }

    fun hasSpecificPerms(context: Context?, permissions: Array<String>): Boolean {
        return permissions.none { context?.checkCallingOrSelfPermission(it) == PackageManager.PERMISSION_DENIED }
    }

    fun changeBlacklist(key: String?, value: Boolean, context: Context) {
        if (key != null) {
            var currentBL: String = Settings.Secure.getString(context.contentResolver, "icon_blacklist") ?: ""

            if (!value) {
                currentBL += if (currentBL.isEmpty()) {
                    key
                } else {
                    ",$key"
                }
            } else {
                val blItems = ArrayList(currentBL.split(","))
                val keyItems = ArrayList(key.split(","))

                keyItems
                        .filter { blItems.contains(it) }
                        .forEach { blItems.remove(it) }

                currentBL = TextUtils.join(",", blItems) ?: ""
            }

            SettingsUtils.writeSecure(context, "icon_blacklist", currentBL)
        }
    }

    fun shouldSetSwitchChecked(fragment: ItemDetailFragment) {
        var blString: String? = Settings.Secure.getString(fragment.activity.contentResolver, "icon_blacklist")
        if (blString == null) blString = ""

        val blItems = ArrayList(blString.split(","))

        for (i in 0 until fragment.preferenceScreen.rootAdapter.count) {
            val o = fragment.preferenceScreen.rootAdapter.getItem(i)

            if (o is SwitchPreference && !o.title.toString().toLowerCase().contains("high brightness warning")) {

                o.isChecked = true

                if (!blString.isEmpty()) {
                    val key = o.key

                    if (key != null) {
                        val keyItems = ArrayList(key.split(","))

                        keyItems
                                .filter { blItems.contains(it) }
                                .forEach { o.isChecked = false }
                    }
                }
            }
        }
    }
}
