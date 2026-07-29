# Prompt para que Claude analice tu informe

Abre una conversación nueva en Claude, **adjunta el `.txt`** que exporta la app
(Progreso → botón de compartir) y pega el texto de abajo.

---

## El prompt

```
Eres un preparador físico con experiencia en fuerza y recomposición corporal.
Te adjunto el informe que exporta mi app de entrenamiento. Quiero una lectura
honesta: qué va bien, qué va mal y qué cambiaría.

MI SITUACIÓN
- Hombre, 26 años, 181 cm, unos 75 kg.
- Jugué voleibol durante años. Lo dejé y estuve parado bastante tiempo.
- He vuelto al gimnasio hace poco. Entreno solo, sin entrenador.
- Objetivo: perder grasa y ganar músculo a la vez. Y no perder lo que me dio el
  voleibol: salto, agilidad y un hombro que aguante.
- Sesiones de 1 a 2 horas. Suelo poder ir 4 días por semana.
- Mi gimnasio tiene máquinas normales, poleas, multipower, mancuernas y las
  típicas de cardio. No hay nada exótico.

QUÉ CONTIENE EL INFORME
- Perfil, peso corporal e IMC.
- Resumen: periodo, sesiones, días de descanso, frecuencia media, series
  completadas, kg totales movidos, duración media de sesión.
- Peso movido por semana, con la tendencia.
- Reparto del trabajo por grupo muscular, en series y en kg, con el balance
  entre empuje y tirón.
- Evolución por ejercicio: el peso del primer día, el del último, el cambio en
  kg y en %, y la mejor marca. Los ejercicios que solo llevan una sesión salen
  aparte, sin evolución.
- Cardio: minutos, distancia, calorías y pulso medio.
- Récords personales y el historial de peso corporal.

CÓMO LEER LOS DATOS (importante)
- Los pesos los apunto yo, así que puede haber algún error de tecleo. Si ves un
  número imposible, dilo en vez de construir una conclusión encima.
- El pulso viene de los sensores de las empuñaduras de las máquinas, que suelen
  leer alto. Trátalo como orientativo.
- Si llevo pocas semanas registrando, dímelo claramente: prefiero "aún no hay
  datos suficientes para saberlo" antes que una conclusión inventada.

QUÉ QUIERO QUE ME DIGAS

1. Resumen en tres frases. ¿Voy bien o no?

2. Qué estoy haciendo bien. Concreto, con los números del informe, no
   generalidades motivacionales.

3. Qué estoy haciendo mal o descuidando. Esta es la parte que más me interesa.
   Mira sobre todo:
   - Ejercicios estancados: mismo peso durante varias sesiones.
   - Progresión demasiado rápida, que suele acabar en lesión.
   - Desequilibrios: grupos musculares con mucho volumen frente a otros
     olvidados. En especial empuje contra tirón, y cuádriceps contra isquios.
   - Frecuencia: si entreno menos de lo que creo, o si no descanso suficiente.
   - Cardio: si estoy yendo demasiado fuerte en los días que deberían ser
     suaves.
   - Peso corporal: si estoy bajando demasiado rápido y arriesgo perder músculo.

4. Los tres cambios concretos que harías esta semana. Ordenados por impacto.
   Nada de listas de diez cosas.

5. Qué vigilar en el próximo mes: dos o tres señales que me digan si voy bien.

CÓMO QUIERO LA RESPUESTA
- En español, directo y sin adornos. Si algo está mal, dilo sin rodeos.
- Apoya cada afirmación en un dato del informe. Si no hay dato, no lo afirmes.
- No me felicites por defecto. Prefiero que me digas lo que falla.
- Si algo te parece que debería verlo un profesional en persona (dolor,
  molestias articulares, algo raro en la evolución del peso), dilo claramente.
- Máximo una página. Nada de tablas enormes ni de repetirme el informe.
```

---

## Notas de uso

**Cada cuánto pasarlo.** Una vez al mes. Con dos semanas de datos no hay
tendencia que valer la pena leer, y te va a dar conclusiones flojas.

**Añade contexto si ha pasado algo.** Si has estado enfermo, de viaje, o un
ejercicio te ha empezado a molestar, dilo en el mismo mensaje. Cambia mucho la
lectura de los datos.

**Actualiza tu edad y tu peso** en el bloque de "MI SITUACIÓN" cuando cambien.

**Si quieres algo más corto**, esta versión reducida funciona bien para una
revisión rápida:

```
Te adjunto el informe de mi app de entrenamiento. Tengo 26 años, 181 cm, 75 kg,
vuelvo al gimnasio tras años parado (venía del voleibol) y busco perder grasa y
ganar músculo.

Dime en media página: qué va bien, qué estoy haciendo mal, y los dos cambios
que harías esta semana. Apóyate en los números del informe y sé directo. Si no
hay datos suficientes para alguna conclusión, dilo en vez de suponer.
```
