package com.zacharee1.systemuituner.misc;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.util.Log;
import android.util.TypedValue;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

@SuppressWarnings({"WeakerAccess", "unused"})
public class Utils
{
    public static boolean isPackageInstalled(@SuppressWarnings("SameParameterValue") String packagename, PackageManager packageManager) {
        try {
            packageManager.getPackageInfo(packagename, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public static float pxToDp(Context context, float px) {
        Resources r = context.getResources();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, px, r.getDisplayMetrics());
    }

    public static float pxToSp(Context context, float px) {
        Resources r = context.getResources();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, px, r.getDisplayMetrics());
    }

    public static String runCommand(String... strings) {
        try{
            Process comm = Runtime.getRuntime().exec("sh");
            DataOutputStream outputStream = new DataOutputStream(comm.getOutputStream());

            for (String s : strings) {
                outputStream.writeBytes(s+"\n");
                outputStream.flush();
            }

            outputStream.writeBytes("exit\n");
            outputStream.flush();

            BufferedReader inputReader = new BufferedReader(new InputStreamReader(comm.getInputStream()));
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(comm.getErrorStream()));

            String ret = "";
            String line;

            while ((line = inputReader.readLine()) != null) {
                ret = ret.concat(line).concat("\n");
            }

            while ((line = errorReader.readLine()) != null) {
                ret = ret.concat(line).concat("\n");
            }

            try {
                comm.waitFor();
            } catch (InterruptedException e) {
                e.printStackTrace();
                Log.e("Err", e.getMessage());
            }
            outputStream.close();

            return ret;
        } catch(IOException e){
            e.printStackTrace();
            return null;
        }
    }
}
