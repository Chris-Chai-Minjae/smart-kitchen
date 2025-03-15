package com.smartkitchen.assistant.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartkitchen.assistant.data.local.MealPlanWithItems
import com.smartkitchen.assistant.data.model.MealPlan
import com.smartkitchen.assistant.data.model.MealPlanItem
import com.smartkitchen.assistant.data.repository.MealPlanRepository
import com.smartkitchen.assistant.data.repository.RecipeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * 식단 계획 화면을 위한 ViewModel
 * 식단 계획 관련 데이터를 관리하고 UI와 비즈니스 로직을 연결합니다.
 */
@HiltViewModel
class MealPlanViewModel @Inject constructor(
    private val mealPlanRepository: MealPlanRepository,
    private val recipeRepository: RecipeRepository
) : ViewModel() {
    // 주간 식단 계획
    private val _weeklyMealPlans = MutableStateFlow<List<MealPlanWithItems>>(emptyList())
    val weeklyMealPlans: StateFlow<List<MealPlanWithItems>> = _weeklyMealPlans
    
    // 선택된 날짜의 식단 계획
    private val _selectedDayMealPlan = MutableStateFlow<MealPlanWithItems?>(null)
    val selectedDayMealPlan: StateFlow<MealPlanWithItems?> = _selectedDayMealPlan
    
    // 선택된 날짜
    private val _selectedDate = MutableStateFlow(System.currentTimeMillis())
    val selectedDate: StateFlow<Long> = _selectedDate
    
    // 로딩 상태
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    
    // 오류 메시지
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage
    
    init {
        loadWeeklyMealPlans()
    }
    
    /**
     * 주간 식단 계획을 로드합니다.
     */
    fun loadWeeklyMealPlans() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // 이번 주 시작일과 종료일 계산
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = _selectedDate.value
                calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
                val startDate = calendar.timeInMillis
                
                calendar.add(Calendar.DAY_OF_YEAR, 6)
                val endDate = calendar.timeInMillis
                
                mealPlanRepository.getMealPlansWithItems(startDate, endDate).collect { mealPlans ->
                    _weeklyMealPlans.value = mealPlans
                }
            } catch (e: Exception) {
                _errorMessage.value = "주간 식단 계획을 로드하는 중 오류가 발생했습니다: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * 선택된 날짜의 식단 계획을 로드합니다.
     */
    fun loadSelectedDayMealPlan(date: Long = _selectedDate.value) {
        _selectedDate.value = date
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val mealPlan = mealPlanRepository.getMealPlanForDate(date)
                _selectedDayMealPlan.value = mealPlan
            } catch (e: Exception) {
                _errorMessage.value = "선택된 날짜의 식단 계획을 로드하는 중 오류가 발생했습니다: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * 식단 계획 항목을 저장합니다.
     */
    fun saveMealPlanItem(item: MealPlanItem) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                if (item.id == 0L) {
                    // 새 항목 추가
                    val mealPlanId = item.mealPlanId
                    if (mealPlanId == 0L) {
                        // 새 식단 계획 생성
                        val mealPlan = MealPlan(date = _selectedDate.value)
                        val newMealPlanId = mealPlanRepository.insertMealPlan(mealPlan)
                        mealPlanRepository.insertMealPlanItem(item.copy(mealPlanId = newMealPlanId))
                    } else {
                        // 기존 식단 계획에 항목 추가
                        mealPlanRepository.insertMealPlanItem(item)
                    }
                } else {
                    // 기존 항목 업데이트
                    mealPlanRepository.updateMealPlanItem(item)
                }
                
                loadSelectedDayMealPlan()
                loadWeeklyMealPlans()
            } catch (e: Exception) {
                _errorMessage.value = "식단 계획 항목을 저장하는 중 오류가 발생했습니다: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * 식단 계획 항목을 삭제합니다.
     */
    fun deleteMealPlanItem(item: MealPlanItem) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                mealPlanRepository.deleteMealPlanItem(item)
                loadSelectedDayMealPlan()
                loadWeeklyMealPlans()
            } catch (e: Exception) {
                _errorMessage.value = "식단 계획 항목을 삭제하는 중 오류가 발생했습니다: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * 레시피를 식단 계획에 추가합니다.
     */
    fun addRecipeToMealPlan(
        recipeId: Long,
        mealType: String,
        servings: Int = 2,
        notes: String? = null
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                mealPlanRepository.addRecipeToMealPlan(
                    date = _selectedDate.value,
                    recipeId = recipeId,
                    mealType = mealType,
                    servings = servings,
                    notes = notes
                )
                
                loadSelectedDayMealPlan()
                loadWeeklyMealPlans()
            } catch (e: Exception) {
                _errorMessage.value = "레시피를 식단 계획에 추가하는 중 오류가 발생했습니다: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * 주간 식단 계획을 생성합니다.
     */
    fun createWeeklyMealPlan(recipeIds: Map<Long, List<Pair<String, Int>>>) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // 이번 주 시작일 계산
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = _selectedDate.value
                calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
                val startDate = calendar.timeInMillis
                
                mealPlanRepository.createWeeklyMealPlan(startDate, recipeIds)
                loadWeeklyMealPlans()
            } catch (e: Exception) {
                _errorMessage.value = "주간 식단 계획을 생성하는 중 오류가 발생했습니다: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * 이전 주로 이동합니다.
     */
    fun navigateToPreviousWeek() {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = _selectedDate.value
        calendar.add(Calendar.WEEK_OF_YEAR, -1)
        _selectedDate.value = calendar.timeInMillis
        loadWeeklyMealPlans()
    }
    
    /**
     * 다음 주로 이동합니다.
     */
    fun navigateToNextWeek() {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = _selectedDate.value
        calendar.add(Calendar.WEEK_OF_YEAR, 1)
        _selectedDate.value = calendar.timeInMillis
        loadWeeklyMealPlans()
    }
    
    /**
     * 오류 메시지를 초기화합니다.
     */
    fun clearErrorMessage() {
        _errorMessage.value = null
    }
}
