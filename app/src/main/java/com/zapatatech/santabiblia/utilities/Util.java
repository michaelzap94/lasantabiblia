package com.zapatatech.santabiblia.utilities;

import android.content.Context;
import android.os.Environment;
import android.util.Patterns;
import android.view.View;

import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import java.io.File;
import java.util.regex.Pattern;

public class Util {
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

    public static File getMyLocalDownloadFolder(String relativePath, Context context) {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            //context.getExternalFilesDir(null) -> /storage/emulated/0/Android/data/com.zapatatech.santabiblia/files
            //context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) -> /storage/emulated/0/Android/data/com.zapatatech.santabiblia/files/Download
            return new File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), relativePath);
        } else {
            return new File(context.getFilesDir(), Environment.DIRECTORY_DOWNLOADS + File.separator + relativePath);
        }
    }
    //display a spiner while image is loading
    public static CircularProgressDrawable getProgressDrawable(Context context) {
        CircularProgressDrawable progressDrawable = new CircularProgressDrawable(context);
        progressDrawable.setStrokeWidth(10f);
        progressDrawable.setCenterRadius(50f);
        progressDrawable.start();
        return progressDrawable;
    }
}
