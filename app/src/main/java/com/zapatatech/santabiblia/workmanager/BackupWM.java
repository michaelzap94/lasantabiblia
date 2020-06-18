//package com.zapatatech.santabiblia.workmanager;
//
//import android.app.NotificationChannel;
//import android.app.NotificationManager;
//import android.content.Context;
//import android.database.sqlite.SQLiteDatabase;
//import android.graphics.Color;
//import android.os.Build;
//import android.util.Log;
//
//import androidx.annotation.NonNull;
//import androidx.core.app.NotificationCompat;
//import androidx.work.Data;
//import androidx.work.Worker;
//import androidx.work.WorkerParameters;
//
//import com.zapatatech.santabiblia.interfaces.retrofit.RetrofitRESTendpointsService;
//import com.zapatatech.santabiblia.retrofit.RetrofitServiceGenerator;
//import com.zapatatech.santabiblia.utilities.Util;
//
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//
//import okhttp3.ResponseBody;
//import retrofit2.Call;
//
//import static android.content.Context.MODE_PRIVATE;
//
//public class DownloadResourceWM  extends Worker {
//    private static final String TAG = "DownloadResourceWM";
//    //====================================================================================================
//    public RetrofitRESTendpointsService resourcesService = RetrofitServiceGenerator.createService(RetrofitRESTendpointsService.class, null);
//    //public RetrofitRESTendpointsService resourcesService = RetrofitServiceGenerator.createServiceRx(RetrofitRESTendpointsService.class, null);
//    //====================================================================================================
//    private NotificationCompat.Builder notificationBuilder;
//    private NotificationManager notificationManager;//DELETE BELOW
//    private final Context context;
//
//
//    public DownloadResourceWM(
//            @NonNull Context context,
//            @NonNull WorkerParameters params) {
//        super(context, params);
//        this.context = context;
////        String fileName = getInputData().getString("RESOURCE_FILENAME");
////        // Set initial progress to 0
////        Data output = new Data.Builder()
////                .putInt("progress", 0)
////                .putString("fileNameProcessing", fileName)
////                .build();
////        setProgressAsync(new Data.Builder().putInt("progress", 0).build());
//    }
//
//    //    This method is called on a background thread - you are required to synchronously do your work and return the ListenableWorker.
////    Result from this method. Once you return from this method, the Worker is considered to have finished what its doing and will be destroyed. If you need to do your work asynchronously on a thread of your own choice, see ListenableWorker
//    @Override
//    public Result doWork() {
//        String resourceUrl = getInputData().getString("RESOURCE_URL");
//        String fileName = getInputData().getString("RESOURCE_FILENAME");
//        if(fileName == null || resourceUrl == null) {
//            return Result.failure();
//        }
//        Data processingData = new Data.Builder()
//                .putInt("progress", 0)
//                .putString("fileNameProcessing", fileName)
//                .build();
//        setProgressAsync(processingData);
//
//        startNotificationBar(fileName);
//        //only attempt to retry 2 times
//        if (getRunAttemptCount() > 2) {
//            Log.d(TAG, "too many failed attemp, give up");
//            notificationManager.cancel(0);
//            return Result.failure();
//        }
//        // Do the work here--in this case, upload the images.
//        boolean result = initRetrofit(resourceUrl, fileName);
//        if(result) {
//            Data output = new Data.Builder()
//                    .putBoolean("downloadComplete", true)
//                    .putString("fileName", fileName)
//                    .build();
//            return Result.success(output);
//        } else {
//            notificationManager.cancel(0);
//            return Result.failure();
//        }
//    }
//
//
//    @Override
//    public void onStopped() {
//        super.onStopped();
//        notificationManager.cancel(0);
//    }
//
//    public void startNotificationBar(String fileName){
//        notificationManager = (NotificationManager) this.context.getSystemService(Context.NOTIFICATION_SERVICE);//DELETE BELOW
//        //notificationManager = (NotificationManagerCompat) NotificationManagerCompat.from(getApplicationContext());
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationChannel notificationChannel = new NotificationChannel("id", "an", NotificationManager.IMPORTANCE_LOW);
//
//            notificationChannel.setDescription("no sound");
//            notificationChannel.setSound(null, null);
//            notificationChannel.enableLights(false);
//            notificationChannel.setLightColor(Color.BLUE);
//            notificationChannel.enableVibration(false);
//            notificationManager.createNotificationChannel(notificationChannel);//DELETE 2 BELOW
//            //NotificationManager notificationManagerOreo = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//            //notificationManagerOreo.createNotificationChannel(notificationChannel);
//        }
//
//        notificationBuilder = new NotificationCompat.Builder(this.context, "id")
//                .setSmallIcon(android.R.drawable.stat_sys_download)
//                .setContentTitle("Download")
//                .setContentText("Downloading " + fileName)
//                .setDefaults(0)
//                .setAutoCancel(true);
//        notificationManager.notify(0, notificationBuilder.build());
//
//    }
//
//    //start Retrofit setup, synchronous since this is running in the background already
//    private boolean initRetrofit(String fileURL, String fileName) {
//        Log.d(TAG, "initRetrofit: " + fileURL);
//        Call<ResponseBody> request = resourcesService.downloadResource(fileURL);
//        try {
//            //convertFile(request.execute().body(), fileName);
//            boolean result = writeResponseBodyToDisk(request.execute().body(), fileName);
//            if(result){
//                return true;
//            } else {
//                //if for some reason the process did not CREATE the db file, delete anything we have written to the disk so far;
//                boolean deleteFileResult = deleteFile(fileName);
//                Log.d(TAG, "deleteDB: " + deleteFileResult);
//                return false;
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//            return false;
//        }
//
//    }
//
//    private boolean writeResponseBodyToDisk(ResponseBody body, String fileName) {
//        try {
//            //File futureStudioIconFile = new File(getExternalFilesDir(null) + File.separator + "Future Studio Icon.png");
////            File folder = Util.getMyLocalDownloadFolder("bibles", this.context);
////            if (!folder.exists()) {
////                folder.mkdirs();
////            }
//            //WRITE TO A TEMPORAL FILE
//            File outputFile = new File(Util.getDBPath(this.context), getTempFileName(fileName));
//
//            //THEN OVERWRITE THE FLE:
//            InputStream inputStream = null;
//            OutputStream outputStream = null;
//            try {
//                byte[] fileReader = new byte[4096];
//                long fileSize = body.contentLength();
//                long fileSizeDownloaded = 0;
//                inputStream = body.byteStream();
//                outputStream = new FileOutputStream(outputFile);
//                boolean downloadComplete = false;
//                while (true) {
//                    int read = inputStream.read(fileReader);
//                    if (read == -1) {
//                        break;
//                    }
//
//                    outputStream.write(fileReader, 0, read);
//                    fileSizeDownloaded += read;
//
//                    int progress = (int) ((double) (fileSizeDownloaded * 100) / (double) fileSize);
//                    updateProgress(progress, fileName);
//                    //Log.d(TAG, "file download: " + fileSizeDownloaded + " of " + fileSize);
//                    if(fileSizeDownloaded == fileSize) downloadComplete = true;
//                }
//                boolean createDBSuccess = createDB(downloadComplete, fileName);
//                //set notification bar as download complete
//                onDownloadComplete(createDBSuccess, fileName);
//                outputStream.flush();
//                return createDBSuccess;
//            } catch (Exception e) {
//                Log.d(TAG, "writeResponseBodyToDisk: Exception inner" + e.getMessage());
//                return false;
//            } finally {
//                if (inputStream != null) inputStream.close();
//                if (outputStream != null) outputStream.close();
//            }
//        } catch (Exception e) {
//            Log.d(TAG, "writeResponseBodyToDisk: Exception " + e.getMessage());
//            return false;
//        }
//    }
//
//    //updates the process in the Notification bar
//    private void updateProgress(int currentProgress, String fileName) {
//        //Log.d(TAG, "updateProgress: " + currentProgress);
//        //workmanager specific-----------------------------------------------------------------
//        Data processingData = new Data.Builder()
//                .putInt("progress", currentProgress)
//                .putString("fileNameProcessing", fileName)
//                .build();
//        setProgressAsync(processingData);
//        //------------------------------------------------------------------------------------
//        notificationBuilder.setProgress(100, currentProgress, false);
//        notificationBuilder.setContentText("Downloaded: " + currentProgress + "%");
//        notificationManager.notify(0, notificationBuilder.build());
//    }
//    //called from downloadImage once completed -> will call sendProgressUpdate(trigger the event to the LocalBroadcastManager)
//    private void onDownloadComplete(boolean createDBSuccess, String fileName) {
//        Log.d(TAG, "createDBSuccess: " + createDBSuccess);
//        notificationManager.cancel(0);
//        notificationBuilder.setProgress(0, 0, false);
//        notificationBuilder.setContentText((createDBSuccess) ? "Download Complete" : "Download Failed");
//        notificationBuilder.setSmallIcon(android.R.drawable.stat_sys_download_done);
//        notificationManager.notify(0, notificationBuilder.build());
//
//    }
//    private boolean createDB(boolean downloadComplete, String fileName) {
//        Log.d(TAG, "createDB: " + downloadComplete);
//        if(downloadComplete) {
//            //Rename file:
//            boolean renameSuccess = renameFile(fileName);
//            if(renameSuccess){
//                //CREATE empty db:
//                try {
//                    SQLiteDatabase db = this.context.openOrCreateDatabase(fileName, MODE_PRIVATE, null);
//                    db.close();
//                    return true;
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    return false;
//                }
//            } else {
//                return false;
//            }
//        } else {
//            return false;
//        }
//    }
//
//    private boolean renameFile(String fileName){
//        try{
//            File from      = new File(Util.getDBPath(this.context), getTempFileName(fileName));
//            File to        = new File(Util.getDBPath(this.context), fileName);
//            return from.renameTo(to);
//        } catch (Exception e) {
//            Log.e(TAG, "renameFile: ", e);
//            return false;
//        }
//    }
//
//    private boolean deleteFile(String fileName) {
//        boolean deleteResult = false;
//        File outputFile = new File(Util.getDBPath(this.context), getTempFileName(fileName));
//        if(outputFile.exists()){
//            deleteResult = outputFile.delete();
//            Log.d(TAG, "deleteDB: file: " + deleteResult);
//        }
//        return deleteResult;
//        //return this.context.deleteDatabase(fileName);
//    }
//
//    private String getTempFileName(String fileName){
//        return fileName + "-temp";
//    }
//
//}
