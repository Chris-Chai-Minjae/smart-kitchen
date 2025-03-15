package com.smartkitchen.assistant.data.model

import com.google.gson.annotations.SerializedName

/**
 * 레시피 검색 API 응답 모델 클래스
 * API 응답 데이터를 매핑하기 위한 데이터 클래스들을 포함합니다.
 */
class RecipeSearchResponse {
    
    /**
     * 레시피 검색 결과 모델
     */
    data class SearchResults(
        @SerializedName("results")
        val results: List<Recipe>,
        @SerializedName("offset")
        val offset: Int,
        @SerializedName("number")
        val number: Int,
        @SerializedName("totalResults")
        val totalResults: Int
    )
    
    /**
     * 레시피 정보 모델
     */
    data class Recipe(
        @SerializedName("id")
        val id: Int,
        @SerializedName("title")
        val title: String,
        @SerializedName("image")
        val image: String?,
        @SerializedName("imageType")
        val imageType: String?,
        @SerializedName("servings")
        val servings: Int,
        @SerializedName("readyInMinutes")
        val readyInMinutes: Int,
        @SerializedName("license")
        val license: String?,
        @SerializedName("sourceName")
        val sourceName: String?,
        @SerializedName("sourceUrl")
        val sourceUrl: String?,
        @SerializedName("spoonacularSourceUrl")
        val spoonacularSourceUrl: String?,
        @SerializedName("healthScore")
        val healthScore: Double,
        @SerializedName("spoonacularScore")
        val spoonacularScore: Double,
        @SerializedName("pricePerServing")
        val pricePerServing: Double,
        @SerializedName("analyzedInstructions")
        val analyzedInstructions: List<AnalyzedInstruction>?,
        @SerializedName("cheap")
        val cheap: Boolean,
        @SerializedName("creditsText")
        val creditsText: String?,
        @SerializedName("cuisines")
        val cuisines: List<String>?,
        @SerializedName("dairyFree")
        val dairyFree: Boolean,
        @SerializedName("diets")
        val diets: List<String>?,
        @SerializedName("gaps")
        val gaps: String?,
        @SerializedName("glutenFree")
        val glutenFree: Boolean,
        @SerializedName("instructions")
        val instructions: String?,
        @SerializedName("ketogenic")
        val ketogenic: Boolean,
        @SerializedName("lowFodmap")
        val lowFodmap: Boolean,
        @SerializedName("occasions")
        val occasions: List<String>?,
        @SerializedName("sustainable")
        val sustainable: Boolean,
        @SerializedName("vegan")
        val vegan: Boolean,
        @SerializedName("vegetarian")
        val vegetarian: Boolean,
        @SerializedName("veryHealthy")
        val veryHealthy: Boolean,
        @SerializedName("veryPopular")
        val veryPopular: Boolean,
        @SerializedName("whole30")
        val whole30: Boolean,
        @SerializedName("weightWatcherSmartPoints")
        val weightWatcherSmartPoints: Int,
        @SerializedName("dishTypes")
        val dishTypes: List<String>?,
        @SerializedName("extendedIngredients")
        val extendedIngredients: List<ExtendedIngredient>?,
        @SerializedName("summary")
        val summary: String?
    )
    
