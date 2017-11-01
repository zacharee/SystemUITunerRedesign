package com.zacharee1.systemuituner.prefs;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.preference.Preference;
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

    private int mTitleRes;
    private int mSummaryRes;
    private int mDrawableRes;

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
            try {
                mTitleRes = a.getInt(R.styleable.IconPreference_pref_title, 0);
            } catch (NumberFormatException e) {
                mTitleString = a.getString(R.styleable.IconPreference_pref_title);
            }
            try {
                mSummaryRes = a.getInt(R.styleable.IconPreference_pref_summary, 0);
            } catch (NumberFormatException e) {
                mSummaryString = a.getString(R.styleable.IconPreference_pref_summary);
            }
            mDrawableRes = a.getInt(R.styleable.IconPreference_pref_icon, 0);
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

        setIconInternal(mDrawableRes);
        setIconInternal(mDrawable);

        setTitleInternal(mTitleRes);
        setTitleInternal(mTitleString);

        setSummaryInternal(mSummaryRes);
        setSummaryInternal(mSummaryString);

        return view;
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);

        mView = view;

        setIconInternal(mDrawableRes);
        setIconInternal(mDrawable);

        setTitleInternal(mTitleRes);
        setTitleInternal(mTitleString);

        setSummaryInternal(mSummaryRes);
        setSummaryInternal(mSummaryString);
    }

    public void setIcon(int drawableResId) {
        mDrawableRes = drawableResId;
        notifyChanged();
    }

    public void setIcon(Drawable drawable) {
        mDrawable = drawable;
        notifyChanged();
    }

    @Nullable
    public View getView() {
        return mView;
    }

    @Override
    public void setTitle(int titleResId) {
        mTitleRes = titleResId;
        notifyChanged();
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitleString = title;
        notifyChanged();
    }

    @Override
    public void setSummary(int summaryResId) {
        mSummaryRes = summaryResId;
        notifyChanged();
    }

    @Override
    public void setSummary(CharSequence summary) {
        mSummaryString = summary;
        notifyChanged();
    }

    @Override
    protected void notifyChanged() {
        setIconInternal(mDrawableRes);
        setIconInternal(mDrawable);

        setTitleInternal(mTitleRes);
        setTitleInternal(mTitleString);

        setSummaryInternal(mSummaryRes);
        setSummaryInternal(mSummaryString);

        super.notifyChanged();
    }

    private void setIconInternal(int res) {
        if (mView != null && res != 0) {
            ((ImageView) mView.findViewById(R.id.icon)).setImageResource(res);
            mView.invalidate();
        }
    }

    private void setIconInternal(Drawable drawable) {
        if (mView != null && drawable != null) {
            ((ImageView) mView.findViewById(R.id.icon)).setImageDrawable(drawable);
            mView.invalidate();
        }
    }

    private void setTitleInternal(int res) {
        if (mView != null && res != 0) {
            ((TextView) mView.findViewById(R.id.title)).setText(res);
            mView.invalidate();
        }
    }

    private void setTitleInternal(CharSequence string) {
        if (mView != null && string != null) {
            ((TextView) mView.findViewById(R.id.title)).setText(string);
            mView.invalidate();
        }
    }

    private void setSummaryInternal(int res) {
        if (mView != null && res != 0) {
            ((TextView) mView.findViewById(R.id.summary)).setText(res);
            mView.invalidate();
        }
    }

    private void setSummaryInternal(CharSequence string) {
        if (mView != null && string != null) {
            ((TextView) mView.findViewById(R.id.summary)).setText(string);
        }
    }
}
