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

//        Pattern viewsPattern = Pattern.compile("icon views(?s)(.*)icon slots");
//        Matcher viewsMatcher = viewsPattern.matcher(dump);
//
//        String logViews = "";
//
//        while (!viewsMatcher.hitEnd()) {
//            if (viewsMatcher.find()) logViews = logViews.concat(viewsMatcher.group());
//        }
//
//        Pattern removeTitle = Pattern.compile("icon views:(.*?)\\n");
//        Matcher remTitleM = removeTitle.matcher(logViews);
//        String rem = "";
//
//        while (!remTitleM.hitEnd()) {
//            if (remTitleM.find()) rem = rem.concat(remTitleM.group());
//        }
//
//        logViews = logViews.replace(rem, "");
//        logViews = logViews.replace("icon slots", "");
//
//        Pattern slotsPattern = Pattern.compile("icon slots(?s)(.*)Hotspot");
//        Matcher slotsMatcher = slotsPattern.matcher(dump);
//
//        String logSlots = "";

//        while (!slotsMatcher.hitEnd()) {
//            if (slotsMatcher.find()) logSlots = logSlots.concat(slotsMatcher.group());
//        }
//
//        Pattern removeTitleViews = Pattern.compile("icon slots:(.*?)\\n");
//        Matcher remTitleMViews = removeTitleViews.matcher(logSlots);
//        String remViews = "";
//
//        while (!remTitleMViews.hitEnd()) {
//            if (remTitleMViews.find()) remViews = remViews.concat(remTitleMViews.group());
//        }
//
//        logSlots = logSlots.replace(remViews, "");
//
//        parseViews(logViews);
//        parseSlots(logSlots);

        String icons = dump.substring(dump.indexOf("icon slots"));
        ArrayList<String> ico = new ArrayList<>(Arrays.asList(icons.split("[\\n]")));
        ico.remove(0);
        for (String slot : ico) {
            if (slot.startsWith("         ") || slot.startsWith("        ")) {
                Pattern p = Pattern.compile("\\((.*?)\\)");
                Matcher m = p.matcher(slot);

                while (!m.hitEnd()) {
                    if (m.find()){
                        String result = m.group().replace("(", "").replace(")", "");
                        Log.e("SLOT", result);

                        SwitchPreference preference = new SwitchPreference(mFragment.getContext());
                        preference.setTitle(result);
                        preference.setKey(result);
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
                        break;
                    }
                }
            }
            else break;
        }

        Pattern p = Pattern.compile("slot=(.+?)\\s");
        Matcher m = p.matcher(dump);
        String find = "";

        while (!m.hitEnd()) if (m.find()) find = find.concat(m.group()).concat("\n");

        ArrayList<String> slots = new ArrayList<>(Arrays.asList(find.split("[\\n]")));
        for (String slot : slots) {
            slot = slot.replace("slot=", "").replaceAll(" ", "");
            Log.e("SLOT", slot);

            SwitchPreference preference = new SwitchPreference(mFragment.getContext());
            preference.setTitle(slot);
            preference.setKey(slot);
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

        for (Preference preference : mPrefs.values()) {
            mFragment.getPreferenceScreen().addPreference(preference);
        }

        SettingsUtils.shouldSetSwitchChecked(mFragment);
    }

//    private void parseViews(String input) {
//        ArrayList<String> slots = new ArrayList<>(Arrays.asList(input.split("[\n]")));
//        Log.e("Views", slots.toString());
//
//        for (String slot : slots) {
//            Pattern p = Pattern.compile("slot=(.+?)\\b");
//            Matcher m = p.matcher(slot);
//
//            String slotInfo = "";
//
//            while (!m.hitEnd()) if (m.find()) slotInfo = m.group();
//
//            if (slotInfo.isEmpty()) continue;
//
//            slotInfo = slotInfo.replace("slot=", "");
//
//            SwitchPreference preference = new SwitchPreference(mFragment.getContext());
//            preference.setTitle(slotInfo);
//            preference.setKey(slotInfo);
//            preference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
//            {
//                @Override
//                public boolean onPreferenceChange(Preference preference, Object o)
//                {
//                    SettingsUtils.changeBlacklist(preference.getKey(), Boolean.valueOf(o.toString()), mFragment.getContext());
//                    return true;
//                }
//            });
//
//            mPrefs.put(preference.getKey(), preference);
//        }
//    }

//    private void parseSlots(String input) {
//        ArrayList<String> slots = new ArrayList<>(Arrays.asList(input.split("[\n]")));
//        Log.e("Slots", slots.toString());
//
//        for (String slot : slots) {
//            Pattern p = Pattern.compile(": \\((.*?)\\)");
//            Matcher m = p.matcher(slot);
//
//            String slotInfo = "";
//
//            while (!m.hitEnd()) if (m.find()) slotInfo = m.group();
//
//            if (slotInfo.isEmpty()) continue;
//
//            slotInfo = slotInfo.replace(": ", "").replace("(", "").replace(")", "");
//
//            if (mFragment.findPreference(slotInfo) == null)
//            {
//                SwitchPreference preference = new SwitchPreference(mFragment.getContext());
//                preference.setTitle(slotInfo);
//                preference.setKey(slotInfo);
//                preference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
//                {
//                    @Override
//                    public boolean onPreferenceChange(Preference preference, Object o)
//                    {
//                        SettingsUtils.changeBlacklist(preference.getKey(), Boolean.valueOf(o.toString()), mFragment.getContext());
//                        return true;
//                    }
//                });
//
//                mPrefs.put(preference.getKey(), preference);
//            }
//        }
//    }
}
