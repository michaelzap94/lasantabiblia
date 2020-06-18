package com.zapatatech.santabiblia.workmanager;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.zapatatech.santabiblia.interfaces.retrofit.RetrofitSyncUp;
import com.zapatatech.santabiblia.retrofit.RetrofitServiceGenerator;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;

public class OverrideServerWM extends Worker {
    private static final String TAG = "OverrideServerWM";

    public OverrideServerWM(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        return null;
    }
}
