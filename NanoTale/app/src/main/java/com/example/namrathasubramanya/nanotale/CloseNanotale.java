package com.example.namrathasubramanya.nanotale;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

/**
 * Created by namrathasubramanya on 4/29/18.
 */

public class CloseNanotale extends DialogFragment {
    public static final String _TAG = "close_nt_dialogue";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity().getApplicationContext());
        View view = LayoutInflater.from(getActivity().getApplicationContext()).inflate(R.layout.activity_close_nanotale_builder, null);
        builder = builder.setView(view);
        final AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        view.findViewById(R.id.discard_nt_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // need to exit to main activity
                Intent intent = new Intent(getActivity().getApplicationContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        view.findViewById(R.id.cancel_discard_nt_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        return dialog;
    }
}
