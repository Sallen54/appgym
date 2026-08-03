package com.alejandro.appgym.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
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
import androidx.compose.material.icons.automirrored.rounded.Undo
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.icons.rounded.Hotel
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.SwapHoriz
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.alejandro.appgym.GymViewModel
import com.alejandro.appgym.data.ExerciseBlock
import com.alejandro.appgym.data.ExerciseCatalog
import com.alejandro.appgym.data.SeedData
import com.alejandro.appgym.data.SetEntry
import com.alejandro.appgym.data.WorkoutSession
import com.alejandro.appgym.ui.Routes
import com.alejandro.appgym.ui.components.Badge
import com.alejandro.appgym.ui.components.EmptyState
import com.alejandro.appgym.ui.components.ExerciseThumb
import com.alejandro.appgym.ui.components.Gutter
import com.alejandro.appgym.ui.components.ProgressRing
import com.alejandro.appgym.ui.components.SectionTitle
import com.alejandro.appgym.ui.theme.Control
import com.alejandro.appgym.ui.theme.Danger
import com.alejandro.appgym.ui.theme.Outline
import com.alejandro.appgym.ui.theme.Pill
import com.alejandro.appgym.ui.theme.PlanGradientEnd
import com.alejandro.appgym.ui.theme.PlanGradientStart
import com.alejandro.appgym.ui.theme.Surface1
import com.alejandro.appgym.ui.theme.Surface2
import com.alejandro.appgym.ui.theme.TextPrimary
import com.alejandro.appgym.ui.theme.TextSecondary
import com.alejandro.appgym.ui.theme.TextTertiary
import com.alejandro.appgym.ui.theme.VoltBorder
import com.alejandro.appgym.ui.theme.VoltBorderStrong
import com.alejandro.appgym.ui.theme.accentColor
import com.alejandro.appgym.util.clockText
import com.alejandro.appgym.util.dayInitial
import com.alejandro.appgym.util.distanceText
import com.alejandro.appgym.util.durationText
import com.alejandro.appgym.util.shortText
import com.alejandro.appgym.util.volumeShort
import com.alejandro.appgym.util.weightText
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

