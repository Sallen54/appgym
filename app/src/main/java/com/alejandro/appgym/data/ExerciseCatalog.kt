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
        "curl_femoral_sentado" to "extension_de_cuadriceps"
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
