package com.zacharee1.systemuituner.fragmenthelpers;

import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;

import com.zacharee1.systemuituner.ItemDetailFragment;

/**
 * Created by Zacha on 7/16/2017.
 */

public class DemoHelper
{
    private ItemDetailFragment mFragment;
    private final SharedPreferences mSharedPreferences;

    public DemoHelper(ItemDetailFragment fragment) {
        mFragment = fragment;
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mFragment.getContext());

        setIconTints();
    }

    private void setIconTints() {
        for (int i = 0; i < mFragment.getPreferenceScreen().getRootAdapter().getCount(); i++) {
            Object pref = mFragment.getPreferenceScreen().getRootAdapter().getItem(i);

            if (pref instanceof Preference)
            {
                Preference preference = (Preference) pref;
                Drawable icon = preference.getIcon();

                if (icon != null)
                {
                    boolean DARK = mSharedPreferences.getBoolean("dark_mode", false);
                    if (DARK)
                    {
                        icon.setTintList(ColorStateList.valueOf(Color.WHITE));
                    }
                }
            }
        }
    }
}
