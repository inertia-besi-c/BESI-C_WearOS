package com.linklab.inertia.besic;

import android.annotation.*;
import android.content.*;
import android.preference.*;
import android.widget.*;

import java.io.*;

public class DataLogger extends PreferenceActivity
{
    private String FileName, Content, Subdirectory, DeviceID, mainDirectoryPath, Directory, externalStorageState;        // Variable names for the file characters and contents.
    private SharedPreferences sharedPreferences;        // Gets the shared preference from the system.
    FileOutputStream outputFIle;        // File to be put out into the device
    File mainDirectory, dataFile;     // Sets up the file of the system
    OutputStreamWriter outputWriter;        // Link to the writer of the device

    /**
     * Constructor for when the class is called
     */
    DataLogger()
    {
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());        // Gets a reference the preference object
        this.externalStorageState = android.os.Environment.getExternalStorageState();       // Gets the state of the external storage of the device
        this.Directory = this.sharedPreferences.getString("directory_key", "");     // Gets the main directory of the device
        this.DeviceID = this.sharedPreferences.getString("device_info", "");     // Sets up the device identification information
        this.mainDirectoryPath = this.externalStorageState + "/" + this.Directory;     // Sets up the path to the main directory information
    }

    /**
     * Overloaded constructor taking arguments to set up data information
     * @param subdirectory is the subdirectory where the data should be logged
     * @param fileName is the name of the file that is to be logged
     * @param content is the content that the file should contain
     */
    public DataLogger(String subdirectory, String fileName, String content)
    {
        this.Subdirectory = subdirectory;     // Assigns the subdirectory
        this.FileName = DeviceID + "_" + fileName;      // Assigns the filename
        this.Content = content+"\n";     // Assigns the content to be logged to the file
    }

    /**
     * Method opens a link to a main directory folder specified in the sdcard of the device and logs the
     * the contents of a data stream into the given filename. If any of the file or folders do not exist,
     * one is created and logged into.
     */
    public void logData()
    {
        if (isExternalStorageWritable())        // Checks if the external storage is writable
        {
            try     // Tries to perform the following actions
            {
                this.mainDirectory = new File(this.mainDirectoryPath);      // Makes the main directory as a new file.

                if (!this.mainDirectory.isDirectory())
                {
                    this.mainDirectory.mkdirs();
                }

                this.dataFile = new File(this.mainDirectoryPath+"/"+this.Subdirectory+"/"+this.FileName);
                this.dataFile.createNewFile();      // Creates a new file at the specified path name
                this.outputFIle = new FileOutputStream(this.dataFile, true);        // Creates a new file to be read out into the device
                this.outputWriter = new OutputStreamWriter(this.outputFIle);        // Assigns the output file to be written by the output writer
                this.outputWriter.append(this.Content);        // Appends the content to be logged into the file
                this.outputWriter.close();      // Closes the output writer
                this.outputFIle.close();        // Closes the file that is output into the device
            }
            catch (IOException e)
            {
                Toast.makeText(getApplicationContext(), "Error Making File", Toast.LENGTH_LONG).show();     // Shows a toast
            }
            catch (Exception e)
            {
                Toast.makeText(getApplicationContext(), "Unknown Error", Toast.LENGTH_LONG).show();     // Shows a toast
            }
        }
        else
        {
            Toast.makeText(getApplicationContext(), "Please Check External Storage", Toast.LENGTH_LONG).show();     // Shows a toast
        }
    }

    /**
     * Checks is the external storage of the device can be written to
     * @return true is it is writable
     */
    private boolean isExternalStorageWritable()
    {
        return android.os.Environment.MEDIA_MOUNTED.equals(externalStorageState);       // Returns if the space is available to be written to
    }

    /**
     * Checks if the external storage can be read from
     * @return true if it is readable
     */
    private boolean isExternalStorageReadable()
    {
        return android.os.Environment.MEDIA_MOUNTED.equals(externalStorageState) || android.os.Environment.MEDIA_MOUNTED_READ_ONLY.equals(externalStorageState);        // Checks if there is readable state
    }
}
