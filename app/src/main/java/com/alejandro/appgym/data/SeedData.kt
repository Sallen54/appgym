package com.alejandro.appgym.data

/**
 * El plan que trae la app: perder grasa, tonificar y ganar algo de músculo.
 *
 * No va atado a días de la semana. Son rutinas por parejas de músculos y cada día
 * se elige la que apetece o la que toca por descanso. La fuerza va primero y el
 * cardio al final de cada rutina; además hay una rutina solo de cardio.
 */
object SeedData {

    /** Sube este número para que la app sustituya los planes por una versión nueva. */
    const val VERSION = 7

    data class Item(
        val exerciseId: String,
        val sets: Int = 3,
        /** Repeticiones objetivo: el número alto del rango (ver [Progresion]). */
        val reps: Int = 12,
        val minutes: Int = 0,
        val rest: Int = 90,
        /**
         * Peso de partida orientativo, solo para quien no tiene historial de ese
         * ejercicio. En mancuernas, el de una. Las máquinas cambian mucho de una
         * marca a otra, así que es conservador a propósito.
         */
        val kg: Double = 0.0,
        val note: String = ""
    )

    data class Plan(
        val key: String,
        val name: String,
        /** Qué se trabaja, en una línea. Se enseña en el botón de la rutina. */
        val focus: String,
        val accent: Int,
        val items: List<Item>
    )

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
        "Inicio · Día A", "Inicio · Día B",
        "Pierna A · salto", "Pierna B · core"
    )

    private const val CARDIO_SUAVE =
        "Suave de verdad: deberías poder hablar. Es grasa lo que quemas a este ritmo."

    val groups: List<PlanGroup> = listOf(

        PlanGroup(
            key = "recomp",
            name = "Grasa y tono",
            subtitle = "Elige la rutina cada día",
            description = "Perder grasa, tonificar y ganar algo de músculo. Cada día eliges " +
                "qué toca; intenta no repetir el mismo músculo dos días seguidos. Fuerza " +
                "primero y cardio al final.",
            accent = 0,
            days = listOf(

                Plan("recomp", "Pecho y bíceps", "Pecho · bíceps · cardio suave", 1, listOf(
                    Item("press_de_pecho_en_maquina", 4, 10, rest = 120, kg = 30.0,
                        note = "El principal del día. Baja en dos segundos."),
                    Item("press_inclinado_con_mancuernas", 3, 12, rest = 90, kg = 12.0),
                    Item("contractor_de_pecho_peck_deck", 3, 15, rest = 60, kg = 25.0,
                        note = "Aprieta un segundo con los brazos cerrados."),
                    Item("cruce_de_poleas", 3, 15, rest = 60, kg = 10.0),
                    Item("curl_de_biceps_en_polea", 3, 12, rest = 60, kg = 15.0),
                    Item("curl_martillo", 3, 12, rest = 60, kg = 10.0),
                    Item("andar_en_cinta", 1, 0, minutes = 15, rest = 0,
                        note = "Inclinación al 8-12 % y paso rápido. $CARDIO_SUAVE")
                )),

                Plan("recomp", "Espalda y tríceps", "Espalda · tríceps · cardio suave", 2, listOf(
                    Item("jalon_al_pecho_agarre_ancho", 4, 10, rest = 120, kg = 40.0,
                        note = "El principal del día. Lleva la barra al pecho, no a la nuca."),
                    Item("remo_en_polea_sentado", 4, 12, rest = 90, kg = 35.0,
                        note = "Tira con los codos, no con las manos."),
                    Item("remo_con_mancuerna_a_una_mano", 3, 12, rest = 60, kg = 16.0),
                    Item("face_pull", 3, 15, rest = 60, kg = 15.0,
                        note = "Cuida el hombro. Poco peso y abre bien los codos."),
                    Item("extension_de_triceps_con_cuerda", 3, 12, rest = 60, kg = 15.0),
                    Item("extension_sobre_la_cabeza_con_cuerda", 3, 12, rest = 60, kg = 12.5),
                    Item("eliptica", 1, 0, minutes = 15, rest = 0, note = CARDIO_SUAVE)
                )),

                Plan("recomp", "Pierna y glúteo", "Cuádriceps · femoral · glúteo", 6, listOf(
                    Item("prensa_de_piernas", 4, 10, rest = 120, kg = 80.0,
                        note = "El principal del día. Rodillas en la línea de los pies."),
                    Item("peso_muerto_rumano_con_mancuernas", 3, 10, rest = 120, kg = 14.0,
                        note = "Cadera atrás y espalda recta. Lo notas en la parte de atrás del muslo."),
                    Item("curl_femoral_sentado", 3, 12, rest = 90, kg = 30.0),
                    Item("extension_de_cuadriceps", 3, 12, rest = 60, kg = 30.0),
                    Item("patada_de_gluteo_en_polea", 3, 15, rest = 45, kg = 10.0),
                    Item("abductores_en_maquina", 3, 15, rest = 45, kg = 35.0),
                    Item("bicicleta_estatica", 1, 0, minutes = 12, rest = 0,
                        note = "Muy suave: las piernas ya han trabajado.")
                )),

                Plan("recomp", "Hombro y core", "Hombro · abdomen · cardio", 7, listOf(
                    Item("press_de_hombro_en_maquina", 4, 10, rest = 120, kg = 25.0,
                        note = "El principal del día."),
                    Item("elevacion_lateral_con_mancuernas", 4, 15, rest = 60, kg = 6.0,
                        note = "Lo que da la forma al hombro. Poco peso, sin balancear."),
                    Item("aperturas_invertidas_en_maquina", 3, 15, rest = 60, kg = 25.0),
                    Item("crunch_en_maquina", 3, 15, rest = 45, kg = 25.0),
                    Item("pallof_press", 3, 12, rest = 45, kg = 10.0,
                        note = "Que la polea no te gire. Es core de verdad."),
                    Item("elevacion_de_piernas_en_banco", 3, 15, rest = 45),
                    Item("escaladora", 1, 0, minutes = 12, rest = 0, note = CARDIO_SUAVE)
                )),

                Plan("recomp", "Cuerpo entero", "Para días con poco tiempo · 45 min", 3, listOf(
                    Item("prensa_de_piernas", 3, 12, rest = 90, kg = 70.0),
                    Item("press_de_pecho_en_maquina", 3, 12, rest = 90, kg = 25.0),
                    Item("jalon_al_pecho_agarre_ancho", 3, 12, rest = 90, kg = 35.0),
                    Item("press_de_hombro_en_maquina", 3, 12, rest = 60, kg = 20.0),
                    Item("curl_femoral_sentado", 3, 12, rest = 60, kg = 25.0),
                    Item("crunch_en_maquina", 3, 15, rest = 45, kg = 20.0),
                    Item("remo_ergometro", 1, 0, minutes = 10, rest = 0,
                        note = "Intervalos: 40 s fuerte y 40 s suave. Aquí sí toca apretar.")
                )),

                Plan("recomp", "Cardio", "Solo quemar grasa · 40 min", 4, listOf(
                    Item("andar_en_cinta", 1, 0, minutes = 25, rest = 0,
                        note = "Inclinación al 10 %. " + CARDIO_SUAVE),
                    Item("bicicleta_estatica", 1, 0, minutes = 15, rest = 0,
                        note = "Mismo ritmo cómodo. Si la máquina está ocupada, cámbiala.")
                ))
            )
        )
    )

    /** Todas las rutinas de todos los planes, en orden. */
    val plans: List<Plan> get() = groups.flatMap { it.days }

    fun groupOf(key: String): PlanGroup? = groups.firstOrNull { it.key == key }

    /** La rutina del plan con ese nombre, para leer su foco o sus pesos de partida. */
    fun planNamed(name: String): Plan? = plans.firstOrNull { it.name == name }

    /** El plan con el que arranca la app. */
    const val DEFAULT_KEY = "recomp"
}
