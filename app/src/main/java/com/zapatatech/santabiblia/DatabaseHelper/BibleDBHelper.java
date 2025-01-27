package com.zapatatech.santabiblia.DatabaseHelper;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.util.Log;

import androidx.preference.PreferenceManager;

import com.zapatatech.santabiblia.models.Book;
import com.zapatatech.santabiblia.models.Label;
import com.zapatatech.santabiblia.models.SearchResult;
import com.zapatatech.santabiblia.models.Verse;
import com.zapatatech.santabiblia.utilities.BookHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.zapatatech.santabiblia.utilities.CommonMethods.MAIN_BIBLE_SELECTED;

public class BibleDBHelper {

    private static final String TAG = "BibleDBHelper";
    //BIBLE shipped with APK in assets
    //change according to language MAYBE?
    private static String DB_NAME_MAIN_BIBLE_CONTENT = "RVR60.type-bible.db";
    //------------------------------------------------
    private static String DB_NAME_CURRENT_BIBLE_CONTENT = null;
    //TODO: remove this and only allow this files if downloaded, MAYBE?
    public static final String DB_NAME_BIBLE_CONCORDANCE = "concordance.db";
    public static final String DB_NAME_BIBLE_DICTIONARY = "ibalpedic.db";
    public static final String DB_NAME_BIBLE_COMMENTARIES = "RVR60commentaries.db";
    public static final int DB_VERSION = 1;
    private Context myContext;
    String DB_PATH = null;
    String DB_NAME = null;
    //GETTERS=======================================================================================
    public static String getMainBibleName(){
        return DB_NAME_MAIN_BIBLE_CONTENT;
    }
    public static String getSelectedBibleName(Context context){
        String bibleSelected;
        if(DB_NAME_CURRENT_BIBLE_CONTENT == null){
            //DB_NAME_CURRENT_BIBLE_CONTENT is null for the instance of this SESSION
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            String bibleSelectedInMemory = prefs.getString(MAIN_BIBLE_SELECTED, null);
            if(bibleSelectedInMemory == null){
                //DB_NAME_CURRENT_BIBLE_CONTENT is null in memory, so use DB_NAME_MAIN_BIBLE_CONTENT
                bibleSelected = getMainBibleName();
            } else {
                bibleSelected = bibleSelectedInMemory;
            }
        } else {
            bibleSelected = DB_NAME_CURRENT_BIBLE_CONTENT;
        }
        return bibleSelected;
    }
    //SETTERS=======================================================================================
//    public static void setMainBibleName(String newMainBibleName){
//        DB_NAME_MAIN_BIBLE_CONTENT = newMainBibleName;
//    }
//    public static String setSelectedBibleName(){
//        return DB_NAME_MAIN_BIBLE_CONTENT;
//    }
    //=======================================================================================
    //so we only create one db instance for the duration of the session
    private HashMap<String, SQLiteDatabase> existingDBs = new HashMap<>();
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

