package com.mathadventure.core.quest

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class QuestEngineTest {
    private class MemoryStore : QuestStore {
        private val data = mutableMapOf<String, QuestInstance>()
        override fun get(playerId: String, questId: String): QuestInstance? =
            data["$playerId:$questId"]
        override fun save(instance: QuestInstance) {
            data[instance.playerId + ":" + instance.questId] = instance
        }
    }

    private class CompletedPrerequisites(
        private val completed: Set<String> = emptySet()
    ) : QuestPrerequisiteChecker {
        override fun isSatisfied(playerId: String, prerequisites: Set<String>): Boolean =
            prerequisites.all { it in completed }
    }

    @Test
    fun firstQuestIsAvailable() {
        val engine = QuestEngine(
            FirstQuestChain.definitions.associateBy { it.id },
            MemoryStore(),
            CompletedPrerequisites()
        )
        assertEquals(QuestState.AVAILABLE, engine.availability("p1", "story_home_to_village"))
    }

    @Test
    fun prerequisiteLocksNextQuest() {
        val engine = QuestEngine(
            FirstQuestChain.definitions.associateBy { it.id },
            MemoryStore(),
            CompletedPrerequisites()
        )
        assertEquals(QuestState.LOCKED, engine.availability("p1", "story_village_to_forest"))
    }

    @Test
    fun completionCreatesStableEventIdentity() {
        val store = MemoryStore()
        val engine = QuestEngine(
            FirstQuestChain.definitions.associateBy { it.id },
            store,
            CompletedPrerequisites()
        )
        engine.start("p1", "story_home_to_village", "session-1", 100L)
        val updated = engine.recordObjectiveProgress(
            "p1", "story_home_to_village", "visit_village", nowEpochMillis = 200L
        )

        assertEquals(QuestState.COMPLETED, updated.state)
        val completion = engine.completion("p1", "story_home_to_village", "instance-1")
        assertNotNull(completion)
        assertEquals("quest-completed-instance-1", completion.eventId)
        assertEquals(200L, completion.completedAtEpochMillis)
        assertNull(engine.completion("p1", "story_village_to_forest", "instance-2"))
    }
}
