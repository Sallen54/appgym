package com.alejandro.appgym.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.CalendarViewWeek
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.Today
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.alejandro.appgym.GymViewModel
import com.alejandro.appgym.ui.screens.CatalogScreen
import com.alejandro.appgym.ui.screens.ExerciseDetailScreen
import com.alejandro.appgym.ui.screens.HistoryScreen
import com.alejandro.appgym.ui.screens.PickerScreen
import com.alejandro.appgym.ui.screens.PlanScreen
import com.alejandro.appgym.ui.screens.ProgressScreen
import com.alejandro.appgym.ui.screens.RoutineDetailScreen
import com.alejandro.appgym.ui.screens.RoutinesScreen
import com.alejandro.appgym.ui.screens.SwapScreen
import com.alejandro.appgym.ui.screens.TodayScreen
import com.alejandro.appgym.ui.screens.WelcomeScreen
import com.alejandro.appgym.ui.theme.Ink
import com.alejandro.appgym.ui.theme.OutlineSoft
import com.alejandro.appgym.ui.theme.TextTertiary

object Routes {
    const val TODAY = "today"
    const val CATALOG = "catalog"
    const val ROUTINES = "routines"
    const val PROGRESS = "progress"
    const val HISTORY = "history"
    const val PLAN = "plan"
    fun exercise(id: String) = "exercise/$id"
    fun routine(id: Long) = "routine/$id"
    /** routineId = -1 → añadir a la sesión de hoy */
    fun picker(routineId: Long = -1L) = "picker/$routineId"
    /** Elegir un sustituto para [id] entre todo el catálogo. */
    fun swap(id: String) = "swap/$id"
}

private data class Tab(val route: String, val label: String, val icon: ImageVector)

private val tabs = listOf(
    Tab(Routes.TODAY, "Hoy", Icons.Rounded.Today),
    Tab(Routes.CATALOG, "Máquinas", Icons.Rounded.FitnessCenter),
    Tab(Routes.ROUTINES, "Rutinas", Icons.Rounded.CalendarViewWeek),
    Tab(Routes.PROGRESS, "Progreso", Icons.Rounded.BarChart)
)

@Composable
fun AppRoot() {
    val nav = rememberNavController()
    val vm: GymViewModel = viewModel(factory = GymViewModel.Factory)

    val backStack by nav.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route
    val showBar = currentRoute in tabs.map { it.route }

    val snackbar = remember { SnackbarHostState() }
    val borrada by vm.ultimaBorrada.collectAsStateWithLifecycle()
    val bienvenidaHecha by vm.onboarded.collectAsStateWithLifecycle()

    LaunchedEffect(borrada) {
        val entry = borrada ?: return@LaunchedEffect
        // Con actionLabel, Compose usa Indefinite por defecto y el aviso se queda
        // en pantalla hasta descartarlo. Cuatro segundos bastan para deshacer.
        val res = snackbar.showSnackbar(
            message = "Serie borrada",
            actionLabel = "Deshacer",
            withDismissAction = false,
            duration = SnackbarDuration.Short
        )
        if (res == SnackbarResult.ActionPerformed) vm.deshacerBorrado() else vm.descartarDeshacer()
    }

    // En una instalación nueva no hay app que enseñar hasta que se conteste la
    // bienvenida: ni barra inferior ni navegación, para no distraer.
    if (!bienvenidaHecha) {
        Scaffold(containerColor = Ink) { padding ->
            Box(Modifier.fillMaxSize().padding(padding)) { WelcomeScreen(vm) }
        }
        return
    }

    Scaffold(
        containerColor = Ink,
        snackbarHost = { SnackbarHost(snackbar) },
        bottomBar = { if (showBar) BottomBar(nav) }
    ) { padding ->
        NavHost(
            navController = nav,
            startDestination = Routes.TODAY,
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = if (showBar) padding.calculateBottomPadding() else 0.dp)
        ) {
            composable(Routes.TODAY) { TodayScreen(vm, nav) }
            composable(Routes.CATALOG) { CatalogScreen(vm, nav) }
            composable(Routes.ROUTINES) { RoutinesScreen(vm, nav) }
            composable(Routes.PROGRESS) { ProgressScreen(vm, nav) }
            composable(Routes.HISTORY) { HistoryScreen(vm, nav) }
            composable(Routes.PLAN) { PlanScreen(vm, nav) }

            composable(
                "exercise/{id}",
                arguments = listOf(navArgument("id") { type = NavType.StringType })
            ) { entry ->
                ExerciseDetailScreen(entry.arguments?.getString("id").orEmpty(), vm, nav)
            }
            composable(
                "routine/{id}",
                arguments = listOf(navArgument("id") { type = NavType.LongType })
            ) { entry ->
                RoutineDetailScreen(entry.arguments?.getLong("id") ?: -1L, vm, nav)
            }
            composable(
                "swap/{id}",
                arguments = listOf(navArgument("id") { type = NavType.StringType })
            ) { entry ->
                SwapScreen(entry.arguments?.getString("id").orEmpty(), vm, nav)
            }
            composable(
                "picker/{routineId}",
                arguments = listOf(navArgument("routineId") { type = NavType.LongType })
            ) { entry ->
                PickerScreen(entry.arguments?.getLong("routineId") ?: -1L, vm, nav)
            }
        }
    }
}

/** Barra inferior sin píldora contenedora: fondo de la app y una línea arriba. */
@Composable
private fun BottomBar(nav: NavHostController) {
    val backStack by nav.currentBackStackEntryAsState()
    val destination = backStack?.destination

    Column(
        Modifier
            .fillMaxWidth()
            .background(Ink)
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(OutlineSoft)
        )
        Row(
            Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            tabs.forEach { tab ->
                val selected = destination?.hierarchy?.any { it.route == tab.route } == true
                TabItem(tab, selected) {
                    if (!selected) {
                        nav.navigate(tab.route) {
                            popUpTo(nav.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RowScope.TabItem(tab: Tab, selected: Boolean, onClick: () -> Unit) {
    val accent = MaterialTheme.colorScheme.primary
    Column(
        Modifier
            .weight(1f)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            tab.icon,
            contentDescription = tab.label,
            tint = if (selected) accent else TextTertiary,
            modifier = Modifier.size(22.dp)
        )
        Spacer(Modifier.height(4.dp))
        Text(
            tab.label,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium
            ),
            color = if (selected) accent else TextTertiary
        )
    }
}
