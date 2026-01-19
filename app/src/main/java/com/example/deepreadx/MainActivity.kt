package com.example.deepreadx

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import com.deepreadx.viewer.PdfViewerActivity

/**
 * 主Activity，负责启动PDF查看器
 * 
 * @author DeepReadX团队
 * @created 2023-05-17
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 直接启动PDF查看器Activity
        startActivity(Intent(this, PdfViewerActivity::class.java))
        finish()
    }
}