package com.zacharee1.systemuituner.fragments

import android.content.Intent
import android.os.Build
import android.preference.Preference
import android.preference.PreferenceCategory
import android.preference.SwitchPreference
import android.provider.Settings
import com.pavelsikun.seekbarpreference.SeekBarPreference
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.activites.QuickSettingsLayoutEditor
import com.zacharee1.systemuituner.util.writeSecure

class QSFragment : AnimFragment() {
    override fun onSetTitle() = resources.getString(R.string.quick_settings)

    override fun onAnimationFinishedEnter(enter: Boolean) {
        if (enter) {
            addPreferencesFromResource(R.xml.pref_qs)
            setSwitchStates()
            setSwitchListeners()
            setSliderState()
            setEditorListener()
        }
    }

    private fun setEditorListener() {
        val launch = findPreference("launch_editor")
        launch?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            startActivity(Intent(context, QuickSettingsLayoutEditor::class.java))
            true
        }
    }

    private fun setSwitchStates() {
        (0 until preferenceScreen.rootAdapter.count)
                .map { //loop through every preference
                    preferenceScreen.rootAdapter.getItem(it)
                }
                .filterIsInstance<SwitchPreference>()
                .forEach { //if current preference is a SwitchPreference

                    it.isChecked = Settings.Secure.getInt(context?.contentResolver, it.key, 1) == 1
                }
    }

    private fun setSwitchListeners() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            (0 until preferenceScreen.rootAdapter.count)
                    .map {
                        //loop through every preference
                        preferenceScreen.rootAdapter.getItem(it)
                    }
                    .filterIsInstance<SwitchPreference>()
                    .forEach {
                        //if current preference is a SwitchPreference

                        it.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { preference, o ->
                            context.writeSecure(preference.key, if (o.toString().toBoolean()) 1 else 0)
                            true
                        }
                    }
        } else {
            val category = findPreference(GENERAL_QS) as PreferenceCategory
            category.isEnabled = false

            for (i in 0 until category.preferenceCount) {
                val preference = category.getPreference(i) as SwitchPreference
                preference.isChecked = false
                preference.setSummary(R.string.requires_nougat)
            }
        }
    }

    private fun setSliderState() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val pref = findPreference(QQS_COUNT) as SeekBarPreference //find the SliderPreference
            //            pref.set<in(1);
            pref.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, o ->
                context.writeSecure(QQS_COUNT, o.toString().toFloat().toInt()) //write new value to Settings if user presses OK
                true
            }

            pref.currentValue = Settings.Secure.getInt(context?.contentResolver, QQS_COUNT, 5) //set the progress/value from Settings
        } else {
            val category = findPreference(COUNT_CATEGORY) as PreferenceCategory
            category.isEnabled = false

            (0 until category.preferenceCount)
                    .map { category.getPreference(it) }
                    .forEach { it.setSummary(R.string.requires_nougat) }
        }

    }

    companion object {
        const val GENERAL_QS = "general_qs"
        const val QQS_COUNT = "sysui_qqs_count"
        const val COUNT_CATEGORY = "qqs_count_category"
    }
}