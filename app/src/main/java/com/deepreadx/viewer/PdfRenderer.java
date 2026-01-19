package com.deepreadx.viewer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer.Page;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import java.io.IOException;

/**
 * PDF渲染管理类，负责PDF文件的加载、显示和分页浏览
 * 
 * @author DeepReadX团队
 * @created 2025-05-18
 */
public class PdfRenderer implements AutoCloseable {
    private static final String TAG = "PdfRenderer";
    
    private android.graphics.pdf.PdfRenderer renderer;
    private ParcelFileDescriptor fileDescriptor;
    private int pageCount;
    
    /**
     * 构造函数
     *
     * @param context 上下文
     * @param pdfUri PDF文件URI
     * @throws IOException 如果PDF文件无法打开
     */
    public PdfRenderer(Context context, Uri pdfUri) throws IOException {
        try {
            // 获取PDF文件的文件描述符
            fileDescriptor = context.getContentResolver().openFileDescriptor(pdfUri, "r");
            if (fileDescriptor == null) {
                throw new IOException("无法获取文件描述符");
            }
            
            // 创建PDF渲染器
            renderer = new android.graphics.pdf.PdfRenderer(fileDescriptor);
            pageCount = renderer.getPageCount();
            
            Log.d(TAG, "PDF已加载，共" + pageCount + "页");
        } catch (IOException e) {
            close();
            throw e;
        } catch (Exception e) {
            close();
            throw new IOException("无法初始化PDF渲染器: " + e.getMessage(), e);
        }
    }
    
    /**
     * 获取PDF文档总页数
     *
     * @return 页数
     */
    public int getPageCount() {
        return pageCount;
    }
    
    /**
     * 渲染指定页码到Bitmap对象
     *
     * @param pageIndex 页码索引，从0开始
     * @param width 目标宽度（像素）
     * @param height 目标高度（像素）
     * @return 渲染后的页面Bitmap
     * @throws IllegalArgumentException 当页码超出范围时
     */
    public Bitmap renderPage(int pageIndex, int width, int height) {
        if (renderer == null) {
            throw new IllegalStateException("PDF渲染器未初始化或已关闭");
        }
        
        if (pageIndex < 0 || pageIndex >= pageCount) {
            throw new IllegalArgumentException("页码超出范围: " + pageIndex);
        }
        
        // 打开指定页面
        Page page = renderer.openPage(pageIndex);
        
        // 计算适当的缩放比例以适应目标尺寸
        float pageWidth = page.getWidth();
        float pageHeight = page.getHeight();
        float scale = Math.min((float) width / pageWidth, (float) height / pageHeight);
        
        // 创建Bitmap用于渲染
        int scaledWidth = Math.round(pageWidth * scale);
        int scaledHeight = Math.round(pageHeight * scale);
        Bitmap bitmap = Bitmap.createBitmap(scaledWidth, scaledHeight, Bitmap.Config.ARGB_8888);
        
        // 渲染页面到Bitmap
        page.render(bitmap, null, null, Page.RENDER_MODE_FOR_DISPLAY);
        page.close();
        
        return bitmap;
    }
    
    /**
     * 关闭PDF渲染器并释放资源
     */
    @Override
    public void close() {
        if (renderer != null) {
            renderer.close();
            renderer = null;
        }
        
        if (fileDescriptor != null) {
            try {
                fileDescriptor.close();
            } catch (IOException e) {
                Log.e(TAG, "关闭文件描述符失败", e);
            }
            fileDescriptor = null;
        }
    }
} 