package com.smartkitchen.assistant.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartkitchen.assistant.data.model.ShoppingListItem
import com.smartkitchen.assistant.data.repository.ShoppingListRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 쇼핑 목록 화면을 위한 ViewModel
 * 쇼핑 목록 관련 데이터를 관리하고 UI와 비즈니스 로직을 연결합니다.
 */
@HiltViewModel
class ShoppingListViewModel @Inject constructor(
    private val shoppingListRepository: ShoppingListRepository
) : ViewModel() {
    // 모든 쇼핑 목록 항목
    private val _shoppingListItems = MutableStateFlow<List<ShoppingListItem>>(emptyList())
    val shoppingListItems: StateFlow<List<ShoppingListItem>> = _shoppingListItems
    
    // 활성 상태(체크되지 않은) 쇼핑 목록 항목
    private val _activeItems = MutableStateFlow<List<ShoppingListItem>>(emptyList())
    val activeItems: StateFlow<List<ShoppingListItem>> = _activeItems
    
    // 카테고리 목록
    private val _categories = MutableStateFlow<List<String>>(emptyList())
    val categories: StateFlow<List<String>> = _categories
    
    // 현재 선택된 카테고리
    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory
    
    // 로딩 상태
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    
    // 오류 메시지
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage
    
    init {
        loadAllShoppingListItems()
        loadActiveShoppingListItems()
        loadCategories()
    }
    
    /**
     * 모든 쇼핑 목록 항목을 로드합니다.
     */
    fun loadAllShoppingListItems() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                shoppingListRepository.getAllShoppingListItems().collect { items ->
                    _shoppingListItems.value = items
                }
            } catch (e: Exception) {
                _errorMessage.value = "쇼핑 목록을 로드하는 중 오류가 발생했습니다: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * 활성 상태(체크되지 않은) 쇼핑 목록 항목을 로드합니다.
     */
    fun loadActiveShoppingListItems() {
        viewModelScope.launch {
            try {
                shoppingListRepository.getActiveShoppingListItems().collect { items ->
                    _activeItems.value = items
                }
            } catch (e: Exception) {
                _errorMessage.value = "활성 쇼핑 목록을 로드하는 중 오류가 발생했습니다: ${e.message}"
            }
        }
    }
    
    /**
     * 카테고리 목록을 로드합니다.
     */
    fun loadCategories() {
        viewModelScope.launch {
            try {
                shoppingListRepository.getAllCategories().collect { categoryList ->
                    _categories.value = categoryList
                }
            } catch (e: Exception) {
                _errorMessage.value = "카테고리 목록을 로드하는 중 오류가 발생했습니다: ${e.message}"
            }
        }
    }
    
    /**
     * 카테고리별 쇼핑 목록 항목을 로드합니다.
     */
    fun loadShoppingListItemsByCategory(category: String) {
        _selectedCategory.value = category
        viewModelScope.launch {
            _isLoading.value = true
            try {
                shoppingListRepository.getShoppingListItemsByCategory(category).collect { items ->
                    _shoppingListItems.value = items
                }
            } catch (e: Exception) {
                _errorMessage.value = "카테고리별 쇼핑 목록을 로드하는 중 오류가 발생했습니다: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * 쇼핑 목록 항목을 저장합니다.
     */
    fun saveShoppingListItem(item: ShoppingListItem) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                if (item.id == 0L) {
                    // 새 항목 추가
                    shoppingListRepository.insertShoppingListItem(item)
                } else {
                    // 기존 항목 업데이트
                    shoppingListRepository.updateShoppingListItem(item)
                }
                loadAllShoppingListItems()
                loadActiveShoppingListItems()
                loadCategories()
            } catch (e: Exception) {
                _errorMessage.value = "쇼핑 목록 항목을 저장하는 중 오류가 발생했습니다: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * 쇼핑 목록 항목을 삭제합니다.
     */
    fun deleteShoppingListItem(item: ShoppingListItem) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                shoppingListRepository.deleteShoppingListItem(item)
                loadAllShoppingListItems()
                loadActiveShoppingListItems()
            } catch (e: Exception) {
                _errorMessage.value = "쇼핑 목록 항목을 삭제하는 중 오류가 발생했습니다: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * 항목의 체크 상태를 업데이트합니다.
     */
    fun updateItemCheckedStatus(id: Long, isChecked: Boolean) {
        viewModelScope.launch {
            try {
                shoppingListRepository.updateItemCheckedStatus(id, isChecked)
                loadAllShoppingListItems()
                loadActiveShoppingListItems()
            } catch (e: Exception) {
                _errorMessage.value = "항목 체크 상태를 업데이트하는 중 오류가 발생했습니다: ${e.message}"
            }
        }
    }
    
    /**
     * 체크된 항목을 모두 삭제합니다.
     */
    fun deleteCheckedItems() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                shoppingListRepository.deleteCheckedItems()
                loadAllShoppingListItems()
                loadActiveShoppingListItems()
            } catch (e: Exception) {
                _errorMessage.value = "체크된 항목을 삭제하는 중 오류가 발생했습니다: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * 쇼핑 목록 항목을 추가하거나 업데이트합니다.
     */
    fun addOrUpdateShoppingListItem(item: ShoppingListItem) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                shoppingListRepository.addOrUpdateShoppingListItem(item)
                loadAllShoppingListItems()
                loadActiveShoppingListItems()
            } catch (e: Exception) {
                _errorMessage.value = "쇼핑 목록 항목을 추가/업데이트하는 중 오류가 발생했습니다: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * 오류 메시지를 초기화합니다.
     */
    fun clearErrorMessage() {
        _errorMessage.value = null
    }
}
