package com.zacharee1.systemuituner.handlers

import android.content.Context
import android.content.Intent
import android.provider.Settings
import com.zacharee1.systemuituner.util.prefs
import com.zacharee1.systemuituner.util.writeGlobal
import java.util.concurrent.TimeUnit

class DemoHandler(private val context: Context) {
    val isAllowed: Boolean
        get() = Settings.Global.getInt(context.contentResolver, "sysui_demo_allowed", 0) == 1

    val isEnabled: Boolean
        get() = Settings.Global.getInt(context.contentResolver, "sysui_tuner_demo_on", 0) == 1

    fun showDemo() {
        try {
            var intent = Intent("com.android.systemui.demo")
            intent.putExtra("command", "enter")
            context.sendBroadcast(intent)

            intent = Intent("com.android.systemui.demo")
            intent.putExtra("command", "clock")
            val time = context.prefs.selectedTime
            intent.putExtra("hhmm", String.format("%02d%02d", time.hour(), time.minute()))
            context.sendBroadcast(intent)

            intent = Intent("com.android.systemui.demo")
            intent.putExtra("command", "network")
            intent.putExtra("mobile", if (context.prefs.showMobile) "show" else "hide")
            intent.putExtra("fully", context.prefs.mobileFullyConnected)
            intent.putExtra("level", "${context.prefs.selectedMobileStrength}")
            intent.putExtra("datatype", context.prefs.mobileType)
            context.sendBroadcast(intent)

            intent = Intent("com.android.systemui.demo")
            intent.putExtra("command", "network")
            intent.putExtra("sims", "${context.prefs.simCount + 1}")
            intent.putExtra("nosim", if (context.prefs.noSim) "show" else "hide")
            context.sendBroadcast(intent)

            intent = Intent("com.android.systemui.demo")
            intent.putExtra("command", "network")
            intent.putExtra("wifi", if (context.prefs.showWiFi) "show" else "hide")
            intent.putExtra("fully", context.prefs.wifiFullyConnected)
            intent.putExtra("level", "${context.prefs.wifiStrength}")
            context.sendBroadcast(intent)

            intent = Intent("com.android.systemui.demo")
            intent.putExtra("command", "network")
            intent.putExtra("airplane", if (context.prefs.showAirplane) "show" else "hide")
            context.sendBroadcast(intent)

            intent = Intent("com.android.systemui.demo")
            intent.putExtra("command", "battery")
            intent.putExtra("level", "${context.prefs.selectedBatteryLevel}")
            intent.putExtra("plugged", "${context.prefs.batteryCharging}")
            context.sendBroadcast(intent)

            intent = Intent("com.android.systemui.demo")
            intent.putExtra("command", "notifications")
            intent.putExtra("visible", "${context.prefs.showNotifications}")
            context.sendBroadcast(intent)

            intent = Intent("com.android.systemui.demo")
            intent.putExtra("command", "bars")
            intent.putExtra("mode", context.prefs.statusBarStyle)
            context.sendBroadcast(intent)

            intent = Intent("com.android.systemui.demo")
            intent.putExtra("command", "status")
            intent.putExtra("volume", context.prefs.volumeIcon)
            intent.putExtra("bluetooth", context.prefs.bluetoothIcon)
            intent.putExtra("location", if (context.prefs.locationDemo) "show" else "hide")
            intent.putExtra("alarm", if (context.prefs.alarmDemo) "show" else "hide")
            intent.putExtra("sync", if (context.prefs.syncDemo) "show" else "hide")
            intent.putExtra("tty", if (context.prefs.ttyDemo) "show" else "hide")
            intent.putExtra("eri", if (context.prefs.eriDemo) "show" else "hide")
            intent.putExtra("mute", if (context.prefs.muteDemo) "show" else "hide")
            intent.putExtra("speakerphone", if (context.prefs.speakerphoneDemo) "show" else "hide")
            context.sendBroadcast(intent)

            context.writeGlobal("sysui_tuner_demo_on", 1)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun hideDemo() {
        val intent = Intent("com.android.systemui.demo")
        intent.putExtra("command", "exit")
        context.sendBroadcast(intent)

        context.writeGlobal("sysui_tuner_demo_on", 0)
    }

    private fun Long.hour(): Long = TimeUnit.MILLISECONDS.toHours(this)

    private fun Long.minute(): Long = TimeUnit.MILLISECONDS.toMinutes(this)
}
