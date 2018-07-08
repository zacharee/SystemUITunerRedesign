package com.zacharee1.systemuituner.fragments

import com.zacharee1.systemuituner.R

class CustomFragment : AnimFragment() {
    override fun onAnimationFinished(enter: Boolean) {
        if (enter) {
            addPreferencesFromResource(R.xml.pref_custom)
        }
    }

    override fun onResume() {
        super.onResume()

        activity.title = resources.getString(R.string.custom)
    }
}