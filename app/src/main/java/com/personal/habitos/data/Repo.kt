package com.personal.habitos.data

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDate

/**
 * Guarda todo en el teléfono, en un JSON dentro de SharedPreferences.
 * Sin cuentas, sin servidor y sin librerías extra.
 */
object Repo {

    private const val ARCHIVO = "habitos_estado"
    private const val CLAVE = "estado"

    private var prefs: android.content.SharedPreferences? = null

    var estado by mutableStateOf(Estado())
        private set

    fun iniciar(context: Context) {
        if (prefs != null) return
        val p = context.applicationContext.getSharedPreferences(ARCHIVO, Context.MODE_PRIVATE)
        prefs = p
        val texto = p.getString(CLAVE, null)
        estado = if (texto.isNullOrBlank()) estadoInicial() else leer(JSONObject(texto))
        guardar()
    }

    private fun actualizar(bloque: (Estado) -> Estado) {
        estado = bloque(estado)
        guardar()
    }

    fun guardar() {
        prefs?.edit()?.putString(CLAVE, escribir(estado).toString())?.apply()
    }

    fun exportar(): String = escribir(estado).toString(2)

    fun importar(texto: String): Boolean = try {
        estado = leer(JSONObject(texto))
        guardar()
        true
    } catch (e: Exception) {
        false
    }

    fun reiniciar() = actualizar { estadoInicial() }

    // --- Hábitos ---

    fun marcarHabito(habito: Habito, fecha: LocalDate = LocalDate.now()) = actualizar { e ->
        val dia = fecha.toEpochDay()
        val ya = e.marcas.any { it.habitoId == habito.id && it.fecha == dia }
        if (ya) {
            e.copy(marcas = e.marcas.filterNot { it.habitoId == habito.id && it.fecha == dia })
        } else {
            e.copy(
                marcas = e.marcas + Marca(habito.id, dia),
                puntos = e.puntos + Puntos.HABITO,
                retos = avanzarRetos(e.retos, "hábito")
            )
        }
    }

    fun hechoHoy(habito: Habito, fecha: LocalDate = LocalDate.now()): Boolean =
        estado.marcas.any { it.habitoId == habito.id && it.fecha == fecha.toEpochDay() }

    fun agregarHabito(habito: Habito, activar: Boolean) = actualizar { e ->
        if (activar && e.habitos.size < 3) {
            e.copy(habitos = e.habitos + habito.copy(activo = true))
        } else {
            e.copy(enEspera = e.enEspera + habito.copy(activo = false))
        }
    }

    fun activarDesdeEspera(habito: Habito) = actualizar { e ->
        e.copy(
            enEspera = e.enEspera.filterNot { it.id == habito.id },
            habitos = e.habitos + habito.copy(activo = true)
        )
    }

    fun borrarHabito(habito: Habito) = actualizar { e ->
        e.copy(
            habitos = e.habitos.filterNot { it.id == habito.id },
            enEspera = e.enEspera.filterNot { it.id == habito.id }
        )
    }

    // --- Entreno ---

    fun guardarSesion(registro: RegistroSesion) = actualizar { e ->
        e.copy(
            sesiones = e.sesiones + registro,
            puntos = e.puntos + Puntos.ENTRENO,
            retos = avanzarRetos(e.retos, "entreno")
        )
    }

    // --- Finanzas ---

    fun agregarMovimiento(movimiento: Movimiento) = actualizar { e ->
        e.copy(movimientos = e.movimientos + movimiento)
    }

    fun borrarMovimiento(id: String) = actualizar { e ->
        e.copy(movimientos = e.movimientos.filterNot { it.id == id })
    }

    // --- Agenda ---

    fun agregarBloque(bloque: Bloque) = actualizar { e -> e.copy(bloques = e.bloques + bloque) }

    fun borrarBloque(id: String) = actualizar { e ->
        e.copy(bloques = e.bloques.filterNot { it.id == id })
    }

    // --- Notas ---

    fun agregarNota(nota: Nota) = actualizar { e -> e.copy(notas = e.notas + nota) }

    fun borrarNota(id: String) = actualizar { e -> e.copy(notas = e.notas.filterNot { it.id == id }) }

    // --- Estudio ---

    fun agregarTema(tema: Tema) = actualizar { e -> e.copy(temas = e.temas + tema) }

    fun borrarTema(id: String) = actualizar { e -> e.copy(temas = e.temas.filterNot { it.id == id }) }

    fun calificarTema(tema: Tema, calidad: Int) = actualizar { e ->
        val nuevo = repasarTema(tema, calidad)
        e.copy(
            temas = e.temas.map { if (it.id == tema.id) nuevo else it },
            puntos = e.puntos + Puntos.REPASO,
            retos = avanzarRetos(e.retos, "repaso")
        )
    }

    // --- Retos y recompensas ---

