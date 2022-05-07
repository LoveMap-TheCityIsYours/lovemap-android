package com.lovemap.lovemapandroid.ui.main

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.lovemap.lovemapandroid.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }
}