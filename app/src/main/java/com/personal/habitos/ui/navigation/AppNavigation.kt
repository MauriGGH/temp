package com.personal.habitos.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import com.personal.habitos.ui.screens.AgendaScreen
import com.personal.habitos.ui.screens.AjustesScreen
import com.personal.habitos.ui.screens.EntrenoScreen
import com.personal.habitos.ui.screens.EstudioScreen
import com.personal.habitos.ui.screens.FinanzasScreen
import com.personal.habitos.ui.screens.HabitosScreen
import com.personal.habitos.ui.screens.HoyScreen
import com.personal.habitos.ui.screens.MasScreen
import com.personal.habitos.ui.screens.NotasScreen
import com.personal.habitos.ui.screens.RetosScreen
import com.personal.habitos.ui.screens.RevisionScreen

private val margenInferior = PaddingValues(bottom = 132.dp)

@Composable
fun AppNavigation(navController: NavHostController = rememberNavController()) {
    val entry by navController.currentBackStackEntryAsState()
    val ruta = entry?.destination?.route
    val actual = Destino.porRuta(ruta)
    val enSeccionPrincipal = Destino.entries.any { it.ruta == ruta }

    GlassBackground {
        Box(modifier = Modifier.fillMaxSize()) {
            NavHost(
                navController = navController,
                startDestination = Destino.Hoy.ruta,
                modifier = Modifier.fillMaxSize(),
                enterTransition = { fadeIn(tween(220)) },
                exitTransition = { fadeOut(tween(160)) },
                popEnterTransition = { fadeIn(tween(220)) },
                popExitTransition = { fadeOut(tween(160)) }
            ) {
                composable(Destino.Hoy.ruta) {
                    HoyScreen(
                        contentPadding = margenInferior,
                        irAEntreno = { navController.navigate(Destino.Entreno.ruta) },
                        irAEstudio = { navController.navigate("estudio") },
                        irANotas = { navController.navigate("notas") },
                        irAAjustes = { navController.navigate("ajustes") }
                    )
                }
                composable(Destino.Habitos.ruta) { HabitosScreen(margenInferior) }
                composable(Destino.Entreno.ruta) { EntrenoScreen(margenInferior) }
                composable(Destino.Finanzas.ruta) { FinanzasScreen(margenInferior) }
                composable(Destino.Mas.ruta) {
                    MasScreen(margenInferior) { destino -> navController.navigate(destino) }
                }
                composable("retos") { RetosScreen(margenInferior) { navController.popBackStack() } }
                composable("estudio") { EstudioScreen(margenInferior) { navController.popBackStack() } }
                composable("agenda") { AgendaScreen(margenInferior) { navController.popBackStack() } }
                composable("revision") { RevisionScreen(margenInferior) { navController.popBackStack() } }
                composable("notas") { NotasScreen(margenInferior) { navController.popBackStack() } }
                composable("ajustes") { AjustesScreen(margenInferior) { navController.popBackStack() } }
            }

            GlassBottomBar(
                destinos = Destino.entries,
                actual = if (enSeccionPrincipal) actual else Destino.Mas,
                onSelect = { destino ->
                    navController.navigate(destino.ruta) {
                        popUpTo(Destino.Hoy.ruta) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
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
