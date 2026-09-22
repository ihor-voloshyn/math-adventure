package com.mathadventure.core.integration

import com.mathadventure.core.combat.CombatAction
import com.mathadventure.core.combat.CombatEngine
import com.mathadventure.core.combat.CombatResolution
import com.mathadventure.core.gameprogression.CoreGameProgressionFlow
import com.mathadventure.core.gameprogression.DefaultRpgLevelPolicy
import com.mathadventure.core.gameprogression.GameProgressionState
import com.mathadventure.core.gameprogression.GameProgressionStore
import com.mathadventure.core.gameprogression.ProgressionCommit
import com.mathadventure.core.gameprogression.PrototypeGameRewardPolicy
import com.mathadventure.core.gameprogression.PrototypeGameUnlockPolicy
import com.mathadventure.core.gameprogression.QuestCompletionOutbox
import com.mathadventure.core.gameprogression.QuestProgressionCoordinator
import com.mathadventure.core.gameprogression.QuestProgressionEventMapper
import com.mathadventure.core.quest.FirstQuestChain
import com.mathadventure.core.quest.QuestEngine
import com.mathadventure.core.quest.QuestInstance
import com.mathadventure.core.quest.QuestPrerequisiteChecker
import com.mathadventure.core.quest.QuestState
import com.mathadventure.core.quest.QuestStore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FirstVerticalSliceAcceptanceTest {
    @Test
    fun homeVillageForestBattleReturnHomeCompletesStoryAndRewards() {
        val playerId = "player-1"
        val quests = MemoryQuestStore()
        val questEngine = QuestEngine(
            FirstQuestChain.definitions.associateBy { it.id },
            quests,
            quests
        )
        val progressionStore = MemoryProgressionStore()
        val progression = CoreGameProgressionFlow(
            PrototypeGameRewardPolicy(),
            DefaultRpgLevelPolicy(),
            PrototypeGameUnlockPolicy(),
            progressionStore
        )
        val coordinator = QuestProgressionCoordinator(MemoryOutbox(), progression)
        val combat = CombatEngine()

        completeAndReward(questEngine, coordinator, playerId, "story_home_to_village", "visit_village", 100L)
        completeObjective(questEngine, playerId, "story_village_to_forest", "talk_to_npc", 200L)
        completeAndReward(questEngine, coordinator, playerId, "story_village_to_forest", "reach_forest", 300L)

        var state = combat.start("forest-encounter-01", enemyHp = 3)
        repeat(2) {
            val outcome = combat.resolveMathAction(state, CombatAction.ATTACK, true)
            assertTrue(outcome.resolution == CombatResolution.HIT)
            state = outcome.state
        }
        val victory = combat.resolveMathAction(state, CombatAction.ATTACK, true)
        assertEquals(CombatResolution.VICTORY, victory.resolution)
        assertEquals("COMBAT_VICTORY", victory.gameEventType)

        val battleReward = completeAndReward(
            questEngine, coordinator, playerId, "story_first_battle", "win_first_battle", 400L
        )
        assertEquals(75L, battleReward!!.reward.xpDelta)

        val homeQuest = completeAndReward(
            questEngine, coordinator, playerId, "story_return_home", "return_home", 500L
        )!!
        assertEquals(QuestState.COMPLETED, homeQuest.state)
        assertEquals(300L, progressionStore.state.totalXp)
        assertEquals(60L, progressionStore.state.coins)
    }

    private fun completeAndReward(
        engine: QuestEngine,
        coordinator: QuestProgressionCoordinator,
        playerId: String,
        questId: String,
        objectiveId: String,
        time: Long
    ): ProgressionCommit? {
        val instance = completeObjective(engine, playerId, questId, objectiveId, time)
        val completion = engine.completion(playerId, questId, instance.sessionId!!)!!
        return coordinator.record(
            QuestProgressionEventMapper.map(
                completion,
                FirstQuestChain.definitions.first { it.id == questId }.repeatability
            )
        )
    }

    private fun completeObjective(
        engine: QuestEngine,
        playerId: String,
        questId: String,
        objectiveId: String,
        time: Long
    ): QuestInstance {
        if (engine.availability(playerId, questId) == QuestState.AVAILABLE) {
            engine.start(playerId, questId, "session-$questId", time)
        }
        return engine.recordObjectiveProgress(playerId, questId, objectiveId, nowEpochMillis = time + 1)
    }

    private class MemoryQuestStore : QuestStore, QuestPrerequisiteChecker {
        private val data = mutableMapOf<String, QuestInstance>()
        override fun get(playerId: String, questId: String) = data[playerId + ":" + questId]
        override fun save(instance: QuestInstance) {
            data[instance.playerId + ":" + instance.questId] = instance
        }
        override fun isSatisfied(playerId: String, prerequisites: Set<String>) =
            prerequisites.all { get(playerId, it)?.state == QuestState.COMPLETED }
    }

    private class MemoryOutbox : QuestCompletionOutbox {
        private val events = mutableMapOf<String, com.mathadventure.core.gameprogression.GameProgressionEvent>()
        override fun save(event: com.mathadventure.core.gameprogression.GameProgressionEvent) {
            events[event.eventId] = event
        }
        override fun pending(playerId: String) = events.values.filter { it.playerId == playerId }
        override fun remove(eventId: String) { events.remove(eventId) }
    }

    private class MemoryProgressionStore : GameProgressionStore {
        var state = GameProgressionState()
        override fun get(playerId: String) = state
        override fun commit(commit: ProgressionCommit) {
            state = state.copy(
                totalXp = state.totalXp + commit.reward.xpDelta,
                coins = state.coins + commit.reward.coinsDelta,
                rpgLevel = commit.levelAfter,
                grantedEventIds = state.grantedEventIds + commit.event.eventId,
                unlockedIds = state.unlockedIds + commit.unlocks
            )
        }
    }
}