    private fun avanzarRetos(retos: List<Reto>, tipo: String): List<Reto> = retos.map { reto ->
        val aplica = when (tipo) {
            "hábito" -> reto.titulo.contains("pantalla", true) || reto.titulo.contains("hábito", true)
            "entreno" -> reto.titulo.contains("sesion", true) || reto.titulo.contains("sesión", true)
            else -> reto.titulo.contains("repas", true)
        }
        if (!aplica || reto.completado) reto
        else {
            val progreso = reto.progreso + 1
            reto.copy(progreso = progreso, completado = progreso >= reto.meta)
        }
    }

    fun agregarReto(reto: Reto) = actualizar { e -> e.copy(retos = e.retos + reto) }

    fun borrarReto(id: String) = actualizar { e -> e.copy(retos = e.retos.filterNot { it.id == id }) }

    fun agregarRecompensa(recompensa: Recompensa) = actualizar { e ->
        e.copy(recompensas = e.recompensas + recompensa)
    }

    fun alternarRecompensa(id: String) = actualizar { e ->
        e.copy(recompensas = e.recompensas.map { if (it.id == id) it.copy(lograda = !it.lograda) else it })
    }

    // --- Revisión semanal ---

    fun guardarRevision(revision: Revision) = actualizar { e ->
        e.copy(revisiones = e.revisiones.filterNot { it.semana == revision.semana } + revision)
    }

    fun revisionDe(semana: Long): Revision? = estado.revisiones.firstOrNull { it.semana == semana }

    // --- Ajustes ---

    fun cambiarAcento(indice: Int) = actualizar { e -> e.copy(acento = indice) }

    fun cambiarModoTema(modo: Int) = actualizar { e -> e.copy(modoTema = modo) }

    // --- JSON ---

    private fun escribir(e: Estado): JSONObject {
        fun arr(lista: List<JSONObject>) = JSONArray().also { a -> lista.forEach { a.put(it) } }

        fun habito(h: Habito) = JSONObject()
            .put("id", h.id).put("nombre", h.nombre).put("ancla", h.ancla)
            .put("dias", JSONArray(h.dias.toList())).put("activo", h.activo)

        return JSONObject()
            .put("habitos", arr(e.habitos.map { habito(it) }))
            .put("enEspera", arr(e.enEspera.map { habito(it) }))
            .put("marcas", arr(e.marcas.map {
                JSONObject().put("habitoId", it.habitoId).put("fecha", it.fecha)
            }))
            .put("plantillas", arr(e.plantillas.map { p ->
                JSONObject().put("nombre", p.nombre).put("ejercicios", arr(p.ejercicios.map { ej ->
                    JSONObject().put("nombre", ej.nombre).put("categoria", ej.categoria)
                        .put("extra", ej.extra)
                }))
            }))
            .put("sesiones", arr(e.sesiones.map { s ->
                JSONObject().put("id", s.id).put("fecha", s.fecha).put("sesion", s.sesion)
                    .put("hechos", JSONArray(s.ejerciciosHechos))
                    .put("esfuerzo", JSONObject(s.esfuerzo as Map<*, *>))
                    .put("nota", s.nota)
            }))
            .put("movimientos", arr(e.movimientos.map {
                JSONObject().put("id", it.id).put("fecha", it.fecha).put("concepto", it.concepto)
                    .put("categoria", it.categoria).put("monto", it.monto).put("ingreso", it.ingreso)
            }))
            .put("bloques", arr(e.bloques.map {
                JSONObject().put("id", it.id).put("dia", it.dia).put("periodo", it.periodo)
                    .put("titulo", it.titulo).put("tipo", it.tipo)
            }))
            .put("notas", arr(e.notas.map {
                JSONObject().put("id", it.id).put("fecha", it.fecha).put("texto", it.texto)
                    .put("lugar", it.lugar)
            }))
            .put("temas", arr(e.temas.map {
                JSONObject().put("id", it.id).put("nombre", it.nombre).put("materia", it.materia)
                    .put("intervalo", it.intervalo).put("facilidad", it.facilidad.toDouble())
                    .put("proximo", it.proximo).put("repasos", it.repasos)
            }))
            .put("retos", arr(e.retos.map {
                JSONObject().put("id", it.id).put("titulo", it.titulo).put("meta", it.meta)
                    .put("progreso", it.progreso).put("completado", it.completado)
            }))
            .put("recompensas", arr(e.recompensas.map {
                JSONObject().put("id", it.id).put("titulo", it.titulo)
                    .put("condicion", it.condicion).put("lograda", it.lograda)
            }))
            .put("revisiones", arr(e.revisiones.map {
                JSONObject().put("semana", it.semana)
                    .put("automatismo", JSONObject(it.automatismo as Map<*, *>))
                    .put("reflexion", it.reflexion)
            }))
            .put("puntos", e.puntos)
            .put("acento", e.acento)
            .put("modoTema", e.modoTema)
            .put("iniciado", e.iniciado)
    }

    private fun <T> JSONArray.mapear(bloque: (JSONObject) -> T): List<T> =
        (0 until length()).map { bloque(getJSONObject(it)) }

    private fun JSONArray.textos(): List<String> = (0 until length()).map { getString(it) }

    private fun JSONArray.enteros(): List<Int> = (0 until length()).map { getInt(it) }

    private fun JSONObject.mapaTexto(): Map<String, String> =
        keys().asSequence().associateWith { getString(it) }

