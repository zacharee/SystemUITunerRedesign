package com.zacharee1.systemuituner.util

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import android.preference.PreferenceManager
import android.support.v7.app.AlertDialog
import android.util.TypedValue
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.activites.ItemListActivity
import com.zacharee1.systemuituner.activites.MainActivity
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.util.*

object Utils {
    fun isPackageInstalled(packagename: String, packageManager: PackageManager): Boolean {
        return try {
            packageManager.getPackageInfo(packagename, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }

    }

    fun pxToDp(context: Context, px: Float): Float {
        val r = context.resources
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, px, r.displayMetrics)
    }

    fun pxToSp(context: Context, px: Float): Float {
        val r = context.resources
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, px, r.displayMetrics)
    }

    fun runCommand(vararg strings: String): String? {
        try {
            val comm = Runtime.getRuntime().exec("sh")
            val outputStream = DataOutputStream(comm.outputStream)

            for (s in strings) {
                outputStream.writeBytes(s + "\n")
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

    fun checkPermissions(context: Context, permissions: Array<String>): Array<String> {
        val notPerms = permissions.filter { context.checkCallingOrSelfPermission(it) != PackageManager.PERMISSION_GRANTED }

        return notPerms.toTypedArray()
    }

    fun startUp(context: Activity) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

        val firstStart = sharedPreferences.getBoolean("first_start", true)
        if (firstStart && Build.MANUFACTURER.toLowerCase().contains("samsung") && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            sharedPreferences.edit().putBoolean("safe_mode", true).apply()
            try {
                AlertDialog.Builder(context)
                        .setTitle(context.resources.getString(R.string.notice))
                        .setMessage(context.resources.getString(R.string.safe_mode_auto_enabled))
                        .setPositiveButton(context.resources.getString(R.string.ok), null)
                        .show()
            } catch (e: Exception) {
            }

        }
        sharedPreferences.edit().putBoolean("first_start", false).apply()

        if (sharedPreferences.getBoolean("hide_welcome_screen", false)) {
            context.startActivity(Intent(context, ItemListActivity::class.java))
        } else {
            context.startActivity(Intent(context, MainActivity::class.java))
        }
    }

    fun getInstalledApps(context: Context): List<ApplicationInfo> {
        return context.packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
    }

    fun isInDarkMode(context: Context): Boolean {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("dark_mode", false)
    }

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
}
