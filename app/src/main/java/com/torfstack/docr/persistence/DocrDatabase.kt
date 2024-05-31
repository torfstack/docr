package com.torfstack.docr.persistence

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [CategoryEntity::class, ImageEntity::class], version = 1)
abstract class DocrDatabase : RoomDatabase() {
    abstract fun dao(): Dao

    companion object {
        @Volatile
        private var INSTANCE: DocrDatabase? = null

        fun getInstance(context: Context): DocrDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context,
                    DocrDatabase::class.java,
                    "database"
                )
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}