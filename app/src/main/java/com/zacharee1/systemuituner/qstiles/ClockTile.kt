package com.zacharee1.systemuituner.qstiles

import android.annotation.TargetApi
import android.content.*
import android.os.Handler
import android.os.Looper
import android.preference.PreferenceManager
import android.provider.AlarmClock
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.widget.Toast
import com.zacharee1.systemuituner.R
import java.text.SimpleDateFormat
import java.util.*

@TargetApi(24)
class ClockTile : TileService() {
    companion object {
        const val FORMAT = "clock_format"
        const val TWELVE = "twelve"
        const val TWELVE_NOSEC = "twelve_nosec"
        const val MILITARY = "military"
        const val MILITARY_NOSEC = "military_nosec"

        const val FORMAT_12 = "h:mm:ss a"
        const val FORMAT_12_NOSEC = "h:mm a"
        const val FORMAT_24 = "HH:mm:ss"
        const val FORMAT_24_NOSEC = "HH:mm"
    }

    private val mReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            setTime()
        }
    }
    private val handler = Handler(Looper.getMainLooper())
    private val prefsListener = SharedPreferences.OnSharedPreferenceChangeListener { _, string ->
        if (string == FORMAT) {
            setTime()
        }
    }

    private lateinit var prefs: SharedPreferences

    private var shouldRun = false

    override fun onStartListening() {
        super.onStartListening()
        prefs = PreferenceManager.getDefaultSharedPreferences(this)
        shouldRun = true

        registerReceiver(mReceiver, IntentFilter(Intent.ACTION_TIME_TICK))
        prefs.registerOnSharedPreferenceChangeListener(prefsListener)
        setTime()

        qsTile?.state = Tile.STATE_ACTIVE
        qsTile?.updateTile()
    }

    override fun onStopListening() {
        shouldRun = false
        prefs.unregisterOnSharedPreferenceChangeListener(prefsListener)
        try {
            unregisterReceiver(mReceiver)
        } catch (e: Exception) {}
        super.onStopListening()
    }

    override fun onClick() {
        openClock()

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

    private fun openClock() {
        val intentClock = Intent(AlarmClock.ACTION_SHOW_ALARMS)
        intentClock.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        try {
            startActivityAndCollapse(intentClock)
        } catch (e: Exception) {
            Toast.makeText(this, resources.getString(R.string.target_not_found), Toast.LENGTH_SHORT).show()
        }
    }

    private fun setTime() {
        val formatOption = prefs.getString(FORMAT, TWELVE)

        val format = when (formatOption) {
            TWELVE_NOSEC -> FORMAT_12_NOSEC
            MILITARY -> FORMAT_24
            MILITARY_NOSEC -> FORMAT_24_NOSEC
            else -> FORMAT_12
        }

        shouldRun = formatOption == TWELVE || formatOption == MILITARY

        val date = SimpleDateFormat(format, Locale.getDefault()).format(Date(System.currentTimeMillis()))

        qsTile?.label = date
        qsTile?.updateTile()

        if (shouldRun) handler.postDelayed({setTime()}, 1000)
    }
}
