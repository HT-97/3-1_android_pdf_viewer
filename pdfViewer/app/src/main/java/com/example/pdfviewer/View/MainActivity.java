package com.example.pdfviewer.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.pdfviewer.Adapter.RecyclerViewAdapter;
import com.example.pdfviewer.ItemTouchHelperCallback;
import com.example.pdfviewer.Model.Book;

import com.example.pdfviewer.Model.Repository;
import com.example.pdfviewer.Model.MyDatabase;
import com.example.pdfviewer.R;
import com.github.barteksc.pdfviewer.PDFView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    RecyclerView recyclerView;
    ProgressBar progressBar;
    Animation fab_open, fab_close;
    Boolean isFabOpen = false;
    FloatingActionButton fab, fab1, fab2;
    Repository repository;
    ArrayList<Book> items;
    MyDatabase myDB;
    ItemTouchHelper helper;
    PDFView pdfView;

    private static final int PERMISSION_REQUEST_CODE = 200;
    private static final int READ_REQUEST_CODE = 42;
    private static final int PDF_VIEWER_REQUEST_CODE = 10;
    private static final int CREATE_PDF_CODE = 20;
    private static final int OPEN_PDF_CODE = 31;

    private RecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);

        fab = (FloatingActionButton) findViewById(R.id.main_fab);
        fab1 = (FloatingActionButton) findViewById(R.id.load_pdf_fab);
        fab2 = (FloatingActionButton) findViewById(R.id.create_pdf_fab);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        repository = new Repository(getApplication());
        myDB = MyDatabase.getDB(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        fab.setOnClickListener(this);
        fab1.setOnClickListener(this);
        fab2.setOnClickListener(this);

        checkPermission();

        getData();
    }

    public void getPDF() {

        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        // 열 수 있는 파일만
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        // application, text, image, *는 모두를 의미
        intent.setType("application/pdf");

        try {
            startActivityForResult(intent, READ_REQUEST_CODE);
        } catch (ActivityNotFoundException e) {
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK){
            return;
        }

        String uri = null;
        int pageNum = 0;

        if (requestCode == READ_REQUEST_CODE){
            uri = data.getData().toString();

        } else if (requestCode == CREATE_PDF_CODE){
            uri = data.getStringExtra("createdPdfUri");

        }

        File f = new File(uri);

        Log.v("ht", "uri isExist : " + String.valueOf(repository.isExistUri(uri)));

        Book book = new Book();
        String fileName = Uri.parse(uri).getLastPathSegment();

        if (fileName.contains("primary")){
            fileName = fileName.substring(fileName.lastIndexOf("/")+1);
            fileName = fileName.split("\\.")[0];
        }

        book.setPdfUri(uri);
        book.setFileName(f.getName());

        book.setTitle(fileName);
        book.setPageNum("0");

        adapter.addItem(book);

        openPdf(uri);
        repository.insertData(book);
    }

    public void openPdf(String uri){

        if (uri != null){

            Intent intent = new Intent(MainActivity.this, pdfViewerActivity.class);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION |
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION |
                    Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);

            intent.putExtra("pdfUri", uri);

            startActivityForResult(intent, OPEN_PDF_CODE);
        }
    }

    private void checkPermission() {
        int permission1 = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int permission2 = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);

        if (permission1 == PackageManager.PERMISSION_GRANTED &&
                permission2 == PackageManager.PERMISSION_GRANTED){
            Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
        }
        else {
            requestPermission();
        }
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0) {

                // after requesting permissions we are showing
                // users a toast message of permission granted.
                boolean writeStorage = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean readStorage = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                if (writeStorage && readStorage) {
                    Toast.makeText(this, "Permission Granted..", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Permission Denined.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch (id) {

            case R.id.main_fab:
                anim();
                break;

            case R.id.load_pdf_fab:
                anim();
                getPDF();
                break;

            case R.id.create_pdf_fab:
                anim();

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                View dialogView = getLayoutInflater().inflate(R.layout.create_pdf_dialog, null);

                EditText title = (EditText) dialogView.findViewById(R.id.create_pdf_dialog_edit_title);
                EditText writer = (EditText) dialogView.findViewById(R.id.create_pdf_dialog_edit_writer);

                builder.setTitle("PDF 생성")
                        .setView(dialogView)
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                Intent intent = new Intent(MainActivity.this, CreateActivity.class);

                                intent.putExtra("main_title", title.getText().toString());
                                intent.putExtra("writer", writer.getText().toString());

                                startActivityForResult(intent, CREATE_PDF_CODE);
                            }
                        })
                        .setNegativeButton("취소", null)
                        .show();
                break;
        }
    }

    public void anim() {

        if (isFabOpen) {
            fab1.startAnimation(fab_close);
            fab2.startAnimation(fab_close);
            fab1.setClickable(false);
            fab2.setClickable(false);
            isFabOpen = false;
        } else {
            fab1.startAnimation(fab_open);
            fab2.startAnimation(fab_open);
            fab1.setClickable(true);
            fab2.setClickable(true);
            isFabOpen = true;
        }
    }

    public void getData() {
        class GetData extends AsyncTask<Void,Void,ArrayList<Book>> {

            @Override
            protected ArrayList<Book> doInBackground(Void... voids) {
                List<Book> bookList = myDB.bookDao().getAll();
                ArrayList<Book> books = new ArrayList<Book>(bookList);

                return books;
            }

            @Override
            protected void onPostExecute(ArrayList<Book> books) {
                adapter = new RecyclerViewAdapter(books);
                recyclerView.setAdapter(adapter);

                helper = new ItemTouchHelper(new ItemTouchHelperCallback(adapter));
                helper.attachToRecyclerView(recyclerView);

                adapter.setClickListener(new RecyclerViewAdapter.OnItemClickListener() {
                    @Override
                    public void onClick(Book book) {
                        openPdf(book.getPdfUri());
                    }
                });

                super.onPostExecute(books);
            }
        }
        GetData getData = new GetData();
        getData.execute();
    }
}