package com.alejandro.appgym.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.icons.rounded.Lightbulb
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import com.alejandro.appgym.data.SeedData
import com.alejandro.appgym.ui.Routes
import com.alejandro.appgym.ui.components.Badge
import com.alejandro.appgym.ui.components.CircleIconButton
import com.alejandro.appgym.ui.components.Gutter
import com.alejandro.appgym.ui.components.SectionTitle
import com.alejandro.appgym.ui.theme.Control
import com.alejandro.appgym.ui.theme.OutlineSoft
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
fun PlanScreen(vm: GymViewModel, nav: NavHostController) {

    val planActivo by vm.activePlan.collectAsStateWithLifecycle()
    val diasEntrenados by vm.trainedDays.collectAsStateWithLifecycle()
    val rutinas by vm.routines.collectAsStateWithLifecycle()
    val accent = MaterialTheme.colorScheme.primary
    var abierta by remember { mutableStateOf<String?>(planActivo) }

    LazyColumn(
        Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars),
        contentPadding = PaddingValues(bottom = 40.dp)
    ) {

        item {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircleIconButton(
                    icon = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = "Atrás",
                    onClick = { nav.popBackStack() },
                    size = 40
                )
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text("Tu plan", style = MaterialTheme.typography.headlineMedium, color = TextPrimary)
                    Spacer(Modifier.height(2.dp))
                    Text(
                        "Perder grasa, ganar músculo",
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextSecondary
                    )
                }
            }
        }

        /* -------- Dónde estás ahora -------- */
        item { PlanActual(planActivo, diasEntrenados, accent) }

        /* -------- Rutinas de la fase -------- */
        item { Spacer(Modifier.height(18.dp)) }
        item {
            SectionTitle("Las que te tocan")
        }
        item {
            val grupo = SeedData.groupOf(planActivo)
            val nombres = grupo?.days?.map { it.name } ?: emptyList()

            Column(Modifier.padding(horizontal = Gutter)) {
                nombres.forEachIndexed { i, nombre ->
                    val rutina = rutinas.firstOrNull { it.name == nombre }
                    RutinaFila(
                        nombre = nombre,
                        meta = grupo?.days?.getOrNull(i)?.let {
                            "${it.items.size} ejercicios"
                        } ?: "",
                        primero = i == 0,
                        onClick = { rutina?.let { r -> nav.navigate(Routes.routine(r.id)) } }
                    )
                    if (i < nombres.lastIndex) Spacer(Modifier.height(8.dp))
                }
            }
        }

        /* -------- Secciones -------- */
        item { Spacer(Modifier.height(22.dp)) }
        item {
            SectionTitle("El plan al detalle")
        }

        items(PlanGuide.secciones) { seccion ->
            val activa = seccion.id == planActivo
            SeccionCard(
                seccion = seccion,
                expandida = abierta == seccion.id,
                destacadaAhora = activa,
                accent = accent,
                onToggle = { abierta = if (abierta == seccion.id) null else seccion.id }
            )
        }
    }
}

/* ------------------------------------------------------------------ */

@Composable
private fun PlanActual(planKey: String, diasEntrenados: Int, accent: Color) {
    val grupo = SeedData.groupOf(planKey) ?: return
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
            Badge("Plan activo", accent)
            Spacer(Modifier.height(12.dp))
            Text(grupo.name, style = MaterialTheme.typography.headlineSmall, color = TextPrimary)
            Spacer(Modifier.height(4.dp))
            Text(
                grupo.subtitle,
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary
            )
            Spacer(Modifier.height(14.dp))
            Text(
                when (diasEntrenados) {
                    0 -> "Aún no has registrado ningún día."
                    1 -> "Llevas 1 día entrenado."
                    else -> "Llevas $diasEntrenados días entrenados."
                },
                style = MaterialTheme.typography.bodyMedium,
                color = TextTertiary
            )
            Spacer(Modifier.height(4.dp))
            Text(
                "Para cambiar de plan, ve a Rutinas.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextTertiary
            )
        }
    }
}

@Composable
private fun RutinaFila(nombre: String, meta: String, primero: Boolean, onClick: () -> Unit) {
    val accent = MaterialTheme.colorScheme.primary
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Surface1,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            Modifier.padding(start = 16.dp, end = 10.dp, top = 10.dp, bottom = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                Modifier
                    .weight(1f)
                    .clickable(onClick = onClick)
            ) {
                Text(
                    nombre,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(2.dp))
                Text(meta, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
            }
            Spacer(Modifier.width(10.dp))
            Box(
                Modifier
                    .size(44.dp)
                    .clip(Pill)
                    .background(if (primero) accent else Control)
                    .clickable(onClick = onClick),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Rounded.PlayArrow, "Abrir",
                    tint = if (primero) MaterialTheme.colorScheme.onPrimary else TextSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun SeccionCard(
    seccion: PlanGuide.Seccion,
    expandida: Boolean,
    destacadaAhora: Boolean,
    accent: Color,
    onToggle: () -> Unit
) {
    val giro by animateFloatAsState(if (expandida) 180f else 0f, tween(200), label = "chevron")

    Surface(
        shape = MaterialTheme.shapes.large,
        color = Surface1,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Gutter, vertical = 5.dp)
            .then(
                if (destacadaAhora) Modifier.border(1.dp, accent.copy(alpha = 0.45f), MaterialTheme.shapes.large)
                else Modifier.border(1.dp, OutlineSoft, MaterialTheme.shapes.large)
            )
    ) {
        Column {
            Row(
                Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onToggle)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            seccion.titulo,
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary
                        )
                        if (destacadaAhora) {
                            Spacer(Modifier.width(8.dp))
                            Badge("Ahora", accent)
                        }
                    }
                    Spacer(Modifier.height(3.dp))
                    Text(
                        seccion.resumen,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextTertiary
                    )
                }
                Icon(
                    Icons.Rounded.ExpandMore, null,
                    tint = TextTertiary,
                    modifier = Modifier
                        .size(20.dp)
                        .rotate(giro)
                )
            }

            AnimatedVisibility(expandida) {
                Column(Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)) {

                    seccion.destacado?.let { texto ->
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(accent.copy(alpha = 0.08f))
                                .padding(14.dp)
                        ) {
                            Icon(
                                Icons.Rounded.Lightbulb, null,
                                tint = accent,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(11.dp))
                            Text(
                                texto,
                                style = MaterialTheme.typography.bodyLarge,
                                color = TextSecondary
                            )
                        }
                        Spacer(Modifier.height(14.dp))
                    }

                    seccion.bloques.forEachIndexed { i, bloque ->
                        Text(
                            bloque.titulo,
                            style = MaterialTheme.typography.labelLarge,
                            color = TextPrimary
                        )
                        Spacer(Modifier.height(5.dp))
                        Text(
                            bloque.cuerpo,
                            style = MaterialTheme.typography.bodyLarge,
                            color = TextSecondary
                        )
                        if (i < seccion.bloques.lastIndex) {
                            Spacer(Modifier.height(10.dp))
                            Box(
                                Modifier
                                    .fillMaxWidth()
                                    .height(1.dp)
                                    .background(Surface2)
                            )
                            Spacer(Modifier.height(12.dp))
                        }
                    }
                }
            }
        }
    }
}
