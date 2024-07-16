package com.example.flo.data.remote.song

import retrofit2.Call
import retrofit2.http.GET

interface SongRetrofitInterfaces {
    @GET("/v3/53b8db87-f2f5-4c05-a195-62db6b631ce9")
    fun getSongs(): Call<SongResponse>
}