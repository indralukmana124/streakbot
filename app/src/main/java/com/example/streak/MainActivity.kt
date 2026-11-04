package com.example.streak

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.text.InputType
import android.view.ViewGroup
import android.widget.*

class MainActivity : Activity() {
    override fun onCreate(b: Bundle?) {
        super.onCreate(b)
        val p = getSharedPreferences("cfg", MODE_PRIVATE)
        val pad = (16 * resources.displayMetrics.density).toInt()
        val root = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(pad, pad * 2, pad, pad) }

        fun field(hint: String, value: String, type: Int = InputType.TYPE_CLASS_TEXT) =
            EditText(this).apply { this.hint = hint; setText(value); inputType = type }

        val friend = field("Nama teman persis seperti di TikTok", p.getString("friend", "") ?: "")
        val msg = field("Isi pesan", p.getString("msg", "🔥") ?: "🔥")
        val hour = field("Jam (0-23)", p.getInt("hour", 8).toString(), InputType.TYPE_CLASS_NUMBER)
        val min = field("Menit (0-59)", p.getInt("min", 0).toString(), InputType.TYPE_CLASS_NUMBER)
        val status = TextView(this)

        fun btn(t: String, f: () -> Unit) = Button(this).apply { text = t; setOnClickListener { f() } }
        fun save() {
            p.edit().putString("friend", friend.text.toString().trim())
                .putString("msg", msg.text.toString())
                .putInt("hour", hour.text.toString().toIntOrNull()?.coerceIn(0, 23) ?: 8)
                .putInt("min", min.text.toString().toIntOrNull()?.coerceIn(0, 59) ?: 0).apply()
        }

        listOf(
            friend, msg, hour, min,
            btn("1. Aktifkan Accessibility Service") { startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)) },
            btn("2. Simpan & jadwalkan harian") {
                save(); AlarmReceiver.schedule(this)
                status.text = "Terjadwal. Pastikan HP tidak dalam mode hemat baterai ekstrem."
            },
            btn("Tes sekarang") {
                save()
                val s = StreakService.instance
                status.text = if (s == null) "Service belum aktif." else { s.startRun(); "Menjalankan..." }
            },
            status
        ).forEach { root.addView(it, ViewGroup.LayoutParams(-1, -2)) }
        setContentView(ScrollView(this).apply { addView(root) })
    }
}
