package com.ihorvoloshyn.mathadventure

import android.content.Context
import com.mathadventure.core.gameprogression.GameEventType
import com.mathadventure.core.gameprogression.GameProgressionEvent
import com.mathadventure.core.gameprogression.QuestCompletionOutbox
import org.json.JSONArray
import org.json.JSONObject

class AndroidQuestProgressionOutbox(context: Context) : QuestCompletionOutbox {
    private val preferences = context.getSharedPreferences(
        "math_adventure_quest_progression_outbox",
        Context.MODE_PRIVATE
    )

    @Synchronized
    override fun save(event: GameProgressionEvent) {
        val current = pendingEvents().toMutableList()
        if (current.any { it.eventId == event.eventId }) return
        current += event
        write(current)
    }

    @Synchronized
    override fun pending(playerId: String): List<GameProgressionEvent> =
        pendingEvents().filter { it.playerId == playerId }

    @Synchronized
    override fun remove(eventId: String) {
        write(pendingEvents().filterNot { it.eventId == eventId })
    }

    private fun pendingEvents(): List<GameProgressionEvent> {
        val array = JSONArray(preferences.getString("events", "[]"))
        return buildList {
            for (i in 0 until array.length()) {
                val item = array.getJSONObject(i)
                val metadata = mutableMapOf<String, String>()
                val metadataJson = item.optJSONObject("metadata") ?: JSONObject()
                metadataJson.keys().forEach { key -> metadata[key] = metadataJson.getString(key) }
                add(
                    GameProgressionEvent(
                        eventId = item.getString("eventId"),
                        playerId = item.getString("playerId"),
                        eventType = GameEventType.valueOf(item.getString("eventType")),
                        sourceId = item.getString("sourceId"),
                        sessionId = item.optString("sessionId").takeIf { it.isNotBlank() },
                        outcome = item.getString("outcome"),
                        timestampEpochMillis = item.getLong("timestampEpochMillis"),
                        metadata = metadata
                    )
                )
            }
        }
    }

    private fun write(events: List<GameProgressionEvent>) {
        val array = JSONArray()
        events.forEach { event ->
            val metadata = JSONObject()
            event.metadata.forEach { (key, value) -> metadata.put(key, value) }
            array.put(
                JSONObject()
                    .put("eventId", event.eventId)
                    .put("playerId", event.playerId)
                    .put("eventType", event.eventType.name)
                    .put("sourceId", event.sourceId)
                    .put("sessionId", event.sessionId)
                    .put("outcome", event.outcome)
                    .put("timestampEpochMillis", event.timestampEpochMillis)
                    .put("metadata", metadata)
            )
        }
        preferences.edit().putString("events", array.toString()).commit()
    }
}
