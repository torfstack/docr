package com.torfstack.docr.persistence

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [CategoryEntity::class, ImageEntity::class], version = 1)
@TypeConverters(Converters::class)
abstract class Database : RoomDatabase() {
    abstract fun dao(): CategoryImageDao
}