package com.michaelzap94.santabiblia.utilities;


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
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.michaelzap94.santabiblia.Bible;
import com.michaelzap94.santabiblia.Dashboard;
import com.michaelzap94.santabiblia.DatabaseHelper.BibleCreator;
import com.michaelzap94.santabiblia.MainActivity;
import com.michaelzap94.santabiblia.R;
import com.michaelzap94.santabiblia.Search;
import com.michaelzap94.santabiblia.Settings;
import com.michaelzap94.santabiblia.models.Book;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static android.content.Context.MODE_PRIVATE;
import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_ACTIVITY_REORDER_TO_FRONT;

public class CommonMethods {
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    public static final String BIBLE_EXIST = "Bible_exist";
    public static final String CHAPTER_BOOKMARKED = "CHAPTER_BOOKMARKED";
    public static final String BOOK_BOOKMARKED = "BOOK_BOOKMARKED";
    public static void checkDatabaseExistLoad(Context context){

        SharedPreferences prefs = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        boolean bibleExist = prefs.getBoolean(BIBLE_EXIST, false);
        if(!bibleExist){
            try {
                boolean biblesLoaded = loadBibles(context);
                if(biblesLoaded){
                    SharedPreferences.Editor editor = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                    editor.putBoolean(BIBLE_EXIST, true);
                    editor.apply();
                }
            } catch (Exception e){
            }
        }
    }
    private static boolean loadBibles(Context context) throws ExecutionException, InterruptedException {
        return new ImportBibles().execute(context).get();
    }
    private static class ImportBibles extends AsyncTask<Context, Void, Boolean> {
        //get data and populate the list
        protected Boolean doInBackground(Context... arg) {
            boolean success = false;
            BibleCreator bibleCreator = new BibleCreator(arg[0]);
            try {
                bibleCreator.createDataBases();
                success = true;
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
            return success;
        }
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
                        activity.startActivity(new Intent(activity, MainActivity.class).addFlags(FLAG_ACTIVITY_REORDER_TO_FRONT));
                        activity.overridePendingTransition(0,0);
                        return true;
                    case R.id.bnav_dashboard:
                        activity.startActivity(new Intent(activity, Dashboard.class).addFlags(FLAG_ACTIVITY_REORDER_TO_FRONT));
                        activity.overridePendingTransition(0,0);
                        return true;
                    case R.id.bnav_bible:
                        Intent myIntent = new Intent(activity, Bible.class);
                        myIntent.setFlags(FLAG_ACTIVITY_REORDER_TO_FRONT);
                        myIntent.putExtra("book", 230);
                        myIntent.putExtra("chapter", 1);
                        myIntent.putExtra("verse", 0);
                        activity.startActivity(myIntent);
                        //activity.startActivity(new Intent(activity, Bible.class).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
                        activity.overridePendingTransition(0,0);
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

    public static void copyText(Context context, String title, String text){
        String content = title + "\n" + text;
        ((ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE)).setPrimaryClip(ClipData.newPlainText("Bible content", content));
        Toast.makeText(context, title + " Copied.", Toast.LENGTH_SHORT).show();
    }
}
