package com.michaelzap94.santabiblia.DatabaseHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;

import com.michaelzap94.santabiblia.BuildConfig;
import com.michaelzap94.santabiblia.models.Book;
import com.michaelzap94.santabiblia.models.Label;
import com.michaelzap94.santabiblia.models.SearchResult;
import com.michaelzap94.santabiblia.models.VersesMarked;
import com.michaelzap94.santabiblia.utilities.BookHelper;
import com.michaelzap94.santabiblia.utilities.CommonMethods;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ContentDBHelper extends SQLiteOpenHelper {
    private static final String TAG = "ContentDBHelper";
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
                    int perCol = innerCursor.getColumnIndex("permanent");
                    String name = innerCursor.getString(nameCol);
                    String color = innerCursor.getString(colorCol);
                    int id = innerCursor.getInt(idCol);
                    int permanent = innerCursor.getInt(perCol);

                    list.add(new Label(id, name, color, permanent));
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
    public boolean insertSelectedItemsBulkTransaction(String uuid, Label label, int book_number, int chapter_number, String note, List<Integer> selectedItems) {
        boolean success = true;
        int label_id = label.getId();
        String label_name = label.getName();
        String label_color = label.getColor();
        List<List<Integer>> versesGroups = BookHelper.getVersesSelectedResults(selectedItems);//[[1,2,3],[6,7],[9]]
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        String uniqueID = (uuid != null) ? uuid : UUID.randomUUID().toString();
        try{
            ContentValues cv = new ContentValues();
            cv.put("label_id", label_id);
            cv.put("label_name", label_name);
            cv.put("label_color", label_color);
            cv.put("book_number", book_number);
            cv.put("chapter", chapter_number);
            if(note == null || note.equals("")){
                cv.putNull("note");
            } else {
                cv.put("note", note);
            }
            cv.put("UUID", uniqueID);
            for (int i = 0; i < versesGroups.size(); i++) {
                List<Integer> currentGroup = versesGroups.get(i);
                int verseFrom = (currentGroup.get(0)+1);
                int verseTo = (currentGroup.get(currentGroup.size()-1)+1);
                cv.put("verseFrom", verseFrom);
                cv.put("verseTo", (verseTo));
                db.insert("verses_marked", null, cv);
            }
            db.setTransactionSuccessful();
        } catch (Exception e){
            e.printStackTrace();
            success = false;
        } finally {
            db.endTransaction();
        }
        //insert uuid in verses_learned
        if(success && label_id == CommonMethods.LABEL_ID_MEMORIZE){
            insertVersesLearned(uniqueID, 0);
        }
        return success;
    }
    public boolean updateSelectedItemsBulkTransaction(String uuid, Label label, int book_number, int chapter_number, String note, List<Integer> selectedItems){
        //if user removed all verses in the Edit window, then delete and do not update.
        boolean deletePermanently = (selectedItems.size() == 0) ? true : false;
        boolean deleteSuccess = deleteVersesMarkedGroup(label.getId(), uuid);
        boolean success = false;
        if(deleteSuccess){
            if(deletePermanently){
                success = true;
            } else {
                success = insertSelectedItemsBulkTransaction(uuid, label, book_number, chapter_number, note, selectedItems);
            }
        }
        return success;
    }
    public boolean insertSelectedItems(Label label, int book_number, int chapter_number, String note, List<Integer> selectedItems) {
        int label_id = label.getId();
        String label_name = label.getName();
        String label_color = label.getColor();
        List<List<Integer>> versesGroups = BookHelper.getVersesSelectedResults(selectedItems);//[[1,2,3],[6,7],[9]]
        for (int i = 0; i < versesGroups.size(); i++) {
            List<Integer> currentGroup = versesGroups.get(i);
            int verseFrom = (currentGroup.get(0)+1);
            int verseTo = (currentGroup.get(currentGroup.size()-1)+1);
            boolean success = insertVersesMarked(label_id, label_name, label_color, book_number, chapter_number, verseFrom, verseTo, note);
            if(success == false){
                return false;
            }
        }
        return true;
    }
    public boolean insertVersesMarked(int label_id, String label_name, String label_color, int book_number, int chapter_number, int verseFrom, int verseTo, String note) {
        ContentValues cv = new ContentValues();
        cv.put("label_id", label_id);
        cv.put("label_name", label_name);
        cv.put("label_color", label_color);
        cv.put("book_number", book_number);
        cv.put("chapter", chapter_number);
        cv.put("verseFrom", verseFrom);
        cv.put("verseTo", (verseTo));
        cv.put("note", (!note.equals("") ?  note : "NULL"));
        return this.getWritableDatabase().insert("verses_marked",null, cv) > 0;
    }
    public ArrayList<VersesMarked> getVersesMarked(int label_id, String _uuid, int learned) {
        int labelSpecificRowsCount;
        int i;
        HashMap<String,Integer> history = new HashMap<>();
        ArrayList<VersesMarked> list = new ArrayList<>();
        try {
            String query;
            if(learned > -1){
                query ="SELECT verses_marked.UUID, verses_marked.label_name,verses_marked.label_color,verses_marked.label_permanent,verses_marked.book_number,verses_marked.chapter,verses_marked.verseFrom,verses_marked.verseTo,verses_marked.note " +
                        " FROM verses_marked LEFT JOIN verses_learned ON verses_marked.UUID = verses_learned.UUID WHERE verses_marked.label_id = " + CommonMethods.LABEL_ID_MEMORIZE + " AND verses_learned.learned=" + learned + " ORDER BY verses_learned.priority, verses_marked.date_updated";
            } else {
                query = (_uuid == null) ? "SELECT * FROM verses_marked WHERE label_id=" + label_id : "SELECT * FROM verses_marked WHERE label_id=" + label_id + " AND UUID ='" + _uuid + "'";
            }
            Cursor labelSpecificRows = this.getReadableDatabase().rawQuery(query, null);
            if (labelSpecificRows.moveToFirst()) {
                labelSpecificRowsCount = labelSpecificRows.getCount();
                for (i = 0; i < labelSpecificRowsCount; i++) {
                    //int _idCol= labelSpecificRows.getColumnIndex("_id");
                    int uuidCol = labelSpecificRows.getColumnIndex("UUID");
                    int label_nameCol= labelSpecificRows.getColumnIndex("label_name");
                    int label_colorCol= labelSpecificRows.getColumnIndex("label_color");
                    int label_perCol= labelSpecificRows.getColumnIndex("label_permanent");
                    int book_numberCol= labelSpecificRows.getColumnIndex("book_number");
                    int chapterCol= labelSpecificRows.getColumnIndex("chapter");
                    int verseFromCol= labelSpecificRows.getColumnIndex("verseFrom");
                    int verseToCol= labelSpecificRows.getColumnIndex("verseTo");
                    int noteCol= labelSpecificRows.getColumnIndex("note");
                    //int _id = labelSpecificRows.getInt(_idCol);
                    String uuid = labelSpecificRows.getString(uuidCol);
                    String label_name = labelSpecificRows.getString(label_nameCol);
                    String label_color = labelSpecificRows.getString(label_colorCol);
                    int label_permanent = labelSpecificRows.getInt(label_perCol);
                    int book_number = labelSpecificRows.getInt(book_numberCol);
                    int chapter = labelSpecificRows.getInt(chapterCol);
                    int verseFrom = labelSpecificRows.getInt(verseFromCol);
                    int verseTo = labelSpecificRows.getInt(verseToCol);
                    String note = null;
                    if(!labelSpecificRows.isNull(noteCol)){
                        note = labelSpecificRows.getString(noteCol);
                    }

                    Label specificLabel = new Label(label_id, label_name, label_color, label_permanent);
                    Book specificBook = BookHelper.getBook(book_number);
                    String innerQuery = "SELECT verse , text FROM verses WHERE " +
                                                                        "book_number = "+book_number+" AND " +
                                                                        "chapter = "+chapter+" AND " +
                                                                        "verse BETWEEN "+verseFrom+" AND "+verseTo+" " +
                                                                        "ORDER BY book_number, chapter, verse";
//                    if(verseFrom == verseTo){
//                        innerQuery = "SELECT verse , text FROM verses WHERE book_number = ? AND chapter = ? ORDER BY book_number, chapter, verse";
//                    } else {
//                        innerQuery = "SELECT verse , text FROM verses WHERE book_number = ? AND chapter = ? ORDER BY book_number, chapter, verse";
//                    }
//                    Cursor innerCursor = this.getReadableDatabase().rawQuery(innerQuery, null);
                    Integer  indexIfInHistory = history.get(uuid);
                    Cursor innerCursor = BibleDBHelper.getInstance(context).openDataBaseNoHelper(BibleDBHelper.getSelectedBibleName(context)).rawQuery(innerQuery, null);
                    if (innerCursor.moveToFirst()) {
                        do{
                            int verseCol = innerCursor.getColumnIndex(BibleContracts.VersesContract.COL_VERSE);
                            int textCol = innerCursor.getColumnIndex(BibleContracts.VersesContract.COL_TEXT);
                            int verse = innerCursor.getInt(verseCol);
                            String text = innerCursor.getString(textCol).trim();
                            //if one verse only OR this is the first verse from the query results.
                            //therefore, we'll only create one VersesMarked object and then add to it the rest of the verses.
                            if ((verseFrom == verseTo || verseFrom == verse) && (indexIfInHistory == null)) {
                                VersesMarked innerVerseMarked = new VersesMarked(uuid, specificBook, specificLabel, chapter, verse, text, note);
                                list.add(innerVerseMarked);
                                history.put(uuid, (list.size() - 1));//put this as seen and the index where inserted in HISTORY
                            } else {//if more than one verse and this is not the first verse
                                ((VersesMarked) list.get(history.get(uuid))).addToVerseTextDict(verse, text);
                            }
                        } while(innerCursor.moveToNext());
                    }

                    labelSpecificRows.moveToNext();
                }
            }
            } catch (Exception e){
        }
        return list;
    }
    public ArrayList<VersesMarked> getVersesMarkedByUUID(ArrayList<Label> listOfLabels){
        ArrayList<VersesMarked> finalArray = new ArrayList<>();
        for (int x = 0; x < listOfLabels.size(); x++) {
            Label oneLabel = listOfLabels.get(x);
            finalArray.addAll(getVersesMarked(oneLabel.getId(), oneLabel.getUUID(), -1));
        }
        return finalArray;
    }
    public boolean deleteVersesMarkedGroup(int label_id, String uuid){
        boolean success = false;
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeletedVersesMarked = db.delete("verses_marked", "label_id = ? AND UUID = ?", new String[]{String.valueOf(label_id), uuid});
        if(rowsDeletedVersesMarked > 0 && label_id == CommonMethods.LABEL_ID_MEMORIZE) {
            success = deleteVersesLearned(db, uuid);
        } else {
            success = true;
        }
        return success;
    }
    public int getVersesMarkedNumber(int label_id) {
        try {
            String query = "SELECT * FROM verses_marked WHERE label_id=" + label_id;
            Cursor labelSpecificRows = this.getReadableDatabase().rawQuery(query, null);
            return labelSpecificRows.getCount();
        } catch (Exception e) {
        }
        return -1;
    }
    public boolean editVersesLearned(String uuid, int learned){
        ContentValues cv = new ContentValues();
        cv.put("learned", learned);
        SQLiteDatabase db = this.getWritableDatabase();
        return db.update("verses_learned", cv, "UUID=?", new String[]{uuid}) > 0;
    }
    public boolean insertVersesLearned(String uuid, int learned){
        boolean success = false;
        ContentValues cv = new ContentValues();
        cv.put("UUID", uuid);
        cv.put("learned", learned);
        cv.put("label_id", CommonMethods.LABEL_ID_MEMORIZE);
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsUpdated = db.update("verses_learned", cv, "UUID=?", new String[]{uuid});
        if(rowsUpdated <= 0) {
            success = db.insert("verses_learned",null, cv) > 0;
        } else {
            success = true;
        }
        return success;
    }
    public boolean deleteVersesLearned(SQLiteDatabase _db, String uuid){
        SQLiteDatabase db = (_db == null) ? this.getWritableDatabase() : _db;
        return db.delete("verses_learned", "UUID='" + uuid + "'", null) > 0;
    }
    public ArrayList<VersesMarked> getVersesMarkedLearned(int learned){
        return getVersesMarked(CommonMethods.LABEL_ID_MEMORIZE, null, learned);
    }

    public int getVersesLearnedNumber() {
        try {
            String query = "SELECT * FROM verses_learned";
            Cursor labelSpecificRows = this.getReadableDatabase().rawQuery(query, null);
            return labelSpecificRows.getCount();
        } catch (Exception e) {
        }
        return -1;
    }


    //SEARCH IN NOTES========================================================================================
//    public ArrayList<SearchResult> searchInNotes(String input) {
//        ArrayList<SearchResult> results = new ArrayList<>();
//        int labelSpecificRowsCount;
//        try {
//            String query = "SELECT * FROM verses_marked " +
//                    " WHERE REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(note,'\u00C1','A'), '\u00C9','E'),'\u00CD','I'),'\u00D3','O'),'\u00DA','U'),'\u00e1','a'), '\u00e9','e'),'\u00ed','i'),'\u00f3','o'),'\u00fa','u'),'.',''),':',''),';',''),'?',''),'\u00bf',''),'\u00a1',''),'!',''),'(',''),')','') " +
//                    " LIKE '%" + input.trim() + "%' ORDER BY topic, definition";
//            //String query = "SELECT * FROM dictionary WHERE topic LIKE '%" + input.toUpperCase() + "%' ORDER BY topic, definition COLLATE UNICODE";
//            Cursor labelSpecificRows = this.getReadableDatabase().rawQuery(query, null);
//            if (labelSpecificRows.moveToFirst()) {
//                labelSpecificRowsCount = labelSpecificRows.getCount();
//                for (int i = 0; i < labelSpecificRowsCount; i++) {
//                    int _idCol= labelSpecificRows.getColumnIndex("_id");
//                    int uuidCol = labelSpecificRows.getColumnIndex("UUID");
//                    int label_nameCol= labelSpecificRows.getColumnIndex("label_name");
//                    int label_colorCol= labelSpecificRows.getColumnIndex("label_color");
//                    int label_perCol= labelSpecificRows.getColumnIndex("label_permanent");
//                    int book_numberCol= labelSpecificRows.getColumnIndex("book_number");
//                    int chapterCol= labelSpecificRows.getColumnIndex("chapter");
//                    int verseFromCol= labelSpecificRows.getColumnIndex("verseFrom");
//                    int verseToCol= labelSpecificRows.getColumnIndex("verseTo");
//                    int noteCol= labelSpecificRows.getColumnIndex("note");
//                    int _id = labelSpecificRows.getInt(_idCol);
//                    String uuid = labelSpecificRows.getString(uuidCol);
//                    String label_name = labelSpecificRows.getString(label_nameCol);
//                    String label_color = labelSpecificRows.getString(label_colorCol);
//                    int label_permanent = labelSpecificRows.getInt(label_perCol);
//                    int book_number = labelSpecificRows.getInt(book_numberCol);
//                    int chapter = labelSpecificRows.getInt(chapterCol);
//                    int verseFrom = labelSpecificRows.getInt(verseFromCol);
//                    int verseTo = labelSpecificRows.getInt(verseToCol);
//                    String note = null;
//                    if(!labelSpecificRows.isNull(noteCol)){
//                        note = labelSpecificRows.getString(noteCol);
//                    }
//
//                    Label specificLabel = new Label(label_id, label_name, label_color, label_permanent);
//                    Book specificBook = BookHelper.getBook(book_number);
//                    String innerQuery = "SELECT verse , text FROM verses WHERE " +
//                            "book_number = "+book_number+" AND " +
//                            "chapter = "+chapter+" AND " +
//                            "verse BETWEEN "+verseFrom+" AND "+verseTo+" " +
//                            "ORDER BY book_number, chapter, verse";
////                    if(verseFrom == verseTo){
////                        innerQuery = "SELECT verse , text FROM verses WHERE book_number = ? AND chapter = ? ORDER BY book_number, chapter, verse";
////                    } else {
////                        innerQuery = "SELECT verse , text FROM verses WHERE book_number = ? AND chapter = ? ORDER BY book_number, chapter, verse";
////                    }
////                    Cursor innerCursor = this.getReadableDatabase().rawQuery(innerQuery, null);
//                    Integer  indexIfInHistory = history.get(uuid);
//                    Cursor innerCursor = BibleDBHelper.getInstance(context).openDataBaseNoHelper(BibleDBHelper.DB_NAME_BIBLE_CONTENT).rawQuery(innerQuery, null);
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
//
//                    labelSpecificRows.moveToNext();
//                }
//            }
//            labelSpecificRows.close();
//        } catch (Exception e) {
//        }
//        return results;
//    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE labels (_id INTEGER PRIMARY KEY, name VARCHAR NOT NULL, color VARCHAR NOT NULL, permanent INTEGER DEFAULT 0)");
        db.execSQL("CREATE TABLE verses_marked (_id INTEGER PRIMARY KEY, label_id INTEGER NOT NULL, book_number INTEGER NOT NULL, chapter INTEGER NOT NULL, verseFrom INTEGER NOT NULL, verseTo INTEGER NOT NULL, " +
                " label_name VARCHAR NOT NULL, label_color VARCHAR NOT NULL, label_permanent INTEGER DEFAULT 0, note VARCHAR, date_created datetime DEFAULT current_timestamp, date_updated datetime DEFAULT current_timestamp, UUID VARCHAR NOT NULL, state INTEGER DEFAULT 0," +
                " FOREIGN KEY (label_id) REFERENCES labels (_id) ON DELETE CASCADE)");
        db.execSQL("CREATE TABLE verses_learned (_id INTEGER PRIMARY KEY, UUID VARCHAR NOT NULL, label_id INTEGER NOT NULL, learned INTEGER DEFAULT 0, priority INTEGER DEFAULT 0)");


        db.execSQL("INSERT INTO labels (name,color,permanent) VALUES( \"Memorize\", \"#00ff00\", 1)");
        db.execSQL("INSERT INTO labels (name,color,permanent) VALUES( \"Favourites\", \"#ffd700\", 1)");
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS labels");
        db.execSQL("DROP TABLE IF EXISTS verses_marked");
        onCreate(db);
    }

    @Override
    public void onConfigure(SQLiteDatabase db){
        db.setForeignKeyConstraintsEnabled(true);
    }
}
