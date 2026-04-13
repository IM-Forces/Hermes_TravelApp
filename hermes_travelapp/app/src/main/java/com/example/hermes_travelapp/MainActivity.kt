package com.example.hermes_travelapp

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hermes_travelapp.data.PreferencesManager
import com.example.hermes_travelapp.ui.theme.Hermes_travelappTheme
import com.example.hermes_travelapp.ui.viewmodels.ThemeViewModel
import com.example.hermes_travelapp.ui.viewmodels.ViewModelFactory
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val prefsManager = PreferencesManager(this)
        // Note: applyLocale is NOT called here to avoid recreation loops.
        // The locale is handled via attachBaseContext.
        
        enableEdgeToEdge()
        
        setContent {
            val themeViewModel: ThemeViewModel = viewModel(
                factory = ViewModelFactory(preferencesManager = prefsManager)
            )
            val isDarkMode by themeViewModel.isDarkMode.collectAsState()
            
            Hermes_travelappTheme(darkTheme = isDarkMode) {
                NavGraph(themeViewModel = themeViewModel)
            }
        }
    }

    override fun attachBaseContext(newBase: Context) {
        val prefs = PreferencesManager(newBase)
        val locale = java.util.Locale(prefs.language)
        val config = android.content.res.Configuration(newBase.resources.configuration)
        config.setLocale(locale)
        val context = newBase.createConfigurationContext(config)
        super.attachBaseContext(context)
    }
}
