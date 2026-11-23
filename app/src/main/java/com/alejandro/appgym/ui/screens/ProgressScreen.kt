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
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.EventAvailable
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.IosShare
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.Map
import androidx.compose.material.icons.rounded.MonitorWeight
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.alejandro.appgym.GymViewModel
import com.alejandro.appgym.data.ExerciseCatalog
import com.alejandro.appgym.data.SeedData
import com.alejandro.appgym.ui.Routes
import com.alejandro.appgym.ui.components.AccessCard
import com.alejandro.appgym.ui.components.AppCard
import com.alejandro.appgym.ui.components.Badge
import com.alejandro.appgym.ui.components.BarChart
import com.alejandro.appgym.ui.components.CircleIconButton
import com.alejandro.appgym.ui.components.EmptyState
import com.alejandro.appgym.ui.components.ExerciseThumb
import com.alejandro.appgym.ui.components.Gutter
import com.alejandro.appgym.ui.components.ProgressLine
import com.alejandro.appgym.ui.components.ScreenHeader
import com.alejandro.appgym.ui.components.SectionTitle
import com.alejandro.appgym.ui.components.StatCard
import com.alejandro.appgym.ui.theme.GroupBiceps
import com.alejandro.appgym.ui.theme.GroupLegs
import com.alejandro.appgym.ui.theme.Control
import com.alejandro.appgym.ui.theme.Pill
import com.alejandro.appgym.ui.theme.Surface1
import com.alejandro.appgym.ui.theme.Surface2
import com.alejandro.appgym.ui.theme.TextPrimary
import com.alejandro.appgym.ui.theme.TextSecondary
import com.alejandro.appgym.ui.theme.TextTertiary
import com.alejandro.appgym.ui.theme.groupColor
import com.alejandro.appgym.util.ReportBuilder
import com.alejandro.appgym.util.ReportSharing
import com.alejandro.appgym.util.distanceText
import com.alejandro.appgym.util.epochDayToDate
import com.alejandro.appgym.util.shortText
import com.alejandro.appgym.util.volumeShort
import com.alejandro.appgym.util.weightText
import java.time.LocalDate
import kotlin.math.abs

