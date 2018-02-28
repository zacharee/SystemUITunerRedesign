package com.zacharee1.systemuituner.fragmenthelpers

import android.graphics.Color
import android.preference.Preference
import android.provider.Settings

import com.jaredrummler.android.colorpicker.ColorPreference
import com.zacharee1.sliderpreferenceembedded.SliderPreferenceEmbedded
import com.zacharee1.systemuituner.fragments.ItemDetailFragment
import com.zacharee1.systemuituner.util.SettingsUtils

class TWHelper(fragment: ItemDetailFragment) : BaseHelper(fragment) {

    private val statbarHelper: StatbarHelper = StatbarHelper(fragment)

    init {
        setUpQSStuff()
        setUpNavBarStuff()
    }

    private fun setUpQSStuff() {
        val rows = findPreference(TILE_ROW) as SliderPreferenceEmbedded
        val columns = findPreference(TILE_COLUMN) as SliderPreferenceEmbedded
        val defVal = 3
        val savedRowVal = Settings.Secure.getInt(activity?.contentResolver, rows.key, defVal)
        val savedColVal = Settings.Secure.getInt(activity?.contentResolver, columns.key, defVal)

        val listener = Preference.OnPreferenceChangeListener { preference, newValue ->
            sharedPreferences.edit().putInt(preference.key, Integer.valueOf(newValue.toString())).apply()
            SettingsUtils.writeSecure(activity, preference.key, newValue.toString())
            true
        }

        rows.onPreferenceChangeListener = listener
        columns.onPreferenceChangeListener = listener

        rows.progress = savedRowVal
        columns.progress = savedColVal
    }

    private fun setUpNavBarStuff() {
        val preference = findPreference(NAVBAR_COLOR) as ColorPreference
        val savedVal = Settings.Global.getInt(context?.contentResolver, NAVBAR_COLOR, Color.WHITE)

        preference.saveValue(savedVal)
        preference.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, newValue ->
            SettingsUtils.writeGlobal(context, NAVBAR_COLOR, newValue.toString())
            SettingsUtils.writeGlobal(context, NAVBAR_CURRENT_COLOR, newValue.toString())
            true
        }
    }

    override fun onDestroy() {
        statbarHelper.onDestroy()
    }

    companion object {
        const val TILE_ROW = "qs_tile_row"
        const val TILE_COLUMN = "qs_tile_column"
        const val NAVBAR_COLOR = "navigationbar_color"
        const val NAVBAR_CURRENT_COLOR = "navigationbar_current_color"
    }
}
