package com.zapatatech.santabiblia.utilities;

import android.util.Log;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;


public class RetrofitAuthInterceptor implements Interceptor {
    private static final String TAG = "RetrofitAuthInterceptor";
    private String authToken;

    public RetrofitAuthInterceptor(String token) {
        this.authToken = token;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();

        Request.Builder builder = original.newBuilder()
                .header("Authorization", "Bearer " + authToken);
        Request request = builder.build();
        return chain.proceed(request);
    }
}