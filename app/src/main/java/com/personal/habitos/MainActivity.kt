package com.personal.habitos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import com.personal.habitos.data.Repo
import com.personal.habitos.ui.navigation.AppNavigation
import com.personal.habitos.ui.theme.AccentOption
import com.personal.habitos.ui.theme.HabitosTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        Repo.iniciar(applicationContext)

        setContent {
            val estado = Repo.estado
            val acento = AccentOption.entries.getOrElse(estado.acento) { AccentOption.NaranjaQuemado }
            val oscuro = when (estado.modoTema) {
                1 -> false
                2 -> true
                else -> isSystemInDarkTheme()
            }
            HabitosTheme(accent = acento, darkTheme = oscuro) {
                AppNavigation()
            }
        }
    }
}
