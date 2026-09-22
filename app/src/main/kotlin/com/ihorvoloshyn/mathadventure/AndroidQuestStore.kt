package com.ihorvoloshyn.mathadventure

import android.content.Context
import com.mathadventure.core.quest.QuestDefinition
import com.mathadventure.core.quest.QuestInstance
import com.mathadventure.core.quest.QuestPrerequisiteChecker
import com.mathadventure.core.quest.QuestState
import com.mathadventure.core.quest.QuestStore
import org.json.JSONArray
import org.json.JSONObject

class AndroidQuestStore(context: Context) : QuestStore, QuestPrerequisiteChecker {
    private val preferences = context.getSharedPreferences(
        "math_adventure_quests",
        Context.MODE_PRIVATE
    )

    @Synchronized
    override fun get(playerId: String, questId: String): QuestInstance? {
        val raw = preferences.getString(key(playerId, questId), null) ?: return null
        val json = JSONObject(raw)
        val progress = mutableMapOf<String, Int>()
        val progressJson = json.optJSONObject("objectivesProgress") ?: JSONObject()
        progressJson.keys().forEach { objectiveId ->
            progress[objectiveId] = progressJson.optInt(objectiveId, 0)
        }
        return QuestInstance(
            questId = json.getString("questId"),
            playerId = json.getString("playerId"),
            state = QuestState.valueOf(json.getString("state")),
            objectivesProgress = progress,
            sessionId = json.optString("sessionId").takeIf { it.isNotBlank() },
            startedAtEpochMillis = json.optLong("startedAtEpochMillis").takeIf { json.has("startedAtEpochMillis") },
            completedAtEpochMillis = json.optLong("completedAtEpochMillis").takeIf { json.has("completedAtEpochMillis") }
        )
    }

    @Synchronized
    override fun save(instance: QuestInstance) {
        val progress = JSONObject()
        instance.objectivesProgress.forEach { (objectiveId, value) -> progress.put(objectiveId, value) }

        val json = JSONObject()
            .put("questId", instance.questId)
            .put("playerId", instance.playerId)
            .put("state", instance.state.name)
            .put("objectivesProgress", progress)
        instance.sessionId?.let { json.put("sessionId", it) }
        instance.startedAtEpochMillis?.let { json.put("startedAtEpochMillis", it) }
        instance.completedAtEpochMillis?.let { json.put("completedAtEpochMillis", it) }

        preferences.edit().putString(key(instance.playerId, instance.questId), json.toString()).commit()
    }

    override fun isSatisfied(playerId: String, prerequisites: Set<String>): Boolean =
        prerequisites.all { prerequisite ->
            get(playerId, prerequisite)?.state == QuestState.COMPLETED
        }

    private fun key(playerId: String, questId: String) = "quest:$playerId:$questId"
}
