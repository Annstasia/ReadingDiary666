package com.example.readingdiary.Classes;

import android.util.Log;

import java.io.File;
import java.util.ArrayList;

public class DeleteFilesClass extends Thread {
//    ArrayList<File> files;
    File[] files;
//    public DeleteFilesClass(ArrayList<File> files){
//        this.files = files;
//    }
//
    public DeleteFilesClass(File[] files){
        this.files = files;
    }

    @Override
    public void run() {
        for (File fileDir1 : files){
            try{
                if (!fileDir1.exists()) continue;

                File files1[] = fileDir1.listFiles();
                if (files1 != null){
                    Log.d("deleteFiles", fileDir1.getAbsolutePath() + " " + files1.length);
                    for (File file : files1){
                        file.delete();
                    }
                    Log.d("deleteFiles", fileDir1.getAbsolutePath() + " " + fileDir1.listFiles().length);

                }

                fileDir1.delete();
            }
            catch (Exception e){
                Log.e("deleteFilesError", e + "");
            }
        }
    }


}
