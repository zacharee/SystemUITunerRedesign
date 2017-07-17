package com.zacharee1.systemuituner;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

public class SliderPreference extends DialogPreference
{
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
        return LayoutInflater.from(getContext()).inflate(R.layout.qs_header_count_view, null, true);
    }
}
