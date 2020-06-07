package com.zapatatech.santabiblia.DatabaseHelper;

import android.provider.BaseColumns;

public class BibleContracts {
    public static final class InfoContract implements BaseColumns{
        public static final String TABLE_NAME = "info";
        public static final String COL_NAME = "name";
        public static final String COL_VALUE = "value";
    }
    public static final class BooksContract implements BaseColumns{
        public static final String TABLE_NAME = "books";
        public static final String COL_BOOK_ID = "book_number";
        public static final String COL_BOOK_COLOR = "book_color";
        public static final String COL_SHORT_NAME = "short_name";
        public static final String COL_LONG_NAME = "long_name";
    }
    public static final class StoriesContract implements BaseColumns{
        public static final String TABLE_NAME = "books";
        public static final String COL_BOOK_ID = "book_number";
        public static final String COL_CHAPTER = "chapter";
        public static final String COL_STORY_AT_VERSE = "story_at_verse";
        public static final String COL_ORDER_IF_SEVERAL = "order_if_several";
        public static final String COL_TITLE = "title";
    }
    public static final class CommentariesContract implements BaseColumns{
        public static final String COL_BOOK_ID = "book_number";
        public static final String COL_CHAPTER_FROM = "chapter_number_from";
        public static final String COL_VERSE_FROM = "verse_number_from";
        public static final String COL_TEXT = "text";
        public static final String COL_MARKER = "marker";
    }
    public static final class VersesContract implements BaseColumns{
        public static final String TABLE_NAME = "books";
        public static final String COL_BOOK_ID = "book_number";
        public static final String COL_CHAPTER = "chapter";
        public static final String COL_VERSE = "verse";
        public static final String COL_TEXT = "text";
    }
}
