package com.silverbullet.loadapp.ui

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.lifecycle.lifecycleScope
import com.silverbullet.loadapp.R
import com.silverbullet.loadapp.custom_views.CircularIndicator
import kotlinx.coroutines.delay
import org.w3c.dom.Text
import timber.log.Timber

class ResultActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        val fileNameTextView = findViewById<TextView>(R.id.file_name_text)
        val statusTextView = findViewById<TextView>(R.id.status_text)

        val fileName = intent.getStringExtra("fileName") ?: ""
        val status = intent.getStringExtra("status") ?: ""
        if (status == "Failed") {
            statusTextView.setTextColor(Color.RED)
        }
        fileNameTextView.text = fileName
        statusTextView.text = status

        val motionLayout = findViewById<MotionLayout>(R.id.motion_layout)
        motionLayout.transitionToState(R.id.end)
    }
}