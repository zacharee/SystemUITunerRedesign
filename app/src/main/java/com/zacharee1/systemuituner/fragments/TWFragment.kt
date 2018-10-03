package com.zacharee1.systemuituner.fragments

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.preference.Preference
import android.preference.SwitchPreference
import android.provider.Settings
import android.view.Surface
import android.view.WindowManager
import com.jaredrummler.android.colorpicker.ColorPreference
import com.pavelsikun.seekbarpreference.SeekBarPreference
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.services.SafeModeService
import com.zacharee1.systemuituner.util.writeGlobal
import com.zacharee1.systemuituner.util.writeSecure
import com.zacharee1.systemuituner.util.writeSystem

class TWFragment : StatbarFragment() {
    private var origRowCol = false

    companion object {
        const val TILE_ROW = "qs_tile_row"
        const val TILE_COLUMN = "qs_tile_column"
        const val TILE_ROW_LANDSCAPE = "qs_tile_row_landscape"
        const val TILE_COLUMN_LANDSCAPE = "qs_tile_column_landscape"
        const val NAVBAR_COLOR = "navigationbar_color"
        const val NAVBAR_CURRENT_COLOR = "navigationbar_current_color"
        const val HIGH_BRIGHTNESS_WARNING = "shown_max_brightness_dialog"
    }

    override fun onSetTitle() = resources.getString(R.string.touchwiz)

    override fun onAnimationFinishedEnter(enter: Boolean) {
        if (enter) {
            addPreferencesFromResource(R.xml.pref_tw)
            setUpQSStuff()
            setUpNavBarStuff()
            preferenceListeners()
            switchPreferenceListeners()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        preferenceManager.sharedPreferences.apply {
            origRowCol = getBoolean(SafeModeService.SAFE_MODE_ROW_COL, true)

            edit().apply {
                putBoolean(SafeModeService.SAFE_MODE_ROW_COL, false)
            }.apply()
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        preferenceManager.sharedPreferences.edit().apply {
            putBoolean(SafeModeService.SAFE_MODE_ROW_COL, origRowCol)
        }.apply()
    }

    private fun setUpQSStuff() {
        val rotation = (activity.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.rotation
        val landscape = rotation == Surface.ROTATION_180 || rotation == Surface.ROTATION_270

        val rows = findPreference(TILE_ROW) as SeekBarPreference
        val columns = findPreference(TILE_COLUMN) as SeekBarPreference
        val rowsLandscape = findPreference(TILE_ROW_LANDSCAPE) as SeekBarPreference
        val columnsLandscape = findPreference(TILE_COLUMN_LANDSCAPE) as SeekBarPreference

        val listener = Preference.OnPreferenceChangeListener { pref, value ->
            when (pref.key) {
                TILE_ROW -> {
                    if (!landscape) activity.writeSecure(TILE_ROW, value.toString().toFloat().toInt())
                }

                TILE_ROW_LANDSCAPE -> {
                    if (landscape) activity.writeSecure(TILE_ROW, value.toString().toFloat().toInt())
                }

                TILE_COLUMN -> {
                    if (!landscape) activity.writeSecure(TILE_COLUMN, value.toString().toFloat().toInt())
                }

                TILE_COLUMN_LANDSCAPE -> {
                    if (landscape) activity.writeSecure(TILE_COLUMN, value.toString().toFloat().toInt())
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
        val preference = findPreference(NAVBAR_COLOR) as ColorPreference
        val savedVal = Settings.Global.getInt(context?.contentResolver, NAVBAR_COLOR, Color.WHITE)

        preference.saveValue(savedVal)
        preference.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, newValue ->
            context.writeGlobal(NAVBAR_COLOR, newValue.toString())
            context.writeGlobal(NAVBAR_CURRENT_COLOR, newValue.toString())
            true
        }
    }

    override fun switchPreferenceListeners() {
        super.switchPreferenceListeners()

        val hbw = findPreference(HIGH_BRIGHTNESS_WARNING) as SwitchPreference
        hbw.isChecked = Settings.System.getInt(activity.contentResolver, hbw.key, 0) == 0

        hbw.setOnPreferenceChangeListener { pref, newValue ->
            activity.writeSystem(pref.key, if (newValue.toString().toBoolean()) 0 else 1)
        }
    }
}