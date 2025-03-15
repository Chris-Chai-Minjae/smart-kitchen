package com.smartkitchen.assistant.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * 레시피-재료 연결 엔티티 클래스
 * 레시피와 재료 간의 다대다 관계를 표현하며, 각 재료의 수량과 단위 정보를 저장합니다.
 */
@Entity(
    tableName = "recipe_ingredients",
    foreignKeys = [
        ForeignKey(
            entity = Recipe::class,
            parentColumns = ["id"],
            childColumns = ["recipeId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Ingredient::class,
            parentColumns = ["id"],
            childColumns = ["ingredientId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("recipeId"), Index("ingredientId")]
)
data class RecipeIngredient(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val recipeId: Long,                    // 레시피 ID
    val ingredientId: Long,                // 재료 ID
    val quantity: Double,                  // 수량
    val unit: String,                      // 단위(g, ml, 개 등)
    val isOptional: Boolean = false        // 선택 재료 여부
)
