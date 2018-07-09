package com.zacharee1.systemuituner.prefs

import android.app.AlertDialog
import android.content.Context
import android.preference.Preference
import android.text.format.DateFormat
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.TimePicker
import com.zacharee1.systemuituner.R
import java.util.concurrent.TimeUnit

class TimePreference : Preference {
    val savedHour: Long
        get() = TimeUnit.MILLISECONDS.toHours(getPersistedLong(0))

    val savedMinute: Long
        get() = TimeUnit.MILLISECONDS.toMinutes(getPersistedLong(0)) - TimeUnit.HOURS.toMinutes(savedHour)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context) : super(context)

    override fun onClick() {
        val view = LayoutInflater.from(context).inflate(R.layout.time_pref_view, null, false)
        val pickerView = view.findViewById<TimePicker>(R.id.time_picker)

        pickerView.hour = savedHour.toInt()
        pickerView.minute = savedMinute.toInt()
        pickerView.setIs24HourView(DateFormat.is24HourFormat(context))

        val d = AlertDialog.Builder(context)
                .setView(view)
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    val persist = TimeUnit.HOURS.toMillis(pickerView.hour.toLong()) + TimeUnit.MINUTES.toMillis(pickerView.minute.toLong())
                    persistLong(persist)
                    notifyChanged()
                    callChangeListener(persist)
                }
                .setNegativeButton(android.R.string.cancel, null)
        d.show()
    }
}
