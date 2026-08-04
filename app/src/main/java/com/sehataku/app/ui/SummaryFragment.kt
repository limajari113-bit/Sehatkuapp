package com.sehataku.app.ui

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.sehataku.app.data.AppDatabase
import com.sehataku.app.databinding.FragmentSummaryBinding
import com.sehataku.app.util.DateUtils
import com.sehataku.app.util.StepPrefsManager
import kotlinx.coroutines.launch

/**
 * Fragment untuk halaman Ringkasan.
 * Menampilkan total langkah hari ini + daftar & total kalori makanan hari ini.
 */
class SummaryFragment : Fragment(), SensorEventListener {

    private var _binding: FragmentSummaryBinding? = null
    private val binding get() = _binding!!

    private lateinit var sensorManager: SensorManager
    private var stepSensor: Sensor? = null
    private lateinit var stepPrefsManager: StepPrefsManager

    private lateinit var adapter: FoodAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSummaryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        stepPrefsManager = StepPrefsManager(requireContext())
        sensorManager = requireContext().getSystemService(android.content.Context.SENSOR_SERVICE) as SensorManager
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        adapter = FoodAdapter(emptyList())
        binding.rvDaftarMakanan.layoutManager = LinearLayoutManager(requireContext())
        binding.rvDaftarMakanan.adapter = adapter

        muatDataMakanan()
    }

    override fun onResume() {
        super.onResume()
        stepSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
        // Refresh daftar makanan setiap kali halaman ini ditampilkan kembali,
        // supaya data terbaru dari halaman "Catat Makanan" langsung terlihat
        muatDataMakanan()
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null || event.sensor.type != Sensor.TYPE_STEP_COUNTER) return
        val totalSensorSteps = event.values[0].toInt()
        val langkahHariIni = stepPrefsManager.hitungLangkahHariIni(totalSensorSteps)
        binding.tvTotalLangkah.text = langkahHariIni.toString()
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    private fun muatDataMakanan() {
        lifecycleScope.launch {
            val db = AppDatabase.getInstance(requireContext())
            val tanggalHariIni = DateUtils.getTodayDateString()

            val daftarMakanan = db.foodDao().getFoodByDate(tanggalHariIni)
            val totalKalori = db.foodDao().getTotalCaloriesByDate(tanggalHariIni) ?: 0

            binding.tvTotalKaloriMakanan.text = "$totalKalori kkal"
            adapter.updateData(daftarMakanan)

            // Tampilkan teks "belum ada makanan" jika list kosong
            if (daftarMakanan.isEmpty()) {
                binding.tvKosong.visibility = View.VISIBLE
                binding.rvDaftarMakanan.visibility = View.GONE
            } else {
                binding.tvKosong.visibility = View.GONE
                binding.rvDaftarMakanan.visibility = View.VISIBLE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
