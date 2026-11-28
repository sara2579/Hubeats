package com.hubeats.app.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.hubeats.app.R
import com.hubeats.app.util.SessionManager
import com.hubeats.app.viewmodel.LoginViewModel
import com.hubeats.app.viewmodel.ViewModelFactory
import com.hubeats.app.ui.student.StudentActivity
import com.hubeats.app.ui.employee.EmployeeActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var editUsername: EditText
    private lateinit var editPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var textLoginError: TextView

    private lateinit var viewModel: LoginViewModel
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        editUsername = findViewById(R.id.editUsername)
        editPassword = findViewById(R.id.editPassword)
        btnLogin = findViewById(R.id.btnLogin)
        textLoginError = findViewById(R.id.textLoginError)

        sessionManager = SessionManager(this)

        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(applicationContext)
        )[LoginViewModel::class.java]

        btnLogin.setOnClickListener {
            viewModel.login(
                editUsername.text.toString(),
                editPassword.text.toString()
            )
        }

        observeLogin()
    }

    private fun observeLogin() {
        viewModel.errorMessage.observe(this) { message ->
            if (message.isNullOrEmpty()) {
                textLoginError.visibility = View.GONE
            } else {
                textLoginError.text = message
                textLoginError.visibility = View.VISIBLE
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            btnLogin.isEnabled = !isLoading
            btnLogin.text = if (isLoading) {
                "Signing in..."
            } else {
                "Sign In"
            }
        }

        viewModel.loginResult.observe(this) { user ->
            if (user != null) {
                sessionManager.saveUser(
                    id = user.id,
                    fullName = user.fullName,
                    role = user.role,
                    restaurantId = user.restaurantId
                )

                when (user.role.lowercase()) {
                    "student" -> {
                        startActivity(
                            Intent(this, StudentActivity::class.java)
                        )
                        finish()
                    }

                    "employee" -> {
                        startActivity(
                            Intent(this, EmployeeActivity::class.java)
                        )
                        finish()
                    }

                    else -> {
                        textLoginError.text = "Unknown account role."
                        textLoginError.visibility = View.VISIBLE
                    }
                }
            }
        }
    }
}