    public TreeMap<Integer, ArrayList<Label>> getLabelsOfVersesMarked(int book_number, int chapter) {
        int labelSpecificRowsCount;
        int i;
//        HashMap<String,Integer> history = new HashMap<>();
//        ArrayList<VersesMarked> versesMarkedList = new ArrayList<>();
        TreeMap<Integer, ArrayList<Label>> versesMarkedListOfLabels = new TreeMap<>();
        try {            //String query = "SELECT * FROM verses_marked " + "JOIN categories ON teams.cat = catagories.Id WHERE fav=0)";
            Cursor labelSpecificRows = ContentDBHelper.getInstance(myContext).getVersesMarkedCursor(book_number, chapter);
            if (labelSpecificRows.moveToFirst()) {
                labelSpecificRowsCount = labelSpecificRows.getCount();
                for (i = 0; i < labelSpecificRowsCount; i++) {
                    //int _idCol= labelSpecificRows.getColumnIndex("_id");
                    int uuidCol = labelSpecificRows.getColumnIndex("UUID");
                    int label_idCol = labelSpecificRows.getColumnIndex("label_id");
                    int label_nameCol= labelSpecificRows.getColumnIndex("label_name");
                    int label_colorCol= labelSpecificRows.getColumnIndex("label_color");
                    int label_perCol= labelSpecificRows.getColumnIndex("label_permanent");
                    //int book_numberCol= labelSpecificRows.getColumnIndex("book_number");
                    //int chapterCol= labelSpecificRows.getColumnIndex("chapter");
                    int verseFromCol= labelSpecificRows.getColumnIndex("verseFrom");
                    int verseToCol= labelSpecificRows.getColumnIndex("verseTo");
                    int noteCol= labelSpecificRows.getColumnIndex("note");
                    //int _id = labelSpecificRows.getInt(_idCol);
                    String uuid = labelSpecificRows.getString(uuidCol);
                    String label_id = labelSpecificRows.getString(label_idCol);
                    String label_name = labelSpecificRows.getString(label_nameCol);
                    String label_color = labelSpecificRows.getString(label_colorCol);
                    int label_permanent = labelSpecificRows.getInt(label_perCol);
                    //int book_number = labelSpecificRows.getInt(book_numberCol);
                    //int chapter = labelSpecificRows.getInt(chapterCol);
                    int verseFrom = labelSpecificRows.getInt(verseFromCol);
                    int verseTo = labelSpecificRows.getInt(verseToCol);
                    String note = null;
                    if(!labelSpecificRows.isNull(noteCol)){
                        note = labelSpecificRows.getString(noteCol);
                    }
                    Label specificLabel = new Label(label_id, label_name, label_color, label_permanent, uuid);
                    Book specificBook = BookHelper.getInstance().getBook(book_number);



                    for (int j = verseFrom; j <= verseTo; j++) {
                        //if verse has not been seen yet, add it along with the label for this row result
                        if(!versesMarkedListOfLabels.containsKey(j)) {
                            versesMarkedListOfLabels.put(j, new ArrayList<Label>(Arrays.asList(specificLabel)));
                        } else {
                            ArrayList<Label> listOfLabels = versesMarkedListOfLabels.get(j);
                            listOfLabels.add(specificLabel);
                            versesMarkedListOfLabels.put(j, listOfLabels);
                        }



                        //if one verse only OR this is the first verse from the query results.
                        //therefore, we'll only create one VersesMarked object and then add to it the rest of the verses.
//                        if ((verseFrom == verseTo || verseFrom == verse) && (indexIfInHistory == null)) {
//                                VersesMarked innerVerseMarked = new VersesMarked(_id, uuid, specificBook, specificLabel, chapter, verse, text, note);
//                                list.add(innerVerseMarked);
//                                history.put(uuid, (list.size() - 1));//put this as seen and the index where inserted in HISTORY
//                            } else {//if more than one verse and this is not the first verse
//                                ((VersesMarked) list.get(history.get(uuid))).addToVerseTextDict(verse, text);
//                            }
                    }


//                    String innerQuery = "SELECT verse , text FROM verses WHERE " +
//                            "book_number = "+book_number+" AND " +
//                            "chapter = "+chapter+" AND " +
//                            "verse BETWEEN "+verseFrom+" AND "+verseTo+" " +
//                            "ORDER BY book_number, chapter, verse";
//                    if(verseFrom == verseTo){
//                        innerQuery = "SELECT verse , text FROM verses WHERE book_number = ? AND chapter = ? ORDER BY book_number, chapter, verse";
//                    } else {
//                        innerQuery = "SELECT verse , text FROM verses WHERE book_number = ? AND chapter = ? ORDER BY book_number, chapter, verse";
//                    }
//                    Cursor innerCursor = this.getReadableDatabase().rawQuery(innerQuery, null);
                    //Integer  indexIfInHistory = history.get(uuid);
                    //Cursor innerCursor = BibleDBHelper.getInstance(context).openDataBaseNoHelper(BibleDBHelper.getSelectedBibleName(this.myContext)).rawQuery(innerQuery, null);
//                    if (innerCursor.moveToFirst()) {
//                        do{
//                            int verseCol = innerCursor.getColumnIndex(BibleContracts.VersesContract.COL_VERSE);
//                            int textCol = innerCursor.getColumnIndex(BibleContracts.VersesContract.COL_TEXT);
//                            int verse = innerCursor.getInt(verseCol);
//                            String text = innerCursor.getString(textCol).trim();
//                            //if one verse only OR this is the first verse from the query results.
//                            //therefore, we'll only create one VersesMarked object and then add to it the rest of the verses.
//                            if ((verseFrom == verseTo || verseFrom == verse) && (indexIfInHistory == null)) {
//                                VersesMarked innerVerseMarked = new VersesMarked(_id, uuid, specificBook, specificLabel, chapter, verse, text, note);
//                                list.add(innerVerseMarked);
//                                history.put(uuid, (list.size() - 1));//put this as seen and the index where inserted in HISTORY
//                            } else {//if more than one verse and this is not the first verse
//                                ((VersesMarked) list.get(history.get(uuid))).addToVerseTextDict(verse, text);
//                            }
//                        } while(innerCursor.moveToNext());
//                    }

                    labelSpecificRows.moveToNext();
                }
            }
        } catch (Exception e){
        }
        return versesMarkedListOfLabels;
    }

