package com.zapatatech.santabiblia.utilities;

import android.content.Context;
import android.util.Patterns;
import android.view.View;

import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

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
    //display a spiner while image is loading
    public static CircularProgressDrawable getProgressDrawable(Context context) {
        CircularProgressDrawable progressDrawable = new CircularProgressDrawable(context);
        progressDrawable.setStrokeWidth(10f);
        progressDrawable.setCenterRadius(50f);
        progressDrawable.start();
        return progressDrawable;
    }
}
