package com.example.readingdiary.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.readingdiary.Fragments.SaveDialogFragment;
import com.example.readingdiary.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Document;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

//import android.os.FileUtils;

public class VariousNotebook extends AppCompatActivity implements SaveDialogFragment.SaveDialogListener {
    private boolean shouldSave = true;
    private String id;
    private String type;
    private TextInputEditText text;
    private String path;
    private String position;
    String user;
    private DocumentReference variousNotePaths;
    private CollectionReference variousNoteStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coments);
        user = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Bundle args = getIntent().getExtras();
        id = args.get("id").toString();
        type = args.get("type").toString();
        variousNoteStorage = FirebaseFirestore.getInstance().collection("VariousNotes").document(user).collection(id);
        variousNotePaths = variousNoteStorage.document(type);
//        variousNoteStorage = FirebaseFirestore.getInstance().collection("VariousNotes").document(user).collection(type);

        if (type.equals("description")){
            TextView textView12 = (TextView) findViewById(R.id.textView12);
            //textView12.setText("Описание");
        }
        text = (TextInputEditText) findViewById(R.id.editTextComments);
        if (args.get("path") != null){
            path = args.get("path").toString();
            try{
                openText();
                position= args.get("position").toString();
            }
            catch (Exception e){
                Log.e("openTextException", e.toString());
            }
        }
    }

    @Override
    public void onBackPressed() {
        dialogSaveOpen();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onSaveClicked() {
        returnResult(saveText());
        super.onBackPressed();
    }

    private void openText() throws Exception{
        variousNoteStorage.document(path).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                text.setText(documentSnapshot.get("text").toString());
            }
        });
//        File fileDir1 = getApplicationContext().getDir(type, MODE_PRIVATE);
//        if (!fileDir1.exists()) fileDir1.mkdirs();
//        File file = new File(fileDir1, id+".txt");

//        File file = new File(path);
//        if (!file.exists()) file.createNewFile();
//        BufferedReader br = new BufferedReader(new FileReader(file));
//        StringBuilder str = new StringBuilder();
//        String line;
//        while ((line = br.readLine()) != null){
//            str.append(line);
//            str.append('\n');
//        }
//        text.setText(str.toString());
//        br.close();
    }

    private long saveText(){

        try{
            final long time = (path==null)?System.currentTimeMillis():Long.parseLong(path);
//            final long time = System.currentTimeMillis();
            Map<String, Boolean> map = new HashMap<>();
            map.put(time+"", false);
            variousNotePaths.set(map, SetOptions.merge())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Map<String, String> map1 = new HashMap<String, String>();
                            map1.put("text", text.getText().toString());
                            variousNoteStorage.document(time+"").set(map1)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(getApplicationContext(), "success", Toast.LENGTH_LONG).show();
                                            variousNotePaths.update(time+"", true);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.e("qwerty40", e.toString());
                                            variousNotePaths.update(time+"", FieldValue.delete());
                                        }
                                    });
                        }});
            return (path==null)?time:-2;

//            File file;
//            if (path==null){
//                File fileDir1 = getApplicationContext().getDir(type + File.pathSeparator + id, MODE_PRIVATE);
//                if (!fileDir1.exists()) fileDir1.mkdirs();
//                long time = new GregorianCalendar().getTimeInMillis();
//                file = new File(fileDir1, time+".txt");
//                if (!file.exists()) file.createNewFile();
//                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
//                bw.write(text.getText().toString());
//                bw.close();
//                return time;
//            }
//            else{
//                file = new File(path);
//                if (!file.exists()) file.createNewFile();
//                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
//                bw.write(text.getText().toString());
//                bw.close();
//                return -2;
//            }
////            if (!file.exists()) file.createNewFile();
////            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
////            bw.write(text.getText().toString());
////            bw.close();
////            return time;


        }
        catch (Exception e){
            Log.e("openException", e.toString());
        }
        return -1;
    }

    private void returnResult(long time){
        if (time == -1) return;
        Intent resultIntent = new Intent();
        if (time == -2) {
            resultIntent.putExtra("updatePath", path);
            resultIntent.putExtra("position", position);
        }
        else{
            resultIntent.putExtra("time", time+"");
        }
        setResult(RESULT_OK, resultIntent);

    }

    private void dialogSaveOpen(){
        SaveDialogFragment dialog = new SaveDialogFragment();
        dialog.show(getSupportFragmentManager(), "saveNoteDialog");
    }

    @Override
    public void onNotSaveClicked() {
        finish();
    }
}
