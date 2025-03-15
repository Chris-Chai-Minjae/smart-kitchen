package com.smartkitchen.assistant.data.voice

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.service.voice.VoiceInteractionService
import androidx.annotation.RequiresApi
import com.smartkitchen.assistant.ui.activity.MainActivity
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Google Assistant 통합 서비스
 * Google Assistant와의 통합을 위한 서비스입니다.
 * Android 8.0 (API 레벨 26) 이상에서 사용 가능합니다.
 */
@RequiresApi(26)
@Singleton
class GoogleAssistantService @Inject constructor() : VoiceInteractionService() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Google Assistant에서 앱이 호출될 때 실행되는 코드
        return super.onStartCommand(intent, flags, startId)
    }

    /**
     * Google Assistant에서 앱을 실행합니다.
     */
    fun launchFromAssistant(context: Context, command: String? = null) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(EXTRA_VOICE_COMMAND, command)
        }
        context.startActivity(intent)
    }

    /**
     * Google Assistant 명령을 처리합니다.
     */
    fun processAssistantCommand(context: Context, command: String?) {
        command?.let {
            // 명령 처리 로직
            // 여기서는 앱을 실행하고 명령을 전달하는 간단한 구현만 제공
            launchFromAssistant(context, command)
        }
    }

    companion object {
        const val EXTRA_VOICE_COMMAND = "extra_voice_command"
    }
}
