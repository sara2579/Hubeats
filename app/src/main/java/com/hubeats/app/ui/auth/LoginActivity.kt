package com.hubeats.app.ui.auth

import android.os.Bundle
import android.os.Message
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.hubeats.app.R

class LoginActivity : AppCompatActivity() {

    private lateinit var editUsername: EditText
    private lateinit var editPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var textLoginError: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // connect XML views
        editUsername = findViewById(R.id.editUsername)
        editPassword = findViewById(R.id.editPassword)
        btnLogin = findViewById(R.id.btnLogin)
        textLoginError = findViewById(R.id.textLoginError)

        btnLogin.setOnClickListener { validateInput() }
    }

    // ensures that you enter both username and password
    private fun validateInput() {
        val username = editUsername.text.toString().trim()
        val password = editPassword.text.toString().trim()

        when {
            username.isEmpty() && password.isEmpty() -> showError("Please enter both fields.")
            username.isEmpty() -> showError("Please enter your username.")
            password.isEmpty() -> showError("Please enter your password.")

            else -> { textLoginError.visibility = View.GONE }
        }
    }

    // displays the message in parameter and changes the visibility of the Login Error TextView
    private fun showError(message: String) {
        textLoginError.text = message
        textLoginError.visibility = View.VISIBLE
    }
}