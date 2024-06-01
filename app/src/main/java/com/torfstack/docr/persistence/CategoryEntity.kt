package com.torfstack.docr.persistence

import android.content.Context
import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.torfstack.docr.DocrFileManager

@Entity(tableName = "category")
data class CategoryEntity(
    @PrimaryKey val uid: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "created") val created: Long,
    @ColumnInfo(name = "last_updated") val lastUpdated: Long,
    @ColumnInfo(name = "version") internal val version: Int
) {

    @Ignore
    private var thumbnail: ImageBitmap? = null

    fun thumbnail(context: Context): ImageBitmap {
        if (thumbnail == null) {
            val bytes = DocrFileManager().getThumbnailFromFiles(context, this)
            thumbnail = BitmapFactory.decodeByteArray(bytes, 0, bytes.size).asImageBitmap()
        }
        return thumbnail!!
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CategoryEntity

        if (uid != other.uid) return false
        if (name != other.name) return false
        if (description != other.description) return false
        if (created != other.created) return false
        if (lastUpdated != other.lastUpdated) return false
        if (version != other.version) return false

        return true
    }

    override fun hashCode(): Int {
        var result = uid.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + created.hashCode()
        result = 31 * result + lastUpdated.hashCode()
        result = 31 * result + version.hashCode()
        return result
    }
}