    /**
     * 레시피 상세 정보 모델
     */
    data class RecipeInformation(
        @SerializedName("id")
        val id: Int,
        @SerializedName("title")
        val title: String,
        @SerializedName("image")
        val image: String?,
        @SerializedName("imageType")
        val imageType: String?,
        @SerializedName("servings")
        val servings: Int,
        @SerializedName("readyInMinutes")
        val readyInMinutes: Int,
        @SerializedName("license")
        val license: String?,
        @SerializedName("sourceName")
        val sourceName: String?,
        @SerializedName("sourceUrl")
        val sourceUrl: String?,
        @SerializedName("spoonacularSourceUrl")
        val spoonacularSourceUrl: String?,
        @SerializedName("aggregateLikes")
        val aggregateLikes: Int,
        @SerializedName("healthScore")
        val healthScore: Double,
        @SerializedName("spoonacularScore")
        val spoonacularScore: Double,
        @SerializedName("pricePerServing")
        val pricePerServing: Double,
        @SerializedName("analyzedInstructions")
        val analyzedInstructions: List<AnalyzedInstruction>?,
        @SerializedName("cheap")
        val cheap: Boolean,
        @SerializedName("creditsText")
        val creditsText: String?,
        @SerializedName("cuisines")
        val cuisines: List<String>?,
        @SerializedName("dairyFree")
        val dairyFree: Boolean,
        @SerializedName("diets")
        val diets: List<String>?,
        @SerializedName("gaps")
        val gaps: String?,
        @SerializedName("glutenFree")
        val glutenFree: Boolean,
        @SerializedName("instructions")
        val instructions: String?,
        @SerializedName("ketogenic")
        val ketogenic: Boolean,
        @SerializedName("lowFodmap")
        val lowFodmap: Boolean,
        @SerializedName("occasions")
        val occasions: List<String>?,
        @SerializedName("sustainable")
        val sustainable: Boolean,
        @SerializedName("vegan")
        val vegan: Boolean,
        @SerializedName("vegetarian")
        val vegetarian: Boolean,
        @SerializedName("veryHealthy")
        val veryHealthy: Boolean,
        @SerializedName("veryPopular")
        val veryPopular: Boolean,
        @SerializedName("whole30")
        val whole30: Boolean,
        @SerializedName("weightWatcherSmartPoints")
        val weightWatcherSmartPoints: Int,
        @SerializedName("dishTypes")
        val dishTypes: List<String>?,
        @SerializedName("extendedIngredients")
        val extendedIngredients: List<ExtendedIngredient>?,
        @SerializedName("summary")
        val summary: String?,
        @SerializedName("winePairing")
        val winePairing: WinePairing?,
        @SerializedName("nutrition")
        val nutrition: RecipeNutrition?
    )
    
    /**
     * 상세 재료 정보 모델
     */
    data class ExtendedIngredient(
        @SerializedName("id")
        val id: Int,
        @SerializedName("aisle")
        val aisle: String?,
        @SerializedName("image")
        val image: String?,
        @SerializedName("consistency")
        val consistency: String?,
        @SerializedName("name")
        val name: String,
        @SerializedName("original")
        val original: String?,
        @SerializedName("originalString")
        val originalString: String?,
        @SerializedName("originalName")
        val originalName: String?,
        @SerializedName("amount")
        val amount: Double,
        @SerializedName("unit")
        val unit: String?,
        @SerializedName("meta")
        val meta: List<String>?,
        @SerializedName("metaInformation")
        val metaInformation: List<String>?,
        @SerializedName("measures")
        val measures: Measures?
    )
    
    /**
     * 재료 계량 정보 모델
     */
    data class Measures(
        @SerializedName("us")
        val us: Measure,
        @SerializedName("metric")
        val metric: Measure
    )
    
    /**
     * 계량 단위 모델
     */
    data class Measure(
        @SerializedName("amount")
        val amount: Double,
        @SerializedName("unitShort")
        val unitShort: String,
        @SerializedName("unitLong")
        val unitLong: String
    )
    
    /**
     * 레시피 조리 단계 모델
     */
    data class AnalyzedInstruction(
        @SerializedName("name")
        val name: String,
        @SerializedName("steps")
        val steps: List<Step>
    )
    
    /**
     * 조리 단계 모델
     */
    data class Step(
        @SerializedName("number")
        val number: Int,
        @SerializedName("step")
        val step: String,
        @SerializedName("ingredients")
        val ingredients: List<Ingredient>?,
        @SerializedName("equipment")
        val equipment: List<Equipment>?,
        @SerializedName("length")
        val length: Length?
    )
    
    /**
     * 재료 모델
     */
    data class Ingredient(
        @SerializedName("id")
        val id: Int,
        @SerializedName("name")
        val name: String,
        @SerializedName("localizedName")
        val localizedName: String,
        @SerializedName("image")
        val image: String?
    )
    
