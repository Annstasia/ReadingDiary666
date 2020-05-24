package com.example.readingdiary.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.readingdiary.Classes.DeleteFilesClass;
import com.example.readingdiary.Classes.VariousNotes;
import com.example.readingdiary.R;
import com.example.readingdiary.adapters.VariousViewAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firestore.v1beta1.Write;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;

public class VariousShow extends AppCompatActivity {
    private String id;
    private String type;
    VariousViewAdapter viewAdapter;
    RecyclerView recyclerView;
    ArrayList<VariousNotes> variousNotes;
    ArrayList<Long> variousNotesNames;

    private final int ADD_VIEW_RESULT_CODE = 666;
    File fileDir1;
    MaterialToolbar toolbar;
    TextView counterText;
    int count=0;
    boolean action_mode=false;
    ArrayList<VariousNotes> selectedNotes = new ArrayList<>();
    String user;
    private DocumentReference variousNotePaths;
    private CollectionReference variousNoteStorage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_various_show);
        user = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Bundle args = getIntent().getExtras();
        id = args.get("id").toString();
        type = args.get("type").toString();
        variousNoteStorage = FirebaseFirestore.getInstance().collection("VariousNotes").document(user).collection(id);
        variousNotePaths = variousNoteStorage.document(type);
        variousNotes = new ArrayList<>();
        variousNotesNames = new ArrayList<>();

        fileDir1 = getApplicationContext().getDir(type + File.pathSeparator + id, MODE_PRIVATE);
        openNotes();
        findViews();
        toolbar.getMenu().clear();
        toolbar.setTitle("");
        counterText.setText(type);
        setSupportActionBar(toolbar);

//        counterText.setText("Каталог");
        setAdapters();
        setButtons();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()== R.id.item_delete){
            action_mode=false;
            viewAdapter.setActionMode(false);
            deleteVariousNotes();
            viewAdapter.notifyDataSetChanged();
            toolbar.getMenu().clear();
//            toolbar.inflateMenu(R.menu.menu_catalog);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            counterText.setText(type);
            count=0;
//            selectionList.clear();


        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try{
            if (requestCode == ADD_VIEW_RESULT_CODE && resultCode == RESULT_OK){
                Bundle args = data.getExtras();
                if (args.get("time") != null){
                    long time = Long.parseLong(args.get("time").toString());
                    File file = new File(fileDir1, time+".txt");
                    BufferedReader br = new BufferedReader(new FileReader(file));
                    StringBuilder str = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null){
                        str.append(line);
                        str.append('\n');
                    }
                    variousNotes.add(new VariousNotes(str.toString(), file.getAbsolutePath(), time, false, false));
                    viewAdapter.notifyDataSetChanged();
                }
                else if (args.get("updatePath") != null){
//                    File file = new File(args.get("updatePath").toString());
//                    BufferedReader br = new BufferedReader(new FileReader(file));
//                    StringBuilder str = new StringBuilder();
//                    String line;
//                    while ((line = br.readLine()) != null){
//                        str.append(line);
//                        str.append('\n');
//                    }
                    int position = Integer.parseInt(args.get("position").toString());
                    variousNotes.get(position).setNeedsUpdate(true);
//                    viewAdapter.notifyItemChanged(position);
                }

            }
        }
        catch (Exception e){
            Log.e("resultShowException", e.toString());
        }

    }

    private void deleteVariousNotes(){
        String[] deletePaths = new String[selectedNotes.size()];
        for (int i = 0; i < deletePaths.length; i++){
            variousNotes.remove(selectedNotes.get(i));
            deletePaths[i] = selectedNotes.get(i).getPath();
            variousNotesNames.remove((Long)Long.parseLong(deletePaths[i]));
            variousNotePaths.update(deletePaths[i], FieldValue.delete());
        }
        selectedNotes.clear();
        WriteBatch writeBatch = FirebaseFirestore.getInstance().batch();
        for (int i = 0; i < deletePaths.length; i++){
            writeBatch.delete(variousNoteStorage.document(deletePaths[i]));
//            variousNotes.remove(selectedNotes.get(i));
//            deleteArr[i] = new File(selectedNotes.get(i).getPath());
        }
        writeBatch.commit();
//        DeleteFilesClass deleteClass = new DeleteFilesClass(deleteArr);
//        deleteClass.start();

    }

    private void findViews(){
        recyclerView = (RecyclerView) findViewById(R.id.various_recycler_view);
        toolbar = (MaterialToolbar) findViewById(R.id.long_click_toolbar);
        counterText = (TextView) findViewById(R.id.counter_text);
    }

    private void setAdapters(){
        viewAdapter = new VariousViewAdapter(variousNotes);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(itemAnimator);
        recyclerView.setAdapter(viewAdapter);
        viewAdapter.setOnItemClickListener(new VariousViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(VariousShow.this, VariousNotebook.class);
                intent.putExtra("id", id);
                intent.putExtra("type", type);
                intent.putExtra("path", variousNotes.get(position).getPath());
                intent.putExtra("position", position+"");
                startActivityForResult(intent, ADD_VIEW_RESULT_CODE);
            }

            @Override
            public void onItemLongClick(int position) {
                viewAdapter.setActionMode(true);
                action_mode = true;
                counterText.setText(count + " элементов выбрано");
                toolbar.getMenu().clear();
                toolbar.inflateMenu(R.menu.menu_long_click);
//                toolbar.setMenu(m);
                viewAdapter.notifyDataSetChanged();
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }

            @Override
            public void onCheckClick(int position) {
                selectedNotes.add(variousNotes.get(position));
                count++;
                counterText.setText(count + " элементов выбрано");
                Toast.makeText(getApplicationContext(), selectedNotes.size() + " items selected", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onUncheckClick(int position) {
                selectedNotes.remove(variousNotes.get(position));
                count--;
                counterText.setText(count + " элементов выбрано");

                Toast.makeText(getApplicationContext(), selectedNotes.size() + " items selected", Toast.LENGTH_LONG).show();

            }
        });
    }

    private void setButtons(){
        Button addVariousItem = (Button) findViewById(R.id.addVariousItem);
        addVariousItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VariousShow.this, VariousNotebook.class);
                intent.putExtra("id", id);
                intent.putExtra("type", type);
                startActivityForResult(intent, ADD_VIEW_RESULT_CODE);
            }
        });
    }


    private void openNotes(){
        try{
            variousNotePaths.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                    if (e != null){
                        Log.e("VariousShowOpenNotes", e.toString());
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        return;
                    }
                    else{
                        HashMap<String, Boolean> hashMap = (HashMap) documentSnapshot.getData();
                        if (hashMap!= null){
                            for (String key : hashMap.keySet()){
                                final Long l = Long.parseLong(key);
                                if (!variousNotesNames.contains(l) && hashMap.get(key)==true){
                                    variousNoteStorage.document(key).get()
                                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                @Override
                                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                    if (documentSnapshot != null && documentSnapshot.get("text")!=null){
                                                        variousNotes.add(new VariousNotes(documentSnapshot.get("text").toString(), l+"",
                                                                l, false, false));
                                                        viewAdapter.notifyItemInserted(variousNotes.size());
                                                        variousNotesNames.add(l);
                                                    }

                                                }
                                            });
                                }
                                else if (hashMap.get(key)==true && variousNotesNames.contains(l) &&
                                        variousNotes.get(variousNotesNames.indexOf(l)).isNeedsUpdate()){
                                    variousNoteStorage.document(key).get()
                                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                @Override
                                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                    variousNotes.set(variousNotesNames.indexOf(l), new VariousNotes(documentSnapshot.get("text").toString(), l+"",
                                                            l, false, false));
                                                    viewAdapter.notifyItemChanged(variousNotesNames.indexOf(l));
//                                                    variousNotesNames.add(l);
                                                }
                                            });
                                }

                            }
                        }

                    }
                }
            });

