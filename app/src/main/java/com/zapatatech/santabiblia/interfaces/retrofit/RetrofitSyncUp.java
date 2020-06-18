package com.zapatatech.santabiblia.interfaces.retrofit;

import com.google.gson.JsonObject;
import com.zapatatech.santabiblia.models.Label;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

public interface RetrofitSyncUp {
    //JWT token will be passed in the HEADER: Authorization: JWT token
    //JSON=============================================================================================
//    POTENTIAL ERROR if response from server is Array: Expected a com.google.gson.JsonObject but was com.google.gson.JsonArray
//    @GET("users/{user}")
//    Call<JsonObject> getUser(@Path("user") String user);
//    POTENTIAL ERROR  if response from server is Object: Expected a com.google.gson.JsonArray but was com.google.gson.JsonObject
//    @GET("users/{user}/events")
//    Call<JsonArray> listEvents(@Path("user") String user);
    //==================================================================================================
    // Please note, that we're specifying ResponseBody as return type. You should not use anything else here,
    // otherwise Retrofit will try to parse and convert it, which doesn't make sense when you're downloading a file.
    // ONE SOLUTION FOR THIS WOULD BE TO create a Retrofit service that doesn't include GsonFactoryConverter

    //GET THE SERVER VERSION and check on the device the action we can take.
    @GET("syncup/check/")
    Call<ResponseBody> checkServerState();//you will need to use response.body().string() to convert it to JSON

    @GET("syncup/check/")
    Call<JsonObject> checkServerStateJSON();//this will be json object already

    @GET("syncup/check/")
    Single<JsonObject> getServerStateJSONRx();//this will be json object already

    @FormUrlEncoded
    @POST("syncup/check/")
    Single<JsonObject> checkServerStateJSONRx(@Field("version") int version);//this will be json object already

    //SEND THE SERVER VERSION THE DEVICE HAS AND CHECK ON THE SERVER SIDE the action we can take.
    @FormUrlEncoded //application/x-www-form-urlencoded -> key1=value1&key2=value2 NOT JSON
    @POST("syncup/check/")
    Call<ResponseBody> checkServerState( @Field("version") int version);
    //==================================================================================================
    @GET("auth/test/labels/")
    Call<List<Label>> getAllLabels();

    //DOWNLOADING FILE==================================================================================
    //Please note, that we're specifying ResponseBody as return type. You should not use anything else here,
    // otherwise Retrofit will try to parse and convert it, which doesn't make sense when you're downloading a file.

    //If you’re downloading a large file, Retrofit would try to move the entire file into memory. In order to avoid that, we’ve to add a special annotation to the request declaration:
    @GET("http://192.168.0.14:8000{path}")
    @Streaming
    Call<ResponseBody> downloadResource(@Path(value = "path", encoded = true) String path);

    @GET()
    @Streaming
    Call<ResponseBody> downloadResourceWithUrl(@Url String fileUrl);


    // Retrofit 2 GET request for rxjava
    @Streaming
    @GET
    Observable<Response<ResponseBody>> downloadFileByUrlRx(@Url String fileUrl);

    //if using @Streaming, you need to process the file on the BACKGROUND
    @GET()
    @Streaming
    Single<ResponseBody> downloadResourceRx(@Url String fileUrl);

    //===============================================================================================================

//    // option 1: a resource relative to your base URL
//    @GET("/resource/example.zip")
//    Call<ResponseBody> downloadFileWithFixedUrl();
//    // option 2: using a dynamic URL
//    @GET
//    Call<ResponseBody> downloadFileWithDynamicUrlSync(@Url String fileUrl);

    //==================================================

    //Syntax sugar
    //GET
    //Single<Object> getObject(@Url String urlString), this way you specify your URL in the class implementing this
//    @POST("DevTides/countries/master/countriesV2.json")
//    Single<List<CountryModel>> postCountries();//implement a POST to the specified API

    //we need a BASE_URL for this, specified in the CountriesService
}
