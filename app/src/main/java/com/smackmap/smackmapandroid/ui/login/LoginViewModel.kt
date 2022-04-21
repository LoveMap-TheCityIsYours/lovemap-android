package com.smackmap.smackmapandroid.ui.login

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.smackmap.smackmapandroid.R
import com.smackmap.smackmapandroid.config.AppContext
import com.smackmap.smackmapandroid.data.LoginRepository
import kotlinx.coroutines.runBlocking

class LoginViewModel(private val loginRepository: LoginRepository) : ViewModel() {

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    private val authenticationService = AppContext.instance.authenticationService
    private val userDataStore = AppContext.instance.userDataStore

    fun login(email: String, password: String): Boolean {
        // can be launched in a separate asynchronous job
        val success = authenticationService.login(email, password)

        if (success) {
            val user = runBlocking {
                userDataStore.get()
            }
            _loginResult.value =
                LoginResult(success = LoggedInUserView(displayName = user.userName))
        } else {
            _loginResult.value = LoginResult(error = R.string.login_failed)
        }
        return success
    }

    fun loginDataChanged(username: String, password: String) {
        if (!isEmailValid(username)) {
            _loginForm.value = LoginFormState(usernameError = R.string.invalid_email)
        } else if (!isPasswordValid(password)) {
            _loginForm.value = LoginFormState(passwordError = R.string.invalid_password)
        } else {
            _loginForm.value = LoginFormState(isDataValid = true)
        }
    }

    private fun isEmailValid(email: String): Boolean {
        return if (email.contains('@')) {
            Patterns.EMAIL_ADDRESS.matcher(email).matches()
        } else {
            email.isNotBlank()
        }
    }

    private fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }
}