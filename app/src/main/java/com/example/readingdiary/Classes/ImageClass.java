package com.example.readingdiary.Classes;

import android.graphics.Bitmap;
import android.net.Uri;

import com.example.readingdiary.R;
import com.google.common.io.Resources;

public class ImageClass {
    int type;
    Uri uri;
    Bitmap bitmap;
    public ImageClass(Bitmap bitmap){
        this.bitmap = bitmap;
        type = 0;
    }
    public ImageClass(Uri uri){
        this.uri = uri;
        type = 1;
    }



    public int getType() {
        return type;
    }

    public Uri getUri() {
        if (type==1) return uri;
        return null;
    }

    public Bitmap getBitmap() {
        if (type==0) return bitmap;
        return null;
    }
}
