package com.ihorvoloshyn.mathadventure

import android.content.Context
import com.mathadventure.core.gameprogression.GameProgressionEvent
import com.mathadventure.core.gameprogression.GameProgressionState
import com.mathadventure.core.gameprogression.ProgressionCommit
import com.mathadventure.core.gameprogression.BestResult
import com.mathadventure.core.gameprogression.GameProgressionStore
import org.json.JSONArray
import org.json.JSONObject

class AndroidGameProgressionStore(context: Context) : GameProgressionStore {
    private val preferences = context.getSharedPreferences(
        "math_adventure_game_progression",
        Context.MODE_PRIVATE
    )

    @Synchronized
    override fun get(playerId: String): GameProgressionState =
        readState(playerId)

    /**
     * Prototype persistence boundary:
     * one JSON document is written for a player in one synchronous SharedPreferences commit.
     * This keeps the event effects together and makes retries idempotent within this store.
     */
    @Synchronized
    override fun commit(commit: ProgressionCommit) {
        val playerId = commit.event.playerId
        val current = readState(playerId)
        if (commit.event.eventId in current.grantedEventIds) return

        val bestResults = current.bestResults.toMutableMap()
        commit.bestResultAfter?.let { bestResults[it.sourceId] = it }

        val next = current.copy(
            totalXp = current.totalXp + commit.reward.xpDelta,
            coins = current.coins + commit.reward.coinsDelta,
            rpgLevel = commit.levelAfter,
            bestResults = bestResults,
            grantedEventIds = current.grantedEventIds + commit.event.eventId,
            unlockedIds = current.unlockedIds + commit.unlocks
        )

        preferences.edit()
            .putString(key(playerId), encode(next))
            .commit()
    }

    private fun key(playerId: String) = "state:$playerId"

    private fun readState(playerId: String): GameProgressionState {
        val raw = preferences.getString(key(playerId), null) ?: return GameProgressionState()
        return decode(raw)
    }

    private fun encode(state: GameProgressionState): String {
        val json = JSONObject()
            .put("totalXp", state.totalXp)
            .put("coins", state.coins)
            .put("rpgLevel", state.rpgLevel)
            .put("bestResults", JSONArray().apply {
                state.bestResults.values.forEach { result ->
                    put(
                        JSONObject()
                            .put("sourceId", result.sourceId)
                            .put("outcome", result.outcome)
                            .put("score", result.score)
                            .put("completionState", result.completionState)
                    )
                }
            })
            .put("grantedEventIds", JSONArray().apply {
                state.grantedEventIds.forEach(::put)
            })
            .put("unlockedIds", JSONArray().apply {
                state.unlockedIds.forEach(::put)
            })
        return json.toString()
    }

    private fun decode(raw: String): GameProgressionState {
        val json = JSONObject(raw)
        val bestResults = mutableMapOf<String, BestResult>()
        val results = json.optJSONArray("bestResults") ?: JSONArray()
        for (i in 0 until results.length()) {
            val item = results.getJSONObject(i)
            val result = BestResult(
                sourceId = item.getString("sourceId"),
                outcome = item.getString("outcome"),
                score = if (item.isNull("score")) null else item.getInt("score"),
                completionState = item.optString("completionState", "COMPLETED")
            )
            bestResults[result.sourceId] = result
        }

        val grantedEventIds = buildSet {
            val ids = json.optJSONArray("grantedEventIds") ?: JSONArray()
            for (i in 0 until ids.length()) add(ids.getString(i))
        }

        val unlockedIds = buildSet {
            val ids = json.optJSONArray("unlockedIds") ?: JSONArray()
            for (i in 0 until ids.length()) add(ids.getString(i))
        }

        return GameProgressionState(
            totalXp = json.optLong("totalXp", 0L),
            coins = json.optLong("coins", 0L),
            rpgLevel = json.optInt("rpgLevel", 1),
            bestResults = bestResults,
            grantedEventIds = grantedEventIds,
            unlockedIds = unlockedIds
        )
    }
}
