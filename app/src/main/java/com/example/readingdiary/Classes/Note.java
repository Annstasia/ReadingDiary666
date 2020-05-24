package com.example.readingdiary.Classes;

// общий интерфей для RealNote и Directory. Нужен, чтобы оба класса могли находиться в одном recyclerView
public interface Note{
    int getItemType();
    String getID();





}


//
//public class Directory implements Note{
//    private long id;
//    private String directory;
//    private final int type = 1;
//    public Directory(long id, String directory){
//        this.id = id;
//        this.directory = directory;
//    }
//
//    public String getDirectory() {
//        return directory;
//    }
//
//    @Override
//    public int getItemType() {
//        return type;
//    }
//
//    @Override
//    public long getID() {
//        return id;
//    }
//}

//public class Note {
//    private String path;
//    private String author;
//    private String title;
//
//
//
//    public Note(String path, String author, String title){
//        this.path = path;
//        this.title = title;
//        this.author = author;
//    }
//
//    public String getPath() {
//        return path;
//    }
//
//    public String getAuthor() {
//        return author;
//    }
//
//    public String getTitle() {
//        return title;
//    }
//
//    public void setPath(String path) {
//        this.path = path;
//    }
//
//    public void setAuthor(String author) {
//        this.author = author;
//    }
//
//    public void setTitle(String title) {
//        this.title = title;
//    }
//}
//
//
//

//
//class RealNote extends Note{
//
//}
