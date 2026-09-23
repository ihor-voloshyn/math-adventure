package com.ihorvoloshyn.mathadventure

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import com.mathadventure.core.adaptive.AdaptiveCandidate
import com.mathadventure.core.adaptive.AdaptivePolicy
import com.mathadventure.core.adaptive.AdaptivePriority
import com.mathadventure.core.adaptive.RuleBasedAdaptiveEngine
import com.mathadventure.core.combat.CombatAction
import com.mathadventure.core.combat.CombatEngine
import com.mathadventure.core.combat.CombatResolution
import com.mathadventure.core.combat.CombatState
import com.mathadventure.core.combat.EquipmentCombatStatsResolver
import com.mathadventure.core.curriculum.Curriculum
import com.mathadventure.core.flow.CoreLearningFlow
import com.mathadventure.core.gameprogression.CoreGameProgressionFlow
import com.mathadventure.core.gameprogression.GameProgressionLootCoordinator
import com.mathadventure.core.gameprogression.GameEventType
import com.mathadventure.core.gameprogression.GameProgressionEvent
import com.mathadventure.core.gameprogression.DefaultRpgLevelPolicy
import com.mathadventure.core.gameprogression.PrototypeGameRewardPolicy
import com.mathadventure.core.gameprogression.PrototypeGameUnlockPolicy
import com.mathadventure.core.gameprogression.ProgressionCommit
import com.mathadventure.core.gameprogression.QuestProgressionCoordinator
import com.mathadventure.core.gameprogression.QuestProgressionEventMapper
import com.mathadventure.core.quest.FirstQuestChain
import com.mathadventure.core.quest.QuestEngine
import com.mathadventure.core.quest.QuestState
import com.mathadventure.core.generator.DeterministicTaskGenerator
import com.mathadventure.core.mastery.ApprovedMasteryPolicy
import com.mathadventure.core.mastery.MasteryStateStore
import com.mathadventure.core.mastery.PolicyDrivenMasterySystem
import com.mathadventure.core.math.BasicMathEngine
import com.mathadventure.core.model.AnswerResult
import com.mathadventure.core.model.InputType
import com.mathadventure.core.model.SkillState
import com.mathadventure.core.model.TaskMode
import com.mathadventure.core.validation.LogicalTaskValidator
import com.mathadventure.core.validation.MathematicalTaskValidator
import com.mathadventure.core.validation.StructuralTaskValidator
import com.mathadventure.core.validation.TaskValidationPipeline

class MainActivity : Activity() {
    private enum class Stage { HOME, VILLAGE, FOREST, COMBAT, RETURN_HOME }
    private val playerId = "prototype-player"
    private val mathEngine = BasicMathEngine()
    private val combatEngine = CombatEngine()
    private val equipmentCombatStatsResolver = EquipmentCombatStatsResolver(PrototypeItemCatalog.definitions)
    private lateinit var masteryStore: MasteryStateStore
    private lateinit var masterySystem: PolicyDrivenMasterySystem
    private lateinit var flow: CoreLearningFlow
    private lateinit var progressStore: PrototypeProgressStore
    private lateinit var gameProgression: CoreGameProgressionFlow
    private lateinit var gameProgressionStore: AndroidGameProgressionInventoryStore
    private lateinit var gameProgressionLoot: GameProgressionLootCoordinator
    private lateinit var itemEngine: com.mathadventure.core.items.ItemEngine
    private lateinit var questProgression: QuestProgressionCoordinator
    private lateinit var questStore: AndroidQuestStore
    private lateinit var questEngine: QuestEngine

    private var stage = Stage.HOME
    private var generated: com.mathadventure.core.flow.GeneratedTask? = null
    private var combatState: CombatState? = null
    private var sessionCorrect = 0
    private var sessionIncorrect = 0

