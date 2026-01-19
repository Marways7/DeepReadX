package com.deepreadx.ui;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.deepreadx.dao.ExplanationHistoryDao;
import com.deepreadx.dao.impl.ExplanationHistoryDaoImpl;
import com.deepreadx.model.ExplanationHistory;
import com.deepreadx.ui.adapter.HistoryAdapter;
import com.example.deepreadx.R;

import java.util.List;

/**
 * 解释历史记录Activity，显示用户所有查询过的解释历史
 * 
 * @author DeepReadX团队
 * @created 2025-05-21
 */
public class HistoryActivity extends AppCompatActivity {
    private ExplanationHistoryDao historyDao;
    private RecyclerView rvHistory;
    private HistoryAdapter adapter;
    private TextView tvEmptyView;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        
        // 初始化DAO
        historyDao = new ExplanationHistoryDaoImpl(this);
        
        // 初始化视图
        initViews();
        
        // 加载历史数据
        loadHistoryData();
    }
    
    /**
     * 初始化视图组件
     */
    private void initViews() {
        // 配置Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("解释历史记录");
        
        // 配置RecyclerView
        rvHistory = findViewById(R.id.rvHistory);
        rvHistory.setLayoutManager(new LinearLayoutManager(this));
        
        // 初始化适配器
        adapter = new HistoryAdapter(this, historyDao.queryAll());
        rvHistory.setAdapter(adapter);
        
        // 如果有空视图，则配置
        tvEmptyView = findViewById(R.id.tvEmptyHistory);
        if (tvEmptyView != null) {
            updateEmptyViewVisibility();
        }
    }
    
    /**
     * 加载历史记录数据
     */
    private void loadHistoryData() {
        List<ExplanationHistory> historyList = historyDao.queryAll();
        adapter.updateData(historyList);
        updateEmptyViewVisibility();
    }
    
    /**
     * 更新空视图可见性
     */
    private void updateEmptyViewVisibility() {
        if (tvEmptyView != null) {
            if (adapter.getItemCount() == 0) {
                tvEmptyView.setVisibility(View.VISIBLE);
                rvHistory.setVisibility(View.GONE);
            } else {
                tvEmptyView.setVisibility(View.GONE);
                rvHistory.setVisibility(View.VISIBLE);
            }
        }
    }
    
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
} 