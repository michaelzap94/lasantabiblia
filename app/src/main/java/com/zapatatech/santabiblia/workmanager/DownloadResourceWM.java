package com.zapatatech.santabiblia.workmanager;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
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
import com.zapatatech.santabiblia.retrofit.RetrofitServiceGenerator;
import com.zapatatech.santabiblia.utilities.Util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.ResponseBody;
import retrofit2.Call;

import static android.content.Context.MODE_PRIVATE;

public class DownloadResourceWM  extends Worker {
    private static final String TAG = "DownloadResourceWM";
    public static final int DOWNLOAD_RESOURCE_NOTIFICATION_ID = 0;
    public static final String DOWNLOAD_RESOURCE_NOTIFICATION_CHANNEL_ID = "DOWNLOAD_RESOURCE_NOTIFICATION_CHANNEL_ID";
    //====================================================================================================
    public RetrofitRESTendpointsService resourcesService = RetrofitServiceGenerator.createService(RetrofitRESTendpointsService.class, null, false);
    //public RetrofitRESTendpointsService resourcesService = RetrofitServiceGenerator.createServiceRx(RetrofitRESTendpointsService.class, null);
    //====================================================================================================
    private NotificationCompat.Builder notificationBuilder;
    private NotificationManager notificationManager;//DELETE BELOW
    //private Notification notification;
    private final Context context;
    private String fileName;
    private InputStream inputStream = null;
    private OutputStream outputStream = null;


    public DownloadResourceWM(
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
        String resourceUrl = getInputData().getString("RESOURCE_URL");
        fileName = getInputData().getString("RESOURCE_FILENAME");
        if (fileName == null || resourceUrl == null) {
            return Result.failure(makeDataForFailure());
        }
        Log.d(TAG, "doWork: isStopped " + isStopped());
        if(!isStopped()) {
            Data processingData = new Data.Builder()
                    .putInt("progress", 0)
                    .putString("fileNameProcessing", fileName)
                    .build();
            setProgressAsync(processingData);

            String progress = "Starting Download";
            //mark this as important: WILL RUN IN THE BACKGROUND IF APP IS CLOSED
            setForegroundAsync(createForegroundInfo(progress, fileName));
            //only attempt to retry 2 times
            if (getRunAttemptCount() > 2) {
                Log.d(TAG, "too many failed attemp, give up");
                notificationManager.cancel(DOWNLOAD_RESOURCE_NOTIFICATION_ID);
                return Result.failure(makeDataForFailure());
            }
            // Do the work here--in this case, upload the images.
            boolean result = initRetrofit(resourceUrl, fileName);
            if (result) {
                Data output = new Data.Builder()
                        .putBoolean("downloadComplete", true)
                        .putString("fileName", fileName)
                        .build();
                return Result.success(output);
            } else {
                Log.d(TAG, "initRetrofit failure");
                notificationManager.cancel(DOWNLOAD_RESOURCE_NOTIFICATION_ID);
                return Result.failure(makeDataForFailure());
            }
        } else {
            Log.d(TAG, "doWork: isStopped ELSE");
            notificationManager.cancel(DOWNLOAD_RESOURCE_NOTIFICATION_ID);
            return Result.failure(makeDataForFailure());
        }
    }


    @Override
    public void onStopped() {
        Log.d(TAG, "onStopped: " + fileName);
        super.onStopped();
        stopWriting();
        notificationManager.cancel(DOWNLOAD_RESOURCE_NOTIFICATION_ID);
    }

