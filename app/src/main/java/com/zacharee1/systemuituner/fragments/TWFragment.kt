package com.zacharee1.systemuituner.fragments

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.provider.Settings
import android.view.Surface
import android.view.WindowManager
import androidx.preference.Preference
import androidx.preference.SwitchPreference
import com.jaredrummler.android.colorpicker.ColorPreferenceCompat
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.util.PrefManager.Companion.HIGH_BRIGHTNESS_WARNING
import com.zacharee1.systemuituner.util.PrefManager.Companion.NAVBAR_COLOR
import com.zacharee1.systemuituner.util.PrefManager.Companion.NAVBAR_CURRENT_COLOR
import com.zacharee1.systemuituner.util.PrefManager.Companion.TILE_COLUMN
import com.zacharee1.systemuituner.util.PrefManager.Companion.TILE_COLUMN_LANDSCAPE
import com.zacharee1.systemuituner.util.PrefManager.Companion.TILE_ROW
import com.zacharee1.systemuituner.util.PrefManager.Companion.TILE_ROW_LANDSCAPE
import com.zacharee1.systemuituner.util.prefs
import com.zacharee1.systemuituner.util.writeGlobal
import com.zacharee1.systemuituner.util.writeSecure
import com.zacharee1.systemuituner.util.writeSystem
import tk.zwander.seekbarpreference.SeekBarPreference

class TWFragment : StatbarFragment() {
    private var origRowCol = false

    override val prefsRes = R.xml.pref_tw

    override fun onSetTitle() = resources.getString(R.string.touchwiz)

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        super.onCreatePreferences(savedInstanceState, rootKey)

        setUpQSStuff()
        setUpNavBarStuff()
        preferenceListeners()
        switchPreferenceListeners()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        origRowCol = context!!.prefs.safeModeRowCol

        context!!.prefs.safeModeRowCol = false
    }

    override fun onDestroy() {
        context!!.prefs.safeModeRowCol = origRowCol

        super.onDestroy()
    }

    private fun setUpQSStuff() {
        val rotation = (activity?.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.rotation
        val landscape = rotation == Surface.ROTATION_180 || rotation == Surface.ROTATION_270

        val rows = findPreference(TILE_ROW) as SeekBarPreference
        val columns = findPreference(TILE_COLUMN) as SeekBarPreference
        val rowsLandscape = findPreference(TILE_ROW_LANDSCAPE) as SeekBarPreference
        val columnsLandscape = findPreference(TILE_COLUMN_LANDSCAPE) as SeekBarPreference

        val listener = Preference.OnPreferenceChangeListener { pref, value ->
            when (pref.key) {
                TILE_ROW -> {
                    if (!landscape) activity?.writeSecure(TILE_ROW, value.toString().toFloat().toInt())
                }

                TILE_ROW_LANDSCAPE -> {
                    if (landscape) activity?.writeSecure(TILE_ROW, value.toString().toFloat().toInt())
                }

                TILE_COLUMN -> {
                    if (!landscape) activity?.writeSecure(TILE_COLUMN, value.toString().toFloat().toInt())
                }

                TILE_COLUMN_LANDSCAPE -> {
                    if (landscape) activity?.writeSecure(TILE_COLUMN, value.toString().toFloat().toInt())
                }
            }
            true
        }

        rows.onPreferenceChangeListener = listener
        columns.onPreferenceChangeListener = listener
        rowsLandscape.onPreferenceChangeListener = listener
        columnsLandscape.onPreferenceChangeListener = listener
    }

    private fun setUpNavBarStuff() {
        val preference = findPreference(NAVBAR_COLOR) as ColorPreferenceCompat
        val savedVal = Settings.Global.getInt(context?.contentResolver, NAVBAR_COLOR, Color.WHITE)

        preference.saveValue(savedVal)
        preference.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, newValue ->
            context?.writeGlobal(NAVBAR_COLOR, newValue.toString())
            context?.writeGlobal(NAVBAR_CURRENT_COLOR, newValue.toString())
            true
        }
    }

    override fun switchPreferenceListeners() {
        super.switchPreferenceListeners()

        val hbw = findPreference(HIGH_BRIGHTNESS_WARNING) as SwitchPreference
        hbw.isChecked = Settings.System.getInt(activity?.contentResolver, hbw.key, 0) == 0

        hbw.setOnPreferenceChangeListener { pref, newValue ->
            activity?.writeSystem(pref.key, if (newValue.toString().toBoolean()) 0 else 1)
            true
        }
    }
}