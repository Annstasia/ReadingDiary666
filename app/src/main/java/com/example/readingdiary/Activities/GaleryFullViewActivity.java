package com.example.readingdiary.Activities;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.readingdiary.Classes.ImageClass;
import com.example.readingdiary.Fragments.DeleteDialogFragment;
import com.example.readingdiary.Fragments.SetCoverDialogFragment;
import com.example.readingdiary.R;
import com.example.readingdiary.adapters.GaleryFullViewAdapter;
import com.example.readingdiary.data.LiteratureContract;
import com.example.readingdiary.data.OpenHelper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Nullable;


public class GaleryFullViewActivity extends AppCompatActivity implements DeleteDialogFragment.DeleteDialogListener, SetCoverDialogFragment.SetCoverDialogListener {
    private RecyclerView galeryFullView;;
    int position;
    private GaleryFullViewAdapter adapter;
    private List<ImageClass> images;
    private List<Long> names;
    private boolean changed = false;
    String id;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String user;
    private StorageReference imageStorage;
    private DocumentReference imagePathsDoc;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_galery_full_view);
        // открываем и сохраняем в список изображения для данной записи
        user = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Bundle args = getIntent().getExtras();
        id = args.get("id").toString();
        imagePathsDoc = FirebaseFirestore.getInstance().collection("Common").document(user).collection(id).document("Images");
        imageStorage = FirebaseStorage.getInstance().getReference(user).child(id).child("Images");
        position = Integer.parseInt(args.get("position").toString());
        images = new ArrayList<>();
        names = new ArrayList<>();

