package com.smartkitchen.assistant.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 재료 엔티티 클래스
 * 요리에 사용되는 재료의 기본 정보를 저장합니다.
 */
@Entity(tableName = "ingredients")
data class Ingredient(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,                      // 재료 이름
    val category: String,                  // 재료 카테고리
    val imageUrl: String?                  // 재료 이미지 URL
)