@Composable
fun TodayScreen(vm: GymViewModel, nav: NavHostController) {

    val state by vm.dayState.collectAsStateWithLifecycle()
    val routines by vm.routines.collectAsStateWithLifecycle()
    val diasDescanso by vm.restDays.collectAsStateWithLifecycle()
    val planActivo by vm.activePlan.collectAsStateWithLifecycle()
    val perfil by vm.profile.collectAsStateWithLifecycle()
    val accent = MaterialTheme.colorScheme.primary

    var showTimes by remember { mutableStateOf(false) }
    var showCalendar by remember { mutableStateOf(false) }
    var confirmClear by remember { mutableStateOf(false) }
    var cambiando by remember { mutableStateOf<String?>(null) }
    var expandido by remember { mutableStateOf<String?>(null) }

    // El ejercicio en curso se abre solo: es el primero con series pendientes.
    val enCurso = state.blocks.firstOrNull { it.doneSets < it.sets.size }?.exerciseId
    val abierto = expandido ?: enCurso

    LazyColumn(
        Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars),
        contentPadding = PaddingValues(bottom = 40.dp)
    ) {

        /* ---------------- Cabecera ---------------- */
        item {
            Row(
                Modifier.padding(start = Gutter, end = Gutter, top = 14.dp, bottom = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text(
                        saludo(state.date, perfil.name),
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextSecondary
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        state.session?.title ?: "Entrenamiento",
                        style = MaterialTheme.typography.headlineLarge,
                        color = TextPrimary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(Modifier.width(12.dp))
                TiempoPill(state.session, accent) { showTimes = true }
            }
        }

        /* ---------------- Tira de días ---------------- */
        item {
            TiraDeDias(
                seleccionado = state.date,
                descanso = diasDescanso,
                onSelect = vm::selectDate,
                onCalendario = { showCalendar = true }
            )
        }

        /* ---------------- Resumen ---------------- */
        if (!state.isEmpty) {
            item { Spacer(Modifier.height(16.dp)) }
            item { ResumenSesion(state.doneSets, state.totalSets, state.totalVolume) }
        }

        /* ---------------- Ejercicios ---------------- */
        if (!state.isEmpty) {
            item {
                SectionTitle("Sesión de hoy") {
                    Text(
                        "Tu plan · ${SeedData.groupOf(planActivo)?.name ?: ""}",
                        style = MaterialTheme.typography.labelMedium,
                        color = accent,
                        modifier = Modifier.clickable { nav.navigate(Routes.PLAN) }
                    )
                }
            }
        }

        items(state.blocks, key = { it.exerciseId }) { block ->
            TarjetaEjercicio(
                block = block,
                expandida = abierto == block.exerciseId,
                onToggle = {
                    expandido = if (abierto == block.exerciseId) "" else block.exerciseId
                },
                onUpdate = vm::updateSet,
                onMarcar = vm::toggleDone,
                onBorrarSerie = vm::deleteSetWithUndo,
                onAddSet = { vm.addSet(block.exerciseId) },
                onQuitarEjercicio = { vm.removeExercise(block.exerciseId) },
                onCambiar = { cambiando = block.exerciseId },
                onAbrirFicha = { nav.navigate(Routes.exercise(block.exerciseId)) }
            )
            Spacer(Modifier.height(10.dp))
        }

        /* ---------------- Día vacío ---------------- */
        if (state.session?.restDay == true) {
            item {
                EmptyState(
                    icon = Icons.Rounded.Hotel,
                    title = "Día de descanso",
                    message = "El descanso es parte del plan: es cuando el músculo se construye.",
                    action = {
                        BotonSecundario(
                            text = "Quitar el descanso",
                            icon = Icons.AutoMirrored.Rounded.Undo,
                            onClick = { vm.toggleRestDay() }
                        )
                    }
                )
            }
        } else if (state.isEmpty) {
            // Qué toca hoy según el día de la semana del plan activo.
            val delDia = SeedData.dayFor(planActivo, state.date.dayOfWeek.value)
            val rutinaDelDia = delDia?.let { d -> routines.firstOrNull { it.name == d.name } }

            item {
                if (delDia != null && rutinaDelDia != null) {
                    TarjetaQueToca(
                        nombre = rutinaDelDia.name,
                        detalle = "${delDia.items.size} ejercicios · " +
                            "${delDia.items.sumOf { it.sets }} series",
                        onEmpezar = { vm.startFromRoutine(rutinaDelDia.id) }
                    )
                } else {
                    EmptyState(
                        icon = Icons.Rounded.Hotel,
                        title = "Hoy toca descansar",
                        message = "Tu plan no tiene sesión asignada a este día. " +
                            "Puedes marcarlo como descanso o entrenar igualmente."
                    )
                }
            }

            if (routines.isNotEmpty()) {
                item {
                    SectionTitle(if (rutinaDelDia != null) "O carga otra" else "Cargar una rutina")
                }
                items(routines.take(6), key = { it.id }) { rutina ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = Gutter, vertical = 4.dp)
                            .clip(MaterialTheme.shapes.medium)
                            .background(Surface1)
                            .clickable { vm.startFromRoutine(rutina.id) }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            rutina.name,
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            Icons.Rounded.Add, null,
                            tint = accentColor(rutina.accent),
                            modifier = Modifier.size(19.dp)
                        )
                    }
                }
            }
        }

        /* ---------------- Acciones ---------------- */
        item { Spacer(Modifier.height(18.dp)) }
        if (state.session?.restDay != true) item {
            Column(Modifier.padding(horizontal = Gutter)) {
                BotonPildora(
                    text = "Añadir ejercicio suelto",
                    icon = Icons.Rounded.Add,
                    onClick = { nav.navigate(Routes.picker(-1L)) }
                )
                // Marcar descanso solo cuando el día está vacío: hacerlo con
                // series ya apuntadas las borraría.
                if (state.isEmpty) {
                    Spacer(Modifier.height(10.dp))
                    BotonSecundario(
                        text = "Marcar día de descanso",
                        icon = Icons.Rounded.Hotel,
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { vm.toggleRestDay() }
                    )
                }
                if (!state.isEmpty) {
                    Spacer(Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        BotonSecundario(
                            text = "Cambiar de rutina",
                            icon = Icons.Rounded.SwapHoriz,
                            modifier = Modifier.weight(1f),
                            onClick = { confirmClear = true }
                        )
                        BotonSecundario(
                            text = if (state.session?.hasTimes == true) "Editar horas" else "Poner horas",
                            icon = Icons.Rounded.Schedule,
                            modifier = Modifier.weight(1f),
                            onClick = { showTimes = true }
                        )
                    }
                }
            }
        }
    }

    /* ---------------- Diálogos ---------------- */

    if (showTimes) {
        DialogoHoras(
            sesion = state.session,
            onDismiss = { showTimes = false },
            onSave = { ini, fin ->
                showTimes = false
                vm.setTimes(ini, fin)
            }
        )
    }

    if (showCalendar) {
        DialogoCalendario(
            fecha = state.date,
            onDismiss = { showCalendar = false },
            onSelect = {
                showCalendar = false
                vm.selectDate(it)
            }
        )
    }

    cambiando?.let { exerciseId ->
        DialogoAlternativas(
            exerciseId = exerciseId,
            onDismiss = { cambiando = null },
            onElegir = {
                cambiando = null
                vm.swapExercise(exerciseId, it)
            },
            onVerTodos = {
                cambiando = null
                nav.navigate(Routes.swap(exerciseId))
            }
        )
    }

    if (confirmClear) {
        AlertDialog(
            onDismissRequest = { confirmClear = false },
            containerColor = Surface2,
            shape = MaterialTheme.shapes.large,
            title = { Text("Vaciar el día", style = MaterialTheme.typography.titleLarge, color = TextPrimary) },
            text = {
                Text(
                    "Se borran los ejercicios de hoy para que puedas cargar otra rutina. " +
                        "Los demás días no se tocan.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextSecondary
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    confirmClear = false
                    expandido = null
                    vm.clearDay()
                }) { Text("Vaciar", color = Danger) }
            },
            dismissButton = {
                TextButton(onClick = { confirmClear = false }) {
                    Text("Cancelar", color = TextTertiary)
                }
            }
        )
    }
}

