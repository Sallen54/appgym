package com.alejandro.appgym.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.Repeat
import androidx.compose.material.icons.rounded.Timeline
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.alejandro.appgym.GymViewModel
import com.alejandro.appgym.data.ExerciseCatalog
import com.alejandro.appgym.ui.components.AppCard
import com.alejandro.appgym.ui.components.AssetImage
import com.alejandro.appgym.ui.components.CircleIconButton
import com.alejandro.appgym.ui.components.Gutter
import com.alejandro.appgym.ui.components.ProgressLine
import com.alejandro.appgym.ui.components.SectionTitle
import com.alejandro.appgym.ui.components.StatCard
import com.alejandro.appgym.ui.theme.Ink
import com.alejandro.appgym.ui.theme.TextPrimary
import com.alejandro.appgym.ui.theme.TextSecondary
import com.alejandro.appgym.ui.theme.TextTertiary
import com.alejandro.appgym.ui.theme.groupColor
import com.alejandro.appgym.util.epochDayToDate
import com.alejandro.appgym.util.shortText
import com.alejandro.appgym.util.volumeShort
import com.alejandro.appgym.util.weightText

@Composable
fun ExerciseDetailScreen(exerciseId: String, vm: GymViewModel, nav: NavHostController) {

    val exercise = ExerciseCatalog.require(exerciseId)
    val accent = groupColor(exercise.group)
    val allSets by vm.allSets.collectAsStateWithLifecycle()
    val sessions by vm.sessions.collectAsStateWithLifecycle()

    val mine = allSets.filter { it.exerciseId == exerciseId && it.done }
    val sessionDate = sessions.associate { it.id to it.date }

    // Mejor peso por día, ordenado cronológicamente
    val perDay = mine
        .groupBy { sessionDate[it.sessionId] ?: 0L }
        .toSortedMap()
        .map { (day, sets) -> day to sets.maxOf { it.weight } }

    val bestWeight = mine.maxOfOrNull { it.weight } ?: 0.0
    val bestReps = mine.filter { it.weight == bestWeight }.maxOfOrNull { it.reps } ?: 0
    val totalVolume = mine.sumOf { it.volume }

    LazyColumn(
        Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 40.dp)
    ) {

        item {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(280.dp)
            ) {
                AssetImage(
                    asset = exercise.imageAsset,
                    contentDescription = exercise.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                0f to Ink.copy(alpha = 0.55f),
                                0.45f to Color.Transparent,
                                1f to Ink
                            )
                        )
                )
                Row(
                    Modifier
                        .windowInsetsPadding(WindowInsets.statusBars)
                        .padding(12.dp)
                ) {
                    CircleIconButton(
                        icon = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = "Atrás",
                        onClick = { nav.popBackStack() },
                        size = 40,
                        background = Ink.copy(alpha = 0.55f),
                        tint = TextPrimary
                    )
                }
            }
        }

        item {
            Column(Modifier.padding(horizontal = Gutter)) {
                Text(
                    exercise.name,
                    style = MaterialTheme.typography.headlineLarge,
                    color = TextPrimary
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    exercise.nameEn,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextTertiary
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    "${exercise.group} · ${exercise.equipment} · ${exercise.level}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = accent
                )
            }
        }

        item { Spacer(Modifier.height(20.dp)) }

        item {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Gutter),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(Icons.Rounded.FitnessCenter, bestWeight.weightText(), "mejor peso (kg)", accent, Modifier.weight(1f))
                StatCard(Icons.Rounded.Repeat, bestReps.toString(), "repeticiones", TextSecondary, Modifier.weight(1f))
                StatCard(
                    Icons.Rounded.Timeline,
                    totalVolume.volumeShort(),
                    "kg acumulados",
                    TextSecondary,
                    Modifier.weight(1f)
                )
            }
        }

        if (perDay.size >= 2) {
            item { Spacer(Modifier.height(18.dp)) }
            item { SectionTitle("Evolución del peso") }
            item {
                AppCard(Modifier.padding(horizontal = Gutter)) {
                    Column {
                        ProgressLine(perDay.map { it.second.toFloat() }, accent)
                        Spacer(Modifier.height(6.dp))
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                epochDayToDate(perDay.first().first).shortText(),
                                style = MaterialTheme.typography.labelSmall,
                                color = TextTertiary
                            )
                            Text(
                                epochDayToDate(perDay.last().first).shortText(),
                                style = MaterialTheme.typography.labelSmall,
                                color = TextTertiary
                            )
                        }
                    }
                }
            }
        }

        item { Spacer(Modifier.height(18.dp)) }
        item { SectionTitle("Músculos implicados") }
        item {
            AppCard(Modifier.padding(horizontal = Gutter)) {
                Column {
                    MuscleLine("Principales", exercise.primaryMuscles, accent)
                    if (exercise.secondaryMuscles.isNotEmpty()) {
                        Spacer(Modifier.height(12.dp))
                        MuscleLine("Secundarios", exercise.secondaryMuscles, TextTertiary)
                    }
                }
            }
        }

        if (perDay.isNotEmpty()) {
            item { Spacer(Modifier.height(18.dp)) }
            item { SectionTitle("Últimas sesiones") }
            item {
                Column(Modifier.padding(horizontal = Gutter)) {
                    perDay.takeLast(6).reversed().forEach { (day, weight) ->
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(vertical = 9.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                epochDayToDate(day).shortText(),
                                style = MaterialTheme.typography.bodyLarge,
                                color = TextSecondary,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                "${weight.weightText()} kg",
                                style = MaterialTheme.typography.titleMedium,
                                color = TextPrimary
                            )
                        }
                    }
                }
            }
        }

        item { Spacer(Modifier.height(24.dp)) }
        item {
            BotonAcento(
                text = "Añadir a la sesión de hoy",
                icon = Icons.Rounded.Add,
                modifier = Modifier.padding(horizontal = Gutter),
                onClick = {
                    vm.addExercise(exercise.id)
                    nav.popBackStack()
                }
            )
        }
    }
}

@Composable
private fun MuscleLine(label: String, muscles: List<String>, color: Color) {
    Column {
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium,
            color = TextTertiary
        )
        Spacer(Modifier.height(8.dp))
        Text(
            muscles.take(4).joinToString(" · "),
            style = MaterialTheme.typography.bodyLarge,
            color = color
        )
    }
}
