package com.smartkitchen.assistant.data.bluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import android.bluetooth.BluetoothProfile
import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 블루투스 관리자
 * 블루투스 장치 검색, 연결, 데이터 통신 등을 관리합니다.
 */
@Singleton
class BluetoothManager @Inject constructor() {
    
    // 블루투스 어댑터
    private var bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    
    // 연결된 GATT 서버
    private var bluetoothGatt: BluetoothGatt? = null
    
    // 연결 상태
    private val _connectionState = MutableStateFlow(STATE_DISCONNECTED)
    val connectionState: StateFlow<Int> = _connectionState
    
    // 발견된 장치 목록
    private val _discoveredDevices = MutableStateFlow<List<BluetoothDevice>>(emptyList())
    val discoveredDevices: StateFlow<List<BluetoothDevice>> = _discoveredDevices
    
    // 수신된 데이터
    private val _receivedData = MutableStateFlow<Map<String, String>>(emptyMap())
    val receivedData: StateFlow<Map<String, String>> = _receivedData
    
    // 온도계 데이터
    private val _temperatureData = MutableStateFlow<Float?>(null)
    val temperatureData: StateFlow<Float?> = _temperatureData
    
    // 스케일 데이터
    private val _scaleData = MutableStateFlow<Float?>(null)
    val scaleData: StateFlow<Float?> = _scaleData
    
    // 장치 타입
    private val _deviceType = MutableStateFlow<DeviceType?>(null)
    val deviceType: StateFlow<DeviceType?> = _deviceType
    
    // 오류 메시지
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage
    
