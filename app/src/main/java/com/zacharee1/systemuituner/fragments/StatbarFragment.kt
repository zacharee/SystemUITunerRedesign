package com.zacharee1.systemuituner.fragments

import android.preference.Preference
import android.preference.SwitchPreference
import android.provider.Settings
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.util.SettingsUtils

class StatbarFragment : AnimFragment() {
    override fun onResume() {
        super.onResume()

        activity.title = resources.getString(R.string.status_bar)
    }

    override fun onAnimationFinished(enter: Boolean) {
        if (enter) {
            addPreferencesFromResource(R.xml.pref_statbar)
            preferenceListeners()
            setSwitchPreferenceStates()
            switchPreferenceListeners()
        }
    }

    private fun preferenceListeners() {
        val resetBL = findPreference(RESET_BLACKLIST)
        val backupBL = findPreference(BACKUP_BLACKLIST)
        val restoreBL = findPreference(RESTORE_BLACKLIST)
        val auto = findPreference(AUTO_DETECT)

        resetBL?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            SettingsUtils.writeSecure(context, ICON_BLACKLIST, "")
            setSwitchPreferenceStates()
            true
        }

        backupBL?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            val currentBL = Settings.Secure.getString(context?.contentResolver, ICON_BLACKLIST)
            SettingsUtils.writeGlobal(context, ICON_BLACKLIST_BACKUP, currentBL)
            setSwitchPreferenceStates()
            true
        }

        restoreBL?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            val backupBLString = Settings.Global.getString(context?.contentResolver, ICON_BLACKLIST_BACKUP)
            SettingsUtils.writeSecure(context, ICON_BLACKLIST, backupBLString)
            setSwitchPreferenceStates()
            true
        }

        auto?.setOnPreferenceClickListener {
            val fragment = AutoFragment()
            fragmentManager
                    ?.beginTransaction()
                    ?.setCustomAnimations(R.animator.pop_in, R.animator.pop_out, R.animator.pop_in, R.animator.pop_out)
                    ?.replace(R.id.content_main, fragment)
                    ?.addToBackStack("auto")
                    ?.commit()
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
        const val RESET_BLACKLIST = "reset_blacklist"
        const val BACKUP_BLACKLIST = "backup_blacklist"
        const val RESTORE_BLACKLIST = "restore_blacklist"
        const val ICON_BLACKLIST = "icon_blacklist"
        const val ICON_BLACKLIST_BACKUP = "icon_blacklist_backup"
        const val AUTO_DETECT = "auto_detect"
    }
}