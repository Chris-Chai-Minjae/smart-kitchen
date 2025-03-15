package com.smartkitchen.assistant.test

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
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
import org.mockito.junit.MockitoJUnitRunner

/**
 * 레시피 뷰모델 단위 테스트
 */
@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class RecipeViewModelTest {

    // 테스트용 코루틴 디스패처
    private val testDispatcher = TestCoroutineDispatcher()

    // LiveData 테스트를 위한 규칙
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    // 모의 객체
    @Mock
    private lateinit var recipeRepository: RecipeRepository

    // 테스트 대상
    private lateinit var viewModel: RecipeViewModel

    @Before
    fun setup() {
        // 메인 디스패처를 테스트 디스패처로 설정
        Dispatchers.setMain(testDispatcher)
        
        // 뷰모델 초기화
        viewModel = RecipeViewModel(recipeRepository)
    }

    @After
    fun tearDown() {
        // 메인 디스패처 초기화
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun `getAllRecipes should update recipes state when repository returns data`() = testDispatcher.runBlockingTest {
        // Given
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
        `when`(recipeRepository.getAllRecipes()).thenReturn(flowOf(testRecipes))

        // When
        viewModel.getAllRecipes()

        // Then
        assert(viewModel.recipes.value == testRecipes)
    }

    @Test
    fun `searchRecipes should update recipes state when repository returns data`() = testDispatcher.runBlockingTest {
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
        `when`(recipeRepository.searchRecipes(query)).thenReturn(flowOf(testRecipes))

        // When
        viewModel.searchRecipes(query)

        // Then
        assert(viewModel.recipes.value == testRecipes)
        assert(viewModel.searchQuery.value == query)
    }

    @Test
    fun `getRecipeById should update selectedRecipe state when repository returns data`() = testDispatcher.runBlockingTest {
        // Given
        val recipeId = 1
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
        `when`(recipeRepository.getRecipeById(recipeId)).thenReturn(flowOf(testRecipe))

        // When
        viewModel.getRecipeById(recipeId)

        // Then
        assert(viewModel.selectedRecipe.value == testRecipe)
    }
}
