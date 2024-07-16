package com.example.flo.ui.main.look

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.flo.ui.main.locker.SongRVAdapter
import com.example.flo.data.remote.song.FloChartResult
import com.example.flo.data.remote.song.SongService
import com.example.flo.databinding.FragmentLookBinding


class LookFragment : Fragment(), LookView {
    private lateinit var binding: FragmentLookBinding
    private lateinit var floCharAdapter: SongRVAdapter
//    private lateinit var floCharAdapter: SavedSongRVAdapter
//    lateinit var songDB: SongDatabase

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLookBinding.inflate(inflater, container, false)

//        songDB = SongDatabase.getInstance(requireContext())!!

        return binding.root
    }
//    override fun onStart() {
//        super.onStart()
//        initRecyclerView()
//    }
//    private fun initRecyclerView(){
//        binding.lookFloChartRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
//
//        floCharAdapter = SavedSongRVAdapter()
//
//        floCharAdapter.setMyItemClickListener(object : SavedSongRVAdapter.MyItemClickListener{
//            override fun onRemoveSong(songId: Int) {
//            }
//        })
//
//
//        binding.lookFloChartRv.adapter = floCharAdapter
//
//        floCharAdapter.addSongs(songDB.songDao().getSongs() as ArrayList<Song>)
//    }

    override fun onStart() {
        super.onStart()
        getSongs()
    }

    private fun initRecyclerView(result: FloChartResult) {
        floCharAdapter = SongRVAdapter(requireContext(), result)

        binding.lookFloChartRv.adapter = floCharAdapter
    }

    private fun getSongs() {
        val songService = SongService()
        songService.setLookView(this)

        songService.getSongs()
    }

    override fun onGetSongLoading() {
//        binding.lookLoadingPb.visibility = View.VISIBLE
    }

    override fun onGetSongSuccess(code: Int, result: FloChartResult) {
//        binding.lookLoadingPb.visibility = View.GONE
        initRecyclerView(result)
    }

    override fun onGetSongFailure(code: Int, message: String) {
//        binding.lookLoadingPb.visibility = View.GONE
        Log.d("LOOK-FRAG/SONG-RESPONSE", message)
    }
}