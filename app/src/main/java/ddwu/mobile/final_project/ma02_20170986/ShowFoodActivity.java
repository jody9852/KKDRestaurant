package ddwu.mobile.final_project.ma02_20170986;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class ShowFoodActivity extends AppCompatActivity {
    final static String TAG = "ShowFoodActivity";

    TextView tvName;
    TextView tvAddress;
    ImageView ivAddFavorite;

    private GoogleMap mGoogleMap;
    private Marker marker;
    private MarkerOptions markerOptions;

    FoodDBHelper fHelper;
    ReviewDBHelper rHelper;

    private long id;
    private String name;
    private String telNum;
    private double latitude;
    private double longitude;
    private String favorite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_food);

        Intent intent = getIntent();
        id = intent.getLongExtra("id", 0);

        SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.infoMap);
        mapFragment.getMapAsync(mapReadyCallBack);

        tvName = (TextView)findViewById(R.id.tvName);
        tvAddress = (TextView)findViewById(R.id.tvAddress);
        ivAddFavorite = (ImageView)findViewById(R.id.ivAddFavorite);

        fHelper = new FoodDBHelper(this);
        rHelper = new ReviewDBHelper(this);

        markerOptions = new MarkerOptions();
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker));
    }



    @Override
    protected void onResume() {
        super.onResume();

        SQLiteDatabase db = fHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery( "select * from " + FoodDBHelper.TABLE_NAME + " where " + FoodDBHelper.ID + "=?", new String[] { String.valueOf(id) });
        Log.i(TAG, "id " + String.valueOf(id));
        while (cursor.moveToNext()) {
            tvName.setText(cursor.getString(cursor.getColumnIndex(FoodDBHelper.RESTRT_NAME)));
            tvAddress.setText(cursor.getString(cursor.getColumnIndex(FoodDBHelper.ADDRESS)));
            name = cursor.getString(cursor.getColumnIndex(FoodDBHelper.RESTRT_NAME));
            telNum = cursor.getString(cursor.getColumnIndex(FoodDBHelper.TEL_NUM));
            latitude = cursor.getDouble(cursor.getColumnIndex(FoodDBHelper.LATITUDE));
            longitude = cursor.getDouble(cursor.getColumnIndex(FoodDBHelper.LONGITUDE));
            favorite = cursor.getString(cursor.getColumnIndex(FoodDBHelper.FAVORITE));
            if(Boolean.valueOf(favorite) == false) {
                ivAddFavorite.setImageResource(R.drawable.add_favorite);
            } else {
                ivAddFavorite.setImageResource(R.drawable.favorite);
            }
        }
        cursor.close();
        fHelper.close();
    }

    OnMapReadyCallback mapReadyCallBack = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            mGoogleMap = googleMap;

            LatLng latlng;
            if(latitude > 0 && longitude > 0) {
                latlng = new LatLng(latitude, longitude);
            } else {
                latlng = new LatLng(0, 0);
            }
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 17));

            markerOptions.position(latlng);
            marker = mGoogleMap.addMarker(markerOptions);
            marker.showInfoWindow();
        }
    };

    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.addReview :
                SQLiteDatabase db = rHelper.getReadableDatabase();

                Cursor rCursor = db.rawQuery( "select * from " + ReviewDBHelper.TABLE_NAME +
                        " where " + ReviewDBHelper.NAME + "=?", new String[] { name });
                if(rCursor.getCount() >= 1) {
                    Toast.makeText(this, "이미 리뷰가 있는 음식점 입니다", Toast.LENGTH_SHORT).show();
                } else {
                    Intent addIntent = new Intent(this, AddReviewActivity.class);
                    addIntent.putExtra("name", name);
                    startActivity(addIntent);
                }

                rCursor.close();
                rHelper.close();
                break;
            case R.id.ivAddFavorite :
                SQLiteDatabase dbR = fHelper.getReadableDatabase();
                Cursor cursor = dbR.rawQuery( "select * from " + FoodDBHelper.TABLE_NAME + " where " + FoodDBHelper.ID + "=?", new String[] { String.valueOf(id) });
                while (cursor.moveToNext()) {
                    favorite = cursor.getString(cursor.getColumnIndex(FoodDBHelper.FAVORITE));
                }
                cursor.close();

                SQLiteDatabase dbW = fHelper.getWritableDatabase();
                if(Boolean.valueOf(favorite) == false) {
                    ivAddFavorite.setImageResource(R.drawable.favorite);
                    Toast.makeText(this, "즐겨찾기에 추가 되었습니다", Toast.LENGTH_SHORT).show();
                    dbW.execSQL("UPDATE " + fHelper.TABLE_NAME + " SET favorite = " + "'true' WHERE _id = " + id);
                    Log.i(TAG, "turning true");
                } else {
                    ivAddFavorite.setImageResource(R.drawable.add_favorite);
                    Toast.makeText(this, "즐겨찾기에서 제외 되었습니다", Toast.LENGTH_SHORT).show();
                    dbW.execSQL("UPDATE " + fHelper.TABLE_NAME + " SET favorite = " + "'false' WHERE _id = " + id);
                    Log.i(TAG, "turning false");
                }
                fHelper.close();
                break;
            case R.id.ivCall :
                if(telNum.length() <= 5) {
                    Toast.makeText(this, "전화 번호가 제공 되지 않는 식당입니다", Toast.LENGTH_SHORT).show();
                } else {
                    if(telNum.length() <= 8) telNum = "031" + telNum;
                    String tel = "tel:" + telNum;
                    Log.i(TAG, tel);
                    startActivity(new Intent("android.intent.action.DIAL", Uri.parse(tel)));
                }
                break;
            case R.id.ivShare :
                String subject = name;
                String text = "https://www.google.com/maps/search/" + name;
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_SUBJECT, subject);
                intent.putExtra(Intent.EXTRA_TEXT, text);

                Intent chooser = Intent.createChooser(intent, "Share");
                startActivity(chooser);
                break;
        }
    }
}
