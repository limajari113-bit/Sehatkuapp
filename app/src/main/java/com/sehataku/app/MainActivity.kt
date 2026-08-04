package com.sehataku.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.sehataku.app.databinding.ActivityMainBinding
import com.sehataku.app.ui.FoodEntryFragment
import com.sehataku.app.ui.StepTrackerFragment
import com.sehataku.app.ui.SummaryFragment

/**
 * Activity utama SehatAku.
 * Berperan sebagai "host" yang menampilkan salah satu dari 3 fragment
 * (Langkah / Makan / Ringkasan) tergantung menu yang dipilih di BottomNavigationView.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Tampilkan fragment Langkah sebagai halaman default saat aplikasi pertama dibuka
        if (savedInstanceState == null) {
            tampilkanFragment(StepTrackerFragment())
        }

        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_langkah -> {
                    tampilkanFragment(StepTrackerFragment())
                    true
                }
                R.id.nav_makan -> {
                    tampilkanFragment(FoodEntryFragment())
                    true
                }
                R.id.nav_ringkasan -> {
                    tampilkanFragment(SummaryFragment())
                    true
                }
                else -> false
            }
        }
    }

    // Fungsi bantu untuk mengganti fragment yang tampil di fragment_container
    private fun tampilkanFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}
