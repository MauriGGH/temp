package com.personal.habitos.sistema

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.provider.CalendarContract
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

/**
 * Escribe en el calendario del teléfono. Como tu cuenta de Google ya está
 * sincronizada ahí, los eventos aparecen también en Google Calendar,
 * sin OAuth ni configuración en la nube.
 */
object Calendario {

    private fun aMillis(fecha: LocalDateTime): Long =
        fecha.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

    /** Abre la app de calendario con el evento prellenado (no necesita permisos). */
    fun proponerEvento(context: Context, titulo: String, dia: LocalDate, hora: Int, minutos: Int) {
        val inicio = dia.atTime(hora.coerceIn(0, 23), 0)
        val intent = Intent(Intent.ACTION_INSERT)
            .setData(CalendarContract.Events.CONTENT_URI)
            .putExtra(CalendarContract.Events.TITLE, titulo)
            .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, aMillis(inicio))
            .putExtra(
                CalendarContract.EXTRA_EVENT_END_TIME,
                aMillis(inicio.plusMinutes(minutos.toLong()))
            )
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    /** Inserta directo en el calendario predeterminado. Requiere permiso WRITE_CALENDAR. */
    fun insertarEvento(
        context: Context,
        titulo: String,
        dia: LocalDate,
        hora: Int,
        minutos: Int
    ): Boolean = try {
        val idCalendario = calendarioPredeterminado(context)
        if (idCalendario == null) false else {
            val inicio = dia.atTime(hora.coerceIn(0, 23), 0)
            val valores = ContentValues().apply {
                put(CalendarContract.Events.CALENDAR_ID, idCalendario)
                put(CalendarContract.Events.TITLE, titulo)
                put(CalendarContract.Events.DTSTART, aMillis(inicio))
                put(CalendarContract.Events.DTEND, aMillis(inicio.plusMinutes(minutos.toLong())))
                put(CalendarContract.Events.EVENT_TIMEZONE, ZoneId.systemDefault().id)
            }
            val uri = context.contentResolver.insert(CalendarContract.Events.CONTENT_URI, valores)
            uri != null && ContentUris.parseId(uri) > 0
        }
    } catch (e: SecurityException) {
        false
    } catch (e: Exception) {
        false
    }

    private fun calendarioPredeterminado(context: Context): Long? {
        val proyeccion = arrayOf(
            CalendarContract.Calendars._ID,
            CalendarContract.Calendars.IS_PRIMARY
        )
        context.contentResolver.query(
            CalendarContract.Calendars.CONTENT_URI,
            proyeccion,
            null,
            null,
            null
        )?.use { cursor ->
            var primero: Long? = null
            while (cursor.moveToNext()) {
                val id = cursor.getLong(0)
                if (primero == null) primero = id
                if (cursor.getInt(1) == 1) return id
            }
            return primero
        }
        return null
    }
}
