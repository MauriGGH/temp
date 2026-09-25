package com.personal.habitos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.personal.habitos.ui.navigation.AppNavigation
import com.personal.habitos.ui.theme.AccentOption
import com.personal.habitos.ui.theme.HabitosTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            // El acento y el tema se guardarán en DataStore en la fase 2.
            HabitosTheme(accent = AccentOption.NaranjaQuemado) {
                AppNavigation()
            }
        }
    }
}
