package com.example.flo.ui.main

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.flo.ui.main.home.HomeFragment
import com.example.flo.ui.main.locker.LockerFragment
import com.example.flo.ui.main.look.LookFragment
import com.example.flo.R
import com.example.flo.ui.main.search.SearchFragment
import com.example.flo.ui.song.SongActivity
import com.example.flo.data.local.SongDatabase
import com.example.flo.data.entities.Album
import com.example.flo.data.entities.Song
import com.example.flo.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayout.TabGravity
import com.google.gson.Gson
import java.security.MessageDigest
import com.kakao.sdk.common.util.Utility

class MainActivity : AppCompatActivity(){
    val TAG = "MainActivity"

    lateinit var binding: ActivityMainBinding

    private var song: Song = Song()
    private val gson: Gson = Gson()
    lateinit var timer: Timer
    private var mediaPlayer: MediaPlayer? = null

    val songs = arrayListOf<Song>()
    lateinit var songDB: SongDatabase
    var nowPos = 0

    companion object { const val STRING_INTENT_KEY = "my_string_key"}

    private val getResultText = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        result -> if(result.resultCode == Activity.RESULT_OK){
            /*val returnString = result.data?.getStringExtra(STRING_INTENT_KEY)
            if(returnString != null) Log.d("returnString",returnString)*/
            val album_title = result.data?.getStringExtra("album_title")
            if(album_title != null) Log.d("album_title", album_title)
            Toast.makeText(this@MainActivity, album_title, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_FLO)
//        // SplashScreen을 적용한다.
//        // setContentView 전에 작성해야 한다.
//        installSplashScreen()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var keyHash = Utility.getKeyHash(this)
        Log.i(TAG, "keyhash : $keyHash")

        deleteDatabase("song-database")
        inputDummyAlbums()
        inputDummySongs()

        initPlayList()
        initBottomNavigation()
        initClickListenter()

        Log.d("MAIN/JWT_TO_SERVER", getJwt().toString())
    }

    private fun getJwt():String?{
        val spf = this.getSharedPreferences("auth2", AppCompatActivity.MODE_PRIVATE)

        return spf!!.getString("jwt", "")
    }

