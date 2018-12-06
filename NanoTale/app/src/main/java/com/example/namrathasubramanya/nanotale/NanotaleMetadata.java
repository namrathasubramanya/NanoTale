package com.example.namrathasubramanya.nanotale;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.IntDef;
import android.util.Log;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by namrathasubramanya on 4/29/18.
 */

public class NanotaleMetadata {
    public String mAuthor;
    public String mTale;

    public static final int LEFT_ALIGN = 0;
    public static final int CENTER_ALIGN = 1;
    public static final int RIGHT_ALIGN = 2;

    @IntDef({LEFT_ALIGN, CENTER_ALIGN, RIGHT_ALIGN})
    @Retention(RetentionPolicy.SOURCE)
    public @interface AlignmentButton {}

    @AlignmentButton
    public int mAlignment;

    public String mBackgroundColor;
    public String mFontColor;

    // absolute path to the saved image
    public String mFilePath;

    public String getCurrentDate() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String date = sdf.format(c.getTime());

        return date;
    }

    public void logNanoTaleValues(String tag) {
        Log.e(tag, "Tale : " + mTale);
        Log.e(tag, "Author : " + mAuthor);
        Log.e(tag, "Date : " + getCurrentDate());
        Log.e(tag, "Alignemnt : " + Integer.toString(mAlignment));
        Log.e(tag, "Background color : " + mBackgroundColor);
        Log.e(tag, "Font color : " + mFontColor);
        Log.e(tag, "File Path : " + mFilePath);
    }


    public ContentValues buildContentValues() {
        ContentValues values = new ContentValues();
        values.put(NanotaleBase.NanoTale.AUTHOR, mAuthor);
        values.put(NanotaleBase.NanoTale.TALE, mTale);
        values.put(NanotaleBase.NanoTale.ALIGNMENT, mAlignment);
        values.put(NanotaleBase.NanoTale.BACKGROUND_COLOR, mBackgroundColor);
        values.put(NanotaleBase.NanoTale.FONT_COLOR, mFontColor);
        values.put(NanotaleBase.NanoTale.FILE_PATH, mFilePath);
        values.put(NanotaleBase.NanoTale.DATE_CREATED, getCurrentDate());
        return values;
    }

    public static NanotaleMetadata getNTMetaDataFromCursor(Cursor cursor) {
        NanotaleMetadata data = new NanotaleMetadata();
        data.mTale= cursor.getString(cursor.getColumnIndex(NanotaleBase.NanoTale.TALE));
        data.mAuthor = cursor.getString(cursor.getColumnIndex(NanotaleBase.NanoTale.AUTHOR));

        data.mAlignment = NanotaleMetadata
                .getAlignmentFromInt(cursor.getInt(cursor.getColumnIndex(NanotaleBase.NanoTale.ALIGNMENT)));

        data.mBackgroundColor = cursor.getString(cursor.getColumnIndex(NanotaleBase.NanoTale.BACKGROUND_COLOR));
        data.mFontColor = cursor.getString(cursor.getColumnIndex(NanotaleBase.NanoTale.FONT_COLOR));
        data.mFilePath = cursor.getString(cursor.getColumnIndex(NanotaleBase.NanoTale.FILE_PATH));
        return data;
    }

    @NanotaleMetadata.AlignmentButton
    public static int getAlignmentFromInt(int alignment) {
        switch (alignment) {
            case LEFT_ALIGN:
                return LEFT_ALIGN;
            case RIGHT_ALIGN:
                return RIGHT_ALIGN;
            case CENTER_ALIGN:
                return CENTER_ALIGN;
            default:
                return CENTER_ALIGN;
        }
    }

}

