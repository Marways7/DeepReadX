package com.deepreadx.dao;

import com.deepreadx.model.ExplainStyle;

import java.util.List;

/**
 * 风格数据访问接口，定义对风格数据的增删改查操作
 * 
 * @author DeepReadX团队
 * @created 2025-05-16
 */
public interface StyleDao {
    
    /**
     * 插入新风格
     * 
     * @param style 风格对象
     * @return 新插入记录的ID，失败返回-1
     */
    long insert(ExplainStyle style);
    
    /**
     * 更新风格
     * 
     * @param style 风格对象
     * @return 更新的记录数
     */
    int update(ExplainStyle style);
    
    /**
     * 删除风格
     * 
     * @param id 风格ID
     * @return 删除的记录数
     */
    int delete(int id);
    
    /**
     * 查询所有风格
     * 
     * @return 风格列表
     */
    List<ExplainStyle> queryAll();
    
    /**
     * 根据ID查询风格
     * 
     * @param id 风格ID
     * @return 风格对象，不存在则返回null
     */
    ExplainStyle queryById(int id);
    
    /**
     * 获取默认风格
     * 
     * @return 默认风格对象，如果没有设置默认风格则返回列表中第一个
     */
    ExplainStyle getDefaultStyle();
    
    /**
     * 设置默认风格
     * 
     * @param id 设为默认的风格ID
     * @return 是否设置成功
     */
    boolean setDefaultStyle(int id);
} 