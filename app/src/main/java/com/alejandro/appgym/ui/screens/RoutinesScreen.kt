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
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.icons.rounded.MenuBook
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
import com.alejandro.appgym.data.PlanGuide
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

private val dayHints = listOf(
    "Lunes" to "L", "Martes" to "M", "Miércoles" to "X", "Jueves" to "J",
    "Viernes" to "V", "Sábado" to "S", "Domingo" to "D"
)

@Composable
fun RoutinesScreen(vm: GymViewModel, nav: NavHostController) {

    val routines by vm.routines.collectAsStateWithLifecycle()
    val items by vm.routineItems.collectAsStateWithLifecycle()
    val planActivo by vm.activePlan.collectAsStateWithLifecycle()
    val accent = MaterialTheme.colorScheme.primary

    var showDialog by remember { mutableStateOf(false) }
    var abierto by remember { mutableStateOf<String?>(null) }

    // Las creadas a mano, más las que quedaron de un plan retirado: si no,
    // desaparecerían de la vista aunque sigan en la base de datos.
    val propias = routines.filter {
        it.planKey.isBlank() || SeedData.groupOf(it.planKey) == null
    }
    // El plan que toca va primero.
    val grupos = SeedData.groups.sortedByDescending { it.key == planActivo }

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
            subtitle = SeedData.groupOf(planActivo)?.let { "Sigues «${it.name}»" } ?: "",
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
                val esActivo = grupo.key == planActivo
                val dias = grupo.days.mapNotNull { d -> routines.firstOrNull { it.name == d.name } }
                if (esActivo) {
                    PlanActivo(
                        grupo = grupo,
                        dias = dias,
                        items = items,
                        diaDeHoy = SeedData
                            .dayFor(grupo.key, java.time.LocalDate.now().dayOfWeek.value)
                            ?.let { d -> dias.firstOrNull { it.name == d.name }?.id },
                        onAbrir = { nav.navigate(Routes.routine(it)) },
                        onEmpezar = ::empezar
                    )
                } else {
                    PlanPlegado(
                        grupo = grupo,
                        dias = dias,
                        items = items,
                        expandido = abierto == grupo.key,
                        onToggle = { abierto = if (abierto == grupo.key) null else grupo.key },
                        onActivar = { vm.setActivePlan(grupo.key) },
                        onAbrir = { nav.navigate(Routes.routine(it)) },
                        onEmpezar = ::empezar
                    )
                }
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
                            Icons.Rounded.MenuBook, null,
                            tint = GroupBiceps,
                            modifier = Modifier.size(21.dp)
                        )
                    }
                    Spacer(Modifier.width(14.dp))
                    Column(Modifier.weight(1f)) {
                        Text(
                            "Cómo funciona todo",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            "Progresión, comida y descanso",
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
            onCreate = { name, day ->
                showDialog = false
                vm.createRoutine(name, day, 0)
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
    diaDeHoy: Long?,
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
            Badge("Plan activo", accent)
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
                    detalle = dia.dayHint.ifBlank { "Sin día fijo" } +
                        " · ${own.size} ejercicios · ${own.sumOf { it.targetSets }} series",
                    destacado = dia.id == diaDeHoy,
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
private fun PlanPlegado(
    grupo: SeedData.PlanGroup,
    dias: List<Routine>,
    items: List<RoutineItem>,
    expandido: Boolean,
    onToggle: () -> Unit,
    onActivar: () -> Unit,
    onAbrir: (Long) -> Unit,
    onEmpezar: (Long) -> Unit
) {
    val giro by animateFloatAsState(if (expandido) 180f else 0f, tween(200), label = "chev")
    val accent = MaterialTheme.colorScheme.primary

    Surface(
        shape = MaterialTheme.shapes.large,
        color = Surface1,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Gutter)
            .animateContentSize(tween(220))
    ) {
        Column {
            Row(
                Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onToggle)
                    .padding(18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text(grupo.name, style = MaterialTheme.typography.titleLarge, color = TextPrimary)
                    Spacer(Modifier.height(3.dp))
                    Text(
                        grupo.subtitle,
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextSecondary
                    )
                }
                Icon(
                    Icons.Rounded.ExpandMore, null,
                    tint = TextTertiary,
                    modifier = Modifier
                        .size(22.dp)
                        .rotate(giro)
                )
            }
            AnimatedVisibility(expandido) {
                Column(Modifier.padding(start = 18.dp, end = 18.dp, bottom = 18.dp)) {
                    Text(
                        grupo.description,
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextSecondary
                    )
                    Spacer(Modifier.height(14.dp))
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clip(Pill)
                            .background(accent.copy(alpha = 0.14f))
                            .clickable(onClick = onActivar)
                            .padding(vertical = 11.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Rounded.CheckCircle, null,
                            tint = accent,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Cambiar a este plan",
                            style = MaterialTheme.typography.labelMedium,
                            color = accent
                        )
                    }
                    Spacer(Modifier.height(14.dp))
                    dias.forEachIndexed { i, dia ->
                        val own = items.filter { it.routineId == dia.id }
                        DiaFila(
                            nombre = dia.name,
                            detalle = "${own.size} ejercicios · ${own.sumOf { it.targetSets }} series",
                            destacado = false,
                            fondo = Surface2,
                            onClick = { onAbrir(dia.id) },
                            onEmpezar = { onEmpezar(dia.id) }
                        )
                        if (i < dias.lastIndex) Spacer(Modifier.height(8.dp))
                    }
                }
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
private fun NewRoutineDialog(onDismiss: () -> Unit, onCreate: (String, String) -> Unit) {
    var name by remember { mutableStateOf("") }
    var day by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Surface2,
        shape = MaterialTheme.shapes.large,
        title = { Text("Nueva rutina", style = MaterialTheme.typography.titleLarge, color = TextPrimary) },
        text = {
            Column {
                DialogField(name, { name = it }, "Nombre (ej. Espalda extra)")
                Spacer(Modifier.height(16.dp))
                Text("Día habitual", style = MaterialTheme.typography.bodyMedium, color = TextTertiary)
                Spacer(Modifier.height(9.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                    dayHints.forEach { (d, short) ->
                        val selected = day == d
                        Box(
                            Modifier
                                .size(34.dp)
                                .clip(Pill)
                                .background(if (selected) MaterialTheme.colorScheme.primary else Surface1)
                                .clickable { day = if (selected) "" else d },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                short,
                                style = MaterialTheme.typography.labelMedium,
                                color = if (selected) MaterialTheme.colorScheme.onPrimary else TextSecondary
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onCreate(name, day) }) {
                Text("Crear", color = MaterialTheme.colorScheme.primary)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar", color = TextTertiary) }
        }
    )
}
