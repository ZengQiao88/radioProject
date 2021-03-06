package com.hust.radiofeeler.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by jianghao on 16/1/25.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME="sendFileDatabase.db";//数据库名称
    private static final String TABLE_NAME="localFile";//表名称
    private static final String TABLE_NAME_IQ="iqFile";//表名称
    private static final String TABLE_NAME_POA="poaFile";//表名称
    private static final String TABLE_NAME_TDOA="tdoaFile";//表名称
    private static final int  version=6;
    private static DatabaseHelper mInstance;

    /**
     * 文件名，扫频范围，经纬度，经纬度是否已显式标志，文件上传标志，文件重传次数
     */
    public static final String sql = "create table ["+TABLE_NAME+"](_id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "fileName VARCHAR(50) UNIQUE, " +
            "start SMALLINT ," +
            "end SMALLINT ," +
            "fileTime INTEGER,"+
            "location VARCHAR(25) ," +
            "isShow TINYINT,"+
            "isChanged TINYINT ," +
            "upload TINYINT," +
            "times TINYINT);";
    public static final  String iqFile = "create table ["+TABLE_NAME_IQ+"](_id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "fileName VARCHAR(50)," +
            "upload TINYINT," +
            "_times TINYINT);";
    public static final  String poaFile = "create table ["+TABLE_NAME_POA+"](_id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "fileName VARCHAR(50)," +
            "upload TINYINT," +
            "_times TINYINT);";
    public static final  String tdoaFile = "create table ["+TABLE_NAME_TDOA+"](_id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "fileName VARCHAR(50)," +
            "upload TINYINT," +
            "_times TINYINT);";
    private DatabaseHelper(Context context) {
        super(context, DB_NAME, null, version);
    }

    public synchronized static DatabaseHelper getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new DatabaseHelper(context);
        }
        return mInstance;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(sql);
        db.execSQL(iqFile);
        db.execSQL(poaFile);
        db.execSQL(tdoaFile);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch (oldVersion) {
            case 1:
                db.execSQL("alter table localFile add column location VARCHAR");
                break;
            case 2:
                db.execSQL("alter table localFile add column isShow TINYINT");
                break;
            case 5:

                db.execSQL("alter table localFile add column location VARCHAR");
                break;
            default:
        }
    }
}

