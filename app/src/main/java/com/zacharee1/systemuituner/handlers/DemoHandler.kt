package com.zacharee1.systemuituner.handlers

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.provider.Settings
import com.zacharee1.systemuituner.util.SettingsUtils

class DemoHandler(private val mContext: Context) {
    private val mPrefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext)

    val isAllowed: Boolean
        get() = Settings.Global.getInt(mContext.contentResolver, "sysui_demo_allowed", 0) == 1

    val isEnabled: Boolean
        get() = Settings.Global.getInt(mContext.contentResolver, "sysui_tuner_demo_on", 0) == 1

    fun showDemo() {
        try {
            //create new Intent and add relevant data to show Demo Mode with specified options
            var intent = Intent("com.android.systemui.demo")
            intent.putExtra("command", "enter")
            mContext.sendBroadcast(intent)

            //            logIntent(intent);

            intent = Intent("com.android.systemui.demo")
            intent.putExtra("command", "clock")
            //            intent.putExtra("hhmm", (timeHour() < 10 ? "0" + timeHour() : timeHour()) + "" + (timeMinute() < 10 ? "0" + timeMinute() : timeMinute()));
            intent.putExtra("millis", time().toString() + "")
            mContext.sendBroadcast(intent)

            //            logIntent(intent);

            intent = Intent("com.android.systemui.demo")
            intent.putExtra("command", "network")
            intent.putExtra("mobile", if (showMobile()) "show" else "hide")
            intent.putExtra("fully", mobileFully())
            intent.putExtra("level", mobileLevel().toString() + "")
            intent.putExtra("datatype", mobileType())
            mContext.sendBroadcast(intent)

            //            logIntent(intent);

            intent = Intent("com.android.systemuui.demo")
            intent.putExtra("command", "network")
            intent.putExtra("sims", simCount().toString() + "")
            intent.putExtra("nosim", if (noSim()) "show" else "hide")
            mContext.sendBroadcast(intent)

            //            logIntent(intent);

            intent = Intent("com.android.systemui.demo")
            intent.putExtra("command", "network")
            intent.putExtra("wifi", if (showWiFi()) "show" else "hide")
            intent.putExtra("fully", wifiFully())
            intent.putExtra("level", wifiLevel().toString() + "")
            mContext.sendBroadcast(intent)

            //            logIntent(intent);

            intent = Intent("com.android.systemui.demo")
            intent.putExtra("command", "network")
            intent.putExtra("airplane", if (showAirplane()) "show" else "hide")
            mContext.sendBroadcast(intent)

            //            logIntent(intent);

            intent = Intent("com.android.systemui.demo")
            intent.putExtra("command", "battery")
            intent.putExtra("level", batteryLevel().toString() + "")
            intent.putExtra("plugged", batteryCharging().toString() + "")
            mContext.sendBroadcast(intent)

            //            logIntent(intent);

            intent = Intent("com.android.systemui.demo")
            intent.putExtra("command", "notifications")
            intent.putExtra("visible", showNotifs().toString() + "")
            mContext.sendBroadcast(intent)

            //            logIntent(intent);

            intent = Intent("com.android.systemui.demo")
            intent.putExtra("command", "bars")
            intent.putExtra("mode", statStyle())
            mContext.sendBroadcast(intent)

            //            logIntent(intent);

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
            mContext.sendBroadcast(intent)

            SettingsUtils.writeGlobal(mContext, "sysui_tuner_demo_on", "1")
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun hideDemo() {
        val intent = Intent("com.android.systemui.demo")
        intent.putExtra("command", "exit")
        mContext.sendBroadcast(intent)

        SettingsUtils.writeGlobal(mContext, "sysui_tuner_demo_on", "0")
    }

    private fun showNotifs(): Boolean {
        return mPrefs.getBoolean("show_notifications", false)
    }

    private fun showMobile(): Boolean {
        return mPrefs.getBoolean("show_mobile", false)
    }

    private fun showAirplane(): Boolean {
        return mPrefs.getBoolean("show_airplane", false)
    }

    private fun showWiFi(): Boolean {
        return mPrefs.getBoolean("show_wifi", false)
    }

    private fun batteryCharging(): Boolean {
        return mPrefs.getBoolean("battery_charging", false)
    }

    private fun mobileFully(): Boolean {
        return mPrefs.getBoolean("mobile_fully_connected", false)
    }

    private fun wifiFully(): Boolean {
        return mPrefs.getBoolean("wifi_fully_connected", false)
    }

    private fun noSim(): Boolean {
        return mPrefs.getBoolean("no_sim", false)
    }

    private fun location(): Boolean {
        return mPrefs.getBoolean("location_demo", false)
    }

    private fun alarm(): Boolean {
        return mPrefs.getBoolean("alarm_demo", false)
    }

    private fun sync(): Boolean {
        return mPrefs.getBoolean("sync_demo", false)
    }

    private fun tty(): Boolean {
        return mPrefs.getBoolean("tty_demo", false)
    }

    private fun eri(): Boolean {
        return mPrefs.getBoolean("eri_demo", false)
    }

    private fun mute(): Boolean {
        return mPrefs.getBoolean("mute_demo", false)
    }

    private fun spkphone(): Boolean {
        return mPrefs.getBoolean("speakerphone_demo", false)
    }

    private fun batteryLevel(): Int {
        return mPrefs.getInt("selected_battery_level", 100)
    }

    private fun wifiLevel(): Int {
        return mPrefs.getInt("wifi_strength", 4)
    }

    private fun mobileLevel(): Int {
        return mPrefs.getInt("selected_mobile_strength", 4)
    }

    private fun simCount(): Int {
        return mPrefs.getInt("sim_count", 0) + 1
    }

    private fun time(): Long {
        return mPrefs.getLong("selected_time", System.currentTimeMillis())
    }

    private fun mobileType(): String {
        return mPrefs.getString("mobile_type", "lte")
    }

    private fun statStyle(): String {
        return mPrefs.getString("status_bar_style", "default")
    }

    private fun volumeIcon(): String {
        return mPrefs.getString("volume_icon", "hidden")
    }

    private fun btIcon(): String {
        return mPrefs.getString("bluetooth_icon", "hidden")
    }
}
