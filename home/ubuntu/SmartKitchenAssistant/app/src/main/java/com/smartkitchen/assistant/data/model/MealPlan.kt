package com.smartkitchen.assistant.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 식단 계획 엔티티 클래스
 * 특정 날짜의 식단 계획 정보를 저장합니다.
 */
@Entity(tableName = "meal_plans")
data class MealPlan(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: Long,                        // 날짜
    val createdAt: Long = System.currentTimeMillis(),  // 생성 시간
    val updatedAt: Long = System.currentTimeMillis()   // 수정 시간
)
