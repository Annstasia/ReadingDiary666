package com.example.readingdiary.Activities;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.readingdiary.Classes.DeleteNote;
import com.example.readingdiary.Classes.SaveImage;
import com.example.readingdiary.Fragments.CreateWithoutNoteDialogFragment;
import com.example.readingdiary.Fragments.DeleteDialogFragment;
import com.example.readingdiary.Fragments.DeleteTitleAndAuthorDialogFragment;
import com.example.readingdiary.Fragments.SaveDialogFragment;
import com.example.readingdiary.R;
import com.example.readingdiary.data.LiteratureContract.NoteTable;
import com.example.readingdiary.data.LiteratureContract.PathTable;
import com.example.readingdiary.data.OpenHelper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditNoteActivity extends AppCompatActivity implements DeleteDialogFragment.DeleteDialogListener,
        CreateWithoutNoteDialogFragment.CreateWithoutNoteDialogListener,
        SaveDialogFragment.SaveDialogListener {
    EditText pathView;
    EditText titleView;
    EditText authorView;
    RatingBar ratingView;
    EditText genreView;
    EditText timeView;
    EditText placeView;
    EditText shortCommentView;
    ImageView coverView;
    String imagePath="";
    String oldPath="";
    //    SQLiteDatabase sdb;
//    OpenHelper dbHelper;
    String id;
    boolean isNoteNew;
    String path;
    boolean change = false;
    private ImageView imageView;
    private final int Pick_image = 1;
    private final int EDIT_REQUEST_CODE = 123;
    private String[] beforeChanging;
    private final int GALERY_REQUEST_CODE = 124;
    private String user = "user0";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference imageStorage;
    private DocumentReference imagePathsDoc;
    private Bitmap cover;
    private long time;
//    String newId


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);
        user = FirebaseAuth.getInstance().getCurrentUser().getUid();
        findViews();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Bundle args = getIntent().getExtras();

        if (args != null && args.get("id") != null){
            isNoteNew=false;
            id = args.get("id").toString();
            select(id);
        }
        else if (args != null && args.get("path") != null){
            isNoteNew=true;
            id = db.collection("Notes").document(user).collection("userNotes").document().getId();
            path = args.get("path").toString();
            beforeChanging = new String[]{path, "", "", "0.0", "", "", "", "", ""};
            setViews(beforeChanging);
        }
        else{
            isNoteNew=true;
            id = db.collection("Notes").document(user).collection("userNotes").document().getId();
            path = "./";
            beforeChanging = new String[]{"./", "", "", "0.0", "", "", "", "", ""};
            setViews(beforeChanging);
        }
        imagePathsDoc = FirebaseFirestore.getInstance().collection("Common").document(user).collection(id).document("Images");
        imageStorage = FirebaseStorage.getInstance().getReference(user).child(id).child("Images");
        Log.d("putExtra", "start");
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setButtons();
        setFocuses();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_note, menu);
        return true;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN){
            InputMethodManager imm = (InputMethodManager) getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
            setCursorsVisible(false);
        }

        return super.dispatchTouchEvent(event);
    }

    @Override
    public void onDeleteClicked() {
        deleteNote();
        Intent returnIntent = new Intent();
        returnIntent.putExtra("deleted", "true");
        returnIntent.putExtra("id", id);
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    @Override
    public void onCreateWithoutNoteClicked() {
        String path1 = pathView.getText().toString();
        path1 = fixPath(path1);
        if (!beforeChanging[0].equals(path1)){
            beforeChanging[0] = path1;
            savePaths();
        }
        if (!imagePath.equals("")){
            new DeleteNote().deleteImages(user, id);
        }
        Intent returnIntent = new Intent();
        returnIntent.putExtra("noNote", "true");
        returnIntent.putExtra("path", path);
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    @Override
    public void onSaveClicked() {
        if (saveChanges()){
            finish();
        }
    }

    @Override
    public void onNotSaveClicked() {
        Log.d("qwerty44", imagePath + " " + beforeChanging[8] + " " + isNoteNew);
        if (!imagePath.equals(beforeChanging[8])){
            if (isNoteNew){
                new DeleteNote().deleteImages(user, id);
            }
            else{
                cancelImageChange();
            }
        }
//            Toast.makeText(getApplicationContext(), "werfgthjk", 1).show();

        finish();
    }

    private void setFocuses(){
        setFocuse(pathView);
        setFocuse(titleView);
        setFocuse(authorView);
        setFocuse(timeView);
        setFocuse(placeView);
        setFocuse(shortCommentView);

    }

    private void setFocuse(final EditText editText){
        editText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP){
                    editText.setCursorVisible(true);
                }
                return false;
            }
        });
    }

    private void setCursorsVisible(boolean arg){
        pathView.setCursorVisible(arg);
        titleView.setCursorVisible(arg);
        authorView.setCursorVisible(arg);
        timeView.setCursorVisible(arg);
        placeView.setCursorVisible(arg);
        shortCommentView.setCursorVisible(arg);


    }

    public void findViews(){
        pathView = (EditText) findViewById(R.id.editPath);
        titleView = (EditText) findViewById(R.id.editTitleNoteActivity);
        authorView = (EditText) findViewById(R.id.editAuthorNoteActivity);
        ratingView = (RatingBar) findViewById(R.id.editRatingBar);
        genreView = (EditText) findViewById(R.id.editGenre);
        timeView = (EditText) findViewById(R.id.editTime);
        placeView = (EditText) findViewById(R.id.editPlace);
        shortCommentView = (EditText) findViewById(R.id.editShortComment);
        coverView = (ImageView) findViewById(R.id.editCoverImage);
    }

    public void setViews(String[] strings){
        this.pathView.setText(strings[0].substring(strings[0].indexOf('/')+1));
        this.authorView.setText(strings[1]);
        this.titleView.setText(strings[2]);
        if (!strings[3].equals("")){
            this.ratingView.setRating(Float.parseFloat(strings[3]));
        }
        this.genreView.setText(strings[4]);
        this.timeView.setText(strings[5]);
        this.placeView.setText(strings[6]);
        this.shortCommentView.setText(strings[7]);
        if (!strings[8].equals("")){
            this.coverView.setImageBitmap(BitmapFactory.decodeFile(strings[8]));
            this.imagePath = imagePath;
        }
    }

    private void setButtons(){
        FloatingActionButton accept =  (FloatingActionButton) findViewById(R.id.acceptAddingNote2);
        FloatingActionButton cancel =  (FloatingActionButton) findViewById(R.id.cancelAddingNote2);
        Button deleteButton = (Button) findViewById(R.id.deleteNoteButton);
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (saveChanges()){
                    finish();
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!imagePath.equals(beforeChanging[8])){
                    if (isNoteNew){
                        DeleteNote.deleteImages(user, id);
                    }
                    else{
                        cancelImageChange();
                    }
                }

                finish();
            }
        });

        Button bAddObl = (Button) findViewById(R.id.bAddObl);
        bAddObl.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                if (isNoteNew == false) {
                    Intent intent = new Intent(EditNoteActivity.this, GaleryActivity.class);
                    intent.putExtra("id", id);
                    startActivityForResult(intent, GALERY_REQUEST_CODE);
                }
                else
                {
                    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                    photoPickerIntent.setType("image/*");
                    startActivityForResult(photoPickerIntent, Pick_image);
                }
            }
        });



        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDeletDialog();
            }
        });
    }


    private void openDeletDialog(){
        DeleteDialogFragment dialog = new DeleteDialogFragment();
        dialog.show(getSupportFragmentManager(), "deleteDialog");
    }

    public void select(String id) {
        Log.d("qwerty45", "select");
        db.collection("Notes").document(user).collection("userNotes").document(id).get().
                addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String s = documentSnapshot.get("author").toString();
                        HashMap<String, Object> map = (HashMap<String, Object>) documentSnapshot.getData();
                        imagePath = map.get("imagePath").toString();
                        beforeChanging = new String[]{
                                map.get("path").toString().replace("\\", "/"), map.get("author").toString(),
                                map.get("title").toString(), map.get("rating").toString(),
                                map.get("genre").toString(), map.get("time").toString(),
                                map.get("place").toString(), map.get("short_comment").toString(),
                                map.get("imagePath").toString()};
                        setViews(beforeChanging);
                    }
                });
    }

    public boolean saveChanges(){
        if (authorView.getText().toString().equals("") && titleView.getText().toString().equals("")){
            showNoTitleAndAuthorDialog();
            return false;
        }
//        changedIntent();
        String path1 = pathView.getText().toString();
        path1 = fixPath(path1);
        Map<String, Object> note = new HashMap<String, Object>();
        note.put("path", path1.replace("/", "\\"));
        note.put("author", authorView.getText().toString());
        note.put("title", titleView.getText().toString());
        note.put("imagePath", imagePath);
        note.put("rating", String.valueOf(ratingView.getRating()));
        note.put("genre", genreView.getText().toString());
        note.put("time", timeView.getText().toString());
        note.put("place", placeView.getText().toString());
        note.put("short_comment", shortCommentView.getText().toString());
        if (!beforeChanging[0].equals(path1)){
            beforeChanging[0] = path1;
            savePaths();
        }
        if (isNoteNew == true){
            db.collection("Notes").document(user).collection("userNotes").document(id).set(note);
            HashMap<String, Boolean> map = new HashMap<String, Boolean>();
            insertIntent();
        }
        else
        {
            db.collection("Notes").document(user).collection("userNotes").document(id).set(note);
            changedIntent();
        }
        return true;
    }

    private void showNoTitleAndAuthorDialog(){
        if (isNoteNew){
            CreateWithoutNoteDialogFragment createDialog = new CreateWithoutNoteDialogFragment();
            createDialog.show(getSupportFragmentManager(), "createWithoutNoteDialog");
        }
        else{
            DeleteTitleAndAuthorDialogFragment dialog = new DeleteTitleAndAuthorDialogFragment();
            dialog.show(getSupportFragmentManager(), "deleteTitleAndAuthorDialog");
        }
    }

    public boolean checkChanges(){
        Log.d("putExtra", ratingView.getRating() +"");
        if (beforeChanging[0].equals(fixPath(pathView.getText().toString())) &&
                beforeChanging[1].equals(authorView.getText().toString()) &&
                beforeChanging[2].equals(titleView.getText().toString()) &&
                beforeChanging[3].equals(ratingView.getRating()+"")  &&
                beforeChanging[4].equals(genreView.getText().toString()) &&
                beforeChanging[5].equals(timeView.getText().toString()) &&
                beforeChanging[6].equals(placeView.getText().toString()) &&
                beforeChanging[7].equals(shortCommentView.getText().toString()) &&
                beforeChanging[8].equals(imagePath))
        {
            return false;
        }
        return true;
    }

    public void savePaths(){
        final String pathTokens[] = ((String) beforeChanging[0]).split("/");
        String prev="";
        for (int i = 0; i < pathTokens.length - 1; i++) {
            if (pathTokens[i].equals("")) {
                continue;
            }
            final String prev0 = prev;
            final String doc = prev + pathTokens[i] + "\\";
            final String toAdd = prev + pathTokens[i] + "\\" + pathTokens[i+1]+"\\";
            db.collection("User").document(user).collection("paths").document(doc)
                    .update("paths", FieldValue.arrayUnion(toAdd))
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Map<String, Object> map = new HashMap<>();
                            List<String> list = new ArrayList<>();
                            list.add(toAdd);
                            map.put("parent", prev0);
                            map.put("paths", list);
                            db.collection("User").document(user).collection("paths").document(doc)
                                    .set(map);

                        }
                    });
