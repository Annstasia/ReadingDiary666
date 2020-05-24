package com.example.readingdiary.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.readingdiary.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.dialog.MaterialDialogs;

public class SortDialogFragment extends DialogFragment {
    SortDialogFragment.SortDialogListener listener;
    String[] choices;
    int position;
    public SortDialogFragment(String[] choices){
        this.choices = choices;
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        String title = getResources().getString(R.string.setCoverDialogTitle);
        String message = getResources().getString(R.string.setCoverDialogMessage);
        final String acceptButtonString = getResources().getString(R.string.setCoverDialogAcceptButton);
        final String cancelButtonString = getResources().getString(R.string.setCoverDialogCancelButton);
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getActivity());
        builder.setTitle(title);
//        builder.setMessage(message);
        Log.d("dialogHate", choices.length+"");
        builder.setSingleChoiceItems(choices, position, null);
        Log.d("dialogHate", choices.length+" 2");
        builder.setPositiveButton(acceptButtonString, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getActivity(), acceptButtonString,
                        Toast.LENGTH_LONG).show();
                listener.onSortClick(((AlertDialog)dialog).getListView().getCheckedItemPosition());
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

    public interface SortDialogListener{
        void onSortClick(int position);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (SortDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + "must implement ExampleDialogListener");
        }
    }
}
