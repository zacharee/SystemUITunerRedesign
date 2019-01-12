package com.zacharee1.systemuituner.handlers

import android.content.Context
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import com.google.firebase.analytics.FirebaseAnalytics
import com.zacharee1.systemuituner.util.prefs
import com.zacharee1.systemuituner.util.writeGlobal
import java.util.*

object ImmersiveHandler {
    private const val KEY = "policy_control"
    const val FULL = "immersive.full"
    const val STATUS = "immersive.status"
    const val NAV = "immersive.navigation"
    const val PRECONF = "immersive.preconfirms"
    const val DISABLED = "immersive.none"

    val POLICY_CONTROL = Settings.Global.getUriFor(KEY)!!

    fun isInImmersive(context: Context?): Boolean {
        val imm = Settings.Global.getString(context?.contentResolver, KEY)

        return imm != null && !imm.isEmpty() && (imm.contains(FULL)
                || imm.contains(STATUS)
                || imm.contains(NAV)
                || imm.contains(PRECONF))
    }

    fun getMode(context: Context?): String {
        var imm = Settings.Global.getString(context?.contentResolver ?: return DISABLED, KEY)
                ?: DISABLED
        if (imm.isEmpty()) imm = DISABLED
        imm = imm.replace("=(.+?)$".toRegex(), "")

        return imm
    }

    fun setMode(context: Context?, type: String = getMode(context)) {
        if (type.contains(FULL)
                || type.contains(STATUS)
                || type.contains(NAV)
                || type.contains(PRECONF)
                || type.contains(DISABLED)) {

            val typeNew = concat(context, type)

            context?.writeGlobal(KEY, typeNew)
        } else {
            Log.w("SystemUITuner", "Invalid Immersive Mode Type: $type")
        }
    }

    private fun concat(context: Context?, type: String): String {
        val builder = StringBuilder(type.replace("=*", ""))
        builder.append("=")
        if (isSelecting(context)) {
            builder.append(parseSelectedApps(context, "*"))
        } else {
            builder.append("*")
        }

        return builder.toString()
    }

    private fun parseSelectedApps(context: Context?, def: String): String {
        val apps = parseSelectedApps(context)

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

    fun parseSelectedApps(context: Context?): TreeSet<String> {
        return TreeSet(context?.prefs?.immersiveApps)
    }

    fun addApp(context: Context?, add: String) {
        val set = TreeSet(context?.prefs?.immersiveApps)
        set.add(add)
        context?.prefs?.immersiveApps = set
    }

    fun removeApp(context: Context?, remove: String) {
        val set = TreeSet(context?.prefs?.immersiveApps)
        set.remove(remove)
        context?.prefs?.immersiveApps = set
    }

    private fun isSelecting(context: Context?): Boolean {
        return context?.prefs?.appImmersive!!
    }

    private fun isBlacklist(context: Context?): Boolean {
        return context?.prefs?.immersiveBlacklist!!
    }
}
