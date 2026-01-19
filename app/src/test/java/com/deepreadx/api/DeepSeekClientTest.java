package com.deepreadx.api;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * DeepSeekClient单元测试类
 * 
 * @author DeepReadX团队
 */
@RunWith(MockitoJUnitRunner.class)
public class DeepSeekClientTest {

    private static final String TEST_API_KEY = "test-api-key";
    private static final String TEST_BASE_URL = "https://api.deepseek.com";
    private static final String TEST_TEXT = "测试文本内容";
    private static final String TEST_STYLE = "简明易懂";

    @Mock
    private OkHttpClient mockOkHttpClient;

    @Mock
    private Call mockCall;

    @Mock
    private DeepSeekClient.ApiCallback mockCallback;

    private DeepSeekClient deepSeekClient;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        
        // 创建测试实例，注入mock的OkHttpClient
        deepSeekClient = new DeepSeekClient(TEST_API_KEY, TEST_BASE_URL) {
            @Override
            OkHttpClient createOkHttpClient() {
                return mockOkHttpClient;
            }
        };
        
        // 配置mock行为
        when(mockOkHttpClient.newCall(any())).thenReturn(mockCall);
    }

    @Test
    public void requestExplanation_正常响应_回调onResponse() throws Exception {
        // 准备模拟响应数据
        String successResponseJson = "{"
                + "\"choices\": [{"
                + "    \"message\": {"
                + "        \"content\": \"这是测试解释内容\""
                + "    }"
                + "}]"
                + "}";
                
        Response successResponse = new Response.Builder()
                .request(new Request.Builder().url(TEST_BASE_URL).build())
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .message("OK")
                .body(ResponseBody.create(
                        MediaType.parse("application/json"), 
                        successResponseJson))
                .build();
        
        // 配置mock行为，模拟请求成功
        ArgumentCaptor<Callback> callbackCaptor = ArgumentCaptor.forClass(Callback.class);
        doAnswer(invocation -> {
            Callback callback = callbackCaptor.getValue();
            callback.onResponse(mockCall, successResponse);
            return null;
        }).when(mockCall).enqueue(callbackCaptor.capture());
        
        // 执行测试
        deepSeekClient.requestExplanation(TEST_TEXT, TEST_STYLE, mockCallback);
        
        // 验证结果
        verify(mockCallback).onResponse("这是测试解释内容");
    }

    @Test
    public void requestExplanation_422错误_回调onError() throws Exception {
        // 准备模拟422错误响应
        String errorResponseJson = "{"
                + "\"error\": {"
                + "    \"message\": \"请求参数格式错误\","
                + "    \"code\": \"invalid_request\""
                + "}"
                + "}";
                
        Response errorResponse = new Response.Builder()
                .request(new Request.Builder().url(TEST_BASE_URL).build())
                .protocol(Protocol.HTTP_1_1)
                .code(422)
                .message("Unprocessable Entity")
                .body(ResponseBody.create(
                        MediaType.parse("application/json"), 
                        errorResponseJson))
                .build();
        
        // 配置mock行为，模拟422错误
        ArgumentCaptor<Callback> callbackCaptor = ArgumentCaptor.forClass(Callback.class);
        doAnswer(invocation -> {
            Callback callback = callbackCaptor.getValue();
            callback.onResponse(mockCall, errorResponse);
            return null;
        }).when(mockCall).enqueue(callbackCaptor.capture());
        
        // 执行测试
        deepSeekClient.requestExplanation(TEST_TEXT, TEST_STYLE, mockCallback);
        
        // 验证结果
        ArgumentCaptor<Exception> exceptionCaptor = ArgumentCaptor.forClass(Exception.class);
        verify(mockCallback).onError(exceptionCaptor.capture());
        
        Exception capturedException = exceptionCaptor.getValue();
        assertTrue(capturedException.getMessage().contains("HTTP 422"));
        assertTrue(capturedException.getMessage().contains("请求参数格式错误"));
    }
    
    @Test
    public void requestExplanation_网络失败_回调onError() throws Exception {
        // 配置mock行为，模拟网络失败
        IOException networkException = new IOException("网络连接失败");
        ArgumentCaptor<Callback> callbackCaptor = ArgumentCaptor.forClass(Callback.class);
        doAnswer(invocation -> {
            Callback callback = callbackCaptor.getValue();
            callback.onFailure(mockCall, networkException);
            return null;
        }).when(mockCall).enqueue(callbackCaptor.capture());
        
        // 执行测试
        deepSeekClient.requestExplanation(TEST_TEXT, TEST_STYLE, mockCallback);
        
        // 验证结果
        verify(mockCallback).onError(networkException);
    }
} 
