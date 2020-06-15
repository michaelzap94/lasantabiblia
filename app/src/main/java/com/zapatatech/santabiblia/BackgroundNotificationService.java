package com.zapatatech.santabiblia;

import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.zapatatech.santabiblia.interfaces.retrofit.RetrofitInterface;
import com.zapatatech.santabiblia.interfaces.retrofit.RetrofitRESTendpointsService;
import com.zapatatech.santabiblia.models.Resource;
import com.zapatatech.santabiblia.utilities.RetrofitServiceGenerator;
import com.zapatatech.santabiblia.utilities.Util;

import org.reactivestreams.Subscriber;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
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

public class BackgroundNotificationService extends IntentService {
    private static final String TAG = "BackgroundNotificationS";
    //====================================================================================================
    public RetrofitRESTendpointsService resourcesService = RetrofitServiceGenerator.createServiceRx(RetrofitRESTendpointsService.class, null);
    //Dispose of anything inside it
    private CompositeDisposable disposable = new CompositeDisposable();
    //====================================================================================================
    public BackgroundNotificationService() {
        super("Service");
        Log.d(TAG, "BackgroundNotificationService: ");
    }

    private NotificationCompat.Builder notificationBuilder;
    private NotificationManager notificationManager;//DELETE BELOW
    //private NotificationManagerCompat notificationManager;

//    Inside the onHandleIntent we create the Notification first and then Retrofit instance.
//    Inside the Retrofit call, we do the download stuff and update the progress on the Notification.
//    To show a ProgressBar inside the Notification you just need to call setProgress on the Notification Builder instance.
//    sendProgressUpdate sends the update to the Broadcast Receiver.
    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "onHandleIntent: ");
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);//DELETE BELOW
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


        notificationBuilder = new NotificationCompat.Builder(this, "id")
                .setSmallIcon(android.R.drawable.stat_sys_download)
                .setContentTitle("Download")
                .setContentText("Downloading Image")
                .setDefaults(0)
                .setAutoCancel(true);
        notificationManager.notify(0, notificationBuilder.build());

        initRetrofit("https://unsplash.com/photos/YYW9shdLIwo/download?force=true", "journaldev-image-downloaded.jpg");

    }

    //start Retrofit setup
    private void initRetrofit(String fileURL, String fileName) {
        Log.d(TAG, "initRetrofit: " + fileURL);
        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://unsplash.com/").build();
        RetrofitInterface retrofitInterface = retrofit.create(RetrofitInterface.class);
        Call<ResponseBody> request = retrofitInterface.downloadImage("photos/YYW9shdLIwo/download?force=true");
        try {
            convertFile(request.execute().body(), fileName);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        //==========================================================================================
//        Single<ResponseBody> singleCall = resourcesService.downloadResourceRx(fileURL);
//        disposable.add(
//                singleCall
//                        .subscribeOn(Schedulers.newThread())//enables communication on new Thread background
//                        .observeOn(AndroidSchedulers.mainThread())//handle response on UI Thread
//                        .subscribeWith(new DisposableSingleObserver<ResponseBody>() {//new DisposableSingleObserve so we can call it inside disposable.add()
//
//                            @Override
//                            public void onSuccess(ResponseBody body) {
//                                //actual downloaded file
////                                try {
////                                    //convertFile(body, fileName);
////                                    writeResponseBodyToDisk(body, fileName);
////                                } catch (Exception e) {
////                                    e.printStackTrace();
////                                }
//                                //process conversion in the background IS NOT NEEDED SINCE the intent service is already running in the background
//                                new AsyncTask<Void, Void, Void>() {
//                                    @Override
//                                    protected Void doInBackground(Void... voids) {
//                                        boolean writtenToDisk = writeResponseBodyToDisk(BackgroundNotificationService.this, body, fileName);
//                                        Log.d(TAG, "file download was a success? " + writtenToDisk);
//                                        return null;
//                                    }
//                                }.execute();
//                            }
//
//                            @Override
//                            public void onError(Throwable e) {
//                                Log.d(TAG, "onError: " + e.getMessage());
//                            }
//                        })
//        );
        //==========================================================================================
        //USE FLATMAP
//        Observable<Response<ResponseBody>> observableCall = resourcesService.downloadFileByUrlRx(fileURL);
//        observableCall
//                .flatMap(this::saveToDiskRx)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(file -> {
//                    // file downloaded
//                    Log.d(TAG, "onCompleted");
//                    onDownloadComplete(true, fileName);
//                }, throwable -> {
//                    // error
//                    Log.d(TAG, "Error " + throwable.getMessage());
//                });
    }

    private Observable<File> saveToDiskRx(final Response<ResponseBody> response) {
        return Observable.create(new ObservableOnSubscribe<File>() {
            @Override
            public void subscribe(ObservableEmitter<File> subscriber) {
                try {
                    String header = response.headers().get("Content-Disposition");
                    String fileName = header.replace("attachment; filename=", "");

                    File folder = Util.getMyLocalDownloadFolder("bibles", BackgroundNotificationService.this);
                    if (!folder.exists()) {
                        folder.mkdirs();
                    }
                    File outputFile = new File(folder, fileName);

                    BufferedSink bufferedSink = Okio.buffer(Okio.sink(outputFile));
                    bufferedSink.writeAll(response.body().source());
                    bufferedSink.close();

                    subscriber.onNext(outputFile);
                    subscriber.onComplete();
                } catch (IOException e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                }
            }
        });
    }


    private boolean writeResponseBodyToDisk(Context context, ResponseBody body, String fileName) {
        try {
            //File futureStudioIconFile = new File(getExternalFilesDir(null) + File.separator + "Future Studio Icon.png");
            File folder = Util.getMyLocalDownloadFolder("bibles", this);
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

        File folder = Util.getMyLocalDownloadFolder("bibles", this);
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
        sendProgressUpdate(downloadComplete, fileName);

        notificationManager.cancel(0);
        notificationBuilder.setProgress(0, 0, false);
        notificationBuilder.setContentText("Image Download Complete");
        notificationManager.notify(0, notificationBuilder.build());

    }
    //Trigger the event to the LocalBroadcastManager
    private void sendProgressUpdate(boolean downloadComplete, String fileName) {
        Intent intent = new Intent(MainActivityRT.PROGRESS_UPDATE);
        intent.putExtra("downloadComplete", downloadComplete);
        intent.putExtra("fileName", fileName);
        LocalBroadcastManager.getInstance(BackgroundNotificationService.this).sendBroadcast(intent);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.d(TAG, "onTaskRemoved: ");
        notificationManager.cancel(0);
    }

//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        // We want this service to continue running until it is explicitly
//        // stopped, so return sticky.
//        return START_STICKY;
//    }

}
