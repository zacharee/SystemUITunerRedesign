package com.zacharee1.systemuituner.misc;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zacharee1.systemuituner.R;
import com.zacharee1.systemuituner.activites.AppsListActivity;
import com.zacharee1.systemuituner.activites.ComponentsListActivity;
import com.zacharee1.systemuituner.fragmenthelpers.LockHelper;

import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.CustomHolder> {
    private ArrayList<AppInfo> apps = new ArrayList<>();
    private Context context;
    private boolean isLeft;

    public CustomAdapter(ArrayList<AppInfo> appInfos, Context context, boolean isLeft) {
        apps = appInfos;
        this.context = context;
        this.isLeft = isLeft;
    }

    @Override
    public int getItemCount() {
        return apps.size();
    }

    @Override
    public CustomHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.app_info_layout, parent, false);
        return new CustomHolder(view);
    }

    @Override
    public void onBindViewHolder(CustomHolder holder, int position) {
        holder.setAppInfo(apps.get(position));
    }

    public class CustomHolder extends RecyclerView.ViewHolder {
        public AppInfo appInfo;
        public View view;

        CustomHolder(View v) {
            super(v);
            view = v;
        }

        public void setAppInfo(AppInfo info) {
            appInfo = info;

            setAppName();
            setAppIcon();
            setListener();
        }

        public void setAppName() {
            TextView name = view.findViewById(R.id.app_name);
            name.setText(appInfo.appName);
        }

        public void setAppIcon() {
            ImageView icon = view.findViewById(R.id.app_icon);
            icon.setImageDrawable(appInfo.appIcon);
        }

        private void setListener() {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (context instanceof AppsListActivity) {
                        Intent activity = new Intent(context, ComponentsListActivity.class);
                        activity.putExtra("package", appInfo.packageName);
                        activity.putExtra("name", appInfo.appName);
                        activity.putExtra("isLeft", isLeft);

                        ((Activity) context).startActivityForResult(activity, 1337);
                    } else if (context instanceof ComponentsListActivity) {
                        SettingsUtils.writeSecure(context, isLeft ? "sysui_keyguard_left" : "sysui_keyguard_right", appInfo.packageName + "/" + appInfo.componentName);
                        Activity activity = (Activity) context;
                        activity.setResult(Activity.RESULT_OK);
                        activity.finish();
                    }
                }
            });
        }
    }
}
