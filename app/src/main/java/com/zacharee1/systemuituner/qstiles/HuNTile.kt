package com.zacharee1.systemuituner.qstiles

import android.annotation.TargetApi
import android.database.ContentObserver
import android.graphics.drawable.Icon
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.fragments.MiscFragment
import com.zacharee1.systemuituner.util.SettingsUtils

@TargetApi(24)
class HuNTile : TileService() {
    private val observer = object : ContentObserver(Handler(Looper.getMainLooper())) {
        override fun onChange(selfChange: Boolean, uri: Uri) {
            if (uri == Settings.Global.getUriFor(MiscFragment.HUD_ENABLED)) {
                setState()
            }
        }
    }

    private val isEnabled: Boolean
        get() = Settings.Global.getInt(contentResolver, MiscFragment.HUD_ENABLED, 1) == 1

    override fun onStartListening() {
        super.onStartListening()

        contentResolver.registerContentObserver(Settings.Global.CONTENT_URI, true, observer)

        setState()
    }

    private fun setState() {
        qsTile?.state = if (isEnabled) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
        setIcon()
        qsTile?.updateTile()
    }

    private fun setIcon() {
        qsTile?.icon = Icon.createWithResource(this, if (isEnabled) R.drawable.ic_notifications_black_24dp else R.drawable.ic_notifications_off_black_24dp)
    }

    override fun onClick() {
        SettingsUtils.writeGlobal(this, MiscFragment.HUD_ENABLED, if (isEnabled) "0" else "1")
        setState()

        super.onClick()
    }

    override fun onDestroy() {
        try {
            contentResolver.unregisterContentObserver(observer)
        } catch (e: Exception) {
        }

        super.onDestroy()
    }
}
