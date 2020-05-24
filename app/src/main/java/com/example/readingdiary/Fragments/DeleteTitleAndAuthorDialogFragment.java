package com.example.readingdiary.Fragments;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.readingdiary.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class DeleteTitleAndAuthorDialogFragment extends AppCompatDialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        String title = getResources().getString(R.string.deleteTitleAndAuthorDialogTitle);
        String message = getResources().getString(R.string.deleteTitleAndAuthorDialogMessage);
        final String acceptButtonString = getResources().getString(R.string.deleteTitleAndAuthorDialogButton);
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getActivity());
        builder.setTitle(title);  // заголовок
        builder.setMessage(message); // сообщение

        builder.setPositiveButton(acceptButtonString, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Toast.makeText(getActivity(), acceptButtonString,
                        Toast.LENGTH_LONG).show();




            }
        });

        return builder.create();
    }

}
