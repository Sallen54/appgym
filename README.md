# AppGym

App Android para registrar entrenamientos de gimnasio y planificar rutinas.
Kotlin + Jetpack Compose (Material 3) + Room. Sin conexión a internet: todo es local.

## Cómo abrirlo

1. Android Studio → **Open** → selecciona esta carpeta (`appgym`).
2. La primera sincronización descargará Gradle y las dependencias (necesita internet).
   Si Android Studio avisa de que falta `gradle/wrapper/gradle-wrapper.jar`, acepta
   generarlo o ejecuta *File → Sync Project with Gradle Files*.
3. Run ▶ en un emulador o móvil con Android 8.0 (API 26) o superior.

## Qué hace

**Hoy** — hora de entrada y salida del gimnasio, tira de días para moverte por el calendario, y la sesión del día.
Cada ejercicio es una tarjeta con sus series: peso, repeticiones y check al completarla.
El volumen (kg levantados) se calcula solo.

**Máquinas** — catálogo de 256 ejercicios con foto, buscador (nombre, músculo o material)
y filtros por grupo muscular. Al abrir uno ves tu mejor marca, la evolución del peso y un
botón que busca en YouTube cómo se hace.

**Plan** — desde Rutinas, la guía completa: en qué fase estás, qué rutinas te tocan
esta semana y el porqué de cada cosa.

**Rutinas** — plantillas tipo «Empuje», «Pierna»... con series, repeticiones, peso objetivo
y descanso por ejercicio. Se cargan en la sesión del día con un toque.

**Progreso** — tu perfil (nombre y altura), registro de peso corporal con gráfica de
evolución, y las estadísticas de entrenamiento: volumen por semana, ejercicios más
trabajados, récords personales e historial de sesiones.

No hay login ni cuentas: todo vive en el móvil. La primera vez te pregunta el nombre,
la altura, la edad y el peso (todo opcional) y si quieres empezar con un plan hecho o en
blanco.

## Estructura

```
app/src/main/
├── assets/exercises/          256 imágenes .webp
├── java/com/alejandro/appgym/
│   ├── data/                  Room: entidades, DAOs, repositorio, catálogo
│   ├── ui/
│   │   ├── theme/             paleta, tipografía, formas
│   │   ├── components/        tarjetas, gráficas, carga de imágenes
│   │   ├── screens/           Hoy, Máquinas, Rutinas, Progreso, Historial
│   │   └── AppRoot.kt         navegación y barra inferior
│   ├── util/Format.kt         formato de fechas, pesos y volúmenes en español
│   └── GymViewModel.kt
└── res/
```

## Personalizar

- **Colores**: `ui/theme/Color.kt` (acento principal `Volt`, y un color por grupo muscular).
- **Ejercicios**: `data/ExerciseCatalog.kt`. Para añadir uno, mete su imagen en
  `assets/exercises/<id>.webp` y añade la línea correspondiente.
- **Nombre de la app**: `res/values/strings.xml`.

## El plan cargado

Si en la bienvenida eliges empezar con plan, se carga **Grasa y músculo**
(`data/SeedData.kt`): cuatro días de torso/pierna repartidos por la semana. Está pensado
para un gimnasio normal, con máquinas, poleas, multipower y mancuernas.

Está explicado dentro de la app, en **Rutinas → Tu plan**: fases, cómo progresar, comida y qué esperar mes a mes. Los textos viven en `data/PlanGuide.kt`. Para cambiarlo, edita `SeedData.kt` y sube
`VERSION`: la app sustituye las rutinas automáticas sin tocar las que hayas creado tú ni
las que ya se hayan usado en algún entrenamiento.

## Exportar informe

En la pestaña Progreso, arriba a la derecha, el botón "Informe" genera un `.txt`
con todo el historial (perfil, adherencia, volumen por semana, evolución de cada
ejercicio, récords y peso corporal) y abre la hoja de compartir de Android. Sirve
para guardarlo o mandárselo a alguien que valore si el rendimiento mejora o empeora.

## Dónde se guardan los datos

Base de datos SQLite (Room) en el almacenamiento privado de la app:
`/data/data/com.alejandro.appgym/databases/appgym.db`. Solo la app puede leerla.
Nombre, altura y edad van en `SharedPreferences`. No hay servidor ni cuenta: si desinstalas
la app sin copia de seguridad, se pierde.

## Publicar actualizaciones

El repositorio compila la app sola y publica el APK en cada etiqueta `v*`.
Los pasos para montarlo están en `GITHUB.md`.

## Tests

```
./gradlew test                 # rápidos, sin emulador (formatos y catálogo)
./gradlew connectedAndroidTest # capa de datos, necesita emulador o móvil
```

En Android Studio, botón derecho sobre `FormatTest` o `GymRepositoryTest` → Run.

## Para quien se la instale

El APK está en la pestaña **Releases**. Android avisará de que viene de fuera de Play
Store: hay que darle permiso al navegador para instalar apps desconocidas. No pide
ningún permiso del sistema ni se conecta a nada; lo único que sale del móvil es el
botón "Ver cómo se hace", que abre una búsqueda en YouTube.

## Créditos y licencias

- Imágenes de ejercicios: [free-exercise-db](https://github.com/yuhonas/free-exercise-db) — Unlicense (dominio público).
- Tipografía [Outfit](https://fonts.google.com/specimen/Outfit) — SIL Open Font License 1.1, incluida en `app/src/main/assets/OFL-Outfit.txt`.
