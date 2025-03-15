package com.smartkitchen.assistant.data.api

import com.smartkitchen.assistant.data.model.NutritionResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * USDA 영양 정보 API 서비스 인터페이스
 * 식품 영양 정보를 검색하고 가져오는 API 호출을 정의합니다.
 */
interface UsdaNutritionApiService {
    
    /**
     * 식품 검색 API
     * 검색어를 기반으로 식품 목록을 검색합니다.
     */
    @GET("foods/search")
    suspend fun searchFoods(
        @Query("query") query: String,
        @Query("dataType") dataType: String = "Foundation,SR Legacy,Survey (FNDDS)",
        @Query("pageSize") pageSize: Int = 25,
        @Query("pageNumber") pageNumber: Int = 1,
        @Query("sortBy") sortBy: String = "dataType.keyword",
        @Query("sortOrder") sortOrder: String = "asc",
        @Query("api_key") apiKey: String
    ): Response<NutritionResponse.FoodSearchResponse>
    
    /**
     * 식품 상세 정보 API
     * 특정 식품의 상세 영양 정보를 가져옵니다.
     */
    @GET("food/")
    suspend fun getFoodDetails(
        @Query("fdcId") fdcId: Int,
        @Query("format") format: String = "full",
        @Query("api_key") apiKey: String
    ): Response<NutritionResponse.FoodDetailsResponse>
    
    /**
     * 식품 영양소 리스트 API
     * 지원되는 영양소 목록을 가져옵니다.
     */
    @GET("nutrients/list")
    suspend fun getNutrientsList(
        @Query("api_key") apiKey: String
    ): Response<NutritionResponse.NutrientsListResponse>
    
    companion object {
        const val BASE_URL = "https://api.nal.usda.gov/fdc/v1/"
        const val API_KEY = "YOUR_API_KEY" // 실제 앱에서는 보안을 위해 BuildConfig나 Properties 파일에서 관리
    }
}
