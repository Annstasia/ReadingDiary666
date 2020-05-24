package com.example.readingdiary.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.readingdiary.Classes.DeleteFilesClass;
import com.example.readingdiary.Classes.Directory;
import com.example.readingdiary.Classes.Note;
import com.example.readingdiary.Classes.RealNote;
import com.example.readingdiary.Fragments.SortDialogFragment;
import com.example.readingdiary.R;
import com.example.readingdiary.adapters.CatalogButtonAdapter;
import com.example.readingdiary.adapters.CatalogSortsSpinnerAdapter;
import com.example.readingdiary.adapters.RecyclerViewAdapter;
import com.example.readingdiary.data.LiteratureContract;
import com.example.readingdiary.data.LiteratureContract.NoteTable;
import com.example.readingdiary.data.OpenHelper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class CatalogActivity extends AppCompatActivity implements SortDialogFragment.SortDialogListener {
    // класс отвечает за активность с каталогами
//    OpenHelper dbHelper;
    RecyclerViewAdapter mAdapter;
    //    SQLiteDatabase sdb;
    String parent = "./";
    ArrayList<Note> notes;
    ArrayList<String> buttons;
    //    ArrayList<String> directories;
    RecyclerView recyclerView;
    RecyclerView buttonView;
    CatalogButtonAdapter buttonAdapter;
    CatalogSortsSpinnerAdapter sortsAdapter;
    Button findButton;
    EditText findText1;
    EditText findText;
    ArrayList<String> sortsList;
    Spinner sortsSpinner;
    MaterialToolbar toolbar;
    TextView counterText;
    String comp="";
    String sortTitles1, sortTitles2, sortAuthors1, sortAuthors2, sortRating1, sortRating2;
    int order;
    int startPos;
    int NOTE_REQUEST_CODE = 12345;
    int CREATE_NOTE_REQUEST_CODE = 12346;
    public boolean action_mode = false;
    int count=0;
    int menuType = 0;
    ArrayList<RealNote> selectionRealNotesList = new ArrayList<>();
    ArrayList<Directory> selectionDirectoriesList = new ArrayList<>();
    String[] choices = new String[]{"Сортировка по названиям в лексикографическом порядке",
            "Сортировка по названиям в обратном лексикографическим порядке",
            "Сортировка по автору в лексиграфическом порядке",
            "Сортировка по автору в обратном лексиграфическим порядке",
            "Сортировка по возрастанию рейтинга",
            "Сортировка по убыванию рейтинга"};
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String user = "user0";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);
        user = FirebaseAuth.getInstance().getCurrentUser().getUid();

//        Получение разрешений на чтение и запись
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            Log.v("FILE3", "Permission is granted");

        } else {
            Log.v("FILE3", "Permission is revoked");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

        }
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            Log.v("FILE3", "Permission is granted");

        } else {

            Log.v("FILE3", "Permission is revoked");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

        }



//        dbHelper = new OpenHelper(this);
//
//        sdb = dbHelper.getReadableDatabase();
        notes = new ArrayList<Note>(); // список того, что будет отображаться в каталоге.
        buttons = new ArrayList<String>(); // Список пройденный каталогов до текущего
        setSortTitles();
//        initSortsList();
        findViews();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        counterText.setText("Каталог");
