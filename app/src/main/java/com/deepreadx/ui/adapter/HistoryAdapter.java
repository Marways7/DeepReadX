package com.deepreadx.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.deepreadx.dao.StyleDao;
import com.deepreadx.dao.StyleDaoImpl;
import com.deepreadx.model.ExplainStyle;
import com.deepreadx.model.ExplanationHistory;
import com.deepreadx.viewer.PdfViewerActivity;
import com.example.deepreadx.R;

import java.util.List;

/**
 * 解释历史记录列表适配器
 * 
 * @author DeepReadX团队
 * @created 2025-05-16
 */
public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
    private final List<ExplanationHistory> historyList;
    private final Context context;
    private final StyleDao styleDao;
    
    /**
     * 构造函数
     * 
     * @param context 上下文
     * @param historyList 历史记录列表
     */
    public HistoryAdapter(Context context, List<ExplanationHistory> historyList) {
        this.context = context;
        this.historyList = historyList;
        this.styleDao = new StyleDaoImpl(context);
    }
    
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_history, parent, false);
        return new ViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ExplanationHistory history = historyList.get(position);
        
        // 设置PDF文件名
        holder.tvPdfName.setText(history.getPdfFileName());
        
        // 设置页码
        holder.tvPage.setText(String.format("第 %d 页", history.getPageIndex() + 1));
        
        // 设置日期
        holder.tvDate.setText(history.getFormattedDateTime());
        
        // 设置解释文本（截断显示）
        String text = history.getExplanation();
        if (text.length() > 150) {
            text = text.substring(0, 150) + "...";
        }
        holder.tvText.setText(text);
        
        // 设置风格名称
        ExplainStyle style = styleDao.queryById(history.getStyleId());
        String styleName = style != null ? style.getName() : "未知风格";
        holder.tvStyleName.setText(String.format("风格: %s", styleName));
        
        // 设置点击事件，打开PDF查看器并跳转到对应页面
        holder.cardView.setOnClickListener(v -> {
            Intent intent = new Intent(context, PdfViewerActivity.class);
            intent.putExtra("pdf_uri", history.getPdfUri());
            intent.putExtra("page_index", history.getPageIndex());
            context.startActivity(intent);
        });
    }
    
    @Override
    public int getItemCount() {
        return historyList.size();
    }
    
    /**
     * 更新数据
     * 
     * @param newList 新的历史记录列表
     */
    public void updateData(List<ExplanationHistory> newList) {
        this.historyList.clear();
        this.historyList.addAll(newList);
        notifyDataSetChanged();
    }
    
    /**
     * ViewHolder类
     */
    static class ViewHolder extends RecyclerView.ViewHolder {
        final CardView cardView;
        final TextView tvPdfName;
        final TextView tvPage;
        final TextView tvDate;
        final TextView tvText;
        final TextView tvStyleName;
        
        ViewHolder(View view) {
            super(view);
            cardView = (CardView) view;
            tvPdfName = view.findViewById(R.id.tvPdfName);
            tvPage = view.findViewById(R.id.tvPage);
            tvDate = view.findViewById(R.id.tvDate);
            tvText = view.findViewById(R.id.tvText);
            tvStyleName = view.findViewById(R.id.tvStyleName);
        }
    }
} 