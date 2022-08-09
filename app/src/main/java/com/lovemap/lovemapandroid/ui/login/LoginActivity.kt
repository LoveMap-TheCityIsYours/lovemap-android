package com.lovemap.lovemapandroid.ui.login

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.facebook.*
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.lovemap.lovemapandroid.R
import com.lovemap.lovemapandroid.config.AppContext
import com.lovemap.lovemapandroid.data.metadata.MetadataStore
import com.lovemap.lovemapandroid.databinding.ActivityLoginBinding
import com.lovemap.lovemapandroid.ui.main.MainActivity
import com.lovemap.lovemapandroid.ui.utils.LoadingBarShower
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.json.JSONObject


class LoginActivity : AppCompatActivity() {

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var binding: ActivityLoginBinding
    private lateinit var fbLoginButton: LoginButton
    private val appContext = AppContext.INSTANCE
    private val metadataStore: MetadataStore = appContext.metadataStore
    private val authenticationService = appContext.authenticationService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE or WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        if (!hasPlayServices()) {
            appContext.toaster.showToast("Not working without Google Play Services.")
            onBackPressed()
        }

        val loggedIn = runBlocking {
            metadataStore.isLoggedIn()
        }
        if (loggedIn) {
            startActivity(Intent(this, MainActivity::class.java))
        } else {
            setTheme(R.style.Theme_Lovemapandroid)
        }

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
    }

    private fun initViews() {
        initLoginViewModel()
        val resetPassword = binding.resetPassword
        resetPassword.setOnClickListener {
            startActivity(Intent(this, PasswordResetActivity::class.java))
        }
        initFbLoginButton()
    }

    private fun initLoginViewModel() {
        val email = binding.email
        val password = binding.password
        val login = binding.login
        val register = binding.register
        val loading = binding.loading

        loginViewModel =
            ViewModelProvider(this, LoginViewModelFactory())[LoginViewModel::class.java]

        loginViewModel.loginFormState.observe(this@LoginActivity, Observer {
            val loginState = it ?: return@Observer

            // disable login button unless both username / password is valid
            login.isEnabled = loginState.isDataValid

            if (loginState.usernameError != null) {
                email.error = getString(loginState.usernameError)
            }
            if (loginState.passwordError != null) {
                password.error = getString(loginState.passwordError)
            }
        })

        loginViewModel.loginResult.observe(this@LoginActivity, Observer {
            val loginResult = it ?: return@Observer

            loading.visibility = View.GONE
            if (loginResult.error != null) {
                showLoginFailed(loginResult.error)
            }
            if (loginResult.success != null) {
                updateUiWithUser(loginResult.success)
            }
            setResult(RESULT_OK)

            //Complete and destroy login activity once successful
            finish()
        })

        email.afterTextChanged {
            loginViewModel.loginDataChanged(
                email.text.toString(),
                password.text.toString()
            )
        }

        password.apply {
            afterTextChanged {
                loginViewModel.loginDataChanged(
                    email.text.toString(),
                    password.text.toString()
                )
            }

            login.setOnClickListener {
                loading.visibility = View.VISIBLE
                login(email.text.toString(), password.text.toString())
            }

            register.setOnClickListener {
                startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
            }
        }
    }

    private fun initFbLoginButton() {
        val loadingBarShower = LoadingBarShower(this@LoginActivity)
        fbLoginButton = binding.fbLoginButton
        val callbackManager = CallbackManager.Factory.create()
        fbLoginButton.setPermissions(listOf("email"))
        fbLoginButton.registerCallback(
            callbackManager,
            object : FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult) {
                    loadingBarShower.show()
                    getEmailFromFacebookAndLogin(result, loadingBarShower)
                }

                override fun onCancel() {
                    facebookLoginFailed(loadingBarShower)
                }

                override fun onError(exception: FacebookException) {
                    facebookLoginFailed(loadingBarShower)
                }
            })
    }

    private fun getEmailFromFacebookAndLogin(
        loginResult: LoginResult,
        loadingBarShower: LoadingBarShower
    ) {
        val request = GraphRequest.newMeRequest(
            AccessToken.getCurrentAccessToken()
        ) { me, response ->
            handleFacebookEmailResponse(me, response, loginResult, loadingBarShower)
        }
        val parameters = Bundle()
        parameters.putString("fields", "id,name,email")
        request.parameters = parameters
        request.executeAsync()
    }

    private fun handleFacebookEmailResponse(
        me: JSONObject?,
        response: GraphResponse?,
        result: LoginResult,
        loadingBarShower: LoadingBarShower
    ) {
        MainScope().launch {
            val email = me?.optString("email")
            val id = me?.optString("id")
            if (meRequestFailed(response, email, id)) {
                facebookLoginFailed(loadingBarShower)
            } else {
                facebookLoginWithBackend(email!!, id!!, result, loadingBarShower)
            }
        }
    }

    private suspend fun facebookLoginWithBackend(
        email: String,
        id: String,
        result: LoginResult,
        loadingBarShower: LoadingBarShower
    ) {
        val loggedInUser = authenticationService.facebookLogin(
            email,
            id,
            result.accessToken.token
        )
        if (loggedInUser != null) {
            loadingBarShower.onResponse()
            appContext.toaster.showToast(getString(R.string.welcome_back) + "${loggedInUser.userName}!")
            startActivity(
                Intent(
                    this@LoginActivity,
                    MainActivity::class.java
                )
            )
        } else {
            facebookLoginFailed(loadingBarShower)
        }
    }

    private fun meRequestFailed(
        response: GraphResponse?,
        email: String?,
        id: String?
    ) = response?.error != null || email == null || id == null

    private fun facebookLoginFailed(loadingBarShower: LoadingBarShower) {
        appContext.toaster.showToast(R.string.facebook_login_failed)
        loadingBarShower.onResponse()
    }

    fun login(email: String, password: String) {
        MainScope().launch {
            val loadingBarShower = LoadingBarShower(this@LoginActivity).show()
            val loggedInUser = authenticationService.login(email.trim(), password)
            if (loggedInUser != null) {
                appContext.toaster.showToast(getString(R.string.welcome_back) + "${loggedInUser.userName}!")
                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
            }
            loadingBarShower.onResponse()
        }
    }

    private fun updateUiWithUser(model: LoggedInUserView) {
        val welcome = getString(R.string.welcome)
        val displayName = model.displayName
        Toast.makeText(
            applicationContext,
            "$welcome $displayName",
            Toast.LENGTH_LONG
        ).show()
    }

    private fun showLoginFailed(@StringRes errorString: Int) {
        Toast.makeText(applicationContext, errorString, Toast.LENGTH_SHORT).show()
    }

    override fun onBackPressed() {
        val a = Intent(Intent.ACTION_MAIN)
        a.addCategory(Intent.CATEGORY_HOME)
        a.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(a)
    }

    private fun hasPlayServices() = GoogleApiAvailability.getInstance()
        .isGooglePlayServicesAvailable(applicationContext) == ConnectionResult.SUCCESS
}

/**
 * Extension function to simplify setting an afterTextChanged action to EditText components.
 */
fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}