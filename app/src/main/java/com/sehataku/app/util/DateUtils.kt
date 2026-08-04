package com.sehataku.app.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Kumpulan fungsi bantu untuk format tanggal & jam,
 * dipakai bersama oleh StepTrackerFragment, FoodEntryFragment, dan SummaryFragment.
 */
object DateUtils {

    // Contoh hasil: "2026-08-05" -> dipakai sebagai kunci penyimpanan per hari
    fun getTodayDateString(): String {
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return format.format(Date())
    }

    // Contoh hasil: "14:05"
    fun getCurrentTimeString(): String {
        val format = SimpleDateFormat("HH:mm", Locale.getDefault())
        return format.format(Date())
    }
}
