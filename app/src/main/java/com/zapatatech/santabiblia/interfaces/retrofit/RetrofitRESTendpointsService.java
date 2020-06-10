package com.zapatatech.santabiblia.interfaces.retrofit;

import com.zapatatech.santabiblia.models.Label;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
//These APIS await any requests at endpoint http://http://192.168.0.14/8000/auth and requires JWT authentication to get user data in response.
public interface RetrofitRESTendpointsService {
    @GET("auth/test/labels/")
    Call<List<Label>> getAllLabels();

    //Substitute {user} with this value
//    @GET("/users/{user}")
//    Call<List<GitHubRepo>> reposForUser( @Path("user") String user );
}
