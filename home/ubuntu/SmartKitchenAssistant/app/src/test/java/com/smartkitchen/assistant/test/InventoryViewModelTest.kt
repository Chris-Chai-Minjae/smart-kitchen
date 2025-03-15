package com.smartkitchen.assistant.test

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.smartkitchen.assistant.data.model.InventoryItem
import com.smartkitchen.assistant.data.repository.InventoryRepository
import com.smartkitchen.assistant.ui.viewmodel.InventoryViewModel
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
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import java.util.Date

/**
 * 재고 관리 뷰모델 단위 테스트
 */
@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class InventoryViewModelTest {

    // 테스트용 코루틴 디스패처
    private val testDispatcher = TestCoroutineDispatcher()

    // LiveData 테스트를 위한 규칙
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    // 모의 객체
    @Mock
    private lateinit var inventoryRepository: InventoryRepository

    // 테스트 대상
    private lateinit var viewModel: InventoryViewModel

    @Before
    fun setup() {
        // 메인 디스패처를 테스트 디스패처로 설정
        Dispatchers.setMain(testDispatcher)
        
        // 뷰모델 초기화
        viewModel = InventoryViewModel(inventoryRepository)
    }

    @After
    fun tearDown() {
        // 메인 디스패처 초기화
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun `getAllInventoryItems should update inventoryItems state when repository returns data`() = testDispatcher.runBlockingTest {
        // Given
        val testItems = listOf(
            InventoryItem(
                id = 1,
                name = "테스트 아이템",
                quantity = 2.0,
                unit = "개",
                category = "과일",
                expiryDate = Date(),
                purchaseDate = Date(),
                barcode = "1234567890123"
            )
        )
        `when`(inventoryRepository.getAllInventoryItems()).thenReturn(flowOf(testItems))

        // When
        viewModel.getAllInventoryItems()

        // Then
        assert(viewModel.inventoryItems.value == testItems)
    }

    @Test
    fun `getInventoryItemsByCategory should update inventoryItems state when repository returns data`() = testDispatcher.runBlockingTest {
        // Given
        val category = "과일"
        val testItems = listOf(
            InventoryItem(
                id = 1,
                name = "테스트 아이템",
                quantity = 2.0,
                unit = "개",
                category = category,
                expiryDate = Date(),
                purchaseDate = Date(),
                barcode = "1234567890123"
            )
        )
        `when`(inventoryRepository.getInventoryItemsByCategory(category)).thenReturn(flowOf(testItems))

        // When
        viewModel.getInventoryItemsByCategory(category)

        // Then
        assert(viewModel.inventoryItems.value == testItems)
        assert(viewModel.currentCategory.value == category)
    }

    @Test
    fun `searchInventoryItems should update inventoryItems state when repository returns data`() = testDispatcher.runBlockingTest {
        // Given
        val query = "테스트"
        val testItems = listOf(
            InventoryItem(
                id = 1,
                name = "테스트 아이템",
                quantity = 2.0,
                unit = "개",
                category = "과일",
                expiryDate = Date(),
                purchaseDate = Date(),
                barcode = "1234567890123"
            )
        )
        `when`(inventoryRepository.searchInventoryItems(query)).thenReturn(flowOf(testItems))

        // When
        viewModel.searchInventoryItems(query)

        // Then
        assert(viewModel.inventoryItems.value == testItems)
        assert(viewModel.searchQuery.value == query)
    }

    @Test
    fun `addInventoryItem should call repository addInventoryItem method`() = testDispatcher.runBlockingTest {
        // Given
        val item = InventoryItem(
            id = 0,
            name = "테스트 아이템",
            quantity = 2.0,
            unit = "개",
            category = "과일",
            expiryDate = Date(),
            purchaseDate = Date(),
            barcode = "1234567890123"
        )

        // When
        viewModel.addInventoryItem(item)

        // Then
        verify(inventoryRepository).addInventoryItem(item)
    }

    @Test
    fun `updateInventoryItem should call repository updateInventoryItem method`() = testDispatcher.runBlockingTest {
        // Given
        val item = InventoryItem(
            id = 1,
            name = "테스트 아이템",
            quantity = 2.0,
            unit = "개",
            category = "과일",
            expiryDate = Date(),
            purchaseDate = Date(),
            barcode = "1234567890123"
        )

        // When
        viewModel.updateInventoryItem(item)

        // Then
        verify(inventoryRepository).updateInventoryItem(item)
    }

    @Test
    fun `deleteInventoryItem should call repository deleteInventoryItem method`() = testDispatcher.runBlockingTest {
        // Given
        val itemId = 1L

        // When
        viewModel.deleteInventoryItem(itemId)

        // Then
        verify(inventoryRepository).deleteInventoryItem(itemId)
    }
}
