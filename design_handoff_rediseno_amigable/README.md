# Handoff: Rediseño amigable de AppGym

## Overview
Restyling completo de AppGym (app Android de registro de entrenamientos, Kotlin + Jetpack Compose + Material 3 + Room). Objetivo: interfaz más amigable — oscuro cálido en vez de gris frío, más aire, texto mayor, menos ruido visual (sin micro-etiquetas en mayúsculas ni chips por todas partes), sin emojis. Incluye además 5 mejoras de UX concretas.

## About the Design Files
`AppGym - UI actual.dc.html` es una **referencia de diseño creada en HTML**, no código de producción. La sección `2a` (arriba) es el rediseño; la sección `1a` (abajo) es la recreación de la UI actual, para comparar. La tarea es **recrear el rediseño en el codebase Compose existente** siguiendo sus patrones actuales (Theme.kt, Color.kt, Type.kt, componentes en `ui/components/`, pantallas en `ui/screens/`). No portar el HTML.

## Fidelity
**High-fidelity.** Colores, tipografía, radios y espaciados son valores finales. Recrear píxel a píxel con los componentes Compose existentes.

## Design Tokens (reemplazan los de Color.kt)

Colores (tema oscuro cálido):
- Fondo app: `#171511`
- Tarjeta: `#211E18`
- Tarjeta elevada / input dentro de tarjeta: `#2A2620`
- Superficie de control (chips inactivos, botones secundarios, iconos circulares): `#23211A`
- Borde sutil (solo donde imprescindible): `#322D25`
- Separador barra inferior: `#23211A`
- Texto principal: `#F4F0E6`
- Texto secundario: `#B3AC9D`
- Texto terciario/inactivo: `#7E7869`
- Texto sobre acento destacado dentro de tarjeta lima suave: `#D8D2C4`
- Acento (Volt, se mantiene): `#CBF24C`
- Tinta sobre acento: `#191D07`
- Acento al 14-16% para fondos de badge: `rgba(203,242,76,.14)`
- Borde de tarjeta activa/destacada: `rgba(203,242,76,.3)` a `.4`
- Gradiente de tarjeta "plan activo": `linear-gradient(135°, #2A2E17 → #211E18 al 62%)`
- Colores de grupo muscular (suavizados): coral pecho `#EFA588`, rosa pierna `#E795B1`, azul espalda `#92BFE0`, ámbar `#E9C083`, violeta hombro `#B7A5E6`, lima `#CBF24C`
- Barras de gráfica semanal (rampa hacia lima): `#38391F`, `#43451F`, `#5A611F`, `#7E8B23`, `#A3BC33`, `#CBF24C` (semana actual)

Tipografía — familia **Outfit** (Google Fonts; añadir como FontFamily en Type.kt):
- Título de pantalla: 28sp / 600 / letter-spacing −0.4
- Título de tarjeta destacada: 20sp / 600 / −0.2
- Título de sección (reemplaza a los overline en mayúsculas): 17sp / 600
- Título de fila/tarjeta: 15-16sp / 600
- Cuerpo: 14-14.5sp / 400, line-height 20-22
- Secundario: 13-13.5sp / 400
- Cifra grande (peso corporal): 32sp / 600 / −0.6
- Cifra de stat: 20sp / 600 / −0.3
- Etiqueta de badge: 12sp / 600 / +0.4 (único uso de mayúsculas: "PLAN ACTIVO", "FASE ACTUAL", "SERIE 3")
- Barra inferior: 11.5sp (600 activo / 500 inactivo)

Radios: tarjetas 24dp; tarjetas pequeñas/stats 20dp; filas internas 16-18dp; inputs/steppers 11-14dp; botones, chips, badges y FAB: píldora completa (99dp). Miniaturas de imagen 14-16dp.

Espaciado: margen horizontal de pantalla 22dp; separación entre tarjetas 10-12dp; padding interno de tarjeta 16-18dp; título de sección con 26dp arriba / 12dp abajo.

Iconos: Material Symbols Rounded (FILL=1 en estados activos/botones). **Nunca emojis.**
Sombra: ninguna; la jerarquía se hace con los tonos de superficie.

## Screens / Views

