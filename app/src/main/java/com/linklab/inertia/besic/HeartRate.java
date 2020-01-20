package com.linklab.inertia.besic;

/*
 * Imports needed by the system to function appropriately
 */
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.SharedPreferences;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.os.SystemClock;
import android.preference.PreferenceManager;

import java.util.Objects;
import java.util.Timer;

public class HeartRate extends Service implements SensorEventListener
{
    private SharedPreferences sharedPreferences;        // Initializes the preference item
    private SensorManager sensorManager;        // Initializes a listener
    private Intent HRIntent;
    private PendingIntent HRPendingIntent;
    private AlarmManager alarmManager;
    private Sensor heartrate;       // Sets a accelerometer sensor
    private String data;       // Sets up the string of the class
    private Timer timer;        // Initializes the timer variable
    private DataLogger dataLogger;      // Sets the datalogger variable
    private SystemInformation systemInformation;        // Sets a system preference variable
    private int duration;

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
        this.timer = new Timer();       // Sets up the timer
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());        // Gets the preference from the default preferences
        this.duration = Integer.valueOf(Objects.requireNonNull(this.sharedPreferences.getString("heartrate_duration", "")));     // Gets the duration from the preference file

        this.sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);      // Sets up the sensor service
        this.heartrate = this.sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);        // Sets up the type of sensor
        this.sensorManager.registerListener(this, this.heartrate, SensorManager.SENSOR_DELAY_FASTEST);       // Sets up the listener rate

        this.data = this.systemInformation.getDateTime("yyyy/MM/dd HH:mm:ss:SSS") + (",") + "Heart Rate Service" + (",") + "Starting Heart Rate Service";       // Data to be logged by the system
        this.dataLogger = new DataLogger(getApplicationContext(), getResources().getString(R.string.subdirectory_logs), getResources().getString(R.string.sensors), this.data);      // Sets a new datalogger variable
        this.dataLogger.saveData("log");      // Saves the data in the mode specified

        return START_NOT_STICKY;        // Returns an integer for the service schedule
    }

    /**
     * This method is called every time the sensor detects a change or gets an update information
     * @param event is the event that happened to the sensor
     */
    @Override
    public void onSensorChanged(SensorEvent event)
    {
        this.data = this.systemInformation.getDateTime("yyyy/MM/dd HH:mm:ss:SSS") + (",") + event.values[0] + (",") + event.accuracy + (",") + this.systemInformation.getBatteryLevel(getApplicationContext());       // Data to be logged

        new Thread(new Runnable()       // Sets a runnable thread
        {
            /**
             * This method is called to run once the conditions are met
             */
            @Override
            public void run()
            {
                dataLogger = new DataLogger(getApplicationContext(), getResources().getString(R.string.subdirectory_sensors), getResources().getString(R.string.heartrate), data);       // Sets up the items to be logged
                dataLogger.saveData("log");     // Saves the data in the format specified
            }
        }).start();     // Starts the runnable

    }

    /**
     * This method is called if the class is to be killed for some reason
     */
    @Override
    public void onDestroy()
    {
        this.sensorManager.unregisterListener(this);        // Unregisters the sensor change listener

        this.data = this.systemInformation.getDateTime("yyyy/MM/dd HH:mm:ss:SSS") + (",") + "Heart Rate Service" + (",") + "Killing Heart Rate Service";       // Data to be logged by the system
        this.dataLogger = new DataLogger(getApplicationContext(), getResources().getString(R.string.subdirectory_logs), getResources().getString(R.string.sensors), this.data);      // Sets a new datalogger variable
        this.dataLogger.saveData("log");      // Saves the data in the mode specified

        this.HRIntent = new Intent(this, AlarmReceiver.class);       // Initializes an intent to be run by the system
        this.HRIntent.putExtra(getResources().getString(R.string.survey_alarm_key), getResources().getString(R.string.heartrate_identifier));     // Puts some extra information into the intent service
        this.HRPendingIntent = PendingIntent.getBroadcast(this, 0, this.HRIntent, 0);     // Initializes a pending intent to be run by the alarm manager

        this.alarmManager.set(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime() + Integer.valueOf(Objects.requireNonNull(this.sharedPreferences.getString("heartrate_interval", ""))), this.HRPendingIntent);        // Sets the alarm to run in some specified future time

        this.data = this.systemInformation.getDateTime("yyyy/MM/dd HH:mm:ss:SSS") + (",") + "Heart Rate Service" + (",") + "Scheduling Alarm for Heart Rate Sensor Restart";       // Data to be logged by the system
        this.dataLogger = new DataLogger(getApplicationContext(), getResources().getString(R.string.subdirectory_logs), getResources().getString(R.string.sensors), this.data);      // Sets a new datalogger variable
        this.dataLogger.saveData("log");      // Saves the data in the mode specified
    }

    /**
     * This method is used to set the accuracy of the sensor
     * @param sensor is the sensor to be used
     * @param accuracy is the accuracy wanted
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {
        // Please do not remove this, the code needs this to function properly. Thank you :-)
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
