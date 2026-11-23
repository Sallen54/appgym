package com.alejandro.appgym.data

/**
 * Consejos cortos para el plan "Grasa y tono". Viven aquí y no en un documento
 * aparte porque donde hacen falta es en el gimnasio. Pocos y al grano: antes había
 * una guía larga con el reparto por días de la semana que ya no aplica.
 */
object PlanGuide {

    data class Bloque(val titulo: String, val cuerpo: String)

    data class Seccion(
        val id: String,
        val titulo: String,
        val resumen: String,
        val bloques: List<Bloque>,
        val destacado: String? = null
    )

    val secciones: List<Seccion> = listOf(

        Seccion(
            id = "kilos",
            titulo = "Los kilos sugeridos",
            resumen = "Cómo decide la app cuánto poner",
            destacado = "Fíate de la sugerencia, pero manda cómo te sientes: si una serie " +
                "sale fatal, baja peso sin pena.",
            bloques = listOf(
                Bloque(
                    "Rango de repeticiones",
                    "Cada ejercicio tiene un rango, por ejemplo 10-12. Mientras no hagas todas " +
                        "las series en el número alto, la app te deja el mismo peso y te pide " +
                        "una repetición más. Cuando las completas todas, te sube el peso y " +
                        "vuelves al número bajo."
                ),
                Bloque(
                    "La primera vez",
                    "Si nunca has hecho el ejercicio, el peso es solo orientativo: cada " +
                        "máquina es un mundo. Ajústalo hasta que llegues al número alto con " +
                        "dos repeticiones de margen. A partir de ahí, la app aprende de lo " +
                        "que apuntes."
                ),
                Bloque(
                    "Cuánto apretar",
                    "Deja 1 o 2 repeticiones en reserva en cada serie. No hace falta llegar " +
                        "al fallo para tonificar ni para ganar músculo, y te recuperas mejor."
                )
            )
        ),

        Seccion(
            id = "semana",
            titulo = "Cómo elegir cada día",
            resumen = "Sin días fijos",
            destacado = "No repitas el mismo músculo dos días seguidos. La app marca la " +
                "rutina que más tiempo llevas sin hacer.",
            bloques = listOf(
                Bloque(
                    "Cuántos días",
                    "Con 3 o 4 días a la semana basta. Una forma sencilla: rota las cuatro " +
                        "rutinas por parejas y, si una semana solo puedes ir dos días, haz " +
                        "Cuerpo entero las dos veces."
                ),
                Bloque(
                    "El cardio",
                    "Cada rutina acaba con 10-15 minutos de cardio suave, que es cuando más " +
                        "grasa quemas. Si te sobra un día, la rutina Cardio son 40 minutos a " +
                        "ritmo de poder hablar."
                )
            )
        ),

        Seccion(
            id = "comida",
            titulo = "Comida y descanso",
            resumen = "Lo que pasa fuera del gimnasio",
            destacado = "Para perder grasa manda la comida. El entrenamiento decide si lo " +
                "que pierdes es grasa o músculo.",
            bloques = listOf(
                Bloque(
                    "Proteína",
                    "Entre 1,6 y 2 gramos por kilo de peso al día, repartidos en las comidas. " +
                        "Es lo que protege el músculo mientras bajas grasa.\n\n" +
                        "Referencias: pechuga de pollo (150 g) ≈ 35 g · lata de atún ≈ 25 g · " +
                        "3 huevos ≈ 19 g · yogur griego (200 g) ≈ 18 g · lentejas cocidas " +
                        "(200 g) ≈ 18 g."
                ),
                Bloque(
                    "Un déficit pequeño",
                    "Come algo menos de lo que gastas, pero poco. Un buen ritmo es perder " +
                        "entre 300 y 500 gramos a la semana. Si bajas más de un kilo por " +
                        "semana durante varias semanas, come algo más: estarás perdiendo músculo."
                ),
                Bloque(
                    "Pasos y sueño",
                    "8.000-10.000 pasos al día queman más grasa a lo largo del mes que " +
                        "cualquier sesión de cardio suelta. Y dormir 7-9 horas: dormir poco " +
                        "sube el hambre y baja la recuperación."
                )
            )
        ),

        Seccion(
            id = "esperar",
            titulo = "Qué esperar",
            resumen = "Para no desanimarte",
            destacado = "El peso sube y baja uno o dos kilos de un día a otro por el agua. " +
                "Pésate una vez por semana y mira la línea del mes.",
            bloques = listOf(
                Bloque(
                    "El espejo antes que la báscula",
                    "Al ganar algo de músculo y perder grasa a la vez, la báscula puede " +
                        "moverse poco aunque estés mejorando. Fíjate en cómo te queda la ropa " +
                        "y en si los kilos de cada ejercicio suben."
                ),
                Bloque(
                    "Si algo duele",
                    "Las agujetas son normales. Un dolor agudo en una articulación no: para " +
                        "y cambia el ejercicio con «Cambiar por otro». Si se repite, que te " +
                        "vea alguien en persona."
                )
            )
        )
    )
}
