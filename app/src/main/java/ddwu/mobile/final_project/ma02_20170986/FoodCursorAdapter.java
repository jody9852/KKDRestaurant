package ddwu.mobile.final_project.ma02_20170986;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class FoodCursorAdapter extends BaseAdapter {
    public static final String TAG = "MyCursorAdapter";

    private LayoutInflater inflater;
    private Context context;
    private int layout;
    private ArrayList<FoodDto> list;

    public FoodCursorAdapter(Context context, int resource, ArrayList<FoodDto> list) {
        super();
        this.context = context;
        this.layout = resource;
        this.list = list;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public FoodDto getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return list.get(position).get_id();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d(TAG, "getView with position : " + position);
        View view = convertView;
        ViewHolder viewHolder = null;

        if (view == null) {
            view = inflater.inflate(layout, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.tvName = view.findViewById(R.id.tvName);
            viewHolder.tvAddr = view.findViewById(R.id.tvAddr);
            viewHolder.tvTel = view.findViewById(R.id.tvTel);

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)view.getTag();
        }

        FoodDto dto = list.get(position);

        viewHolder.tvName.setText(dto.getRestrtNm());
        viewHolder.tvAddr.setText(dto.getRoadNmAddr());
        viewHolder.tvTel.setText(dto.getTel());

        return view;
    }

    public void setList(ArrayList<FoodDto> list) {
        this.list = list;
    }


    //    ※ findViewById() 호출 감소를 위해 필수로 사용할 것
    static class ViewHolder {
        public TextView tvName = null;
        public TextView tvAddr = null;
        public TextView tvTel = null;
    }

        /* 네트워크 환경 조사 */
        private boolean isOnline() {
            ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            return (networkInfo != null && networkInfo.isConnected());
        }

        /* 주소를 전달받아 bitmap 다운로드 후 반환 */
        private Bitmap downloadImage(String address) {
            HttpURLConnection conn = null;
            InputStream stream = null;
            Bitmap result = null;

            try {
                URL url = new URL(address);
                conn = (HttpURLConnection)url.openConnection();
                stream = getNetworkConnection(conn);
                result = readStreamToBitmap(stream);
                if (stream != null) stream.close();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (conn != null) conn.disconnect();
            }

            return result;
        }


        /* URLConnection 을 전달받아 연결정보 설정 후 연결, 연결 후 수신한 InputStream 반환 */
        private InputStream getNetworkConnection(HttpURLConnection conn) throws Exception {
            conn.setReadTimeout(3000);
            conn.setConnectTimeout(3000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);

            if (conn.getResponseCode() != HttpsURLConnection.HTTP_OK) {
                throw new IOException("HTTP error code: " + conn.getResponseCode());
            }

            return conn.getInputStream();
        }


        /* InputStream을 전달받아 비트맵으로 변환 후 반환 */
        private Bitmap readStreamToBitmap(InputStream stream) {
            return BitmapFactory.decodeStream(stream);
        }

    }

