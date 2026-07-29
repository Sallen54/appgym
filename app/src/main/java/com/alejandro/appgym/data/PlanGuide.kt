package com.alejandro.appgym.data

/**
 * Los textos del plan, para mostrarlos dentro de la app.
 * Se guardan aquí (y no en un documento aparte) porque donde hacen falta es en el gimnasio.
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

    /**
     * No hay fases automáticas. Alejandro marca qué plan está siguiendo, porque
     * adivinarlo a partir del historial daba números que no cuadraban con la realidad.
     */

    val secciones: List<Seccion> = listOf(

        Seccion(
            id = "recomp",
            titulo = "Cómo está montado",
            resumen = "Torso y pierna · 4 días",
            destacado = "Fuerza primero, cardio al final. Si haces cardio antes llegas cansado a " +
                "lo que de verdad construye músculo.",
            bloques = listOf(
                Bloque(
                    "Cómo repartir la semana",
                    "Lunes: Torso A · empuje\nMartes: Pierna A · salto\n" +
                        "Jueves: Torso B · tirón\nViernes: Pierna B · core\n\n" +
                        "Si solo puedes tres días, haz Torso A, Pierna A y Torso B, y la semana " +
                        "siguiente entra Pierna B. Vas rotando.\n\n" +
                        "Si un día no puedes, no pasa nada: retomas donde lo dejaste sin recuperar " +
                        "sesiones perdidas."
                ),
                Bloque(
                    "El voleibol está dentro",
                    "No hay un plan de voley aparte: lo que te interesa de él ya está repartido " +
                        "en estos cuatro días. El salto y el gemelo en Pierna A, el hombro sano " +
                        "(face pull) en Torso B, y la rotación de core en Pierna B.\n\n" +
                        "Así no tienes que decidir cada día qué plan tocas."
                ),
                Bloque(
                    "Cuánto apretar",
                    "Deja 1 o 2 repeticiones en reserva en cada serie. No hace falta llegar al " +
                        "fallo para crecer, y llegando al fallo te recuperas mucho peor."
                ),
                Bloque(
                    "Si vuelven las agujetas",
                    "Al subir de volumen es normal notarlas otra vez unos días. No son " +
                        "peligrosas: ayuda moverse suave, dormir y comer proteína.\n\n" +
                        "Puedes entrenar con agujetas leves. Si el músculo duele al tocarlo o " +
                        "has perdido rango de movimiento, deja pasar un día más."
                )
            )
        ),

        Seccion(
            id = "progresion",
            titulo = "Cómo subir de peso",
            resumen = "Doble progresión",
            destacado = "No cambies el peso a ojo. La app ya te guarda lo que hiciste la última vez.",
            bloques = listOf(
                Bloque(
                    "Los cuatro pasos",
                    "1. Empiezas en el número bajo del rango. Si pone 8-10, haces 8.\n\n" +
                        "2. Cada semana intentas añadir una repetición en cada serie.\n\n" +
                        "3. Cuando completas todas las series en el número alto (4×10), subes el peso.\n\n" +
                        "4. Al subir peso volverás a 8 repeticiones. Es lo esperado, no es retroceder."
                ),
                Bloque(
                    "Cuánto subir",
                    "2,5 kg en tren superior y 5 kg en pierna. Si el gimnasio solo tiene placas de " +
                        "5 en 5, sube igualmente y quédate más semanas en el rango bajo."
                )
            )
        ),

        Seccion(
            id = "comida",
            titulo = "Comida y descanso",
            resumen = "Lo que pasa fuera del gimnasio",
            destacado = "El entrenamiento da la señal. La comida y el sueño son los que construyen. " +
                "Sin esta parte el plan rinde la mitad.",
            bloques = listOf(
                Bloque(
                    "Proteína: lo único que conviene vigilar",
                    "Para tus 75 kg, entre 110 y 150 gramos al día. Repartidos, no de golpe: unos " +
                        "30-40 g en cada comida principal.\n\n" +
                        "Referencias: pechuga de pollo (150 g) ≈ 35 g · lata de atún ≈ 25 g · " +
                        "3 huevos ≈ 19 g · yogur griego (200 g) ≈ 18 g · lentejas cocidas (200 g) " +
                        "≈ 18 g · requesón (100 g) ≈ 12 g."
                ),
                Bloque(
                    "El déficit: moderado",
                    "Para perder grasa hay que comer algo menos de lo que gastas, pero poco. Un " +
                        "recorte grande te hace perder músculo justo cuando intentas ganarlo, y " +
                        "además no se aguanta.\n\n" +
                        "Un ritmo bueno son 300-500 gramos de peso a la semana. Si bajas más de un " +
                        "kilo por semana de forma sostenida, come algo más: perder rápido no es " +
                        "ganar tiempo, es perder músculo."
                ),
                Bloque(
                    "Sin contar calorías",
                    "Proteína en todas las comidas, verdura en el plato, menos bebidas azucaradas y " +
                        "picoteo, y no repetir plato por inercia. Con eso suele bastar."
                ),
                Bloque(
                    "Sueño y actividad",
                    "Dormir 7-9 horas: dormir poco sube el hambre y baja la recuperación. Es la " +
                        "palanca más infravalorada de todas.\n\n" +
                        "8.000-10.000 pasos al día queman más grasa a lo largo del mes que cualquier " +
                        "sesión de cardio suelta.\n\n" +
                        "El alcohol interfiere con la recuperación. No hay que eliminarlo, pero " +
                        "cuanto menos, mejor van las cosas."
                )
            )
        ),

        Seccion(
            id = "esperar",
            titulo = "Qué esperar",
            resumen = "Mes a mes",
            destacado = "Tu peso oscila uno o dos kilos entre días por agua y comida. Pésate una vez " +
                "por semana y mira la línea del mes, no el número de hoy.",
            bloques = listOf(
                Bloque(
                    "Tu situación juega a favor",
                    "Perder grasa y ganar músculo a la vez es difícil para alguien ya entrenado, " +
                        "pero tú vienes de parar tras años de deporte. En ese punto el cuerpo " +
                        "responde muy bien a las dos cosas al mismo tiempo. Es una ventana de varios " +
                        "meses: aprovéchala siendo constante, no matándote."
                ),
                Bloque(
                    "El calendario realista",
                    "Semanas 1-3: coordinación y confianza. Casi todo lo que ganas es el sistema " +
                        "nervioso aprendiendo el movimiento. La báscula apenas se mueve.\n\n" +
                        "Mes 2: subes peso en casi todos los ejercicios cada semana. Aquí se nota " +
                        "mucho el progreso.\n\n" +
                        "Mes 3-4: empiezan a verse cambios en el espejo y en cómo te queda la ropa. " +
                        "La báscula puede moverse poco aunque estés mejorando: es músculo entrando " +
                        "mientras sale grasa.\n\n" +
                        "Mes 5-6: la progresión se ralentiza. Es normal, y es cuando toca revisar " +
                        "el plan."
                ),
                Bloque(
                    "Un apunte honesto",
                    "Esto es un punto de partida razonable, no una prescripción personalizada de un " +
                        "entrenador que conozca tu historial. Si arrastras alguna lesión del voleibol " +
                        "o notas dolor articular que no sean agujetas normales, que te vea alguien en " +
                        "persona. Si un ejercicio te duele de forma aguda, para: hay alternativas " +
                        "para todo."
                )
            )
        )
    )
}
