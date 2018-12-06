package com.example.namrathasubramanya.nanotale;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by namrathasubramanya on 4/28/18.
 */

public class NanotaleAdapter extends RecyclerView.Adapter<NanotaleAdapter.SavedNTViewHolder> {
    public static final String _TAG = "saved_nt_adapter";
    public static final int NO_SAVED_ITEMS = 0;
    public static final String SAVED_NANOTALE_ID_EXTRA = "saved_nt_id";
    public static final String MODIFY_SAVED_NT_ACTION = "modify_saved_nt";
    private Cursor mCursor;
    public Context mContext;

    public DisplayUpdateNtList mCallback;

    public static class SavedNTViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.saved_nt_tale)
        ImageView ntTale;

        @BindView(R.id.saved_nt_share_btn)
        ImageButton ntShare;

        @BindView(R.id.saved_nt_modify_btn)
        ImageButton ntModify;

        @BindView(R.id.saved_nt_delete_btn)
        ImageButton ntDelete;

        public SavedNTViewHolder(View v) {
            super(v);
            // initReferences(v);
            ButterKnife.bind(this, v);
        }

        public void setClickListeners(Cursor cursor,
                                      DisplayUpdateNtList callback) {
            final int _id = cursor.getInt(cursor.getColumnIndex(NanotaleData.NanoTale._ID));
            ntModify.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), BuildNanotale.class);
                    intent.putExtra(SAVED_NANOTALE_ID_EXTRA, _id);
                    intent.setAction(MODIFY_SAVED_NT_ACTION);
                    v.getContext().startActivity(intent);
                }
            });

            setShareBtnClickListener(cursor.getString(cursor.getColumnIndex(NanotaleData.NanoTale.FILE_PATH)));
            setDeleteBtnClickListener(cursor.getInt(cursor.getColumnIndex(NanotaleData.NanoTale._ID)),
                    cursor.getString(cursor.getColumnIndex(NanotaleData.NanoTale.FILE_PATH)),
                    callback);
        }

        public void setDeleteBtnClickListener(int id,
                                              final String fileName,
                                              final DisplayUpdateNtList callback) {
            final String _id = Integer.toString(id);
            ntDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    File file = new File(fileName);
                    file.delete();
                    Uri deleteUri = ModelNanotale.buildDeleteSingleUri(_id);
                    String selection = NanotaleData.NanoTale._ID + " = ? ";
                    String[] selectionArgs = new String[] {_id};
                    ContentResolver resolver = v.getContext().getContentResolver();
                    resolver.delete(deleteUri,
                            selection,
                            selectionArgs);
                    resolver.notifyChange(ModelNanotale.buildQueryAllUri(), null);
                    //FirebaseAnalytics analytics = FirebaseAnalytics.getInstance(v.getContext());
                    //FirebaseLogger.logDeletedNanoTale(analytics);
                    callback.onNtListUpdate();
                    NanotaleWidget.updateAllWidgets(v.getContext());
                }
            });
        }

        private void setShareBtnClickListener(final String imageFilePath) {
            ntShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String type = "image/*";
                    Uri uri = Uri.fromFile(new File(imageFilePath));
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType(type);
                    intent.putExtra(Intent.EXTRA_STREAM, uri);
                    String title = v.getContext().getString(R.string.intent_share_to);
                    v.getContext().startActivity(Intent.createChooser(intent, title));
                }
            });
        }

        public void setImage(Context context, String fileName) {
            Picasso.with(context).load(new File(fileName)).fit().into(ntTale);
        }
    }

    public void swapCursor(Cursor cursor) {
        Cursor old = mCursor;
        mCursor = cursor;
        if(old != null) {
            old.close();
        }
        notifyDataSetChanged();
    }

    public interface DisplayUpdateNtList {
        public void onNtListUpdate();
    }

    @Override
    public int getItemCount() {
        if(mCursor == null || mCursor.getCount() == NO_SAVED_ITEMS) {
            return NO_SAVED_ITEMS;
        } else {
            return mCursor.getCount();
        }
    }

    public NanotaleAdapter(DisplayUpdateNtList callback, Context context) {
        mCallback = callback;
        mContext = context;
    }

    @Override
    public SavedNTViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.nanotale_savedelement, parent, false);
        return new SavedNTViewHolder(v);
    }

    @Override
    public void onBindViewHolder(SavedNTViewHolder holder, int position) {
        if(mCursor != null && mCursor.getCount() != 0) {
            mCursor.moveToPosition(position);
            // holder.setNTMetaData(mCursor);
            holder.setClickListeners(mCursor, mCallback);
            String fileName = mCursor.getString(mCursor.getColumnIndex(NanotaleData.NanoTale.FILE_PATH));
            holder.setImage(mContext, fileName);
        }
    }
}
