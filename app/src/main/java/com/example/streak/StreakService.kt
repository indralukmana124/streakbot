package com.example.streak

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

class StreakService : AccessibilityService() {
    companion object {
        var instance: StreakService? = null
        val PACKAGES = listOf("com.zhiliaoapp.musically", "com.ss.android.ugc.trill")
    }

    private val h = Handler(Looper.getMainLooper())
    private var step = 0 // 0 idle, 1 inbox, 2 pilih teman, 3 ketik, 4 kirim
    private var tries = 0

    override fun onServiceConnected() { instance = this }
    override fun onUnbind(intent: Intent?): Boolean { instance = null; return super.onUnbind(intent) }
    override fun onAccessibilityEvent(e: AccessibilityEvent?) {}
    override fun onInterrupt() {}

    fun startRun() {
        val intent = PACKAGES.firstNotNullOfOrNull { packageManager.getLaunchIntentForPackage(it) } ?: return
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        step = 1; tries = 0
        h.removeCallbacksAndMessages(null)
        startActivity(intent)
        h.postDelayed(::tick, 5000)
    }

    private fun tick() {
        if (step == 0) return
        if (++tries > 40) { step = 0; return }
        val root = rootInActiveWindow
        if (root != null && root.packageName?.toString() in PACKAGES) {
            val prefs = getSharedPreferences("cfg", MODE_PRIVATE)
            val friend = prefs.getString("friend", "") ?: ""
            val msg = prefs.getString("msg", "🔥") ?: "🔥"
            when (step) {
                1 -> if (clickByText(root, listOf("Inbox", "Kotak masuk"))) step = 2
                2 -> if (clickByText(root, listOf(friend))) step = 3
                3 -> {
                    val et = findEditText(root)
                    if (et != null) {
                        val args = Bundle().apply {
                            putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, msg)
                        }
                        et.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, args)
                        step = 4
                    }
                }
                4 -> if (clickByText(root, listOf("Send", "Kirim"))) step = 0
            }
        }
        h.postDelayed(::tick, (1500..2800).random().toLong())
    }

    private fun clickByText(root: AccessibilityNodeInfo, labels: List<String>): Boolean {
        for (l in labels.filter { it.isNotBlank() }) {
            val nodes = root.findAccessibilityNodeInfosByText(l)
            for (n in nodes) {
                var c: AccessibilityNodeInfo? = n
                while (c != null && !c.isClickable) c = c.parent
                if (c != null && c.performAction(AccessibilityNodeInfo.ACTION_CLICK)) return true
            }
        }
        // fallback: cari lewat contentDescription
        return findByDesc(root, labels)
    }

    private fun findByDesc(n: AccessibilityNodeInfo?, labels: List<String>): Boolean {
        if (n == null) return false
        val d = n.contentDescription?.toString()
        if (d != null && labels.any { it.isNotBlank() && d.contains(it, true) }) {
            var c: AccessibilityNodeInfo? = n
            while (c != null && !c.isClickable) c = c.parent
            if (c != null && c.performAction(AccessibilityNodeInfo.ACTION_CLICK)) return true
        }
        for (i in 0 until n.childCount) if (findByDesc(n.getChild(i), labels)) return true
        return false
    }

    private fun findEditText(n: AccessibilityNodeInfo?): AccessibilityNodeInfo? {
        if (n == null) return null
        if (n.className == "android.widget.EditText") return n
        for (i in 0 until n.childCount) findEditText(n.getChild(i))?.let { return it }
        return null
    }
}
