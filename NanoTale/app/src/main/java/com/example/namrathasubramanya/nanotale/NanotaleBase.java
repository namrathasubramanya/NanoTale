package com.example.namrathasubramanya.nanotale;

import android.provider.BaseColumns;

/**
 * Created by namrathasubramanya on 4/29/18.
 */

public class NanotaleBase {
    public static final String AUTHORITY = "com.example.admin.nanotales";

    public static abstract class NanoTale implements BaseColumns {

        public static final String INSERT = "insert";

        public static final String TABLE_NAME = "nanotale_meta";

        public static final String PATH = "nanotales";

        public static final String _ID = "_id";

        public static final String AUTHOR = "author";

        public static final String TALE = "tale";

        public static final String BACKGROUND_COLOR = "background_color";

        public static final String FONT_COLOR = "font_color";

        public static final String FILE_PATH = "file_path"; // If file path is set only when file is saved

        public static final String ALIGNMENT = "alignment";
        // Left alignment is 0
        // Center alignment is 1
        // Right alignment is 2

        // date created will be of the form yyyymmdd
        // easier to sort
        public static final String DATE_CREATED = "date_created";

        public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " ( " +
                _ID  + " INTEGER PRIMARY KEY , " +
                AUTHOR + " TEXT, " +
                TALE + " TEXT NOT NULL, " +
                BACKGROUND_COLOR + " TEXT NOT NULL, " +
                FONT_COLOR + " TEXT NOT NULL, " +
                DATE_CREATED + " INTEGER NOT NULL, " +
                ALIGNMENT + " INTEGER NOT NULL, " +
                FILE_PATH + " TEXT);";

        public static final String DROP_TABLE = "drop table " + TABLE_NAME;
    }
}
