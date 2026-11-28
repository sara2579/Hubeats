package com.hubeats.app.ui.employee

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


// Temporary screen not final
class EmployeeActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val textView = TextView(this).apply {
            text = "Welcome to HubEats Employee Dashboard!"
            textSize = 22f
            setPadding(32, 64, 32, 32)
        }

        setContentView(textView)
    }
}