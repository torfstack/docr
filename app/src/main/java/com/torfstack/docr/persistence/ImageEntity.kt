package com.torfstack.docr.persistence

import android.content.Context
import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.torfstack.docr.DocrFileManager

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
    @ColumnInfo(name = "category") val category: String
) {

    @Ignore
    private var data: ImageBitmap? = null

    @Ignore
    private var downscaledData: ImageBitmap? = null

    fun data(context: Context): ImageBitmap {
        if (data == null) {
            val bytes = DocrFileManager().getFromFiles(context, this)
            data = BitmapFactory.decodeByteArray(bytes, 0, bytes.size).asImageBitmap()
        }
        return data!!
    }

    fun downscaledData(context: Context): ImageBitmap {
        if (downscaledData == null) {
            val bytes = DocrFileManager().getDownscaledFromFiles(context, this)
            downscaledData = BitmapFactory.decodeByteArray(bytes, 0, bytes.size).asImageBitmap()
        }
        return downscaledData!!
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ImageEntity

        if (uid != other.uid) return false
        if (category != other.category) return false

        return true
    }

    override fun hashCode(): Int {
        var result = uid.hashCode()
        result = 31 * result + category.hashCode()
        return result
    }
}