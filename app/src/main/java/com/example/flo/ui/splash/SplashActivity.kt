package com.example.flo.ui.splash

import android.os.Handler
import android.os.Looper
import com.example.flo.data.remote.auth.AuthService
import com.example.flo.databinding.ActivitySplashBinding
import com.example.flo.ui.BaseActivity
import com.example.flo.ui.login.LoginActivity
import com.example.flo.ui.main.MainActivity

class SplashActivity: BaseActivity<ActivitySplashBinding>(ActivitySplashBinding::inflate), SplashView {

    override fun initAfterBinding() {
        Handler(Looper.getMainLooper()).postDelayed({
            autoLogin()
        }, 2000)
    }

    private fun autoLogin() {
//        AuthService.autoLogin(this)
    }

//    override fun onAutoLoginLoading() {
//
//    }
//
//    override fun onAutoLoginSuccess() {
//        startActivityWithClear(MainActivity::class.java)
//
//    }
//
//    override fun onAutoLoginFailure(code: Int, message: String) {
//        startActivityWithClear(LoginActivity::class.java)
//
//    }
}



