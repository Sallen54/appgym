package com.alejandro.appgym.ui.components

import android.content.Context
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.alejandro.appgym.GymViewModel
import com.alejandro.appgym.ui.theme.Control
import com.alejandro.appgym.ui.theme.Outline
import com.alejandro.appgym.ui.theme.Pill
import com.alejandro.appgym.ui.theme.Surface2
import com.alejandro.appgym.ui.theme.TextPrimary
import com.alejandro.appgym.ui.theme.TextSecondary
import com.alejandro.appgym.util.ES
import kotlinx.coroutines.delay

/**
 * Cuenta atrás del descanso entre series, encima de la barra inferior. Arranca sola
 * al marcar una serie con el descanso que pide la rutina, y vibra al terminar: con
 * el móvil en el bolsillo es lo único que avisa.
 */
@Composable
fun BarraDescanso(
    descanso: GymViewModel.Descanso,
    onAjustar: (Int) -> Unit,
    onSaltar: () -> Unit
) {
    val context = LocalContext.current
    val accent = MaterialTheme.colorScheme.primary
    var ahora by remember { mutableLongStateOf(System.currentTimeMillis()) }

    // Se recalcula con la hora real en cada vuelta, así no se desajusta.
    LaunchedEffect(descanso) {
        while (true) {
            ahora = System.currentTimeMillis()
            if (ahora >= descanso.finMs) {
                vibrar(context)
                delay(2_500)       // deja ver "a por la siguiente" un momento
                onSaltar()
                break
            }
            delay(250)
        }
    }

    val restanteMs = (descanso.finMs - ahora).coerceAtLeast(0)
    val restante = ((restanteMs + 999) / 1000).toInt()
    val progreso = if (descanso.totalSeg > 0)
        (restanteMs / (descanso.totalSeg * 1000f)).coerceIn(0f, 1f) else 0f
    val terminado = restanteMs == 0L

    Column(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = Gutter, vertical = 8.dp)
            .clip(MaterialTheme.shapes.large)
            .background(Surface2)
    ) {
        // Lo que queda, como una línea que se vacía.
        Box(
            Modifier
                .fillMaxWidth()
                .height(3.dp)
                .background(Outline)
        ) {
            Box(
                Modifier
                    .fillMaxWidth(progreso)
                    .height(3.dp)
                    .background(accent)
            )
        }
        Row(
            Modifier.padding(start = 16.dp, end = 10.dp, top = 10.dp, bottom = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Rounded.Timer, null,
                tint = if (terminado) accent else TextSecondary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    if (terminado) "A por la siguiente" else "Descanso",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
                Text(
                    if (terminado) "0:00" else reloj(restante),
                    style = MaterialTheme.typography.headlineSmall,
                    color = if (terminado) accent else TextPrimary
                )
            }
            if (!terminado) {
                BotonDescanso("−15") { onAjustar(-15) }
                Spacer(Modifier.width(6.dp))
                BotonDescanso("+15") { onAjustar(15) }
                Spacer(Modifier.width(6.dp))
            }
            BotonDescanso(if (terminado) "Cerrar" else "Saltar", onClick = onSaltar)
        }
    }
}

@Composable
private fun BotonDescanso(texto: String, onClick: () -> Unit) {
    Box(
        Modifier
            .height(40.dp)
            .clip(Pill)
            .background(Control)
            .clickable(onClick = onClick)
            .padding(horizontal = 13.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(texto, style = MaterialTheme.typography.labelMedium, color = TextPrimary)
    }
}

/** 95 -> "1:35" */
private fun reloj(segundos: Int): String =
    String.format(ES, "%d:%02d", segundos / 60, segundos % 60)

private fun vibrar(context: Context) {
    // Si el móvil no tiene vibración, o no se puede, no pasa nada: el aviso visual sigue ahí.
    runCatching {
        @Suppress("DEPRECATION")
        val v = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator ?: return
        v.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 180, 120, 180), -1))
    }
}
