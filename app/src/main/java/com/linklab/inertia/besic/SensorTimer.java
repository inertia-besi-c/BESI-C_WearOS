package com.linklab.inertia.besic;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class SensorTimer extends Service
{
    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        // Coming soon

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }
}
