package com.zacharee1.systemuituner.fragmenthelpers

import android.content.Intent
import android.database.ContentObserver
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.preference.*
import android.provider.Settings
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.activites.apppickers.ImmersiveSelectActivity
import com.zacharee1.systemuituner.fragments.ItemDetailFragment
import com.zacharee1.systemuituner.handlers.ImmersiveHandler

class ImmersiveHelper(fragment: ItemDetailFragment) : BaseHelper(fragment), Preference.OnPreferenceChangeListener {

    private val none = findPreference(ImmersiveHandler.DISABLED) as CheckBoxPreference?
    private val full = findPreference(ImmersiveHandler.FULL) as CheckBoxPreference?
    private val status = findPreference(ImmersiveHandler.STATUS) as CheckBoxPreference?
    private val navi = findPreference(ImmersiveHandler.NAV) as CheckBoxPreference?
    private val preconf = findPreference(ImmersiveHandler.PRECONF) as CheckBoxPreference?
    private var mObserver: ContentObserver? = null

    init {
        findPreference("immersive_tile_mode")?.onPreferenceChangeListener = this

        none?.onPreferenceChangeListener = this
        full?.onPreferenceChangeListener = this
        status?.onPreferenceChangeListener = this
        navi?.onPreferenceChangeListener = this
        preconf?.onPreferenceChangeListener = this

        setContentObserver()
        setProperBoxChecked()
        disableQSSettingIfBelowNougat()
        setSelectorListener()
    }

    private fun setContentObserver() {
        mObserver = object : ContentObserver(Handler(Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean, uri: Uri) {
                if (uri == ImmersiveHandler.POLICY_CONTROL) {
                    setProperBoxChecked()
                }
            }
        }

        activity.contentResolver.registerContentObserver(Settings.Global.CONTENT_URI, true, mObserver!!)
    }

    private fun setProperBoxChecked() {
        var currentMode = ImmersiveHandler.getMode(context)

        if (currentMode == null || currentMode.isEmpty()) {
            currentMode = ImmersiveHandler.DISABLED
        }

        setAllOthersDisabled(currentMode)
    }

    private fun setAllOthersDisabled(keyToNotDisable: String) {
        val boxes = findPreference(IMMERSIVE_BOXES) as PreferenceCategory

        for (i in 0 until boxes.preferenceCount) {
            val preference = boxes.getPreference(i)

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

            for (i in 0 until category.preferenceCount) {
                val preference = category.getPreference(i)
                preference.isEnabled = false
                preference.setSummary(R.string.requires_nougat)
            }
        }
    }

    private fun setSelectorListener() {
        val preference = findPreference(SELECT_APPS)
        preference?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            activity.startActivity(Intent(activity, ImmersiveSelectActivity::class.java))
            true
        }

        val enabled = findPreference(APP_IMMERSIVE) as SwitchPreference?
        enabled?.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, _ ->
            Handler().postDelayed({ ImmersiveHandler.setMode(context, ImmersiveHandler.getMode(context)) }, 100)
            true
        }
    }

    override fun onDestroy() {
        try {
            activity.contentResolver.unregisterContentObserver(mObserver!!)
        } catch (e: Exception) {
        }

    }

    override fun onPreferenceChange(preference: Preference, o: Any): Boolean {
        if (preference is CheckBoxPreference) {

            val isChecked = java.lang.Boolean.valueOf(o.toString())

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
        const val APP_IMMERSIVE = "app_immersive"
        const val SELECT_APPS = "select_apps"
        const val CONFIG_QS = "config_qs"
    }
}
