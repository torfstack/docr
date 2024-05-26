package com.torfstack.docr.persistence

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.torfstack.docr.crypto.DocrCrypto

@Dao
interface Dao : LiveDataDao, CategoryImageDao

@Dao
interface LiveDataDao {
    @Query("SELECT * FROM category")
    fun getAllCategories(): LiveData<List<CategoryEntity>>

    @Query("SELECT * FROM category WHERE uid = :id")
    fun getCategoryById(id: String): LiveData<CategoryEntity>

    @Query("SELECT * FROM image WHERE category = :categoryId")
    fun getImagesForCategory(categoryId: String): LiveData<List<ImageEntity>>
}

@Dao
interface CategoryImageDao {
    @Insert
    suspend fun insertCategoryInternal(category: CategoryEntity)

    @Insert
    suspend fun insertCategory(category: CategoryEntity) {
        insertCategoryInternal(category.copy(thumbnailInternal = DocrCrypto.encrypt(category.thumbnailInternal)))
    }

    @Update
    suspend fun updateCategory(category: CategoryEntity)

    @Insert
    suspend fun insertImageInternal(image: ImageEntity)

    @Insert
    suspend fun insertImage(image: ImageEntity) {
        insertImageInternal(
            image.copy(
                dataInternal = DocrCrypto.encrypt(image.dataInternal),
                downscaledInternal = DocrCrypto.encrypt(image.downscaledInternal)
            )
        )
    }

    @Query("SELECT * FROM image WHERE category = :categoryId")
    suspend fun imagesForCategory(categoryId: String): List<ImageEntity>

    @Delete
    suspend fun deleteCategory(category: CategoryEntity)

    @Delete
    suspend fun deleteImage(image: ImageEntity)

    @Transaction
    suspend fun deleteCategoryWithImages(category: CategoryEntity) {
        deleteCategory(category)
        imagesForCategory(category.uid).forEach { deleteImage(it) }
    }

    @Transaction
    suspend fun insertCategoryWithImages(category: CategoryEntity, images: List<ImageEntity>) {
        insertCategory(category)
        images.forEach { insertImage(it) }
    }
}
