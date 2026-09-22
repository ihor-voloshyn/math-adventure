package com.mathadventure.core.integration

import com.mathadventure.core.gameprogression.CoreGameProgressionFlow
import com.mathadventure.core.gameprogression.DefaultRpgLevelPolicy
import com.mathadventure.core.gameprogression.GameProgressionState
import com.mathadventure.core.gameprogression.GameProgressionStore
import com.mathadventure.core.gameprogression.ProgressionCommit
import com.mathadventure.core.gameprogression.PrototypeGameRewardPolicy
import com.mathadventure.core.gameprogression.PrototypeGameUnlockPolicy
import com.mathadventure.core.gameprogression.QuestProgressionEventMapper
import com.mathadventure.core.quest.FirstQuestChain
import com.mathadventure.core.quest.QuestEngine
import com.mathadventure.core.quest.QuestInstance
import com.mathadventure.core.quest.QuestPrerequisiteChecker
import com.mathadventure.core.quest.QuestState
import com.mathadventure.core.quest.QuestStore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class QuestGameProgressionIntegrationTest {
    @Test
    fun completingFirstStoryQuestProducesExactlyOneProgressionReward() {
        val questStore = MemoryQuestStore()
        val questEngine = QuestEngine(
            definitions = FirstQuestChain.definitions.associateBy { it.id },
            store = questStore,
            prerequisiteChecker = questStore
        )
        val progressionStore = MemoryProgressionStore()
        val progression = CoreGameProgressionFlow(
            rewardPolicy = PrototypeGameRewardPolicy(),
            levelPolicy = DefaultRpgLevelPolicy(),
            unlockPolicy = PrototypeGameUnlockPolicy(),
            store = progressionStore
        )

        val started = questEngine.start(
            playerId = "player-1",
            questId = "story_home_to_village",
            sessionId = "quest-session-1",
            nowEpochMillis = 100L
        )
        val completed = questEngine.recordObjectiveProgress(
            playerId = started.playerId,
            questId = started.questId,
            objectiveId = "visit_village",
            nowEpochMillis = 200L
        )
        val completion = questEngine.completion(
            playerId = "player-1",
            questId = "story_home_to_village",
            instanceId = "quest-session-1"
        )!!
        val event = QuestProgressionEventMapper.map(
            completion,
            FirstQuestChain.definitions.first { it.id == "story_home_to_village" }.repeatability
        )

        assertEquals(QuestState.COMPLETED, completed.state)
        val firstCommit = progression.record(event)
        val duplicateCommit = progression.record(event)

        assertEquals(75L, firstCommit!!.reward.xpDelta)
        assertEquals(15L, firstCommit.reward.coinsDelta)
        assertNull(duplicateCommit)
        assertEquals(1, progressionStore.commits.size)
        assertEquals(75L, progressionStore.state.totalXp)
        assertEquals(15L, progressionStore.state.coins)
    }

    private class MemoryQuestStore : QuestStore, QuestPrerequisiteChecker {
        private val data = mutableMapOf<String, QuestInstance>()

        override fun get(playerId: String, questId: String): QuestInstance? =
            data[playerId + ":" + questId]

        override fun save(instance: QuestInstance) {
            data[instance.playerId + ":" + instance.questId] = instance
        }

        override fun isSatisfied(playerId: String, prerequisites: Set<String>): Boolean =
            prerequisites.all { get(playerId, it)?.state == QuestState.COMPLETED }
    }

    private class MemoryProgressionStore : GameProgressionStore {
        var state = GameProgressionState()
        val commits = mutableListOf<ProgressionCommit>()

        override fun get(playerId: String): GameProgressionState = state

        override fun commit(commit: ProgressionCommit) {
            if (commit.event.eventId in state.grantedEventIds) return
            commits += commit
            state = state.copy(
                totalXp = state.totalXp + commit.reward.xpDelta,
                coins = state.coins + commit.reward.coinsDelta,
                rpgLevel = commit.levelAfter,
                bestResults = state.bestResults + listOfNotNull(commit.bestResultAfter).associateBy { it.sourceId },
                grantedEventIds = state.grantedEventIds + commit.event.eventId
            )
        }
    }
}