### 1. Hoy (TodayScreen.kt)
- **Header**: saludo "Hola, Alejandro" (15sp, secundario) sobre el nombre de la sesión (28sp). A la derecha, píldora `#23211A` con icono `schedule` lima + "1 h 12 min" (13sp/500). Desaparecen el overline "HOY" y el rango horario del header (el rango pasa a "Editar horas" si se mantiene).
- **Tira de días**: 7 días SIN tarjeta (sin fondo ni borde): letra del día 11sp terciario + número 15sp/500 secundario, apilados con gap 3dp, padding vertical 9dp. Día seleccionado: fondo lima radio 14dp, textos en tinta `#191D07` (letra 600, número 700). Al final de la fila, **botón cuadrado 42dp radio 14dp `#23211A` con icono `calendar_month`** → abre date picker (UX: salto de fecha).
- **Tarjeta resumen de sesión** (sustituye las 3 stat-cards): tarjeta 24dp con anillo de progreso 56dp (track `#322D25` grosor 6, progreso lima con cap redondeado, "57%" centrado 13sp/600) + "Buena sesión, sigue así" (17sp/600) + "12 de 21 series · 6.240 kg movidos" (14sp secundario).
- **Título de sección**: "Sesión de hoy" (17sp/600) con enlace "Tu plan · Fase 0" (13sp/500 lima) a la derecha → navega a PlanScreen (UX: plan visible).
- **Tarjeta de ejercicio completado (colapsada)**: imagen 54dp radio 16dp, nombre 16sp/600, "Completado · 4 de 4 series" (13.5sp secundario), círculo 30dp `rgba(203,242,76,.16)` con check lima. Tap expande.
- **Tarjeta de ejercicio en curso (expandida)**: mismo header con "En curso · 2 de 4 series" y chevron `expand_less`. Series:
  - **Serie hecha**: fila radio 16dp fondo `#2A2620`, `check_circle` lima 18sp, "55 kg × 10" (15sp secundario), **icono `close` 16sp terciario a la derecha para borrar esa serie** (UX: borrar serie visible).
  - **Serie activa**: bloque radio 18dp fondo `#2A2620` borde `rgba(203,242,76,.4)`: etiqueta "SERIE 3" (12sp/600 terciario); fila de steppers — botón − (34dp, radio 11dp, `#211E18`), valor "55 kg" (18sp/600, centrado, flex 1), botón +, divisor vertical 1×24dp `#322D25`, − , "10", + (UX: entrada con menos toques; el incremento de peso es ±2,5 kg, el de reps ±1; tap en el valor abre teclado numérico). Debajo, botón lima 46dp radio 14dp "Marcar serie" con check.
  - **"Añadir otra serie"**: botón fantasma centrado (icono `add` + texto 13.5sp/500 secundario). "Quitar serie" desaparece (cada fila tiene su ×).
- **CTA**: "Añadir ejercicio suelto" — píldora 52dp, fondo `#23211A`, borde `#322D25`, texto 14.5sp/600 claro (ya no lima: el acento se reserva para la acción de la serie activa). "Cambiar de rutina" y "Editar horas" pasan a un menú overflow o quedan como fila secundaria.
- **Barra inferior**: sin píldora contenedora; fondo app + borde superior 1dp `#23211A`. 4 ítems: icono 22sp + label 11.5sp. Activo: lima, FILL=1, peso 600. Inactivo: terciario, sin FILL.

### 2. Máquinas (CatalogScreen.kt)
- Header: "Máquinas" (28sp) + subtítulo "92 ejercicios en tu gimnasio" (15sp secundario). Sin overline.
- Buscador: píldora completa 50dp `#23211A` sin borde, icono `search` + placeholder terciarios.
- Chips de filtro: píldoras sin borde — activo lima/tinta 13sp/600, inactivos `#23211A` secundario 13sp/500.
- Grid 2 columnas, gap 14dp: tarjetas radio 24dp `#211E18`, imagen 118dp **sin gradiente ni barra de color encima**, nombre 15sp/600 (2 líneas, min-height 40dp), y una sola línea "Pecho · Máquina" (13sp) en el color del grupo muscular — sustituye a los dos chips.

### 3. Rutinas (RoutinesScreen.kt)
- Header: "Rutinas" (28sp) + "3 planes guardados". FAB circular 46dp **lima** con `add` en tinta.
- **Tarjeta plan activo** (expandida arriba, sustituye el orden actual): gradiente `#2A2E17→#211E18`, borde lima .3, badge píldora "PLAN ACTIVO", título 20sp, descripción 14sp secundaria (acortada). Días: filas radio 18dp fondo `rgba(23,21,17,.55)`, nombre 15sp/600 + "8 ejercicios · 26 series" (12.5sp), botón píldora "Empezar" — **lima sólo en el día que toca hoy**, resto `#23211A` con texto claro. Desaparece el código de color por día.
- Planes inactivos: tarjetas 24dp normales con nombre 17sp/600, subtítulo 14sp y chevron. Sin barra de color lateral.
- Tarjeta guía: fondo `#23211A`, icono `menu_book` en contenedor 46dp radio 16dp `rgba(233,192,131,.14)` ámbar, título 16sp/600, subtítulo 14sp, chevron.

