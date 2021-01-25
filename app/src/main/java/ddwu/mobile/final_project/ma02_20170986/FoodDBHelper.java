package ddwu.mobile.final_project.ma02_20170986;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class FoodDBHelper extends SQLiteOpenHelper {
    private final static String TAG = "FoodDBHelper";

    private final static String DB_NAME = "food_db";
    public final static String TABLE_NAME = "food_table";
    public final static String ID = "_id";
    public final static String CITY_NAME = "city_name";
    public final static String RESTRT_NAME = "restrt_name";
    public final static String TEL_NUM = "tel_num";
    public final static String ADDRESS = "address";
    public final static String LATITUDE = "latitude";
    public final static String LONGITUDE = "longtidue";
    public final static String FAVORITE = "favorite";

    public FoodDBHelper(Context context) { super(context, DB_NAME, null, 1); }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String sql = "create table " + TABLE_NAME + " (" + ID + " integer primary key autoincrement, "
                + CITY_NAME + " text, " + RESTRT_NAME + " text, " + TEL_NUM + " text, " + ADDRESS + " text, " +
                LATITUDE + " double, " + LONGITUDE + " double, " + FAVORITE + " boolean);";
        Log.d(TAG, sql);
        sqLiteDatabase.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("drop table " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
