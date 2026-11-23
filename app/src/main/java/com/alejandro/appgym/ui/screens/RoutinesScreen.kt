package com.alejandro.appgym.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.alejandro.appgym.GymViewModel
import com.alejandro.appgym.data.Routine
import com.alejandro.appgym.data.RoutineItem
import com.alejandro.appgym.data.SeedData
import com.alejandro.appgym.ui.Routes
import com.alejandro.appgym.ui.components.Badge
import com.alejandro.appgym.ui.components.Gutter
import com.alejandro.appgym.ui.components.ScreenHeader
import com.alejandro.appgym.ui.theme.Control
import com.alejandro.appgym.ui.theme.GroupBiceps
import com.alejandro.appgym.ui.theme.Ink
import com.alejandro.appgym.ui.theme.Pill
import com.alejandro.appgym.ui.theme.PlanGradientEnd
import com.alejandro.appgym.ui.theme.PlanGradientStart
import com.alejandro.appgym.ui.theme.Surface1
import com.alejandro.appgym.ui.theme.Surface2
import com.alejandro.appgym.ui.theme.TextPrimary
import com.alejandro.appgym.ui.theme.TextSecondary
import com.alejandro.appgym.ui.theme.TextTertiary
import com.alejandro.appgym.ui.theme.VoltBorder

