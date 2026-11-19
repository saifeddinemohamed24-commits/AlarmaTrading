package com.alarma58.app

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.RadioGroup
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var statusText: TextView
    private lateinit var nextAlarmText: TextView
    private lateinit var toneRadioGroup: RadioGroup
    private lateinit var toggleButton: Button
    private lateinit var stopSoundButton: Button

    private val requestNotificationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            refreshUi()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        statusText = findViewById(R.id.statusText)
        nextAlarmText = findViewById(R.id.nextAlarmText)
        toneRadioGroup = findViewById(R.id.toneRadioGroup)
        toggleButton = findViewById(R.id.toggleButton)
        stopSoundButton = findViewById(R.id.stopSoundButton)

        when (AlarmScheduler.getSelectedTone(this)) {
            AlarmScheduler.TONE_MY_DARLING -> toneRadioGroup.check(R.id.toneMyDarling)
            AlarmScheduler.TONE_PROTEGEONS -> toneRadioGroup.check(R.id.toneProtegeons)
            else -> toneRadioGroup.check(R.id.toneDefault)
        }

        toneRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            val toneKey = when (checkedId) {
                R.id.toneMyDarling -> AlarmScheduler.TONE_MY_DARLING
                R.id.toneProtegeons -> AlarmScheduler.TONE_PROTEGEONS
                else -> AlarmScheduler.TONE_DEFAULT
            }
            AlarmScheduler.setSelectedTone(this, toneKey)
        }

        requestNotificationsIfNeeded()

        toggleButton.setOnClickListener {
            if (AlarmScheduler.isEnabled(this)) {
                AlarmScheduler.cancel(this)
                stopAlarmService()
            } else {
                if (!hasNotificationPermission()) {
                    requestNotificationsIfNeeded()
                }
                if (!AlarmScheduler.canScheduleExactAlarms(this)) {
                    openExactAlarmSettings()
                } else {
                    AlarmScheduler.enableAndScheduleNext(this)
                }
            }
            refreshUi()
        }

        stopSoundButton.setOnClickListener {
            stopAlarmService()
        }
    }

    override fun onResume() {
        super.onResume()
        refreshUi()
    }

    private fun refreshUi() {
        val enabled = AlarmScheduler.isEnabled(this)
        val exactAllowed = AlarmScheduler.canScheduleExactAlarms(this)
        val notificationsAllowed = hasNotificationPermission()

        toggleButton.text = if (enabled) {
            getString(R.string.action_disable_alarm)
        } else {
            getString(R.string.action_enable_alarm)
        }

        statusText.text = when {
            !notificationsAllowed -> getString(R.string.status_need_notifications)
            !exactAllowed -> getString(R.string.status_need_exact_alarm)
            enabled -> getString(R.string.status_active)
            else -> getString(R.string.status_inactive)
        }

        val nextMillis = AlarmScheduler.getNextTriggerMillis(this)
        nextAlarmText.text = if (enabled && nextMillis > 0L) {
            val fmt = SimpleDateFormat("HH:mm · dd MMM yyyy", Locale.getDefault())
            getString(R.string.next_alarm_format, fmt.format(Date(nextMillis)))
        } else {
            getString(R.string.next_alarm_none)
        }
    }

    private fun hasNotificationPermission(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return true
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestNotificationsIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !hasNotificationPermission()) {
            requestNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    private fun openExactAlarmSettings() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                data = Uri.parse("package:$packageName")
            }
            startActivity(intent)
        }
    }

    private fun stopAlarmService() {
        val intent = Intent(this, AlarmService::class.java).apply {
            action = AlarmService.ACTION_STOP_ALARM
        }
        startService(intent)
    }
}
