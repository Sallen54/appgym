package com.alejandro.appgym.ui.screens

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.SearchOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.alejandro.appgym.GymViewModel
import com.alejandro.appgym.data.Exercise
import com.alejandro.appgym.data.ExerciseCatalog
import com.alejandro.appgym.ui.Routes
import com.alejandro.appgym.ui.components.CircleIconButton
import com.alejandro.appgym.ui.components.EmptyState
import com.alejandro.appgym.ui.components.ExerciseGridCard
import com.alejandro.appgym.ui.components.ExerciseRow
import com.alejandro.appgym.ui.components.ExerciseThumb
import com.alejandro.appgym.ui.components.FilterPill
import com.alejandro.appgym.ui.components.Gutter
import com.alejandro.appgym.ui.components.ScreenHeader
import com.alejandro.appgym.ui.theme.Control
import com.alejandro.appgym.ui.theme.Ink
import com.alejandro.appgym.ui.theme.OutlineSoft
import com.alejandro.appgym.ui.theme.Pill
import com.alejandro.appgym.ui.theme.TextPrimary
import com.alejandro.appgym.ui.theme.TextSecondary
import com.alejandro.appgym.ui.theme.TextTertiary
import com.alejandro.appgym.ui.theme.groupColor
import java.text.Normalizer

/* ------------------------------------------------------------------ */
/*  Utilidades de búsqueda                                             */
/* ------------------------------------------------------------------ */

private fun norm(s: String): String =
    Normalizer.normalize(s, Normalizer.Form.NFD)
        .replace(Regex("\\p{InCombiningDiacriticalMarks}+"), "")
        .lowercase()

fun filterExercises(query: String, group: String?): List<Exercise> {
    val q = norm(query.trim())
    return ExerciseCatalog.all.filter { ex ->
        (group == null || ex.group == group) &&
            (q.isEmpty() ||
                norm(ex.name).contains(q) ||
                norm(ex.nameEn).contains(q) ||
                norm(ex.equipment).contains(q) ||
                ex.primaryMuscles.any { norm(it).contains(q) })
    }
}

/* ------------------------------------------------------------------ */
/*  Barra de búsqueda                                                  */
/* ------------------------------------------------------------------ */

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier
            .fillMaxWidth()
            .height(50.dp)
            .clip(Pill)
            .background(Control)
            .padding(horizontal = 18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Rounded.Search, null, tint = TextTertiary, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(10.dp))
        Box(Modifier.weight(1f)) {
            if (query.isEmpty()) {
                Text(
                    "Buscar máquina o músculo",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextTertiary
                )
            }
            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyLarge.copy(color = TextPrimary),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                modifier = Modifier.fillMaxWidth()
            )
        }
        if (query.isNotEmpty()) {
            Icon(
                Icons.Rounded.Close, "Limpiar búsqueda",
                tint = TextTertiary,
                modifier = Modifier
                    .size(18.dp)
                    .clickable { onQueryChange("") }
            )
        }
    }
}

@Composable
fun GroupFilters(
    selected: String?,
    onSelect: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = Gutter),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            FilterPill("Todo", selected == null, { onSelect(null) })
        }
        items(ExerciseCatalog.groups) { group ->
            FilterPill(
                text = group,
                selected = selected == group,
                onClick = { onSelect(if (selected == group) null else group) }
            )
        }
    }
}

/* ------------------------------------------------------------------ */
/*  Catálogo                                                           */
/* ------------------------------------------------------------------ */

@Composable
fun CatalogScreen(vm: GymViewModel, nav: NavHostController) {
    var query by remember { mutableStateOf("") }
    var group by remember { mutableStateOf<String?>(null) }
    val results = remember(query, group) { filterExercises(query, group) }

    Column(
        Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        ScreenHeader(
            title = "Máquinas",
            subtitle = "${ExerciseCatalog.all.size} ejercicios en tu gimnasio"
        )

        SearchBar(query, { query = it }, Modifier.padding(horizontal = Gutter, vertical = 6.dp))

        Spacer(Modifier.height(6.dp))
        GroupFilters(group, { group = it })
        Spacer(Modifier.height(12.dp))

        if (results.isEmpty()) {
            EmptyState(
                icon = Icons.Rounded.SearchOff,
                title = "Sin resultados",
                message = "Prueba con otro nombre, otro músculo o quita el filtro."
            )
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(start = Gutter, end = Gutter, bottom = 30.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(results, key = { it.id }) { exercise ->
                    ExerciseGridCard(
                        exercise = exercise,
                        onClick = { nav.navigate(Routes.exercise(exercise.id)) }
                    )
                }
            }
        }
    }
}

/* ------------------------------------------------------------------ */
/*  Selector multiple (sesión de hoy o rutina)                         */
/* ------------------------------------------------------------------ */

