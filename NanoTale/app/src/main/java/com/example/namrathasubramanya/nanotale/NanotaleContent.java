package com.example.namrathasubramanya.nanotale;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

import static com.example.namrathasubramanya.nanotale.NanotaleData.AUTHORITY;

/**
 * Created by namrathasubramanya on 4/29/18.
 */

public class NanotaleContent extends ContentProvider {
    public static final String _TAG = "nanotale_cprovider";

    public static UriMatcher sUriMatcher;
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "nanotale_db";

    public static final String DELETE = "delete";
    public static final String DELETE_MANY = "delete_many";
    public static final String INSERT = "insert";

    public static final String DELETE_MANY_QUERY_PARAM = "delete_many_qp";

    public static final int DELETE_SINGLE_CODE = 1;
    public static final int DELETE_MANY_CODE = 2;
    public static final int INSERT_CODE = 3;
    public static final int QUERY_ALL_CODE = 4;
    public static final int SINGLE_NT_CODE = 5;

    private NanotaleDatabase mDbHelper;

    static {
        // build the URI matcher;
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(AUTHORITY, NanotaleBase.NanoTale.PATH + "/" + DELETE + "/#", DELETE_SINGLE_CODE);
        sUriMatcher.addURI(AUTHORITY, NanotaleBase.NanoTale.PATH + "/" + INSERT, INSERT_CODE);
        sUriMatcher.addURI(AUTHORITY, NanotaleBase.NanoTale.PATH + "/" + DELETE_MANY, DELETE_MANY_CODE);
        sUriMatcher.addURI(AUTHORITY, NanotaleBase.NanoTale.PATH, QUERY_ALL_CODE);
        sUriMatcher.addURI(AUTHORITY, NanotaleBase.NanoTale.PATH + "/#", SINGLE_NT_CODE);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        switch(sUriMatcher.match(uri)) {
            case DELETE_SINGLE_CODE:
                return db.delete(NanotaleBase.NanoTale.TABLE_NAME,
                        selection,
                        selectionArgs);

            case DELETE_MANY_CODE:
                db.beginTransaction();
                String queryParam = uri.getQueryParameter(DELETE_MANY_QUERY_PARAM);
                String[] ids = queryParam.split(" ");
                for(String id : ids) {
                    db.delete(NanotaleBase.NanoTale.TABLE_NAME,
                            selection,
                            new String[] {id});
                }
                db.setTransactionSuccessful();
                db.endTransaction();
                return ids.length;

            default:
                return 0;
        }
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new NanotaleDatabase(getContext(),
                DATABASE_NAME,
                null,
                DATABASE_VERSION);
        return true;
    }


    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor = null;
        switch(sUriMatcher.match(uri)) {
            case QUERY_ALL_CODE:
                cursor = db.query(NanotaleBase.NanoTale.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case SINGLE_NT_CODE:
                String sel = NanotaleBase.NanoTale._ID + " =? ";
                String[] selArgs = new String[] {uri.getLastPathSegment()};
                cursor = db.query(NanotaleBase.NanoTale.TABLE_NAME,
                        null,
                        sel,
                        selArgs,
                        null,
                        null,
                        null);
                break;
        }
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        Uri.Builder builder = new Uri.Builder()
                .authority(AUTHORITY)
                .appendPath(NanotaleBase.NanoTale.PATH);
        long rowId = -1;
        switch(sUriMatcher.match(uri)) {
            case INSERT_CODE:
                rowId = db.insert(NanotaleBase.NanoTale.TABLE_NAME,
                        null,
                        values);
                break;
        }
        builder = builder.appendPath(Long.toString(rowId));
        return builder.build();
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        switch (sUriMatcher.match(uri)) {
            case SINGLE_NT_CODE:
                db.update(
                        NanotaleBase.NanoTale.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs
                );
                break;
        }
        return 0;
    }
}
