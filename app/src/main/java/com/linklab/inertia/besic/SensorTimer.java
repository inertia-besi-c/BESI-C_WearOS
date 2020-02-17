package com.linklab.inertia.besic;

/*
 * Imports needed by the system to function appropriately
 */
import android.content.SharedPreferences;
import android.content.Context;
import android.app.Service;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.app.ActivityManager;
import android.content.Intent;

import java.util.TimerTask;
import java.util.Objects;
import java.util.Timer;

/**
 * This class is responsible for the starting and stopping of certain sensors in the application. This class is extended from by the
 * sensors that need the integration. All sensors in the application are responsible for stopping themselves if they are on a duty cycle,
 * however, if needed to be ended, the individual class can be stopped.
 */
public class SensorTimer extends Service
{
    private SharedPreferences sharedPreferences;        // Access the shared preferences of the system
    private Intent accelerometer, pedometer, heartrate, estimote;     // Initializes the intents of the class
    private SystemInformation systemInformation;        // Initializes the system information class
    private DataLogger dataLogger, batteryData;      // Sets up the datalogger class
    private Timer heartrateTimer, estimoteTimer;       // Sets the timers for the class
    private String data, batteryFileInformation;        // Initializes the string variables

    /**
     * This is the method that is called to run as soon as this service is called to run
     * @param intent is the intent service of the application
     * @param flags is any flags added to the service item
     * @param startId is the starting identification
     * @return an integer allowing for the continual running of the service or not
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        this.systemInformation = new SystemInformation();       // Sets up an instance of the class
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());       // Gets the default preferences of this application

        this.startPedometer();      // Calls the method
        this.startAccelerometer(true);      // Calls the method
        this.startHeartRate(true);      // Calls the method
        this.startEstimote(true);       // Calls the method

        return START_STICKY;        // Allows the service to be run outside the context of the application
    }

    /**
     * This class is set to run the accelerometer sensor at a specified duty cycle rate
     * @param runMode is the mode that decides if the sensor should run or not
     */
    private void startHeartRate(boolean runMode)
    {
        this.heartrate = new Intent(getBaseContext(), HeartRate.class);     // Gets an intent on the specified class

        if (runMode && !this.systemInformation.isCharging(getApplicationContext()))        // Checks if the sensor is wanted to run
        {
            this.heartrateTimer = new Timer();      // Sets up a timer for the heartrate sensor
            this.heartrateTimer.schedule(new TimerTask()        // Schedules the timer
            {
                /**
                 * The following is called to run
                 */
                @Override
                public void run()
                {
                    if (!isRunning(HeartRate.class))        // If the heartrate class is not running
                    {
                        startService(heartrate);        // Starts the heartrate class;

                        data = systemInformation.getDateTime("yyyy/MM/dd HH:mm:ss:SSS") + (",") + "Sensor Time Service" + (",") + "Calling to Start the HeartRate Class";       // Data to be logged by the system
                        dataLogger = new DataLogger(getApplicationContext(), getResources().getString(R.string.subdirectory_logs), getResources().getString(R.string.sensors), data);      // Sets a new datalogger variable
                        dataLogger.saveData("log");      // Saves the data in the mode specified

                        batteryFileInformation = systemInformation.getDateTime("yyyy/MM/dd HH:mm:ss:SSS") + (",") + systemInformation.getBatteryLevel(getApplicationContext()) + (",") + systemInformation.isCharging(getApplicationContext());     // Data to be logged by the system
                        batteryData = new DataLogger(getApplicationContext(), getResources().getString(R.string.subdirectory_logs), getResources().getString(R.string.battery), batteryFileInformation);      // Sets a new datalogger variable
                        batteryData.saveData("log");        // Saves the data with the specified type
                    }
                    else    // If the if statement fails
                    {
                        data = systemInformation.getDateTime("yyyy/MM/dd HH:mm:ss:SSS") + (",") + "Sensor Time Service" + (",") + "Already running the HeartRate Class";       // Data to be logged by the system
                        dataLogger = new DataLogger(getApplicationContext(), getResources().getString(R.string.subdirectory_logs), getResources().getString(R.string.sensors), data);      // Sets a new datalogger variable
                        dataLogger.saveData("log");      // Saves the data in the mode specified
                    }
                }
            }, 0, Integer.valueOf(Objects.requireNonNull(this.sharedPreferences.getString("heartrate_interval", ""))) * 1000);     // Repeats at the specified interval
        }
        else        // If it is not wanted to run
        {
            stopService(this.heartrate);        // Stops the sensor from running

            data = systemInformation.getDateTime("yyyy/MM/dd HH:mm:ss:SSS") + (",") + "Sensor Time Service" + (",") + "Stopped the HeartRate Class";       // Data to be logged by the system
            dataLogger = new DataLogger(getApplicationContext(), getResources().getString(R.string.subdirectory_logs), getResources().getString(R.string.sensors), data);      // Sets a new datalogger variable
            dataLogger.saveData("log");      // Saves the data in the mode specified
        }
    }

