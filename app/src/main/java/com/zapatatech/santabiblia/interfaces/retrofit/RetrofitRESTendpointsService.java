package com.zapatatech.santabiblia.interfaces.retrofit;

import com.zapatatech.santabiblia.models.Label;
import com.zapatatech.santabiblia.models.Resource;

import java.util.List;

import io.reactivex.Single;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

//These APIS await any requests at endpoint http://http://192.168.0.14/8000/auth and requires JWT authentication to get user data in response.
public interface RetrofitRESTendpointsService {
    @GET("auth/test/labels/")
    Call<List<Label>> getAllLabels();

    //Substitute {path} with this value
    @GET("/resources/all")
    Single<List<Resource>> getResourcesAll();

    //Substitute {path} with this value
    @GET("/resources/type/{path}")
    Single<List<Resource>> getResourcesByType(@Path("path") String path );

    //Substitute {path} with this value
    @GET("/resources/language/{lang}")
    Single<List<Resource>> getResourcesByLang(@Path("lang") String lang );

    //Syntax sugar
    //GET
    //Single<Object> getObject(@Url String urlString), this way you specify your URL in the class implementing this

//    @POST("DevTides/countries/master/countriesV2.json")
//    Single<List<CountryModel>> postCountries();//implement a POST to the specified API

    //we need a BASE_URL for this, specified in the CountriesService
}