//        File fileDir1 = getApplicationContext().getDir(getResources().getString(R.string.imagesDir) + File.pathSeparator + id, MODE_PRIVATE);
//        File[] files = fileDir1.listFiles();
//        if (files != null){
//            for (int i = 0; i < files.length; i++){
//                images.add(BitmapFactory.decodeFile(files[i].getAbsolutePath()));
//                names.add(files[i].getAbsolutePath());
//            }
//        }
        Button deleteButton = (Button) findViewById(R.id.deleteFullImageButton);
        Button coverButton = (Button) findViewById(R.id.setAsCoverButton);
        galeryFullView = (RecyclerView) findViewById(R.id.galery_full_recycle_view);

        // добавляем адаптер
        adapter = new GaleryFullViewAdapter(images, getApplicationContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        layoutManager.scrollToPosition(position);
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        galeryFullView.setAdapter(adapter);
        galeryFullView.setLayoutManager(layoutManager);

        galeryFullView.setItemAnimator(itemAnimator);
        final LinearLayout buttonsLayout = (LinearLayout) findViewById(R.id.full_view_button_layout);


        imagePathsDoc.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                Log.d("qwerty31", "HI");
                if (e != null){
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    return;
                }
                else{
                    HashMap<String, Boolean> hashMap = (HashMap) documentSnapshot.getData();
                    if (hashMap != null){
                        for (String key : hashMap.keySet()){
                            final Long l = Long.parseLong(key);
                            if (names.contains(l) && hashMap.get(key) == true && images.get(names.indexOf(l)).getType()==0){
                                imageStorage.child(key).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        Log.d("qwerty31", "HI2");
                                        images.set(names.indexOf(l), new ImageClass(uri));
                                        adapter.notifyItemChanged(names.indexOf(l));
                                    }
                                })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                            }
                                        });

                            }
                            else if (!names.contains(l)){
                                if (hashMap.get(key)==false){
                                    Log.d("qwerty31", "HI3");
//                                    names.add(l);
//                                    images.add(new ImageClass(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.no_image)));
//                                    adapter.notifyItemInserted(images.size()-1);
                                    int index = -1;
                                    for (int i = 0; i < names.size(); i++){
                                        if (names.get(i) > l){
                                            index = i;
                                            break;
                                        }
                                    }
                                    if (index==-1){
                                        names.add(l);
                                        images.add(new ImageClass(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.no_image)));
                                        adapter.notifyItemInserted(images.size()-1);
                                    }
                                    else{
                                        names.add(index, l);
                                        images.add(index, new ImageClass(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.no_image)));
                                        adapter.notifyItemInserted(index);
                                    }
                                }
                                else{
                                    imageStorage.child(key).getDownloadUrl().
                                            addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    int index = -1;
                                                    for (int i = 0; i < names.size(); i++){
                                                        if (names.get(i) > l){
                                                            index = i;
                                                            break;
                                                        }
                                                    }
                                                    if (index==-1){
                                                        names.add(l);
                                                        images.add(new ImageClass(uri));
                                                        adapter.notifyItemInserted(images.size()-1);
                                                    }
                                                    else{
                                                        names.add(index, l);
                                                        images.add(index, new ImageClass(uri));
                                                        adapter.notifyItemInserted(index);
                                                    }
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.d("qwerty31", "HI5");
                                                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                                }
                                            });
                                }
                            }
                        }
                    }

                }


            }
        });

        final Handler uiHandler = new Handler();

        final Runnable makeLayoutGone = new Runnable(){
            @Override
            public void run(){
                buttonsLayout.setVisibility(View.INVISIBLE);
            }
        };

        // при нажатии на картинку появляется менюшка к ней. Там есть кнопки удаления и установки в качестве обложки
        adapter.setOnItemClickListener(new GaleryFullViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int pos) {
                buttonsLayout.setVisibility(View.VISIBLE);
                position = pos;

                // через 8 секунд меню пропадает
                uiHandler.postDelayed(makeLayoutGone, 8000);
            }
        });


        // кнопка удаления. При нажатии изображение удаляется
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogDeleteOpen();
//                File file = new File(names.get(position));
//                if (file.exists()){
//                    dialogDeleteOpen();
//                }

            }
        });
        coverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogSetCoverOpen();

            }
        });
    }

    @Override
    public void onDeleteClicked() {
        String toDel = ""+names.get(position);
        names.remove(position);
        images.remove(position);
        adapter.notifyDataSetChanged();
        imageStorage.child(toDel).delete();
        imagePathsDoc.update(toDel, FieldValue.delete());
//        File file = new File(names.get(position));
        if (!changed){
            changed=true;
            setResultChanged();
        }
//        if (file.exists()){
//            file.delete();
//            names.remove(position);
//            images.remove(position);
//            adapter.notifyDataSetChanged();
//            // Отмечаем, что список изображений был изменен - нужно для возвращаемого интента
//            if (!changed){
//                changed=true;
//                setResultChanged();
//            }
//        }
    }

    @Override
    public  void onSetCover() {
        db.collection("Notes").document(user).collection("userNotes").document(id).
                update("imagePath", names.get(position));

//        OpenHelper dbHelper = new OpenHelper(getApplicationContext());
//        SQLiteDatabase sdb = dbHelper.getReadableDatabase();
//        ContentValues cv = new ContentValues();
//        cv.put(LiteratureContract.NoteTable.COLUMN_COVER_IMAGE, names.get(position));
//        Log.d("IMAGE1", "!!! " + names.get(position));
//        sdb.update(LiteratureContract.NoteTable.TABLE_NAME, cv, LiteratureContract.NoteTable._ID + " = " + id, null);
//        Log.d("IMAGE1", "!!!end " + id);
    }

    private void dialogDeleteOpen(){
        DeleteDialogFragment dialog = new DeleteDialogFragment();
        dialog.show(getSupportFragmentManager(), "deleteDialog");
    }

    private void dialogSetCoverOpen(){
        SetCoverDialogFragment dialog = new SetCoverDialogFragment();
        dialog.show(getSupportFragmentManager(), "setCover");
    }

    private void setResultChanged(){
        // создание возвращаемого интента
        Log.d("DELETEIMAGE1", "resultChanged");
        Intent returnIntent = new Intent();
        returnIntent.putExtra("changed", changed);
        setResult(RESULT_OK, returnIntent);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
