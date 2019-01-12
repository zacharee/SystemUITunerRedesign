package com.zacharee1.systemuituner.prefs

import android.content.Context
import android.content.SharedPreferences
import android.util.AttributeSet
import androidx.preference.ListPreference

class AutoSummaryListPreference : ListPreference, SharedPreferences.OnSharedPreferenceChangeListener {
    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)

    override fun onAttached() {
        super.onAttached()

        syncSummary()
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onDetached() {
        super.onDetached()

        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (key == this.key) syncSummary()
    }

    private fun syncSummary() {
        summary = entry
    }
}