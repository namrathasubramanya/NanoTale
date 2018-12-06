package com.example.namrathasubramanya.nanotale;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.namrathasubramanya.nanotale.NanotaleMetadata.LEFT_ALIGN;

/**
 * Created by namrathasubramanya on 4/28/18.
 */

class BuildNanotale extends AppCompatActivity {
    private static final String _TAG = "build_nanotale";

    public static final String NEW_NT_ACTION = MainActivity.BUILD_NEW_NANOTALE;

    public static final String MODIFY_SAVED_NT_ACTION = NanotaleAdapter.MODIFY_SAVED_NT_ACTION;

    public static final String EDIT_DRAFT_NT_ACTION = Modify.EDIT_DRAFT_ACTION;

    private static final int EXT_STORAGE_REQ_CODE = 108;

    private String EMPTY_TALE_ERROR; // = getResources().getString(R.string.empty_story_error_msg);
    private String LENGTHY_TALE_ERROR;//  = getResources().getString(R.string.lengthy_story_error_msg);

    private int STORY_MAX_CHAR_COUNT; // = getResources().getInteger(R.integer.nanotale_max_char_count);

    @BindView(R.id.nt_input_tale)
    public EditText mTale;

    @BindView(R.id.nt_input_author)
    public EditText mAuthor;

    @BindView(R.id.nt_tale_error)
    public ImageView mTaleErrorImage;

    @BindView(R.id.nt_input_story_char_count)
    public TextView mWordCount;

    @BindView(R.id.nt_finished)
    public ImageButton mDoneBtn;

    private boolean isNewNanoTale;
    private int ntId = -1;

    private NanotaleMetadata ntData;

    private FirebaseAnalytics mAnalytics;

    private void initErrorStrings() {
        EMPTY_TALE_ERROR = "Story cannot be blank";
        LENGTHY_TALE_ERROR = "Story character limit reached";
    }

    private void initResources() {
        initErrorStrings();
        STORY_MAX_CHAR_COUNT = 140;
    }

    private void getViewReferences() {
        mTale = (EditText) findViewById(R.id.nt_input_tale);
        mAuthor = (EditText) findViewById(R.id.nt_input_author);
        mTaleErrorImage = (ImageView) findViewById(R.id.nt_tale_error);
        mWordCount = (TextView) findViewById(R.id.nt_input_story_char_count);
        mDoneBtn = (ImageButton) findViewById(R.id.nt_finished);
    }

    private void displayErrorToast(String errorMsg) {
        mTaleErrorImage.setVisibility(View.VISIBLE);
        Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show();
    }

    private String numCharactersLeft(int numChar) {
        String charLeft = Integer.toString(STORY_MAX_CHAR_COUNT - numChar);
        return charLeft;
    }

    private void setupViewListeners() {
        mTale.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int charCount = s.length();
                if(charCount > STORY_MAX_CHAR_COUNT) {
                    mTaleErrorImage.setVisibility(View.VISIBLE);
                    displayErrorToast(LENGTHY_TALE_ERROR);
                } else {
                    // this is a waste of resources.
                    // there must be a way to reset the visibility
                    // without having to query
                    mTaleErrorImage.setVisibility(View.GONE);
                }
                mWordCount.setText(Integer.toString(charCount) + "/" + Integer.toString(STORY_MAX_CHAR_COUNT));
                mWordCount.setContentDescription(numCharactersLeft(charCount));
            }
        });
    }

    @OnClick(R.id.nt_finished)
    public void ntDoneBuild() {
        int taleLength = mTale.getText().length();
        if(taleLength == 0) {
            displayErrorToast(EMPTY_TALE_ERROR);
        } else if (taleLength > STORY_MAX_CHAR_COUNT) {
            displayErrorToast(LENGTHY_TALE_ERROR);
        } else {
            Intent intent = new Intent(BuildNanotale.this, Modify.class);
            intent.putExtra("nanotale_tale", mTale.getText().toString());
            intent.putExtra("nanotale_author", mAuthor.getText().toString());
            if(!isNewNanoTale) {
                if(ntId != -1) {
                    intent.putExtra(Modify.NT_ID_TAG, ntId);
                }
                intent.putExtra("nt_alignment", ntData.mAlignment);
                intent.putExtra("nt_font", ntData.mFontColor);
                intent.putExtra("nt_background", ntData.mBackgroundColor);
                if(ntData.mFilePath != null) {
                    intent.putExtra("nt_file_path", ntData.mFilePath);
                    intent.setAction(MODIFY_SAVED_NT_ACTION);
                } else {
                    intent.setAction(EDIT_DRAFT_NT_ACTION);
                }
            } else {
                intent.setAction(MainActivity.BUILD_NEW_NANOTALE);
            }
            startActivity(intent);
        }
    }

    private void getStoragePermission() {
        int permission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(BuildNanotale.this,
                    new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    EXT_STORAGE_REQ_CODE);
        }
    }

    /**
     * Aborts writing the Nanotale and returns to main activity.
     * @param v
     */
    public void backBtnClick(View v) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void displayNanotaleTaleAndAuthor(String tale, String author) {
        mTale.setText(tale);
        mAuthor.setText(author);
    }

    public NanotaleMetadata getSavedNanoTaleMetaData() {
        Intent intent = getIntent();
        NanotaleMetadata data = null;
        ntId = intent.getIntExtra(NanotaleAdapter.SAVED_NANOTALE_ID_EXTRA, -1);
        if(ntId < 0) {

            return data;
        } else {
            Uri savedNtUri = ModelNanotale.buildUriForSavedNT(ntId);
            Cursor cursor = getContentResolver()
                    .query(ModelNanotale.buildUriForSavedNT(ntId),
                            null,
                            null,
                            null,
                            null);
            cursor.moveToFirst();
            data = NanotaleMetadata.getNTMetaDataFromCursor(cursor);
            data.logNanoTaleValues(_TAG + " building ");
            cursor.close();
            return data;
        }
    }

    private NanotaleMetadata getDraftNanoTaleMetaData() {
        Intent intent = getIntent();
        NanotaleMetadata data = new NanotaleMetadata();
        data.mTale = intent.getStringExtra("nanotale_tale");
        data.mAuthor = intent.getStringExtra("nanotale_author");
        data.mBackgroundColor = intent.getStringExtra("nt_background");
        data.mAlignment = NanotaleMetadata.getAlignmentFromInt(
                intent.getIntExtra("nt_alignment", LEFT_ALIGN));
        data.mFontColor = intent.getStringExtra("nt_font");

        return data;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nanotale_storywriter);
        initResources();
        getStoragePermission();
        // getViewReferences();
        ButterKnife.bind(this);
        setupViewListeners();

        mAnalytics = FirebaseAnalytics.getInstance(this);

        String action = getIntent().getAction();
        if( action.equals(MainActivity.BUILD_NEW_NANOTALE)) {
            isNewNanoTale = true;
            //FirebaseLogger.logNewNanoTale(mAnalytics);
        } else {
            isNewNanoTale = false;
            //FirebaseLogger.logModifyEvent(mAnalytics);

            if( action.equals(Modify.EDIT_DRAFT_ACTION)) {
                ntData = getDraftNanoTaleMetaData();
            } else if( action.equals(NanotaleAdapter.MODIFY_SAVED_NT_ACTION)) {
                ntData = getSavedNanoTaleMetaData();
            }
            ntData.logNanoTaleValues(_TAG + "building nt: ");
            displayNanotaleTaleAndAuthor(ntData.mTale, ntData.mAuthor);
        }
    }
}
