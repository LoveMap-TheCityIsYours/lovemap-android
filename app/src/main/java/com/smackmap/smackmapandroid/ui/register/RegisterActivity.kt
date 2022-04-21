package com.smackmap.smackmapandroid.ui.register

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.smackmap.smackmapandroid.R
import com.smackmap.smackmapandroid.api.SmackmapBackendApi

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        SmackmapBackendApi().hello(this)
    }
}