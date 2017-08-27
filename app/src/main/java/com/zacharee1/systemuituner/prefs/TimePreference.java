package com.zacharee1.systemuituner.prefs;

import android.content.Context;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TimePicker;

import com.zacharee1.systemuituner.R;

import java.util.Calendar;
import java.util.Date;

@SuppressWarnings("unused")
public class TimePreference extends DialogPreference
{
    private View view;
    private OnPreferenceChangeListener mListener;

    public TimePreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public TimePreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public TimePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TimePreference(Context context) {
        super(context);
    }

    @Override
    protected View onCreateDialogView()
    {
        final View view = LayoutInflater.from(getContext()).inflate(R.layout.time_pref_view, null, true);
        this.view = view;

        setCurrentTime(getSavedHour(), getSavedMinute());

        return view;
    }

    @Override
    protected void onDialogClosed(boolean positiveResult)
    {
        super.onDialogClosed(positiveResult);

        if (mListener != null && positiveResult) {
            Bundle bundle = new Bundle();
            bundle.putInt("hour", getCurrentHour());
            bundle.putInt("minute", getCurrentMinute());
            bundle.putLong("millis", getCurrentTimeMillis());

            mListener.onPreferenceChange(this, bundle);
        }
        if (positiveResult) setSavedTime(getCurrentHour(), getCurrentMinute());
    }

    @Override
    public void setOnPreferenceChangeListener(OnPreferenceChangeListener onPreferenceChangeListener)
    {
        mListener = onPreferenceChangeListener;
    }

    public View getView() {
        if (view == null) onCreateDialogView();

        return view;
    }

    private void setCurrentTime(int hour, int minute) {
        TimePicker picker = view.findViewById(R.id.time_picker);

        picker.setHour(hour);
        picker.setMinute(minute);
    }

    private void setSavedTime(int hour, int minute) {
        getSharedPreferences().edit().putInt(getKey() + "hour", hour).apply();
        getSharedPreferences().edit().putInt(getKey() + "minute", minute).apply();
    }

    private int getCurrentHour() {
        TimePicker picker = view.findViewById(R.id.time_picker);
        return picker.getHour();
    }

    private int getCurrentMinute() {
        TimePicker picker = view.findViewById(R.id.time_picker);
        return picker.getMinute();
    }

    private long getCurrentTimeMillis() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(new Date().getYear(), new Date().getMonth(), new Date().getDay(), getCurrentHour(), getCurrentMinute(), 0);
        return calendar.getTimeInMillis();
    }

    public int getSavedHour() {
        return getSharedPreferences().getInt(getKey() + "hour", 12);
    }

    public int getSavedMinute() {
        return getSharedPreferences().getInt(getKey() + "minute", 0);
    }

    public long getSavedTimeMillis() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(new Date().getYear(), new Date().getMonth(), new Date().getDay(), getSavedHour(), getSavedMinute(), 0);
        return calendar.getTimeInMillis();
    }
}
