package com.linklab.inertia.besic;

/*
 * Imports needed by the system to function appropriately
 */
import android.preference.PreferenceManager;
import android.os.Environment;
import android.widget.Toast;
import android.content.SharedPreferences;
import android.content.Context;

import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileReader;
import java.io.File;

/**
 * This class is made to enable the logging and reading of data from the saved location on the external storage
 * of the device. There is a log mode, write mode, and read mode implementation for the file readers.
 */
public class DataLogger
{
    private String FileName, Content, Subdirectory, DeviceID, mainDirectoryPath, Directory, externalStorageState, externalStorageDirectory, lineItems;        // Variable names for the file characters and contents.
    private FileOutputStream outputFIle;        // File to be put out into the device
    private OutputStreamWriter outputWriter;        // Link to the writer of the device
    private StringBuilder fileContent;      // A string builder variable storing data
    private BufferedReader bufferedReader;      // A buffer reader to read from the file
    private File mainDirectory, subDirectory, dataFile;     // Sets up the file of the system


    private SharedPreferences sharedPreferences;        // Gets the shared preference from the system.
    private Context context;        // Holds a context of the application

    /**
     * Overloaded constructor taking arguments to set up data information
     * @param subdirectory is the subdirectory where the data should be logged
     * @param fileName is the name of the file that is to be logged
     * @param content is the content that the file should contain
     */
    DataLogger(Context context, String subdirectory, String fileName, String content)
    {
        this.context = context;     // Sets the context of the application
        this.setUp();       // Calls the method to set up the required values
        this.Subdirectory = subdirectory;     // Assigns the subdirectory
        this.FileName = DeviceID + "_" + fileName;      // Assigns the filename
        this.Content = content+"\n";     // Assigns the content to be logged to the file
    }

    /**
     * Method opens a link to a main directory folder specified in the sdcard of the device and saves the
     * the contents of a data stream into the given filename. If any of the file or folders do not exist,
     * one is created and logged into.
     * @param saveType is the argument passed into the method to determine what type of method to save the
     *                 data. An argument with "log" would append the data to the file without erasing the
     *                 contents. An argument with "write" would erase the content of the file and add the
     *                 new contents to the file.
     */
    void saveData(String saveType)
    {
        if (isExternalStorageWritable())        // Checks if the external storage is writable
        {
            try     // Tries to perform the following actions
            {
                this.mainDirectory = new File(this.mainDirectoryPath);      // Makes the main directory as a new file.
                this.subDirectory = new File(this.mainDirectoryPath + "/" + this.Subdirectory);     // Makes the subdirectory path into a new file

                if (!this.mainDirectory.isDirectory() || !this.subDirectory.isDirectory())      // Checks if both the main directory and subdirectory has been made
                {
                    this.mainDirectory.mkdirs();        // If the main directory have not been made, it makes it
                    this.subDirectory.mkdirs();         // If the sub directory has not been made, it makes it
                }

                this.dataFile = new File(this.mainDirectoryPath+"/"+this.Subdirectory+"/"+this.FileName);       // Makes the file desired for into a file path
                this.dataFile.createNewFile();      // Creates a new file at the specified path name
                this.outputFIle = new FileOutputStream(this.dataFile,true);        // Creates a new file to be read out into the device
                this.outputWriter = new OutputStreamWriter(this.outputFIle);        // Assigns the output file to be written by the output writer

                if (saveType.toLowerCase().contentEquals("log"))       // Checks if the data is supposed to be in log mode
                    this.outputWriter.append(this.Content);        // Appends the content to be saved into the file without erasing it

                else if (saveType.toLowerCase().contentEquals("write"))     // Checks if the data is supposed to be in write mode
                    this.outputWriter.write(this.Content);      // Writes the content to be saved into the file after erasing it.

                else
                    Toast.makeText(context, "Error: Invalid Save Argument", Toast.LENGTH_LONG).show();     // Shows a toast

                this.outputWriter.close();      // Closes the output writer
                this.outputFIle.close();        // Closes the file that is output into the device
            }
            catch (IOException e)       // If a file error happens
            {
                Toast.makeText(context, "Error Making File", Toast.LENGTH_LONG).show();     // Shows a toast
            }
            catch (Exception e)     // If any other error happens
            {
                Toast.makeText(context, "Unknown Error", Toast.LENGTH_LONG).show();     // Shows a toast
            }
        }
        else        // Runs if the external storage is not writable
        {
            Toast.makeText(context, "Cannot Write to sdcard", Toast.LENGTH_LONG).show();     // Shows a toast
        }
    }

    /**
     * This method reads the content from a file specified with a path.
     * @return the string format of what the file contains.
     */
    String readData()
    {
        if (isExternalStorageReadable())        // Checks if the external storage can be read from
        {
            this.fileContent = new StringBuilder();     // Creates a new string builder variable to store all the data
            try
            {
                this.dataFile = new File(this.mainDirectoryPath+"/"+this.Subdirectory+"/"+this.FileName);       // Specifies a path to the file
                this.bufferedReader = new BufferedReader(new FileReader(this.dataFile));        // Opens a reader for the file

                while((this.lineItems = this.bufferedReader.readLine()) != null)        // Checks if there is still a line to read from and it's not null
                {
                    this.fileContent.append(this.lineItems);        // Appends the value to the string builder
                    this.fileContent.append("\n");      // Appends a next line character.
                }

                this.bufferedReader.close();       // Closes the reader
            }
            catch (IOException e)       // If a file error happens
            {
                Toast.makeText(context, "Cannot Find File", Toast.LENGTH_LONG).show();     // Shows a toast
            }
            catch (Exception e)     // If any other error happens
            {
                Toast.makeText(context, "Unknown Error", Toast.LENGTH_LONG).show();     // Shows a toast
            }

            return this.fileContent.toString();     // Returns the file contents a string
        }
        else        // Runs if the external storage is not readable
        {
            Toast.makeText(context, "Cannot Read from sdcard", Toast.LENGTH_LONG).show();     // Shows a toast
        }

        return "Cannot Read from sdcard";       // Returns a false cannot read from sdcard error message
    }

    /**
     * Setup values for when the class is called
     */
    public void setUp()
    {
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);        // Gets a reference the preference object
        this.externalStorageState = Environment.getExternalStorageState();       // Gets the state of the external storage of the device
        this.externalStorageDirectory = Environment.getExternalStorageDirectory().toString();      // Gets the main directory of the device
        this.Directory = this.sharedPreferences.getString("directory_key", "");     // Gets the main directory of the device
        this.DeviceID = this.sharedPreferences.getString("device_info", "");     // Sets up the device identification information
        this.mainDirectoryPath = this.externalStorageDirectory + "/" + this.Directory;     // Sets up the path to the main directory information
    }

    /**
     * Checks is the external storage of the device can be written to
     * @return true is it is writable
     */
    private boolean isExternalStorageWritable()
    {
        return Environment.MEDIA_MOUNTED.equals(this.externalStorageState);       // Returns if the space is available to be written to
    }

    /**
     * Checks if the external storage can be read from
     * @return true if it is readable
     */
    private boolean isExternalStorageReadable()
    {
        return Environment.MEDIA_MOUNTED.equals(this.externalStorageState) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(this.externalStorageState);        // Checks if there is readable state
    }
}