@Composable
fun RoutinesScreen(vm: GymViewModel, nav: NavHostController) {

    val routines by vm.routines.collectAsStateWithLifecycle()
    val items by vm.routineItems.collectAsStateWithLifecycle()
    val accent = MaterialTheme.colorScheme.primary

    var showDialog by remember { mutableStateOf(false) }

    // Las creadas a mano, más cualquiera que quedara de un plan retirado: si no,
    // desaparecerían de la vista aunque sigan en la base de datos.
    val delPlan = SeedData.plans.map { it.name }.toSet()
    val propias = routines.filter { it.name !in delPlan }
    val grupos = SeedData.groups

    fun empezar(id: Long) {
        vm.startFromRoutine(id)
        nav.navigate(Routes.TODAY) { popUpTo(Routes.TODAY) { inclusive = true } }
    }

    Column(
        Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        ScreenHeader(
            title = "Rutinas",
            subtitle = "Elige una y empieza",
            trailing = {
                Box(
                    Modifier
                        .size(46.dp)
                        .clip(Pill)
                        .background(accent)
                        .clickable { showDialog = true },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Rounded.Add, "Nueva rutina",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        )

        LazyColumn(contentPadding = PaddingValues(top = 6.dp, bottom = 30.dp)) {

            items(grupos, key = { it.key }) { grupo ->
                val dias = grupo.days.mapNotNull { d -> routines.firstOrNull { it.name == d.name } }
                PlanActivo(
                    grupo = grupo,
                    dias = dias,
                    items = items,
                    onAbrir = { nav.navigate(Routes.routine(it)) },
                    onEmpezar = ::empezar
                )
                Spacer(Modifier.height(12.dp))
            }

            item {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Gutter)
                        .clip(MaterialTheme.shapes.large)
                        .background(Control)
                        .clickable { nav.navigate(Routes.PLAN) }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(GroupBiceps.copy(alpha = 0.14f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.AutoMirrored.Rounded.MenuBook, null,
                            tint = GroupBiceps,
                            modifier = Modifier.size(21.dp)
                        )
                    }
                    Spacer(Modifier.width(14.dp))
                    Column(Modifier.weight(1f)) {
                        Text(
                            "Consejos",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            "Cómo subir peso, comida y descanso",
                            style = MaterialTheme.typography.bodyLarge,
                            color = TextSecondary
                        )
                    }
                    Icon(
                        Icons.Rounded.ChevronRight, null,
                        tint = TextTertiary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            if (propias.isNotEmpty()) {
                item { Spacer(Modifier.height(26.dp)) }
                item {
                    Text(
                        "Tus rutinas",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimary,
                        modifier = Modifier.padding(horizontal = Gutter, vertical = 10.dp)
                    )
                }
                items(propias, key = { it.id }) { rutina ->
                    val own = items.filter { it.routineId == rutina.id }
                    DiaFila(
                        nombre = rutina.name,
                        detalle = "${own.size} ejercicios",
                        destacado = false,
                        onClick = { nav.navigate(Routes.routine(rutina.id)) },
                        onEmpezar = { empezar(rutina.id) },
                        modifier = Modifier.padding(horizontal = Gutter, vertical = 4.dp)
                    )
                }
            }
        }
    }

    if (showDialog) {
        NewRoutineDialog(
            onDismiss = { showDialog = false },
            onCreate = { name ->
                showDialog = false
                vm.createRoutine(name, "", 0)
            }
        )
    }
}

/* ------------------------------------------------------------------ */

@Composable
private fun PlanActivo(
    grupo: SeedData.PlanGroup,
    dias: List<Routine>,
    items: List<RoutineItem>,
    onAbrir: (Long) -> Unit,
    onEmpezar: (Long) -> Unit
) {
    val accent = MaterialTheme.colorScheme.primary
    Box(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = Gutter)
            .clip(MaterialTheme.shapes.large)
            .background(
                Brush.linearGradient(
                    0f to PlanGradientStart,
                    0.62f to PlanGradientEnd
                )
            )
            .border(1.dp, VoltBorder, MaterialTheme.shapes.large)
            .padding(18.dp)
    ) {
        Column {
            Badge("Tu plan", accent)
            Spacer(Modifier.height(12.dp))
            Text(grupo.name, style = MaterialTheme.typography.headlineSmall, color = TextPrimary)
            Spacer(Modifier.height(4.dp))
            Text(
                grupo.description,
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary
            )
            Spacer(Modifier.height(16.dp))
            dias.forEachIndexed { i, dia ->
                val own = items.filter { it.routineId == dia.id }
                DiaFila(
                    nombre = dia.name,
                    detalle = (SeedData.planNamed(dia.name)?.focus ?: "${own.size} ejercicios"),
                    destacado = false,
                    fondo = Ink.copy(alpha = 0.55f),
                    onClick = { onAbrir(dia.id) },
                    onEmpezar = { onEmpezar(dia.id) }
                )
                if (i < dias.lastIndex) Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun DiaFila(
    nombre: String,
    detalle: String,
    destacado: Boolean,
    onClick: () -> Unit,
    onEmpezar: () -> Unit,
    modifier: Modifier = Modifier,
    fondo: Color = Surface1
) {
    val accent = MaterialTheme.colorScheme.primary
    Row(
        modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(fondo),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            Modifier
                .weight(1f)
                .clickable(onClick = onClick)
                .padding(start = 14.dp, top = 12.dp, bottom = 12.dp)
        ) {
            Text(
                nombre,
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(2.dp))
            Text(detalle, style = MaterialTheme.typography.bodyMedium, color = TextTertiary)
        }
        Box(
            Modifier
                .padding(8.dp)
                .clip(Pill)
                .background(if (destacado) accent else Control)
                .clickable(onClick = onEmpezar)
                .padding(horizontal = 18.dp, vertical = 10.dp)
        ) {
            Text(
                "Empezar",
                style = MaterialTheme.typography.labelMedium,
                color = if (destacado) MaterialTheme.colorScheme.onPrimary else TextPrimary
            )
        }
    }
}

/* ------------------------------------------------------------------ */

@Composable
private fun NewRoutineDialog(onDismiss: () -> Unit, onCreate: (String) -> Unit) {
    var name by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Surface2,
        shape = MaterialTheme.shapes.large,
        title = { Text("Nueva rutina", style = MaterialTheme.typography.titleLarge, color = TextPrimary) },
        text = { DialogField(name, { name = it }, "Nombre (ej. Espalda extra)") },
        confirmButton = {
            TextButton(onClick = { onCreate(name) }) {
                Text("Crear", color = MaterialTheme.colorScheme.primary)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar", color = TextTertiary) }
        }
    )
}
