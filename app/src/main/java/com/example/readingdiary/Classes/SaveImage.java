package com.example.readingdiary.Classes;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class SaveImage {
    public static Bitmap decodeSampledBitmapFromResource(Uri imageUri,
                                                  int reqWidth, int reqHeight, Context context) throws Exception{

        // Читаем с inJustDecodeBounds=true для определения размеров
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        InputStream imageStream = context.getContentResolver().openInputStream(imageUri);
        BitmapFactory.decodeStream(imageStream, null, options);

        // Вычисляем inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth,
                reqHeight);

        // Читаем с использованием inSampleSize коэффициента
        options.inJustDecodeBounds = false;
        imageStream.close();
        imageStream = context.getContentResolver().openInputStream(imageUri);
        return BitmapFactory.decodeStream(imageStream, null, options);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options,
                                     int reqWidth, int reqHeight) {
        // Реальные размеры изображения
        final int height = options.outHeight;
        final int width = options.outWidth;
        Log.d("SCALE", "OPTIONS " + height + " "  + width);
        int inSampleSize = (int) Math.max((double)height / reqHeight, (double)width/reqWidth);
        return inSampleSize;
    }

    public static Bitmap saveImage(String user, String id, final Uri imageUri, long time1, Context context) {
        try{
            Log.d("qwerty42", id);
            final StorageReference imageStorage = FirebaseStorage.getInstance().getReference(user).child(id).child("Images");
            final DocumentReference imagePathsDoc = FirebaseFirestore.getInstance().collection("Common").document(user).collection(id).document("Images");
            int px = 500;
            Bitmap cover = decodeSampledBitmapFromResource(imageUri, px, px, context); // файл сжимается
            final long time = time1;
            final ByteArrayOutputStream stream = new ByteArrayOutputStream();
            cover.compress(Bitmap.CompressFormat.PNG, 60, stream);// сохранение
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
//                                                    Toast.makeText(getApplicationContext(), "success", Toast.LENGTH_LONG).show();
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
            return cover;
        }
        catch (Exception e){
            Log.e("saveImageException", e.toString());
        }
        return null;

    }
}
