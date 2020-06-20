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

import com.zapatatech.santabiblia.DatabaseHelper.ContentDBHelper;
import com.zapatatech.santabiblia.R;
import com.zapatatech.santabiblia.interfaces.retrofit.RetrofitRESTendpointsService;
import com.zapatatech.santabiblia.interfaces.retrofit.RetrofitSyncUp;
import com.zapatatech.santabiblia.models.User;
import com.zapatatech.santabiblia.retrofit.Pojos.POJOSyncUp;
import com.zapatatech.santabiblia.retrofit.Pojos.POJOSyncUpHelper;
import com.zapatatech.santabiblia.retrofit.RetrofitServiceGenerator;
import com.zapatatech.santabiblia.utilities.CommonMethods;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;

public class SyncUpServerWM extends Worker {
    private static final String TAG = "SyncUpServerWM";
    public static final int SYNCUP_NOTIFICATION_ID = 1;
    public static final String SYNCUP_NOTIFICATION_CHANNEL_ID = "SYNCUP_NOTIFICATION_CHANNEL_ID";

    private final Context context;
    //====================================================================================================
    private User user;
    public RetrofitSyncUp syncupService;
    //public RetrofitSyncUp syncupService = RetrofitServiceGenerator.createServiceRx(RetrofitSyncUp.class, null);
    //====================================================================================================
    private NotificationManager notificationManager;
    private NotificationCompat.Builder notificationBuilder;
    private NotificationChannel notificationChannel;

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

        String accessToken = CommonMethods.getAccessToken(context);
        Log.d(TAG, "doWork: accessToken " + accessToken);
        if(accessToken == null) {
            return Result.failure();
        } else {
            user = CommonMethods.decodeJWTAndCreateUser(accessToken);
            this.syncupService = RetrofitServiceGenerator.createService(RetrofitSyncUp.class, accessToken, true);
        }

        int client_version = getInputData().getInt("CLIENT_VERSION", -1);
        int client_state = getInputData().getInt("CLIENT_STATE", -1);
        int syncUpOrOverride = getInputData().getInt("ACTION_FLAG", -1);
        if(client_version == -1 || client_state == -1 || syncUpOrOverride == -1) {
            return Result.failure();
        }

