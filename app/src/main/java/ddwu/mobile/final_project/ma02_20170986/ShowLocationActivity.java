package ddwu.mobile.final_project.ma02_20170986;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;

public class ShowLocationActivity extends AppCompatActivity {
    final static String TAG = "ShowLocationActivity";
    final static int PERMISSION_REQ_CODE = 100;

    TextView tvLocation;

    private GoogleMap mGoogleMap;
    private Marker marker;
    private MarkerOptions markerOptions;

    private AddressResultReceiver addressResultReceiver;
    private double latitude;
    private double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_location);

        if (checkPermission()) {
            mapLoad();
        };

        tvLocation = (TextView)findViewById(R.id.tvLocation);

        markerOptions = new MarkerOptions();
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker));

        addressResultReceiver = new AddressResultReceiver(new Handler());

        Toast.makeText(this, "현재 주소를 알고싶으면 버튼을 클릭하세요", Toast.LENGTH_SHORT).show();
    }

    OnMapReadyCallback mapReadyCallBack = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            mGoogleMap = googleMap;

            LatLng latlng = new LatLng(latitude, longitude);
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 17));

            mGoogleMap.setMyLocationEnabled(true);
            mGoogleMap.setOnMyLocationButtonClickListener(locationButtonClickListener);
            mGoogleMap.setOnMyLocationClickListener(locationClickListener);
        }
    };

    GoogleMap.OnMyLocationButtonClickListener locationButtonClickListener =
            new GoogleMap.OnMyLocationButtonClickListener() {
                @Override
                public boolean onMyLocationButtonClick() {
                    Toast.makeText(ShowLocationActivity.this, "현재 위치로 찾아가는 중", Toast.LENGTH_SHORT).show();
                    return false;
                }
            };

    GoogleMap.OnMyLocationClickListener locationClickListener =
            new GoogleMap.OnMyLocationClickListener() {
                @Override
                public void onMyLocationClick(@NonNull Location location) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    startAddressService();
                }
            };

    private void startAddressService() {
        Intent intent = new Intent(ShowLocationActivity.this, FetchAddressIntentService.class);
        intent.putExtra(Constants.RECEIVER, addressResultReceiver);
        intent.putExtra(Constants.LAT_DATA_EXTRA, latitude);
        intent.putExtra(Constants.LNG_DATA_EXTRA, longitude);
        startService(intent);
    }


    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            String addressOutput = null;

            if (resultCode == Constants.SUCCESS_RESULT) {
                if (resultData == null) return;
                addressOutput = resultData.getString(Constants.RESULT_DATA_KEY);
                if (addressOutput == null) addressOutput = "";
                tvLocation.setText(addressOutput);
            } else {
                tvLocation.setText("주소를 찾을 수 없습니다");
            }
            Log.i(TAG, "received address : " + addressOutput);
        }
    }

    /*구글맵을 멤버변수로 로딩*/
    private void mapLoad() {
        SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.locationMap);
        mapFragment.getMapAsync(mapReadyCallBack);
    }

    /* 필요 permission 요청 */
    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION },
                        PERMISSION_REQ_CODE);
                return false;
            }
        }
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == PERMISSION_REQ_CODE) {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                // 퍼미션을 획득하였을 경우 맵 로딩 실행
                mapLoad();
            } else {
                // 퍼미션 미획득 시 액티비티 종료
                Toast.makeText(this, "앱 실행을 위해 권한 허용이 필요함", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}
