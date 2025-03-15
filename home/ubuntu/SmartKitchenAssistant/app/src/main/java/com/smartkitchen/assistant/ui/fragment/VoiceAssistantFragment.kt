package com.smartkitchen.assistant.ui.fragment

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.smartkitchen.assistant.databinding.FragmentVoiceAssistantBinding
import com.smartkitchen.assistant.ui.viewmodel.VoiceViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.Locale

/**
 * 음성 인식 화면
 * 음성 명령 인식 및 처리 기능을 제공합니다.
 */
@AndroidEntryPoint
class VoiceAssistantFragment : Fragment(), TextToSpeech.OnInitListener {
    
    private var _binding: FragmentVoiceAssistantBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: VoiceViewModel by viewModels()
    
    // 음성 합성 엔진
    private var textToSpeech: TextToSpeech? = null
    
    // 음성 인식 권한 요청 결과 처리
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // 권한 획득 성공
            startVoiceRecognition()
        } else {
            // 권한 획득 실패
            Toast.makeText(requireContext(), "음성 인식을 위해 마이크 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
        }
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVoiceAssistantBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // 음성 인식기 초기화
        viewModel.initialize(requireContext())
        
        // 음성 합성 엔진 초기화
        textToSpeech = TextToSpeech(requireContext(), this)
        
        setupUI()
        setupObservers()
    }
    
    private fun setupUI() {
        // 음성 인식 시작 버튼 클릭 리스너
        binding.buttonStartListening.setOnClickListener {
            checkMicrophonePermission()
        }
        
        // 음성 인식 중지 버튼 클릭 리스너
        binding.buttonStopListening.setOnClickListener {
            viewModel.stopListening()
        }
        
        // 도움말 버튼 클릭 리스너
        binding.buttonHelp.setOnClickListener {
            showHelpDialog()
        }
    }
    
    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            // 인식된 텍스트 관찰
            viewModel.recognizedText.collect { text ->
                text?.let {
                    binding.textViewRecognizedText.text = it
                }
            }
        }
        
        viewLifecycleOwner.lifecycleScope.launch {
            // 음성 인식 상태 관찰
            viewModel.isListening.collect { isListening ->
                updateListeningState(isListening)
            }
        }
        
        viewLifecycleOwner.lifecycleScope.launch {
            // 오류 메시지 관찰
            viewModel.errorMessage.collect { message ->
                message?.let {
                    Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                    viewModel.clearErrorMessage()
                }
            }
        }
        
        viewLifecycleOwner.lifecycleScope.launch {
            // 명령 처리 결과 관찰
            viewModel.commandResult.collect { result ->
                result?.let {
                    binding.textViewCommandResult.text = it
                    speakText(it)
                    viewModel.clearCommandResult()
                }
            }
        }
        
        viewLifecycleOwner.lifecycleScope.launch {
            // 명령 처리 상태 관찰
            viewModel.isProcessingCommand.collect { isProcessing ->
                binding.progressBarCommand.visibility = if (isProcessing) View.VISIBLE else View.GONE
            }
        }
    }
    
    private fun checkMicrophonePermission() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED -> {
                // 권한이 이미 있는 경우
                startVoiceRecognition()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO) -> {
                // 권한 요청 이유 설명이 필요한 경우
                Toast.makeText(requireContext(), "음성 인식을 위해 마이크 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
                requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
            else -> {
                // 권한 요청
                requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
        }
    }
    
    private fun startVoiceRecognition() {
        viewModel.startListening(requireContext())
    }
    
    private fun updateListeningState(isListening: Boolean) {
        binding.buttonStartListening.isEnabled = !isListening
        binding.buttonStopListening.isEnabled = isListening
        
        binding.imageViewMic.setImageResource(
            if (isListening) {
                android.R.drawable.ic_btn_speak_now
            } else {
                android.R.drawable.ic_lock_silent_mode_off
            }
        )
        
        binding.textViewListeningStatus.text = if (isListening) {
            "듣고 있습니다..."
        } else {
            "음성 인식 대기 중"
        }
    }
    
    private fun showHelpDialog() {
        val helpText = """
            사용 가능한 음성 명령어:
            
            1. 레시피 검색 [검색어]
            2. 레시피 보여줘 [레시피 이름]
            3. 타이머 시작 [시간]
            4. 타이머 중지
            5. 다음 단계
            6. 이전 단계
            7. 단계 반복
            8. 재료 목록
            9. 도움말
        """.trimIndent()
        
        binding.textViewCommandResult.text = helpText
        speakText("사용 가능한 음성 명령어를 화면에 표시합니다.")
    }
    
    private fun speakText(text: String) {
        textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
    }
    
    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            // 음성 합성 엔진 초기화 성공
            val result = textToSpeech?.setLanguage(Locale.KOREAN)
            
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                // 언어 데이터가 없거나 지원되지 않는 경우
                Toast.makeText(requireContext(), "한국어 음성 합성을 사용할 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
        } else {
            // 음성 합성 엔진 초기화 실패
            Toast.makeText(requireContext(), "음성 합성 엔진을 초기화할 수 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        // 음성 인식 중지
        viewModel.stopListening()
        
        // 음성 합성 엔진 해제
        textToSpeech?.stop()
        textToSpeech?.shutdown()
        
        _binding = null
    }
}
