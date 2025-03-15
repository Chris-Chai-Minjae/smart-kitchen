package com.smartkitchen.assistant.data.api

import com.smartkitchen.assistant.data.model.RecipeSearchResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * 레시피 검색 API 서비스 인터페이스
 * 외부 레시피 검색 API 호출을 정의합니다.
 */
interface RecipeSearchApiService {
    
    /**
     * 레시피 검색 API
     * 검색어를 기반으로 레시피를 검색합니다.
     */
    @GET("recipes/complexSearch")
    suspend fun searchRecipes(
        @Query("query") query: String,
        @Query("cuisine") cuisine: String? = null,
        @Query("diet") diet: String? = null,
        @Query("intolerances") intolerances: String? = null,
        @Query("includeIngredients") includeIngredients: String? = null,
        @Query("excludeIngredients") excludeIngredients: String? = null,
        @Query("type") type: String? = null,
        @Query("instructionsRequired") instructionsRequired: Boolean = true,
        @Query("fillIngredients") fillIngredients: Boolean = true,
        @Query("addRecipeInformation") addRecipeInformation: Boolean = true,
        @Query("number") number: Int = 10,
        @Query("offset") offset: Int = 0,
        @Query("apiKey") apiKey: String
    ): Response<RecipeSearchResponse.SearchResults>
    
    /**
     * 레시피 상세 정보 API
     * 특정 레시피의 상세 정보를 가져옵니다.
     */
    @GET("recipes/{id}/information")
    suspend fun getRecipeInformation(
        @Query("id") id: Int,
        @Query("includeNutrition") includeNutrition: Boolean = true,
        @Query("apiKey") apiKey: String
    ): Response<RecipeSearchResponse.RecipeInformation>
    
    /**
     * 레시피 영양 정보 API
     * 특정 레시피의 영양 정보를 가져옵니다.
     */
    @GET("recipes/{id}/nutritionWidget.json")
    suspend fun getRecipeNutrition(
        @Query("id") id: Int,
        @Query("apiKey") apiKey: String
    ): Response<RecipeSearchResponse.RecipeNutrition>
    
    /**
     * 비슷한 레시피 API
     * 특정 레시피와 비슷한 레시피를 검색합니다.
     */
    @GET("recipes/{id}/similar")
    suspend fun getSimilarRecipes(
        @Query("id") id: Int,
        @Query("number") number: Int = 5,
        @Query("apiKey") apiKey: String
    ): Response<List<RecipeSearchResponse.SimilarRecipe>>
    
    companion object {
        const val BASE_URL = "https://api.spoonacular.com/"
        const val API_KEY = "YOUR_API_KEY" // 실제 앱에서는 보안을 위해 BuildConfig나 Properties 파일에서 관리
    }
}
