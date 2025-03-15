package com.smartkitchen.assistant.test

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.smartkitchen.assistant.data.model.Timer
import com.smartkitchen.assistant.data.repository.TimerRepository
import com.smartkitchen.assistant.ui.viewmodel.TimerViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
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
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner

/**
 * 타이머 뷰모델 단위 테스트
 */
@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class TimerViewModelTest {

    // 테스트용 코루틴 디스패처
    private val testDispatcher = TestCoroutineDispatcher()

    // LiveData 테스트를 위한 규칙
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    // 모의 객체
    @Mock
    private lateinit var timerRepository: TimerRepository

    // 테스트 대상
    private lateinit var viewModel: TimerViewModel

    @Before
    fun setup() {
        // 메인 디스패처를 테스트 디스패처로 설정
        Dispatchers.setMain(testDispatcher)
        
        // 뷰모델 초기화
        viewModel = TimerViewModel(timerRepository)
    }

    @After
    fun tearDown() {
        // 메인 디스패처 초기화
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun `getAllTimers should update timers state when repository returns data`() = testDispatcher.runBlockingTest {
        // Given
        val testTimers = listOf(
            Timer(
                id = 1,
                name = "테스트 타이머",
                durationSeconds = 300,
                remainingSeconds = 300,
                isRunning = false,
                recipeId = 1
            )
        )
        `when`(timerRepository.getAllTimers()).thenReturn(flowOf(testTimers))

        // When
        viewModel.getAllTimers()

        // Then
        assert(viewModel.timers.value == testTimers)
    }

    @Test
    fun `createTimer should call repository createTimer method`() = testDispatcher.runBlockingTest {
        // Given
        val timerName = "테스트 타이머"
        val durationSeconds = 300
        val recipeId = 1L

        // When
        viewModel.createTimer(timerName, durationSeconds, recipeId)

        // Then
        verify(timerRepository).createTimer(timerName, durationSeconds, recipeId)
    }

    @Test
    fun `startTimer should call repository startTimer method`() = testDispatcher.runBlockingTest {
        // Given
        val timerId = 1L

        // When
        viewModel.startTimer(timerId)

        // Then
        verify(timerRepository).startTimer(timerId)
    }

    @Test
    fun `pauseTimer should call repository pauseTimer method`() = testDispatcher.runBlockingTest {
        // Given
        val timerId = 1L

        // When
        viewModel.pauseTimer(timerId)

        // Then
        verify(timerRepository).pauseTimer(timerId)
    }

    @Test
    fun `resetTimer should call repository resetTimer method`() = testDispatcher.runBlockingTest {
        // Given
        val timerId = 1L

        // When
        viewModel.resetTimer(timerId)

        // Then
        verify(timerRepository).resetTimer(timerId)
    }

    @Test
    fun `deleteTimer should call repository deleteTimer method`() = testDispatcher.runBlockingTest {
        // Given
        val timerId = 1L

        // When
        viewModel.deleteTimer(timerId)

        // Then
        verify(timerRepository).deleteTimer(timerId)
    }
}
