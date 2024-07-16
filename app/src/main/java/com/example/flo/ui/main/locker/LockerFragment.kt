package com.example.flo.ui.main.locker

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.flo.ui.login.LoginActivity
import com.example.flo.databinding.FragmentLockerBinding
import com.example.flo.ui.main.MainActivity
import com.google.android.material.tabs.TabLayoutMediator

class LockerFragment : Fragment() {

    lateinit var binding: FragmentLockerBinding

    private val information = arrayListOf("저장한곡", "음악파일", "저장앨범")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLockerBinding.inflate(inflater, container, false)

        val lockerAdapter = LockerVPAdapter(this)
        binding.lockerContentVp.adapter = lockerAdapter
        TabLayoutMediator(binding.lockerContentTb, binding.lockerContentVp) {//TabLayout와 Vp를 연결하는 중재자
                tab, position ->
            tab.text = information[position]
        }.attach()

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        initViews()
    }
    private fun getJwt(): Int{
        val spf = activity?.getSharedPreferences("auth", AppCompatActivity.MODE_PRIVATE)//activity?는 fragment에서 생성할 때 필요
        return spf!!.getInt("jwt", 0)
    }
    private fun getKakaoToken() : String?{
        val spf = activity?.getSharedPreferences("auth2", AppCompatActivity.MODE_PRIVATE)//activity?는 fragment에서 생성할 때 필요
        return spf!!.getString("token", "fail")
    }
    private fun initViews(){
        val jwt:Int = getJwt()
        val kakaoToken:String = getKakaoToken()!!

        if(jwt == 0 || kakaoToken == "fail"){
            Log.i("LockerFragment", "${kakaoToken}")
            binding.loginBtn.text = "로그인"
            binding.loginBtn.setOnClickListener{
                startActivity(Intent(activity, LoginActivity::class.java))
            }
        }else{
            binding.loginBtn.text = "로그아웃"
            binding.loginBtn.setOnClickListener {
                //로그아웃 진행
                logout()
                startActivity(Intent(activity, MainActivity::class.java))
            }
        }
    }
    private fun  logout(){
        val spf = activity?.getSharedPreferences("auth", AppCompatActivity.MODE_PRIVATE)//activity?는 fragment에서 생성할 때 필요
        val editor = spf!!.edit()
        editor.remove("jwt")//jwt key값에 저장된 값을 없애준다.
        editor.apply()
    }
}