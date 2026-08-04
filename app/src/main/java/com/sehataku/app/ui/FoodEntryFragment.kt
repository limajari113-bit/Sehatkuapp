package com.sehataku.app.ui

import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.sehataku.app.R
import com.sehataku.app.data.AppDatabase
import com.sehataku.app.data.FoodEntry
import com.sehataku.app.databinding.FragmentFoodEntryBinding
import com.sehataku.app.util.DateUtils
import kotlinx.coroutines.launch
import java.util.Calendar

/**
 * Fragment untuk fitur Catat Makanan.
 * Berisi form: nama makanan, jam makan (dipilih lewat TimePickerDialog), dan kalori.
 * Data disimpan ke Room database, ditandai dengan tanggal hari ini.
 */
class FoodEntryFragment : Fragment() {

    private var _binding: FragmentFoodEntryBinding? = null
    private val binding get() = _binding!!

    // Jam makan yang sedang dipilih, default = jam sekarang
    private var jamTerpilih: String = DateUtils.getCurrentTimeString()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFoodEntryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnPilihJam.text = "${resources.getString(R.string.pilih_jam)} ($jamTerpilih)"

        binding.btnPilihJam.setOnClickListener { tampilkanTimePicker() }
        binding.btnSimpan.setOnClickListener { simpanMakanan() }
    }

    private fun tampilkanTimePicker() {
        val kalenderSekarang = Calendar.getInstance()
        val jamAwal = kalenderSekarang.get(Calendar.HOUR_OF_DAY)
        val menitAwal = kalenderSekarang.get(Calendar.MINUTE)

        TimePickerDialog(
            requireContext(),
            { _, jam, menit ->
                jamTerpilih = String.format("%02d:%02d", jam, menit)
                binding.btnPilihJam.text = "${resources.getString(R.string.pilih_jam)} ($jamTerpilih)"
            },
            jamAwal, menitAwal, true // true = format 24 jam
        ).show()
    }

    private fun simpanMakanan() {
        val nama = binding.etNamaMakanan.text.toString().trim()
        val kaloriText = binding.etKalori.text.toString().trim()

        if (nama.isEmpty() || kaloriText.isEmpty()) {
            Toast.makeText(requireContext(), R.string.isi_semua_field, Toast.LENGTH_SHORT).show()
            return
        }

        val kalori = kaloriText.toIntOrNull() ?: 0

        val entryBaru = FoodEntry(
            name = nama,
            time = jamTerpilih,
            calories = kalori,
            date = DateUtils.getTodayDateString()
        )

        // Operasi database dijalankan di coroutine agar tidak memblokir UI thread
        lifecycleScope.launch {
            val db = AppDatabase.getInstance(requireContext())
            db.foodDao().insert(entryBaru)

            Toast.makeText(requireContext(), R.string.makanan_tersimpan, Toast.LENGTH_SHORT).show()
            kosongkanForm()
        }
    }

    private fun kosongkanForm() {
        binding.etNamaMakanan.text.clear()
        binding.etKalori.text.clear()
        jamTerpilih = DateUtils.getCurrentTimeString()
        binding.btnPilihJam.text = "${resources.getString(R.string.pilih_jam)} ($jamTerpilih)"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
