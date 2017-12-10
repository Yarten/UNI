package com.yarten.unimanager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteTransactionListener;

/**
 * Created by ivan on 2017/11/22.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    private SQLiteDatabase sqLiteDatabase;

    public DatabaseHelper(Context context) {
        super(context, "data_db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        this.sqLiteDatabase = sqLiteDatabase;
        createTable();
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // 更改数据库版本的操作
        String sql = "DROP TABLE IF EXISTS data";
        sqLiteDatabase.execSQL(sql);
        onCreate(sqLiteDatabase);
    }

    public void createTable()
    {
        // 创建数据库后，对数据库的操作(即创建表)
        // data(id，姓名，头像，性别，籍贯，出生年，死亡年，故事，是否已喜爱，是否已删除，主效势力)
        String sql = "create table if not exists data ";
        sql += "(id integer primary key autoincrement, name text, img text, sex integer, ";
        sql += "region text, born text, dead text, story text, isFavourite integer, isDeleted integer, master text);";
        sqLiteDatabase.execSQL(sql);
    }
}
