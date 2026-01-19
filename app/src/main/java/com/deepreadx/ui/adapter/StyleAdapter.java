package com.deepreadx.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.deepreadx.model.ExplainStyle;
import com.example.deepreadx.R;

import java.util.List;

/**
 * 风格列表适配器，用于显示风格列表
 * 
 * @author DeepReadX团队
 * @created 2025-05-19
 */
public class StyleAdapter extends RecyclerView.Adapter<StyleAdapter.StyleViewHolder> {
    private Context context;
    private List<ExplainStyle> styles;
    private StyleItemListener listener;

    /**
     * 构造函数
     * 
     * @param context 上下文
     * @param styles 风格列表
     */
    public StyleAdapter(Context context, List<ExplainStyle> styles) {
        this.context = context;
        this.styles = styles;
    }

    /**
     * 更新数据
     * 
     * @param styles 新的风格列表
     */
    public void updateData(List<ExplainStyle> styles) {
        this.styles = styles;
        notifyDataSetChanged();
    }

    /**
     * 设置风格项监听器
     * 
     * @param listener 监听器
     */
    public void setStyleItemListener(StyleItemListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public StyleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_style, parent, false);
        return new StyleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StyleViewHolder holder, int position) {
        final ExplainStyle style = styles.get(position);
        
        holder.tvStyleName.setText(style.getName());
        holder.tvStylePrompt.setText(style.getPromptTemplate());
        holder.rbDefault.setChecked(style.isDefault());
        
        // 设置默认风格选中事件
        holder.rbDefault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null && !style.isDefault()) {
                    listener.onStyleDefaultChanged(style);
                }
            }
        });
        
        // 设置编辑按钮点击事件
        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onStyleEdit(style);
                }
            }
        });
        
        // 设置删除按钮点击事件
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onStyleDelete(style);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return styles == null ? 0 : styles.size();
    }

    /**
     * 风格ViewHolder
     */
    public static class StyleViewHolder extends RecyclerView.ViewHolder {
        TextView tvStyleName;
        TextView tvStylePrompt;
        RadioButton rbDefault;
        ImageButton btnEdit;
        ImageButton btnDelete;

        public StyleViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStyleName = itemView.findViewById(R.id.tvStyleName);
            tvStylePrompt = itemView.findViewById(R.id.tvStylePrompt);
            rbDefault = itemView.findViewById(R.id.rbDefault);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }

    /**
     * 风格项监听器接口
     */
    public interface StyleItemListener {
        /**
         * 当风格默认状态发生变化时调用
         * 
         * @param style 变化的风格
         */
        void onStyleDefaultChanged(ExplainStyle style);
        
        /**
         * 当编辑风格时调用
         * 
         * @param style 要编辑的风格
         */
        void onStyleEdit(ExplainStyle style);
        
        /**
         * 当删除风格时调用
         * 
         * @param style 要删除的风格
         */
        void onStyleDelete(ExplainStyle style);
    }
} 