package com.zapatatech.santabiblia.utilities;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.preference.PreferenceManager;
import androidx.work.BackoffPolicy;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.auth0.android.jwt.JWT;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.common.util.concurrent.ListenableFuture;
import com.zapatatech.santabiblia.Bible;
import com.zapatatech.santabiblia.BibleCompare;
import com.zapatatech.santabiblia.Dashboard;
import com.zapatatech.santabiblia.DatabaseHelper.BibleCreator;
import com.zapatatech.santabiblia.DatabaseHelper.BibleDBHelper;
import com.zapatatech.santabiblia.Home;
import com.zapatatech.santabiblia.Login;
import com.zapatatech.santabiblia.MainActivity;
import com.zapatatech.santabiblia.R;
import com.zapatatech.santabiblia.Search;
import com.zapatatech.santabiblia.Settings;
import com.zapatatech.santabiblia.SignUp;
import com.zapatatech.santabiblia.interfaces.retrofit.RetrofitAuthService;
import com.zapatatech.santabiblia.models.APIError;
import com.zapatatech.santabiblia.models.AuthInfo;
import com.zapatatech.santabiblia.models.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.HttpException;
import retrofit2.Response;

import static android.content.Intent.FLAG_ACTIVITY_REORDER_TO_FRONT;
import static android.content.Intent.makeMainActivity;

public class CommonMethods {
    private static final String TAG = "CommonMethods";
    //flags==================================================
    public static final int USER_NONE = 0;
    public static final int USER_ONLINE = 1;
    public static final int USER_OFFLINE = 2;
    //flags==================================================
    public static final String DOWNLOAD_RESOURCE_TAG = "DOWNLOAD_RESOURCE_TAG";
    public static final String MAIN_CONTENT_DB = "content.db";
    public static final String RESOURCE_TYPE_BIBLE = "type-bible";
    public static final String TEMP_FILE_EXT = "-temp";

    public static final String USER_STATUS = "USER_ONLINE";
    public static final String ACCESS_TOKEN_SP = "ACCESS_TOKEN_SP";
    public static final String REFRESH_TOKEN_SP = "REFRESH_TOKEN_SP";
    public static final String ACCOUNT_TYPE = "ACCOUNT_TYPE";

