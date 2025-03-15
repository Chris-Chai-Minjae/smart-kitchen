package com.smartkitchen.assistant.test

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.smartkitchen.assistant.data.voice.VoiceRecognitionManager
import com.smartkitchen.assistant.ui.viewmodel.VoiceViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

/**
 * 음성 인식 뷰모델 단위 테스트
 */
@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class VoiceViewModelTest {

    // 테스트용 코루틴 디스패처
    private val testDispatcher = TestCoroutineDispatcher()

    // LiveData 테스트를 위한 규칙
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    // 모의 객체
    @Mock
    private lateinit var voiceRecognitionManager: VoiceRecognitionManager

    // 테스트 대상
    private lateinit var viewModel: VoiceViewModel

    @Before
    fun setup() {
        // 메인 디스패처를 테스트 디스패처로 설정
        Dispatchers.setMain(testDispatcher)
        
        // 모의 객체 설정
        `when`(voiceRecognitionManager.recognizedText).thenReturn(MutableStateFlow(null))
        `when`(voiceRecognitionManager.isListening).thenReturn(MutableStateFlow(false))
        `when`(voiceRecognitionManager.errorMessage).thenReturn(MutableStateFlow(null))
        
        // 뷰모델 초기화
        viewModel = VoiceViewModel(voiceRecognitionManager)
    }

    @After
    fun tearDown() {
        // 메인 디스패처 초기화
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun `initialize should call voiceRecognitionManager initialize method`() = testDispatcher.runBlockingTest {
        // Given
        val mockContext = android.content.Context::class.java.newInstance()

        // When
        viewModel.initialize(mockContext)

        // Then
        verify(voiceRecognitionManager).initialize(mockContext)
    }

    @Test
    fun `startListening should call voiceRecognitionManager startListening method`() = testDispatcher.runBlockingTest {
        // Given
        val mockContext = android.content.Context::class.java.newInstance()

        // When
        viewModel.startListening(mockContext)

        // Then
        verify(voiceRecognitionManager).startListening(mockContext)
    }

    @Test
    fun `stopListening should call voiceRecognitionManager stopListening method`() = testDispatcher.runBlockingTest {
        // When
        viewModel.stopListening()

        // Then
        verify(voiceRecognitionManager).stopListening()
    }

    @Test
    fun `clearErrorMessage should call voiceRecognitionManager clearErrorMessage method`() = testDispatcher.runBlockingTest {
        // When
        viewModel.clearErrorMessage()

        // Then
        verify(voiceRecognitionManager).clearErrorMessage()
    }

    @Test
    fun `onCommandRecognized should update commandResult state`() = testDispatcher.runBlockingTest {
        // Given
        val command = VoiceRecognitionManager.VoiceCommand(
            VoiceRecognitionManager.CommandType.HELP,
            null
        )

        // When
        viewModel.onCommandRecognized(command)

        // Then
        assert(viewModel.commandResult.value?.contains("사용 가능한 명령어") == true)
        assert(viewModel.isProcessingCommand.value == false)
    }
}
