package com.zapatatech.santabiblia.utilities;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.util.Patterns;
import android.view.View;

import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import java.io.File;
import java.util.regex.Pattern;

public class Util {
    private static final String TAG = "Util";
    public static int dpAsPixels(Context context, int sizeInDp){
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (sizeInDp*scale + 0.5f);
    }
    public static void setPaddingBottom(Context context, View v, int paddingDp) {
        int paddingPx = dpAsPixels(context, paddingDp);
        v.setPadding(v.getPaddingLeft(), v.getPaddingTop(), v.getPaddingRight(), paddingPx);
    }
    public static boolean validEmail(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }
    //display a spiner while image is loading
    public static CircularProgressDrawable getProgressDrawable(Context context) {
        CircularProgressDrawable progressDrawable = new CircularProgressDrawable(context);
        progressDrawable.setStrokeWidth(10f);
        progressDrawable.setCenterRadius(50f);
        progressDrawable.start();
        return progressDrawable;
    }

    //==================================================================================
    public static boolean isResourceInstalled(Context context, String resourceName){
        //dbName should be unique in both the Server and the App
        for (String dbName: context.databaseList()) {
            if(dbName.equals(resourceName)) {
                return true;
            }
        }
        return false;
    }
    public static boolean isResourceFullyInstalled(Context context, String resourceName, int fileSize){
        //dbName should be unique in both the Server and the App
        File file = context.getDatabasePath(resourceName);
        if(file.exists()){
            Log.d(TAG, "isResourceFullyInstalled: " + file.getName() + ": " + file.length());
            if(file.length() >= fileSize){
                return true;
            } else {
                return false;
            }
        }
        else{
            return false;
        }
    }
    public static File getMyLocalDownloadFolder(String relativePath, Context context) {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            //context.getExternalFilesDir(null) -> /storage/emulated/0/Android/data/com.zapatatech.santabiblia/files
            //context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) -> /storage/emulated/0/Android/data/com.zapatatech.santabiblia/files/Download
            return new File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), relativePath);
        } else {
            return new File(context.getFilesDir(), Environment.DIRECTORY_DOWNLOADS + File.separator + relativePath);
        }
    }
    public static File getMyLocalDownloadDBFolder(String relativePath, Context context) {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            //context.getExternalFilesDir(null) -> /storage/emulated/0/Android/data/com.zapatatech.santabiblia/files
            //context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) -> /storage/emulated/0/Android/data/com.zapatatech.santabiblia/files/Download
            return new File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), relativePath);
        } else {
            return new File(context.getFilesDir(), Environment.DIRECTORY_DOWNLOADS + File.separator + relativePath);
        }
    }

    public static String getDBPath(Context context){
        return "/data/data/" + context.getPackageName() + "/" + "databases/";
    }

}
