package com.sehataku.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity Room yang merepresentasikan satu catatan makanan.
 * Setiap baris tersimpan dengan field [date] (format yyyy-MM-dd)
 * sehingga data bisa difilter "khusus hari ini".
 */
@Entity(tableName = "food_entries")
data class FoodEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,      // nama makanan, contoh: "Nasi Goreng"
    val time: String,      // jam makan, format "HH:mm"
    val calories: Int,     // perkiraan kalori dalam kkal
    val date: String       // tanggal pencatatan, format "yyyy-MM-dd"
)
