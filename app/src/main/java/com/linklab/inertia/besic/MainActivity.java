package com.linklab.inertia.besic;

/*
 * Imports needed by the system to function appropriately
 */

import android.Manifest;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.wearable.activity.WearableActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.util.Calendar;
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
    private Calendar calendar;      // Initializes the calendar variable
    private Window window;      // Gets access to the touch screen of the device
    private Map<String, ?> preferenceKeys;      // Creates a map to store key values
    private SystemInformation systemInformation;        // Initializes the system information
    private Intent startSettings, startEMA, startLowBattery, startSensors, estimoteSensor, startAWSUpload;      // Initializes intents for the class
    private IntentFilter minuteTimeTick;      // Makes the intent filter of the system
    private File directory;     // Initializes the files of the class
    private DataLogger dataLogger, checkSteps, checkDate;      // initializes the datalogger of the class
    private StringBuilder stringBuilder;        // Initializes string builder of the system
    private Button start, sleep, dailyEMA, powerOffScreen;        // Makes all button on the system
    private TextView date, time, battery;        // Makes all text views on the system
    private String batteryInformation, data;      // Sets up the string in the class
    private int hapticLevel, sleepAutomatically, lowBatteryThreshHold, lowBatteryTime, startHour, startMinute, startSecond, endHour, endMinute, endSecond;        // Initializes the integers of the class
    private boolean sleepMode, runLowBattery, runEODEMAButton, uploadToAWS;      // Initializes the boolean variables of the class
    private long startAutomaticEMA;     // Initializes the long variables of the class

    /**
     * This method is run when the application is called at anytime.
     * @param savedInstanceState is a parameter passed into the super class
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);     // Creates an instance of the application

        this.unlockScreen();        // Unlocks the screen

        this.startSettings = new Intent(getApplicationContext(), Settings.class);       // Starts a new intent for the settings class
        this.startSensors = new Intent(getApplicationContext(), SensorTimer.class);     // Sets up the intent for the service
        this.estimoteSensor = new Intent(getApplicationContext(), Estimote.class);     // Sets up the intent for the service
        this.startLowBattery = new Intent(getApplicationContext(), Battery.class);     // Sets up the intent for the service
        this.startAWSUpload = new Intent(getApplicationContext(), Amazon.class);
        this.systemInformation = new SystemInformation();       // Initializes the system information
        this.minuteTimeTick = new IntentFilter();     // Initializes the intent filter

        this.CheckPermissions();        // Calls the method to check for the required permissions for the device.
        this.setContentView(R.layout.activity_main);        // Sets the view of the system

        this.start = findViewById(R.id.start);      // Sets up the start button in the view
        this.sleep = findViewById(R.id.sleep);      // Sets up the sleep button in the view
        this.dailyEMA = findViewById(R.id.eodSurvey);       // Sets up the button in the view
        this.date = findViewById(R.id.date);        // Sets up the date view
        this.time = findViewById(R.id.time);        // Sets up the time view
        this.battery = findViewById(R.id.battery);      // Sets up the battery view

        start.setVisibility(View.INVISIBLE);       // Sets the button look
        sleep.setVisibility(View.INVISIBLE);       // Sets the button look
        dailyEMA.setVisibility(View.INVISIBLE);       // Sets the button look
        time.setVisibility(View.INVISIBLE);       // Sets the button look
        date.setVisibility(View.INVISIBLE);       // Sets the button look
        battery.setVisibility(View.INVISIBLE);       // Sets the button look
        this.powerOffScreen = findViewById(R.id.powerOffScreen);        // Sets up the power off screen item
        this.powerOffScreen.setText(this.systemInformation.getDateTime("h:mm a"));      // Sets some text on the screen

        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());        // Gets the preferences from the shared preference object.
        this.vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);      // Get instance of Vibrator from current Context
        this.preferenceKeys = this.sharedPreferences.getAll();      // Saves all the key values into a map
        this.minuteTimeTick.addAction(Intent.ACTION_TIME_TICK);       // Initializes the filter to be tied to the time tick

        this.hapticLevel = Integer.valueOf(Objects.requireNonNull(this.sharedPreferences.getString("haptic_level", "20")));       // Gets the haptic  level preference
        this.sleepAutomatically = Integer.valueOf(Objects.requireNonNull(this.sharedPreferences.getString("sleepmode_setup", "45")));     // Gets the sleep level max number
        this.lowBatteryThreshHold = Integer.valueOf(Objects.requireNonNull(this.sharedPreferences.getString("low_battery_alert", "15")));     // Gets the low battery thresh hold
        this.lowBatteryTime = Integer.valueOf(Objects.requireNonNull(this.sharedPreferences.getString("battery_remind", "10")));        // Sets up a timer
        this.directory = new File(Environment.getExternalStorageDirectory() + "/" + this.sharedPreferences.getString("directory_key", getResources().getString(R.string.app_name_abbreviated)));     // Makes a reference to a directory
        this.checkSteps = new DataLogger(getApplicationContext(), getResources().getString(R.string.subdirectory_information), getResources().getString(R.string.steps), "no");      // Sets a new datalogger variable
        this.checkDate = new DataLogger(getApplicationContext(), getResources().getString(R.string.subdirectory_information), getResources().getString(R.string.eodmode), "Date");      // Sets a new datalogger variable
        this.sleepMode = false;     // Initializes the sleepmode variable
        this.uploadToAWS = false;     // Initializes the variable

        this.setUpUIElements();     // Calls the specified method to run
        this.setUpLowBattery();     // Calls the method
        this.setUpEODEMAButton();       // Calls the method
        this.logHeaders();      // Calls the method

        this.start.setOnClickListener(new View.OnClickListener()        // Sets up the start button
        {
            /**
             * The following is run when the button is clicked
             * @param v is the view in which it is clicked in
             */
            @Override
            public void onClick(View v)
            {
                vibrator.vibrate(hapticLevel);      // Vibrates at the specific interval

                data = systemInformation.getDateTime("yyyy/MM/dd HH:mm:ss:SSS") + (",") + "Main Activity" + (",") + "Starting Pain EMA";       // Data to be logged by the system
                dataLogger = new DataLogger(getApplicationContext(), getResources().getString(R.string.subdirectory_logs), getResources().getString(R.string.system), data);      // Sets a new datalogger variable
                dataLogger.saveData("log");      // Saves the data in the mode specified

                startEMA = new Intent(getApplicationContext(), PainSurvey.class);       // Makes a new intent
                startActivity(startEMA);        // Starts the activity
                finish();       // Clears the main activity
            }
        });

        this.dailyEMA.setOnClickListener(new View.OnClickListener()        // Sets up the daily survey button
        {
            /**
             * The following is run when the button is clicked
             * @param v is the view in which it is clicked in
             */
            @Override
            public void onClick(View v)
            {
                vibrator.vibrate(hapticLevel);      // Vibrates at the specific interval

                data = systemInformation.getDateTime("yyyy/MM/dd HH:mm:ss:SSS") + (",") + "Main Activity" + (",") + "Starting End of Day EMA";       // Data to be logged by the system
                dataLogger = new DataLogger(getApplicationContext(), getResources().getString(R.string.subdirectory_logs), getResources().getString(R.string.system), data);      // Sets a new datalogger variable
                dataLogger.saveData("log");      // Saves the data in the mode specified

                startEMA = new Intent(getApplicationContext(), EndOfDayPromptM1.class);       // Makes a new intent
                startActivity(startEMA);        // Starts the activity
                finish();       // Clears the main activity
            }
        });

        this.sleep.setOnClickListener(new View.OnClickListener()        // Sets up the sleep button
        {
            /**
             * The following is run when the button is clicked
             * @param v is the view in which the button is in
             */
            @Override
            public void onClick(View v)
            {
                vibrator.vibrate(hapticLevel);      // Vibrates at the specific interval
                sleepSettings(true);        // Calls the method accordingly
            }
        });

        this.minuteUpdateReceiver = new BroadcastReceiver()     // Makes a broadcast receiver from the system
        {
            /**
             * This method receives information from the system when the time changes
             *
             * @param context is the application context around the system
             * @param intent  is the intent to be used
             */
            @Override
            public void onReceive(Context context, Intent intent)
            {
                startMinuteUpdater();       // Calls the method
            }
        };

        this.powerOffScreen.setOnClickListener(new View.OnClickListener()       // Listener for the screen off look
        {
            /**
             * This is run when the button is clicked
             * @param v is the view of the button
             */
            @Override
            public void onClick(View v)
            {
                powerOffScreen.setVisibility(View.INVISIBLE);       // Sets the button look
                start.setVisibility(View.VISIBLE);       // Sets the button look
                sleep.setVisibility(View.VISIBLE);       // Sets the button look
                dailyEMA.setVisibility(View.VISIBLE);       // Sets the button look
                time.setVisibility(View.VISIBLE);       // Sets the button look
                date.setVisibility(View.VISIBLE);       // Sets the button look
                battery.setVisibility(View.VISIBLE);       // Sets the button look
            }
        });

        registerReceiver(this.minuteUpdateReceiver, this.minuteTimeTick);     // Registers the receiver with the system to make sure it runs
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
            ActivityCompat.requestPermissions(this, Required_Permissions, 0);     // Allow them to work on device.
        }
    }

    /**
     * This method creates the header files for the directories data is logged to
     */
    private void logHeaders()
    {
        String[][] Files =      // A list of file and their headers to be made
                {
                        {getResources().getString(R.string.subdirectory_information), getResources().getString(R.string.eodmode), "Date"},       // End of day Updater file
                        {getResources().getString(R.string.subdirectory_information), getResources().getString(R.string.steps), "no"},      // stepMode Updater file
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

        if (this.directory.isDirectory())       // Checks if the directory is a directory or not, if not, it runs the following
        {
            if (Files.length != this.findAndLogAllFiles(this.directory))        // Checks if all the files have been added
            {
                for (String[] file : Files)     // For every file in the files
                {
                    this.dataLogger = new DataLogger(getApplicationContext(), file[0], file[1], file[2]);       // Make a specified data to the file
                    this.dataLogger.saveData("write");        // Save that data in log mode
                }

                this.logInitialSettings();      // Calls the method to log the initial setting into the file
                this.systemInformation.setSetSettings(true);        // Sets the variable in the information level to be done
            }
        }
        else        // If Not
        {
            this.startActivity(this.startSettings);     // Run the settings

            for (String[] file : Files)     // For every file in the files
            {
                this.dataLogger = new DataLogger(getApplicationContext(), file[0], file[1], file[2]);       // Make a specified data to the file
                this.dataLogger.saveData("write");        // Save that data in log mode
            }

            this.logInitialSettings();      // Calls the method to log the initial setting into the file
            this.systemInformation.setSetSettings(true);        // Sets the variable in the information level to be done
        }
    }

    /**
     * This method is made to recursively find all the files in a given directory
     * @param folder  is the directory files is needed from
     */
    private int findAndLogAllFiles(final File folder)
    {
        int value = 0;      // Initializes a value

        for (final File fileEntry : folder.listFiles())     // For every file in the folder
        {
            if (fileEntry.isDirectory())        // If the file is a directory
            {
                value += this.findAndLogAllFiles(fileEntry);     // Calls the method
            }
            else        // IF not
            {
                value++;        // Increments the value
            }
        }

        return value;       // returns the value
    }

    /**
     * Method to set up the items on the user interface and update them
     */
    private void setUpUIElements()
    {
        this.batteryInformation = getResources().getString(R.string.battery_level_string) + " " + this.systemInformation.getBatteryLevel(getApplicationContext()) + "%";        // Sets up the text for the battery

        this.time.setText(this.systemInformation.getDateTime("h:mm a"));        // Sets up the time text
        this.date.setText(this.systemInformation.getDateTime("MMM d, YYYY"));       // Sets up the date text
        this.battery.setText(this.batteryInformation);      // Sets up the battery text

        this.checkSteps.saveData("write");       // Writes data to the storage location
    }

    /**
     * Method to set up the information for the low battery screen check
     */
    private void setUpLowBattery()
    {
        this.startHour = Integer.valueOf(Objects.requireNonNull(this.sharedPreferences.getString("battery_hour_alert_start", "00")));     // Gets the start hour from preferences
        this.startMinute = Integer.valueOf(Objects.requireNonNull(this.sharedPreferences.getString("battery_hour_alert_start", "00")));     // Gets the start minute from preferences
        this.startSecond = Integer.valueOf(Objects.requireNonNull(this.sharedPreferences.getString("battery_hour_alert_start", "00")));     // Gets the start second from preferences
        this.endHour = Integer.parseInt(Objects.requireNonNull(this.sharedPreferences.getString("battery_hour_alert_end", "07")));         // Gets the end hour from preferences
        this.endMinute = Integer.valueOf(Objects.requireNonNull(this.sharedPreferences.getString("battery_hour_alert_end", "59")));     // Gets the end minute from preferences
        this.endSecond = Integer.valueOf(Objects.requireNonNull(this.sharedPreferences.getString("battery_hour_alert_end", "59")));     // Gets the end second from preferences

        this.runLowBattery = this.systemInformation.isTimeBetweenTimes(this.systemInformation.getDateTime("HH:mm:ss"), startHour, endHour, startMinute, endMinute, startSecond, endSecond);     // Calls the deciding method
        this.lowBatteryTime--;      // Decrements the time

        if(this.systemInformation.getBatteryLevel(this.getApplicationContext()) <= this.lowBatteryThreshHold && !this.runLowBattery  && this.lowBatteryTime < 0 && !this.isRunning(Battery.class) && !this.systemInformation.isCharging(this.getApplicationContext()))        // Checks if the battery level is lower than expected
        {
            this.lowBatteryTime = Integer.valueOf(Objects.requireNonNull(this.sharedPreferences.getString("battery_remind", "10")));        // Sets up a timer
            this.data = this.systemInformation.getDateTime("yyyy/MM/dd HH:mm:ss:SSS") + (",") + "Main Activity" + (",") + "Starting Low Battery Notification";       // Data to be logged by the system
            this.dataLogger = new DataLogger(this.getApplicationContext(), getResources().getString(R.string.subdirectory_logs), getResources().getString(R.string.system), data);      // Sets a new datalogger variable
            this.dataLogger.saveData("log");      // Saves the data in the mode specified

            this.startActivity(this.startLowBattery);
            this.finish();       // Clears the main activity
        }
    }

    /**
     * This method sets up the actions that the sleep button would perform if called
     */
    private void sleepSettings(Boolean toast)
    {
        this.setUpUIElements();      // Calls the method to set up UI elements

        if (!this.systemInformation.isCharging(this.getApplicationContext()))     // Checks if the system is charging
        {
            if (this.sleepMode)      // Checks if sleepmode is enabled, if it is
            {
                this.data = this.systemInformation.getDateTime("yyyy/MM/dd HH:mm:ss:SSS") + (",") + "Main Activity" + (",") + "Do not disturb De-Activated";       // Data to be logged by the system
                this.dataLogger = new DataLogger(this.getApplicationContext(), this.getResources().getString(R.string.subdirectory_logs), this.getResources().getString(R.string.system), this.data);      // Sets a new datalogger variable
                this.dataLogger.saveData("log");      // Saves the data in the mode specified

                this.sleep.setBackgroundColor(Color.BLUE);       // Changes the background

                if(toast)       // Checks if there is a need to toast
                    this.systemInformation.toast(this.getApplicationContext(), "Do not disturb is OFF");      // Shows a toast

                if(!this.isRunning(SensorTimer.class))       // Checks if the class is not running
                    this.startService(this.startSensors);     // Calls to start the service class
                this.sleepAutomatically = Integer.valueOf(Objects.requireNonNull(this.sharedPreferences.getString("sleepmode_setup", "")));     // Resets the value

                this.sleepMode = false;       // Explicitly sets the sleepmode to be false
            }
            else        // If sleepmode is not enabled
            {
                this.data = this.systemInformation.getDateTime("yyyy/MM/dd HH:mm:ss:SSS") + (",") + "Main Activity" + (",") + "Do not disturb Activated";       // Data to be logged by the system
                this.dataLogger = new DataLogger(this.getApplicationContext(), this.getResources().getString(R.string.subdirectory_logs), this.getResources().getString(R.string.system), this.data);      // Sets a new datalogger variable
                this.dataLogger.saveData("log");      // Saves the data in the mode specified

                this.sleep.setBackgroundColor(Color.GRAY);       // Changes the background

                if(toast)       // Checks if there is a need to toast
                    this.systemInformation.toast(this.getApplicationContext(), "Do not disturb is ON");       // Shows a toast

                this.sleepMode = true;       // Explicitly sets the sleepmode to be true
            }
        }
        else        // If the system is charging
        {
            this.sleep.setBackgroundColor(Color.GRAY);       // Sets the background color

            if(!this.uploadToAWS)        // If we have not uploaded to AWS
                this.systemInformation.toast(getApplicationContext(), "Charging Device");        // Shows a toast

            if (isRunning(SensorTimer.class))       // Checks if the class is running
                this.stopService(startSensors);     // Calls to start the service class

            this.sleepMode = true;       // Explicitly sets the sleepmode to be true
        }

        this.checkSteps.saveData("write");       // Writes data to the storage location
    }

    /**
     * Method to set up the information for the low battery screen check
     */
    private void setUpEODEMAButton()
    {
        this.calendar = Calendar.getInstance();     // Makes an instance of the calendar
        this.calendar.set(Calendar.HOUR_OF_DAY, Integer.valueOf(Objects.requireNonNull(this.sharedPreferences.getString("eod_automatic_start_hour", "20"))));     // Assigns the hour
        this.calendar.set(Calendar.MINUTE, Integer.valueOf(Objects.requireNonNull(this.sharedPreferences.getString("eod_automatic_start_minute", "00"))));        // Assigns the minute
        this.calendar.set(Calendar.SECOND, Integer.valueOf(Objects.requireNonNull(this.sharedPreferences.getString("eod_automatic_start_second", "00"))));        // Assigns the seconds

        this.startHour = Integer.valueOf(Objects.requireNonNull(this.sharedPreferences.getString("eod_manual_start_hour", "17")));     // Gets the start hour from preferences
        this.startMinute = Integer.valueOf(Objects.requireNonNull(this.sharedPreferences.getString("eod_manual_start_minute", "00")));     // Gets the start minute from preferences
        this.startSecond = Integer.valueOf(Objects.requireNonNull(this.sharedPreferences.getString("eod_manual_start_second", "00")));     // Gets the start second from preferences
        this.endHour = Integer.parseInt(Objects.requireNonNull(this.sharedPreferences.getString("eod_manual_end_hour", "23")));         // Gets the end hour from preferences
        this.endMinute = Integer.valueOf(Objects.requireNonNull(this.sharedPreferences.getString("eod_manual_end_minute", "59")));     // Gets the end minute from preferences
        this.endSecond = Integer.valueOf(Objects.requireNonNull(this.sharedPreferences.getString("eod_manual_end_second", "59")));     // Gets the end second from preferences

        this.runEODEMAButton = this.systemInformation.isTimeBetweenTimes(this.systemInformation.getDateTime("HH:mm:ss"), startHour, endHour, startMinute, endMinute, startSecond, endSecond);     // Calls the deciding method
        this.startAutomaticEMA = this.calendar.getTimeInMillis();       // Gets the time in milliseconds for the calendar time

        if (runEODEMAButton && !this.checkDate.readData().contains(this.systemInformation.getDateTime("yyyy/MM/dd")))       // Checks the conditions for the daily survey button
        {
            data = systemInformation.getDateTime("yyyy/MM/dd HH:mm:ss:SSS") + (",") + "Main Activity" + (",") + "End of Day Button Activated";       // Data to be logged by the system
            dataLogger = new DataLogger(getApplicationContext(), getResources().getString(R.string.subdirectory_logs), getResources().getString(R.string.system), data);      // Sets a new datalogger variable
            dataLogger.saveData("log");      // Saves the data in the mode specified

            this.dailyEMA.bringToFront();       // Brings the button to the front
        }
        else        // If the conditions fail
        {
            data = systemInformation.getDateTime("yyyy/MM/dd HH:mm:ss:SSS") + (",") + "Main Activity" + (",") + "Sleep Button Activated";       // Data to be logged by the system
            dataLogger = new DataLogger(getApplicationContext(), getResources().getString(R.string.subdirectory_logs), getResources().getString(R.string.system), data);      // Sets a new datalogger variable
            dataLogger.saveData("log");      // Saves the data in the mode specified

            this.sleep.bringToFront();      // Brings the button to the front
        }

        if (System.currentTimeMillis() >= this.startAutomaticEMA && System.currentTimeMillis() <= this.startAutomaticEMA + (60*1000))     // Checks the time for the automatic ema
        {
            if (!this.systemInformation.isCharging(getApplicationContext()) && !sleepMode && !this.checkDate.readData().contains(this.systemInformation.getDateTime("yyyy/MM/dd")))      // Makes sure the system is not charging or in sleepmode
            {
                data = systemInformation.getDateTime("yyyy/MM/dd HH:mm:ss:SSS") + (",") + "Main Activity" + (",") + "Automatically Starting End of Day Survey";       // Data to be logged by the system
                dataLogger = new DataLogger(getApplicationContext(), getResources().getString(R.string.subdirectory_logs), getResources().getString(R.string.system), data);      // Sets a new datalogger variable
                dataLogger.saveData("log");      // Saves the data in the mode specified

                this.startEMA = new Intent(getApplicationContext(), EndOfDayPromptA1.class);       // Makes a new intent
                this.startActivity(this.startEMA);        // Starts the activity
            }
        }
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
        setUpLowBattery();      // Calls the method
        setUpEODEMAButton();        // Calls the method

        if(systemInformation.isCharging(getApplicationContext()))       // Checks if the system is charging
        {
            sleepMode = false;      // Forces the sleep mode to be false;
            sleepSettings(false);        // Calls the sleep setting

            if (isRunning(SensorTimer.class))
                stopService(startSensors);      // Stops the sensor class
            if(!isRunning(Estimote.class))       // Checks if the class is not running
                startService(estimoteSensor);        // Starts the estimote service

            data = systemInformation.getDateTime("yyyy/MM/dd HH:mm:ss:SSS") + (",") + "Main Activity" + (",") + "Uploading Data to Amazon Web Services!!!";       // Data to be logged by the system
            dataLogger = new DataLogger(getApplicationContext(), getResources().getString(R.string.subdirectory_logs), getResources().getString(R.string.system), data);      // Sets a new datalogger variable
            dataLogger.saveData("log");      // Saves the data in the mode specified

            if(!uploadToAWS && !isRunning(Amazon.class))      // If the update has not been run
            {
                startActivity(startAWSUpload);       // Starts an upload intent to aws
                uploadToAWS = true;       // Sets the variable to be true
            }
        }
        else        // if the system is not charging
        {
            uploadToAWS = false;      // Resets the variable

            if (sleepAutomatically <= 0)     // Checks if the variable is below the limit
            {
                sleepMode = false;      // Forces the sleepmode to be false
                sleepSettings(false);        // Calls the sleep setting
                if (isRunning(SensorTimer.class))       // If the sensor timer class is not running
                    stopService(startSensors);     // Calls to stop the service class
            }
            else        // If the variable is within parameters
            {
                if (!isRunning(SensorTimer.class))       // If the sensor timer class is not running
                    startService(startSensors);     // Calls to start the service class
            }

            if (checkSteps.readData().contains("no"))       // Checks if the file is a no
                sleepAutomatically--;       // Decrements the boolean value

            if (checkSteps.readData().contains("yes"))        // If it contains anything else
            {
                sleepAutomatically = Integer.valueOf(Objects.requireNonNull(sharedPreferences.getString("sleepmode_setup", "45")));       // Resets the variable
                sleepMode = true;       // Sets the value to be true
                sleepSettings(false);        // Calls the sleep setting
            }
        }

        checkSteps.saveData("write");       // Writes data to the file
        setUpUIElements();      // Calls the method to set up UI elements

        sleepAutomatically++;       // Increments the counter

        start.setVisibility(View.INVISIBLE);       // Sets the button look
        sleep.setVisibility(View.INVISIBLE);       // Sets the button look
        dailyEMA.setVisibility(View.INVISIBLE);       // Sets the button look
        time.setVisibility(View.INVISIBLE);       // Sets the button look
        date.setVisibility(View.INVISIBLE);       // Sets the button look
        battery.setVisibility(View.INVISIBLE);       // Sets the button look
        powerOffScreen.setVisibility(View.VISIBLE);      // Sets the visibility of the item to be true
        powerOffScreen.setText(systemInformation.getDateTime("h:mm a"));      // Sets some text on the screen
    }

    /**
     * Checks if a given service is currently running or not
     * @param serviceClass is the service class to be checked
     * @return a boolean true or false
     */
    @SuppressWarnings("ALL")        // Suppresses the warnings associated with this method
    private boolean isRunning(Class<?> serviceClass)        // A general file that checks if a system is running.
    {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);     // Starts the activity manager to check the service called.
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE))        // For each service called by the running service.
        {
            if (serviceClass.getName().equals(service.service.getClassName()))      // It checks if it is running.
            {
                return true;        // Returns true
            }
        }
        return false;       // If not, it returns false.
    }

    /**
     * This method sets up the screen actions that go along with waking up to the activity and how the screen behaves while the activity is ongoing
     */
    private void unlockScreen()
    {
        this.window = this.getWindow();     // Gets access to the screen of the device
        this.window.addFlags(WindowManager.LayoutParams.FLAG_TOUCHABLE_WHEN_WAKING);        // Makes the screen touchable while in the waking process
        this.window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);        // Makes sure the screen is on if off
        this.window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);      // Makes sure the device can wake up if locked
        this.window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);       // Keeps the screen on constantly.
    }

    /**
     * This method is called when the system resumes back to this class
     */
    @Override
    protected void onResume()
    {
        super.onResume();       // Calls the super class method
        this.setUpUIElements();     // Calls the method
        this.registerReceiver(this.minuteUpdateReceiver, this.minuteTimeTick);     // Registers the receiver with the system to make sure it runs
        this.minuteUpdateReceiver = new BroadcastReceiver()     // Makes a broadcast receiver from the system
        {
            /**
             * This method receives information from the system when the time changes
             *
             * @param context is the application context around the system
             * @param intent  is the intent to be used
             */
            @Override
            public void onReceive(Context context, Intent intent)
            {
                startMinuteUpdater();       // Calls the method
            }
        };
    }

    /**
     * This method is called when the system pauses to do some action
     */
    @Override
    protected void onPause()
    {
        super.onPause();        // Calls the super class method
        try     // Tries to perform the action
        {
            unregisterReceiver(this.minuteUpdateReceiver);     // Unregisters the receiver with the system to make sure it stops running
            finish();       // Clears the main activity
        }
        catch (Exception e)     // If an error occurs
        {
            // Do nothing
        }
    }

    /**
     * This method is called when the system needs to end this activity
     */
    @Override
    protected void onStop()     // To stop the activity.
    {
        super.onStop();     // Calls the super class method
        try     // Tries to perform the action
        {
            unregisterReceiver(this.minuteUpdateReceiver);     // Unregisters the receiver with the system to make sure it stops running
            finish();       // Clears the main activity
        }
        catch (Exception e)     // If an error occurs
        {
            // Do nothing
        }
    }
}
