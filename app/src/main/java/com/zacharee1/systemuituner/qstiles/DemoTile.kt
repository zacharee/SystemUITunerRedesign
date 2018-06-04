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
    private var mReceiver: BroadcastReceiver? = null
    private var mDemoHandler: DemoHandler? = null

    override fun onStartListening() {
        super.onStartListening()

        mDemoHandler = DemoHandler(this)

        mReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (intent.action == "com.android.systemui.demo") {
                    val mode = intent.getStringExtra("command")

                    if (mode != null) {
                        setState()
                    }
                }
            }
        }

        val filter = IntentFilter("com.android.systemui.demo")

        try {
            unregisterReceiver(mReceiver)
        } catch (e: Exception) {
        }

        registerReceiver(mReceiver, filter)

        setState()
    }

    private fun setState() {
        if (mDemoHandler!!.isAllowed) {
            qsTile?.state = if (mDemoHandler!!.isEnabled) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
        } else {
            qsTile?.state = Tile.STATE_UNAVAILABLE
        }
        qsTile?.updateTile()
    }

    override fun onClick() {
        if (mDemoHandler!!.isEnabled) {
            mDemoHandler!!.hideDemo()
        } else {
            mDemoHandler!!.showDemo()
        }

        setState()

        super.onClick()
    }

    override fun onDestroy() {
        try {
            unregisterReceiver(mReceiver)
        } catch (e: Exception) {
        }

        super.onDestroy()
    }
}
