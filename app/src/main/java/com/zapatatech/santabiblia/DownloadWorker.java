package com.zapatatech.santabiblia;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.zapatatech.santabiblia.interfaces.retrofit.RetrofitInterface;
import com.zapatatech.santabiblia.interfaces.retrofit.RetrofitRESTendpointsService;
import com.zapatatech.santabiblia.utilities.RetrofitServiceGenerator;
import com.zapatatech.santabiblia.utilities.Util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import okio.BufferedSink;
import okio.Okio;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public class DownloadWorker extends Worker {
    private static final String TAG = "DownloadWorker";
    //====================================================================================================
    public RetrofitRESTendpointsService resourcesService = RetrofitServiceGenerator.createService(RetrofitRESTendpointsService.class, null);
    //public RetrofitRESTendpointsService resourcesService = RetrofitServiceGenerator.createServiceRx(RetrofitRESTendpointsService.class, null);
    //====================================================================================================
    private NotificationCompat.Builder notificationBuilder;
    private NotificationManager notificationManager;//DELETE BELOW
    private final Context context;


    public DownloadWorker(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);
        this.context = context;
    }

//    This method is called on a background thread - you are required to synchronously do your work and return the ListenableWorker.
//    Result from this method. Once you return from this method, the Worker is considered to have finished what its doing and will be destroyed. If you need to do your work asynchronously on a thread of your own choice, see ListenableWorker
    @Override
    public Result doWork() {
        String resourceUrl = getInputData().getString("RESOURCE_URL");
        String fileName = getInputData().getString("RESOURCE_FILENAME");
        if(fileName == null || resourceUrl == null) {
            return Result.failure();
        }

        startNotificationBar();
        //only attempt to retry 2 times
        if (getRunAttemptCount() > 2) {
            Log.d(TAG, "too many failed attemp, give up");
            notificationManager.cancel(0);
            return Result.failure();
        }
        // Do the work here--in this case, upload the images.
        initRetrofit(resourceUrl, fileName);
        // Indicate whether the work finished successfully with the Result
        //return Result.success();

        //...set the output, and we're done!
        Data output = new Data.Builder()
                .putBoolean("downloadComplete", true)
                .putString("fileName", fileName)
                .build();
        return Result.success(output);
    }


    @Override
    public void onStopped() {
        super.onStopped();
        notificationManager.cancel(0);
    }

    public void startNotificationBar(){
        notificationManager = (NotificationManager) this.context.getSystemService(Context.NOTIFICATION_SERVICE);//DELETE BELOW
        //notificationManager = (NotificationManagerCompat) NotificationManagerCompat.from(getApplicationContext());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel("id", "an", NotificationManager.IMPORTANCE_LOW);

            notificationChannel.setDescription("no sound");
            notificationChannel.setSound(null, null);
            notificationChannel.enableLights(false);
            notificationChannel.setLightColor(Color.BLUE);
            notificationChannel.enableVibration(false);
            notificationManager.createNotificationChannel(notificationChannel);//DELETE 2 BELOW
            //NotificationManager notificationManagerOreo = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            //notificationManagerOreo.createNotificationChannel(notificationChannel);
        }

        notificationBuilder = new NotificationCompat.Builder(this.context, "id")
                .setSmallIcon(android.R.drawable.stat_sys_download)
                .setContentTitle("Download")
                .setContentText("Downloading Image")
                .setDefaults(0)
                .setAutoCancel(true);
        notificationManager.notify(0, notificationBuilder.build());

    }

    //start Retrofit setup, syncroneus since this is running in the background already
    private void initRetrofit(String fileURL, String fileName) {
        Log.d(TAG, "initRetrofit: " + fileURL);
        Call<ResponseBody> request = resourcesService.downloadResource(fileURL);
        try {
            //convertFile(request.execute().body(), fileName);
            writeResponseBodyToDisk(request.execute().body(), fileName);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this.context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    private boolean writeResponseBodyToDisk(ResponseBody body, String fileName) {
        try {
            //File futureStudioIconFile = new File(getExternalFilesDir(null) + File.separator + "Future Studio Icon.png");
            File folder = Util.getMyLocalDownloadFolder("bibles", this.context);
            if (!folder.exists()) {
                folder.mkdirs();
            }
            File outputFile = new File(folder, fileName);

            InputStream inputStream = null;
            OutputStream outputStream = null;
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
                    updateNotification(progress);
                    //Log.d(TAG, "file download: " + fileSizeDownloaded + " of " + fileSize);
                    if(fileSizeDownloaded == fileSize) downloadComplete = true;
                }
                onDownloadComplete(downloadComplete, fileName);
                outputStream.flush();
                return true;
            } catch (IOException e) {
                return false;
            } finally {
                if (inputStream != null) inputStream.close();
                if (outputStream != null) outputStream.close();
            }
        } catch (IOException e) {
            return false;
        }
    }
    //start downloading -> will call updateNotification every time AND onDownloadComplete once completed
    private void convertFile(ResponseBody body, String fileName) throws IOException {
        Log.d(TAG, "convertFile: " + fileName);
        int count;
        byte data[] = new byte[1024 * 4];
        long fileSize = body.contentLength();
        InputStream inputStream = new BufferedInputStream(body.byteStream(), 1024 * 8);
        //getExternalStorageDirectory() -> It returns the root path to your SD card (e.g mnt/sdcard/). If you save data on this path and uninstall the app, that data won't be lost.
        //Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), if this were Windows, would return some standard location on the C:\ drive where the user would typically look to find saved DOWNLOADS.
        //File outputFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "journaldev-image-downloaded.jpg");
        //getExternalFilesDir() -> It returns the path to files folder inside Android/data/data/your_package/ on your SD card. It is used to store any required files for your app (e.g. images downloaded from web or cache files). Once the app is uninstalled, any data stored in this folder is gone too.
        //you DO NOT NEED PERMISSIONS IN THE MANIFEST TO USE THIS//new File(getExternalFilesDir(null), "journaldev-image-downloaded.jpg");

        File folder = Util.getMyLocalDownloadFolder("bibles", this.context);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        File outputFile = new File(folder, fileName);
        OutputStream outputStream = new FileOutputStream(outputFile);
        long total = 0;
        boolean downloadComplete = false;
        //int totalFileSize = (int) (fileSize / (Math.pow(1024, 2)));

        while ((count = inputStream.read(data)) != -1) {

            total += count;
            int progress = (int) ((double) (total * 100) / (double) fileSize);


            updateNotification(progress);
            outputStream.write(data, 0, count);
            downloadComplete = true;
        }
        onDownloadComplete(downloadComplete, fileName);
        outputStream.flush();
        outputStream.close();
        inputStream.close();

    }

    //updates the process in the Notification bar
    private void updateNotification(int currentProgress) {
        notificationBuilder.setProgress(100, currentProgress, false);
        notificationBuilder.setContentText("Downloaded: " + currentProgress + "%");
        notificationManager.notify(0, notificationBuilder.build());
    }
    //called from downloadImage once completed -> will call sendProgressUpdate(trigger the event to the LocalBroadcastManager)
    private void onDownloadComplete(boolean downloadComplete, String fileName) {
        Log.d(TAG, "onDownloadComplete: ");
        //sendProgressUpdate(downloadComplete, fileName);

        notificationManager.cancel(0);
        notificationBuilder.setProgress(0, 0, false);
        notificationBuilder.setContentText("Download Complete");
        notificationBuilder.setSmallIcon(android.R.drawable.stat_sys_download_done);
        notificationManager.notify(0, notificationBuilder.build());

    }
    //Trigger the event to the LocalBroadcastManager
//    private void sendProgressUpdate(boolean downloadComplete, String fileName) {
//        Intent intent = new Intent(MainActivityRT.PROGRESS_UPDATE);
//        intent.putExtra("downloadComplete", downloadComplete);
//        intent.putExtra("fileName", fileName);
//        LocalBroadcastManager.getInstance(this.context).sendBroadcast(intent);
//    }

}
