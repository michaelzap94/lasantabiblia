package com.zapatatech.santabiblia.DatabaseHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.zapatatech.santabiblia.fragments.dialogs.VersesLearned;
import com.zapatatech.santabiblia.models.Book;
import com.zapatatech.santabiblia.models.Label;
import com.zapatatech.santabiblia.models.User;
import com.zapatatech.santabiblia.retrofit.Pojos.POJOLabel;
import com.zapatatech.santabiblia.retrofit.Pojos.POJOSyncUp;
import com.zapatatech.santabiblia.models.VersesMarked;
import com.zapatatech.santabiblia.retrofit.Pojos.POJOSyncUpHelper;
import com.zapatatech.santabiblia.retrofit.Pojos.POJOVersesLearned;
import com.zapatatech.santabiblia.retrofit.Pojos.POJOVersesMarked;
import com.zapatatech.santabiblia.utilities.BookHelper;
import com.zapatatech.santabiblia.utilities.CommonMethods;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static com.zapatatech.santabiblia.DatabaseHelper.ContentDBContracts.DB_NAME;
import static com.zapatatech.santabiblia.DatabaseHelper.ContentDBContracts.DB_VERSION;

public class ContentDBHelper extends SQLiteOpenHelper {
    private static final String TAG = "ContentDBHelper";
    private Context context;
    private static ContentDBHelper dbHelperSingleton = null;
    private final SQLiteDatabase db;
    private User user;

