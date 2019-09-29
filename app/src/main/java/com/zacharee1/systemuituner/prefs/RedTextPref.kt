package com.zacharee1.systemuituner.prefs

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.widget.TextView
import androidx.preference.Preference
import androidx.preference.PreferenceViewHolder
import com.zacharee1.systemuituner.R

open class RedTextPref : Preference {
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context) : super(context)

    init {
        title = context.resources.getString(R.string.warning)
        isSelectable = false
    }

    override fun onBindViewHolder(holder: PreferenceViewHolder?) {
        super.onBindViewHolder(holder)

        holder?.itemView?.findViewById<TextView>(android.R.id.title)?.setTextColor(Color.RED)
        holder?.itemView?.findViewById<TextView>(android.R.id.summary)?.setTextColor(Color.RED)
    }
}
