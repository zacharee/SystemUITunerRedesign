package com.zacharee1.systemuituner.fragments

import android.graphics.Color
import android.preference.Preference
import android.provider.Settings
import com.jaredrummler.android.colorpicker.ColorPreference
import com.pavelsikun.seekbarpreference.SeekBarPreference
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.util.writeGlobal
import com.zacharee1.systemuituner.util.writeSecure

class TWFragment : StatbarFragment() {
    companion object {
        const val TILE_ROW = "qs_tile_row"
        const val TILE_COLUMN = "qs_tile_column"
        const val NAVBAR_COLOR = "navigationbar_color"
        const val NAVBAR_CURRENT_COLOR = "navigationbar_current_color"
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

    private fun setUpQSStuff() {
        val rows = findPreference(TILE_ROW) as SeekBarPreference
        val columns = findPreference(TILE_COLUMN) as SeekBarPreference
        val defVal = 3
        val savedRowVal = Settings.Secure.getInt(activity?.contentResolver, rows.key, defVal)
        val savedColVal = Settings.Secure.getInt(activity?.contentResolver, columns.key, defVal)

        val listener = Preference.OnPreferenceChangeListener { preference, newValue ->
            activity.writeSecure(preference.key, newValue.toString().toFloat().toInt())
            true
        }

        rows.onPreferenceChangeListener = listener
        columns.onPreferenceChangeListener = listener

        rows.currentValue = savedRowVal
        columns.currentValue = savedColVal
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
}