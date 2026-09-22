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
import com.mathadventure.core.contracts.ValidationResult
import com.mathadventure.core.curriculum.Curriculum
import com.mathadventure.core.flow.CoreLearningFlow
import com.mathadventure.core.generator.DeterministicTaskGenerator
import com.mathadventure.core.mastery.InMemoryMasteryStateStore
import com.mathadventure.core.mastery.MasteryUpdatePolicy
import com.mathadventure.core.mastery.PolicyDrivenMasterySystem
import com.mathadventure.core.math.BasicMathEngine
import com.mathadventure.core.model.AnswerResult
import com.mathadventure.core.model.InputType
import com.mathadventure.core.model.TaskMode
import com.mathadventure.core.validation.LogicalTaskValidator
import com.mathadventure.core.validation.MathematicalTaskValidator
import com.mathadventure.core.validation.StructuralTaskValidator
import com.mathadventure.core.validation.TaskValidationPipeline

class MainActivity : Activity() {
    private enum class Stage { HOME, VILLAGE, FOREST, COMBAT, RETURN_HOME }
    private val playerId = "prototype-player"
    private val mathEngine = BasicMathEngine()
    private val masteryStore = InMemoryMasteryStateStore()
    private val masterySystem = PolicyDrivenMasterySystem(masteryStore, MasteryUpdatePolicy { current, input ->
        when (input.answerResult) {
            AnswerResult.CORRECT -> current.copy(mastery = minOf(5, current.mastery + 1), attempts = current.attempts + 1, correctAttempts = current.correctAttempts + 1, consecutiveCorrect = current.consecutiveCorrect + 1, consecutiveErrors = 0)
            AnswerResult.INCORRECT -> current.copy(attempts = current.attempts + 1, incorrectAttempts = current.incorrectAttempts + 1, recentErrors = current.recentErrors + 1, consecutiveCorrect = 0, consecutiveErrors = current.consecutiveErrors + 1)
            AnswerResult.SKIPPED -> current.copy(attempts = current.attempts + 1)
        }
    })
    private val adaptivePolicy = object : AdaptivePolicy {
        override fun priority(candidate: AdaptiveCandidate): AdaptivePriority? =
            if (!candidate.requiredPrerequisitesSatisfied) AdaptivePriority.REQUIRED_PREREQUISITE
            else if (candidate.state.mastery < 4) AdaptivePriority.REINFORCE
            else AdaptivePriority.ADVANCE
        override fun mode(candidate: AdaptiveCandidate) = TaskMode.DIRECT
        override fun difficulty(candidate: AdaptiveCandidate) = 1
        override fun contextType(candidate: AdaptiveCandidate) = "BATTLE"
    }
    private val flow = CoreLearningFlow(
        adaptive = RuleBasedAdaptiveEngine(Curriculum.mvp(), adaptivePolicy),
        generator = DeterministicTaskGenerator(),
        validation = TaskValidationPipeline(StructuralTaskValidator(), LogicalTaskValidator(), MathematicalTaskValidator(mathEngine)),
        mathEngine = mathEngine,
        mastery = masterySystem
    )
    private var stage = Stage.HOME
    private var generated: com.mathadventure.core.flow.GeneratedTask? = null
    private var sessionCorrect = 0
    private var sessionIncorrect = 0
    private var lastTaskSignature = ""

    private lateinit var renderer: AdventureRenderer
    private lateinit var title: TextView
    private lateinit var message: TextView
    private lateinit var action: Button
    private lateinit var answers: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val root = FrameLayout(this)
        renderer = AdventureRenderer(this)
        root.addView(renderer)
        val hud = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(28, 20, 28, 20) }
        title = textView(24f); message = textView(17f); hud.addView(title); hud.addView(message)
        val bottom = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; gravity = Gravity.CENTER; setPadding(28, 8, 28, 22) }
        action = Button(this).apply { setOnClickListener { advance() } }
        bottom.addView(action, LinearLayout.LayoutParams(-1, 62))
        answers = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER; visibility = View.GONE }
        listOf("14", "15", "17", "18").forEach { value ->
            answers.addView(Button(this).apply { text = value; setTextColor(Color.WHITE); setOnClickListener { submit(value) } }, LinearLayout.LayoutParams(0, 62, 1f))
        }
        bottom.addView(answers)
        root.addView(hud, FrameLayout.LayoutParams(-1, -2))
        root.addView(bottom, FrameLayout.LayoutParams(-1, -2, Gravity.BOTTOM))
        setContentView(root)
        renderStage()
    }

    private fun textView(size: Float) = TextView(this).apply { textSize = size; setTextColor(Color.WHITE); setShadowLayer(6f, 2f, 2f, Color.BLACK) }

    private fun advance() {
        stage = when (stage) { Stage.HOME -> Stage.VILLAGE; Stage.VILLAGE -> Stage.FOREST; Stage.FOREST -> Stage.COMBAT; Stage.COMBAT -> Stage.COMBAT; Stage.RETURN_HOME -> Stage.HOME }
        renderStage()
    }

    private fun generateMathTask() {
        val numCompare = masterySystem.getSkillState(playerId, "NUM_COMPARE").copy(mastery = 3)
        val add = masterySystem.getSkillState(playerId, "ADD_BASIC")
        generated = flow.generateNext(playerId, listOf(numCompare, add), setOf("ADD_BASIC"), InputType.NUMERIC)
    }

    private fun submit(value: String) {
        val current = generated ?: return
        val answered = flow.answer(playerId, current, "attempt-" + System.currentTimeMillis(), value, System.currentTimeMillis())
        if (answered.evaluation.result == AnswerResult.CORRECT) {\n            sessionCorrect++
            stage = Stage.RETURN_HOME
            renderer.setVictory(true)
            title.text = "Победа!"
            message.text = "Правильно. Mastery навыка: " + answered.mastery.mastery + "/5"
            action.text = "Вернуться домой"
            answers.visibility = View.GONE
        } else {
            renderer.setVictory(false)
            message.text = "Попробуй ещё раз. Ошибка не сбрасывает прогресс."
        }
    }

    private fun renderStage() {
        renderer.setStage(stage.ordinal)
        when (stage) {
            Stage.HOME -> { title.text = "Дом героя"; message.text = "Питомец ждёт нового приключения."; action.text = "Идти в деревню"; answers.visibility = View.GONE }
            Stage.VILLAGE -> { title.text = "Деревенская площадь"; message.text = "NPC просит проверить дорогу в лес."; action.text = "Идти в лес"; answers.visibility = View.GONE }
            Stage.FOREST -> { title.text = "Лес"; message.text = "Впереди маленькое существо."; action.text = "Начать бой"; answers.visibility = View.GONE }
            Stage.COMBAT -> { generateMathTask(); title.text = "Математическая атака"; message.text = generated?.task?.prompt ?: "Математическая задача"; action.text = "Выбери ответ"; answers.visibility = View.VISIBLE }
            Stage.RETURN_HOME -> { title.text = "Возвращение"; message.text = "Игровой цикл завершён."; action.text = "Вернуться домой"; answers.visibility = View.GONE }
        }
    }
}
