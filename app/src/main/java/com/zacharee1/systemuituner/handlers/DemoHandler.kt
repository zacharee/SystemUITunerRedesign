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
            val time = context.prefs.demoModeSelectedTime
            intent.putExtra("hhmm", String.format("%02d%02d", time.hour(), time.minute()))
            context.sendBroadcast(intent)

            intent = Intent("com.android.systemui.demo")
            intent.putExtra("command", "network")
            intent.putExtra("mobile", if (context.prefs.demoModeShowMobileIcon) "show" else "hide")
            intent.putExtra("fully", context.prefs.demoModeMobileFullyConnected)
            intent.putExtra("level", "${context.prefs.demoModeMobileStrength}")
            intent.putExtra("datatype", context.prefs.demoModeMobileType)
            context.sendBroadcast(intent)

            intent = Intent("com.android.systemui.demo")
            intent.putExtra("command", "network")
            intent.putExtra("sims", "${context.prefs.demoModeSIMCount + 1}")
            intent.putExtra("nosim", if (context.prefs.demoModeNoSIM) "show" else "hide")
            context.sendBroadcast(intent)

            intent = Intent("com.android.systemui.demo")
            intent.putExtra("command", "network")
            intent.putExtra("wifi", if (context.prefs.demoModeShowWiFi) "show" else "hide")
            intent.putExtra("fully", context.prefs.demoModeWiFiFullyConnected)
            intent.putExtra("level", "${context.prefs.demoModeWiFiStrength}")
            context.sendBroadcast(intent)

            intent = Intent("com.android.systemui.demo")
            intent.putExtra("command", "network")
            intent.putExtra("airplane", if (context.prefs.demoModeShowAirplaneMode) "show" else "hide")
            context.sendBroadcast(intent)

            intent = Intent("com.android.systemui.demo")
            intent.putExtra("command", "battery")
            intent.putExtra("level", "${context.prefs.demoModeBatteryLevel}")
            intent.putExtra("plugged", "${context.prefs.demoModeBatteryCharging}")
            context.sendBroadcast(intent)

            intent = Intent("com.android.systemui.demo")
            intent.putExtra("command", "notifications")
            intent.putExtra("visible", "${context.prefs.demoModeShowNotifs}")
            context.sendBroadcast(intent)

            intent = Intent("com.android.systemui.demo")
            intent.putExtra("command", "bars")
            intent.putExtra("mode", context.prefs.demoModeStatusBarStyle)
            context.sendBroadcast(intent)

            intent = Intent("com.android.systemui.demo")
            intent.putExtra("command", "status")
            intent.putExtra("volume", context.prefs.demoModeVolumeIcon)
            intent.putExtra("bluetooth", context.prefs.demoModeBluetoothIconState)
            intent.putExtra("location", if (context.prefs.demoModeShowLocation) "show" else "hide")
            intent.putExtra("alarm", if (context.prefs.demoModeAlarm) "show" else "hide")
            intent.putExtra("sync", if (context.prefs.demoModeShowSync) "show" else "hide")
            intent.putExtra("tty", if (context.prefs.demoModeShowTTY) "show" else "hide")
            intent.putExtra("eri", if (context.prefs.demoModeShowEri) "show" else "hide")
            intent.putExtra("mute", if (context.prefs.demoModeShowMute) "show" else "hide")
            intent.putExtra("speakerphone", if (context.prefs.demoModeShowSpkerPhone) "show" else "hide")
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
