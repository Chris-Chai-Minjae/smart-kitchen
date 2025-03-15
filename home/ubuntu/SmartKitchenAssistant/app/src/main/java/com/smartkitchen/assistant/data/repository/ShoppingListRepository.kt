package com.smartkitchen.assistant.data.repository

import com.smartkitchen.assistant.data.local.ShoppingListDao
import com.smartkitchen.assistant.data.model.ShoppingListItem
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 쇼핑 목록 저장소
 * 쇼핑 목록 관련 데이터 작업을 처리하고 데이터 소스를 추상화합니다.
 */
@Singleton
class ShoppingListRepository @Inject constructor(
    private val shoppingListDao: ShoppingListDao
) {
    /**
     * 모든 쇼핑 목록 항목을 가져옵니다.
     */
    fun getAllShoppingListItems(): Flow<List<ShoppingListItem>> = shoppingListDao.getAllShoppingListItems()
    
    /**
     * 활성 상태(체크되지 않은) 쇼핑 목록 항목을 가져옵니다.
     */
    fun getActiveShoppingListItems(): Flow<List<ShoppingListItem>> = shoppingListDao.getActiveShoppingListItems()
    
    /**
     * 재료 ID로 쇼핑 목록 항목을 가져옵니다.
     */
    fun getShoppingListItemsByIngredientId(ingredientId: Long): Flow<List<ShoppingListItem>> = 
        shoppingListDao.getShoppingListItemsByIngredientId(ingredientId)
    
    /**
     * 카테고리별 쇼핑 목록 항목을 가져옵니다.
     */
    fun getShoppingListItemsByCategory(category: String): Flow<List<ShoppingListItem>> = 
        shoppingListDao.getShoppingListItemsByCategory(category)
    
    /**
     * 모든 카테고리 목록을 가져옵니다.
     */
    fun getAllCategories(): Flow<List<String>> = shoppingListDao.getAllCategories()
    
    /**
     * 쇼핑 목록 항목을 저장합니다.
     */
    suspend fun insertShoppingListItem(item: ShoppingListItem): Long = shoppingListDao.insertShoppingListItem(item)
    
    /**
     * 쇼핑 목록 항목을 업데이트합니다.
     */
    suspend fun updateShoppingListItem(item: ShoppingListItem) = shoppingListDao.updateShoppingListItem(item)
    
    /**
     * 쇼핑 목록 항목을 삭제합니다.
     */
    suspend fun deleteShoppingListItem(item: ShoppingListItem) = shoppingListDao.deleteShoppingListItem(item)
    
    /**
     * 항목의 체크 상태를 업데이트합니다.
     */
    suspend fun updateItemCheckedStatus(id: Long, isChecked: Boolean) = 
        shoppingListDao.updateItemCheckedStatus(id, isChecked)
    
    /**
     * 체크된 항목을 모두 삭제합니다.
     */
    suspend fun deleteCheckedItems() = shoppingListDao.deleteCheckedItems()
    
    /**
     * 쇼핑 목록 항목을 추가하거나 업데이트합니다.
     */
    suspend fun addOrUpdateShoppingListItem(item: ShoppingListItem): Long = 
        shoppingListDao.addOrUpdateShoppingListItem(item)
    
    /**
     * 레시피 재료를 쇼핑 목록에 추가합니다.
     */
    suspend fun addRecipeIngredientsToShoppingList(
        recipeIngredients: List<com.smartkitchen.assistant.data.model.RecipeIngredient>,
        ingredientNames: Map<Long, String>
    ): List<Long> {
        val itemIds = mutableListOf<Long>()
        
        recipeIngredients.forEach { recipeIngredient ->
            val ingredientName = ingredientNames[recipeIngredient.ingredientId] ?: "알 수 없는 재료"
            val category = getCategoryForIngredient(ingredientName)
            
            val shoppingItem = ShoppingListItem(
                ingredientId = recipeIngredient.ingredientId,
                name = ingredientName,
                quantity = recipeIngredient.quantity,
                unit = recipeIngredient.unit,
                isChecked = false,
                category = category,
                notes = null
            )
            
            val itemId = addOrUpdateShoppingListItem(shoppingItem)
            itemIds.add(itemId)
        }
        
        return itemIds
    }
    
    /**
     * 재료명에 따라 카테고리를 추정합니다.
     */
    private fun getCategoryForIngredient(ingredientName: String): String {
        return when {
            ingredientName.contains("고기") || ingredientName.contains("육") || 
            ingredientName.contains("돼지") || ingredientName.contains("소고기") || 
            ingredientName.contains("닭") -> "육류"
            
            ingredientName.contains("생선") || ingredientName.contains("새우") || 
            ingredientName.contains("조개") || ingredientName.contains("해산물") -> "해산물"
            
            ingredientName.contains("양파") || ingredientName.contains("마늘") || 
            ingredientName.contains("당근") || ingredientName.contains("배추") || 
            ingredientName.contains("채소") -> "채소"
            
            ingredientName.contains("사과") || ingredientName.contains("배") || 
            ingredientName.contains("바나나") || ingredientName.contains("과일") -> "과일"
            
            ingredientName.contains("우유") || ingredientName.contains("치즈") || 
            ingredientName.contains("요거트") -> "유제품"
            
            ingredientName.contains("쌀") || ingredientName.contains("밀가루") || 
            ingredientName.contains("면") -> "곡물"
            
            ingredientName.contains("소금") || ingredientName.contains("설탕") || 
            ingredientName.contains("간장") || ingredientName.contains("고추장") -> "양념"
            
            else -> "기타"
        }
    }
}