    /**
     * 조리 도구 모델
     */
    data class Equipment(
        @SerializedName("id")
        val id: Int,
        @SerializedName("name")
        val name: String,
        @SerializedName("localizedName")
        val localizedName: String,
        @SerializedName("image")
        val image: String?
    )
    
    /**
     * 조리 시간 모델
     */
    data class Length(
        @SerializedName("number")
        val number: Int,
        @SerializedName("unit")
        val unit: String
    )
    
    /**
     * 와인 페어링 모델
     */
    data class WinePairing(
        @SerializedName("pairedWines")
        val pairedWines: List<String>?,
        @SerializedName("pairingText")
        val pairingText: String?,
        @SerializedName("productMatches")
        val productMatches: List<ProductMatch>?
    )
    
    /**
     * 제품 매칭 모델
     */
    data class ProductMatch(
        @SerializedName("id")
        val id: Int,
        @SerializedName("title")
        val title: String,
        @SerializedName("description")
        val description: String?,
        @SerializedName("price")
        val price: String,
        @SerializedName("imageUrl")
        val imageUrl: String,
        @SerializedName("averageRating")
        val averageRating: Double,
        @SerializedName("ratingCount")
        val ratingCount: Int,
        @SerializedName("score")
        val score: Double,
        @SerializedName("link")
        val link: String
    )
    
    /**
     * 레시피 영양 정보 모델
     */
    data class RecipeNutrition(
        @SerializedName("nutrients")
        val nutrients: List<Nutrient>,
        @SerializedName("properties")
        val properties: List<Property>?,
        @SerializedName("flavonoids")
        val flavonoids: List<Flavonoid>?,
        @SerializedName("ingredients")
        val ingredients: List<NutritionIngredient>?,
        @SerializedName("caloricBreakdown")
        val caloricBreakdown: CaloricBreakdown?,
        @SerializedName("weightPerServing")
        val weightPerServing: WeightPerServing?
    )
    
    /**
     * 영양소 모델
     */
    data class Nutrient(
        @SerializedName("name")
        val name: String,
        @SerializedName("amount")
        val amount: Double,
        @SerializedName("unit")
        val unit: String,
        @SerializedName("percentOfDailyNeeds")
        val percentOfDailyNeeds: Double
    )
    
    /**
     * 영양 속성 모델
     */
    data class Property(
        @SerializedName("name")
        val name: String,
        @SerializedName("amount")
        val amount: Double,
        @SerializedName("unit")
        val unit: String
    )
    
    /**
     * 플라보노이드 모델
     */
    data class Flavonoid(
        @SerializedName("name")
        val name: String,
        @SerializedName("amount")
        val amount: Double,
        @SerializedName("unit")
        val unit: String
    )
    
    /**
     * 영양 재료 모델
     */
    data class NutritionIngredient(
        @SerializedName("id")
        val id: Int,
        @SerializedName("name")
        val name: String,
        @SerializedName("amount")
        val amount: Double,
        @SerializedName("unit")
        val unit: String,
        @SerializedName("nutrients")
        val nutrients: List<Nutrient>?
    )
    
    /**
     * 칼로리 분석 모델
     */
    data class CaloricBreakdown(
        @SerializedName("percentProtein")
        val percentProtein: Double,
        @SerializedName("percentFat")
        val percentFat: Double,
        @SerializedName("percentCarbs")
        val percentCarbs: Double
    )
    
    /**
     * 서빙 당 무게 모델
     */
    data class WeightPerServing(
        @SerializedName("amount")
        val amount: Double,
        @SerializedName("unit")
        val unit: String
    )
    
    /**
     * 비슷한 레시피 모델
     */
    data class SimilarRecipe(
        @SerializedName("id")
        val id: Int,
        @SerializedName("title")
        val title: String,
        @SerializedName("imageType")
        val imageType: String,
        @SerializedName("readyInMinutes")
        val readyInMinutes: Int,
        @SerializedName("servings")
        val servings: Int,
        @SerializedName("sourceUrl")
        val sourceUrl: String
    )
}
