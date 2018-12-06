package com.example.namrathasubramanya.nanotale;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

/**
 * Created by namrathasubramanya on 4/29/18.
 */

public class ColorAdapter extends RecyclerView.Adapter<ColorAdapter.ColorViewHolder> {
    public static final String _TAG = "color_adapter";
    Context mContext;
    ColorChoiceListener mColorChoiceListener;

    // Keeps track of previously selected color.
    // When the next color is selected,
    // The previous color is unselected, ie, the blue band is hidden.
    View previousSelectedView;
    int selectedPos;

    // The colors are read from arrays resource, under the colorsArray string array.
    String[] colors;

    // Name of the colors.
    String[] colorNames;

    String colorContentDescription;

    public ColorAdapter(Context context, ColorChoiceListener listener) {
        mContext = context;
        colors = context.getResources().getStringArray(R.array.colorsArray);
        colorNames = context.getResources().getStringArray(R.array.colorsName);
        colorContentDescription = " is the chosen color";
        mColorChoiceListener = listener;
    }

    public static class ColorViewHolder extends RecyclerView.ViewHolder {
        public FrameLayout parent;
        public ImageView colorElem;

        public ColorViewHolder(View itemView) {
            super(itemView);
            colorElem = (ImageView) itemView.findViewById(R.id.color_elem);
            parent = (FrameLayout) itemView.findViewById(R.id.color_elem_parent);
        }
    }

    @Override
    public int getItemCount() {
        return colors.length;
    }

    @Override
    public ColorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.color_item, parent, false);
        return new ColorViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ColorViewHolder holder, final int position) {
        holder.colorElem.setBackgroundColor(Color.parseColor(colors[position]));
        if(previousSelectedView != null && position != selectedPos) {
            holder.parent.findViewById(R.id.color_selected).setVisibility(View.GONE);
        } else if(previousSelectedView != null && position == selectedPos){
            holder.parent.findViewById(R.id.color_selected).setVisibility(View.VISIBLE);
        }

        holder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (previousSelectedView == null) {
                    previousSelectedView = v;
                } else {
                    previousSelectedView.findViewById(R.id.color_selected).setVisibility(View.GONE);
                }

                selectedPos = position;
                Log.e(_TAG, colors[selectedPos]);
                mColorChoiceListener.onColorChoice(colors[selectedPos]);
                v.setContentDescription(colorNames[selectedPos] + colorContentDescription);
                // display a small band to indicate the selected color
                v.findViewById(R.id.color_selected).setVisibility(View.VISIBLE);
                previousSelectedView = v;
            }
        });
    }
}
