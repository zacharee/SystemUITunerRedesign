package com.zacharee1.systemuituner.fragments

import android.os.Bundle
import android.preference.PreferenceFragment
import com.zacharee1.systemuituner.fragmenthelpers.*
import com.zacharee1.systemuituner.misc.TweakItems

class ItemDetailFragment : PreferenceFragment() {
    private var helper: BaseHelper? = null

    /**
     * The dummy content this fragment is presenting.
     */
    private var mItem: TweakItems.TweakItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        if (arguments.containsKey(ARG_ITEM_ID)) {
            val id = arguments.getString(ARG_ITEM_ID)!!

            mItem = TweakItems.ITEM_MAP[id]

            addPreferencesFromResource(mItem!!.layoutId)

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

            activity.title = mItem!!.content
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
