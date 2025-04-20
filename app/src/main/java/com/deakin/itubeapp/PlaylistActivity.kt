package com.deakin.itubeapp

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.deakin.itubeapp.data.DatabaseHelper

class PlaylistActivity : AppCompatActivity() {
    fun videoIdsToURL(videoIds: List<String>): List<String> {
        return videoIds.map { videoId -> "https://youtu.be/$videoId" }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_playlist)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val db = DatabaseHelper(this, null)

        // INTENT
        val userId: String = intent.getStringExtra("USER_ID") ?: "-1"

        val playlist: List<String> = db.getPlaylist(userId)
        val playlistURLs = videoIdsToURL(playlist)
        val playlistView = findViewById<RecyclerView>(R.id.PlaylistRecyclerView)

        val adapter = RecyclerViewAdapter(playlistURLs, this)
        playlistView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        playlistView.adapter = adapter
    }
}