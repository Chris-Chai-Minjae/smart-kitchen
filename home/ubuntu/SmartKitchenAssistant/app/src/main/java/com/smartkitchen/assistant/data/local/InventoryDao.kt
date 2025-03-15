package com.smartkitchen.assistant.data.local

import androidx.room.*
import com.smartkitchen.assistant.data.model.InventoryItem
import kotlinx.coroutines.flow.Flow

/**
 * 재고 관리 데이터 접근 객체 (DAO)
 * 식품 재고 관련 데이터베이스 작업을 처리합니다.
 */
@Dao
interface InventoryDao {
    @Insert
    suspend fun insertInventoryItem(item: InventoryItem): Long
    
    @Update
    suspend fun updateInventoryItem(item: InventoryItem)
    
    @Delete
    suspend fun deleteInventoryItem(item: InventoryItem)
    
    @Query("SELECT * FROM inventory_items ORDER BY name ASC")
    fun getAllInventoryItems(): Flow<List<InventoryItem>>
    
    @Query("SELECT * FROM inventory_items WHERE expiryDate < :date ORDER BY expiryDate ASC")
    fun getExpiringItems(date: Long): Flow<List<InventoryItem>>
    
    @Query("SELECT * FROM inventory_items WHERE name LIKE '%' || :query || '%'")
    fun searchInventoryItems(query: String): Flow<List<InventoryItem>>
    
    @Query("SELECT * FROM inventory_items WHERE barcode = :barcode")
    suspend fun getInventoryItemByBarcode(barcode: String): InventoryItem?
    
    @Query("SELECT * FROM inventory_items WHERE id = :itemId")
    suspend fun getInventoryItemById(itemId: Long): InventoryItem?
    
    @Query("SELECT * FROM inventory_items WHERE ingredientId = :ingredientId")
    fun getInventoryItemsByIngredientId(ingredientId: Long): Flow<List<InventoryItem>>
    
    @Query("SELECT * FROM inventory_items WHERE storageLocation = :location ORDER BY name ASC")
    fun getInventoryItemsByLocation(location: String): Flow<List<InventoryItem>>
    
    @Query("UPDATE inventory_items SET quantity = :newQuantity, updatedAt = :updateTime WHERE id = :itemId")
    suspend fun updateInventoryItemQuantity(itemId: Long, newQuantity: Double, updateTime: Long = System.currentTimeMillis())
    
    @Query("SELECT DISTINCT storageLocation FROM inventory_items WHERE storageLocation IS NOT NULL")
    fun getAllStorageLocations(): Flow<List<String>>
}
