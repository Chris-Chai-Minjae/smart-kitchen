package com.smartkitchen.assistant.data.voice

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 음성 인식 관리자
 * 음성 명령 인식 및 처리를 담당합니다.
 */
@Singleton
class VoiceRecognitionManager @Inject constructor() {
    
    // 음성 인식기
    private var speechRecognizer: SpeechRecognizer? = null
    
    // 인식된 텍스트
    private val _recognizedText = MutableStateFlow<String?>(null)
    val recognizedText: StateFlow<String?> = _recognizedText
    
    // 음성 인식 상태
    private val _isListening = MutableStateFlow(false)
    val isListening: StateFlow<Boolean> = _isListening
    
    // 오류 메시지
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage
    
    // 음성 명령 처리 리스너
    private var commandListener: VoiceCommandListener? = null
    
    /**
     * 음성 인식기를 초기화합니다.
     */
    fun initialize(context: Context) {
        if (SpeechRecognizer.isRecognitionAvailable(context)) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
            speechRecognizer?.setRecognitionListener(createRecognitionListener())
        } else {
            _errorMessage.value = "음성 인식 기능을 사용할 수 없습니다."
        }
    }
    
    /**
     * 음성 인식을 시작합니다.
     */
    fun startListening(context: Context) {
        speechRecognizer?.let { recognizer ->
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            }
            
            try {
                _isListening.value = true
                recognizer.startListening(intent)
            } catch (e: Exception) {
                _isListening.value = false
                _errorMessage.value = "음성 인식 시작 중 오류가 발생했습니다: ${e.message}"
            }
        } ?: run {
            initialize(context)
            _errorMessage.value = "음성 인식기가 초기화되지 않았습니다."
        }
    }
    
    /**
     * 음성 인식을 중지합니다.
     */
    fun stopListening() {
        speechRecognizer?.let { recognizer ->
            try {
                recognizer.stopListening()
            } catch (e: Exception) {
                _errorMessage.value = "음성 인식 중지 중 오류가 발생했습니다: ${e.message}"
            } finally {
                _isListening.value = false
            }
        }
    }
    
    /**
     * 음성 인식기를 해제합니다.
     */
    fun release() {
        speechRecognizer?.let { recognizer ->
            try {
                recognizer.cancel()
                recognizer.destroy()
            } catch (e: Exception) {
                _errorMessage.value = "음성 인식기 해제 중 오류가 발생했습니다: ${e.message}"
            } finally {
                speechRecognizer = null
                _isListening.value = false
            }
        }
    }
    
    /**
     * 음성 명령 처리 리스너를 설정합니다.
     */
    fun setCommandListener(listener: VoiceCommandListener) {
        commandListener = listener
    }
    
    /**
     * 음성 명령을 처리합니다.
     */
    private fun processCommand(text: String) {
        // 명령어 처리 로직
        val command = parseCommand(text)
        commandListener?.onCommandRecognized(command)
    }
    
    /**
     * 음성 텍스트에서 명령을 파싱합니다.
     */
    private fun parseCommand(text: String): VoiceCommand {
        val lowerText = text.lowercase(Locale.getDefault())
        
        return when {
            // 레시피 관련 명령
            lowerText.contains("레시피") && lowerText.contains("검색") -> 
                VoiceCommand(CommandType.SEARCH_RECIPE, extractSearchQuery(lowerText))
            
            lowerText.contains("레시피") && lowerText.contains("보여줘") -> 
                VoiceCommand(CommandType.SHOW_RECIPE, extractRecipeName(lowerText))
            
            // 타이머 관련 명령
            lowerText.contains("타이머") && (lowerText.contains("시작") || lowerText.contains("설정")) -> 
                VoiceCommand(CommandType.START_TIMER, extractTimerDuration(lowerText))
            
            lowerText.contains("타이머") && lowerText.contains("중지") -> 
                VoiceCommand(CommandType.STOP_TIMER, null)
            
            // 단계 관련 명령
            lowerText.contains("다음") && lowerText.contains("단계") -> 
                VoiceCommand(CommandType.NEXT_STEP, null)
            
            lowerText.contains("이전") && lowerText.contains("단계") -> 
                VoiceCommand(CommandType.PREVIOUS_STEP, null)
            
            lowerText.contains("단계") && lowerText.contains("반복") -> 
                VoiceCommand(CommandType.REPEAT_STEP, null)
            
            // 재료 관련 명령
            lowerText.contains("재료") && lowerText.contains("목록") -> 
                VoiceCommand(CommandType.SHOW_INGREDIENTS, null)
            
            // 기타 명령
            lowerText.contains("도움말") || lowerText.contains("명령어") -> 
                VoiceCommand(CommandType.HELP, null)
            
            // 알 수 없는 명령
            else -> VoiceCommand(CommandType.UNKNOWN, text)
        }
    }
    
    /**
     * 검색 쿼리를 추출합니다.
     */
    private fun extractSearchQuery(text: String): String {
        // "레시피 검색" 이후의 텍스트를 쿼리로 간주
        val searchIndex = text.indexOf("검색")
        return if (searchIndex != -1 && searchIndex + 2 < text.length) {
            text.substring(searchIndex + 2).trim()
        } else {
            ""
        }
    }
    
    /**
     * 레시피 이름을 추출합니다.
     */
    private fun extractRecipeName(text: String): String {
        // "레시피 보여줘" 이후의 텍스트를 레시피 이름으로 간주
        val showIndex = text.indexOf("보여줘")
        return if (showIndex != -1 && showIndex + 3 < text.length) {
            text.substring(showIndex + 3).trim()
        } else {
            ""
        }
    }
    
    /**
     * 타이머 시간을 추출합니다.
     */
    private fun extractTimerDuration(text: String): String {
        // 숫자와 시간 단위(분, 초)를 찾아 추출
        val regex = "(\\d+)\\s*(분|초)".toRegex()
        val matches = regex.findAll(text)
        
        val duration = StringBuilder()
        for (match in matches) {
            val value = match.groupValues[1]
            val unit = match.groupValues[2]
            duration.append("$value$unit ")
        }
        
        return duration.toString().trim()
    }
    
    /**
     * 음성 인식 리스너를 생성합니다.
     */
    private fun createRecognitionListener(): RecognitionListener {
        return object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                _errorMessage.value = null
            }
            
            override fun onBeginningOfSpeech() {
                // 음성 입력 시작
            }
            
            override fun onRmsChanged(rmsdB: Float) {
                // 음성 레벨 변경
            }
            
            override fun onBufferReceived(buffer: ByteArray?) {
                // 오디오 버퍼 수신
            }
            
            override fun onEndOfSpeech() {
                _isListening.value = false
            }
            
            override fun onError(error: Int) {
                _isListening.value = false
                _errorMessage.value = when (error) {
                    SpeechRecognizer.ERROR_AUDIO -> "오디오 녹음 오류"
                    SpeechRecognizer.ERROR_CLIENT -> "클라이언트 오류"
                    SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "권한 부족"
                    SpeechRecognizer.ERROR_NETWORK -> "네트워크 오류"
                    SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "네트워크 시간 초과"
                    SpeechRecognizer.ERROR_NO_MATCH -> "일치하는 결과 없음"
                    SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "인식기 사용 중"
                    SpeechRecognizer.ERROR_SERVER -> "서버 오류"
                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "음성 입력 시간 초과"
                    else -> "알 수 없는 오류: $error"
                }
            }
            
            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    val text = matches[0]
                    _recognizedText.value = text
                    processCommand(text)
                }
            }
            
            override fun onPartialResults(partialResults: Bundle?) {
                val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    _recognizedText.value = matches[0]
                }
            }
            
            override fun onEvent(eventType: Int, params: Bundle?) {
                // 이벤트 처리
            }
        }
    }
    
    /**
     * 오류 메시지를 초기화합니다.
     */
    fun clearErrorMessage() {
        _errorMessage.value = null
    }
    
    /**
     * 음성 명령 타입
     */
    enum class CommandType {
        SEARCH_RECIPE,      // 레시피 검색
        SHOW_RECIPE,        // 레시피 표시
        START_TIMER,        // 타이머 시작
        STOP_TIMER,         // 타이머 중지
        NEXT_STEP,          // 다음 단계
        PREVIOUS_STEP,      // 이전 단계
        REPEAT_STEP,        // 단계 반복
        SHOW_INGREDIENTS,   // 재료 목록 표시
        HELP,               // 도움말
        UNKNOWN             // 알 수 없는 명령
    }
    
    /**
     * 음성 명령 데이터 클래스
     */
    data class VoiceCommand(
        val type: CommandType,
        val parameter: String?
    )
    
    /**
     * 음성 명령 처리 리스너 인터페이스
     */
    interface VoiceCommandListener {
        fun onCommandRecognized(command: VoiceCommand)
    }
}
