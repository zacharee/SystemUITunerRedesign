package com.zacharee1.systemuituner.misc

import android.os.Build
import android.support.annotation.DrawableRes
import android.support.annotation.XmlRes
import com.zacharee1.systemuituner.R
import java.util.*

object TweakItems {
    val ITEMS: MutableList<TweakItem> = ArrayList()

    val ITEM_MAP: MutableMap<String, TweakItem> = HashMap()

    init {
        addItem(TweakItem("statbar",
                R.drawable.ic_visibility_off_black_24dp,
                R.string.status_bar,
                R.xml.pref_statbar)
        )
        addItem(TweakItem("qs",
                R.drawable.ic_settings_black_24dp,
                R.string.quick_settings,
                R.xml.pref_qs)
        )
        addItem(TweakItem("demo",
                R.drawable.ic_tv_black_24dp,
                R.string.demo_mode,
                R.xml.pref_demo)
        )
        addItem(TweakItem("touchwiz",
                R.drawable.ic_phone_android_black_24dp,
                R.string.touchwiz,
                R.xml.pref_tw)
        )
        addItem(TweakItem("immersive",
                R.drawable.ic_fullscreen_black_24dp,
                R.string.immersive_mode,
                R.xml.pref_imm)
        )
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1)
            addItem(TweakItem("lockscreen",
                    R.drawable.ic_lock_outline_black_24dp,
                    R.string.lockscreen,
                    R.xml.pref_lock)
            )
        addItem(TweakItem("misc",
                R.drawable.ic_brush_black_24dp,
                R.string.miscellaneous,
                R.xml.pref_misc)
        )
    }

    private fun addItem(item: TweakItem) {
        ITEMS.add(item)
        ITEM_MAP[item.id] = item
    }

    class TweakItem(val id: String, @param:DrawableRes @field:DrawableRes val drawableId: Int, val title: Int, @param:XmlRes @field:XmlRes val layoutId: Int) {

        override fun toString(): String {
            return title.toString()
        }
    }
}
