package com.example.pdfviewer.Model;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "bookTable")
public class Book {
    @PrimaryKey
    @NonNull
    private String pdfUri;

    @ColumnInfo(name="fileName")
    private String fileName;

    @ColumnInfo(name="title")
    private String title;

    @ColumnInfo(name="pageNum")
    private String pageNum;

    @ColumnInfo(name="preview")
    private String preview;

    public String getPdfUri() { return this.pdfUri; }
    public void setPdfUri(String pdfUri){ this.pdfUri = pdfUri; }

    public String getFileName() {return this.fileName;}
    public void setFileName(String fileName){this.fileName = fileName;}

    public String getTitle() { return this.title; }
    public void setTitle(String title){ this.title = title; }

    public String getPageNum() { return this.pageNum; }
    public void setPageNum(String pageNum) { this.pageNum = pageNum; }

    public String getPreview() { return this.preview; }
    public void setPreview(String preview) { this.preview = preview; }
}
