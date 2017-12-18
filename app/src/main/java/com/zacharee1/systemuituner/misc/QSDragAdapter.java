package com.zacharee1.systemuituner.misc;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zacharee1.systemuituner.R;
import com.zacharee1.systemuituner.util.SettingsUtils;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QSDragAdapter extends RecyclerView.Adapter<QSDragAdapter.QSViewHolder> {
    public ArrayList<QSTile> mTiles = new ArrayList<>();

    public ArrayList<QSTile> mAvailableTiles = new ArrayList<>();

    private Context mContext;

    public QSDragAdapter(Context context) {
        mContext = context;
        parseTileList();
    }

    public void parseTileList() {
        String tiles = Settings.Secure.getString(mContext.getContentResolver(), "sysui_qs_tiles");

        if (tiles == null) {
            tiles = getDefaultTileOrder();
        }

        String[] tileArray = tiles.split("[,]");

        ArrayList<QSTile> tempTiles = new ArrayList<>();

        for (String tile : tileArray) {
            tempTiles.add(new QSTile(tile, mContext));
        }

        mTiles.clear();
        mTiles.addAll(tempTiles);

        refreshAvailableTiles();
    }

    public void refreshAvailableTiles() {
        mAvailableTiles.clear();
        for (QSTile tile : getDefaultTiles()) {
            boolean hasTile = false;

            for (QSTile tile1 : mTiles) {
                if (tile1.key.equals(tile.key)) hasTile = true;
            }

            if (!hasTile) {
                mAvailableTiles.add(tile);
            }
        }
    }

    private ArrayList<QSTile> getDefaultTiles() {
        String order = getDefaultTileOrder();
        String[] array = order.split("[,]");

        ArrayList<QSTile> ret = new ArrayList<>();

        for (String tile : array) {
            ret.add(new QSTile(tile, mContext));
        }

        return ret;
    }

    private String getDefaultTileOrder() {
        PackageManager pm = mContext.getPackageManager();

        try {
            Resources resources = pm.getResourcesForApplication("com.android.systemui");
            int id = resources.getIdentifier("quick_settings_tiles_default", "string", "com.android.systemui");

            return resources.getString(id);
        } catch (Exception e) {
            return "";
        }
    }

    public void addTile(QSTile tile) {
        mTiles.add(tile);
        notifyDataSetChanged();

        setOrder(mTiles);
        refreshAvailableTiles();
    }

    public void setOrder(ArrayList<QSDragAdapter.QSTile> tiles) {
        ArrayList<String> keys = new ArrayList<>();

        for (QSTile tile : tiles) {
            keys.add(tile.key);
        }

        String tileString = TextUtils.join(",", keys);

        SettingsUtils.writeSecure(mContext, "sysui_qs_tiles", tileString);
    }

    @Override
    public QSViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.qs_tile_layout, parent, false);
        return new QSViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final QSViewHolder holder, int position) {
        holder.setTitle(mTiles.get(holder.getAdapterPosition()).title);
        holder.setIcon(mTiles.get(holder.getAdapterPosition()).icon);
        holder.setCloseListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(mContext)
                        .setTitle(R.string.removing_tile)
                        .setMessage(String.format(holder.getContext().getResources().getString(R.string.remove_tile), mTiles.get(holder.getAdapterPosition()).title))
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mTiles.remove(holder.getAdapterPosition());
                                setOrder(mTiles);
                                notifyItemRemoved(holder.getAdapterPosition());
                            }
                        })
                        .setNegativeButton(R.string.no, null)
                        .show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mTiles.size();
    }

    public static class QSViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public QSViewHolder(View view) {
            super(view);
            mView = view;

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                mView.findViewById(R.id.close_button).setVisibility(View.GONE);
            }
        }

        public void setTitle(String title) {
            TextView textView = mView.findViewById(R.id.textView);
            textView.setText(title);
        }

        public void setIcon(Drawable icon) {
            ImageView imageView = mView.findViewById(R.id.imageView);
            imageView.setImageDrawable(icon);
        }

        public void setCloseListener(View.OnClickListener listener) {
            mView.findViewById(R.id.close_button).setOnClickListener(listener);
        }

        public Context getContext() {
            return mView.getContext();
        }
    }

    public static class QSTile {
        public String title;
        public Drawable icon;
        public String key;

        public TileParser mParser;

        public QSTile(String key, Context context) {
            this.key = key;
            mParser = new TileParser(key, context);

            this.title = mParser.title;
            this.icon = mParser.icon;
        }
    }

    public static class TileParser {
        public String key;
        public Drawable icon;
        public String title;

        private Context mContext;

        public TileParser(String key, Context context) {
            this.key = key;
            mContext = context;

            parseKey();
        }

        private void parseKey() {
            if (key.toLowerCase().contains("intent(")) {
                parseIntent();
            } else if (key.toLowerCase().contains("custom(")) {
                parseCustom();
            } else {
                parseStandard();
            }
        }

        private void parseIntent() {
            Drawable drawable = mContext.getResources().getDrawable(R.drawable.ic_android_black_24dp, null);
            drawable.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);

            icon = drawable;

            Pattern p = Pattern.compile("\\((.*?)\\)");
            Matcher m = p.matcher(key);

            String title = "";

            while (!m.hitEnd()) {
                if (m.find()) title = m.group();
            }

            this.title = title.replace("(", "").replace(")", "");
        }

        private void parseCustom() {
            Pattern p = Pattern.compile("\\((.*?)\\)");
            Matcher m = p.matcher(key);

            String name = "";

            while (!m.hitEnd()) {
                if (m.find()) name = m.group();
            }

            name = name.replace("(", "").replace(")", "");

            String packageName = name.split("[/]")[0];
            String component = name.split("[/]")[1];

            try {
                icon = mContext.getPackageManager().getApplicationIcon(packageName);
            } catch (Exception e) {}

            try {
                String[] split = component.split("[.]");
                title = split[split.length - 1];
            } catch (Exception e) {}
        }

        private void parseStandard() {
            title = key.toLowerCase();

            int iconRes = R.drawable.ic_android_black_24dp;

            switch (key.toLowerCase()) {
                case "wifi":
                    iconRes = R.drawable.ic_signal_wifi_4_bar_black_24dp;
                    break;
                case "bluetooth":
                case "bt":
                    iconRes = R.drawable.ic_bluetooth_black_24dp;
                    break;
                case "color_inversion":
                case "inversion":
                    iconRes = R.drawable.ic_invert_colors_black_24dp;
                    break;
                case "cell":
                    iconRes = R.drawable.ic_signal_cellular_4_bar_black_24dp;
                    break;
                case "do_not_disturb":
                case "dnd":
                    iconRes = R.drawable.ic_do_not_disturb_on_black_24dp;
                    break;
                case "airplane":
                    iconRes = R.drawable.ic_airplanemode_active_black_24dp;
                    break;
                case "cast":
                    iconRes = R.drawable.ic_cast_black_24dp;
                    break;
                case "location":
                    iconRes = R.drawable.ic_location_on_black_24dp;
                    break;
                case "rotation":
                    iconRes = R.drawable.ic_screen_rotation_black_24dp;
                    break;
                case "flashlight":
                    iconRes = R.drawable.ic_highlight_black_24dp;
                    break;
                case "hotspot":
                    iconRes = R.drawable.ic_wifi_tethering_black_24dp;
                    break;
                case "battery":
                    iconRes = R.drawable.ic_battery_full_black_24dp;
                    break;
                case "sound":
                    iconRes = R.drawable.ic_volume_up_black_24dp;
                    break;
                case "sync":
                    iconRes = R.drawable.ic_sync_black_24dp;
                    break;
                case "nfc":
                    iconRes = R.drawable.ic_nfc_black_24dp;
                    break;
                case "data":
                    iconRes = R.drawable.ic_data_usage_black_24dp;
                    break;
            }

            Drawable drawable = mContext.getResources().getDrawable(iconRes, null).getCurrent().mutate();
            drawable.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);

            icon = drawable;
        }
    }
}
