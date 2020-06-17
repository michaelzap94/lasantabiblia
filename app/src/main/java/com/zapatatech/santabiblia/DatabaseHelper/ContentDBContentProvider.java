package com.zapatatech.santabiblia.DatabaseHelper;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ContentDBContentProvider  extends ContentProvider {
    // Use an int for each URI we will run, this represents the different queries
    private static final int LABEL = 100;
    private static final int LABEL_ID = 101;
    private static final int VERSE_MARKED = 200;
    private static final int VERSE_MARKED_ID = 201;
    private static final int VERSE_LEARNED = 300;
    private static final int VERSE_LEARNED_ID = 301;

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private SQLiteDatabase db;

    /**
     * Builds a UriMatcher that is used to determine witch database request is being made.
     */
    public static UriMatcher buildUriMatcher(){
        String content_authority = ContentDBContracts.CONTENT_AUTHORITY;
        // All paths to the UriMatcher have a corresponding code to return, when a match is found (the ints above).
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(content_authority, ContentDBContracts.PATH_LABELS, LABEL);
        matcher.addURI(content_authority, ContentDBContracts.PATH_LABELS + "/#", LABEL_ID);
        matcher.addURI(content_authority, ContentDBContracts.PATH_VERSES_MARKED, VERSE_MARKED);
        matcher.addURI(content_authority, ContentDBContracts.PATH_VERSES_MARKED + "/#", VERSE_MARKED_ID);
        matcher.addURI(content_authority, ContentDBContracts.PATH_VERSES_LEARNED, VERSE_LEARNED);
        matcher.addURI(content_authority, ContentDBContracts.PATH_VERSES_LEARNED + "/#", VERSE_LEARNED_ID);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        db = ContentDBHelper.getInstance(getContext()).getDb();
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor c;
        switch (sUriMatcher.match(uri)) {
            //===============================================================================
            // Query for multiple results
            case LABEL:
                c = db.query(ContentDBContracts.LABELS.NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            // Query for single result
            case LABEL_ID:
                long label_id = ContentUris.parseId(uri);
                c = db.query(ContentDBContracts.LABELS.NAME,
                        projection,
                        ContentDBContracts.LABELS.COL_ID + "=?",
                        new String[] { String.valueOf(label_id) },
                        null,
                        null,
                        sortOrder);
                break;
            //===============================================================================
            // Query for multiple results
            case VERSE_MARKED:
                c = db.query(ContentDBContracts.VERSES_MARKED.NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            // Query for single result
            case VERSE_MARKED_ID:
                long verse_marked_id = ContentUris.parseId(uri);
                c = db.query(ContentDBContracts.VERSES_MARKED.NAME,
                        projection,
                        ContentDBContracts.VERSES_MARKED.COL_ID + "=?",
                        new String[] { String.valueOf(verse_marked_id) },
                        null,
                        null,
                        sortOrder);
                break;
            //===============================================================================
            // Query for multiple results
            case VERSE_LEARNED:
                c = db.query(ContentDBContracts.VERSES_LEARNED.NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            // Query for single result
            case VERSE_LEARNED_ID:
                long verse_learned_id = ContentUris.parseId(uri);
                c = db.query(ContentDBContracts.VERSES_LEARNED.NAME,
                        projection,
                        ContentDBContracts.VERSES_LEARNED.COL_ID + "=?",
                        new String[] { String.valueOf(verse_learned_id) },
                        null,
                        null,
                        sortOrder);
                break;
            //===============================================================================
            default: throw new IllegalArgumentException("Invalid URI!");
        }

        // Tell the cursor to register a content observer to observe changes to the URI or its descendants.
        assert getContext() != null;
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    //The getType method is used to find the MIME type of the results, either a directory of multiple results, or an individual item
    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch(sUriMatcher.match(uri)){
            case LABEL:
                return ContentDBContracts.LABELS.CONTENT_TYPE;
            case LABEL_ID:
                return ContentDBContracts.LABELS.CONTENT_ITEM_TYPE;
            case VERSE_MARKED:
                return ContentDBContracts.VERSES_MARKED.CONTENT_TYPE;
            case VERSE_MARKED_ID:
                return ContentDBContracts.VERSES_MARKED.CONTENT_ITEM_TYPE;
            case VERSE_LEARNED:
                return ContentDBContracts.VERSES_LEARNED.CONTENT_TYPE;
            case VERSE_LEARNED_ID:
                return ContentDBContracts.VERSES_LEARNED.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        Uri returnUri;
        long _id;

        switch (sUriMatcher.match(uri)) {
            case LABEL:
                _id = db.insert(ContentDBContracts.LABELS.NAME, null, values);
                returnUri = ContentUris.withAppendedId(ContentDBContracts.LABELS.CONTENT_URI, _id);
                break;
            case VERSE_MARKED:
                _id = db.insert(ContentDBContracts.VERSES_MARKED.NAME, null, values);
                returnUri = ContentUris.withAppendedId(ContentDBContracts.VERSES_MARKED.CONTENT_URI, _id);
                break;
            case VERSE_LEARNED:
                _id = db.insert(ContentDBContracts.VERSES_LEARNED.NAME, null, values);
                returnUri = ContentUris.withAppendedId(ContentDBContracts.VERSES_LEARNED.CONTENT_URI, _id);
                break;
            default: throw new IllegalArgumentException("Invalid URI!");
        }

        // Notify any observers to update the UI
        assert getContext() != null;
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int rows;
        switch (sUriMatcher.match(uri)) {
            case LABEL:
                rows = db.delete(ContentDBContracts.LABELS.NAME, selection, selectionArgs);
                break;
            case VERSE_MARKED:
                rows = db.delete(ContentDBContracts.VERSES_MARKED.NAME, selection, selectionArgs);
                break;
            case VERSE_LEARNED:
                rows = db.delete(ContentDBContracts.VERSES_LEARNED.NAME, selection, selectionArgs);
                break;
            default: throw new IllegalArgumentException("Invalid URI!");
        }

        // Notify any observers to update the UI
        if (rows != 0) {
            assert getContext() != null;
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rows;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        int rows;
        switch (sUriMatcher.match(uri)) {
            case LABEL:
                rows = db.update(ContentDBContracts.LABELS.NAME, values, selection, selectionArgs);
                break;
            case VERSE_MARKED:
                rows = db.update(ContentDBContracts.VERSES_MARKED.NAME, values, selection, selectionArgs);
                break;
            case VERSE_LEARNED:
                rows = db.update(ContentDBContracts.VERSES_LEARNED.NAME, values, selection, selectionArgs);
                break;
            default: throw new IllegalArgumentException("Invalid URI!");
        }
        // Notify any observers to update the UI
        if (rows != 0) {
            assert getContext() != null;
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rows;
    }
}
