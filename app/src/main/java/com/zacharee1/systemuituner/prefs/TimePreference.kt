package com.zacharee1.systemuituner.prefs

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.preference.DialogPreference
import android.preference.Preference
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.TimePicker
import com.zacharee1.systemuituner.R
import java.util.*

class TimePreference : DialogPreference {
    private var view: View? = null
    private var mListener: Preference.OnPreferenceChangeListener? = null

    private val currentHour: Int
        get() {
            val picker = view!!.findViewById<TimePicker>(R.id.time_picker)
            return picker.hour
        }

    private val currentMinute: Int
        get() {
            val picker = view!!.findViewById<TimePicker>(R.id.time_picker)
            return picker.minute
        }

    private val currentTimeMillis: Long
        get() {
            val calendar = Calendar.getInstance()
            calendar.set(Date().year, Date().month, Date().day, currentHour, currentMinute, 0)
            return calendar.timeInMillis
        }

    private val savedHour: Int
        get() = sharedPreferences.getInt(key + "hour", 12)

    private val savedMinute: Int
        get() = sharedPreferences.getInt(key + "minute", 0)

    val savedTimeMillis: Long
        get() {
            val calendar = Calendar.getInstance()
            calendar.set(Date().year, Date().month, Date().day, savedHour, savedMinute, 0)
            return calendar.timeInMillis
        }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context) : super(context)

    @SuppressLint("InflateParams")
    override fun onCreateDialogView(): View {
        val view = LayoutInflater.from(context).inflate(R.layout.time_pref_view, null, true)
        this.view = view

        setCurrentTime(savedHour, savedMinute)

        return view
    }

    override fun onDialogClosed(positiveResult: Boolean) {
        super.onDialogClosed(positiveResult)

        if (mListener != null && positiveResult) {
            val bundle = Bundle()
            bundle.putInt("hour", currentHour)
            bundle.putInt("minute", currentMinute)
            bundle.putLong("millis", currentTimeMillis)

            mListener!!.onPreferenceChange(this, bundle)
        }
        if (positiveResult) setSavedTime(currentHour, currentMinute)
    }

    override fun setOnPreferenceChangeListener(onPreferenceChangeListener: Preference.OnPreferenceChangeListener) {
        mListener = onPreferenceChangeListener
    }

    fun getView(): View? {
        if (view == null) onCreateDialogView()

        return view
    }

    private fun setCurrentTime(hour: Int, minute: Int) {
        val picker = view!!.findViewById<TimePicker>(R.id.time_picker)

        picker.hour = hour
        picker.minute = minute
    }

    private fun setSavedTime(hour: Int, minute: Int) {
        sharedPreferences.edit().putInt(key + "hour", hour).apply()
        sharedPreferences.edit().putInt(key + "minute", minute).apply()
    }
}