### 4. Progreso (ProgressScreen.kt)
- Header: "Progreso" (28sp) + "Alejandro · 181 cm · 74,6 kg" (15sp secundario; tap edita perfil — desaparece la tarjeta de perfil separada). Botón circular 46dp `#23211A` con `ios_share`.
- **3 stat-cards arriba** (radio 20dp): icono de color (lima `exercise`, ámbar `local_fire_department`, rosa `event_available`), cifra 20sp/600, label 12.5sp secundario. Sustituyen la fila anterior con barritas de color.
- Peso corporal: título de sección 17sp/600 + botón píldora **lima** "Registrar" con `add`. Tarjeta: "74,6 kg" 32sp/600, badge píldora lima suave "−1,2 kg"; línea lima grosor 3 con relleno degradado y punto final; fechas 12sp terciario.
- Peso por semana: tarjeta con "Esta semana llevas **18.320 kg**" (15sp, cifra en claro/600); 8 barras radio 10dp con la rampa de verdes (sin fondo de track); etiquetas de semana 12sp, la actual en lima/600.
- Se elimina la sección "Lo que más trabajas" (redundante con récords) — confirmar con producto.
- Récords personales: tarjetas-fila radio 20dp `#211E18`: imagen 46dp radio 14dp (sin overlay), nombre 15sp/600, "10 repeticiones" 13sp secundario, peso 17sp/600 en color de grupo muscular.
- **Fila final de 2 tarjetas de acceso** (fondo `#23211A`): "Historial · 34 sesiones" (icono `history`) y "Tu plan · Fase 0 · sem. 2" (icono `map`) (UX: Historial y Plan visibles).

### 5. Tu plan (PlanScreen.kt)
- Header: botón atrás circular 40dp `#23211A`; "Tu plan" (22sp/600) + "Perder grasa, ganar músculo" (14sp secundario, ya no overline en mayúsculas ni lima).
- **Tarjeta fase actual**: mismo tratamiento que "plan activo" de Rutinas (gradiente + borde lima + badge "FASE ACTUAL"); título "Fase 0 · Adaptación" 20sp; "Semana 2 de 3 · 2 días por semana" 14sp; 3 segmentos de progreso 8dp radio píldora (hechos lima, resto lima al 18%); nota 13sp secundaria.
- "Las que te tocan ahora": filas radio 20dp con nombre 15sp/600, meta 13sp ("6 ejercicios · 40 min aprox."), **botón play circular 44dp** — lima en la primera, `#23211A` en el resto.
- "El plan al detalle": acordeones radio 24dp. El abierto lleva borde lima .3; el callout pasa a fondo `rgba(203,242,76,.08)` radio 16dp con icono `lightbulb` lima (sustituye la barra lateral de color); cuerpo 14.5sp line-height 22 secundario.

### Pantallas no rediseñadas aún
Historial, detalle de ejercicio, detalle de rutina y selector múltiple mantienen su estructura actual; aplicarles los mismos tokens (superficies cálidas, Outfit, radios, sin overlines/gradientes sobre imágenes, chips → línea de texto en color de grupo).

## Interactions & Behavior
- Tarjetas de ejercicio en Hoy: acordeón — la completada se colapsa sola (animación 200-250ms, `animateContentSize`); tap en header alterna.
- Steppers: peso ±2,5 kg, reps ±1; long-press repite; tap en el valor abre TextField numérico.
- "Marcar serie" completa la serie activa, la convierte en fila hecha y expande la SERIE siguiente; al completar la última, la tarjeta se colapsa y marca "Completado".
- `close` en serie hecha: elimina con undo (Snackbar), sin diálogo.
- `calendar_month`: DatePickerDialog de Material 3; volver a hoy con tap en el título.
- Navegación nueva: Hoy → PlanScreen (enlace "Tu plan · Fase 0"); Progreso → Historial y PlanScreen (tarjetas finales).
- Estados press: los existentes de Material (ripple); activo/inactivo según tokens.

## State Management
Sin cambios de datos (Room igual). Nuevo estado de UI en TodayScreen: `expandedExerciseId` (acordeón) y `activeSetIndex` por ejercicio. El resto es presentación.

## Assets
- Imágenes de ejercicios: las webp existentes en `app/src/main/assets/exercises/` (aquí copiadas en `assets/exercises/`).
- Fuente Outfit (400-700): Google Fonts, o descargar y añadir a `res/font/`.
- Iconos: Material Symbols Rounded (en Compose, `Icons.Rounded.*` o la librería material-symbols).

## Screenshots
`screenshots/01-hoy.png` … `05-tu-plan.png` — capturas 2x de las 5 pantallas rediseñadas, en orden: Hoy, Máquinas, Rutinas, Progreso, Tu plan.

## Files
- `AppGym - UI actual.dc.html` — sección `2a`: rediseño (5 pantallas); sección `1a`: UI actual recreada (8 pantallas), útil como mapa 1:1 con el código.
- `assets/exercises/*.webp` — imágenes usadas por los mocks.
