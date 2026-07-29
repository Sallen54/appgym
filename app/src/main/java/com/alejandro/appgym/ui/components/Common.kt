package com.alejandro.appgym.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.alejandro.appgym.data.Exercise
import com.alejandro.appgym.ui.theme.Control
import com.alejandro.appgym.ui.theme.Pill
import com.alejandro.appgym.ui.theme.Surface1
import com.alejandro.appgym.ui.theme.Surface2
import com.alejandro.appgym.ui.theme.TextPrimary
import com.alejandro.appgym.ui.theme.TextSecondary
import com.alejandro.appgym.ui.theme.TextTertiary
import com.alejandro.appgym.ui.theme.groupColor

/** Margen horizontal de todas las pantallas. */
val Gutter = 22.dp

/* ------------------------------------------------------------------ */
/*  Cabeceras                                                          */
/* ------------------------------------------------------------------ */

/**
 * Cabecera de pantalla: título grande y subtítulo en lenguaje normal.
 * Ya no hay overline en mayúsculas.
 */
@Composable
fun ScreenHeader(
    title: String,
    subtitle: String? = null,
    modifier: Modifier = Modifier,
    onSubtitleClick: (() -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null
) {
    Row(
        modifier
            .fillMaxWidth()
            .padding(horizontal = Gutter, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.headlineLarge, color = TextPrimary)
            if (subtitle != null) {
                Spacer(Modifier.height(3.dp))
                Text(
                    subtitle,
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextSecondary,
                    modifier = if (onSubtitleClick != null)
                        Modifier.clickable(onClick = onSubtitleClick) else Modifier
                )
            }
        }
        if (trailing != null) {
            Spacer(Modifier.width(12.dp))
            trailing()
        }
    }
}

/** Título de sección, con acción opcional a la derecha. */
@Composable
fun SectionTitle(
    text: String,
    modifier: Modifier = Modifier,
    action: (@Composable () -> Unit)? = null
) {
    Row(
        modifier
            .fillMaxWidth()
            .padding(start = Gutter, end = Gutter, top = 26.dp, bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text,
            style = MaterialTheme.typography.titleLarge,
            color = TextPrimary,
            modifier = Modifier.weight(1f)
        )
        action?.invoke()
    }
}

/* ------------------------------------------------------------------ */
/*  Contenedores                                                       */
/* ------------------------------------------------------------------ */

@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    color: Color = Surface1,
    border: BorderStroke? = null,
    onClick: (() -> Unit)? = null,
    contentPadding: PaddingValues = PaddingValues(18.dp),
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = color,
        border = border
    ) {
        Box(
            Modifier
                .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
                .padding(contentPadding)
        ) { content() }
    }
}