@Composable
fun PickerScreen(routineId: Long, vm: GymViewModel, nav: NavHostController) {
    var query by remember { mutableStateOf("") }
    var group by remember { mutableStateOf<String?>(null) }
    val selection = remember { mutableStateListOf<String>() }
    val results = remember(query, group) { filterExercises(query, group) }

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
            CircleIconButton(
                icon = Icons.AutoMirrored.Rounded.ArrowBack,
                contentDescription = "Atrás",
                onClick = { nav.popBackStack() },
                size = 40
            )
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    if (selection.isEmpty()) "Elige ejercicios" else "${selection.size} seleccionados",
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextPrimary
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    if (routineId > 0) "Se añaden a la rutina" else "Se añaden a la sesión de hoy",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextTertiary
                )
            }
        }

        SearchBar(query, { query = it }, Modifier.padding(horizontal = Gutter, vertical = 6.dp))
        Spacer(Modifier.height(6.dp))
        GroupFilters(group, { group = it })
        Spacer(Modifier.height(10.dp))

        androidx.compose.foundation.lazy.LazyColumn(
            Modifier.weight(1f),
            contentPadding = PaddingValues(bottom = 12.dp)
        ) {
            items(results, key = { it.id }) { exercise ->
                val checked = selection.contains(exercise.id)
                SelectableRow(
                    exercise = exercise,
                    checked = checked,
                    onToggle = {
                        if (checked) selection.remove(exercise.id) else selection.add(exercise.id)
                    }
                )
            }
        }

        Box(
            Modifier
                .fillMaxWidth()
                .background(Ink)
                .padding(Gutter)
        ) {
            BotonAcento(
                text = if (selection.isEmpty()) "Selecciona al menos uno"
                else "Añadir ${selection.size} ejercicio${if (selection.size == 1) "" else "s"}",
                icon = Icons.Rounded.Check,
                enabled = selection.isNotEmpty(),
                onClick = {
                    if (routineId > 0) vm.addToRoutine(routineId, selection.toList())
                    else vm.addExercises(selection.toList())
                    nav.popBackStack()
                }
            )
        }
    }
}

@Composable
private fun SelectableRow(
    exercise: Exercise,
    checked: Boolean,
    onToggle: () -> Unit
) {
    val accent = groupColor(exercise.group)
    Row(
        Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle)
            .padding(horizontal = Gutter, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ExerciseThumb(exercise, 50)
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            Text(
                exercise.name,
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(3.dp))
            Text(
                "${exercise.group} · ${exercise.equipment}",
                style = MaterialTheme.typography.bodyMedium,
                color = TextTertiary
            )
        }
        Spacer(Modifier.width(10.dp))
        Box(
            Modifier
                .size(26.dp)
                .clip(CircleShape)
                .background(if (checked) accent else Color.Transparent)
                .border(1.5.dp, if (checked) accent else OutlineSoft, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (checked) {
                Icon(
                    Icons.Rounded.Check, null,
                    tint = Color.Black.copy(alpha = 0.8f),
                    modifier = Modifier.size(15.dp)
                )
            }
        }
    }
}

/**
 * Elegir sustituto entre todo el catálogo, cuando ninguna de las sugerencias vale.
 * Arriba salen las alternativas recomendadas; debajo, el catálogo entero.
 */
@Composable
fun SwapScreen(exerciseId: String, vm: GymViewModel, nav: NavHostController) {
    val original = ExerciseCatalog.require(exerciseId)
    var query by remember { mutableStateOf("") }
    var group by remember { mutableStateOf<String?>(original.group) }
    val sugeridas = remember(exerciseId) { ExerciseCatalog.alternativesFor(exerciseId, 4) }
    val results = remember(query, group) {
        filterExercises(query, group).filter { it.id != exerciseId }
    }

    fun elegir(id: String) {
        vm.swapExercise(exerciseId, id)
        nav.popBackStack()
    }

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
            CircleIconButton(
                icon = Icons.AutoMirrored.Rounded.ArrowBack,
                contentDescription = "Atrás",
                onClick = { nav.popBackStack() },
                size = 40
            )
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    "Cambiar ejercicio",
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextPrimary
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    "En lugar de ${original.name}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextTertiary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        SearchBar(query, { query = it }, Modifier.padding(horizontal = Gutter, vertical = 6.dp))
        Spacer(Modifier.height(6.dp))
        GroupFilters(group, { group = it })
        Spacer(Modifier.height(10.dp))

        androidx.compose.foundation.lazy.LazyColumn(
            contentPadding = PaddingValues(bottom = 30.dp)
        ) {
            if (sugeridas.isNotEmpty() && query.isBlank()) {
                item {
                    Text(
                        "Recomendados",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimary,
                        modifier = Modifier.padding(horizontal = Gutter, vertical = 8.dp)
                    )
                }
                items(sugeridas, key = { "sug-" + it.id }) { ex ->
                    ExerciseRow(exercise = ex, onClick = { elegir(ex.id) })
                }
                item {
                    Text(
                        "Todo el catálogo",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimary,
                        modifier = Modifier.padding(start = Gutter, end = Gutter, top = 20.dp, bottom = 8.dp)
                    )
                }
            }
            items(results, key = { it.id }) { ex ->
                ExerciseRow(exercise = ex, onClick = { elegir(ex.id) })
            }
        }
    }
}
