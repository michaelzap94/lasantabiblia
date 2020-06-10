package com.zapatatech.santabiblia;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.zapatatech.santabiblia.interfaces.retrofit.RetrofitAuthService;
import com.zapatatech.santabiblia.interfaces.retrofit.RetrofitRESTendpointsService;
import com.zapatatech.santabiblia.models.APIError;
import com.zapatatech.santabiblia.models.Label;
import com.zapatatech.santabiblia.models.AuthInfo;
import com.zapatatech.santabiblia.utilities.CommonMethods;
import com.zapatatech.santabiblia.utilities.RetrofitErrorUtils;
import com.zapatatech.santabiblia.utilities.RetrofitServiceGenerator;
import com.zapatatech.santabiblia.utilities.Util;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Login extends AppCompatActivity {
    private static final String TAG = "Login";
    private TextInputLayout email;
    private TextInputLayout password;
    //FLAGS==========================
    public static final String FLAG_LANG = "Lang";
    private Menu menu;
    private String flagInSharedPref;
    private Drawable flag_gb;
    private Drawable flag_es;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //===================================================================================
        email = findViewById(R.id.login_input_email);
        password = findViewById(R.id.login_input_password);
        //===================================================================================

        //FLAGS================================================================================
        sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        flagInSharedPref = sp.getString(FLAG_LANG, "");

        flag_gb = ContextCompat.getDrawable(getApplicationContext(),R.drawable.flag_gb);
        flag_es = ContextCompat.getDrawable(getApplicationContext(),R.drawable.flag_es);
        //=====================================================================================

    }

    public void testGetLabels(String auth_token){
        Log.d(TAG, "testGetLabels: " + auth_token);
//        String API_BASE_URL = "https://api.github.com/";
//        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
//        Retrofit.Builder builder = new Retrofit.Builder()
//                        .baseUrl(API_BASE_URL)
//                        .addConverterFactory(GsonConverterFactory.create());
//        Retrofit retrofit = builder.client(httpClient.build()).build();
//        RetrofitGitHubClient client =  retrofit.create(RetrofitGitHubClient.class);

        // Create a very simple REST adapter which points the GitHub API endpoint.
        RetrofitRESTendpointsService client =  RetrofitServiceGenerator.createService(RetrofitRESTendpointsService.class, auth_token);
        // Fetch a list of the Github repositories.
        Call<List<Label>> call = client.getAllLabels();
        // Execute the call asynchronously. Get a positive or negative callback.
        call.enqueue(new Callback<List<Label>>() {
            @Override
            public void onResponse(Call<List<Label>> call, Response<List<Label>> response) {
                // The network call was a success and we got a response
                Log.d(TAG, "onResponse: " + response);

            }

            @Override
            public void onFailure(Call<List<Label>> call, Throwable t) {
                // the network call was a failure
                // TODO: handle error
                Log.d(TAG, "onFailure: " + t);
            }
        });
    }

    public boolean isValidInput(){
        boolean isValid = true;
        if (email.getEditText().getText().toString().trim().equalsIgnoreCase("")) {
            email.getEditText().setError("This field can not be blank");
            isValid = false;
        }
        if(!Util.validEmail(email.getEditText().getText().toString().trim())){
            email.getEditText().setError("Please, provide a valid email");
            isValid = false;
        }
        if (password.getEditText().getText().toString().trim().equalsIgnoreCase("")) {
            password.getEditText().setError("This field can not be blank");
            isValid = false;
        }
        return isValid;
    }

    public void startLogIn(View view) {
        //1st check if input is valid
        if(isValidInput()){
            String emailValue = email.getEditText().getText().toString().trim();
            String passwordValue = password.getEditText().getText().toString().trim();

            retrofitLogin(emailValue, passwordValue);
        } else {
            Toast.makeText(this, "Please, provide a valid input", Toast.LENGTH_SHORT).show();
        }
    }

    private void retrofitLogin(String emailValue, String passwordValue){
        RetrofitAuthService loginService = RetrofitServiceGenerator.createService(RetrofitAuthService.class, null);

        HashMap<String, Object> loginObject = new HashMap<>();
        loginObject.put("email", emailValue);
        loginObject.put("password", passwordValue);

        Call<AuthInfo> call = loginService.requestLogin(loginObject);
        call.enqueue(new Callback<AuthInfo >() {
            @Override
            public void onResponse(Call<AuthInfo> call, Response<AuthInfo> response) {
                if (response.isSuccessful()) {
                    // user object available
                    Log.d(TAG, "onResponse: success" + response.body());
                    if(response.body().getAccessToken() != null && response.body().getRefreshToken() != null ){
                        //Store tokens
                        CommonMethods.storeBothTokens(Login.this, response.body());
                        //Continue to App
                        CommonMethods.continueToApp(Login.this);
//                        testGetLabels(response.body().getAccessToken());
                    } else {
                        String error = "Sorry, something went wrong";
                        if(response.body().getDetail() != null) {
                            error = response.body().getDetail();
                        } else if (response.body().getError() != null) {
                            error = response.body().getError().toString();
                        }
                        Toast.makeText(Login.this, error, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // parse the response body …
                    APIError error = RetrofitErrorUtils.parseError(response);
                    // … and use it to show error information
                    Toast.makeText(Login.this, error.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AuthInfo> call, Throwable t) {
                // something went completely south (like no internet connection)
                Log.d("onFailure Error", t.getMessage());
            }
        });
    }

    public void goToSignUp(View view){
        Intent i = new Intent(Login.this, SignUp.class);
        startActivity(i);
    }

    public void continueOffline(View view){
        int newStatus = CommonMethods.updateUserStatus(this, CommonMethods.USER_OFFLINE);
        if(newStatus == CommonMethods.USER_OFFLINE){
            Intent i = new Intent(Login.this, MainActivity.class);
            startActivity(i);
            finish();
        } else {
            Toast.makeText(this, "Status not updated", Toast.LENGTH_SHORT).show();
        }
    }

    //LANGUAGE===============================================================
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_languages, menu);

        //you need menu.findItem as the Menu has not been fully inflated yet. so findViewById will not work.
        MenuItem item_gb = menu.findItem(R.id.lang_en);
        MenuItem item_es = menu.findItem(R.id.lang_es);
        if(flagInSharedPref.length()>0) {

            switch (flagInSharedPref) {
                case "en":
                    menu.getItem(0).setIcon(flag_gb);
                    item_gb.setChecked(true);
                    break;
                case "es":
                    menu.getItem(0).setIcon(flag_es);
                    item_es.setChecked(true);
                    break;

            }
        }else{
            //default language is English for now
            sp.edit().putString(FLAG_LANG,"en").apply();
            menu.getItem(0).setIcon(flag_gb);
            item_gb.setChecked(true);
        }


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.lang_en:
                sp.edit().putString(FLAG_LANG,"en").apply();
                if (item.isChecked()){
                    item.setChecked(false);
                }else{
                    item.setChecked(true);
                    setLocale("en");
                }
//                menu.getItem(0).setIcon(flag_gb);
                return true;
            case R.id.lang_es:
                sp.edit().putString(FLAG_LANG,"es").apply();
                if (item.isChecked()){
                    item.setChecked(false);
                }else{
                    item.setChecked(true);
                    setLocale("es");
                }
//                menu.getItem(0).setIcon(flag_es);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void setLocale(String lang) {
        //change language files===================
        Locale myLocale = new Locale(lang);
        Locale.setDefault(myLocale);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        //start Intent===================
        Intent refresh = new Intent(this, LanguageChangeLoader.class);
        refresh.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);//CLEAR ALL ACTIVITIES
        startActivity(refresh);
    }

}