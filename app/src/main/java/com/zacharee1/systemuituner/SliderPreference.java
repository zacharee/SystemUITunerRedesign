package com.zacharee1.systemuituner;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

public class SliderPreference extends DialogPreference
{
    private View view;
    private OnDialogClosedListener listener;

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
        final View view = LayoutInflater.from(getContext()).inflate(R.layout.qs_header_count_view, null, true);

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

        this.view = view;
        return view;
    }

    @Override
    protected void onDialogClosed(boolean positiveResult)
    {
        super.onDialogClosed(positiveResult);

        if (listener != null) listener.onDialogClosed(positiveResult, ((SeekBar)view.findViewById(R.id.qqs_count_seekbar)).getProgress());
    }

    public void setOnDialogClosedListener(OnDialogClosedListener listener) {
        this.listener = listener;
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

    public interface OnDialogClosedListener {
        void onDialogClosed(boolean positiveResult, int progress);
    }
}
