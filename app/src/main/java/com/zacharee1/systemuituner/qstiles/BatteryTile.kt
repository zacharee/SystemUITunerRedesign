package com.zacharee1.systemuituner.qstiles

import android.annotation.TargetApi
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.drawable.Icon
import android.os.BatteryManager
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.widget.Toast

import com.zacharee1.systemuituner.R

@TargetApi(24)
class BatteryTile : TileService() {
    private var mReceiver: BroadcastReceiver? = null

    override fun onStartListening() {
        super.onStartListening()

        mReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                setLevel(intent)
            }
        }

        registerReceiver(mReceiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))

        qsTile?.state = Tile.STATE_ACTIVE
        qsTile?.updateTile()
    }

    override fun onClick() {
        val intentBatteryUsage = Intent(Intent.ACTION_POWER_USAGE_SUMMARY)
        intentBatteryUsage.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        try {
            startActivityAndCollapse(intentBatteryUsage)
        } catch (e: Exception) {
            Toast.makeText(this, resources.getString(R.string.target_not_found), Toast.LENGTH_SHORT).show()
        }

        super.onClick()
    }

    override fun onDestroy() {
        try {
            unregisterReceiver(mReceiver)
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }

        super.onDestroy()
    }

    private fun setLevel(intent: Intent) {
        val batteryTile = qsTile
        val batteryStatus = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
        val batteryCharging = batteryStatus == BatteryManager.BATTERY_STATUS_CHARGING || batteryStatus == BatteryManager.BATTERY_STATUS_FULL
        val batteryLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val resId: Int

        resId = when {
            batteryLevel >= 95 -> if (batteryCharging) R.drawable.ic_battery_charging_full_black_24dp else R.drawable.ic_battery_full_black_24dp
            batteryLevel >= 85 -> if (batteryCharging) R.drawable.ic_battery_charging_90_black_24dp else R.drawable.ic_battery_90_black_24dp
            batteryLevel >= 70 -> if (batteryCharging) R.drawable.ic_battery_charging_80_black_24dp else R.drawable.ic_battery_80_black_24dp
            batteryLevel >= 55 -> if (batteryCharging) R.drawable.ic_battery_charging_60_black_24dp else R.drawable.ic_battery_60_black_24dp
            batteryLevel >= 40 -> if (batteryCharging) R.drawable.ic_battery_charging_50_black_24dp else R.drawable.ic_battery_50_black_24dp
            batteryLevel >= 25 -> if (batteryCharging) R.drawable.ic_battery_charging_30_black_24dp else R.drawable.ic_battery_30_black_24dp
            batteryLevel >= 15 -> if (batteryCharging) R.drawable.ic_battery_charging_20_black_24dp else R.drawable.ic_battery_20_black_24dp
            else -> if (batteryCharging) R.drawable.ic_battery_charging_20_black_24dp else R.drawable.ic_battery_alert_black_24dp
        }

        batteryTile?.icon = Icon.createWithResource(this, resId)
        batteryTile?.label = batteryLevel.toString() + "%"
        batteryTile?.updateTile()
    }
}
