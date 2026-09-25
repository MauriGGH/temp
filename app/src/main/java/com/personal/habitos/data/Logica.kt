package com.personal.habitos.data

import java.time.LocalDate

/**
 * Fuerza del hábito: sube al cumplir y baja poco a poco al fallar,
 * pero nunca se reinicia a cero. Solo cuenta los días en que aplica.
 */
fun fuerzaHabito(habito: Habito, marcas: List<Marca>, hoy: LocalDate = LocalDate.now()): Float {
    var valor = 0f
    for (i in 29 downTo 0) {
        val dia = hoy.minusDays(i.toLong())
        if (!habito.dias.contains(dia.dayOfWeek.value)) continue
        val hecho = marcas.any { it.habitoId == habito.id && it.fecha == dia.toEpochDay() }
        valor = if (hecho) valor + (1f - valor) * 0.25f else valor * 0.92f
    }
    return valor.coerceIn(0f, 1f)
}

fun etapaFuerza(valor: Float): String = when {
    valor < 0.35f -> "En construcción"
    valor < 0.7f -> "Tomando forma"
    else -> "Sólido"
}

/** Alterna A y B según la última sesión registrada, no según el calendario. */
fun siguienteSesion(sesiones: List<RegistroSesion>): String {
    val ultima = sesiones.maxByOrNull { it.fecha }?.sesion
    return if (ultima == "A") "B" else "A"
}

/** Escalera de rangos. Los puntos solo suben. */
data class Rango(val nombre: String, val minimo: Int)

val rangos = listOf(
    Rango("Plata", 0),
    Rango("Oro", 200),
    Rango("Platino", 500),
    Rango("Diamante", 1000),
    Rango("Ascendente", 1800),
    Rango("Inmortal", 3000),
    Rango("Radiante", 5000)
)

fun rangoDe(puntos: Int): Rango = rangos.last { puntos >= it.minimo }

fun siguienteRango(puntos: Int): Rango? = rangos.firstOrNull { puntos < it.minimo }

fun nivelDe(puntos: Int): Int = 1 + puntos / 50

fun progresoRango(puntos: Int): Float {
    val actual = rangoDe(puntos)
    val siguiente = siguienteRango(puntos) ?: return 1f
    val tramo = (siguiente.minimo - actual.minimo).toFloat()
    return ((puntos - actual.minimo) / tramo).coerceIn(0f, 1f)
}

object Puntos {
    const val HABITO = 5
    const val ENTRENO = 15
    const val REPASO = 5
    const val RETO = 30
}

/**
 * Repetición espaciada, variante simplificada de SM-2.
 * calidad: 0 = poco, 1 = más o menos, 2 = bien.
 */
fun repasarTema(tema: Tema, calidad: Int, hoy: LocalDate = LocalDate.now()): Tema {
    val facilidad = (tema.facilidad + when (calidad) {
        0 -> -0.25f
        1 -> 0f
        else -> 0.1f
    }).coerceIn(1.3f, 2.8f)

    val intervalo = when (calidad) {
        0 -> 1
        1 -> maxOf(2, (tema.intervalo * 1.2f).toInt())
        else -> when (tema.repasos) {
            0 -> 3
            1 -> 8
            else -> maxOf(3, (tema.intervalo * facilidad).toInt())
        }
    }

    return tema.copy(
        facilidad = facilidad,
        intervalo = intervalo,
        proximo = hoy.plusDays(intervalo.toLong()).toEpochDay(),
        repasos = tema.repasos + 1
    )
}

fun intervaloPrevisto(tema: Tema, calidad: Int): Int = repasarTema(tema, calidad).intervalo

fun temasDeHoy(temas: List<Tema>, hoy: LocalDate = LocalDate.now()): List<Tema> =
    temas.filter { it.proximo <= hoy.toEpochDay() }.sortedBy { it.proximo }

/** Intercalado: evita dos temas seguidos de la misma materia. */
fun intercalar(temas: List<Tema>): List<Tema> {
    val restantes = temas.toMutableList()
    val salida = mutableListOf<Tema>()
    while (restantes.isNotEmpty()) {
        val indice = restantes.indexOfFirst { it.materia != salida.lastOrNull()?.materia }
        val elegido = if (indice >= 0) indice else 0
        salida.add(restantes.removeAt(elegido))
    }
    return salida
}

fun inicioDeSemana(hoy: LocalDate = LocalDate.now()): LocalDate =
    hoy.minusDays((hoy.dayOfWeek.value - 1).toLong())

fun vecesEnSemana(habitoId: String, marcas: List<Marca>, hoy: LocalDate = LocalDate.now()): Int {
    val inicio = inicioDeSemana(hoy).toEpochDay()
    return marcas.count { it.habitoId == habitoId && it.fecha >= inicio }
}

fun sesionesEnSemana(sesiones: List<RegistroSesion>, hoy: LocalDate = LocalDate.now()): Int {
    val inicio = inicioDeSemana(hoy).toEpochDay()
    return sesiones.count { it.fecha >= inicio }
}

fun balanceMes(movimientos: List<Movimiento>, hoy: LocalDate = LocalDate.now()): Triple<Double, Double, Double> {
    val inicio = hoy.withDayOfMonth(1).toEpochDay()
    val delMes = movimientos.filter { it.fecha >= inicio }
    val ingresos = delMes.filter { it.ingreso }.sumOf { it.monto }
    val gastos = delMes.filter { !it.ingreso }.sumOf { it.monto }
    return Triple(ingresos, gastos, ingresos - gastos)
}
