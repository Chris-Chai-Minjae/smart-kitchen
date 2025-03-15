package com.smartkitchen.assistant.test.integration

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.smartkitchen.assistant.data.bluetooth.BluetoothManager
import com.smartkitchen.assistant.ui.viewmodel.BluetoothViewModel
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
 * 블루투스 기능 통합 테스트
 * 블루투스 관리자와 뷰모델 간의 통합을 테스트합니다.
 */
@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class BluetoothIntegrationTest {

    // 테스트용 코루틴 디스패처
    private val testDispatcher = TestCoroutineDispatcher()

    // LiveData 테스트를 위한 규칙
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    // 모의 객체
    @Mock
    private lateinit var bluetoothManager: BluetoothManager

    // 테스트 대상
    private lateinit var bluetoothViewModel: BluetoothViewModel

    @Before
    fun setup() {
        // 메인 디스패처를 테스트 디스패처로 설정
        Dispatchers.setMain(testDispatcher)
        
        // 모의 객체 설정
        `when`(bluetoothManager.isScanning).thenReturn(MutableStateFlow(false))
        `when`(bluetoothManager.isConnected).thenReturn(MutableStateFlow(false))
        `when`(bluetoothManager.discoveredDevices).thenReturn(MutableStateFlow(emptyList()))
        `when`(bluetoothManager.connectedDevice).thenReturn(MutableStateFlow(null))
        `when`(bluetoothManager.receivedData).thenReturn(MutableStateFlow(null))
        `when`(bluetoothManager.errorMessage).thenReturn(MutableStateFlow(null))
        
        // 뷰모델 초기화
        bluetoothViewModel = BluetoothViewModel(bluetoothManager)
    }

    @After
    fun tearDown() {
        // 메인 디스패처 초기화
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun `startScan should call bluetoothManager startScan method`() = testDispatcher.runBlockingTest {
        // When
        bluetoothViewModel.startScan()

        // Then
        verify(bluetoothManager).startScan()
    }

    @Test
    fun `stopScan should call bluetoothManager stopScan method`() = testDispatcher.runBlockingTest {
        // When
        bluetoothViewModel.stopScan()

        // Then
        verify(bluetoothManager).stopScan()
    }

    @Test
    fun `connectToDevice should call bluetoothManager connectToDevice method`() = testDispatcher.runBlockingTest {
        // Given
        val deviceAddress = "00:11:22:33:44:55"

        // When
        bluetoothViewModel.connectToDevice(deviceAddress)

        // Then
        verify(bluetoothManager).connectToDevice(deviceAddress)
    }

    @Test
    fun `disconnectDevice should call bluetoothManager disconnectDevice method`() = testDispatcher.runBlockingTest {
        // When
        bluetoothViewModel.disconnectDevice()

        // Then
        verify(bluetoothManager).disconnectDevice()
    }

    @Test
    fun `clearErrorMessage should call bluetoothManager clearErrorMessage method`() = testDispatcher.runBlockingTest {
        // When
        bluetoothViewModel.clearErrorMessage()

        // Then
        verify(bluetoothManager).clearErrorMessage()
    }
}
