package com.zacharee1.systemuituner.Utils;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Zacha on 7/17/2017.
 */

public class SuUtils
{
    public static void sudo(String... strings) {
        try{
            Process su = Runtime.getRuntime().exec("su");
            DataOutputStream outputStream = new DataOutputStream(su.getOutputStream());

            for (String s : strings) {
                outputStream.writeBytes(s+"\n");
                outputStream.flush();
            }

            outputStream.writeBytes("exit\n");
            outputStream.flush();
            try {
                su.waitFor();
            } catch (InterruptedException e) {
                e.printStackTrace();
                Log.e("No Root?", e.getMessage());
            }
            outputStream.close();
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    public static boolean testSudo() {
        StackTraceElement st = null;

        try{
            Process su = Runtime.getRuntime().exec("su");
            DataOutputStream outputStream = new DataOutputStream(su.getOutputStream());

            outputStream.writeBytes("exit\n");
            outputStream.flush();

            DataInputStream inputStream = new DataInputStream(su.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            while (bufferedReader.readLine() != null) {
                bufferedReader.readLine();
            }

            su.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
            for (StackTraceElement s : e.getStackTrace()) {
                st = s;
                if (st != null) break;
            }
        }

        return st == null;
    }
}