    private fun JSONObject.mapaEntero(): Map<String, Int> =
        keys().asSequence().associateWith { getInt(it) }

    private fun leer(o: JSONObject): Estado {
        fun habito(j: JSONObject) = Habito(
            id = j.optString("id", nuevoId()),
            nombre = j.optString("nombre"),
            ancla = j.optString("ancla"),
            dias = j.optJSONArray("dias")?.enteros()?.toSet() ?: setOf(1, 2, 3, 4, 5, 6, 7),
            activo = j.optBoolean("activo", true)
        )

        return Estado(
            habitos = o.optJSONArray("habitos")?.mapear { habito(it) } ?: emptyList(),
            enEspera = o.optJSONArray("enEspera")?.mapear { habito(it) } ?: emptyList(),
            marcas = o.optJSONArray("marcas")?.mapear {
                Marca(it.optString("habitoId"), it.optLong("fecha"))
            } ?: emptyList(),
            plantillas = o.optJSONArray("plantillas")?.mapear { p ->
                Plantilla(
                    p.optString("nombre"),
                    p.optJSONArray("ejercicios")?.mapear { ej ->
                        Ejercicio(
                            ej.optString("nombre"),
                            ej.optString("categoria"),
                            ej.optBoolean("extra")
                        )
                    } ?: emptyList()
                )
            } ?: plantillasPorDefecto(),
            sesiones = o.optJSONArray("sesiones")?.mapear {
                RegistroSesion(
                    id = it.optString("id", nuevoId()),
                    fecha = it.optLong("fecha"),
                    sesion = it.optString("sesion"),
                    ejerciciosHechos = it.optJSONArray("hechos")?.textos() ?: emptyList(),
                    esfuerzo = it.optJSONObject("esfuerzo")?.mapaTexto() ?: emptyMap(),
                    nota = it.optString("nota")
                )
            } ?: emptyList(),
            movimientos = o.optJSONArray("movimientos")?.mapear {
                Movimiento(
                    id = it.optString("id", nuevoId()),
                    fecha = it.optLong("fecha"),
                    concepto = it.optString("concepto"),
                    categoria = it.optString("categoria"),
                    monto = it.optDouble("monto", 0.0),
                    ingreso = it.optBoolean("ingreso")
                )
            } ?: emptyList(),
            bloques = o.optJSONArray("bloques")?.mapear {
                Bloque(
                    id = it.optString("id", nuevoId()),
                    dia = it.optInt("dia", 1),
                    periodo = it.optString("periodo", "Mañana"),
                    titulo = it.optString("titulo"),
                    tipo = it.optString("tipo", "otro")
                )
            } ?: emptyList(),
            notas = o.optJSONArray("notas")?.mapear {
                Nota(
                    id = it.optString("id", nuevoId()),
                    fecha = it.optLong("fecha"),
                    texto = it.optString("texto"),
                    lugar = it.optString("lugar")
                )
            } ?: emptyList(),
            temas = o.optJSONArray("temas")?.mapear {
                Tema(
                    id = it.optString("id", nuevoId()),
                    nombre = it.optString("nombre"),
                    materia = it.optString("materia"),
                    intervalo = it.optInt("intervalo"),
                    facilidad = it.optDouble("facilidad", 2.2).toFloat(),
                    proximo = it.optLong("proximo", LocalDate.now().toEpochDay()),
                    repasos = it.optInt("repasos")
                )
            } ?: emptyList(),
            retos = o.optJSONArray("retos")?.mapear {
                Reto(
                    id = it.optString("id", nuevoId()),
                    titulo = it.optString("titulo"),
                    meta = it.optInt("meta", 1),
                    progreso = it.optInt("progreso"),
                    completado = it.optBoolean("completado")
                )
            } ?: emptyList(),
            recompensas = o.optJSONArray("recompensas")?.mapear {
                Recompensa(
                    id = it.optString("id", nuevoId()),
                    titulo = it.optString("titulo"),
                    condicion = it.optString("condicion"),
                    lograda = it.optBoolean("lograda")
                )
            } ?: emptyList(),
            revisiones = o.optJSONArray("revisiones")?.mapear {
                Revision(
                    semana = it.optLong("semana"),
                    automatismo = it.optJSONObject("automatismo")?.mapaEntero() ?: emptyMap(),
                    reflexion = it.optString("reflexion")
                )
            } ?: emptyList(),
            puntos = o.optInt("puntos"),
            acento = o.optInt("acento"),
            modoTema = o.optInt("modoTema"),
            iniciado = o.optBoolean("iniciado", true)
        )
    }
}

fun Repo.marcasDe(habito: Habito): List<Marca> = estado.marcas.filter { it.habitoId == habito.id }

fun Repo.hechoEn(habito: Habito, diaSemana: Int): Boolean {
    val hoy = LocalDate.now()
    val inicio = inicioDeSemana(hoy)
    val dia = inicio.plusDays((diaSemana - 1).toLong())
    return estado.marcas.any { it.habitoId == habito.id && it.fecha == dia.toEpochDay() }
}