/** Tarjeta de estadística: icono de color, cifra y etiqueta. Sin barritas. */
@Composable
fun StatCard(
    icon: ImageVector,
    value: String,
    label: String,
    accent: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = Surface1
    ) {
        Column(Modifier.padding(16.dp)) {
            Icon(icon, null, tint = accent, modifier = Modifier.size(20.dp))
            Spacer(Modifier.height(12.dp))
            Text(value, style = MaterialTheme.typography.titleSmall, color = TextPrimary)
            Spacer(Modifier.height(2.dp))
            Text(
                label,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/** Fila de acceso a otra pantalla. Sustituye a los enlaces sueltos. */
@Composable
fun AccessCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    accent: Color = TextSecondary
) {
    Surface(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        color = Control
    ) {
        Column(Modifier.padding(16.dp)) {
            Icon(icon, null, tint = accent, modifier = Modifier.size(20.dp))
            Spacer(Modifier.height(12.dp))
            Text(
                title,
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(2.dp))
            Text(
                subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = TextTertiary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/* ------------------------------------------------------------------ */
/*  Controles                                                          */
/* ------------------------------------------------------------------ */

/** Chip de filtro. Píldora sin borde. */
@Composable
fun FilterPill(text: String, selected: Boolean, onClick: () -> Unit) {
    Surface(
        shape = Pill,
        color = if (selected) MaterialTheme.colorScheme.primary else Control,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Text(
            text,
            style = if (selected) MaterialTheme.typography.labelMedium.copy(
                fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
            ) else MaterialTheme.typography.labelMedium,
            color = if (selected) MaterialTheme.colorScheme.onPrimary else TextSecondary,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 9.dp)
        )
    }
}

/** Badge en mayúsculas. PLAN ACTIVO, FASE ACTUAL. */
@Composable
fun Badge(text: String, color: Color, modifier: Modifier = Modifier) {
    Surface(shape = Pill, color = color.copy(alpha = 0.14f), modifier = modifier) {
        Text(
            text.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = color,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

/** Botón circular de icono. Para atrás, compartir, calendario. */
@Composable
fun CircleIconButton(
    icon: ImageVector,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Int = 46,
    background: Color = Control,
    tint: Color = TextSecondary
) {
    Box(
        modifier
            .size(size.dp)
            .clip(Pill)
            .background(background)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription, tint = tint, modifier = Modifier.size((size * 0.44f).dp))
    }
}

/* ------------------------------------------------------------------ */
/*  Ejercicios                                                         */
/* ------------------------------------------------------------------ */

/** Miniatura limpia: sin gradiente ni barra de color encima. */
@Composable
fun ExerciseThumb(exercise: Exercise, size: Int = 54, modifier: Modifier = Modifier) {
    Box(
        modifier
            .size(size.dp)
            .clip(RoundedCornerShape((size * 0.28f).coerceIn(12f, 16f).dp))
            .background(Surface2)
    ) {
        AssetImage(
            asset = exercise.imageAsset,
            contentDescription = exercise.name,
            modifier = Modifier.fillMaxSize()
        )
    }
}

/**
 * Tarjeta del catálogo. La imagen va limpia y el grupo muscular se indica con
 * una sola línea de texto en su color, en vez de dos chips.
 */
@Composable
fun ExerciseGridCard(exercise: Exercise, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.clickable(onClick = onClick),
        shape = MaterialTheme.shapes.large,
        color = Surface1
    ) {
        Column {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(118.dp)
                    .background(Surface2)
            ) {
                AssetImage(
                    asset = exercise.imageAsset,
                    contentDescription = exercise.name,
                    modifier = Modifier.fillMaxSize()
                )
            }
            Column(Modifier.padding(start = 14.dp, end = 14.dp, top = 12.dp, bottom = 14.dp)) {
                Text(
                    exercise.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.heightIn(min = 40.dp)
                )
                Spacer(Modifier.height(5.dp))
                Text(
                    "${exercise.group} · ${exercise.equipment}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = groupColor(exercise.group),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

/** Fila compacta con miniatura. */
@Composable
fun ExerciseRow(
    exercise: Exercise,
    subtitle: String? = null,
    trailingIcon: ImageVector? = Icons.Rounded.ChevronRight,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = Gutter, vertical = 10.dp),
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
                subtitle ?: "${exercise.group} · ${exercise.equipment}",
                style = MaterialTheme.typography.bodyMedium,
                color = if (subtitle == null) groupColor(exercise.group) else TextTertiary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        if (trailingIcon != null) {
            Icon(trailingIcon, null, tint = TextTertiary, modifier = Modifier.size(20.dp))
        }
    }
}

/* ------------------------------------------------------------------ */

@Composable
fun EmptyState(
    icon: ImageVector,
    title: String,
    message: String,
    modifier: Modifier = Modifier,
    action: (@Composable () -> Unit)? = null
) {
    Column(
        modifier
            .fillMaxWidth()
            .padding(horizontal = 34.dp, vertical = 44.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            Modifier
                .size(58.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Control),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = TextTertiary, modifier = Modifier.size(24.dp))
        }
        Spacer(Modifier.height(18.dp))
        Text(title, style = MaterialTheme.typography.titleLarge, color = TextPrimary)
        Spacer(Modifier.height(6.dp))
        Text(
            message,
            style = MaterialTheme.typography.bodyLarge,
            color = TextTertiary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        if (action != null) {
            Spacer(Modifier.height(22.dp))
            action()
        }
    }
}
