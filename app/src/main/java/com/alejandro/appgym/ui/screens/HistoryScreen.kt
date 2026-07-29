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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.History
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.alejandro.appgym.GymViewModel
import com.alejandro.appgym.data.ExerciseCatalog
import com.alejandro.appgym.ui.Routes
import com.alejandro.appgym.ui.components.AppCard
import com.alejandro.appgym.ui.components.CircleIconButton
import com.alejandro.appgym.ui.components.EmptyState
import com.alejandro.appgym.ui.components.ExerciseThumb
import com.alejandro.appgym.ui.components.Gutter
import com.alejandro.appgym.ui.theme.Control
import com.alejandro.appgym.ui.theme.TextPrimary
import com.alejandro.appgym.ui.theme.TextSecondary
import com.alejandro.appgym.ui.theme.TextTertiary
import com.alejandro.appgym.util.durationText
import com.alejandro.appgym.util.epochDayToDate
import com.alejandro.appgym.util.longText
import com.alejandro.appgym.util.volumeShort

@Composable
fun HistoryScreen(vm: GymViewModel, nav: NavHostController) {

    val sessions by vm.sessions.collectAsStateWithLifecycle()
    val allSets by vm.allSets.collectAsStateWithLifecycle()

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
            Spacer(Modifier.width(12.dp))
            Text(
                "Historial",
                style = MaterialTheme.typography.headlineMedium,
                color = TextPrimary
            )
        }

        if (sessions.isEmpty()) {
            EmptyState(
                icon = Icons.Rounded.History,
                title = "Sin entrenamientos",
                message = "Aquí aparecerán todas tus sesiones ordenadas por fecha."
            )
        } else LazyColumn(contentPadding = PaddingValues(bottom = 32.dp)) {
            items(sessions, key = { it.id }) { session ->
                val sets = allSets.filter { it.sessionId == session.id }
                val volume = sets.filter { it.done }.sumOf { it.volume }
                val ids = sets.map { it.exerciseId }.distinct()

                AppCard(
                    modifier = Modifier.padding(horizontal = Gutter, vertical = 6.dp),
                    onClick = {
                        vm.selectDate(epochDayToDate(session.date))
                        nav.navigate(Routes.TODAY) { popUpTo(Routes.TODAY) { inclusive = true } }
                    }
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text(
                                    epochDayToDate(session.date).longText(),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextTertiary
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    session.title,
                                    style = MaterialTheme.typography.titleLarge,
                                    color = TextPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            BorrarIcono {
                                vm.deleteSession(session.id)
                            }
                        }

                        Spacer(Modifier.height(8.dp))
                        Text(
                            if (session.restDay) "Día de descanso" else buildString {
                                append("${ids.size} ejercicios · ${sets.size} series")
                                if (volume > 0) append(" · ${volume.volumeShort()} kg")
                                if (session.hasTimes) append(" · ${session.durationMinutes.durationText()}")
                            },
                            style = MaterialTheme.typography.bodyLarge,
                            color = TextSecondary
                        )

                        if (ids.isNotEmpty()) {
                            Spacer(Modifier.height(12.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                ids.take(6).forEach { id ->
                                    ExerciseThumb(ExerciseCatalog.require(id), 38)
                                }
                                if (ids.size > 6) {
                                    Box(
                                        Modifier
                                            .size(38.dp)
                                            .clip(RoundedCornerShape(9.dp))
                                            .background(Control),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            "+${ids.size - 6}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = TextSecondary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BorrarIcono(onClick: () -> Unit) {
    Icon(
        Icons.Rounded.DeleteOutline, "Borrar sesión",
        tint = TextTertiary,
        modifier = Modifier
            .size(18.dp)
            .clickable(onClick = onClick)
    )
}
