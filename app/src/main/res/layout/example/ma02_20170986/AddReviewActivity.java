package com.example.ma02_20170986;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddReviewActivity extends AppCompatActivity {

    private static final int REQUEST_TAKE_PHOTO = 200;

    private String mCurrentPhotoPath;
    private File photofile;

    ImageView ivPhoto;
    EditText etMemo;

    MemoDBHelper helper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_review);

        helper = new MemoDBHelper(this);

        ivPhoto = (ImageView)findViewById(R.id.ivPhoto);
        etMemo = (EditText)findViewById(R.id.etMemo);

        ivPhoto.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if(takePictureIntent.resolveActivity(getPackageManager()) != null) {
                        photofile = null;
                        try {
                            photofile = createImageFile();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }

                        if(photofile != null) {
                            Uri photoURI = FileProvider.getUriForFile(AddReviewActivity.this, "ddwucom.mobile.final_project.ma02_20170986.fileprovider", photofile);
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                            startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                        }
                    }
                    return true;
                }
                return false;
            }
        });

    }
    /*사진의 크기를 ImageView에서 표시할 수 있는 크기로 변경*/
    private void setPic() {
        // Get the dimensions of the View
        int targetW = ivPhoto.getWidth();
        int targetH = ivPhoto.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
//        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        ivPhoto.setImageBitmap(bitmap);
    }
    /*현재 시간 정보를 사용하여 파일 정보 생성*/
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            setPic();
        }
    }
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.btnSave:
//                DB에 촬영한 사진의 파일 경로 및 메모 저장
                MemoDBHelper helepr = new MemoDBHelper(this);
                SQLiteDatabase db = helepr.getWritableDatabase();

                ContentValues row = new ContentValues();
                row.put(MemoDBHelper.PATH, mCurrentPhotoPath);
                row.put(MemoDBHelper.MEMO, etMemo.getText().toString());
                db.insert(MemoDBHelper.TABLE_NAME, null, row);
                helepr.close();

                Toast.makeText(this, "Save!", Toast.LENGTH_SHORT).show();
            case R.id.btnCancel:
                photofile.delete();
                finish();
                break;
        }
    }




}
