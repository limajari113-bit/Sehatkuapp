package com.sehataku.app.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

/**
 * DAO (Data Access Object) untuk operasi CRUD tabel food_entries.
 * Semua fungsi bersifat suspend agar dijalankan di coroutine (bukan main thread).
 */
@Dao
interface FoodDao {

    @Insert
    suspend fun insert(food: FoodEntry)

    @Delete
    suspend fun delete(food: FoodEntry)

    // Ambil semua makanan pada tanggal tertentu, diurutkan berdasarkan jam makan
    @Query("SELECT * FROM food_entries WHERE date = :date ORDER BY time ASC")
    suspend fun getFoodByDate(date: String): List<FoodEntry>

    // Jumlahkan total kalori pada tanggal tertentu
    @Query("SELECT SUM(calories) FROM food_entries WHERE date = :date")
    suspend fun getTotalCaloriesByDate(date: String): Int?
}
