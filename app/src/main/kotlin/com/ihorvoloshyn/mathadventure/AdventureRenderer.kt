package com.ihorvoloshyn.mathadventure

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.view.MotionEvent
import android.view.View
import kotlin.math.cos
import kotlin.math.sin

/** Illustrated 2.5D renderer using the generated game artwork. */
class AdventureRenderer(context: Context) : View(context) {
    private var stage = 0
    private var victory = false
    private val p = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
    private val path = Path()

    private val battleField = BitmapFactory.decodeResource(resources, R.drawable.battle_field)
    private val houseArt = BitmapFactory.decodeResource(resources, R.drawable.house)
    private val knightArt = prepareCharacter(R.drawable.knight)
    private val monsterArt = prepareCharacter(R.drawable.monster)

    init { isClickable = true }

    fun setStage(value: Int) { stage = value; invalidate() }
    fun setVictory(value: Boolean) { victory = value; invalidate() }
    fun setEquippedWeapon(visualId: String?) {}
    fun setHeroClass(value: String) {}
    override fun onTouchEvent(event: MotionEvent): Boolean = true

    override fun onDraw(c: Canvas) {
        val w = width.toFloat()
        val h = height.toFloat()
        if (w <= 0f || h <= 0f) return

        when (stage) {
            0 -> home(c, w, h)
            1 -> village(c, w, h)
            2 -> forest(c, w, h)
            3, 4 -> combat(c, w, h)
        }
        p.color = 0x10000000
        c.drawRect(0f, h * .43f, w, h, p)
    }

    private fun home(c: Canvas, w: Float, h: Float) {
        drawCover(c, houseArt, RectF(0f, 0f, w, h))
        p.color = 0x28000000
        c.drawRect(0f, h * .58f, w, h, p)
        sign(c, "ДОМ", w * .50f, h * .82f, 19f)
    }

    private fun village(c: Canvas, w: Float, h: Float) {
        sky(c, w, h, 0xFF8CCAE9.toInt(), 0xFFFFDFAB.toInt())
        ground(c, w, h, 0xFF759958.toInt())
        polyN(c, 0xFFB68A55.toInt(), .0f, .94f, 1f, .94f, .63f, .55f, .37f, .55f)
        drawBitmapFit(c, houseArt, RectF(w * .18f, h * .23f, w * .82f, h * .69f))
        tree(c, w * .07f, h * .53f, .90f)
        tree(c, w * .93f, h * .54f, .90f)
        fence(c, .04f, .70f, .25f, .81f)
        fence(c, .75f, .70f, .96f, .81f)
        sign(c, "ДЕРЕВНЯ", w * .50f, h * .84f, 18f)
    }

    private fun forest(c: Canvas, w: Float, h: Float) {
        sky(c, w, h, 0xFF547A83.toInt(), 0xFFB6C493.toInt())
        ground(c, w, h, 0xFF315B3C.toInt())
        polyN(c, 0xFFA58A5B.toInt(), .40f, 1f, .60f, 1f, .55f, .56f, .45f, .56f)
        polyN(c, 0xFFC5A96F.toInt(), .47f, 1f, .53f, 1f, .515f, .58f, .485f, .58f)
        tree(c, w * .04f, h * .38f, 1.45f)
        tree(c, w * .18f, h * .40f, 1.18f)
        tree(c, w * .32f, h * .42f, .92f)
        tree(c, w * .68f, h * .42f, 1.0f)
        tree(c, w * .83f, h * .39f, 1.25f)
        tree(c, w * .97f, h * .37f, 1.48f)
        tree(c, w * .23f, h * .59f, .70f)
        tree(c, w * .77f, h * .59f, .72f)
        bush(c, w * .13f, h * .76f)
        bush(c, w * .87f, h * .75f)
        mushroom(c, w * .25f, h * .78f)
        mushroom(c, w * .73f, h * .80f)
        sign(c, "ДРЕМУЧИЙ ЛЕС", w * .50f, h * .66f, 17f)
    }

    private fun combat(c: Canvas, w: Float, h: Float) {
        drawCover(c, battleField, RectF(0f, 0f, w, h))
        p.color = if (victory) 0x183B6B28 else 0x18000000
        c.drawRect(0f, 0f, w, h, p)

        val characterH = h * .48f
        val heroW = characterH * 0.72f
        val monsterW = characterH * 0.72f
        val groundY = h * .78f

        drawBitmapFit(c, knightArt, RectF(
            w * .08f, groundY - characterH, w * .08f + heroW, groundY
        ))
        drawBitmapFit(c, monsterArt, RectF(
            w * .92f - monsterW, groundY - characterH, w * .92f, groundY
        ))

        if (victory) {
            sparkle(c, w * .50f, h * .34f)
            sign(c, "ПОБЕДА", w * .50f, h * .39f, 18f)
        }
    }

    private fun sky(c: Canvas, w: Float, h: Float, top: Int, bottom: Int) {
        p.shader = android.graphics.LinearGradient(
            0f, 0f, 0f, h * .72f, top, bottom, android.graphics.Shader.TileMode.CLAMP
        )
        c.drawRect(0f, 0f, w, h, p)
        p.shader = null
        p.color = 0xCFFFFFF2.toInt()
        c.drawCircle(w * .80f, h * .15f, h * .055f, p)
        c.drawCircle(w * .16f, h * .16f, h * .025f, p)
        c.drawCircle(w * .20f, h * .15f, h * .04f, p)
        c.drawCircle(w * .25f, h * .17f, h * .028f, p)
        polyN(c, 0xFF78957A.toInt(), 0f, .48f, .16f, .36f, .34f, .45f, .54f, .32f, .76f, .44f, 1f, .36f)
        polyN(c, 0xFF4F6C58.toInt(), 0f, .55f, .18f, .42f, .34f, .54f, .54f, .40f, .76f, .52f, 1f, .45f)
    }

