package com.smackmap.smackmapandroid.ui.register

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.smackmap.smackmapandroid.R
import com.smackmap.smackmapandroid.config.AppContext
import com.smackmap.smackmapandroid.data.model.LoggedInUser
import com.smackmap.smackmapandroid.data.validator.validateEmail
import com.smackmap.smackmapandroid.data.validator.validatePassword
import com.smackmap.smackmapandroid.data.validator.validatePasswordAgain
import com.smackmap.smackmapandroid.data.validator.validateUsername
import com.smackmap.smackmapandroid.databinding.ActivityRegisterBinding
import com.smackmap.smackmapandroid.service.authentication.AuthenticationService
import com.smackmap.smackmapandroid.ui.main.MainActivity
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var email: EditText
    private lateinit var username: EditText
    private lateinit var password: EditText
    private lateinit var passwordAgain: EditText
    private lateinit var register: Button
    private lateinit var loading: ProgressBar
    private lateinit var authenticationService: AuthenticationService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE or WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        initFields()
        addEmailListener()
        addUsernameListener()
        addPasswordListener()
        addPasswordAgainListener()
        addRegisterListener()
    }

    private fun initFields() {
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        email = binding.regEmail
        username = binding.regUsername
        password = binding.regPassword
        passwordAgain = binding.regPasswordAgain
        register = binding.regRegister
        loading = binding.regLoading
        authenticationService = AppContext.INSTANCE.authenticationService
    }

    private fun addEmailListener() {
        email.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val emailText = email.text.toString()
                if (validateEmail(emailText)) {
                    enableRegister()
                } else {
                    email.error = getString(R.string.invalid_email)
                }
            }
        })
    }

    private fun addUsernameListener() {
        username.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val usernameText = username.text.toString()
                if (validateUsername(usernameText)) {
                    enableRegister()
                } else {
                    username.error = getString(R.string.invalid_username)
                }
            }
        })
    }

    private fun addPasswordListener() {
        password.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val passwordText = password.text.toString()
                if (validatePassword(passwordText)) {
                    enableRegister()
                } else {
                    password.error = getString(R.string.invalid_password)
                }
            }
        })
    }

    private fun addPasswordAgainListener() {
        passwordAgain.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val passwordText = password.text.toString()
                val passwordAgainText = passwordAgain.text.toString()
                if (validatePasswordAgain(passwordText, passwordAgainText)) {
                    enableRegister()
                } else {
                    passwordAgain.error = getString(R.string.invalid_password_again)
                }
            }
        })
    }

    private fun addRegisterListener() {
        register.setOnClickListener {
            loading.visibility = View.VISIBLE
            MainScope().launch {
                val loggedInUser: LoggedInUser? = authenticationService.register(
                    userName = username.text.toString(),
                    email = email.text.toString(),
                    password = password.text.toString()
                )
                if (loggedInUser != null) {
                    AppContext.INSTANCE.onLogin()
                    Handler(Looper.getMainLooper()).post {
                        val toast = Toast.makeText(
                            applicationContext,
                            getString(R.string.welcome) + "${loggedInUser.userName}!",
                            Toast.LENGTH_LONG
                        )
                        toast.show()
                    }
                    startActivity(Intent(this@RegisterActivity, MainActivity::class.java))
                }
            }
        }
    }

    private fun enableRegister() {
        register.isEnabled =
            validEmail() && validUsername() && validPassword() && validPasswordAgain()
    }

    private fun validEmail() = email.error == null || email.text.isNotEmpty()
    private fun validUsername() = username.error == null || username.text.isNotEmpty()
    private fun validPassword() = password.error == null || password.text.isNotEmpty()
    private fun validPasswordAgain() =
        passwordAgain.error == null || passwordAgain.text.isNotEmpty()
}