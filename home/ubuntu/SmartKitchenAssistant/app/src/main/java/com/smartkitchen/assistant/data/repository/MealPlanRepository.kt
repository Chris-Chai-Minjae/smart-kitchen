package com.smartkitchen.assistant.data.repository

import com.smartkitchen.assistant.data.local.MealPlanDao
import com.smartkitchen.assistant.data.local.MealPlanWithItems
import com.smartkitchen.assistant.data.model.MealPlan
import com.smartkitchen.assistant.data.model.MealPlanItem
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 식단 계획 저장소
 * 식단 계획 관련 데이터 작업을 처리하고 데이터 소스를 추상화합니다.
 */
@Singleton
class MealPlanRepository @Inject constructor(
    private val mealPlanDao: MealPlanDao
) {
    /**
     * 특정 기간의 식단 계획을 가져옵니다.
     */
    fun getMealPlansWithItems(startDate: Long, endDate: Long): Flow<List<MealPlanWithItems>> = 
        mealPlanDao.getMealPlansWithItems(startDate, endDate)
    
    /**
     * 특정 날짜의 식단 계획을 가져옵니다.
     */
    suspend fun getMealPlanForDate(date: Long): MealPlanWithItems? = 
        mealPlanDao.getMealPlanForDate(date)
    
    /**
     * 식단 계획을 저장합니다.
     */
    suspend fun insertMealPlan(mealPlan: MealPlan): Long = mealPlanDao.insertMealPlan(mealPlan)
    
    /**
     * 식단 계획을 업데이트합니다.
     */
    suspend fun updateMealPlan(mealPlan: MealPlan) = mealPlanDao.updateMealPlan(mealPlan)
    
    /**
     * 식단 계획을 삭제합니다.
     */
    suspend fun deleteMealPlan(mealPlan: MealPlan) = mealPlanDao.deleteMealPlan(mealPlan)
    
    /**
     * 식단 계획 항목을 저장합니다.
     */
    suspend fun insertMealPlanItem(item: MealPlanItem): Long = mealPlanDao.insertMealPlanItem(item)
    
    /**
     * 식단 계획 항목을 업데이트합니다.
     */
    suspend fun updateMealPlanItem(item: MealPlanItem) = mealPlanDao.updateMealPlanItem(item)
    
    /**
     * 식단 계획 항목을 삭제합니다.
     */
    suspend fun deleteMealPlanItem(item: MealPlanItem) = mealPlanDao.deleteMealPlanItem(item)
    
    /**
     * 식단 계획의 항목 목록을 가져옵니다.
     */
    fun getMealPlanItems(mealPlanId: Long): Flow<List<MealPlanItem>> = 
        mealPlanDao.getMealPlanItems(mealPlanId)
    
    /**
     * 특정 날짜의 식단 계획을 생성하거나 업데이트합니다.
     */
    suspend fun createOrUpdateMealPlanForDate(date: Long, items: List<MealPlanItem>): Long = 
        mealPlanDao.createOrUpdateMealPlanForDate(date, items)
    
    /**
     * 주간 식단 계획을 생성합니다.
     */
    suspend fun createWeeklyMealPlan(
        startDate: Long,
        recipeIds: Map<Long, List<Pair<String, Int>>> // 날짜 -> (식사 유형, 레시피 ID) 목록
    ): List<Long> {
        val mealPlanIds = mutableListOf<Long>()
        
        for ((date, mealInfoList) in recipeIds) {
            val mealPlanItems = mealInfoList.map { (mealType, recipeId) ->
                MealPlanItem(
                    mealPlanId = 0, // 임시값, createOrUpdateMealPlanForDate에서 설정됨
                    recipeId = recipeId.toLong(),
                    mealType = mealType,
                    servings = 2, // 기본값
                    notes = null
                )
            }
            
            val mealPlanId = createOrUpdateMealPlanForDate(date, mealPlanItems)
            mealPlanIds.add(mealPlanId)
        }
        
        return mealPlanIds
    }
    
    /**
     * 레시피를 식단 계획에 추가합니다.
     */
    suspend fun addRecipeToMealPlan(
        date: Long,
        recipeId: Long,
        mealType: String,
        servings: Int = 2,
        notes: String? = null
    ): Long {
        // 기존 식단 계획 확인
        val existingMealPlan = mealPlanDao.getMealPlanByDate(date)
        val mealPlanId = existingMealPlan?.id ?: insertMealPlan(MealPlan(date = date))
        
        // 새 식단 항목 추가
        val mealPlanItem = MealPlanItem(
            mealPlanId = mealPlanId,
            recipeId = recipeId,
            mealType = mealType,
            servings = servings,
            notes = notes
        )
        
        return insertMealPlanItem(mealPlanItem)
    }
}
