package com.zacharee1.systemuituner.prefs

import android.content.Context
import android.graphics.Color
import android.preference.Preference
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.zacharee1.systemuituner.R

open class RedTextPref : Preference {
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context) : super(context)

    override fun onCreateView(parent: ViewGroup): View {
        val view = super.onCreateView(parent)
        title = context.resources.getString(R.string.warning)
        view.findViewById<TextView>(android.R.id.title).setTextColor(Color.RED)
        view.findViewById<TextView>(android.R.id.summary).setTextColor(Color.RED)
        isSelectable = false
        return view
    }
}
