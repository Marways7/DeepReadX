package com.deepreadx.ocr;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.chinese.ChineseTextRecognizerOptions;

/**
 * OCR文本识别处理器，负责从图像中提取文本
 * 
 * @author DeepReadX团队
 * @created 2025-05-18
 */
public class OcrProcessor {
    private static final String TAG = "OcrProcessor";
    
    private final Context context;
    private final TextRecognizer recognizer;
    
    /**
     * OCR处理结果回调接口
     */
    public interface OcrCallback {
        /**
         * OCR识别成功回调
         * 
         * @param text 识别出的文本内容
         */
        void onSuccess(String text);
        
        /**
         * OCR识别失败回调
         * 
         * @param e 异常信息
         */
        void onFailure(Exception e);
    }
    
    /**
     * 构造函数
     * 
     * @param context 应用上下文
     */
    public OcrProcessor(Context context) {
        this.context = context;
        
        // 创建文本识别器，支持中文识别
        this.recognizer = TextRecognition.getClient(new ChineseTextRecognizerOptions.Builder().build());
    }
    
    /**
     * 从位图图像中提取文本
     * 
     * @param bitmap 包含文本的位图图像
     * @param callback 处理结果回调
     */
    public void extractText(Bitmap bitmap, final OcrCallback callback) {
        if (bitmap == null) {
            callback.onFailure(new IllegalArgumentException("Bitmap不能为空"));
            return;
        }
        
        InputImage image = InputImage.fromBitmap(bitmap, 0);
        
        // 执行文本识别
        recognizer.process(image)
                .addOnSuccessListener(new OnSuccessListener<Text>() {
                    @Override
                    public void onSuccess(Text text) {
                        // 处理识别结果
                        String recognizedText = processRecognizedText(text);
                        callback.onSuccess(recognizedText);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "文本识别失败", e);
                        callback.onFailure(e);
                    }
                });
    }
    
    /**
     * 处理识别结果，提取所有文本块
     * 
     * @param text 识别结果
     * @return 处理后的文本字符串
     */
    private String processRecognizedText(Text text) {
        StringBuilder sb = new StringBuilder();
        
        for (Text.TextBlock block : text.getTextBlocks()) {
            for (Text.Line line : block.getLines()) {
                sb.append(line.getText()).append("\n");
            }
            sb.append("\n");
        }
        
        return sb.toString().trim();
    }
    
    /**
     * 释放资源
     */
    public void close() {
        recognizer.close();
    }
} 