    /**
     * 블루투스 GATT 콜백
     */
    private val gattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                _connectionState.value = STATE_CONNECTED
                // 서비스 검색 시작
                bluetoothGatt?.discoverServices()
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                _connectionState.value = STATE_DISCONNECTED
            }
        }
        
        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                // 서비스 및 특성 탐색
                processGattServices(gatt.services)
            } else {
                _errorMessage.value = "서비스 검색 실패: $status"
            }
        }
        
        override fun onCharacteristicRead(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            status: Int
        ) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                // 특성 데이터 처리
                processCharacteristicData(characteristic)
            }
        }
        
        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic
        ) {
            // 특성 데이터 변경 처리
            processCharacteristicData(characteristic)
        }
    }
    
    /**
     * 블루투스 장치에 연결합니다.
     */
    fun connect(context: Context, device: BluetoothDevice): Boolean {
        bluetoothAdapter?.let { adapter ->
            try {
                val deviceAddress = device.address
                
                // 이미 연결된 장치인지 확인
                if (bluetoothGatt != null) {
                    if (deviceAddress == bluetoothGatt?.device?.address) {
                        // 이미 연결된 장치에 다시 연결 시도
                        if (bluetoothGatt?.connect() == true) {
                            _connectionState.value = STATE_CONNECTING
                            return true
                        } else {
                            return false
                        }
                    }
                    // 기존 연결 해제
                    bluetoothGatt?.close()
                    bluetoothGatt = null
                }
                
                // 새 연결 설정
                bluetoothGatt = device.connectGatt(context, false, gattCallback)
                _connectionState.value = STATE_CONNECTING
                
                // 장치 타입 설정
                _deviceType.value = determineDeviceType(device)
                
                return true
            } catch (e: Exception) {
                _errorMessage.value = "연결 오류: ${e.message}"
                return false
            }
        } ?: run {
            _errorMessage.value = "블루투스를 사용할 수 없습니다."
            return false
        }
    }
    
    /**
     * 블루투스 장치 연결을 해제합니다.
     */
    fun disconnect() {
        bluetoothGatt?.let { gatt ->
            gatt.disconnect()
            gatt.close()
            bluetoothGatt = null
            _connectionState.value = STATE_DISCONNECTED
        }
    }
    
    /**
     * 블루투스 장치를 검색합니다.
     */
    fun scanDevices() {
        bluetoothAdapter?.let { adapter ->
            if (adapter.isEnabled) {
                // 페어링된 장치 목록 가져오기
                val pairedDevices = adapter.bondedDevices
                val deviceList = mutableListOf<BluetoothDevice>()
                deviceList.addAll(pairedDevices)
                _discoveredDevices.value = deviceList
            } else {
                _errorMessage.value = "블루투스가 활성화되어 있지 않습니다."
            }
        } ?: run {
            _errorMessage.value = "블루투스를 사용할 수 없습니다."
        }
    }
    
    /**
     * GATT 서비스를 처리합니다.
     */
    private fun processGattServices(services: List<BluetoothGattService>) {
        for (service in services) {
            val characteristics = service.characteristics
            
            // 장치 타입에 따라 특성 처리
            when (_deviceType.value) {
                DeviceType.THERMOMETER -> {
                    // 온도계 특성 찾기
                    for (characteristic in characteristics) {
                        if (characteristic.uuid == THERMOMETER_CHARACTERISTIC_UUID) {
                            // 특성 읽기 활성화
                            enableCharacteristicNotification(characteristic)
                            // 초기 데이터 읽기
                            bluetoothGatt?.readCharacteristic(characteristic)
                        }
                    }
                }
                DeviceType.SCALE -> {
                    // 스케일 특성 찾기
                    for (characteristic in characteristics) {
                        if (characteristic.uuid == SCALE_CHARACTERISTIC_UUID) {
                            // 특성 읽기 활성화
                            enableCharacteristicNotification(characteristic)
                            // 초기 데이터 읽기
                            bluetoothGatt?.readCharacteristic(characteristic)
                        }
                    }
                }
                else -> {
                    // 알 수 없는 장치 타입
                    _errorMessage.value = "지원되지 않는 장치 타입입니다."
                }
            }
        }
    }
    
    /**
     * 특성 알림을 활성화합니다.
     */
    private fun enableCharacteristicNotification(characteristic: BluetoothGattCharacteristic) {
        bluetoothGatt?.setCharacteristicNotification(characteristic, true)
        
        // 알림 활성화를 위한 디스크립터 설정
        val descriptor = characteristic.getDescriptor(CLIENT_CHARACTERISTIC_CONFIG_UUID)
        descriptor?.let {
            it.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
            bluetoothGatt?.writeDescriptor(it)
        }
    }
    
    /**
     * 특성 데이터를 처리합니다.
     */
    private fun processCharacteristicData(characteristic: BluetoothGattCharacteristic) {
        when {
            // 온도계 데이터
            characteristic.uuid == THERMOMETER_CHARACTERISTIC_UUID -> {
                val data = characteristic.value
                if (data != null && data.isNotEmpty()) {
                    // 온도 데이터 파싱 (장치별로 다를 수 있음)
                    val temperature = parseTemperatureData(data)
                    _temperatureData.value = temperature
                    
                    // 수신 데이터 맵 업데이트
                    val currentData = _receivedData.value.toMutableMap()
                    currentData["temperature"] = "$temperature°C"
                    _receivedData.value = currentData
                }
            }
            // 스케일 데이터
            characteristic.uuid == SCALE_CHARACTERISTIC_UUID -> {
                val data = characteristic.value
                if (data != null && data.isNotEmpty()) {
                    // 무게 데이터 파싱 (장치별로 다를 수 있음)
                    val weight = parseWeightData(data)
                    _scaleData.value = weight
                    
                    // 수신 데이터 맵 업데이트
                    val currentData = _receivedData.value.toMutableMap()
                    currentData["weight"] = "$weight g"
                    _receivedData.value = currentData
                }
            }
        }
    }
    
    /**
     * 온도 데이터를 파싱합니다.
     */
    private fun parseTemperatureData(data: ByteArray): Float {
        // 예시 구현 (실제 장치에 맞게 수정 필요)
        return try {
            // 간단한 예: 첫 2바이트를 온도로 해석
            val temp = ((data[1].toInt() and 0xFF) shl 8) or (data[0].toInt() and 0xFF)
            temp / 100.0f
        } catch (e: Exception) {
            _errorMessage.value = "온도 데이터 파싱 오류: ${e.message}"
            0.0f
        }
    }
    
    /**
     * 무게 데이터를 파싱합니다.
     */
    private fun parseWeightData(data: ByteArray): Float {
        // 예시 구현 (실제 장치에 맞게 수정 필요)
        return try {
            // 간단한 예: 첫 2바이트를 무게로 해석
            val weight = ((data[1].toInt() and 0xFF) shl 8) or (data[0].toInt() and 0xFF)
            weight / 10.0f
        } catch (e: Exception) {
            _errorMessage.value = "무게 데이터 파싱 오류: ${e.message}"
            0.0f
        }
    }
    
    /**
     * 장치 타입을 결정합니다.
     */
    private fun determineDeviceType(device: BluetoothDevice): DeviceType {
        // 장치 이름이나 주소를 기반으로 타입 결정
        return when {
            device.name?.contains("therm", ignoreCase = true) == true -> DeviceType.THERMOMETER
            device.name?.contains("scale", ignoreCase = true) == true -> DeviceType.SCALE
            device.name?.contains("temp", ignoreCase = true) == true -> DeviceType.THERMOMETER
            device.name?.contains("weight", ignoreCase = true) == true -> DeviceType.SCALE
            // 기타 조건 추가
            else -> DeviceType.UNKNOWN
        }
    }
    
    /**
     * 오류 메시지를 초기화합니다.
     */
    fun clearErrorMessage() {
        _errorMessage.value = null
    }
    
    companion object {
        // 연결 상태
        const val STATE_DISCONNECTED = 0
        const val STATE_CONNECTING = 1
        const val STATE_CONNECTED = 2
        
        // UUID (예시, 실제 장치에 맞게 수정 필요)
        val THERMOMETER_SERVICE_UUID: UUID = UUID.fromString("0000180A-0000-1000-8000-00805f9b34fb")
        val THERMOMETER_CHARACTERISTIC_UUID: UUID = UUID.fromString("00002A1C-0000-1000-8000-00805f9b34fb")
        
        val SCALE_SERVICE_UUID: UUID = UUID.fromString("0000181B-0000-1000-8000-00805f9b34fb")
        val SCALE_CHARACTERISTIC_UUID: UUID = UUID.fromString("00002A98-0000-1000-8000-00805f9b34fb")
        
        val CLIENT_CHARACTERISTIC_CONFIG_UUID: UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")
    }
    
    /**
     * 장치 타입 열거형
     */
    enum class DeviceType {
        THERMOMETER,
        SCALE,
        UNKNOWN
    }
}
