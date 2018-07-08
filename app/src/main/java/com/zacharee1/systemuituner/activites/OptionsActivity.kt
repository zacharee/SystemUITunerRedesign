package com.zacharee1.systemuituner.activites

import android.os.Build
import android.os.Bundle
import android.preference.PreferenceFragment
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.fragments.AnimFragment
import com.zacharee1.systemuituner.handlers.RecreateHandler
import com.zacharee1.systemuituner.misc.OptionSelected
import com.zacharee1.systemuituner.util.Utils

class OptionsActivity : AppCompatActivity() {
    companion object {
        const val ALLOW_CUSTOM_INPUT = "allow_custom_settings_input"
    }

    private lateinit var main: MainPrefs

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTheme(if (Utils.isInDarkMode(this)) R.style.AppTheme_Dark else R.style.AppTheme)

        RecreateHandler.onCreate(this)

        setContentView(R.layout.activity_item_list)

        val hideWelcomeScreen = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("hide_welcome_screen", false)
        supportActionBar?.setDisplayHomeAsUpEnabled(!hideWelcomeScreen)
        supportActionBar?.setDisplayShowHomeEnabled(!hideWelcomeScreen)

        main = MainPrefs()
        fragmentManager
                ?.beginTransaction()
                ?.setCustomAnimations(R.animator.pop_in, R.animator.pop_out, R.animator.pop_in, R.animator.pop_out)
                ?.replace(R.id.content_main, main)
                ?.addToBackStack("main")
                ?.commit()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == android.R.id.home) {
            handleBackPressed()
            return true
        }

        return OptionSelected.doAction(item, this)
    }

    override fun onBackPressed() {
        handleBackPressed()
    }

    private fun handleBackPressed() {
        if (fragmentManager != null) {
            if (fragmentManager.backStackEntryCount > 1) {
                fragmentManager.popBackStackImmediate()

                val hideWelcomeScreen = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("hide_welcome_screen", false)
                val stillAboveOne = fragmentManager.backStackEntryCount > 1
                supportActionBar?.setDisplayHomeAsUpEnabled(if (stillAboveOne) true else !hideWelcomeScreen)
                supportActionBar?.setDisplayShowHomeEnabled(if (stillAboveOne) true else !hideWelcomeScreen)
            } else {
                finish()
            }
        } else {
            finish()
        }
    }

    class MainPrefs : AnimFragment() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            addPreferencesFromResource(R.xml.prefs_main)

            removeTouchWizIfNeeded()
            removeLockScreenIfNeeded()
            setListeners()
        }

        override fun onResume() {
            super.onResume()

            activity.title = resources.getString(R.string.app_name)
            updateCustomEnabledState()
        }

        private fun updateCustomEnabledState() {
            val customPref = findPreference("custom")
            val enabled = preferenceManager.sharedPreferences.getBoolean(ALLOW_CUSTOM_INPUT, false)

            customPref.isEnabled = enabled
            customPref.summary = if (enabled) null else resources.getString(R.string.enable_in_settings)
        }

        private fun removeTouchWizIfNeeded() {
            if (!Utils.checkSamsung()) preferenceScreen.removePreference(findPreference("touchwiz"))
        }

        private fun removeLockScreenIfNeeded() {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) preferenceScreen.removePreference(findPreference("lockscreen"))
        }

        private fun setListeners() {
            for (i in 0 until preferenceScreen.preferenceCount) {
                val pref = preferenceScreen.getPreference(i)
                pref.setOnPreferenceClickListener {
                    val fragment = Class.forName(pref.fragment ?: return@setOnPreferenceClickListener false).newInstance() as PreferenceFragment
                    fragmentManager
                            ?.beginTransaction()
                            ?.setCustomAnimations(R.animator.pop_in, R.animator.pop_out, R.animator.pop_in, R.animator.pop_out)
                            ?.replace(R.id.content_main, fragment, it.key)
                            ?.addToBackStack(it.key)
                            ?.commit()
                    (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
                    (activity as AppCompatActivity).supportActionBar?.setDisplayShowHomeEnabled(true)
                    true
                }
            }
        }
    }
}
