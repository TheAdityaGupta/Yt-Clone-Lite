package com.assignment.ytCloneLite.network

import com.assignment.ytCloneLite.model.Video
import retrofit2.http.GET

interface SupabaseService {
    @GET("rest/v1/videos?select=*")
    suspend fun listVideos(): List<Video>
}
