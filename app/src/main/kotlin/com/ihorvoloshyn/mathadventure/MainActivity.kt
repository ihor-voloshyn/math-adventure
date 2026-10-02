package com.ihorvoloshyn.mathadventure

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.graphics.drawable.GradientDrawable
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import com.mathadventure.core.adaptive.AdaptiveCandidate
import com.mathadventure.core.adaptive.AdaptivePolicy
import com.mathadventure.core.adaptive.AdaptivePriority
import com.mathadventure.core.adaptive.RuleBasedAdaptiveEngine
import com.mathadventure.core.combat.CombatEngine
import com.mathadventure.core.combat.CombatResolution
import com.mathadventure.core.combat.CombatState
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
import com.mathadventure.core.model.InputType
import com.mathadventure.core.model.SkillState
import com.mathadventure.core.model.TaskMode
import com.mathadventure.core.validation.LogicalTaskValidator
import com.mathadventure.core.validation.MathematicalTaskValidator
import com.mathadventure.core.validation.StructuralTaskValidator
import com.mathadventure.core.validation.TaskValidationPipeline
import com.ihorvoloshyn.mathadventure.ui.flow.MathBattleFlowCoordinator
import com.ihorvoloshyn.mathadventure.ui.math.MathTaskUiState

class MainActivity : Activity() {
    private enum class Stage { HOME, VILLAGE, FOREST, COMBAT }
    private val playerId = "prototype-player"
    private val mathEngine = BasicMathEngine()
    private val combatEngine = CombatEngine()
    private lateinit var masteryStore: MasteryStateStore
    private lateinit var masterySystem: PolicyDrivenMasterySystem
    private lateinit var flow: CoreLearningFlow
    private lateinit var flowCoordinator: MathBattleFlowCoordinator
    private lateinit var progressStore: PrototypeProgressStore
    private lateinit var gameProgression: CoreGameProgressionFlow
    private lateinit var gameProgressionStore: AndroidGameProgressionInventoryStore
    private lateinit var gameProgressionLoot: GameProgressionLootCoordinator
    private lateinit var questProgression: QuestProgressionCoordinator
    private lateinit var questStore: AndroidQuestStore
    private lateinit var questEngine: QuestEngine

    private var stage = Stage.HOME
    private var generated: com.mathadventure.core.flow.GeneratedTask? = null
    private var combatState: CombatState? = null
    private var combatTaskIndex = 0
    private var combatInputLocked = false
    private val disabledCombatAnswers = mutableSetOf<String>()
    private var combatOriginStage = Stage.FOREST

    private lateinit var renderer: AdventureRenderer
    private lateinit var title: TextView
    private lateinit var message: TextView
    private lateinit var action: Button
    private lateinit var backAction: Button
    private lateinit var answers: LinearLayout
    private lateinit var taskPanel: LinearLayout
    private lateinit var taskText: TextView
    private lateinit var heartsText: TextView

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
        flowCoordinator = MathBattleFlowCoordinator(flow, combatEngine)

        val root = FrameLayout(this)
        renderer = AdventureRenderer(this)
        root.addView(renderer)