//        toolbar.inflateMenu(R.menu.menu_catalog);

        buttons.add(parent);
        selectAll(); // чтение данных из бд

        setAdapters();

        // Кнопка добавление новой активности
        FloatingActionButton addNote = (FloatingActionButton) findViewById(R.id.addNote);
        addNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                // Возвращается intent, если пользователь действительно добавил активность
                Intent intent = new Intent(CatalogActivity.this, EditNoteActivity.class);

                intent.putExtra("path", parent);
                Log.d("putExtra", parent + " !");
                startActivityForResult(intent, CREATE_NOTE_REQUEST_CODE);


            }
        });
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN){
//            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            InputMethodManager imm = (InputMethodManager) getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
        }
        return super.dispatchTouchEvent(event);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("qwerty15", "OnActivityResult");
        Log.d("qwerty15", "requestCode " + requestCode + " " + resultCode);

        if (data != null && requestCode == NOTE_REQUEST_CODE){
            // если изменился путь до записи, добавилась новая запись, то переходим к этой записи
            if (data.getExtras().get("deleted") != null){
                String id = data.getExtras().get("id").toString();
                int index = deleteNote(id);
                if (index != -1){
                    mAdapter.notifyItemRemoved(index);
                }
            }

            if (data.getExtras().get("path") != null){
                parent = data.getExtras().get("path").toString().replace("\\", "/");
                reloadRecyclerView();
                reloadButtonsView();
            }
        }

        if (requestCode==CREATE_NOTE_REQUEST_CODE && resultCode == RESULT_OK){
            Log.d("qwerty15", data.getExtras().get("deleted") + " ! " + data.getExtras().get("noNote") + " !");
            if ((data.getExtras().get("deleted") == null && data.getExtras().get("noNote") == null)){
                Log.d("qwerty15", "hi");

                Intent intent = new Intent(CatalogActivity.this, NoteActivity.class); // вызов активности записи
                intent.putExtra("id", data.getExtras().get("id").toString()); // передаем id активности в бд, чтобы понять какую активность надо показывать
                intent.putExtra("changed", "true");
                startActivityForResult(intent, NOTE_REQUEST_CODE);
            }
            else if (data.getExtras().get("noNote") != null){
                parent = data.getExtras().get("path").toString();
                reloadRecyclerView();
                reloadButtonsView();
            }

        }



    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId()== R.id.item_delete){

            action_mode=false;
            mAdapter.setActionMode(false);
            deleteSelectedRealNote();
            deleteSelectedDirectories();
            mAdapter.notifyDataSetChanged();
//            RecyclerViewAdapter recyclerViewAdapter = (RecyclerViewAdapter) mAdapter;
//            recyclerViewAdapter.updateAdapter(selectionList);
            toolbar.getMenu().clear();
            toolbar.inflateMenu(R.menu.menu_catalog);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            menuType = 0;
            counterText.setText("Каталог");
            count=0;
//            selectionList.clear();


        }
        if (item.getItemId() == R.id.item_search){
            counterText.setVisibility(View.GONE);
            findText1.setVisibility(View.VISIBLE);
            toolbar.getMenu().clear();
            toolbar.inflateMenu(R.menu.menu_search);
//            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            menuType = 2;

        }
        if (item.getItemId() == R.id.item_search1){
//            counterText.setVisibility(View.VISIBLE);
//            findText1.setVisibility(View.GONE);
//            toolbar.getMenu().clear();
//            toolbar.inflateMenu(R.menu.menu_catalog);
            notes.clear();
//                buttons.clear();
            selectTitle(findText1.getText().toString());
            mAdapter.notifyDataSetChanged();
            findText1.clearComposingText();
            counterText.setVisibility(View.VISIBLE);
            findText1.setVisibility(View.GONE);
            toolbar.getMenu().clear();
            toolbar.inflateMenu(R.menu.menu_catalog);
            menuType = 0;
//            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }

        if (item.getItemId() == R.id.item_sort){
            SortDialogFragment sortDialogFragment = new SortDialogFragment(choices);
//            SaveDialogFragment saveDialogFragment = new SaveDialogFragment();
            FragmentManager manager = getSupportFragmentManager();
            //myDialogFragment.show(manager, "dialog");

            FragmentTransaction transaction = manager.beginTransaction();
            sortDialogFragment.show(transaction, "dialog");
        }

        if (item.getItemId() == android.R.id.home){
            if (menuType==0){
                finish();
            }
            else if (menuType == 1){
                action_mode=false;
                mAdapter.setActionMode(false);
                mAdapter.notifyDataSetChanged();
                toolbar.getMenu().clear();
                toolbar.inflateMenu(R.menu.menu_catalog);
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                menuType = 0;
                counterText.setText("Каталог");
                count=0;
            }
            else if (menuType==2){
                findText1.clearComposingText();
                counterText.setVisibility(View.VISIBLE);
                findText1.setVisibility(View.GONE);
                toolbar.getMenu().clear();
                toolbar.inflateMenu(R.menu.menu_catalog);
                menuType = 0;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void deleteInStorage(String id, final String delEl){
        final StorageReference storageReference = FirebaseStorage.getInstance().getReference(user).child(id);
        db.collection("Common").document(user).collection(id).document(delEl).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Map <String, Boolean> map = (HashMap) documentSnapshot.getData();
//                            ArrayList<String> arrayList = (ArrayList<String>) documentSnapshot.get("Paths");
                        if (map != null){
                            for (String path : map.keySet()){
                                storageReference.child(delEl).child(path).delete();
//                                FirebaseStorage.getInstance().getReference(user).child(id)
                            }
                        }

                    }
                });
        db.collection("Common").document(user).collection(id).document(delEl).delete();
    }

    public void deleteSelectedRealNote(){
        for (int i = 0; i < selectionRealNotesList.size(); i++){
            String id = selectionRealNotesList.get(i).getID();
            notes.remove(selectionRealNotesList.get(i));
            db.collection("Notes").document(user).collection("userNotes").document(id).delete();
            deleteInStorage(id, "Images");
            deleteInStorage(id, "Comment");
            deleteInStorage(id, "Description");
            deleteInStorage(id, "Quotes");
        }
        mAdapter.notifyDataSetChanged();


//        File fileArr[] = new File[selectionRealNotesList.size() * 4];
//        for (int i = 0; i < selectionRealNotesList.size() * 4; i+=4){
//            String id = selectionRealNotesList.get(i / 4).getID() + "";
////            sdb.delete(NoteTable.TABLE_NAME, NoteTable._ID + " = ? ", new String[]{id+""});
//            db.collection("Notes").document(user).collection("userNotes").document(id).delete();
//            notes.remove(selectionRealNotesList.get(i / 4));
//            fileArr[i] = getApplicationContext().getDir(getResources().getString(R.string.imagesDir) + File.pathSeparator + id, MODE_PRIVATE);
//            fileArr[i + 1] = getApplicationContext().getDir(getResources().getString(R.string.descriptionDir) + File.pathSeparator + id, MODE_PRIVATE);
//            fileArr[i + 2] = getApplicationContext().getDir(getResources().getString(R.string.commentDir) + File.pathSeparator + id, MODE_PRIVATE);
//            fileArr[i + 3] = getApplicationContext().getDir(getResources().getString(R.string.quoteDir) + File.pathSeparator + id, MODE_PRIVATE);
//        }
//        DeleteFilesClass deleteClass = new DeleteFilesClass(fileArr);
//        deleteClass.start();
//        selectionRealNotesList.clear();
//        mAdapter.notifyDataSetChanged();
    }

    public void deleteSelectedDirectories(){
        for (Directory directory : selectionDirectoriesList){
            notes.remove(directory);
            deleteDirectory(directory.getDirectory().replace("/", "\\"));
            String s = directory.getDirectory();
            String parDoc = s.substring(0, s.substring(0, s.length() - 1).lastIndexOf("/")+1).replace("/", "\\");
            db.collection("User").document(user).collection("paths").document(parDoc).update("paths", FieldValue.arrayRemove(s.replace("/", "\\")));
        }
        selectionDirectoriesList.clear();
    }

    @Override
    public void onSortClick(int position) {
        Log.d("strangeSort", choices[position]);
        startSort(choices[position]);
    }


    public void deleteDirectory(String path){
        final String path1 = path;
        final File dir0 = new File(path);
        Log.d("qwerty12", "start " + path1);
        db.collection("User").document(user).collection("paths").whereEqualTo("parent", path1).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots != null){
                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
//                                Log.d("qwerty12", path1 + " "  + documentSnapshot.getId());
                                deleteDirectory(documentSnapshot.getId());
//                                ArrayList<String> list = (ArrayList) documentSnapshot.get("paths");
//                                if (list != null){
//                                    for (String i : list){
//                                        Log.d("qwerty12", documentSnapshot.getId() + " list " + i);
//                                        deleteDirectory(i);
//                                    }
//
//                                }

                            }
                        }
                    }
                });
        db.collection("User").document(user).collection("paths").document(path1).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(), "success", Toast.LENGTH_LONG).show();

