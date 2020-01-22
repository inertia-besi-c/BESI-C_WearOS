package com.linklab.inertia.besic;

/*
 * Imports needed by the system to function appropriately
 */
import android.content.Context;
import android.app.Service;
import android.os.IBinder;
import android.app.ActivityManager;
import android.content.Intent;

/**
 * This class is responsible for the starting and stopping of certain sensors in the application. This class is extended from by the
 * sensors that need the integration.
 */
public class SensorTimer extends Service
{
    private Intent accelerometer, pedometer, heartrate, estimote;     // Initializes the intents of the class
    private SystemInformation systemInformation;        // Initializes the system information class
    private DataLogger dataLogger;      // Sets up the datalogger class
    private String data;        // Initializes the string variables

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        this.systemInformation = new SystemInformation();       // Sets up an instance of the class

        this.startPedometer(true);      // Calls the method
        this.startAccelerometer(true);      // Calls the method

        return START_STICKY;
    }

    /**
     * This method starts the accelerometer sensor
     */
    private void startAccelerometer(boolean runMode)
    {
        this.accelerometer = new Intent(getBaseContext(), Accelerometer.class);     // Sets up the intent to start the service
        if(!isRunning(Accelerometer.class) && runMode)     // Checks if the service is already running, if it is not
        {
            startService(this.accelerometer);       // Automatically starts the service

            this.data = this.systemInformation.getDateTime("yyyy/MM/dd HH:mm:ss:SSS") + (",") + "Sensor Time Service" + (",") + "Calling to Start the Accelerometer Class";       // Data to be logged by the system
            this.dataLogger = new DataLogger(getApplicationContext(), getResources().getString(R.string.subdirectory_logs), getResources().getString(R.string.sensors), this.data);      // Sets a new datalogger variable
            this.dataLogger.saveData("log");      // Saves the data in the mode specified
        }
    }

    /**
     * This method starts the pedometer sensor
     */
    private void startPedometer(boolean runMode)
    {
        this.pedometer = new Intent(getBaseContext(), Pedometer.class);     // Sets up the intent to start the service
        if(!isRunning(Pedometer.class) && runMode)     // Checks if the service is already running, if it is not
        {
            startService(this.pedometer);       // Automatically starts the service

            this.data = this.systemInformation.getDateTime("yyyy/MM/dd HH:mm:ss:SSS") + (",") + "Sensor Time Service" + (",") + "Calling to Start the Pedometer Class";       // Data to be logged by the system
            this.dataLogger = new DataLogger(getApplicationContext(), getResources().getString(R.string.subdirectory_logs), getResources().getString(R.string.sensors), this.data);      // Sets a new datalogger variable
            this.dataLogger.saveData("log");      // Saves the data in the mode specified
        }
    }

    private void startHeartRate(boolean runMode)
    {
        // Not implemented yet
    }

    private void startEstimote(boolean runMode)
    {

    }

    /**
     * Checks if a given service is currently running or not
     * @param serviceClass is the service class to be checked
     * @return a boolean true or false
     */
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

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }
}
