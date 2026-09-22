package com.ihorvoloshyn.mathadventure

import android.content.Context
import com.mathadventure.core.gameprogression.BestResult
import com.mathadventure.core.gameprogression.GameProgressionState
import com.mathadventure.core.gameprogression.GameProgressionStore
import com.mathadventure.core.gameprogression.ProgressionCommit
import org.json.JSONArray
import org.json.JSONObject

class AndroidGameProgressionStore(context: Context) : GameProgressionStore {
    private val prefs = context.getSharedPreferences("math_adventure_game_progression", Context.MODE_PRIVATE)

    override fun get(playerId: String): GameProgressionState = GameProgressionState(
        totalXp = prefs.getLong("xp", 0L),
        coins = prefs.getLong("coins", 0L),
        rpgLevel = prefs.getInt("level", 1),
        bestResults = loadBestResults(),
        grantedEventIds = loadEventIds(),
        unlockedIds = emptySet()
    )

    override fun commit(commit: ProgressionCommit) {
        val current = get(commit.event.playerId)
        val ids = current.grantedEventIds.toMutableSet()
        val best = current.bestResults.toMutableMap()
        commit.bestResultAfter?.let { best[it.sourceId] = it }
        ids.add(commit.event.eventId)
        prefs.edit()
            .putLong("xp", current.totalXp + commit.reward.xpDelta)
            .putLong("coins", current.coins + commit.reward.coinsDelta)
            .putInt("level", commit.levelAfter)
            .putString("events", JSONArray(ids.toList()).toString())
            .putString("best_results", JSONArray(best.values.map { JSONObject().put("sourceId", it.sourceId).put("outcome", it.outcome).put("completionState", it.completionState) }).toString())
            .apply()
    }

    private fun loadBestResults(): Map<String, BestResult> {
        val raw = prefs.getString("best_results", null) ?: return emptyMap()
        return runCatching {
            val json = JSONArray(raw)
            buildMap {
                for (i in 0 until json.length()) {
                    val item = json.getJSONObject(i)
                    val result = BestResult(item.getString("sourceId"), item.getString("outcome"), completionState = item.optString("completionState", "COMPLETED"))
                    put(result.sourceId, result)
                }
            }
        }.getOrDefault(emptyMap())
    }

    private fun loadEventIds(): Set<String> {
        val raw = prefs.getString("events", null) ?: return emptySet()
        return runCatching {
            val json = JSONArray(raw)
            buildSet {
                for (i in 0 until json.length()) add(json.getString(i))
            }
        }.getOrDefault(emptySet())
    }
}
