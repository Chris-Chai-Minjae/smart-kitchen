package com.smartkitchen.assistant.ui.fragment

import android.Manifest
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.smartkitchen.assistant.data.bluetooth.BluetoothManager
import com.smartkitchen.assistant.data.bluetooth.BluetoothPermissionManager
import com.smartkitchen.assistant.databinding.FragmentBluetoothBinding
import com.smartkitchen.assistant.ui.adapter.BluetoothDeviceAdapter
import com.smartkitchen.assistant.ui.viewmodel.BluetoothViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 블루투스 연결 화면
 * 블루투스 장치 검색, 연결, 데이터 수신 기능을 제공합니다.
 */
@AndroidEntryPoint
class BluetoothFragment : Fragment() {
    
    private var _binding: FragmentBluetoothBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: BluetoothViewModel by viewModels()
    
    @Inject
    lateinit var bluetoothPermissionManager: BluetoothPermissionManager
    
    private lateinit var deviceAdapter: BluetoothDeviceAdapter
    
    // 블루투스 활성화 요청 결과 처리
    private val enableBluetoothLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            // 블루투스 활성화 성공
            viewModel.setBluetoothEnabled(true)
            scanDevices()
        } else {
            // 블루투스 활성화 실패
            viewModel.setBluetoothEnabled(false)
            Toast.makeText(requireContext(), "블루투스를 활성화해야 합니다.", Toast.LENGTH_SHORT).show()
        }
    }
    
    // 블루투스 권한 요청 결과 처리
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.entries.all { it.value }
        if (allGranted) {
            // 모든 권한 획득 성공
            checkBluetoothEnabled()
        } else {
            // 일부 권한 획득 실패
            Toast.makeText(requireContext(), "블루투스 사용을 위해 모든 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
        }
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBluetoothBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupUI()
        setupObservers()
        checkBluetoothPermissions()
    }
    
    private fun setupUI() {
        // 장치 어댑터 설정
        deviceAdapter = BluetoothDeviceAdapter { device ->
            connectToDevice(device)
        }
        
        binding.recyclerViewDevices.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = deviceAdapter
        }
        
        // 스캔 버튼 클릭 리스너
        binding.buttonScan.setOnClickListener {
            checkBluetoothPermissions()
        }
        
        // 연결 해제 버튼 클릭 리스너
        binding.buttonDisconnect.setOnClickListener {
            viewModel.disconnectDevice()
        }
    }
    
    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            // 발견된 장치 목록 관찰
            viewModel.discoveredDevices.collect { devices ->
                deviceAdapter.submitList(devices.toList())
                binding.textViewNoDevices.visibility = if (devices.isEmpty()) View.VISIBLE else View.GONE
            }
        }
        
        viewLifecycleOwner.lifecycleScope.launch {
            // 연결 상태 관찰
            viewModel.connectionState.collect { state ->
                updateConnectionState(state)
            }
        }
        
        viewLifecycleOwner.lifecycleScope.launch {
            // 수신된 데이터 관찰
            viewModel.receivedData.collect { data ->
                updateReceivedData(data)
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
            // 로딩 상태 관찰
            viewModel.isLoading.collect { isLoading ->
                binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            }
        }
        
        viewLifecycleOwner.lifecycleScope.launch {
            // 선택된 장치 관찰
            viewModel.selectedDevice.collect { device ->
                updateSelectedDevice(device)
            }
        }
    }
    
    private fun checkBluetoothPermissions() {
        val permissions = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            arrayOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT
            )
        } else {
            arrayOf(
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        }
        
        requestPermissionLauncher.launch(permissions)
    }
    
    private fun checkBluetoothEnabled() {
        if (bluetoothPermissionManager.isBluetoothEnabled(requireContext())) {
            viewModel.setBluetoothEnabled(true)
            scanDevices()
        } else {
            // 블루투스 활성화 요청
            bluetoothPermissionManager.requestBluetoothEnable(enableBluetoothLauncher)
        }
    }
    
    private fun scanDevices() {
        viewModel.scanDevices()
    }
    
    private fun connectToDevice(device: BluetoothDevice) {
        viewModel.connectToDevice(requireContext(), device)
    }
    
    private fun updateConnectionState(state: Int) {
        binding.textViewConnectionStatus.text = when (state) {
            BluetoothManager.STATE_DISCONNECTED -> "연결 해제됨"
            BluetoothManager.STATE_CONNECTING -> "연결 중..."
            BluetoothManager.STATE_CONNECTED -> "연결됨"
            else -> "알 수 없음"
        }
        
        // 연결 상태에 따라 UI 업데이트
        val isConnected = state == BluetoothManager.STATE_CONNECTED
        binding.buttonDisconnect.isEnabled = isConnected
        binding.layoutDeviceData.visibility = if (isConnected) View.VISIBLE else View.GONE
    }
    
    private fun updateSelectedDevice(device: BluetoothDevice?) {
        device?.let {
            binding.textViewSelectedDevice.text = "연결된 장치: ${it.name ?: "알 수 없음"} (${it.address})"
        } ?: run {
            binding.textViewSelectedDevice.text = "연결된 장치 없음"
        }
    }
    
    private fun updateReceivedData(data: Map<String, String>) {
        val dataText = if (data.isEmpty()) {
            "수신된 데이터 없음"
        } else {
            data.entries.joinToString("\n") { "${it.key}: ${it.value}" }
        }
        
        binding.textViewReceivedData.text = dataText
        
        // 온도계 데이터 업데이트
        viewModel.temperatureData.value?.let {
            binding.textViewTemperature.text = "온도: $it°C"
            binding.textViewTemperature.visibility = View.VISIBLE
        } ?: run {
            binding.textViewTemperature.visibility = View.GONE
        }
        
        // 스케일 데이터 업데이트
        viewModel.scaleData.value?.let {
            binding.textViewWeight.text = "무게: $it g"
            binding.textViewWeight.visibility = View.VISIBLE
        } ?: run {
            binding.textViewWeight.visibility = View.GONE
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
