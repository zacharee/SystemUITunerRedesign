package com.zacharee1.systemuituner.handlers

import android.content.Context
import android.preference.PreferenceManager
import android.provider.Settings
import android.util.Log
import com.zacharee1.systemuituner.util.SettingsUtils
import java.util.*

object ImmersiveHandler {
    private const val KEY = "policy_control"
    const val FULL = "immersive.full"
    const val STATUS = "immersive.status"
    const val NAV = "immersive.navigation"
    const val PRECONF = "immersive.preconfirms"
    const val DISABLED = "immersive.none"

    val POLICY_CONTROL = Settings.Global.getUriFor(KEY)!!

    fun isInImmersive(context: Context): Boolean {
        val imm = Settings.Global.getString(context.contentResolver, KEY)

        return imm != null && !imm.isEmpty() && (imm.contains(FULL)
                || imm.contains(STATUS)
                || imm.contains(NAV)
                || imm.contains(PRECONF))
    }

    fun getMode(context: Context): String? {
        var imm: String? = Settings.Global.getString(context.contentResolver, KEY)
        if (imm == null || imm.isEmpty()) imm = "immersive.none"
        imm = imm.replace("=(.+?)$".toRegex(), "")

        return imm
    }

    fun setMode(context: Context, type: String?) {
        Log.e("Setting Mode", type)

        type?.let {
            if (it.contains(FULL)
                    || it.contains(STATUS)
                    || it.contains(NAV)
                    || it.contains(PRECONF)
                    || it.contains(DISABLED)) {

                val typeNew = concat(context, it)

                SettingsUtils.writeGlobal(context, KEY, typeNew)
            } else {
                throw IllegalArgumentException("Invalid Immersive Mode type: " + type)
            }
        }
    }

    private fun concat(context: Context, type: String): String {
        val builder = StringBuilder(type.replace("=*", ""))
        builder.append("=")
        if (isSelecting(context)) {
            builder.append(parseSelectedApps(context, "*"))
        } else {
            builder.append("*")
        }

        Log.e("Options", builder.toString())

        return builder.toString()
    }

    private fun parseSelectedApps(context: Context, def: String): String {
        val apps = parseSelectedApps(context, TreeSet())

        return if (apps.isEmpty())
            def
        else {
            val ret = StringBuilder()
            if (isBlacklist(context)) ret.append("apps,")

            for (app in apps) {
                ret.append(if (isBlacklist(context)) "-" else "").append(app).append(",")
            }

            ret.toString()
        }
    }

    fun parseSelectedApps(context: Context, def: TreeSet<String>): TreeSet<String> {
        return TreeSet(PreferenceManager.getDefaultSharedPreferences(context).getStringSet("immersive_apps", def)!!)
    }

    fun addApp(context: Context, add: String) {
        val set = TreeSet(PreferenceManager.getDefaultSharedPreferences(context).getStringSet("immersive_apps", TreeSet())!!)
        set.add(add)
        PreferenceManager.getDefaultSharedPreferences(context).edit().putStringSet("immersive_apps", set).apply()
    }

    fun removeApp(context: Context, remove: String) {
        val set = TreeSet(PreferenceManager.getDefaultSharedPreferences(context).getStringSet("immersive_apps", TreeSet())!!)
        set.remove(remove)
        PreferenceManager.getDefaultSharedPreferences(context).edit().putStringSet("immersive_apps", set).apply()
    }

    private fun isSelecting(context: Context): Boolean {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("app_immersive", false)
    }

    private fun isBlacklist(context: Context): Boolean {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("immersive_blacklist", false)
    }
}
