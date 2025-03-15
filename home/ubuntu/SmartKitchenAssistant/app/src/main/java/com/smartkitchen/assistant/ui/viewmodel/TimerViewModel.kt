package com.smartkitchen.assistant.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartkitchen.assistant.data.model.Timer
import com.smartkitchen.assistant.data.repository.TimerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 타이머 화면을 위한 ViewModel
 * 타이머 관련 데이터를 관리하고 UI와 비즈니스 로직을 연결합니다.
 */
@HiltViewModel
class TimerViewModel @Inject constructor(
    private val timerRepository: TimerRepository
) : ViewModel() {
    // 모든 타이머 목록
    private val _timers = MutableStateFlow<List<Timer>>(emptyList())
    val timers: StateFlow<List<Timer>> = _timers
    
    // 실행 중인 타이머 목록
    private val _runningTimers = MutableStateFlow<List<Timer>>(emptyList())
    val runningTimers: StateFlow<List<Timer>> = _runningTimers
    
    // 현재 타이머 시간 업데이트 (UI 표시용)
    private val _timerUpdates = MutableStateFlow<Map<Long, Long>>(emptyMap())
    val timerUpdates: StateFlow<Map<Long, Long>> = _timerUpdates
    
    // 타이머 완료 이벤트
    private val _timerCompletedEvent = MutableStateFlow<Long?>(null)
    val timerCompletedEvent: StateFlow<Long?> = _timerCompletedEvent
    
    // 로딩 상태
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    
    // 오류 메시지
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage
    
    // 타이머 업데이트 작업
    private var timerUpdateJob: Job? = null
    
    init {
        loadTimers()
        loadRunningTimers()
        startTimerUpdates()
    }
    
    /**
     * 모든 타이머를 로드합니다.
     */
    fun loadTimers() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                timerRepository.getAllTimers().collect { timerList ->
                    _timers.value = timerList
                }
            } catch (e: Exception) {
                _errorMessage.value = "타이머를 로드하는 중 오류가 발생했습니다: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * 실행 중인 타이머를 로드합니다.
     */
    fun loadRunningTimers() {
        viewModelScope.launch {
            try {
                timerRepository.getRunningTimers().collect { timerList ->
                    _runningTimers.value = timerList
                }
            } catch (e: Exception) {
                _errorMessage.value = "실행 중인 타이머를 로드하는 중 오류가 발생했습니다: ${e.message}"
            }
        }
    }
    
    /**
     * 타이머를 생성합니다.
     */
    fun createTimer(name: String, durationSeconds: Int): Long {
        var timerId = 0L
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val timer = Timer(
                    name = name,
                    duration = durationSeconds * 1000L, // 초를 밀리초로 변환
                    recipeId = null,
                    recipeStepId = null,
                    isRunning = false,
                    startTime = null,
                    remainingTime = null,
                    soundUri = null
                )
                timerId = timerRepository.insertTimer(timer)
                loadTimers()
            } catch (e: Exception) {
                _errorMessage.value = "타이머를 생성하는 중 오류가 발생했습니다: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
        return timerId
    }
    
    /**
     * 타이머를 시작합니다.
     */
    fun startTimer(timerId: Long) {
        viewModelScope.launch {
            try {
                timerRepository.startTimer(timerId, System.currentTimeMillis())
                loadRunningTimers()
            } catch (e: Exception) {
                _errorMessage.value = "타이머를 시작하는 중 오류가 발생했습니다: ${e.message}"
            }
        }
    }
    
    /**
     * 타이머를 일시정지합니다.
     */
    fun pauseTimer(timerId: Long, remainingTime: Long) {
        viewModelScope.launch {
            try {
                timerRepository.pauseTimer(timerId, remainingTime)
                loadRunningTimers()
            } catch (e: Exception) {
                _errorMessage.value = "타이머를 일시정지하는 중 오류가 발생했습니다: ${e.message}"
            }
        }
    }
    
    /**
     * 타이머를 재설정합니다.
     */
    fun resetTimer(timerId: Long) {
        viewModelScope.launch {
            try {
                timerRepository.resetTimer(timerId)
                loadRunningTimers()
                
                // 타이머 업데이트에서 제거
                val currentUpdates = _timerUpdates.value.toMutableMap()
                currentUpdates.remove(timerId)
                _timerUpdates.value = currentUpdates
            } catch (e: Exception) {
                _errorMessage.value = "타이머를 재설정하는 중 오류가 발생했습니다: ${e.message}"
            }
        }
    }
    
    /**
     * 타이머를 삭제합니다.
     */
    fun deleteTimer(timer: Timer) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                timerRepository.deleteTimer(timer)
                loadTimers()
                loadRunningTimers()
                
                // 타이머 업데이트에서 제거
                val currentUpdates = _timerUpdates.value.toMutableMap()
                currentUpdates.remove(timer.id)
                _timerUpdates.value = currentUpdates
            } catch (e: Exception) {
                _errorMessage.value = "타이머를 삭제하는 중 오류가 발생했습니다: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * 모든 타이머를 정지합니다.
     */
    fun stopAllTimers() {
        viewModelScope.launch {
            try {
                timerRepository.stopAllTimers()
                loadRunningTimers()
                
                // 모든 타이머 업데이트 제거
                _timerUpdates.value = emptyMap()
            } catch (e: Exception) {
                _errorMessage.value = "모든 타이머를 정지하는 중 오류가 발생했습니다: ${e.message}"
            }
        }
    }
    
    /**
     * 타이머 업데이트 작업을 시작합니다.
     * 1초마다 실행 중인 타이머의 남은 시간을 업데이트합니다.
     */
    private fun startTimerUpdates() {
        timerUpdateJob?.cancel()
        timerUpdateJob = viewModelScope.launch {
            while (true) {
                updateRunningTimers()
                delay(1000) // 1초마다 업데이트
            }
        }
    }
    
    /**
     * 실행 중인 타이머의 남은 시간을 업데이트합니다.
     */
    private fun updateRunningTimers() {
        val currentTime = System.currentTimeMillis()
        val currentUpdates = _timerUpdates.value.toMutableMap()
        
        _runningTimers.value.forEach { timer ->
            if (timer.isRunning && timer.startTime != null) {
                val elapsedTime = currentTime - timer.startTime
                val remainingTime = (timer.duration - elapsedTime).coerceAtLeast(0)
                
                currentUpdates[timer.id] = remainingTime
                
                // 타이머가 완료된 경우
                if (remainingTime == 0L) {
                    viewModelScope.launch {
                        timerRepository.resetTimer(timer.id)
                        _timerCompletedEvent.value = timer.id
                    }
                }
            } else if (timer.remainingTime != null) {
                // 일시정지된 타이머
                currentUpdates[timer.id] = timer.remainingTime
            }
        }
        
        _timerUpdates.value = currentUpdates
    }
    
    /**
     * 타이머 완료 이벤트를 처리한 후 초기화합니다.
     */
    fun onTimerCompletedEventHandled() {
        _timerCompletedEvent.value = null
    }
    
    /**
     * 오류 메시지를 초기화합니다.
     */
    fun clearErrorMessage() {
        _errorMessage.value = null
    }
    
    override fun onCleared() {
        super.onCleared()
        timerUpdateJob?.cancel()
    }
}
