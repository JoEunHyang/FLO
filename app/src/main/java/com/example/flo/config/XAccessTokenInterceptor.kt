package com.example.flo.config

import com.example.flo.ApplicationClass.Companion.X_ACCESS_TOKEN
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

//api요청시 마다 sharedpreference에 저장되어있는 jwt를 가져와서 헤더에 넣어주는 작업.
class XAccessTokenInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val builder : Request.Builder = chain.request().newBuilder()

//        val jwtToken: String = getJwt()
//
//        jwtToken?.let {
//            builder.addHeader(X_ACCESS_TOKEN, jwtToken)
//        }
        return chain.proceed(builder.build())
    }
}