        if(!isStopped()) {
            Log.d(TAG, "doWork: starting");
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
            boolean result = false;
            // Do the work here
            if(syncUpOrOverride == CommonMethods.DATA_SYNCUP_FLAG) {
                //DO SYNCUP: DOWNLOAD
                result = initSyncUp(client_version, client_state);
            } else {
                //DO OVERRIDE: UPLOAD
                result = initOverride(client_version, client_state);
            }
            //send new notification with result
            onSyncUpComplete(result);

            if (result) {
                Data output = new Data.Builder().putBoolean("syncupComplete", true).build();
                return Result.success(output);
            } else {
                //Log.d(TAG, "doWork: isStopped inner " + isStopped());
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
        Log.d(TAG, "onStopped: ");
        super.onStopped();
        notificationManager.cancel(SYNCUP_NOTIFICATION_ID);
    }

    private boolean initSyncUp(int client_version, int client_state){
        Log.d(TAG, "initSyncUp: " + client_state + " - " + client_version);
        Call<POJOSyncUpHelper> request = syncupService.syncUpWithServer();
        try {
            boolean result = ContentDBHelper.getInstance(context).overrideLocalData(request.execute().body());
            if(result){
                return true;
            } else {
                //if for some reason the process did not CREATE the db file, delete anything we have written to the disk so far;
                //boolean deleteFileResult = deleteFile(fileName);
                //Log.d(TAG, "deleteDB: " + deleteFileResult);
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    //-----------------------------------------------------------------------------------------------

    private boolean initOverride(int client_version, int client_state){
        Log.d(TAG, "initOverride: " + client_state + " - " + client_version);
        try {
            Call<POJOSyncUpHelper> request = syncupService.overrideServer(new POJOSyncUpHelper(
                    ContentDBHelper.getInstance(context).getAllLabelsRaw(),
                    ContentDBHelper.getInstance(context).getAllVersesMarkedRaw(),
                    ContentDBHelper.getInstance(context).getAllVersesLearnedRaw(),
                    client_state,
                    client_version
            ));
            POJOSyncUpHelper response = request.execute().body();
            Log.d(TAG, "initOverride: " + response);
            Log.d(TAG, "initOverride: status " + response.getStatus());
            Log.d(TAG, "initOverride: version " + response.getVersion());
            if(response.getStatus().equals("success")) {
                if(response.getVersion() == client_version){
                    Log.d(TAG, "initOverride: equals");
                    return true;
                } else {
                    Log.d(TAG, "initOverride: not equals");
                    return ContentDBHelper.getInstance(context).updateSyncUp(user.getEmail(), response.getVersion(), 1, null);
                }
            } else {
                Log.d(TAG, "initOverride: out " + response.getStatus());
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    //===========================================================================================================================
    @NonNull
    private ForegroundInfo createForegroundInfo(@NonNull String progress) {
        // Build a notification using bytesRead and contentLength

        //Context context = getApplicationContext();
        String title = "Synchronizing data...";//context.getString(R.string.notification_title);
        String cancel = "Cancel";//context.getString(R.string.cancel_download);
        // This PendingIntent can be used to cancel the worker
        PendingIntent intent = WorkManager.getInstance(context).createCancelPendingIntent(getId());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel();
        }

        notificationBuilder = new NotificationCompat.Builder(context, SYNCUP_NOTIFICATION_CHANNEL_ID)
                .setContentTitle(title)
                .setContentText("Please Wait.")
                //.setTicker(title)
                .setProgress(0, 0, true)
                .setSmallIcon(android.R.drawable.ic_popup_sync)
                .setOngoing(true);
                // Add the cancel action to the notification which can
                // be used to cancel the worker
                //.addAction(android.R.drawable.ic_delete, cancel, intent);
        Notification notification = notificationBuilder.build();
        //This notification gets cancelled when the worker STOPS
        return new ForegroundInfo(SYNCUP_NOTIFICATION_ID, notification);
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private void createChannel() {
        // Create a Notification channel
        notificationChannel = new NotificationChannel(SYNCUP_NOTIFICATION_CHANNEL_ID, "SyncUpChannelName", NotificationManager.IMPORTANCE_LOW);
        notificationChannel.setDescription("no sound");
        notificationChannel.setSound(null, null);
        notificationChannel.enableLights(false);
        notificationChannel.setLightColor(Color.BLUE);
        notificationChannel.enableVibration(false);
        notificationManager.createNotificationChannel(notificationChannel);
    }
    //called from downloadImage once completed -> will call sendProgressUpdate(trigger the event to the LocalBroadcastManager)
    private void onSyncUpComplete(boolean success) {
        Log.d(TAG, "onSyncUpComplete: " + success);
        //cancel current notification JUST IN CASE, IT GETS CANCELLED AUTOMATICALLY
        notificationManager.cancel(SYNCUP_NOTIFICATION_ID);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if(notificationChannel == null ){
                createChannel();
            }
        }

        //Replace notification with a new one using the same id DOWNLOAD_RESOURCE_NOTIFICATION_ID
        //this is so we remove the cancel button
        notificationBuilder = new NotificationCompat.Builder(context, SYNCUP_NOTIFICATION_CHANNEL_ID);
        notificationBuilder.setContentTitle("Synchronization Result:");
        //notificationBuilder.setProgress(0, 0, false);
        notificationBuilder.setContentText((success) ? "You are now Up-to-Date" : "Synchronization Failed");
        notificationBuilder.setSmallIcon(android.R.drawable.stat_notify_sync_noanim);
        Notification notification = notificationBuilder.build();
        notificationManager.notify(SYNCUP_NOTIFICATION_ID + 10000, notification);

    }

}
