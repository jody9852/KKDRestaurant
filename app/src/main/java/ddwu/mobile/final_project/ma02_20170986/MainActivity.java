package ddwu.mobile.final_project.ma02_20170986;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {
    final static String TAG = "MainActivity";

    EditText etCity;
    ListView lvList;
    String apiAddress;

    String city;

    FoodCursorAdapter adapter;
    ArrayList<FoodDto> resultList;
    FoodXmlParser parser;

    FoodDBHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etCity = findViewById(R.id.etCityName);
        lvList = findViewById(R.id.lvList);

        resultList = new ArrayList<FoodDto>();
        adapter = new FoodCursorAdapter(this, R.layout.food_info, resultList);

        lvList.setAdapter(adapter);

        lvList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long _id) {
                Intent intent = new Intent(MainActivity.this, ShowFoodActivity.class);
                intent.putExtra("id", _id);
                startActivity(intent);
            }
        });

        apiAddress = getResources().getString(R.string.food_api_url) + "?KEY=" + getString(R.string.food_api_key);
        parser = new FoodXmlParser();

        helper = new FoodDBHelper(this);

        new FoodAsyncTask().execute(apiAddress);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    public void onClick(View v) {
        Intent intent = null;
        switch(v.getId()) {
            case R.id.btnDownRestrt:
                city = etCity.getText().toString();
                if(city.equals("경기도")) {
                    new FoodAsyncTask().execute(apiAddress);
                    Toast.makeText(this, "경기도 전체의 음식점을 검색합니다", Toast.LENGTH_SHORT).show();
                } else if (!city.equals("")) {
                    if(!city.endsWith("시")) {
                        city += "시";
                    }
                    new FoodAsyncTask().execute(apiAddress + "&SIGUN_NM=" + city);
                }
                else {
                    Toast.makeText(this, "도시이름(경기도)을 입력해주세요", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.clickLocation:
                intent = new Intent(this, ShowLocationActivity.class);
                break;
            case R.id.clickList :
                intent = new Intent(this, ShowReviewListActivity.class);
                break;
            case R.id.clickFavorites :
                intent = new Intent(this, ShowFavorites.class);
                break;
            case R.id.clickInfo :
                intent = new Intent(this, DeveloperInfo.class);
                break;
        }
        if(intent != null) startActivity(intent);
    }


    class FoodAsyncTask extends AsyncTask<String, Integer, String> {
        ProgressDialog progressDlg;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDlg = ProgressDialog.show(MainActivity.this, "Wait", "Downloading...");
        }


        @Override
        protected String doInBackground(String... strings) {
            String address = strings[0];
            String result = downloadContents(address);
            if (result == null) return "Error!";
            return result;
        }


        @Override
        protected void onPostExecute(String result) {
            resultList = parser.parse(result);      // 파싱 수행

            adapter.setList(resultList);    // Adapter 에 파싱 결과를 담고 있는 ArrayList 를 설정
            adapter.notifyDataSetChanged();

            progressDlg.dismiss();

            createFoodDatabase();

            if(resultList.size() == 0) {
                Toast.makeText(MainActivity.this, city + "의 검색 결과는 없습니다", Toast.LENGTH_SHORT).show();
            }
        }

        private boolean isOnline() {
            ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            return (networkInfo != null && networkInfo.isConnected());
        }

        private InputStream getNetworkConnection(HttpURLConnection conn) throws Exception {

            // 클라이언트 아이디 및 시크릿 그리고 요청 URL 선언
            conn.setReadTimeout(3000);
            conn.setConnectTimeout(3000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);

            if (conn.getResponseCode() != HttpsURLConnection.HTTP_OK) {
                throw new IOException("HTTP error code: " + conn.getResponseCode());
            }

            return conn.getInputStream();
        }


        /* InputStream을 전달받아 문자열로 변환 후 반환 */
        protected String readStreamToString(InputStream stream){
            StringBuilder result = new StringBuilder();

            try {
                InputStreamReader inputStreamReader = new InputStreamReader(stream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                String readLine = bufferedReader.readLine();

                while (readLine != null) {
                    result.append(readLine + "\n");
                    readLine = bufferedReader.readLine();
                }

                bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return result.toString();
        }


        /* 주소(address)에 접속하여 문자열 데이터를 수신한 후 반환 */
        protected String downloadContents(String address) {
            HttpURLConnection conn = null;
            InputStream stream = null;
            String result = null;

            try {
                URL url = new URL(address);
                conn = (HttpURLConnection)url.openConnection();
                stream = getNetworkConnection(conn);
                result = readStreamToString(stream);
                if (stream != null) stream.close();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (conn != null) conn.disconnect();
            }

            return result;
        }

    }

    private void createFoodDatabase() {
        SQLiteDatabase db = helper.getWritableDatabase();
        for(int i = 0; i < resultList.size(); i++) {
            ContentValues row = new ContentValues();

            FoodDto dto = resultList.get(i);
            dto.set_id(i + 1);
            row.put(FoodDBHelper.CITY_NAME, String.valueOf(dto.getCityNm()));
            row.put(FoodDBHelper.RESTRT_NAME, String.valueOf(dto.getRestrtNm()));
            row.put(FoodDBHelper.TEL_NUM, String.valueOf(dto.getTel()));
            row.put(FoodDBHelper.ADDRESS, String.valueOf(dto.getRoadNmAddr()));
            row.put(FoodDBHelper.LATITUDE, String.valueOf(dto.getLatitude()));
            row.put(FoodDBHelper.LONGITUDE, String.valueOf(dto.getLongitude()));
            row.put(FoodDBHelper.FAVORITE, String.valueOf(dto.getFavorite()));

            db.insert(FoodDBHelper.TABLE_NAME, null, row);
        }
        helper.close();
    }
}
