package com.deepreadx.dao.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.deepreadx.db.HistoryDbHelper;
import com.deepreadx.dao.ExplanationHistoryDao;
import com.deepreadx.model.ExplanationHistory;

import java.util.ArrayList;
import java.util.List;

/**
 * 解释历史数据访问接口实现类
 * 
 * @author DeepReadX团队
 * @created 2025-05-15
 */
public class ExplanationHistoryDaoImpl implements ExplanationHistoryDao {
    private static final String TAG = "ExplanationHistoryDao";
    
    private final HistoryDbHelper dbHelper;
    
    /**
     * 构造函数
     * 
     * @param context 应用上下文
     */
    public ExplanationHistoryDaoImpl(Context context) {
        this.dbHelper = new HistoryDbHelper(context);
    }
    
    @Override
    public long insert(ExplanationHistory history) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long id = -1;
        
        try {
            ContentValues values = new ContentValues();
            values.put(HistoryDbHelper.COLUMN_PDF_URI, history.getPdfUri());
            values.put(HistoryDbHelper.COLUMN_PAGE_INDEX, history.getPageIndex());
            values.put(HistoryDbHelper.COLUMN_STYLE_ID, history.getStyleId());
            values.put(HistoryDbHelper.COLUMN_EXPLANATION, history.getExplanation());
            values.put(HistoryDbHelper.COLUMN_TIMESTAMP, history.getTimestamp());
            
            id = db.insert(HistoryDbHelper.TABLE_EXPLANATION_HISTORY, null, values);
            Log.d(TAG, "插入历史记录成功，ID: " + id);
        } catch (Exception e) {
            Log.e(TAG, "插入历史记录失败: " + e.getMessage(), e);
        } finally {
            db.close();
        }
        
        return id;
    }
    
    @Override
    public List<ExplanationHistory> queryAll() {
        List<ExplanationHistory> histories = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        
        try {
            Cursor cursor = db.query(
                    HistoryDbHelper.TABLE_EXPLANATION_HISTORY,
                    null,
                    null,
                    null,
                    null,
                    null,
                    HistoryDbHelper.COLUMN_TIMESTAMP + " DESC"
            );
            
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    histories.add(extractHistoryFromCursor(cursor));
                }
                cursor.close();
            }
            
            Log.d(TAG, "查询到 " + histories.size() + " 条历史记录");
        } catch (Exception e) {
            Log.e(TAG, "查询所有历史记录失败: " + e.getMessage(), e);
        } finally {
            db.close();
        }
        
        return histories;
    }
    
    @Override
    public List<ExplanationHistory> queryByPdf(String pdfUri) {
        List<ExplanationHistory> histories = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        
        try {
            Cursor cursor = db.query(
                    HistoryDbHelper.TABLE_EXPLANATION_HISTORY,
                    null,
                    HistoryDbHelper.COLUMN_PDF_URI + " = ?",
                    new String[]{pdfUri},
                    null,
                    null,
                    HistoryDbHelper.COLUMN_TIMESTAMP + " DESC"
            );
            
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    histories.add(extractHistoryFromCursor(cursor));
                }
                cursor.close();
            }
            
            Log.d(TAG, "查询到 " + histories.size() + " 条PDF历史记录");
        } catch (Exception e) {
            Log.e(TAG, "根据PDF URI查询历史记录失败: " + e.getMessage(), e);
        } finally {
            db.close();
        }
        
        return histories;
    }
    
    @Override
    public int delete(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int affectedRows = 0;
        
        try {
            affectedRows = db.delete(
                    HistoryDbHelper.TABLE_EXPLANATION_HISTORY,
                    HistoryDbHelper.COLUMN_ID + " = ?",
                    new String[]{String.valueOf(id)}
            );
            
            Log.d(TAG, "删除历史记录成功，ID: " + id);
        } catch (Exception e) {
            Log.e(TAG, "删除历史记录失败: " + e.getMessage(), e);
        } finally {
            db.close();
        }
        
        return affectedRows;
    }
    
    @Override
    public int deleteOlderThan(long timestamp) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int affectedRows = 0;
        
        try {
            affectedRows = db.delete(
                    HistoryDbHelper.TABLE_EXPLANATION_HISTORY,
                    HistoryDbHelper.COLUMN_TIMESTAMP + " < ?",
                    new String[]{String.valueOf(timestamp)}
            );
            
            Log.d(TAG, "删除旧历史记录成功，影响行数: " + affectedRows);
        } catch (Exception e) {
            Log.e(TAG, "删除旧历史记录失败: " + e.getMessage(), e);
        } finally {
            db.close();
        }
        
        return affectedRows;
    }
    
    @Override
    public int deleteAll() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int affectedRows = 0;
        
        try {
            affectedRows = db.delete(
                    HistoryDbHelper.TABLE_EXPLANATION_HISTORY,
                    null,
                    null
            );
            
            Log.d(TAG, "清空历史记录成功，影响行数: " + affectedRows);
        } catch (Exception e) {
            Log.e(TAG, "清空历史记录失败: " + e.getMessage(), e);
        } finally {
            db.close();
        }
        
        return affectedRows;
    }
    
    /**
     * 从游标中提取历史记录对象
     * 
     * @param cursor 数据库游标
     * @return 历史记录对象
     */
    private ExplanationHistory extractHistoryFromCursor(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndexOrThrow(HistoryDbHelper.COLUMN_ID));
        String pdfUri = cursor.getString(cursor.getColumnIndexOrThrow(HistoryDbHelper.COLUMN_PDF_URI));
        int pageIndex = cursor.getInt(cursor.getColumnIndexOrThrow(HistoryDbHelper.COLUMN_PAGE_INDEX));
        int styleId = cursor.getInt(cursor.getColumnIndexOrThrow(HistoryDbHelper.COLUMN_STYLE_ID));
        String explanation = cursor.getString(cursor.getColumnIndexOrThrow(HistoryDbHelper.COLUMN_EXPLANATION));
        long timestamp = cursor.getLong(cursor.getColumnIndexOrThrow(HistoryDbHelper.COLUMN_TIMESTAMP));
        
        return new ExplanationHistory(id, pdfUri, pageIndex, styleId, explanation, timestamp);
    }
} 