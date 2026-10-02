package com.ihorvoloshyn.mathadventure

import android.content.Context
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Shader
import android.view.MotionEvent
import android.view.View

/**
 * Fixed 2.5D world renderer.
 * No OpenGL, meshes, 3D characters, pets, monsters or free camera rotation.
 */
class AdventureRenderer(context: Context) : View(context) {
    private var stage = 0
    private var victory = false
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val path = Path()

    init {
        isClickable = true
        setBackgroundColor(0x101722)
    }

    fun setStage(stage: Int) {
        this.stage = stage
        invalidate()
    }

    fun setVictory(value: Boolean) {
        victory = value
        invalidate()
    }

    fun setEquippedWeapon(visualId: String?) {
        // API compatibility only. No 3D equipment is rendered.
        invalidate()
    }

    fun setHeroClass(value: String) {
        // API compatibility only. No hero model is rendered.
        invalidate()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean = true

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val w = width.toFloat()
        val h = height.toFloat()
        if (w <= 0f || h <= 0f) return

        drawSky(canvas, w, h)
        drawGround(canvas, w, h)

        when (stage) {
            0, 4 -> drawHome(canvas, w, h)
            1 -> drawVillage(canvas, w, h)
            2 -> drawForest(canvas, w, h)
            3 -> drawClearing(canvas, w, h)
            else -> drawHome(canvas, w, h)
        }
    }

    private fun drawSky(canvas: Canvas, w: Float, h: Float) {
        val colors = when (stage) {
            0, 4 -> intArrayOf(0xFF88B9D8.toInt(), 0xFFE7CFA6.toInt())
            1 -> intArrayOf(0xFF8CC7E8.toInt(), 0xFFE9D7A9.toInt())
            2 -> intArrayOf(0xFF557C72.toInt(), 0xFF173B32.toInt())
            3 -> intArrayOf(0xFF789BC0.toInt(), 0xFF26364B.toInt())
            else -> intArrayOf(0xFF88AFC7.toInt(), 0xFF27384A.toInt())
        }
        paint.shader = LinearGradient(0f, 0f, 0f, h * 0.72f, colors[0], colors[1], Shader.TileMode.CLAMP)
        canvas.drawRect(0f, 0f, w, h, paint)
        paint.shader = null
    }

    private fun drawGround(canvas: Canvas, w: Float, h: Float) {
        val horizon = h * 0.43f
        paint.color = when (stage) {
            0, 4 -> 0xFF8FAE69.toInt()
            1 -> 0xFF7FA45F.toInt()
            2 -> 0xFF315A3A.toInt()
            3 -> 0xFF6B7C50.toInt()
            else -> 0xFF66805B.toInt()
        }
        canvas.drawRect(0f, horizon, w, h, paint)
        for (i in 0..5) {
            val y = horizon + (h - horizon) * (i + 1) / 7f
            paint.color = if (i % 2 == 0) 0x18000000 else 0x12000000
            canvas.drawRect(0f, y, w, y + 18f + i * 3f, paint)
        }
    }

    private fun drawHome(canvas: Canvas, w: Float, h: Float) {
        drawPath(canvas, 0xFFB98A57.toInt(),
            0.12f to 0.73f, 0.88f to 0.73f, 0.78f to 0.92f, 0.20f to 0.92f)
        drawRect(canvas, 0xFFB96F43.toInt(), w * 0.25f, h * 0.40f, w * 0.70f, h * 0.69f)
        drawPath(canvas, 0xFF7D4735.toInt(),
            0.21f to 0.40f, 0.50f to 0.22f, 0.76f to 0.40f, 0.50f to 0.54f)
        drawRect(canvas, 0xFF704638.toInt(), w * 0.46f, h * 0.53f, w * 0.57f, h * 0.69f)
        drawRect(canvas, 0xFF7CB0B5.toInt(), w * 0.30f, h * 0.49f, w * 0.39f, h * 0.59f)
        drawRect(canvas, 0xFF7CB0B5.toInt(), w * 0.61f, h * 0.49f, w * 0.70f, h * 0.59f)
        drawLabel(canvas, "ДОМ", w * 0.50f, h * 0.79f, 22f)
    }

    private fun drawVillage(canvas: Canvas, w: Float, h: Float) {
        drawPath(canvas, 0xFFB98B58.toInt(),
            0.04f to 0.88f, 0.96f to 0.88f, 0.64f to 0.55f, 0.36f to 0.55f)
        drawHouse(canvas, w * 0.20f, h * 0.43f, 0.70f)
        drawHouse(canvas, w * 0.70f, h * 0.48f, 0.56f)
        drawTree(canvas, w * 0.10f, h * 0.58f, 0.95f)
        drawTree(canvas, w * 0.86f, h * 0.59f, 0.85f)
        drawTree(canvas, w * 0.53f, h * 0.47f, 0.58f)
        drawPath(canvas, 0xFF6D5235.toInt(),
            0.46f to 0.88f, 0.54f to 0.88f, 0.57f to 0.55f, 0.43f to 0.55f)
        drawLabel(canvas, "ДЕРЕВНЯ", w * 0.50f, h * 0.79f, 22f)
    }

