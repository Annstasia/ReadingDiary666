package com.example.readingdiary.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.readingdiary.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.dialog.MaterialDialogs;

public class SetCoverDialogFragment extends DialogFragment {
    SetCoverDialogListener listener;
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        String title = getResources().getString(R.string.setCoverDialogTitle);
        String message = getResources().getString(R.string.setCoverDialogMessage);
        final String acceptButtonString = getResources().getString(R.string.setCoverDialogAcceptButton);
        final String cancelButtonString = getResources().getString(R.string.setCoverDialogCancelButton);
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getActivity());
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(acceptButtonString, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getActivity(), acceptButtonString,
                        Toast.LENGTH_LONG).show();
                listener.onSetCover();
            }
        });
        builder.setNegativeButton(cancelButtonString, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getActivity(), cancelButtonString,
                        Toast.LENGTH_LONG).show();
            }
        });
        builder.setCancelable(true);
        return builder.create();
    }

    public interface SetCoverDialogListener{
        void onSetCover();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (SetCoverDialogFragment.SetCoverDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + "must implement ExampleDialogListener");
        }
    }
}
