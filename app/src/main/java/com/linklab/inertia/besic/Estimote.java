package com.linklab.inertia.besic;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.estimote.coresdk.observation.region.RegionUtils;
import com.estimote.coresdk.observation.region.beacon.BeaconRegion;
import com.estimote.coresdk.service.BeaconManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class Estimote extends SensorTimer
{
    private SharedPreferences sharedPreferences;        // Initializes the preference item
    private String data;       // Sets up the string of the class
    private Timer timer;        // Initializes the timer variable
    private DataLogger dataLogger;      // Sets the datalogger variable
    private SystemInformation systemInformation;        // Sets a system preference variable
    private BeaconManager beaconManager;        // This is the beacon manager
    private BeaconRegion beaconRegion;      // The region to be observed
    private ArrayList<Beacon> beaconArrayList;      // An array list of all the beacons

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

        this.timer = new Timer();       // Sets up the timer
        this.timer.schedule(new TimerTask()         // Schedules the timer at a fixed rate
        {
            /**
             * The following is called to run
             */
            @Override
            public void run()
            {
                data = systemInformation.getDateTime("yyyy/MM/dd HH:mm:ss:SSS") + (",") + "Estimote Service" + (",") + "Stopping Estimote Sensor";       // Data to be logged by the system
                dataLogger = new DataLogger(getApplicationContext(), getResources().getString(R.string.subdirectory_logs), getResources().getString(R.string.sensors), data);      // Sets a new datalogger variable
                dataLogger.saveData("log");      // Saves the data in the mode specified

                stopSelf();     // Kills the service
            }
        }, Integer.valueOf(Objects.requireNonNull(this.sharedPreferences.getString("estimote_duration", ""))) * 1000);      // Repeats at the specified interval

        startRanging();

        return START_STICKY;
    }

    public void startRanging()
    {
        this.beaconArrayList = new ArrayList<>();
        this.beaconManager = new BeaconManager(this);
        this.beaconRegion = new BeaconRegion("ranged region", UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"), null, null);

        this.beaconManager.connect(new BeaconManager.ServiceReadyCallback()
        {
            @Override
            public void onServiceReady()
            {
                beaconManager.startRanging(beaconRegion);
            }
        });

        this.beaconManager.setRangingListener(new BeaconManager.BeaconRangingListener() {
            @Override
            public void onBeaconsDiscovered(BeaconRegion beaconRegion, List<com.estimote.coresdk.recognition.packets.Beacon> beacons) {
                if(!beaconArrayList.isEmpty())
                {
                    int beaconsFound = 0;
                    for (Beacon beacon : beaconArrayList)
                    {
                        beaconArrayList.add(new Beacon(beaconsFound++, beaconArrayList.size(), beacon.getRssi(), RegionUtils.computeAccuracy(beacon)));
                        data = systemInformation.getDateTime("yyyy/MM/dd HH:mm:ss:SSS") + (",") + "Estimote Service" + (",");       // Data to be logged by the system
                        dataLogger = new DataLogger(getApplicationContext(), getResources().getString(R.string.subdirectory_logs), getResources().getString(R.string.sensors), data);      // Sets a new datalogger variable
                        dataLogger.saveData("log");      // Saves the data in the mode specified
                    }
                }
            }
        });
    }
}
