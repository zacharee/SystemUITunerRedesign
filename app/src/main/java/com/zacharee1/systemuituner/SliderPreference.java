package com.zacharee1.systemuituner;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

public class SliderPreference extends DialogPreference
{
    private View view;
    private OnPreferenceChangeListener mListener;

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

        SeekBar seekBar = view.findViewById(R.id.qqs_count_seekbar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b)
            {
                TextView textView = view.findViewById(R.id.qqs_count_text);
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

        int progress = getSharedPreferences().getInt(getKey(), 0);
        setProgressState(progress);

        return view;
    }

    @Override
    protected void onDialogClosed(boolean positiveResult)
    {
        super.onDialogClosed(positiveResult);

        if (mListener != null && positiveResult) mListener.onPreferenceChange(this, getCurrentProgress());
        if (positiveResult) getSharedPreferences().edit().putInt(getKey(), getCurrentProgress()).apply();
    }

    @Override
    public void setOnPreferenceChangeListener(OnPreferenceChangeListener onPreferenceChangeListener)
    {
        mListener = onPreferenceChangeListener;
    }

    public void setProgressState(int progress) {
        if (view == null) onCreateDialogView();

        SeekBar seekBar = view.findViewById(R.id.qqs_count_seekbar);
        TextView textView = view.findViewById(R.id.qqs_count_text);

        seekBar.setProgress(progress);
        textView.setText(String.valueOf(progress));
    }

    public void setMaxProgess(int maxProgess) {
        if (view == null) onCreateDialogView();

        SeekBar seekBar = view.findViewById(R.id.qqs_count_seekbar);
        seekBar.setMax(maxProgess);
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
        return ((SeekBar) view.findViewById(R.id.qqs_count_seekbar)).getProgress();
    }
}
