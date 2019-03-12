package com.zacharee1.systemuituner.util

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.preference.PreferenceFragment
import android.preference.PreferenceManager
import android.preference.SwitchPreference
import android.provider.Settings
import android.support.v7.app.AlertDialog
import android.text.TextUtils
import android.util.TypedValue
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.activites.info.GrantWSActivity
import com.zacharee1.systemuituner.activites.MainActivity
import com.zacharee1.systemuituner.activites.OptionsActivity
import com.zacharee1.systemuituner.activites.info.SettingWriteFailed
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.util.*
import kotlin.collections.ArrayList

fun PackageManager.isPackageInstalled(packageName: String) =
        try {
            getPackageInfo(packageName, 0)
            true
        } catch (e: Exception) {
            false
        }

fun Context.pxToDp(px: Float) =
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, px, resources.displayMetrics)

fun runCommand(vararg commands: String): String? {
    try {
        val comm = Runtime.getRuntime().exec("sh")
        val outputStream = DataOutputStream(comm.outputStream)

        commands.forEach {
            outputStream.writeBytes(it + "\n")
            outputStream.flush()
        }

        outputStream.writeBytes("exit\n")
        outputStream.flush()

        val inputReader = BufferedReader(InputStreamReader(comm.inputStream))
        val errorReader = BufferedReader(InputStreamReader(comm.errorStream))

        var ret = ""
        var line: String?

        do {
            line = inputReader.readLine()
            if (line == null) break
            ret = ret + line + "\n"
        } while (true)

        do {
            line = errorReader.readLine()
            if (line == null) break
            ret = ret + line + "\n"
        } while (true)

        try {
            comm.waitFor()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

        outputStream.close()

        return ret
    } catch (e: IOException) {
        e.printStackTrace()
        return null
    }
}

fun Context.checkPermissions(permissions: ArrayList<String>) =
        ArrayList(permissions.filter {
            checkCallingOrSelfPermission(it) != PackageManager.PERMISSION_GRANTED
        })

fun Context.hasUsage(): Boolean {
    return checkCallingOrSelfPermission(Manifest.permission.PACKAGE_USAGE_STATS) == PackageManager.PERMISSION_GRANTED
}

fun Activity.startUp() {
    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

    val firstStart = sharedPreferences.getBoolean("first_start", true)
    if (firstStart && checkSamsung()) {
        sharedPreferences.edit().putBoolean("safe_mode", true).apply()
        try {
            AlertDialog.Builder(this)
                    .setTitle(resources.getString(R.string.notice))
                    .setMessage(resources.getString(R.string.safe_mode_auto_enabled))
                    .setPositiveButton(resources.getString(R.string.ok), null)
                    .show()
        } catch (e: Exception) {}
    }
    sharedPreferences.edit().putBoolean("first_start", false).apply()

    if (sharedPreferences.getBoolean("hide_welcome_screen", false)) {
        startActivity(Intent(this, OptionsActivity::class.java))
    } else {
        startActivity(Intent(this, MainActivity::class.java))
    }
}

fun Context.getInstalledApps() =
        packageManager.getInstalledApplications(PackageManager.GET_META_DATA)

fun Context.isInDarkMode() =
        PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean("dark_mode", false)

fun checkMIUI(): Boolean {
    val miui = ArrayList<String>()
    miui.add("ro.miui.ui.version.code")
    miui.add("ro.miui.ui.version.name")
    miui.add("ro.miui.cust_variant")
    miui.add("ro.miui.has_cust_partition")
    miui.add("ro.miui.has_handy_mode_sf")
    miui.add("ro.miui.has_real_blur")
    miui.add("ro.miui.mcc")
    miui.add("ro.miui.mnc")
    miui.add("ro.miui.region")
    miui.add("ro.miui.version.code_time")

    val props = ArrayList<String>()
    props.addAll(miui)

    try {
        @SuppressLint("PrivateApi") val SystemProperties = Class.forName("android.os.SystemProperties")
        val get = SystemProperties.getMethod("get", String::class.java)

        for (prop in props) {
            val ret = get.invoke(null, prop).toString()

            if (!ret.isEmpty()) {
                return true
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return false
}

// Native call recording doesn't seem to be available on OnePlus One (A000X), OnePlus X (E100X)
// and OnePlus 2 (A200X) models
private val onePlusOldModels = """^(ONEPLUS\s)*[AE][02]\d{3}$""".toRegex()

fun checkOnePlusWithCallRecording() =
        Build.MANUFACTURER == "OnePlus" && !onePlusOldModels.matches(Build.MODEL)

fun Context.checkSamsung() =
        packageManager.hasSystemFeature("com.samsung.feature.samsung_experience_mobile")

fun Context.writeGlobal(key: String?, value: Any?): Boolean {
    if (key == null) return false
    return try {
        Settings.Global.putString(contentResolver, key, value?.toString())
    } catch (e: Exception) {
        val baseCommand = if (value != null) "settings put global $key $value" else "settings delete global $key"
        return if (SuUtils.testSudo()) {
            SuUtils.sudo(baseCommand)
            true
        } else {
            launchErrorActivity(baseCommand)
            false
        }
    }
}

fun Context.writeSecure(key: String?, value: Any?): Boolean {
    if (key == null) return false
    return try {
        Settings.Secure.putString(contentResolver, key, value?.toString())
    } catch (e: Exception) {
        val baseCommand = if (value != null) "settings put secure $key $value" else "settings delete secure $key"
        return if (SuUtils.testSudo()) {
            SuUtils.sudo(baseCommand)
            true
        } else {
            launchErrorActivity(baseCommand)
            false
        }
    }
}

fun Context.writeSystem(key: String?, value: Any?, showError: Boolean = true): Boolean {
    return if (!Settings.System.canWrite(this)) {
        GrantWSActivity.start(this, key ?: return false, value)
        false
    } else {
        key != null && try {
            Settings.System.putString(contentResolver, key, value?.toString())
        } catch (e: Exception) {
            val baseCommand = if (value != null) "settings put system $key $value" else "settings delete system $key"
            return if (SuUtils.testSudo()) {
                SuUtils.sudo(baseCommand)
                true
            } else {
                if (showError) launchErrorActivity(baseCommand)
                false
            }
        }
    }
}

fun Context.launchErrorActivity(baseCommand: String?) {
    val adbCommand = "adb shell $baseCommand"
    val intent = Intent(this, SettingWriteFailed::class.java)
    intent.action = Intent.ACTION_VIEW
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    intent.putExtra("command", adbCommand)
    startActivity(intent)
}

fun Context.hasSpecificPerm(permission: String) =
        checkCallingOrSelfPermission(permission) == PackageManager.PERMISSION_GRANTED

fun Context.changeBlacklist(key: String?, remove: Boolean) =
        if (key != null) {
            var currentBL: String = Settings.Secure.getString(contentResolver, "icon_blacklist") ?: ""
            val blItems = TreeSet(currentBL.split(","))
            val keyItems = TreeSet(key.split(","))

            keyItems
                    .filter { if (remove) blItems.contains(it) else !blItems.contains(it) }
                    .forEach { if (remove) blItems.remove(it) else blItems.add(it) }

            currentBL = TextUtils.join(",", blItems) ?: ""

            writeSecure("icon_blacklist", currentBL)
        } else false

fun PreferenceFragment.updateBlacklistSwitches() {
    var blString: String? = Settings.Secure.getString(activity.contentResolver, "icon_blacklist")
    if (blString == null) blString = ""

    val blItems = TreeSet(blString.split(","))

    for (i in 0 until preferenceScreen.rootAdapter.count) {
        val o = preferenceScreen.rootAdapter.getItem(i)

        if (o is SwitchPreference) {
            o.isChecked = true

            if (!blString.isEmpty()) {
                val key = o.key

                if (key != null) {
                    val keyItems = TreeSet(key.split(","))

                    keyItems
                            .filter { blItems.contains(it) }
                            .forEach { _ -> o.isChecked = false }
                }
            }
        }
    }
}