    override fun onStart() {
        Log.d("onStart","onStart")

        super.onStart()

        val spf = getSharedPreferences("song", MODE_PRIVATE)
        val songId = spf.getInt("songId", 0)

        val songDB = SongDatabase.getInstance(this)!!

        song = if (songId == 0){
            songDB.songDao().getSong(1)
        } else{
            songDB.songDao().getSong(songId)
        }

        Log.d("song ID", song.id.toString())

        startTimer()//없으면 안열림.
        setMiniPlayer(song)
    }
    private fun setMiniPlayer(song: Song){
        binding.mainMiniplayerTitleTv.text = song.title
        binding.mainMiniplayerSingerTv.text = song.singer
        binding.mainProgressSb.progress = (song.second * 100000 / song.playTime)
        val music = resources.getIdentifier(song.music, "raw", this.packageName)
        mediaPlayer = MediaPlayer.create(this, music)
//
        setPlayerStatus(song.isPlaying)//이것도 오류
    }
    private fun inputDummySongs(){
        val songDB = SongDatabase.getInstance(this)!!
        val songs = songDB.songDao().getSongs()

        if (songs.isNotEmpty()) return

        songDB.songDao().insert(
            Song(
                "Lilac",
                "아이유 (IU)",
                0,
                200,
                false,
                "music_lilac",
                R.drawable.img_album_exp2,
                false,
                1
            )
        )

        songDB.songDao().insert(
            Song(
                "Flu",
                "아이유 (IU)",
                0,
                200,
                false,
                "music_flu",
                R.drawable.img_album_exp2,
                false,
                1
            )
        )

        songDB.songDao().insert(
            Song(
                "Butter",
                "방탄소년단 (BTS)",
                0,
                190,
                false,
                "music_butter",
                R.drawable.img_album_exp,
                false,
                0
            )
        )

        songDB.songDao().insert(
            Song(
                "Next Level",
                "에스파 (AESPA)",
                0,
                210,
                false,
                "music_next",
                R.drawable.img_album_exp3,
                false,
                2
            )
        )


        songDB.songDao().insert(
            Song(
                "Boy with Luv",
                "music_boy",
                0,
                230,
                false,
                "music_lilac",
                R.drawable.img_album_exp4,
                false,
                3
            )
        )


        songDB.songDao().insert(
            Song(
                "BBoom BBoom",
                "모모랜드 (MOMOLAND)",
                0,
                240,
                false,
                "music_bboom",
                R.drawable.img_album_exp5,
                false,
                4
            )
        )

        val _songs = songDB.songDao().getSongs()
        Log.d("DB데이터", _songs.toString())
    }
    private fun inputDummyAlbums() {
        val songDB = SongDatabase.getInstance(this)!!
        val albums = songDB.albumDao().getAlbums()

        if (albums.isNotEmpty()) return

/*        trackList = arrayListOf(
            Song("Next Level", "에스파(AESPA)", 0, 0, false, "music_flu",1),
            Song("라일락", "아이유(IU)", 0, 0, false, "music_lilac",2),
        )*/

        songDB.albumDao().insert(
            Album(0,
                "Butter",
                "방탄소년단 (BTS)",
                R.drawable.img_album_exp,
            )
        )
        songDB.albumDao().insert(
            Album(1,"Lilac", "아이유 (IU)", R.drawable.img_album_exp2,
            )
        )
        songDB.albumDao().insert(
            Album(2, "Next Level", "에스파 (AESPA)", R.drawable.img_album_exp3,
            )
        )
        songDB.albumDao().insert(
            Album(3, "Boy with Luv", "방탄소년단 (BTS)", R.drawable.img_album_exp4,
            )
        )
        songDB.albumDao().insert(
            Album(4, "BBoom BBoom", " 모모랜드 (MOMOLAND)", R.drawable.img_album_exp5,
            )
        )
        songDB.albumDao().insert(
            Album(5, "Weekend", "태연 (Tae Yeon)", R.drawable.img_album_exp6,
            )
        )
        val _albums = songDB.albumDao().getAlbums()
        Log.d("DB앨범데이터", _albums.toString())
    }

    private fun initPlayList(){
        songDB = SongDatabase.getInstance(this)!!
        songs.addAll(songDB.songDao().getSongs())//songs 초기화
    }

    private fun setPlayerStatus(isPlaying : Boolean){
        song.isPlaying = isPlaying
        timer.isPlaying = isPlaying

        if(isPlaying){
            binding.mainMiniplayerBtn.visibility = View.GONE
            binding.mainPauseBtn.visibility = View.VISIBLE
            mediaPlayer?.start()
        } else {
            binding.mainMiniplayerBtn.visibility = View.VISIBLE
            binding.mainPauseBtn.visibility = View.GONE
            if(mediaPlayer?.isPlaying == true){
                mediaPlayer?.pause()
            }
        }
    }



    override fun onDestroy() {
        super.onDestroy()
        timer.interrupt()
        mediaPlayer?.release()//미디어플레이어가 갖고 있던 리소스 해제
        mediaPlayer = null //미디어 플레이어 해제
    }