    private lateinit var renderer: AdventureRenderer
    private lateinit var title: TextView
    private lateinit var message: TextView
    private lateinit var action: Button
    private lateinit var answers: LinearLayout
    private lateinit var fleeButton: Button
    private lateinit var equipmentButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        progressStore = PrototypeProgressStore(this)
        gameProgressionStore = AndroidGameProgressionInventoryStore(this, PrototypeItemCatalog.definitions)
        gameProgression = CoreGameProgressionFlow(
            rewardPolicy = PrototypeGameRewardPolicy(),
            levelPolicy = DefaultRpgLevelPolicy(),
            unlockPolicy = PrototypeGameUnlockPolicy(),
            store = gameProgressionStore
        )
        itemEngine = com.mathadventure.core.items.ItemEngine(PrototypeItemCatalog.definitions, gameProgressionStore)
        gameProgressionLoot = GameProgressionLootCoordinator(
            progression = gameProgression,
            store = gameProgressionStore,
            lootFactory = com.mathadventure.core.items.DeterministicLootItemFactory(PrototypeItemCatalog.definitions)
        )
        questProgression = QuestProgressionCoordinator(
            outbox = AndroidQuestProgressionOutbox(this),
            progression = gameProgression
        )
        questStore = AndroidQuestStore(this)
        questEngine = QuestEngine(
            definitions = FirstQuestChain.definitions.associateBy { it.id },
            store = questStore,
            prerequisiteChecker = questStore
        )
        masteryStore = AndroidMasteryStateStore(this)
        masterySystem = PolicyDrivenMasterySystem(masteryStore, ApprovedMasteryPolicy())
        flow = CoreLearningFlow(
            adaptive = RuleBasedAdaptiveEngine(Curriculum.mvp(), adaptivePolicy()),
            generator = DeterministicTaskGenerator(),
            validation = TaskValidationPipeline(
                StructuralTaskValidator(),
                LogicalTaskValidator(),
                MathematicalTaskValidator(mathEngine)
            ),
            mathEngine = mathEngine,
            mastery = masterySystem
        )

        val root = FrameLayout(this)
        renderer = AdventureRenderer(this)
        root.addView(renderer)

