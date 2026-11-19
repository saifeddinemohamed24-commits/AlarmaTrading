package com.alarma58.app

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import java.util.Calendar

object AlarmScheduler {
    private const val PREFS_NAME = "alarma58_prefs"
    private const val KEY_ENABLED = "alarm_enabled"
    private const val KEY_NEXT_TRIGGER = "next_trigger_millis"
    private const val KEY_TONE = "selected_tone"
    private const val REQUEST_CODE_ALARM = 5800

    const val TONE_DEFAULT = "default"
    const val TONE_MY_DARLING = "my_darling"
    const val TONE_PROTEGEONS = "protegeons_nos_leaders"

    fun isEnabled(context: Context): Boolean {
        return prefs(context).getBoolean(KEY_ENABLED, false)
    }

    fun getSelectedTone(context: Context): String {
        return prefs(context).getString(KEY_TONE, TONE_DEFAULT) ?: TONE_DEFAULT
    }

    fun setSelectedTone(context: Context, tone: String) {
        prefs(context).edit().putString(KEY_TONE, tone).apply()
    }

    fun getNextTriggerMillis(context: Context): Long {
        return prefs(context).getLong(KEY_NEXT_TRIGGER, 0L)
    }

    fun canScheduleExactAlarms(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return true
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        return alarmManager.canScheduleExactAlarms()
    }

    fun calculateNextMinute58(nowMillis: Long = System.currentTimeMillis()): Long {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = nowMillis
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            set(Calendar.MINUTE, 58)
        }
        if (calendar.timeInMillis <= nowMillis) {
            calendar.add(Calendar.HOUR_OF_DAY, 1)
        }
        return calendar.timeInMillis
    }

    fun enableAndScheduleNext(context: Context): Long? {
        if (!canScheduleExactAlarms(context)) {
            return null
        }
        prefs(context).edit().putBoolean(KEY_ENABLED, true).apply()
        return scheduleNext(context)
    }

    fun scheduleNext(context: Context): Long? {
        if (!isEnabled(context) || !canScheduleExactAlarms(context)) {
            return null
        }

        val triggerAtMillis = calculateNextMinute58()
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pendingIntent = buildAlarmPendingIntent(context)

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerAtMillis,
            pendingIntent
        )

        prefs(context).edit().putLong(KEY_NEXT_TRIGGER, triggerAtMillis).apply()
        return triggerAtMillis
    }

    fun cancel(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(buildAlarmPendingIntent(context))
        prefs(context).edit()
            .putBoolean(KEY_ENABLED, false)
            .remove(KEY_NEXT_TRIGGER)
            .apply()
    }

    private fun buildAlarmPendingIntent(context: Context): PendingIntent {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = AlarmReceiver.ACTION_TRIGGER_58
        }
        return PendingIntent.getBroadcast(
            context,
            REQUEST_CODE_ALARM,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
}
