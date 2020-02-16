package com.linklab.inertia.besic;

/*
 * Imports needed by the system to function appropriately
 */

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.wearable.activity.WearableActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.util.Map;
import java.util.Objects;

/**
 * This is the main process that is fired upon the application being initiated on the device.
 * This class asks the user to pick what watchface they would like, allows the developer to set
 * the preferences of the device, and also checks for the required permissions the application needs
 */
public class MainActivity extends WearableActivity
{
    private BroadcastReceiver minuteUpdateReceiver;     // Sets up the broadcast receiver
    private SharedPreferences sharedPreferences;        // Initializes the shared preferences
    private Vibrator vibrator;      // Initializes the vibrator of the class
    private Map<String, ?> preferenceKeys;      // Creates a map to store key values
    private SystemInformation systemInformation;        // Initializes the system information
    private Intent startSettings, startEMA;      // Initializes intents for the class
    private IntentFilter intentFilter;      // Makes the intent filter of the system
    private File directory;     // Initializes the files of the class
    private DataLogger dataLogger;      // initializes the datalogger of the class
    private StringBuilder stringBuilder;        // Initializes string builder of the system
    private Button start, sleep;        // Makes all button on the system
    private TextView date, time, battery;        // Makes all text views on the system
    private String batteryInformation;      // Sets up the string in the class
    private int hapticLevel;        // Initializes the integers of the class

    /**
     * This method is run when the application is called at anytime.
     * @param savedInstanceState is a parameter passed into the super class
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);     // Creates an instance of the application

        this.startSettings = new Intent(getApplicationContext(), Settings.class);       // Starts a new intent for the settings class
        this.systemInformation = new SystemInformation();       // Initializes the system information
        this.intentFilter = new IntentFilter();     // Initializes the intent filter

        this.setContentView(R.layout.activity_main);        // Sets the view of the system

        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());        // Gets the preferences from the shared preference object.
        this.vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);      // Get instance of Vibrator from current Context

        this.preferenceKeys = this.sharedPreferences.getAll();      // Saves all the key values into a map
        this.hapticLevel = Integer.valueOf(Objects.requireNonNull(this.sharedPreferences.getString("haptic_level", "")));
        this.intentFilter.addAction(Intent.ACTION_TIME_TICK);       // Initializes the filter to be tied to the time tick

        this.start = findViewById(R.id.start);      // Sets up the start button in the view
        this.sleep = findViewById(R.id.sleep);      // Sets up the sleep button in the view
        this.date = findViewById(R.id.date);        // Sets up the date view
        this.time = findViewById(R.id.time);        // Sets up the time view
        this.battery = findViewById(R.id.battery);      // Sets up the battery view

        this.CheckPermissions();        // Calls the method to check for the required permissions for the device.
        this.setUpUIElements();     // Calls the specified method to run

        this.start.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                vibrator.vibrate(hapticLevel);
                logHeaders();

                startEMA = new Intent(getApplicationContext(), PainSurvey.class);
                startActivity(startEMA);
            }
        });
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

            this.startActivity(this.startSettings);       // Starts the intent for the settings
            this.logHeaders();      // Calls the method to log the header files
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

            this.logInitialSettings();      // Calls the method to log the initial setting into the file
        }
    }

    private void setUpUIElements()
    {
        this.batteryInformation = getResources().getString(R.string.battery_level_string) + " " + this.systemInformation.getBatteryLevel(getApplicationContext()) + "%";

        this.time.setText(this.systemInformation.getDateTime("h:mm a"));
        this.date.setText(this.systemInformation.getDateTime("MMM d, YYYY"));
        this.battery.setText(this.batteryInformation);
    }

    /**
     * This method is called to log the data that is set in the shared preferences to the device.
     */
    private void logInitialSettings()
    {
        this.stringBuilder = new StringBuilder();       // Initializes the string builder variable

        for(Map.Entry<String,?> preferenceItem : preferenceKeys.entrySet())     // For every key in the map
        {
            this.stringBuilder.append(this.systemInformation.getDateTime("yyyy/MM/dd HH:mm:ss:SSS")).append(",").append(preferenceItem.getKey()).append(",").append(preferenceItem.getValue());     // Appends the data to be logged
            this.stringBuilder.append("\n");        // Appends a new line to the data
        }

        this.dataLogger = new DataLogger(getApplicationContext(), getResources().getString(R.string.subdirectory_logs), getResources().getString(R.string.settings), String.valueOf(this.stringBuilder));        // Make a new datalogger inference
        this.dataLogger.saveData("log");        // Type of save to do
    }

    /**
     * This method is here to run specified items at every watch minute update on the system time change
     */
    private void startMinuteUpdater()
    {
        this.minuteUpdateReceiver = new BroadcastReceiver()     // Makes a broadcast receiver from the system
        {
            /**
             * This method receives information from the system when the time changes
             * @param context is the application context around the system
             * @param intent is the intent to be used
             */
            @Override
            public void onReceive(Context context, Intent intent)
            {
                logHeaders();      // Calls the method to log the header files
                setUpUIElements();      // Calls the method to set up UI elements
            }
        };

        registerReceiver(this.minuteUpdateReceiver, this.intentFilter);     // Registers the receiver with the system to make sure it runs
    }

    /**
     * This method is called if the application is paused for any reason
     */
    @Override
    protected void onPause()
    {
        super.onPause();        // Calls the super class method
        unregisterReceiver(this.minuteUpdateReceiver);      // Unregisters the receiver
    }

    /**
     * This method is called when the system resumes back to this class
     */
    @Override
    protected void onResume()
    {
        super.onResume();       // Calls the super class method
        startMinuteUpdater();       // Unregisters the receiver
    }

    /**
     * This method is called when the system needs to end this activity
     */
    @Override
    protected void onStop()     // To stop the activity.
    {
        super.onStop();     // Calls the super class method
    }
}
