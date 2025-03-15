package com.smartkitchen.assistant.data.model

import com.google.gson.annotations.SerializedName

/**
 * USDA 영양 정보 API 응답 모델 클래스
 * API 응답 데이터를 매핑하기 위한 데이터 클래스들을 포함합니다.
 */
class NutritionResponse {
    
    /**
     * 식품 검색 API 응답 모델
     */
    data class FoodSearchResponse(
        @SerializedName("foodSearchCriteria")
        val foodSearchCriteria: FoodSearchCriteria,
        @SerializedName("totalHits")
        val totalHits: Int,
        @SerializedName("currentPage")
        val currentPage: Int,
        @SerializedName("totalPages")
        val totalPages: Int,
        @SerializedName("foods")
        val foods: List<Food>
    )
    
    /**
     * 식품 검색 조건 모델
     */
    data class FoodSearchCriteria(
        @SerializedName("query")
        val query: String,
        @SerializedName("dataType")
        val dataType: List<String>,
        @SerializedName("pageSize")
        val pageSize: Int,
        @SerializedName("pageNumber")
        val pageNumber: Int,
        @SerializedName("sortBy")
        val sortBy: String,
        @SerializedName("sortOrder")
        val sortOrder: String
    )
    
    /**
     * 식품 정보 모델
     */
    data class Food(
        @SerializedName("fdcId")
        val fdcId: Int,
        @SerializedName("description")
        val description: String,
        @SerializedName("dataType")
        val dataType: String,
        @SerializedName("gtinUpc")
        val gtinUpc: String?,
        @SerializedName("publishedDate")
        val publishedDate: String,
        @SerializedName("brandOwner")
        val brandOwner: String?,
        @SerializedName("ingredients")
        val ingredients: String?,
        @SerializedName("foodNutrients")
        val foodNutrients: List<FoodNutrient>
    )
    
    /**
     * 식품 영양소 정보 모델
     */
    data class FoodNutrient(
        @SerializedName("nutrientId")
        val nutrientId: Int,
        @SerializedName("nutrientName")
        val nutrientName: String,
        @SerializedName("nutrientNumber")
        val nutrientNumber: String,
        @SerializedName("unitName")
        val unitName: String,
        @SerializedName("value")
        val value: Double,
        @SerializedName("derivationCode")
        val derivationCode: String?,
        @SerializedName("derivationDescription")
        val derivationDescription: String?
    )
    
    /**
     * 식품 상세 정보 API 응답 모델
     */
    data class FoodDetailsResponse(
        @SerializedName("fdcId")
        val fdcId: Int,
        @SerializedName("description")
        val description: String,
        @SerializedName("dataType")
        val dataType: String,
        @SerializedName("publicationDate")
        val publicationDate: String,
        @SerializedName("foodCategory")
        val foodCategory: FoodCategory?,
        @SerializedName("foodPortions")
        val foodPortions: List<FoodPortion>?,
        @SerializedName("foodNutrients")
        val foodNutrients: List<FoodNutrient>,
        @SerializedName("foodAttributes")
        val foodAttributes: List<FoodAttribute>?
    )
    
    /**
     * 식품 카테고리 모델
     */
    data class FoodCategory(
        @SerializedName("id")
        val id: Int,
        @SerializedName("code")
        val code: String,
        @SerializedName("description")
        val description: String
    )
    
    /**
     * 식품 분량 정보 모델
     */
    data class FoodPortion(
        @SerializedName("id")
        val id: Int,
        @SerializedName("amount")
        val amount: Double,
        @SerializedName("gramWeight")
        val gramWeight: Double,
        @SerializedName("sequenceNumber")
        val sequenceNumber: Int,
        @SerializedName("modifier")
        val modifier: String,
        @SerializedName("portionDescription")
        val portionDescription: String
    )
    
    /**
     * 식품 속성 모델
     */
    data class FoodAttribute(
        @SerializedName("id")
        val id: Int,
        @SerializedName("sequenceNumber")
        val sequenceNumber: Int,
        @SerializedName("value")
        val value: String,
        @SerializedName("foodAttributeType")
        val foodAttributeType: FoodAttributeType
    )
    
    /**
     * 식품 속성 유형 모델
     */
    data class FoodAttributeType(
        @SerializedName("id")
        val id: Int,
        @SerializedName("name")
        val name: String,
        @SerializedName("description")
        val description: String
    )
    
    /**
     * 영양소 목록 API 응답 모델
     */
    data class NutrientsListResponse(
        @SerializedName("nutrients")
        val nutrients: List<Nutrient>
    )
    
    /**
     * 영양소 정보 모델
     */
    data class Nutrient(
        @SerializedName("id")
        val id: Int,
        @SerializedName("number")
        val number: String,
        @SerializedName("name")
        val name: String,
        @SerializedName("rank")
        val rank: Int,
        @SerializedName("unitName")
        val unitName: String
    )
}
