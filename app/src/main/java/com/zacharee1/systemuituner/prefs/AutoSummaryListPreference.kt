package com.zacharee1.systemuituner.prefs

import android.content.Context
import android.util.AttributeSet
import androidx.preference.ListPreference

class AutoSummaryListPreference : ListPreference {
    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)

    override fun onSetInitialValue(defaultValue: Any?) {
        super.onSetInitialValue(defaultValue)

        syncSummary()
    }

    override fun notifyChanged() {
        super.notifyChanged()

        syncSummary()
    }

    private fun syncSummary() {
        summary = entry
    }
}