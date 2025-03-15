package com.smartkitchen.assistant.data.repository

import com.smartkitchen.assistant.data.local.TimerDao
import com.smartkitchen.assistant.data.model.Timer
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 타이머 저장소
 * 타이머 관련 데이터 작업을 처리하고 데이터 소스를 추상화합니다.
 */
@Singleton
class TimerRepository @Inject constructor(
    private val timerDao: TimerDao
) {
    /**
     * 모든 타이머 목록을 가져옵니다.
     */
    fun getAllTimers(): Flow<List<Timer>> = timerDao.getAllTimers()
    
    /**
     * 실행 중인 타이머 목록을 가져옵니다.
     */
    fun getRunningTimers(): Flow<List<Timer>> = timerDao.getRunningTimers()
    
    /**
     * 타이머 ID로 타이머를 가져옵니다.
     */
    suspend fun getTimerById(timerId: Long): Timer? = timerDao.getTimerById(timerId)
    
    /**
     * 레시피에 연결된 타이머 목록을 가져옵니다.
     */
    fun getTimersForRecipe(recipeId: Long): Flow<List<Timer>> = timerDao.getTimersForRecipe(recipeId)
    
    /**
     * 레시피 단계에 연결된 타이머 목록을 가져옵니다.
     */
    fun getTimersForRecipeStep(recipeStepId: Long): Flow<List<Timer>> = 
        timerDao.getTimersForRecipeStep(recipeStepId)
    
    /**
     * 타이머를 저장합니다.
     */
    suspend fun insertTimer(timer: Timer): Long = timerDao.insertTimer(timer)
    
    /**
     * 타이머를 업데이트합니다.
     */
    suspend fun updateTimer(timer: Timer) = timerDao.updateTimer(timer)
    
    /**
     * 타이머를 삭제합니다.
     */
    suspend fun deleteTimer(timer: Timer) = timerDao.deleteTimer(timer)
    
    /**
     * 모든 타이머를 정지합니다.
     */
    suspend fun stopAllTimers() = timerDao.stopAllTimers()
    
    /**
     * 타이머를 시작합니다.
     */
    suspend fun startTimer(timerId: Long, startTime: Long = System.currentTimeMillis()) = 
        timerDao.startTimer(timerId, startTime)
    
    /**
     * 타이머를 일시정지합니다.
     */
    suspend fun pauseTimer(timerId: Long, remainingTime: Long) = 
        timerDao.pauseTimer(timerId, remainingTime)
    
    /**
     * 타이머를 재설정합니다.
     */
    suspend fun resetTimer(timerId: Long) = timerDao.resetTimer(timerId)
    
    /**
     * 레시피에 연결된 타이머를 삭제합니다.
     */
    suspend fun deleteTimersForRecipe(recipeId: Long) = timerDao.deleteTimersForRecipe(recipeId)
    
    /**
     * 레시피 단계에 대한 타이머를 생성합니다.
     */
    suspend fun createTimerForRecipeStep(
        recipeId: Long,
        recipeStepId: Long,
        stepName: String,
        durationSeconds: Int
    ): Long {
        val timer = Timer(
            name = stepName,
            duration = durationSeconds * 1000L, // 초를 밀리초로 변환
            recipeId = recipeId,
            recipeStepId = recipeStepId,
            isRunning = false,
            startTime = null,
            remainingTime = null,
            soundUri = null
        )
        return insertTimer(timer)
    }
}
