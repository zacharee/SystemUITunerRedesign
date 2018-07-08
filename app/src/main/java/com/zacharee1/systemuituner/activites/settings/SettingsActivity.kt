package com.zacharee1.systemuituner.activites.settings

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.preference.Preference
import android.preference.PreferenceCategory
import android.preference.SwitchPreference
import android.support.v4.content.ContextCompat
import android.view.MenuItem
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.activites.BaseAnimActivity
import com.zacharee1.systemuituner.activites.instructions.TaskerInstructionActivity
import com.zacharee1.systemuituner.fragments.AnimFragment
import com.zacharee1.systemuituner.services.SafeModeService

class SettingsActivity : BaseAnimActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_item_list)
        setTitle(R.string.settings)

        fragmentManager.beginTransaction().replace(R.id.content_main, GeneralPreferenceFragment()).commit()
    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    class GeneralPreferenceFragment : AnimFragment() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            addPreferencesFromResource(R.xml.settings_general)
            setHasOptionsMenu(true)
            setUpQSStuff()
            setSwitchListeners()
            setPreferenceListeners()
        }

        override fun onOptionsItemSelected(item: MenuItem): Boolean {
            val id = item.itemId
            if (id == android.R.id.home) {
                activity.finish()
                return true
            }
            return super.onOptionsItemSelected(item)
        }

        private fun setUpQSStuff() {
            val category = findPreference("quick_settings") as PreferenceCategory

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                category.isEnabled = false
                for (i in 0 until category.preferenceCount) {
                    category.getPreference(i).summary = resources.getText(R.string.requires_nougat)
                }
            }
        }

        private fun setSwitchListeners() {
            val taskerEnabled = findPreference("tasker_support_enabled") as SwitchPreference
            val safeMode = findPreference("safe_mode") as SwitchPreference
            val safeNotif = findPreference("show_safe_mode_notif") as SwitchPreference

            taskerEnabled.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, o ->
                val enabled = java.lang.Boolean.valueOf(o.toString())
                findPreference("tasker_instructions").isEnabled = enabled
                true
            }

            safeMode.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, newValue ->
                if (java.lang.Boolean.valueOf(newValue.toString())) {
                    activity.stopService(Intent(activity, SafeModeService::class.java))
                    ContextCompat.startForegroundService(activity, Intent(activity, SafeModeService::class.java))
                } else {
                    activity.stopService(Intent(activity, SafeModeService::class.java))
                }

                true
            }

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1) {
                safeNotif.isEnabled = false
                safeNotif.summary = resources.getText(R.string.safe_mode_notif_desc_not_supported)
            }
        }

        private fun setPreferenceListeners() {
            val tasker = findPreference("tasker_instructions")

            tasker.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                startActivity(Intent(context, TaskerInstructionActivity::class.java))
                true
            }

            tasker.isEnabled = preferenceManager.sharedPreferences.getBoolean("tasker_support_enabled", false)
        }
    }

    companion object {

        const val RECREATE_ACTIVITY = "recreate_activity"
    }
}
