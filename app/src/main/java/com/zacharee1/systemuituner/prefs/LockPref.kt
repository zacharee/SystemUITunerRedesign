package com.zacharee1.systemuituner.prefs

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.preference.Preference
import androidx.preference.PreferenceViewHolder
import com.zacharee1.systemuituner.R

class LockPref : Preference {
    var resetListener: (() -> Boolean)? = null

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context) : super(context)

    override fun onBindViewHolder(holder: PreferenceViewHolder?) {
        super.onBindViewHolder(holder)

        val view = holder?.itemView

        val widgetView = view?.findViewById<LinearLayout>(android.R.id.widget_frame)
        widgetView?.visibility = if (resetListener != null) View.VISIBLE else View.INVISIBLE

        val widgetButton = widgetView?.findViewById<ImageView>(R.id.reset)
        widgetButton?.setOnClickListener {
            val listener = resetListener
            if (listener == null || !listener.invoke()) {
                onClick()
            }
        }
    }
}
