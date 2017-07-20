package com.zacharee1.systemuituner.misc;

import android.support.annotation.DrawableRes;
import android.support.annotation.XmlRes;

import com.zacharee1.systemuituner.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class TweakItems
{

    /**
     * An array of sample (dummy) items.
     */
    public static final List<TweakItem> ITEMS = new ArrayList<>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, TweakItem> ITEM_MAP = new HashMap<>();

    static {
        addItem(new TweakItem("statbar", R.drawable.ic_visibility_off_black_24dp, "Status Bar", R.xml.pref_statbar));
        addItem(new TweakItem("qs", R.drawable.ic_settings_black_24dp, "Quick Settings", R.xml.pref_qs));
        addItem(new TweakItem("demo", R.drawable.ic_tv_black_24dp, "Demo Mode", R.xml.pref_demo));
        addItem(new TweakItem("touchwiz", R.drawable.ic_phone_android_black_24dp, "TouchWiz", R.xml.pref_tw));
        addItem(new TweakItem("misc", R.drawable.ic_brush_black_24dp, "Miscellaneous", R.xml.pref_misc));
    }

    private static void addItem(TweakItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    /**
     * A dummy item representing a piece of content.
     */
    public static class TweakItem
    {
        public final String id;
        @DrawableRes public final int drawableId;
        public final String content;
        @XmlRes public final int layoutId;

        public TweakItem(String id, @DrawableRes int drawableId, String content, @XmlRes int layoutId) {
            this.id = id;
            this.drawableId = drawableId;
            this.content = content;
            this.layoutId = layoutId;
        }

        @Override
        public String toString() {
            return content;
        }
    }
}
