package com.alejandro.appgym.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.alejandro.appgym.GymViewModel
import com.alejandro.appgym.data.ExerciseCatalog
import com.alejandro.appgym.data.RoutineItem
import com.alejandro.appgym.ui.Routes
import com.alejandro.appgym.ui.components.AppCard
import com.alejandro.appgym.ui.components.CircleIconButton
import com.alejandro.appgym.ui.components.EmptyState
import com.alejandro.appgym.ui.components.ExerciseThumb
import com.alejandro.appgym.ui.components.Gutter
import com.alejandro.appgym.ui.components.SectionTitle
import com.alejandro.appgym.ui.theme.Danger
import com.alejandro.appgym.ui.theme.Surface2
import com.alejandro.appgym.ui.theme.TextPrimary
import com.alejandro.appgym.ui.theme.TextSecondary
import com.alejandro.appgym.ui.theme.TextTertiary
import com.alejandro.appgym.ui.theme.accentColor
import com.alejandro.appgym.ui.theme.groupColor
import com.alejandro.appgym.util.weightText

@Composable
fun RoutineDetailScreen(routineId: Long, vm: GymViewModel, nav: NavHostController) {

    val routines by vm.routines.collectAsStateWithLifecycle()
    val allItems by vm.routineItems.collectAsStateWithLifecycle()
    val routine = routines.firstOrNull { it.id == routineId }
    val items = allItems.filter { it.routineId == routineId }.sortedBy { it.position }
    val accent = accentColor(routine?.accent ?: 0)
    var confirmDelete by remember { mutableStateOf(false) }

    Column(
        Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircleIconButton(Icons.AutoMirrored.Rounded.ArrowBack, "Atrás", { nav.popBackStack() }, size = 40)
            Spacer(Modifier.width(4.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    routine?.name ?: "Rutina",
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (routine?.dayHint?.isNotBlank() == true) {
                    Spacer(Modifier.height(2.dp))
                    Text(
                        routine.dayHint,
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextSecondary
                    )
                }
            }
            CircleIconButton(Icons.Rounded.DeleteOutline, "Eliminar rutina", { confirmDelete = true }, size = 40)
        }

        LazyColumn(
            Modifier.weight(1f),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            if (items.isEmpty()) {
                item {
                    EmptyState(
                        icon = Icons.Rounded.Add,
                        title = "Rutina vacía",
                        message = "Añade las máquinas de esta rutina y ajusta series, repeticiones y peso objetivo."
                    )
                }
            } else {
                item { SectionTitle("${items.size} ejercicios · ${items.sumOf { it.targetSets }} series") }
                items(items, key = { it.id }) { item ->
                    RoutineItemCard(
                        item = item,
                        onUpdate = vm::updateRoutineItem,
                        onDelete = { vm.deleteRoutineItem(item) },
                        onOpen = { nav.navigate(Routes.exercise(item.exerciseId)) }
                    )
                }
            }
        }

        Row(
            Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            BotonPildora(
                text = "Añadir ejercicio",
                icon = Icons.Rounded.Add,
                modifier = Modifier.weight(1f),
                onClick = { nav.navigate(Routes.picker(routineId)) }
            )
            if (items.isNotEmpty()) {
                CircleIconButton(
                    icon = Icons.Rounded.PlayArrow,
                    contentDescription = "Empezar hoy",
                    onClick = {
                        vm.startFromRoutine(routineId)
                        nav.navigate(Routes.TODAY) { popUpTo(Routes.TODAY) { inclusive = true } }
                    },
                    size = 52,
                    background = MaterialTheme.colorScheme.primary,
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }

    if (confirmDelete) {
        AlertDialog(
            onDismissRequest = { confirmDelete = false },
            containerColor = Surface2,
            shape = MaterialTheme.shapes.large,
            title = { Text("Eliminar rutina", color = TextPrimary) },
            text = {
                Text(
                    "Se borrará «${routine?.name ?: ""}» y sus ejercicios planificados. Tu historial de entrenamientos no se toca.",
                    color = TextSecondary
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    confirmDelete = false
                    vm.deleteRoutine(routineId)
                    nav.popBackStack()
                }) { Text("Eliminar", color = Danger) }
            },
            dismissButton = {
                TextButton(onClick = { confirmDelete = false }) {
                    Text("Cancelar", color = TextTertiary)
                }
            }
        )
    }
}

@Composable
private fun RoutineItemCard(
    item: RoutineItem,
    onUpdate: (RoutineItem) -> Unit,
    onDelete: () -> Unit,
    onOpen: () -> Unit
) {
    val exercise = ExerciseCatalog.require(item.exerciseId)
    val accent = groupColor(exercise.group)

    AppCard(
        modifier = Modifier.padding(horizontal = Gutter, vertical = 6.dp),
        contentPadding = PaddingValues(14.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.clickable(onClick = onOpen)) {
                    ExerciseThumb(exercise, 48)
                }
                Spacer(Modifier.width(12.dp))
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
                        if (exercise.isCardio) exercise.group else "${exercise.group} · descanso ${item.restSeconds}s",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextTertiary
                    )
                }
                Icon(
                    Icons.Rounded.DeleteOutline, "Quitar",
                    tint = TextTertiary,
                    modifier = Modifier.size(18.dp).clickable(onClick = onDelete)
                )
            }

            Spacer(Modifier.height(14.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                LabeledField("Series", item.targetSets.toString(), 76) {
                    onUpdate(item.copy(targetSets = it.toIntOrNull()?.coerceIn(1, 20) ?: 1))
                }
                if (exercise.isCardio) {
                    LabeledField("Minutos", item.targetMinutes.toString(), 88) {
                        onUpdate(item.copy(targetMinutes = it.toIntOrNull() ?: 0))
                    }
                } else {
                    LabeledField("Reps", item.targetReps.toString(), 76) {
                        onUpdate(item.copy(targetReps = it.toIntOrNull() ?: 0))
                    }
                    LabeledField("Peso kg", item.targetWeight.weightText(), 88, decimals = true) {
                        onUpdate(item.copy(targetWeight = it.toWeightOrNull() ?: 0.0))
                    }
                }
            }

            if (item.note.isNotBlank()) {
                Spacer(Modifier.height(12.dp))
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(accent.copy(alpha = 0.09f))
                        .padding(11.dp)
                ) {
                    Box(
                        Modifier
                            .size(width = 3.dp, height = 16.dp)
                            .clip(CircleShape)
                            .background(accent)
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        item.note,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
            }

            if (!exercise.isCardio) {
            Spacer(Modifier.height(10.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf(60, 90, 120, 180).forEach { sec ->
                    val selected = item.restSeconds == sec
                    Box(
                        Modifier
                            .clip(CircleShape)
                            .background(if (selected) accent.copy(alpha = 0.18f) else Surface2)
                            .clickable { onUpdate(item.copy(restSeconds = sec)) }
                            .padding(horizontal = 11.dp, vertical = 6.dp)
                    ) {
                        Text(
                            "${sec}s",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (selected) accent else TextTertiary
                        )
                    }
                }
            }
            }
        }
    }
}

@Composable
private fun LabeledField(
    label: String,
    value: String,
    width: Int,
    decimals: Boolean = false,
    onValueChange: (String) -> Unit
) {
    Column {
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium,
            color = TextTertiary
        )
        Spacer(Modifier.height(5.dp))
        NumberField(
            value = value,
            onValueChange = onValueChange,
            width = width,
            decimals = decimals
        )
    }
}
