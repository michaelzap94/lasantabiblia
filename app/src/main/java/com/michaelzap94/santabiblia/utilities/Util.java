package com.michaelzap94.santabiblia.utilities;

import android.content.Context;
import android.view.View;

public class Util {
    public static int dpAsPixels(Context context, int sizeInDp){
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (sizeInDp*scale + 0.5f);
    }
    public static void setPaddingBottom(Context context, View v, int paddingDp) {
        int paddingPx = dpAsPixels(context, paddingDp);
        v.setPadding(v.getPaddingLeft(), v.getPaddingTop(), v.getPaddingRight(), paddingPx);
    }
}
