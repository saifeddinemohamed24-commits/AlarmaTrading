package com.alarma58.app

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootAndTimeReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val action = intent?.action ?: return
        val supportedActions = setOf(
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_LOCKED_BOOT_COMPLETED,
            Intent.ACTION_TIME_CHANGED,
            Intent.ACTION_TIMEZONE_CHANGED,
            Intent.ACTION_MY_PACKAGE_REPLACED
        )
        if (action !in supportedActions) return

        if (AlarmScheduler.isEnabled(context)) {
            AlarmScheduler.scheduleNext(context)
        }
    }
}
