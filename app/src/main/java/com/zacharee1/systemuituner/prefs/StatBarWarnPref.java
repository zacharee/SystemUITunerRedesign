package com.zacharee1.systemuituner.prefs;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.preference.Preference;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

import com.zacharee1.systemuituner.R;

public class StatBarWarnPref extends Preference {
    public StatBarWarnPref(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public StatBarWarnPref(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public StatBarWarnPref(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public StatBarWarnPref(Context context) {
        super(context);
    }

    @Override
    protected View onCreateView(ViewGroup parent) {
        View view = super.onCreateView(parent);
        setTitle(R.string.warning);
        setSummary(R.string.statbar_rotation_lock_notif);
        setIcon(R.drawable.ic_smartphone_black_24dp);
        setSelectable(false);

        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = getContext().getTheme();
        theme.resolveAttribute(R.attr.colorAccent, typedValue, true);
        @ColorInt int color = typedValue.data;

        Drawable icon = getContext().getResources().getDrawable(R.drawable.ic_smartphone_black_24dp, null);
        icon.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);

        setIcon(icon);
        return view;
    }
}
