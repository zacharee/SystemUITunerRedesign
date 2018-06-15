package com.zacharee1.systemuituner.fragments

import android.graphics.Color
import android.os.Bundle
import android.preference.Preference
import android.preference.PreferenceFragment
import android.preference.SwitchPreference
import android.provider.Settings
import com.jaredrummler.android.colorpicker.ColorPreference
import com.pavelsikun.seekbarpreference.SeekBarPreference
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.util.SettingsUtils

class TWFragment : PreferenceFragment() {
    override fun onResume() {
        super.onResume()

        activity.title = resources.getString(R.string.touchwiz)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.pref_tw)
        setUpQSStuff()
        setUpNavBarStuff()
        preferenceListeners()
        switchPreferenceListeners()
    }

    private fun setUpQSStuff() {
        val rows = findPreference(TILE_ROW) as SeekBarPreference
        val columns = findPreference(TILE_COLUMN) as SeekBarPreference
        val defVal = 3
        val savedRowVal = Settings.Secure.getInt(activity?.contentResolver, rows.key, defVal)
        val savedColVal = Settings.Secure.getInt(activity?.contentResolver, columns.key, defVal)

        val listener = Preference.OnPreferenceChangeListener { preference, newValue ->
            SettingsUtils.writeSecure(activity, preference.key, newValue.toString().toFloat().toInt().toString())
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
            SettingsUtils.writeGlobal(context, NAVBAR_COLOR, newValue.toString())
            SettingsUtils.writeGlobal(context, NAVBAR_CURRENT_COLOR, newValue.toString())
            true
        }
    }

    private fun preferenceListeners() {
        val resetBL = findPreference(StatbarFragment.RESET_BLACKLIST)
        val backupBL = findPreference(StatbarFragment.BACKUP_BLACKLIST)
        val restoreBL = findPreference(StatbarFragment.RESTORE_BLACKLIST)
        val auto = findPreference(StatbarFragment.AUTO_DETECT)

        resetBL?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            SettingsUtils.writeSecure(context, StatbarFragment.ICON_BLACKLIST, "")
            setSwitchPreferenceStates()
            true
        }

        backupBL?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            val currentBL = Settings.Secure.getString(context?.contentResolver, StatbarFragment.ICON_BLACKLIST)
            SettingsUtils.writeGlobal(context, StatbarFragment.ICON_BLACKLIST_BACKUP, currentBL)
            setSwitchPreferenceStates()
            true
        }

        restoreBL?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            val backupBLString = Settings.Global.getString(context?.contentResolver, StatbarFragment.ICON_BLACKLIST_BACKUP)
            SettingsUtils.writeSecure(context, StatbarFragment.ICON_BLACKLIST, backupBLString)
            setSwitchPreferenceStates()
            true
        }

        auto?.setOnPreferenceClickListener {
            val fragment = AutoFragment()
            fragmentManager?.beginTransaction()?.replace(R.id.content_main, fragment)?.addToBackStack("auto")?.commit()
            true
        }
    }

    private fun setSwitchPreferenceStates() {
        SettingsUtils.shouldSetSwitchChecked(this)
    }

    private fun switchPreferenceListeners() {
        (0 until preferenceScreen.rootAdapter.count)
                .map { preferenceScreen.rootAdapter.getItem(it) }
                .filterIsInstance<SwitchPreference>()
                .forEach {
                    it.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { preference, o ->
                        val key = preference.key
                        val value = java.lang.Boolean.valueOf(o.toString())

                        SettingsUtils.changeBlacklist(key, value, context)
                        true
                    }
                }
    }

    companion object {
        const val TILE_ROW = "qs_tile_row"
        const val TILE_COLUMN = "qs_tile_column"
        const val NAVBAR_COLOR = "navigationbar_color"
        const val NAVBAR_CURRENT_COLOR = "navigationbar_current_color"
    }
}