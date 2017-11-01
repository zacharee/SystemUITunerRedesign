package com.zacharee1.systemuituner.fragmenthelpers;

import android.app.Activity;
import android.content.Context;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;

import com.zacharee1.systemuituner.fragments.ItemDetailFragment;

public abstract class BaseHelper {
    private ItemDetailFragment mFragment;

    public BaseHelper(ItemDetailFragment fragment) {
        mFragment = fragment;
    }

    public Context getContext() {
        return mFragment.getContext();
    }

    public Activity getActivity() {
        return mFragment.getActivity();
    }

    public ItemDetailFragment getFragment() {
        return mFragment;
    }

    public Preference findPreference(String preference) {
        return mFragment.findPreference(preference);
    }

    public PreferenceScreen getPreferenceScreen() {
        return mFragment.getPreferenceScreen();
    }

    public PreferenceManager getPreferenceManager() {
        return mFragment.getPreferenceManager();
    }

    public void onResume() {
        
    }

    public abstract void onDestroy();
}