        val hud = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(24, 18, 24, 12)
            background = panelBackground(0xB81B2638.toInt(), 24f)
        }
        title = textView(24f).apply { setTypeface(typeface, android.graphics.Typeface.BOLD) }
        message = textView(16f).apply { setPadding(0, 8, 0, 4) }
        hud.addView(title)
        hud.addView(message)

        val bottom = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(18, 8, 18, 18)
            background = panelBackground(0xCC101722.toInt(), 26f)
        }
        action = gameButton().apply { setOnClickListener { onPrimaryAction() } }
        bottom.addView(action, LinearLayout.LayoutParams(-1, 56).apply { bottomMargin = 8 })
        backAction = gameButton().apply { setOnClickListener { onBackAction() } }
        bottom.addView(backAction, LinearLayout.LayoutParams(-1, 48).apply { bottomMargin = 4 })
        answers = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            visibility = View.GONE
        }
        root.addView(hud, FrameLayout.LayoutParams(-1, -2))
        taskPanel = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(14, 12, 14, 12)
            background = panelBackground(0xD9162034.toInt(), 22f)
            visibility = View.GONE
        }
        heartsText = textView(17f).apply {
            gravity = Gravity.CENTER
            setTypeface(typeface, android.graphics.Typeface.BOLD)
            setPadding(8, 2, 8, 6)
        }
        taskText = textView(30f).apply {
            gravity = Gravity.CENTER
            setTypeface(typeface, android.graphics.Typeface.BOLD)
            setPadding(8, 2, 8, 12)
        }
        taskPanel.addView(heartsText, LinearLayout.LayoutParams(-1, -2))
        taskPanel.addView(taskText, LinearLayout.LayoutParams(-1, -2))
        taskPanel.addView(answers, LinearLayout.LayoutParams(-1, 122))
        val taskParams = FrameLayout.LayoutParams(-1, -2, Gravity.BOTTOM).apply {
            setMargins(18, 0, 18, 132)
        }
        root.addView(taskPanel, taskParams)
        root.addView(bottom, FrameLayout.LayoutParams(-1, -2, Gravity.BOTTOM))
        setContentView(root)
        questProgression.recover(playerId)
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
        if (questEngine.availability(playerId, "story_first_battle") == QuestState.COMPLETED) {
            renderStage()
            return
        }
        combatState = combatEngine.start("forest-encounter-01")
        combatTaskIndex = 0
        combatInputLocked = false
        disabledCombatAnswers.clear()
        combatOriginStage = stage
        stage = Stage.COMBAT
        renderer.setVictory(false)
        flowCoordinator.startCombat(combatState!!)
        generateMathTask()
        renderStage()
    }

    private fun generateMathTask() {
        generated = flow.generateNext(
            playerId = playerId,
            skillStates = skillStates(),
            availableSkills = setOf("ADD_BASIC", "ADD_CROSS_TEN"),
            inputType = InputType.NUMERIC,
            generationContext = mapOf("taskIndex" to combatTaskIndex.toString())
        )
        flowCoordinator.presentTask(generated!!, combatTaskIndex + 1, 0)
        showAnswerOptions(generated!!.task.answerSpec, disabledCombatAnswers)
        taskText.text = generated!!.task.prompt
        updateCombatHud(combatState!!)
        taskPanel.visibility = View.VISIBLE
        combatInputLocked = false
        combatTaskIndex++
    }

    private fun showAnswerOptions(answerSpec: String, disabledValues: Set<String> = emptySet()) {
        answers.removeAllViews()
        val correct = answerSpec.toIntOrNull() ?: return
        // Every battle always has exactly six answer choices.
        val offsets = listOf(-10, -1, 0, 1, 2, 10)
        val values = offsets.map { correct + it }.distinct()
        val ordered = when (correct % 6) {
            0 -> values
            1 -> values.reversed()
            2 -> listOf(values[2], values[0], values[4], values[1], values[5], values[3])
            3 -> listOf(values[3], values[5], values[1], values[4], values[0], values[2])
            4 -> listOf(values[4], values[1], values[3], values[0], values[5], values[2])
            else -> listOf(values[5], values[2], values[0], values[4], values[1], values[3])
        }
        ordered.chunked(3).forEach { rowValues ->
            val row = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER
            }
            rowValues.forEach { value ->
                row.addView(gameButton().apply {
                    text = value.toString()
                    isEnabled = value.toString() !in disabledValues
                    alpha = if (isEnabled) 1f else 0.38f
                    setTextSize(20f)
                    typeface = android.graphics.Typeface.DEFAULT_BOLD
                    background = panelBackground(0xF03B4B63.toInt(), 18f)
                    setOnClickListener {
                        if (combatInputLocked) return@setOnClickListener
                        combatInputLocked = true
                        isEnabled = false
                        alpha = 0.38f
                        animate().scaleX(0.94f).scaleY(0.94f).setDuration(70L).withEndAction {
                            submitAnswer(value.toString())
                        }.start()
                    }
                }, LinearLayout.LayoutParams(0, 56, 1f).apply {
                    marginStart = 4
                    marginEnd = 4
                })
            }
            answers.addView(row, LinearLayout.LayoutParams(-1, 58))
        }
    }

    private fun showIncorrectFeedback() {
        val state = combatState ?: return
        title.text = "💡 Попробуй ещё раз"
        message.text = "❌ Неверный ответ. Осталось сердец: ${state.heroHearts}. Та же задача остаётся."
        heartsText.animate()
            .scaleX(1.18f)
            .scaleY(1.18f)
            .setDuration(110L)
            .withEndAction {
                heartsText.animate().scaleX(1f).scaleY(1f).setDuration(150L).start()
            }
            .start()
        taskPanel.animate()
            .translationX(10f)
            .setDuration(45L)
            .withEndAction {
                taskPanel.animate()
                    .translationX(-10f)
                    .setDuration(45L)
                    .withEndAction {
                        taskPanel.animate().translationX(0f).setDuration(45L).start()
                    }
                    .start()
            }
            .start()
    }

    private fun updateCombatHud(state: CombatState) {
        val hearts = buildString {
            repeat(state.heroHearts) { append("♥ ") }
            repeat(state.maxHeroHearts - state.heroHearts) { append("♡ ") }
        }.trim()
        heartsText.text = "СЕРДЦА  $hearts"
        title.text = "Поляна с монстром"
    }

    private fun textView(size: Float) = TextView(this).apply {
        textSize = size
        setTextColor(Color.WHITE)
        setShadowLayer(5f, 2f, 2f, Color.BLACK)
    }

    private fun panelBackground(color: Int, radius: Float) =
        GradientDrawable().apply {
            setColor(color)
            cornerRadius = radius
        }

    private fun gameButton() = Button(this).apply {
        setTextColor(Color.WHITE)
        textSize = 15f
        isAllCaps = false
        minHeight = 0
        minimumHeight = 0
        setPadding(10, 0, 10, 0)
        background = panelBackground(0xE52D3A4A.toInt(), 22f)
        stateListAnimator = null
    }

    private fun submitAnswer(value: String) {
        if (stage != Stage.COMBAT || combatState?.active != true) {
            combatInputLocked = false
            return
        }
        val current = generated ?: run { combatInputLocked = false; return }
        val resolution = flowCoordinator.evaluate(
            playerId = playerId,
            generated = current,
            attemptId = "attempt-" + System.currentTimeMillis(),
            answer = value,
            timestampEpochMillis = System.currentTimeMillis(),
            evidenceMetadata = mapOf(
                "difficultyBand" to if (current.task.difficulty <= 1) "INTRO" else "NORMAL",
                "evidenceDiverse" to "false"
            )
        )
        combatState = flowCoordinator.battleState?.combat
        when (resolution) {
            CombatResolution.VICTORY, CombatResolution.CORRECT -> {
                progressStore.recordCorrect()
                finishResolvedAnswer(CombatResolution.VICTORY)
            }
            CombatResolution.INCORRECT -> {
                progressStore.recordIncorrect()
                disabledCombatAnswers.add(value)
                combatInputLocked = false
                showAnswerOptions(current.task.answerSpec, disabledCombatAnswers)
                updateCombatHud(combatState!!)
                showIncorrectFeedback()
            }
            CombatResolution.DEFEAT -> {
                progressStore.recordIncorrect()
                finishResolvedAnswer(resolution)
            }
        }
    }

    private fun finishResolvedAnswer(resolution: CombatResolution) {
        combatState = flowCoordinator.battleState?.combat
        when (resolution) {
            CombatResolution.VICTORY, CombatResolution.CORRECT -> {
                renderer.setVictory(true)
                val questCommit = recordQuestObjective("story_first_battle", "win_first_battle")
                val combatId = combatState!!.combatId
                val attempts = combatState!!.attemptsUsed + 1
                val masteryLabel = when (attempts) {
                    1 -> "Максимум"
                    2 -> "Среднее"
                    else -> "Минимум"
                }
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
                flowCoordinator.rewardCurrentBattle()
                answers.visibility = View.GONE
                taskPanel.visibility = View.VISIBLE
                taskPanel.alpha = 0f
                taskPanel.scaleX = 0.88f
                taskPanel.scaleY = 0.88f
                heartsText.text = "СЕРДЦА  " + buildString {
                    repeat(combatState!!.heroHearts) { append("♥ ") }
                    repeat(combatState!!.maxHeroHearts - combatState!!.heroHearts) { append("♡ ") }
                }.trim()
                taskText.text = when (attempts) {
                    1 -> "★★★  МАСТЕРСТВО\nМаксимум"
                    2 -> "★★☆  МАСТЕРСТВО\nСреднее"
                    else -> "★☆☆  МАСТЕРСТВО\nМинимум"
                }
                title.text = "Задача решена!"
                message.text = buildString {
                    append(if (commit != null) {
                        "Победа! Задача решена с " + attempts +
                            " попытки. Сердец осталось: " + combatState!!.heroHearts + "/" + combatState!!.maxHeroHearts +
                            ". +" + commit.reward.xpDelta + " XP, +" + commit.reward.coinsDelta + " монет."
                    } else {
                        "Задача уже была завершена. Награда за неё уже получена."
                    })
                    if (questCommit != null) {
                        append(" Квест завершён: +")
                        append(questCommit.reward.xpDelta)
                        append(" XP, +")
                        append(questCommit.reward.coinsDelta)
                        append(" монет.")
                    }
                    append(" Мастерство: ")
                    append(masteryLabel)
                    append(". RPG Level ")
                    append(progression.rpgLevel)
                    append(".")
                }
                action.text = "↩ Вернуться в локацию"
                backAction.visibility = View.GONE
                taskPanel.animate()
                    .alpha(1f)
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(220L)
                    .start()
                stage = combatOriginStage\n                renderStage()\n
            }
            CombatResolution.DEFEAT -> {
                // Defeat returns to the exact location from which the battle was entered.
                // The quest remains active, so the battle transition is still available.
                stage = combatOriginStage
                answers.visibility = View.GONE
                taskPanel.visibility = View.GONE
                taskPanel.alpha = 1f
                taskPanel.scaleX = 1f
                taskPanel.scaleY = 1f
                title.text = "Бой проигран"
                message.text = "Все 3 сердца потеряны. Задание не выполнено — можно снова пойти на поляну."
                renderStage()
            }
            CombatResolution.INCORRECT -> Unit
        }
    }

    private fun onPrimaryAction() {
        when (stage) {
            Stage.HOME -> {
                stage = Stage.VILLAGE
                renderStage()
            }
            Stage.VILLAGE -> {
                stage = Stage.FOREST
                ensureQuestStarted("story_first_battle")
                renderStage()
            }
            Stage.FOREST -> {
                if (questEngine.availability(playerId, "story_first_battle") == QuestState.COMPLETED) {
                    renderStage()
                } else {
                    ensureQuestStarted("story_first_battle")
                    startCombat()
                }
            }
            Stage.COMBAT -> Unit
        }
    }

    private fun onBackAction() {
        when (stage) {
            Stage.HOME -> Unit
            Stage.VILLAGE -> {
                stage = Stage.HOME
                renderStage()
            }
            Stage.FOREST -> {
                stage = Stage.VILLAGE
                renderStage()
            }
            Stage.COMBAT -> Unit
        }
    }

    private fun renderStage() {
        renderer.setStage(stage.ordinal)
        taskPanel.visibility = if (stage == Stage.COMBAT) View.VISIBLE else View.GONE
        renderer.setEquippedWeapon("weapon_sword_sparks")
        backAction.visibility = if (stage == Stage.COMBAT || stage == Stage.HOME) View.GONE else View.VISIBLE

        when (stage) {
            Stage.HOME -> {
                title.text = "Дом"
                message.text = "Задание: Отправиться в деревню."
                action.text = "→ Деревня"
                answers.visibility = View.GONE
            }
            Stage.VILLAGE -> {
                title.text = "Деревня"
                message.text = "Задание: Пройти из деревни в Дремучий лес."
                action.text = "→ Дремучий лес"
                backAction.text = "← Дом"
                answers.visibility = View.GONE
            }
            Stage.FOREST -> {
                title.text = "Дремучий лес"
                val defeated = questEngine.availability(playerId, "story_first_battle") == QuestState.COMPLETED
                if (defeated) {
                    message.text = "Задание: Исследовать Дремучий лес."
                    action.text = "Монстр побеждён"
                } else {
                    message.text = "Задание: Сразиться с монстром в Дремучем лесу."
                    action.text = "→ Поляна с монстром"
                }
                backAction.text = "← Деревня"
                answers.visibility = View.GONE
            }
            Stage.COMBAT -> {
                val state = combatState ?: return
                updateCombatHud(state)
                message.text = "Задание: Сразиться с монстром в Дремучем лесу."
                action.text = ""
                backAction.visibility = View.GONE
                answers.visibility = View.VISIBLE
            }
        }
    }
}

