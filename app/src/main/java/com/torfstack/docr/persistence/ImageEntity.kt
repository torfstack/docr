package com.torfstack.docr.persistence

import android.graphics.Bitmap
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "image",
    foreignKeys = [ForeignKey(
        entity = CategoryEntity::class,
        parentColumns = ["uid"],
        childColumns = ["category"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class ImageEntity(
    @PrimaryKey val uid: String,
    @ColumnInfo(name = "data") val data: Bitmap,
    @ColumnInfo(name = "downscaled") val downscaled: Bitmap,
    @ColumnInfo(name = "category") val category: String
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ImageEntity

        if (uid != other.uid) return false
        if (data != other.data) return false

        return true
    }

    override fun hashCode(): Int {
        var result = uid.hashCode()
        result = 31 * result + data.hashCode()
        return result
    }
}