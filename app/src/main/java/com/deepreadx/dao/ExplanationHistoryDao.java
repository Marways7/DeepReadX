package com.deepreadx.dao;

import com.deepreadx.model.ExplanationHistory;
import java.util.List;

/**
 * 解释历史数据访问接口，定义对历史记录的增删改查操作
 * 
 * @author DeepReadX团队
 * @created 2025-05-15
 */
public interface ExplanationHistoryDao {
    
    /**
     * 插入新的历史记录
     * 
     * @param history 历史记录对象
     * @return 新插入记录的ID，失败返回-1
     */
    long insert(ExplanationHistory history);
    
    /**
     * 查询所有历史记录，按时间戳降序排列
     * 
     * @return 历史记录列表
     */
    List<ExplanationHistory> queryAll();
    
    /**
     * 根据PDF文件URI查询历史记录
     * 
     * @param pdfUri PDF文件URI
     * @return 对应PDF文件的历史记录列表
     */
    List<ExplanationHistory> queryByPdf(String pdfUri);
    
    /**
     * 根据ID删除历史记录
     * 
     * @param id 历史记录ID
     * @return 删除的记录数
     */
    int delete(int id);
    
    /**
     * 删除指定时间戳之前的历史记录
     * 
     * @param timestamp 时间戳
     * @return 删除的记录数
     */
    int deleteOlderThan(long timestamp);
    
    /**
     * 清空所有历史记录
     * 
     * @return 删除的记录数
     */
    int deleteAll();
} 