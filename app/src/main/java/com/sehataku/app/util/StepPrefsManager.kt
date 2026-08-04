package com.sehataku.app.util

import android.content.Context

/**
 * Helper SharedPreferences khusus untuk menyimpan "baseline" step counter.
 *
 * Sensor TYPE_STEP_COUNTER Android memberikan jumlah langkah KUMULATIF
 * sejak HP terakhir kali nyala (reboot), BUKAN langkah hari ini saja.
 * Supaya bisa menampilkan "langkah hari ini", kita perlu menyimpan nilai
 * sensor pada awal hari ini (baseline), lalu:
 *
 *      langkah hari ini = nilai sensor sekarang - baseline
 *
 * Setiap kali tanggal berganti, baseline direset ke nilai sensor saat itu.
 */
class StepPrefsManager(context: Context) {

    private val prefs = context.getSharedPreferences("sehataku_step_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_BASELINE = "baseline_steps"
        private const val KEY_DATE = "baseline_date"
    }

    /**
     * Menghitung langkah hari ini berdasarkan nilai mentah sensor [totalSensorSteps].
     * Fungsi ini otomatis mengurus reset baseline jika hari sudah berganti,
     * atau jika HP baru saja reboot (nilai sensor lebih kecil dari baseline lama).
     */
    fun hitungLangkahHariIni(totalSensorSteps: Int): Int {
        val today = DateUtils.getTodayDateString()
        val savedDate = prefs.getString(KEY_DATE, null)
        var baseline = prefs.getInt(KEY_BASELINE, -1)

        val perluResetBaseline =
            savedDate != today ||        // hari sudah berganti
            baseline == -1 ||             // belum pernah diset sama sekali
            totalSensorSteps < baseline   // HP baru reboot, counter sensor ke-reset

        if (perluResetBaseline) {
            baseline = totalSensorSteps
            prefs.edit()
                .putInt(KEY_BASELINE, baseline)
                .putString(KEY_DATE, today)
                .apply()
        }

        val langkahHariIni = totalSensorSteps - baseline
        return if (langkahHariIni < 0) 0 else langkahHariIni
    }
}
