package com.example.pdfviewer.View;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.fragment.app.ListFragment;

import com.example.pdfviewer.R;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class PageFragment extends Fragment implements View.OnClickListener {

    EditText edTxtTitle, edTxtMemo;
    TextView tvPageNum, txtMainTitle;
    ImageView image;
    Button addImage;
    ImageButton editTitle;

    String mainTitle = null;
    String pageNum = null;
    Uri mImageUri = null;
    String absoultePath;
    Bitmap bitmap = null;

    final int PICK_FROM_CAMERA = 2;
    final int PICK_FROM_ALBUM = 3;
    final int CROP_FROM_IMAGE = 4;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.view_pager, container, false);

        edTxtMemo = (EditText) view.findViewById(R.id.edTxt_memo);
        edTxtTitle = (EditText) view.findViewById(R.id.edTxt_title);
        image = (ImageView) view.findViewById(R.id.image_view);
        addImage = (Button) view.findViewById(R.id.btn_addImage);
        tvPageNum = (TextView) view.findViewById(R.id.page_tv_pageNum);
        txtMainTitle = (TextView) view.findViewById(R.id.page_text_main_title);
        editTitle = (ImageButton) view.findViewById(R.id.page_btn_revise_title);

        tvPageNum.setText(pageNum + " 쪽");
        txtMainTitle.setText(mainTitle);

        addImage.setOnClickListener(this);

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_addImage:
                doTakeAlbumAction();
                break;
            case R.id.page_btn_revise_title:

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (bitmap != null){
            image.setImageBitmap(bitmap);
        }

    }

    public void doTakeAlbumAction(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode != -1){
            return;
        }

        switch (requestCode){
            case PICK_FROM_ALBUM:
            {
                mImageUri = data.getData();
            }
            case PICK_FROM_CAMERA:
            {
                Intent intent = new Intent("com.android.camera.action.CROP");
                intent.setDataAndType(mImageUri, "image/*");

                intent.putExtra("outputX", 200);
                intent.putExtra("outputY", 200);
                intent.putExtra("aspectX", 1);
                intent.putExtra("aspectY", 1);
                intent.putExtra("scale", true);
                intent.putExtra("return-data", true);
                startActivityForResult(intent, CROP_FROM_IMAGE);
                break;
            }
            case CROP_FROM_IMAGE:
                if(resultCode != -1){
                    return;
                }

                final Bundle extras = data.getExtras();

                String filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/crop/";

                File path = new File(filePath);
                File cropImage = new File(path, System.currentTimeMillis() + ".jpg");
                if (!path.exists()){
                    path.mkdirs();
                }

                if (extras != null){
                    Bitmap photo = extras.getParcelable("data");
                    bitmap = photo;
                    // 크롭한 이미지를 이미지뷰에 셑.
                    image.setImageBitmap(photo);
                    // 크롭한 이미지 저장.
                    storeCropImage(photo, cropImage.toString());
                    absoultePath = cropImage.toString();
                    break;
                }

                File f = new File(mImageUri.getPath());
                if (f.exists()){
                    f.delete();
                }
        }
    }

    private void storeCropImage(Bitmap bitmap, String filePath){
        String dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/crop/";
        File directory_SmartWheel = new File(dir);

        if(!directory_SmartWheel.exists()){
            directory_SmartWheel.mkdirs();
        }

        File copyFile = new File(filePath);
        BufferedOutputStream out = null;

        try {
            copyFile.createNewFile();

            out = new BufferedOutputStream(new FileOutputStream(copyFile));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

            getContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(copyFile)));

            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getImagePath(){
        return absoultePath;
    }

    public void setPageNum(int num) {
        this.pageNum = String.valueOf(num);
    }

    public void setMainTitle(String name){ this.mainTitle = name; }

}
