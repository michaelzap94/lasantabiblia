package com.michaelzap94.santabiblia.DatabaseHelper;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.text.Html;
import android.text.Spanned;

import com.michaelzap94.santabiblia.models.Book;
import com.michaelzap94.santabiblia.models.Concordance;
import com.michaelzap94.santabiblia.models.Verse;
import com.michaelzap94.santabiblia.utilities.BookHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
                    String text = innerCursor.getString(textCol);
                    //String parsedText = Html.fromHtml(text).toString();
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

    public ArrayList<Verse> executeQuery(String query){
        ArrayList<Verse> versesFound = new ArrayList<>();
        Cursor innerCursor;
        int rowCount;
        int i;
        try {
            innerCursor = openDataBaseNoHelper(DB_NAME_BIBLE_CONTENT).rawQuery(query, null);
            if (innerCursor.moveToFirst()) {
                rowCount = innerCursor.getCount();
                for (i = 0; i < rowCount; i++) {
                    int verseCol = innerCursor.getColumnIndex(BibleContracts.VersesContract.COL_VERSE);
                    int textCol = innerCursor.getColumnIndex(BibleContracts.VersesContract.COL_TEXT);
                    int book_numberCol = innerCursor.getColumnIndex(BibleContracts.VersesContract.COL_BOOK_ID);
                    int chapter_numberCol = innerCursor.getColumnIndex(BibleContracts.VersesContract.COL_CHAPTER);
                    int verse = innerCursor.getInt(verseCol);
                    String text = innerCursor.getString(textCol).trim();
                    int book_number = innerCursor.getInt(book_numberCol);
                    int chapter_number = innerCursor.getInt(chapter_numberCol);

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

                    versesFound.add(new Verse(book_number, chapter_number, verse, textSpanned, 0));

                    innerCursor.moveToNext();
                }
            }
            innerCursor.close();
        } catch (Exception e) {
        }
        return versesFound;
    }

    public HashMap<String, ArrayList<Verse>> getVersesFromCommentaries(String textWithHTML) {
        boolean containsHyphon = textWithHTML.contains(">—<");
        HashMap<String, ArrayList<Verse>> versesDictionary = new HashMap<>();

        String line = textWithHTML;
        Pattern pattern = Pattern.compile("B:(\\d+) (\\d+):(\\d+)-?(\\d+)?");
        Matcher matcher = pattern.matcher(line);
        int groups = 0;
        while (matcher.find()){
            groups++;
        }
        System.out.println("groups: " + groups);
        matcher.reset();
        if(groups == 1 && !containsHyphon) {
            matcher.find();//triggers the first match;
            String book = matcher.group(1);
            String chapter = matcher.group(2);
            String verseFirst = matcher.group(3);
            String verseSecond = matcher.group(4);
            String query = makeQuery(book, chapter,verseFirst, verseSecond);
            String title = makeTitle(book, chapter, verseFirst, verseSecond);
            ArrayList<Verse> versesFound = executeQuery(query);
            versesDictionary.put(title,versesFound);
        } else if( groups > 1 && !containsHyphon){
            //ArrayList<String> queries = new ArrayList<>();
            String[] queries = new String[groups];
            String[] titles = new String[groups];
            int counter = 0;
            while (matcher.find()) {
                System.out.println("=========reading group: " + counter + "===================");
                System.out.println("string: " + matcher.group(0));
                System.out.println("book: " + matcher.group(1));
                System.out.println("chapter: " + matcher.group(2));
                System.out.println("verseFirst: " + matcher.group(3));
                System.out.println("verseSecond: " + matcher.group(4));
                String book = matcher.group(1);
                String chapter = matcher.group(2);
                String verseFirst = matcher.group(3);
                String verseSecond = matcher.group(4);
                String query = makeQuery(book, chapter,verseFirst, verseSecond);
                queries[counter] = query;
                titles[counter] = makeTitle(book, chapter, verseFirst, verseSecond);
                counter++;
            }
            // use queries.size to iterate trough the array and execute the queries
            for(int i = 0; i<queries.length; i++){
                ArrayList<Verse> versesFound = executeQuery(queries[i]);
                versesDictionary.put(titles[i], versesFound);
            }

        } else if(groups == 2 && containsHyphon){
            //ArrayList<String> queries = new ArrayList<>();
            String[] queries = new String[groups];
            String[] books = new String[2];
            String[] chapters = new String[2];
            String[] verses = new String[2];
            int counter = 0;
            while (matcher.find()) {
                System.out.println("=========reading group: " + counter + "===================");
                System.out.println("string: " + matcher.group(0));
                System.out.println("book: " + matcher.group(1));
                System.out.println("chapter: " + matcher.group(2));
                System.out.println("verseFirst: " + matcher.group(3));
                //System.out.println("verseSecond: " + matcher.group(4));//SHOULD BE NULL ALWAYS
                books[counter] = matcher.group(1);
                chapters[counter] = matcher.group(2);
                verses[counter] = matcher.group(3);

                counter++;
            }

            if(books.length == 2 && chapters.length == 2 && verses.length == 2){
                String titleWithHyphon = makeTitle(books[0], chapters[0], verses[0], null) + " — " + makeTitle(books[1], chapters[1], verses[1], null);
                String queryForHyphon = "SELECT * FROM verses WHERE (book_number >= "+ books[0] +" AND chapter >= "+ chapters[0] +" AND verse >= "+ verses[0] +") " +
                        "AND (book_number <= "+ books[1] +" AND chapter <= "+ chapters[1] +" AND verse <= "+ verses[1] +")";
                ArrayList<Verse> versesFound = executeQuery(queryForHyphon);
                versesDictionary.put(titleWithHyphon, versesFound);
            }
        }
        return versesDictionary;
    }

    public String makeQuery(String book, String chapter, String verseFirst, String verseSecond){
        String query = null;
        if(verseSecond == null && verseFirst == null){
            //NORMAL QUERY get all chapters for a book and move user to 0
            query = "SELECT * FROM verses WHERE book_number= " + book + " AND chapter = " + chapter + " ORDER BY verse";
        } else if(verseSecond == null && verseFirst != null){
            query = "SELECT * FROM verses WHERE book_number= " + book + " AND chapter = " + chapter + " AND verse = " + verseFirst + " ORDER BY verse";
        } else {
            query = "SELECT * FROM verses WHERE book_number= " + book + " AND chapter = " + chapter + " AND verse >= " + verseFirst + " AND verse <= " + verseSecond + " ORDER BY verse";
        }
        return query;
    }

    public String makeTitle(String book, String chapter, String verseFirst, String verseSecond){
        Book bookObject = BookHelper.getBook(Integer.parseInt(book));
        String title = bookObject.getName() + " " + chapter;
        if(verseFirst != null){
            title = title + ":" + verseFirst;
        }
        if(verseSecond != null) {
            title = title + "-" + verseSecond;
        }
        return title;
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
