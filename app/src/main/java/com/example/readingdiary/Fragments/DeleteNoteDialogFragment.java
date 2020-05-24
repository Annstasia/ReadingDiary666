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

public class DeleteNoteDialogFragment extends AppCompatDialogFragment {
    private DeleteDialogListener listener;
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        String title = getResources().getString(R.string.deleteDialogTitleNote);
        String message = getResources().getString(R.string.deleteDialogMessageNote);
        final String acceptButtonString = getResources().getString(R.string.deleteDialogAcceptButton);
        final String cancelButtonString = getResources().getString(R.string.deleteDialogCancelButton);
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getActivity());
        builder.setTitle(title);  // заголовок
        builder.setMessage(message); // сообщение

        builder.setPositiveButton(acceptButtonString, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Toast.makeText(getActivity(), acceptButtonString,
                        Toast.LENGTH_LONG).show();
                listener.onDeleteClicked();




            }
        });

        builder.setNegativeButton(cancelButtonString, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Toast.makeText(getActivity(), cancelButtonString, Toast.LENGTH_LONG)
                        .show();
//                ((EditNoteActivity)getActivity()).finish();
            }
        });
        builder.setCancelable(true);

        return builder.create();
    }

    public interface DeleteDialogListener {
        void onDeleteClicked();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (DeleteDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + "must implement ExampleDialogListener");
        }
    }
}