private fun saludo(fecha: LocalDate, nombre: String): String = when (fecha) {
    LocalDate.now() -> "Hola, ${nombre.ifBlank { "qué tal" }}"
    LocalDate.now().minusDays(1) -> "Ayer"
    LocalDate.now().plusDays(1) -> "Mañana"
    else -> com.alejandro.appgym.util.relativeDay(fecha)
}

/* ------------------------------------------------------------------ */
/*  Cabecera                                                           */
/* ------------------------------------------------------------------ */

@Composable
private fun TiempoPill(sesion: WorkoutSession?, accent: Color, onClick: () -> Unit) {
    val tiene = sesion?.hasTimes == true
    Row(
        Modifier
            .clip(Pill)
            .background(Control)
            .clickable(onClick = onClick)
            .padding(horizontal = 15.dp, vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            Icons.Rounded.Schedule, null,
            tint = if (tiene) accent else TextTertiary,
            modifier = Modifier.size(16.dp)
        )
        Spacer(Modifier.width(7.dp))
        Text(
            if (tiene) sesion!!.durationMinutes.durationText() else "Poner hora",
            style = MaterialTheme.typography.labelMedium,
            color = if (tiene) TextPrimary else TextSecondary
        )
    }
}

@Composable
private fun TiraDeDias(
    seleccionado: LocalDate,
    descanso: Set<Long>,
    onSelect: (LocalDate) -> Unit,
    onCalendario: () -> Unit
) {
    val hoy = LocalDate.now()
    val accent = MaterialTheme.colorScheme.primary

    // La tira es siempre una semana de verdad, de lunes a domingo. Antes era una
    // ventana móvil alrededor del día elegido, así que las letras bailaban.
    val lunes = seleccionado.minusDays((seleccionado.dayOfWeek.value - 1).toLong())
    val dias = (0..6).map { lunes.plusDays(it.toLong()) }

    val lunesDeHoy = hoy.minusDays((hoy.dayOfWeek.value - 1).toLong())
    val etiqueta = when (lunes) {
        lunesDeHoy -> "Esta semana"
        lunesDeHoy.minusWeeks(1) -> "Semana pasada"
        lunesDeHoy.plusWeeks(1) -> "Semana que viene"
        else -> "${lunes.shortText()} – ${dias.last().shortText()}"
    }

    Column(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = Gutter)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                etiqueta,
                style = MaterialTheme.typography.labelMedium,
                color = TextSecondary,
                modifier = Modifier.weight(1f)
            )
            BotonTira(Icons.Rounded.ChevronLeft, "Semana anterior") {
                onSelect(seleccionado.minusWeeks(1))
            }
            Spacer(Modifier.width(5.dp))
            BotonTira(Icons.Rounded.ChevronRight, "Semana siguiente") {
                onSelect(seleccionado.plusWeeks(1))
            }
            Spacer(Modifier.width(5.dp))
            BotonTira(Icons.Rounded.CalendarMonth, "Elegir fecha", onCalendario)
        }

        Spacer(Modifier.height(8.dp))

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            dias.forEach { dia ->
                val activo = dia == seleccionado
                val esHoy = dia == hoy
                Column(
                    Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { onSelect(dia) }
                        .padding(vertical = 5.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        dia.dayInitial(),
                        style = MaterialTheme.typography.labelMedium,
                        color = if (activo) TextSecondary else TextTertiary
                    )
                    Spacer(Modifier.height(4.dp))
                    // Solo el número se rellena: el bloque entero en lima era
                    // demasiado pesado y chocaba con la tarjeta de abajo.
                    Box(
                        Modifier
                            .size(34.dp)
                            .clip(Pill)
                            .background(if (activo) accent else Color.Transparent)
                            .then(
                                if (esHoy && !activo) Modifier.border(1.dp, VoltBorderStrong, Pill)
                                else Modifier
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            dia.dayOfMonth.toString(),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = if (activo || esHoy) FontWeight.Bold
                                else FontWeight.Medium
                            ),
                            color = when {
                                activo -> MaterialTheme.colorScheme.onPrimary
                                esHoy -> accent
                                else -> TextSecondary
                            }
                        )
                    }
                    Spacer(Modifier.height(4.dp))
                    // Punto bajo el número en los días marcados como descanso.
                    Box(
                        Modifier
                            .size(4.dp)
                            .clip(Pill)
                            .background(
                                if (dia.toEpochDay() in descanso) {
                                    if (activo) accent else TextTertiary
                                } else Color.Transparent
                            )
                    )
                }
            }
        }
    }
}

