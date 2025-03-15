package com.smartkitchen.assistant.test.ui

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.smartkitchen.assistant.R
import com.smartkitchen.assistant.ui.activity.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * 레시피 화면 UI 테스트
 */
@RunWith(AndroidJUnit4::class)
class RecipeScreenUITest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun testRecipeListDisplay() {
        // 레시피 목록 화면으로 이동
        onView(withId(R.id.navigation_recipes)).perform(click())
        
        // 레시피 목록이 표시되는지 확인
        onView(withId(R.id.recyclerViewRecipes)).check(matches(isDisplayed()))
    }

    @Test
    fun testRecipeSearch() {
        // 레시피 목록 화면으로 이동
        onView(withId(R.id.navigation_recipes)).perform(click())
        
        // 검색창에 텍스트 입력
        onView(withId(R.id.editTextSearch)).perform(typeText("김치찌개"))
        
        // 검색 버튼 클릭
        onView(withId(R.id.buttonSearch)).perform(click())
        
        // 검색 결과가 표시되는지 확인
        onView(withId(R.id.recyclerViewRecipes)).check(matches(isDisplayed()))
    }

    @Test
    fun testRecipeDetail() {
        // 레시피 목록 화면으로 이동
        onView(withId(R.id.navigation_recipes)).perform(click())
        
        // 첫 번째 레시피 항목 클릭
        onView(withId(R.id.recyclerViewRecipes)).perform(click())
        
        // 레시피 상세 화면이 표시되는지 확인
        onView(withId(R.id.textViewRecipeTitle)).check(matches(isDisplayed()))
        onView(withId(R.id.textViewIngredients)).check(matches(isDisplayed()))
        onView(withId(R.id.textViewInstructions)).check(matches(isDisplayed()))
    }

    @Test
    fun testAddToFavorites() {
        // 레시피 목록 화면으로 이동
        onView(withId(R.id.navigation_recipes)).perform(click())
        
        // 첫 번째 레시피 항목 클릭
        onView(withId(R.id.recyclerViewRecipes)).perform(click())
        
        // 즐겨찾기 버튼 클릭
        onView(withId(R.id.buttonFavorite)).perform(click())
        
        // 즐겨찾기 상태 확인
        onView(withId(R.id.buttonFavorite)).check(matches(isDisplayed()))
    }
}
