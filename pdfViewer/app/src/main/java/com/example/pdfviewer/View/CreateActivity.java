package com.example.pdfviewer.View;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import com.example.pdfviewer.Adapter.PagerAdapter;
import com.example.pdfviewer.BuildConfig;
import com.example.pdfviewer.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class CreateActivity extends FragmentActivity {

    private Button btnBack, btnSave, addMemo, delMemo;
    private ViewPager viewPager;
    private PagerAdapter pagerAdapter;
    private FragmentManager fManager;
    private Uri createdPdfUri = null;
    String mainTitle = null, writer = null;

    int pageWidth = 720;
    int pageHeight = 1280;
    Intent intent;

    private static final int PERMISSION_REQUEST_CODE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);

        btnBack = (Button) findViewById(R.id.btn_back);
        btnSave = (Button) findViewById(R.id.btn_save);
        addMemo = (Button) findViewById(R.id.btn_addPage);
        delMemo = (Button) findViewById(R.id.btn_delPage);
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        fManager = getSupportFragmentManager();

        intent = getIntent();

        viewPager.setOffscreenPageLimit(3);
        pagerAdapter = new PagerAdapter(this, fManager);
        viewPager.setAdapter(pagerAdapter);

        PageFragment page = new PageFragment();
        pagerAdapter.addItem(page);
        pagerAdapter.notifyDataSetChanged();

        getIntentData(intent);
        checkPermission();

        addMemo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PageFragment page = new PageFragment();
                pagerAdapter.addItem(page);
                //String tag = String.valueOf(pagerAdapter.getCount());
                //fManager.beginTransaction().add(page, tag);
                pagerAdapter.notifyDataSetChanged();

            }
        });

        delMemo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int current = viewPager.getCurrentItem();

                if (current == 0){
                    viewPager.setCurrentItem(1);
                }
                else {
                    viewPager.setCurrentItem(current - 1);
                }

                pagerAdapter.removeItem(viewPager, current);
                pagerAdapter.notifyDataSetChanged();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){

                if (generatePDF()){
                    String uri = createdPdfUri.toString();

                    intent.putExtra("createdPdfUri", uri);
                    intent.putExtra("pageNum", pagerAdapter.getCount());

                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }
            }
        });
    }

    private void getIntentData(Intent intent){
        if (intent.getStringExtra("main_title") != null){
            mainTitle = intent.getStringExtra("main_title");
            pagerAdapter.setMainTitle(mainTitle);
        } else if(intent.getStringExtra("writer") != null){
            writer = intent.getStringExtra("writer");
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

    private Boolean generatePDF() {

        int pageNum = pagerAdapter.getCount();

        PdfDocument pdfDocument = new PdfDocument();

        Paint paint = new Paint();
        Paint str = new Paint();

        if (pageNum != 0){
            for (int i = 0; i< pageNum; i++){
                // 각 페이지마다 pdf 저장
                PageFragment mFragment = pagerAdapter.getItem(i);

                String titleStr = null;
                String memoStr = null;
                String imagePath = null;
                Bitmap bmp, scaledbmp = null;

                try {
                    titleStr = mFragment.edTxtTitle.getText().toString();
                    memoStr = mFragment.edTxtMemo.getText().toString();
                    imagePath = mFragment.getImagePath();
                } catch (NullPointerException e){
                    return false;
                }

                try {
                    if (imagePath != null){
                        bmp = BitmapFactory.decodeFile(imagePath);
                        Log.v("ht", "decodeFile imagePath is :" + imagePath);
                        scaledbmp = Bitmap.createScaledBitmap(bmp, 620, 480, false);
                        if (bmp != scaledbmp){
                            bmp.recycle();
                        }
                    }

                    PdfDocument.PageInfo mypageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, i).create();
                    PdfDocument.Page myPage = pdfDocument.startPage(mypageInfo);

                    Canvas canvas = myPage.getCanvas();

                    if (mainTitle != null){
                        paint.setTypeface(Typeface.create(Typeface.DEFAULT_BOLD, Typeface.NORMAL));
                        paint.setTextSize(44);
                        paint.setColor(ContextCompat.getColor(this, R.color.text_color));
                        canvas.drawText(mainTitle, 30, 100, paint);
                    }
                    if (titleStr != null){
                        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
                        paint.setTextSize(36);
                        paint.setColor(ContextCompat.getColor(this, R.color.text_color));
                        canvas.drawText(titleStr, 50, 200, paint);
                    }
                    if (memoStr != null){
                        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
                        paint.setTextSize(28);
                        paint.setColor(ContextCompat.getColor(this, R.color.text_color));
                        canvas.drawText(memoStr, 50, 800, paint);
                    }
                    if (writer != null){
                        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
                        paint.setTextSize(12);
                        paint.setColor(ContextCompat.getColor(this, R.color.text_color));
                        canvas.drawText(writer, 35, 1100, paint);
                    }
                    if (imagePath != null){
                        canvas.drawBitmap(scaledbmp, 50, 250, paint);
                    }

                    pdfDocument.finishPage(myPage);
                } catch (Exception e){
                    return false;
                }
            }
        }

        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        File file = new File(path, mainTitle + ".pdf");

        if (!path.exists()){ path.mkdirs(); }

        if (file.exists()) {
            Toast.makeText(CreateActivity.this, "오류 : 같은 이름의 파일이 있습니다 주 제목을 변경해 주세요.", Toast.LENGTH_SHORT).show();
            return false;
        }

        try {
            pdfDocument.writeTo(new FileOutputStream(file));
            Toast.makeText(CreateActivity.this, "Save: " + path, Toast.LENGTH_SHORT).show();

            createdPdfUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID, file);
            Log.v("ht", "created pdf file as " + createdPdfUri.toString());

            pdfDocument.close();
        } catch (Exception e) {
            Toast.makeText(CreateActivity.this, "오류 : ???", Toast.LENGTH_SHORT).show();

            return false;
        }
        return true;
    }
}

