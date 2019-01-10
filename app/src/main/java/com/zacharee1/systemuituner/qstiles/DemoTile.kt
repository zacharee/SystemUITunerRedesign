package com.zacharee1.systemuituner.qstiles

import android.annotation.TargetApi
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService

import com.zacharee1.systemuituner.handlers.DemoHandler

@TargetApi(24)
class DemoTile : TileService() {
    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == "com.android.systemui.demo") {
                val mode = intent.getStringExtra("command")

                if (mode != null) {
                    setState()
                }
            }
        }
    }
    private val demoHandler by lazy { DemoHandler(this) }

    override fun onStartListening() {
        super.onStartListening()

        val filter = IntentFilter("com.android.systemui.demo")

        try {
            unregisterReceiver(receiver)
        } catch (e: Exception) {
        }

        registerReceiver(receiver, filter)

        setState()
    }

    private fun setState() {
        if (demoHandler.isAllowed) {
            qsTile?.state = if (demoHandler.isEnabled) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
        } else {
            qsTile?.state = Tile.STATE_UNAVAILABLE
        }
        qsTile?.updateTile()
    }

    override fun onClick() {
        if (demoHandler.isEnabled) {
            demoHandler.hideDemo()
        } else {
            demoHandler.showDemo()
        }

        setState()

        super.onClick()
    }

    override fun onDestroy() {
        try {
            unregisterReceiver(receiver)
        } catch (e: Exception) {
        }

        super.onDestroy()
    }
}
