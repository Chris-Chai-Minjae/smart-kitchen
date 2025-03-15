package com.smartkitchen.assistant.data.repository

import com.smartkitchen.assistant.data.local.RecipeDao
import com.smartkitchen.assistant.data.local.RecipeWithDetails
import com.smartkitchen.assistant.data.model.Recipe
import com.smartkitchen.assistant.data.model.RecipeIngredient
import com.smartkitchen.assistant.data.model.RecipeStep
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 레시피 저장소
 * 레시피 관련 데이터 작업을 처리하고 데이터 소스를 추상화합니다.
 */
@Singleton
class RecipeRepository @Inject constructor(
    private val recipeDao: RecipeDao
) {
    /**
     * 모든 레시피 목록을 가져옵니다.
     */
    fun getAllRecipes(): Flow<List<Recipe>> = recipeDao.getAllRecipes()
    
    /**
     * 즐겨찾기 레시피 목록을 가져옵니다.
     */
    fun getFavoriteRecipes(): Flow<List<Recipe>> = recipeDao.getFavoriteRecipes()
    
    /**
     * 레시피 ID로 레시피를 가져옵니다.
     */
    suspend fun getRecipeById(recipeId: Long): Recipe? = recipeDao.getRecipeById(recipeId)
    
    /**
     * 레시피 이름으로 레시피를 검색합니다.
     */
    fun searchRecipes(query: String): Flow<List<Recipe>> = recipeDao.searchRecipes(query)
    
    /**
     * 레시피와 관련된 재료 및 단계를 함께 가져옵니다.
     */
    fun getRecipeWithDetails(recipeId: Long): Flow<RecipeWithDetails> = 
        recipeDao.getRecipeWithIngredientsAndSteps(recipeId)
    
    /**
     * 레시피의 단계 목록을 가져옵니다.
     */
    fun getRecipeSteps(recipeId: Long): Flow<List<RecipeStep>> = recipeDao.getRecipeSteps(recipeId)
    
    /**
     * 레시피의 재료 목록을 가져옵니다.
     */
    fun getRecipeIngredients(recipeId: Long): Flow<List<RecipeIngredient>> = 
        recipeDao.getRecipeIngredients(recipeId)
    
    /**
     * 레시피를 저장합니다.
     */
    suspend fun insertRecipe(recipe: Recipe): Long = recipeDao.insertRecipe(recipe)
    
    /**
     * 레시피를 업데이트합니다.
     */
    suspend fun updateRecipe(recipe: Recipe) = recipeDao.updateRecipe(recipe)
    
    /**
     * 레시피를 삭제합니다.
     */
    suspend fun deleteRecipe(recipe: Recipe) = recipeDao.deleteRecipe(recipe)
    
    /**
     * 레시피 단계를 저장합니다.
     */
    suspend fun insertRecipeStep(step: RecipeStep): Long = recipeDao.insertRecipeStep(step)
    
    /**
     * 레시피 단계를 업데이트합니다.
     */
    suspend fun updateRecipeStep(step: RecipeStep) = recipeDao.updateRecipeStep(step)
    
    /**
     * 레시피 단계를 삭제합니다.
     */
    suspend fun deleteRecipeStep(step: RecipeStep) = recipeDao.deleteRecipeStep(step)
    
    /**
     * 레시피 재료를 저장합니다.
     */
    suspend fun insertRecipeIngredient(recipeIngredient: RecipeIngredient): Long = 
        recipeDao.insertRecipeIngredient(recipeIngredient)
    
    /**
     * 레시피 재료를 업데이트합니다.
     */
    suspend fun updateRecipeIngredient(recipeIngredient: RecipeIngredient) = 
        recipeDao.updateRecipeIngredient(recipeIngredient)
    
    /**
     * 레시피 재료를 삭제합니다.
     */
    suspend fun deleteRecipeIngredient(recipeIngredient: RecipeIngredient) = 
        recipeDao.deleteRecipeIngredient(recipeIngredient)
    
    /**
     * 레시피와 관련 재료, 단계를 함께 저장합니다.
     */
    suspend fun insertRecipeWithDetails(
        recipe: Recipe,
        ingredients: List<RecipeIngredient>,
        steps: List<RecipeStep>
    ): Long = recipeDao.insertRecipeWithIngredientsAndSteps(recipe, ingredients, steps)
    
    /**
     * 레시피와 관련 재료, 단계를 함께 업데이트합니다.
     */
    suspend fun updateRecipeWithDetails(
        recipe: Recipe,
        ingredients: List<RecipeIngredient>,
        steps: List<RecipeStep>
    ) = recipeDao.updateRecipeWithIngredientsAndSteps(recipe, ingredients, steps)
}
