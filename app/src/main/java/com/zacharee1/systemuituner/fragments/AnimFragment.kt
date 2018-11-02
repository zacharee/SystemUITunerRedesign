package com.zacharee1.systemuituner.fragments

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat

abstract class AnimFragment : PreferenceFragmentCompat() {
    internal abstract val prefsRes: Int

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(prefsRes, rootKey)
    }

    override fun onResume() {
        super.onResume()
        activity?.title = onSetTitle() ?: return
    }

    internal abstract fun onSetTitle(): String?
}