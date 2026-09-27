package com.personal.habitos.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
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

@Composable
fun AppNavigation(navController: NavHostController = rememberNavController()) {
    val entry by navController.currentBackStackEntryAsState()
    val ruta = entry?.destination?.route
    val actual = Destino.porRuta(ruta)
    val enSeccionPrincipal = Destino.entries.any { it.ruta == ruta }

    GlassBackground {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            contentColor = androidx.compose.material3.MaterialTheme.colorScheme.onBackground,
            bottomBar = {
                GlassBottomBar(
                    destinos = Destino.entries,
                    actual = if (enSeccionPrincipal) actual else Destino.Mas,
                    onSelect = { destino ->
                        navController.navigate(destino.ruta) {
                            popUpTo(Destino.Hoy.ruta) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        ) { interno ->
            val direccion = LocalLayoutDirection.current
            val margen = PaddingValues(
                start = interno.calculateStartPadding(direccion),
                end = interno.calculateEndPadding(direccion),
                top = 0.dp,
                bottom = interno.calculateBottomPadding() + 12.dp
            )

            NavHost(
                navController = navController,
                startDestination = Destino.Hoy.ruta,
                modifier = Modifier.fillMaxSize(),
                enterTransition = { fadeIn(tween(200)) },
                exitTransition = { fadeOut(tween(140)) },
                popEnterTransition = { fadeIn(tween(200)) },
                popExitTransition = { fadeOut(tween(140)) }
            ) {
                composable(Destino.Hoy.ruta) {
                    HoyScreen(
                        contentPadding = margen,
                        irAEntreno = { navController.navigate(Destino.Entreno.ruta) },
                        irAEstudio = { navController.navigate("estudio") },
                        irANotas = { navController.navigate("notas") },
                        irAAjustes = { navController.navigate("ajustes") }
                    )
                }
                composable(Destino.Habitos.ruta) { HabitosScreen(margen) }
                composable(Destino.Entreno.ruta) { EntrenoScreen(margen) }
                composable(Destino.Retos.ruta) { RetosScreen(margen) }
                composable(Destino.Mas.ruta) {
                    MasScreen(margen) { destino -> navController.navigate(destino) }
                }
                composable("finanzas") { FinanzasScreen(margen) }
                composable("estudio") { EstudioScreen(margen) { navController.popBackStack() } }
                composable("agenda") { AgendaScreen(margen) { navController.popBackStack() } }
                composable("revision") { RevisionScreen(margen) { navController.popBackStack() } }
                composable("notas") { NotasScreen(margen) { navController.popBackStack() } }
                composable("ajustes") { AjustesScreen(margen) { navController.popBackStack() } }
            }
        }
    }
}
