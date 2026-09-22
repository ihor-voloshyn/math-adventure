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

class MainActivity : Activity() {
    private lateinit var renderer: AdventureRenderer
    private lateinit var title: TextView
    private lateinit var message: TextView
    private lateinit var action: Button
    private lateinit var answers: LinearLayout

    private enum class Stage { HOME, VILLAGE, FOREST, COMBAT, RETURN_HOME }

    private var stage = Stage.HOME

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = FrameLayout(this)
        renderer = AdventureRenderer(this)
        root.addView(renderer)

        val hud = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.TOP
            setPadding(28, 20, 28, 20)
        }

        title = TextView(this).apply {
            setTextColor(Color.WHITE)
            textSize = 24f
            setShadowLayer(6f, 2f, 2f, Color.BLACK)
        }
        message = TextView(this).apply {
            setTextColor(Color.WHITE)
            textSize = 17f
            setShadowLayer(5f, 2f, 2f, Color.BLACK)
        }
        hud.addView(title)
        hud.addView(message)

        val bottom = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(28, 8, 28, 22)
        }

        action = Button(this).apply {
            setTextColor(Color.WHITE)
            setOnClickListener { advance() }
        }
        bottom.addView(action, LinearLayout.LayoutParams(-1, 62))

        answers = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            visibility = View.GONE
        }
        listOf(14, 15, 16, 17).forEach { value ->
            val b = Button(this).apply {
                text = value.toString()
                setTextColor(Color.WHITE)
                setOnClickListener { answer(value) }
            }
            answers.addView(b, LinearLayout.LayoutParams(0, 62, 1f))
        }
        bottom.addView(answers)

        root.addView(hud, FrameLayout.LayoutParams(-1, -2))
        root.addView(bottom, FrameLayout.LayoutParams(-1, -2, Gravity.BOTTOM))

        setContentView(root)
        renderStage()
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

    private fun answer(value: Int) {
        if (value == 15) {
            stage = Stage.RETURN_HOME
            renderer.setVictory(true)
            title.text = "Победа!"
            message.text = "15 — правильный ответ. Враг отступил. Можно возвращаться домой."
            action.text = "Вернуться домой"
            answers.visibility = View.GONE
        } else {
            renderer.setVictory(false)
            message.text = "Попробуй ещё раз. Подумай о 7 + 8."
        }
    }

    private fun renderStage() {
        renderer.setStage(stage.ordinal)
        when (stage) {
            Stage.HOME -> {
                title.text = "Дом героя"
                message.text = "Твой маленький дом и питомец ждут приключения."
                action.text = "Идти в деревню"
                answers.visibility = View.GONE
            }
            Stage.VILLAGE -> {
                title.text = "Деревенская площадь"
                message.text = "NPC просит проверить дорогу в лес."
                action.text = "Идти в лес"
                answers.visibility = View.GONE
            }
            Stage.FOREST -> {
                title.text = "Лес"
                message.text = "Впереди маленькое существо. Приготовься!"
                action.text = "Начать бой"
                answers.visibility = View.GONE
            }
            Stage.COMBAT -> {
                title.text = "Математическая атака"
                message.text = "Реши: 7 + 8 = ?"
                action.text = "Выбери ответ"
                answers.visibility = View.VISIBLE
            }
            Stage.RETURN_HOME -> {
                title.text = "Возвращение"
                message.text = "Первый короткий игровой цикл завершён."
                action.text = "Вернуться домой"
                answers.visibility = View.GONE
            }
        }
    }
}
