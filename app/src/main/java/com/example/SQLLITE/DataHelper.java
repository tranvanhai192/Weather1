package com.example.SQLLITE;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DataHelper extends SQLiteOpenHelper {
    private static final String TAG = "DataHelper";
    static final String DB_NAME = "Product.db";
    static final String DB_NAME_TABLE = "Product";
    static final int DB_VERSION = 1;
    SQLiteDatabase sqLiteDatabase;
    ContentValues contentValues;
    Cursor cursor;
    public DataHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION); }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String queryCreaTable = "CREATE TABLE Product ( " +
                "id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                "city Text)";
        //Chạy câu lệnh tạo bảng product
        db.execSQL(queryCreaTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            db.execSQL("drop table if exists " + DB_NAME_TABLE);
            onCreate(db);
        }
    }

    public void addData(String city) {
        sqLiteDatabase = getWritableDatabase();
        contentValues = new ContentValues();
        contentValues.put("city",city);
        sqLiteDatabase.insert(DB_NAME_TABLE,null,contentValues);
        closeDB();

    }
    public void  ALLdata(){
        sqLiteDatabase = getReadableDatabase();
        cursor = sqLiteDatabase.query(false, DB_NAME_TABLE, null, null, null
                , null, null, null, null);

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex("id"));
            String city= cursor.getString(cursor.getColumnIndex("city"));
            Log.d(TAG, "ALLdata: " + "id - " + id + " - city - " + city );
        }
        closeDB();

    }
    private void closeDB() {
        if (sqLiteDatabase != null) sqLiteDatabase.close();
        if (contentValues != null) contentValues.clear();
        if (cursor != null) cursor.close();
    }

}
