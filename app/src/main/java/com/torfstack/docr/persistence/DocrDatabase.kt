package com.torfstack.docr.persistence

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.torfstack.docr.crypto.DocrCrypto
import java.io.ByteArrayOutputStream

@Database(entities = [CategoryEntity::class, ImageEntity::class], version = 1)
@TypeConverters(DocrDatabase.BitmapConverter::class)
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

    class BitmapConverter {
        @TypeConverter
        fun fromBitmap(bitmap: Bitmap): ByteArray {
            val outputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            return DocrCrypto.encrypt(outputStream.toByteArray())
        }

        @TypeConverter
        fun toBitmap(byteArray: ByteArray): Bitmap {
            val decrypted = DocrCrypto.decrypt(byteArray)
            return BitmapFactory.decodeByteArray(decrypted, 0, decrypted.size)
        }
    }
}