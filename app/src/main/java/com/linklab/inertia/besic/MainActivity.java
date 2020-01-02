package com.linklab.inertia.besic;

/*
 * Imports needed by the system to function appropriately
 */
import android.content.pm.PackageManager;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.Manifest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

/**
 * This is the main process that is fired upon the application being initiated on the device.
 * This class asks the user to pick what watchface they would like, allows the developer to set
 * the preferences of the device, and also checks for the required permissions the application needs
 */
public class MainActivity extends AppCompatActivity
{
    /**
     * This method is run when the application is called at anytime.
     * @param savedInstanceState is a parameter passed into the super class
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);     // Creates an instance of the application

        Intent changeWatchFace = new Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER).putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,        // Gets a watchface picker to show
                new ComponentName(getPackageName(), WatchFace.class.getName()));        // Shows the components of the watchface
        startActivity(changeWatchFace);     // Starts the intent for the watchface picker

        Intent startSettings = new Intent(MainActivity.this, Settings.class);       // Starts a new intent for the settings class
        startActivity(startSettings);       // Starts the intent for the settings

        this.CheckPermissions();        // Calls the method to check for the required permissions for the device.

        finish();       // Finishes the activity and quits it.
    }

    /**
     * This method checks for the required permission for the application. If the permissions is not granted by the device,
     * it prompts the user to grant such permission to the application.
     */
    private void CheckPermissions()
    {
        String[] Required_Permissions =     // Checks if Device has permission to work on device.
                {
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,     // This is to access the storage
                        Manifest.permission.READ_EXTERNAL_STORAGE,      // This is to access the storage
                        Manifest.permission.VIBRATE,        // This is to access the vibrator of the device
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
