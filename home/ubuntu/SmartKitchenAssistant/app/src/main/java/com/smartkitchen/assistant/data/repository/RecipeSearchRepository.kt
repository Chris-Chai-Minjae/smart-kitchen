package com.smartkitchen.assistant.data.repository

import com.smartkitchen.assistant.data.api.RecipeSearchApiService
import com.smartkitchen.assistant.data.model.RecipeSearchResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 레시피 검색 저장소
 * 외부 레시피 검색 API를 사용하여 레시피를 검색하고 관리합니다.
 */
@Singleton
class RecipeSearchRepository @Inject constructor(
    private val recipeSearchApiService: RecipeSearchApiService
) {
    /**
     * 레시피를 검색합니다.
     */
    fun searchRecipes(
        query: String,
        cuisine: String? = null,
        diet: String? = null,
        intolerances: String? = null,
        includeIngredients: String? = null,
        excludeIngredients: String? = null,
        type: String? = null,
        number: Int = 10,
        offset: Int = 0
    ): Flow<Result<RecipeSearchResponse.SearchResults>> = flow {
        try {
            val response = recipeSearchApiService.searchRecipes(
                query = query,
                cuisine = cuisine,
                diet = diet,
                intolerances = intolerances,
                includeIngredients = includeIngredients,
                excludeIngredients = excludeIngredients,
                type = type,
                number = number,
                offset = offset,
                apiKey = RecipeSearchApiService.API_KEY
            )
            
            if (response.isSuccessful) {
                response.body()?.let {
                    emit(Result.success(it))
                } ?: emit(Result.failure(Exception("응답 데이터가 없습니다.")))
            } else {
                emit(Result.failure(Exception("API 호출 실패: ${response.code()} ${response.message()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)
    
    /**
     * 레시피 상세 정보를 가져옵니다.
     */
    fun getRecipeInformation(id: Int): Flow<Result<RecipeSearchResponse.RecipeInformation>> = flow {
        try {
            val response = recipeSearchApiService.getRecipeInformation(
                id = id,
                includeNutrition = true,
                apiKey = RecipeSearchApiService.API_KEY
            )
            
            if (response.isSuccessful) {
                response.body()?.let {
                    emit(Result.success(it))
                } ?: emit(Result.failure(Exception("응답 데이터가 없습니다.")))
            } else {
                emit(Result.failure(Exception("API 호출 실패: ${response.code()} ${response.message()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)
    
    /**
     * 레시피 영양 정보를 가져옵니다.
     */
    fun getRecipeNutrition(id: Int): Flow<Result<RecipeSearchResponse.RecipeNutrition>> = flow {
        try {
            val response = recipeSearchApiService.getRecipeNutrition(
                id = id,
                apiKey = RecipeSearchApiService.API_KEY
            )
            
            if (response.isSuccessful) {
                response.body()?.let {
                    emit(Result.success(it))
                } ?: emit(Result.failure(Exception("응답 데이터가 없습니다.")))
            } else {
                emit(Result.failure(Exception("API 호출 실패: ${response.code()} ${response.message()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)
    
    /**
     * 비슷한 레시피를 검색합니다.
     */
    fun getSimilarRecipes(id: Int, number: Int = 5): Flow<Result<List<RecipeSearchResponse.SimilarRecipe>>> = flow {
        try {
            val response = recipeSearchApiService.getSimilarRecipes(
                id = id,
                number = number,
                apiKey = RecipeSearchApiService.API_KEY
            )
            
            if (response.isSuccessful) {
                response.body()?.let {
                    emit(Result.success(it))
                } ?: emit(Result.failure(Exception("응답 데이터가 없습니다.")))
            } else {
                emit(Result.failure(Exception("API 호출 실패: ${response.code()} ${response.message()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.IO)
    
    /**
     * 레시피를 앱 내부 데이터베이스로 가져옵니다.
     * 외부 API에서 검색한 레시피를 앱 내부 데이터베이스에 저장하는 기능입니다.
     */
    suspend fun importRecipeToDatabase(recipeInfo: RecipeSearchResponse.RecipeInformation) {
        // TODO: 외부 API에서 가져온 레시피를 앱 내부 데이터베이스에 저장하는 로직 구현
        // 이 기능은 RecipeRepository와 협력하여 구현해야 합니다.
    }
    
    /**
     * 레시피의 대체 재료를 제안합니다.
     * 특정 재료를 대체할 수 있는 다른 재료를 제안하는 기능입니다.
     */
    fun suggestIngredientSubstitutes(ingredient: String): Map<String, String> {
        // 실제 앱에서는 API를 통해 대체 재료를 검색하거나 데이터베이스에서 가져와야 합니다.
        // 여기서는 예시로 몇 가지 대체 재료를 하드코딩합니다.
        val substitutes = mapOf(
            "우유" to "두유, 아몬드 밀크, 코코넛 밀크",
            "버터" to "마가린, 코코넛 오일, 올리브 오일",
            "설탕" to "꿀, 메이플 시럽, 스테비아",
            "밀가루" to "아몬드 가루, 코코넛 가루, 쌀가루",
            "계란" to "두부, 바나나, 아마씨 가루와 물",
            "소고기" to "돼지고기, 닭고기, 두부",
            "돼지고기" to "소고기, 닭고기, 두부",
            "닭고기" to "소고기, 돼지고기, 두부",
            "생크림" to "코코넛 크림, 두유 크림",
            "치즈" to "영양효모, 두부 치즈"
        )
        
        return mapOf(ingredient to (substitutes[ingredient] ?: "대체 재료를 찾을 수 없습니다."))
    }
}
