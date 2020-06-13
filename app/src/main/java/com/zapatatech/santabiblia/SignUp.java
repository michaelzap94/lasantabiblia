package com.zapatatech.santabiblia;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.zapatatech.santabiblia.interfaces.retrofit.RetrofitAuthService;
import com.zapatatech.santabiblia.models.APIError;
import com.zapatatech.santabiblia.models.AuthInfo;
import com.zapatatech.santabiblia.utilities.CommonMethods;
import com.zapatatech.santabiblia.utilities.RetrofitErrorUtils;
import com.zapatatech.santabiblia.utilities.RetrofitServiceGenerator;
import com.zapatatech.santabiblia.utilities.Util;

import java.io.IOException;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.HttpException;
import retrofit2.Response;

import static android.content.Intent.FLAG_ACTIVITY_REORDER_TO_FRONT;

public class SignUp extends AppCompatActivity {
    private static final String TAG = "SignUp";
    private TextInputLayout name;
    private TextInputLayout email;
    private TextInputLayout password1;
    private TextInputLayout password2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        //===================================================================================
        name = findViewById(R.id.signup_input_name);
        email = findViewById(R.id.signup_input_email);
        password1 = findViewById(R.id.signup_input_password1);
        password2 = findViewById(R.id.signup_input_password2);
        //===================================================================================
        //name.getEditText().setText(nameVal);

    }

    public boolean isValidInput(){
        boolean isValid = true;
        if (name.getEditText().getText().toString().trim().equalsIgnoreCase("")) {
            name.getEditText().setError("This field can not be blank");
            isValid = false;
        }
        if (email.getEditText().getText().toString().trim().equalsIgnoreCase("")) {
            email.getEditText().setError("This field can not be blank");
            isValid = false;
        }
        if(!Util.validEmail(email.getEditText().getText().toString().trim())){
            email.getEditText().setError("Please, provide a valid email");
            isValid = false;
        }
        if (password1.getEditText().getText().toString().trim().equalsIgnoreCase("")) {
            password1.getEditText().setError("This field can not be blank");
            isValid = false;
        }
        if (password2.getEditText().getText().toString().trim().equalsIgnoreCase("")) {
            password2.getEditText().setError("This field can not be blank");
            isValid = false;
        }
        if(!password1.getEditText().getText().toString().trim().equalsIgnoreCase(password2.getEditText().getText().toString().trim())){
            password2.getEditText().setError("Passwords must match");
            isValid = false;
        }
        return isValid;
    }

    public void startSignUp(View view){
        //1st check if input is valid
        if(isValidInput()){
            String nameValue = name.getEditText().getText().toString().trim();
            String emailValue = email.getEditText().getText().toString().trim();
            String password1Value = password1.getEditText().getText().toString().trim();
            String password2Value = password2.getEditText().getText().toString().trim();
            retrofitSignUp(nameValue, emailValue, password1Value, password2Value);
        } else {
            Toast.makeText(this, "Please, provide a valid input", Toast.LENGTH_SHORT).show();
        }
    }

    private void retrofitSignUp(String nameValue, String emailValue, String password1Value, String password2Value){
        RetrofitAuthService authService = RetrofitServiceGenerator.createService(RetrofitAuthService.class, null);

        HashMap<String, Object> signUpObject = new HashMap<>();
        signUpObject.put("fullname", nameValue);
        signUpObject.put("email", emailValue);
        signUpObject.put("password", password1Value);
        signUpObject.put("password2", password2Value);

        Call<AuthInfo> call = authService.requestSignUp(signUpObject);

        // Set up progress before call
        ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage("Signing you up...");
        mProgressDialog.show();
        call.enqueue(new Callback<AuthInfo >() {
            @Override
            public void onResponse(Call<AuthInfo> call, Response<AuthInfo> response) {
                if (response.isSuccessful()) {
                    if(response.body().getAccessToken() != null && response.body().getRefreshToken() != null ){
                        //Store tokens
                        CommonMethods.storeBothTokens(SignUp.this, response.body());
                        //save type of auth method
                        CommonMethods.storeAccountType(SignUp.this, "local");
                        //Continue to App
                        CommonMethods.continueToApp(SignUp.this);
                    } else {
                        String error = "Sorry, something went wrong";
                        if(response.body().getDetail() != null) {
                            error = response.body().getDetail();
                        } else if (response.body().getError() != null) {
                            error = response.body().getError().toString();
                        }
                        Toast.makeText(SignUp.this, error, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // parse the response body …
                    APIError error = RetrofitErrorUtils.parseError(response);
                    // … and use it to show error information
                    Toast.makeText(SignUp.this, error.message(), Toast.LENGTH_SHORT).show();
                }
                mProgressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<AuthInfo> call, Throwable throwable) {
                // something went completely south (like no internet connection)
                Log.d("onFailure Error", throwable.getMessage());
                String message = "Sorry, something went wrong";
                if (throwable instanceof HttpException) {
                    // We had non-2XX http error
                    message = "Sorry, we could not connect to the server";
                }
                if (throwable instanceof IOException) {
                    // A network or conversion error happened
                    message = "A network or conversion error happened";
                }

                Toast.makeText(SignUp.this, message, Toast.LENGTH_SHORT).show();
                mProgressDialog.dismiss();
            }
        });
    }

    public void goToLogin(View view){
        Intent i = new Intent(SignUp.this, Login.class);
        i.addFlags(FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(i);
        finish();
    }

}