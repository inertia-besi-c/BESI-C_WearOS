package com.linklab.inertia.besic;

/*
 * Imports needed by the system to function appropriately
 */
import android.content.*;
import android.preference.*;
import android.widget.*;
import java.io.*;

/**
 * This class is made to enable the logging and reading of data from the saved location on the external storage
 * of the device. There is a log mode, write mode, and read mode implementation for the file readers.
 */
public class DataLogger extends PreferenceActivity
{
    private String FileName, Content, Subdirectory, DeviceID, mainDirectoryPath, Directory, externalStorageState, lineItems;        // Variable names for the file characters and contents.
    private SharedPreferences sharedPreferences;        // Gets the shared preference from the system.
    FileOutputStream outputFIle;        // File to be put out into the device
    File mainDirectory, dataFile;     // Sets up the file of the system
    OutputStreamWriter outputWriter;        // Link to the writer of the device
    StringBuilder fileContent;      // A string builder variable storing data
    BufferedReader bufferedReader;      // A buffer reader to read from the file

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
     * Method opens a link to a main directory folder specified in the sdcard of the device and saves the
     * the contents of a data stream into the given filename. If any of the file or folders do not exist,
     * one is created and logged into.
     * @param saveType is the argument passed into the method to determine what type of method to save the
     *                 data. An argument with "log" would append the data to the file without erasing the
     *                 contents. An argument with "write" would erase the content of the file and add the
     *                 new contents to the file.
     */
    public void saveData(String saveType)
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

                if (saveType.toLowerCase().equals("log"))       // Checks if the data is supposed to be in log mode
                    this.outputWriter.append(this.Content);        // Appends the content to be saved into the file without erasing it

                else if (saveType.toLowerCase().equals("write"))     // Checks if the data is supposed to be in write mode
                    this.outputWriter.write(this.Content);      // Writes the content to be saved into the file after erasing it.

                else
                    Toast.makeText(getApplicationContext(), "Error: Invalid Save Argument", Toast.LENGTH_LONG).show();     // Shows a toast

                this.outputWriter.close();      // Closes the output writer
                this.outputFIle.close();        // Closes the file that is output into the device
            }
            catch (IOException e)       // If a file error happens
            {
                Toast.makeText(getApplicationContext(), "Error Making File", Toast.LENGTH_LONG).show();     // Shows a toast
            }
            catch (Exception e)     // If any other error happens
            {
                Toast.makeText(getApplicationContext(), "Unknown Error", Toast.LENGTH_LONG).show();     // Shows a toast
            }
        }
        else        // Runs if the external storage is not writable
        {
            Toast.makeText(getApplicationContext(), "Cannot Write to sdcard", Toast.LENGTH_LONG).show();     // Shows a toast
        }
    }

    /**
     * This method reads the content from a file specified with a path.
     * @return the string format of what the file contains.
     */
    public String readData()
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
                Toast.makeText(getApplicationContext(), "Cannot Find File", Toast.LENGTH_LONG).show();     // Shows a toast
            }
            catch (Exception e)     // If any other error happens
            {
                Toast.makeText(getApplicationContext(), "Unknown Error", Toast.LENGTH_LONG).show();     // Shows a toast
            }

            return this.fileContent.toString();     // Returns the file contents a string
        }
        else        // Runs if the external storage is not readable
        {
            Toast.makeText(getApplicationContext(), "Cannot Read from sdcard", Toast.LENGTH_LONG).show();     // Shows a toast
        }

        return "Cannot Read from sdcard";       // Returns a false cannot read from sdcard error message
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
