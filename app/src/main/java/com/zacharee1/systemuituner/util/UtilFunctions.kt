package com.zacharee1.systemuituner.util

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES
import android.os.Build.VERSION_CODES.N_MR1
import android.os.Build.VERSION_CODES.O
import android.provider.Settings
import android.util.TypedValue
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceGroup
import androidx.preference.SwitchPreference
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.topjohnwu.superuser.Shell
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.activites.MainActivity
import com.zacharee1.systemuituner.activites.OptionsActivity
import com.zacharee1.systemuituner.activites.info.GrantWSActivity
import com.zacharee1.systemuituner.activites.info.SettingWriteFailed
import com.zacharee1.systemuituner.services.ForceADBService
import com.zacharee1.systemuituner.services.SafeModeService


fun PackageManager.isPackageInstalled(packageName: String) =
        try {
            getPackageInfo(packageName, 0)
            true
        } catch (e: Exception) {
            false
        }

fun Context.pxToDp(px: Float) =
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, px, resources.displayMetrics)

fun Context.checkPermissions(permissions: ArrayList<String>) =
        ArrayList(permissions.filter {
            checkCallingOrSelfPermission(it) != PackageManager.PERMISSION_GRANTED
        })

fun Context.hasUsage(): Boolean {
    return checkCallingOrSelfPermission(Manifest.permission.PACKAGE_USAGE_STATS) == PackageManager.PERMISSION_GRANTED
}

fun Context.hasDump(): Boolean {
    return checkCallingOrSelfPermission(Manifest.permission.DUMP) == PackageManager.PERMISSION_GRANTED
}

fun Activity.startUp() {
    if (prefs.firstStart && checkSamsung()) {
        prefs.safeMode = true
        try {
            MaterialAlertDialogBuilder(this)
                    .setTitle(resources.getString(R.string.notice))
                    .setMessage(resources.getString(R.string.safe_mode_auto_enabled))
                    .setPositiveButton(resources.getString(R.string.ok), null)
                    .show()
        } catch (e: Exception) {}
    }
    prefs.firstStart = false

    if (prefs.hideWelcomeScreen) {
        startActivity(Intent(this, OptionsActivity::class.java))
    } else {
        startActivity(Intent(this, MainActivity::class.java))
    }
}

fun Context.getInstalledApps() =
        packageManager.getInstalledApplications(PackageManager.GET_META_DATA)

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

fun Context.checkSamsung() =
        packageManager.hasSystemFeature("com.samsung.feature.samsung_experience_mobile")

fun Context.writeGlobal(key: String?, value: Any?): Boolean {
    if (key == null) return false
    return try {
        Settings.Global.putString(contentResolver, key, value?.toString())
    } catch (e: Exception) {
        val baseCommand = if (value != null) "settings put global $key $value" else "settings delete global $key"
        return if (Shell.rootAccess()) {
            sudo(baseCommand)
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
        return if (Shell.rootAccess()) {
            sudo(baseCommand)
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
            return if (Shell.rootAccess()) {
                sudo(baseCommand)
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

fun PreferenceFragmentCompat.updateBlacklistSwitches() {
    val blItems = context!!.blacklistManager.currentBlacklistAsList

    preferenceScreen.forEachPreference { pref ->
        if (pref is SwitchPreference) {
            val key = pref.key
            pref.isChecked = true

            if (!blItems.isEmpty()) {
                if (key != null) {
                    val keyItems = ArrayList(key.split(","))

                    if (keyItems.any { blItems.contains(it) })
                        pref.isChecked = false
                }
            }
        }
    }
}

fun PreferenceGroup.forEachPreference(consumer: (pref: Preference) -> Unit) {
    for (i in 0 until preferenceCount) {
        val child = getPreference(i)

        consumer.invoke(child)

        if (child is PreferenceGroup) child.forEachPreference(consumer)
    }
}

fun FragmentManager.getAnimTransaction() =
        beginTransaction().apply { setCustomAnimations(R.anim.fade_in, R.anim.fade_out,
                R.anim.fade_in, R.anim.fade_out) }

fun sudo(vararg cmds: String) {
    cmds.forEach {
        Shell.su(it).submit()
    }
}

val Context.prefs: PrefManager
    get() = PrefManager.getInstance(this)

val Context.blacklistManager: BlacklistManager
    get() = BlacklistManager.getInstance(this)

fun Context.createApplicationContext(appInfo: ApplicationInfo): Context {
    val method = Context::class.java.getMethod("createApplicationContext", ApplicationInfo::class.java, Int::class.java)

    return method.invoke(this, appInfo, 0) as Context
}

val PreferenceFragmentCompat.navController: NavController
    get() = NavHostFragment.findNavController(this)

val navOptions =
        NavOptions.Builder()
                .setEnterAnim(android.R.anim.fade_in)
                .setExitAnim(android.R.anim.fade_out)
                .setPopEnterAnim(android.R.anim.fade_in)
                .setPopExitAnim(android.R.anim.fade_out)
                .build()

val Activity.navController: NavController
    get() = findNavController(R.id.nav_host)

fun Context.getNotificationSettingsForChannel(channel: String?): Intent {
    val intent = Intent()
    when {
        SDK_INT >= VERSION_CODES.P -> {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            if (channel != null) {
                intent.action = Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS
                intent.putExtra(Settings.EXTRA_CHANNEL_ID, channel)
            } else {
                intent.action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
            }
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
        }
        SDK_INT >= O -> {
            if (channel != null) {
                intent.action = Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS
                intent.putExtra(Settings.EXTRA_CHANNEL_ID, channel)
            } else {
                intent.action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
            }
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
        }
        SDK_INT >= N_MR1 -> {
            intent.action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
        }
        SDK_INT >= VERSION_CODES.M -> {
            intent.action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
            intent.putExtra("app_package", packageName)
            intent.putExtra("app_uid", applicationInfo.uid)
        }
    }

    return intent
}

fun Context.startSafeModeService() {
    val intent = Intent(this, SafeModeService::class.java)
    ContextCompat.startForegroundService(this, intent)
}

fun Context.stopSafeModeService() {
    val intent = Intent(this, SafeModeService::class.java)
    stopService(intent)
}

fun Context.startForceADBService() {
    val intent = Intent(this, ForceADBService::class.java)
    ContextCompat.startForegroundService(this, intent)
}

fun Context.stopForceADBService() {
    val intent = Intent(this, ForceADBService::class.java)
    stopService(intent)
}
