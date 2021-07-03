package com.wxk.opengl.l

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.wxk.opengl.l.first.FirstOpenGLProjectActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.btn_first_open_gl).setOnClickListener {
            startActivity(Intent(this@MainActivity, FirstOpenGLProjectActivity::class.java))
        }
    }
}