package com.smartkitchen.assistant.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 쇼핑 목록 아이템 엔티티 클래스
 * 구매할 식품 목록 정보를 저장합니다.
 */
@Entity(tableName = "shopping_list_items")
data class ShoppingListItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val ingredientId: Long?,               // 재료 ID (연결된 재료가 있는 경우)
    val name: String,                      // 아이템 이름
    val quantity: Double,                  // 수량
    val unit: String,                      // 단위
    val isChecked: Boolean = false,        // 구매 완료 여부
    val category: String?,                 // 카테고리
    val notes: String?,                    // 메모
    val createdAt: Long = System.currentTimeMillis()   // 생성 시간
)