    private fun drawForest(canvas: Canvas, w: Float, h: Float) {
        drawTree(canvas, w * 0.10f, h * 0.47f, 1.00f)
        drawTree(canvas, w * 0.30f, h * 0.45f, 0.78f)
        drawTree(canvas, w * 0.72f, h * 0.45f, 0.82f)
        drawTree(canvas, w * 0.90f, h * 0.48f, 1.02f)
        drawTree(canvas, w * 0.21f, h * 0.61f, 0.66f)
        drawTree(canvas, w * 0.79f, h * 0.62f, 0.62f)
        drawTree(canvas, w * 0.48f, h * 0.54f, 0.52f)
        drawPath(canvas, 0xFF9B7A4A.toInt(),
            0.43f to 1.00f, 0.57f to 1.00f, 0.54f to 0.47f, 0.46f to 0.47f)
        drawPath(canvas, 0xFFB69660.toInt(),
            0.47f to 0.95f, 0.53f to 0.95f, 0.515f to 0.49f, 0.485f to 0.49f)
        drawSign(canvas, "ПОЛЯНА →", w * 0.76f, h * 0.64f)
        drawLabel(canvas, "ЛЕС", w * 0.50f, h * 0.80f, 22f)
    }

    private fun drawClearing(canvas: Canvas, w: Float, h: Float) {
        drawPath(canvas, 0xFFB69A69.toInt(),
            0.12f to 0.93f, 0.88f to 0.93f, 0.68f to 0.56f, 0.32f to 0.56f)
        drawTree(canvas, w * 0.10f, h * 0.47f, 0.92f)
        drawTree(canvas, w * 0.90f, h * 0.48f, 0.88f)
        drawTree(canvas, w * 0.22f, h * 0.58f, 0.55f)
        drawTree(canvas, w * 0.78f, h * 0.58f, 0.55f)
        paint.color = if (victory) 0xFFB8D68B.toInt() else 0xFFD2B26C.toInt()
        canvas.drawOval(w * 0.31f, h * 0.55f, w * 0.69f, h * 0.79f, paint)
        drawPath(canvas, 0xFF8B6A44.toInt(),
            0.47f to 0.93f, 0.53f to 0.93f, 0.54f to 0.70f, 0.46f to 0.70f)
        drawSign(canvas, if (victory) "ПОЛЯНА • ПОБЕДА" else "ПОЛЯНА • БОЙ", w * 0.50f, h * 0.42f)
    }

    private fun drawHouse(canvas: Canvas, cx: Float, cy: Float, scale: Float) {
        val width = 150f * scale
        val height = 92f * scale
        drawRect(canvas, 0xFFB96F43.toInt(), cx - width / 2, cy, cx + width / 2, cy + height)
        drawPath(canvas, 0xFF7D4735.toInt(),
            (cx - width * 0.58f / 150f) to cy,
            cx to (cy - 52f * scale),
            (cx + width * 0.58f / 150f) to cy,
            cx to (cy + 38f * scale))
        drawRect(canvas, 0xFF704638.toInt(), cx - 13f * scale, cy + 40f * scale, cx + 13f * scale, cy + height)
    }

    private fun drawTree(canvas: Canvas, x: Float, y: Float, scale: Float) {
        paint.color = 0xFF68452F.toInt()
        canvas.drawRect(x - 9f * scale, y + 38f * scale, x + 9f * scale, y + 115f * scale, paint)
        paint.color = 0xFF315D3A.toInt()
        canvas.drawCircle(x, y + 26f * scale, 48f * scale, paint)
        paint.color = 0xFF40744A.toInt()
        canvas.drawCircle(x - 28f * scale, y + 42f * scale, 34f * scale, paint)
        canvas.drawCircle(x + 28f * scale, y + 43f * scale, 34f * scale, paint)
        paint.color = 0xFF4D8650.toInt()
        canvas.drawCircle(x, y - 2f * scale, 27f * scale, paint)
    }

    private fun drawSign(canvas: Canvas, text: String, x: Float, y: Float) {
        paint.color = 0xFFD4A45E.toInt()
        canvas.drawRoundRect(x - 92f, y - 22f, x + 92f, y + 22f, 12f, 12f, paint)
        drawLabel(canvas, text, x, y + 7f, 15f, 0xFF34281E.toInt())
    }

    private fun drawLabel(canvas: Canvas, text: String, x: Float, y: Float, size: Float, color: Int = 0xFFFFFFFF.toInt()) {
        paint.shader = null
        paint.color = color
        paint.textSize = size
        paint.textAlign = Paint.Align.CENTER
        paint.isFakeBoldText = true
        canvas.drawText(text, x, y, paint)
        paint.isFakeBoldText = false
    }

    private fun drawRect(canvas: Canvas, color: Int, left: Float, top: Float, right: Float, bottom: Float) {
        paint.shader = null
        paint.color = color
        canvas.drawRect(left, top, right, bottom, paint)
    }

    private fun drawPath(canvas: Canvas, color: Int, vararg points: Pair<Float, Float>) {
        if (points.isEmpty()) return
        paint.shader = null
        paint.color = color
        path.reset()
        path.moveTo(points[0].first * width, points[0].second * height)
        for (i in 1 until points.size) {
            path.lineTo(points[i].first * width, points[i].second * height)
        }
        path.close()
        canvas.drawPath(path, paint)
    }
}
