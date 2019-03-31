package com.zacharee1.systemuituner.activites

import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.preference.Preference
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.fragments.AnimFragment
import com.zacharee1.systemuituner.misc.OptionSelected
import com.zacharee1.systemuituner.util.checkSamsung
import com.zacharee1.systemuituner.util.navController
import com.zacharee1.systemuituner.util.navOptions
import com.zacharee1.systemuituner.util.prefs

class OptionsActivity : BaseAnimActivity(), NavController.OnDestinationChangedListener {
    companion object {
        const val STATBAR = "statbar"
        const val QS = "qs"
        const val DEMO = "demo"
        const val TOUCHWIZ = "touchwiz"
        const val IMMERSIVE = "immersive"
        const val LOCKSCREEN = "lockscreen"
        const val MISC = "misc"
        const val CUSTOM = "custom"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_item_list)

        backButton.scaleX = 0f
        backButton.scaleY = 0f

        navController.addOnDestinationChangedListener(this)
    }

    override fun onResume() {
        super.onResume()

        updateBackClickable()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == android.R.id.home) {
            onBackPressed()
            return true
        }

        return OptionSelected.doAction(item.itemId, this)
    }

    override fun onDestinationChanged(controller: NavController, destination: NavDestination, arguments: Bundle?) {
        updateBackClickable()
    }

    override fun onDestroy() {
        super.onDestroy()

        navController.removeOnDestinationChangedListener(this)
    }

    private fun updateBackClickable() {
        val currentFrag = navController.currentDestination

        setBackClickable(currentFrag?.id != R.id.mainPrefs || !prefs.hideWelcomeScreen)
    }

    class MainPrefs : AnimFragment() {
        override val prefsRes = R.xml.prefs_main

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

            removeTouchWizIfNeeded()
            removeLockScreenIfNeeded()
        }

        override fun onResume() {
            super.onResume()
            updateCustomEnabledState()
        }

        override fun onPreferenceTreeClick(preference: Preference?): Boolean {
            val (action, res) = when (preference?.key) {
                STATBAR -> R.id.action_mainPrefs_to_statbarFragment to true
                QS -> R.id.action_mainPrefs_to_QSFragment to true
                DEMO -> R.id.action_mainPrefs_to_demoFragment to true
                IMMERSIVE -> R.id.action_mainPrefs_to_immersiveFragment to true
                LOCKSCREEN -> R.id.action_mainPrefs_to_lockFragment to true
                MISC -> R.id.action_mainPrefs_to_miscFragment to true
                CUSTOM -> R.id.action_mainPrefs_to_customFragment to true
                else -> null to super.onPreferenceTreeClick(preference)
            }

            action?.let {
                navController.navigate(
                        it,
                        null,
                        navOptions
                )
            }

            return res
        }

        override fun onSetTitle() = resources.getString(R.string.app_name)

        private fun updateCustomEnabledState() {
            val customPref = findPreference<Preference>(CUSTOM)!!
            val enabled = context!!.prefs.allowCustomSettingsInput

            customPref.isEnabled = enabled
            customPref.summary = if (enabled) null else resources.getString(R.string.enable_in_settings)
        }

        private fun removeTouchWizIfNeeded() {
            if (activity?.checkSamsung() == false)
                preferenceScreen.removePreference(findPreference(TOUCHWIZ) ?: return)
        }

        private fun removeLockScreenIfNeeded() {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
                preferenceScreen.removePreference(findPreference("lockscreen") ?: return)
        }
    }
}
