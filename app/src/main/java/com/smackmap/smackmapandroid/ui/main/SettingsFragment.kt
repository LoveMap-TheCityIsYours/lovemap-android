package com.smackmap.smackmapandroid.ui.main

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.smackmap.smackmapandroid.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }
}