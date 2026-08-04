package com.sehataku.app.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sehataku.app.data.FoodEntry
import com.sehataku.app.databinding.ItemFoodBinding

/**
 * Adapter RecyclerView sederhana untuk menampilkan daftar makanan hari ini
 * di halaman Ringkasan.
 */
class FoodAdapter(private var daftarMakanan: List<FoodEntry>) :
    RecyclerView.Adapter<FoodAdapter.FoodViewHolder>() {

    inner class FoodViewHolder(val binding: ItemFoodBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val binding = ItemFoodBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return FoodViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        val item = daftarMakanan[position]
        holder.binding.tvNamaMakanan.text = item.name
        holder.binding.tvJamMakan.text = item.time
        holder.binding.tvKaloriItem.text = "${item.calories} kkal"
    }

    override fun getItemCount(): Int = daftarMakanan.size

    // Dipanggil setiap kali data dari database berubah agar list ter-update
    fun updateData(dataBaru: List<FoodEntry>) {
        daftarMakanan = dataBaru
        notifyDataSetChanged()
    }
}
