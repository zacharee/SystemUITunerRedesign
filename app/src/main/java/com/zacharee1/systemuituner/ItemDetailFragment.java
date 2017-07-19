package com.zacharee1.systemuituner;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.os.Bundle;
import android.preference.PreferenceGroup;
import android.preference.PreferenceScreen;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.zacharee1.systemuituner.activites.ItemDetailActivity;
import com.zacharee1.systemuituner.activites.ItemListActivity;
import com.zacharee1.systemuituner.fragmenthelpers.DemoHelper;
import com.zacharee1.systemuituner.fragmenthelpers.MiscHelper;
import com.zacharee1.systemuituner.fragmenthelpers.QSHelper;
import com.zacharee1.systemuituner.fragmenthelpers.StatbarHelper;
import com.zacharee1.systemuituner.fragmenthelpers.TWHelper;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Hashtable;

/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a {@link ItemListActivity}
 * in two-pane mode (on tablets) or a {@link ItemDetailActivity}
 * on handsets.
 */
public class ItemDetailFragment extends PreferenceFragment
{
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The dummy content this fragment is presenting.
     */
    private TweakItems.TweakItem mItem;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ItemDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            final String id = getArguments().getString(ARG_ITEM_ID);
            assert id != null;

            mItem = TweakItems.ITEM_MAP.get(id);

            addPreferencesFromResource(mItem.layoutId);

            switch (id) {
                case "statbar":
                    new StatbarHelper(this);
                    break;
                case "qs":
                    new QSHelper(this);
                    break;
                case "demo":
                    new DemoHelper(this);
                    break;
                case "touchwiz":
                    new StatbarHelper(this);
                    new TWHelper(this);
                    break;
                case "misc":
                    new MiscHelper(this);
                    break;
            }

            getActivity().setTitle(mItem.content);

//            new Thread(new Runnable()
//            {
//                @Override
//                public void run()
//                {
//                    try
//                    {
//                        Field f = PreferenceFragment.class.getDeclaredField("mHavePrefs"); //NoSuchFieldException
//                        f.setAccessible(true);
//
//                        while (!((boolean) f.get(PreferenceFragment.class.cast(ItemDetailFragment.this))));
//
//                        getActivity().runOnUiThread(new Runnable()
//                        {
//                            @Override
//                            public void run()
//                            {
//                                setIconTints();
//                            }
//                        });
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            }).start();
        }
    }

    private void setIconTints() {

        Log.e("PREF", getPreferenceScreen().getRootAdapter().getCount() + "");

        for (int i = 0; i < getPreferenceScreen().getRootAdapter().getCount(); i++) {
            Preference preference = (Preference) getPreferenceScreen().getRootAdapter().getItem(i);

            Drawable icon = preference.getIcon();

            if (icon != null)
            {
                boolean DARK = getPreferenceManager().getSharedPreferences().getBoolean("dark_mode", false);
                if (DARK)
                {
                    icon.setTintList(ColorStateList.valueOf(Color.WHITE));
                } else {
                    icon.setTintList(ColorStateList.valueOf(Color.BLACK));
                }
            }
        }
    }
}
