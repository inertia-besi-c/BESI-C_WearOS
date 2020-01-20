package com.linklab.inertia.besic;

import android.content.SharedPreferences;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;

import java.text.DecimalFormat;
import java.util.Objects;

public class Accelerometer extends Service implements SensorEventListener
{
    private SensorManager sensorManager;
    private SharedPreferences sharedPreferences;
    private Sensor accelerometer;
    private DataLogger dataLogger;
    private SystemInformation systemInformation;
    private DecimalFormat decimalFormat;
    private StringBuilder stringBuilder1, stringBuilder2;
    private int currentCount;
    private double[] accelerometer_data;

    @Override
    public int onStartCommand(Intent intent, int flags, int startID)
    {
        this.sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        this.accelerometer = this.sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        this.sensorManager.registerListener(this, this.accelerometer, SensorManager.SENSOR_DELAY_UI);
        this.stringBuilder1 = new StringBuilder();
        this.stringBuilder2 = new StringBuilder();
        this.decimalFormat = new DecimalFormat("#.####");
        this.currentCount = 0;

        return START_STICKY;
    }

    @Override
    public void onSensorChanged(SensorEvent event)
    {
        this.currentCount++;
        this.accelerometer_data = new double[3];

        accelerometer_data[0] = event.values[0];     // Accelerometer value with gravity on the x-axis
        accelerometer_data[1] = event.values[1];     // Accelerometer value with gravity on the y-axis
        accelerometer_data[2] = event.values[2];     // Accelerometer value with gravity on the z-axis

        String linearx = decimalFormat.format(accelerometer_data[0]);     // Limits the length of the x-axis value to 4 digits
        String lineary = decimalFormat.format(accelerometer_data[1]);     // Limits the length of the y-axis value to 4 digits
        String linearz = decimalFormat.format(accelerometer_data[2]);     // Limits the length of the z-axis value to 4 digits

        final String accelerometerValues =      // Shows the values in a string.
                this.systemInformation.getDateTime("yyyy/MM/dd HH:mm:ss:SSS") + "," +          // Starts a new string line.
                        linearx + "," +         // Acceleration value on x-axis
                        lineary + "," +         // Acceleration value on y-axis
                        linearz;        // Acceleration value on z-axis

        stringBuilder1.append(accelerometerValues);      // Appends the values to the string builder
        stringBuilder1.append("\n");     // Makes a new line

        if ((this.stringBuilder1 != null) && (this.currentCount >= Integer.valueOf(Objects.requireNonNull(this.sharedPreferences.getString("accelerometer_data_limit", "")))))
        {
            this.stringBuilder2.append(this.stringBuilder1);
            this.stringBuilder1.setLength(0);
            this.currentCount = 0;

            new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    dataLogger = new DataLogger(getApplicationContext(), getResources().getString(R.string.subdirectory_accelerometer), getResources().getString(R.string.accelerometer), stringBuilder2.toString());
                    dataLogger.saveData("log");
                }
            }).start();
        }
    }

    @Override
    public void onDestroy()
    {
        this.sensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {
        // Please do not remove this, the code needs this to function properly. Thank you :-)
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
