package com.michaelzap94.santabiblia.DatabaseHelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ContentDBHelper extends SQLiteOpenHelper {
    private static int DATABASE_VERSION = 1;
    private static String DB_NAME = "content.db";
    private Context context;

    public ContentDBHelper(Context context) {
        super(context, DB_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE label (id INTEGER PRIMARY KEY, name VARCHAR NOT NULL, color VARCHAR NOT NULL)");
        db.execSQL("CREATE TABLE verses_selected (id INTEGER PRIMARY KEY, label INTEGER NOT NULL, book_number INTEGER NOT NULL, chapter INTEGER NOT NULL, verseFrom INTEGER NOT NULL, verseTo INTEGER NOT NULL, color VARCHAR NOT NULL, text VARCHAR, date_created datetime default current_timestamp, date_updated datetime default current_timestamp, state INTEGER NOT NULL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