@Composable
private fun BotonTira(icon: ImageVector, desc: String, onClick: () -> Unit) {
    Box(
        Modifier
            .size(32.dp)
            .clip(Pill)
            .background(Control)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, desc, tint = TextSecondary, modifier = Modifier.size(17.dp))
    }
}

@Composable
private fun ResumenSesion(hechas: Int, total: Int, volumen: Double) {
    val progreso = if (total == 0) 0f else hechas.toFloat() / total
    Surface(
        shape = MaterialTheme.shapes.large,
        color = Surface1,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Gutter)
    ) {
        Row(
            Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ProgressRing(progreso)
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    mensajeSesion(progreso, total),
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary
                )
                Spacer(Modifier.height(3.dp))
                Text(
                    "$hechas de $total series · ${volumen.volumeShort()} kg movidos",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextSecondary
                )
            }
        }
    }
}

private fun mensajeSesion(progreso: Float, total: Int): String = when {
    total == 0 -> "Sin series todavía"
    progreso >= 1f -> "Sesión completada"
    progreso >= 0.5f -> "Buena sesión, sigue así"
    progreso > 0f -> "Ya has empezado"
    else -> "Cuando quieras"
}

/* ------------------------------------------------------------------ */
/*  Tarjeta de ejercicio                                               */
/* ------------------------------------------------------------------ */

