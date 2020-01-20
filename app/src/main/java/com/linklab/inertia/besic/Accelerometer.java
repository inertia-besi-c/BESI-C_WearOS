package com.linklab.inertia.besic;

/*
 * Imports needed by the system to function appropriately
 */
import android.content.SharedPreferences;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.app.Service;
import android.preference.PreferenceManager;
import android.hardware.SensorEvent;
import android.hardware.Sensor;
import android.content.Intent;

import java.text.DecimalFormat;
import java.util.Objects;

/**
 * This class is responsible for the gathering and logging of the accelerometer data obtained from the wearable device.
 * This class is designed to run for the duration of the application's cycle with no sort of modulation.
 */
public class Accelerometer extends Service implements SensorEventListener
{
    private SensorManager sensorManager;        // Initializes a listener
    private SharedPreferences sharedPreferences;        // Initializes a shared preference variable
    private Sensor accelerometer;       // Sets a accelerometer sensor
    private String linearx, lineary, linearz, data;       // Sets up the string of the class
    private DataLogger dataLogger;      // Sets the datalogger variable
    private SystemInformation systemInformation;        // Sets a system preference variable
    private DecimalFormat decimalFormat;        // Initializes a decimal formatter
    private StringBuilder stringBuilder1, stringBuilder2;       // Sets up the string builder variables
    private int currentCount;       // Sets up the integers of the class
    private double[] accelerometer_data;        // Sets up a list of doubles

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
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());        // Sets up the shared preference
        this.decimalFormat = new DecimalFormat("#.####");       // Sets up the decimal formatter
        this.systemInformation = new SystemInformation();       // Sets up the system information
        this.stringBuilder1 = new StringBuilder();      // Sets up a string builder
        this.stringBuilder2 = new StringBuilder();      // Sets up a string builder

        this.currentCount = 0;      // Sets up a variable

        this.sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);      // Sets up the sensor service
        this.accelerometer = this.sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);        // Sets up the type of sensor
        this.sensorManager.registerListener(this, this.accelerometer, SensorManager.SENSOR_DELAY_UI);       // Sets up the listener rate

        this.data = this.systemInformation.getDateTime("yyyy/MM/dd HH:mm:ss:SSS") + (",") + "Accelerometer Service" + (",") + "Starting Accelerometer Service";       // Data to be logged by the system
        this.dataLogger = new DataLogger(getApplicationContext(), getResources().getString(R.string.subdirectory_logs), getResources().getString(R.string.sensors), this.data);      // Sets a new datalogger variable
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
        this.currentCount++;        // Increments the current count variable
        this.accelerometer_data = new double[3];        // Resets the double variable to be new

        this.accelerometer_data[0] = event.values[0];     // Accelerometer value with gravity on the x-axis
        this.accelerometer_data[1] = event.values[1];     // Accelerometer value with gravity on the y-axis
        this.accelerometer_data[2] = event.values[2];     // Accelerometer value with gravity on the z-axis

        this.linearx = this.decimalFormat.format(this.accelerometer_data[0]);     // Limits the length of the x-axis value to 4 digits
        this.lineary = this.decimalFormat.format(this.accelerometer_data[1]);     // Limits the length of the y-axis value to 4 digits
        this.linearz = this.decimalFormat.format(this.accelerometer_data[2]);     // Limits the length of the z-axis value to 4 digits

        final String accelerometerValues =      // Shows the values in a string.
                this.systemInformation.getDateTime("yyyy/MM/dd HH:mm:ss:SSS") + "," +          // Starts a new string line.
                        this.linearx + "," +         // Acceleration value on x-axis
                        this.lineary + "," +         // Acceleration value on y-axis
                        this.linearz + "," +         // Acceleration value on z-axis
                        this.systemInformation.getBatteryLevel(getApplicationContext());        // Adds the battery level information to the file

        stringBuilder1.append(accelerometerValues);      // Appends the values to the string builder
        stringBuilder1.append("\n");     // Makes a new line

        if ((this.stringBuilder1 != null) && (this.currentCount >= Integer.valueOf(Objects.requireNonNull(this.sharedPreferences.getString("accelerometer_data_limit", "")))))      // Checks if the conditions are met
        {
            this.stringBuilder2.append(this.stringBuilder1);        // Appends all the values of the string builder to another
            this.stringBuilder1.setLength(0);       // Resets the string builder
            this.currentCount = 0;      // Resets the variable

            new Thread(new Runnable()       // Sets a runnable thread
            {
                /**
                 * This method is called to run once the conditions are met
                 */
                @Override
                public void run()
                {
                    dataLogger = new DataLogger(getApplicationContext(), getResources().getString(R.string.subdirectory_sensors), getResources().getString(R.string.accelerometer), stringBuilder2.toString());       // Sets up the items to be logged
                    dataLogger.saveData("log");     // Saves the data in the format specified
                }
            }).start();     // Starts the runnable
        }
    }

    /**
     * This method is called if the class is to be killed for some reason
     */
    @Override
    public void onDestroy()
    {
        this.data = this.systemInformation.getDateTime("yyyy/MM/dd HH:mm:ss:SSS") + (",") + "Accelerometer Service" + (",") + "Killing Accelerometer Service";       // Data to be logged by the system
        this.dataLogger = new DataLogger(getApplicationContext(), getResources().getString(R.string.subdirectory_logs), getResources().getString(R.string.sensors), this.data);      // Sets a new datalogger variable
        this.dataLogger.saveData("log");      // Saves the data in the mode specified

        this.sensorManager.unregisterListener(this);        // Unregisters the sensor change listener

        super.onDestroy();          // Calls the higher on destroy function
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
