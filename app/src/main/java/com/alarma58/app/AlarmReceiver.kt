package com.alarma58.app

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action != ACTION_TRIGGER_58) return

        // Reprogramar inmediatamente el siguiente minuto :58 para mantener el ciclo horario
        if (AlarmScheduler.isEnabled(context)) {
            AlarmScheduler.scheduleNext(context)
        }

        val serviceIntent = Intent(context, AlarmService::class.java).apply {
            action = AlarmService.ACTION_START_ALARM
        }
        ContextCompat.startForegroundService(context, serviceIntent)
    }

    companion object {
        const val ACTION_TRIGGER_58 = "com.alarma58.app.ACTION_TRIGGER_58"
    }
}
