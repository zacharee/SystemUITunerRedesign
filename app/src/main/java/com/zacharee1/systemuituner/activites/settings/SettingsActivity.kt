package com.zacharee1.systemuituner.activites.settings

import android.annotation.TargetApi
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.preference.Preference
import android.preference.PreferenceFragment
import android.preference.SwitchPreference
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import android.view.MenuItem
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.activites.instructions.TaskerInstructionActivity
import com.zacharee1.systemuituner.handlers.RecreateHandler
import com.zacharee1.systemuituner.services.SafeModeService
import com.zacharee1.systemuituner.util.Utils

class SettingsActivity : AppCompatPreferenceActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(if (Utils.isInDarkMode(this)) R.style.AppTheme_Dark else R.style.AppTheme)

        RecreateHandler.onCreate(this)

        super.onCreate(savedInstanceState)
        setupActionBar()
        fragmentManager.beginTransaction().replace(android.R.id.content, GeneralPreferenceFragment()).commit()
    }

    override fun onDestroy() {
        RecreateHandler.onDestroy(this)
        super.onDestroy()
    }

    /**
     * Set up the [android.app.ActionBar], if the API is available.
     */
    private fun setupActionBar() {
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
    }

    /**
     * {@inheritDoc}
     */
    override fun onIsMultiPane(): Boolean {
        return false
    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    override fun isValidFragment(fragmentName: String): Boolean {
        Log.e("Gen", GeneralPreferenceFragment::class.java.name)
        return PreferenceFragment::class.java.name == fragmentName || GeneralPreferenceFragment::class.java.name == fragmentName
    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    class GeneralPreferenceFragment : PreferenceFragment() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            addPreferencesFromResource(R.xml.settings_general)
            setHasOptionsMenu(true)
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

        private fun setSwitchListeners() {
            val darkMode = findPreference("dark_mode") as SwitchPreference
            val taskerEnabled = findPreference("tasker_support_enabled") as SwitchPreference
            val safeMode = findPreference("safe_mode") as SwitchPreference

            darkMode.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, _ ->
                val intent = Intent()
                intent.action = RECREATE_ACTIVITY
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
                true
            }

            taskerEnabled.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, o ->
                val enabled = java.lang.Boolean.valueOf(o.toString())
                findPreference("tasker_instructions").isEnabled = enabled
                true
            }

            safeMode.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, newValue ->
                if (java.lang.Boolean.valueOf(newValue.toString())) {
                    activity.stopService(Intent(activity, SafeModeService::class.java))
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                        activity.startService(Intent(activity, SafeModeService::class.java))
                    } else {
                        activity.startForegroundService(Intent(activity, SafeModeService::class.java))
                    }
                } else {
                    activity.stopService(Intent(activity, SafeModeService::class.java))
                }

                true
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
