package com.example.flo.ui.main.home

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.flo.R
import com.example.flo.data.local.SongDatabase
import com.example.flo.data.entities.Album
import com.example.flo.databinding.FragmentHomeBinding
import com.example.flo.ui.main.MainActivity
import com.example.flo.ui.main.album.AlbumFragment
import com.google.gson.Gson
import java.util.Timer
import kotlin.concurrent.scheduleAtFixedRate

class HomeFragment : Fragment() {

    lateinit var binding: FragmentHomeBinding

    private val timer = Timer()
    private val handler = Handler(Looper.getMainLooper())

    private var albumDatas = ArrayList<Album>()
    private lateinit var songDB : SongDatabase

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        songDB = SongDatabase.getInstance(requireContext())!!
        albumDatas.addAll(songDB.albumDao().getAlbums())

        val albumRVAdapter = AlbumRVAdapter(albumDatas)
        binding.homeTodayMucicAlbumRv.adapter = albumRVAdapter
        binding.homeTodayMucicAlbumRv.layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)


//        binding.homeMainMusicImgIv.setOnClickListener{
////            (context as MainActivity).supportFragmentManager.beginTransaction().replace(R.id.main_frm, AlbumFragment()).commitAllowingStateLoss()
//            parentFragmentManager.beginTransaction()
//                .replace(R.id.main_frm ,fragment).commit()
//        }

        albumRVAdapter.setMyItemClickListener(object : AlbumRVAdapter.MyItemClickListener {
            override fun onItemClick(album: Album) {
                changeAlbumFragment(album)
            }

            override fun onPlay(album: Album) {
                (context as MainActivity).onAlbumSelected(album)
            }
//
//            override fun onRemoveAlbum(position: Int) {
//                albumRVAdapter.removeItem(position)
//            }
        })

        val bannerAdapter = BannerVPAdapter(this)
        bannerAdapter.addFragment(BannerFragment(R.drawable.img_home_viewpager_exp))
        bannerAdapter.addFragment(BannerFragment(R.drawable.img_home_viewpager_exp2))
        binding.homeBannerVp.adapter = bannerAdapter//adapter 연결
        binding.homeBannerVp.orientation = ViewPager2.ORIENTATION_HORIZONTAL//vp가 좌우로 스크롤 될 수 있도록 함

        val panelAdapter = PanelVPAdapter(this)
        panelAdapter.addFragment(PanelFragment(R.drawable.img_first_album_default, "매혹적인 음색의 여성 보컬\n팝"))
        panelAdapter.addFragment(PanelFragment(R.drawable.img_album_exp4, "달밤의 감성 산책"))
        panelAdapter.addFragment(PanelFragment(R.drawable.img_album_exp, "매혹적인 음색의 여성 보컬\n팝"))
        binding.homePannelBackgroundVp.adapter = panelAdapter
        binding.homePannelBackgroundVp.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        // Indicator에 viewPager 설정
        binding.homePannelIndicator.setViewPager(binding.homePannelBackgroundVp)

        startAutoSlide(panelAdapter)

        return binding.root
    }
    private fun changeAlbumFragment(album: Album) {
        (context as MainActivity).supportFragmentManager.beginTransaction()
            .replace(R.id.main_frm, AlbumFragment().apply {
                arguments = Bundle().apply {
                    val gson = Gson()
                    val albmJson = gson.toJson(album)
                    putString("album", albmJson)
                }
            })
            .commitAllowingStateLoss()
        //                (context as MainActivity).supportFragmentManager.beginTransaction()
//                    .replace(R.id.main_frm, AlbumFragment())
//                    .commitAllowingStateLoss()
//                parentFragmentManager.beginTransaction()
//                    .replace(R.id.main_frm, fragment).commit()
    }

    private fun startAutoSlide(adpater : PanelVPAdapter) {
        // 일정 간격으로 슬라이드 변경 (3초마다)
        timer.scheduleAtFixedRate(3000, 3000) {
            handler.post {
                val nextItem = binding.homePannelBackgroundVp.currentItem + 1
                if (nextItem < adpater.itemCount) {
                    binding.homePannelBackgroundVp.currentItem = nextItem
                } else {
                    binding.homePannelBackgroundVp.currentItem = 0 // 마지막 페이지에서 첫 페이지로 순환
                }
            }
        }
    }
}