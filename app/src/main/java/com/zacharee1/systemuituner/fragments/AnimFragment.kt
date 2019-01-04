package com.zacharee1.systemuituner.fragments

import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceScreen
import androidx.recyclerview.widget.RecyclerView
import com.zacharee1.systemuituner.prefs.CustomInputPreference
import tk.zwander.collapsiblepreferencecategory.CollapsiblePreferenceGroupAdapter

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

    override fun onCreateAdapter(preferenceScreen: PreferenceScreen): RecyclerView.Adapter<*> {
        return CollapsiblePreferenceGroupAdapter(preferenceScreen)
    }

    internal abstract fun onSetTitle(): String?
}