package com.smartkitchen.assistant.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartkitchen.assistant.data.model.NutritionResponse
import com.smartkitchen.assistant.data.repository.NutritionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 영양 정보 화면을 위한 ViewModel
 * 영양 정보 검색 및 분석 기능을 관리합니다.
 */
@HiltViewModel
class NutritionViewModel @Inject constructor(
    private val nutritionRepository: NutritionRepository
) : ViewModel() {
    // 검색 결과
    private val _searchResults = MutableStateFlow<List<NutritionResponse.Food>>(emptyList())
    val searchResults: StateFlow<List<NutritionResponse.Food>> = _searchResults
    
    // 선택된 식품 상세 정보
    private val _selectedFoodDetails = MutableStateFlow<NutritionResponse.FoodDetailsResponse?>(null)
    val selectedFoodDetails: StateFlow<NutritionResponse.FoodDetailsResponse?> = _selectedFoodDetails
    
    // 분석된 영양 정보
    private val _analyzedNutrition = MutableStateFlow<Map<String, String>>(emptyMap())
    val analyzedNutrition: StateFlow<Map<String, String>> = _analyzedNutrition
    
    // 건강 점수
    private val _healthScore = MutableStateFlow(0)
    val healthScore: StateFlow<Int> = _healthScore
    
    // 로딩 상태
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    
    // 오류 메시지
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage
    
    // 검색 쿼리
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery
    
    /**
     * 식품을 검색합니다.
     */
    fun searchFoods(query: String, pageSize: Int = 25, pageNumber: Int = 1) {
        _searchQuery.value = query
        viewModelScope.launch {
            _isLoading.value = true
            try {
                nutritionRepository.searchFoods(query, pageSize, pageNumber).collect { result ->
                    result.onSuccess { response ->
                        _searchResults.value = response.foods
                    }.onFailure { error ->
                        _errorMessage.value = "식품 검색 중 오류가 발생했습니다: ${error.message}"
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = "식품 검색 중 오류가 발생했습니다: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * 식품 상세 정보를 가져옵니다.
     */
    fun getFoodDetails(fdcId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                nutritionRepository.getFoodDetails(fdcId).collect { result ->
                    result.onSuccess { response ->
                        _selectedFoodDetails.value = response
                        
                        // 영양 정보 분석
                        _analyzedNutrition.value = nutritionRepository.analyzeFoodNutrition(response)
                        
                        // 건강 점수 계산
                        _healthScore.value = nutritionRepository.calculateHealthScore(response)
                    }.onFailure { error ->
                        _errorMessage.value = "식품 상세 정보를 가져오는 중 오류가 발생했습니다: ${error.message}"
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = "식품 상세 정보를 가져오는 중 오류가 발생했습니다: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * 영양소 목록을 가져옵니다.
     */
    fun getNutrientsList() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                nutritionRepository.getNutrientsList().collect { result ->
                    result.onSuccess { response ->
                        // 영양소 목록 처리
                    }.onFailure { error ->
                        _errorMessage.value = "영양소 목록을 가져오는 중 오류가 발생했습니다: ${error.message}"
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = "영양소 목록을 가져오는 중 오류가 발생했습니다: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * 오류 메시지를 초기화합니다.
     */
    fun clearErrorMessage() {
        _errorMessage.value = null
    }
}
