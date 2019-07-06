package com.zacharee1.systemuituner.prefs

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.text.format.DateFormat
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.TimePicker
import androidx.preference.Preference
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.zacharee1.systemuituner.R
import java.util.concurrent.TimeUnit

class TimePreference : Preference, SharedPreferences.OnSharedPreferenceChangeListener {
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

        val d = MaterialAlertDialogBuilder(context)
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

    override fun onAttached() {
        super.onAttached()

        syncSummary()
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onDetached() {
        super.onDetached()

        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (key == this.key) syncSummary()
    }

    private fun syncSummary() {
        val updatedHour = if (savedHour == 0L) 12 else if (savedHour > 12) savedHour - 12 else savedHour

        summary = StringBuilder().run {
            append(String.format(
                    "%01d:%02d",
                    updatedHour,
                    savedMinute
            ))
            append(" ")
            append(if (savedHour < 12) "AM" else "PM")
        }
    }
}
