package com.linklab.inertia.besic;

import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;

/**
 * This class is used to detect user steps. This service should not be killed at any time during the lifecycle of
 * this application. If it is killed, it should automatically restart itself.
 */
public class Pedometer extends Service implements SensorEventListener
{
    private SensorManager sensorManager;        // Initializes a listener
    private Sensor pedometer;       // Sets a accelerometer sensor
    private String data;       // Sets up the string of the class
    private DataLogger dataLogger;      // Sets the datalogger variable
    private SystemInformation systemInformation;        // Sets a system preference variable

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

        this.sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);      // Sets up the sensor service
        this.pedometer = this.sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);        // Sets up the type of sensor
        this.sensorManager.registerListener(this, this.pedometer, SensorManager.SENSOR_DELAY_UI);       // Sets up the listener rate

        this.data = this.systemInformation.getDateTime("yyyy/MM/dd HH:mm:ss:SSS") + (",") + "Pedometer Service" + (",") + "Starting Pedometer Service";       // Data to be logged by the system
        this.dataLogger = new DataLogger(getApplicationContext(), getResources().getString(R.string.subdirectory_logs), getResources().getString(R.string.system), this.data);      // Sets a new datalogger variable
        this.dataLogger.saveData("log");      // Saves the data in the mode specified

        return START_STICKY;        // Returns an integer for the service schedule
    }

    /**
     * This method is called every time the sensor detects a change or gets an update information
     * @param event is the event that happened to the sensor
     */
    @Override
    public void onSensorChanged(SensorEvent event)
    {
        this.data = this.systemInformation.getDateTime("yyyy/MM/dd HH:mm:ss:SSS") + (",") + event.values[0] + (",") + event.accuracy;       // Data to be logged

        new Thread(new Runnable()       // Sets a runnable thread
            {
                /**
                 * This method is called to run once the conditions are met
                 */
                @Override
                public void run()
                {
                    dataLogger = new DataLogger(getApplicationContext(), getResources().getString(R.string.sensors), getResources().getString(R.string.pedometer), data);       // Sets up the items to be logged
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

        this.data = this.systemInformation.getDateTime("yyyy/MM/dd HH:mm:ss:SSS") + (",") + "Pedometer Service" + (",") + "Killing Pedometer Service";       // Data to be logged by the system
        this.dataLogger = new DataLogger(getApplicationContext(), getResources().getString(R.string.subdirectory_logs), getResources().getString(R.string.system), this.data);      // Sets a new datalogger variable
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
