package com.example.readingdiary.data;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.readingdiary.data.LiteratureContract.NoteTable;

import androidx.annotation.Nullable;

public class OpenHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "literature6.db";


    public static final int DATABASE_VERSION = 1;


    public OpenHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
//        String query = "";
        String query = "CREATE TABLE " + LiteratureContract.NoteTable.TABLE_NAME + " (" +
                LiteratureContract.NoteTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                LiteratureContract.NoteTable.COLUMN_PATH + " TEXT, " +
                LiteratureContract.NoteTable.COLUMN_AUTHOR + " TEXT, " +
                LiteratureContract.NoteTable.COLUMN_TITLE + " TEXT, " +
                NoteTable.COLUMN_COVER_IMAGE + " TEXT, " +
                NoteTable.COLUMN_RATING + " TEXT, " +
                NoteTable.COLUMN_GENRE + " TEXT, " +
                NoteTable.COLUMN_TIME + " TEXT, " +
                NoteTable.COLUMN_PLACE + " TEXT, " +
                NoteTable.COLUMN_SHORT_COMMENT+ " TEXT" +

                ");";

        db.execSQL(query);
        String query1 = "CREATE TABLE " + LiteratureContract.PathTable.TABLE_NAME + " (" +
                LiteratureContract.PathTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                LiteratureContract.PathTable.COLUMN_PARENT + " TEXT, " +
                LiteratureContract.PathTable.COLUMN_CHILD + " TEXT UNIQUE" + ");";
        db.execSQL(query1);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
// todo
        db.execSQL("DROP TABLE IF EXISTS " + NoteTable.TABLE_NAME);

        onCreate(db);
    }
}
