package ddwu.mobile.final_project.ma02_20170986;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class ReviewDBHelper extends SQLiteOpenHelper {
    private final static String TAG = "ReviewDBHelper";

    private final static String DB_NAME ="review_db";
    public final static String TABLE_NAME = "review_table";
    public final static String ID = "_id";
    public final static String PATH = "path";
    public final static String NAME = "name";
    public final static String REVIEW = "review";
    public final static String RATING = "rating";

    public ReviewDBHelper(@Nullable Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String sql = "create table " + TABLE_NAME + " (" + ID + " integer primary key autoincrement, "
                + PATH + " text, " + NAME + " text, " + REVIEW + " text, " + RATING + " float);";
        Log.d(TAG, sql);
        sqLiteDatabase.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("drop table " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
