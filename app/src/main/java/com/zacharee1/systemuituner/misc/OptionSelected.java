package com.zacharee1.systemuituner.misc;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.MenuItem;

import com.zacharee1.systemuituner.R;
import com.zacharee1.systemuituner.activites.AboutActivity;
import com.zacharee1.systemuituner.activites.SettingsActivity;

public class OptionSelected
{
    public static boolean doAction(MenuItem item, Context context) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent settingsIntent = new Intent(context, SettingsActivity.class);
                context.startActivity(settingsIntent);
                return true;
            case R.id.action_about:
                Intent aboutIntent = new Intent(context, AboutActivity.class);
                context.startActivity(aboutIntent);
                return true;
            case R.id.action_github:
                Intent ghIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://github.com/zacharee/SystemUITunerRedesign"));
                context.startActivity(ghIntent);
                return true;
            case R.id.action_telegram:
                Intent teleIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/joinchat/AAAAAEIB6WKWL-yphJbZwg"));
                context.startActivity(teleIntent);
                return true;
            case R.id.action_g_plus:
                Intent gIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://plus.google.com/communities/113741695211107417994"));
                context.startActivity(gIntent);
                return true;
            case R.id.action_xda_thread:
                Intent xdaIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://forum.xda-developers.com/android/apps-games/app-systemui-tuner-t3588675"));
                context.startActivity(xdaIntent);
                return true;
        }
        return false;
    }
}
