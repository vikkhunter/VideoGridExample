package com.example.videogridexample

import android.Manifest
import android.content.ContentUris
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.MediaStore.Video
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
    private val REQUEST_CODE: Int = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestPermissionForStorage()
    }

    private fun requestPermissionForStorage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_MEDIA_VIDEO
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_MEDIA_VIDEO),
                    REQUEST_CODE
                )
            } else {
                fetchAllVideosAndShow(this)
            }
        } else {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    REQUEST_CODE
                )
            } else {
                fetchAllVideosAndShow(this)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                fetchAllVideosAndShow(this)
            }
        }
    }

    private fun fetchAllVideosAndShow(context: Context) {
        val videoList = ArrayList<VideoItem>()
        val contentResolver = context.contentResolver
        val uri: Uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DATA,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.DURATION,
        )

        val sort = "${MediaStore.Video.Media.DATE_ADDED} DESC"

        val cursor = contentResolver.query(uri, projection, null, null, sort)
        cursor?.let { cur ->
            val idIndex = cur.getColumnIndex(MediaStore.Video.Media._ID)
            val dataIndex = cur.getColumnIndex(MediaStore.Video.Media.DATA)
            val nameIndex = cur.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME)
            val durationIndex = cur.getColumnIndex(MediaStore.Video.Media.DURATION)

            while (cur.moveToNext()) {
                val id = cur.getLong(idIndex)
                val data = cur.getString(dataIndex)
                val name = cur.getString(nameIndex)
                val duration = cur.getLong(durationIndex)
                val contentUri = ContentUris.withAppendedId(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id
                )
                val videoItem = VideoItem(id, data, name, duration, contentUri)
                videoList.add(videoItem)
            }
        }

        showVideos(videoList)
    }

    private fun showVideos(videoList: java.util.ArrayList<VideoItem>) {
        val recyclerView = findViewById<RecyclerView>(R.id.videoList)
        val videoAdapter = VideoAdapter(videoList)
        val gridLayoutManager = GridLayoutManager(this, 3)
        recyclerView.layoutManager = gridLayoutManager
        recyclerView.adapter = videoAdapter
        recyclerView.addItemDecoration(GridItemDecoration(10))
    }
}