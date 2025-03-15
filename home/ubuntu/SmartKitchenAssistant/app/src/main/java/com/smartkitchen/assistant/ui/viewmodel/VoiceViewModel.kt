package com.smartkitchen.assistant.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartkitchen.assistant.data.voice.VoiceRecognitionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 음성 인식 화면을 위한 ViewModel
 * 음성 명령 인식 및 처리 기능을 관리합니다.
 */
@HiltViewModel
class VoiceViewModel @Inject constructor(
    private val voiceRecognitionManager: VoiceRecognitionManager
) : ViewModel(), VoiceRecognitionManager.VoiceCommandListener {
    
    // 인식된 텍스트
    val recognizedText = voiceRecognitionManager.recognizedText
    
    // 음성 인식 상태
    val isListening = voiceRecognitionManager.isListening
    
    // 오류 메시지
    val errorMessage = voiceRecognitionManager.errorMessage
    
    // 명령 처리 결과
    private val _commandResult = MutableStateFlow<String?>(null)
    val commandResult: StateFlow<String?> = _commandResult
    
    // 명령 처리 상태
    private val _isProcessingCommand = MutableStateFlow(false)
    val isProcessingCommand: StateFlow<Boolean> = _isProcessingCommand
    
    init {
        // 음성 명령 처리 리스너 설정
        voiceRecognitionManager.setCommandListener(this)
    }
    
    /**
     * 음성 인식기를 초기화합니다.
     */
    fun initialize(context: Context) {
        voiceRecognitionManager.initialize(context)
    }
    
    /**
     * 음성 인식을 시작합니다.
     */
    fun startListening(context: Context) {
        voiceRecognitionManager.startListening(context)
    }
    
    /**
     * 음성 인식을 중지합니다.
     */
    fun stopListening() {
        voiceRecognitionManager.stopListening()
    }
    
    /**
     * 음성 명령이 인식되었을 때 호출됩니다.
     */
    override fun onCommandRecognized(command: VoiceRecognitionManager.VoiceCommand) {
        _isProcessingCommand.value = true
        
        // 명령 처리 결과 생성
        val result = when (command.type) {
            VoiceRecognitionManager.CommandType.SEARCH_RECIPE -> {
                if (command.parameter.isNullOrBlank()) {
                    "검색어를 말씀해주세요."
                } else {
                    "'${command.parameter}' 레시피를 검색합니다."
                    // TODO: 실제 레시피 검색 기능 호출
                }
            }
            VoiceRecognitionManager.CommandType.SHOW_RECIPE -> {
                if (command.parameter.isNullOrBlank()) {
                    "어떤 레시피를 보여드릴까요?"
                } else {
                    "'${command.parameter}' 레시피를 보여드립니다."
                    // TODO: 실제 레시피 표시 기능 호출
                }
            }
            VoiceRecognitionManager.CommandType.START_TIMER -> {
                if (command.parameter.isNullOrBlank()) {
                    "타이머 시간을 말씀해주세요."
                } else {
                    "${command.parameter} 타이머를 시작합니다."
                    // TODO: 실제 타이머 시작 기능 호출
                }
            }
            VoiceRecognitionManager.CommandType.STOP_TIMER -> {
                "타이머를 중지합니다."
                // TODO: 실제 타이머 중지 기능 호출
            }
            VoiceRecognitionManager.CommandType.NEXT_STEP -> {
                "다음 단계로 이동합니다."
                // TODO: 실제 다음 단계 이동 기능 호출
            }
            VoiceRecognitionManager.CommandType.PREVIOUS_STEP -> {
                "이전 단계로 이동합니다."
                // TODO: 실제 이전 단계 이동 기능 호출
            }
            VoiceRecognitionManager.CommandType.REPEAT_STEP -> {
                "현재 단계를 반복합니다."
                // TODO: 실제 단계 반복 기능 호출
            }
            VoiceRecognitionManager.CommandType.SHOW_INGREDIENTS -> {
                "재료 목록을 보여드립니다."
                // TODO: 실제 재료 목록 표시 기능 호출
            }
            VoiceRecognitionManager.CommandType.HELP -> {
                "사용 가능한 명령어: 레시피 검색, 레시피 보여줘, 타이머 시작, 타이머 중지, 다음 단계, 이전 단계, 단계 반복, 재료 목록"
            }
            VoiceRecognitionManager.CommandType.UNKNOWN -> {
                "인식할 수 없는 명령입니다. '도움말'이라고 말씀하시면 사용 가능한 명령어를 알려드립니다."
            }
        }
        
        _commandResult.value = result
        _isProcessingCommand.value = false
    }
    
    /**
     * 명령 처리 결과를 초기화합니다.
     */
    fun clearCommandResult() {
        _commandResult.value = null
    }
    
    /**
     * 오류 메시지를 초기화합니다.
     */
    fun clearErrorMessage() {
        voiceRecognitionManager.clearErrorMessage()
    }
    
    override fun onCleared() {
        super.onCleared()
        // ViewModel이 소멸될 때 음성 인식기 해제
        voiceRecognitionManager.release()
    }
}