    private fun ground(c: Canvas, w: Float, h: Float, color: Int) {
        p.shader = null
        p.color = color
        c.drawRect(0f, h * .43f, w, h, p)
    }

    private fun prepareCharacter(id: Int): Bitmap {
        val src = BitmapFactory.decodeResource(resources, id)
        val out = src.copy(Bitmap.Config.ARGB_8888, true)
        val pixels = IntArray(out.width * out.height)
        out.getPixels(pixels, 0, out.width, 0, 0, out.width, out.height)
        for (i in pixels.indices) {
            val color = pixels[i]
            val r = (color shr 16) and 0xFF
            val g = (color shr 8) and 0xFF
            val b = color and 0xFF
            val nearWhite = r > 238 && g > 238 && b > 238
            if (nearWhite) pixels[i] = color and 0x00FFFFFF
        }
        out.setPixels(pixels, 0, out.width, 0, 0, out.width, out.height)
        return out
    }

    private fun drawCover(c: Canvas, bitmap: Bitmap, dst: RectF) {
        val scale = maxOf(dst.width() / bitmap.width, dst.height() / bitmap.height)
        val sw = bitmap.width * scale
        val sh = bitmap.height * scale
        val left = dst.centerX() - sw / 2f
        val top = dst.centerY() - sh / 2f
        c.drawBitmap(bitmap, null, RectF(left, top, left + sw, top + sh), p)
    }

    private fun drawBitmapFit(c: Canvas, bitmap: Bitmap, dst: RectF) {
        val scale = minOf(dst.width() / bitmap.width, dst.height() / bitmap.height)
        val sw = bitmap.width * scale
        val sh = bitmap.height * scale
        val left = dst.centerX() - sw / 2f
        val top = dst.bottom - sh
        c.drawBitmap(bitmap, null, RectF(left, top, left + sw, top + sh), p)
    }

    private fun tree(c: Canvas, x: Float, y: Float, s: Float) {
        p.color = 0xFF5B3F2A.toInt()
        c.drawRoundRect(x - 8*s, y + 28*s, x + 8*s, y + 120*s, 6*s, 6*s, p)
        p.color = 0xFF214A31.toInt()
        c.drawCircle(x, y + 25*s, 48*s, p)
        p.color = 0xFF2F633A.toInt()
        c.drawCircle(x - 28*s, y + 40*s, 34*s, p)
        c.drawCircle(x + 28*s, y + 42*s, 35*s, p)
        p.color = 0xFF43804A.toInt()
        c.drawCircle(x, y - 4*s, 27*s, p)
    }

    private fun bush(c: Canvas, x: Float, y: Float) {
        p.color = 0xFF376C3D.toInt()
        c.drawCircle(x, y, 28f, p)
        c.drawCircle(x + 22, y + 3, 22f, p)
        c.drawCircle(x - 21, y + 5, 20f, p)
    }

    private fun fence(c: Canvas, x1: Float, y1: Float, x2: Float, y2: Float) {
        val yy1 = y1 * height
        val yy2 = y2 * height
        p.color = 0xFF8A633F.toInt()
        var x = x1 * width
        while (x <= x2 * width) {
            c.drawRect(x, yy1 - 22, x + 7, yy2 + 5, p)
            x += 34
        }
        p.color = 0xFF9D754B.toInt()
        c.drawRect(x1*width, yy1-9, x2*width, yy1-3, p)
        c.drawRect(x1*width, yy1+9, x2*width, yy1+15, p)
    }

    private fun mushroom(c: Canvas, x: Float, y: Float) {
        p.color = 0xFFE4D1A5.toInt()
        c.drawRect(x-4, y, x+4, y+18, p)
        p.color = 0xFFC75B4B.toInt()
        c.drawOval(x-13, y-10, x+13, y+6, p)
    }

    private fun sparkle(c: Canvas, x: Float, y: Float) {
        p.color = 0xFFFFE28A.toInt()
        for (i in 0 until 8) {
            val a = i * .78f
            c.drawCircle(x + cos(a) * wScale(78f), y + sin(a) * 28f, 4f, p)
        }
    }

    private fun wScale(v: Float) = v * width / 1080f

    private fun sign(c: Canvas, text: String, x: Float, y: Float, size: Float) {
        p.color = 0xFFE0B66E.toInt()
        c.drawRoundRect(x-150, y-25, x+150, y+25, 13f, 13f, p)
        p.color = 0xFF3D2B20.toInt()
        p.textAlign = Paint.Align.CENTER
        p.textSize = size
        p.isFakeBoldText = true
        c.drawText(text, x, y+6, p)
        p.isFakeBoldText = false
    }

    private fun stone(c: Canvas, x: Float, y: Float, r: Float) {
        p.color = 0xFF817C70.toInt()
        c.drawOval(x-r, y-r*.55f, x+r, y+r*.55f, p)
    }

    private fun polyN(c: Canvas, color: Int, vararg q: Float) {
        p.color = color
        path.reset()
        path.moveTo(q[0]*width, q[1]*height)
        var i = 2
        while (i < q.size) {
            path.lineTo(q[i]*width, q[i+1]*height)
            i += 2
        }
        path.close()
        c.drawPath(path, p)
    }
}
