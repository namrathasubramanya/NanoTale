package com.example.namrathasubramanya.nanotale;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by namrathasubramanya on 4/29/18.
 */

public class Modify extends AppCompatActivity implements ColorChoiceListener {
    // call nanotale.invalidate() to redraw the image
    public static final String EDIT_DRAFT_ACTION = "edit_tale_action";
    public static final String NT_ID_TAG = "nt_id_tag";

    private static final String _TAG = "nt_display";

    private static final String SAVE_DIALOGUE_TAG = "save_file_dialogue_tag";
    private static final String CLOSE_DIALOGUE_TAG = "close_nt_dialogue_tag";

    // 3 aligment options are available.
    // Left, center and right.
    private static final int ALIGNMENT_OPTIONS = 3;

    // Left aligned is mapped to 0.
    // The rest are self explanatory.
    @NanotaleMetadata.AlignmentButton
    private static final int LEFT_ALIGN = NanotaleMetadata.LEFT_ALIGN;

    @NanotaleMetadata.AlignmentButton
    private static final int CENTER_ALIGN = NanotaleMetadata.CENTER_ALIGN;

    @NanotaleMetadata.AlignmentButton
    private static final int RIGHT_ALIGN = NanotaleMetadata.RIGHT_ALIGN;

    /*
    @IntDef({LEFT_ALIGN, CENTER_ALIGN, RIGHT_ALIGN})
    @Retention(RetentionPolicy.SOURCE)
    public @interface AlignmentButton {
    }
    */

    @NanotaleMetadata.AlignmentButton
    private int selectedAlignment;

    private ImageButton[] alignments;
    private Drawable[] normalAlignIcon;
    private Drawable[] selectedAlignIcon;

    private String mTale;
    private String mAuthor;

    // The linear layout of the left, center and right alignments
    // The visibility is set to GONE
    // This is used to ensure that 3 buttons are equally spaced
    private LinearLayout alignmentIconLayout;

    private boolean isAlignmentOptionVisible = false;

    private Nanotale nanoTale;
    private Bitmap nanoTaleBitmap;

    // Recyclerview displays both color
    private RecyclerView mColorPalette;
    private boolean isColorPaletteVisible = false;

    private String mBackgroundColor;
    private String mFontColor;

    private int ntId;

    // Values for color change target
    private static final int BACKGROUND_COLOR_CHANGE = 1;
    private static final int FONT_COLOR_CHANGE = 2;

    @IntDef({BACKGROUND_COLOR_CHANGE, FONT_COLOR_CHANGE})
    @Retention(RetentionPolicy.SOURCE)
    private @interface ColorChangeOption {
    }

    // colorChangeTarget is used to decide if background or font color
    // should change.
    // Its values are restricted to BACKGROUND_COLOR_CHANGE.
    // and FONT_COLOR_CHANGE.
    @ColorChangeOption
    private int colorChangeTarget;

    private static final int BACKGROUND_BUTTON = 0;
    private static final int ALIGNMENT_BUTTON = 1;
    private static final int FONT_BUTTON = 2;
    private static final int SAVE_BUTTON = 3;

    // The close button doesn't need to be highlighted
    // as the user won't be viewing it.
    // The dialogue pop up will obscure the button.
    // When the pop up disappears(and the user is stil
    // on the same screen), the button will need to
    // be normal.
    // There is no point in highlighting the button
    // when the user is not looking at that part of
    // the screen.

    // Used to reset all buttons to normal icons.
    private static final int NO_BUTTON_SELECTED = 0;

    private int[] formatButtonIds;
    private Drawable[] selectedFormatButtonIcons;
    private Drawable[] normalFormatButtonIcons;
    private static final int FORMAT_BUTTON_COUNT = 4;

    private int prevSelectedFormatButton;

    private boolean hasExistingNTMetaData;

    private String mSavedFilePath;

    private String buildContentDescription(String tale, String author) {
        if(author.length() > 0) {
            return tale + " by " + author;
        } else {
            return tale;
        }
    }

