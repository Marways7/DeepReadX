package com.deepreadx.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.deepreadx.db.StyleDbHelper;
import com.deepreadx.model.ExplainStyle;

import java.util.ArrayList;
import java.util.List;

/**
 * 风格数据访问接口实现类
 * 
 * @author DeepReadX团队
 * @created 2025-05-16
 */
public class StyleDaoImpl implements StyleDao {
    private static final String TAG = "StyleDaoImpl";
    
    private final StyleDbHelper dbHelper;
    
    /**
     * 构造函数
     * 
     * @param context 应用上下文
     */
    public StyleDaoImpl(Context context) {
        this.dbHelper = new StyleDbHelper(context);
    }
    
    @Override
    public long insert(ExplainStyle style) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long id = -1;
        
        try {
            // 如果设置了此风格为默认，先将所有风格设为非默认
            if (style.isDefault()) {
                clearDefaultStyles(db);
            }
            
            // 插入新记录
            ContentValues values = new ContentValues();
            values.put(StyleDbHelper.COLUMN_NAME, style.getName());
            values.put(StyleDbHelper.COLUMN_PROMPT_TEMPLATE, style.getPromptTemplate());
            values.put(StyleDbHelper.COLUMN_IS_DEFAULT, style.isDefault() ? 1 : 0);
            
            id = db.insert(StyleDbHelper.TABLE_STYLES, null, values);
            Log.d(TAG, "插入风格成功，ID: " + id);
        } catch (Exception e) {
            Log.e(TAG, "插入风格失败: " + e.getMessage(), e);
        } finally {
            db.close();
        }
        
