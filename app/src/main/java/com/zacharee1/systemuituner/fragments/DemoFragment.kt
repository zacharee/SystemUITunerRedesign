package com.zacharee1.systemuituner.fragments

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.preference.Preference
import androidx.preference.SwitchPreference
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.activites.instructions.SetupActivity.Companion.make
import com.zacharee1.systemuituner.handlers.DemoHandler
import com.zacharee1.systemuituner.util.forEachPreference
import com.zacharee1.systemuituner.util.hasSpecificPerm
import com.zacharee1.systemuituner.util.writeGlobal

class DemoFragment : AnimFragment() {
    override val prefsRes = R.xml.pref_demo

    private var switchReceiver: BroadcastReceiver? = null
    
    private lateinit var demoHandler: DemoHandler

    override fun onSetTitle() = resources.getString(R.string.demo_mode)

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        super.onCreatePreferences(savedInstanceState, rootKey)

        demoHandler = DemoHandler(context!!)
        if (context?.hasSpecificPerm(Manifest.permission.DUMP) == true) {
            setPrefListeners()
            setDemoSwitchListener()
        } else {
            make(context!!, arrayListOf(Manifest.permission.DUMP))

            activity?.finish()
        }
    }

    private fun setPrefListeners() {
        val enableDemo = findPreference<SwitchPreference>(DEMO_ALLOWED)
        enableDemo?.isEnabled = Settings.Global.getInt(context?.contentResolver, DEMO_ALLOWED, 0) == 0
        enableDemo?.onPreferenceClickListener = Preference.OnPreferenceClickListener { preference ->
            if (activity?.checkCallingOrSelfPermission(Manifest.permission.DUMP) == PackageManager.PERMISSION_GRANTED) {
                context?.writeGlobal(preference.key, 1)
                findPreference<SwitchPreference>(SHOW_DEMO)?.isEnabled = true
            } else {
                Toast.makeText(context, resources.getString(R.string.grant_dump_perm), Toast.LENGTH_LONG).show()
            }
            true
        }
    }

    private fun setDemoSwitchListener() {
        val demo = findPreference<SwitchPreference>(SHOW_DEMO)!!

        switchReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val enabled = demoHandler.isEnabled

                demo.isChecked = enabled
                disableOtherPreferences(enabled)
            }
        }

        val filter = IntentFilter(DEMO_ACTION)

        activity?.registerReceiver(switchReceiver, filter)

        demo.isEnabled = demoHandler.isAllowed

        demo.isChecked = demoHandler.isEnabled

        disableOtherPreferences(demoHandler.isEnabled)

        demo.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, o ->
            if (o.toString().toBoolean()) {
                showDemo()
            } else {
                hideDemo()
            }

            true
        }
    }

    private fun showDemo() {
        disableOtherPreferences(true)
        demoHandler.showDemo()
    }

    private fun hideDemo() {
        disableOtherPreferences(false)
        demoHandler.hideDemo()
    }

    private fun disableOtherPreferences(disable: Boolean) {
        preferenceScreen.forEachPreference {
            if (it.hasKey() && !(it.key == DEMO_ALLOWED || it.key == SHOW_DEMO)) {
                it.isEnabled = !disable
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            activity?.unregisterReceiver(switchReceiver)
        } catch (e: Exception) {
        }

    }

    companion object {
        const val DEMO_ALLOWED = "sysui_demo_allowed"
        const val SHOW_DEMO = "show_demo"

        const val DEMO_ACTION = "com.android.systemui.demo"
    }
}