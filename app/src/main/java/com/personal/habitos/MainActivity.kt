package com.personal.habitos

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.personal.habitos.sistema.Recordatorios
import androidx.compose.foundation.isSystemInDarkTheme
import com.personal.habitos.data.Repo
import com.personal.habitos.ui.navigation.AppNavigation
import com.personal.habitos.ui.screens.BienvenidaScreen
import com.personal.habitos.ui.theme.AccentOption
import com.personal.habitos.ui.theme.HabitosTheme

class MainActivity : ComponentActivity() {

    private val permisoNotificaciones =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { }

    private fun pedirPermisoNotificaciones() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val concedido = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!concedido) permisoNotificaciones.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    private fun reprogramarRecordatorios() {
        val estado = Repo.estado
        listOf("entreno", "revision", "bloques").forEach { clave ->
            Recordatorios.programar(
                applicationContext,
                clave,
                estado.recordatorios[clave] == true,
                estado.horas[clave] ?: 8
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        Repo.iniciar(applicationContext)
        Recordatorios.crearCanal(applicationContext)
        pedirPermisoNotificaciones()
        reprogramarRecordatorios()

        setContent {
            val estado = Repo.estado
            val acento = AccentOption.entries.getOrElse(estado.acento) { AccentOption.NaranjaQuemado }
            val oscuro = when (estado.modoTema) {
                1 -> false
                2 -> true
                else -> isSystemInDarkTheme()
            }
            HabitosTheme(accent = acento, darkTheme = oscuro) {
                if (estado.iniciado) AppNavigation() else BienvenidaScreen()
            }
        }
    }
}
