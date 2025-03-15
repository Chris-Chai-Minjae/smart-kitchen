package com.smartkitchen.assistant.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartkitchen.assistant.data.model.InventoryItem
import com.smartkitchen.assistant.data.repository.InventoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * 재고 관리 화면을 위한 ViewModel
 * 식품 재고 관련 데이터를 관리하고 UI와 비즈니스 로직을 연결합니다.
 */
@HiltViewModel
class InventoryViewModel @Inject constructor(
    private val inventoryRepository: InventoryRepository
) : ViewModel() {
    // 모든 재고 항목 목록
    private val _inventoryItems = MutableStateFlow<List<InventoryItem>>(emptyList())
    val inventoryItems: StateFlow<List<InventoryItem>> = _inventoryItems
    
    // 유통기한 임박 항목 목록
    private val _expiringItems = MutableStateFlow<List<InventoryItem>>(emptyList())
    val expiringItems: StateFlow<List<InventoryItem>> = _expiringItems
    
    // 보관 위치 목록
    private val _storageLocations = MutableStateFlow<List<String>>(emptyList())
    val storageLocations: StateFlow<List<String>> = _storageLocations
    
    // 현재 선택된 보관 위치
    private val _selectedLocation = MutableStateFlow<String?>(null)
    val selectedLocation: StateFlow<String?> = _selectedLocation
    
    // 검색 쿼리
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery
    
    // 로딩 상태
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    
    // 오류 메시지
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage
    
    // 바코드 스캔 결과
    private val _barcodeScanResult = MutableStateFlow<String?>(null)
    val barcodeScanResult: StateFlow<String?> = _barcodeScanResult
    
    init {
        loadInventoryItems()
        loadExpiringItems()
        loadStorageLocations()
    }
    
    /**
     * 모든 재고 항목을 로드합니다.
     */
    fun loadInventoryItems() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                inventoryRepository.getAllInventoryItems().collect { items ->
                    _inventoryItems.value = items
                }
            } catch (e: Exception) {
                _errorMessage.value = "재고 항목을 로드하는 중 오류가 발생했습니다: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * 유통기한 임박 항목을 로드합니다.
     */
    fun loadExpiringItems() {
        viewModelScope.launch {
            try {
                // 7일 이내에 유통기한이 임박한 항목
                val sevenDaysLater = System.currentTimeMillis() + TimeUnit.DAYS.toMillis(7)
                inventoryRepository.getExpiringItems(sevenDaysLater).collect { items ->
                    _expiringItems.value = items
                }
            } catch (e: Exception) {
                _errorMessage.value = "유통기한 임박 항목을 로드하는 중 오류가 발생했습니다: ${e.message}"
            }
        }
    }
    
    /**
     * 보관 위치 목록을 로드합니다.
     */
    fun loadStorageLocations() {
        viewModelScope.launch {
            try {
                inventoryRepository.getAllStorageLocations().collect { locations ->
                    _storageLocations.value = locations
                }
            } catch (e: Exception) {
                _errorMessage.value = "보관 위치 목록을 로드하는 중 오류가 발생했습니다: ${e.message}"
            }
        }
    }
    
    /**
     * 보관 위치별 재고 항목을 로드합니다.
     */
    fun loadInventoryItemsByLocation(location: String) {
        _selectedLocation.value = location
        viewModelScope.launch {
            _isLoading.value = true
            try {
                inventoryRepository.getInventoryItemsByLocation(location).collect { items ->
                    _inventoryItems.value = items
                }
            } catch (e: Exception) {
                _errorMessage.value = "보관 위치별 재고 항목을 로드하는 중 오류가 발생했습니다: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * 재고 항목을 검색합니다.
     */
    fun searchInventoryItems(query: String) {
        _searchQuery.value = query
        viewModelScope.launch {
            _isLoading.value = true
            try {
                inventoryRepository.searchInventoryItems(query).collect { items ->
                    _inventoryItems.value = items
                }
            } catch (e: Exception) {
                _errorMessage.value = "재고 항목 검색 중 오류가 발생했습니다: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * 재고 항목을 저장합니다.
     */
    fun saveInventoryItem(item: InventoryItem) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                if (item.id == 0L) {
                    // 새 항목 추가
                    inventoryRepository.insertInventoryItem(item)
                } else {
                    // 기존 항목 업데이트
                    inventoryRepository.updateInventoryItem(item)
                }
                loadInventoryItems()
                loadExpiringItems()
            } catch (e: Exception) {
                _errorMessage.value = "재고 항목을 저장하는 중 오류가 발생했습니다: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * 재고 항목을 삭제합니다.
     */
    fun deleteInventoryItem(item: InventoryItem) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                inventoryRepository.deleteInventoryItem(item)
                loadInventoryItems()
                loadExpiringItems()
            } catch (e: Exception) {
                _errorMessage.value = "재고 항목을 삭제하는 중 오류가 발생했습니다: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * 재고 항목의 수량을 업데이트합니다.
     */
    fun updateInventoryItemQuantity(itemId: Long, newQuantity: Double) {
        viewModelScope.launch {
            try {
                inventoryRepository.updateInventoryItemQuantity(itemId, newQuantity)
                loadInventoryItems()
            } catch (e: Exception) {
                _errorMessage.value = "재고 항목 수량을 업데이트하는 중 오류가 발생했습니다: ${e.message}"
            }
        }
    }
    
    /**
     * 바코드 스캔 결과를 처리합니다.
     */
    fun processBarcodeResult(barcode: String) {
        _barcodeScanResult.value = barcode
        viewModelScope.launch {
            try {
                val existingItem = inventoryRepository.getInventoryItemByBarcode(barcode)
                if (existingItem != null) {
                    // 기존 항목이 있는 경우, 수량 증가 등의 처리
                    val updatedQuantity = existingItem.quantity + 1
                    inventoryRepository.updateInventoryItemQuantity(existingItem.id, updatedQuantity)
                    loadInventoryItems()
                }
                // 없는 경우는 UI에서 새 항목 입력 화면으로 이동
            } catch (e: Exception) {
                _errorMessage.value = "바코드 처리 중 오류가 발생했습니다: ${e.message}"
            }
        }
    }
    
    /**
     * 바코드 스캔 결과를 초기화합니다.
     */
    fun clearBarcodeResult() {
        _barcodeScanResult.value = null
    }
    
    /**
     * 오류 메시지를 초기화합니다.
     */
    fun clearErrorMessage() {
        _errorMessage.value = null
    }
}
