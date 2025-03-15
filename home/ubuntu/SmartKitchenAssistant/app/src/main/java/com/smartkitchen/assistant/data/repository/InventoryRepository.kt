package com.smartkitchen.assistant.data.repository

import com.smartkitchen.assistant.data.local.InventoryDao
import com.smartkitchen.assistant.data.model.InventoryItem
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 재고 관리 저장소
 * 식품 재고 관련 데이터 작업을 처리하고 데이터 소스를 추상화합니다.
 */
@Singleton
class InventoryRepository @Inject constructor(
    private val inventoryDao: InventoryDao
) {
    /**
     * 모든 재고 항목 목록을 가져옵니다.
     */
    fun getAllInventoryItems(): Flow<List<InventoryItem>> = inventoryDao.getAllInventoryItems()
    
    /**
     * 유통기한이 임박한 재고 항목 목록을 가져옵니다.
     */
    fun getExpiringItems(date: Long): Flow<List<InventoryItem>> = inventoryDao.getExpiringItems(date)
    
    /**
     * 재고 항목을 검색합니다.
     */
    fun searchInventoryItems(query: String): Flow<List<InventoryItem>> = 
        inventoryDao.searchInventoryItems(query)
    
    /**
     * 바코드로 재고 항목을 가져옵니다.
     */
    suspend fun getInventoryItemByBarcode(barcode: String): InventoryItem? = 
        inventoryDao.getInventoryItemByBarcode(barcode)
    
    /**
     * ID로 재고 항목을 가져옵니다.
     */
    suspend fun getInventoryItemById(itemId: Long): InventoryItem? = 
        inventoryDao.getInventoryItemById(itemId)
    
    /**
     * 재료 ID로 재고 항목 목록을 가져옵니다.
     */
    fun getInventoryItemsByIngredientId(ingredientId: Long): Flow<List<InventoryItem>> = 
        inventoryDao.getInventoryItemsByIngredientId(ingredientId)
    
    /**
     * 보관 위치별 재고 항목 목록을 가져옵니다.
     */
    fun getInventoryItemsByLocation(location: String): Flow<List<InventoryItem>> = 
        inventoryDao.getInventoryItemsByLocation(location)
    
    /**
     * 모든 보관 위치 목록을 가져옵니다.
     */
    fun getAllStorageLocations(): Flow<List<String>> = inventoryDao.getAllStorageLocations()
    
    /**
     * 재고 항목을 저장합니다.
     */
    suspend fun insertInventoryItem(item: InventoryItem): Long = inventoryDao.insertInventoryItem(item)
    
    /**
     * 재고 항목을 업데이트합니다.
     */
    suspend fun updateInventoryItem(item: InventoryItem) = inventoryDao.updateInventoryItem(item)
    
    /**
     * 재고 항목을 삭제합니다.
     */
    suspend fun deleteInventoryItem(item: InventoryItem) = inventoryDao.deleteInventoryItem(item)
    
    /**
     * 재고 항목의 수량을 업데이트합니다.
     */
    suspend fun updateInventoryItemQuantity(itemId: Long, newQuantity: Double) = 
        inventoryDao.updateInventoryItemQuantity(itemId, newQuantity)
    
    /**
     * 바코드 스캔 결과로 재고 항목을 추가하거나 업데이트합니다.
     */
    suspend fun addOrUpdateInventoryItemFromBarcode(
        barcode: String,
        name: String,
        quantity: Double,
        unit: String,
        expiryDate: Long? = null
    ): Long {
        val existingItem = getInventoryItemByBarcode(barcode)
        
        return if (existingItem != null) {
            // 기존 항목 업데이트
            val updatedItem = existingItem.copy(
                quantity = quantity,
                expiryDate = expiryDate ?: existingItem.expiryDate,
                updatedAt = System.currentTimeMillis()
            )
            updateInventoryItem(updatedItem)
            existingItem.id
        } else {
            // 새 항목 추가
            val newItem = InventoryItem(
                name = name,
                quantity = quantity,
                unit = unit,
                barcode = barcode,
                purchaseDate = System.currentTimeMillis(),
                expiryDate = expiryDate,
                ingredientId = null  // 바코드 스캔으로는 재료 ID를 바로 연결하기 어려움
            )
            insertInventoryItem(newItem)
        }
    }
    
    /**
     * 재고 항목의 유통기한 임박 여부를 확인합니다.
     */
    fun isItemExpiringSoon(item: InventoryItem, thresholdDays: Int = 3): Boolean {
        val expiryDate = item.expiryDate ?: return false
        val currentTime = System.currentTimeMillis()
        val thresholdMillis = thresholdDays * 24 * 60 * 60 * 1000L
        
        return expiryDate - currentTime <= thresholdMillis
    }
}
