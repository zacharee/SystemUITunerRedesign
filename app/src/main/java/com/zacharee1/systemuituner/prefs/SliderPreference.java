package com.zacharee1.systemuituner.prefs;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.zacharee1.systemuituner.R;

public class SliderPreference extends DialogPreference
{
    private View view;
    private OnPreferenceChangeListener mListener;
    private int mProgress = -1;
    private int mMaxProgress = -1;

    public SliderPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public SliderPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public SliderPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SliderPreference(Context context) {
        super(context);
    }

    @Override
    protected View onCreateDialogView()
    {
        final View view = LayoutInflater.from(getContext()).inflate(R.layout.slider_pref_view, null, true);
        this.view = view;

        mProgress = (mProgress == -1 ? getSavedProgress() : mProgress);
        mMaxProgress = (mMaxProgress == -1 ? 100 : mMaxProgress);

        SeekBar seekBar = view.findViewById(R.id.slider_pref_seekbar);
        final TextView textView = view.findViewById(R.id.slider_pref_text);

        seekBar.setMax(mMaxProgress);
        seekBar.setProgress(mProgress);

        textView.setText(String.valueOf(mProgress));

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b)
            {
                textView.setText(String.valueOf(i));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {

            }
        });

        Log.e("KEY", getKey());

        return view;
    }

    @Override
    protected void onDialogClosed(boolean positiveResult)
    {
        if (mListener != null && positiveResult) mListener.onPreferenceChange(this, getCurrentProgress());
        if (positiveResult) getSharedPreferences().edit().putInt(getKey(), getCurrentProgress()).apply();

        super.onDialogClosed(positiveResult);
    }

    @Override
    public void setOnPreferenceChangeListener(OnPreferenceChangeListener onPreferenceChangeListener)
    {
        mListener = onPreferenceChangeListener;
    }

    public void setProgressState(int progress) {
        mProgress = progress;
    }

    public void setMaxProgess(int maxProgess) {
        if (view == null) onCreateDialogView();

        mMaxProgress = maxProgess;
    }

    public View getView() {
        if (view == null) onCreateDialogView();

        return view;
    }

    public int getSavedProgress() {
        return getSharedPreferences().getInt(getKey(), 0);
    }

    public int getCurrentProgress() {
        if (view == null) onCreateDialogView();
        return ((SeekBar) view.findViewById(R.id.slider_pref_seekbar)).getProgress();
    }
}
