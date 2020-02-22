package com.linklab.inertia.besic;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;

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

public class Amazon extends WearableActivity
{
    private SharedPreferences sharedPreferences;
    private InputStream inputStream;
    private AmazonS3 s3;
    private CognitoCachingCredentialsProvider credentialsProvider;
    private ByteArrayOutputStream byteArrayOutputStream;
    private SystemInformation systemInformation;
    private TransferUtility transferUtility;
    private TransferObserver observer;
    private JSONObject jObjectI, jObjectII;
    private String bucketName, keyValue, identityID;
    private File fileToUpload;
    private int fileLength;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        this.inputStream = this.getResources().openRawResource(R.raw.awsconfiguration);
        this.byteArrayOutputStream = new ByteArrayOutputStream();
        this.systemInformation = new SystemInformation();
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        this.fileToUpload = new File(Environment.getExternalStorageDirectory().toString() + "/" + this.sharedPreferences.getString("directory_key", "BESI-C"));

        this.getApplicationContext().startService(new Intent(getApplicationContext(), TransferService.class));
        this.getSecretValues();
        this.findAndLogAllFiles(this.fileToUpload);
    }

    private void findAndLogAllFiles(final File folder)
    {
        for (final File fileEntry : folder.listFiles())
        {
            if (fileEntry.isDirectory())
            {
                this.findAndLogAllFiles(fileEntry);
            }
            else
            {
                this.fileToUpload = new File(fileEntry.getAbsolutePath());
                this.keyValue = this.sharedPreferences.getString("deployment_key", "P0D0")+"/"+this.fileToUpload.getParentFile().getName()+"/"+this.fileToUpload.getName();
                this.uploadFileToAWS(this.bucketName, this.keyValue, this.fileToUpload);
            }
        }

        finish();
    }

    private void getSecretValues()
    {
        try
        {
            this.fileLength = this.inputStream.read();
            while (this.fileLength != -1)
            {
                this.byteArrayOutputStream.write(this.fileLength);
                this.fileLength = this.inputStream.read();
            }
            this.inputStream.close();

            this.jObjectI = new JSONObject(this.byteArrayOutputStream.toString());
            this.jObjectII = this.jObjectI.getJSONObject("AmazonInformation").getJSONObject("BESICloudInformation");
            this.bucketName = this.jObjectII.getString("Bucket");
            this.identityID = this.jObjectII.getString("PoolId");
        }
        catch (IOException | JSONException e)
        {
            System.out.println(e);
            e.printStackTrace();
        }
    }

    private void uploadFileToAWS(String bucket, String key, File file)
    {
        this.credentialsProvider = new CognitoCachingCredentialsProvider(this.getApplicationContext(), this.identityID, Regions.US_EAST_1);
        this.s3 = new AmazonS3Client(this.credentialsProvider);
        this.transferUtility = new TransferUtility(this.s3, this.getApplicationContext());
        this.observer = transferUtility.upload(bucket,key, file);

        this.observer.setTransferListener(new TransferListener()
        {
            @Override
            public void onStateChanged(int id, TransferState state)
            {
                if (state.equals(TransferState.COMPLETED))
                {
                    systemInformation.toast(getApplicationContext(), "Upload Completed!!!");
                }
                else if (state.equals(TransferState.FAILED))
                {
                    systemInformation.toast(getApplicationContext(), "Upload Failed");
                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal)
            {
                float percentDonef = ((float) bytesCurrent / (float) bytesTotal) * 100;
                int percentDone = (int) percentDonef;

                Log.d("YourActivity", "ID:" + id + " bytesCurrent: " + bytesCurrent + " bytesTotal: " + bytesTotal + " " + percentDone + "%");
                // Updates to the UI will be done here
            }

            @Override
            public void onError(int id, Exception ex)
            {
                // Error logging and handling will be done here
            }
        });
    }
}
