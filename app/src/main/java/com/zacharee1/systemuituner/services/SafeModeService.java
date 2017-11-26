package com.zacharee1.systemuituner.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.provider.Settings;

import com.zacharee1.systemuituner.R;
import com.zacharee1.systemuituner.activites.settings.SettingsActivity;
import com.zacharee1.systemuituner.util.SettingsUtils;

public class SafeModeService extends Service {
    private ShutDownReceiver mShutDownReceiver;
    private ThemeChangeReceiver mThemeReceiver;

    private ResolutionChangeListener mResListener;

    private Handler mHandler;

    public SafeModeService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mHandler = new Handler(Looper.getMainLooper());

        startInForeground();
        restoreStateOnStartup();
        setUpReceivers();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        try {
            unregisterReceiver(mThemeReceiver);
        } catch (Exception e) {}

        try {
            unregisterReceiver(mShutDownReceiver);
        } catch (Exception e) {}

        try {
            getContentResolver().unregisterContentObserver(mResListener);
        } catch (Exception e) {}
    }

    private void startInForeground() {
        PendingIntent settingsIntent = PendingIntent.getActivity(this, 0, new Intent(this, SettingsActivity.class), 0);

        Notification notification = new Notification.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Safe Mode Service is running")
                .setContentText("Tap to change settings")
                .setPriority(Notification.PRIORITY_MIN)
                .setContentIntent(settingsIntent)
                .build();

        startForeground(1001, notification);
    }

    private void setUpReceivers() {
        mShutDownReceiver = new ShutDownReceiver();
        mThemeReceiver = new ThemeChangeReceiver();
        mResListener = new ResolutionChangeListener(mHandler);
    }

    private void restoreStateOnStartup() {
        String blacklist = Settings.Secure.getString(getContentResolver(), "icon_blacklist");

        if (blacklist == null || blacklist.isEmpty()) {
            String blacklistBackup = Settings.Global.getString(getContentResolver(), "icon_blacklist_backup");

            if (blacklistBackup != null && !blacklistBackup.isEmpty()) {
                SettingsUtils.writeSecure(this, "icon_blacklist", blacklistBackup);
            }
        }

        String qsAnimState = Settings.Secure.getString(getContentResolver(), "sysui_qs_fancy_anim");

        if (qsAnimState == null || qsAnimState.isEmpty() || qsAnimState.equals("1")) {
            String backupState = Settings.Global.getString(getContentResolver(), "sysui_qs_fancy_anim_backup");

            if (backupState != null && !backupState.isEmpty()) {
                SettingsUtils.writeSecure(this, "sysui_qs_fancy_anim", backupState);
            }
        }

        SettingsUtils.writeGlobal(this, "system_booted", "1");
    }

    private void resetBlacklist(boolean restore) {
        final String blacklist = Settings.Secure.getString(getContentResolver(), "icon_blacklist");

        SettingsUtils.writeGlobal(this, "icon_blacklist_backup", blacklist);
        SettingsUtils.writeSecure(this, "icon_blacklist", "");

       if (restore) {
           mHandler.postDelayed(new Runnable() {
               @Override
               public void run() {
                   SettingsUtils.writeSecure(SafeModeService.this, "icon_blacklist", blacklist);
               }
           }, 400);
       }
    }

    public class ShutDownReceiver extends BroadcastReceiver {
        public ShutDownReceiver() {
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_SHUTDOWN);
            filter.addAction(Intent.ACTION_REBOOT);
            filter.addAction("android.intent.action.QUICKBOOT_POWEROFF");
            filter.addAction("com.htc.intent.action.QUICKBOOT_POWEROFF");

            registerReceiver(this, filter);
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            resetBlacklist(false);
        }
    }

    public class ThemeChangeReceiver extends BroadcastReceiver {
        public ThemeChangeReceiver() {
            IntentFilter filter = new IntentFilter("broadcast com.samsung.android.theme.themecenter.THEME_APPLY");

            registerReceiver(this, filter);
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            resetBlacklist(true);
        }
    }

    public class ResolutionChangeListener extends ContentObserver {
        public ResolutionChangeListener(Handler handler) {
            super(handler);

            getContentResolver().registerContentObserver(Settings.Secure.CONTENT_URI, true, this);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            Uri twRes = Settings.Secure.getUriFor("default_display_size_forced");
            Uri res = Settings.Secure.getUriFor("display_size_forced");

            if (uri.equals(twRes) || uri.equals(res)) {
                resetBlacklist(true);
            }
        }
    }
}
