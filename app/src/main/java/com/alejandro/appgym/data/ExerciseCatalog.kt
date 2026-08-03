package com.alejandro.appgym.data

/**
 * Catálogo estático de ejercicios.
 * Las imágenes viven en app/src/main/assets/exercises/<id>.webp
 * Fuente de las imágenes: free-exercise-db (dominio público / Unlicense).
 */
data class Exercise(
    val id: String,
    val name: String,
    val nameEn: String,
    val group: String,
    val equipment: String,
    val level: String,
    val isCardio: Boolean,
    val primaryMuscles: List<String>,
    val secondaryMuscles: List<String>
) {
    val imageAsset: String get() = "exercises/$id.webp"
}

object ExerciseCatalog {

    val groups: List<String> = listOf(
        "Pecho", "Espalda", "Hombro", "Bíceps", "Tríceps", "Pierna", "Trapecio", "Core", "Cardio"
    )

    val all: List<Exercise> = listOf(
        Exercise("press_de_pecho_en_maquina", "Press de pecho en máquina", "Machine Bench Press", "Pecho", "Máquina", "Principiante", false, listOf("Pecho"), listOf("Hombros", "Tríceps")),
        Exercise("press_inclinado_en_maquina", "Press inclinado en máquina", "Leverage Incline Chest Press", "Pecho", "Máquina", "Principiante", false, listOf("Pecho"), listOf("Hombros", "Tríceps")),
        Exercise("press_declinado_en_maquina", "Press declinado en máquina", "Leverage Decline Chest Press", "Pecho", "Máquina", "Principiante", false, listOf("Pecho"), listOf("Hombros", "Tríceps")),
        Exercise("contractor_de_pecho_peck_deck", "Contractor de pecho (peck deck)", "Butterfly", "Pecho", "Máquina", "Principiante", false, listOf("Pecho"), listOf()),
        Exercise("cruce_de_poleas", "Cruce de poleas", "Cable Crossover", "Pecho", "Polea", "Principiante", false, listOf("Pecho"), listOf("Hombros")),
        Exercise("cruce_de_poleas_bajas", "Cruce de poleas bajas", "Low Cable Crossover", "Pecho", "Polea", "Principiante", false, listOf("Pecho"), listOf("Hombros")),
        Exercise("aperturas_en_polea_inclinado", "Aperturas en polea inclinado", "Incline Cable Flye", "Pecho", "Polea", "Intermedio", false, listOf("Pecho"), listOf("Hombros")),
        Exercise("press_de_pecho_en_polea", "Press de pecho en polea", "Cable Chest Press", "Pecho", "Polea", "Principiante", false, listOf("Pecho"), listOf("Hombros", "Tríceps")),
        Exercise("press_banca_en_multipower", "Press banca en multipower", "Smith Machine Bench Press", "Pecho", "Máquina", "Principiante", false, listOf("Pecho"), listOf("Hombros", "Tríceps")),
        Exercise("press_inclinado_en_multipower", "Press inclinado en multipower", "Smith Machine Incline Bench Press", "Pecho", "Máquina", "Principiante", false, listOf("Pecho"), listOf("Hombros", "Tríceps")),
        Exercise("press_banca_con_barra", "Press banca con barra", "Barbell Bench Press - Medium Grip", "Pecho", "Barra", "Principiante", false, listOf("Pecho"), listOf("Hombros", "Tríceps")),
        Exercise("press_banca_con_mancuernas", "Press banca con mancuernas", "Dumbbell Bench Press", "Pecho", "Mancuernas", "Principiante", false, listOf("Pecho"), listOf("Hombros", "Tríceps")),
        Exercise("press_inclinado_con_mancuernas", "Press inclinado con mancuernas", "Incline Dumbbell Press", "Pecho", "Mancuernas", "Principiante", false, listOf("Pecho"), listOf("Hombros", "Tríceps")),
        Exercise("jalon_al_pecho_agarre_ancho", "Jalón al pecho agarre ancho", "Wide-Grip Lat Pulldown", "Espalda", "Polea", "Principiante", false, listOf("Dorsales"), listOf("Bíceps", "Espalda media", "Hombros")),
        Exercise("jalon_al_pecho_agarre_cerrado", "Jalón al pecho agarre cerrado", "Close-Grip Front Lat Pulldown", "Espalda", "Polea", "Principiante", false, listOf("Dorsales"), listOf("Bíceps", "Espalda media", "Hombros")),
        Exercise("jalon_con_barra_en_v", "Jalón con barra en V", "V-Bar Pulldown", "Espalda", "Polea", "Intermedio", false, listOf("Dorsales"), listOf("Bíceps", "Espalda media", "Hombros")),
        Exercise("jalon_agarre_supino", "Jalón agarre supino", "Underhand Cable Pulldowns", "Espalda", "Polea", "Principiante", false, listOf("Dorsales"), listOf("Bíceps", "Espalda media", "Hombros")),
        Exercise("jalon_a_una_mano", "Jalón a una mano", "One Arm Lat Pulldown", "Espalda", "Polea", "Principiante", false, listOf("Dorsales"), listOf("Bíceps", "Espalda media")),
        Exercise("remo_en_polea_sentado", "Remo en polea sentado", "Seated Cable Rows", "Espalda", "Polea", "Principiante", false, listOf("Espalda media"), listOf("Bíceps", "Dorsales", "Hombros")),
        Exercise("remo_en_polea_a_una_mano", "Remo en polea a una mano", "Seated One-arm Cable Pulley Rows", "Espalda", "Polea", "Intermedio", false, listOf("Espalda media"), listOf("Bíceps", "Dorsales", "Trapecios")),
        Exercise("remo_alto_en_maquina", "Remo alto en máquina", "Leverage High Row", "Espalda", "Máquina", "Principiante", false, listOf("Espalda media"), listOf("Dorsales")),
        Exercise("remo_iso_lateral_en_maquina", "Remo iso-lateral en máquina", "Leverage Iso Row", "Espalda", "Máquina", "Principiante", false, listOf("Dorsales"), listOf("Bíceps", "Espalda media")),
        Exercise("pull_over_en_polea", "Pull-over en polea", "Straight-Arm Pulldown", "Espalda", "Polea", "Principiante", false, listOf("Dorsales"), listOf()),
        Exercise("pull_over_en_polea_con_cuerda", "Pull-over en polea con cuerda", "Rope Straight-Arm Pulldown", "Espalda", "Polea", "Principiante", false, listOf("Dorsales"), listOf()),
        Exercise("remo_en_t_tumbado", "Remo en T tumbado", "Lying T-Bar Row", "Espalda", "Máquina", "Intermedio", false, listOf("Espalda media"), listOf("Bíceps", "Dorsales")),
        Exercise("remo_inclinado_en_multipower", "Remo inclinado en multipower", "Smith Machine Bent Over Row", "Espalda", "Máquina", "Principiante", false, listOf("Espalda media"), listOf("Bíceps", "Dorsales", "Hombros")),
        Exercise("remo_con_barra", "Remo con barra", "Bent Over Barbell Row", "Espalda", "Barra", "Principiante", false, listOf("Espalda media"), listOf("Bíceps", "Dorsales", "Hombros")),
        Exercise("dominadas", "Dominadas", "Pullups", "Espalda", "Peso corporal", "Principiante", false, listOf("Dorsales"), listOf("Bíceps", "Espalda media")),
        Exercise("press_militar_en_maquina", "Press militar en máquina", "Machine Shoulder (Military) Press", "Hombro", "Máquina", "Principiante", false, listOf("Hombros"), listOf("Tríceps")),
        Exercise("press_de_hombro_en_maquina", "Press de hombro en máquina", "Leverage Shoulder Press", "Hombro", "Máquina", "Principiante", false, listOf("Hombros"), listOf("Tríceps")),
        Exercise("press_de_hombro_en_polea", "Press de hombro en polea", "Seated Cable Shoulder Press", "Hombro", "Polea", "Principiante", false, listOf("Hombros"), listOf("Tríceps")),
        Exercise("press_militar_en_multipower", "Press militar en multipower", "Smith Machine Overhead Shoulder Press", "Hombro", "Máquina", "Principiante", false, listOf("Hombros"), listOf("Tríceps")),
        Exercise("elevacion_lateral_en_polea", "Elevación lateral en polea", "Cable Seated Lateral Raise", "Hombro", "Polea", "Principiante", false, listOf("Hombros"), listOf("Espalda media", "Trapecios")),
        Exercise("elevacion_frontal_en_polea", "Elevación frontal en polea", "Front Cable Raise", "Hombro", "Polea", "Principiante", false, listOf("Hombros"), listOf()),
        Exercise("aperturas_invertidas_en_maquina", "Aperturas invertidas en máquina", "Reverse Machine Flyes", "Hombro", "Máquina", "Principiante", false, listOf("Hombros"), listOf()),
        Exercise("aperturas_posteriores_en_polea", "Aperturas posteriores en polea", "Cable Rear Delt Fly", "Hombro", "Polea", "Principiante", false, listOf("Hombros"), listOf()),
        Exercise("face_pull", "Face pull", "Face Pull", "Hombro", "Polea", "Intermedio", false, listOf("Hombros"), listOf("Espalda media")),
        Exercise("remo_al_menton_en_polea", "Remo al mentón en polea", "Upright Cable Row", "Hombro", "Polea", "Intermedio", false, listOf("Trapecios"), listOf("Hombros")),
        Exercise("elevacion_lateral_con_mancuernas", "Elevación lateral con mancuernas", "Side Lateral Raise", "Hombro", "Mancuernas", "Principiante", false, listOf("Hombros"), listOf()),
        Exercise("curl_de_biceps_en_maquina", "Curl de bíceps en máquina", "Machine Bicep Curl", "Bíceps", "Máquina", "Principiante", false, listOf("Bíceps"), listOf()),
        Exercise("curl_predicador_en_maquina", "Curl predicador en máquina", "Machine Preacher Curls", "Bíceps", "Máquina", "Principiante", false, listOf("Bíceps"), listOf()),
        Exercise("curl_de_biceps_en_polea", "Curl de bíceps en polea", "Standing Biceps Cable Curl", "Bíceps", "Polea", "Principiante", false, listOf("Bíceps"), listOf()),
        Exercise("curl_martillo_en_polea", "Curl martillo en polea", "Cable Hammer Curls - Rope Attachment", "Bíceps", "Polea", "Principiante", false, listOf("Bíceps"), listOf()),
        Exercise("curl_predicador_en_polea", "Curl predicador en polea", "Cable Preacher Curl", "Bíceps", "Polea", "Principiante", false, listOf("Bíceps"), listOf("Antebrazos")),
        Exercise("curl_en_poleas_altas", "Curl en poleas altas", "High Cable Curls", "Bíceps", "Polea", "Intermedio", false, listOf("Bíceps"), listOf()),
        Exercise("curl_inverso_en_polea", "Curl inverso en polea", "Reverse Cable Curl", "Bíceps", "Polea", "Principiante", false, listOf("Bíceps"), listOf("Antebrazos")),
        Exercise("curl_con_barra", "Curl con barra", "Barbell Curl", "Bíceps", "Barra", "Principiante", false, listOf("Bíceps"), listOf("Antebrazos")),
        Exercise("curl_con_mancuernas", "Curl con mancuernas", "Dumbbell Bicep Curl", "Bíceps", "Mancuernas", "Principiante", false, listOf("Bíceps"), listOf("Antebrazos")),
        Exercise("extension_de_triceps_en_polea", "Extensión de tríceps en polea", "Triceps Pushdown", "Tríceps", "Polea", "Principiante", false, listOf("Tríceps"), listOf()),
        Exercise("extension_de_triceps_con_cuerda", "Extensión de tríceps con cuerda", "Triceps Pushdown - Rope Attachment", "Tríceps", "Polea", "Principiante", false, listOf("Tríceps"), listOf()),
        Exercise("extension_de_triceps_barra_v", "Extensión de tríceps barra V", "Triceps Pushdown - V-Bar Attachment", "Tríceps", "Polea", "Principiante", false, listOf("Tríceps"), listOf()),
        Exercise("extension_de_triceps_agarre_supino", "Extensión de tríceps agarre supino", "Reverse Grip Triceps Pushdown", "Tríceps", "Polea", "Principiante", false, listOf("Tríceps"), listOf()),
        Exercise("extension_sobre_la_cabeza_con_cuerda", "Extensión sobre la cabeza con cuerda", "Cable Rope Overhead Triceps Extension", "Tríceps", "Polea", "Principiante", false, listOf("Tríceps"), listOf()),
        Exercise("extension_de_triceps_en_maquina", "Extensión de tríceps en máquina", "Machine Triceps Extension", "Tríceps", "Máquina", "Principiante", false, listOf("Tríceps"), listOf()),
        Exercise("fondos_en_maquina", "Fondos en máquina", "Dip Machine", "Tríceps", "Máquina", "Principiante", false, listOf("Tríceps"), listOf("Pecho", "Hombros")),
        Exercise("press_cerrado_en_multipower", "Press cerrado en multipower", "Smith Machine Close-Grip Bench Press", "Tríceps", "Máquina", "Principiante", false, listOf("Tríceps"), listOf("Pecho", "Hombros")),
        Exercise("prensa_de_piernas", "Prensa de piernas", "Leg Press", "Pierna", "Máquina", "Principiante", false, listOf("Cuádriceps"), listOf("Gemelos", "Glúteos", "Isquiotibiales")),
        Exercise("prensa_con_pies_juntos", "Prensa con pies juntos", "Narrow Stance Leg Press", "Pierna", "Máquina", "Intermedio", false, listOf("Cuádriceps"), listOf("Gemelos", "Glúteos", "Isquiotibiales")),
        Exercise("extension_de_cuadriceps", "Extensión de cuádriceps", "Leg Extensions", "Pierna", "Máquina", "Principiante", false, listOf("Cuádriceps"), listOf()),
        Exercise("extension_de_cuadriceps_a_una_pierna", "Extensión de cuádriceps a una pierna", "Single-Leg Leg Extension", "Pierna", "Máquina", "Principiante", false, listOf("Cuádriceps"), listOf()),
        Exercise("curl_femoral_tumbado", "Curl femoral tumbado", "Lying Leg Curls", "Pierna", "Máquina", "Principiante", false, listOf("Isquiotibiales"), listOf()),
        Exercise("curl_femoral_sentado", "Curl femoral sentado", "Seated Leg Curl", "Pierna", "Máquina", "Principiante", false, listOf("Isquiotibiales"), listOf()),
        Exercise("curl_femoral_de_pie", "Curl femoral de pie", "Standing Leg Curl", "Pierna", "Máquina", "Principiante", false, listOf("Isquiotibiales"), listOf()),
        Exercise("hack_squat", "Hack squat", "Hack Squat", "Pierna", "Máquina", "Principiante", false, listOf("Cuádriceps"), listOf("Gemelos", "Glúteos", "Isquiotibiales")),
        Exercise("sentadilla_en_multipower", "Sentadilla en multipower", "Smith Machine Squat", "Pierna", "Máquina", "Principiante", false, listOf("Cuádriceps"), listOf("Gemelos", "Glúteos", "Isquiotibiales", "Lumbares")),
        Exercise("prensa_en_multipower", "Prensa en multipower", "Smith Machine Leg Press", "Pierna", "Máquina", "Intermedio", false, listOf("Cuádriceps"), listOf("Gemelos", "Glúteos", "Isquiotibiales")),
        Exercise("abductores_en_maquina", "Abductores en máquina", "Thigh Abductor", "Pierna", "Máquina", "Principiante", false, listOf("Abductores"), listOf("Glúteos")),
        Exercise("aductores_en_maquina", "Aductores en máquina", "Thigh Adductor", "Pierna", "Máquina", "Principiante", false, listOf("Aductores"), listOf("Glúteos", "Isquiotibiales")),
        Exercise("patada_de_gluteo_en_polea", "Patada de glúteo en polea", "One-Legged Cable Kickback", "Pierna", "Polea", "Intermedio", false, listOf("Glúteos"), listOf("Isquiotibiales")),
        Exercise("pull_through_en_polea", "Pull through en polea", "Pull Through", "Pierna", "Polea", "Principiante", false, listOf("Glúteos"), listOf("Isquiotibiales", "Lumbares")),
        Exercise("elevacion_gluteo_femoral", "Elevación glúteo-femoral", "Glute Ham Raise", "Pierna", "Máquina", "Intermedio", false, listOf("Isquiotibiales"), listOf("Gemelos", "Glúteos")),
        Exercise("sentadilla_con_barra", "Sentadilla con barra", "Barbell Squat", "Pierna", "Barra", "Principiante", false, listOf("Cuádriceps"), listOf("Gemelos", "Glúteos", "Isquiotibiales", "Lumbares")),
        Exercise("peso_muerto_rumano", "Peso muerto rumano", "Romanian Deadlift", "Pierna", "Barra", "Intermedio", false, listOf("Isquiotibiales"), listOf("Gemelos", "Glúteos", "Lumbares")),
        Exercise("elevacion_de_gemelos_de_pie", "Elevación de gemelos de pie", "Standing Calf Raises", "Pierna", "Máquina", "Principiante", false, listOf("Gemelos"), listOf()),
        Exercise("elevacion_de_gemelos_sentado", "Elevación de gemelos sentado", "Seated Calf Raise", "Pierna", "Máquina", "Principiante", false, listOf("Gemelos"), listOf()),
        Exercise("gemelos_en_prensa", "Gemelos en prensa", "Calf Press On The Leg Press Machine", "Pierna", "Máquina", "Principiante", false, listOf("Gemelos"), listOf()),
        Exercise("encogimientos_en_maquina", "Encogimientos en máquina", "Leverage Shrug", "Trapecio", "Máquina", "Principiante", false, listOf("Trapecios"), listOf("Antebrazos")),
        Exercise("encogimientos_en_polea", "Encogimientos en polea", "Cable Shrugs", "Trapecio", "Polea", "Principiante", false, listOf("Trapecios"), listOf()),
        Exercise("remo_al_menton_en_multipower", "Remo al mentón en multipower", "Smith Machine Upright Row", "Trapecio", "Máquina", "Principiante", false, listOf("Trapecios"), listOf("Bíceps", "Espalda media", "Hombros")),
        Exercise("crunch_en_maquina", "Crunch en máquina", "Ab Crunch Machine", "Core", "Máquina", "Intermedio", false, listOf("Abdominales"), listOf()),
        Exercise("crunch_en_polea", "Crunch en polea", "Cable Crunch", "Core", "Polea", "Principiante", false, listOf("Abdominales"), listOf()),
        Exercise("crunch_con_cuerda", "Crunch con cuerda", "Rope Crunch", "Core", "Polea", "Principiante", false, listOf("Abdominales"), listOf()),
        Exercise("crunch_inverso_en_polea", "Crunch inverso en polea", "Cable Reverse Crunch", "Core", "Polea", "Principiante", false, listOf("Abdominales"), listOf()),
        Exercise("lenador_en_polea", "Leñador en polea", "Standing Cable Wood Chop", "Core", "Polea", "Principiante", false, listOf("Abdominales"), listOf("Hombros")),
        Exercise("pallof_press", "Pallof press", "Pallof Press", "Core", "Polea", "Principiante", false, listOf("Abdominales"), listOf("Pecho", "Hombros", "Tríceps")),
        Exercise("elevacion_de_piernas_colgado", "Elevación de piernas colgado", "Hanging Leg Raise", "Core", "Peso corporal", "Avanzado", false, listOf("Abdominales"), listOf()),
        Exercise("plancha_abdominal", "Plancha abdominal", "Plank", "Core", "Peso corporal", "Principiante", false, listOf("Abdominales"), listOf()),
        Exercise("cinta_de_correr", "Cinta de correr", "Running, Treadmill", "Cardio", "Máquina", "Principiante", true, listOf("Cuádriceps"), listOf("Gemelos", "Glúteos", "Isquiotibiales")),
        Exercise("eliptica", "Elíptica", "Elliptical Trainer", "Cardio", "Máquina", "Intermedio", true, listOf("Cuádriceps"), listOf("Gemelos", "Glúteos", "Isquiotibiales")),
        Exercise("bicicleta_estatica", "Bicicleta estática", "Bicycling, Stationary", "Cardio", "Máquina", "Principiante", true, listOf("Cuádriceps"), listOf("Gemelos", "Glúteos", "Isquiotibiales")),
        Exercise("remo_ergometro", "Remo ergómetro", "Rowing, Stationary", "Cardio", "Máquina", "Intermedio", true, listOf("Cuádriceps"), listOf("Bíceps", "Gemelos", "Glúteos", "Isquiotibiales", "Lumbares", "Espalda media")),
        Exercise("escaladora", "Escaladora", "Stairmaster", "Cardio", "Máquina", "Intermedio", true, listOf("Cuádriceps"), listOf("Gemelos", "Glúteos", "Isquiotibiales")),

        /* --- Pecho (ampliación) --- */
        Exercise("press_inclinado_con_barra", "Press inclinado con barra", "Barbell Incline Bench Press - Medium Grip", "Pecho", "Barra", "Principiante", false, listOf("Pecho"), listOf("Hombros", "Tríceps")),
        Exercise("press_declinado_con_barra", "Press declinado con barra", "Decline Barbell Bench Press", "Pecho", "Barra", "Principiante", false, listOf("Pecho"), listOf("Hombros", "Tríceps")),
        Exercise("press_banca_agarre_ancho", "Press banca agarre ancho", "Wide-Grip Barbell Bench Press", "Pecho", "Barra", "Intermedio", false, listOf("Pecho"), listOf("Hombros", "Tríceps")),
        Exercise("press_declinado_con_mancuernas", "Press declinado con mancuernas", "Decline Dumbbell Bench Press", "Pecho", "Mancuernas", "Principiante", false, listOf("Pecho"), listOf("Hombros", "Tríceps")),
        Exercise("press_banca_con_mancuernas_agarre_neutro", "Press banca con mancuernas agarre neutro", "Dumbbell Bench Press with Neutral Grip", "Pecho", "Mancuernas", "Principiante", false, listOf("Pecho"), listOf("Hombros", "Tríceps")),
        Exercise("press_banca_con_mancuerna_a_una_mano", "Press banca con mancuerna a una mano", "One Arm Dumbbell Bench Press", "Pecho", "Mancuernas", "Principiante", false, listOf("Pecho"), listOf("Hombros", "Tríceps")),
        Exercise("aperturas_con_mancuernas", "Aperturas con mancuernas", "Dumbbell Flyes", "Pecho", "Mancuernas", "Principiante", false, listOf("Pecho"), listOf()),
        Exercise("aperturas_inclinado_con_mancuernas", "Aperturas inclinado con mancuernas", "Incline Dumbbell Flyes", "Pecho", "Mancuernas", "Principiante", false, listOf("Pecho"), listOf("Hombros")),
        Exercise("aperturas_declinado_con_mancuernas", "Aperturas declinado con mancuernas", "Decline Dumbbell Flyes", "Pecho", "Mancuernas", "Principiante", false, listOf("Pecho"), listOf()),
        Exercise("pullover_con_mancuerna", "Pullover con mancuerna", "Straight-Arm Dumbbell Pullover", "Pecho", "Mancuernas", "Intermedio", false, listOf("Pecho"), listOf("Dorsales", "Hombros", "Tríceps")),
        Exercise("aperturas_en_polea_tumbado", "Aperturas en polea tumbado", "Flat Bench Cable Flyes", "Pecho", "Polea", "Intermedio", false, listOf("Pecho"), listOf()),
        Exercise("press_inclinado_en_polea", "Press inclinado en polea", "Incline Cable Chest Press", "Pecho", "Polea", "Principiante", false, listOf("Pecho"), listOf("Hombros", "Tríceps")),
        Exercise("cruce_de_polea_a_una_mano", "Cruce de polea a una mano", "Single-Arm Cable Crossover", "Pecho", "Polea", "Principiante", false, listOf("Pecho"), listOf()),
        Exercise("press_declinado_en_multipower", "Press declinado en multipower", "Smith Machine Decline Press", "Pecho", "Máquina", "Principiante", false, listOf("Pecho"), listOf("Hombros", "Tríceps")),
        Exercise("flexiones", "Flexiones", "Pushups", "Pecho", "Peso corporal", "Principiante", false, listOf("Pecho"), listOf("Hombros", "Tríceps")),
        Exercise("flexiones_con_pies_elevados", "Flexiones con pies elevados", "Push-Ups With Feet Elevated", "Pecho", "Peso corporal", "Principiante", false, listOf("Pecho"), listOf("Hombros", "Tríceps")),
        Exercise("flexiones_con_manos_elevadas", "Flexiones con manos elevadas", "Incline Push-Up", "Pecho", "Peso corporal", "Principiante", false, listOf("Pecho"), listOf("Hombros", "Tríceps")),
        Exercise("flexiones_agarre_ancho", "Flexiones agarre ancho", "Push-Up Wide", "Pecho", "Peso corporal", "Principiante", false, listOf("Pecho"), listOf("Abdominales", "Hombros", "Tríceps")),

        /* --- Espalda (ampliación) --- */
        Exercise("peso_muerto_con_barra", "Peso muerto con barra", "Barbell Deadlift", "Espalda", "Barra", "Intermedio", false, listOf("Lumbares"), listOf("Gemelos", "Antebrazos", "Glúteos", "Isquiotibiales", "Dorsales", "Espalda media", "Cuádriceps", "Trapecios")),
        Exercise("peso_muerto_desde_rack", "Peso muerto desde rack", "Rack Pulls", "Espalda", "Barra", "Intermedio", false, listOf("Lumbares"), listOf("Antebrazos", "Glúteos", "Isquiotibiales", "Trapecios")),
        Exercise("remo_con_dos_mancuernas", "Remo con dos mancuernas", "Bent Over Two-Dumbbell Row", "Espalda", "Mancuernas", "Principiante", false, listOf("Espalda media"), listOf("Bíceps", "Dorsales", "Hombros")),
        Exercise("remo_con_mancuernas_agarre_neutro", "Remo con mancuernas agarre neutro", "Bent Over Two-Dumbbell Row With Palms In", "Espalda", "Mancuernas", "Principiante", false, listOf("Espalda media"), listOf("Bíceps", "Dorsales")),
        Exercise("remo_con_mancuerna_a_una_mano", "Remo con mancuerna a una mano", "One-Arm Dumbbell Row", "Espalda", "Mancuernas", "Principiante", false, listOf("Espalda media"), listOf("Bíceps", "Dorsales", "Hombros")),
        Exercise("remo_con_mancuernas_en_banco_inclinado", "Remo con mancuernas en banco inclinado", "Dumbbell Incline Row", "Espalda", "Mancuernas", "Principiante", false, listOf("Espalda media"), listOf("Bíceps", "Antebrazos", "Dorsales", "Hombros")),
        Exercise("remo_con_barra_agarre_supino", "Remo con barra agarre supino", "Reverse Grip Bent-Over Rows", "Espalda", "Barra", "Intermedio", false, listOf("Espalda media"), listOf("Bíceps", "Dorsales", "Hombros")),
        Exercise("remo_en_t", "Remo en T", "T-Bar Row with Handle", "Espalda", "Barra", "Principiante", false, listOf("Espalda media"), listOf("Bíceps", "Dorsales")),
        Exercise("dominadas_agarre_supino", "Dominadas agarre supino", "Chin-Up", "Espalda", "Peso corporal", "Principiante", false, listOf("Dorsales"), listOf("Bíceps", "Antebrazos", "Espalda media")),
        Exercise("dominadas_con_agarre_en_v", "Dominadas con agarre en V", "V-Bar Pullup", "Espalda", "Peso corporal", "Principiante", false, listOf("Dorsales"), listOf("Bíceps", "Espalda media", "Hombros")),
        Exercise("dominadas_tras_nuca", "Dominadas tras nuca", "Wide-Grip Rear Pull-Up", "Espalda", "Peso corporal", "Intermedio", false, listOf("Dorsales"), listOf("Bíceps", "Espalda media", "Hombros")),
        Exercise("jalon_al_pecho_recorrido_completo", "Jalón al pecho recorrido completo", "Full Range-Of-Motion Lat Pulldown", "Espalda", "Polea", "Intermedio", false, listOf("Dorsales"), listOf("Bíceps", "Espalda media", "Hombros")),
        Exercise("jalon_tras_nuca", "Jalón tras nuca", "Wide-Grip Pulldown Behind The Neck", "Espalda", "Polea", "Intermedio", false, listOf("Dorsales"), listOf("Bíceps", "Espalda media", "Hombros")),
        Exercise("remo_en_polea_alta_de_rodillas", "Remo en polea alta de rodillas", "Kneeling High Pulley Row", "Espalda", "Polea", "Principiante", false, listOf("Dorsales"), listOf("Bíceps", "Espalda media")),
        Exercise("superman", "Superman", "Superman", "Espalda", "Peso corporal", "Principiante", false, listOf("Lumbares"), listOf("Glúteos", "Isquiotibiales")),
        Exercise("extensiones_lumbares_en_el_suelo", "Extensiones lumbares en el suelo", "Hyperextensions With No Hyperextension Bench", "Espalda", "Peso corporal", "Intermedio", false, listOf("Lumbares"), listOf("Glúteos", "Isquiotibiales")),

        /* --- Hombro (ampliación) --- */
        Exercise("press_militar_con_barra", "Press militar con barra", "Barbell Shoulder Press", "Hombro", "Barra", "Intermedio", false, listOf("Hombros"), listOf("Pecho", "Tríceps")),
        Exercise("press_militar_sentado_con_barra", "Press militar sentado con barra", "Seated Barbell Military Press", "Hombro", "Barra", "Intermedio", false, listOf("Hombros"), listOf("Tríceps")),
        Exercise("press_militar_de_pie", "Press militar de pie", "Standing Military Press", "Hombro", "Barra", "Principiante", false, listOf("Hombros"), listOf("Tríceps")),
        Exercise("press_de_hombro_con_mancuernas", "Press de hombro con mancuernas", "Dumbbell Shoulder Press", "Hombro", "Mancuernas", "Intermedio", false, listOf("Hombros"), listOf("Tríceps")),
        Exercise("press_de_hombro_sentado_con_mancuernas", "Press de hombro sentado con mancuernas", "Seated Dumbbell Press", "Hombro", "Mancuernas", "Principiante", false, listOf("Hombros"), listOf("Tríceps")),
        Exercise("press_arnold", "Press Arnold", "Arnold Dumbbell Press", "Hombro", "Mancuernas", "Intermedio", false, listOf("Hombros"), listOf("Tríceps")),
        Exercise("push_press_con_barra", "Push press con barra", "Push Press", "Hombro", "Barra", "Avanzado", false, listOf("Hombros"), listOf("Cuádriceps", "Tríceps")),
        Exercise("press_cubano", "Press cubano", "Cuban Press", "Hombro", "Mancuernas", "Intermedio", false, listOf("Hombros"), listOf("Trapecios")),
        Exercise("elevaciones_frontales_con_mancuernas", "Elevaciones frontales con mancuernas", "Front Dumbbell Raise", "Hombro", "Mancuernas", "Principiante", false, listOf("Hombros"), listOf()),
        Exercise("elevaciones_laterales_sentado", "Elevaciones laterales sentado", "Seated Side Lateral Raise", "Hombro", "Mancuernas", "Principiante", false, listOf("Hombros"), listOf()),
        Exercise("elevaciones_laterales_y_frontales", "Elevaciones laterales y frontales", "Side Laterals to Front Raise", "Hombro", "Mancuernas", "Principiante", false, listOf("Hombros"), listOf("Trapecios")),
        Exercise("elevacion_en_plano_escapular", "Elevación en plano escapular", "Dumbbell Scaption", "Hombro", "Mancuernas", "Principiante", false, listOf("Hombros"), listOf("Trapecios")),
        Exercise("elevacion_lateral_en_polea_baja", "Elevación lateral en polea baja", "Standing Low-Pulley Deltoid Raise", "Hombro", "Polea", "Principiante", false, listOf("Hombros"), listOf("Antebrazos")),
        Exercise("elevacion_lateral_en_polea_inclinado", "Elevación lateral en polea inclinado", "Bent Over Low-Pulley Side Lateral", "Hombro", "Polea", "Principiante", false, listOf("Hombros"), listOf("Lumbares", "Espalda media", "Trapecios")),
        Exercise("aperturas_invertidas_con_mancuernas", "Aperturas invertidas con mancuernas", "Reverse Flyes", "Hombro", "Mancuernas", "Principiante", false, listOf("Hombros"), listOf()),
        Exercise("pajaros_sentado", "Pájaros sentado", "Seated Bent-Over Rear Delt Raise", "Hombro", "Mancuernas", "Intermedio", false, listOf("Hombros"), listOf()),
        Exercise("pajaros_tumbado", "Pájaros tumbado", "Dumbbell Lying Rear Lateral Raise", "Hombro", "Mancuernas", "Intermedio", false, listOf("Hombros"), listOf()),
        Exercise("remo_al_menton_con_barra", "Remo al mentón con barra", "Upright Barbell Row", "Hombro", "Barra", "Principiante", false, listOf("Hombros"), listOf("Trapecios")),
        Exercise("remo_al_menton_con_mancuerna_a_una_mano", "Remo al mentón con mancuerna a una mano", "Dumbbell One-Arm Upright Row", "Hombro", "Mancuernas", "Intermedio", false, listOf("Hombros"), listOf("Bíceps", "Trapecios")),
        Exercise("rotacion_externa_en_polea", "Rotación externa en polea", "External Rotation with Cable", "Hombro", "Polea", "Principiante", false, listOf("Hombros"), listOf()),
        Exercise("rotacion_interna_en_polea", "Rotación interna en polea", "Cable Internal Rotation", "Hombro", "Polea", "Principiante", false, listOf("Hombros"), listOf()),
        Exercise("aperturas_con_banda_elastica", "Aperturas con banda elástica", "Band Pull Apart", "Hombro", "Bandas", "Principiante", false, listOf("Hombros"), listOf("Espalda media", "Trapecios")),

        /* --- Bíceps (ampliación) --- */
        Exercise("curl_con_barra_z", "Curl con barra Z", "EZ-Bar Curl", "Bíceps", "Barra Z", "Principiante", false, listOf("Bíceps"), listOf()),
        Exercise("curl_con_barra_agarre_ancho", "Curl con barra agarre ancho", "Wide-Grip Standing Barbell Curl", "Bíceps", "Barra", "Principiante", false, listOf("Bíceps"), listOf()),
        Exercise("curl_con_barra_agarre_cerrado", "Curl con barra agarre cerrado", "Close-Grip Standing Barbell Curl", "Bíceps", "Barra", "Principiante", false, listOf("Bíceps"), listOf("Antebrazos")),
        Exercise("curl_en_banco_predicador", "Curl en banco predicador", "Preacher Curl", "Bíceps", "Barra", "Principiante", false, listOf("Bíceps"), listOf()),
        Exercise("curl_predicador_con_mancuernas", "Curl predicador con mancuernas", "Two-Arm Dumbbell Preacher Curl", "Bíceps", "Mancuernas", "Principiante", false, listOf("Bíceps"), listOf()),
        Exercise("curl_predicador_a_una_mano", "Curl predicador a una mano", "One Arm Dumbbell Preacher Curl", "Bíceps", "Mancuernas", "Principiante", false, listOf("Bíceps"), listOf()),
        Exercise("curl_concentrado", "Curl concentrado", "Concentration Curls", "Bíceps", "Mancuernas", "Principiante", false, listOf("Bíceps"), listOf("Antebrazos")),
        Exercise("curl_martillo", "Curl martillo", "Hammer Curls", "Bíceps", "Mancuernas", "Principiante", false, listOf("Bíceps"), listOf()),
        Exercise("curl_martillo_inclinado", "Curl martillo inclinado", "Incline Hammer Curls", "Bíceps", "Mancuernas", "Principiante", false, listOf("Bíceps"), listOf()),
        Exercise("curl_martillo_cruzado", "Curl martillo cruzado", "Cross Body Hammer Curl", "Bíceps", "Mancuernas", "Principiante", false, listOf("Bíceps"), listOf("Antebrazos")),
        Exercise("curl_inclinado_con_mancuernas", "Curl inclinado con mancuernas", "Incline Dumbbell Curl", "Bíceps", "Mancuernas", "Principiante", false, listOf("Bíceps"), listOf()),
        Exercise("curl_sentado_con_mancuernas", "Curl sentado con mancuernas", "Seated Dumbbell Curl", "Bíceps", "Mancuernas", "Principiante", false, listOf("Bíceps"), listOf()),
        Exercise("curl_en_polea_a_una_mano", "Curl en polea a una mano", "Standing One-Arm Cable Curl", "Bíceps", "Polea", "Intermedio", false, listOf("Bíceps"), listOf()),
        Exercise("curl_en_polea_por_encima_de_la_cabeza", "Curl en polea por encima de la cabeza", "Overhead Cable Curl", "Bíceps", "Polea", "Intermedio", false, listOf("Bíceps"), listOf()),
        Exercise("curl_en_polea_tumbado", "Curl en polea tumbado", "Lying Cable Curl", "Bíceps", "Polea", "Intermedio", false, listOf("Bíceps"), listOf()),
        Exercise("curl_inverso_con_barra", "Curl inverso con barra", "Reverse Barbell Curl", "Bíceps", "Barra", "Principiante", false, listOf("Bíceps"), listOf("Antebrazos")),
        Exercise("curl_zottman", "Curl Zottman", "Zottman Curl", "Bíceps", "Mancuernas", "Intermedio", false, listOf("Bíceps"), listOf("Antebrazos")),
        Exercise("curl_arana", "Curl araña", "Spider Curl", "Bíceps", "Barra Z", "Principiante", false, listOf("Bíceps"), listOf()),
        Exercise("curl_arrastrado", "Curl arrastrado", "Drag Curl", "Bíceps", "Barra", "Intermedio", false, listOf("Bíceps"), listOf("Antebrazos")),
        Exercise("curl_de_muneca_con_barra", "Curl de muñeca con barra", "Palms-Up Barbell Wrist Curl Over A Bench", "Bíceps", "Barra", "Principiante", false, listOf("Antebrazos"), listOf()),
        Exercise("curl_de_muneca_en_polea", "Curl de muñeca en polea", "Cable Wrist Curl", "Bíceps", "Polea", "Principiante", false, listOf("Antebrazos"), listOf()),

        /* --- Tríceps (ampliación) --- */
        Exercise("press_banca_agarre_cerrado", "Press banca agarre cerrado", "Close-Grip Barbell Bench Press", "Tríceps", "Barra", "Principiante", false, listOf("Tríceps"), listOf("Pecho", "Hombros")),
        Exercise("press_cerrado_con_mancuernas", "Press cerrado con mancuernas", "Close-Grip Dumbbell Press", "Tríceps", "Mancuernas", "Principiante", false, listOf("Tríceps"), listOf("Pecho", "Hombros")),
        Exercise("press_frances_con_barra_z", "Press francés con barra Z", "EZ-Bar Skullcrusher", "Tríceps", "Barra Z", "Principiante", false, listOf("Tríceps"), listOf("Antebrazos")),
        Exercise("press_frances_tumbado", "Press francés tumbado", "Lying Triceps Press", "Tríceps", "Barra Z", "Intermedio", false, listOf("Tríceps"), listOf()),
        Exercise("press_frances_declinado", "Press francés declinado", "Decline EZ Bar Triceps Extension", "Tríceps", "Barra", "Principiante", false, listOf("Tríceps"), listOf()),
        Exercise("extension_de_triceps_con_barra_sobre_la_cabeza", "Extensión de tríceps con barra sobre la cabeza", "Standing Overhead Barbell Triceps Extension", "Tríceps", "Barra", "Principiante", false, listOf("Tríceps"), listOf("Hombros")),
        Exercise("extension_de_triceps_sentado_con_mancuerna", "Extensión de tríceps sentado con mancuerna", "Seated Triceps Press", "Tríceps", "Mancuernas", "Principiante", false, listOf("Tríceps"), listOf()),
        Exercise("extension_de_triceps_de_pie_con_mancuerna", "Extensión de tríceps de pie con mancuerna", "Standing Dumbbell Triceps Extension", "Tríceps", "Mancuernas", "Principiante", false, listOf("Tríceps"), listOf()),
        Exercise("extension_de_triceps_con_mancuerna_a_una_mano", "Extensión de tríceps con mancuerna a una mano", "Dumbbell One-Arm Triceps Extension", "Tríceps", "Mancuernas", "Intermedio", false, listOf("Tríceps"), listOf()),
        Exercise("patada_de_triceps_con_mancuerna", "Patada de tríceps con mancuerna", "Tricep Dumbbell Kickback", "Tríceps", "Mancuernas", "Principiante", false, listOf("Tríceps"), listOf()),
        Exercise("extension_de_triceps_con_cuerda_sobre_la_cabeza", "Extensión de tríceps con cuerda sobre la cabeza", "Triceps Overhead Extension with Rope", "Tríceps", "Polea", "Principiante", false, listOf("Tríceps"), listOf()),
        Exercise("extension_de_triceps_en_polea_tumbado", "Extensión de tríceps en polea tumbado", "Cable Lying Triceps Extension", "Tríceps", "Polea", "Principiante", false, listOf("Tríceps"), listOf()),
        Exercise("extension_de_triceps_en_polea_a_una_mano", "Extensión de tríceps en polea a una mano", "Cable One Arm Tricep Extension", "Tríceps", "Polea", "Principiante", false, listOf("Tríceps"), listOf()),
        Exercise("extension_de_triceps_en_polea_de_rodillas", "Extensión de tríceps en polea de rodillas", "Kneeling Cable Triceps Extension", "Tríceps", "Polea", "Intermedio", false, listOf("Tríceps"), listOf()),
        Exercise("fondos_en_banco", "Fondos en banco", "Bench Dips", "Tríceps", "Peso corporal", "Principiante", false, listOf("Tríceps"), listOf("Pecho", "Hombros")),
        Exercise("fondos_en_paralelas", "Fondos en paralelas", "Dips - Triceps Version", "Tríceps", "Peso corporal", "Principiante", false, listOf("Tríceps"), listOf("Pecho", "Hombros")),
        Exercise("flexiones_diamante", "Flexiones diamante", "Close-Grip Push-Up off of a Dumbbell", "Tríceps", "Peso corporal", "Intermedio", false, listOf("Tríceps"), listOf("Abdominales", "Pecho", "Hombros")),
        Exercise("press_en_el_suelo_con_mancuernas", "Press en el suelo con mancuernas", "Dumbbell Floor Press", "Tríceps", "Mancuernas", "Intermedio", false, listOf("Tríceps"), listOf("Pecho", "Hombros")),
        Exercise("press_tate", "Press Tate", "Tate Press", "Tríceps", "Mancuernas", "Intermedio", false, listOf("Tríceps"), listOf("Pecho", "Hombros")),

        /* --- Pierna (ampliación) --- */
        Exercise("sentadilla_frontal_con_barra", "Sentadilla frontal con barra", "Front Barbell Squat", "Pierna", "Barra", "Avanzado", false, listOf("Cuádriceps"), listOf("Gemelos", "Glúteos", "Isquiotibiales")),
        Exercise("sentadilla_al_cajon", "Sentadilla al cajón", "Box Squat", "Pierna", "Barra", "Intermedio", false, listOf("Cuádriceps"), listOf("Aductores", "Gemelos", "Glúteos", "Isquiotibiales", "Lumbares")),
        Exercise("sentadilla_sin_peso", "Sentadilla sin peso", "Bodyweight Squat", "Pierna", "Peso corporal", "Principiante", false, listOf("Cuádriceps"), listOf("Glúteos", "Isquiotibiales")),
        Exercise("sentadilla_goblet", "Sentadilla goblet", "Goblet Squat", "Pierna", "Mancuernas", "Principiante", false, listOf("Cuádriceps"), listOf("Gemelos", "Glúteos", "Isquiotibiales", "Hombros")),
        Exercise("sentadilla_con_mancuernas", "Sentadilla con mancuernas", "Dumbbell Squat", "Pierna", "Mancuernas", "Principiante", false, listOf("Cuádriceps"), listOf("Gemelos", "Glúteos", "Isquiotibiales", "Lumbares")),
        Exercise("sentadilla_sumo_con_mancuerna", "Sentadilla sumo con mancuerna", "Plie Dumbbell Squat", "Pierna", "Mancuernas", "Principiante", false, listOf("Cuádriceps"), listOf("Abdominales", "Gemelos", "Glúteos", "Isquiotibiales")),
        Exercise("sentadilla_sobre_la_cabeza", "Sentadilla sobre la cabeza", "Overhead Squat", "Pierna", "Barra", "Avanzado", false, listOf("Cuádriceps"), listOf("Abdominales", "Gemelos", "Glúteos", "Isquiotibiales", "Lumbares", "Hombros", "Tríceps")),
        Exercise("sentadilla_zercher", "Sentadilla Zercher", "Zercher Squats", "Pierna", "Barra", "Avanzado", false, listOf("Cuádriceps"), listOf("Gemelos", "Glúteos", "Isquiotibiales")),
        Exercise("sentadilla_hack_con_barra", "Sentadilla hack con barra", "Barbell Hack Squat", "Pierna", "Barra", "Intermedio", false, listOf("Cuádriceps"), listOf("Gemelos", "Antebrazos", "Isquiotibiales")),
        Exercise("sentadilla_bulgara_con_barra", "Sentadilla búlgara con barra", "One Leg Barbell Squat", "Pierna", "Barra", "Avanzado", false, listOf("Cuádriceps"), listOf("Gemelos", "Glúteos", "Isquiotibiales")),
        Exercise("sentadilla_bulgara_con_mancuernas", "Sentadilla búlgara con mancuernas", "Split Squat with Dumbbells", "Pierna", "Mancuernas", "Principiante", false, listOf("Cuádriceps"), listOf("Glúteos", "Isquiotibiales")),
        Exercise("zancada_con_barra", "Zancada con barra", "Barbell Lunge", "Pierna", "Barra", "Intermedio", false, listOf("Cuádriceps"), listOf("Gemelos", "Glúteos", "Isquiotibiales")),
        Exercise("zancada_caminando_con_barra", "Zancada caminando con barra", "Barbell Walking Lunge", "Pierna", "Barra", "Principiante", false, listOf("Cuádriceps"), listOf("Gemelos", "Glúteos", "Isquiotibiales")),
        Exercise("zancada_con_mancuernas", "Zancada con mancuernas", "Dumbbell Lunges", "Pierna", "Mancuernas", "Principiante", false, listOf("Cuádriceps"), listOf("Gemelos", "Glúteos", "Isquiotibiales")),
        Exercise("zancada_hacia_atras_con_mancuernas", "Zancada hacia atrás con mancuernas", "Dumbbell Rear Lunge", "Pierna", "Mancuernas", "Intermedio", false, listOf("Cuádriceps"), listOf("Gemelos", "Glúteos", "Isquiotibiales")),
        Exercise("subida_al_cajon_con_mancuernas", "Subida al cajón con mancuernas", "Dumbbell Step Ups", "Pierna", "Mancuernas", "Intermedio", false, listOf("Cuádriceps"), listOf("Gemelos", "Glúteos", "Isquiotibiales")),
        Exercise("subida_al_cajon_con_barra", "Subida al cajón con barra", "Barbell Step Ups", "Pierna", "Barra", "Intermedio", false, listOf("Cuádriceps"), listOf("Gemelos", "Glúteos", "Isquiotibiales", "Cuádriceps")),
        Exercise("empuje_de_cadera_con_barra", "Empuje de cadera con barra", "Barbell Hip Thrust", "Pierna", "Barra", "Intermedio", false, listOf("Glúteos"), listOf("Gemelos", "Isquiotibiales")),
        Exercise("puente_de_gluteo_con_barra", "Puente de glúteo con barra", "Barbell Glute Bridge", "Pierna", "Barra", "Intermedio", false, listOf("Glúteos"), listOf("Gemelos", "Isquiotibiales")),
        Exercise("puente_de_gluteo", "Puente de glúteo", "Butt Lift (Bridge)", "Pierna", "Peso corporal", "Principiante", false, listOf("Glúteos"), listOf("Isquiotibiales")),
        Exercise("puente_de_gluteo_a_una_pierna", "Puente de glúteo a una pierna", "Single Leg Glute Bridge", "Pierna", "Peso corporal", "Principiante", false, listOf("Glúteos"), listOf("Isquiotibiales")),
        Exercise("patada_de_gluteo", "Patada de glúteo", "Glute Kickback", "Pierna", "Peso corporal", "Principiante", false, listOf("Glúteos"), listOf("Isquiotibiales")),
        Exercise("peso_muerto_rumano_con_barra", "Peso muerto rumano con barra", "Stiff-Legged Barbell Deadlift", "Pierna", "Barra", "Intermedio", false, listOf("Isquiotibiales"), listOf("Glúteos", "Lumbares")),
        Exercise("peso_muerto_rumano_con_mancuernas", "Peso muerto rumano con mancuernas", "Stiff-Legged Dumbbell Deadlift", "Pierna", "Mancuernas", "Principiante", false, listOf("Isquiotibiales"), listOf("Glúteos", "Lumbares")),
        Exercise("peso_muerto_sumo", "Peso muerto sumo", "Sumo Deadlift", "Pierna", "Barra", "Intermedio", false, listOf("Isquiotibiales"), listOf("Aductores", "Antebrazos", "Glúteos", "Lumbares", "Espalda media", "Cuádriceps", "Trapecios")),
        Exercise("buenos_dias_con_barra", "Buenos días con barra", "Good Morning", "Pierna", "Barra", "Intermedio", false, listOf("Isquiotibiales"), listOf("Abdominales", "Glúteos", "Lumbares")),
        Exercise("curl_nordico", "Curl nórdico", "Natural Glute Ham Raise", "Pierna", "Peso corporal", "Intermedio", false, listOf("Isquiotibiales"), listOf("Gemelos", "Glúteos", "Lumbares")),
        Exercise("elevacion_de_talones_con_barra", "Elevación de talones con barra", "Standing Barbell Calf Raise", "Pierna", "Barra", "Principiante", false, listOf("Gemelos"), listOf()),
        Exercise("elevacion_de_talones_con_mancuernas", "Elevación de talones con mancuernas", "Standing Dumbbell Calf Raise", "Pierna", "Mancuernas", "Intermedio", false, listOf("Gemelos"), listOf()),
        Exercise("elevacion_de_talones_en_multipower", "Elevación de talones en multipower", "Smith Machine Calf Raise", "Pierna", "Máquina", "Principiante", false, listOf("Gemelos"), listOf()),
        Exercise("elevacion_de_talones_en_prensa", "Elevación de talones en prensa", "Calf Press", "Pierna", "Máquina", "Principiante", false, listOf("Gemelos"), listOf()),
        Exercise("elevacion_de_puntas_en_multipower", "Elevación de puntas en multipower", "Smith Machine Reverse Calf Raises", "Pierna", "Máquina", "Principiante", false, listOf("Gemelos"), listOf()),
        Exercise("aduccion_de_cadera_en_polea", "Aducción de cadera en polea", "Cable Hip Adduction", "Pierna", "Polea", "Principiante", false, listOf("Cuádriceps"), listOf()),
        Exercise("sentadilla_con_salto", "Sentadilla con salto", "Freehand Jump Squat", "Pierna", "Peso corporal", "Intermedio", false, listOf("Cuádriceps"), listOf("Gemelos", "Glúteos", "Isquiotibiales")),
        Exercise("salto_con_rodillas_al_pecho", "Salto con rodillas al pecho", "Knee Tuck Jump", "Pierna", "Peso corporal", "Principiante", false, listOf("Isquiotibiales"), listOf("Abductores", "Aductores", "Gemelos", "Glúteos", "Cuádriceps")),
        Exercise("salto_al_cajon", "Salto al cajón", "Bench Jump", "Pierna", "Peso corporal", "Intermedio", false, listOf("Cuádriceps"), listOf("Gemelos", "Glúteos", "Isquiotibiales")),
        Exercise("salto_horizontal", "Salto horizontal", "Standing Long Jump", "Pierna", "Peso corporal", "Principiante", false, listOf("Cuádriceps"), listOf("Gemelos", "Glúteos", "Isquiotibiales")),
        Exercise("salto_lateral", "Salto lateral", "Lateral Bound", "Pierna", "Peso corporal", "Principiante", false, listOf("Aductores"), listOf("Abductores", "Gemelos", "Glúteos", "Isquiotibiales", "Cuádriceps")),
        Exercise("salto_en_zancada", "Salto en zancada", "Split Jump", "Pierna", "Peso corporal", "Principiante", false, listOf("Cuádriceps"), listOf("Gemelos", "Glúteos", "Isquiotibiales")),

        /* --- Trapecio (ampliación) --- */
        Exercise("encogimiento_de_hombros_con_barra", "Encogimiento de hombros con barra", "Barbell Shrug", "Trapecio", "Barra", "Principiante", false, listOf("Trapecios"), listOf()),
        Exercise("encogimiento_de_hombros_con_mancuernas", "Encogimiento de hombros con mancuernas", "Dumbbell Shrug", "Trapecio", "Mancuernas", "Principiante", false, listOf("Trapecios"), listOf()),
        Exercise("encogimiento_con_barra_por_detras", "Encogimiento con barra por detrás", "Barbell Shrug Behind The Back", "Trapecio", "Barra", "Principiante", false, listOf("Trapecios"), listOf("Antebrazos", "Espalda media")),
        Exercise("encogimiento_en_multipower_por_detras", "Encogimiento en multipower por detrás", "Smith Machine Behind the Back Shrug", "Trapecio", "Máquina", "Principiante", false, listOf("Trapecios"), listOf("Hombros")),
        Exercise("encogimiento_en_maquina_de_gemelos", "Encogimiento en máquina de gemelos", "Calf-Machine Shoulder Shrug", "Trapecio", "Máquina", "Principiante", false, listOf("Trapecios"), listOf()),
        Exercise("remo_al_menton_con_mancuernas", "Remo al mentón con mancuernas", "Standing Dumbbell Upright Row", "Trapecio", "Mancuernas", "Principiante", false, listOf("Trapecios"), listOf("Bíceps", "Hombros")),

        /* --- Core (ampliación) --- */
        Exercise("crunch_abdominal", "Crunch abdominal", "Crunches", "Core", "Peso corporal", "Principiante", false, listOf("Abdominales"), listOf()),
        Exercise("crunch_cruzado", "Crunch cruzado", "Cross-Body Crunch", "Core", "Peso corporal", "Principiante", false, listOf("Abdominales"), listOf()),
        Exercise("crunch_invertido", "Crunch invertido", "Reverse Crunch", "Core", "Peso corporal", "Principiante", false, listOf("Abdominales"), listOf()),
        Exercise("crunch_en_banco_declinado", "Crunch en banco declinado", "Decline Crunch", "Core", "Peso corporal", "Intermedio", false, listOf("Abdominales"), listOf()),
        Exercise("crunch_oblicuo", "Crunch oblicuo", "Oblique Crunches", "Core", "Peso corporal", "Principiante", false, listOf("Abdominales"), listOf()),
        Exercise("abdominal_completo", "Abdominal completo", "Sit-Up", "Core", "Peso corporal", "Principiante", false, listOf("Abdominales"), listOf()),
        Exercise("navaja", "Navaja", "Jackknife Sit-Up", "Core", "Peso corporal", "Principiante", false, listOf("Abdominales"), listOf()),
        Exercise("giro_ruso", "Giro ruso", "Russian Twist", "Core", "Peso corporal", "Intermedio", false, listOf("Abdominales"), listOf("Lumbares")),
        Exercise("giro_ruso_en_polea", "Giro ruso en polea", "Cable Russian Twists", "Core", "Polea", "Principiante", false, listOf("Abdominales"), listOf()),
        Exercise("crunch_en_polea_de_pie", "Crunch en polea de pie", "Standing Rope Crunch", "Core", "Polea", "Principiante", false, listOf("Abdominales"), listOf()),
        Exercise("crunch_en_polea_sentado", "Crunch en polea sentado", "Cable Seated Crunch", "Core", "Polea", "Principiante", false, listOf("Abdominales"), listOf()),
        Exercise("press_pallof_con_rotacion", "Press Pallof con rotación", "Pallof Press With Rotation", "Core", "Polea", "Principiante", false, listOf("Abdominales"), listOf("Pecho", "Hombros", "Tríceps")),
        Exercise("plancha_lateral", "Plancha lateral", "Side Bridge", "Core", "Peso corporal", "Principiante", false, listOf("Abdominales"), listOf("Hombros")),
        Exercise("elevacion_de_piernas_en_banco", "Elevación de piernas en banco", "Flat Bench Lying Leg Raise", "Core", "Peso corporal", "Principiante", false, listOf("Abdominales"), listOf()),
        Exercise("rodillas_al_pecho_tumbado", "Rodillas al pecho tumbado", "Leg Pull-In", "Core", "Peso corporal", "Principiante", false, listOf("Abdominales"), listOf()),
        Exercise("rueda_abdominal_de_rodillas", "Rueda abdominal de rodillas", "Barbell Ab Rollout - On Knees", "Core", "Barra", "Avanzado", false, listOf("Abdominales"), listOf("Lumbares", "Hombros")),
        Exercise("inclinacion_lateral_con_mancuerna", "Inclinación lateral con mancuerna", "Dumbbell Side Bend", "Core", "Mancuernas", "Principiante", false, listOf("Abdominales"), listOf()),
        Exercise("bicicleta_abdominal", "Bicicleta abdominal", "Air Bike", "Core", "Peso corporal", "Principiante", false, listOf("Abdominales"), listOf()),
        Exercise("tijeras", "Tijeras", "Scissor Kick", "Core", "Peso corporal", "Principiante", false, listOf("Abdominales"), listOf()),
        Exercise("bicho_muerto", "Bicho muerto", "Dead Bug", "Core", "Peso corporal", "Principiante", false, listOf("Abdominales"), listOf()),
        Exercise("vacio_abdominal", "Vacío abdominal", "Stomach Vacuum", "Core", "Peso corporal", "Principiante", false, listOf("Abdominales"), listOf()),

        /* --- Cardio (ampliación) --- */
        Exercise("andar_en_cinta", "Andar en cinta", "Walking, Treadmill", "Cardio", "Máquina", "Principiante", true, listOf("Cuádriceps"), listOf("Gemelos", "Glúteos", "Isquiotibiales")),
        Exercise("bicicleta_reclinada", "Bicicleta reclinada", "Recumbent Bike", "Cardio", "Máquina", "Principiante", true, listOf("Cuádriceps"), listOf("Gemelos", "Glúteos", "Isquiotibiales")),
    )

