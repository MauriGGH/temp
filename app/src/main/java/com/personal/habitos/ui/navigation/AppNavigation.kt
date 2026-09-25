package com.personal.habitos.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.personal.habitos.ui.components.GlassBackground
import com.personal.habitos.ui.components.GlassBottomBar
import com.personal.habitos.ui.screens.EntrenoScreen
import com.personal.habitos.ui.screens.FinanzasScreen
import com.personal.habitos.ui.screens.HabitosScreen
import com.personal.habitos.ui.screens.HoyScreen
import com.personal.habitos.ui.screens.MasScreen

@Composable
fun AppNavigation(navController: NavHostController = rememberNavController()) {
    val entry by navController.currentBackStackEntryAsState()
    val actual = Destino.porRuta(entry?.destination?.route)

    GlassBackground {
        Box(modifier = Modifier.fillMaxSize()) {
            NavHost(
                navController = navController,
                startDestination = Destino.Hoy.ruta,
                modifier = Modifier.fillMaxSize()
            ) {
                composable(Destino.Hoy.ruta) { HoyScreen(contentPadding = contenidoInferior) }
                composable(Destino.Habitos.ruta) { HabitosScreen(contentPadding = contenidoInferior) }
                composable(Destino.Entreno.ruta) { EntrenoScreen(contentPadding = contenidoInferior) }
                composable(Destino.Finanzas.ruta) { FinanzasScreen(contentPadding = contenidoInferior) }
                composable(Destino.Mas.ruta) { MasScreen(contentPadding = contenidoInferior) }
            }

            GlassBottomBar(
                destinos = Destino.entries,
                actual = actual,
                onSelect = { destino ->
                    if (destino != actual) {
                        navController.navigate(destino.ruta) {
                            popUpTo(Destino.Hoy.ruta) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .padding(bottom = 16.dp)
            )
        }
    }
}

/** Espacio para que el contenido no quede debajo de la barra flotante. */
private val contenidoInferior = PaddingValues(bottom = 112.dp)