    /**
     * This method is called to run the estimote service at a certain duty cycle
     * @param runMode is set to decide if the system should run or not
     */
    private void startEstimote(boolean runMode)
    {
        this.estimote = new Intent(getBaseContext(), Estimote.class);     // Gets an intent on the specified class

        if (runMode)        // Checks if the sensor is wanted to run
        {
            this.estimoteTimer = new Timer();      // Sets up a timer for the heartrate sensor
            this.estimoteTimer.schedule(new TimerTask()        // Schedules the timer
            {
                /**
                 * The following is called to run
                 */
                @Override
                public void run()
                {
                    if (!isRunning(Estimote.class))        // If the heartrate class is not running
                    {
                        startService(estimote);        // Starts the heartrate class;

                        data = systemInformation.getDateTime("yyyy/MM/dd HH:mm:ss:SSS") + (",") + "Sensor Time Service" + (",") + "Calling to Start the Estimote Class";       // Data to be logged by the system
                        dataLogger = new DataLogger(getApplicationContext(), getResources().getString(R.string.subdirectory_logs), getResources().getString(R.string.sensors), data);      // Sets a new datalogger variable
                        dataLogger.saveData("log");      // Saves the data in the mode specified
                    }
                    else    // If the if statement fails
                    {
                        data = systemInformation.getDateTime("yyyy/MM/dd HH:mm:ss:SSS") + (",") + "Sensor Time Service" + (",") + "Already running the Estimote Class";       // Data to be logged by the system
                        dataLogger = new DataLogger(getApplicationContext(), getResources().getString(R.string.subdirectory_logs), getResources().getString(R.string.sensors), data);      // Sets a new datalogger variable
                        dataLogger.saveData("log");      // Saves the data in the mode specified
                    }
                }
            }, 0, Integer.valueOf(Objects.requireNonNull(this.sharedPreferences.getString("estimote_interval", ""))) * 1000);     // Repeats at the specified interval
        }
        else        // If it is not wanted to run
        {
            stopService(this.estimote);        // Stops the sensor from running

            data = systemInformation.getDateTime("yyyy/MM/dd HH:mm:ss:SSS") + (",") + "Sensor Time Service" + (",") + "Stopped the Estimote Class";       // Data to be logged by the system
            dataLogger = new DataLogger(getApplicationContext(), getResources().getString(R.string.subdirectory_logs), getResources().getString(R.string.sensors), data);      // Sets a new datalogger variable
            dataLogger.saveData("log");      // Saves the data in the mode specified
        }
    }

    /**
     * This method starts the accelerometer sensor
     */
    private void startAccelerometer(boolean runMode)
    {
        this.accelerometer = new Intent(getBaseContext(), Accelerometer.class);     // Sets up the intent to start the service
        if(!isRunning(Accelerometer.class) && runMode && !this.systemInformation.isCharging(getApplicationContext()))     // Checks if the service is already running, if it is not
        {
            startService(this.accelerometer);       // Automatically starts the service

            this.data = this.systemInformation.getDateTime("yyyy/MM/dd HH:mm:ss:SSS") + (",") + "Sensor Time Service" + (",") + "Calling to Start the Accelerometer Class";       // Data to be logged by the system
            this.dataLogger = new DataLogger(getApplicationContext(), getResources().getString(R.string.subdirectory_logs), getResources().getString(R.string.sensors), this.data);      // Sets a new datalogger variable
            this.dataLogger.saveData("log");      // Saves the data in the mode specified
        }
        else        // If it fails the requirements
        {
            stopService(this.accelerometer);        // Stops the service

            this.data = this.systemInformation.getDateTime("yyyy/MM/dd HH:mm:ss:SSS") + (",") + "Sensor Time Service" + (",") + "Stopped the Accelerometer Class";       // Data to be logged by the system
            this.dataLogger = new DataLogger(getApplicationContext(), getResources().getString(R.string.subdirectory_logs), getResources().getString(R.string.sensors), this.data);      // Sets a new datalogger variable
            this.dataLogger.saveData("log");      // Saves the data in the mode specified
        }
    }

    /**
     * This method starts the pedometer sensor
     */
    private void startPedometer()
    {
        this.pedometer = new Intent(getBaseContext(), Pedometer.class);     // Sets up the intent to start the service
        if(!isRunning(Pedometer.class))     // Checks if the service is already running, if it is not
        {
            startService(this.pedometer);       // Automatically starts the service

            this.data = this.systemInformation.getDateTime("yyyy/MM/dd HH:mm:ss:SSS") + (",") + "Sensor Time Service" + (",") + "Calling to Start the Pedometer Class";       // Data to be logged by the system
            this.dataLogger = new DataLogger(getApplicationContext(), getResources().getString(R.string.subdirectory_logs), getResources().getString(R.string.sensors), this.data);      // Sets a new datalogger variable
            this.dataLogger.saveData("log");      // Saves the data in the mode specified
        }
    }


    /**
     * This method is called when the service needs to be killed for some reason
     */
    private void killProcess()
    {
        this.heartrateTimer.cancel();       // Cancels the timer

        this.startAccelerometer(false);     // Stops the sensor from running
        this.startHeartRate(false);     // Stops the sensor from running
    }

    /**
     * This method is called if the class is to be killed for some reason
     */
    @Override
    public void onDestroy()
    {
        this.killProcess();     // Calls the method listed
        super.onDestroy();      // Calls the super class method
    }

    /**
     * Checks if a given service is currently running or not
     * @param serviceClass is the service class to be checked
     * @return a boolean true or false
     */
    @SuppressWarnings("ALL")        // Ignores the warning from this method
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
     * Unknown method implementation needed to run
     * @param intent is the intent of the service
     * @return unknown.
     */
    @Override
    public IBinder onBind(Intent intent)
    {
        throw new UnsupportedOperationException("Not yet implemented");     // Message about an error
    }
}
