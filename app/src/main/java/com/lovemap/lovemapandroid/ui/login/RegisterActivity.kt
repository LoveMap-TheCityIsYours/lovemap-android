package com.lovemap.lovemapandroid.ui.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.widget.addTextChangedListener
import com.lovemap.lovemapandroid.R
import com.lovemap.lovemapandroid.config.AppContext
import com.lovemap.lovemapandroid.data.metadata.LoggedInUser
import com.lovemap.lovemapandroid.data.validator.validateEmail
import com.lovemap.lovemapandroid.data.validator.validatePassword
import com.lovemap.lovemapandroid.data.validator.validatePasswordAgain
import com.lovemap.lovemapandroid.data.validator.validateUsername
import com.lovemap.lovemapandroid.databinding.ActivityRegisterBinding
import com.lovemap.lovemapandroid.ui.main.MainActivity
import com.lovemap.lovemapandroid.ui.utils.InfoPopupShower
import com.lovemap.lovemapandroid.ui.utils.LoadingBarShower
import com.lovemap.lovemapandroid.ui.utils.ProfileUtils
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private val appContext = AppContext.INSTANCE
    private val authenticationService = appContext.authenticationService

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var email: EditText
    private lateinit var username: EditText
    private lateinit var password: EditText
    private lateinit var passwordAgain: EditText
    private lateinit var register: Button
    private lateinit var loading: ProgressBar

    private lateinit var registerPublicImage: ImageView
    private lateinit var registerPublicToggle: SwitchCompat
    private lateinit var registerPublicToggleText: TextView
    private lateinit var registerPublicProfileInfoButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE or WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        val rootView = initFields()
        addEmailListener()
        addUsernameListener()
        addPasswordListener()
        addPasswordAgainListener()
        addRegisterListener()
        initPublicProfileViews(rootView)
    }

    private fun initFields(): ScrollView {
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        email = binding.regEmail
        username = binding.regUsername
        password = binding.regPassword
        passwordAgain = binding.regPasswordAgain
        register = binding.regRegister
        loading = binding.regLoading
        registerPublicImage = binding.registerPublicImage
        registerPublicToggle = binding.registerPublicToggle
        registerPublicToggleText = binding.registerPublicToggleText
        registerPublicProfileInfoButton = binding.registerPublicProfileInfoButton
        return binding.root
    }


    private fun initPublicProfileViews(rootView: View) {
        registerPublicProfileInfoButton.setOnClickListener {
            val infoPopupShower = InfoPopupShower(R.string.register_public_profile_info)
            infoPopupShower.show(rootView)
        }
        registerPublicToggle.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                onPublicProfileSwitchChanged(true)
            }
            if (!isChecked) {
                onPublicProfileSwitchChanged(false)
            }
        }
    }

    private fun onPublicProfileSwitchChanged(publicProfile: Boolean) {
        ProfileUtils.setPublicPrivateProfileImage(
            publicProfile,
            registerPublicImage,
            registerPublicToggleText
        )
    }

    private fun addEmailListener() {
        email.addTextChangedListener {
            val emailText = email.text.toString()
            if (validateEmail(emailText)) {
                email.error = null
                enableRegister()
            } else {
                email.error = getString(R.string.invalid_email)
                enableRegister()
            }
        }
    }

    private fun addUsernameListener() {
        username.addTextChangedListener {
            val usernameText = username.text.toString()
            if (validateUsername(usernameText)) {
                username.error = null
                enableRegister()
            } else {
                username.error = getString(R.string.invalid_username)
                enableRegister()
            }
        }
    }

    private fun addPasswordListener() {
        password.addTextChangedListener {
            val passwordText = password.text.toString()
            if (validatePassword(passwordText)) {
                password.error = null
                enableRegister()
            } else {
                password.error = getString(R.string.invalid_password)
                enableRegister()
            }
        }
    }

    private fun addPasswordAgainListener() {
        passwordAgain.addTextChangedListener {
            val passwordText = password.text.toString()
            val passwordAgainText = passwordAgain.text.toString()
            if (validatePasswordAgain(passwordText, passwordAgainText)) {
                passwordAgain.error = null
                enableRegister()
            } else {
                passwordAgain.error = getString(R.string.invalid_password_again)
                enableRegister()
            }
        }
    }

    private fun addRegisterListener() {
        register.setOnClickListener {
            val imm: InputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(
                register.windowToken,
                InputMethodManager.RESULT_UNCHANGED_SHOWN
            )
            loading.visibility = View.VISIBLE
            MainScope().launch {
                val loadingBarShower = LoadingBarShower(this@RegisterActivity).show()
                val loggedInUser: LoggedInUser? = authenticationService.register(
                    userName = username.text.toString().trim(),
                    email = email.text.toString().trim(),
                    password = password.text.toString(),
                    publicProfile = registerPublicToggle.isChecked
                )
                if (loggedInUser != null) {
                    appContext.toaster.showToast(getString(R.string.welcome) + "${loggedInUser.displayName}!")
                    startActivity(Intent(this@RegisterActivity, MainActivity::class.java))
                }
                loadingBarShower.onResponse()
            }
        }
    }

    private fun enableRegister() {
        register.isEnabled =
            validEmail() && validUsername() && validPassword() && validPasswordAgain()
    }

    private fun validEmail() = email.error == null && email.text.isNotEmpty()
    private fun validUsername() = username.error == null && username.text.isNotEmpty()
    private fun validPassword() = password.error == null && password.text.isNotEmpty()
    private fun validPasswordAgain() =
        passwordAgain.error == null && passwordAgain.text.isNotEmpty() && passwordAgain.text.toString() == password.text.toString()
}