package com.deakin.itubeapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.deakin.itubeapp.data.DatabaseHelper

class HomeActivity : AppCompatActivity() {
    fun getVideoID(videoURL: String): String? {
        val videoIdRegex = Regex("""(?:v=|/)([0-9A-Za-z_-]{11})""")
        val match = videoIdRegex.find(videoURL)
        val videoId = match?.groups?.get(1)?.value

        return videoId
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val db = DatabaseHelper(this, null)

        // INTENT
        val userId: String = intent.getStringExtra("USER_ID") ?: "-1"

        // PLAY BUTTON
        val playButton = findViewById<Button>(R.id.PlayButton)
        val urlEditText = findViewById<EditText>(R.id.URLEditText)

        playButton.setOnClickListener {
            val urlText = urlEditText.text.toString()
            val videoId = getVideoID(urlText)

            if (videoId != null) {
                val playYTIntent = Intent(this, Play_YT_Activity::class.java).apply {
                    putExtra("VIDEO_ID", videoId)
                }
                startActivity(playYTIntent)
            }
            else {
                Toast.makeText(this, "Invalid video URL", Toast.LENGTH_SHORT).show()
            }
        }

        // ADD TO PLAYLIST
        val addToPlaylistButton = findViewById<Button>(R.id.addToPlaylistButton)

        addToPlaylistButton.setOnClickListener {
            val urlText = urlEditText.text.toString()
            val videoId = getVideoID(urlText)

            if (videoId != null) {
                // check if video ID already exists
                val videoExists = db.checkVideoExists(userId, videoId)

                if (!videoExists) {
                    val result = db.insertVideoToPlaylist(userId, videoId)
                    if (result > -1) {
                        Toast.makeText(this, "Added video to playlist!", Toast.LENGTH_SHORT).show()
                    }
                }
                else {
                    Toast.makeText(this, "Video already exists in playlist", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Invalid video URL", Toast.LENGTH_SHORT).show()
            }
        }

        // MY PLAYLIST
        val myPlaylistButton = findViewById<Button>(R.id.myPlaylistButton)

        myPlaylistButton.setOnClickListener {
            val myPlaylistIntent = Intent(this, PlaylistActivity::class.java).apply {
                putExtra("USER_ID", userId.toString())
            }
            startActivity(myPlaylistIntent)
        }
    }
}