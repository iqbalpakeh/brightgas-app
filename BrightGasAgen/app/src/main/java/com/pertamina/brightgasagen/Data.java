package com.pertamina.brightgasagen;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class Data extends SQLiteOpenHelper {

    private static final String TAG = "debug_data";

    public static final String CREATE_TABLE = "create table ";
    public static final String TEXT = " text";
    public static final String QOMAS_SEPARATOR = ", ";
    public static final String START_OF_LINE = "(";
    public static final String END_OF_LINE = ");";
    public static final String QUESTION_MARK = " ? ";
    public static final String LIKE = " like ";
    public static final String INTEGER_NOT_NULL = " integer not null";
    public static final String INTEGER = " integer ";

    public static final String TABLE_REQUEST = "table_request";
    public static final String TABLE_LOGIN_TYPE = "table_login_type";
    public static final String TABLE_SEARCH_HISTORY = "table_search_history";
    public static final String TABLE_USER = "table_user";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_TOKEN = "_token";
    public static final String COLUMN_AGEN = "_agen";
    public static final String COLUMN_LATITUDE = "latitude";
    public static final String COLUMN_LONGITUDE = "longitude";

    private static final String DATABASE_NAME = "brigtgasagen2.db";

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_USER = CREATE_TABLE
            + TABLE_USER + START_OF_LINE
            + COLUMN_ID + TEXT + QOMAS_SEPARATOR
            + COLUMN_NAME + TEXT + QOMAS_SEPARATOR
            + COLUMN_TOKEN + TEXT + QOMAS_SEPARATOR
            + COLUMN_AGEN + TEXT + QOMAS_SEPARATOR
            + COLUMN_LATITUDE + TEXT + QOMAS_SEPARATOR
            + COLUMN_LONGITUDE + TEXT +
            END_OF_LINE;

    private static final String DATABASE_LOGIN_TYPE = CREATE_TABLE
            + TABLE_LOGIN_TYPE + START_OF_LINE
            + COLUMN_ID + INTEGER_NOT_NULL + QOMAS_SEPARATOR
            + COLUMN_NAME + TEXT +
            END_OF_LINE;

    private static final String DATABASE_USER_REQUEST = CREATE_TABLE
            + TABLE_REQUEST + START_OF_LINE
            + COLUMN_ID + TEXT + QOMAS_SEPARATOR
            + COLUMN_NAME + TEXT +
            END_OF_LINE;

    private static final String DATABASE_SEARCH_HISTORY = CREATE_TABLE
            + TABLE_SEARCH_HISTORY + START_OF_LINE
            + COLUMN_NAME + TEXT +
            END_OF_LINE;

    private static final String[] allColumnsRequest = {
            COLUMN_ID,
            COLUMN_NAME
    };

    private static final String[] allColumnsLoginType = {
            COLUMN_ID,
            COLUMN_NAME
    };

    private static final String[] allColumnsSearchHistory = {
            COLUMN_NAME
    };

    private static final String[] allColumnsUser = {
            COLUMN_ID,
            COLUMN_NAME,
            COLUMN_TOKEN,
            COLUMN_AGEN,
            COLUMN_LATITUDE,
            COLUMN_LONGITUDE
    };

    public static Data self;

    public Data(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        self = this;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "create data");
        db.execSQL(DATABASE_USER_REQUEST);
        db.execSQL(DATABASE_LOGIN_TYPE);
        db.execSQL(DATABASE_SEARCH_HISTORY);
        db.execSQL(DATABASE_USER);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insertUser() {
        clearTable(TABLE_USER);
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_ID, User.id);
        contentValues.put(COLUMN_NAME, User.name);
        contentValues.put(COLUMN_TOKEN, User.token);
        contentValues.put(COLUMN_AGEN, User.agen);
        contentValues.put(COLUMN_LATITUDE, User.latitude);
        contentValues.put(COLUMN_LONGITUDE, User.longitude);
        getWritableDatabase().insert(TABLE_USER, null, contentValues);
    }

    public void insertRequest(String id, String request) {
        if (getRequest(id) != null) {
            updateRequest(id, request);
        } else {
            ContentValues contentValues = new ContentValues();
            contentValues.put(COLUMN_ID, id);
            contentValues.put(COLUMN_NAME, request);
            getWritableDatabase().insert(TABLE_REQUEST, null, contentValues);
        }
    }

    public void clearTable(String tableName) {
        getWritableDatabase().delete(tableName, null, null);
    }

    public void updateRequest(String id, String request) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, id);
        values.put(COLUMN_NAME, request);
        getWritableDatabase().update(TABLE_REQUEST, values, COLUMN_ID + LIKE + QUESTION_MARK, new String[]{id});
    }

    public String getRequest(String id) {
        String result = null;
        try {
            Cursor cursor = getWritableDatabase().query(TABLE_REQUEST,
                    allColumnsRequest, COLUMN_ID + LIKE + QUESTION_MARK, new String[]{id}, null, null, null);
            if (cursor.moveToFirst()) {
                result = cursor.getString(1);
            }
            cursor.close();
        } catch (Exception ex) {
            Log.d(TAG, "ERROR GET REQUEST : " + ex.toString());
        }
        return result;
    }

    public void updateLoginType(int loginType) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, loginType);
        getWritableDatabase().update(TABLE_LOGIN_TYPE, values, null, null);
    }

    public void updateLoginInfo(int loginType, String info) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, loginType);
        values.put(COLUMN_NAME, info);
        getWritableDatabase().update(TABLE_LOGIN_TYPE, values, null, null);
    }

    public String getLoginInfo() {
        Cursor cursor = getWritableDatabase().query(TABLE_LOGIN_TYPE,
                allColumnsLoginType, null, null, null, null, null);
        cursor.moveToFirst();
        String result = cursor.getString(1);
        cursor.close();
        return result;
    }

    public void getUser() {
        try {
            Cursor cursor = getWritableDatabase().query(TABLE_USER,
                    allColumnsUser, null, null, null, null, null);
            if (cursor.moveToFirst()) {

                User.id = cursor.getString(0);
                User.name = cursor.getString(1);
                User.token = cursor.getString(2);
                User.agen = cursor.getString(3);
            }
            cursor.close();
        } catch (Exception ex) {
            Log.d(TAG, ex.toString());
        }
    }

    public String[] getAllSearchHistory() {
        String[] datas = null;
        try {
            Cursor cursor = getWritableDatabase().query(TABLE_SEARCH_HISTORY,
                    allColumnsSearchHistory, null, null, null, null, null);
            if (cursor.moveToFirst()) {
                datas = new String[cursor.getCount()];
                int counter = 0;
                while (!cursor.isAfterLast()) {
                    datas[counter] = cursor.getString(0);
                    counter++;
                    cursor.moveToNext();
                }
            }
            cursor.close();
        } catch (Exception ex) {
            Log.d(TAG, ex.toString());
        }
        if (datas == null) {
            datas = new String[0];
        }
        return datas;
    }

    public void clearSearchHistory() {
        getWritableDatabase().delete(TABLE_SEARCH_HISTORY, null, null);
    }

    public void insertSearchHistory(String history) {
        if (!isSearchHistoryExist(history)) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(COLUMN_NAME, history);
            getWritableDatabase().insert(TABLE_SEARCH_HISTORY, null, contentValues);
        }
    }

    public boolean isSearchHistoryExist(String query) {
        Cursor cursor = getWritableDatabase().query(TABLE_SEARCH_HISTORY,
                allColumnsSearchHistory, COLUMN_NAME + LIKE + QUESTION_MARK, new String[]{query}, null, null, null);
        if (cursor.moveToFirst()) {
            cursor.close();
            return true;
        }
        return false;
    }

}
