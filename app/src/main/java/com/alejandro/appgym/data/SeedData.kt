package com.alejandro.appgym.data

/**
 * Un solo plan: perder grasa y ganar músculo, con el trabajo de voleibol
 * (salto, gemelo, hombro, core rotacional) repartido dentro.
 *
 * La semana de adaptación se retiró una vez superada. Si algún día hace falta
 * otro plan, basta con añadir otro PlanGroup a la lista.
 */
object SeedData {

    /** Sube este número para que la app sustituya los planes por una versión nueva. */
    const val VERSION = 6

    data class Item(
        val exerciseId: String,
        val sets: Int = 3,
        val reps: Int = 12,
        val minutes: Int = 0,
        val rest: Int = 90,
        val note: String = ""
    )

    data class Plan(
        val key: String,
        val name: String,
        /** Día de la semana en el que toca. 1 = lunes … 7 = domingo. 0 = sin asignar. */
        val weekday: Int,
        val accent: Int,
        val items: List<Item>
    ) {
        val dayHint: String get() = when (weekday) {
            1 -> "Lunes"; 2 -> "Martes"; 3 -> "Miércoles"; 4 -> "Jueves"
            5 -> "Viernes"; 6 -> "Sábado"; 7 -> "Domingo"; else -> ""
        }
    }

    data class PlanGroup(
        val key: String,
        val name: String,
        val subtitle: String,
        val description: String,
        val accent: Int,
        val days: List<Plan>
    )

    /** Nombres usados por versiones anteriores, para poder retirarlos. */
    val obsoleteNames: List<String> = listOf(
        "Empuje", "Tirón", "Pierna y salto", "Full body + cardio", "Cardio y core",
        "Adaptación A", "Adaptación B",
        "Torso A · empuje", "Pierna A", "Torso B · tirón", "Pierna B + core",
        "Voley · Salto y pierna", "Voley · Hombro y core",
        "Inicio · Día A", "Inicio · Día B"
    )

    val groups: List<PlanGroup> = listOf(

        PlanGroup(
            key = "recomp",
            name = "Grasa y músculo",
            subtitle = "4 días por semana",
            description = "Torso y pierna en cuatro días, con el trabajo de voleibol repartido " +
                "dentro: salto y gemelo en las piernas, hombro sano en los tirones, y core " +
                "rotacional. Fuerza primero y cardio al final.",
            accent = 0,
            days = listOf(
                Plan("recomp", "Torso A · empuje", 1, 1, listOf(
                    Item("press_de_pecho_en_maquina", 4, 8, rest = 150,
                        note = "El principal. Cuando saques 4x10 limpias, sube peso."),
                    Item("jalon_al_pecho_agarre_ancho", 4, 10, rest = 120),
                    Item("press_de_hombro_en_maquina", 3, 10, rest = 120,
                        note = "Base del remate."),
                    Item("remo_en_polea_sentado", 3, 12, rest = 90),
                    Item("elevacion_lateral_en_polea", 3, 15, rest = 60,
                        note = "Poco peso, mucho control."),
                    Item("extension_de_triceps_con_cuerda", 3, 12, rest = 60),
                    Item("curl_de_biceps_en_polea", 3, 12, rest = 60),
                    Item("remo_ergometro", 1, 0, minutes = 12, rest = 0,
                        note = "HIIT: 40 s fuerte / 40 s suave. Aquí sí toca meter caña.")
                )),
                Plan("recomp", "Pierna A · salto", 2, 6, listOf(
                    Item("prensa_de_piernas", 4, 10, rest = 150,
                        note = "Baja controlado 3 segundos y sube rápido: la velocidad al subir es lo que mantiene el salto."),
                    Item("curl_femoral_tumbado", 4, 12, rest = 90,
                        note = "Isquios fuertes = rodilla protegida al aterrizar."),
                    Item("extension_de_cuadriceps", 3, 15, rest = 60),
                    Item("elevacion_de_gemelos_de_pie", 4, 15, rest = 45,
                        note = "Gemelo = altura de salto. Rango completo."),
                    Item("abductores_en_maquina", 3, 15, rest = 45,
                        note = "Estabilidad lateral de rodilla, para los desplazamientos."),
                    Item("crunch_en_maquina", 3, 15, rest = 45),
                    Item("bicicleta_estatica", 1, 0, minutes = 12, rest = 0,
                        note = "Suave de verdad: entre 110 y 135 ppm.")
                )),
                Plan("recomp", "Torso B · tirón", 4, 2, listOf(
                    Item("remo_iso_lateral_en_maquina", 4, 10, rest = 150,
                        note = "Tira con el codo hacia atrás, no con la mano."),
                    Item("press_inclinado_con_mancuernas", 4, 10, rest = 120),
                    Item("jalon_agarre_supino", 3, 12, rest = 90),
                    Item("face_pull", 4, 15, rest = 60,
                        note = "El seguro del hombro. No lo saltes si piensas volver a rematar."),
                    Item("aperturas_invertidas_en_maquina", 3, 15, rest = 60),
                    Item("curl_martillo_en_polea", 3, 12, rest = 60),
                    Item("extension_sobre_la_cabeza_con_cuerda", 3, 12, rest = 60),
                    Item("escaladora", 1, 0, minutes = 12, rest = 0)
                )),
                Plan("recomp", "Pierna B · core", 5, 7, listOf(
                    Item("peso_muerto_rumano", 4, 10, rest = 150,
                        note = "Cadera atrás, espalda recta. Si no lo dominas, usa curl femoral."),
                    Item("sentadilla_en_multipower", 4, 10, rest = 150,
                        note = "Alternativa: hack squat o prensa con pies altos."),
                    Item("curl_femoral_sentado", 3, 12, rest = 90),
                    Item("aductores_en_maquina", 3, 15, rest = 45),
                    Item("elevacion_de_gemelos_sentado", 4, 15, rest = 45),
                    Item("lenador_en_polea", 3, 12, rest = 45,
                        note = "Rotación con potencia, como el gesto del saque."),
                    Item("pallof_press", 3, 12, rest = 45,
                        note = "Antirrotación: el core que de verdad usas al golpear."),
                    Item("eliptica", 1, 0, minutes = 25, rest = 0,
                        note = "Zona 2: podrías mantener una conversación.")
                ))
            )
        )
    )

    /** Todos los días de todos los planes, en orden. */
    val plans: List<Plan> get() = groups.flatMap { it.days }

    fun groupOf(key: String): PlanGroup? = groups.firstOrNull { it.key == key }

    /**
     * Qué rutina toca ese día de la semana dentro del plan activo.
     * Devuelve null en los días de descanso.
     */
    fun dayFor(planKey: String, weekday: Int): Plan? =
        groupOf(planKey)?.days?.firstOrNull { it.weekday == weekday }

    /** El plan con el que arranca la app. */
    const val DEFAULT_KEY = "recomp"
}
