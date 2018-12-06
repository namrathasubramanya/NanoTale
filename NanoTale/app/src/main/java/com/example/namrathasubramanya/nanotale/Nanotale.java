package com.example.namrathasubramanya.nanotale;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by namrathasubramanya on 4/29/18.
 */

public class Nanotale extends View {
    private static final String _Tag = "NTClass";

    // offset needed to display the author's name
    // need to add calculation details
    private static final int AUTHOR_NAME_Y_OFFSET = 25;
    private static final int AUTHOR_NAME_X_OFFSET = 10;

    public Layout.Alignment mAlignment = Layout.Alignment.ALIGN_CENTER;

    // co-ords are measured from top left
    // ie origin (0, 0) is the top left corner

    // padding of the parent
    private int paddingTop;
    private int paddingLeft;

    public int nanoTaleBackgroundColor = Color.BLACK;
    public int nanoTaleFontColor = Color.WHITE;

    // need to translate canvas and then restore
    // also use StaticLayout
    private TextPaint mTalePaint;
    public String mTaleString;
    StaticLayout taleLayout;

    private Paint mAuthor;
    public String mAuthorString;
    private int mTextPixelLength;

    public Nanotale(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }



    public void changeFontColor(String color) {
        // the method needs to set the colors for the paint objects
        // changing nanoTaleFontColor does not reflect in the
        // fonts because the paint objects are initialised
        // with white and there is no resetting of the colors
        mAuthor.setColor(Color.parseColor(color));
        mTalePaint.setColor(Color.parseColor(color));
        nanoTaleFontColor = Color.parseColor(color);
    }

    private void initNTale() {
        mTalePaint = new TextPaint(TEXT_ALIGNMENT_CENTER);
        mTalePaint.setAntiAlias(true);
        mTalePaint.setSubpixelText(true);
        mTalePaint.setColor(nanoTaleFontColor);
        mTalePaint.setTextSize(getResources().getDimensionPixelSize(R.dimen.tale_text_size));
    }

    public void addTaleAndAuthor(String tale, String author) {
        mTaleString = tale;
        mAuthorString = author;
        calcAuthorDimensions();
    }

    private void calcAuthorDimensions() {
        float[] widths = new float[mAuthorString.length()];
        mAuthor.getTextWidths(mAuthorString, widths);
        mTextPixelLength = 0; // length of the smallest rectangle that can hold the text
        for(float f : widths) {
            mTextPixelLength += f;
        }
    }

    private void initAuthor() {
        mAuthor = new Paint(Paint.ANTI_ALIAS_FLAG);
        mAuthor.setSubpixelText(true);
        mAuthor.setColor(nanoTaleFontColor);
        mAuthor.setTextSize(getResources().getDimensionPixelSize(R.dimen.author_text_size));
    }


    private void initPadding() {
        paddingTop = getResources().getDimensionPixelSize(R.dimen.gen_padding);
        paddingLeft = getResources().getDimensionPixelSize(R.dimen.gen_padding);
    }

    private void init() {
        initPadding();
        initNTale();
        initAuthor();
        //mTaleX = (float) (paddingLeft + 30);
        //mTaleY = (float) (paddingTop + 30);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    public Canvas getScaledNanoTaleCanvas() {
        Canvas canvas = new Canvas();
        canvas.drawColor(nanoTaleBackgroundColor);
        canvas.save();
        taleLayout = new StaticLayout(mTaleString, mTalePaint, canvas.getWidth() - 25, mAlignment, 1f, 0f, false);
        int nanoTaleY = (canvas.getHeight() - taleLayout.getHeight()) / 2;

        canvas.translate(paddingLeft, paddingTop + nanoTaleY);
        taleLayout.draw(canvas);
        canvas.restore();
        int authY = nanoTaleY + taleLayout.getHeight();
        canvas.drawText(mAuthorString,
                canvas.getWidth() - mTextPixelLength - AUTHOR_NAME_X_OFFSET,
                authY + mAuthor.getTextSize() + AUTHOR_NAME_Y_OFFSET,
                mAuthor);
        return canvas;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        // TODO
        // verify that the vertical length of the nanotale is such that it fits within the canvas
        // and that it allows the author's name to be displayed.
        // the size of the nanotale should not overflow the image

        //super.onDraw(canvas);
        //canvas.drawARGB(255, 0, 0, 0);
        canvas.drawColor(nanoTaleBackgroundColor);
        canvas.save();
        // potential performance sink

        // magic number 25 is random padding to the left again.
        // not sure about magic numbers 1f and 0f
        taleLayout = new StaticLayout(mTaleString, mTalePaint, canvas.getWidth() - 25, mAlignment, 1f, 0f, false);

        // nanoTaleY holds the y coord to start drawing the nanotale
        // taleLayout needs to be vertically centered.
        // get the height of the canvas
        // get the height of the taleLayout
        // subtract the 2 heights.
        // subtracting will give the space left over
        // ideally, the space should be displayed such that there is
        // equal space both above and below the taleLayout
        // therefore, we divide the subtracted value by 2.
        int nanoTaleY = (canvas.getHeight() - taleLayout.getHeight()) / 2;

        canvas.translate(paddingLeft, paddingTop + nanoTaleY);
        taleLayout.draw(canvas);
        canvas.restore();

        // authY is the yCoord to draw the author's name
        int authY = nanoTaleY + taleLayout.getHeight();
        canvas.drawText(mAuthorString,
                canvas.getWidth() - mTextPixelLength - AUTHOR_NAME_X_OFFSET,
                authY + mAuthor.getTextSize() + AUTHOR_NAME_Y_OFFSET,
                mAuthor);
    }
}
