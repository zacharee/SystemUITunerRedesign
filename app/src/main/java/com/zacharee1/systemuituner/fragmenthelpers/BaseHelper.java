package com.zacharee1.systemuituner.fragmenthelpers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
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

    public SharedPreferences getSharedPreferences() {
        return getFragment().getPreferenceManager().getSharedPreferences();
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

    public Resources getResources() {
        return getActivity().getResources();
    }

    public void startActivity(Intent intent) {
        getActivity().startActivity(intent);
    }

    public void startActivityForResult(Intent intent, int requestCode) {
        getActivity().startActivityForResult(intent, requestCode);
    }

    public void sendBroadcast(Intent intent) {
        getActivity().sendBroadcast(intent);
    }

    public void onResume() {

    }

    public abstract void onDestroy();
}
