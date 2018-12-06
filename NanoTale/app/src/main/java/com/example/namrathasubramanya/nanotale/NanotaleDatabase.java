package com.example.namrathasubramanya.nanotale;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.example.namrathasubramanya.nanotale.NanotaleBase.NanoTale.CREATE_TABLE;
import static com.example.namrathasubramanya.nanotale.NanotaleBase.NanoTale.DROP_TABLE;

/**
 * Created by namrathasubramanya on 4/29/18.
 */

public class NanotaleDatabase extends SQLiteOpenHelper {
    public NanotaleDatabase(Context context,
                            String name,
                            SQLiteDatabase.CursorFactory factory,
                            int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE);
    }
}
