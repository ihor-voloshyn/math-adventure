package com.ihorvoloshyn.mathadventure

import android.content.Context
import com.mathadventure.core.gameprogression.BestResult
import com.mathadventure.core.gameprogression.GameProgressionState
import com.mathadventure.core.gameprogression.GameProgressionStore
import com.mathadventure.core.gameprogression.ProgressionCommit

class PrototypeGameProgressStore(context: Context) : GameProgressionStore {
    private val prefs = context.getSharedPreferences("math_adventure_game_progress", Context.MODE_PRIVATE)

    override fun get(playerId: String): GameProgressionState =
        GameProgressionState(
            totalXp = prefs.getLong("xp", 0L),
            coins = prefs.getLong("coins", 0L),
            rpgLevel = prefs.getInt("level", 1),
            grantedEventIds = prefs.getStringSet("events", emptySet())?.toSet() ?: emptySet(),
            unlockedIds = prefs.getStringSet("unlocks", emptySet())?.toSet() ?: emptySet(),
            bestResults = emptyMap()
        )

    override fun commit(commit: ProgressionCommit) {
        val current = get(commit.event.playerId)
        val events = current.grantedEventIds + commit.event.eventId
        val unlocks = current.unlockedIds + commit.unlocks
        prefs.edit()
            .putLong("xp", current.totalXp + commit.reward.xpDelta)
            .putLong("coins", current.coins + commit.reward.coinsDelta)
            .putInt("level", commit.levelAfter)
            .putStringSet("events", events)
            .putStringSet("unlocks", unlocks)
            .apply()
    }
}
