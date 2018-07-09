package com.zacharee1.systemuituner.activites

import android.app.FragmentManager
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceFragment
import android.preference.PreferenceManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.fragments.AnimFragment
import com.zacharee1.systemuituner.misc.OptionSelected
import com.zacharee1.systemuituner.util.Utils

class OptionsActivity : BaseAnimActivity() {
    companion object {
        const val ALLOW_CUSTOM_INPUT = "allow_custom_settings_input"
    }

    private lateinit var main: MainPrefs

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_item_list)

        val hideWelcomeScreen = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("hide_welcome_screen", false)
        setBackClickable(!hideWelcomeScreen)

        main = MainPrefs()

        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        fragmentManager
                ?.beginTransaction()
                ?.replace(R.id.content_main, main)
                ?.addToBackStack("main")
                ?.commit()
    }

    override fun onResume() {
        super.onResume()

        val hideWelcomeScreen = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("hide_welcome_screen", false)
        setBackClickable(fragmentManager.backStackEntryCount > 1 || !hideWelcomeScreen)
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
        val hideWelcomeScreen = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("hide_welcome_screen", false)

        when {
            fragmentManager != null -> when {
                fragmentManager.backStackEntryCount > 1 -> {
                    fragmentManager.popBackStackImmediate()

                    val stillAboveOne = fragmentManager.backStackEntryCount > 1

                    setBackClickable(stillAboveOne || !hideWelcomeScreen)
                }
                else -> finish()
            }
            else -> finish()
        }
    }

    class MainPrefs : AnimFragment() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            addPreferencesFromResource(R.xml.prefs_main)
        }

        override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

            removeTouchWizIfNeeded()
            removeLockScreenIfNeeded()
            setListeners()
        }

        override fun onResume() {
            super.onResume()
            updateCustomEnabledState()
        }

        override fun onSetTitle() = resources.getString(R.string.app_name)

        private fun updateCustomEnabledState() {
            val customPref = findPreference("custom")
            val enabled = preferenceManager.sharedPreferences.getBoolean(ALLOW_CUSTOM_INPUT, false)

            customPref.isEnabled = enabled
            customPref.summary = if (enabled) null else resources.getString(R.string.enable_in_settings)
        }

        private fun removeTouchWizIfNeeded() {
            if (!Utils.checkSamsung()) preferenceScreen.removePreference(findPreference("touchwiz") ?: return)
        }

        private fun removeLockScreenIfNeeded() {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) preferenceScreen.removePreference(findPreference("lockscreen") ?: return)
        }

        private fun setListeners() {
            for (i in 0 until preferenceScreen.preferenceCount) {
                val pref = preferenceScreen.getPreference(i)
                pref.setOnPreferenceClickListener {
                    (activity as BaseAnimActivity).setBackClickable(true)

                    val fragment = Class.forName(pref.fragment ?: return@setOnPreferenceClickListener false).newInstance() as PreferenceFragment
                    fragmentManager
                            ?.beginTransaction()
                            ?.replace(R.id.content_main, fragment, it.key)
                            ?.addToBackStack(it.key)
                            ?.commit()
                    true
                }
            }
        }
    }
}