    //7주차
    override fun onPause() {
        super.onPause()

        val sharedPreferences = getSharedPreferences("song", MODE_PRIVATE)
        val editor = sharedPreferences.edit() // 에디터

//        editor.putInt("songId", songs[nowPos].id)

        editor.putInt("songId", song.id)
        editor.apply()
    }
    /*6주차 미션 MainActivity <-> Home Fragment7주차도 수정봄*/
    fun onAlbumSelected(album: Album) {
        //albumIdx같은 = 같은 앨범에 속한 수록곡들 추합
        Log.d("클릭한 앨범 id", album.id.toString())
        songDB = SongDatabase.getInstance(this)!!
//        songs.addAll(songDB.songDao().getSongs())//songs 초기화
        val albumSongs = songDB.songDao().getAlbumSongs(album.id)
        songs.clear()
        songs.addAll(albumSongs)

        Log.d("앨범 클릭 후 albumSongs", albumSongs.toString())
        Log.d("앨범 클릭 후 songs", songs.toString())

        // 기존 타이머 스레드 중지
        timer.interrupt()
        mediaPlayer?.release()//미디어플레이어가 갖고 있던 리소스 해제
        mediaPlayer = null //미디어 플레이어 해제

        //progressbar랑 mediaplayer 초기화 후 다시 플레이어 세팅.
        setMiniPlayer(songs[0])
        setPlayerStatus(true)
        // 타이머 스레드를 다시 시작
        startTimer()
//        mediaPlayer?.seekTo(0)
        mediaPlayer?.start()
    }
    private fun initClickListenter(){
        binding.mainPlayerCl.setOnClickListener{
            val editor = getSharedPreferences("song", MODE_PRIVATE).edit()
            editor.putInt("songId", song.id)
            editor.apply()

            val intent = Intent(this, SongActivity::class.java)

            getResultText.launch(intent)//이거 사라지고 돌아감..
//            startActivity(intent) //바로 위에 줄 있으면 이거 실행 안해도 됨
        }
        Log.d("Songsong", song.title + song.singer + song.second + song.playTime + song.isPlaying+song.music)

//        Log.d("Songsong", song.title + song.singer + song.second + song.playTime + song.isPlaying)

        binding.mainMiniplayerBtn.setOnClickListener{
            setPlayerStatus(true)
        }
        binding.mainPauseBtn.setOnClickListener{
            setPlayerStatus(false)
        }

        binding.mainNextIv.setOnClickListener{
            moveSong(+1)
        }

        binding.mainPreviousIv.setOnClickListener{
            moveSong(-1)
        }
    }
    //7주차 추가
    private fun moveSong(direct: Int){
        if(nowPos + direct < 0){
            Toast.makeText(this,"first song", Toast.LENGTH_SHORT).show()
            return
        }
        if(nowPos + direct >= songs.size){
            Toast.makeText(this,"last song", Toast.LENGTH_SHORT).show()
            return
        }
        nowPos += direct

        timer.interrupt()
        startTimer()

        mediaPlayer?.release()//미디어플레이어가 갖고 있던 리소스 해제
        mediaPlayer = null //미디어 플레이어 해제

        setMiniPlayer(songs[nowPos])
    }




    //거슬림
    private fun initBottomNavigation(){

        supportFragmentManager.beginTransaction()
            .replace(R.id.main_frm, HomeFragment())
            .commitAllowingStateLoss()

        binding.mainBnv.setOnItemSelectedListener{ item ->
            when (item.itemId) {

                R.id.homeFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, HomeFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }

                R.id.lookFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, LookFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }
                R.id.searchFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, SearchFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }
                R.id.lockerFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, LockerFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }
            }
            false
        }
    }
    private fun startTimer(){
        timer = Timer(song.playTime, song.isPlaying)
        timer.start()
    }
    inner class Timer(private val playTime: Int,var isPlaying: Boolean = true):Thread() {

        private var second: Int = 0
        private var mills: Float = 0f

        override fun run() {
            super.run()
            try {
                while (true) {

                    if (second >= playTime) {
                        isPlaying == false
                        runOnUiThread {
                            binding.mainMiniplayerBtn.visibility = View.VISIBLE
                            binding.mainPauseBtn.visibility = View.GONE
                            if(mediaPlayer?.isPlaying == true){
                                mediaPlayer?.pause()
                            }
                        }
                        break
                    }

                    if (isPlaying) {
                        sleep(50)
                        mills += 50

                        runOnUiThread {
                            binding.mainProgressSb.progress = ((mills / playTime) * 100).toInt()
                        }

                        if (mills % 1000 == 0f)
                            second++
                    }
                }
            } catch (e: InterruptedException) {
                Log.d("Song", "쓰레드가 죽었습니다. ${e.message}")
            }
        }
    }
}
