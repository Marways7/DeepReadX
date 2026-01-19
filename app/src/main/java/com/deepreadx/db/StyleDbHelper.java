package com.deepreadx.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * 风格数据库帮助类，负责管理风格表的创建与升级
 * 
 * @author DeepReadX团队
 * @created 2025-05-21
 */
public class StyleDbHelper extends SQLiteOpenHelper {
    private static final String TAG = "StyleDbHelper";
    
    // 数据库信息
    private static final String DATABASE_NAME = "deepreadx.db";
    private static final int DATABASE_VERSION = 2;
    
    // 表名
    public static final String TABLE_STYLES = "styles";
    
    // 列名
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_PROMPT_TEMPLATE = "prompt_template";
    public static final String COLUMN_IS_DEFAULT = "is_default";
    
    // 建表SQL
    private static final String SQL_CREATE_STYLES_TABLE = 
            "CREATE TABLE " + TABLE_STYLES + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NAME + " TEXT NOT NULL, " +
                    COLUMN_PROMPT_TEMPLATE + " TEXT NOT NULL, " +
                    COLUMN_IS_DEFAULT + " INTEGER DEFAULT 0" +
            ")";
    
    // 默认风格数据
    private static final String[] DEFAULT_STYLES = {
            "INSERT INTO " + TABLE_STYLES + " (" + COLUMN_NAME + ", " + COLUMN_PROMPT_TEMPLATE + ", " + COLUMN_IS_DEFAULT + ") " +
                    "VALUES ('简明易懂', '请以简明易懂的语言解释以下文本，用通俗的词汇和例子帮助理解：\n\n{text}', 1)",
            "INSERT INTO " + TABLE_STYLES + " (" + COLUMN_NAME + ", " + COLUMN_PROMPT_TEMPLATE + ", " + COLUMN_IS_DEFAULT + ") " +
                    "VALUES ('专业学术', '请以学术专业的方式分析以下文本，使用准确的术语并深入探讨其中的概念：\n\n{text}', 0)",
            "INSERT INTO " + TABLE_STYLES + " (" + COLUMN_NAME + ", " + COLUMN_PROMPT_TEMPLATE + ", " + COLUMN_IS_DEFAULT + ") " +
                    "VALUES ('小学生理解', '请用小学生能理解的简单语言解释以下内容，多用比喻和生活例子：\n\n{text}', 0)",
            "INSERT INTO " + TABLE_STYLES + " (" + COLUMN_NAME + ", " + COLUMN_PROMPT_TEMPLATE + ", " + COLUMN_IS_DEFAULT + ") " +
                    "VALUES ('深入解析', '请对以下内容进行深入解析，探讨其背景、含义和应用，以及可能的不同理解角度：\n\n{text}', 0)"
    };
    
    /**
     * 构造函数
     * 
     * @param context 应用上下文
     */
    public StyleDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    
    @Override
    public void onCreate(SQLiteDatabase db) {
        // 创建风格表
        db.execSQL(SQL_CREATE_STYLES_TABLE);
        
        // 插入默认风格数据
        for (String defaultStyle : DEFAULT_STYLES) {
            db.execSQL(defaultStyle);
        }
        
        Log.d(TAG, "数据库创建成功，已添加默认风格");
    }
    
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 简单处理：删除旧表，重新创建
        Log.w(TAG, "数据库升级：从 " + oldVersion + " 到 " + newVersion);
        // 如果是从版本1升级到版本2，并且 TABLE_STYLES 已经存在，则不需要删除重建。
        // HistoryDbHelper 会负责创建它自己的表。
        // 这里需要更精细的判断，或者确保两个Helper的onUpgrade逻辑不会冲突。
        // 为简单起见，暂时保持原有的删除重建逻辑，但这可能不是最优的，
        // 如果 HistoryDbHelper 已经创建了它的表，这里的 onCreate 可能会因为表已存在而失败。
        // 更好的做法是，StyleDbHelper的onUpgrade只处理与TABLE_STYLES相关的升级。
        // 如果 oldVersion是1，newVersion是2，并且TABLE_STYLES结构不变，则这里可以什么都不做。

        if (oldVersion < 2 && newVersion >= 2) {
            // 假设 TABLE_STYLES 在版本1时已经正确创建，并且到版本2其结构没有变化
            // HistoryDbHelper 会处理它自己的表创建，所以这里不需要对 TABLE_STYLES 做操作
            Log.i(TAG, "TABLE_STYLES 结构在版本2中无变化，无需升级。");
        } else {
            // 对于其他版本的升级，或者如果需要强制重建，执行以下逻辑
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_STYLES);
            onCreate(db);
        }
    }
} 