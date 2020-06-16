package com.zapatatech.santabiblia.DatabaseHelper;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

class ContentDBContracts {
    // ContentProvider information
    /**
     * The Content Authority is a name for the entire content provider, similar to the relationship
     * between a domain name and its website. A convenient string to use for content authority is
     * the package name for the app, since it is guaranteed to be unique on the device.
     */
    public static final String CONTENT_AUTHORITY = "com.zapatatech.santabiblia";
    /**
     * The content authority is used to create the base of all URIs which apps will use to contact this content provider.
     */
    static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    /**
     * A list of possible paths that will be appended to the base URI for each of the different tables.
     */
    static final String PATH_LABELS = "labels";
    static final String PATH_VERSES_MARKED = "verses_marked";
    static final String PATH_VERSES_LEARNED = "verses_learned";

    // Database information
    static final String DB_NAME = "content.db";
    static final int DB_VERSION = 1;

    /**
     * This represents our SQLite table for our articles.
     */
    public static abstract class LABELS implements BaseColumns {
        public static final String NAME = "labels";
        public static final String COL_ID = "_id";
        public static final String COL_NAME = "name";
        public static final String COL_COLOR = "color";
        public static final String COL_PERMANENT = "permanent";

        // Content URI represents the base location for the table LABELS
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_LABELS).build();
        // These are special type prefixes that specify if a URI returns a list or a specific item
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/" + CONTENT_URI + "/" + PATH_LABELS;
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/" + CONTENT_URI + "/" + PATH_LABELS;

        // Define a function to build a URI to find a specific movie by it's identifier
        public static Uri buildLabelsUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    /**
     * This represents our SQLite table for our articles.
     */
    public static abstract class VERSES_MARKED implements BaseColumns {
        public static final String NAME = "verses_marked";
        public static final String COL_ID = "_id";
        public static final String COL_LABEL_ID = "label_id";
        public static final String COL_BOOK_NUMBER = "book_number";
        public static final String COL_CHAPTER = "chapter";
        public static final String COL_VERSE_FROM = "verseFrom";
        public static final String COL_VERSE_TO = "verseTo";
        public static final String COL_LABEL_NAME = "label_name";
        public static final String COL_LABEL_COLOR = "label_color";
        public static final String COL_LABEL_PERMANENT = "label_permanent";
        public static final String COL_NOTE = "note";
        public static final String COL_DATE_CREATED = "date_created";
        public static final String COL_DATE_UPDATED = "date_updated";
        public static final String COL_UUID = "UUID";
        public static final String COL_STATE = "state";

        // Content URI represents the base location for the table VERSES MARKED
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_VERSES_MARKED).build();
        // These are special type prefixes that specify if a URI returns a list or a specific item
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/" + CONTENT_URI + "/" + PATH_VERSES_MARKED;
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/" + CONTENT_URI + "/" + PATH_VERSES_MARKED;

        // Define a function to build a URI to find a specific movie by it's identifier
        public static Uri buildVersesMarkedUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    /**
     * This represents our SQLite table for our articles.
     */
    public static abstract class VERSES_LEARNED implements BaseColumns {
        public static final String NAME = "verses_learned";
        public static final String COL_ID = "_id";
        public static final String COL_UUID = "UUID";
        public static final String COL_LABEL_ID = "label_id";
        public static final String COL_LEARNED = "learned";
        public static final String COL_PRIORITY = "priority";

        // Content URI represents the base location for the table VERSES LEARNED
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_VERSES_LEARNED).build();
        // These are special type prefixes that specify if a URI returns a list or a specific item
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/" + CONTENT_URI + "/" + PATH_VERSES_LEARNED;
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/" + CONTENT_URI + "/" + PATH_VERSES_LEARNED;

        // Define a function to build a URI to find a specific movie by it's identifier
        public static Uri buildVersesLearnedUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
