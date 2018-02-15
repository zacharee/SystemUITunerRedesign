package com.zacharee1.systemuituner.fragments

import android.os.Bundle
import android.preference.PreferenceFragment
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.fragmenthelpers.*
import com.zacharee1.systemuituner.misc.TweakItems

class ItemDetailFragment : PreferenceFragment() {
    private var helper: BaseHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        if (arguments.containsKey(ARG_ITEM_ID)) {
            val id = arguments.getString(ARG_ITEM_ID)!!
            var item = TweakItems.ITEM_MAP[id]

            if (id == "auto") item = TweakItems.TweakItem(id,
                    R.drawable.ic_help_outline_black_24dp,
                    R.string.auto_detect,
                    R.xml.pref_auto)

            addPreferencesFromResource(item!!.layoutId)

            when (id) {
                "statbar" -> helper = StatbarHelper(this)
                "qs" -> helper = QSHelper(this)
                "demo" -> helper = DemoHelper(this)
                "touchwiz" -> helper = TWHelper(this)
                "misc" -> helper = MiscHelper(this)
                "auto" -> helper = AutoHelper(this)
                "immersive" -> helper = ImmersiveHelper(this)
                "lockscreen" -> helper = LockHelper(this)
            }

            activity.title = resources.getString(item.title)
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        if (helper != null) {
            helper!!.onDestroy()
        }
    }

    override fun onResume() {
        super.onResume()
        if (helper != null) helper!!.onResume()
    }

    companion object {

        /**
         * The fragment argument representing the item ID that this fragment
         * represents.
         */
        const val ARG_ITEM_ID = "item_id"
    }
}
