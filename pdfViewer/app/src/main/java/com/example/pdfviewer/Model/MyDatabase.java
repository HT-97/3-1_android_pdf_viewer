package com.example.pdfviewer.Model;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Book.class}, version = 1)
public abstract class MyDatabase extends RoomDatabase {

    public abstract BookDao bookDao();

    // Room Database는 싱글톤으로
    private static MyDatabase INSTANCE;

    public static MyDatabase getDB(Context context) {

        synchronized (MyDatabase.class) {
            if (INSTANCE == null) {
                // 데이터베이스 생성 부분
                INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                        MyDatabase.class, "my_database").build();
            }
        }
        return INSTANCE;
    }

    public static void destroyDBInstance() {
        INSTANCE = null;
    }
}