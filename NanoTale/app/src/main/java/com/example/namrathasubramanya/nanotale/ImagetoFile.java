package com.example.namrathasubramanya.nanotale;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by namrathasubramanya on 4/29/18.
 */

public class ImagetoFile extends DialogFragment {
    public static final String _TAG = "save_image_dialogue";
    public static final String NT_ALIGNMENT = "nt_alignment";

    private Bitmap nanoTaleBitmap;
    private NanotaleMetadata mNanoTale;

    @BindView(R.id.save_file_filename_input)
    public EditText mEditText;

    @BindView(R.id.file_error_icon)
    public ImageView mErrorIcon;

    private boolean isOverwriteImage;

    @BindView(R.id.save_nt_btn)
    public Button mSaveBtn;

    private String mExistingFilePath;
    private int ntId;

    public void setNanoTale(Bundle data) {
        mNanoTale = new NanotaleMetadata();
        ntId = data.getInt(Modify.NT_ID_TAG, 0);
        mNanoTale.mTale = data.getString("nanotale_tale");
        mNanoTale.mAuthor = data.getString("nanotale_author");
        mNanoTale.mBackgroundColor = data.getString("nt_background");
        mNanoTale.mFontColor = data.getString("nt_font");
        mNanoTale.mAlignment = NanotaleMetadata.getAlignmentFromInt(data.getInt(NT_ALIGNMENT));
        mNanoTale.mFilePath = data.getString("nt_file_path");
    }

    public void setImageToSave(Bitmap bitmap) {
        nanoTaleBitmap = bitmap;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setNanoTale(getArguments());
    }

    private String getFileNameFromAbsFilePath() {
        // Gets the file name without extension.
        String[] pathSegments = mNanoTale.mFilePath.split("/");
        String fileName = pathSegments[pathSegments.length - 1];
        fileName = fileName.substring(0, fileName.lastIndexOf("."));
        return fileName;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = LayoutInflater.from(getActivity().getApplicationContext()).inflate(R.layout.save, null);
        ButterKnife.bind(this, v);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setView(v);
        final AlertDialog dialog = builder.create();

        if (mNanoTale.mFilePath != null) {
            isOverwriteImage = true;
            mExistingFilePath = getFileNameFromAbsFilePath();
            mEditText.setText(mExistingFilePath);
            mSaveBtn.setText("Overwrite");
        }

        // Dialog background is set as transparent,
        // to allow the cardview to display its rounded corners.
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String fileName = s.toString();
                if(isFileNameSameAsExisting(fileName)) {
                    mSaveBtn.setText("Overwrite");
                } else {
                    mSaveBtn.setText(R.string.save_file_continue);
                }
                if (s.length() > 0) {
                    mErrorIcon.setVisibility(View.GONE);
                } else {
                    mErrorIcon.setVisibility(View.VISIBLE);
                }

            }
        });

        v.findViewById(R.id.save_nt_btn);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fileName = mEditText.getText().toString().trim();

                if (fileName.length() == 0) {
                    mErrorIcon.setVisibility(View.VISIBLE);
                    Toast.makeText(getActivity().getApplicationContext(), "Filename cannot be empty.", Toast.LENGTH_LONG).show();
                } else {
                    isOverwriteImage = isFileNameSameAsExisting(fileName);
                    if (isOverwriteImage) {
                        ((Button) v).setText("Overwrite");
                    } else {
                        ((Button) v).setText(R.string.save_file_continue);
                    }
                    fileName += ".png";

                    @Storage.STORAGE_RESULTS
                    int storageResults = Storage
                            .saveBitmapToExtStorage(getActivity().getApplicationContext(),
                                    nanoTaleBitmap,
                                    fileName,
                                    isOverwriteImage);
                    mErrorIcon.setVisibility(View.VISIBLE);
                    Toast toast;
                    switch (storageResults) {
                        case Storage.OUT_OF_STORAGE_SPACE:
                            toast = Toast.makeText(getActivity().getApplicationContext(),
                                    "No Storage Space Left",
                                    Toast.LENGTH_LONG);
                            break;
                        case Storage.FILE_ALREADY_EXISTS:
                            toast = Toast.makeText(getActivity().getApplicationContext(),
                                    "File Already Exists",
                                    Toast.LENGTH_LONG);
                            break;
                        case Storage.UNKNOWN_ERROR:
                            toast = Toast.makeText(getActivity().getApplicationContext(),
                                    "Unable To Save File",
                                    Toast.LENGTH_LONG);
                            break;
                        case Storage.FILE_SAVED:
                            toast = Toast.makeText(getActivity().getApplicationContext(),
                                    "File saved",
                                    Toast.LENGTH_LONG);
                            mErrorIcon.setVisibility(View.GONE);
                            mNanoTale.mFilePath = Storage.getAbsoluteBaseFilePath() + "/" +
                                    fileName;
                            saveNanoTaleMetaData();
                            Intent intent = new Intent(getActivity().getApplicationContext(), MainActivity.class);
                            intent.putExtra(MainActivity.NEW_NT_SAVED_TAG, true);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            dialog.dismiss();
                            break;
                        default:
                            toast = Toast.makeText(getActivity().getApplicationContext(), "This is not right",
                                    Toast.LENGTH_SHORT);
                    }
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            }
        });
        v.findViewById(R.id.cancel_discard_nt_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        return dialog;
    }

    private boolean isFileNameSameAsExisting(String fileName) {
        // If the file name is the same as that stored in the db, then
        // the file has to be overwritten.

        if(fileName.equals(mExistingFilePath)) {
            return true;
        } else {
            return false;
        }
    }

    public void saveNanoTaleMetaData() {
        SaveToDBTask saveToDBTask = new SaveToDBTask();
        ContentValues values = mNanoTale.buildContentValues();
        if(isOverwriteImage) {
            // Update db
            saveToDBTask.updateUri = ModelNanotale.buildUpdateUri(ntId);
        }
        NanotaleWidget.updateAllWidgets(getActivity().getApplicationContext());
        saveToDBTask.execute(values);
    }

    public class SaveToDBTask extends AsyncTask<ContentValues, Void, Void> {
        public Uri updateUri; // URI of the record to be updated.

        private void updateNTData(ContentValues values) {
            ContentResolver resolver = getContext().getContentResolver();
            String selection = NanotaleBase.NanoTale._ID + " =? ";
            String[] selectionArgs = new String[] {Integer.toString(ntId)};
            resolver.update(
                    updateUri,
                    values,
                    selection,
                    selectionArgs
            );
        }

        private void saveFreshNTData(ContentValues values) {
            Uri insertUri = ModelNanotale.buildInsertUri();
            ContentResolver resolver = getContext().getContentResolver();
            resolver.insert(insertUri, values);
        }

        @Override
        protected Void doInBackground(ContentValues... params) {
            if(updateUri != null) {
                updateNTData(params[0]);
            } else {
                saveFreshNTData(params[0]);
            }
            return null;
        }
    }
}