@Composable
fun ProgressScreen(vm: GymViewModel, nav: NavHostController) {

    val sessions by vm.sessions.collectAsStateWithLifecycle()
    val allSets by vm.allSets.collectAsStateWithLifecycle()
    val profile by vm.profile.collectAsStateWithLifecycle()
    val weights by vm.bodyWeights.collectAsStateWithLifecycle()
    val planActivo by vm.activePlan.collectAsStateWithLifecycle()
    val accent = MaterialTheme.colorScheme.primary
    val context = LocalContext.current

    var showWeightDialog by remember { mutableStateOf(false) }
    var showProfileDialog by remember { mutableStateOf(false) }

    val doneSets = allSets.filter { it.done }
    val sessionDate = sessions.associate { it.id to it.date }
    val today = LocalDate.now()

    val actual = weights.lastOrNull()?.weight
    val inicial = weights.firstOrNull()?.weight
    val delta = if (actual != null && inicial != null) actual - inicial else 0.0

    LazyColumn(
        Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars),
        contentPadding = PaddingValues(bottom = 36.dp)
    ) {

        /* ---------------- Cabecera con el perfil dentro ---------------- */
        item {
            ScreenHeader(
                title = "Progreso",
                trailing = {
                    CircleIconButton(
                        icon = Icons.Rounded.IosShare,
                        contentDescription = "Compartir informe",
                        onClick = {
                            ReportSharing.share(
                                context,
                                ReportBuilder.build(
                                    name = profile.name,
                                    heightCm = profile.heightCm,
                                    age = profile.age,
                                    planName = com.alejandro.appgym.data.SeedData
                                        .groupOf(planActivo)?.name.orEmpty(),
                                    sessions = sessions,
                                    sets = allSets,
                                    weights = weights
                                )
                            )
                        }
                    )
                }
            )
        }

        /* ---------------- Perfil ---------------- */
        item {
            TarjetaPerfil(
                nombre = profile.name,
                edad = profile.age,
                alturaCm = profile.heightCm,
                pesoKg = actual,
                onEditar = { showProfileDialog = true }
            )
        }

        /* ---------------- Tres cifras ---------------- */
        if (sessions.isNotEmpty()) {
            item {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Gutter, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(
                        icon = Icons.Rounded.FitnessCenter,
                        value = doneSets.sumOf { it.volume }.volumeShort(),
                        label = "kg movidos",
                        accent = accent,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        icon = Icons.Rounded.LocalFireDepartment,
                        value = doneSets.mapNotNull { sessionDate[it.sessionId] }.distinct().size.toString(),
                        label = "días entrenados",
                        accent = GroupBiceps,
                        modifier = Modifier.weight(1f)
                    )
                    // Solo días con alguna serie hecha: antes contaba también los de
                    // descanso y los que se abrieron sin llegar a entrenar.
                    StatCard(
                        icon = Icons.Rounded.EventAvailable,
                        value = doneSets.mapNotNull { sessionDate[it.sessionId] }.distinct().count {
                            val d = LocalDate.ofEpochDay(it)
                            d.year == today.year && d.month == today.month
                        }.toString(),
                        label = "este mes",
                        accent = GroupLegs,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        /* ---------------- Peso corporal ---------------- */
        item {
            SectionTitle("Peso corporal") {
                Row(
                    Modifier
                        .clip(Pill)
                        .background(accent)
                        .clickable { showWeightDialog = true }
                        .padding(horizontal = 15.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Rounded.Add, null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        "Registrar",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }

        item {
            AppCard(Modifier.padding(horizontal = Gutter)) {
                Column {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            actual?.weightText() ?: "—",
                            style = MaterialTheme.typography.displayMedium,
                            color = TextPrimary
                        )
                        Spacer(Modifier.width(5.dp))
                        Text(
                            "kg",
                            style = MaterialTheme.typography.bodyLarge,
                            color = TextSecondary,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                        Spacer(Modifier.weight(1f))
                        if (weights.size > 1 && abs(delta) > 0.05) {
                            Badge(
                                (if (delta > 0) "+" else "−") + abs(delta).weightText() + " kg",
                                accent,
                                Modifier.padding(bottom = 6.dp)
                            )
                        }
                    }

                    if (weights.size >= 2) {
                        Spacer(Modifier.height(10.dp))
                        ProgressLine(weights.map { it.weight.toFloat() }, accent)
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                epochDayToDate(weights.first().date).shortText(),
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextTertiary
                            )
                            Text(
                                epochDayToDate(weights.last().date).shortText(),
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextTertiary
                            )
                        }
                    } else {
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "Apunta tu peso una vez por semana, siempre en las mismas " +
                                "condiciones. Con dos registros ya verás la evolución.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = TextTertiary
                        )
                    }
                }
            }
        }

        if (weights.size >= 2) {
            item {
                Column(Modifier.padding(horizontal = Gutter, vertical = 6.dp)) {
                    weights.takeLast(5).reversed().forEach { entry ->
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(vertical = 9.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                epochDayToDate(entry.date).shortText(),
                                style = MaterialTheme.typography.bodyLarge,
                                color = TextSecondary,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                "${entry.weight.weightText()} kg",
                                style = MaterialTheme.typography.titleMedium,
                                color = TextPrimary
                            )
                            Spacer(Modifier.width(10.dp))
                            Icon(
                                Icons.Rounded.Close, "Borrar registro",
                                tint = TextTertiary,
                                modifier = Modifier
                                    .size(16.dp)
                                    .clickable { vm.deleteWeight(entry.id) }
                            )
                        }
                    }
                }
            }
        }

        /* ---------------- Entrenamiento ---------------- */
        if (sessions.isEmpty()) {
            item {
                EmptyState(
                    icon = Icons.Rounded.BarChart,
                    title = "Sin entrenamientos aún",
                    message = "En cuanto registres tu primera sesión verás aquí el peso movido, los récords y tu evolución."
                )
            }
        } else {

            val lunes = today.minusDays(((today.dayOfWeek.value + 6) % 7).toLong())
            val semanas = (7 downTo 0).map { lunes.minusWeeks(it.toLong()) }
            val volumenes = semanas.map { s ->
                val desde = s.toEpochDay()
                val hasta = s.plusDays(7).toEpochDay()
                doneSets.filter { (sessionDate[it.sessionId] ?: -1L) in desde until hasta }
                    .sumOf { it.volume }
                    .toFloat()
            }

            item { SectionTitle("Peso movido por semana") }
            item {
                AppCard(Modifier.padding(horizontal = Gutter)) {
                    Column {
                        Row {
                            Text(
                                "Esta semana llevas ",
                                style = MaterialTheme.typography.bodyLarge,
                                color = TextSecondary
                            )
                            Text(
                                "${volumenes.last().toDouble().volumeShort()} kg",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = TextPrimary
                            )
                        }
                        Spacer(Modifier.height(16.dp))
                        BarChart(
                            values = volumenes,
                            labels = semanas.map { it.dayOfMonth.toString() }
                        )
                    }
                }
            }

            val cardio = doneSets.filter { it.minutes > 0 }
            if (cardio.isNotEmpty()) {
                item { SectionTitle("Cardio") }
                item {
                    AppCard(Modifier.padding(horizontal = Gutter)) {
                        Column {
                            val minutos = cardio.sumOf { it.minutes }
                            val km = cardio.sumOf { it.distanceKm }
                            val kcal = cardio.sumOf { it.calories }
                            val conPulso = cardio.filter { it.avgHeartRate > 0 }
                            val pulsoMedio = if (conPulso.isEmpty()) 0
                            else conPulso.sumOf { it.avgHeartRate } / conPulso.size

                            Text(
                                buildString {
                                    append("$minutos min acumulados")
                                    if (km > 0) append(" · ${km.distanceText()} km")
                                    if (kcal > 0) append(" · $kcal kcal")
                                },
                                style = MaterialTheme.typography.bodyLarge,
                                color = TextSecondary
                            )

                            if (pulsoMedio > 0) {
                                Spacer(Modifier.height(14.dp))
                                Row(verticalAlignment = Alignment.Bottom) {
                                    Text(
                                        pulsoMedio.toString(),
                                        style = MaterialTheme.typography.displayMedium,
                                        color = TextPrimary
                                    )
                                    Spacer(Modifier.width(5.dp))
                                    Text(
                                        "ppm de media",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = TextSecondary,
                                        modifier = Modifier.padding(bottom = 6.dp)
                                    )
                                    Spacer(Modifier.weight(1f))
                                    Badge(
                                        profile.zoneOf(pulsoMedio),
                                        if (pulsoMedio in profile.zone2) accent else GroupLegs,
                                        Modifier.padding(bottom = 6.dp)
                                    )
                                }
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    "Tu zona 2 está entre ${profile.zone2.first} y " +
                                        "${profile.zone2.last} ppm: el ritmo en el que podrías " +
                                        "mantener una conversación.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextTertiary
                                )
                            }
                        }
                    }
                }
            }

            // Sin cardio ni ejercicios sin carga: su volumen es 0, y si solo había de
            // esos, la barra se dibujaba con 0/0 = NaN y la pantalla se cerraba.
            val masTrabajados = doneSets
                .filter { it.volume > 0 }
                .groupBy { it.exerciseId }
                .map { (id, list) -> Triple(id, list.sumOf { it.volume }, list.size) }
                .sortedByDescending { it.second }
                .take(5)

            if (masTrabajados.isNotEmpty()) {
                item { SectionTitle("Lo que más trabajas") }
                item {
                    Column(Modifier.padding(horizontal = Gutter)) {
                        val max = masTrabajados.first().second.coerceAtLeast(1.0)
                        masTrabajados.forEach { (id, volumen, series) ->
                            val exercise = ExerciseCatalog.require(id)
                            val color = groupColor(exercise.group)
                            Column(Modifier.padding(vertical = 9.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        exercise.name,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = TextPrimary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Spacer(Modifier.width(10.dp))
                                    Text(
                                        "${volumen.volumeShort()} kg",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = color
                                    )
                                }
                                Spacer(Modifier.height(8.dp))
                                Box(
                                    Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                        .clip(Pill)
                                        .background(Surface2)
                                ) {
                                    Box(
                                        Modifier
                                            .fillMaxWidth((volumen / max).toFloat().coerceIn(0.02f, 1f))
                                            .height(6.dp)
                                            .clip(Pill)
                                            .background(color)
                                    )
                                }
                                Spacer(Modifier.height(6.dp))
                                Text(
                                    "$series series registradas",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextTertiary
                                )
                            }
                        }
                    }
                }
            }

            val records = doneSets
                .filter { it.weight > 0 }
                .groupBy { it.exerciseId }
                .map { (id, l) -> Triple(id, l.maxOf { it.weight }, l.maxByOrNull { it.weight }!!.reps) }
                .sortedByDescending { it.second }
                .take(6)

            if (records.isNotEmpty()) {
                item { SectionTitle("Récords personales") }
                items(records.size) { i ->
                    val (id, peso, reps) = records[i]
                    val exercise = ExerciseCatalog.require(id)
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Surface1,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = Gutter, vertical = 4.dp)
                            .clickable { nav.navigate(Routes.exercise(id)) }
                    ) {
                        Row(
                            Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            ExerciseThumb(exercise, 46)
                            Spacer(Modifier.width(13.dp))
                            Column(Modifier.weight(1f)) {
                                Text(
                                    exercise.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = TextPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(Modifier.height(2.dp))
                                Text(
                                    "$reps repeticiones",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextSecondary
                                )
                            }
                            Spacer(Modifier.width(10.dp))
                            Text(
                                "${peso.weightText()} kg",
                                style = MaterialTheme.typography.titleLarge,
                                color = groupColor(exercise.group)
                            )
                        }
                    }
                }
            }

            /* ---------------- Accesos ---------------- */
            item { Spacer(Modifier.height(26.dp)) }
            item {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Gutter),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    AccessCard(
                        icon = Icons.Rounded.History,
                        title = "Historial",
                        subtitle = "${sessions.size} sesiones",
                        onClick = { nav.navigate(Routes.HISTORY) },
                        modifier = Modifier.weight(1f)
                    )
                    AccessCard(
                        icon = Icons.Rounded.Map,
                        title = "Tu plan",
                        subtitle = SeedData.groupOf(planActivo)?.name ?: "",
                        onClick = { nav.navigate(Routes.PLAN) },
                        accent = accent,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }

    if (showWeightDialog) {
        DialogoPeso(
            inicial = actual?.weightText() ?: "",
            onDismiss = { showWeightDialog = false },
            onSave = {
                showWeightDialog = false
                vm.logWeight(it)
            }
        )
    }

    if (showProfileDialog) {
        DialogoPerfil(
            name = profile.name,
            heightCm = profile.heightCm,
            age = profile.age,
            weightKg = actual,
            onDismiss = { showProfileDialog = false },
            onSave = { n, h, e, p ->
                showProfileDialog = false
                vm.saveProfile(n, h, e, p)
            }
        )
    }
}

