package com.alejandro.appgym.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.alejandro.appgym.ui.theme.Outline
import com.alejandro.appgym.ui.theme.TextPrimary
import com.alejandro.appgym.ui.theme.TextTertiary
import com.alejandro.appgym.ui.theme.Volt
import com.alejandro.appgym.ui.theme.WeekRamp
import kotlin.math.max

/**
 * Anillo de progreso de la sesión. Grosor 6, extremo redondeado y el porcentaje dentro.
 */
@Composable
fun ProgressRing(
    progress: Float,
    modifier: Modifier = Modifier,
    size: Int = 56,
    accent: Color = Volt
) {
    val animado by animateFloatAsState(progress.coerceIn(0f, 1f), tween(600), label = "ring")

    Box(modifier.size(size.dp), contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxWidth().height(size.dp)) {
            val grosor = 6.dp.toPx()
            val inset = grosor / 2f
            drawArc(
                color = Outline,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = Offset(inset, inset),
                size = Size(this.size.width - grosor, this.size.height - grosor),
                style = Stroke(width = grosor)
            )
            if (animado > 0f) {
                drawArc(
                    color = accent,
                    startAngle = -90f,
                    sweepAngle = 360f * animado,
                    useCenter = false,
                    topLeft = Offset(inset, inset),
                    size = Size(this.size.width - grosor, this.size.height - grosor),
                    style = Stroke(width = grosor, cap = StrokeCap.Round)
                )
            }
        }
        Text(
            "${(animado * 100).toInt()}%",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = TextPrimary
        )
    }
}

/**
 * Barras semanales. Sin pista de fondo: cada barra toma un color de la rampa según su
 * altura, y la última (semana en curso) va en lima.
 */
@Composable
fun BarChart(
    values: List<Float>,
    labels: List<String>,
    height: Int = 130,
    modifier: Modifier = Modifier
) {
    val maxValue = max(values.maxOrNull() ?: 0f, 1f)
    val progreso by animateFloatAsState(1f, tween(600), label = "bars")

    Column(modifier.fillMaxWidth()) {
        Canvas(
            Modifier
                .fillMaxWidth()
                .height(height.dp)
        ) {
            val n = values.size
            if (n == 0) return@Canvas
            val gap = size.width * 0.035f
            val ancho = (size.width - gap * (n - 1)) / n
            val radio = CornerRadius(10.dp.toPx(), 10.dp.toPx())

            values.forEachIndexed { i, v ->
                val ratio = (v / maxValue).coerceIn(0f, 1f)
                val alto = ratio * size.height * progreso
                val x = i * (ancho + gap)
                val esActual = i == n - 1
                val color = if (esActual) Volt else {
                    val paso = ((ratio * (WeekRamp.size - 2)).toInt()).coerceIn(0, WeekRamp.size - 2)
                    WeekRamp[paso]
                }
                if (alto > 1f) {
                    drawRoundRect(
                        color = color,
                        topLeft = Offset(x, size.height - alto),
                        size = Size(ancho, alto),
                        cornerRadius = radio
                    )
                }
            }
        }
        Spacer(Modifier.height(9.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            labels.forEachIndexed { i, etiqueta ->
                val esActual = i == labels.lastIndex
                Text(
                    etiqueta,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = if (esActual) FontWeight.SemiBold else FontWeight.Normal
                    ),
                    color = if (esActual) Volt else TextTertiary,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

/**
 * Línea de evolución del peso corporal: grosor 3, relleno degradado y punto final.
 */
@Composable
fun ProgressLine(
    values: List<Float>,
    accent: Color = Volt,
    height: Int = 120,
    modifier: Modifier = Modifier
) {
    if (values.size < 2) return
    val minV = values.min()
    val maxV = values.max()
    val rango = max(maxV - minV, 0.0001f)
    val progreso by animateFloatAsState(1f, tween(700), label = "line")

    Canvas(
        modifier
            .fillMaxWidth()
            .height(height.dp)
            .padding(vertical = 8.dp)
    ) {
        val pasoX = size.width / (values.size - 1)
        fun punto(i: Int) = Offset(
            x = i * pasoX,
            y = size.height - ((values[i] - minV) / rango) * size.height * 0.86f - size.height * 0.07f
        )

        val linea = Path()
        val area = Path()
        linea.moveTo(punto(0).x, punto(0).y)
        area.moveTo(punto(0).x, size.height)
        area.lineTo(punto(0).x, punto(0).y)

        for (i in 1 until values.size) {
            val p0 = punto(i - 1)
            val p1 = punto(i)
            val cx = (p0.x + p1.x) / 2f
            linea.cubicTo(cx, p0.y, cx, p1.y, p1.x, p1.y)
            area.cubicTo(cx, p0.y, cx, p1.y, p1.x, p1.y)
        }
        area.lineTo(punto(values.size - 1).x, size.height)
        area.close()

        drawPath(
            path = area,
            brush = Brush.verticalGradient(
                listOf(accent.copy(alpha = 0.22f * progreso), Color.Transparent)
            )
        )
        drawPath(
            path = linea,
            color = accent.copy(alpha = progreso),
            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
        )
        val ultimo = punto(values.size - 1)
        drawCircle(accent, radius = 5.dp.toPx() / 1.6f, center = ultimo)
    }
}
