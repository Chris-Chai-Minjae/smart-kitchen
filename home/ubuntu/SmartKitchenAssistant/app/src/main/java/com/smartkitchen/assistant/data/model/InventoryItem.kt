package com.smartkitchen.assistant.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 재고 아이템 엔티티 클래스
 * 주방에 보관 중인 식품 재고 정보를 저장합니다.
 */
@Entity(tableName = "inventory_items")
data class InventoryItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val ingredientId: Long?,               // 재료 ID (연결된 재료가 있는 경우)
    val name: String,                      // 아이템 이름
    val quantity: Double,                  // 수량
    val unit: String,                      // 단위
    val purchaseDate: Long?,               // 구매 날짜
    val expiryDate: Long?,                 // 유통기한
    val barcode: String?,                  // 바코드
    val storageLocation: String?,          // 보관 위치(냉장, 냉동, 실온 등)
    val notes: String?,                    // 메모
    val createdAt: Long = System.currentTimeMillis(),  // 생성 시간
    val updatedAt: Long = System.currentTimeMillis()   // 수정 시간
)
