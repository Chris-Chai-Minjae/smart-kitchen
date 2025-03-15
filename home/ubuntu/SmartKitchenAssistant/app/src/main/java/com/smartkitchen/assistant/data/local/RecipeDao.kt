package com.smartkitchen.assistant.data.local

import androidx.room.*
import com.smartkitchen.assistant.data.model.Recipe
import com.smartkitchen.assistant.data.model.RecipeIngredient
import com.smartkitchen.assistant.data.model.RecipeStep
import kotlinx.coroutines.flow.Flow

/**
 * 레시피 데이터 접근 객체 (DAO)
 * 레시피 관련 데이터베이스 작업을 처리합니다.
 */
@Dao
interface RecipeDao {
    @Insert
    suspend fun insertRecipe(recipe: Recipe): Long
    
    @Update
    suspend fun updateRecipe(recipe: Recipe)
    
    @Delete
    suspend fun deleteRecipe(recipe: Recipe)
    
    @Query("SELECT * FROM recipes ORDER BY name ASC")
    fun getAllRecipes(): Flow<List<Recipe>>
    
    @Query("SELECT * FROM recipes WHERE isFavorite = 1 ORDER BY name ASC")
    fun getFavoriteRecipes(): Flow<List<Recipe>>
    
    @Query("SELECT * FROM recipes WHERE id = :recipeId")
    suspend fun getRecipeById(recipeId: Long): Recipe?
    
    @Query("SELECT * FROM recipes WHERE name LIKE '%' || :query || '%'")
    fun searchRecipes(query: String): Flow<List<Recipe>>
    
    @Insert
    suspend fun insertRecipeStep(step: RecipeStep): Long
    
    @Update
    suspend fun updateRecipeStep(step: RecipeStep)
    
    @Delete
    suspend fun deleteRecipeStep(step: RecipeStep)
    
    @Query("SELECT * FROM recipe_steps WHERE recipeId = :recipeId ORDER BY stepNumber ASC")
    fun getRecipeSteps(recipeId: Long): Flow<List<RecipeStep>>
    
    @Insert
    suspend fun insertRecipeIngredient(recipeIngredient: RecipeIngredient): Long
    
    @Update
    suspend fun updateRecipeIngredient(recipeIngredient: RecipeIngredient)
    
    @Delete
    suspend fun deleteRecipeIngredient(recipeIngredient: RecipeIngredient)
    
    @Query("SELECT * FROM recipe_ingredients WHERE recipeId = :recipeId")
    fun getRecipeIngredients(recipeId: Long): Flow<List<RecipeIngredient>>
    
    @Transaction
    @Query("SELECT * FROM recipes WHERE id = :recipeId")
    fun getRecipeWithIngredientsAndSteps(recipeId: Long): Flow<RecipeWithDetails>
    
    @Transaction
    suspend fun insertRecipeWithIngredientsAndSteps(
        recipe: Recipe,
        ingredients: List<RecipeIngredient>,
        steps: List<RecipeStep>
    ): Long {
        val recipeId = insertRecipe(recipe)
        
        ingredients.forEach { ingredient ->
            insertRecipeIngredient(ingredient.copy(recipeId = recipeId))
        }
        
        steps.forEach { step ->
            insertRecipeStep(step.copy(recipeId = recipeId))
        }
        
        return recipeId
    }
    
    @Transaction
    suspend fun updateRecipeWithIngredientsAndSteps(
        recipe: Recipe,
        ingredients: List<RecipeIngredient>,
        steps: List<RecipeStep>
    ) {
        updateRecipe(recipe)
        
        // 기존 재료 및 단계 삭제 (CASCADE로 자동 삭제되지만 명시적으로 처리)
        deleteRecipeIngredientsForRecipe(recipe.id)
        deleteRecipeStepsForRecipe(recipe.id)
        
        // 새 재료 및 단계 추가
        ingredients.forEach { ingredient ->
            insertRecipeIngredient(ingredient.copy(recipeId = recipe.id))
        }
        
        steps.forEach { step ->
            insertRecipeStep(step.copy(recipeId = recipe.id))
        }
    }
    
    @Query("DELETE FROM recipe_ingredients WHERE recipeId = :recipeId")
    suspend fun deleteRecipeIngredientsForRecipe(recipeId: Long)
    
    @Query("DELETE FROM recipe_steps WHERE recipeId = :recipeId")
    suspend fun deleteRecipeStepsForRecipe(recipeId: Long)
}

/**
 * 레시피 상세 정보를 포함하는 데이터 클래스
 */
data class RecipeWithDetails(
    @Embedded val recipe: Recipe,
    @Relation(
        parentColumn = "id",
        entityColumn = "recipeId"
    )
    val steps: List<RecipeStep>,
    @Relation(
        parentColumn = "id",
        entityColumn = "recipeId"
    )
    val ingredients: List<RecipeIngredient>
)
