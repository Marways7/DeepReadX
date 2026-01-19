plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.deepreadx"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.deepreadx"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val deepseekApiKey = System.getenv("DEEPSEEK_API_KEY") ?: ""
        val deepseekBaseUrl = System.getenv("DEEPSEEK_BASE_URL") ?: "https://api.deepseek.com"
        buildConfigField("String", "DEEPSEEK_API_KEY", "\"$deepseekApiKey\"")
        buildConfigField("String", "DEEPSEEK_BASE_URL", "\"$deepseekBaseUrl\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // ML Kit Text Recognition (OCR)
    implementation("com.google.mlkit:text-recognition:16.0.0")
    implementation("com.google.mlkit:text-recognition-chinese:16.0.0") // 中文支持

    // OkHttp - HTTP客户端
    implementation("com.squareup.okhttp3:okhttp:4.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")
    
    // DrawerLayout - 侧边栏
    implementation("androidx.drawerlayout:drawerlayout:1.2.0")
    
    // MaterialComponents 支持
    implementation("com.google.android.material:material:1.10.0")

    // ConstraintLayout 支持
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // AppCompat 支持
    implementation("androidx.appcompat:appcompat:1.6.1")

    // RecyclerView 支持
    implementation("androidx.recyclerview:recyclerview:1.3.2")

    // CoordinatorLayout 支持
    implementation("androidx.coordinatorlayout:coordinatorlayout:1.2.0")
}
