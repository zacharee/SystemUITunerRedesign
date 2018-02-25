package com.zacharee1.systemuituner.fragmenthelpers

import android.content.Intent
import android.os.Bundle
import android.preference.Preference
import android.preference.SwitchPreference
import android.provider.Settings
import android.view.View
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.activites.ItemDetailActivity

import com.zacharee1.systemuituner.fragments.ItemDetailFragment
import com.zacharee1.systemuituner.util.SettingsUtils

class StatbarHelper(fragment: ItemDetailFragment) : BaseHelper(fragment) {

    init {
        preferenceListeners()
        setSwitchPreferenceStates()
        switchPreferenceListeners()
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
            val currentBL = Settings.Secure.getString(context.contentResolver, ICON_BLACKLIST)
            SettingsUtils.writeGlobal(context, ICON_BLACKLIST_BACKUP, currentBL)
            setSwitchPreferenceStates()
            true
        }

        restoreBL?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            val backupBLString = Settings.Global.getString(context.contentResolver, ICON_BLACKLIST_BACKUP)
            SettingsUtils.writeSecure(context, ICON_BLACKLIST, backupBLString)
            setSwitchPreferenceStates()
            true
        }

        auto?.setOnPreferenceClickListener {
            if (fragment.activity.findViewById<View>(R.id.item_detail_container) != null) {
                val arguments = Bundle()
                arguments.putString(ItemDetailFragment.ARG_ITEM_ID, "auto")
                val fragment = ItemDetailFragment()
                fragment.arguments = arguments
                activity
                        ?.fragmentManager
                        ?.beginTransaction()
                        ?.replace(R.id.item_detail_container, fragment)
                        ?.commit()
            } else {
                val intent = Intent(context, ItemDetailActivity::class.java)
                intent.putExtra(ItemDetailFragment.ARG_ITEM_ID, "auto")

                context.startActivity(intent)
            }
            true
        }
    }

    private fun setSwitchPreferenceStates() {
        SettingsUtils.shouldSetSwitchChecked(fragment)
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

    override fun onDestroy() {

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
