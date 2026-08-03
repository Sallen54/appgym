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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.alejandro.appgym.GymViewModel
import com.alejandro.appgym.data.SeedData
import com.alejandro.appgym.ui.components.Gutter
import com.alejandro.appgym.ui.theme.Outline
import com.alejandro.appgym.ui.theme.Pill
import com.alejandro.appgym.ui.theme.Surface1
import com.alejandro.appgym.ui.theme.TextPrimary
import com.alejandro.appgym.ui.theme.TextSecondary
import com.alejandro.appgym.ui.theme.TextTertiary
import com.alejandro.appgym.ui.theme.VoltBorderStrong

/**
 * Primera pantalla en una instalación nueva.
 *
 * Todo lo que se pide aquí es opcional: la altura solo sirve para el IMC y la edad
 * para las zonas de pulso, así que dejarlo en blanco no rompe nada. La decisión de
 * verdad es la de abajo, porque el plan sembrado está hecho para un gimnasio y unos
 * objetivos concretos y no tiene sentido imponérselo a quien solo quiere apuntar.
 */
@Composable
fun WelcomeScreen(vm: GymViewModel) {

    var nombre by remember { mutableStateOf("") }
    var altura by remember { mutableStateOf("") }
    var edad by remember { mutableStateOf("") }
    var peso by remember { mutableStateOf("") }
    var conPlan by remember { mutableStateOf(true) }

    val plan = SeedData.groupOf(SeedData.DEFAULT_KEY)

    LazyColumn(
        Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars),
        contentPadding = PaddingValues(bottom = 40.dp)
    ) {
        item {
            Column(Modifier.padding(start = Gutter, end = Gutter, top = 40.dp)) {
                Text(
                    "Bienvenido",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextSecondary
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "AppGym",
                    style = MaterialTheme.typography.headlineLarge,
                    color = TextPrimary
                )
                Spacer(Modifier.height(14.dp))
                Text(
                    "Apunta lo que haces en el gimnasio y mira cómo progresas. " +
                        "Todo se queda en tu móvil: no hay cuenta ni servidor.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextSecondary
                )
            }
        }

        item { Spacer(Modifier.height(30.dp)) }

        item {
            Column(Modifier.padding(horizontal = Gutter)) {
                Text(
                    "Tus datos",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "Puedes dejarlo en blanco. Solo se usan para el IMC y para " +
                        "estimar tus zonas de pulso, y se cambian luego en Progreso.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextTertiary
                )
                Spacer(Modifier.height(16.dp))

                Etiqueta("Nombre")
                DialogField(nombre, { nombre = it }, "Cómo te llamas")

                Spacer(Modifier.height(14.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Column(Modifier.weight(1f)) {
                        Etiqueta("Altura")
                        NumberField(
                            altura, { altura = it }, suffix = "cm",
                            width = 0, modifier = Modifier.fillMaxWidth()
                        )
                    }
                    Column(Modifier.weight(1f)) {
                        Etiqueta("Edad")
                        NumberField(
                            edad, { edad = it }, suffix = "años",
                            width = 0, modifier = Modifier.fillMaxWidth()
                        )
                    }
                    Column(Modifier.weight(1f)) {
                        Etiqueta("Peso")
                        NumberField(
                            peso, { peso = it }, suffix = "kg",
                            width = 0, decimals = true, modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }

        item { Spacer(Modifier.height(30.dp)) }

        item {
            Column(Modifier.padding(horizontal = Gutter)) {
                Text(
                    "¿Cómo quieres empezar?",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary
                )
                Spacer(Modifier.height(14.dp))

                OpcionInicio(
                    titulo = "Con un plan hecho",
                    detalle = plan?.let {
                        "${it.name} · ${it.days.size} días por semana. " +
                            "Puedes cambiarlo entero o borrarlo cuando quieras."
                    } ?: "Un plan de cuatro días listo para usar.",
                    seleccionada = conPlan,
                    onClick = { conPlan = true }
                )
                Spacer(Modifier.height(10.dp))
                OpcionInicio(
                    titulo = "En blanco",
                    detalle = "Sin rutinas. Vas montando las tuyas o apuntas " +
                        "ejercicios sueltos según entrenes.",
                    seleccionada = !conPlan,
                    onClick = { conPlan = false }
                )
            }
        }

        item { Spacer(Modifier.height(28.dp)) }

        item {
            BotonAcento(
                text = "Empezar",
                icon = Icons.Rounded.PlayArrow,
                modifier = Modifier.padding(horizontal = Gutter),
                onClick = {
                    vm.completeOnboarding(
                        name = nombre,
                        heightCm = altura.toIntOrNull()?.coerceIn(120, 230) ?: 0,
                        age = edad.toIntOrNull()?.coerceIn(10, 100) ?: 0,
                        weightKg = peso.toWeightOrNull()?.takeIf { it in 25.0..300.0 } ?: 0.0,
                        conPlan = conPlan
                    )
                }
            )
        }
    }
}

@Composable
private fun Etiqueta(texto: String) {
    Text(
        texto,
        style = MaterialTheme.typography.bodyMedium,
        color = TextTertiary,
        modifier = Modifier.padding(bottom = 7.dp)
    )
}

@Composable
private fun OpcionInicio(
    titulo: String,
    detalle: String,
    seleccionada: Boolean,
    onClick: () -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .background(Surface1)
            .border(
                1.dp,
                if (seleccionada) VoltBorderStrong else Outline,
                MaterialTheme.shapes.large
            )
            .clickable(onClick = onClick)
            .padding(17.dp)
    ) {
        Column(Modifier.weight(1f)) {
            Text(titulo, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
            Spacer(Modifier.height(5.dp))
            Text(detalle, style = MaterialTheme.typography.bodyMedium, color = TextTertiary)
        }
        Spacer(Modifier.width(12.dp))
        Box(
            Modifier
                .size(23.dp)
                .clip(Pill)
                .background(
                    if (seleccionada) MaterialTheme.colorScheme.primary else Surface1
                )
                .border(
                    1.dp,
                    if (seleccionada) MaterialTheme.colorScheme.primary else Outline,
                    Pill
                ),
            contentAlignment = Alignment.Center
        ) {
            if (seleccionada) {
                Icon(
                    Icons.Rounded.Check, null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}
