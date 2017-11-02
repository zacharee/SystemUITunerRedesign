package com.zacharee1.systemuituner.prefs;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.preference.Preference;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zacharee1.systemuituner.R;

public class IconPreference extends Preference {
    private View mView;

    private CharSequence mTitleString;
    private CharSequence mSummaryString;

    private int mDrawableColor;

    private Drawable mDrawable;

    public IconPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public IconPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public IconPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.IconPreference,
                0, 0);

        try {
            mTitleString = a.getString(R.styleable.IconPreference_pref_title);
            mSummaryString = a.getString(R.styleable.IconPreference_pref_summary);
            mDrawable = a.getDrawable(R.styleable.IconPreference_pref_icon);
            mDrawableColor = a.getColor(R.styleable.IconPreference_pref_icon_tint, Color.TRANSPARENT);

            if (mDrawable != null) mDrawable.setColorFilter(mDrawableColor, PorterDuff.Mode.SRC_ATOP);
        } finally {
            a.recycle();
        }
    }

    public IconPreference(Context context) {
        super(context);
    }

    @Override
    protected View onCreateView(ViewGroup parent) {
        setLayoutResource(R.layout.preference_icon);

        View view = super.onCreateView(parent);
        mView = view;

        setInternal();

        return view;
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);

        mView = view;

        setInternal();
    }

    @Nullable
    public View getView() {
        return mView;
    }

    public void setIcon(int drawableResId) {
        mDrawable = getContext().getResources().getDrawable(drawableResId, null);
        notifyChanged();
    }

    public void setIcon(Drawable drawable) {
        mDrawable = drawable;
        notifyChanged();
    }

    public void setIconColor(@ColorInt int color) {
        mDrawableColor = color;
        notifyChanged();
    }

    @Override
    public void setTitle(int titleResId) {
        mTitleString = getContext().getResources().getString(titleResId);
        notifyChanged();
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitleString = title;
        notifyChanged();
    }

    @Override
    public void setSummary(int summaryResId) {
        mSummaryString = getContext().getResources().getString(summaryResId);
        notifyChanged();
    }

    @Override
    public void setSummary(CharSequence summary) {
        mSummaryString = summary;
        notifyChanged();
    }

    @Override
    protected void notifyChanged() {
        setInternal();
        super.notifyChanged();
    }

    private void setInternal() {
        setIconColorInternal(mDrawableColor);

        setIconInternal(mDrawable);

        setTitleInternal(mTitleString);

        setSummaryInternal(mSummaryString);
    }

    private void setIconInternal(Drawable drawable) {
        if (mView != null && drawable != null) {
            ((ImageView) mView.findViewById(R.id.icon)).setImageDrawable(drawable);
            mView.invalidate();
        }
    }

    private void setIconColorInternal(@ColorInt int color) {
        if (mView != null && mDrawable != null && color != -1) {
            mDrawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
            setIconInternal(mDrawable);
            mView.invalidate();
        }
    }

    private void setTitleInternal(CharSequence string) {
        if (mView != null && string != null) {
            ((TextView) mView.findViewById(R.id.title)).setText(string);
            mView.invalidate();
        }
    }

    private void setSummaryInternal(CharSequence string) {
        if (mView != null && string != null) {
            ((TextView) mView.findViewById(R.id.summary)).setText(string);
        }
    }
}