    private void initNanoTaleMetData() {
        selectedAlignment = CENTER_ALIGN;
        mBackgroundColor = getString(R.string.color_black);
        mFontColor = getString(R.string.color_white);
        nanoTale = (Nanotale) findViewById(R.id.nt_display);
        Intent intent = getIntent();
        mTale = intent.getStringExtra("nanotale_tale");
        String author = intent.getStringExtra("nanotale_author").trim();
        if (author.length() > 0) {
            mAuthor = author;
        } else {
            mAuthor = "";
        }

        nanoTale.addTaleAndAuthor(mTale, mAuthor);
        String ntContentDescription = buildContentDescription(mTale, mAuthor);
        nanoTale.setContentDescription(ntContentDescription);

        if(hasExistingNTMetaData) {
            mBackgroundColor = intent.getStringExtra("nt_background");
            mFontColor = intent.getStringExtra("nt_font");
            selectedAlignment = NanotaleMetadata.getAlignmentFromInt(intent.getIntExtra("nt_alignment", -1));
            setNanoTaleAlignment();
            nanoTale.nanoTaleBackgroundColor = Color.parseColor(mBackgroundColor);
            nanoTale.nanoTaleFontColor = Color.parseColor(mFontColor);
            nanoTale.changeFontColor(mFontColor);

            if(intent.hasExtra(NT_ID_TAG)) {
                ntId = intent.getIntExtra(NT_ID_TAG, 0);
            }

            if(intent.hasExtra("nt_file_path")) {
                mSavedFilePath = intent.getStringExtra("nt_file_path");
            }
        }
        nanoTale.invalidate();
    }

    private boolean isModifyingNanoTale() {
        Intent intent = getIntent();
        if(intent.getAction().equals(BuildNanotale.class)) {
            return false;
        } else {
            return true;
        }
    }

