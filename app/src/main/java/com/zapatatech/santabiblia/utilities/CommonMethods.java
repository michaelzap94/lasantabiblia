package com.zapatatech.santabiblia.utilities;


import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.zapatatech.santabiblia.Bible;
import com.zapatatech.santabiblia.Dashboard;
import com.zapatatech.santabiblia.DatabaseHelper.BibleCreator;
import com.zapatatech.santabiblia.DatabaseHelper.BibleDBHelper;
import com.zapatatech.santabiblia.Home;
import com.zapatatech.santabiblia.R;
import com.zapatatech.santabiblia.Search;
import com.zapatatech.santabiblia.Settings;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static android.content.Intent.FLAG_ACTIVITY_REORDER_TO_FRONT;

public class CommonMethods {
    public static final String USER_SELECTED_OFFLINE = "USER_SELECTED_OFFLINE";
    public static final String USER_IS_LOGGED_IN = "USER_IS_LOGGED_IN";
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
    //TODO: implement logic to determine if user is logged in
    public static boolean checkIfUserSelectedOfflineOrIsLoggedIn(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(USER_SELECTED_OFFLINE, false);
    }
    public static boolean updateUserOfflineSelection(Context context, boolean value){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(USER_SELECTED_OFFLINE, value);
        editor.apply();
        return prefs.getBoolean(USER_SELECTED_OFFLINE, false);
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
