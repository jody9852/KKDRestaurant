package ddwu.mobile.final_project.ma02_20170986;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ShowReviewActivity extends AppCompatActivity {
    final static String TAG = "ShowReviewActivity";

    ReviewDBHelper helper;
    ImageView ivPhoto;
    TextView tvName;
    TextView tvReview;
    RatingBar rtBar;

    private long id;
    private String path;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_review);

        Intent intent = getIntent();
        id = intent.getLongExtra("id", 0);

        ivPhoto = (ImageView)findViewById(R.id.ivPhoto);
        tvName = (TextView)findViewById(R.id.tvName);
        tvReview = (TextView)findViewById(R.id.etReview);
        rtBar = (RatingBar)findViewById(R.id.rbReview);

        helper = new ReviewDBHelper(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor cursor = db.rawQuery( "select * from " + ReviewDBHelper.TABLE_NAME + " where " + ReviewDBHelper.ID + "=?", new String[] { String.valueOf(id) });
        while (cursor.moveToNext()) {
            path = cursor.getString(cursor.getColumnIndex(ReviewDBHelper.PATH));
            tvName.setText( cursor.getString( cursor.getColumnIndex(ReviewDBHelper.NAME) ) );
            tvReview.setText( cursor.getString( cursor.getColumnIndex(ReviewDBHelper.REVIEW) ) );
            rtBar.setRating(Float.valueOf(cursor.getString(cursor.getColumnIndex(ReviewDBHelper.RATING))));
        }
        cursor.close();
        helper.close();

        if(getSavedBitmapFromExternal() == null) {
            ivPhoto.setImageResource(R.drawable.food);
        } else {
            ivPhoto.setImageBitmap(getSavedBitmapFromExternal());
        }
    }

    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.btnClose:
                finish();
                break;
        }
    }

    public Bitmap getSavedBitmapFromExternal() {
        if (!isExternalStorageWritable()) return null;
        Log.i(TAG, "file path : " + path);
        Bitmap bitmap = BitmapFactory.decodeFile(path);

        try {
            ExifInterface exif = new ExifInterface(path);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);
            bitmap = rotateBitmap(bitmap, ExifInterface.ORIENTATION_ROTATE_90);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public Bitmap rotateBitmap(Bitmap bitmap, int orientation) {
        Matrix matrix = new Matrix();
        matrix.setRotate(90);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }
}
