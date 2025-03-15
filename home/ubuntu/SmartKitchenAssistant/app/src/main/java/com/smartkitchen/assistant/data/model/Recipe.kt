package com.smartkitchen.assistant.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 레시피 엔티티 클래스
 * 요리 레시피의 기본 정보를 저장합니다.
 */
@Entity(tableName = "recipes")
data class Recipe(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,                      // 레시피 이름
    val description: String,               // 레시피 설명
    val prepTime: Int,                     // 준비 시간(분)
    val cookTime: Int,                     // 조리 시간(분)
    val servings: Int,                     // 인분 수
    val difficulty: String,                // 난이도(쉬움, 중간, 어려움)
    val imageUrl: String?,                 // 레시피 이미지 URL
    val isFavorite: Boolean = false,       // 즐겨찾기 여부
    val source: String?,                   // 레시피 출처
    val notes: String?,                    // 메모
    val createdAt: Long = System.currentTimeMillis(),  // 생성 시간
    val updatedAt: Long = System.currentTimeMillis()   // 수정 시간
)
