package ddwu.mobile.final_project.ma02_20170986;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ShowFavorites extends AppCompatActivity {
    final static String TAG = "ShowFavorites";
    SimpleCursorAdapter favoriteAdapter;
    Cursor cursor;
    FoodDBHelper helper;
    ListView lvFavorites;
    EditText etFavoritesSearchName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_favorites);

        helper = new FoodDBHelper(this);
        etFavoritesSearchName = (EditText)findViewById(R.id.etSearchFavorites);

        favoriteAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, null,
                new String[] {FoodDBHelper.RESTRT_NAME}, new int[] {android.R.id.text1}, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

        lvFavorites = (ListView)findViewById(R.id.lv_favorites);
        lvFavorites.setAdapter(favoriteAdapter);

        lvFavorites.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long _id) {
                Intent intent = new Intent(ShowFavorites.this,  ShowFoodActivity.class);
                intent.putExtra("id", _id);
                startActivity(intent);
            }
        });
    }
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.btnSearchFavorite:
                String name = etFavoritesSearchName.getText().toString();
                if(name.equals("")) {
                    Toast.makeText(this, "음식점이 입력되지 않아 전체목록을 출력합니다", Toast.LENGTH_SHORT).show();
                }
                SQLiteDatabase db = helper.getReadableDatabase();
                Cursor cursor = db.rawQuery("select * from " + FoodDBHelper.TABLE_NAME + " where "
                        + FoodDBHelper.RESTRT_NAME + " like '%" + name + "%' and "
                        + FoodDBHelper.FAVORITE + " ='true'", null);
                favoriteAdapter.changeCursor(cursor);
                helper.close();
                if(cursor.getCount() == 0) {
                    Toast.makeText(this, name + "의 검색 결과가 없습니다", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        SQLiteDatabase db = helper.getReadableDatabase();
        cursor = db.rawQuery("select * from " + FoodDBHelper.TABLE_NAME + " where " + FoodDBHelper.FAVORITE + " ='true'", null);

        favoriteAdapter.changeCursor(cursor);
        helper.close();
    }
}
