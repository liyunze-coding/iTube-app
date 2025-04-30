package com.deakin.itubeapp

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.deakin.itubeapp.data.DatabaseHelper
import com.deakin.itubeapp.model.User

class SignupActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_signup)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val sUsernameEditText = findViewById<EditText>(R.id.sUsernameEditText)
        val sPasswordEditText = findViewById<EditText>(R.id.sPasswordEditText)
        val sConfirmPasswordEditText = findViewById<EditText>(R.id.sConfirmPasswordEditText)

        val saveButton = findViewById<Button>(R.id.saveButton)

        val db = DatabaseHelper(this, null)

        saveButton.setOnClickListener {
            val username = sUsernameEditText.text.toString()
            val password = sPasswordEditText.text.toString()
            val confirmPassword = sConfirmPasswordEditText.text.toString()
            val usernameExists = db.userAlreadyExists(username)

            if (password != confirmPassword)
            {
                Toast.makeText(this@SignupActivity, "Two passwords do not match!", Toast.LENGTH_SHORT).show()
            }
            else if (usernameExists){
                Toast.makeText(this@SignupActivity, "Username already exists!", Toast.LENGTH_SHORT).show()
            }
            else
            {
                val userObj = User(username, password)

                val result: Long = db.insertUser(userObj)
                if (result > -1) {
                    Toast.makeText(this, "Registered successfully!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Registration error.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}