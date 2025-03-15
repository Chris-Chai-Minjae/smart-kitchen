package com.smartkitchen.assistant.test.integration

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.smartkitchen.assistant.data.local.RecipeDao
import com.smartkitchen.assistant.data.model.Recipe
import com.smartkitchen.assistant.data.repository.RecipeRepository
import com.smartkitchen.assistant.ui.viewmodel.RecipeViewModel
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
 * 레시피 기능 통합 테스트
 * 레시피 저장소와 뷰모델 간의 통합을 테스트합니다.
 */
@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class RecipeIntegrationTest {

    // 테스트용 코루틴 디스패처
    private val testDispatcher = TestCoroutineDispatcher()

    // LiveData 테스트를 위한 규칙
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    // 모의 객체
    @Mock
    private lateinit var recipeDao: RecipeDao

    // 테스트 대상
    private lateinit var recipeRepository: RecipeRepository
    private lateinit var recipeViewModel: RecipeViewModel

    @Before
    fun setup() {
        // 메인 디스패처를 테스트 디스패처로 설정
        Dispatchers.setMain(testDispatcher)
        
        // 저장소 초기화
        recipeRepository = RecipeRepository(recipeDao)
        
        // 뷰모델 초기화
        recipeViewModel = RecipeViewModel(recipeRepository)
    }

    @After
    fun tearDown() {
        // 메인 디스패처 초기화
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun `end-to-end recipe search flow`() = testDispatcher.runBlockingTest {
        // Given
        val query = "테스트"
        val testRecipes = listOf(
            Recipe(
                id = 1,
                title = "테스트 레시피",
                description = "테스트 설명",
                cookingTime = 30,
                servings = 4,
                difficulty = "중간",
                imageUrl = "https://example.com/image.jpg",
                isFavorite = false
            )
        )
        
        // DAO에서 검색 결과 반환 설정
        `when`(recipeDao.searchRecipes("%$query%")).thenReturn(flowOf(testRecipes))

        // When - 뷰모델을 통해 검색 실행
        recipeViewModel.searchRecipes(query)

        // Then - 뷰모델의 상태가 올바르게 업데이트되었는지 확인
        assert(recipeViewModel.recipes.value == testRecipes)
        assert(recipeViewModel.searchQuery.value == query)
        
        // DAO의 searchRecipes 메서드가 호출되었는지 확인
        verify(recipeDao).searchRecipes("%$query%")
    }

    @Test
    fun `end-to-end recipe favorite toggle flow`() = testDispatcher.runBlockingTest {
        // Given
        val recipeId = 1L
        val testRecipe = Recipe(
            id = recipeId,
            title = "테스트 레시피",
            description = "테스트 설명",
            cookingTime = 30,
            servings = 4,
            difficulty = "중간",
            imageUrl = "https://example.com/image.jpg",
            isFavorite = false
        )
        
        // DAO에서 레시피 반환 설정
        `when`(recipeDao.getRecipeById(recipeId)).thenReturn(flowOf(testRecipe))

        // When - 뷰모델을 통해 레시피 로드 및 즐겨찾기 토글
        recipeViewModel.getRecipeById(recipeId)
        recipeViewModel.toggleFavorite(recipeId)

        // Then - 저장소를 통해 DAO의 toggleFavorite 메서드가 호출되었는지 확인
        verify(recipeDao).toggleFavorite(recipeId)
    }
}
