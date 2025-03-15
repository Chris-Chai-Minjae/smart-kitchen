package com.smartkitchen.assistant.data.repository

import com.smartkitchen.assistant.data.api.UsdaNutritionApiService
import com.smartkitchen.assistant.data.model.NutritionResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 영양 정보 저장소
 * USDA 영양 정보 API를 사용하여 식품 영양 정보를 검색하고 관리합니다.
 */
@Singleton
class NutritionRepository @Inject constructor(
    private val nutritionApiService: UsdaNutritionApiService
) {
    /**
     * 식품을 검색합니다.
     */
    fun searchFoods(query: String, pageSize: Int = 25, pageNumber: Int = 1): Flow<Result<NutritionResponse.FoodSearchResponse>> = flow {
        try {
            val response = nutritionApiService.searchFoods(
                query = query,
                pageSize = pageSize,
                pageNumber = pageNumber,
                apiKey = UsdaNutritionApiService.API_KEY
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
     * 식품 상세 정보를 가져옵니다.
     */
    fun getFoodDetails(fdcId: Int): Flow<Result<NutritionResponse.FoodDetailsResponse>> = flow {
        try {
            val response = nutritionApiService.getFoodDetails(
                fdcId = fdcId,
                apiKey = UsdaNutritionApiService.API_KEY
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
     * 영양소 목록을 가져옵니다.
     */
    fun getNutrientsList(): Flow<Result<NutritionResponse.NutrientsListResponse>> = flow {
        try {
            val response = nutritionApiService.getNutrientsList(
                apiKey = UsdaNutritionApiService.API_KEY
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
     * 식품의 영양 정보를 분석합니다.
     * 이 메서드는 식품 상세 정보에서 주요 영양소를 추출하여 사용자 친화적인 형태로 반환합니다.
     */
    fun analyzeFoodNutrition(foodDetails: NutritionResponse.FoodDetailsResponse): Map<String, String> {
        val nutritionMap = mutableMapOf<String, String>()
        
        // 주요 영양소 추출
        foodDetails.foodNutrients.forEach { nutrient ->
            when (nutrient.nutrientName) {
                "Energy" -> nutritionMap["칼로리"] = "${nutrient.value} ${nutrient.unitName}"
                "Protein" -> nutritionMap["단백질"] = "${nutrient.value} ${nutrient.unitName}"
                "Total lipid (fat)" -> nutritionMap["지방"] = "${nutrient.value} ${nutrient.unitName}"
                "Carbohydrate, by difference" -> nutritionMap["탄수화물"] = "${nutrient.value} ${nutrient.unitName}"
                "Fiber, total dietary" -> nutritionMap["식이섬유"] = "${nutrient.value} ${nutrient.unitName}"
                "Sugars, total" -> nutritionMap["당류"] = "${nutrient.value} ${nutrient.unitName}"
                "Calcium, Ca" -> nutritionMap["칼슘"] = "${nutrient.value} ${nutrient.unitName}"
                "Iron, Fe" -> nutritionMap["철분"] = "${nutrient.value} ${nutrient.unitName}"
                "Sodium, Na" -> nutritionMap["나트륨"] = "${nutrient.value} ${nutrient.unitName}"
                "Vitamin C, total ascorbic acid" -> nutritionMap["비타민 C"] = "${nutrient.value} ${nutrient.unitName}"
                "Vitamin A, RAE" -> nutritionMap["비타민 A"] = "${nutrient.value} ${nutrient.unitName}"
            }
        }
        
        return nutritionMap
    }
    
    /**
     * 식품의 건강 점수를 계산합니다.
     * 이 메서드는 영양소 함량을 기반으로 식품의 건강 점수를 계산합니다.
     */
    fun calculateHealthScore(foodDetails: NutritionResponse.FoodDetailsResponse): Int {
        var score = 50 // 기본 점수
        
        // 영양소별 점수 가중치
        foodDetails.foodNutrients.forEach { nutrient ->
            when (nutrient.nutrientName) {
                "Protein" -> score += (nutrient.value / 5).toInt() // 단백질은 점수 상승
                "Fiber, total dietary" -> score += (nutrient.value / 2).toInt() // 식이섬유는 점수 상승
                "Vitamin C, total ascorbic acid" -> score += (nutrient.value / 10).toInt() // 비타민 C는 점수 상승
                "Sodium, Na" -> score -= (nutrient.value / 500).toInt() // 나트륨은 점수 하락
                "Sugars, total" -> score -= (nutrient.value / 5).toInt() // 당류는 점수 하락
                "Total lipid (fat)" -> {
                    // 지방은 일정 수준까지는 괜찮지만 그 이상은 점수 하락
                    if (nutrient.value > 20) {
                        score -= ((nutrient.value - 20) / 2).toInt()
                    }
                }
            }
        }
        
        // 점수 범위 제한
        return score.coerceIn(0, 100)
    }
}
