package com.smartkitchen.assistant.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartkitchen.assistant.data.repository.RecipeImportRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 레시피 가져오기 화면을 위한 ViewModel
 * CSV 파일에서 레시피 데이터를 가져오는 기능을 관리합니다.
 */
@HiltViewModel
class RecipeImportViewModel @Inject constructor(
    private val recipeImportRepository: RecipeImportRepository
) : ViewModel() {
    // 가져오기 진행 상태
    private val _importProgress = MutableStateFlow(0)
    val importProgress: StateFlow<Int> = _importProgress
    
    // 가져오기 완료된 레시피 수
    private val _importedCount = MutableStateFlow(0)
    val importedCount: StateFlow<Int> = _importedCount
    
    // 로딩 상태
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    
    // 오류 메시지
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage
    
    // 성공 메시지
    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage
    
    /**
     * 애셋 폴더의 CSV 파일에서 레시피를 가져옵니다.
     */
    fun importRecipesFromAssets(context: Context, fileName: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _importProgress.value = 0
            try {
                val count = recipeImportRepository.importRecipesFromCsv(context, fileName)
                _importedCount.value = count
                _successMessage.value = "$count개의 레시피를 성공적으로 가져왔습니다."
                _importProgress.value = 100
            } catch (e: Exception) {
                _errorMessage.value = "레시피를 가져오는 중 오류가 발생했습니다: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * 외부 파일에서 레시피를 가져옵니다.
     */
    fun importRecipesFromExternalFile(filePath: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _importProgress.value = 0
            try {
                val count = recipeImportRepository.importRecipesFromExternalFile(filePath)
                _importedCount.value = count
                _successMessage.value = "$count개의 레시피를 성공적으로 가져왔습니다."
                _importProgress.value = 100
            } catch (e: Exception) {
                _errorMessage.value = "레시피를 가져오는 중 오류가 발생했습니다: ${e.message}"
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
    
    /**
     * 성공 메시지를 초기화합니다.
     */
    fun clearSuccessMessage() {
        _successMessage.value = null
    }
}
