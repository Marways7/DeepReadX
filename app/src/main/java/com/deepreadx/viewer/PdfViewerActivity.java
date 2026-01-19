package com.deepreadx.viewer;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.deepreadx.api.DeepSeekClient;
import com.deepreadx.api.DeepSeekClient.ApiCallback;
import com.deepreadx.dao.ExplanationHistoryDao;
import com.deepreadx.dao.StyleDao;
import com.deepreadx.dao.StyleDaoImpl;
import com.deepreadx.dao.impl.ExplanationHistoryDaoImpl;
import com.deepreadx.model.ExplainStyle;
import com.deepreadx.model.ExplanationHistory;
import com.deepreadx.ocr.OcrProcessor;
import com.deepreadx.ocr.OcrProcessor.OcrCallback;
import com.deepreadx.ui.HistoryActivity;
import com.deepreadx.ui.StyleManagerActivity;
import com.example.deepreadx.BuildConfig;
import com.example.deepreadx.R;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.util.List;

/**
 * PDF文档查看器Activity，提供PDF阅读与AI解释功能
 * 
 * @author DeepReadX团队
 * @created 2025-05-18
 */
public class PdfViewerActivity extends AppCompatActivity {
    private static final String TAG = "PdfViewerActivity";
    private static final String EXTRA_PDF_URI = "pdf_uri";
    private static final String PREF_LAST_STYLE_ID = "last_style_id";

    private Toolbar toolbar;
    private ImageView pdfImageView;
    private ImageButton btnPrevPage;
    private ImageButton btnNextPage;
    private ImageButton btnExplain;
    private ImageButton btnManageStyles;
    private DrawerLayout drawerLayout;
    private ExplanationFragment explanationFragment;
    
    private PdfRenderer pdfRenderer;
    private Uri pdfUri;
    private int currentPage = 0;
    private int pageCount = 0;
    