@Composable
private fun TarjetaEjercicio(
    block: ExerciseBlock,
    expandida: Boolean,
    onToggle: () -> Unit,
    onUpdate: (SetEntry) -> Unit,
    onMarcar: (SetEntry) -> Unit,
    onBorrarSerie: (SetEntry) -> Unit,
    onAddSet: () -> Unit,
    onQuitarEjercicio: () -> Unit,
    onCambiar: () -> Unit,
    onAbrirFicha: () -> Unit
) {
    val exercise = ExerciseCatalog.require(block.exerciseId)
    val accent = MaterialTheme.colorScheme.primary
    val completado = block.doneSets == block.sets.size
    val activa = block.sets.firstOrNull { !it.done }
    var confirmarQuitar by remember { mutableStateOf(false) }

    Surface(
        shape = MaterialTheme.shapes.large,
        color = Surface1,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Gutter)
            .animateContentSize(tween(220))
    ) {
        Column {
            /* Cabecera */
            Row(
                Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onToggle)
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(Modifier.clickable(onClick = onAbrirFicha)) {
                    ExerciseThumb(exercise, 54)
                }
                Spacer(Modifier.width(14.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        exercise.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.height(3.dp))
                    Text(
                        if (completado) "Completado · ${block.sets.size} de ${block.sets.size} series"
                        else "En curso · ${block.doneSets} de ${block.sets.size} series",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
                Spacer(Modifier.width(10.dp))
                if (completado && !expandida) {
                    Box(
                        Modifier
                            .size(30.dp)
                            .clip(Pill)
                            .background(accent.copy(alpha = 0.16f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Rounded.Check, null, tint = accent, modifier = Modifier.size(17.dp))
                    }
                } else {
                    Icon(
                        if (expandida) Icons.Rounded.ExpandLess else Icons.Rounded.ExpandMore,
                        null,
                        tint = TextTertiary,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            /* Contenido */
            AnimatedVisibility(expandida) {
                Column(Modifier.padding(start = 14.dp, end = 14.dp, bottom = 14.dp)) {

                    block.sets.forEach { serie ->
                        if (serie.done) {
                            SerieHecha(
                                serie = serie,
                                cardio = exercise.isCardio,
                                onDeshacer = { onMarcar(serie) },
                                onBorrar = { onBorrarSerie(serie) }
                            )
                            Spacer(Modifier.height(8.dp))
                        } else if (serie.id == activa?.id) {
                            SerieActiva(
                                serie = serie,
                                cardio = exercise.isCardio,
                                porMancuerna = exercise.equipment == "Mancuernas",
                                onUpdate = onUpdate,
                                onMarcar = { onMarcar(serie) }
                            )
                            Spacer(Modifier.height(8.dp))
                        } else {
                            SeriePendiente(
                                serie = serie,
                                cardio = exercise.isCardio,
                                onBorrar = { onBorrarSerie(serie) }
                            )
                            Spacer(Modifier.height(8.dp))
                        }
                    }

                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .clickable(onClick = onAddSet)
                            .padding(vertical = 11.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Rounded.Add, null, tint = TextSecondary, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(7.dp))
                        Text(
                            "Añadir otra serie",
                            style = MaterialTheme.typography.labelMedium,
                            color = TextSecondary
                        )
                    }

                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AccionMenor(
                            icon = Icons.Rounded.SwapHoriz,
                            text = "Cambiar por otro",
                            onClick = onCambiar,
                            modifier = Modifier.weight(1f)
                        )
                        AccionMenor(
                            icon = Icons.Rounded.DeleteOutline,
                            text = "Quitar",
                            onClick = { confirmarQuitar = true },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }

    if (confirmarQuitar) {
        AlertDialog(
            onDismissRequest = { confirmarQuitar = false },
            containerColor = Surface2,
            shape = MaterialTheme.shapes.large,
            title = { Text("Quitar ejercicio", style = MaterialTheme.typography.titleLarge, color = TextPrimary) },
            text = {
                Text(
                    "Se borra «${exercise.name}» de la sesión de hoy con todas sus series.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextSecondary
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    confirmarQuitar = false
                    onQuitarEjercicio()
                }) { Text("Quitar", color = Danger) }
            },
            dismissButton = {
                TextButton(onClick = { confirmarQuitar = false }) {
                    Text("Cancelar", color = TextTertiary)
                }
            }
        )
    }
}

@Composable
private fun SerieHecha(
    serie: SetEntry,
    cardio: Boolean,
    onDeshacer: () -> Unit,
    onBorrar: () -> Unit
) {
    val accent = MaterialTheme.colorScheme.primary
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Surface2)
            .padding(horizontal = 14.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            Icons.Rounded.CheckCircle, "Hecha",
            tint = accent,
            modifier = Modifier
                .size(18.dp)
                .clickable(onClick = onDeshacer)
        )
        Spacer(Modifier.width(12.dp))
        Text(
            textoSerie(serie, cardio),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Normal),
            color = TextSecondary,
            modifier = Modifier.weight(1f)
        )
        Icon(
            Icons.Rounded.Close, "Borrar serie",
            tint = TextTertiary,
            modifier = Modifier
                .size(16.dp)
                .clickable(onClick = onBorrar)
        )
    }
}

@Composable
private fun SeriePendiente(serie: SetEntry, cardio: Boolean, onBorrar: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Surface2.copy(alpha = 0.45f))
            .padding(horizontal = 14.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            "Serie ${serie.setNumber}",
            style = MaterialTheme.typography.bodyMedium,
            color = TextTertiary,
            modifier = Modifier.weight(1f)
        )
        Text(
            textoSerie(serie, cardio),
            style = MaterialTheme.typography.bodyMedium,
            color = TextTertiary
        )
        Spacer(Modifier.width(12.dp))
        Icon(
            Icons.Rounded.Close, "Borrar serie",
            tint = TextTertiary,
            modifier = Modifier
                .size(16.dp)
                .clickable(onClick = onBorrar)
        )
    }
}

@Composable
private fun SerieActiva(
    serie: SetEntry,
    cardio: Boolean,
    porMancuerna: Boolean,
    onUpdate: (SetEntry) -> Unit,
    onMarcar: () -> Unit
) {
    val accent = MaterialTheme.colorScheme.primary

    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Surface2)
            .border(1.dp, VoltBorderStrong, RoundedCornerShape(18.dp))
            .padding(14.dp)
    ) {
        Text(
            "SERIE ${serie.setNumber}",
            style = MaterialTheme.typography.labelSmall,
            color = TextTertiary
        )
        Spacer(Modifier.height(11.dp))

        if (cardio) {
            StepperField(
                value = serie.minutes.toString(),
                suffix = "min",
                onDecrement = { onUpdate(serie.copy(minutes = (serie.minutes - 1).coerceAtLeast(0))) },
                onIncrement = { onUpdate(serie.copy(minutes = serie.minutes + 1)) },
                onValueChange = { onUpdate(serie.copy(minutes = it.toIntOrNull() ?: 0)) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
            Text(
                "De la pantalla de la máquina",
                style = MaterialTheme.typography.bodyMedium,
                color = TextTertiary
            )
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                CampoCardio(
                    etiqueta = "km",
                    valor = if (serie.distanceKm > 0) serie.distanceKm.distanceText() else "",
                    decimales = true,
                    modifier = Modifier.weight(1f)
                ) { onUpdate(serie.copy(distanceKm = it.toWeightOrNull() ?: 0.0)) }
                CampoCardio(
                    etiqueta = "kcal",
                    valor = if (serie.calories > 0) serie.calories.toString() else "",
                    modifier = Modifier.weight(1f)
                ) { onUpdate(serie.copy(calories = it.toIntOrNull() ?: 0)) }
                CampoCardio(
                    etiqueta = "ppm",
                    valor = if (serie.avgHeartRate > 0) serie.avgHeartRate.toString() else "",
                    modifier = Modifier.weight(1f)
                ) { onUpdate(serie.copy(avgHeartRate = it.toIntOrNull() ?: 0)) }
            }
        } else {
            Row(verticalAlignment = Alignment.CenterVertically) {
                StepperField(
                    value = serie.weight.weightText(),
                    suffix = "kg",
                    onDecrement = { onUpdate(serie.copy(weight = (serie.weight - 2.5).coerceAtLeast(0.0))) },
                    onIncrement = { onUpdate(serie.copy(weight = serie.weight + 2.5)) },
                    onValueChange = { onUpdate(serie.copy(weight = it.toWeightOrNull() ?: 0.0)) },
                    decimals = true,
                    modifier = Modifier.weight(1f)
                )
                Box(
                    Modifier
                        .padding(horizontal = 10.dp)
                        .size(width = 1.dp, height = 24.dp)
                        .background(Outline)
                )
                StepperField(
                    value = serie.reps.toString(),
                    suffix = "",
                    onDecrement = { onUpdate(serie.copy(reps = (serie.reps - 1).coerceAtLeast(0))) },
                    onIncrement = { onUpdate(serie.copy(reps = serie.reps + 1)) },
                    onValueChange = { onUpdate(serie.copy(reps = it.toIntOrNull() ?: 0)) },
                    modifier = Modifier.weight(1f)
                )
            }
            if (porMancuerna) {
                Spacer(Modifier.height(9.dp))
                Text(
                    "Apunta el peso de una mancuerna",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextTertiary
                )
            }
        }

        Spacer(Modifier.height(13.dp))
        Row(
            Modifier
                .fillMaxWidth()
                .height(46.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(accent)
                .clickable(onClick = onMarcar),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Rounded.Check, null,
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                "Marcar serie",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

private fun textoSerie(serie: SetEntry, cardio: Boolean): String = if (cardio) {
    buildString {
        append("${serie.minutes} min")
        if (serie.distanceKm > 0) append(" · ${serie.distanceKm.distanceText()} km")
        if (serie.calories > 0) append(" · ${serie.calories} kcal")
        if (serie.avgHeartRate > 0) append(" · ${serie.avgHeartRate} ppm")
    }
} else {
    "${serie.weight.weightText()} kg × ${serie.reps}"
}

/** Campo pequeño para los datos que muestra la máquina al acabar. */
@Composable
private fun CampoCardio(
    etiqueta: String,
    valor: String,
    modifier: Modifier = Modifier,
    decimales: Boolean = false,
    onChange: (String) -> Unit
) {
    Column(modifier) {
        Text(etiqueta, style = MaterialTheme.typography.bodyMedium, color = TextTertiary)
        Spacer(Modifier.height(5.dp))
        NumberField(
            value = valor,
            onValueChange = onChange,
            decimals = decimales,
            width = 0,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

/* ------------------------------------------------------------------ */
/*  Botones                                                            */
/* ------------------------------------------------------------------ */

/** Botón principal en píldora. El acento se reserva para "Marcar serie". */
@Composable
fun BotonPildora(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(Pill)
            .background(Control)
            .border(1.dp, Outline, Pill)
            .clickable(onClick = onClick),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = TextPrimary, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(9.dp))
        Text(text, style = MaterialTheme.typography.labelLarge, color = TextPrimary)
    }
}

@Composable
fun BotonSecundario(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier
            .height(46.dp)
            .clip(Pill)
            .background(Control)
            .clickable(onClick = onClick),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = TextSecondary, modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(7.dp))
        Text(text, style = MaterialTheme.typography.labelMedium, color = TextSecondary)
    }
}

/* ------------------------------------------------------------------ */
/*  Diálogos                                                           */
/* ------------------------------------------------------------------ */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DialogoCalendario(
    fecha: LocalDate,
    onDismiss: () -> Unit,
    onSelect: (LocalDate) -> Unit
) {
    val estado = rememberDatePickerState(
        initialSelectedDateMillis = fecha.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
    )
    DatePickerDialog(
        onDismissRequest = onDismiss,
        colors = androidx.compose.material3.DatePickerDefaults.colors(containerColor = Surface2),
        confirmButton = {
            TextButton(onClick = {
                estado.selectedDateMillis?.let {
                    onSelect(Instant.ofEpochMilli(it).atZone(ZoneOffset.UTC).toLocalDate())
                }
            }) { Text("Ir", color = MaterialTheme.colorScheme.primary) }
        },
        dismissButton = {
            TextButton(onClick = { onSelect(LocalDate.now()) }) {
                Text("Hoy", color = TextSecondary)
            }
        }
    ) {
        DatePicker(
            state = estado,
            colors = androidx.compose.material3.DatePickerDefaults.colors(containerColor = Surface2)
        )
    }
}

@Composable
private fun DialogoHoras(
    sesion: WorkoutSession?,
    onDismiss: () -> Unit,
    onSave: (Int, Int) -> Unit
) {
    val ahora = java.time.LocalTime.now()
    val minutoAhora = ahora.hour * 60 + ahora.minute

    var ini by remember {
        mutableIntStateOf(if (sesion?.hasTimes == true) sesion.startMinute else (minutoAhora - 60).coerceAtLeast(0))
    }
    var fin by remember {
        mutableIntStateOf(if (sesion?.hasTimes == true) sesion.endMinute else minutoAhora)
    }
    val duracion = if (fin >= ini) fin - ini else (24 * 60 - ini) + fin

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Surface2,
        shape = MaterialTheme.shapes.large,
        icon = { Icon(Icons.Rounded.Schedule, null, tint = MaterialTheme.colorScheme.primary) },
        title = { Text("Horas en el gimnasio", style = MaterialTheme.typography.titleLarge, color = TextPrimary) },
        text = {
            Column {
                SelectorHora("Entrada", ini) { ini = it }
                Spacer(Modifier.height(18.dp))
                SelectorHora("Salida", fin) { fin = it }
                Spacer(Modifier.height(18.dp))
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.10f))
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Duración", style = MaterialTheme.typography.bodyLarge, color = TextSecondary)
                    Spacer(Modifier.weight(1f))
                    Text(
                        duracion.durationText(),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onSave(ini, fin) }) {
                Text("Guardar", color = MaterialTheme.colorScheme.primary)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar", color = TextTertiary) }
        }
    )
}

@Composable
private fun SelectorHora(etiqueta: String, minutos: Int, onChange: (Int) -> Unit) {
    Column {
        Text(etiqueta, style = MaterialTheme.typography.bodyMedium, color = TextTertiary)
        Spacer(Modifier.height(8.dp))
        StepperField(
            value = minutos.clockText(),
            suffix = "",
            onDecrement = { onChange(((minutos - 15) + 24 * 60) % (24 * 60)) },
            onIncrement = { onChange((minutos + 15) % (24 * 60)) },
            onValueChange = { },
            editable = false,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

/** Acción secundaria dentro de la tarjeta de ejercicio. */
@Composable
private fun AccionMenor(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier
            .clip(Pill)
            .clickable(onClick = onClick)
            .padding(vertical = 9.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = TextTertiary, modifier = Modifier.size(15.dp))
        Spacer(Modifier.width(6.dp))
        Text(text, style = MaterialTheme.typography.bodyMedium, color = TextTertiary)
    }
}

/**
 * Sustitutos para cuando la máquina está ocupada o el movimiento molesta.
 * Se conservan las series, las repeticiones y el peso.
 */
@Composable
private fun DialogoAlternativas(
    exerciseId: String,
    onDismiss: () -> Unit,
    onElegir: (String) -> Unit,
    onVerTodos: () -> Unit
) {
    val original = ExerciseCatalog.require(exerciseId)
    val alternativas = remember(exerciseId) { ExerciseCatalog.alternativesFor(exerciseId, 5) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Surface2,
        shape = MaterialTheme.shapes.large,
        title = {
            Column {
                Text(
                    "Cambiar ejercicio",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary
                )
                Spacer(Modifier.height(3.dp))
                Text(
                    "En lugar de ${original.name}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextTertiary
                )
            }
        },
        text = {
            Column {
                if (alternativas.isEmpty()) {
                    Text(
                        "No encuentro un sustituto parecido en el catálogo.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextSecondary
                    )
                } else {
                    Text(
                        "Trabajan lo mismo. Se conservan tus series y el peso.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextTertiary
                    )
                    Spacer(Modifier.height(12.dp))
                    alternativas.forEach { alt ->
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .clickable { onElegir(alt.id) }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            ExerciseThumb(alt, 44)
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text(
                                    alt.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = TextPrimary,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(Modifier.height(2.dp))
                                Text(
                                    alt.equipment,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextTertiary
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onVerTodos) {
                Text("Ver todos", color = MaterialTheme.colorScheme.primary)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar", color = TextTertiary) }
        }
    )
}

/** Lo que toca hoy según el plan. Un toque y la sesión queda cargada. */
@Composable
private fun TarjetaQueToca(
    nombre: String,
    detalle: String,
    onEmpezar: () -> Unit
) {
    val accent = MaterialTheme.colorScheme.primary
    Box(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = Gutter)
            .clip(MaterialTheme.shapes.large)
            .background(
                Brush.linearGradient(0f to PlanGradientStart, 0.62f to PlanGradientEnd)
            )
            .border(1.dp, VoltBorder, MaterialTheme.shapes.large)
            .padding(18.dp)
    ) {
        Column {
            Badge("Hoy toca", accent)
            Spacer(Modifier.height(12.dp))
            Text(nombre, style = MaterialTheme.typography.headlineSmall, color = TextPrimary)
            Spacer(Modifier.height(4.dp))
            Text(detalle, style = MaterialTheme.typography.bodyLarge, color = TextSecondary)
            Spacer(Modifier.height(16.dp))
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(Pill)
                    .background(accent)
                    .clickable(onClick = onEmpezar),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Rounded.PlayArrow, null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(19.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    "Empezar",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}
