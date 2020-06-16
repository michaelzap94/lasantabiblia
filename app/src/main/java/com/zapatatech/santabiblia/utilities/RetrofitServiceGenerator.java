package com.zapatatech.santabiblia.utilities;

import android.text.TextUtils;
import android.util.Log;

import okhttp3.OkHttpClient;
//import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

// Why we use static fields and methods within the ServiceGenerator class.
// Actually, it has one simple reason: we want to use the same objects (OkHttpClient, Retrofit, …) throughout the app to just open one socket connection that handles all the request and responses including caching and many more.
// It’s common practice to just use one OkHttpClient instance to reuse open socket connections. That means, we either need to inject the OkHttpClient to this class via dependency injection or use a static field.
// As you can see, we chose to use the static field. And because we use the OkHttpClient throughout this class, we need to make all fields and methods static.
// Additionally to speeding things up, we can save a little bit of valuable memory on mobile devices when we don't have to recreate the same objects over and over again.

public class RetrofitServiceGenerator {
    private static final String TAG = "RetrofitServiceGenerato";
    public static final String BASE_URL = "http://192.168.0.14:8000";
    // Create a new REST client with the given API base url
    private static Retrofit.Builder builder = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create());

    private static Retrofit.Builder builderWithRx = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())//GsonConverterFactory will convert it to Object Type
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create());//RxJava2CallAdapterFactory converts List into a Single Observable of the List,
            // this will allow REtrofit to convert JSON from the api into a Single element which was specified to be List<CountryModel> in the CountriesApi Interface

    public static Retrofit retrofitWithRx = builderWithRx.build();
    public static Retrofit retrofit = builder.build();

    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

    //ADD LOGGING OF REQUESTS AND RESPONSES 2)=================================================
    private static HttpLoggingInterceptor logging = new HttpLoggingInterceptor()
            .setLevel(HttpLoggingInterceptor.Level.BODY);//USE Level.NONE for RELEASE/PRODUCTION
    //=====================================================================================
    /**
     *
     * @param serviceClass - annotated interface for API requests
     * @param <S> - Class
     * @return - creates and returns a usable client from serviceClass
     */
    public static <S> S createService(Class<S> serviceClass) {
        //return createService(serviceClass, null, null);//for basic authentication
        return createService(serviceClass, null);
    }

    //BASIC AUTHENTICATION
//    public static <S> S createService(Class<S> serviceClass, String email, String password) {
//        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
//            String authToken = Credentials.basic(email, password);
//            return createService(serviceClass, authToken);
//        }
//        return createService(serviceClass, null);
//    }

    //OAUTH AUTHENTICATION USING clientId and clientSecret
//    public static <S> S createService(Class<S> serviceClass, String clientId, String clientSecret) {
//        if (!TextUtils.isEmpty(clientId)
//                && !TextUtils.isEmpty(clientSecret)) {
//            String authToken = Credentials.basic(clientId, clientSecret);
//            return createService(serviceClass, authToken);
//        }
//        return createService(serviceClass, null, null);
//    }

    public static <S> S createService( Class<S> serviceClass, final String authToken) {
        mAddLogging();
        if (!TextUtils.isEmpty(authToken)) {
            RetrofitAuthInterceptor interceptor = new RetrofitAuthInterceptor(authToken);
            //creates the custom Interceptor using the authToken
            if (!httpClient.interceptors().contains(interceptor)) {
                httpClient.addInterceptor(interceptor);//add HTTP header field for Authorization: Bearer 12345
                builder.client(httpClient.build());
                retrofit = builder.build();
            }
        }

        return retrofit.create(serviceClass);
    }
    //=====================================================================================

    private static void mAddLogging(){
        //ADD LOGGING OF REQUESTS AND RESPONSES 1)============================================
        //Check if the logging interceptor is already present
        if (!httpClient.interceptors().contains(logging)) {
            httpClient.addInterceptor(logging);//if not, then add it
            builder.client(httpClient.build());
            //Make sure to not build the retrofit object on every createService call
            retrofit = builder.build();
        }
        //=====================================================================================
    }

    //=====================================================================================
    /**
     *
     * @param serviceClass - annotated interface for API requests
     * @param <S> - Class
     * @return - creates and returns a usable client from serviceClass
     */
    public static <S> S createServiceRx(Class<S> serviceClass) {
        //return createService(serviceClass, null, null);//for basic authentication
        return createServiceRx(serviceClass, null);
    }
    public static <S> S createServiceRx( Class<S> serviceClass, final String authToken) {
        //mAddLogging();
        if (!TextUtils.isEmpty(authToken)) {
            RetrofitAuthInterceptor interceptor = new RetrofitAuthInterceptor(authToken);
            //creates the custom Interceptor using the authToken
            if (!httpClient.interceptors().contains(interceptor)) {
                httpClient.addInterceptor(interceptor);//add HTTP header field for Authorization: Bearer 12345
                builderWithRx.client(httpClient.build());
                retrofitWithRx = builderWithRx.build();
            }
        }

        return retrofitWithRx.create(serviceClass);
    }
}