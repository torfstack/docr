package com.torfstack.docr.persistence

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query

@Entity(tableName = "image")
data class Image(
    @PrimaryKey val uid: String,
    @ColumnInfo(name = "data", typeAffinity = ColumnInfo.BLOB) val data: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Image

        if (uid != other.uid) return false
        if (!data.contentEquals(other.data)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = uid.hashCode()
        result = 31 * result + data.contentHashCode()
        return result
    }
}

@Dao
interface ImageDao {
    @Query("SELECT * FROM image WHERE uid = :withId")
    suspend fun getImage(withId: String): Image

    @Insert
    fun insertImage(image: Image)
}