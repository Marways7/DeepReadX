package com.deepreadx.viewer;

import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.example.deepreadx.R;

/**
 * 解释展示Fragment，负责显示AI解释内容
 * 
 * @author DeepReadX团队
 * @created 2025-05-10
 */
public class ExplanationFragment extends Fragment {
    private TextView tvExplanation;
    private ImageButton btnCloseDrawer;
    private DrawerLayout drawerLayout;
    
    /**
     * 创建Fragment实例
     * 
     * @param drawerLayout 父Activity中的DrawerLayout
     * @return ExplanationFragment实例
     */
    public static ExplanationFragment newInstance(DrawerLayout drawerLayout) {
        ExplanationFragment fragment = new ExplanationFragment();
        fragment.drawerLayout = drawerLayout;
        return fragment;
    }
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_explanation, container, false);
        
        // 初始化视图
        tvExplanation = view.findViewById(R.id.tvExplanation);
        btnCloseDrawer = view.findViewById(R.id.btnCloseDrawer);
        
        // 设置关闭按钮点击事件
        btnCloseDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout != null) {
                    drawerLayout.closeDrawer(GravityCompat.END);
                }
            }
        });
        
        return view;
    }
    
    /**
     * 更新解释内容
     * 
     * @param htmlContent 解释内容（支持HTML格式）
     */
    public void updateExplanation(String htmlContent) {
        if (tvExplanation != null) {
            tvExplanation.setText(Html.fromHtml(htmlContent, Html.FROM_HTML_MODE_COMPACT));
        }
    }
    
    /**
     * 设置DrawerLayout引用
     * 
     * @param drawerLayout DrawerLayout实例
     */
    public void setDrawerLayout(DrawerLayout drawerLayout) {
        this.drawerLayout = drawerLayout;
    }
} 