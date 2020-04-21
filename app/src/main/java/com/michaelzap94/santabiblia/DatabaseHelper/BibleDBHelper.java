package com.michaelzap94.santabiblia.DatabaseHelper;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.michaelzap94.santabiblia.models.Verse;

import java.util.ArrayList;

public class BibleDBHelper {

    private static final String TAG = "BibleDBHelper";

    public static final String DB_NAME_BIBLE_CONTENT = "RVR60.db";
    public static final String DB_NAME_BIBLE_COMMENTARIES = "RVR60commentaries.db";
    public static final int DB_VERSION = 1;
    private Context myContext;
    String DB_PATH = null;
    String DB_NAME = null;
    private SQLiteDatabase myDataBase;
    private static BibleDBHelper dbHelperSingleton = null;

    public static synchronized BibleDBHelper getInstance(Context context) {
        BibleDBHelper dbHelperInner;
        synchronized (BibleDBHelper.class) {
            if (dbHelperSingleton == null) {
                //if not singleton, create one.
                dbHelperSingleton = new BibleDBHelper(context);
            }//else return existing one.
            dbHelperInner = dbHelperSingleton;
        }
        return dbHelperInner;
    }

    public BibleDBHelper(Context context) {
          this.myContext = context;
          //this.DB_PATH = "/data/data/" + context.getPackageName() + "/" + "databases/";
    }

    public ArrayList<Verse> getVerses(int book_id, int chapter_number) {
        Cursor innerCursor;
        int rowCount;
        int i;

        ArrayList<Verse> list = new ArrayList();
        try {
            innerCursor = openDataBaseNoHelper(DB_NAME_BIBLE_CONTENT).rawQuery("SELECT  verse , text FROM verses WHERE book_number = ? AND chapter = ? ORDER BY verse", new String[] {String.valueOf(book_id), String.valueOf(chapter_number)});
            if (innerCursor.moveToFirst()) {
                rowCount = innerCursor.getCount();
                for (i = 0; i < rowCount; i++) {
//                    int bookCol = innerCursor.getColumnIndex(BibleContracts.VersesContract.COL_BOOK_ID);//NOT NEEDED AS BEING PASSED
//                    int chapterCol = innerCursor.getColumnIndex(BibleContracts.VersesContract.COL_CHAPTER);//NOT NEEDED AS BEING PASSED
                    int verseCol = innerCursor.getColumnIndex(BibleContracts.VersesContract.COL_VERSE);
                    int textCol = innerCursor.getColumnIndex(BibleContracts.VersesContract.COL_TEXT);
//                    int id = innerCursor.getInt(bookCol);
                    int verse = innerCursor.getInt(verseCol);
                    String text = innerCursor.getString(textCol);
                    list.add(new Verse(book_id, chapter_number, verse, text.trim(), 0));
                    innerCursor.moveToNext();
                }
            }
            innerCursor.close();
        } catch (Exception e) {
        }
        return list;
    }

    public SQLiteDatabase openDataBaseNoHelper(String db_name) throws SQLException {
        String myPath = this.myContext.getDatabasePath(db_name).getPath();
        return SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
    }

    public Cursor query(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit) {
        return myDataBase.query(table, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
    }
}
