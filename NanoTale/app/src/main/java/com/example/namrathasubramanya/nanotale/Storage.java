package com.example.namrathasubramanya.nanotale;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.IntDef;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by namrathasubramanya on 4/29/18.
 */

public class Storage {
    private static final String _TAG = "storage_util";

    public static final int OUT_OF_STORAGE_SPACE = 1;
    public static final int FILE_ALREADY_EXISTS = 2;
    public static final int FILE_SAVED = 3;
    public static final int UNKNOWN_ERROR = 4;

    @IntDef({OUT_OF_STORAGE_SPACE, FILE_ALREADY_EXISTS, FILE_SAVED, UNKNOWN_ERROR})
    @Retention(RetentionPolicy.SOURCE)
    public @interface STORAGE_RESULTS {}

    private static boolean isExternalStorageAvailable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /**
     *
     * @return The base path to the folder that contains all Nanotales
     */
    public static String getAbsoluteBaseFilePath() {
        File picFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File nanoTaleDir = new File(picFolder, "Nanotale");
        return nanoTaleDir.getAbsolutePath();
    }

    /**
     *
     * @param bitmap Image to be saved.
     * @param fileName Filename.extension of the saved image.
     * @return True if image was saved, false otherwise. Will return false if file already exists
     *         or some other error occurs.
     */
    @STORAGE_RESULTS
    public static int saveBitmapToExtStorage(Context context,
                                             Bitmap bitmap,
                                             String fileName,
                                             boolean isOverwriteImage) {
        if (isExternalStorageAvailable()) {
            File picFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            File nanoTaleDir = new File(picFolder, "Nanotale");
            //Log.e(_TAG, "create dir : " + Boolean.toString(nanoTaleDir.mkdirs()));
            if(nanoTaleDir.exists()) {

            } else {
                nanoTaleDir.mkdirs();

            }
            File nanoTaleImage = new File(nanoTaleDir, fileName);
            if (isOverwriteImage || !nanoTaleImage.exists()) {
                try {
                    nanoTaleImage.createNewFile();
                    FileOutputStream fos = new FileOutputStream(nanoTaleImage);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                    if(nanoTaleImage.getFreeSpace() < bitmap.getByteCount()) {
                        return OUT_OF_STORAGE_SPACE;
                    }
                    fos.flush();
                    fos.close();

                    // Send broadcast to register new file in Media Store
                    Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    intent.setData(Uri.fromFile(nanoTaleImage));
                    context.sendBroadcast(intent);
                    return FILE_SAVED;
                } catch (IOException e) {
                    e.printStackTrace();

                }
            } else {

                return FILE_ALREADY_EXISTS;
            }
        }
        return UNKNOWN_ERROR;
    }
}
