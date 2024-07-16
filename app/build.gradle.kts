import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id("kotlin-kapt")
}

// 1. Properties 객체 생성 방식
val properties = Properties().apply {
    load(FileInputStream(rootProject.file("local.properties")))
}

android {
    namespace = "com.example.flo"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.flo"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Properties 객체에서 API_KEY 가져오기
        buildConfigField("String", "KAKAO_NATIVE_APP_KEY", "${properties.getProperty("kakao_native_app_key")}")
        resValue("string", "kakao_oauth_host", "${properties.getProperty("kakao_oauth_host")}")
        resValue("string", "kakao_native_app_key", "${properties.getProperty("kakao_native_app_key")}")
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    viewBinding{
        enable = true
    }
    buildFeatures {
        buildConfig = true
    }
}
dependencies {
    implementation("me.relex:circleindicator:2.1.6")  //circleindicator
    implementation("androidx.viewpager2:viewpager2:1.0.0")    // Splash
    implementation ("androidx.core:core-splashscreen:1.0.1")
    implementation("com.google.code.gson:gson:2.8.7")    //GSON
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    //roomDb
    implementation("androidx.room:room-ktx:2.4.1")
    implementation("androidx.room:room-runtime:2.4.1")
//    implementation("androidx.room:room-compiler:2.4.1")
    kapt("androidx.room:room-compiler:2.4.1")

    //Retrofit
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("com.squareup.retrofit2:adapter-rxjava2:2.9.0")

    //okHttp
    implementation ("com.squareup.okhttp3:okhttp:4.9.0")
    implementation ("com.squareup.okhttp3:logging-interceptor:4.9.0")

    //Glide
    implementation ("com.github.bumptech.glide:glide:4.11.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.11.0")

    implementation ("com.kakao.sdk:v2-user:2.20.1") // 카카오 로그인 API 모듈
}