//                        Log.d("qwerty12", "Success " + path1);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("qwerty12", e.toString());
                    }
                });

        db.collection("Notes").document(user).collection("userNotes").whereEqualTo("path", path1).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        if (queryDocumentSnapshots != null){
                            WriteBatch batch = db.batch();
//                            File[] arr = new File[queryDocumentSnapshots.size()];
                            int k = 0;

                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
//                                db.document(documentSnapshot.getId()).delete();

                                //            arr[k] = getApplicationContext().getDir(path + "/" + currentID, MODE_PRIVATE);
//                                arr[k] = new File(dir0 + File.pathSeparator + documentSnapshot.getId());
                                // Тут содержится pathSeparator, на что ide ругается
//                                k++;
                                deleteInStorage(documentSnapshot.getId(), "Images");
                                deleteInStorage(documentSnapshot.getId(), "Comment");
                                deleteInStorage(documentSnapshot.getId(), "Description");
                                deleteInStorage(documentSnapshot.getId(), "Quotes");

                                batch.delete(db.collection("Notes").document(user).collection("userNotes").document(documentSnapshot.getId()));
                            }
                            batch.commit();
//                            DeleteFilesClass deleteFilesClass = new DeleteFilesClass(arr);
//                            deleteFilesClass.start();
                        }

                    }
                });



    }

