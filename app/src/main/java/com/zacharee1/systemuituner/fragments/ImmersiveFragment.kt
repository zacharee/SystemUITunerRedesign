package com.zacharee1.systemuituner.fragments

import android.content.Intent
import android.database.ContentObserver
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import androidx.preference.CheckBoxPreference
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceCategory
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.activites.apppickers.ImmersiveSelectActivity
import com.zacharee1.systemuituner.handlers.ImmersiveHandler
import com.zacharee1.systemuituner.util.PrefManager
import com.zacharee1.systemuituner.util.forEachPreference

class ImmersiveFragment : AnimFragment(), Preference.OnPreferenceChangeListener {
    override val prefsRes = R.xml.pref_imm

    private var observer = object : ContentObserver(Handler(Looper.getMainLooper())) {
        override fun onChange(selfChange: Boolean, uri: Uri) {
            if (uri == ImmersiveHandler.POLICY_CONTROL) {
                setProperBoxChecked()
            }
        }
    }

    override fun onSetTitle() = resources.getString(R.string.immersive_mode)

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        super.onCreatePreferences(savedInstanceState, rootKey)

        findPreference("immersive_tile_mode")?.onPreferenceChangeListener = this

        val none = findPreference(ImmersiveHandler.DISABLED) as CheckBoxPreference
        val full = findPreference(ImmersiveHandler.FULL) as CheckBoxPreference
        val status = findPreference(ImmersiveHandler.STATUS) as CheckBoxPreference
        val navi = findPreference(ImmersiveHandler.NAV) as CheckBoxPreference
        val preconf = findPreference(ImmersiveHandler.PRECONF) as CheckBoxPreference

        none.onPreferenceChangeListener = this
        full.onPreferenceChangeListener = this
        status.onPreferenceChangeListener = this
        navi.onPreferenceChangeListener = this
        preconf.onPreferenceChangeListener = this
    }

    override fun onResume() {
        super.onResume()

        setContentObserver()
        setProperBoxChecked()
        disableQSSettingIfBelowNougat()
        setSelectorListener()
    }

    private fun setContentObserver() {
        activity?.contentResolver
                ?.registerContentObserver(Settings.Global.CONTENT_URI, true, observer)
    }

    private fun setProperBoxChecked() {
        val currentMode = ImmersiveHandler.getMode(context)
        setAllOthersDisabled(currentMode)
    }

    private fun setAllOthersDisabled(keyToNotDisable: String) {
        val boxes = findPreference(IMMERSIVE_BOXES) as PreferenceCategory

        boxes.forEachPreference { preference ->
            if (preference is CheckBoxPreference) {
                preference.isEnabled = preference.key != keyToNotDisable

                if (preference.key != keyToNotDisable) {
                    preference.isChecked = false
                } else if (!preference.isChecked) {
                    preference.isChecked = true
                }
            }
        }
    }

    private fun disableQSSettingIfBelowNougat() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            val category = findPreference(CONFIG_QS) as PreferenceCategory

            category.forEachPreference { preference ->
                preference.isEnabled = false
                preference.setSummary(R.string.requires_nougat)
            }
        }
    }

    private fun setSelectorListener() {
        val preference = findPreference(SELECT_APPS)
        preference?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            activity?.startActivity(Intent(activity, ImmersiveSelectActivity::class.java))
            true
        }

        val listener = Preference.OnPreferenceChangeListener { _, _ ->
            Handler().postDelayed({ ImmersiveHandler.setMode(context, ImmersiveHandler.getMode(context)) }, 100)
            true
        }

        findPreference(PrefManager.APP_IMMERSIVE).onPreferenceChangeListener = listener
        findPreference(PrefManager.IMMERSIVE_BLACKLIST).onPreferenceChangeListener = listener


    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            activity?.contentResolver?.unregisterContentObserver(observer)
        } catch (e: Exception) {}
    }

    override fun onPreferenceChange(preference: Preference, o: Any): Boolean {
        if (preference is CheckBoxPreference) {

            val isChecked = o.toString().toBoolean()

            if (isChecked) {
                setAllOthersDisabled(preference.key)
                ImmersiveHandler.setMode(activity, preference.key)
            }
        }

        if (preference is ListPreference) {
            val which = o.toString()

            if (ImmersiveHandler.isInImmersive(context)) ImmersiveHandler.setMode(context, which)
        }

        return true
    }

    companion object {
        const val IMMERSIVE_BOXES = "imm_boxes"
        const val SELECT_APPS = "select_apps"
        const val CONFIG_QS = "config_qs"
    }
}