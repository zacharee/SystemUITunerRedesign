package com.zacharee1.systemuituner.prefs;

import android.content.Context;
import android.preference.Preference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TableLayout;
import android.widget.TextView;

import com.zacharee1.systemuituner.R;

public class SliderPreferenceEmbedded extends Preference
{
    private View view;
    private OnPreferenceChangeListener mListener;
    private int mProgress = -1;
    private int mMaxProgress = -1;

    public SliderPreferenceEmbedded(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public SliderPreferenceEmbedded(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public SliderPreferenceEmbedded(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SliderPreferenceEmbedded(Context context) {
        super(context);
    }

    @Override
    protected View onCreateView(ViewGroup parent)
    {
        final View view = super.onCreateView(parent);

        this.view = view;

        mProgress = (mProgress == -1 ? getSavedProgress() : mProgress);
        mMaxProgress = (mMaxProgress == -1 ? 100 : mMaxProgress);

        Log.e("Prog", mProgress + "");
        Log.e("mProg", mMaxProgress + "");

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
                if (mListener != null) mListener.onPreferenceChange(SliderPreferenceEmbedded.this, i);
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

        return view;
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
        mMaxProgress = maxProgess;
    }

    public View getView() {
        return view;
    }

    public int getSavedProgress() {
        return getSharedPreferences().getInt(getKey(), 0);
    }

    @SuppressWarnings("WeakerAccess")
    public int getCurrentProgress() {
        return ((SeekBar) view.findViewById(R.id.slider_pref_seekbar)).getProgress();
    }
}
