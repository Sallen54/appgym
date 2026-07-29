package com.alejandro.appgym.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.alejandro.appgym.ui.theme.Pill
import com.alejandro.appgym.ui.theme.Surface1
import com.alejandro.appgym.ui.theme.Surface2
import com.alejandro.appgym.ui.theme.TextPrimary
import com.alejandro.appgym.ui.theme.TextSecondary
import com.alejandro.appgym.ui.theme.TextTertiary

/**
 * Stepper de la serie activa: − valor + . Tocar el valor abre el teclado numérico,
 * que es lo que evita las peleas con los decimales a mitad de serie.
 */
@Composable
fun StepperField(
    value: String,
    suffix: String,
    onDecrement: () -> Unit,
    onIncrement: () -> Unit,
    onValueChange: (String) -> Unit,
    decimals: Boolean = false,
    /** Si es false, el valor solo se cambia con los botones (p. ej. la hora). */
    editable: Boolean = true,
    modifier: Modifier = Modifier
) {
    var editando by remember { mutableStateOf(false) }
    // Mientras escribes, el texto es tuyo: no se sobrescribe con el valor de fuera.
    var texto by remember { mutableStateOf(TextFieldValue(value)) }

    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    fun empezarAEditar() {
        // Entra con todo seleccionado: escribes encima sin borrar antes.
        texto = TextFieldValue(value, selection = TextRange(0, value.length))
        editando = true
    }

    fun confirmar() {
        if (!editando) return          // evita reentrar desde onFocusChanged
        editando = false
        onValueChange(texto.text.ifBlank { "0" })
        focusManager.clearFocus()      // cierra el teclado
    }

    LaunchedEffect(editando) {
        if (editando) focusRequester.requestFocus()
    }

    Row(modifier, verticalAlignment = Alignment.CenterVertically) {
        StepperButton(Icons.Rounded.Remove, "Bajar", onDecrement)

        Box(
            Modifier
                .weight(1f)
                .clickable(enabled = editable && !editando) { empezarAEditar() },
            contentAlignment = Alignment.Center
        ) {
            if (editando) {
                BasicTextField(
                    value = texto,
                    onValueChange = { nuevo ->
                        val limpio = nuevo.text
                            .filter { it.isDigit() || (decimals && (it == ',' || it == '.')) }
                            .replace('.', ',')
                            .take(6)
                        texto = nuevo.copy(text = limpio)
                    },
                    singleLine = true,
                    textStyle = MaterialTheme.typography.headlineSmall.copy(
                        color = TextPrimary,
                        textAlign = TextAlign.Center
                    ),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = if (decimals) KeyboardType.Decimal else KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onDone = { confirmar() }),
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester)
                        // Tocar fuera también guarda, no se pierde lo escrito.
                        .onFocusChanged { if (!it.isFocused && editando) confirmar() }
                )
            } else {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(value, style = MaterialTheme.typography.headlineSmall, color = TextPrimary)
                    if (suffix.isNotEmpty()) {
                        Spacer(Modifier.width(3.dp))
                        Text(
                            suffix,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary,
                            modifier = Modifier.padding(bottom = 2.dp)
                        )
                    }
                }
            }
        }

        StepperButton(Icons.Rounded.Add, "Subir", onIncrement)
    }
}

@Composable
private fun StepperButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    description: String,
    onClick: () -> Unit
) {
    Box(
        Modifier
            .size(34.dp)
            .clip(RoundedCornerShape(11.dp))
            .background(Surface1)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, description, tint = TextSecondary, modifier = Modifier.size(17.dp))
    }
}

/** Campo numérico compacto para rutinas y diálogos. */
@Composable
fun NumberField(
    value: String,
    onValueChange: (String) -> Unit,
    suffix: String = "",
    width: Int = 76,
    decimals: Boolean = false,
    modifier: Modifier = Modifier
) {
    var texto by remember(value) { mutableStateOf(value) }

    Row(
        modifier
            .then(if (width > 0) Modifier.width(width.dp) else Modifier)
            .height(44.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Surface2)
            .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BasicTextField(
            value = texto,
            onValueChange = { raw ->
                texto = raw.filter { it.isDigit() || (decimals && (it == ',' || it == '.')) }
                    .replace('.', ',')
                    .take(6)
                onValueChange(texto)
            },
            singleLine = true,
            textStyle = MaterialTheme.typography.titleMedium.copy(
                color = TextPrimary,
                textAlign = TextAlign.End
            ),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            keyboardOptions = KeyboardOptions(
                keyboardType = if (decimals) KeyboardType.Decimal else KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            modifier = Modifier.weight(1f)
        )
        if (suffix.isNotEmpty()) {
            Spacer(Modifier.width(4.dp))
            Text(suffix, style = MaterialTheme.typography.bodyMedium, color = TextTertiary)
        }
    }
}

/** Campo de texto de los diálogos. */
@Composable
fun DialogField(value: String, onValueChange: (String) -> Unit, placeholder: String) {
    Box(
        Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Surface2)
            .padding(horizontal = 14.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        if (value.isEmpty()) {
            Text(placeholder, style = MaterialTheme.typography.bodyLarge, color = TextTertiary)
        }
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyLarge.copy(color = TextPrimary),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

/** Convierte "72,5" -> 72.5 */
fun String.toWeightOrNull(): Double? = replace(',', '.').toDoubleOrNull()

/** Botón de acento. Se reserva para la acción principal de cada pantalla. */
@Composable
fun BotonAcento(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val fondo = if (enabled) MaterialTheme.colorScheme.primary else Surface2
    val color = if (enabled) MaterialTheme.colorScheme.onPrimary else TextTertiary
    Row(
        modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(com.alejandro.appgym.ui.theme.Pill)
            .background(fondo)
            .clickable(enabled = enabled, onClick = onClick),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = color, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(9.dp))
        Text(text, style = MaterialTheme.typography.labelLarge, color = color)
    }
}
