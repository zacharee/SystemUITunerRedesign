package com.zacharee1.systemuituner.fragments

import android.os.Bundle
import android.preference.PreferenceFragment
import com.zacharee1.systemuituner.R

class CustomFragment : PreferenceFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.pref_custom)
    }

    override fun onResume() {
        super.onResume()

        activity.title = resources.getString(R.string.custom)
    }
}