    private val byId: Map<String, Exercise> = all.associateBy { it.id }

    fun find(id: String): Exercise? = byId[id]

    fun require(id: String): Exercise =
        byId[id] ?: Exercise(id, id, id, "Otro", "Otro", "Intermedio", false, emptyList(), emptyList())

    /**
     * Sustitutos escogidos a mano para los casos en los que puntuar por músculo
     * se queda corto. Los datos de músculo del catálogo son gruesos: todo el
     * hombro figura como "Hombros", así que un face pull acabaría proponiendo
     * elevaciones laterales, que no trabajan lo mismo.
     */
    private val preferidas: Map<String, List<String>> = mapOf(
        "face_pull" to listOf(
            "aperturas_invertidas_en_maquina",
            "aperturas_posteriores_en_polea",
            "remo_al_menton_en_polea"
        ),
        "aperturas_invertidas_en_maquina" to listOf("face_pull", "aperturas_posteriores_en_polea"),
        "aperturas_posteriores_en_polea" to listOf("face_pull", "aperturas_invertidas_en_maquina"),
        "abductores_en_maquina" to listOf("patada_de_gluteo_en_polea", "pull_through_en_polea"),
        "aductores_en_maquina" to listOf("prensa_con_pies_juntos", "sentadilla_en_multipower"),
        "elevacion_lateral_en_polea" to listOf(
            "elevacion_lateral_con_mancuernas",
            "remo_al_menton_en_polea"
        ),
        "peso_muerto_rumano" to listOf("curl_femoral_tumbado", "elevacion_gluteo_femoral", "pull_through_en_polea"),
        "dominadas" to listOf("jalon_al_pecho_agarre_ancho", "jalon_agarre_supino"),
        "plancha_abdominal" to listOf("pallof_press", "crunch_en_maquina")
    )

