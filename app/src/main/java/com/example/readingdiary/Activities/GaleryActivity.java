package com.example.readingdiary.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.readingdiary.Classes.ImageClass;
import com.example.readingdiary.R;
import com.example.readingdiary.adapters.GaleryFullViewAdapter;
import com.example.readingdiary.adapters.GaleryRecyclerViewAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.remote.FirestoreCallCredentials;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

public class GaleryActivity extends AppCompatActivity {
    private ImageView imageView;
    private final int Pick_image = 1;
    private RecyclerView galeryView;;
    private GaleryRecyclerViewAdapter adapter;
    private GaleryFullViewAdapter adapter1;
    private List<ImageClass> images;
    private List<Bitmap> newImages;
    private List<Long> names;
    private final int FULL_GALERY_CODE = 8800;
    String user;
    private StorageReference imageStorage;
    private DocumentReference imagePathsDoc;

    private int count = 3;
    String id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.galery_activity);
        user = FirebaseAuth.getInstance().getUid();
        Bundle args = getIntent().getExtras();
        id = args.get("id").toString();
        imagePathsDoc = FirebaseFirestore.getInstance().collection("Common").document(user).collection(id).document("Images");
        imageStorage = FirebaseStorage.getInstance().getReference(user).child(id).child("Images");
        images = new ArrayList<>(); // список bitmap изображений
        names = new ArrayList<Long>(); // список путей к изображениями в файловой системе
        newImages = new ArrayList<>();

        galeryView = (RecyclerView) findViewById(R.id.galery_recycle_view);
