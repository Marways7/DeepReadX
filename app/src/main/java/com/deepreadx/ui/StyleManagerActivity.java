package com.deepreadx.ui;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.deepreadx.dao.StyleDao;
import com.deepreadx.dao.StyleDaoImpl;
import com.deepreadx.model.ExplainStyle;
import com.deepreadx.ui.adapter.StyleAdapter;
import com.example.deepreadx.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

/**
 * 讲解风格管理Activity，负责风格的增删改查操作
 * 
 * @author DeepReadX团队
 * @created 2025-05-21
 */
public class StyleManagerActivity extends AppCompatActivity implements StyleAdapter.StyleItemListener {
    private RecyclerView rvStyles;
    private FloatingActionButton fabAddStyle;
    private StyleAdapter styleAdapter;
    private StyleDao styleDao;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_style_manager);
        
        // 初始化Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("讲解风格管理");
        
        // 初始化组件
        rvStyles = findViewById(R.id.rvStyles);
        fabAddStyle = findViewById(R.id.fabAddStyle);
        
        // 初始化DAO
        styleDao = new StyleDaoImpl(this);
        
        // 设置RecyclerView
        rvStyles.setLayoutManager(new LinearLayoutManager(this));
        styleAdapter = new StyleAdapter(this, styleDao.queryAll());
        styleAdapter.setStyleItemListener(this);
        rvStyles.setAdapter(styleAdapter);
        
        // 设置添加按钮点击事件
        fabAddStyle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddStyleDialog();
            }
        });
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    public void onStyleDefaultChanged(ExplainStyle style) {
        styleDao.setDefaultStyle(style.getId());
        loadStyles();
    }
    
    @Override
    public void onStyleEdit(ExplainStyle style) {
        showEditStyleDialog(style);
    }
    
    @Override
    public void onStyleDelete(ExplainStyle style) {
        showDeleteConfirmationDialog(style);
    }
    
    /**
     * 加载风格列表
     */
    private void loadStyles() {
        List<ExplainStyle> styles = styleDao.queryAll();
        styleAdapter.updateData(styles);
    }
    
    /**
     * 显示添加风格对话框
     */
    private void showAddStyleDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_edit_style, null);
        final EditText etStyleName = dialogView.findViewById(R.id.etStyleName);
        final EditText etPromptTemplate = dialogView.findViewById(R.id.etPromptTemplate);
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("添加讲解风格")
                .setView(dialogView)
                .setPositiveButton("保存", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = etStyleName.getText().toString().trim();
                        String template = etPromptTemplate.getText().toString().trim();
                        
                        if (name.isEmpty() || template.isEmpty()) {
                            Toast.makeText(StyleManagerActivity.this, 
                                    "风格名称和提示词模板不能为空", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        
                        ExplainStyle newStyle = new ExplainStyle(name, template);
                        long id = styleDao.insert(newStyle);
                        
                        if (id > 0) {
                            Toast.makeText(StyleManagerActivity.this, 
                                    "风格添加成功", Toast.LENGTH_SHORT).show();
                            loadStyles();
                        } else {
                            Toast.makeText(StyleManagerActivity.this, 
                                    "风格添加失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("取消", null);
        
        builder.create().show();
    }
    
    /**
     * 显示编辑风格对话框
     * 
     * @param style 要编辑的风格
     */
    private void showEditStyleDialog(final ExplainStyle style) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_edit_style, null);
        final EditText etStyleName = dialogView.findViewById(R.id.etStyleName);
        final EditText etPromptTemplate = dialogView.findViewById(R.id.etPromptTemplate);
        
        etStyleName.setText(style.getName());
        etPromptTemplate.setText(style.getPromptTemplate());
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("编辑讲解风格")
                .setView(dialogView)
                .setPositiveButton("保存", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = etStyleName.getText().toString().trim();
                        String template = etPromptTemplate.getText().toString().trim();
                        
                        if (name.isEmpty() || template.isEmpty()) {
                            Toast.makeText(StyleManagerActivity.this, 
                                    "风格名称和提示词模板不能为空", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        
                        style.setName(name);
                        style.setPromptTemplate(template);
                        
                        int rows = styleDao.update(style);
                        
                        if (rows > 0) {
                            Toast.makeText(StyleManagerActivity.this, 
                                    "风格更新成功", Toast.LENGTH_SHORT).show();
                            loadStyles();
                        } else {
                            Toast.makeText(StyleManagerActivity.this, 
                                    "风格更新失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("取消", null);
        
        builder.create().show();
    }
    
    /**
     * 显示删除确认对话框
     * 
     * @param style 要删除的风格
     */
    private void showDeleteConfirmationDialog(final ExplainStyle style) {
        // 默认风格不允许删除
        if (style.isDefault()) {
            Toast.makeText(this, "不能删除默认风格", Toast.LENGTH_SHORT).show();
            return;
        }
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("删除风格")
                .setMessage("确定要删除风格 \"" + style.getName() + "\" 吗？")
                .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int rows = styleDao.delete(style.getId());
                        
                        if (rows > 0) {
                            Toast.makeText(StyleManagerActivity.this, 
                                    "风格已删除", Toast.LENGTH_SHORT).show();
                            loadStyles();
                        } else {
                            Toast.makeText(StyleManagerActivity.this, 
                                    "删除风格失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("取消", null);
        
        builder.create().show();
    }
} 