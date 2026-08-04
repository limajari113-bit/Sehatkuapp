package com.sehataku.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Kelas database Room utama aplikasi.
 * Menggunakan pola Singleton agar hanya ada 1 instance database
 * selama aplikasi berjalan (mencegah memory leak & konflik akses).
 */
@Database(entities = [FoodEntry::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun foodDao(): FoodDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "sehataku_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
