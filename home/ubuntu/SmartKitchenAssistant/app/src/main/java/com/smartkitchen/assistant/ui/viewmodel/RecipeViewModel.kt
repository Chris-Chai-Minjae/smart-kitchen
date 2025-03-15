package com.smartkitchen.assistant.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartkitchen.assistant.data.local.RecipeWithDetails
import com.smartkitchen.assistant.data.model.Recipe
import com.smartkitchen.assistant.data.model.RecipeIngredient
import com.smartkitchen.assistant.data.model.RecipeStep
import com.smartkitchen.assistant.data.repository.RecipeRepository
import com.smartkitchen.assistant.data.repository.ShoppingListRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 레시피 화면을 위한 ViewModel
 * 레시피 관련 데이터를 관리하고 UI와 비즈니스 로직을 연결합니다.
 */
@HiltViewModel
class RecipeViewModel @Inject constructor(
    private val recipeRepository: RecipeRepository,
    private val shoppingListRepository: ShoppingListRepository
) : ViewModel() {
    // 모든 레시피 목록
    private val _recipes = MutableStateFlow<List<Recipe>>(emptyList())
    val recipes: StateFlow<List<Recipe>> = _recipes
    
    // 현재 선택된 레시피
    private val _selectedRecipe = MutableStateFlow<RecipeWithDetails?>(null)
    val selectedRecipe: StateFlow<RecipeWithDetails?> = _selectedRecipe
    
    // 로딩 상태
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    
    // 오류 메시지
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage
    
    // 검색 쿼리
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery
    
    // 필터 설정
    private val _filterCategory = MutableStateFlow<String?>(null)
    val filterCategory: StateFlow<String?> = _filterCategory
    
    init {
        loadRecipes()
    }
    
    /**
     * 모든 레시피를 로드합니다.
     */
    fun loadRecipes() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                recipeRepository.getAllRecipes().collect { recipeList ->
                    _recipes.value = recipeList
                }
            } catch (e: Exception) {
                _errorMessage.value = "레시피를 로드하는 중 오류가 발생했습니다: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * 즐겨찾기 레시피를 로드합니다.
     */
    fun loadFavoriteRecipes() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                recipeRepository.getFavoriteRecipes().collect { recipeList ->
                    _recipes.value = recipeList
                }
            } catch (e: Exception) {
                _errorMessage.value = "즐겨찾기 레시피를 로드하는 중 오류가 발생했습니다: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * 레시피 ID로 레시피 상세 정보를 로드합니다.
     */
    fun loadRecipeDetails(recipeId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                recipeRepository.getRecipeWithDetails(recipeId).collect { recipeWithDetails ->
                    _selectedRecipe.value = recipeWithDetails
                }
            } catch (e: Exception) {
                _errorMessage.value = "레시피 상세 정보를 로드하는 중 오류가 발생했습니다: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * 레시피를 검색합니다.
     */
    fun searchRecipes(query: String) {
        _searchQuery.value = query
        viewModelScope.launch {
            _isLoading.value = true
            try {
                recipeRepository.searchRecipes(query).collect { recipeList ->
                    _recipes.value = recipeList
                }
            } catch (e: Exception) {
                _errorMessage.value = "레시피 검색 중 오류가 발생했습니다: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * 레시피 카테고리로 필터링합니다.
     */
    fun filterByCategory(category: String?) {
        _filterCategory.value = category
        // 실제 필터링 로직은 Repository에 구현해야 함
    }
    
    /**
     * 레시피를 저장합니다.
     */
    fun saveRecipe(
        recipe: Recipe,
        ingredients: List<RecipeIngredient>,
        steps: List<RecipeStep>
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val recipeId = if (recipe.id == 0L) {
                    // 새 레시피 추가
                    recipeRepository.insertRecipeWithDetails(recipe, ingredients, steps)
                } else {
                    // 기존 레시피 업데이트
                    recipeRepository.updateRecipeWithDetails(recipe, ingredients, steps)
                    recipe.id
                }
                
                // 저장 후 상세 정보 로드
                loadRecipeDetails(recipeId)
            } catch (e: Exception) {
                _errorMessage.value = "레시피를 저장하는 중 오류가 발생했습니다: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * 레시피를 삭제합니다.
     */
    fun deleteRecipe(recipe: Recipe) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                recipeRepository.deleteRecipe(recipe)
                _selectedRecipe.value = null
                loadRecipes()
            } catch (e: Exception) {
                _errorMessage.value = "레시피를 삭제하는 중 오류가 발생했습니다: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * 레시피 즐겨찾기 상태를 토글합니다.
     */
    fun toggleFavorite(recipe: Recipe) {
        viewModelScope.launch {
            try {
                val updatedRecipe = recipe.copy(isFavorite = !recipe.isFavorite)
                recipeRepository.updateRecipe(updatedRecipe)
                
                // 현재 선택된 레시피인 경우 업데이트
                _selectedRecipe.value?.let { selected ->
                    if (selected.recipe.id == recipe.id) {
                        loadRecipeDetails(recipe.id)
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = "즐겨찾기 상태를 변경하는 중 오류가 발생했습니다: ${e.message}"
            }
        }
    }
    
    /**
     * 레시피 재료를 쇼핑 목록에 추가합니다.
     */
    fun addIngredientsToShoppingList(recipeId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // 레시피 재료 가져오기
                val ingredients = mutableListOf<RecipeIngredient>()
                recipeRepository.getRecipeIngredients(recipeId).collect { recipeIngredients ->
                    ingredients.addAll(recipeIngredients)
                }
                
                // 재료 ID를 이름으로 매핑
                val ingredientNames = mutableMapOf<Long, String>()
                for (ingredient in ingredients) {
                    val ingredientEntity = recipeRepository.getIngredientById(ingredient.ingredientId)
                    ingredientEntity?.let {
                        ingredientNames[it.id] = it.name
                    }
                }
                
                // 쇼핑 목록에 추가
                shoppingListRepository.addRecipeIngredientsToShoppingList(ingredients, ingredientNames)
            } catch (e: Exception) {
                _errorMessage.value = "쇼핑 목록에 추가하는 중 오류가 발생했습니다: ${e.message}"
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
