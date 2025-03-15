package com.smartkitchen.assistant.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartkitchen.assistant.data.model.RecipeSearchResponse
import com.smartkitchen.assistant.data.repository.RecipeSearchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 레시피 검색 화면을 위한 ViewModel
 * 외부 API를 통한 레시피 검색 및 상세 정보 조회 기능을 관리합니다.
 */
@HiltViewModel
class RecipeSearchViewModel @Inject constructor(
    private val recipeSearchRepository: RecipeSearchRepository
) : ViewModel() {
    // 검색 결과
    private val _searchResults = MutableStateFlow<List<RecipeSearchResponse.Recipe>>(emptyList())
    val searchResults: StateFlow<List<RecipeSearchResponse.Recipe>> = _searchResults
    
    // 선택된 레시피 상세 정보
    private val _selectedRecipe = MutableStateFlow<RecipeSearchResponse.RecipeInformation?>(null)
    val selectedRecipe: StateFlow<RecipeSearchResponse.RecipeInformation?> = _selectedRecipe
    
    // 레시피 영양 정보
    private val _recipeNutrition = MutableStateFlow<RecipeSearchResponse.RecipeNutrition?>(null)
    val recipeNutrition: StateFlow<RecipeSearchResponse.RecipeNutrition?> = _recipeNutrition
    
    // 비슷한 레시피 목록
    private val _similarRecipes = MutableStateFlow<List<RecipeSearchResponse.SimilarRecipe>>(emptyList())
    val similarRecipes: StateFlow<List<RecipeSearchResponse.SimilarRecipe>> = _similarRecipes
    
    // 대체 재료 제안
    private val _ingredientSubstitutes = MutableStateFlow<Map<String, String>>(emptyMap())
    val ingredientSubstitutes: StateFlow<Map<String, String>> = _ingredientSubstitutes
    
    // 로딩 상태
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    
    // 오류 메시지
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage
    
    // 검색 쿼리
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery
    
    // 검색 필터
    private val _cuisine = MutableStateFlow<String?>(null)
    val cuisine: StateFlow<String?> = _cuisine
    
    private val _diet = MutableStateFlow<String?>(null)
    val diet: StateFlow<String?> = _diet
    
    private val _intolerances = MutableStateFlow<String?>(null)
    val intolerances: StateFlow<String?> = _intolerances
    
    /**
     * 레시피를 검색합니다.
     */
    fun searchRecipes(
        query: String,
        cuisine: String? = _cuisine.value,
        diet: String? = _diet.value,
        intolerances: String? = _intolerances.value,
        includeIngredients: String? = null,
        excludeIngredients: String? = null,
        type: String? = null,
        number: Int = 10,
        offset: Int = 0
    ) {
        _searchQuery.value = query
        _cuisine.value = cuisine
        _diet.value = diet
        _intolerances.value = intolerances
        
        viewModelScope.launch {
            _isLoading.value = true
            try {
                recipeSearchRepository.searchRecipes(
                    query = query,
                    cuisine = cuisine,
                    diet = diet,
                    intolerances = intolerances,
                    includeIngredients = includeIngredients,
                    excludeIngredients = excludeIngredients,
                    type = type,
                    number = number,
                    offset = offset
                ).collect { result ->
                    result.onSuccess { response ->
                        _searchResults.value = response.results
                    }.onFailure { error ->
                        _errorMessage.value = "레시피 검색 중 오류가 발생했습니다: ${error.message}"
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = "레시피 검색 중 오류가 발생했습니다: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * 레시피 상세 정보를 가져옵니다.
     */
    fun getRecipeInformation(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                recipeSearchRepository.getRecipeInformation(id).collect { result ->
                    result.onSuccess { response ->
                        _selectedRecipe.value = response
                    }.onFailure { error ->
                        _errorMessage.value = "레시피 상세 정보를 가져오는 중 오류가 발생했습니다: ${error.message}"
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = "레시피 상세 정보를 가져오는 중 오류가 발생했습니다: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * 레시피 영양 정보를 가져옵니다.
     */
    fun getRecipeNutrition(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                recipeSearchRepository.getRecipeNutrition(id).collect { result ->
                    result.onSuccess { response ->
                        _recipeNutrition.value = response
                    }.onFailure { error ->
                        _errorMessage.value = "레시피 영양 정보를 가져오는 중 오류가 발생했습니다: ${error.message}"
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = "레시피 영양 정보를 가져오는 중 오류가 발생했습니다: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * 비슷한 레시피를 검색합니다.
     */
    fun getSimilarRecipes(id: Int, number: Int = 5) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                recipeSearchRepository.getSimilarRecipes(id, number).collect { result ->
                    result.onSuccess { response ->
                        _similarRecipes.value = response
                    }.onFailure { error ->
                        _errorMessage.value = "비슷한 레시피를 검색하는 중 오류가 발생했습니다: ${error.message}"
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = "비슷한 레시피를 검색하는 중 오류가 발생했습니다: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * 레시피를 앱 내부 데이터베이스로 가져옵니다.
     */
    fun importRecipeToDatabase() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _selectedRecipe.value?.let { recipe ->
                    recipeSearchRepository.importRecipeToDatabase(recipe)
                    // 성공 메시지 또는 처리 로직 추가
                }
            } catch (e: Exception) {
                _errorMessage.value = "레시피를 가져오는 중 오류가 발생했습니다: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * 재료의 대체품을 제안합니다.
     */
    fun suggestIngredientSubstitutes(ingredient: String) {
        viewModelScope.launch {
            try {
                val substitutes = recipeSearchRepository.suggestIngredientSubstitutes(ingredient)
                _ingredientSubstitutes.value = substitutes
            } catch (e: Exception) {
                _errorMessage.value = "대체 재료를 제안하는 중 오류가 발생했습니다: ${e.message}"
            }
        }
    }
    
    /**
     * 검색 필터를 설정합니다.
     */
    fun setSearchFilters(cuisine: String?, diet: String?, intolerances: String?) {
        _cuisine.value = cuisine
        _diet.value = diet
        _intolerances.value = intolerances
    }
    
    /**
     * 오류 메시지를 초기화합니다.
     */
    fun clearErrorMessage() {
        _errorMessage.value = null
    }
}