//    private void deleteFileDir(String path1, String id){
//        File fileDir1 = getApplicationContext().getDir(path1 + File.pathSeparator + id, MODE_PRIVATE);
//        if (!fileDir1.exists()) return;
//
//        File files1[] = fileDir1.listFiles();
//        if (files1 != null){
//            for (File file : files1){
//                file.delete();
//            }
//        }
//        fileDir1.delete();
//    }

    private int deleteNote(String id){
        int index = -1;
        for (int i = 0; i < notes.size(); i++){
            if (notes.get(i).getID().equals(id)){
                index = i;
                break;
            }
        }
        if (index != -1){
            notes.remove(index);
        }
        db.collection("Notes").document(user).collection("userNotes").document(id).delete();
        deleteInStorage(id, "Images");
        deleteInStorage(id, "Comment");
        deleteInStorage(id, "Description");
        deleteInStorage(id, "Quotes");

//        DeleteFilesClass deleteClass = new DeleteFilesClass(new File[]
//                {
//                        getApplicationContext().getDir(getResources().getString(R.string.imagesDir) + File.pathSeparator + id, MODE_PRIVATE),
//                        getApplicationContext().getDir(getResources().getString(R.string.quoteDir) + File.pathSeparator + id, MODE_PRIVATE),
//                        getApplicationContext().getDir(getResources().getString(R.string.descriptionDir) + File.pathSeparator + id, MODE_PRIVATE),
//                        getApplicationContext().getDir(getResources().getString(R.string.commentDir) + File.pathSeparator + id, MODE_PRIVATE)
//                });
//        deleteClass.start();
//        deleteFileDir(getResources().getString(R.string.imagesDir), id);
//        deleteFileDir(getResources().getString(R.string.commentDir), id);
//        deleteFileDir(getResources().getString(R.string.descriptionDir), id);
//        deleteFileDir(getResources().getString(R.string.quoteDir), id);
        return index;
    }

    private void selectAll() {
        Toast.makeText(this, parent, Toast.LENGTH_LONG).show();
        final String par1 = parent.replace("/", "\\");
        Log.d("qwerty11", par1);
        db.collection("User").document(user).collection("paths").document(par1).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Log.d("qwerty11", par1 + " child");
                        Log.d("qwerty8", "onSuccess");
                        if (documentSnapshot != null){
                            Log.d("qwerty11", "length " + par1);
                            ArrayList<String> list = (ArrayList<String>) documentSnapshot.get("paths");
//                            Log.d("qwerty11", "length " + list.size());
                            if (list != null) {
                                for (String i : list) {
                                    notes.add(new Directory(i, i.replace("\\", "/")));
                                }
                            }
                        }
                        startPos = notes.size();
                        mAdapter.notifyDataSetChanged();

                        db.collection("Notes").document(user).collection("userNotes").whereEqualTo("path", par1).get()
                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        if (queryDocumentSnapshots != null){
                                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                                HashMap<String, Object> map = (HashMap<String, Object>) documentSnapshot.getData();
                                                notes.add(new RealNote(documentSnapshot.getId(), map.get("path").toString(),
                                                        map.get("author").toString(), map.get("title").toString(), Double.valueOf(map.get("rating").toString())));
                                            }
                                        }
                                        mAdapter.notifyDataSetChanged();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
                                        Log.e("qwerty9", e.toString());
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
                        Log.e("qwerty10", e.toString());
                    }
                });
    }

    private void selectTitle(String title){
        db.collection("Notes").document(user).collection("userNotes").whereEqualTo("title", title).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots != null){
                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                HashMap<String, Object> map = (HashMap<String, Object>) documentSnapshot.getData();
                                notes.add(new RealNote(documentSnapshot.getId(), map.get("path").toString(),
                                        map.get("author").toString(), map.get("title").toString(), Double.valueOf(map.get("rating").toString())));
                            }
                        }
                        mAdapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
                        Log.e("qwerty9", e.toString());
                    }
                });
    }

    private void setSortTitles(){
        sortTitles1 = "по названиям по возрастанию";
        sortTitles2 = "по названиям по убыванию";
        sortAuthors1 = "по автору по возрастанию";
        sortAuthors2 = "по автору по убыванию";
        sortRating1 = "по рейтингу по возрастанию";
        sortRating2 = "по рейтингу по убыванию";
    }