//        adapter = new GaleryRecyclerViewAdapter(images, getApplicationContext());

        GridLayoutManager layoutManager = new GridLayoutManager(this, 3); // отображение изображений в 3 колонки
        final LinearLayoutManager layoutManager1 = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();

        galeryView.setLayoutManager(layoutManager);

        galeryView.setItemAnimator(itemAnimator);
        adapter = new GaleryRecyclerViewAdapter(images, getApplicationContext());
        galeryView.setAdapter(adapter);

        imagePathsDoc.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
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
                                    names.add(l);
                                    images.add(new ImageClass(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.no_image)));
                                    adapter.notifyItemInserted(images.size()-1);
                                }
                                else{
                                    imageStorage.child(key).getDownloadUrl().
                                            addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    Toast.makeText(getApplicationContext(), "uri ", Toast.LENGTH_LONG).show();
                                                    names.add(l);
                                                    images.add(new ImageClass(uri));
                                                    adapter.notifyItemInserted(images.size()-1);
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
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
//        imagePathsDoc.get()
//                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                    @Override
//                    public void onSuccess(DocumentSnapshot documentSnapshot) {
//                        names = (ArrayList) documentSnapshot.get("Paths");
//                        if (names != null){
//                            for (long i : names){
//                                imageStorage.child(i+"").getDownloadUrl()
//                                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
//                                            @Override
//                                            public void onSuccess(Uri uri) {
//                                                images.add(new ImageClass(uri));
////                                                adapter = new GaleryRecyclerViewAdapter(images, getApplicationContext());
////                                                galeryView.setAdapter(adapter);
//                                                adapter.notifyItemInserted(images.size()-1);
//                                            }
//                                        })
//                                        .addOnFailureListener(new OnFailureListener() {
//                                            @Override
//                                            public void onFailure(@NonNull Exception e) {
//                                                Log.e("qwerty26", e.toString());
//                                            }
//                                        });
////                                adapter.notifyDataSetChanged();
//                            }
//                        }
//                        else{
//                            Log.d("qwerty25", "null!!!");
//                        }
//
////                        adapter = new GaleryRecyclerViewAdapter(images, getApplicationContext());
////                        galeryView.setAdapter(adapter);
//                        // Ускорить загрузку
//                        // Сделать норм добавление
//                        // Посмотреть как сломала пути
//                        // Добавить удаление
//                    }
////                    adapter = new GaleryRecyclerViewAdapter(images, getApplicationContext());
////                    galeryView.setAdapter(adapter);
//
//
//                });

//        FireStorageTFirebaseStorage.getInstance().getReference(user).child("Images");
//        File fileDir1 = getApplicationContext().getDir(getResources().getString(R.string.imagesDir) + File.pathSeparator + id, MODE_PRIVATE); // путь к папке с изображениями
//        File[] files = fileDir1.listFiles(); // список файлов в папке
//        if (files != null){
//            for (int i = 0; i < files.length; i++){
//                images.add(BitmapFactory.decodeFile(files[i].getAbsolutePath()));
//                names.add(files[i].getAbsolutePath());
//            }
//        }




        // при нажатии на изображение переходим в активность с полным изображением
        adapter.setOnItemClickListener(new GaleryRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(GaleryActivity.this, GaleryFullViewActivity.class);
                intent.putExtra("id", id);
                intent.putExtra("position", position);
                startActivityForResult(intent, FULL_GALERY_CODE);

            }
        });




        Button pickImage = (Button) findViewById(R.id.button);

        // Выбор изображений из галереи
        pickImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, Pick_image);
            }
        });
        setResultChanged();
    }



    private void setResultChanged(){
        // создание возвращаемого интента
        Intent returnIntent = new Intent();
//        returnIntent.putExtra("changed", "changed);
        setResult(RESULT_OK, returnIntent);
    }


    // методы проверки размера изображения до открытия. Если размер слишком большой - сжимаем
    public Bitmap decodeSampledBitmapFromResource(Uri imageUri,
                                                  int reqWidth, int reqHeight) throws Exception{

        // Читаем с inJustDecodeBounds=true для определения размеров
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        InputStream imageStream = getContentResolver().openInputStream(imageUri);
        BitmapFactory.decodeStream(imageStream, null, options);

        // Вычисляем inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth,
                reqHeight);

        // Читаем с использованием inSampleSize коэффициента
        options.inJustDecodeBounds = false;
        imageStream.close();
        imageStream = getContentResolver().openInputStream(imageUri);
        return BitmapFactory.decodeStream(imageStream, null, options);
    }

    public int calculateInSampleSize(BitmapFactory.Options options,
                                     int reqWidth, int reqHeight) {
        // Реальные размеры изображения
        final int height = options.outHeight;
        final int width = options.outWidth;
        Log.d("SCALE", "OPTIONS " + height + " "  + width);
        int inSampleSize = (int) Math.max((double)height / reqHeight, (double)width/reqWidth);
        return inSampleSize;
    }

    private void saveAndOpenImage(final Uri imageUri) throws Exception{
        int px = 500;
        final Bitmap bitmap = decodeSampledBitmapFromResource(imageUri, px, px); // файл сжимается
//        FirebaseStorage.getInstance().getReference(user).child("Images").put
        final long time = System.currentTimeMillis();
        final ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 60, stream);// сохранение
        ImageClass t = new ImageClass(bitmap);
        images.add(t);
        adapter.notifyItemInserted(images.size()-1);
        names.add(time);
        Map<String, Boolean> map = new HashMap<>();
        map.put(time+"", false);
        imagePathsDoc.set(map, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        imageStorage.child(time + "").putBytes(stream.toByteArray())
                                .addOnSuccessListener(
                                        new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                              @Override
                                              public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                  Toast.makeText(getApplicationContext(), "success", Toast.LENGTH_LONG).show();
                                                  imagePathsDoc.update(time+"", true);
                                              }
                                          })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        imagePathsDoc.update(time+"", FieldValue.delete());
                                    }
                                });
                    }});
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Pick_image)
        {
            if (resultCode == RESULT_OK) {
                try {
                    saveAndOpenImage(data.getData());
                    // Обработка выбранного изображения из галереи
                } catch (Exception e) {
                    Log.d("IMAGE1", e.toString());
                }
            }
        }

        else if (requestCode == FULL_GALERY_CODE) {
            Log.d("DELETEIMAGE1", "request");

            // Вернулись из показа полных изображений. Если там удалили изображение, то меняем список имен и изображений
            if (resultCode == RESULT_OK) {
//                List<Integer> index = new ArrayList<>();
//                for (int i = 0; i < names.size(); i++) {
//                    if (!(new File(names.get(i)).exists())) {
//                        index.add(i);
//                    }
//                }
//
//                int minus = 0;
//                for (int i : index) {
//                    names.remove(i - minus);
//                    images.remove(i - minus);
//                    adapter.notifyItemRemoved(i - minus);
//                    minus++;
//                }
//                adapter.notifyDataSetChanged();
            }
        }


    }
}
