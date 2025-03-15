package com.smartkitchen.assistant.ui.viewmodel

import android.bluetooth.BluetoothDevice
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartkitchen.assistant.data.bluetooth.BluetoothManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 블루투스 연결 화면을 위한 ViewModel
 * 블루투스 장치 검색, 연결, 데이터 수신 기능을 관리합니다.
 */
@HiltViewModel
class BluetoothViewModel @Inject constructor(
    private val bluetoothManager: BluetoothManager
) : ViewModel() {
    // 블루투스 연결 상태
    val connectionState = bluetoothManager.connectionState
    
    // 발견된 장치 목록
    val discoveredDevices = bluetoothManager.discoveredDevices
    
    // 수신된 데이터
    val receivedData = bluetoothManager.receivedData
    
    // 온도계 데이터
    val temperatureData = bluetoothManager.temperatureData
    
    // 스케일 데이터
    val scaleData = bluetoothManager.scaleData
    
    // 장치 타입
    val deviceType = bluetoothManager.deviceType
    
    // 오류 메시지
    val errorMessage = bluetoothManager.errorMessage
    
    // 선택된 장치
    private val _selectedDevice = MutableStateFlow<BluetoothDevice?>(null)
    val selectedDevice: StateFlow<BluetoothDevice?> = _selectedDevice
    
    // 로딩 상태
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    
    // 블루투스 활성화 상태
    private val _isBluetoothEnabled = MutableStateFlow(false)
    val isBluetoothEnabled: StateFlow<Boolean> = _isBluetoothEnabled
    
    /**
     * 블루투스 장치를 검색합니다.
     */
    fun scanDevices() {
        _isLoading.value = true
        bluetoothManager.scanDevices()
        _isLoading.value = false
    }
    
    /**
     * 블루투스 장치에 연결합니다.
     */
    fun connectToDevice(context: Context, device: BluetoothDevice) {
        _isLoading.value = true
        _selectedDevice.value = device
        
        val connected = bluetoothManager.connect(context, device)
        if (!connected) {
            _isLoading.value = false
        } else {
            // 연결 상태 관찰
            viewModelScope.launch {
                bluetoothManager.connectionState.collect { state ->
                    if (state != BluetoothManager.STATE_CONNECTING) {
                        _isLoading.value = false
                    }
                }
            }
        }
    }
    
    /**
     * 블루투스 장치 연결을 해제합니다.
     */
    fun disconnectDevice() {
        bluetoothManager.disconnect()
        _selectedDevice.value = null
    }
    
    /**
     * 블루투스 활성화 상태를 설정합니다.
     */
    fun setBluetoothEnabled(enabled: Boolean) {
        _isBluetoothEnabled.value = enabled
    }
    
    /**
     * 온도계 데이터를 가져옵니다.
     */
    fun getTemperature(): Float? {
        return temperatureData.value
    }
    
    /**
     * 스케일 데이터를 가져옵니다.
     */
    fun getWeight(): Float? {
        return scaleData.value
    }
    
    /**
     * 오류 메시지를 초기화합니다.
     */
    fun clearErrorMessage() {
        bluetoothManager.clearErrorMessage()
    }
    
    override fun onCleared() {
        super.onCleared()
        // ViewModel이 소멸될 때 블루투스 연결 해제
        bluetoothManager.disconnect()
    }
}
