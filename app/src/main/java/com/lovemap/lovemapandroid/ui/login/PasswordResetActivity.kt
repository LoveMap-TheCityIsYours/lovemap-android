package com.lovemap.lovemapandroid.ui.login

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.lovemap.lovemapandroid.R
import com.lovemap.lovemapandroid.config.AppContext
import com.lovemap.lovemapandroid.data.validator.validateEmail
import com.lovemap.lovemapandroid.data.validator.validatePassword
import com.lovemap.lovemapandroid.data.validator.validatePasswordAgain
import com.lovemap.lovemapandroid.databinding.ActivityPasswordResetBinding
import com.lovemap.lovemapandroid.ui.main.MainActivity
import com.lovemap.lovemapandroid.ui.utils.LoadingBarShower
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class PasswordResetActivity : AppCompatActivity() {

    private val appContext = AppContext.INSTANCE
    private val authenticationService = appContext.authenticationService

    private lateinit var binding: ActivityPasswordResetBinding
    private lateinit var email: EditText
    private lateinit var requestCodeButton: Button
    private lateinit var resetCode: EditText
    private lateinit var newPassword: EditText
    private lateinit var newPasswordAgain: EditText
    private lateinit var changePasswordButton: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initFields()
        addEmailListener()
        setRequestCodeButtonListener()
        addResetCodeListener()
        addPasswordListener()
        addPasswordAgainListener()
        setChangePasswordButton()
    }

    private fun initFields() {
        binding = ActivityPasswordResetBinding.inflate(layoutInflater)
        setContentView(binding.root)
        email = binding.resetEmail
        requestCodeButton = binding.requestCodeButton
        resetCode = binding.resetResetCode
        newPassword = binding.resetNewPassword
        newPasswordAgain = binding.resetNewPasswordAgain
        changePasswordButton = binding.resetChangePasswordButton
    }

    private fun addEmailListener() {
        email.addTextChangedListener { text ->
            if (validateEmail(text.toString())) {
                email.error = null
                enableRequestCode()
            } else {
                email.error = getString(R.string.invalid_email)
                disableRequestCodeButton()
            }
        }
    }

    private fun setRequestCodeButtonListener() {
        requestCodeButton.setOnClickListener {
            MainScope().launch {
                val loadingBarShower = LoadingBarShower(this@PasswordResetActivity).show()
                val success = authenticationService.requestPasswordReset(email.text.toString())
                if (success) {
                    email.isEnabled = false
                    disableRequestCodeButton()
                }
                loadingBarShower.onResponse()
            }
        }
    }

    private fun addResetCodeListener() {
        resetCode.addTextChangedListener { text ->
            if (resetCode.text.toString() != resetCode.text.toString().trim()) {
                resetCode.setText(text.toString().trim())
            }
            if (validResetCode()) {
                resetCode.error = null
            } else {
                resetCode.error = getString(R.string.invalidPwResetCode)
            }
            enableChangePasswordButton()
        }
    }

    private fun addPasswordListener() {
        newPassword.addTextChangedListener {
            val passwordText = newPassword.text.toString()
            if (validatePassword(passwordText)) {
                newPassword.error = null
            } else {
                newPassword.error = getString(R.string.invalid_password)
            }
            enableChangePasswordButton()
        }
    }

    private fun addPasswordAgainListener() {
        newPasswordAgain.addTextChangedListener {
            val passwordText = newPassword.text.toString()
            val passwordAgainText = newPasswordAgain.text.toString()
            if (validatePasswordAgain(passwordText, passwordAgainText)) {
                newPasswordAgain.error = null
            } else {
                newPasswordAgain.error = getString(R.string.invalid_password_again)
            }
            enableChangePasswordButton()
        }
    }

    private fun setChangePasswordButton() {
        changePasswordButton.setOnClickListener {
            MainScope().launch {
                val loadingBarShower = LoadingBarShower(this@PasswordResetActivity).show()
                val loggedInUser = authenticationService.newPassword(
                    email = email.text.toString().trim(),
                    resetCode = resetCode.text.toString(),
                    newPassword = newPassword.text.toString()
                )
                if (loggedInUser != null) {
                    val intent = Intent(this@PasswordResetActivity, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)
                }
                loadingBarShower.onResponse()
            }
        }
    }

    private fun enableChangePasswordButton() {
        changePasswordButton.isEnabled =
            validPassword() && validPasswordAgain() && validResetCode() && validateEmail(email.text.toString())

    }

    private fun enableRequestCode() {
        requestCodeButton.isEnabled = true
    }

    private fun disableRequestCodeButton() {
        requestCodeButton.isEnabled = false
    }

    private fun validResetCode() = resetCode.text.length == 8
    private fun validPassword() = newPassword.error == null && newPassword.text.isNotEmpty()
    private fun validPasswordAgain() =
        newPasswordAgain.error == null && newPasswordAgain.text.isNotEmpty() && newPasswordAgain.text.toString() == newPassword.text.toString()
}