    /**
     * We use a Singleton to prevent leaking the SQLiteDatabase or Context.
     * @return {@link ContentDBHelper}
     */
    public static ContentDBHelper getInstance(Context c) {
        if (dbHelperSingleton == null) {
            synchronized (ContentDBHelper.class) {
                if (dbHelperSingleton == null) {
                    dbHelperSingleton = new ContentDBHelper(c);
                }
            }
        }
        return dbHelperSingleton;
    }
    public ContentDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
        //it will return null if no Credentials stored
        this.user = CommonMethods.decodeJWTAndCreateUser(context);
        this.db = getWritableDatabase();//start onCreate();

    }
    public void addUserToSingleton(Context context){this.user = CommonMethods.decodeJWTAndCreateUser(context);}
    public void removeUserFromSingleton(){
        this.user = null;
    }
    //-----------------------------------------------------------------------
    public boolean createLabel(String name, String color){
        int userId = (user == null) ? 0 : user.getUserId();
        ContentValues cv = new ContentValues();
        cv.put("user_id", userId);
        cv.put("name", name);
        cv.put("color", color);
        return this.db.insert("labels",null, cv) > 0;
    }
    public boolean editLabel(String name, String color, int id){
        ContentValues cv = new ContentValues();
        cv.put("name", name);
        cv.put("color", color);
        cv.put("state", 0);
        return this.db.update("labels", cv, "_id="+id, null) > 0;
    }
    public ArrayList<Label> getAllLabels(){
        Cursor innerCursor;
        int rowCount;
        int i;
        ArrayList<Label> list = new ArrayList();
        try {
            String query = "SELECT * FROM labels";
            innerCursor = this.db.rawQuery(query, null);
            if (innerCursor.moveToFirst()) {
                rowCount = innerCursor.getCount();
                for (i = 0; i < rowCount; i++) {

                    String name = innerCursor.getString(innerCursor.getColumnIndex("name"));
                    String color = innerCursor.getString(innerCursor.getColumnIndex("color"));
                    int id = innerCursor.getInt(innerCursor.getColumnIndex("_id"));
                    int permanent = innerCursor.getInt(innerCursor.getColumnIndex("permanent"));
                    int state = innerCursor.getInt(innerCursor.getColumnIndex("state"));

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
         return this.db.delete("labels", "_id =" + id, null) > 0;
    }
    public boolean insertSelectedItemsBulkTransaction(String uuid, Label label, int book_number, int chapter_number, String note, List<Integer> selectedItems) {
        boolean success = true;
        int userId = (user == null) ? 0 : user.getUserId();
        int label_id = label.getId();
        String label_name = label.getName();
        String label_color = label.getColor();
        List<List<Integer>> versesGroups = BookHelper.getVersesSelectedResults(selectedItems);//[[1,2,3],[6,7],[9]]
        SQLiteDatabase db = this.db;
        db.beginTransaction();
        String uniqueID = (uuid != null) ? uuid : UUID.randomUUID().toString();
        try{
            ContentValues cv = new ContentValues();
            cv.put("user_id", userId);
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
    public boolean insertVersesMarked(int label_id, String label_name, String label_color, int book_number, int chapter_number, int verseFrom, int verseTo, String note) {
        int userId = (user == null) ? 0 : user.getUserId();
        ContentValues cv = new ContentValues();
        cv.put("user_id", userId);
        cv.put("label_id", label_id);
        cv.put("label_name", label_name);
        cv.put("label_color", label_color);
        cv.put("book_number", book_number);
        cv.put("chapter", chapter_number);
        cv.put("verseFrom", verseFrom);
        cv.put("verseTo", (verseTo));
        cv.put("note", (!note.equals("") ?  note : "NULL"));
        return this.db.insert("verses_marked",null, cv) > 0;
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
            Cursor labelSpecificRows = this.db.rawQuery(query, null);
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
                    Book specificBook = BookHelper.getInstance().getBook(book_number);
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
//                    Cursor innerCursor = this.db.rawQuery(innerQuery, null);
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
        SQLiteDatabase db = this.db;
        int rowsDeletedVersesMarked = db.delete("verses_marked", "label_id = ? AND UUID = ?", new String[]{String.valueOf(label_id), uuid});
        if(rowsDeletedVersesMarked > 0 && label_id == CommonMethods.LABEL_ID_MEMORIZE) {
            success = deleteVersesLearned(db, uuid);
        } else {
            success = true;
        }
        return success;
    }
    public boolean editVersesLearned(String uuid, int learned){
        ContentValues cv = new ContentValues();
        cv.put("learned", learned);
        cv.put("state", 0);
        return this.db.update("verses_learned", cv, "UUID=?", new String[]{uuid}) > 0;
    }
    public boolean insertVersesLearned(String uuid, int learned){
        boolean success = false;
        int userId = (user == null) ? 0 : user.getUserId();
        ContentValues cv = new ContentValues();
        cv.put("user_id", userId);
        cv.put("UUID", uuid);
        cv.put("learned", learned);
        cv.put("label_id", CommonMethods.LABEL_ID_MEMORIZE);
        SQLiteDatabase db = this.db;
        int rowsUpdated = db.update("verses_learned", cv, "UUID=?", new String[]{uuid});
        if(rowsUpdated <= 0) {
            success = db.insert("verses_learned",null, cv) > 0;
        } else {
            success = true;
        }
        return success;
    }
    public boolean deleteVersesLearned(SQLiteDatabase _db, String uuid){
        SQLiteDatabase db = (_db == null) ? this.db : _db;
        return db.delete("verses_learned", "UUID='" + uuid + "'", null) > 0;
    }
    public ArrayList<VersesMarked> getVersesMarkedLearned(int learned){
        return getVersesMarked(CommonMethods.LABEL_ID_MEMORIZE, null, learned);
    }
    //-----------------------------------------------------------------------
    public int getVersesMarkedNumber(int label_id) {
        try {
            String query = "SELECT * FROM verses_marked WHERE label_id=" + label_id;
            Cursor labelSpecificRows = this.db.rawQuery(query, null);
            return labelSpecificRows.getCount();
        } catch (Exception e) {
        }
        return -1;
    }
    public int getVersesLearnedNumber() {
        try {
            String query = "SELECT * FROM verses_learned";
            Cursor labelSpecificRows = this.db.rawQuery(query, null);
            return labelSpecificRows.getCount();
        } catch (Exception e) {
        }
        return -1;
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
    //==============================================================================================
    public POJOSyncUp getSyncUp(String email){
        POJOSyncUp result = null;
        String query = "SELECT * FROM syncup WHERE email='" + email +"'";
        Cursor cursor = null;
        try{
            cursor = this.db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
//                do {
                    int version = cursor.getInt(cursor.getColumnIndex(ContentDBContracts.SYNC_UPS.COL_VERSION));
                    int state = cursor.getInt(cursor.getColumnIndex(ContentDBContracts.SYNC_UPS.COL_STATE));
                    String updated = cursor.getString(cursor.getColumnIndex(ContentDBContracts.SYNC_UPS.COL_UPDATED));
                    Log.d(TAG, "getLocalServerDBVersion: " + updated);

                    result = new POJOSyncUp(email, version, state, updated);
//                    break;
//                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "getLocalServerDBVersion: ", e);
        } finally {
            if(cursor != null){
                cursor.close();
            }
        }
        return result;
    }
    public boolean insertOrUpdateSyncUp(@NonNull String email, Integer version, Integer state, String updated){
        boolean success = false;
        ContentValues initialValues = new ContentValues();
//        initialValues.put(ContentDBContracts.SYNC_UPS.COL_ID, 1); // id will always be 1
        initialValues.put(ContentDBContracts.SYNC_UPS.COL_VERSION, email);
        if(version!=null) initialValues.put(ContentDBContracts.SYNC_UPS.COL_VERSION, version);
        if(state!=null) initialValues.put(ContentDBContracts.SYNC_UPS.COL_STATE, state);
        if(updated!=null) initialValues.put(ContentDBContracts.SYNC_UPS.COL_UPDATED, updated);

        int id = (int) this.db.insertWithOnConflict("syncup", null, initialValues, SQLiteDatabase.CONFLICT_IGNORE);
        if (id == -1) {
            //value already exists
            success = this.db.update("syncup", initialValues, "email=?", new String[] {email}) > 0;  // it will always be one, since we are keeping only one record
        } else {
            //first time
            success = true;
        }
        return success;
    }
    public void insertSyncUpIfNotExist(@NonNull String email){
        db.execSQL("INSERT OR IGNORE INTO syncup(email) VALUES('"+ email +"')");
    }
    public boolean updateSyncUp(@NonNull String email, Integer version, Integer state, String updated){
        ContentValues initialValues = new ContentValues();
        if(version!=null) initialValues.put(ContentDBContracts.SYNC_UPS.COL_VERSION, version);
        if(state!=null) initialValues.put(ContentDBContracts.SYNC_UPS.COL_STATE, state);
        if(updated!=null) initialValues.put(ContentDBContracts.SYNC_UPS.COL_UPDATED, updated);
        return this.db.update("syncup", initialValues, "email=?", new String[] {email}) > 0;
    }

    public ArrayList<POJOLabel> getAllLabelsRaw(){
        int userId = 0;
        if(user != null ){
            userId = user.getUserId();
        }
        Cursor innerCursor;
        int rowCount;
        int i;
        ArrayList<POJOLabel> list = new ArrayList();
        try {
            String query = "SELECT * FROM labels WHERE user_id = " + userId;
            innerCursor = this.db.rawQuery(query, null);
            if (innerCursor.moveToFirst()) {
                rowCount = innerCursor.getCount();
                for (i = 0; i < rowCount; i++) {

                    int user_id = innerCursor.getInt(innerCursor.getColumnIndex("user_id"));
                    int _id = innerCursor.getInt(innerCursor.getColumnIndex("_id"));
                    String name = innerCursor.getString(innerCursor.getColumnIndex("name"));
                    String color = innerCursor.getString(innerCursor.getColumnIndex("color"));
                    int permanent = innerCursor.getInt(innerCursor.getColumnIndex("permanent"));
                    int state = innerCursor.getInt(innerCursor.getColumnIndex("state"));

                    list.add(new POJOLabel(user_id, _id, name, color, permanent, state));
                    innerCursor.moveToNext();
                }
            }
            innerCursor.close();
        } catch (Exception e) {
            return null;
        }
        return list;
    }
    public ArrayList<POJOVersesMarked> getAllVersesMarkedRaw(){
        int userId = 0;
        if(user != null ){
            userId = user.getUserId();
        }
        Cursor innerCursor;
        int rowCount;
        int i;
        ArrayList<POJOVersesMarked> list = new ArrayList();
        try {
            String query = "SELECT * FROM verses_marked WHERE user_id = " + userId;
            innerCursor = this.db.rawQuery(query, null);
            if (innerCursor.moveToFirst()) {
                rowCount = innerCursor.getCount();
                for (i = 0; i < rowCount; i++) {

                    int user_id = innerCursor.getInt(innerCursor.getColumnIndex("user_id"));
                    int _id = innerCursor.getInt(innerCursor.getColumnIndex("_id"));
                    int label_id = innerCursor.getInt(innerCursor.getColumnIndex("label_id"));
                    String uuid = innerCursor.getString(innerCursor.getColumnIndex("UUID"));
                    String label_name = innerCursor.getString(innerCursor.getColumnIndex("label_name"));
                    String label_color = innerCursor.getString(innerCursor.getColumnIndex("label_color"));
                    int label_permanent = innerCursor.getInt(innerCursor.getColumnIndex("label_permanent"));
                    int book_number = innerCursor.getInt(innerCursor.getColumnIndex("book_number"));
                    int chapter = innerCursor.getInt(innerCursor.getColumnIndex("chapter"));
                    int verseFrom = innerCursor.getInt(innerCursor.getColumnIndex("verseFrom"));
                    int verseTo = innerCursor.getInt(innerCursor.getColumnIndex("verseTo"));
                    String note = innerCursor.getString(innerCursor.getColumnIndex("note"));
                    String date_created = innerCursor.getString(innerCursor.getColumnIndex("date_created"));
                    String date_updated = innerCursor.getString(innerCursor.getColumnIndex("date_updated"));
                    int state = innerCursor.getInt(innerCursor.getColumnIndex("state"));


                    list.add(new POJOVersesMarked(user_id, _id, label_id, book_number, chapter, verseFrom, verseTo, label_name, label_color, label_permanent, note, date_created, date_updated, uuid, state));
                    innerCursor.moveToNext();
                }
            }
            innerCursor.close();
        } catch (Exception e) {
            return null;
        }
        return list;
    }
    public ArrayList<POJOVersesLearned> getAllVersesLearnedRaw(){
        int userId = 0;
        if(user != null ){
            userId = user.getUserId();
        }
        Cursor innerCursor;
        int rowCount;
        int i;
        ArrayList<POJOVersesLearned> list = new ArrayList();
        try {
            String query = "SELECT * FROM verses_learned WHERE user_id = " + userId;
            innerCursor = this.db.rawQuery(query, null);
            if (innerCursor.moveToFirst()) {
                rowCount = innerCursor.getCount();
                for (i = 0; i < rowCount; i++) {

                    int user_id = innerCursor.getInt(innerCursor.getColumnIndex("user_id"));
                    int _id = innerCursor.getInt(innerCursor.getColumnIndex("_id"));
                    int label_id = innerCursor.getInt(innerCursor.getColumnIndex("label_id"));
                    String uuid = innerCursor.getString(innerCursor.getColumnIndex("UUID"));
                    int learned = innerCursor.getInt(innerCursor.getColumnIndex("learned"));
                    int priority = innerCursor.getInt(innerCursor.getColumnIndex("priority"));
                    int state = innerCursor.getInt(innerCursor.getColumnIndex("state"));

                    list.add(new POJOVersesLearned(user_id, _id, label_id, uuid, learned, priority, state));
                    innerCursor.moveToNext();
                }
            }
            innerCursor.close();
        } catch (Exception e) {
            return null;
        }
        return list;
    }

    public boolean overrideLocalData(POJOSyncUpHelper data){
        if(user == null){
            return false;
        }
        List<POJOLabel> labels = data.getLabels();
        List<POJOVersesMarked> versesMarked = data.getVerses_marked();
        List<POJOVersesLearned> versesLearned = data.getVerses_learned();
        boolean success = true;
        SQLiteDatabase db = this.db;
        db.beginTransaction();
        try{
            //delete all existing data
//            db.execSQL("DELETE FROM labels WHERE user_id = ?", user.getUserId());
//            db.execSQL("DELETE FROM verses_marked WHERE user_id = ?", user.getUserId());
//            db.execSQL("DELETE FROM verses_learned WHERE user_id = ?", user.getUserId());
            db.execSQL("DELETE FROM labels");
            db.execSQL("DELETE FROM verses_marked");
            db.execSQL("DELETE FROM verses_learned");

            if(labels != null && labels.size() > 0){
                ContentValues cv = new ContentValues();
                for (int i = 0; i < labels.size(); i++) {
                    cv.put("user_id", labels.get(i).getUserId());
                    cv.put("_id", labels.get(i).get_id());
                    cv.put("name", labels.get(i).getName());
                    cv.put("color", labels.get(i).getColor());
                    cv.put("permanent", labels.get(i).getPermanent());
                    cv.put("state", labels.get(i).getState());
                    db.insert("labels", null, cv);
                }
            }

            if(versesMarked != null && versesMarked.size() > 0){
                ContentValues cv = new ContentValues();
                for (int i = 0; i < versesMarked.size(); i++) {
                    cv.put("user_id", versesMarked.get(i).getUserId());
                    cv.put("_id", versesMarked.get(i).get_id());
                    cv.put("label_id", versesMarked.get(i).getLabel_id());
                    cv.put("book_number", versesMarked.get(i).getBook_number());
                    cv.put("chapter", versesMarked.get(i).getChapter());
                    cv.put("verseFrom", versesMarked.get(i).getVerseFrom());
                    cv.put("verseTo", versesMarked.get(i).getVerseTo());
                    cv.put("label_name", (versesMarked.get(i).getLabel_name()));
                    cv.put("label_color", (versesMarked.get(i).getLabel_color()));
                    cv.put("label_permanent", (versesMarked.get(i).getLabel_permanent()));
                    cv.put("note", (versesMarked.get(i).getNote()));
                    cv.put("date_created", (versesMarked.get(i).getDate_created()));
                    cv.put("date_updated", (versesMarked.get(i).getDate_updated()));
                    cv.put("UUID", (versesMarked.get(i).getUUID()));
                    cv.put("state", (versesMarked.get(i).getState()));
                    db.insert("verses_marked", null, cv);
                }
            }

            if(versesLearned != null && versesLearned.size() > 0){
                ContentValues cv = new ContentValues();
                for (int i = 0; i < versesLearned.size(); i++) {
                    cv.put("user_id", versesLearned.get(i).getUserId());
                    cv.put("_id", versesLearned.get(i).getUserId());
                    cv.put("UUID", versesLearned.get(i).getUUID());
                    cv.put("label_id", versesLearned.get(i).getLabel_id());
                    cv.put("learned", versesLearned.get(i).getLearned());
                    cv.put("priority", versesLearned.get(i).getPriority());
                    cv.put("state", versesLearned.get(i).getState());
                    db.insert("verses_learned", null, cv);
                }
            }

            //finally update the SyncUp version
            updateSyncUp(user.getEmail(), data.getVersion(), 1, null);

            db.setTransactionSuccessful();
        } catch (Exception e){
            e.printStackTrace();
            success = false;
        } finally {
            db.endTransaction();
        }
        return success;
    }
    //==============================================================================================

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE syncup (email VARCHAR PRIMARY KEY, version INTEGER DEFAULT 0, updated datetime DEFAULT current_timestamp, state INTEGER DEFAULT 0)");
        db.execSQL("CREATE TABLE labels (_id INTEGER PRIMARY KEY, user_id INTEGER DEFAULT 0, name VARCHAR NOT NULL, color VARCHAR NOT NULL, permanent INTEGER DEFAULT 0, state INTEGER DEFAULT 0)");
        db.execSQL("CREATE TABLE verses_marked (_id INTEGER PRIMARY KEY, user_id INTEGER DEFAULT 0, label_id INTEGER NOT NULL, book_number INTEGER NOT NULL, chapter INTEGER NOT NULL, verseFrom INTEGER NOT NULL, verseTo INTEGER NOT NULL, " +
                " label_name VARCHAR NOT NULL, label_color VARCHAR NOT NULL, label_permanent INTEGER DEFAULT 0, note VARCHAR, date_created datetime DEFAULT current_timestamp, date_updated datetime DEFAULT current_timestamp, UUID VARCHAR NOT NULL, state INTEGER DEFAULT 0," +
                " FOREIGN KEY (label_id) REFERENCES labels (_id) ON DELETE CASCADE)");
        db.execSQL("CREATE TABLE verses_learned (_id INTEGER PRIMARY KEY, user_id INTEGER DEFAULT 0, UUID VARCHAR NOT NULL, label_id INTEGER NOT NULL, learned INTEGER DEFAULT 0, priority INTEGER DEFAULT 0, state INTEGER DEFAULT 0)");

        initPutData(db);
    }

    private void initPutData(SQLiteDatabase db){
        Log.d(TAG, "initPutData: " + user);
        if(user == null){
            db.execSQL("INSERT INTO labels (name,color,permanent) VALUES( \"Memorize\", \"#00ff00\", 1)");
            db.execSQL("INSERT INTO labels (name,color,permanent) VALUES( \"Favourites\", \"#ffd700\", 1)");
        } else {
            db.execSQL("INSERT INTO labels (user_id, name,color,permanent) VALUES( "+user.getUserId()+", \"Memorize\", \"#00ff00\", 1)");
            db.execSQL("INSERT INTO labels (user_id, name,color,permanent) VALUES( "+user.getUserId()+", \"Favourites\", \"#ffd700\", 1)");
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS [" + ContentDBContracts.LABELS.NAME + "];");
        db.execSQL("DROP TABLE IF EXISTS [" + ContentDBContracts.VERSES_MARKED.NAME + "];");
        db.execSQL("DROP TABLE IF EXISTS [" + ContentDBContracts.VERSES_LEARNED.NAME + "];");
        onCreate(db);
    }

    @Override
    public void onConfigure(SQLiteDatabase db){
        db.setForeignKeyConstraintsEnabled(true);
    }

    /**
     * Provide access to our database.
     */
    public SQLiteDatabase getDb() {
        return db;
    }
}
