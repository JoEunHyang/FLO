package com.example.flo

import android.app.Application
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.v2.auth.BuildConfig

class GlobalApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // 다른 초기화 코드들

        // Kakao SDK 초기화

        KakaoSdk.init(this, com.example.flo.BuildConfig.KAKAO_NATIVE_APP_KEY)
    }
}