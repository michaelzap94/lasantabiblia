package com.michaelzap94.santabiblia.DatabaseHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.text.Html;
import android.text.Spanned;

import com.michaelzap94.santabiblia.models.Label;
import com.michaelzap94.santabiblia.models.Verse;

import java.util.ArrayList;

public class ContentDBHelper extends SQLiteOpenHelper {
    private static int DATABASE_VERSION = 1;
    private static String DB_NAME = "content.db";
    private Context context;
    private static ContentDBHelper dbHelperSingleton = null;

    public static synchronized ContentDBHelper getInstance(Context context) {
        ContentDBHelper dbHelperInner;
        synchronized (ContentDBHelper.class) {
            if (dbHelperSingleton == null) {
                //if not singleton, create one.
                dbHelperSingleton = new ContentDBHelper(context);
            }//else return existing one.
            dbHelperInner = dbHelperSingleton;
        }
        return dbHelperInner;
    }

    public ContentDBHelper(Context context) {
        super(context, DB_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    public boolean createLabel(String name, String color){
        ContentValues cv = new ContentValues();
        cv.put("name", name);
        cv.put("color", color);
        return this.getWritableDatabase().insert("labels",null, cv) > 0;
    }
    public boolean editLabel(String name, String color, int id){
        ContentValues cv = new ContentValues();
        cv.put("name", name);
        cv.put("color", color);
        return this.getWritableDatabase().update("labels", cv, "_id="+id, null) > 0;
    }
    public ArrayList<Label> getAllLabels(){
        Cursor innerCursor;
        int rowCount;
        int i;
        ArrayList<Label> list = new ArrayList();
        try {
            String query = "SELECT * FROM labels";
            innerCursor = this.getReadableDatabase().rawQuery(query, null);
            if (innerCursor.moveToFirst()) {
                rowCount = innerCursor.getCount();
                for (i = 0; i < rowCount; i++) {
                    int nameCol = innerCursor.getColumnIndex("name");
                    int colorCol = innerCursor.getColumnIndex("color");
                    int idCol = innerCursor.getColumnIndex("_id");

                    String name = innerCursor.getString(nameCol);
                    String color = innerCursor.getString(colorCol);
                    int id = innerCursor.getInt(idCol);

                    list.add(new Label(name, color, id));
                    innerCursor.moveToNext();
                }
            }
            innerCursor.close();
        } catch (Exception e) {
        }
        return list;
    }
    public boolean deleteOneLabel(int id){
        return this.getWritableDatabase().delete("labels", "_id =" + id, null) > 0;
    }



    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE labels (_id INTEGER PRIMARY KEY, name VARCHAR NOT NULL, color VARCHAR NOT NULL)");
        db.execSQL("CREATE TABLE verses_marked (_id INTEGER PRIMARY KEY, label INTEGER NOT NULL, book_number INTEGER NOT NULL, chapter INTEGER NOT NULL, verseFrom INTEGER NOT NULL, verseTo INTEGER NOT NULL, color VARCHAR NOT NULL, text VARCHAR, date_created datetime default current_timestamp, date_updated datetime default current_timestamp, state INTEGER NOT NULL)");
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS labels");
        db.execSQL("DROP TABLE IF EXISTS verses_marked");
        onCreate(db);
    }
}
