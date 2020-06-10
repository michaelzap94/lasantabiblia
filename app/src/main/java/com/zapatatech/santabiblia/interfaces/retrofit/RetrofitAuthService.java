package com.zapatatech.santabiblia.interfaces.retrofit;

import com.zapatatech.santabiblia.models.AuthInfo;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RetrofitAuthService {
    //Instead of classes we can also directly use the HashMap<String, Object> to send body parameters for example
    @POST("/auth/api/jwt/login/") //The server needs the URL to end with a /
    Call<AuthInfo> requestLogin(@Body HashMap<String, Object> body);

    @POST("/auth/api/jwt/signup/") //The server needs the URL to end with a /
    Call<AuthInfo> requestSignUp(@Body HashMap<String, Object> body);

    @POST("/auth/api/jwt/logout/") //The server needs the URL to end with a /
    Call<AuthInfo> requestLogOut(@Body HashMap<String, Object> body);

    //SOCIAL ENDPOINTS
    @POST("/auth/api/jwt/login/social/") //The server needs the URL to end with a /
    Call<AuthInfo> requestLoginSocial(@Body HashMap<String, Object> body);
}
