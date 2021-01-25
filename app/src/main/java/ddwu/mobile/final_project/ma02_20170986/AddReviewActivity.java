package ddwu.mobile.final_project.ma02_20170986;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddReviewActivity extends AppCompatActivity {
    private final static String TAG = "AddReviewActivity";

    private static final int REQUEST_TAKE_PHOTO = 200;

    private String mCurrentPhotoPath;
    private File photofile;

    ImageView ivPhoto;
    TextView tvName;
    EditText etReview;
    RatingBar rtBar;

    ReviewDBHelper helper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_review);

        helper = new ReviewDBHelper(this);

        ivPhoto = (ImageView)findViewById(R.id.ivPhoto);
        tvName = (TextView) findViewById(R.id.tvName);
        etReview = (EditText)findViewById(R.id.etReview);
        rtBar = (RatingBar)findViewById(R.id.ratingBar);

        Intent intent = getIntent();
        tvName.setText(intent.getStringExtra("name"));

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
                            Uri photoURI = FileProvider.getUriForFile(AddReviewActivity.this, "ddwu.mobile.final_project.ma02_20170986.fileprovider", photofile);
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
                if(etReview.getText().toString().equals("")) {
                    Toast.makeText(this, "후기를 입력해주세요", Toast.LENGTH_SHORT).show();
                } else if(rtBar.getRating() == 0.0) {
                    Toast.makeText(this, "별점을 입력해주세요", Toast.LENGTH_SHORT).show();
                } else {
                    ReviewDBHelper helepr = new ReviewDBHelper(this);
                    SQLiteDatabase db = helepr.getWritableDatabase();

                    ContentValues row = new ContentValues();
                    row.put(ReviewDBHelper.PATH, mCurrentPhotoPath);
                    row.put(ReviewDBHelper.NAME, tvName.getText().toString());
                    row.put(ReviewDBHelper.REVIEW, etReview.getText().toString());
                    row.put(ReviewDBHelper.RATING, rtBar.getRating());
                    db.insert(ReviewDBHelper.TABLE_NAME, null, row);
                    helepr.close();
                    Toast.makeText(this, "후기가 저장 됬습니다", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            case R.id.btnClose:
                //photofile.delete();
                finish();
                break;
        }
    }
}
