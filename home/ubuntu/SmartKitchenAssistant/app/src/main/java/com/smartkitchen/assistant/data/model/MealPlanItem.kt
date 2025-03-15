package com.smartkitchen.assistant.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * 식단 계획 아이템 엔티티 클래스
 * 식단 계획에 포함된 각 식사 항목의 정보를 저장합니다.
 */
@Entity(
    tableName = "meal_plan_items",
    foreignKeys = [
        ForeignKey(
            entity = MealPlan::class,
            parentColumns = ["id"],
            childColumns = ["mealPlanId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Recipe::class,
            parentColumns = ["id"],
            childColumns = ["recipeId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("mealPlanId"), Index("recipeId")]
)
data class MealPlanItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val mealPlanId: Long,                  // 식단 계획 ID
    val recipeId: Long?,                   // 레시피 ID
    val mealType: String,                  // 식사 유형(아침, 점심, 저녁, 간식)
    val servings: Int,                     // 인분 수
    val notes: String?                     // 메모
)