        return id;
    }
    
    @Override
    public int update(ExplainStyle style) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int affectedRows = 0;
        
        try {
            // 如果设置了此风格为默认，先将所有风格设为非默认
            if (style.isDefault()) {
                clearDefaultStyles(db);
            }
            
            // 更新记录
            ContentValues values = new ContentValues();
            values.put(StyleDbHelper.COLUMN_NAME, style.getName());
            values.put(StyleDbHelper.COLUMN_PROMPT_TEMPLATE, style.getPromptTemplate());
            values.put(StyleDbHelper.COLUMN_IS_DEFAULT, style.isDefault() ? 1 : 0);
            
            affectedRows = db.update(
                    StyleDbHelper.TABLE_STYLES,
                    values,
                    StyleDbHelper.COLUMN_ID + " = ?",
                    new String[]{String.valueOf(style.getId())}
            );
            
            Log.d(TAG, "更新风格成功，影响行数: " + affectedRows);
        } catch (Exception e) {
            Log.e(TAG, "更新风格失败: " + e.getMessage(), e);
        } finally {
            db.close();
        }
        
        return affectedRows;
    }
    
    @Override
    public int delete(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int affectedRows = 0;
        
        try {
            // 检查是否是默认风格
            ExplainStyle style = queryById(id);
            if (style != null && style.isDefault()) {
                Log.w(TAG, "尝试删除默认风格，操作取消");
                return 0; // 不允许删除默认风格
            }
            
            // 删除记录
            affectedRows = db.delete(
                    StyleDbHelper.TABLE_STYLES,
                    StyleDbHelper.COLUMN_ID + " = ?",
                    new String[]{String.valueOf(id)}
            );
            
            Log.d(TAG, "删除风格成功，影响行数: " + affectedRows);
        } catch (Exception e) {
            Log.e(TAG, "删除风格失败: " + e.getMessage(), e);
        } finally {
            db.close();
        }
        
        return affectedRows;
    }
    
    @Override
    public List<ExplainStyle> queryAll() {
        List<ExplainStyle> styles = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        
        try {
            Cursor cursor = db.query(
                    StyleDbHelper.TABLE_STYLES,
                    null,
                    null,
                    null,
                    null,
                    null,
                    StyleDbHelper.COLUMN_IS_DEFAULT + " DESC, " + StyleDbHelper.COLUMN_NAME + " ASC"
            );
            
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    styles.add(extractStyleFromCursor(cursor));
                }
                cursor.close();
            }
            
            Log.d(TAG, "查询到 " + styles.size() + " 个风格");
        } catch (Exception e) {
            Log.e(TAG, "查询所有风格失败: " + e.getMessage(), e);
        } finally {
            db.close();
        }
        
        return styles;
    }
    
    @Override
    public ExplainStyle queryById(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        ExplainStyle style = null;
        
        try {
            Cursor cursor = db.query(
                    StyleDbHelper.TABLE_STYLES,
                    null,
                    StyleDbHelper.COLUMN_ID + " = ?",
                    new String[]{String.valueOf(id)},
                    null,
                    null,
                    null
            );
            
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    style = extractStyleFromCursor(cursor);
                }
                cursor.close();
            }
            
            Log.d(TAG, "查询风格，ID: " + id + ", 结果: " + (style != null));
        } catch (Exception e) {
            Log.e(TAG, "根据ID查询风格失败: " + e.getMessage(), e);
        } finally {
            db.close();
        }
        
        return style;
    }
    
    @Override
    public ExplainStyle getDefaultStyle() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        ExplainStyle style = null;
        
        try {
            // 查询默认风格
            Cursor cursor = db.query(
                    StyleDbHelper.TABLE_STYLES,
                    null,
                    StyleDbHelper.COLUMN_IS_DEFAULT + " = 1",
                    null,
                    null,
                    null,
                    null,
                    "1"
            );
            
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    style = extractStyleFromCursor(cursor);
                }
                cursor.close();
            }
            
            // 如果没有默认风格，返回第一个风格
            if (style == null) {
                cursor = db.query(
                        StyleDbHelper.TABLE_STYLES,
                        null,
                        null,
                        null,
                        null,
                        null,
                        StyleDbHelper.COLUMN_ID + " ASC",
                        "1"
                );
                
                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        style = extractStyleFromCursor(cursor);
                    }
                    cursor.close();
                }
            }
            
            Log.d(TAG, "获取默认风格: " + (style != null ? style.getName() : "无"));
        } catch (Exception e) {
            Log.e(TAG, "获取默认风格失败: " + e.getMessage(), e);
        } finally {
            db.close();
        }
        
        return style;
    }
    
    @Override
    public boolean setDefaultStyle(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        boolean success = false;
        
        try {
            db.beginTransaction();
            
            // 先将所有风格设为非默认
            clearDefaultStyles(db);
            
            // 将指定风格设为默认
            ContentValues values = new ContentValues();
            values.put(StyleDbHelper.COLUMN_IS_DEFAULT, 1);
            
            int affectedRows = db.update(
                    StyleDbHelper.TABLE_STYLES,
                    values,
                    StyleDbHelper.COLUMN_ID + " = ?",
                    new String[]{String.valueOf(id)}
            );
            
            success = affectedRows > 0;
            
            if (success) {
                db.setTransactionSuccessful();
                Log.d(TAG, "设置默认风格成功，ID: " + id);
            } else {
                Log.w(TAG, "设置默认风格失败，ID不存在: " + id);
            }
        } catch (Exception e) {
            Log.e(TAG, "设置默认风格失败: " + e.getMessage(), e);
        } finally {
            if (db.inTransaction()) {
                db.endTransaction();
            }
            db.close();
        }
        
        return success;
    }
    
    /**
     * 清除所有默认风格标记
     * 
     * @param db 数据库实例
     */
    private void clearDefaultStyles(SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put(StyleDbHelper.COLUMN_IS_DEFAULT, 0);
        db.update(StyleDbHelper.TABLE_STYLES, values, null, null);
    }
    
    /**
     * 从游标提取风格对象
     * 
     * @param cursor 数据库游标
     * @return 风格对象
     */
    private ExplainStyle extractStyleFromCursor(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndexOrThrow(StyleDbHelper.COLUMN_ID));
        String name = cursor.getString(cursor.getColumnIndexOrThrow(StyleDbHelper.COLUMN_NAME));
        String promptTemplate = cursor.getString(cursor.getColumnIndexOrThrow(StyleDbHelper.COLUMN_PROMPT_TEMPLATE));
        int isDefault = cursor.getInt(cursor.getColumnIndexOrThrow(StyleDbHelper.COLUMN_IS_DEFAULT));
        
        return new ExplainStyle(id, name, promptTemplate, isDefault == 1);
    }
} 