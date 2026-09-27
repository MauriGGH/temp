package com.personal.habitos.sistema

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.personal.habitos.MainActivity
import com.personal.habitos.R
import java.time.Duration
import java.time.LocalDateTime

/** Programa y muestra los recordatorios. Horarios aproximados, sin alarmas exactas. */
object Recordatorios {

    const val CANAL = "recordatorios"

    private val textos = mapOf(
        "entreno" to ("Hoy toca entrenar" to "Tu sesión está lista cuando tú lo estés."),
        "revision" to ("Revisión semanal" to "Cinco minutos para ver cómo te fue."),
        "bloques" to ("Tu siguiente bloque" to "Lo que sigue en tu agenda de hoy.")
    )

    fun crearCanal(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val canal = NotificationChannel(
                CANAL,
                "Recordatorios",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply { description = "Avisos de hábitos, entrenamiento y revisión" }
            val manager = context.getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(canal)
        }
    }

    /** Programa (o cancela) el recordatorio diario de una clave. */
    fun programar(context: Context, clave: String, activo: Boolean, hora: Int) {
        val work = WorkManager.getInstance(context.applicationContext)
        if (!activo) {
            work.cancelUniqueWork(clave)
            return
        }

        val ahora = LocalDateTime.now()
        var objetivo = ahora.withHour(hora.coerceIn(0, 23)).withMinute(0).withSecond(0)
        if (!objetivo.isAfter(ahora)) objetivo = objetivo.plusDays(1)
        val retraso = Duration.between(ahora, objetivo).toMinutes().coerceAtLeast(1)

        val peticion = PeriodicWorkRequestBuilder<TrabajoRecordatorio>(Duration.ofHours(24))
            .setInitialDelay(Duration.ofMinutes(retraso))
            .setInputData(workDataOf("clave" to clave))
            .setConstraints(Constraints.Builder().build())
            .build()

        work.enqueueUniquePeriodicWork(clave, ExistingPeriodicWorkPolicy.UPDATE, peticion)
    }

    fun mostrar(context: Context, clave: String) {
        val (titulo, cuerpo) = textos[clave] ?: return
        crearCanal(context)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendiente = PendingIntent.getActivity(
            context,
            clave.hashCode(),
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val aviso = NotificationCompat.Builder(context, CANAL)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(titulo)
            .setContentText(cuerpo)
            .setContentIntent(pendiente)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(clave.hashCode(), aviso)
        } catch (e: SecurityException) {
            // Sin permiso de notificaciones: no pasa nada.
        }
    }
}

class TrabajoRecordatorio(
    context: Context,
    params: WorkerParameters
) : Worker(context, params) {
    override fun doWork(): Result {
        val clave = inputData.getString("clave") ?: return Result.success()
        Recordatorios.mostrar(applicationContext, clave)
        return Result.success()
    }
}
