package com.example.pdfviewer.Model;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface BookDao {

    @Insert
    public void insertBook(Book book);

    @Delete
    public void deleteBook(Book book);

    @Query("select pdfUri from bookTable where pdfUri like :uri ")
    public boolean isExist(String uri);

    @Query("select pageNum from bookTable where pdfUri like :uri and pageNum like null")
    public boolean isPageNum(String uri);

    @Transaction
    @Query("SELECT * FROM bookTable")
    public List<Book> getAll();
}
