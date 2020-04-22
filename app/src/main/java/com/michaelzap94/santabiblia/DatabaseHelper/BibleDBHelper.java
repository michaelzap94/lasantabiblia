package com.michaelzap94.santabiblia.DatabaseHelper;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.text.Html;
import android.text.Spanned;

import com.michaelzap94.santabiblia.models.Concordance;
import com.michaelzap94.santabiblia.models.Verse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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


    public ArrayList<Verse> getVerses(int book_number, int chapter_number) {
        Cursor innerCursor;
        int rowCount;
        int i;
        Map<Integer,Boolean> history = new HashMap<>();

        ArrayList<Verse> list = new ArrayList();
        try {
            String query = "SELECT verses.verse , verses.text, stories.title, stories.verse AS story_at_verse, order_if_several FROM verses LEFT JOIN stories" +
                    " ON verses.book_number =  stories.book_number AND verses.chapter =  stories.chapter AND verses.verse = stories.verse " +
                    " WHERE verses.book_number = ? AND verses.chapter = ? ORDER BY verses.book_number, verses.chapter, verses.verse, stories.verse";
            innerCursor = openDataBaseNoHelper(DB_NAME_BIBLE_CONTENT).rawQuery(query, new String[] {String.valueOf(book_number), String.valueOf(chapter_number)});
            if (innerCursor.moveToFirst()) {
                rowCount = innerCursor.getCount();
                for (i = 0; i < rowCount; i++) {
                    int verseCol = innerCursor.getColumnIndex(BibleContracts.VersesContract.COL_VERSE);
                    int textCol = innerCursor.getColumnIndex(BibleContracts.VersesContract.COL_TEXT);
                    int textTitleCol = innerCursor.getColumnIndex(BibleContracts.StoriesContract.COL_TITLE);
                    //int titleAtVerseCol = innerCursor.getColumnIndex(BibleContracts.StoriesContract.COL_STORY_AT_VERSE);
                    //int orderIfSeveralTitlesCol = innerCursor.getColumnIndex(BibleContracts.StoriesContract.COL_ORDER_IF_SEVERAL);


                    int verse = innerCursor.getInt(verseCol);
                    String text = innerCursor.getString(textCol).trim();
                    String textTitle = null;
                    //int titleAtVerse;
                    int orderIfSeveralTitles;
                    if(!innerCursor.isNull(textTitleCol)){
                        textTitle = innerCursor.getString(textTitleCol);
                        //titleAtVerse = innerCursor.getInt(titleAtVerseCol);
                        //orderIfSeveralTitles = innerCursor.getInt(orderIfSeveralTitlesCol);
                    }

                    String textToBeParsed;

                    if(text.contains("<n>[") && text.contains("]</n>")) {
                        textToBeParsed = text.replace("<n>[","<br><b><i>(").replace("]</n>",")</i></b>");
                    } else {
                        textToBeParsed = "<b>" + verse + "</b>" + ". " + text;
                    }

                    Spanned textSpanned;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        textSpanned = Html.fromHtml(textToBeParsed, Html.FROM_HTML_MODE_COMPACT);
                    } else {
                        textSpanned = Html.fromHtml(textToBeParsed);
                    }
                    //If a Verse is in the array already and we see the same verse again, it's because there are 2+ titles
                    if(!history.containsKey(verse)){
                        list.add(new Verse(book_number, chapter_number, verse, textSpanned, textTitle, 0));
                        history.put(verse, true);
                    } else {
                        Verse existingVerse = list.get(verse - 1);
                        String newTextTitle = existingVerse.getTextTitle() + "\n" + textTitle;
                        existingVerse.setTextTitle(newTextTitle);
                    }

                    innerCursor.moveToNext();
                }
            }
            innerCursor.close();
        } catch (Exception e) {
        }
        return list;
    }

    public String[] getConcordance(int book_number, String marker){
        //ArrayList<Concordance> list = new ArrayList();
        //String toReturn = "No element could be found";
        ArrayList<String> listString = new ArrayList<>();
        String[] arrToReturn = {"No element found"};
        Cursor innerCursor;
        int rowCount;
        int i;
        Map<Integer,Boolean> history = new HashMap<>();

        try {
            String query = "SELECT chapter_number_from, verse_number_from, text  FROM commentaries " +
                    " WHERE book_number = ? AND marker = ? ORDER BY marker";
            innerCursor = openDataBaseNoHelper(DB_NAME_BIBLE_CONTENT).rawQuery(query, new String[] {String.valueOf(book_number), String.valueOf(marker)});
            if (innerCursor.moveToFirst()) {
                rowCount = innerCursor.getCount();
                for (i = 0; i < rowCount; i++) {
                    int chapterCol = innerCursor.getColumnIndex(BibleContracts.CommentariesContract.COL_CHAPTER_FROM);
                    int verseCol = innerCursor.getColumnIndex(BibleContracts.CommentariesContract.COL_VERSE_FROM);
                    int textCol = innerCursor.getColumnIndex(BibleContracts.CommentariesContract.COL_TEXT);
                    int chapter  = innerCursor.getInt(chapterCol);
                    int verse = innerCursor.getInt(verseCol);
                    String text = Html.fromHtml(innerCursor.getString(textCol)).toString();
                    arrToReturn = text.split(";");//|\.;
                    //listString.add(text);
//                    String textToBeParsed = "<b>" + verse + "</b>" + ". " + text.trim();
//                    Spanned textSpanned;
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                        textSpanned = Html.fromHtml(textToBeParsed, Html.FROM_HTML_MODE_COMPACT);
//                    } else {
//                        textSpanned = Html.fromHtml(textToBeParsed);
//                    }

                    innerCursor.moveToNext();
                }
            }
            innerCursor.close();
        } catch (Exception e) {
        }

        return arrToReturn;
    }

//    public ArrayList<Verse> getVersesSimple(int book_id, int chapter_number) {
//        Cursor innerCursor;
//        int rowCount;
//        int i;
//
//        ArrayList<Verse> list = new ArrayList();
//        try {
//            innerCursor = openDataBaseNoHelper(DB_NAME_BIBLE_CONTENT).rawQuery("SELECT  verse , text FROM verses WHERE book_number = ? AND chapter = ? ORDER BY verse", new String[] {String.valueOf(book_id), String.valueOf(chapter_number)});
//            if (innerCursor.moveToFirst()) {
//                rowCount = innerCursor.getCount();
//                for (i = 0; i < rowCount; i++) {
//                    int verseCol = innerCursor.getColumnIndex(BibleContracts.VersesContract.COL_VERSE);
//                    int textCol = innerCursor.getColumnIndex(BibleContracts.VersesContract.COL_TEXT);
//                    int verse = innerCursor.getInt(verseCol);
//                    String text = innerCursor.getString(textCol);
//                    String textParsed;
//
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                        textParsed = Html.fromHtml(text, Html.FROM_HTML_MODE_COMPACT).toString();
//                    } else {
//                        textParsed = Html.fromHtml(text).toString();
//                    }
//
//                    String finalText = verse + ". "+textParsed.trim();
//                    list.add(new Verse(book_id, chapter_number, verse, finalText, 0));
//                    innerCursor.moveToNext();
//                }
//            }
//            innerCursor.close();
//        } catch (Exception e) {
//        }
//        return list;
//    }

    public SQLiteDatabase openDataBaseNoHelper(String db_name) throws SQLException {
        String myPath = this.myContext.getDatabasePath(db_name).getPath();
        return SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
    }

    public Cursor query(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit) {
        return myDataBase.query(table, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
    }
}