//    private void initSortsList(){
//        sortsList = new ArrayList<>();
//        sortsList.add("");
//        sortsList.add("Сортировка по названиям в лексикографическом порядке");
//        sortsList.add("Сортировка по названиям в обратном лексикографическим порядке");
//        sortsList.add("Сортировка по автору в лексиграфическом порядке");
//        sortsList.add("Сортировка по автору в обратном лексиграфическим порядке");
//        sortsList.add("Сортировка по возрастанию рейтинга");
//        sortsList.add("Сортировка по убыванию рейтинга");
//    }

    private void reloadRecyclerView(){
        // перезагрузка recyclerView. Удаляются все элементы notes, выбираются новые из бд
        notes.clear();
        selectAll();
        mAdapter.notifyDataSetChanged();
    }

    private void reloadButtonsView(){
        // перезагрузка buttonView. Удаляются все элементы button, выбираются новые из текущего пути
        buttons.clear();
        String pathTokens[] = (parent).split("/");
        // текущий путь - строка из названий директорий
        String prev = "";
        for (int i = 0; i < pathTokens.length; i++){
            if (pathTokens[i].equals("")){
                continue;
            }
            prev = prev + pathTokens[i] + "/";
            buttons.add(prev);
        }
        buttonAdapter.notifyDataSetChanged();
    }

    private void findViews(){
        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewCatalog);  // здесь будут отображаться каталоги и файлы notes
        buttonView = (RecyclerView) findViewById(R.id.buttonViewCatalog);  // здесь будут отображаться пройденные поддиректории buttons
//        sortsSpinner = (Spinner) findViewById(R.id.spinnerSorts);
        toolbar = (MaterialToolbar) findViewById(R.id.long_click_toolbar);
//        findButton = (Button) findViewById(R.id.findButton);
//        findText = (EditText) findViewById(R.id.findText);
        counterText = (TextView) findViewById(R.id.counter_text);
        findText1 = (EditText) findViewById(R.id.editTextFind);
    }

    private void setAdapters(){
        mAdapter = new RecyclerViewAdapter(notes);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(itemAnimator);


        Button sigout = (Button) findViewById(R.id.button2);
        sigout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity out = new MainActivity();
                out.signOut();
            }
        });


        mAdapter.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                // В notes хранятся объекты двух классов, имплементирующих Note - RealNote и Directory
                // RealNote - собственно запись пользователя. При клике нужно перейти к записи, т.е к NoteActivity
                // Directory - директория. При клике нужно перейти в эту директорию.
                int type = notes.get(position).getItemType();
                if (type == 0){
                    RealNote realNote = (RealNote) notes.get(position);
                    Intent intent = new Intent(CatalogActivity.this, NoteActivity.class);
                    // чтобы понять какую запись нужно отобразить в NoteActivity, запихиваем в intent id записи из бд
                    intent.putExtra("id", realNote.getID());
                    startActivityForResult(intent, NOTE_REQUEST_CODE); // в NoteActivity пользователь может изменить путь.
                    //Если изменит, то вернется intent, чтобы можно было изменить отображение каталогов
                }
                if (type == 1){
                    Directory directory = (Directory) notes.get(position);
                    parent = directory.getDirectory(); // устанавливаем директорию, на которую нажали в качестве отправной
                    notes.clear();
                    Log.d("qwerty17", parent);
                    buttons.add(parent);
                    buttonAdapter.notifyDataSetChanged();
                    selectAll(); // выбираем новые данные из бд
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onItemLongClick(int position) {
                mAdapter.setActionMode(true);
                action_mode = true;
                counterText.setText(count + " элементов выбрано");
                toolbar.getMenu().clear();
                toolbar.inflateMenu(R.menu.menu_long_click);
                menuType=1;
//                toolbar.setMenu(m);
                mAdapter.notifyDataSetChanged();
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }

            @Override
            public void onCheckClick(int position) {
                count++;
                counterText.setText(count + " элементов выбрано");
                Note note = notes.get(position);
                if (note.getItemType()==1){
                    selectionDirectoriesList.add((Directory) note);
                    Toast.makeText(getApplicationContext(), selectionDirectoriesList.size() + " Directory", Toast.LENGTH_LONG).show();
                }
                else{
                    selectionRealNotesList.add((RealNote) note);
                    Toast.makeText(getApplicationContext(), selectionRealNotesList.size() + " RealNote", Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onUncheckClick(int position) {
                count--;
                counterText.setText(count + " элементов выбрано");
                Note note = notes.get(position);
                if (note.getItemType()==1){
                    selectionDirectoriesList.remove((Directory) note);
                    Toast.makeText(getApplicationContext(), selectionDirectoriesList.size() + " Directory", Toast.LENGTH_LONG).show();
                }
                else{
                    selectionRealNotesList.remove((RealNote) note);
                    Toast.makeText(getApplicationContext(), selectionRealNotesList.size() + " RealNote", Toast.LENGTH_LONG).show();
                }

            }
        });



        buttonAdapter = new CatalogButtonAdapter(buttons);
        LinearLayoutManager layoutManager1 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        RecyclerView.ItemAnimator itemAnimator1 = new DefaultItemAnimator();
        buttonView.setAdapter(buttonAdapter);
        buttonView.setLayoutManager(layoutManager1);
        buttonView.setItemAnimator(itemAnimator1);
        buttonAdapter.setOnItemClickListener(new CatalogButtonAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                parent = buttons.get(position);
                reloadButtonsView();
                reloadRecyclerView();
            }
        });
