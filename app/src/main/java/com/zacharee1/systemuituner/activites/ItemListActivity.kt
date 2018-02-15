package com.zacharee1.systemuituner.activites


import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.annotation.ColorInt
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.TypedValue
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.fragments.ItemDetailFragment
import com.zacharee1.systemuituner.handlers.RecreateHandler
import com.zacharee1.systemuituner.misc.OptionSelected
import com.zacharee1.systemuituner.misc.TweakItems
import com.zacharee1.systemuituner.util.Utils

class ItemListActivity : AppCompatActivity() {

    private var mTwoPane: Boolean = false
    private val mItems = TweakItems.ITEMS
    private var mSharedPreferences: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

        setTheme(if (Utils.isInDarkMode(this)) R.style.AppTheme_Dark_NoActionBar else R.style.AppTheme_NoActionBar)

        RecreateHandler.onCreate(this)

        setContentView(R.layout.activity_item_list)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.popupTheme = if (Utils.isInDarkMode(this)) R.style.AppTheme_Dark_PopupOverlay else R.style.AppTheme_PopupOverlay
        setSupportActionBar(toolbar)
        toolbar.title = title

        assert(supportActionBar != null)
        if (!mSharedPreferences!!.getBoolean("hide_welcome_screen", false))
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val recyclerView = findViewById<View>(R.id.item_list)!!
        setupRecyclerView(recyclerView as RecyclerView)

        if (findViewById<View>(R.id.item_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true

            val arguments = Bundle()
            arguments.putString(ItemDetailFragment.ARG_ITEM_ID, "statbar")
            val fragment = ItemDetailFragment()
            fragment.arguments = arguments
            fragmentManager.beginTransaction()
                    .replace(R.id.item_detail_container, fragment)
                    .commit()
        }

        showWarningDialog()
        showMIUIDialog()
    }

    @SuppressLint("SetTextI18n")
    private fun showWarningDialog() {
        if (mSharedPreferences!!.getBoolean("show_system_settings_warning", true)) {
            val dialog = AlertDialog.Builder(this)
                    .setTitle(R.string.warning)
                    .setMessage(resources.getString(R.string.warn_message))
                    .setCancelable(false)
                    .setPositiveButton(R.string.agree, null)
                    .setNeutralButton(R.string.cancel) { _, _ -> finish() }
                    .setNegativeButton(R.string.dont_show) { _, _ -> mSharedPreferences!!.edit().putBoolean("show_system_settings_warning", false).apply() }
                    .create()

            dialog.setOnShowListener {
                val time = 5

                val ok = dialog.getButton(DialogInterface.BUTTON_POSITIVE)
                val dontShow = dialog.getButton(DialogInterface.BUTTON_NEGATIVE)

                ok.isEnabled = false
                dontShow.isEnabled = false

                ok.text = "${ok.text} ($time)"
                dontShow.text ="${dontShow.text} ($time)"

                Thread(Runnable {
                    for (i in time downTo 1) {

                        runOnUiThread {
                            ok.text = ok.text.toString().replace((i + 1).toString(), i.toString())
                            dontShow.text = dontShow.text.toString().replace((i + 1).toString(), i.toString())
                        }

                        try {
                            Thread.sleep(1000)
                        } catch (e: Exception) {
                        }

                    }

                    runOnUiThread {
                        ok.setText(R.string.agree)
                        dontShow.setText(R.string.dont_show)

                        ok.isEnabled = true
                        dontShow.isEnabled = true
                    }
                }).start()
            }

            dialog.show()
        }
    }

    private fun showMIUIDialog() {
        if (Utils.checkMIUI() && mSharedPreferences!!.getBoolean("warn_miui", true)) {
            AlertDialog.Builder(this)
                    .setTitle(R.string.miui_detected)
                    .setMessage(R.string.miui_warning)
                    .setPositiveButton(R.string.ok, null)
                    .setNeutralButton(R.string.dont_show) { _, _ -> mSharedPreferences!!.edit().putBoolean("warn_miui", false).apply() }
                    .show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return OptionSelected.doAction(item, this)
    }

    private fun setupRecyclerView(recyclerView: RecyclerView) {
        recyclerView.addItemDecoration(DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL))
        recyclerView.adapter = SimpleItemRecyclerViewAdapter(mItems)
    }

    override fun onDestroy() {
        RecreateHandler.onDestroy(this)
        super.onDestroy()
    }

    inner class SimpleItemRecyclerViewAdapter(private val mValues: List<TweakItems.TweakItem>) : RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_list_content, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.mItem = mValues[position]
            holder.mIconView.setImageDrawable(resources.getDrawable(holder.mItem!!.drawableId, null).constantState!!.newDrawable().mutate())
            setIconTint(holder)
            holder.mContentView.text = mValues[position].content

            holder.mView.setOnClickListener { v ->
                if (mTwoPane) {
                    val arguments = Bundle()
                    arguments.putString(ItemDetailFragment.ARG_ITEM_ID, holder.mItem!!.id)
                    val fragment = ItemDetailFragment()
                    fragment.arguments = arguments
                    fragmentManager.beginTransaction()
                            .replace(R.id.item_detail_container, fragment)
                            .commit()
                } else {
                    val context = v.context
                    val intent = Intent(context, ItemDetailActivity::class.java)
                    intent.putExtra(ItemDetailFragment.ARG_ITEM_ID, holder.mItem!!.id)

                    context.startActivity(intent)
                }
            }
        }

        override fun getItemCount(): Int {
            return mValues.size
        }

        inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
            val mIconView: ImageView = mView.findViewById(R.id.icon)
            val mContentView: TextView = mView.findViewById(R.id.content)
            var mItem: TweakItems.TweakItem? = null

            override fun toString(): String {
                return super.toString() + " '" + mContentView.text + "'"
            }
        }

        private fun setIconTint(holder: ViewHolder) {

            val typedValue = TypedValue()
            val theme = theme
            theme.resolveAttribute(R.attr.colorAccent, typedValue, true)
            @ColorInt val color = typedValue.data

            holder.mIconView.drawable.setTintList(ColorStateList.valueOf(color))
        }
    }
}
