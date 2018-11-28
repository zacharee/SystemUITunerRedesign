package com.zacharee1.systemuituner.fragments

import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.zacharee1.systemuituner.prefs.CustomInputPreference

abstract class AnimFragment : PreferenceFragmentCompat() {
    internal abstract val prefsRes: Int

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(prefsRes, rootKey)
    }

    override fun onResume() {
        super.onResume()
        activity?.title = onSetTitle() ?: return
    }

    override fun onDisplayPreferenceDialog(preference: Preference?) {
        when (preference) {
            is CustomInputPreference -> {
                val frag = CustomInputPreference.Fragment.newInstance(preference.key)
                frag.setTargetFragment(this, 0)
                frag.show(fragmentManager, null)
            }
            else -> super.onDisplayPreferenceDialog(preference)
        }
    }

    internal abstract fun onSetTitle(): String?
}