/** El perfil a la vista, con un botón de editar que se ve. */
@Composable
private fun TarjetaPerfil(
    nombre: String,
    edad: Int,
    alturaCm: Int,
    pesoKg: Double?,
    onEditar: () -> Unit
) {
    val accent = MaterialTheme.colorScheme.primary
    val imc = pesoKg?.takeIf { alturaCm > 0 }?.let { it / ((alturaCm / 100.0) * (alturaCm / 100.0)) }
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = Gutter, vertical = 4.dp)
            .clip(MaterialTheme.shapes.large)
            .background(Surface1)
            .clickable(onClick = onEditar)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier
                .size(46.dp)
                .clip(Pill)
                .background(accent.copy(alpha = 0.16f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                nombre.trim().firstOrNull()?.uppercase() ?: "·",
                style = MaterialTheme.typography.titleLarge,
                color = accent
            )
        }
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            Text(
                nombre.ifBlank { "Tu perfil" },
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary
            )
            Spacer(Modifier.height(2.dp))
            Text(
                listOfNotNull(
                    "$edad años",
                    "$alturaCm cm",
                    pesoKg?.let { "${it.weightText()} kg" }
                ).joinToString(" · "),
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
            if (imc != null) {
                Text(
                    "IMC ${String.format(com.alejandro.appgym.util.ES, "%.1f", imc)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextTertiary
                )
            }
        }
        Row(
            Modifier
                .clip(Pill)
                .background(Control)
                .padding(horizontal = 13.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Rounded.Edit, null, tint = TextSecondary, modifier = Modifier.size(15.dp))
            Spacer(Modifier.width(6.dp))
            Text("Editar", style = MaterialTheme.typography.labelMedium, color = TextPrimary)
        }
    }
}