    private void setNanoTaleAlignment() {
        boolean isRTL = false;
        switch (selectedAlignment) {
            case LEFT_ALIGN:
                nanoTale.mAlignment = isRTL ? Layout.Alignment.ALIGN_OPPOSITE : Layout.Alignment.ALIGN_NORMAL;
                break;
            case CENTER_ALIGN:
                nanoTale.mAlignment = Layout.Alignment.ALIGN_CENTER;
                break;
            case RIGHT_ALIGN:
                nanoTale.mAlignment = (!isRTL) ? Layout.Alignment.ALIGN_OPPOSITE : Layout.Alignment.ALIGN_NORMAL;
                break;
            default:
                nanoTale.mAlignment = Layout.Alignment.ALIGN_CENTER;
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View v = getLayoutInflater().inflate(R.layout.nanotale_builder, null);
        setContentView(v);
        if(isModifyingNanoTale()) {
            hasExistingNTMetaData = true;
        }
        initFormatButtons();
        initNanoTaleMetData();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void initFormatButtons() {
        formatButtonIds = new int[FORMAT_BUTTON_COUNT];
        formatButtonIds[BACKGROUND_BUTTON] = R.id.background_palette;
        formatButtonIds[ALIGNMENT_BUTTON] = R.id.font_alignment;
        formatButtonIds[FONT_BUTTON] = R.id.font_color_palette;
        formatButtonIds[SAVE_BUTTON] = R.id.completed_nt;

        selectedFormatButtonIcons = new Drawable[FORMAT_BUTTON_COUNT];
//        selectedFormatButtonIcons[BACKGROUND_BUTTON] = getDrawable(R.drawable.ic_color_palette_blue_larger);
//        selectedFormatButtonIcons[ALIGNMENT_BUTTON] = getDrawable(R.drawable.ic_format_align_justify_blue_larger);
//        selectedFormatButtonIcons[FONT_BUTTON] = getDrawable(R.drawable.ic_text_format_blue_larger);
//        selectedFormatButtonIcons[SAVE_BUTTON] = getDrawable(R.drawable.ic_done_blue_larger);

        normalFormatButtonIcons = new Drawable[FORMAT_BUTTON_COUNT];
        normalFormatButtonIcons[BACKGROUND_BUTTON] = getDrawable(R.drawable.ic_palette_black);
        normalFormatButtonIcons[ALIGNMENT_BUTTON] = getDrawable(R.drawable.ic_format_align_justify_black);
        normalFormatButtonIcons[FONT_BUTTON] = getDrawable(R.drawable.ic_text_format_black);
        normalFormatButtonIcons[SAVE_BUTTON] = getDrawable(R.drawable.ic_done_black);
    }

    /**
     * Initialises and displays the Color Palette.
     */
    private void initColorPalette() {
        mColorPalette = (RecyclerView) findViewById(R.id._options);
        mColorPalette.setVisibility(View.VISIBLE);
        LinearLayoutManager manager = new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL,
                false);
        mColorPalette.setLayoutManager(manager);
        isColorPaletteVisible = true;
        ColorAdapter adapter = new ColorAdapter(this, this);
        mColorPalette.setAdapter(adapter);
    }

    private void hideColorPalette() {
        if (isColorPaletteVisible) {
            mColorPalette.setVisibility(View.GONE);
            isColorPaletteVisible = false;
        }
    }

    /**
     * @param selectedButton
     */
    private void highlightSelectedAlignmentIcon(@NanotaleMetadata.AlignmentButton int selectedButton) {
        // toggles the secondary alignment states
        // if a particular alignment button is selected,
        // only that button's icon will be replaced with the selected icon
        // the remaining will be changed to the normal icon
        for (int i = LEFT_ALIGN; i < alignments.length; i++) {
            if (i == selectedButton) {
                alignments[selectedButton].setImageDrawable(selectedAlignIcon[selectedButton]);
                selectedAlignment = selectedButton;
            } else {
                alignments[i].setImageDrawable(normalAlignIcon[i]);
            }
        }
    }

    /**
     * Initialization of alignment button array, selected and normal icon drawable arrays.
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void initAlignmentDrawableArrays() {
        alignments = new ImageButton[ALIGNMENT_OPTIONS];
        alignments[LEFT_ALIGN] = (ImageButton) findViewById(R.id.left_align_btn);
        alignments[CENTER_ALIGN] = (ImageButton) findViewById(R.id.center_align_btn);
        alignments[RIGHT_ALIGN] = (ImageButton) findViewById(R.id.right_align_btn);

//        selectedAlignIcon = new Drawable[ALIGNMENT_OPTIONS];
//        selectedAlignIcon[LEFT_ALIGN] = getDrawable(R.drawable.ic_format_align_left_blue_larger);
//        selectedAlignIcon[CENTER_ALIGN] = getDrawable(R.drawable.ic_format_align_center_blue_larger);
//        selectedAlignIcon[RIGHT_ALIGN] = getDrawable(R.drawable.ic_format_align_right_blue_larger);

        normalAlignIcon = new Drawable[ALIGNMENT_OPTIONS];
        normalAlignIcon[LEFT_ALIGN] = getDrawable(R.drawable.ic_format_align_left_black);
        normalAlignIcon[CENTER_ALIGN] = getDrawable(R.drawable.ic_format_align_center_black);
        normalAlignIcon[RIGHT_ALIGN] = getDrawable(R.drawable.ic_format_align_right_black);
    }

    /**
     * Alignes the text to either left, center or right justification, based on the button pressed.
     *
     * @param v Secondary alignment button
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void changeNTAlignment(View v) {
        highlightSelectedFormatButton(R.id.font_alignment);
        prevSelectedFormatButton = formatButtonIds[ALIGNMENT_BUTTON];

        // If secondary icons are visible.
        // Hides the icons.
        if (isAlignmentOptionVisible) {
            hideSecondaryAlignmentIcons();
        } else {
            // if secondary icons are not visible
            // displays and sets up functionality of the icons
            hideColorPalette();
            alignmentIconLayout = (LinearLayout) findViewById(R.id.alignment_options_layout);
            alignmentIconLayout.setVisibility(View.VISIBLE);
            isAlignmentOptionVisible = true;

            initAlignmentDrawableArrays();

            // code design can be improved below
            // possible repetition of code
            alignments[LEFT_ALIGN].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    nanoTale.mAlignment = Layout.Alignment.ALIGN_NORMAL;
                    highlightSelectedAlignmentIcon(LEFT_ALIGN);
                    nanoTale.invalidate();
                }
            });

            alignments[CENTER_ALIGN].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    nanoTale.mAlignment = Layout.Alignment.ALIGN_CENTER;
                    highlightSelectedAlignmentIcon(CENTER_ALIGN);
                    nanoTale.invalidate();
                }
            });

            alignments[RIGHT_ALIGN].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    nanoTale.mAlignment = Layout.Alignment.ALIGN_OPPOSITE;
                    highlightSelectedAlignmentIcon(RIGHT_ALIGN);
                    nanoTale.invalidate();
                }
            });
        }
    }

    public void hideSecondaryAlignmentIcons() {
        // should be self explanatory
        if (isAlignmentOptionVisible) {
            alignmentIconLayout.setVisibility(View.GONE);
            isAlignmentOptionVisible = false;
        }
    }

    /**
     * Displays a color picker in the form of a horizontal list.
     *
     * @param v
     */
    public void displayColorPalette(View v) {
        int viewId = v.getId();
        hideSecondaryAlignmentIcons();
        highlightSelectedFormatButton(viewId);
        if (prevSelectedFormatButton == viewId) {
            hideColorPalette();
        } else {
            if (viewId == formatButtonIds[BACKGROUND_BUTTON]) {
                colorChangeTarget = BACKGROUND_COLOR_CHANGE;
            } else {
                colorChangeTarget = FONT_COLOR_CHANGE;
            }
            initColorPalette();
        }
        // we're not using the formatButtonIds[] here.
        if (prevSelectedFormatButton == viewId) {
            prevSelectedFormatButton = NO_BUTTON_SELECTED;
        } else {
            prevSelectedFormatButton = viewId;
        }
    }

    /**
     * Highlights just the selected icons. The rest of the icons are returned to their
     * previous states.
     *
     * @param selectedButtonId
     */
    private void highlightSelectedFormatButton(int selectedButtonId) {
        for (int i = 0; i < formatButtonIds.length; i++) {
            ImageButton v = (ImageButton) findViewById(formatButtonIds[i]);
            if (formatButtonIds[i] == selectedButtonId && prevSelectedFormatButton != selectedButtonId) {
                //v.setBackground(selectedFormatButtonIcons[i]);
                v.setImageDrawable(selectedFormatButtonIcons[i]);
            } else {
                //v.setBackground(normalFormatButtonIcons[i]);
                v.setImageDrawable(normalFormatButtonIcons[i]);
            }
        }
    }


    /**
     * Saves the NanoTale to external storage
     *
     * @param view done button
     */
    public void saveNT(View view) {
        // To eliminate delection of button when dialogue opens and closes.
        highlightSelectedFormatButton(NO_BUTTON_SELECTED);
        if (nanoTale != null) {
            nanoTale.setDrawingCacheEnabled(true);
            // nanoTale.buildDrawingCache();

            nanoTale.setDrawingCacheBackgroundColor(nanoTale.nanoTaleBackgroundColor);
            nanoTale.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
            nanoTaleBitmap = Bitmap.createBitmap(nanoTale.getDrawingCache());
            nanoTaleBitmap.setDensity(500);
            int imageHeight = 1920;
            int imageWidth = 1920;
            nanoTaleBitmap = Bitmap.createScaledBitmap(nanoTaleBitmap, imageWidth, imageHeight, false);
            nanoTale.setDrawingCacheEnabled(false);

            int permission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (permission == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(this, "No permission to save file to storage", Toast.LENGTH_LONG).show();
            } else {
                ImagetoFile saveFileDialog = new ImagetoFile();
                saveFileDialog.setArguments(setSaveFileDialogArgs());
                saveFileDialog.setImageToSave(nanoTaleBitmap);
                saveFileDialog.show(getFragmentManager(), SAVE_DIALOGUE_TAG);
            }
        }
        prevSelectedFormatButton = formatButtonIds[SAVE_BUTTON];
        logAllDBValues();
    }

    private Bundle setSaveFileDialogArgs() {
        Bundle args = new Bundle();
        NanotaleMetadata data = getNanoMetaData();
        args.putInt(NT_ID_TAG, ntId);
        args.putString("nanotale_tale", data.mTale);
        args.putString("nanotale_author", data.mAuthor);
        args.putString("nt_background", data.mBackgroundColor);
        args.putString("nt_font", data.mFontColor);
        args.putString("nt_file_path", data.mFilePath);
        args.putInt("nt_alignment", data.mAlignment);
        return args;
    }

    /**
     * @return NanoTaleMetaData object
     */
    private NanotaleMetadata getNanoMetaData() {
        NanotaleMetadata data = new NanotaleMetadata();
        data.mAlignment = selectedAlignment;
        data.mBackgroundColor = mBackgroundColor;
        data.mFontColor = mFontColor;
        data.mTale = mTale.replace("\\n", System.getProperty("line.separator"));
        data.mAuthor = mAuthor;
        if(mSavedFilePath != null) {
            data.mFilePath = mSavedFilePath;
        } else {
            data.mFilePath = null;
        }
        return data;
    }

    /**
     * Displays pop up to close the color editor for the Nanotale.
     *
     * @param v
     */
    public void closeNTBuilder(View v) {
        CloseNanotale dialogue = new CloseNanotale();
        dialogue.show(getFragmentManager(), CLOSE_DIALOGUE_TAG);
    }

    /**
     * Changes the color of either the background or the font of the nanotale
     *
     * @param color passed from the recyclerview adapter
     */
    @Override
    public void onColorChoice(String color) {
        if (colorChangeTarget == BACKGROUND_COLOR_CHANGE) {
            nanoTale.nanoTaleBackgroundColor = Color.parseColor(color);
            mBackgroundColor = color;
        } else if (colorChangeTarget == FONT_COLOR_CHANGE) {
            nanoTale.changeFontColor(color);
            mFontColor = color;
        }
        nanoTale.invalidate();
    }

    private void logAllDBValues() {
        ContentResolver resolver = getContentResolver();
        Cursor cursor = resolver.query(
                ModelNanotale.buildQueryAllUri(),
                null,
                null,
                null,
                null);
        cursor.moveToFirst();
        int count = cursor.getCount();

        int taleColIndex = cursor.getColumnIndex(NanotaleBase.NanoTale.TALE);
        int authorColIndex = cursor.getColumnIndex(NanotaleBase.NanoTale.AUTHOR);
        int backgroundColIndex = cursor.getColumnIndex(NanotaleBase.NanoTale.BACKGROUND_COLOR);
        int fontColIndex = cursor.getColumnIndex(NanotaleBase.NanoTale.FONT_COLOR);
        int fileColIndex = cursor.getColumnIndex(NanotaleBase.NanoTale.FILE_PATH);
        int alignColIndex = cursor.getColumnIndex(NanotaleBase.NanoTale.ALIGNMENT);
        int dateColIndex = cursor.getColumnIndex(NanotaleBase.NanoTale.DATE_CREATED);

        for(int i = 0; i < count; i++) {
            cursor.moveToPosition(i);
            Log.e(_TAG, "Tale : " + cursor.getString(taleColIndex));
            Log.e(_TAG, "Author : " + cursor.getString(authorColIndex));
            Log.e(_TAG, "Background : " + cursor.getString(backgroundColIndex));
            Log.e(_TAG, "Font : " + cursor.getString(fontColIndex));
            Log.e(_TAG, "File : " + cursor.getString(fileColIndex));
            Log.e(_TAG, "Alignment : " + cursor.getInt(alignColIndex));
            Log.e(_TAG, "Date : " + cursor.getString(dateColIndex));
        }
        cursor.close();
    }

    public void editTale(View v) {
        Intent intent = new Intent(Modify.this, BuildNanotale.class);
        intent.putExtra("nanotale_tale", mTale);
        intent.putExtra("nanotale_author", mAuthor);
        intent.putExtra("nt_background", mBackgroundColor);
        intent.putExtra("nt_font", mFontColor);
        intent.putExtra("nt_alignment", selectedAlignment);
        intent.setAction(EDIT_DRAFT_ACTION);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
