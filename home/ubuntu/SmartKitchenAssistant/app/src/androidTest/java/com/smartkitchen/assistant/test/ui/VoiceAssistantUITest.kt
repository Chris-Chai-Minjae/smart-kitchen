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
 * 음성 인식 화면 UI 테스트
 */
@RunWith(AndroidJUnit4::class)
class VoiceAssistantUITest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun testVoiceAssistantDisplay() {
        // 음성 인식 화면으로 이동
        onView(withId(R.id.navigation_voice)).perform(click())
        
        // 음성 인식 화면이 표시되는지 확인
        onView(withId(R.id.textViewTitle)).check(matches(isDisplayed()))
        onView(withId(R.id.buttonStartListening)).check(matches(isDisplayed()))
    }

    @Test
    fun testStartListeningButton() {
        // 음성 인식 화면으로 이동
        onView(withId(R.id.navigation_voice)).perform(click())
        
        // 음성 인식 시작 버튼 클릭
        onView(withId(R.id.buttonStartListening)).perform(click())
        
        // 음성 인식 중지 버튼이 활성화되었는지 확인
        onView(withId(R.id.buttonStopListening)).check(matches(isDisplayed()))
    }

    @Test
    fun testHelpButton() {
        // 음성 인식 화면으로 이동
        onView(withId(R.id.navigation_voice)).perform(click())
        
        // 도움말 버튼 클릭
        onView(withId(R.id.buttonHelp)).perform(click())
        
        // 도움말 내용이 표시되는지 확인
        onView(withId(R.id.textViewCommandResult)).check(matches(isDisplayed()))
    }
}
