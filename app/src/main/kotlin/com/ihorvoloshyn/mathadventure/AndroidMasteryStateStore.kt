package com.ihorvoloshyn.mathadventure

import android.content.Context
import com.mathadventure.core.mastery.MasteryStateStore
import com.mathadventure.core.model.SkillState

class AndroidMasteryStateStore(context: Context) : MasteryStateStore {
    private val prefs = context.getSharedPreferences("math_adventure_mastery", Context.MODE_PRIVATE)

    override fun get(playerId: String, skillId: String): SkillState? {
        val prefix = key(playerId, skillId)
        if (!prefs.contains(prefix + "mastery")) return null
        return SkillState(
            skillId = skillId,
            mastery = prefs.getInt(prefix + "mastery", 0),
            attempts = prefs.getInt(prefix + "attempts", 0),
            correctAttempts = prefs.getInt(prefix + "correctAttempts", 0),
            incorrectAttempts = prefs.getInt(prefix + "incorrectAttempts", 0),
            recentErrors = prefs.getInt(prefix + "recentErrors", 0),
            lastAttemptAtEpochMillis = nullableLong(prefix + "lastAttemptAt"),
            lastCorrectAtEpochMillis = nullableLong(prefix + "lastCorrectAt"),
            consecutiveCorrect = prefs.getInt(prefix + "consecutiveCorrect", 0),
            consecutiveErrors = prefs.getInt(prefix + "consecutiveErrors", 0),
            reviewState = prefs.getString(prefix + "reviewState", null)
        )
    }

    override fun put(playerId: String, state: SkillState) {
        val prefix = key(playerId, state.skillId)
        prefs.edit()
            .putInt(prefix + "mastery", state.mastery)
            .putInt(prefix + "attempts", state.attempts)
            .putInt(prefix + "correctAttempts", state.correctAttempts)
            .putInt(prefix + "incorrectAttempts", state.incorrectAttempts)
            .putInt(prefix + "recentErrors", state.recentErrors)
            .putLongOrRemove(prefix + "lastAttemptAt", state.lastAttemptAtEpochMillis)
            .putLongOrRemove(prefix + "lastCorrectAt", state.lastCorrectAtEpochMillis)
            .putInt(prefix + "consecutiveCorrect", state.consecutiveCorrect)
            .putInt(prefix + "consecutiveErrors", state.consecutiveErrors)
            .putString(prefix + "reviewState", state.reviewState)
            .apply()
    }

    private fun nullableLong(key: String): Long? =
        if (prefs.contains(key)) prefs.getLong(key, 0L) else null

    private fun key(playerId: String, skillId: String): String =
        playerId + "::" + skillId + "::"

    private fun android.content.SharedPreferences.Editor.putLongOrRemove(key: String, value: Long?): android.content.SharedPreferences.Editor =
        if (value == null) remove(key) else putLong(key, value)
}
