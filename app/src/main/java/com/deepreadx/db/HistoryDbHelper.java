package com.deepreadx.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * 历史记录数据库辅助类，负责管理历史记录表的创建与升级
 * 
 * @author DeepReadX团队
 * @created 2025-05-15
 */
public class HistoryDbHelper extends SQLiteOpenHelper {
    private static final String TAG = "HistoryDbHelper";
    
    // 数据库信息
    private static final String DATABASE_NAME = "deepreadx.db";
    private static final int DATABASE_VERSION = 2;
    
    // 表名
    public static final String TABLE_EXPLANATION_HISTORY = "explanation_history";
    
    // 列名
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_PDF_URI = "pdfUri";
    public static final String COLUMN_PAGE_INDEX = "pageIndex";
    public static final String COLUMN_STYLE_ID = "styleId";
    public static final String COLUMN_EXPLANATION = "explanation";
    public static final String COLUMN_TIMESTAMP = "timestamp";
    
    // 创建表SQL
    private static final String SQL_CREATE_HISTORY_TABLE = 
            "CREATE TABLE " + TABLE_EXPLANATION_HISTORY + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_PDF_URI + " TEXT NOT NULL, " +
                    COLUMN_PAGE_INDEX + " INTEGER NOT NULL, " +
                    COLUMN_STYLE_ID + " INTEGER NOT NULL, " +
                    COLUMN_EXPLANATION + " TEXT NOT NULL, " +
                    COLUMN_TIMESTAMP + " INTEGER NOT NULL" +
            ")";
    
    /**
     * 构造函数
     * 
     * @param context 应用上下文
     */
    public HistoryDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    
    @Override
    public void onCreate(SQLiteDatabase db) {
        // 创建历史记录表
        db.execSQL(SQL_CREATE_HISTORY_TABLE);
        Log.d(TAG, "创建历史记录表成功");
    }
    
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "数据库升级：从 " + oldVersion + " 到 " + newVersion);
        
        // 升级逻辑
        if (oldVersion < 2) {
            // 从版本1升级到版本2，添加历史记录表
            db.execSQL(SQL_CREATE_HISTORY_TABLE);
            Log.d(TAG, "添加历史记录表");
        }
        
        // 如果有更多版本升级需求，可以继续添加条件
    }
} 