package com.smartkitchen.assistant.data.local

import androidx.room.*
import com.smartkitchen.assistant.data.model.MealPlan
import com.smartkitchen.assistant.data.model.MealPlanItem
import com.smartkitchen.assistant.data.model.Recipe
import kotlinx.coroutines.flow.Flow

/**
 * 식단 계획 데이터 접근 객체 (DAO)
 * 식단 계획 관련 데이터베이스 작업을 처리합니다.
 */
@Dao
interface MealPlanDao {
    @Insert
    suspend fun insertMealPlan(mealPlan: MealPlan): Long
    
    @Update
    suspend fun updateMealPlan(mealPlan: MealPlan)
    
    @Delete
    suspend fun deleteMealPlan(mealPlan: MealPlan)
    
    @Transaction
    @Query("SELECT * FROM meal_plans WHERE date BETWEEN :startDate AND :endDate ORDER BY date ASC")
    fun getMealPlansWithItems(startDate: Long, endDate: Long): Flow<List<MealPlanWithItems>>
    
    @Query("SELECT * FROM meal_plans WHERE date = :date")
    suspend fun getMealPlanByDate(date: Long): MealPlan?
    
    @Insert
    suspend fun insertMealPlanItem(item: MealPlanItem): Long
    
    @Update
    suspend fun updateMealPlanItem(item: MealPlanItem)
    
    @Delete
    suspend fun deleteMealPlanItem(item: MealPlanItem)
    
    @Query("SELECT * FROM meal_plan_items WHERE mealPlanId = :mealPlanId ORDER BY mealType")
    fun getMealPlanItems(mealPlanId: Long): Flow<List<MealPlanItem>>
    
    @Query("DELETE FROM meal_plan_items WHERE mealPlanId = :mealPlanId")
    suspend fun deleteMealPlanItemsForMealPlan(mealPlanId: Long)
    
    @Transaction
    suspend fun getMealPlanForDate(date: Long): MealPlanWithItems? {
        val mealPlan = getMealPlanByDate(date) ?: return null
        return getMealPlanWithItemsById(mealPlan.id)
    }
    
    @Transaction
    @Query("SELECT * FROM meal_plans WHERE id = :mealPlanId")
    suspend fun getMealPlanWithItemsById(mealPlanId: Long): MealPlanWithItems?
    
    @Transaction
    suspend fun createOrUpdateMealPlanForDate(date: Long, items: List<MealPlanItem>): Long {
        val existingMealPlan = getMealPlanByDate(date)
        val mealPlanId = if (existingMealPlan != null) {
            // 기존 식단 계획 업데이트
            val updatedMealPlan = existingMealPlan.copy(updatedAt = System.currentTimeMillis())
            updateMealPlan(updatedMealPlan)
            
            // 기존 항목 삭제
            deleteMealPlanItemsForMealPlan(existingMealPlan.id)
            existingMealPlan.id
        } else {
            // 새 식단 계획 생성
            insertMealPlan(MealPlan(date = date))
        }
        
        // 새 항목 추가
        items.forEach { item ->
            insertMealPlanItem(item.copy(mealPlanId = mealPlanId))
        }
        
        return mealPlanId
    }
}

/**
 * 식단 계획과 항목을 함께 포함하는 데이터 클래스
 */
data class MealPlanWithItems(
    @Embedded val mealPlan: MealPlan,
    @Relation(
        entity = MealPlanItem::class,
        parentColumn = "id",
        entityColumn = "mealPlanId"
    )
    val mealPlanItems: List<MealPlanItemWithRecipe>
)

/**
 * 식단 계획 항목과 연결된 레시피를 함께 포함하는 데이터 클래스
 */
data class MealPlanItemWithRecipe(
    @Embedded val mealPlanItem: MealPlanItem,
    @Relation(
        parentColumn = "recipeId",
        entityColumn = "id"
    )
    val recipe: Recipe?
)
