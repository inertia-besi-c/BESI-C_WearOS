package com.linklab.inertia.besic;

/*
 * Imports needed by the system to function appropriately
 */
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.content.Intent;

import com.estimote.coresdk.observation.region.beacon.BeaconRegion;
import com.estimote.coresdk.service.BeaconManager;

import java.util.TimerTask;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.UUID;

/**
 * This class is responsible for the data getting from the bluetooth sensor and beacons set up. This class is started by the
 * Sensor Timer superclass and kills itself after the specified time period.
 */
public class Estimote extends SensorTimer
{
    private SharedPreferences sharedPreferences;        // Initializes the preference item
    private String data;       // Sets up the string of the class
    private Timer ESTimer;        // Initializes the ESTimer variable
    private DataLogger dataLogger;      // Sets the datalogger variable
    private SystemInformation systemInformation;        // Sets a system preference variable
    private BeaconManager beaconManager;        // This is the beacon manager
    private BeaconRegion beaconRegion;      // The region to be observed

    /**
     * This method is called as soon as the sensor is called to start
     * @param intent is the intent of the service
     * @param flags the flag of the service
     * @param startID the start identification
     * @return an integer that schedules if the service can be killed
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startID)
    {
        this.systemInformation = new SystemInformation();       // Sets up the system information
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());        // Gets the preference from the default preferences

        this.data = this.systemInformation.getDateTime("yyyy/MM/dd HH:mm:ss:SSS") + (",") + "Estimote Service" + (",") + "Starting Estimote Service";       // Data to be logged by the system
        this.dataLogger = new DataLogger(getApplicationContext(), getResources().getString(R.string.subdirectory_logs), getResources().getString(R.string.sensors), this.data);      // Sets a new datalogger variable
        this.dataLogger.saveData("log");      // Saves the data in the mode specified

        this.ESTimer = new Timer();       // Sets up the ESTimer
        this.ESTimer.schedule(new TimerTask()         // Schedules the ESTimer at a fixed rate
        {
            /**
             * The following is called to run
             */
            @Override
            public void run()
            {
                stopSelf();     // Kills the service
            }
        }, Integer.valueOf(Objects.requireNonNull(this.sharedPreferences.getString("estimote_duration", ""))) * 1000);      // Repeats at the specified interval

        startRanging();     // Calls the method to start running

        return START_STICKY;        // Makes sure the method can run outside of the application context
    }

    /**
     * This method starts looking for the beacons system set up and logs the data to the specified file
     */
    public void startRanging()
    {
        this.beaconManager = new BeaconManager(getApplicationContext());        // Sets up a new beacon with the application context
        this.beaconRegion = new BeaconRegion("ranged region", UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"), null, null);     // Implements the region needed to be looking for

        this.beaconManager.connect(new BeaconManager.ServiceReadyCallback()     // Connects to the region
        {
            @Override
            public void onServiceReady()        // Checks if the service is ready
            {
                beaconManager.startRanging(beaconRegion);       // Calls the system to start looking for the region
            }
        });

        this.beaconManager.setRangingListener(new BeaconManager.BeaconRangingListener()     // Sets the listener for the range
        {
            @Override
            public void onBeaconsDiscovered(BeaconRegion beaconRegion, List<com.estimote.coresdk.recognition.packets.Beacon> beacons)       // This is called if a beacon is discovered in the region
            {
                if (!beacons.isEmpty())     // Makes sure that the list being checked is not empty
                {
                    for (com.estimote.coresdk.recognition.packets.Beacon beacon : beacons)      // For every beacon in the list
                    {
                        data = systemInformation.getDateTime("yyyy/MM/dd HH:mm:ss:SSS") + (",") + beacon.getMacAddress() + (",") + beacon.getMajor() + (",") + beacon.getMinor() + (",") + beacon.getRssi() + (",") + systemInformation.getBatteryLevel(getApplicationContext()) + (",") + systemInformation.isCharging(getApplicationContext());       // Data to be logged by the system
                        dataLogger = new DataLogger(getApplicationContext(), getResources().getString(R.string.subdirectory_sensors), getResources().getString(R.string.estimote), data);      // Sets a new datalogger variable
                        dataLogger.saveData("log");      // Saves the data in the mode specified
                    }
                }
            }
        });
    }

    /**
     * This method is called if the class is to be killed for some reason
     */
    public void killProcess()
    {
        this.data = this.systemInformation.getDateTime("yyyy/MM/dd HH:mm:ss:SSS") + (",") + "Estimote Service" + (",") + "Stopping Estimote Sensor";       // Data to be logged by the system
        this.dataLogger = new DataLogger(getApplicationContext(), getResources().getString(R.string.subdirectory_logs), getResources().getString(R.string.sensors), this.data);      // Sets a new datalogger variable
        this.dataLogger.saveData("log");      // Saves the data in the mode specified


        try     // Tries to perform the following
        {
            this.beaconManager.stopRanging(this.beaconRegion);       // Calls the beacon range to stop
            this.ESTimer.cancel();        // Cancels the ESTimer made by the class
            this.stopForeground(true);      // Stops the foreground notification
        }
        catch (Exception e)     // If it fails
        {
            // Do nothing
        }
    }

    /**
     * The destructor for the method
     */
    @Override
    public void onDestroy()
    {
        this.killProcess();     // Calls the method listed
    }
}
