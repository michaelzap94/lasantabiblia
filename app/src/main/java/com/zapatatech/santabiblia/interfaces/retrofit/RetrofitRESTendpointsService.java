package com.zapatatech.santabiblia.interfaces.retrofit;

import com.zapatatech.santabiblia.models.Label;
import com.zapatatech.santabiblia.models.Resource;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

//These APIS await any requests at endpoint http://http://192.168.0.14/8000/auth and requires JWT authentication to get user data in response.
public interface RetrofitRESTendpointsService {
    @GET("auth/test/labels/")
    Call<List<Label>> getAllLabels();

    //RESOURCES==================================================
    //Substitute {path} with this value
    @GET("/resources/all/")
    Single<List<Resource>> getResourcesAll();
    //Substitute {path} with this value
    @GET("/resources/extra/")
    Single<List<Resource>> getResourcesExtra();
    //Substitute {path} with this value
    @GET("/resources/type/{path}/")
    Single<List<Resource>> getResourcesByType(@Path("path") String path );
    //Substitute {path} with this value
    @GET("/resources/language/{lang}/")
    Single<List<Resource>> getResourcesByLang(@Path("lang") String lang );

    //DOWNLOADING FILE==================================================================================
    //Please note, that we're specifying ResponseBody as return type. You should not use anything else here,
    // otherwise Retrofit will try to parse and convert it, which doesn't make sense when you're downloading a file.


    //If you’re downloading a large file, Retrofit would try to move the entire file into memory. In order to avoid that, we’ve to add a special annotation to the request declaration:
    @GET()
    @Streaming
    Call<ResponseBody> downloadResource(@Url String fileUrl);


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
