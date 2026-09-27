package com.personal.habitos.data

import java.util.UUID

fun nuevoId(): String = UUID.randomUUID().toString()

data class Habito(
    val id: String = nuevoId(),
    val nombre: String,
    val ancla: String = "",
    val dias: Set<Int> = setOf(1, 2, 3, 4, 5, 6, 7),
    val activo: Boolean = true
)

data class Marca(val habitoId: String, val fecha: Long)

data class Ejercicio(
    val nombre: String,
    val categoria: String,
    val extra: Boolean = false,
    /** Segundos de descanso sugeridos entre series. */
    val descanso: Int = 90
)

/** Una serie registrada: peso y repeticiones tal como los escribiste. */
data class Serie(val ejercicio: String, val peso: String, val reps: String)

data class Plantilla(val nombre: String, val ejercicios: List<Ejercicio>)

data class RegistroSesion(
    val id: String = nuevoId(),
    val fecha: Long,
    val sesion: String,
    val ejerciciosHechos: List<String> = emptyList(),
    val esfuerzo: Map<String, String> = emptyMap(),
    val series: List<Serie> = emptyList(),
    val duracion: Int = 0,
    val nota: String = ""
)

data class Movimiento(
    val id: String = nuevoId(),
    val fecha: Long,
    val concepto: String,
    val categoria: String,
    val monto: Double,
    val ingreso: Boolean
)

data class Bloque(
    val id: String = nuevoId(),
    val dia: Int,
    val periodo: String,
    val titulo: String,
    val tipo: String = "otro"
)

data class Nota(
    val id: String = nuevoId(),
    val fecha: Long,
    val texto: String,
    val lugar: String = ""
)

data class Tema(
    val id: String = nuevoId(),
    val nombre: String,
    val materia: String,
    val intervalo: Int = 0,
    val facilidad: Float = 2.2f,
    val proximo: Long,
    val repasos: Int = 0,
    val apuntes: String = ""
)

data class Reto(
    val id: String = nuevoId(),
    val titulo: String,
    val meta: Int,
    val progreso: Int = 0,
    val completado: Boolean = false,
    /** "entreno", "repaso" o "habito:<id>". Decide qué evento lo avanza. */
    val objetivo: String = "entreno"
)

data class Recompensa(
    val id: String = nuevoId(),
    val titulo: String,
    val condicion: String,
    val lograda: Boolean = false
)

data class Revision(
    val semana: Long,
    val automatismo: Map<String, Int> = emptyMap(),
    val reflexion: String = ""
)

data class Estado(
    val habitos: List<Habito> = emptyList(),
    val enEspera: List<Habito> = emptyList(),
    val marcas: List<Marca> = emptyList(),
    val plantillas: List<Plantilla> = plantillasPorDefecto(),
    val sesiones: List<RegistroSesion> = emptyList(),
    val movimientos: List<Movimiento> = emptyList(),
    val bloques: List<Bloque> = emptyList(),
    val notas: List<Nota> = emptyList(),
    val temas: List<Tema> = emptyList(),
    val retos: List<Reto> = emptyList(),
    val recompensas: List<Recompensa> = emptyList(),
    val revisiones: List<Revision> = emptyList(),
    val puntos: Int = 0,
    /** "entreno", "revision", "bloques" -> encendido */
    val recordatorios: Map<String, Boolean> = mapOf("entreno" to true, "revision" to true),
    /** misma clave -> hora del día (0-23) */
    val horas: Map<String, Int> = mapOf("entreno" to 8, "revision" to 18),
    /** categoría de gasto -> tope mensual */
    val presupuestos: Map<String, Double> = emptyMap(),
    val acento: Int = 0,
    val modoTema: Int = 0,
    val iniciado: Boolean = false
)

fun plantillasPorDefecto(): List<Plantilla> = listOf(
    Plantilla(
        "A",
        listOf(
            Ejercicio("Sentadilla goblet", "Pierna", descanso = 120),
            Ejercicio("Flexiones", "Empuje", descanso = 90),
            Ejercicio("Remo a una mano", "Jalón", descanso = 90),
            Ejercicio("Peso muerto rumano", "Pierna", descanso = 120),
            Ejercicio("Elevaciones laterales", "Hombro", extra = true, descanso = 60),
            Ejercicio("Box", "Cierre", extra = true, descanso = 60)
        )
    ),
    Plantilla(
        "B",
        listOf(
            Ejercicio("Sentadilla búlgara", "Pierna", descanso = 120),
            Ejercicio("Press militar", "Empuje", descanso = 90),
            Ejercicio("Remo inclinado", "Jalón", descanso = 90),
            Ejercicio("Peso muerto sumo", "Pierna", descanso = 120),
            Ejercicio("Curl y tríceps", "Brazo", extra = true, descanso = 60),
            Ejercicio("Box", "Cierre", extra = true, descanso = 60)
        )
    )
)

fun estadoInicial(): Estado = Estado(iniciado = false)

/** Se llama al terminar la bienvenida, con los hábitos que el usuario escribió. */
fun estadoTrasBienvenida(habitos: List<Habito>): Estado = Estado(
    habitos = habitos,
    bloques = listOf(
        Bloque(dia = 1, periodo = "Mañana", titulo = "Entrenar", tipo = "entreno"),
        Bloque(dia = 1, periodo = "Tarde", titulo = "Escuela", tipo = "escuela"),
        Bloque(dia = 1, periodo = "Noche", titulo = "Tiempo libre", tipo = "libre")
    ),
    iniciado = true
)
