package com.sehataku.app.ui

import android.Manifest
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.sehataku.app.databinding.FragmentStepTrackerBinding
import com.sehataku.app.util.StepPrefsManager
import kotlin.math.roundToInt

/**
 * Fragment untuk fitur Tracker Langkah.
 * Membaca sensor STEP_COUNTER bawaan HP secara real-time,
 * lalu menghitung estimasi jarak tempuh dan kalori terbakar.
 */
class StepTrackerFragment : Fragment(), SensorEventListener {

    private var _binding: FragmentStepTrackerBinding? = null
    private val binding get() = _binding!!

    private lateinit var sensorManager: SensorManager
    private var stepSensor: Sensor? = null
    private lateinit var stepPrefsManager: StepPrefsManager

    // Target langkah harian sesuai request
    private val targetLangkah = 10000

    // Konstanta untuk estimasi. Rata-rata panjang 1 langkah orang dewasa.
    private val panjangLangkahMeter = 0.762
    // Rata-rata kalori terbakar per langkah (estimasi umum, bukan medis presisi)
    private val kaloriPerLangkah = 0.04

    companion object {
        private const val REQUEST_CODE_ACTIVITY_RECOGNITION = 100
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStepTrackerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        stepPrefsManager = StepPrefsManager(requireContext())
        sensorManager = requireContext().getSystemService(android.content.Context.SENSOR_SERVICE) as SensorManager
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        if (stepSensor == null) {
            // HP tidak memiliki sensor step counter
            binding.tvInfoSensor.text = "HP ini tidak memiliki sensor penghitung langkah"
        } else {
            mintaIzinJikaPerlu()
        }
    }

    // Di Android 10 (API 29) ke atas, membaca sensor step counter butuh izin runtime
    private fun mintaIzinJikaPerlu() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val izinDiberikan = ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACTIVITY_RECOGNITION
            ) == PackageManager.PERMISSION_GRANTED

            if (!izinDiberikan) {
                requestPermissions(
                    arrayOf(Manifest.permission.ACTIVITY_RECOGNITION),
                    REQUEST_CODE_ACTIVITY_RECOGNITION
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_ACTIVITY_RECOGNITION) {
            if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                binding.tvInfoSensor.text =
                    resources.getString(com.sehataku.app.R.string.izin_sensor_ditolak)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Daftarkan listener sensor setiap kali fragment tampil di layar
        stepSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onPause() {
        super.onPause()
        // Lepas listener saat fragment tidak terlihat untuk hemat baterai
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null || event.sensor.type != Sensor.TYPE_STEP_COUNTER) return

        // event.values[0] adalah total langkah kumulatif sejak HP terakhir reboot
        val totalSensorSteps = event.values[0].toInt()
        val langkahHariIni = stepPrefsManager.hitungLangkahHariIni(totalSensorSteps)

        tampilkanData(langkahHariIni)
    }

    private fun tampilkanData(langkah: Int) {
        binding.tvJumlahLangkah.text = langkah.toString()

        // Progress bar dibatasi maksimal 100% walau langkah melebihi target
        val persentase = ((langkah.toFloat() / targetLangkah) * 100).roundToInt().coerceIn(0, 100)
        binding.progressLangkah.progress = persentase
        binding.tvPersentase.text = "$persentase%"

        // Estimasi jarak dalam kilometer
        val jarakKm = (langkah * panjangLangkahMeter) / 1000.0
        binding.tvJarak.text = String.format("%.2f km", jarakKm)

        // Estimasi kalori terbakar
        val kalori = (langkah * kaloriPerLangkah).roundToInt()
        binding.tvKaloriTerbakar.text = "$kalori kkal"
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Tidak digunakan, tapi wajib di-override karena interface SensorEventListener
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
