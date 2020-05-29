package com.michaelzap94.santabiblia.utilities;

import android.content.Context;

public class Util {
    public static int dpAsPixels(Context context, int sizeInDp){
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (sizeInDp*scale + 0.5f);
    }
}
