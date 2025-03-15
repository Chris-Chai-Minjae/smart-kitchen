package com.smartkitchen.assistant.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * 레시피 단계 엔티티 클래스
 * 요리 레시피의 각 단계 정보를 저장합니다.
 */
@Entity(
    tableName = "recipe_steps",
    foreignKeys = [
        ForeignKey(
            entity = Recipe::class,
            parentColumns = ["id"],
            childColumns = ["recipeId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("recipeId")]
)
data class RecipeStep(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val recipeId: Long,                    // 레시피 ID
    val stepNumber: Int,                   // 단계 번호
    val description: String,               // 단계 설명
    val imageUrl: String?,                 // 단계 이미지 URL
    val videoUrl: String?,                 // 단계 비디오 URL
    val timerDuration: Int? = null         // 타이머 시간(초)
)
