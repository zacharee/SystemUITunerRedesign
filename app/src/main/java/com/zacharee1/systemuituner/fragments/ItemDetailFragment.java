package com.zacharee1.systemuituner.fragments;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.os.Bundle;
import android.util.Log;

import com.zacharee1.systemuituner.fragmenthelpers.AutoHelper;
import com.zacharee1.systemuituner.fragmenthelpers.BaseHelper;
import com.zacharee1.systemuituner.misc.TweakItems;
import com.zacharee1.systemuituner.activites.ItemDetailActivity;
import com.zacharee1.systemuituner.activites.ItemListActivity;
import com.zacharee1.systemuituner.fragmenthelpers.DemoHelper;
import com.zacharee1.systemuituner.fragmenthelpers.MiscHelper;
import com.zacharee1.systemuituner.fragmenthelpers.QSHelper;
import com.zacharee1.systemuituner.fragmenthelpers.StatbarHelper;
import com.zacharee1.systemuituner.fragmenthelpers.TWHelper;

/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a {@link ItemListActivity}
 * in two-pane mode (on tablets) or a {@link ItemDetailActivity}
 * on handsets.
 */
public class ItemDetailFragment extends PreferenceFragment
{
    private BaseHelper helper;
    private BaseHelper helper2;

    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The dummy content this fragment is presenting.
     */
    @SuppressWarnings("FieldCanBeLocal")
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

        if (getArguments().containsKey(ARG_ITEM_ID))
        {
            final String id = getArguments().getString(ARG_ITEM_ID);
            assert id != null;

            mItem = TweakItems.ITEM_MAP.get(id);

            addPreferencesFromResource(mItem.layoutId);

            switch (id)
            {
                case "statbar":
                    helper = new StatbarHelper(this);
                    break;
                case "qs":
                    helper = new QSHelper(this);
                    break;
                case "demo":
                    helper = new DemoHelper(this);
                    break;
                case "touchwiz":
                    helper = new StatbarHelper(this);
                    helper2 = new TWHelper(this);
                    break;
                case "misc":
                    helper = new MiscHelper(this);
                    break;
                case "auto":
                    helper = new AutoHelper(this);
                    break;
            }

            getActivity().setTitle(mItem.content);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (helper != null) {
            helper.onDestroy();
        }

        if (helper2 != null) {
            helper2.onDestroy();
        }
    }
}
