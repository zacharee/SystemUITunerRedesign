package com.zacharee1.systemuituner.services;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.zacharee1.systemuituner.receivers.ShutdownReceiver;

public class ShutdownService extends Service
{
    private ShutdownReceiver mReceiver;

    public ShutdownService()
    {
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.d("ShutdownService", "Started!");
        mReceiver = new ShutdownReceiver();
        registerReceiver(mReceiver, new IntentFilter(Intent.ACTION_SHUTDOWN));

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy()
    {
        try {
            unregisterReceiver(mReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }
}
