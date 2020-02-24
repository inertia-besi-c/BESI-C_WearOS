package com.linklab.inertia.besic;

/*
 * Imports needed by the system to function appropriately
 */
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.wearable.activity.WearableActivity;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferService;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * This class is run when the device needs to upload data to a S3 bucket set up on a
 * Amazon web service cloud storage space.
 */
public class Amazon extends WearableActivity
{
    private SharedPreferences sharedPreferences;        // Sets up the preferences
    private InputStream inputStream;        // Sets up an input stream
    private AmazonS3 s3;        // Sets up the s3 link
    private CognitoCachingCredentialsProvider credentialsProvider;      // Sets up the cognito reader
    private ByteArrayOutputStream byteArrayOutputStream;        // Sets up the byte stream
    private SystemInformation systemInformation;        // Sets up system information
    private TransferUtility transferUtility;        // Sets up the transfer utility
    private TransferObserver observer;      // Sets up the observer
    private JSONObject jObjectI, jObjectII;     // Sets up json objects
    private String bucketName, keyValue, identityID;        // Sets up strings
    private File fileToUpload;      // The files to upload
    private int fileLength;     // File length

    /**
     * This method is run on the creation of the class
     * @param savedInstanceState is an instance passed to the super class
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);     // Calls th super class

        this.inputStream = this.getResources().openRawResource(R.raw.awsconfiguration);     //  Sets the input stream
        this.byteArrayOutputStream = new ByteArrayOutputStream();       // Initializes the variable
        this.systemInformation = new SystemInformation();       // Initializes the variable
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());       // Initializes the variable
        this.fileToUpload = new File(Environment.getExternalStorageDirectory().toString() + "/" + this.sharedPreferences.getString("directory_key", "BESI-C"));       // Initializes the variable

        this.getApplicationContext().startService(new Intent(getApplicationContext(), TransferService.class));      // Starts the service
        this.getSecretValues();       // Calls the method
        this.findAndLogAllFiles(this.fileToUpload);     // Calls the method
    }

    /**
     * This method is made to recursively find all the files in a given directory
     * @param folder  is the directory files is needed from
     */
    private void findAndLogAllFiles(final File folder)
    {
        for (final File fileEntry : folder.listFiles())     // For every file in the folder
        {
            if (fileEntry.isDirectory())        // If the file is a directory
            {
                this.findAndLogAllFiles(fileEntry);     // Calls the method
            }
            else        // IF not
            {
                this.fileToUpload = new File(fileEntry.getAbsolutePath());      // Find the path to the file
                this.keyValue = this.sharedPreferences.getString("deployment_key", "P0D0")+"/"+this.fileToUpload.getParentFile().getName()+"/"+this.fileToUpload.getName();     // Set the value
                this.uploadFileToAWS(this.bucketName, this.keyValue, this.fileToUpload);        // Upload the file
            }
        }

        finish();       // Finish the class
    }

    /**
     * This method is made to read the credential values from the
     * gitIgnored JSON file
     */
    private void getSecretValues()
    {
        try     // Tires the following
        {
            this.fileLength = this.inputStream.read();      // Makes a file length to read
            while (this.fileLength != -1)       // While there is still data
            {
                this.byteArrayOutputStream.write(this.fileLength);      // Write the byte
                this.fileLength = this.inputStream.read();      // Set the length
            }
            this.inputStream.close();       // Close the stream

            this.jObjectI = new JSONObject(this.byteArrayOutputStream.toString());      // Set the json object to be a string
            this.jObjectII = this.jObjectI.getJSONObject("AmazonInformation").getJSONObject("BESICloudInformation");        // Read the data
            this.bucketName = this.jObjectII.getString("Bucket");       // Read the data
            this.identityID = this.jObjectII.getString("PoolId");       // Read the data
        }
        catch (IOException | JSONException e)       // If an error occurs
        {
            e.printStackTrace();        // Print it out.
        }
    }

    /**
     * Method to upload a file to a given bucket
     * @param bucket is the name of the bucket to be uploaded to
     * @param key is the key that is needed
     * @param file is the file to be uploaded
     */
    private void uploadFileToAWS(String bucket, String key, File file)
    {
        this.credentialsProvider = new CognitoCachingCredentialsProvider(this.getApplicationContext(), this.identityID, Regions.US_EAST_1);     // Sets the credentials
        this.s3 = new AmazonS3Client(this.credentialsProvider);     // Sends that to the bucket for verification
        this.transferUtility = new TransferUtility(this.s3, this.getApplicationContext());      // Initializes a transfer utility
        this.observer = transferUtility.upload(bucket,key, file);       // Initialises an observer

        this.observer.setTransferListener(new TransferListener()
        {
            /**
             * Method run if the state was to change during upload
             * @param id is the identification
             * @param state is the state changed
             */
            @Override
            public void onStateChanged(int id, TransferState state)
            {
                if (state.equals(TransferState.COMPLETED))      // Checks for success
                {
                    systemInformation.toast(getApplicationContext(), "Upload Completed!!!");        // Toasts the screen
                }
                else if (state.equals(TransferState.FAILED))        // Checks for failure
                {
                    systemInformation.toast(getApplicationContext(), "Upload Failed");        // Toasts the screen
                }
            }

            /**
             * Method run when the progress of an upload is changed
             * @param id is the identification
             * @param bytesCurrent is the current bytes
             * @param bytesTotal is the total bytes
             */
            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {}

            /**
             * This method is called if an error happens with uploading one of the files
             * @param id is the identification of the device
             * @param ex is the exception that happened
             */
            @Override
            public void onError(int id, Exception ex) {}
        });
    }
}
