package com.smartkitchen.assistant.data.repository

import android.content.Context
import com.smartkitchen.assistant.data.local.RecipeDao
import com.smartkitchen.assistant.data.model.Ingredient
import com.smartkitchen.assistant.data.model.RecipeImport
import com.smartkitchen.assistant.data.model.RecipeIngredient
import com.smartkitchen.assistant.data.model.RecipeStep
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 레시피 가져오기 저장소
 * CSV 파일에서 레시피 데이터를 가져와 데이터베이스에 저장하는 기능을 제공합니다.
 */
@Singleton
class RecipeImportRepository @Inject constructor(
    private val recipeDao: RecipeDao
) {
    /**
     * CSV 파일에서 레시피 데이터를 가져와 데이터베이스에 저장합니다.
     */
    suspend fun importRecipesFromCsv(context: Context, fileName: String): Int = withContext(Dispatchers.IO) {
        val recipes = parseRecipesCsv(context, fileName)
        val ingredientMap = getIngredientMap()
        var importedCount = 0
        
        recipes.forEach { recipeImport ->
            try {
                // 레시피 엔티티로 변환
                val recipe = recipeImport.toRecipe()
                
                // 레시피 저장
                val recipeId = recipeDao.insertRecipe(recipe)
                
                // 재료 파싱 및 저장
                val ingredients = recipeImport.parseIngredients(recipeId, ingredientMap)
                ingredients.forEach { ingredient ->
                    if (ingredient.ingredientId == -1L) {
                        // 새 재료 생성
                        val newIngredientId = recipeDao.insertIngredient(
                            Ingredient(name = ingredient.ingredientId.toString(), category = "기타")
                        )
                        recipeDao.insertRecipeIngredient(
                            ingredient.copy(ingredientId = newIngredientId)
                        )
                    } else {
                        recipeDao.insertRecipeIngredient(ingredient)
                    }
                }
                
                // 레시피 단계 생성 및 저장
                val steps = recipeImport.createRecipeSteps(recipeId)
                steps.forEach { step ->
                    recipeDao.insertRecipeStep(step)
                }
                
                importedCount++
            } catch (e: Exception) {
                // 오류 로깅 (실제 앱에서는 로깅 라이브러리 사용)
                e.printStackTrace()
            }
        }
        
        importedCount
    }
    
    /**
     * CSV 파일을 파싱하여 RecipeImport 객체 목록으로 변환합니다.
     */
    private fun parseRecipesCsv(context: Context, fileName: String): List<RecipeImport> {
        val recipes = mutableListOf<RecipeImport>()
        val inputStream = context.assets.open(fileName)
        val reader = BufferedReader(InputStreamReader(inputStream))
        
        // 헤더 건너뛰기
        var headerLine = reader.readLine()
        val headers = headerLine.split(",")
        
        // 데이터 파싱
        var line: String?
        while (reader.readLine().also { line = it } != null) {
            line?.let {
                val values = parseCsvLine(it)
                if (values.size >= headers.size) {
                    val recipe = RecipeImport(
                        recipeId = values[0],
                        title = values[1],
                        cookingName = values[2],
                        registerId = values[3],
                        registerName = values[4],
                        inquiryCount = values[5].toIntOrNull() ?: 0,
                        recommendCount = values[6].toIntOrNull() ?: 0,
                        scrapCount = values[7].toIntOrNull() ?: 0,
                        cookingMethod = values[8],
                        cookingStatus = values[9],
                        cookingMaterial = values[10],
                        cookingType = values[11],
                        description = values[12],
                        ingredients = values[13],
                        servings = values[14],
                        difficulty = values[15],
                        cookingTime = values[16],
                        registerDate = values[17],
                        imageUrl = values[18]
                    )
                    recipes.add(recipe)
                }
            }
        }
        
        reader.close()
        return recipes
    }
    
    /**
     * CSV 라인을 파싱하여 필드 값 목록으로 변환합니다.
     * 쉼표로 구분되지만 따옴표 내의 쉼표는 무시합니다.
     */
    private fun parseCsvLine(line: String): List<String> {
        val values = mutableListOf<String>()
        var currentValue = StringBuilder()
        var inQuotes = false
        
        for (i in line.indices) {
            val c = line[i]
            when {
                c == '\"' -> inQuotes = !inQuotes
                c == ',' && !inQuotes -> {
                    values.add(currentValue.toString())
                    currentValue = StringBuilder()
                }
                else -> currentValue.append(c)
            }
        }
        
        values.add(currentValue.toString())
        return values
    }
    
    /**
     * 모든 재료와 ID를 매핑한 맵을 가져옵니다.
     */
    private suspend fun getIngredientMap(): Map<String, Long> {
        val ingredients = recipeDao.getAllIngredientsSync()
        return ingredients.associateBy({ it.name }, { it.id })
    }
    
    /**
     * 외부 파일에서 레시피 데이터를 가져와 데이터베이스에 저장합니다.
     */
    suspend fun importRecipesFromExternalFile(filePath: String): Int = withContext(Dispatchers.IO) {
        val recipes = parseRecipesFromExternalFile(filePath)
        val ingredientMap = getIngredientMap()
        var importedCount = 0
        
        recipes.forEach { recipeImport ->
            try {
                // 레시피 엔티티로 변환
                val recipe = recipeImport.toRecipe()
                
                // 레시피 저장
                val recipeId = recipeDao.insertRecipe(recipe)
                
                // 재료 파싱 및 저장
                val ingredients = recipeImport.parseIngredients(recipeId, ingredientMap)
                ingredients.forEach { ingredient ->
                    if (ingredient.ingredientId == -1L) {
                        // 새 재료 생성
                        val newIngredientId = recipeDao.insertIngredient(
                            Ingredient(name = ingredient.ingredientId.toString(), category = "기타")
                        )
                        recipeDao.insertRecipeIngredient(
                            ingredient.copy(ingredientId = newIngredientId)
                        )
                    } else {
                        recipeDao.insertRecipeIngredient(ingredient)
                    }
                }
                
                // 레시피 단계 생성 및 저장
                val steps = recipeImport.createRecipeSteps(recipeId)
                steps.forEach { step ->
                    recipeDao.insertRecipeStep(step)
                }
                
                importedCount++
            } catch (e: Exception) {
                // 오류 로깅 (실제 앱에서는 로깅 라이브러리 사용)
                e.printStackTrace()
            }
        }
        
        importedCount
    }
    
    /**
     * 외부 파일을 파싱하여 RecipeImport 객체 목록으로 변환합니다.
     */
    private fun parseRecipesFromExternalFile(filePath: String): List<RecipeImport> {
        val recipes = mutableListOf<RecipeImport>()
        val reader = BufferedReader(InputStreamReader(java.io.FileInputStream(filePath)))
        
        // 헤더 건너뛰기
        var headerLine = reader.readLine()
        val headers = headerLine.split(",")
        
        // 데이터 파싱
        var line: String?
        while (reader.readLine().also { line = it } != null) {
            line?.let {
                val values = parseCsvLine(it)
                if (values.size >= headers.size) {
                    val recipe = RecipeImport(
                        recipeId = values[0],
                        title = values[1],
                        cookingName = values[2],
                        registerId = values[3],
                        registerName = values[4],
                        inquiryCount = values[5].toIntOrNull() ?: 0,
                        recommendCount = values[6].toIntOrNull() ?: 0,
                        scrapCount = values[7].toIntOrNull() ?: 0,
                        cookingMethod = values[8],
                        cookingStatus = values[9],
                        cookingMaterial = values[10],
                        cookingType = values[11],
                        description = values[12],
                        ingredients = values[13],
                        servings = values[14],
                        difficulty = values[15],
                        cookingTime = values[16],
                        registerDate = values[17],
                        imageUrl = values[18]
                    )
                    recipes.add(recipe)
                }
            }
        }
        
        reader.close()
        return recipes
    }
}
