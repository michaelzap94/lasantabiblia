package com.michaelzap94.santabiblia.DatabaseHelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import static android.content.Context.MODE_PRIVATE;

public class BibleCreator {

    private static final String TAG = "BibleDBHelper";
    public static final int DB_VERSION = 1;
    private final Context myContext;
    String DB_PATH = null;

    public BibleCreator(Context ct) {
        this.myContext = ct;
        this.DB_PATH = "/data/data/" + ct.getPackageName() + "/" + "databases/";
    }

    public String[] listOfDBAssets(){
        try {
            String[] assets = myContext.getAssets().list("databases");
            return assets;
        } catch (Exception e){
            Log.e(TAG, e.getLocalizedMessage(), e);
            return null;
        }
    }

    public void createDataBases() throws IOException {
        String[] assets = listOfDBAssets();
        Log.d(TAG, "List of Assets" + Arrays.toString(assets));

        if(assets == null){
            throw new Error("Error copying database");
        }
        for (String db_name: assets) {
            Log.d(TAG, "db_name: " + db_name);

            boolean dbExist = checkDataBase(db_name);
            if (dbExist) {
                Log.d(TAG, "createDataBase: DB already created, Name: " + db_name);
            } else {
                try {
                    Log.d(TAG, "Copying db: " + db_name);
                    copyDataBase(db_name);
                } catch (IOException e) {
                    Log.e(TAG, e.getLocalizedMessage(), e);
                    throw new Error("Error copying database: " + db_name);
                }
            }
        }
    }

    private boolean checkDataBase(String db_name) {
        SQLiteDatabase checkDB = null;
        try {
            String myPath = DB_PATH + db_name;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        } catch (SQLiteException e) {
            //Database is not present. Name:
        }
        if (checkDB != null) {
            checkDB.close();
            return true;
        }
        return false;
    }

    private void copyDataBase(String db_name) throws IOException {
        //first create an empty db:
        SQLiteDatabase db = myContext.openOrCreateDatabase(db_name, MODE_PRIVATE, null);
        InputStream myInput = myContext.getAssets().open("databases/"+db_name);
        String outFileName = DB_PATH + db_name;
        OutputStream myOutput = new FileOutputStream(outFileName);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }
        myOutput.flush();
        myOutput.close();
        myInput.close();
        db.close();
    }

}