//                    .set(map, SetOptions.merge());
            prev += pathTokens[i] + "\\";
        }
        Map<String, Object> map = new HashMap<>();
//        map.put("paths", new ArrayList<String>());
        map.put("parent", prev);
        db.collection("User").document(user).collection("paths").document(
                prev+pathTokens[pathTokens.length - 1]+"\\").set(map, SetOptions.merge());

    }

    public String fixPath(String path){
        if (path.equals("") || path.equals("/")) path = "./";
        else{
            if (path.charAt(path.length() - 1) != '/'){
                path = path + "/";
            }
            if (path.charAt(0) == '/'){
                path = "." + path;
            }
            if (path.charAt(0) != '.'){
                path = "./" + path;
            }
        }
        return path;
    }

    private void deleteNote(){
        if (!isNoteNew) {
            DeleteNote.deleteNote(user, id);
        }
        else{
            DeleteNote.deleteImages(user, id);
        }
    }


    public void changedIntent(){
        Intent returnIntent = new Intent();
        returnIntent.putExtra("changed", "true");
        setResult(RESULT_OK, returnIntent);
    }

    public void insertIntent()
    {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("id", id);
        returnIntent.putExtra("path", path);
        setResult(RESULT_OK, returnIntent);
    }

    private void saveDialog(){
        SaveDialogFragment saveDialogFragment = new SaveDialogFragment();
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        saveDialogFragment.show(transaction, "dialog");
        FirebaseFirestore.getInstance().collection("Common").document(user).collection(id).document("Images").get().
                addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.getData()!=null){
                            Log.d("qwerty43", "saveD1");
                        }
                        else{
                            Log.d("qwerty43", "saveD1Null");
                        }

                    }
                });

    }


    private void saveAndOpenImage(final Uri imageUri){
        time = System.currentTimeMillis();
        imagePath = time+"";
        cover = SaveImage.saveImage(user, id, imageUri, time, getApplicationContext());




        coverView.setImageBitmap(cover);
    }

    private void cancelImageChange(){
        db.collection("Notes").document(user).collection("userNotes").document(id).
                update("imagePath", beforeChanging[8]);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Pick_image){
            Toast.makeText(getApplicationContext(), "pick_image", Toast.LENGTH_LONG).show();
            if (data != null){
                try{
                    saveAndOpenImage(data.getData());
                }
                catch (Exception e){
                    Log.e("EditNoteResult", e.toString());
                }

            }
        }
        else if (requestCode==GALERY_REQUEST_CODE){
            db.collection("Notes").document(user).collection("userNotes").document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot != null && documentSnapshot.get("imagePath")!= null){
                        imagePath = documentSnapshot.get("imagePath").toString();
                        imageStorage.child(imagePath).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Picasso.get().load(uri).into(coverView);
                            }
                        });
                    }
                }
            });
            Toast.makeText(getApplicationContext(), "galery_request", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        if (checkChanges()){
            saveDialog();
        }
        else{
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}