@Composable
private fun DialogoPeso(inicial: String, onDismiss: () -> Unit, onSave: (Double) -> Unit) {
    var value by remember { mutableStateOf(inicial) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Surface2,
        shape = MaterialTheme.shapes.large,
        icon = { Icon(Icons.Rounded.MonitorWeight, null, tint = MaterialTheme.colorScheme.primary) },
        title = { Text("Peso de hoy", style = MaterialTheme.typography.titleLarge, color = TextPrimary) },
        text = {
            Column {
                StepperField(
                    value = value.ifBlank { "75" },
                    suffix = "kg",
                    onDecrement = {
                        val v = (value.toWeightOrNull() ?: 75.0) - 0.1
                        value = v.coerceAtLeast(0.0).weightText()
                    },
                    onIncrement = {
                        val v = (value.toWeightOrNull() ?: 75.0) + 0.1
                        value = v.weightText()
                    },
                    onValueChange = { value = it },
                    decimals = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    "Si ya habías registrado hoy, se sobrescribe.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextTertiary
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { value.toWeightOrNull()?.let(onSave) }) {
                Text("Guardar", color = MaterialTheme.colorScheme.primary)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar", color = TextTertiary) }
        }
    )
}

@Composable
private fun DialogoPerfil(
    name: String,
    heightCm: Int,
    age: Int,
    weightKg: Double?,
    onDismiss: () -> Unit,
    onSave: (String, Int, Int, Double) -> Unit
) {
    var newName by remember { mutableStateOf(name) }
    var newHeight by remember { mutableStateOf(heightCm.toString()) }
    var newAge by remember { mutableStateOf(age.toString()) }
    var newWeight by remember { mutableStateOf(weightKg?.weightText().orEmpty()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Surface2,
        shape = MaterialTheme.shapes.large,
        title = { Text("Tu perfil", style = MaterialTheme.typography.titleLarge, color = TextPrimary) },
        text = {
            Column {
                Text("Nombre", style = MaterialTheme.typography.bodyMedium, color = TextTertiary)
                Spacer(Modifier.height(8.dp))
                DialogField(newName, { newName = it }, "Tu nombre")
                Spacer(Modifier.height(18.dp))
                Text("Altura, edad y peso", style = MaterialTheme.typography.bodyMedium, color = TextTertiary)
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    NumberField(
                        value = newHeight,
                        onValueChange = { newHeight = it },
                        suffix = "cm",
                        width = 0,
                        modifier = Modifier.weight(1f)
                    )
                    NumberField(
                        value = newAge,
                        onValueChange = { newAge = it },
                        suffix = "años",
                        width = 0,
                        modifier = Modifier.weight(1f)
                    )
                    NumberField(
                        value = newWeight,
                        onValueChange = { newWeight = it },
                        suffix = "kg",
                        width = 0,
                        decimals = true,
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(Modifier.height(10.dp))
                Text(
                    "Si cambias el peso, se apunta como el de hoy y sale en la gráfica.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextTertiary
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val peso = newWeight.toWeightOrNull() ?: 0.0
                onSave(
                    newName.trim(),
                    newHeight.toIntOrNull()?.coerceIn(120, 230) ?: heightCm,
                    newAge.toIntOrNull()?.coerceIn(12, 100) ?: age,
                    // Sin cambios no se registra nada: si no, guardar el nombre
                    // apuntaría un peso repetido con la fecha de hoy.
                    if (weightKg != null && kotlin.math.abs(peso - weightKg) < 0.05) 0.0 else peso
                )
            }) { Text("Guardar", color = MaterialTheme.colorScheme.primary) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar", color = TextTertiary) }
        }
    )
}
