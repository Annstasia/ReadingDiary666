package com.example.readingdiary.Classes;

import android.graphics.Bitmap;
import android.util.Log;

import com.squareup.picasso.Transformation;

public class SmallGaleryTransform implements Transformation {
    int x, y;
    public SmallGaleryTransform(int x, int y){
        this.x = x;
        this.y = y;

    }
    @Override public Bitmap transform(Bitmap source) {
//        DisplayMetrics metricsB = context.getResources().getDisplayMetrics();
//        display.getMetrics(metricsB);
        float size = x / 3;
        float size1 = Math.min(source.getWidth(), source.getHeight());
        float k = size / size1;
        Log.d("SCALE", k+" " + size + " " + size1);
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(source, (int)(source.getWidth() * k), (int)(source.getHeight() * k), false);
        int x1 = (int)((resizedBitmap.getWidth() - size) / 2);
        int y1 = (int)((resizedBitmap.getHeight() - size) / 2);
        Bitmap result = Bitmap.createBitmap(resizedBitmap, x1, y1, (int)size, (int)size);
        source.recycle();
        return result;
    }

    @Override public String key() { return "square()"; }
}