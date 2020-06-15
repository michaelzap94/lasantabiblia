package com.zapatatech.santabiblia;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;
import com.zapatatech.santabiblia.utilities.Util;

import java.io.File;

public class MainActivityRT extends AppCompatActivity {
    private static final String TAG = "MainActivityRT";
    public static final String PROGRESS_UPDATE = "progress_update";
    private static final int PERMISSION_REQUEST_CODE = 1;
    ImageView imageView;
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
        fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startWorkManager("https://unsplash.com/photos/YYW9shdLIwo/download?force=true", "journaldev-image-downloaded.jpg");

//                if (checkPermission()) {
//                    //startImageDownload();
//                    startWorkManager();
//                } else {
//                    requestPermission();
//                }
            }
        });

        //registerReceiver();
    }

    //BROADCAST MANAGER -> TO BE USED WITH INTENT SERVICE=======================================
    private void registerReceiver() {
        //CUSTOM INTENT FILTER as we'll use LocalBroadcastManager to listen for LOCAL(My APP) updates NOT system-wide updates
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(PROGRESS_UPDATE);
        //WE WILL ONLY LISTEN FOR THIS:
//        private void sendProgressUpdate(boolean downloadComplete) {
//            Intent intent = new Intent(MainActivityRT.PROGRESS_UPDATE);
//            intent.putExtra("downloadComplete", downloadComplete);
//            LocalBroadcastManager.getInstance(BackgroundNotificationService.this).sendBroadcast(intent);
//        }
        //============================================
        //LocalBroadcastManager is as its name says, an implementation of the broadcast methods that are only available to your app.
        //So this class is not the same as Context, it is simply a very specific, app-only implementation of Context's receiver/broadcast methods.
        // You should use it when there is absolutely no point for your listener to listen on global (system-wide) broadcasts and when your broadcast does not need to target anything outside your app.
        LocalBroadcastManager bManager = LocalBroadcastManager.getInstance(this);
        bManager.registerReceiver(mBroadcastReceiver, intentFilter);//LISTEN ONLY FOR THIS intent PROGRESS_UPDATE

    }
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(PROGRESS_UPDATE)) {
                boolean downloadComplete = intent.getBooleanExtra("downloadComplete", false);
                String fileName = intent.getStringExtra("fileName");
                Log.d("API123", "downloadComplete: " + downloadComplete);
                //Log.d("API123", download.getProgress() + " current progress");
                processImage(fileName, downloadComplete);
            }
        }
    };
    //START INTENTSERVICE
    private void startImageDownload() {
        Log.d(TAG, "startImageDownload: ");
        Intent intent = new Intent(MainActivityRT.this, BackgroundNotificationService.class);
        startService(intent);
    }
    //=================================================================================================


    //START WORKMANAGER===========================================================
    private void startWorkManager(String resourceUrl, String fileName){
        //CONSTRAINTS
        Constraints constraints = new Constraints.Builder()
                .setRequiresStorageNotLow(true)
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        WorkManager mWorkManager = WorkManager.getInstance(MainActivityRT.this);

        WorkRequest downloadWorkRequest = new OneTimeWorkRequest.Builder(DownloadWorker.class)
                    .setConstraints(constraints)
                    .addTag("downloadResource")
//                    .setBackoffCriteria(
//                            BackoffPolicy.LINEAR,
//                            OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
//                            TimeUnit.MILLISECONDS)
                    .setInputData(
                            new Data.Builder()
                                    .putString("RESOURCE_URL", resourceUrl)
                                    .putString("RESOURCE_FILENAME", fileName)
                                    .build()
                    )
                    .build();

        mWorkManager.enqueue(downloadWorkRequest);
        //================================================================================================
        //The returned value would be available in the task's WorkInfo:
        mWorkManager.getWorkInfoByIdLiveData(downloadWorkRequest.getId())
                .observe(this, info -> {
                    Log.d(TAG, "startWorkManager: " + info.getState());
                    if (info != null && info.getState().isFinished()) {
                        boolean myResult = info.getOutputData().getBoolean("downloadComplete", false);
                        //String fileName = info.getOutputData().getString("fileName");
                        processImage(fileName, myResult);
                    }
                });
    }

    //PERMISSIONS=================================================================================
    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }
    private void requestPermission() {

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startImageDownload();
                } else {
                    Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_SHORT).show();

                }
                break;
        }
    }
    //=================================================================================================
    private void processImage(String fileName, boolean downloadComplete){
        if (downloadComplete) {
            Toast.makeText(getApplicationContext(), "File download completed", Toast.LENGTH_SHORT).show();
            //File.separator is the same AS /
            //File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + File.separator +
            //        "journaldev-image-downloaded.jpg");

            File folder =  Util.getMyLocalDownloadFolder("bibles", MainActivityRT.this);
            File file = new File(folder,fileName);
            Picasso.get().load(file).into(imageView);
        }
    }
}
