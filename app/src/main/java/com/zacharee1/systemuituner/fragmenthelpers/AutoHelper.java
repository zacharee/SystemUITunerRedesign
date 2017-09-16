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
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AutoHelper extends BaseHelper
{
    private ItemDetailFragment mFragment;
    private Map<String, Preference> mPrefs = new TreeMap<>();

    public AutoHelper(ItemDetailFragment fragment) {
        mFragment = fragment;

        String dump = Utils.runCommand("dumpsys activity service com.android.systemui/.SystemUIService");
        assert dump != null;

        int index = dump.indexOf("icon slots");
        if (index != -1)
        {
            String icons = dump.substring(index);
            ArrayList<String> ico = new ArrayList<>(Arrays.asList(icons.split("[\\n]")));
            ico.remove(0);
            for (String slot : ico)
            {
                if (slot.startsWith("         ") || slot.startsWith("        "))
                {
                    Pattern p = Pattern.compile("\\((.*?)\\)");
                    Matcher m = p.matcher(slot);

                    while (!m.hitEnd())
                    {
                        if (m.find())
                        {
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
                } else break;
            }
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

    @Override
    public void onDestroy() {

    }
}
