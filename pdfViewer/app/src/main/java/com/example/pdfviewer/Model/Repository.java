package com.example.pdfviewer.Model;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;


public class Repository extends Thread{

    private BookDao bookDao;
    private MyDatabase db;

    public Repository(Application application){
        db = MyDatabase.getDB(application);
        bookDao = db.bookDao();
    }

    public void insertData(Book book) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                bookDao.insertBook(book);
            }
        }).start();
    }

    public void deleteData(Book book){
        new Thread(new Runnable() {
            @Override
            public void run() {
                bookDao.deleteBook(book);
            }
        }).start();
    }

    public boolean isExistUri(String uri){
        final boolean[] isExist = new boolean[1];

        new Thread(new Runnable() {
            @Override
            public void run() {
                isExist[0] = bookDao.isExist(uri);
            }
        }).start();
        try { this.join(); }
        catch (InterruptedException e) { e.printStackTrace(); }

        return isExist[0];
    }

    public boolean isExistPageNum(String uri){
        final boolean[] isExist = new boolean[1];

        new Thread(new Runnable() {
            @Override
            public void run() { isExist[0] = bookDao.isExist(uri); }
        }).start();

        try { this.join(); }
        catch (InterruptedException e) { e.printStackTrace(); }

        return isExist[0];
    }
}