    private OcrProcessor ocrProcessor;
    private DeepSeekClient deepSeekClient;
    private StyleDao styleDao;
    private ExplainStyle currentStyle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_viewer);
        
        // 初始化DAO
        styleDao = new StyleDaoImpl(this);
        
        // 初始化OCR处理器和DeepSeek客户端
        initServices();
        
        // 初始化视图
        initViews();
        
        // 初始化解释面板Fragment
        initExplanationFragment();
        
        // 设置Toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("PDF查看器");
        
        // 获取PDF URI
        pdfUri = getIntent().getParcelableExtra(EXTRA_PDF_URI);
        if (pdfUri == null) {
            // 如果未传入PDF URI，则调用文件选择器
            openPdfPicker();
        } else {
            // 初始化PDF渲染器并渲染第一页
            openPdf(pdfUri);
        }
    }
    
    /**
     * 打开PDF文件选择器
     */
    private void openPdfPicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/pdf");
        startActivityForResult(intent, REQUEST_CODE_OPEN_PDF);
    }
    
    private static final int REQUEST_CODE_OPEN_PDF = 1;
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == REQUEST_CODE_OPEN_PDF && resultCode == RESULT_OK) {
            if (data != null) {
                pdfUri = data.getData();
                
                // 对选定的URI授予持久性权限
                final int takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION;
                getContentResolver().takePersistableUriPermission(pdfUri, takeFlags);
                
                // 打开选定的PDF
                openPdf(pdfUri);
            }
        } else if (requestCode == REQUEST_CODE_OPEN_PDF && resultCode == RESULT_CANCELED) {
            // 用户取消了文件选择，如果是首次启动且没有传入URI，则关闭Activity
            if (pdfUri == null) {
                Toast.makeText(this, "请选择PDF文件", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // 加载当前风格
        loadCurrentStyle();
    }
    
    /**
     * 初始化解释面板Fragment
     */
    private void initExplanationFragment() {
        drawerLayout = findViewById(R.id.drawerLayout);
        
        // 创建并添加Fragment
        explanationFragment = ExplanationFragment.newInstance(drawerLayout);
        explanationFragment.setDrawerLayout(drawerLayout);
        
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.drawerExplanation, explanationFragment);
        transaction.commit();
    }
    
    /**
     * 加载当前选择的风格
     */
    private void loadCurrentStyle() {
        // 从SharedPreferences获取上次选择的风格ID
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        int lastStyleId = prefs.getInt(PREF_LAST_STYLE_ID, -1);
        
        if (lastStyleId != -1) {
            // 根据ID获取风格
            currentStyle = styleDao.queryById(lastStyleId);
        }
        
        // 如果没有保存的风格或获取失败，使用默认风格
        if (currentStyle == null) {
            currentStyle = styleDao.getDefaultStyle();
        }
        
        // 更新Toolbar副标题显示当前风格
        if (currentStyle != null) {
            getSupportActionBar().setSubtitle("风格：" + currentStyle.getName());
        }
    }
    
    /**
     * 设置当前风格
     * 
     * @param style 要设置的风格
     */
    private void setCurrentStyle(ExplainStyle style) {
        currentStyle = style;
        
        // 保存到SharedPreferences
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.edit().putInt(PREF_LAST_STYLE_ID, style.getId()).apply();
        
        // 更新UI
        getSupportActionBar().setSubtitle("风格：" + style.getName());
        Toast.makeText(this, "已切换到\"" + style.getName() + "\"风格", Toast.LENGTH_SHORT).show();
    }
    
    /**
     * 初始化服务
     */
    private void initServices() {
        // 初始化OCR处理器
        ocrProcessor = new OcrProcessor(this);
        
        // 从配置中获取API密钥和URL
        String apiKey = BuildConfig.DEEPSEEK_API_KEY;
        String baseUrl = BuildConfig.DEEPSEEK_BASE_URL;
        if (baseUrl == null || baseUrl.isEmpty()) {
            baseUrl = "https://api.deepseek.com";
        }
        if (apiKey == null || apiKey.isEmpty()) {
            Log.w(TAG, "未配置DeepSeek API Key，请在环境变量中设置DEEPSEEK_API_KEY");
        }
        
        // 初始化DeepSeek客户端
        deepSeekClient = new DeepSeekClient(apiKey, baseUrl);
    }
    
    /**
     * 初始化视图组件
     */
    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        pdfImageView = findViewById(R.id.pdfImageView);
        btnPrevPage = findViewById(R.id.btnPrevPage);
        btnNextPage = findViewById(R.id.btnNextPage);
        btnExplain = findViewById(R.id.btnExplain);
        btnManageStyles = findViewById(R.id.btnManageStyles);
        
        // 设置页面导航按钮点击事件
        btnPrevPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentPage > 0) {
                    currentPage--;
                    renderPage(currentPage);
                }
            }
        });
        
        btnNextPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentPage < pageCount - 1) {
                    currentPage++;
                    renderPage(currentPage);
                }
            }
        });
        
        // 设置AI解释按钮点击事件
        btnExplain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processCurrentPageForExplanation();
            }
        });
        
        // 设置管理风格按钮点击事件
        btnManageStyles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openStyleManager();
            }
        });
    }
    
    /**
     * 打开风格管理页面
     */
    private void openStyleManager() {
        Intent intent = new Intent(this, StyleManagerActivity.class);
        startActivity(intent);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_pdf_viewer, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        
        if (id == android.R.id.home) {
            finish();
            return true;
        } else if (id == R.id.action_select_style) {
            showStyleSelectionDialog();
            return true;
        } else if (id == R.id.action_manage_styles) {
            openStyleManager();
            return true;
        } else if (id == R.id.action_history) {
            // 打开历史记录界面
            Intent intent = new Intent(this, HistoryActivity.class);
            startActivity(intent);
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }
    
    /**
     * 显示风格选择对话框
     */
    private void showStyleSelectionDialog() {
        // 获取所有风格
        List<ExplainStyle> styles = styleDao.queryAll();
        
        if (styles.isEmpty()) {
            Toast.makeText(this, "没有可用的风格", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // 构建风格名称列表和当前选中项
        String[] styleNames = new String[styles.size()];
        int checkedItem = 0;
        
        for (int i = 0; i < styles.size(); i++) {
            ExplainStyle style = styles.get(i);
            styleNames[i] = style.getName();
            
            if (currentStyle != null && currentStyle.getId() == style.getId()) {
                checkedItem = i;
            }
        }
        
        // 创建并显示对话框
        new AlertDialog.Builder(this)
                .setTitle("选择讲解风格")
                .setSingleChoiceItems(styleNames, checkedItem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setCurrentStyle(styles.get(which));
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }
    
    /**
     * 处理当前页面以获取AI解释
     */
    private void processCurrentPageForExplanation() {
        // 确保已选择风格
        if (currentStyle == null) {
            currentStyle = styleDao.getDefaultStyle();
            if (currentStyle == null) {
                Toast.makeText(this, "没有可用的解释风格", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        
        // 获取当前显示的PDF页面图像
        Bitmap pageBitmap = getCurrentPageBitmap();
        if (pageBitmap == null) {
            Toast.makeText(this, "无法获取当前页面图像", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // 显示加载提示
        Toast.makeText(this, "正在进行OCR识别...", Toast.LENGTH_SHORT).show();
        
        // 使用OCR处理器提取文本
        ocrProcessor.extractText(pageBitmap, new OcrCallback() {
            @Override
            public void onSuccess(String text) {
                if (text.isEmpty()) {
                    runOnUiThread(() -> Toast.makeText(PdfViewerActivity.this, 
                            "未能识别到文字", Toast.LENGTH_SHORT).show());
                    return;
                }
                
                // 显示加载提示
                runOnUiThread(() -> Toast.makeText(PdfViewerActivity.this, 
                        "正在生成AI解释...", Toast.LENGTH_SHORT).show());
                
                // 构建提示词
                String prompt = currentStyle.getPromptTemplate().replace("{text}", text);
                String styleTemplate = currentStyle.getPromptTemplate();
                
                // 请求DeepSeek API生成解释
                deepSeekClient.requestExplanation(text, styleTemplate, new ApiCallback() {
                    @Override
                    public void onResponse(String explanation) {
                        runOnUiThread(() -> {
                            // 显示解释内容
                            showExplanation(explanation);
                            
                            // 保存解释结果到历史
                            ExplanationHistoryDao historyDao = new ExplanationHistoryDaoImpl(PdfViewerActivity.this);
                            ExplanationHistory history = new ExplanationHistory(
                                    pdfUri.toString(),
                                    currentPage,
                                    currentStyle.getId(),
                                    explanation
                            );
                            historyDao.insert(history);
                        });
                    }
                    
                    @Override
                    public void onError(Exception e) {
                        runOnUiThread(() -> {
                            // 创建带有重试按钮的Snackbar
                            Snackbar snackbar = Snackbar.make(
                                    pdfImageView,
                                    "生成解释失败：" + e.getMessage() + "。请检查网络或稍后重试。",
                                    Snackbar.LENGTH_LONG);
                                    
                            // 添加重试按钮
                            snackbar.setAction("重试", v -> {
                                // 重新执行OCR识别和解释
                                processCurrentPageForExplanation();
                            });
                            
                            // 显示Snackbar
                            snackbar.show();
                            
                            Log.e(TAG, "生成解释失败", e);
                        });
                    }
                });
            }
            
            @Override
            public void onFailure(Exception e) {
                runOnUiThread(() -> Toast.makeText(PdfViewerActivity.this, 
                        "OCR识别失败: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        });
    }
    
    /**
     * 获取当前页面的Bitmap图像
     * 
     * @return 页面图像，或null如果获取失败
     */
    private Bitmap getCurrentPageBitmap() {
        if (pdfImageView.getDrawable() instanceof BitmapDrawable) {
            return ((BitmapDrawable) pdfImageView.getDrawable()).getBitmap();
        } else {
            // 如果ImageView中不是BitmapDrawable，尝试从视图创建Bitmap
            Bitmap bitmap = Bitmap.createBitmap(
                    pdfImageView.getWidth(), 
                    pdfImageView.getHeight(), 
                    Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            pdfImageView.draw(canvas);
            return bitmap;
        }
    }
    
    /**
     * 显示解释内容到侧边栏
     * 
     * @param explanation 解释文本
     */
    private void showExplanation(String explanation) {
        // 格式化解释文本（添加HTML标签）
        String formattedExplanation = formatExplanation(explanation);
        
        // 更新Fragment中的内容
        explanationFragment.updateExplanation(formattedExplanation);
        
        // 打开侧边栏
        drawerLayout.openDrawer(GravityCompat.END);
    }
    
    /**
     * 格式化解释文本，添加HTML标记
     * 
     * @param explanation 原始解释文本
     * @return 格式化后的HTML文本
     */
    private String formatExplanation(String explanation) {
        // 替换换行符为HTML段落
        String formatted = explanation.replace("\n\n", "</p><p>")
                .replace("\n", "<br>");
        
        // 添加基本HTML结构
        return "<p>" + formatted + "</p>";
    }
    
    /**
     * 打开PDF文件
     *
     * @param uri PDF文件的URI
     */
    private void openPdf(Uri uri) {
        try {
            pdfRenderer = new PdfRenderer(this, uri);
            pageCount = pdfRenderer.getPageCount();
            
            // 初始显示第一页
            currentPage = 0;
            renderPage(currentPage);
            
            updatePagingButtons();
            
            Log.d(TAG, "PDF已打开，共" + pageCount + "页");
        } catch (IOException e) {
            Log.e(TAG, "打开PDF失败: " + e.getMessage(), e);
            Toast.makeText(this, "无法打开PDF文件: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            finish();
        }
    }
    
    /**
     * 渲染指定页码的PDF页面
     *
     * @param pageIndex 页码索引（从0开始）
     */
    private void renderPage(int pageIndex) {
        if (pdfRenderer == null) {
            return;
        }
        
        try {
            // 获取页面尺寸
            int width = pdfImageView.getWidth();
            int height = pdfImageView.getHeight();
            
            // 如果视图尚未测量，使用屏幕尺寸
            if (width <= 0 || height <= 0) {
                width = getResources().getDisplayMetrics().widthPixels;
                height = getResources().getDisplayMetrics().heightPixels;
            }
            
            // 渲染页面到Bitmap
            Bitmap pageBitmap = pdfRenderer.renderPage(pageIndex, width, height);
            pdfImageView.setImageBitmap(pageBitmap);
            
            // 更新标题显示当前页码
            getSupportActionBar().setSubtitle("第 " + (pageIndex + 1) + " / " + pageCount + " 页");
            
            // 更新分页按钮状态
            updatePagingButtons();
        } catch (Exception e) {
            Log.e(TAG, "渲染页面失败: " + e.getMessage(), e);
            Toast.makeText(this, "渲染页面失败", Toast.LENGTH_SHORT).show();
        }
    }
    
    /**
     * 更新分页按钮的可用状态
     */
    private void updatePagingButtons() {
        btnPrevPage.setEnabled(currentPage > 0);
        btnPrevPage.setAlpha(currentPage > 0 ? 1.0f : 0.5f);
        
        btnNextPage.setEnabled(currentPage < pageCount - 1);
        btnNextPage.setAlpha(currentPage < pageCount - 1 ? 1.0f : 0.5f);
    }
    
    /**
     * 关闭PDF渲染器并释放资源
     */
    private void closeRenderer() {
        if (pdfRenderer != null) {
            pdfRenderer.close();
            pdfRenderer = null;
        }
    }
    
    /**
     * 启动OCR识别选定区域
     * 
     * TODO: 实现长按选择区域功能
     * TODO: 调用OCR识别API
     * TODO: 显示识别结果并允许编辑
     */
    private void startTextRecognition() {
        // TODO: 实现文本识别逻辑
    }
    
    /**
     * 使用DeepSeek API生成解释
     * 
     * TODO: 发送文本到DeepSeek API
     * TODO: 接收并显示解释结果
     * 
     * @param text 需要解释的文本
     */
    private void generateExplanation(String text) {
        // TODO: 实现解释生成逻辑
    }
    
    @Override
    protected void onDestroy() {
        closeRenderer();
        super.onDestroy();
    }
} 
