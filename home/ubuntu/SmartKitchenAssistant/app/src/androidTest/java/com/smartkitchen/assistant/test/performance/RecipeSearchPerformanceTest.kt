package com.smartkitchen.assistant.test.performance

import androidx.benchmark.junit4.BenchmarkRule
import androidx.benchmark.junit4.measureRepeated
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.smartkitchen.assistant.data.model.Recipe
import com.smartkitchen.assistant.data.repository.RecipeRepository
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock

/**
 * 레시피 검색 성능 테스트
 */
@RunWith(AndroidJUnit4::class)
class RecipeSearchPerformanceTest {

    @get:Rule
    val benchmarkRule = BenchmarkRule()

    private val mockRepository = mock(RecipeRepository::class.java)

    @Test
    fun benchmarkRecipeSearch() {
        val recipes = generateLargeRecipeList(1000)
        
        benchmarkRule.measureRepeated {
            runBlocking {
                // 검색 알고리즘 성능 측정
                val results = searchRecipes(recipes, "김치")
                
                // 결과 확인 (최적화 방지)
                if (results.isEmpty()) {
                    throw IllegalStateException("검색 결과가 없습니다.")
                }
            }
        }
    }

    @Test
    fun benchmarkRecipeFiltering() {
        val recipes = generateLargeRecipeList(1000)
        
        benchmarkRule.measureRepeated {
            runBlocking {
                // 필터링 알고리즘 성능 측정
                val results = filterRecipes(recipes, "한식", 30)
                
                // 결과 확인 (최적화 방지)
                if (results.isEmpty()) {
                    throw IllegalStateException("필터링 결과가 없습니다.")
                }
            }
        }
    }

    @Test
    fun benchmarkRecipeSorting() {
        val recipes = generateLargeRecipeList(1000)
        
        benchmarkRule.measureRepeated {
            runBlocking {
                // 정렬 알고리즘 성능 측정
                val results = sortRecipesByTime(recipes)
                
                // 결과 확인 (최적화 방지)
                if (results.isEmpty()) {
                    throw IllegalStateException("정렬 결과가 없습니다.")
                }
            }
        }
    }

    // 테스트용 대량 레시피 생성
    private fun generateLargeRecipeList(count: Int): List<Recipe> {
        val recipes = mutableListOf<Recipe>()
        val categories = listOf("한식", "중식", "일식", "양식", "분식")
        val difficulties = listOf("쉬움", "중간", "어려움")
        
        for (i in 1..count) {
            val category = categories[i % categories.size]
            val difficulty = difficulties[i % difficulties.size]
            val cookingTime = (10 + (i % 50)) * 5
            
            recipes.add(
                Recipe(
                    id = i.toLong(),
                    title = "테스트 레시피 $i",
                    description = "테스트 설명 $i",
                    cookingTime = cookingTime,
                    servings = 4,
                    difficulty = difficulty,
                    imageUrl = "https://example.com/image$i.jpg",
                    isFavorite = i % 5 == 0,
                    category = category
                )
            )
        }
        
        return recipes
    }

    // 레시피 검색 알고리즘
    private fun searchRecipes(recipes: List<Recipe>, query: String): List<Recipe> {
        return recipes.filter { recipe ->
            recipe.title.contains(query, ignoreCase = true) ||
            recipe.description.contains(query, ignoreCase = true)
        }
    }

    // 레시피 필터링 알고리즘
    private fun filterRecipes(recipes: List<Recipe>, category: String, maxTime: Int): List<Recipe> {
        return recipes.filter { recipe ->
            recipe.category == category && recipe.cookingTime <= maxTime
        }
    }

    // 레시피 정렬 알고리즘
    private fun sortRecipesByTime(recipes: List<Recipe>): List<Recipe> {
        return recipes.sortedBy { it.cookingTime }
    }
}
