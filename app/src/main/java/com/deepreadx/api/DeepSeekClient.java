package com.deepreadx.api;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * DeepSeek API客户端，负责与DeepSeek服务通信并获取AI解释结果
 * 
 * @author DeepReadX团队
 * @created 2025-05-18
 */
public class DeepSeekClient {
    private static final String TAG = "DeepSeekClient";
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final int TIMEOUT_SECONDS = 60;
    
    private final String apiKey;
    private final String baseUrl;
    private final OkHttpClient client;
    
    /**
     * API请求回调接口
     */
    public interface ApiCallback {
        /**
         * 成功接收到响应内容时回调
         * 
         * @param explanation AI生成的解释内容
         */
        void onResponse(String explanation);
        
        /**
         * 请求发生错误时回调
         * 
         * @param e 异常信息
         */
        void onError(Exception e);
    }
    
    /**
     * 构造函数
     * 
     * @param apiKey DeepSeek API密钥
     * @param baseUrl API基础URL
     */
    public DeepSeekClient(String apiKey, String baseUrl) {
        this.apiKey = apiKey;
        this.baseUrl = baseUrl;
        
        // 使用createOkHttpClient方法创建OkHttpClient实例
        this.client = createOkHttpClient();
    }
    
    /**
     * 请求AI解释
     * 
     * @param text 需要解释的文本
     * @param style 解释风格，如"简明易懂"、"学术分析"等
     * @param callback 请求回调
     */
    public void requestExplanation(String text, String style, final ApiCallback callback) {
        if (text == null || text.isEmpty()) {
            callback.onError(new IllegalArgumentException("文本不能为空"));
            return;
        }
        
        try {
            // 构建请求体
            JSONObject requestJson = buildRequestJson(text, style);
            String jsonBody = requestJson.toString();
            // 打印完整请求JSON，便于调试
            Log.d(TAG, "请求JSON: " + jsonBody);
            
            RequestBody requestBody = RequestBody.create(JSON, jsonBody);
            
            // 构建请求
            Request request = new Request.Builder()
                    .url(baseUrl + "/v1/chat/completions")
                    .addHeader("Authorization", "Bearer " + apiKey)
                    .addHeader("Content-Type", "application/json; charset=utf-8")
                    .post(requestBody)
                    .build();
            
            // 发送异步请求
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e(TAG, "请求失败", e);
                    callback.onError(e);
                }
                
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try {
                        // 捕获4xx响应
                        if (response.code() >= 400) {
                            String errBody = response.body() != null ? response.body().string() : "";
                            Log.e(TAG, "API错误: HTTP " + response.code() + " - " + errBody);
                            callback.onError(new Exception("HTTP " + response.code() + ": " + errBody));
                            return;
                        }
                        
                        try (ResponseBody responseBody = response.body()) {
                            if (!response.isSuccessful()) {
                                callback.onError(new IOException("请求失败，状态码: " + response.code()));
                                return;
                            }
                            
                            if (responseBody == null) {
                                callback.onError(new IOException("响应为空"));
                                return;
                            }
                            
                            String responseJson = responseBody.string();
                            String explanation = parseResponse(responseJson);
                            callback.onResponse(explanation);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "处理响应失败", e);
                        callback.onError(e);
                    }
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "构建请求失败", e);
            callback.onError(e);
        }
    }
    
    /**
     * 构建请求JSON
     * 
     * @param text 要解释的文本
     * @param style 解释风格
     * @return JSON对象
     * @throws JSONException 如果JSON构建失败
     */
    private JSONObject buildRequestJson(String text, String style) throws JSONException {
        // 构建系统消息
        JSONObject systemMessageObj = new JSONObject();
        systemMessageObj.put("role", "system");
        systemMessageObj.put("content", style);
        
        // 构建用户消息
        JSONObject userMessageObj = new JSONObject();
        userMessageObj.put("role", "user");
        userMessageObj.put("content", text);
        
        // 创建消息数组
        JSONArray messagesArray = new JSONArray();
        messagesArray.put(systemMessageObj);
        messagesArray.put(userMessageObj);
        
        // 创建主请求对象
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("model", "deepseek-chat");
        jsonObject.put("temperature", 0.7);
        jsonObject.put("max_tokens", 1000);
        jsonObject.put("messages", messagesArray);
        jsonObject.put("stream", false);
        
        return jsonObject;
    }
    
    /**
     * 解析API响应
     * 
     * @param responseJson 响应JSON字符串
     * @return 提取的解释文本
     * @throws JSONException 如果解析失败
     */
    private String parseResponse(String responseJson) throws JSONException {
        JSONObject jsonObject = new JSONObject(responseJson);
        JSONObject choicesObject = jsonObject.getJSONArray("choices").getJSONObject(0);
        JSONObject messageObject = choicesObject.getJSONObject("message");
        return messageObject.getString("content");
    }

    /**
     * 创建OkHttpClient实例
     * 该方法被设计成可被子类覆盖，便于单元测试
     * 
     * @return 配置好的OkHttpClient
     */
    OkHttpClient createOkHttpClient() {
        // 创建日志拦截器
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        
        // 创建OkHttpClient实例，设置超时时间和日志拦截器
        return new OkHttpClient.Builder()
                .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .addInterceptor(loggingInterceptor) // 添加日志拦截器
                .build();
    }
} 