    //start Retrofit setup, synchronous since this is running in the background already
    private boolean initRetrofit(String fileURL, String fileName) {
        Log.d(TAG, "initRetrofit: " + fileURL);
        Call<ResponseBody> request = resourcesService.downloadResource(fileURL);
        try {
            //convertFile(request.execute().body(), fileName);
            boolean result = writeResponseBodyToDisk(request.execute().body(), fileName);
            if(result){
                return true;
            } else {
                //if for some reason the process did not CREATE the db file, delete anything we have written to the disk so far;
                boolean deleteFileResult = deleteFile(fileName);
                Log.d(TAG, "deleteDB: " + deleteFileResult);
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

    }

    private void stopWriting(){
        inputStream = null;
        outputStream = null;
    }

    private boolean writeResponseBodyToDisk(ResponseBody body, String fileName) {
        try {
            //File futureStudioIconFile = new File(getExternalFilesDir(null) + File.separator + "Future Studio Icon.png");
//            File folder = Util.getMyLocalDownloadFolder("bibles", this.context);
//            if (!folder.exists()) {
//                folder.mkdirs();
//            }
            //WRITE TO A TEMPORAL FILE
            File outputFile = new File(Util.getDBPath(this.context), getTempFileName(fileName));

            //THEN OVERWRITE THE FLE:

            try {
                byte[] fileReader = new byte[4096];
                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;
                inputStream = body.byteStream();
                outputStream = new FileOutputStream(outputFile);
                boolean downloadComplete = false;
                while (true) {
                    int read = inputStream.read(fileReader);
                    if (read == -1) {
                        break;
                    }

                    outputStream.write(fileReader, 0, read);
                    fileSizeDownloaded += read;

                    int progress = (int) ((double) (fileSizeDownloaded * 100) / (double) fileSize);
                    updateProgress(progress, fileName);
                    //Log.d(TAG, "file download: " + fileSizeDownloaded + " of " + fileSize);
                    if(fileSizeDownloaded == fileSize) downloadComplete = true;
                }
                boolean createDBSuccess = createDB(downloadComplete, fileName);
                //set notification bar as download complete
                onDownloadComplete(createDBSuccess, fileName);
                outputStream.flush();
                return createDBSuccess;
            } catch (Exception e) {
                Log.d(TAG, "writeResponseBodyToDisk: Exception inner" + e.getMessage());
                return false;
            } finally {
                if (inputStream != null) inputStream.close();
                if (outputStream != null) outputStream.close();
            }
        } catch (Exception e) {
            Log.d(TAG, "writeResponseBodyToDisk: Exception " + e.getMessage());
            return false;
        }
    }

    //------------------------------------------------------------------------------------------

    @NonNull
    private ForegroundInfo createForegroundInfo(@NonNull String progress, String fileName) {
        // Build a notification using bytesRead and contentLength

        //Context context = getApplicationContext();
        String title = "Downloading Resource";//context.getString(R.string.notification_title);
        String cancel = "Cancel";//context.getString(R.string.cancel_download);
        // This PendingIntent can be used to cancel the worker
        Log.d(TAG, "createForegroundInfo: " + getId());
        PendingIntent intent = WorkManager.getInstance(context).createCancelPendingIntent(getId());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel();
        }

        notificationBuilder = new NotificationCompat.Builder(context, DOWNLOAD_RESOURCE_NOTIFICATION_CHANNEL_ID)
                .setContentTitle(title)
                //.setContentText("Downloading " + fileName)
                //.setTicker(title)
                .setSmallIcon(android.R.drawable.stat_sys_download)
                .setOngoing(true)
                //.setDefaults(0)
                //.setAutoCancel(true)//cancel when user taps on icon
                // Add the cancel action to the notification which can be used to cancel the worker
                .addAction(android.R.drawable.ic_delete, cancel, intent);

        Notification notification = notificationBuilder.build();

        return new ForegroundInfo(DOWNLOAD_RESOURCE_NOTIFICATION_ID, notification);
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private void createChannel() {
        // Create a Notification channel
        NotificationChannel notificationChannel = new NotificationChannel(DOWNLOAD_RESOURCE_NOTIFICATION_CHANNEL_ID, "DownloadResourceChannel", NotificationManager.IMPORTANCE_LOW);
        notificationChannel.setDescription("no sound");
        notificationChannel.setSound(null, null);
        notificationChannel.enableLights(false);
        notificationChannel.setLightColor(Color.BLUE);
        notificationChannel.enableVibration(false);
        notificationManager.createNotificationChannel(notificationChannel);
    }
    //------------------------------------------------------------------------------------------

    //updates the process in the Notification bar
    private void updateProgress(int currentProgress, String fileName) {
        Log.d(TAG, "updateProgress: " + currentProgress);
        //workmanager specific-----------------------------------------------------------------
        Data processingData = new Data.Builder()
                .putInt("progress", currentProgress)
                .putString("fileNameProcessing", fileName)
                .build();
        setProgressAsync(processingData);
        //------------------------------------------------------------------------------------
        notificationBuilder.setProgress(100, currentProgress, false);
        notificationBuilder.setContentText("Downloaded: " + currentProgress + "%");
        Notification notification = notificationBuilder.build();
        notificationManager.notify(DOWNLOAD_RESOURCE_NOTIFICATION_ID, notification);
    }
    //called from downloadImage once completed -> will call sendProgressUpdate(trigger the event to the LocalBroadcastManager)
    private void onDownloadComplete(boolean createDBSuccess, String fileName) {
        Log.d(TAG, "createDBSuccess: " + createDBSuccess);
        //cancel current notification
        notificationManager.cancel(DOWNLOAD_RESOURCE_NOTIFICATION_ID);

        //Replace notification with a new one using the same id DOWNLOAD_RESOURCE_NOTIFICATION_ID
        //this is so we remove the cancel button
        notificationBuilder = new NotificationCompat.Builder(context, DOWNLOAD_RESOURCE_NOTIFICATION_CHANNEL_ID);
        notificationBuilder.setContentTitle(fileName);
        notificationBuilder.setProgress(0, 0, false);
        notificationBuilder.setContentText((createDBSuccess) ? "Download Complete" : "Download Failed");
        notificationBuilder.setSmallIcon(android.R.drawable.stat_sys_download_done);
        Notification notification = notificationBuilder.build();
        notificationManager.notify(DOWNLOAD_RESOURCE_NOTIFICATION_ID, notification);

    }
    private boolean createDB(boolean downloadComplete, String fileName) {
        Log.d(TAG, "createDB: " + downloadComplete);
        if(downloadComplete) {
            //Rename file:
            boolean renameSuccess = renameFile(fileName);
            if(renameSuccess){
                //CREATE empty db:
                try {
                    SQLiteDatabase db = this.context.openOrCreateDatabase(fileName, MODE_PRIVATE, null);
                    db.close();
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private boolean renameFile(String fileName){
        try{
            File from      = new File(Util.getDBPath(this.context), getTempFileName(fileName));
            File to        = new File(Util.getDBPath(this.context), fileName);
            return from.renameTo(to);
        } catch (Exception e) {
            Log.e(TAG, "renameFile: ", e);
            return false;
        }
    }

    private boolean deleteFile(String fileName) {
        boolean deleteResult = false;
        File outputFile = new File(Util.getDBPath(this.context), getTempFileName(fileName));
        if(outputFile.exists()){
            deleteResult = outputFile.delete();
            Log.d(TAG, "deleteDB: file: " + deleteResult);
        }
        return deleteResult;
        //return this.context.deleteDatabase(fileName);
    }

    private String getTempFileName(String fileName){
        return fileName + "-temp";
    }

    //-------------------------------------------------------------------------------------------
    private Data makeDataForFailure(){
        return new Data.Builder()
                .putBoolean("downloadComplete", false)
                .putString("fileName", fileName)
                .build();
    }

}
