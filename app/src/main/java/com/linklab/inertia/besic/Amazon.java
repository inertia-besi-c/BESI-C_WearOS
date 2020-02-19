package com.linklab.inertia.besic;

import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;

import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;

public class Amazon extends WearableActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        AmazonS3 s3Client = new AmazonS3Client(new BasicSessionCredentials(awsAccessKey, awsSecretKey, sessionToken));
        s3Client.setRegion(Region.getRegion(Regions.US_EAST_2));

        TransferUtility transferUtility = new TransferUtility(s3Client, context);
        TransferObserver transferObserver = transferUtility.upload(bucketName, pathToStore, file, CannedAccessControlList.PublicRead);

        transferObserver.setTransferListener(new TransferListener() {

            @Override
            public void onStateChanged(int id, TransferState state)
            {
                // Implement the code for handle the file status changed.
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal)
            {
                //Implement the code to handle the file uploaded progress.
            }

            @Override
            public void onError(int id, Exception exception)
            {
                //Implement the code to handle the file upload error.
            }

        });
    }


}

