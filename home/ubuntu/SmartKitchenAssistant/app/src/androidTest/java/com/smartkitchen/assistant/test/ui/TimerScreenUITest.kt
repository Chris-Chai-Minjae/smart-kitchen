package com.smartkitchen.assistant.test.ui

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.smartkitchen.assistant.R
import com.smartkitchen.assistant.ui.activity.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * 타이머 화면 UI 테스트
 */
@RunWith(AndroidJUnit4::class)
class TimerScreenUITest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun testTimerListDisplay() {
        // 타이머 화면으로 이동
        onView(withId(R.id.navigation_timers)).perform(click())
        
        // 타이머 목록이 표시되는지 확인
        onView(withId(R.id.recyclerViewTimers)).check(matches(isDisplayed()))
    }

    @Test
    fun testAddNewTimer() {
        // 타이머 화면으로 이동
        onView(withId(R.id.navigation_timers)).perform(click())
        
        // 타이머 추가 버튼 클릭
        onView(withId(R.id.buttonAddTimer)).perform(click())
        
        // 타이머 추가 다이얼로그가 표시되는지 확인
        onView(withId(R.id.dialogAddTimer)).check(matches(isDisplayed()))
    }

    @Test
    fun testStartTimer() {
        // 타이머 화면으로 이동
        onView(withId(R.id.navigation_timers)).perform(click())
        
        // 첫 번째 타이머의 시작 버튼 클릭
        onView(withId(R.id.buttonStartTimer)).perform(click())
        
        // 타이머가 실행 중인지 확인
        onView(withId(R.id.textViewTimerStatus)).check(matches(isDisplayed()))
    }

    @Test
    fun testPauseTimer() {
        // 타이머 화면으로 이동
        onView(withId(R.id.navigation_timers)).perform(click())
        
        // 첫 번째 타이머의 시작 버튼 클릭
        onView(withId(R.id.buttonStartTimer)).perform(click())
        
        // 첫 번째 타이머의 일시 정지 버튼 클릭
        onView(withId(R.id.buttonPauseTimer)).perform(click())
        
        // 타이머가 일시 정지되었는지 확인
        onView(withId(R.id.textViewTimerStatus)).check(matches(isDisplayed()))
    }

    @Test
    fun testResetTimer() {
        // 타이머 화면으로 이동
        onView(withId(R.id.navigation_timers)).perform(click())
        
        // 첫 번째 타이머의 시작 버튼 클릭
        onView(withId(R.id.buttonStartTimer)).perform(click())
        
        // 첫 번째 타이머의 재설정 버튼 클릭
        onView(withId(R.id.buttonResetTimer)).perform(click())
        
        // 타이머가 재설정되었는지 확인
        onView(withId(R.id.textViewTimerStatus)).check(matches(isDisplayed()))
    }
}
