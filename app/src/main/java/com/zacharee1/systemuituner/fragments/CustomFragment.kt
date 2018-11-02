package com.zacharee1.systemuituner.fragments

import com.zacharee1.systemuituner.R

class CustomFragment : AnimFragment() {
    override val prefsRes = R.xml.pref_custom

    override fun onSetTitle() = resources.getString(R.string.custom)
}