//            variousNotePaths.addSnapshotListener(new EventListener<DocumentSnapshot>() {
//                @Override
//                public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
//                    if (e != null){
//                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
//                        return;
//                    }
//                    else{
//                        HashMap<String, Boolean> hashMap = (HashMap) documentSnapshot.getData();
//                        if (hashMap != null){
//                            for (String key : hashMap.keySet()){
//                                final Long l = Long.parseLong(key);
//                                if (!names.contains(l) && hashMap.get(key)==true){
////                                    variousNoteStorage.child(key).getBytes().toS;
//
//
//                                }
//                                else if (!names.contains(l)){
//                                    if (hashMap.get(key)==false){
//                                        names.add(l);
//                                        images.add(new ImageClass(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.no_image)));
//                                        adapter.notifyItemInserted(images.size()-1);
//                                    }
//                                    else{
//                                        imageStorage.child(key).getDownloadUrl().
//                                                addOnSuccessListener(new OnSuccessListener<Uri>() {
//                                                    @Override
//                                                    public void onSuccess(Uri uri) {
//                                                        Toast.makeText(getApplicationContext(), "uri ", Toast.LENGTH_LONG).show();
//                                                        names.add(l);
//                                                        images.add(new ImageClass(uri));
//                                                        adapter.notifyItemInserted(images.size()-1);
//                                                    }
//                                                })
//                                                .addOnFailureListener(new OnFailureListener() {
//                                                    @Override
//                                                    public void onFailure(@NonNull Exception e) {
//                                                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
//                                                    }
//                                                });
//                                    }
//                                }
//                            }
//                        }
//
//                    }
//
//
//                }
//            });

//            File[] files = fileDir1.listFiles();
//            if (files != null) {
//                for (int i = 0; i < files.length; i++) {
//                    BufferedReader br = new BufferedReader(new FileReader(files[i]));
//                    StringBuilder str = new StringBuilder();
//                    String line;
//                    while ((line = br.readLine()) != null) {
//                        str.append(line);
//                        str.append('\n');
//                    }
//                    String[] pathTokens = files[i].getAbsolutePath().split(File.pathSeparator);
//
//
//                    variousNotes.add(new VariousNotes(str.toString(), files[i].getAbsolutePath(),
//                            Long.parseLong(pathTokens[pathTokens.length - 1].split("\\.")[0].split("/")[1]),
//                            false));
//
//                }
//            }
        }
        catch (Exception e){
            Log.e("openShowException", e.toString());
        }
    }

//    private void saveChanges(){
//        try {
//            for (VariousNotes note : variousNotes) {
//                if (note.isChanged()) {
//                    if (!fileDir1.exists()) fileDir1.mkdirs();
//                    File file = new File(fileDir1, note.getTime() + ".txt");
//                    if (!file.exists()) file.createNewFile();
//                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
//                    // пишем данные
//                    bw.write(note.getText());
//                    // закрываем поток
//                    bw.close();
//                }
//            }
//        }
//        catch (Exception e){
//            Log.e("saveShowException", e.toString());
//        }
//    }

}
