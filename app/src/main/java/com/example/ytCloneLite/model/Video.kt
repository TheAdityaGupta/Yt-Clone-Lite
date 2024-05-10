package com.assignment.ytCloneLite.model

data class Video(
    val id: Int,
    val title: String,
    val description: String,
    val channel_name: String,
    val likes: Int,
    val video_url: String
)