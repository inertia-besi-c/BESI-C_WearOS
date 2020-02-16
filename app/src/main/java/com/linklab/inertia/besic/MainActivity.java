package com.linklab.inertia.besic;

/*
 * Imports needed by the system to function appropriately
 */
import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.wearable.activity.WearableActivity;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;

/**
 * This is the main process that is fired upon the application being initiated on the device.
 * This class asks the user to pick what watchface they would like, allows the developer to set
 * the preferences of the device, and also checks for the required permissions the application needs
 */
public class MainActivity extends WearableActivity
{
    SharedPreferences sharedPreferences;        // Initializes the shared preferences
    SystemInformation systemInformation;        // Initializes the system information
    Intent startSettings;      // Initializes intents for the class
    File directory;     // Initializes the files of the class
    DataLogger dataLogger;      // initializes the datalogger of the class
    boolean loggedHeaders;      // Initializes the boolean of the class

    /**
     * This method is run when the application is called at anytime.
     * @param savedInstanceState is a parameter passed into the super class
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);     // Creates an instance of the application

        this.startSettings = new Intent(getApplicationContext(), Settings.class);       // Starts a new intent for the settings class

        this.CheckPermissions();        // Calls the method to check for the required permissions for the device.
        this.startActivity(this.startSettings);       // Starts the intent for the settings

//        this.logHeaders();      // Calls the method to log the files

        this.setContentView(R.layout.activity_main);        // Sets the view of the system
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
                Manifest.permission.BLUETOOTH_ADMIN ,    // This is access the bluetooth and allow changes
                Manifest.permission.WAKE_LOCK,       // This is access to control the screen and cpu thinking power
                Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS        // Asks the user to ignore the battery optimization doze mode setting
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

    /**
     * This method creates the header files for the directories data is logged to
     */
    private void logHeaders()
    {
        this.directory = new File(Environment.getExternalStorageDirectory() + "/" + this.sharedPreferences.getString("directory_key", ""));     // Makes a reference to a directory
        if (!this.directory.isDirectory())       // Checks if the directory is a directory or not, if not, it runs the following
        {
            String[][] Files =      // A list of file and their headers to be made
                    {
                            {getResources().getString(R.string.subdirectory_information), getResources().getString(R.string.eodmode), "Date"},       // End of day Updater file
                            {getResources().getString(R.string.subdirectory_information), getResources().getString(R.string.sleepmode), String.valueOf(this.systemInformation.getSleepMode())},      // SleepMode Updater file
                            {getResources().getString(R.string.subdirectory_sensors), getResources().getString(R.string.pedometer), getResources().getString(R.string.pedometer_header)},       // Pedometer file
                            {getResources().getString(R.string.subdirectory_sensors), getResources().getString(R.string.accelerometer), getResources().getString(R.string.accelerometer_header)},      // Accelerometer file
                            {getResources().getString(R.string.subdirectory_sensors), getResources().getString(R.string.heartrate), getResources().getString(R.string.heartrate_header)},       // Heart Rate File
                            {getResources().getString(R.string.subdirectory_sensors), getResources().getString(R.string.estimote), getResources().getString(R.string.estimote_header)},       // Estimote File
                            {getResources().getString(R.string.subdirectory_logs), getResources().getString(R.string.battery), getResources().getString(R.string.battery_header)},      // Battery file
                            {getResources().getString(R.string.subdirectory_logs), getResources().getString(R.string.settings), getResources().getString(R.string.settings_header)},        // Settings file
                            {getResources().getString(R.string.subdirectory_logs), getResources().getString(R.string.system), getResources().getString(R.string.system_header)},        // System response file
                            {getResources().getString(R.string.subdirectory_logs), getResources().getString(R.string.sensors), getResources().getString(R.string.sensor_header)},        // Sensor response file
                            {getResources().getString(R.string.subdirectory_survey_activities), getResources().getString(R.string.painactivity), getResources().getString(R.string.painactivity_header)},        // Pain activity file
                            {getResources().getString(R.string.subdirectory_survey_activities), getResources().getString(R.string.followupactivity), getResources().getString(R.string.followupactivity_header)},        // Followup activity file
                            {getResources().getString(R.string.subdirectory_survey_activities), getResources().getString(R.string.endofdayactivity), getResources().getString(R.string.endofdayactivity_header)},        // Followup activity file
                            {getResources().getString(R.string.subdirectory_survey_responses), getResources().getString(R.string.painresponse), getResources().getString(R.string.painresponse_header)},       // Pain response file
                            {getResources().getString(R.string.subdirectory_survey_responses), getResources().getString(R.string.followupresponse), getResources().getString(R.string.followupresponse_header)},        // Followup response file
                            {getResources().getString(R.string.subdirectory_survey_responses), getResources().getString(R.string.endofdayresponse), getResources().getString(R.string.endofdayresponse_header)},        // End of Day response file
                    };

            for (String[] file : Files)     // Foe every file in the files
            {
                this.dataLogger = new DataLogger(getApplicationContext(), file[0], file[1], file[2]);       // Make a specified data to the file
                this.dataLogger.saveData("log");        // Save that data in log mode
            }
        }
    }
}
