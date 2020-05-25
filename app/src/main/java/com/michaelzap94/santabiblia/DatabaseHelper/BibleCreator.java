package com.michaelzap94.santabiblia.DatabaseHelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;

import static android.content.Context.MODE_PRIVATE;

public class BibleCreator {

    private static final String TAG = "BibleCreator";
    public static final int DB_VERSION = 1;
    private final Context myContext;
    private static BibleCreator dbHelperSingleton;
    private String DB_PATH = null;

    public BibleCreator(Context ct) {
        this.myContext = ct;
        this.DB_PATH = "/data/data/" + ct.getPackageName() + "/" + "databases/";
    }

    public static synchronized BibleCreator getInstance(Context context) {
        BibleCreator dbHelperInner;
        synchronized (BibleCreator.class) {
            if (dbHelperSingleton == null) {
                //if not singleton, create one.
                dbHelperSingleton = new BibleCreator(context);
            }// return existing one.
            dbHelperInner = dbHelperSingleton;
        }
        return dbHelperInner;
    }

    public String[] listOfDefaultDBBibles(){
        try {
            String[] assets = myContext.getAssets().list("databases/bibles/es");
            return assets;
        } catch (Exception e){
            Log.e(TAG, e.getLocalizedMessage(), e);
            return null;
        }
    }
    public ArrayList<String> listOfAllDBAssets(){
        ArrayList<String> allAssets = new ArrayList<>();
        try {
            String[] typesInDatabases = myContext.getAssets().list("databases");
            for( String type : typesInDatabases ) {
                String[] langsInType = myContext.getAssets().list("databases/" + type);
                for( String lang : langsInType ) {
                    String[] filesInLang = myContext.getAssets().list("databases/" + type + "/" + lang);
                    allAssets.addAll(Arrays.asList(filesInLang));
                }
            }
            return allAssets;
        } catch (Exception e){
            Log.e(TAG, e.getLocalizedMessage(), e);
            return null;
        }
    }

    public ArrayList<String> listOfAssetsByType(@NonNull String type){
        ArrayList<String> allAssets = new ArrayList<>();
        try {
            String[] langsInType = myContext.getAssets().list("databases/" + type);
            for( String lang : langsInType ) {
                String[] filesInLang = myContext.getAssets().list("databases/" + type + "/" + lang);
                allAssets.addAll(Arrays.asList(filesInLang));
            }
            return allAssets;
        } catch (Exception e){
            Log.e(TAG, e.getLocalizedMessage(), e);
            return null;
        }
    }

    public ArrayList<String> listOfAssetsByLang(@NonNull String lang){
        ArrayList<String> allAssets = new ArrayList<>();
        try {
            String[] typesInDatabases = myContext.getAssets().list("databases");
            for( String type : typesInDatabases ) {
                String[] filesInLang = myContext.getAssets().list("databases/" + type + "/" + lang);
                allAssets.addAll(Arrays.asList(filesInLang));
            }
            return allAssets;
        } catch (Exception e){
            Log.e(TAG, e.getLocalizedMessage(), e);
            return null;
        }
    }

    public String[] listOfAssetsSpecific(@NonNull String type, @NonNull String lang){
        try {
            String[] filesInLang = myContext.getAssets().list("databases/" + type + "/" + lang);
            return filesInLang;
        } catch (Exception e){
            Log.e(TAG, e.getLocalizedMessage(), e);
            return null;
        }
    }

    public void createDefaultDataBases() throws IOException {
        String[] assets = listOfDefaultDBBibles();
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
                    copyDataBase(null, null, db_name);
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

    private void copyDataBase(String type, String lang, String db_name) throws IOException {
        String mPath = (type != null && lang != null) ? "databases/" + type + "/" + lang + "/": "databases/";
        //first create an empty db:
        SQLiteDatabase db = myContext.openOrCreateDatabase(db_name, MODE_PRIVATE, null);
        InputStream myInput = myContext.getAssets().open(mPath+db_name);
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
    //==================================================================
    public void createSpecificDataBase(String type, String lang) throws IOException {
        String[] assets = listOfAssetsSpecific(type, lang);
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
                    copyDataBase(type, lang, db_name);
                } catch (IOException e) {
                    Log.e(TAG, e.getLocalizedMessage(), e);
                    throw new Error("Error copying database: " + db_name);
                }
            }
        }
    }
    public void createDataBasesByType(String type) throws IOException {
        ArrayList<String> list = listOfAssetsByType(type);
        String[] assets = list.toArray(new String[list.size()]);
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
                String[] langsInType = myContext.getAssets().list("databases/" + type);
                for (String lang:langsInType) {
                    try {
                        Log.d(TAG, "Copying db: " + db_name);
                        copyDataBase(type, lang, db_name);
                    } catch (IOException e) {
                        Log.e(TAG, e.getLocalizedMessage(), e);
                        throw new Error("Error copying database: " + db_name);
                    }
                }
            }
        }
    }


}
