package com.zapatatech.santabiblia.workmanager;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.work.Data;
import androidx.work.ForegroundInfo;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.zapatatech.santabiblia.R;
import com.zapatatech.santabiblia.interfaces.retrofit.RetrofitRESTendpointsService;
import com.zapatatech.santabiblia.interfaces.retrofit.RetrofitSyncUp;
import com.zapatatech.santabiblia.retrofit.RetrofitServiceGenerator;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;

public class SyncUpServerWM extends Worker {
    private static final String TAG = "SyncUpServerWM";
    public static final int SYNCUP_NOTIFICATION_ID = 1;
    public static final String SYNCUP_NOTIFICATION_CHANNEL_ID = "syncup_notification_channel_id";

    private final Context context;
    //====================================================================================================
    public RetrofitSyncUp syncupService = RetrofitServiceGenerator.createService(RetrofitSyncUp.class, null);
    //public RetrofitSyncUp syncupService = RetrofitServiceGenerator.createServiceRx(RetrofitSyncUp.class, null);
    //====================================================================================================
    private NotificationManager notificationManager;
    private NotificationCompat.Builder notificationBuilder;

    public SyncUpServerWM(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);
        this.context = context;
        this.notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
    }

    //    This method is called on a background thread - you are required to synchronously do your work and return the ListenableWorker.
//    Result from this method. Once you return from this method, the Worker is considered to have finished what its doing and will be destroyed. If you need to do your work asynchronously on a thread of your own choice, see ListenableWorker
    @Override
    public Result doWork() {
        String client_version = getInputData().getString("CLIENT_VERSION");
        String client_state = getInputData().getString("CLIENT_STATE");
        if(client_version == null || client_state == null) {
            return Result.failure();
        }

        if(!isStopped()) {

            setProgressAsync(new Data.Builder().putBoolean("processing", true).build());

            String progress = "Starting Sync Up";
            //mark this as important: WILL RUN IN THE BACKGROUND IF APP IS CLOSED
            setForegroundAsync(createForegroundInfo(progress));
            //only attempt to retry 2 times
            if (getRunAttemptCount() > 2) {
                Log.d(TAG, "too many failed attemp, give up");
                notificationManager.cancel(SYNCUP_NOTIFICATION_ID);
                return Result.failure();
            }
            // Do the work here--in this case, upload the images.
            boolean result = initRetrofit(client_version, client_state);
            if (result) {
                Data output = new Data.Builder().putBoolean("syncupComplete", true).build();
                return Result.success(output);
            } else {
                //Log.d(TAG, "doWork: isStopped inner " + isStopped());
                notificationManager.cancel(SYNCUP_NOTIFICATION_ID);
                return Result.failure();
            }
        } else {
            Log.d(TAG, "doWork: isStopped ELSE");
            notificationManager.cancel(SYNCUP_NOTIFICATION_ID);
            return Result.failure();
        }
    }


    @Override
    public void onStopped() {
        super.onStopped();
        notificationManager.cancel(SYNCUP_NOTIFICATION_ID);
    }

//    //start Retrofit setup, synchronous since this is running in the background already
    private boolean initRetrofit(String client_version, String client_state) {
//        Log.d(TAG, "initRetrofit: " + client_state);
//
//        Call<ResponseBody> request = syncupService.syncUpWithServer();
//        try {
//            boolean result = writeResponseBodyToDisk(request.execute().body(), fileName);
//            if(result){
//                return true;
//            } else {
//                //if for some reason the process did not CREATE the db file, delete anything we have written to the disk so far;
//                //boolean deleteFileResult = deleteFile(fileName);
//                Log.d(TAG, "deleteDB: " + deleteFileResult);
//                return false;
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//            return false;
//        }
        //TODO: remove
        return true;
    }

    @NonNull
    private ForegroundInfo createForegroundInfo(@NonNull String progress) {
        // Build a notification using bytesRead and contentLength

        //Context context = getApplicationContext();
        String title = "Synchronizing data";//context.getString(R.string.notification_title);
        String cancel = "Cancel";//context.getString(R.string.cancel_download);
        // This PendingIntent can be used to cancel the worker
        PendingIntent intent = WorkManager.getInstance(context).createCancelPendingIntent(getId());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel();
        }

        Notification notification = new NotificationCompat.Builder(context, SYNCUP_NOTIFICATION_CHANNEL_ID)
                .setContentTitle(title)
                .setTicker(title)
                .setSmallIcon(R.drawable.ic_sync)
                .setOngoing(true)
                // Add the cancel action to the notification which can
                // be used to cancel the worker
                .addAction(android.R.drawable.ic_delete, cancel, intent)
                .build();

        return new ForegroundInfo(SYNCUP_NOTIFICATION_ID, notification);
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private void createChannel() {
        // Create a Notification channel
        NotificationChannel notificationChannel = new NotificationChannel(SYNCUP_NOTIFICATION_CHANNEL_ID, "SyncUpChannel", NotificationManager.IMPORTANCE_LOW);
        notificationChannel.setDescription("no sound");
        notificationChannel.setSound(null, null);
        notificationChannel.enableLights(false);
        notificationChannel.setLightColor(Color.BLUE);
        notificationChannel.enableVibration(false);
        notificationManager.createNotificationChannel(notificationChannel);
    }

}
