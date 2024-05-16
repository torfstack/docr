package com.torfstack.docr.persistence

import androidx.lifecycle.LiveData
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.TypeConverter
import androidx.room.Update
import java.util.Date

@Entity(tableName = "category")
data class CategoryEntity(
    @PrimaryKey val uid: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "created") val created: Long,
    @ColumnInfo(name = "last_updated") val lastUpdated: Long,
    @ColumnInfo(name = "thumbnail") val thumbnail: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CategoryEntity

        if (uid != other.uid) return false
        if (name != other.name) return false
        if (description != other.description) return false
        if (created != other.created) return false
        if (lastUpdated != other.lastUpdated) return false

        return true
    }

    override fun hashCode(): Int {
        var result = uid.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + created.hashCode()
        result = 31 * result + lastUpdated.hashCode()
        return result
    }
}

@Dao
interface CategoryImageDao {
    @Query("SELECT * FROM category")
    fun getAllCategories(): LiveData<List<CategoryEntity>>

    @Query("SELECT * FROM category WHERE uid = :id")
    fun getCategoryById(id: String): LiveData<CategoryEntity>

    @Insert
    suspend fun insertCategory(category: CategoryEntity)

    @Update
    suspend fun updateCategory(category: CategoryEntity)

    @Insert
    suspend fun insertImage(image: ImageEntity)

    @Transaction
    suspend fun insertCategoryWithImages(category: CategoryEntity, images: List<ImageEntity>) {
        insertCategory(category)
        images.forEach { insertImage(it) }
    }
}

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}
