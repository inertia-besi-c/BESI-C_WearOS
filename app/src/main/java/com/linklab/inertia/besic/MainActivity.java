package com.linklab.inertia.besic;

import android.content.pm.PackageManager;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.Manifest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Intent changeWatchFace = new Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER).putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                new ComponentName(getPackageName(), WatchFace.class.getName()));
        startActivity(changeWatchFace);

        Intent startSettings = new Intent(MainActivity.this, Settings.class);
        startActivity(startSettings);

        this.CheckPermissions();

        finish();
    }

    private void CheckPermissions()
    {
        String[] Required_Permissions =     // Checks if Device has permission to work on device.
                {
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,     // This is to access the storage
                        Manifest.permission.READ_EXTERNAL_STORAGE,      // This is to access the storage
                        Manifest.permission.VIBRATE,
                        Manifest.permission.BODY_SENSORS,       // This is to access the sensors of the device
                        Manifest.permission.ACCESS_WIFI_STATE,      // This is to access the wifi of the device.
                        Manifest.permission.CHANGE_WIFI_STATE,      // This is to change the wifi state of the device.
                        Manifest.permission.ACCESS_NETWORK_STATE,       // This is to access the network
                        Manifest.permission.CHANGE_NETWORK_STATE,        // This is to change the network setting of the device.
                        Manifest.permission.ACCESS_COARSE_LOCATION,     // This is to access the location in a general sense
                        Manifest.permission.ACCESS_FINE_LOCATION,       // This is to access the location in a more specific manner
                        Manifest.permission.BLUETOOTH,      // This is to access th bluetooth
                        Manifest.permission.BLUETOOTH_ADMIN     // This is access the bluetooth and allow changes
                };

        boolean needPermissions = false;        // To begin the permission is set to false.

        for (String permission : Required_Permissions)     // For each of the permission listed above.
        {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED)       // Check if they have permission to work on the device.
            {
                needPermissions = true;     // if they do, grant them permission
            }
        }

        if (needPermissions)        // When they have permission
        {
            ActivityCompat.requestPermissions(this, Required_Permissions,0);     // Allow them to work on device.
        }
    }
}
