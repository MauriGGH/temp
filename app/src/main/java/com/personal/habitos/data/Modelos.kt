package com.personal.habitos.data

import java.time.LocalDate
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

data class Ejercicio(val nombre: String, val categoria: String, val extra: Boolean = false)

data class Plantilla(val nombre: String, val ejercicios: List<Ejercicio>)

data class RegistroSesion(
    val id: String = nuevoId(),
    val fecha: Long,
    val sesion: String,
    val ejerciciosHechos: List<String> = emptyList(),
    val esfuerzo: Map<String, String> = emptyMap(),
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
    val repasos: Int = 0
)

data class Reto(
    val id: String = nuevoId(),
    val titulo: String,
    val meta: Int,
    val progreso: Int = 0,
    val completado: Boolean = false
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
    val acento: Int = 0,
    val modoTema: Int = 0,
    val iniciado: Boolean = false
)

fun plantillasPorDefecto(): List<Plantilla> = listOf(
    Plantilla(
        "A",
        listOf(
            Ejercicio("Sentadilla goblet", "Pierna"),
            Ejercicio("Flexiones", "Empuje"),
            Ejercicio("Remo a una mano", "Jalón"),
            Ejercicio("Peso muerto rumano", "Pierna"),
            Ejercicio("Elevaciones laterales", "Hombro", extra = true),
            Ejercicio("Box", "Cierre", extra = true)
        )
    ),
    Plantilla(
        "B",
        listOf(
            Ejercicio("Sentadilla búlgara", "Pierna"),
            Ejercicio("Press militar", "Empuje"),
            Ejercicio("Remo inclinado", "Jalón"),
            Ejercicio("Peso muerto sumo", "Pierna"),
            Ejercicio("Curl y tríceps", "Brazo", extra = true),
            Ejercicio("Box", "Cierre", extra = true)
        )
    )
)

fun estadoInicial(): Estado {
    val hoy = LocalDate.now().toEpochDay()
    return Estado(
        habitos = listOf(
            Habito(nombre = "Comer sin pantalla", ancla = "En cada comida en casa"),
            Habito(
                nombre = "Entrenamiento",
                ancla = "Después de lavarme los dientes",
                dias = setOf(1, 3, 5)
            )
        ),
        enEspera = listOf(
            Habito(nombre = "Preparar comida para la escuela", activo = false),
            Habito(nombre = "Practicar instrumento", activo = false),
            Habito(nombre = "Clase de francés", activo = false)
        ),
        bloques = listOf(
            Bloque(dia = 1, periodo = "Mañana", titulo = "Entrenar", tipo = "entreno"),
            Bloque(dia = 1, periodo = "Mañana", titulo = "Desayuno sin pantalla", tipo = "habito"),
            Bloque(dia = 1, periodo = "Tarde", titulo = "Escuela", tipo = "escuela"),
            Bloque(dia = 1, periodo = "Noche", titulo = "Tiempo libre", tipo = "libre")
        ),
        recompensas = listOf(
            Recompensa(titulo = "Cuerdas nuevas", condicion = "Al completar 3 retos"),
            Recompensa(titulo = "Salida con amigos", condicion = "Al terminar el mes")
        ),
        retos = listOf(
            Reto(titulo = "Comer sin pantalla 4 veces esta semana", meta = 4),
            Reto(titulo = "Dos sesiones esta semana", meta = 2)
        ),
        temas = listOf(
            Tema(nombre = "Derivadas parciales", materia = "Cálculo", proximo = hoy),
            Tema(nombre = "Estructuras de datos", materia = "Programación", proximo = hoy)
        ),
        iniciado = true
    )
}
