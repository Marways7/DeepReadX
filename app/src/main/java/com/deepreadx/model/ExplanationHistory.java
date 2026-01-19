package com.deepreadx.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 解释历史记录实体类，保存AI解释的历史记录
 * 
 * @author DeepReadX团队
 * @created 2025-05-15
 */
public class ExplanationHistory {
    private int id;
    private String pdfUri;
    private int pageIndex;
    private int styleId;
    private String explanation;
    private long timestamp;
    
    /**
     * 默认构造函数
     */
    public ExplanationHistory() {
        this.timestamp = System.currentTimeMillis();
    }
    
    /**
     * 完整构造函数
     * 
     * @param id 记录ID
     * @param pdfUri PDF文件URI
     * @param pageIndex 页码索引
     * @param styleId 风格ID
     * @param explanation 解释内容
     * @param timestamp 时间戳
     */
    public ExplanationHistory(int id, String pdfUri, int pageIndex, int styleId, String explanation, long timestamp) {
        this.id = id;
        this.pdfUri = pdfUri;
        this.pageIndex = pageIndex;
        this.styleId = styleId;
        this.explanation = explanation;
        this.timestamp = timestamp;
    }
    
    /**
     * 不含ID的构造函数
     * 
     * @param pdfUri PDF文件URI
     * @param pageIndex 页码索引
     * @param styleId 风格ID
     * @param explanation 解释内容
     */
    public ExplanationHistory(String pdfUri, int pageIndex, int styleId, String explanation) {
        this.pdfUri = pdfUri;
        this.pageIndex = pageIndex;
        this.styleId = styleId;
        this.explanation = explanation;
        this.timestamp = System.currentTimeMillis();
    }
    
    /**
     * 获取ID
     * 
     * @return 记录ID
     */
    public int getId() {
        return id;
    }
    
    /**
     * 设置ID
     * 
     * @param id 记录ID
     */
    public void setId(int id) {
        this.id = id;
    }
    
    /**
     * 获取PDF文件URI
     * 
     * @return PDF文件URI
     */
    public String getPdfUri() {
        return pdfUri;
    }
    
    /**
     * 设置PDF文件URI
     * 
     * @param pdfUri PDF文件URI
     */
    public void setPdfUri(String pdfUri) {
        this.pdfUri = pdfUri;
    }
    
    /**
     * 获取页码索引
     * 
     * @return 页码索引
     */
    public int getPageIndex() {
        return pageIndex;
    }
    
    /**
     * 设置页码索引
     * 
     * @param pageIndex 页码索引
     */
    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }
    
    /**
     * 获取风格ID
     * 
     * @return 风格ID
     */
    public int getStyleId() {
        return styleId;
    }
    
    /**
     * 设置风格ID
     * 
     * @param styleId 风格ID
     */
    public void setStyleId(int styleId) {
        this.styleId = styleId;
    }
    
    /**
     * 获取解释内容
     * 
     * @return 解释内容
     */
    public String getExplanation() {
        return explanation;
    }
    
    /**
     * 设置解释内容
     * 
     * @param explanation 解释内容
     */
    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }
    
    /**
     * 获取时间戳
     * 
     * @return 时间戳
     */
    public long getTimestamp() {
        return timestamp;
    }
    
    /**
     * 设置时间戳
     * 
     * @param timestamp 时间戳
     */
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    
    /**
     * 获取格式化的日期时间字符串
     * 
     * @return 格式化的日期时间字符串
     */
    public String getFormattedDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }
    
    /**
     * 获取PDF文件名（从URI中提取）
     * 
     * @return PDF文件名
     */
    public String getPdfFileName() {
        if (pdfUri == null || pdfUri.isEmpty()) {
            return "未知文件";
        }
        
        // 提取文件名
        String fileName = pdfUri;
        int lastSlashIndex = fileName.lastIndexOf('/');
        if (lastSlashIndex != -1 && lastSlashIndex < fileName.length() - 1) {
            fileName = fileName.substring(lastSlashIndex + 1);
        }
        
        return fileName;
    }
} 