package com.example.flo.ui.song

import android.app.Activity
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.flo.R
import com.example.flo.data.local.SongDatabase
import com.example.flo.data.entities.Song
import com.example.flo.databinding.ActivitySongBinding
import com.example.flo.ui.main.MainActivity
import com.google.gson.Gson

class SongActivity : AppCompatActivity(){
    lateinit var binding: ActivitySongBinding//나중에 초기화 해줄게
    lateinit var timer: Timer
    private var mediaPlayer: MediaPlayer? = null
    private var gson: Gson = Gson()
    private var isRepeatOne: Boolean = false // 한 곡 반복 상태를 추적하는 변수

    val songs = arrayListOf<Song>()
    lateinit var songDB: SongDatabase
    var nowPos = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySongBinding.inflate(layoutInflater)//초기화
        setContentView(binding.root)

        initPlayList()
        initSong()
        initClickListenter()
    }


    private fun setPlayer(song: Song){
        binding.songMusicTitleTv.text = song.title
        binding.songSingerNameTv.text = song.singer
        binding.songStartTimeTv.text = String.format("%02d:%02d", song.second / 60, song.second % 60)
        binding.songEndTimeTv.text = String.format("%02d:%02d", song.playTime / 60, song.playTime % 60)
        binding.songAlbumIv.setImageResource(song.coverImg!!)
        binding.songProgressSb.progress = (song.second * 1000 / song.playTime)
        val music = resources.getIdentifier(song.music, "raw", this.packageName)
        mediaPlayer = MediaPlayer.create(this, music)

        //좋아요
        if(song.isLike){
            binding.songLikeIv.setImageResource(R.drawable.ic_my_like_on)
        }else{
            binding.songLikeIv.setImageResource(R.drawable.ic_my_like_off)
        }


        setPlayerStatus(song.isPlaying)
    }

    private fun setPlayerStatus(isPlaying : Boolean){
        songs[nowPos].isPlaying = isPlaying
        timer.isPlaying = isPlaying

        if(isPlaying){
            binding.songMiniplayerIv.visibility = View.GONE
            binding.songPauseIv.visibility = View.VISIBLE
            mediaPlayer?.start()
        } else {
            binding.songMiniplayerIv.visibility = View.VISIBLE
            binding.songPauseIv.visibility = View.GONE
            if(mediaPlayer?.isPlaying == true){
                mediaPlayer?.pause()
            }
        }
    }
    private fun restartSong() {
        // 미디어 플레이어를 처음부터 다시 설정
        mediaPlayer?.seekTo(0)
        mediaPlayer?.start()

        // 기존 타이머 스레드 중지
        timer.interrupt()

        // 타이머 스레드를 다시 시작
        startTimer()
    }
    override fun onDestroy() {
        super.onDestroy()
        timer.interrupt()
        mediaPlayer?.release()//미디어플레이어가 갖고 있던 리소스 해제
        mediaPlayer = null //미디어 플레이어 해제
    }

    //사용자가 포커스를 잃었을 때 음악이 중지
    override fun onPause() {
        super.onPause()

        songs[nowPos].second = ((binding.songProgressSb.progress * songs[nowPos].playTime)/100)/1000 //ms -> s위해 /1000
        songs[nowPos].isPlaying = false
        setPlayerStatus(false)

        val sharedPreferences = getSharedPreferences("song", MODE_PRIVATE)
        val editor = sharedPreferences.edit() // 에디터

        editor.putInt("songId", songs[nowPos].id)

        editor.apply()
    }
    //7주차
    private fun initSong(){
        val spf = getSharedPreferences("song", MODE_PRIVATE)
        val songId = spf.getInt("songId", 0)

        nowPos = getPlayingSongPosition(songId)

        Log.d("new Song ID", songs[nowPos].id.toString())

        startTimer()
        setPlayer(songs[nowPos])
    }

    private fun initPlayList(){
        songDB = SongDatabase.getInstance(this)!!
        songs.addAll(songDB.songDao().getSongs())//songs 초기화
    }

    private fun initClickListenter(){
        binding.songDownIb.setOnClickListener{
            val intent = Intent(this, MainActivity :: class.java).apply {
                putExtra(MainActivity.STRING_INTENT_KEY, "Returning to Main Activity")
                putExtra("album_title", songs[nowPos].title.toString())
            }
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
        binding.songMiniplayerIv.setOnClickListener{
            setPlayerStatus(true)
        }
        binding.songPauseIv.setOnClickListener{
            setPlayerStatus(false)
        }

        //한곡반복재생 버튼 클릭시.
        binding.songRepeatIv.setOnClickListener{
            isRepeatOne = !isRepeatOne // 버튼을 클릭할 때마다 반복 상태를 토글
            if (isRepeatOne) {
                // 반복 모드 활성화 시, 아이콘 변경 또는 사용자에게 피드백 제공
                Log.d("반복","반복")
            } else {
                // 반복 모드 비활성화 시, 아이콘 변경 또는 사용자에게 피드백 제공
                Log.d("반복안함","반복안함")
            }
        }

        binding.songNextIv.setOnClickListener{
            moveSong(+1)
        }

        binding.songPreviousIv.setOnClickListener{
            moveSong(-1)
        }

        binding.songLikeIv.setOnClickListener{
            setLike(songs[nowPos].isLike)
        }

    }
    private fun setLike(isLike: Boolean){
        songs[nowPos].isLike = !isLike
        songDB.songDao().updateIsLikeById(!isLike, songs[nowPos].id)

        if(!isLike){
            binding.songLikeIv.setImageResource(R.drawable.ic_my_like_on)
            CustomToast(applicationContext, "좋아요 한 곡에 담겼습니다.").show()
        }else{
            binding.songLikeIv.setImageResource(R.drawable.ic_my_like_off)
            CustomToast(applicationContext, "좋아요 한 곡이 취소되었습니다.").show()
        }
    }
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

        setPlayer(songs[nowPos])
    }
    private fun getPlayingSongPosition(songId: Int): Int{
        for (i in 0 until songs.size) {
            if (songs[i].id == songId) {
                return i
            }
        }
        return 0
    }



    private fun startTimer(){
        timer = Timer(songs[nowPos].playTime, songs[nowPos].isPlaying)
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
                        if (isRepeatOne) {
                            // 한 곡 반복 상태이면, 다시 시작
                            mills = 0f
                            second = 0

                            restartSong()
                            continue
                        } else {
                            isPlaying == false
                            runOnUiThread {
                                binding.songMiniplayerIv.visibility = View.VISIBLE
                                binding.songPauseIv.visibility = View.GONE
                                if(mediaPlayer?.isPlaying == true){
                                    mediaPlayer?.pause()
                                }
                            }
                            break
                        }
                    }

                    if (isPlaying) {
                        sleep(50)
                        mills += 50

                        runOnUiThread {
                            binding.songProgressSb.progress = ((mills / playTime) * 100).toInt()
                        }

                        if (mills % 1000 == 0f) {
                            runOnUiThread {
                                binding.songStartTimeTv.text =
                                    String.format("%02d:%02d", second / 60, second % 60)
                            }
                            second++
                        }
                    }
                }
            } catch (e: InterruptedException) {
                Log.d("Song", "쓰레드가 죽었습니다. ${e.message}")
            }
        }
    }
}