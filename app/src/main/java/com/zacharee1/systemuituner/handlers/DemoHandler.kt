package com.zacharee1.systemuituner.handlers

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.provider.Settings
import com.zacharee1.systemuituner.util.SettingsUtils
import java.util.concurrent.TimeUnit

class DemoHandler(private val context: Context?) {
    private val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    val isAllowed: Boolean
        get() = Settings.Global.getInt(context?.contentResolver, "sysui_demo_allowed", 0) == 1

    val isEnabled: Boolean
        get() = Settings.Global.getInt(context?.contentResolver, "sysui_tuner_demo_on", 0) == 1

    fun showDemo() {
        try {
            var intent = Intent("com.android.systemui.demo")
            intent.putExtra("command", "enter")
            context?.sendBroadcast(intent)

            intent = Intent("com.android.systemui.demo")
            intent.putExtra("command", "clock")
            intent.putExtra("hhmm", String.format("%02d%02d", time().hour(), time().minute()))
            context?.sendBroadcast(intent)

            intent = Intent("com.android.systemui.demo")
            intent.putExtra("command", "network")
            intent.putExtra("mobile", if (showMobile()) "show" else "hide")
            intent.putExtra("fully", mobileFully())
            intent.putExtra("level", mobileLevel().toString() + "")
            intent.putExtra("datatype", mobileType())
            context?.sendBroadcast(intent)

            intent = Intent("com.android.systemui.demo")
            intent.putExtra("command", "network")
            intent.putExtra("sims", simCount().toString() + "")
            intent.putExtra("nosim", if (noSim()) "show" else "hide")
            context?.sendBroadcast(intent)

            intent = Intent("com.android.systemui.demo")
            intent.putExtra("command", "network")
            intent.putExtra("wifi", if (showWiFi()) "show" else "hide")
            intent.putExtra("fully", wifiFully())
            intent.putExtra("level", wifiLevel().toString() + "")
            context?.sendBroadcast(intent)

            intent = Intent("com.android.systemui.demo")
            intent.putExtra("command", "network")
            intent.putExtra("airplane", if (showAirplane()) "show" else "hide")
            context?.sendBroadcast(intent)

            intent = Intent("com.android.systemui.demo")
            intent.putExtra("command", "battery")
            intent.putExtra("level", batteryLevel().toString() + "")
            intent.putExtra("plugged", batteryCharging().toString() + "")
            context?.sendBroadcast(intent)

            intent = Intent("com.android.systemui.demo")
            intent.putExtra("command", "notifications")
            intent.putExtra("visible", showNotifs().toString() + "")
            context?.sendBroadcast(intent)

            intent = Intent("com.android.systemui.demo")
            intent.putExtra("command", "bars")
            intent.putExtra("mode", statStyle())
            context?.sendBroadcast(intent)

            intent = Intent("com.android.systemui.demo")
            intent.putExtra("command", "status")
            intent.putExtra("volume", volumeIcon())
            intent.putExtra("bluetooth", btIcon())
            intent.putExtra("location", if (location()) "show" else "hide")
            intent.putExtra("alarm", if (alarm()) "show" else "hide")
            intent.putExtra("sync", if (sync()) "show" else "hide")
            intent.putExtra("tty", if (tty()) "show" else "hide")
            intent.putExtra("eri", if (eri()) "show" else "hide")
            intent.putExtra("mute", if (mute()) "show" else "hide")
            intent.putExtra("speakerphone", if (spkphone()) "show" else "hide")
            context?.sendBroadcast(intent)

            SettingsUtils.writeGlobal(context, "sysui_tuner_demo_on", "1")
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun hideDemo() {
        val intent = Intent("com.android.systemui.demo")
        intent.putExtra("command", "exit")
        context?.sendBroadcast(intent)

        SettingsUtils.writeGlobal(context, "sysui_tuner_demo_on", "0")
    }

    private fun showNotifs(): Boolean {
        return prefs.getBoolean("show_notifications", false)
    }

    private fun showMobile(): Boolean {
        return prefs.getBoolean("show_mobile", false)
    }

    private fun showAirplane(): Boolean {
        return prefs.getBoolean("show_airplane", false)
    }

    private fun showWiFi(): Boolean {
        return prefs.getBoolean("show_wifi", false)
    }

    private fun batteryCharging(): Boolean {
        return prefs.getBoolean("battery_charging", false)
    }

    private fun mobileFully(): Boolean {
        return prefs.getBoolean("mobile_fully_connected", false)
    }

    private fun wifiFully(): Boolean {
        return prefs.getBoolean("wifi_fully_connected", false)
    }

    private fun noSim(): Boolean {
        return prefs.getBoolean("no_sim", false)
    }

    private fun location(): Boolean {
        return prefs.getBoolean("location_demo", false)
    }

    private fun alarm(): Boolean {
        return prefs.getBoolean("alarm_demo", false)
    }

    private fun sync(): Boolean {
        return prefs.getBoolean("sync_demo", false)
    }

    private fun tty(): Boolean {
        return prefs.getBoolean("tty_demo", false)
    }

    private fun eri(): Boolean {
        return prefs.getBoolean("eri_demo", false)
    }

    private fun mute(): Boolean {
        return prefs.getBoolean("mute_demo", false)
    }

    private fun spkphone(): Boolean {
        return prefs.getBoolean("speakerphone_demo", false)
    }

    private fun batteryLevel(): Int {
        return prefs.getInt("selected_battery_level", 100)
    }

    private fun wifiLevel(): Int {
        return prefs.getInt("wifi_strength", 4)
    }

    private fun mobileLevel(): Int {
        return prefs.getInt("selected_mobile_strength", 4)
    }

    private fun simCount(): Int {
        return prefs.getInt("sim_count", 0) + 1
    }

    private fun time(): Long {
        return prefs.getLong("selected_time", System.currentTimeMillis())
    }

    private fun Long.hour(): Long = TimeUnit.MILLISECONDS.toHours(this)

    private fun Long.minute(): Long = TimeUnit.MILLISECONDS.toMinutes(this)

    private fun mobileType(): String {
        return prefs.getString("mobile_type", "lte")
    }

    private fun statStyle(): String {
        return prefs.getString("status_bar_style", "default")
    }

    private fun volumeIcon(): String {
        return prefs.getString("volume_icon", "hidden")
    }

    private fun btIcon(): String {
        return prefs.getString("bluetooth_icon", "hidden")
    }
}
