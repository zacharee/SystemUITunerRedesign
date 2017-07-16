package com.zacharee1.systemuituner.dummy;

import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;

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
    public static final List<TweakItem> ITEMS = new ArrayList<TweakItem>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, TweakItem> ITEM_MAP = new HashMap<String, TweakItem>();

    static {
        addItem(new TweakItem("home", R.drawable.ic_home_black_24dp, "Home", R.layout.layout_home));
        addItem(new TweakItem("statbar", R.drawable.ic_visibility_off_black_24dp, "Status Bar", R.layout.layout_statbar));
        addItem(new TweakItem("qs", R.drawable.ic_settings_black_24dp, "Quick Settings", R.layout.layout_qs));
        addItem(new TweakItem("demo", R.drawable.ic_tv_black_24dp, "Demo Mode", R.layout.layout_demo));
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
        @LayoutRes public final int layoutId;

        public TweakItem(String id, @DrawableRes int drawableId, String content, @LayoutRes int layoutId) {
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
