package com.zacharee1.systemuituner.fragments

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.preference.Preference
import androidx.preference.PreferenceCategory
import androidx.preference.SwitchPreference
import com.pavelsikun.seekbarpreference.SeekBarPreferenceCompat
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.activites.QuickSettingsLayoutEditor
import com.zacharee1.systemuituner.util.PrefManager.Companion.QQS_COUNT
import com.zacharee1.systemuituner.util.forEachPreference
import com.zacharee1.systemuituner.util.prefs
import com.zacharee1.systemuituner.util.writeSecure

class QSFragment : AnimFragment() {
    override val prefsRes = R.xml.pref_qs
    
    private var origHeader = false

    override fun onSetTitle() = resources.getString(R.string.quick_settings)

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        super.onCreatePreferences(savedInstanceState, rootKey)

        setSwitchStates()
        setSwitchListeners()
        setSliderState()
        setEditorListener()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        origHeader = context!!.prefs.safeModeHeaderCount

        context!!.prefs.safeModeHeaderCount = false
    }

    override fun onDestroy() {
        super.onDestroy()

        context!!.prefs.safeModeHeaderCount = origHeader
    }

    private fun setEditorListener() {
        val launch = findPreference("launch_editor")
        launch?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            startActivity(Intent(context, QuickSettingsLayoutEditor::class.java))
            true
        }
    }

    private fun setSwitchStates() {
        preferenceScreen.forEachPreference {
            if (it is SwitchPreference) {
                it.isChecked = Settings.Secure.getInt(context?.contentResolver, it.key, 1) == 1
            }
        }
    }

    private fun setSwitchListeners() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            preferenceScreen.forEachPreference {
                if (it is SwitchPreference) {
                    it.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { preference, o ->
                        context?.writeSecure(preference.key, if (o.toString().toBoolean()) 1 else 0)
                        true
                    }
                }
            }
        } else {
            val category = findPreference(GENERAL_QS) as PreferenceCategory
            category.isEnabled = false

            category.forEachPreference {
                if (it is SwitchPreference) {
                    it.isChecked = false
                    it.setSummary(R.string.requires_nougat)
                }
            }
        }
    }

    private fun setSliderState() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val pref = findPreference(QQS_COUNT) as SeekBarPreferenceCompat //find the SliderPreference
            //            pref.set<in(1);
            pref.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, o ->
                context?.writeSecure(QQS_COUNT, o.toString().toFloat().toInt()) //write new value to Settings if user presses OK
                true
            }

            pref.currentValue = Settings.Secure.getInt(context?.contentResolver, QQS_COUNT, 5) //set the progress/value from Settings
        } else {
            val category = findPreference(COUNT_CATEGORY) as PreferenceCategory
            category.isEnabled = false

            category.forEachPreference {
                it.setSummary(R.string.requires_nougat)
            }
        }

    }

    companion object {
        const val GENERAL_QS = "general_qs"
        const val COUNT_CATEGORY = "qqs_count_category"
    }
}