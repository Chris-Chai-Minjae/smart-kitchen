package com.smartkitchen.assistant.data.local

import androidx.room.*
import com.smartkitchen.assistant.data.model.ShoppingListItem
import kotlinx.coroutines.flow.Flow

/**
 * 쇼핑 목록 데이터 접근 객체 (DAO)
 * 쇼핑 목록 관련 데이터베이스 작업을 처리합니다.
 */
@Dao
interface ShoppingListDao {
    @Insert
    suspend fun insertShoppingListItem(item: ShoppingListItem): Long
    
    @Update
    suspend fun updateShoppingListItem(item: ShoppingListItem)
    
    @Delete
    suspend fun deleteShoppingListItem(item: ShoppingListItem)
    
    @Query("SELECT * FROM shopping_list_items ORDER BY category, name ASC")
    fun getAllShoppingListItems(): Flow<List<ShoppingListItem>>
    
    @Query("SELECT * FROM shopping_list_items WHERE isChecked = 0 ORDER BY category, name ASC")
    fun getActiveShoppingListItems(): Flow<List<ShoppingListItem>>
    
    @Query("UPDATE shopping_list_items SET isChecked = :isChecked WHERE id = :id")
    suspend fun updateItemCheckedStatus(id: Long, isChecked: Boolean)
    
    @Query("DELETE FROM shopping_list_items WHERE isChecked = 1")
    suspend fun deleteCheckedItems()
    
    @Query("SELECT * FROM shopping_list_items WHERE ingredientId = :ingredientId")
    fun getShoppingListItemsByIngredientId(ingredientId: Long): Flow<List<ShoppingListItem>>
    
    @Query("SELECT * FROM shopping_list_items WHERE category = :category ORDER BY name ASC")
    fun getShoppingListItemsByCategory(category: String): Flow<List<ShoppingListItem>>
    
    @Query("SELECT DISTINCT category FROM shopping_list_items WHERE category IS NOT NULL")
    fun getAllCategories(): Flow<List<String>>
    
    @Transaction
    suspend fun addOrUpdateShoppingListItem(item: ShoppingListItem): Long {
        val existingItem = item.ingredientId?.let { ingredientId ->
            getShoppingListItemByIngredientId(ingredientId)
        }
        
        return if (existingItem != null) {
            val updatedItem = existingItem.copy(
                quantity = existingItem.quantity + item.quantity,
                isChecked = false
            )
            updateShoppingListItem(updatedItem)
            existingItem.id
        } else {
            insertShoppingListItem(item)
        }
    }
    
    @Query("SELECT * FROM shopping_list_items WHERE ingredientId = :ingredientId AND isChecked = 0 LIMIT 1")
    suspend fun getShoppingListItemByIngredientId(ingredientId: Long): ShoppingListItem?
}
