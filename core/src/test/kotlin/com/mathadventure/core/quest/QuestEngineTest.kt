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
        val completion = engine.completion("p1", "story_home_to_village", "session-1")
        assertNotNull(completion)
        assertEquals("quest-completed-session-1", completion.eventId)
        assertEquals(200L, completion.completedAtEpochMillis)
        assertNull(engine.completion("p1", "story_village_to_forest", "instance-2"))
    }
    @Test
    fun failedQuestBecomesAvailableAgainButCompletedQuestStaysClosed() {
        val store = MemoryStore()
        val engine = QuestEngine(
            FirstQuestChain.definitions.associateBy { it.id },
            store,
            CompletedPrerequisites()
        )

        engine.start("p1", "story_first_battle", "session-1", 100L)
        engine.fail("p1", "story_first_battle", 200L)
        assertEquals(QuestState.AVAILABLE, engine.availability("p1", "story_first_battle"))

        engine.start("p1", "story_first_battle", "session-2", 300L)
        val completed = engine.recordObjectiveProgress(
            "p1", "story_first_battle", "win_first_battle", nowEpochMillis = 400L
        )
        assertEquals(QuestState.COMPLETED, completed.state)
        assertEquals(QuestState.COMPLETED, engine.availability("p1", "story_first_battle"))
    }

}
