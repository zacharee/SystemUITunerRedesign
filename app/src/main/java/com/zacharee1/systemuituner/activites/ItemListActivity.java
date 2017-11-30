package com.zacharee1.systemuituner.activites;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import com.zacharee1.systemuituner.fragments.ItemDetailFragment;
import com.zacharee1.systemuituner.R;
import com.zacharee1.systemuituner.misc.TweakItems;
import com.zacharee1.systemuituner.misc.OptionSelected;
import com.zacharee1.systemuituner.handlers.RecreateHandler;
import com.zacharee1.systemuituner.util.Utils;

import java.util.List;

/**
 * An activity representing a list of Items. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link ItemDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class ItemListActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private final List<TweakItems.TweakItem> mItems = TweakItems.ITEMS;
    @SuppressWarnings("FieldCanBeLocal")
    private SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        setTheme(Utils.isInDarkMode(this) ? R.style.AppTheme_Dark_NoActionBar : R.style.AppTheme_NoActionBar);

        RecreateHandler.onCreate(this);

        setContentView(R.layout.activity_item_list);

        final Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setPopupTheme(Utils.isInDarkMode(this) ? R.style.AppTheme_Dark_PopupOverlay : R.style.AppTheme_PopupOverlay);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        assert getSupportActionBar() != null;
        if (!mSharedPreferences.getBoolean("hide_welcome_screen", false))
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        View recyclerView = findViewById(R.id.item_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);

        if (findViewById(R.id.item_detail_container) != null)
        {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;

            Bundle arguments = new Bundle();
            arguments.putString(ItemDetailFragment.ARG_ITEM_ID, "statbar");
            ItemDetailFragment fragment = new ItemDetailFragment();
            fragment.setArguments(arguments);
            getFragmentManager().beginTransaction()
                    .replace(R.id.item_detail_container, fragment)
                    .commit();
        }

        showWarningDialog();
    }

    private void showWarningDialog() {
        if (mSharedPreferences.getBoolean("show_system_settings_warning", true)) {
            final AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle(R.string.warning)
                    .setMessage("This app modifies system settings. Uninstalling won't revert anything, nor can I make that happen. Unless you are comfortable with manual recovery, don't use the app. You have been warned!")
                    .setCancelable(false)
                    .setPositiveButton(R.string.agree, null)
                    .setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    })
                    .setNegativeButton(R.string.dont_show, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            mSharedPreferences.edit().putBoolean("show_system_settings_warning", false).apply();
                        }
                    })
                    .create();

            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialogInterface) {
                    final int time = 5;

                    final Button ok = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
                    final Button dontShow = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);

                    ok.setEnabled(false);
                    dontShow.setEnabled(false);

                    ok.setText(ok.getText().toString().concat(" (" + time + ")"));
                    dontShow.setText(dontShow.getText().toString().concat(" (" + time + ")"));

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            for (int i = time; i > 0; i--) {
                                final int i2 = i;

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ok.setText(ok.getText().toString().replace(String.valueOf(i2 + 1), i2 + ""));
                                        dontShow.setText(dontShow.getText().toString().replace(String.valueOf(i2 + 1), i2 + ""));
                                    }
                                });

                                try {
                                    Thread.sleep(1000);
                                } catch (Exception e) {}
                            }

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ok.setText(R.string.agree);
                                    dontShow.setText(R.string.dont_show);

                                    ok.setEnabled(true);
                                    dontShow.setEnabled(true);
                                }
                            });
                        }
                    }).start();
                }
            });

            dialog.show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return OptionSelected.doAction(item, this);
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(mItems));
    }

    @Override
    protected void onDestroy()
    {
        RecreateHandler.onDestroy(this);
        super.onDestroy();
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<TweakItems.TweakItem> mValues;

        public SimpleItemRecyclerViewAdapter(List<TweakItems.TweakItem> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);
            holder.mIconView.setImageDrawable(getResources().getDrawable(holder.mItem.drawableId, null).getConstantState().newDrawable().mutate());
            setIconTint(holder);
            holder.mContentView.setText(mValues.get(position).content);

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putString(ItemDetailFragment.ARG_ITEM_ID, holder.mItem.id);
                        ItemDetailFragment fragment = new ItemDetailFragment();
                        fragment.setArguments(arguments);
                        getFragmentManager().beginTransaction()
                                .replace(R.id.item_detail_container, fragment)
                                .commit();
                    } else {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, ItemDetailActivity.class);
                        intent.putExtra(ItemDetailFragment.ARG_ITEM_ID, holder.mItem.id);

                        context.startActivity(intent);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final ImageView mIconView;
            public final TextView mContentView;
            public TweakItems.TweakItem mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mIconView = view.findViewById(R.id.icon);
                mContentView = view.findViewById(R.id.content);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mContentView.getText() + "'";
            }
        }

        private void setIconTint(ViewHolder holder) {

            TypedValue typedValue = new TypedValue();
            Resources.Theme theme = getTheme();
            theme.resolveAttribute(R.attr.colorAccent, typedValue, true);
            @ColorInt int color = typedValue.data;

            holder.mIconView.getDrawable().setTintList(ColorStateList.valueOf(color));
        }
    }
}
