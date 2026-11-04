package com.example.streak

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import java.util.Calendar

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(ctx: Context, intent: Intent) {
        StreakService.instance?.startRun()
        schedule(ctx) // jadwalkan lagi untuk besok
    }

    companion object {
        fun schedule(ctx: Context) {
            val p = ctx.getSharedPreferences("cfg", Context.MODE_PRIVATE)
            val cal = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, p.getInt("hour", 8))
                set(Calendar.MINUTE, p.getInt("min", 0))
                set(Calendar.SECOND, 0)
                // jeda acak 0-10 menit supaya tidak persis sama tiap hari
                add(Calendar.MINUTE, (0..10).random())
                if (timeInMillis <= System.currentTimeMillis()) add(Calendar.DAY_OF_YEAR, 1)
            }
            val am = ctx.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val pi = PendingIntent.getBroadcast(
                ctx, 1, Intent(ctx, AlarmReceiver::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            am.setAlarmClock(AlarmManager.AlarmClockInfo(cal.timeInMillis, pi), pi)
        }
    }
}
