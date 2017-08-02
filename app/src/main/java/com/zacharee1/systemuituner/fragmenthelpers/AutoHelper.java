package com.zacharee1.systemuituner.fragmenthelpers;

import android.content.pm.PackageManager;
import android.preference.Preference;
import android.preference.SwitchPreference;
import android.util.Log;

import com.zacharee1.systemuituner.fragments.ItemDetailFragment;
import com.zacharee1.systemuituner.misc.SettingsUtils;
import com.zacharee1.systemuituner.misc.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AutoHelper
{
    private ItemDetailFragment mFragment;
    private Map<String, Preference> mPrefs = new TreeMap<>();

    public AutoHelper(ItemDetailFragment fragment) {
        mFragment = fragment;

        String dump = Utils.runCommand("dumpsys activity service com.android.systemui/.SystemUIService");
        assert dump != null;

        Pattern viewsPattern = Pattern.compile("icon views(?s)(.*)icon slots");
        Matcher viewsMatcher = viewsPattern.matcher(dump);

        String logViews = "";

        while (!viewsMatcher.hitEnd()) {
            if (viewsMatcher.find()) logViews = logViews.concat(viewsMatcher.group());
        }

        Pattern removeTitle = Pattern.compile("icon views:(.*?)\\n");
        Matcher remTitleM = removeTitle.matcher(logViews);
        String rem = "";

        while (!remTitleM.hitEnd()) {
            if (remTitleM.find()) rem = rem.concat(remTitleM.group());
        }

        logViews = logViews.replace(rem, "");
        logViews = logViews.replace("icon slots", "");

        Pattern slotsPattern = Pattern.compile("icon slots(?s)(.*)Hotspot");
        Matcher slotsMatcher = slotsPattern.matcher(dump);

        String logSlots = "";

        while (!slotsMatcher.hitEnd()) {
            if (slotsMatcher.find()) logSlots = logSlots.concat(slotsMatcher.group());
        }

        Pattern removeTitleViews = Pattern.compile("icon slots:(.*?)\\n");
        Matcher remTitleMViews = removeTitleViews.matcher(logSlots);
        String remViews = "";

        while (!remTitleMViews.hitEnd()) {
            if (remTitleMViews.find()) remViews = remViews.concat(remTitleMViews.group());
        }

        logSlots = logSlots.replace(remViews, "");

        Pattern removeFooterViews = Pattern.compile("HotspotController(?s)(.*)mHotspot");
        Matcher remFootViews = removeFooterViews.matcher(logSlots);
        String remFoot = "";

        while (!remFootViews.hitEnd()) {
            if (remFootViews.find()) remFoot = remFoot.concat(remFootViews.group());
        }

        logSlots = logSlots.replace(remFoot, "");

        parseViews(logViews);
        parseSlots(logSlots);

        SettingsUtils.shouldSetSwitchChecked(mFragment);

        for (Preference preference : mPrefs.values()) {
            mFragment.getPreferenceScreen().addPreference(preference);
        }
    }

    private void parseViews(String input) {
        ArrayList<String> slots = new ArrayList<>(Arrays.asList(input.split("[\n]")));

        for (String slot : slots) {
            Pattern p = Pattern.compile("slot=(.+?)\\b");
            Matcher m = p.matcher(slot);

            String slotInfo = "";

            while (!m.hitEnd()) if (m.find()) slotInfo = m.group();

            if (slotInfo.isEmpty()) continue;

            slotInfo = slotInfo.replace("slot=", "");

            SwitchPreference preference = new SwitchPreference(mFragment.getContext());
            preference.setTitle(slotInfo);
            preference.setKey(slotInfo);
            preference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
            {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o)
                {
                    SettingsUtils.changeBlacklist(preference.getKey(), Boolean.valueOf(o.toString()), mFragment.getContext());
                    return true;
                }
            });

            mPrefs.put(preference.getKey(), preference);
        }
    }

    private void parseSlots(String input) {
        ArrayList<String> slots = new ArrayList<>(Arrays.asList(input.split("[\n]")));

        for (String slot : slots) {
            Pattern p = Pattern.compile(": \\((.*?)\\)");
            Matcher m = p.matcher(slot);

            String slotInfo = "";

            while (!m.hitEnd()) if (m.find()) slotInfo = m.group();

            if (slotInfo.isEmpty()) continue;

            slotInfo = slotInfo.replace(": ", "").replace("(", "").replace(")", "");

            if (mFragment.findPreference(slotInfo) == null)
            {
                SwitchPreference preference = new SwitchPreference(mFragment.getContext());
                preference.setTitle(slotInfo);
                preference.setKey(slotInfo);
                preference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
                {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object o)
                    {
                        SettingsUtils.changeBlacklist(preference.getKey(), Boolean.valueOf(o.toString()), mFragment.getContext());
                        return true;
                    }
                });

                mPrefs.put(preference.getKey(), preference);
            }
        }
    }
}