//
//        sortsAdapter = new CatalogSortsSpinnerAdapter(this, sortsList);
//        sortsSpinner.setAdapter(sortsAdapter);
//        sortsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                String clickedItem = (String) parent.getItemAtPosition(position);
//                startSort(clickedItem);
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//            }
//        });
    }
//

    private void startSort(String sortType) {
        if (sortType.equals(sortTitles1)){
            comp = "title";
            order = 1;
        }
        if (sortType.equals(sortTitles2)){
            comp = "title";
            order = -1;
        }
        if (sortType.equals(sortAuthors1)){
            comp = "author";
            order = 1;
        }
        if (sortType.equals(sortAuthors2)){
            comp = "author";
            order = -1;
        }
        if (sortType.equals(sortRating1)){
            comp = "rating";
            order = 1;
        }
        if (sortType.equals(sortRating2)){
            comp = "rating";
            order = -1;
        }
        quickSort(startPos, notes.size() - 1);
        mAdapter.notifyDataSetChanged();


    }

    private void quickSort(int from, int to) {
        if (from < to) {
            int divideIndex;
            if (comp != "rating"){
                divideIndex = partitionString(from, to);
            }
            else{
                divideIndex = partitionDouble(from, to);
            }

            quickSort(from, divideIndex - 1);
            quickSort(divideIndex, to);
        }
    }
    private int partitionString(int from, int to)
    {
        int rightIndex = to;
        int leftIndex = from;

        String pivot = getComparable((RealNote) notes.get(from + (to - from) / 2));
        while (leftIndex <= rightIndex)
        {

            while (order * (getComparable((RealNote) notes.get(leftIndex)).compareTo(pivot)) < 0)
            {
                leftIndex++;
            }

            while (order * (getComparable((RealNote) notes.get(rightIndex)).compareTo(pivot)) > 0)
            {
                rightIndex--;
            }

            if (leftIndex <= rightIndex)
            {
                swap(rightIndex, leftIndex);
                leftIndex++;
                rightIndex--;
            }
        }
        return leftIndex;
    }

    private int partitionDouble(int from, int to){
        int rightIndex = to;
        int leftIndex = from;

        Double pivot = getComparableDouble((RealNote) notes.get(from + (to - from) / 2));
        while (leftIndex <= rightIndex)
        {

            while (order * (getComparableDouble((RealNote) notes.get(leftIndex)).compareTo(pivot)) < 0)
            {
                leftIndex++;
            }

            while (order * (getComparableDouble((RealNote) notes.get(rightIndex)).compareTo(pivot)) > 0)
            {
                rightIndex--;
            }

            if (leftIndex <= rightIndex)
            {
                swap(rightIndex, leftIndex);
                leftIndex++;
                rightIndex--;
            }
        }
        return leftIndex;
    }


    private String getComparable(RealNote realNote){
        if (comp.equals("title")){
            return realNote.getTitle();
        }
        if (comp.equals("author")){
            return realNote.getAuthor();
        }

        return "";
    }

    private void swap(int index1, int index2)
    {
        Note tmp  = notes.get(index1);
        notes.set(index1, notes.get(index2));
        notes.set(index2, tmp);
    }

    private Double getComparableDouble(RealNote realNote){
        return realNote.getRating();
    }




}
