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

public class ShowReviewListActivity extends AppCompatActivity {
    final static String TAG = "ShowReviewListAcitvity";
    SimpleCursorAdapter reviewAdapter;
    Cursor cursor;
    ReviewDBHelper helper;
    ListView lvReview;
    EditText etReviewSearchName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_review_list);

        helper = new ReviewDBHelper(this);
        etReviewSearchName = (EditText)findViewById(R.id.etSearchReview);

        reviewAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, null,
                new String[] {"name"}, new int[] {android.R.id.text1}, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

        lvReview = (ListView)findViewById(R.id.lv_review);
        lvReview.setAdapter(reviewAdapter);

        lvReview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long _id) {
                Intent intent = new Intent(ShowReviewListActivity.this,  ShowReviewActivity.class);
                intent.putExtra("id", _id);
                startActivity(intent);
            }
        });

        lvReview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                final long targetId = id;	// id 값을 다이얼로그 객체 내부에서 사용하기 위하여 상수로 선언
                TextView tvName = view.findViewById(android.R.id.text1);	// 리스트 뷰의 클릭한 위치에 있는 뷰 확인

                String dialogMessage = "'" + tvName.getText().toString() + "' 리뷰 삭제?";	// 클릭한 위치의 뷰에서 문자열 값 확인

                new AlertDialog.Builder(ShowReviewListActivity.this).setTitle(R.string.title_dialog)
                        .setMessage(dialogMessage)
                        .setPositiveButton(R.string.ok_dialog, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SQLiteDatabase db = helper.getWritableDatabase();

                                String whereClause = ReviewDBHelper.ID + "=?";
                                String[] whereArgs = new String[] { String.valueOf(targetId) };

                                db.delete(ReviewDBHelper.TABLE_NAME, whereClause, whereArgs);
                                helper.close();
                                readAllReviews();		// 삭제 상태를 반영하기 위하여 전체 목록을 다시 읽음
                            }
                        })
                        .setNegativeButton(R.string.cancel_dialog, null)
                        .show();


                return true;
            }
        });
    }

    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.btnSearchReview:
                String name = etReviewSearchName.getText().toString();
                if(name.equals("")) {
                    Toast.makeText(this, "음식점이 입력되지 않아 전체목록을 출력합니다", Toast.LENGTH_SHORT).show();
                }
                SQLiteDatabase db = helper.getReadableDatabase();
                Cursor cursor = db.rawQuery("select * from " + ReviewDBHelper.TABLE_NAME + " where "
                        + ReviewDBHelper.NAME + " like '%" + name +"%'", null);
                reviewAdapter.changeCursor(cursor);
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
        readAllReviews();
    }

    private void readAllReviews() {
//        DB에서 데이터를 읽어와 Adapter에 설정
        SQLiteDatabase db = helper.getReadableDatabase();
        cursor = db.rawQuery("select * from " + ReviewDBHelper.TABLE_NAME, null);

        reviewAdapter.changeCursor(cursor);
        helper.close();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cursor != null) cursor.close();
    }
}
