package com.example.flo.ui.main.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.flo.databinding.FragmentPanelBinding

class PanelFragment(val imgRes : Int, val text : String) : Fragment() {
    lateinit var binding : FragmentPanelBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPanelBinding.inflate(inflater,container,false)

        binding.homePannelBackgroundIv.setImageResource(imgRes)//인자값으로 받은 imgRes으로 bannerImageIv의 src값이 변경됨
        binding.homePannelTitleTv.text = text
        return binding.root
    }
}