        val hud = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(28, 20, 28, 20)
        }
        title = textView(24f)
        message = textView(17f)
        hud.addView(title)
        hud.addView(message)

        val bottom = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(28, 8, 28, 22)
        }
        action = Button(this).apply { setOnClickListener { onPrimaryAction() } }
        bottom.addView(action, LinearLayout.LayoutParams(-1, 62))
        fleeButton = Button(this).apply {
            text = "Убежать"
            setOnClickListener { flee() }
        }
        bottom.addView(fleeButton, LinearLayout.LayoutParams(-1, 62))
        equipmentButton = Button(this).apply { setOnClickListener { toggleWeapon() } }
        bottom.addView(equipmentButton, LinearLayout.LayoutParams(-1, 62))

        answers = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            visibility = View.GONE
        }
        bottom.addView(answers)

        root.addView(hud, FrameLayout.LayoutParams(-1, -2))
        root.addView(bottom, FrameLayout.LayoutParams(-1, -2, Gravity.BOTTOM))
        setContentView(root)
        questProgression.recover(playerId)
        ensureQuestStarted("story_home_to_village")
        renderStage()
    }

    private fun ensureQuestStarted(questId: String) {
        if (questEngine.availability(playerId, questId) != QuestState.AVAILABLE) return
        questEngine.start(
            playerId = playerId,
            questId = questId,
            sessionId = "quest-session-" + questId + "-" + System.currentTimeMillis(),
            nowEpochMillis = System.currentTimeMillis()
        )
    }

    private fun recordQuestObjective(questId: String, objectiveId: String): ProgressionCommit? {
        val current = questStore.get(playerId, questId)
        if (current?.state != QuestState.ACTIVE) return null
        val updated = questEngine.recordObjectiveProgress(
            playerId = playerId,
            questId = questId,
            objectiveId = objectiveId,
            nowEpochMillis = System.currentTimeMillis()
        )
        if (updated.state != QuestState.COMPLETED) return null
        val definition = FirstQuestChain.definitions.first { it.id == questId }
        val completion = questEngine.completion(
            playerId = playerId,
            questId = questId,
            instanceId = updated.sessionId ?: error("completed quest has no session id")
        ) ?: return null
        return questProgression.record(QuestProgressionEventMapper.map(completion, definition.repeatability))
    }

    private fun adaptivePolicy(): AdaptivePolicy = object : AdaptivePolicy {
        override fun priority(candidate: AdaptiveCandidate): AdaptivePriority? =
            when {
                !candidate.requiredPrerequisitesSatisfied -> AdaptivePriority.REQUIRED_PREREQUISITE
                candidate.state.consecutiveErrors >= 2 -> AdaptivePriority.RECENT_ERROR_RECOVERY
                candidate.state.reviewState == "REVIEW" -> AdaptivePriority.DUE_REVIEW
                candidate.state.mastery < 4 -> AdaptivePriority.REINFORCE
                else -> AdaptivePriority.ADVANCE
            }

        override fun mode(candidate: AdaptiveCandidate): TaskMode = TaskMode.DIRECT

        override fun difficulty(candidate: AdaptiveCandidate): Int =
            when {
                candidate.state.consecutiveErrors >= 2 -> 1
                candidate.state.mastery <= 1 -> 1
                candidate.state.mastery == 2 -> 2
                else -> 3
            }

        override fun contextType(candidate: AdaptiveCandidate): String = "BATTLE"
    }

    private fun skillStates(): List<SkillState> =
        listOf("ADD_BASIC", "ADD_CROSS_TEN").map { masterySystem.getSkillState(playerId, it) }

    private fun startCombat() {
        val stats = equipmentCombatStatsResolver.resolve(itemEngine.getInventory(playerId), itemEngine.getEquipment(playerId))
        combatState = combatEngine.start("forest-encounter-01", heroHearts = 3 + stats.hearts, enemyHp = 3)
        stage = Stage.COMBAT
        renderer.setVictory(false)
        generateMathTask()
        renderStage()
    }

    private fun generateMathTask() {
        generated = flow.generateNext(
            playerId = playerId,
            skillStates = skillStates(),
            availableSkills = setOf("ADD_BASIC", "ADD_CROSS_TEN"),
            inputType = InputType.NUMERIC
        )
        showAnswerOptions(generated!!.task.answerSpec)
    }

    private fun showAnswerOptions(answerSpec: String) {
        answers.removeAllViews()
        val correct = answerSpec.toIntOrNull() ?: return
        val offsets = listOf(-1, 0, 1, 10)
        val values = offsets.map { correct + it }.distinct()
        val ordered = when (correct % 4) {
            0 -> values
            1 -> values.reversed()
            2 -> listOf(values[1], values[0], values[3], values[2])
            else -> listOf(values[2], values[3], values[0], values[1])
        }
        ordered.forEach { value ->
            answers.addView(Button(this).apply {
                text = value.toString()
                setTextColor(Color.WHITE)
                setOnClickListener { submitAttack(value.toString()) }
            }, LinearLayout.LayoutParams(0, 62, 1f))
        }
    }

    private fun textView(size: Float) = TextView(this).apply {
        textSize = size
        setTextColor(Color.WHITE)
        setShadowLayer(6f, 2f, 2f, Color.BLACK)
    }

    private fun advance() {
        stage = when (stage) {
            Stage.HOME -> Stage.VILLAGE
            Stage.VILLAGE -> Stage.FOREST
            Stage.FOREST -> Stage.COMBAT
            Stage.COMBAT -> Stage.COMBAT
            Stage.RETURN_HOME -> Stage.HOME
        }
        renderStage()
    }

    private fun submitAttack(value: String) {
        val current = generated ?: return
        val answered = flow.answer(
            playerId = playerId,
            generated = current,
            attemptId = "attempt-" + System.currentTimeMillis(),
            submittedAnswer = value,
            timestampEpochMillis = System.currentTimeMillis(),
            evidenceMetadata = mapOf(
                "difficultyBand" to if (current.task.difficulty <= 1) "INTRO" else "NORMAL",
                "evidenceDiverse" to "false"
            )
        )
        val stats = equipmentCombatStatsResolver.resolve(itemEngine.getInventory(playerId), itemEngine.getEquipment(playerId))
        val result = combatEngine.resolveMathAction(
            combatState ?: return,
            CombatAction.ATTACK,
            answered.evaluation.result == AnswerResult.CORRECT,
            attackDamage = stats.attackPower
        )
        combatState = result.state
        if (answered.evaluation.result == AnswerResult.CORRECT) progressStore.recordCorrect()
        else progressStore.recordIncorrect()

        when (result.resolution) {
            CombatResolution.VICTORY -> {
                renderer.setVictory(true)
                val questCommit = recordQuestObjective("story_first_battle", "win_first_battle")
                val combatId = combatState!!.combatId
                val commit = gameProgressionLoot.record(
                    GameProgressionEvent(
                        eventId = "combat-victory-" + combatId,
                        playerId = playerId,
                        eventType = GameEventType.COMBAT_VICTORY,
                        sourceId = combatId,
                        sessionId = combatId,
                        outcome = "VICTORY",
                        timestampEpochMillis = System.currentTimeMillis()
                    )
                )
                val progression = gameProgressionStore.get(playerId)
                stage = Stage.RETURN_HOME
                answers.visibility = View.GONE
                fleeButton.visibility = View.GONE
                title.text = "Победа над врагом!"
                message.text = buildString {
                    append(if (commit != null) {
                        "Победа! +" + commit.reward.xpDelta + " XP, +" + commit.reward.coinsDelta + " монет."
                    } else {
                        "Победа! Награда за бой уже была получена."
                    })
                    if (questCommit != null) {
                        append(" Квест завершён: +")
                        append(questCommit.reward.xpDelta)
                        append(" XP, +")
                        append(questCommit.reward.coinsDelta)
                        append(" монет.")
                    }
                    append(" RPG Level ")
                    append(progression.rpgLevel)
                    append(".")
                }
                action.text = "Вернуться домой"
            }
            CombatResolution.DEFEAT -> {
                stage = Stage.RETURN_HOME
                answers.visibility = View.GONE
                title.text = "Поражение"
                message.text = "Ты потерял бой. Подтверждённый прогресс сохранён."
                action.text = "Вернуться домой"
            }
            else -> {
                message.text = if (answered.evaluation.result == AnswerResult.CORRECT)
                    "Попадание! Враг: " + combatState!!.enemyHp + "/3 HP. Сердца: " + combatState!!.heroHearts + "/" + combatState!!.maxHeroHearts + "."
                else
                    "Промах. Враг атакует! Сердца: " + combatState!!.heroHearts + "/" + combatState!!.maxHeroHearts + "."
                generateMathTask()
                renderStage()
            }
        }
    }

    private fun defend() {
        val result = combatEngine.resolveMathAction(combatState ?: return, CombatAction.DEFEND, true)
        combatState = result.state
        if (result.resolution == CombatResolution.DEFEAT) {
            stage = Stage.RETURN_HOME
            answers.visibility = View.GONE
            title.text = "Поражение"
            message.text = "Враг оказался сильнее. Подтверждённый прогресс сохранён."
            action.text = "Вернуться домой"
        } else {
            generateMathTask()
            renderStage()
            message.text = "Ты защищаешься. Сердца: " + combatState!!.heroHearts + "/3. Теперь твой ход."
        }
    }

    private fun flee() {
        combatState = combatEngine.resolveMathAction(combatState ?: return, CombatAction.FLEE, false).state
        stage = Stage.RETURN_HOME
        answers.visibility = View.GONE
        title.text = "Отступление"
        message.text = "Ты покинул бой без победы. Награда за победу не получена."
        action.text = "Вернуться домой"
    }

    private fun onPrimaryAction() {
        when (stage) {
            Stage.HOME -> {
                val questCommit = recordQuestObjective("story_home_to_village", "visit_village")
                ensureQuestStarted("story_village_to_forest")
                stage = Stage.VILLAGE
                renderStage()
                if (questCommit != null) {
                    message.text = "Квест завершён: +" + questCommit.reward.xpDelta + " XP, +" + questCommit.reward.coinsDelta + " монет. Новый путь открыт."
                }
            }
            Stage.VILLAGE -> {
                recordQuestObjective("story_village_to_forest", "talk_to_npc")
                val questCommit = recordQuestObjective("story_village_to_forest", "reach_forest")
                ensureQuestStarted("story_first_battle")
                stage = Stage.FOREST
                renderStage()
                if (questCommit != null) {
                    message.text = "Квест завершён: +" + questCommit.reward.xpDelta + " XP, +" + questCommit.reward.coinsDelta + " монет."
                }
            }
            Stage.FOREST -> startCombat()
            Stage.COMBAT -> defend()
            Stage.RETURN_HOME -> {
                ensureQuestStarted("story_return_home")
                val questCommit = recordQuestObjective("story_return_home", "return_home")
                stage = Stage.HOME
                renderStage()
                if (questCommit != null) {
                    message.text = "Квест завершён: +" + questCommit.reward.xpDelta + " XP, +" + questCommit.reward.coinsDelta + " монет."
                }
            }
        }
    }

    private fun toggleWeapon() {
        val equipment = itemEngine.getEquipment(playerId)
        if (equipment.weaponInstanceId != null) {
            itemEngine.unequip(playerId, com.mathadventure.core.items.EquipmentSlot.WEAPON)
        } else {
            val sword = itemEngine.getInventory(playerId).firstOrNull { it.itemId == "sword_sparks" } ?: return
            itemEngine.equip(playerId, sword.instanceId, gameProgressionStore.get(playerId).rpgLevel)
        }
        renderStage()
    }

    private fun renderStage() {
        val equipment = itemEngine.getEquipment(playerId)
        val weaponVisualId = equipment.weaponInstanceId?.let { instanceId ->
            itemEngine.getInventory(playerId).firstOrNull { it.instanceId == instanceId }?.let { item ->
                itemEngine.getItemDefinition(item.itemId).visualId
            }
        }
        renderer.setEquippedWeapon(weaponVisualId)
        when (stage) {
            Stage.HOME -> {
                title.text = "Дом героя"
                val weapon = equipment.weaponInstanceId
                val ownedSword = itemEngine.getInventory(playerId).any { it.itemId == "sword_sparks" }
                message.text = if (weapon != null) "Питомец ждёт нового приключения. Оружие экипировано." else "Питомец ждёт нового приключения."
                action.text = "Идти в деревню"
                equipmentButton.visibility = if (ownedSword) View.VISIBLE else View.GONE
                equipmentButton.text = if (weapon != null) "Снять меч" else "Экипировать меч"
                answers.visibility = View.GONE
            }
            Stage.VILLAGE -> {
                title.text = "Деревенская площадь"
                equipmentButton.visibility = View.GONE
                message.text = "NPC просит проверить дорогу в лес."
                action.text = "Идти в лес"
                answers.visibility = View.GONE
            }
            Stage.FOREST -> {
                title.text = "Лес"
                message.text = "Впереди маленькое существо."
                action.text = "Начать бой"
                equipmentButton.visibility = View.GONE
                answers.visibility = View.GONE
            }
            Stage.COMBAT -> {
                val state = combatState ?: return
                title.text = "Бой • Сердца " + state.heroHearts + "/3 • Враг " + state.enemyHp + "/3"
                message.text = generated?.task?.prompt ?: "Математическая атака"
                action.text = "Защищаться"
                equipmentButton.visibility = View.GONE
                answers.visibility = View.VISIBLE
                fleeButton.visibility = View.VISIBLE
            }
            Stage.RETURN_HOME -> {
                title.text = "Возвращение"
                message.text = "Игровой цикл завершён. Правильных ответов в сохранении: ${progressStore.totalCorrect}."
                action.text = "Вернуться домой"
                equipmentButton.visibility = View.GONE
                answers.visibility = View.GONE
            }
        }
    }
}
