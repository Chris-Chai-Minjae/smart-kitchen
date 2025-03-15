package com.smartkitchen.assistant.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 타이머 엔티티 클래스
 * 요리 과정에서 사용되는 타이머 정보를 저장합니다.
 */
@Entity(tableName = "timers")
data class Timer(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,                      // 타이머 이름
    val duration: Long,                    // 타이머 시간(밀리초)
    val recipeId: Long?,                   // 연결된 레시피 ID
    val recipeStepId: Long?,               // 연결된 레시피 단계 ID
    val isRunning: Boolean = false,        // 실행 중 여부
    val startTime: Long?,                  // 시작 시간
    val remainingTime: Long?,              // 남은 시간
    val soundUri: String?,                 // 알림음 URI
    val vibration: Boolean = true,         // 진동 여부
    val createdAt: Long = System.currentTimeMillis()   // 생성 시간
)