    public static final String DEFAULT_BIBLE_EXIST = "default_bible_exist";
    public static final String MAIN_BIBLE_SELECTED = "pref_bible_selected";
    public static final String CHAPTER_BOOKMARKED = "CHAPTER_BOOKMARKED";
    public static final String BOOK_BOOKMARKED = "BOOK_BOOKMARKED";
    public static final String CHAPTER_LASTSEEN = "CHAPTER_LASTSEEN";
    public static final String BOOK_LASTSEEN = "BOOK_LASTSEEN";
    public static final int LABEL_ID_MEMORIZE = 1;
    //DEFAULT DBS========================================================================================
    public static void checkDefaultDatabaseExistLoad(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean bibleExist = prefs.getBoolean(DEFAULT_BIBLE_EXIST, false);
        if(!bibleExist){
            try {
                boolean biblesLoaded = loadDefaultBibles(context);
                if(biblesLoaded){
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean(DEFAULT_BIBLE_EXIST, true);
                    editor.apply();
                }
            } catch (Exception e){
            }
        }
    }
    private static boolean loadDefaultBibles(Context context) throws ExecutionException, InterruptedException {
        return new ImportDefaultBibles().execute(context).get();
    }
    private static class ImportDefaultBibles extends AsyncTask<Context, Void, Boolean> {
        //get data and populate the list
        protected Boolean doInBackground(Context... arg) {
            boolean success = false;
            BibleCreator bibleCreator = BibleCreator.getInstance(arg[0]);
            try {
                bibleCreator.createDefaultDataBases();
                success = true;
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
            return success;
        }
    }
    //REST of the DBS====================================================================================
    public static void checkBibleSelectedExist(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String bibleSelected = prefs.getString(MAIN_BIBLE_SELECTED, null);
        //This will only get executed the first time
        if(bibleSelected == null){
            try {
                //boolean biblesLoaded = loadDatabasesByType(context, "bibles");
                boolean biblesLoaded = loadDatabasesByLang(context, "es");
                if(biblesLoaded){
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString(MAIN_BIBLE_SELECTED, BibleDBHelper.getSelectedBibleName(context));
                    editor.apply();
                }
            } catch (Exception e){
            }
        }
    }
    private static boolean loadDatabasesByType(Context context, String type) throws ExecutionException, InterruptedException {
        return new ImportDatabasesByType().execute(context, type).get();
    }
    private static class ImportDatabasesByType extends AsyncTask<Object, Void, Boolean> {
        protected Boolean doInBackground(Object... arg) {
            boolean success = false;
            BibleCreator bibleCreator = BibleCreator.getInstance((Context) arg[0]);
            try {
                bibleCreator.createDataBasesByType((String) arg[1]);
                success = true;
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
            return success;
        }
    }
    private static boolean loadDatabasesByLang(Context context, String lang) throws ExecutionException, InterruptedException {
        return new ImportDatabasesByLang().execute(context, lang).get();
    }
    private static class ImportDatabasesByLang extends AsyncTask<Object, Void, Boolean> {
        protected Boolean doInBackground(Object... arg) {
            boolean success = false;
            BibleCreator bibleCreator = BibleCreator.getInstance((Context) arg[0]);
            try {
                bibleCreator.createDataBasesByLang((String) arg[1]);
                success = true;
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
            return success;
        }
    }
    //===================================================================================================
    public static void setBookmark(Object object, int book_number, int chapter_number){
        SharedPreferences.Editor editor;
        if(object instanceof SharedPreferences){
            editor = ((SharedPreferences) object).edit();
        } else {
            editor = PreferenceManager.getDefaultSharedPreferences((Context) object).edit();
        }
        editor.putInt(BOOK_BOOKMARKED, book_number);
        editor.putInt(CHAPTER_BOOKMARKED, chapter_number);
        editor.apply();
    }
    public static void setLastSeen(Object object, int book_number, int chapter_number){
        SharedPreferences.Editor editor;
        if(object instanceof SharedPreferences){
            editor = ((SharedPreferences) object).edit();
        } else {
            editor = PreferenceManager.getDefaultSharedPreferences((Context) object).edit();
        }
        editor.putInt(BOOK_LASTSEEN, book_number);
        editor.putInt(CHAPTER_LASTSEEN, chapter_number);
        editor.apply();
    }
    public static void bottomBarActionHandler(BottomNavigationView bottomNavigationView, final int itemId, final AppCompatActivity activity){
        //Set item selected
        bottomNavigationView.setSelectedItemId(itemId);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                if(itemId == menuItem.getItemId()) return true;
                switch (menuItem.getItemId()){
                    case R.id.bnav_home:
                        activity.startActivity(new Intent(activity, Home.class).addFlags(FLAG_ACTIVITY_REORDER_TO_FRONT));
                        activity.overridePendingTransition(0,0);
                        return true;
                    case R.id.bnav_dashboard:
                        activity.startActivity(new Intent(activity, Dashboard.class).addFlags(FLAG_ACTIVITY_REORDER_TO_FRONT));
                        activity.overridePendingTransition(0,0);
                        return true;
                    case R.id.bnav_bible:
                        goToLastSeen(activity);
                        return true;
                    case R.id.bnav_search:
                        activity.startActivity(new Intent(activity, Search.class).addFlags(FLAG_ACTIVITY_REORDER_TO_FRONT));
                        activity.overridePendingTransition(0,0);
                        return true;
                    case R.id.bnav_settings:
                        activity.startActivity(new Intent(activity, Settings.class).addFlags(FLAG_ACTIVITY_REORDER_TO_FRONT));
                        activity.overridePendingTransition(0,0);
                        return true;
                    default: return false;
                }
            }
        });
    }
    public static void goToLastSeen(AppCompatActivity activity){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        int book_lastseen = prefs.getInt(BOOK_LASTSEEN, 0);
        int chapter_lastseen = prefs.getInt(CHAPTER_LASTSEEN, 0);

        Intent myIntent = new Intent(activity, Bible.class);
        myIntent.setFlags(FLAG_ACTIVITY_REORDER_TO_FRONT);

        if(chapter_lastseen != 0 && book_lastseen != 0) {
            myIntent.putExtra("book", book_lastseen);
            myIntent.putExtra("chapter", chapter_lastseen);
            myIntent.putExtra("verse", 0);
            myIntent.putExtra("resetstate", true);
        } else {
            myIntent.putExtra("book", 230);
            myIntent.putExtra("chapter", 1);
            myIntent.putExtra("verse", 0);
        }
        activity.startActivity(myIntent);
        //activity.startActivity(new Intent(activity, Bible.class).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
        activity.overridePendingTransition(0,0);
    }
    //==================================================================================================
    public static int checkUserStatus(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getInt(USER_STATUS, USER_NONE);
    }
    public static int updateUserStatus(Context context, int value){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(USER_STATUS, value);
        editor.apply();
        int newStatusFlag = prefs.getInt(USER_STATUS, USER_NONE);
        return newStatusFlag;
    }
    public static void storeBothTokens(Context context, AuthInfo authInfo){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(ACCESS_TOKEN_SP, authInfo.getAccessToken());
        editor.putString(REFRESH_TOKEN_SP, authInfo.getRefreshToken());
        editor.apply();
    }
    public static void storeAccountType(Context context, String account_type){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(ACCOUNT_TYPE, account_type);
        editor.apply();
    }
    public static String getAccountType(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(ACCOUNT_TYPE, null);
    }
    public static String getAccessToken(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(ACCESS_TOKEN_SP, null);
    }
    public static String getRefreshToken(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(REFRESH_TOKEN_SP, null);
    }
    public static boolean clearAccessToken(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.edit().remove(ACCESS_TOKEN_SP).commit();//commit will return true if success
    }
    public static boolean clearRefreshToken(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.edit().remove(REFRESH_TOKEN_SP).commit();//commit will return true if success
    }
    public static boolean clearAccountType(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.edit().remove(ACCOUNT_TYPE).commit();//commit will return true if success
    }
    public static void continueToApp(Activity activity){
        if(CommonMethods.getAccessToken(activity) != null && CommonMethods.getAccessToken(activity) != null ){
            int newStatus = updateUserStatus(activity, USER_ONLINE);
            if(newStatus == USER_ONLINE){
                Intent intent = new Intent(activity, Home.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);//CLEAR ALL ACTIVITIES
                activity.startActivity(intent);
            } else {
                Toast.makeText(activity, "Status not updated", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(activity, "Tokens are not stored", Toast.LENGTH_SHORT).show();
        }
    }

    public static void retrofitLogout(Activity mActivity){

        String refreshToken = getRefreshToken(mActivity);
        String accessToken = getAccessToken(mActivity);
        if( refreshToken != null && accessToken != null ) {
            RetrofitAuthService logOutService = RetrofitServiceGenerator.createService(RetrofitAuthService.class, accessToken);

            HashMap<String, Object> logOutObject = new HashMap<>();
            logOutObject.put("refresh", refreshToken);

            Call<AuthInfo> call = logOutService.requestLogOut(logOutObject);
            // Set up progress before call
            ProgressDialog mProgressDialog = new ProgressDialog(mActivity);
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setMessage("Logging you out...");
            mProgressDialog.show();
            call.enqueue(new Callback<AuthInfo >() {
                @Override
                public void onResponse(Call<AuthInfo> call, Response<AuthInfo> response) {
                    if (response.isSuccessful()) {
                        // user object available
                        Log.d(TAG, "onResponse: logout success " + response.body());
                        if(!response.body().getStatus().equals("success") ){
                            String error = "Sorry, something went wrong";
                            if(response.body().getDetail() != null) {
                                error = response.body().getDetail();
                            } else if (response.body().getError() != null) {
                                error = response.body().getError().toString();
                            }
                            Log.d(TAG, "onResponse: logout failure: " + error);
                        }
                    } else {
                        // parse the response body …
                        APIError error = RetrofitErrorUtils.parseError(response);
                        // … and use it to show error information
                        Log.d(TAG, "onResponse: logout failure: " + error);
                    }
                    mProgressDialog.dismiss();
                    //Remove Both tokens and change status to USER_NONE
                    CommonMethods.logOutOfApp(mActivity);
                }

                @Override
                public void onFailure(Call<AuthInfo> call, Throwable t) {
                    // something went completely south (like no internet connection)
                    Log.d("onFailure logout Error", t.getMessage());
                    mProgressDialog.dismiss();
                    //Remove Both tokens and change status to USER_NONE
                    CommonMethods.logOutOfApp(mActivity);
                }
            });
        } else {
            //Remove Both tokens if any and change status to USER_NONE
            CommonMethods.logOutOfApp(mActivity);
        }
    }
    public static void logOutOfApp(Activity activity){
        String accountName = CommonMethods.getAccountType(activity);
        String account_type = (null != accountName) ? accountName : "offline";//local WILL GO TO default too
        switch (account_type) {
            case "google": CommonMethods.googleLogOut(activity);
                break;
            default: CommonMethods.deviceLogOut(activity);
        }
    }
    private static void googleLogOut(Activity activity) {
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(activity.getString(R.string.server_client_id))
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(activity, gso);
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(activity, new OnCompleteListener<Void>() {//SUCCESS OR FAILURE
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        deviceLogOut(activity);
                    }
                });
    }
    private static void deviceLogOut(Activity activity){
        if(CommonMethods.clearAccessToken(activity) && CommonMethods.clearRefreshToken(activity) && CommonMethods.clearAccountType(activity)){
            int newStatus = CommonMethods.updateUserStatus(activity, CommonMethods.USER_NONE);
            if(newStatus == CommonMethods.USER_NONE){
                Intent intent = new Intent(activity, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);//CLEAR ALL ACTIVITIES
                activity.startActivity(intent);
            } else {
                Toast.makeText(activity, "Status not updated", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(activity, "Tokens are not stored", Toast.LENGTH_SHORT).show();
        }
    }

    public static void retrofitVerifyCredentials(Activity mActivity){
        String refreshToken = getRefreshToken(mActivity);
        String accessToken = getAccessToken(mActivity);
        if( accessToken != null && refreshToken != null ) {
            RetrofitAuthService logOutService = RetrofitServiceGenerator.createService(RetrofitAuthService.class, accessToken);
            HashMap<String, Object> authObj = new HashMap<>();
            authObj.put("token", accessToken);

            Call<AuthInfo> call = logOutService.requestVerify(authObj);
            // Set up progress before call=========================================
            ProgressDialog mProgressDialog = new ProgressDialog(mActivity);
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setMessage("Verifying credentials...");
            mProgressDialog.show();
            //======================================================================
            call.enqueue(new Callback<AuthInfo >() {
                @Override
                public void onResponse(Call<AuthInfo> call, Response<AuthInfo> response) {
                    mProgressDialog.dismiss();
                    if (response.isSuccessful()) { //200 means token is valid
                        //UPDATE STATUS TO ONLINE
                        CommonMethods.updateUserStatus(mActivity, CommonMethods.USER_ONLINE);
                        //reuse credentials and go home
                        goToHome(mActivity);
                    } else {
                        //if token is invalid, try to get new credentials using the refreshtoken
                        CommonMethods.retrofitRefreshCredentials(mActivity, refreshToken);
                    }
                }

                @Override
                public void onFailure(Call<AuthInfo> call, Throwable throwable) {
                    // NOT INTERNET OR SERVER DOWN GO OFFLINE
                    Log.d("onFailure Error", throwable.getMessage());
                    String message = "Sorry, something went wrong. You can use the app OFFLINE.";
                    if (throwable instanceof HttpException) {
                        // We had non-2XX http error
                        message = "Sorry, we could not connect to the server. You can use the app OFFLINE.";
                    }
                    if (throwable instanceof IOException) {
                        // A network or conversion error happened
                        message = "A network error happened.\nYou can use the app OFFLINE.";
                    }
                    Toast.makeText(mActivity, message, Toast.LENGTH_LONG).show();
                    mProgressDialog.dismiss();

                    //UPDATE STATUS TO OFFLINE
                    CommonMethods.updateUserStatus(mActivity, CommonMethods.USER_OFFLINE);
                    //DO NOT REMOVE THE CREDENTIALS
                    goToHome(mActivity);
                }
            });
        } else {
            //Remove Both tokens if any and change status to USER_NONE
            CommonMethods.logOutOfApp(mActivity);
        }
    }

    public static void retrofitRefreshCredentials(Activity mActivity, String refreshToken) {
        RetrofitAuthService logOutService = RetrofitServiceGenerator.createService(RetrofitAuthService.class, null);
        HashMap<String, Object> authObj = new HashMap<>();
        authObj.put("refresh", refreshToken);


        Call<AuthInfo> call = logOutService.requestRefresh(authObj);

        // Set up progress before call
        ProgressDialog mProgressDialog = new ProgressDialog(mActivity);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage("Refreshing credentials...");
        mProgressDialog.show();
        call.enqueue(new Callback<AuthInfo >() {
            @Override
            public void onResponse(Call<AuthInfo> call, Response<AuthInfo> response) {
                if (response.isSuccessful()) { //200 SUCCESS REFRESHING
                    // user object available
                    Log.d(TAG, "onResponse: success" + response.body());
                    if(response.body().getAccessToken() != null && response.body().getRefreshToken() != null ){
                        //Store new credentials tokens
                        CommonMethods.storeBothTokens(mActivity, response.body());
                        //Reuse previous account type, as it should be the same
                        //CommonMethods.storeAccountType(mActivity, account_type);
                        //UPDATE STATUS TO ONLINE
                        CommonMethods.updateUserStatus(mActivity, CommonMethods.USER_ONLINE);
                        //go home
                        goToHome(mActivity);

                    } else { //No NEW access or refresh token -> LOG USER OUT
                        String error = "Sorry, something went wrong";
                        if(response.body().getDetail() != null) {
                            error = response.body().getDetail();
                        } else if (response.body().getError() != null) {
                            error = response.body().getError().toString();
                        }
                        Toast.makeText(mActivity, error, Toast.LENGTH_SHORT).show();
//                        if(account_type == "google") {
//                            mGoogleSignInClient.signOut();
//                        }
                        CommonMethods.retrofitLogout(mActivity);
                    }
                } else { //401 Unauthorized -> TOKEN IS BLACKLISTED
                    // parse the response body …
                    APIError error = RetrofitErrorUtils.parseError(response);
                    // … and use it to show error information
                    Toast.makeText(mActivity, error.message(), Toast.LENGTH_SHORT).show();
//                    if(account_type == "google") {
//                        mGoogleSignInClient.signOut();
//                    }
                    CommonMethods.retrofitLogout(mActivity);
                }
                mProgressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<AuthInfo> call, Throwable throwable) {
                // NOT INTERNET OR SERVER DOWN -> GO OFFLINE
                Log.d("onFailure Error", throwable.getMessage());
                String message = "Sorry, something went wrong. You can use the app OFFLINE.";
                if (throwable instanceof HttpException) {
                    // We had non-2XX http error
                    message = "Sorry, we could not connect to the server. You can use the app OFFLINE.";
                }
                if (throwable instanceof IOException) {
                    // A network or conversion error happened
                    message = "A network error happened.\nYou can use the app OFFLINE.";
                }
                Toast.makeText(mActivity, message, Toast.LENGTH_LONG).show();
                mProgressDialog.dismiss();
                //UPDATE STATUS TO OFFLINE
                CommonMethods.updateUserStatus(mActivity, CommonMethods.USER_OFFLINE);
                //DO NOT REMOVE THE CREDENTIALS
                goToHome(mActivity);
            }
        });
    }

    public static void goToHome(Activity mActivity){
        Intent intent = new Intent(mActivity, Home.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);//CLEAR ALL ACTIVITIES
        mActivity.startActivity(intent);
    }

    //==================================================================================================
    public static User decodeJWTAndCreateUser(String jwt_token){
        JWT jwt = new JWT(jwt_token);
        return new User(jwt.getClaim("user_id").asString(),
                jwt.getClaim("email").asString(),
                jwt.getClaim("fullname").asString(),
                jwt.getClaim("account_type").asString(),
                jwt.getClaim("social_id").asString());
    }
    public static User decodeJWTAndCreateUser(Activity mActivity){
        String jwt_token = CommonMethods.getAccessToken(mActivity);
        if(jwt_token != null){
            JWT jwt = new JWT(jwt_token);
            Log.d(TAG, "decodeJWTAndCreateUser: " + jwt.getClaim("fullname").asString());
            return new User(jwt.getClaim("user_id").asString(),
                    jwt.getClaim("email").asString(),
                    jwt.getClaim("fullname").asString(),
                    jwt.getClaim("account_type").asString(),
                    jwt.getClaim("social_id").asString());
        } else {
            return null;
        }
    }
    public static boolean isAccessTokenExpired(String jwt_token){
        JWT jwt = new JWT(jwt_token);
        return jwt.isExpired(10); // 10 seconds leeway
    }

    //==================================================================================================
    public static void startWorkManager(Activity mActivity, String resourceUrl, String fileName){
        //CONSTRAINTS
        Constraints constraints = new Constraints.Builder()
                .setRequiresStorageNotLow(true)
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        WorkManager mWorkManager = WorkManager.getInstance(mActivity);

        OneTimeWorkRequest downloadWorkRequest = new OneTimeWorkRequest.Builder(DownloadResourceWM.class)
                .setConstraints(constraints)
                .addTag(DOWNLOAD_RESOURCE_TAG)
                .setBackoffCriteria(
                        BackoffPolicy.EXPONENTIAL,
                        OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
                        TimeUnit.MILLISECONDS)
                .setInputData(
                        new Data.Builder()
                                .putString("RESOURCE_URL", resourceUrl)
                                .putString("RESOURCE_FILENAME", fileName)
                                .build()
                )
                .build();

        mWorkManager.enqueueUniqueWork(fileName, ExistingWorkPolicy.REPLACE, downloadWorkRequest);
    }

    public static boolean isTaskEnqueuedOrRunning(Activity mActivity, String tag){
        //tag should be unique
        WorkManager instance = WorkManager.getInstance(mActivity);
        ListenableFuture<List<WorkInfo>> statuses = instance.getWorkInfosByTag(tag);
        try {
            boolean running = false;
            List<WorkInfo> workInfoList = statuses.get();
            for (WorkInfo workInfo : workInfoList) {
                WorkInfo.State state = workInfo.getState();
                running = state == WorkInfo.State.RUNNING | state == WorkInfo.State.ENQUEUED;
            }
            return running;
        } catch (ExecutionException e) {
            e.printStackTrace();
            return false;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static WorkInfo.State getWorkState(Activity mActivity, String tag) {
        //tag should be unique
        WorkManager instance = WorkManager.getInstance(mActivity);
        ListenableFuture<List<WorkInfo>> statuses = instance.getWorkInfosByTag(tag);
        try {
            WorkInfo.State state = null;
            List<WorkInfo> workInfoList = statuses.get();
            for (WorkInfo workInfo : workInfoList) {
                state = workInfo.getState();
            }
            return state;
        } catch (ExecutionException e) {
            e.printStackTrace();
            return null;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void registerWorkManagerListenerTag(Activity mActivity, String fileNameTag, Callable<Void> func){
        WorkManager.getInstance(mActivity).getWorkInfosByTagLiveData(fileNameTag)
                .observe((LifecycleOwner) mActivity, listOfWorkInfo -> {
                    // If there are no matching work info, do nothing
                    if (listOfWorkInfo == null || listOfWorkInfo.isEmpty()) {
                        return;
                    }
                    WorkInfo info = listOfWorkInfo.get(0);
                    Log.d(TAG, "startWorkManager: " + info.getState());
                    if (info != null && info.getState() == WorkInfo.State.RUNNING) {
                        Log.d(TAG, "startWorkManager: STILL RUNNING");

                    } else if (info != null && info.getState().isFinished()) {

                        boolean myResult = info.getOutputData().getBoolean("downloadComplete", false);
                        //String fileName = info.getOutputData().getString("fileName");
                        if(myResult){
                            //SUCCESS
                            try {
                                func.call();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        //clears unfinished tasks
                        //WorkManager.getInstance(MainActivityRT.this).cancelAllWorkByTag("downloadResourceUWID");

                        //You can call the method pruneWork() on your WorkManager to clear the List<WorkStatus> && List<WorkInfo>
                        WorkManager.getInstance(mActivity).pruneWork();//kill the workmanager we start
                    }
                });
    }

    public void registerWorkManagerListenerTagAll(Activity mActivity, Callable<Void> func){
        WorkManager.getInstance(mActivity).getWorkInfosByTagLiveData(DOWNLOAD_RESOURCE_TAG)
                .observe((LifecycleOwner) mActivity, new Observer<List<WorkInfo>>() {
                    @Override
                    public void onChanged(@Nullable List<WorkInfo> workInfoList) {
                        if (workInfoList.size() > 0) {
                            for (WorkInfo workInfo: workInfoList) {
                                Log.d(TAG, "startWorkManager: " + workInfo.getState());
                                if (workInfo != null && workInfo.getState() == WorkInfo.State.RUNNING) {
                                    Log.d(TAG, "startWorkManager: STILL RUNNING");
                                    String fileNameProcessing = workInfo.getProgress().getString("fileNameProcessing");
                                    int progress = workInfo.getProgress().getInt("progress", -1);
                                    if(fileNameProcessing!=null){

                                    } else {
                                        //fileName dit not start processing or is not processing
                                    }

                                } else if (workInfo != null && workInfo.getState().isFinished()) {
                                    boolean myResult = workInfo.getOutputData().getBoolean("downloadComplete", false);
                                    //String fileName = info.getOutputData().getString("fileName");
                                    if(myResult){
                                        //SUCCESS
                                        try {
                                            func.call();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    //clears unfinished tasks
                                    //WorkManager.getInstance(MainActivityRT.this).cancelAllWorkByTag("downloadResourceUWID");
                                    //You can call the method pruneWork() on your WorkManager to clear the List<WorkStatus> && List<WorkInfo>
                                    WorkManager.getInstance(mActivity).pruneWork();//kill the workmanager we start
                                }
                            }
                        }
                    }
                });
    }

    //==================================================================================================
    public static ArrayList[] getBiblesDownloaded(Context ctx){
        ArrayList<String> listBibles = new ArrayList<>();
        ArrayList<String> listBiblesDisplayName = new ArrayList<>();
        for (String dbName: ctx.databaseList()) {
            if(!dbName.contains("-journal") && !dbName.equals(MAIN_CONTENT_DB) && !dbName.contains(TEMP_FILE_EXT) && dbName.contains(RESOURCE_TYPE_BIBLE)) {
                listBibles.add(dbName);
                //split filename in _
                String[] resultSplit = dbName.split("_");
                String displayName = Util.joinArrayResourceName(" ", true, resultSplit);
                listBiblesDisplayName.add(displayName);
            }
        }
        return new ArrayList[]{listBibles, listBiblesDisplayName};
    }
    //==================================================================================================

    public static void copyText(Context context, String title, String text){
        String content = title + "\n" + text;
        ((ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE)).setPrimaryClip(ClipData.newPlainText("Bible content", content));
        Toast.makeText(context, title + " Copied.", Toast.LENGTH_SHORT).show();
    }
    public static void share(Context ctx, String title, String body) {
        String content = title + "\n" + body;
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");
        sendIntent.putExtra(Intent.EXTRA_TEXT, content);
        sendIntent.putExtra(Intent.EXTRA_TITLE, title);
        Intent shareIntent = Intent.createChooser(sendIntent, "Share it with the people you love!");
        ctx.startActivity(shareIntent);
    }
}
