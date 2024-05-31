package com.torfstack.docr.persistence

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.torfstack.docr.crypto.DocrCrypto

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
    @ColumnInfo(name = "data", typeAffinity = ColumnInfo.BLOB) internal val dataInternal: ByteArray,
    @ColumnInfo(
        name = "downscaled",
        typeAffinity = ColumnInfo.BLOB
    ) internal val downscaledInternal: ByteArray,
    @ColumnInfo(name = "category") val category: String
) {

    val data by lazy {
        DocrCrypto.decrypt(dataInternal)
    }

    val downscaled by lazy {
        DocrCrypto.decrypt(downscaledInternal)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ImageEntity

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