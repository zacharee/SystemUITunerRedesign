package com.zacharee1.systemuituner.fragmenthelpers

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.preference.Preference
import android.preference.SwitchPreference
import android.provider.Settings
import android.widget.Toast
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.activites.instructions.SetupActivity
import com.zacharee1.systemuituner.fragments.ItemDetailFragment
import com.zacharee1.systemuituner.handlers.DemoHandler
import com.zacharee1.systemuituner.util.SettingsUtils

class DemoHelper(fragment: ItemDetailFragment) : BaseHelper(fragment) {

    private val mDemoHandler: DemoHandler = DemoHandler(context)
    private var switchReceiver: BroadcastReceiver? = null

    init {

        if (SettingsUtils.hasSpecificPerm(context, Manifest.permission.DUMP)) {
            setPrefListeners()
            setDemoSwitchListener()
        } else {
            val intent = Intent(context, SetupActivity::class.java)
            intent.putExtra(PERMISSION_NEEDED, arrayOf(Manifest.permission.DUMP))
            startActivity(intent)

            activity.finish()
        }
    }

    private fun setPrefListeners() {
        val enableDemo = findPreference(DEMO_ALLOWED)
        enableDemo.isEnabled = Settings.Global.getInt(context.contentResolver, DEMO_ALLOWED, 0) == 0
        enableDemo.onPreferenceClickListener = Preference.OnPreferenceClickListener { preference ->
            if (activity.checkCallingOrSelfPermission(Manifest.permission.DUMP) == PackageManager.PERMISSION_GRANTED) {
                SettingsUtils.writeGlobal(context, preference.key, "1")
                findPreference(SHOW_DEMO).isEnabled = true
            } else {
                Toast.makeText(context, resources.getString(R.string.grant_dump_perm), Toast.LENGTH_LONG).show()
            }
            true
        }
    }

    private fun setDemoSwitchListener() {
        val demo = findPreference(SHOW_DEMO) as SwitchPreference

        switchReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val enabled = mDemoHandler.isEnabled

                demo.isChecked = enabled
                disableOtherPreferences(enabled)
            }
        }

        val filter = IntentFilter(DEMO_ACTION)

        activity.registerReceiver(switchReceiver, filter)

        demo.isEnabled = mDemoHandler.isAllowed

        demo.isChecked = mDemoHandler.isEnabled

        disableOtherPreferences(mDemoHandler.isEnabled)

        demo.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, o ->
            if (java.lang.Boolean.valueOf(o.toString())) {
                showDemo()
            } else {
                hideDemo()
            }

            true
        }
    }

    private fun showDemo() {
        disableOtherPreferences(true)
        mDemoHandler.showDemo()
    }

    private fun hideDemo() {
        disableOtherPreferences(false)
        mDemoHandler.hideDemo()
    }

    private fun disableOtherPreferences(disable: Boolean) {
        (0 until preferenceScreen.rootAdapter.count)
                .map { preferenceScreen.rootAdapter.getItem(it) }
                .filterIsInstance<Preference>()
                .filter { it.hasKey() && !(it.key == DEMO_ALLOWED || it.key == SHOW_DEMO) }
                .forEach { it.isEnabled = !disable }
    }

    override fun onDestroy() {
        try {
            activity.unregisterReceiver(switchReceiver)
        } catch (e: Exception) {
        }

    }

    companion object {
        const val PERMISSION_NEEDED = "permission_needed"
        const val DEMO_ALLOWED = "sysui_demo_allowed"
        const val SHOW_DEMO = "show_demo"

        const val DEMO_ACTION = "com.android.systemui.demo"
    }
}
