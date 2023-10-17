package com.example.videogridexample

import android.net.Uri

data class VideoItem(
    val id: Long,
    val data: String,
    val name: String,
    val duration: Long,
    val contentUri: Uri
)