    /** Parejas de movimientos opuestos: nunca uno como sustituto del otro. */
    private val opuestos: Set<Pair<String, String>> = setOf(
        "abductores_en_maquina" to "aductores_en_maquina",
        "aductores_en_maquina" to "abductores_en_maquina",
        "extension_de_cuadriceps" to "curl_femoral_tumbado",
        "curl_femoral_tumbado" to "extension_de_cuadriceps",
        "extension_de_cuadriceps" to "curl_femoral_sentado",
        "curl_femoral_sentado" to "extension_de_cuadriceps",
        // La elevación de puntas trabaja el tibial, justo lo contrario que los gemelos.
        "elevacion_de_gemelos_de_pie" to "elevacion_de_puntas_en_multipower",
        "elevacion_de_puntas_en_multipower" to "elevacion_de_gemelos_de_pie",
        "elevacion_de_talones_en_multipower" to "elevacion_de_puntas_en_multipower",
        "elevacion_de_puntas_en_multipower" to "elevacion_de_talones_en_multipower",
        "rotacion_externa_en_polea" to "rotacion_interna_en_polea",
        "rotacion_interna_en_polea" to "rotacion_externa_en_polea"
    )

    /**
     * Alternativas para cuando un ejercicio no se puede hacer: la máquina está
     * ocupada, no existe en el gimnasio o el movimiento molesta.
     *
     * Puntúa por músculo trabajado, no por nombre parecido: lo que importa es que
     * el sustituto haga el mismo trabajo, aunque sea con otro aparato.
     */
    fun alternativesFor(id: String, limit: Int = 6): List<Exercise> {
        val origen = find(id) ?: return emptyList()

        fun puntuar(otro: Exercise): Int {
            var p = 0
            // Mismos músculos principales: es lo que de verdad decide.
            p += otro.primaryMuscles.count { it in origen.primaryMuscles } * 5
            // Que el principal de uno sea secundario del otro también sirve.
            p += otro.primaryMuscles.count { it in origen.secondaryMuscles } * 2
            p += otro.secondaryMuscles.count { it in origen.primaryMuscles } * 2
            p += otro.secondaryMuscles.count { it in origen.secondaryMuscles }
            // Mismo material: si la máquina está ocupada, cambiar de aparato ayuda,
            // pero a igualdad de músculo se prefiere algo parecido.
            if (otro.equipment == origen.equipment) p += 1
            if (otro.level == origen.level) p += 1
            return p
        }

        val aMano = preferidas[id].orEmpty().mapNotNull { find(it) }

        val calculadas = all
            .filter {
                it.id != id &&
                    it.group == origen.group &&
                    it.isCardio == origen.isCardio &&
                    (id to it.id) !in opuestos &&
                    aMano.none { m -> m.id == it.id }
            }
            .map { it to puntuar(it) }
            .filter { it.second > 0 }
            .sortedWith(compareByDescending<Pair<Exercise, Int>> { it.second }.thenBy { it.first.name })
            .map { it.first }

        return (aMano + calculadas).take(limit)
    }
}
