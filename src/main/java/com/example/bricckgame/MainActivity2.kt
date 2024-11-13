package com.example.bricckgame

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        val btn_but = findViewById<Button>(R.id.button)
        btn_but.setOnClickListener {

            val Intent = Intent(this, MainActivity::class.java)
            startActivity(Intent)
        }

    }
}