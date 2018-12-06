package com.example.namrathasubramanya.nanotale;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by namrathasubramanya on 4/29/18.
 */

public class NanotaleRemoteViewService extends RemoteViewsService {
    public static final String _TAG = "widget service";
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {

        return new StackRemoteViewsFactory(this.getApplicationContext());
    }
}

class StackRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    public static final String _TAG = "remote_factory";
    public static final int NO_NANOTALE_COUNT = 0;
    private Context mContext;
    private Cursor mCursor;
    private ArrayList<String> fileNames;

    private void getFileNames() {
        String[] projection = new String[] {NanotaleBase.NanoTale.FILE_PATH};
        mCursor = mContext.getContentResolver()
                .query(ModelNanotale.buildQueryAllUri(),
                        projection,
                        null,
                        null,
                        NanotaleBase.NanoTale._ID + " DESC");
        int count = mCursor.getCount();
        fileNames = new ArrayList<>();
        for(int i = 0; i < count; i++) {
            mCursor.moveToPosition(i);
            fileNames.add(mCursor.getString(mCursor.getColumnIndex(NanotaleBase.NanoTale.FILE_PATH)));
        }
        mCursor.close();
    }

    public StackRemoteViewsFactory(Context context) {
        mContext = context;
        getFileNames();
    }

    @Override
    public int getCount() {

        if(fileNames != null) {
            return fileNames.size();
        } else {
            return 0;
        }
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        getFileNames();
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public RemoteViews getViewAt(int position) {
        // mCursor.moveToPosition(position);
        if(fileNames.size() == 0 || position < 0 || position > fileNames.size()) {
            return null;
        }
        File file = new File(fileNames.get(position));
        RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.imagewidget);
        try {
            Bitmap bitmap = Picasso.with(mContext).load(file).get();
            rv.setImageViewBitmap(R.id.widget_nanotale_image, bitmap);
            // rv.setImageViewUri(R.id.widget_nanotale_image, Uri.fromFile(file));
        } catch(IOException e) {
            e.printStackTrace();
        }
        rv.setImageViewUri(R.id.widget_nanotale_image, Uri.fromFile(file));
        // rv.setImageViewResource(R.id.widget_nanotale_image, R.drawable.ic_done_blue_larger);
        return rv;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }
}