    public ArrayList<Verse> getVerses(int book_number, int chapter_number) {

        TreeMap<Integer, ArrayList<Label>> labelsOfVersesMarked = getLabelsOfVersesMarked(book_number, chapter_number);
        //==========================================
        Cursor innerCursor;
        int rowCount;
        int i;
        Map<Integer,Boolean> history = new HashMap<>();
        ArrayList<Verse> list = new ArrayList();
        try {
            String query = "SELECT verses.verse , verses.text, stories.title, stories.verse AS story_at_verse, order_if_several FROM verses LEFT JOIN stories" +
                    " ON verses.book_number =  stories.book_number AND verses.chapter =  stories.chapter AND verses.verse = stories.verse " +
                    " WHERE verses.book_number = ? AND verses.chapter = ? ORDER BY verses.book_number, verses.chapter, verses.verse, stories.verse";
            innerCursor = openDataBaseNoHelper(getSelectedBibleName(this.myContext)).rawQuery(query, new String[] {String.valueOf(book_number), String.valueOf(chapter_number)});
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
                    String[] textTitleReferences = null;
                    //int titleAtVerse;
                    int orderIfSeveralTitles;
                    if(!innerCursor.isNull(textTitleCol)){
                        textTitle = innerCursor.getString(textTitleCol);
                        //titleAtVerse = innerCursor.getInt(titleAtVerseCol);
                        //orderIfSeveralTitles = innerCursor.getInt(orderIfSeveralTitlesCol);
                        if(textTitle.contains("<x>")){
                            textTitleReferences = textTitle.replace("(", "").replace(")", "").split(";");
                            textTitle = null;
                        }
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

                    SpannableString ssTextVerse = new SpannableString(textSpanned);
                    int ssTextVerseLength = ssTextVerse.length();
                    //CHECK IF THIS VERSE SHOULD HAVE A COLOR AND LABEL ASSIGNED TO IT.
                    ArrayList<Label> listOfLabels = null;
                    if(labelsOfVersesMarked.containsKey(verse)){
                        listOfLabels = labelsOfVersesMarked.get(verse);
                        int labelsTotal = listOfLabels.size();
                        int temp = (int) Math.ceil(ssTextVerseLength / labelsTotal);
                        for (int j = 0; j < labelsTotal; j++) {
                            int start = j * temp;
                            int end = start + temp;
                            Label currentLabel = listOfLabels.get(j);
                            ssTextVerse.setSpan(new BackgroundColorSpan(Color.parseColor(currentLabel.getColor())), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }
                    }

                    //If a Verse is in the array already and we see the same verse again, it's because there are 2+ titles
                    if(!history.containsKey(verse)){
                        list.add(new Verse(book_number, chapter_number, verse, textSpanned, ssTextVerse, textTitle, textTitleReferences, listOfLabels));
                        history.put(verse, true);
                    } else {
                        Verse existingVerse = list.get(verse - 1);
                        if(textTitle != null) {
                            String newTextTitle = existingVerse.getTextTitle() + "\n" + textTitle;
                            existingVerse.setTextTitle(newTextTitle);
                        }
                        existingVerse.setTextTitleRefs(textTitleReferences);
                    }

                    innerCursor.moveToNext();
                }
            }
            innerCursor.close();
        } catch (Exception e) {
        }
        return list;
    }

