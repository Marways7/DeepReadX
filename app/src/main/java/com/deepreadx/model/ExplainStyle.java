package com.deepreadx.model;

/**
 * AI解释风格实体类，定义讲解风格的数据模型
 * 
 * @author DeepReadX团队
 * @created 2023-05-21
 */
public class ExplainStyle {
    private int id;
    private String name;
    private String promptTemplate;
    private boolean isDefault;
    
    /**
     * 默认构造函数
     */
    public ExplainStyle() {
    }
    
    /**
     * 带参数的构造函数（不含ID）
     * 
     * @param name 风格名称
     * @param promptTemplate 提示词模板
     */
    public ExplainStyle(String name, String promptTemplate) {
        this.name = name;
        this.promptTemplate = promptTemplate;
        this.isDefault = false;
    }
    
    /**
     * 带所有参数的构造函数
     * 
     * @param id 风格ID
     * @param name 风格名称
     * @param promptTemplate 提示词模板
     * @param isDefault 是否为默认风格
     */
    public ExplainStyle(int id, String name, String promptTemplate, boolean isDefault) {
        this.id = id;
        this.name = name;
        this.promptTemplate = promptTemplate;
        this.isDefault = isDefault;
    }
    
    /**
     * 获取风格ID
     * 
     * @return 风格ID
     */
    public int getId() {
        return id;
    }
    
    /**
     * 设置风格ID
     * 
     * @param id 风格ID
     */
    public void setId(int id) {
        this.id = id;
    }
    
    /**
     * 获取风格名称
     * 
     * @return 风格名称
     */
    public String getName() {
        return name;
    }
    
    /**
     * 设置风格名称
     * 
     * @param name 风格名称
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * 获取提示词模板
     * 
     * @return 提示词模板
     */
    public String getPromptTemplate() {
        return promptTemplate;
    }
    
    /**
     * 设置提示词模板
     * 
     * @param promptTemplate 提示词模板
     */
    public void setPromptTemplate(String promptTemplate) {
        this.promptTemplate = promptTemplate;
    }
    
    /**
     * 判断是否为默认风格
     * 
     * @return 是否为默认风格
     */
    public boolean isDefault() {
        return isDefault;
    }
    
    /**
     * 设置是否为默认风格
     * 
     * @param isDefault 是否为默认风格
     */
    public void setDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }
    
    @Override
    public String toString() {
        return name + (isDefault ? " (默认)" : "");
    }
} 