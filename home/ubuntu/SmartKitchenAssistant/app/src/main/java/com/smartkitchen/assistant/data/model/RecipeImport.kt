package com.smartkitchen.assistant.data.model

/**
 * CSV 파일에서 가져온 레시피 정보를 담는 데이터 클래스
 * 제공된 TB_RECIPE_SEARCH_241226.csv 파일의 형식에 맞춤
 */
data class RecipeImport(
    val recipeId: String,              // RCP_SNO: 레시피 일련번호
    val title: String,                 // RCP_TTL: 레시피 제목
    val cookingName: String,           // CKG_NM: 요리명
    val registerId: String,            // RGTR_ID: 등록자 ID
    val registerName: String,          // RGTR_NM: 등록자 이름
    val inquiryCount: Int,             // INQ_CNT: 조회수
    val recommendCount: Int,           // RCMM_CNT: 추천수
    val scrapCount: Int,               // SRAP_CNT: 스크랩수
    val cookingMethod: String,         // CKG_MTH_ACTO_NM: 조리방법
    val cookingStatus: String,         // CKG_STA_ACTO_NM: 상황
    val cookingMaterial: String,       // CKG_MTRL_ACTO_NM: 주재료
    val cookingType: String,           // CKG_KND_ACTO_NM: 종류
    val description: String,           // CKG_IPDC: 설명
    val ingredients: String,           // CKG_MTRL_CN: 재료 내용
    val servings: String,              // CKG_INBUN_NM: 인분
    val difficulty: String,            // CKG_DODF_NM: 난이도
    val cookingTime: String,           // CKG_TIME_NM: 조리시간
    val registerDate: String,          // FIRST_REG_DT: 등록일시
    val imageUrl: String               // RCP_IMG_URL: 레시피 이미지 URL
) {
    /**
     * RecipeImport 객체를 Recipe 엔티티로 변환
     */
    fun toRecipe(): Recipe {
        // 조리 시간을 분 단위로 변환 (예: "60분이내" -> 60)
        val prepTimeMinutes = when {
            cookingTime.contains("분이내") -> cookingTime.replace("분이내", "").toIntOrNull() ?: 30
            cookingTime.contains("시간이내") -> {
                val hours = cookingTime.replace("시간이내", "").toIntOrNull() ?: 1
                hours * 60
            }
            else -> 30 // 기본값
        }
        
        // 난이도 변환
        val difficultyLevel = when (difficulty) {
            "초급" -> "쉬움"
            "중급" -> "중간"
            "고급" -> "어려움"
            else -> "중간" // 기본값
        }
        
        // 인분 수 변환
        val servingsCount = servings.replace("인분", "").toIntOrNull() ?: 2
        
        return Recipe(
            name = title,
            description = description,
            prepTime = prepTimeMinutes / 3, // 준비 시간은 전체 조리 시간의 1/3로 가정
            cookTime = prepTimeMinutes * 2 / 3, // 실제 조리 시간은 전체 조리 시간의 2/3로 가정
            servings = servingsCount,
            difficulty = difficultyLevel,
            imageUrl = imageUrl,
            isFavorite = false,
            source = "가져온 레시피 (등록자: $registerName)",
            notes = "조리방법: $cookingMethod\n주재료: $cookingMaterial\n종류: $cookingType"
        )
    }
    
    /**
     * 재료 문자열을 파싱하여 RecipeIngredient 객체 목록으로 변환
     */
    fun parseIngredients(recipeId: Long, ingredientMap: Map<String, Long>): List<RecipeIngredient> {
        val result = mutableListOf<RecipeIngredient>()
        
        // "[재료]" 부분 제거 및 재료 분리
        val cleanedIngredients = ingredients.replace("[재료]", "").trim()
        val ingredientParts = cleanedIngredients.split("|")
        
        ingredientParts.forEach { part ->
            val trimmedPart = part.trim()
            if (trimmedPart.isNotEmpty()) {
                // 재료명과 수량 분리 (예: "양파1/2개" -> "양파", "1/2개")
                val ingredientName = trimmedPart.replace(Regex("[0-9/.]+[a-zA-Z가-힣]*$"), "").trim()
                val quantityStr = trimmedPart.replace(ingredientName, "").trim()
                
                // 수량과 단위 분리 (예: "1/2개" -> 0.5, "개")
                val quantity = parseQuantity(quantityStr)
                val unit = parseUnit(quantityStr)
                
                // 재료 ID 찾기 또는 새로 생성
                val ingredientId = findOrCreateIngredient(ingredientName, ingredientMap)
                
                result.add(
                    RecipeIngredient(
                        recipeId = recipeId,
                        ingredientId = ingredientId,
                        quantity = quantity,
                        unit = unit,
                        isOptional = false
                    )
                )
            }
        }
        
        return result
    }
    
    /**
     * 수량 문자열을 Double로 변환 (예: "1/2" -> 0.5)
     */
    private fun parseQuantity(quantityStr: String): Double {
        // 숫자만 추출
        val numberPattern = Regex("[0-9/.]+")
        val numberMatch = numberPattern.find(quantityStr)
        val numberStr = numberMatch?.value ?: return 1.0
        
        return when {
            numberStr.contains("/") -> {
                val parts = numberStr.split("/")
                if (parts.size == 2) {
                    val numerator = parts[0].toDoubleOrNull() ?: 1.0
                    val denominator = parts[1].toDoubleOrNull() ?: 1.0
                    if (denominator != 0.0) numerator / denominator else 1.0
                } else {
                    1.0
                }
            }
            else -> numberStr.toDoubleOrNull() ?: 1.0
        }
    }
    
    /**
     * 단위 문자열 추출 (예: "1/2개" -> "개")
     */
    private fun parseUnit(quantityStr: String): String {
        val unitPattern = Regex("[가-힣a-zA-Z]+$")
        val unitMatch = unitPattern.find(quantityStr)
        return unitMatch?.value ?: "개"
    }
    
    /**
     * 재료명으로 재료 ID 찾기 또는 -1 반환 (새로 생성해야 함을 의미)
     */
    private fun findOrCreateIngredient(name: String, ingredientMap: Map<String, Long>): Long {
        // 정확히 일치하는 재료 찾기
        if (ingredientMap.containsKey(name)) {
            return ingredientMap[name]!!
        }
        
        // 부분 일치하는 재료 찾기
        for ((key, id) in ingredientMap) {
            if (name.contains(key) || key.contains(name)) {
                return id
            }
        }
        
        // 일치하는 재료가 없으면 -1 반환 (나중에 새로 생성)
        return -1
    }
    
    /**
     * 레시피 단계를 생성 (설명에서 단계 추출)
     */
    fun createRecipeSteps(recipeId: Long): List<RecipeStep> {
        val steps = mutableListOf<RecipeStep>()
        
        // 설명에서 단계를 추출하는 로직 (간단한 구현)
        // 실제로는 더 복잡한 자연어 처리가 필요할 수 있음
        val sentences = description.split(Regex("[.!?]"))
        
        sentences.forEachIndexed { index, sentence ->
            val trimmedSentence = sentence.trim()
            if (trimmedSentence.isNotEmpty()) {
                steps.add(
                    RecipeStep(
                        recipeId = recipeId,
                        stepNumber = index + 1,
                        description = trimmedSentence,
                        imageUrl = if (index == 0) imageUrl else null,
                        videoUrl = null,
                        timerDuration = null
                    )
                )
            }
        }
        
        return steps
    }
}
