package com.smartkitchen.assistant.data.local

import androidx.room.*
import com.smartkitchen.assistant.data.model.Timer
import kotlinx.coroutines.flow.Flow

/**
 * 타이머 데이터 접근 객체 (DAO)
 * 타이머 관련 데이터베이스 작업을 처리합니다.
 */
@Dao
interface TimerDao {
    @Insert
    suspend fun insertTimer(timer: Timer): Long
    
    @Update
    suspend fun updateTimer(timer: Timer)
    
    @Delete
    suspend fun deleteTimer(timer: Timer)
    
    @Query("SELECT * FROM timers ORDER BY createdAt DESC")
    fun getAllTimers(): Flow<List<Timer>>
    
    @Query("SELECT * FROM timers WHERE isRunning = 1")
    fun getRunningTimers(): Flow<List<Timer>>
    
    @Query("SELECT * FROM timers WHERE id = :timerId")
    suspend fun getTimerById(timerId: Long): Timer?
    
    @Query("UPDATE timers SET isRunning = 0")
    suspend fun stopAllTimers()
    
    @Query("UPDATE timers SET isRunning = 1, startTime = :startTime WHERE id = :timerId")
    suspend fun startTimer(timerId: Long, startTime: Long)
    
    @Query("UPDATE timers SET isRunning = 0, remainingTime = :remainingTime WHERE id = :timerId")
    suspend fun pauseTimer(timerId: Long, remainingTime: Long)
    
    @Query("UPDATE timers SET isRunning = 0, remainingTime = null WHERE id = :timerId")
    suspend fun resetTimer(timerId: Long)
    
    @Query("SELECT * FROM timers WHERE recipeId = :recipeId")
    fun getTimersForRecipe(recipeId: Long): Flow<List<Timer>>
    
    @Query("SELECT * FROM timers WHERE recipeStepId = :recipeStepId")
    fun getTimersForRecipeStep(recipeStepId: Long): Flow<List<Timer>>
    
    @Query("DELETE FROM timers WHERE recipeId = :recipeId")
    suspend fun deleteTimersForRecipe(recipeId: Long)
}
