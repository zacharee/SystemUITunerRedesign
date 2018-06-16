package com.zacharee1.systemuituner.prefs

import android.content.Context
import android.preference.Preference
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import com.zacharee1.systemuituner.R

class StatBarWarnPref : Preference {
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context) : super(context)

    override fun onCreateView(parent: ViewGroup): View {
        val view = super.onCreateView(parent)
        setTitle(R.string.warning)
        setSummary(R.string.statbar_rotation_lock_notif)
        setIcon(R.drawable.ic_smartphone_black_24dp)
        isSelectable = false
        return view
    }
}