    public String[] getReferences(int book_number, String marker){
        //ArrayList<Concordance> list = new ArrayList();
        //String toReturn = "No element could be found";

        String[] arrToReturn = {"No element found"};
        Cursor innerCursor;
        int rowCount;
        int i;
        Map<Integer,Boolean> history = new HashMap<>();

        try {
            String query = "SELECT chapter_number_from, verse_number_from, text  FROM commentaries " +
                    " WHERE book_number = ? AND marker = ? ORDER BY marker";
            innerCursor = openDataBaseNoHelper(getSelectedBibleName(this.myContext)).rawQuery(query, new String[] {String.valueOf(book_number), String.valueOf(marker)});
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
            innerCursor = openDataBaseNoHelper(getSelectedBibleName(this.myContext)).rawQuery(query, null);
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

                    versesFound.add(new Verse(book_number, chapter_number, verse, textSpanned, null));

                    innerCursor.moveToNext();
                }
            }
            innerCursor.close();
        } catch (Exception e) {
        }
        return versesFound;
    }

    public HashMap<String, ArrayList<Verse>> getVersesFromCommentaries(String textWithHTML) {
        boolean containsXForTitle = textWithHTML.contains("<x>");

        Pattern pattern = (containsXForTitle) ? Pattern.compile("<x>(\\d+) (\\d+):(\\d+)-?(\\d+)?") : Pattern.compile("B:(\\d+) (\\d+):(\\d+)-?(\\d+)?");

        boolean containsHyphon = textWithHTML.contains(">—<");
        HashMap<String, ArrayList<Verse>> versesDictionary = new HashMap<>();

        String line = textWithHTML;
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

    public ArrayList<String[]> getBibleCompareData(int book_number, int chapter_number, ArrayList<Integer> selectedVerses, ArrayList<String> selectedBibles){
        ArrayList<String[]> arrToReturn = new ArrayList<>();
        for (String bibleDBName:selectedBibles) {
            arrToReturn.add(specificVerseGroup(bibleDBName, book_number, chapter_number, selectedVerses));
        }
        return arrToReturn;
    }

    public String[] specificVerseGroup(String bibleDBName, int book_number, int chapter_number, ArrayList<Integer> selectedVerses){
        String[] arrToReturn = new String[2];
        arrToReturn[0] = bibleDBName;
        StringBuilder content = new StringBuilder();
        //===========================================================================
        String queryStart = "SELECT verse , text FROM verses WHERE ";
        StringBuilder queryBuilder = new StringBuilder(queryStart);
        queryBuilder.append(" book_number = " + book_number);
        queryBuilder.append(" AND chapter = " + chapter_number + " AND (");
        for (int i = 0; i < selectedVerses.size(); i++) {
            queryBuilder.append(" verse = " + (selectedVerses.get(i) + 1));
            if(i != selectedVerses.size() - 1){
                queryBuilder.append(" OR ");
            } else {
                queryBuilder.append(" )");
            }
        }
        queryBuilder.append(" ORDER BY verse");
        Log.d(TAG, "specificVerseGroup: " + queryBuilder.toString());
        //===========================================================================
        try {
            Cursor innerCursor = openDataBaseNoHelper(bibleDBName).rawQuery(queryBuilder.toString(), null);
            int rowCount;
            if (innerCursor.moveToFirst()) {
                rowCount = innerCursor.getCount();
                for (int i = 0; i < rowCount; i++) {
                    int verseCol = innerCursor.getColumnIndex(BibleContracts.VersesContract.COL_VERSE);
                    int textCol = innerCursor.getColumnIndex(BibleContracts.VersesContract.COL_TEXT);

                    int verse = innerCursor.getInt(verseCol);
                    String text = innerCursor.getString(textCol).trim();

                    String textToBeParsed;
                    if(text.contains("<n>[") && text.contains("]</n>")) {
                        textToBeParsed = text.replace("<n>[","<br><b><i>(").replace("]</n>",")</i></b>");
                    } else {
                        textToBeParsed = " <b>" + verse + "</b>" + ". " + text;
                    }
                    content.append(textToBeParsed);
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
        arrToReturn[1] = content.toString();
        return arrToReturn;
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
        Book bookObject = BookHelper.getInstance().getBook(Integer.parseInt(book));
        String title = bookObject.getName() + " " + chapter;
        if(verseFirst != null){
            title = title + ":" + verseFirst;
        }
        if(verseSecond != null) {
            title = title + "-" + verseSecond;
        }
        return title;
    }

    public SQLiteDatabase openDataBaseNoHelper(String db_name) throws SQLException {

        //db connection exists(was openned) so reuse it
        if(existingDBs.containsKey(db_name)){
            return existingDBs.get(db_name);
        } else {
            String myPath = this.myContext.getDatabasePath(db_name).getPath();
            SQLiteDatabase myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
            existingDBs.put(db_name, myDataBase);
            return myDataBase;
        }


    }

    //SEARCH IN BIBLE=================================================================================================
    public ArrayList<SearchResult> searchInBible(String input) {
        ArrayList<SearchResult> results = new ArrayList<>();
        int innerCursorRowsCount;
        try {
            String query = "SELECT book_number, chapter, verse, text FROM verses " +
                    " WHERE REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(text,'\u00C1','A'), '\u00C9','E'),'\u00CD','I'),'\u00D3','O'),'\u00DA','U'),'\u00e1','a'), '\u00e9','e'),'\u00ed','i'),'\u00f3','o'),'\u00fa','u'),'.',''),',',''),':',''),';',''),'?',''),'\u00bf',''),'\u00a1',''),'!',''),'(',''),')','') " +
                    " LIKE '%" + input.trim() + "%' ORDER BY book_number, chapter, verse";
            Log.d(TAG, "searchInBible: " + query);
            Cursor innerCursor = openDataBaseNoHelper(getSelectedBibleName(this.myContext)).rawQuery(query, null);
            if (innerCursor.moveToFirst()) {
                innerCursorRowsCount = innerCursor.getCount();
                for (int i = 0; i < innerCursorRowsCount; i++) {
                    int verse = innerCursor.getInt(innerCursor.getColumnIndex(BibleContracts.VersesContract.COL_VERSE));
                    String text = innerCursor.getString(innerCursor.getColumnIndex(BibleContracts.VersesContract.COL_TEXT)).trim();
                    int book_number = innerCursor.getInt(innerCursor.getColumnIndex(BibleContracts.VersesContract.COL_BOOK_ID));
                    int chapter_number = innerCursor.getInt(innerCursor.getColumnIndex(BibleContracts.VersesContract.COL_CHAPTER));

                    Spanned definitionSpanned;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        definitionSpanned = Html.fromHtml(text, Html.FROM_HTML_MODE_COMPACT);
                    } else {
                        definitionSpanned = Html.fromHtml(text);
                    }
                    results.add(new SearchResult(book_number, chapter_number, verse, definitionSpanned));

                    innerCursor.moveToNext();
                }
            }
            innerCursor.close();
        } catch (Exception e) {
        }
        return results;
    }

    //CONCORDANCE OR DICTIONARY========================================================================================
    public ArrayList<SearchResult> searchInConcordanceOrDictionary(String input, String type) {
        String dbName = (type == "conc") ? DB_NAME_BIBLE_CONCORDANCE: DB_NAME_BIBLE_DICTIONARY;
        ArrayList<SearchResult> results = new ArrayList<>();
        int labelSpecificRowsCount;
        try {
            String query = "SELECT _id, topic, definition FROM dictionary " +
                    " WHERE REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(topic,'\u00C1','A'), '\u00C9','E'),'\u00CD','I'),'\u00D3','O'),'\u00DA','U'),'\u00e1','a'), '\u00e9','e'),'\u00ed','i'),'\u00f3','o'),'\u00fa','u'),'.',''),':',''),';',''),'?',''),'\u00bf',''),'\u00a1',''),'!',''),'(',''),')','') " +
                    " LIKE '%" + input.trim() + "%' ORDER BY topic, definition";
            //String query = "SELECT * FROM dictionary WHERE topic LIKE '%" + input.toUpperCase() + "%' ORDER BY topic, definition COLLATE UNICODE";
            Cursor cursorResults = openDataBaseNoHelper(dbName).rawQuery(query, null);
            if (cursorResults.moveToFirst()) {
                labelSpecificRowsCount = cursorResults.getCount();
                for (int i = 0; i < labelSpecificRowsCount; i++) {
                    int idCol = cursorResults.getColumnIndex("_id");
                    int topicCol = cursorResults.getColumnIndex("topic");
                    int defCol = cursorResults.getColumnIndex("definition");
                    int id = cursorResults.getInt(idCol);
                    String title = cursorResults.getString(topicCol).trim();
                    String definition = cursorResults.getString(defCol).trim();

                    Spanned definitionSpanned;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        definitionSpanned = Html.fromHtml(definition, Html.FROM_HTML_MODE_COMPACT);
                    } else {
                        definitionSpanned = Html.fromHtml(definition);
                    }

                    results.add(new SearchResult(id, title, definitionSpanned));
                    cursorResults.moveToNext();
                }
            }
            cursorResults.close();
        } catch (Exception e) {
        }
        return results;
    }
    
}
