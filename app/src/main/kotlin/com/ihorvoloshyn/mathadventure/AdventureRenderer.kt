package com.ihorvoloshyn.mathadventure

import android.content.Context
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Shader
import android.view.MotionEvent
import android.view.View
import kotlin.math.cos
import kotlin.math.sin

/** Fixed illustrated 2.5D locations. No 3D models and no rotating camera. */
class AdventureRenderer(context: Context) : View(context) {
    private var stage = 0
    private var victory = false
    private val p = Paint(Paint.ANTI_ALIAS_FLAG)
    private val path = Path()

    init { isClickable = true }
    fun setStage(value: Int) { stage = value; invalidate() }
    fun setVictory(value: Boolean) { victory = value; invalidate() }
    fun setEquippedWeapon(visualId: String?) {}
    fun setHeroClass(value: String) {}
    override fun onTouchEvent(event: MotionEvent): Boolean = true

    override fun onDraw(c: Canvas) {
        val w = width.toFloat(); val h = height.toFloat()
        if (w <= 0f || h <= 0f) return
        sky(c,w,h)
        when (stage) {
            0,4 -> home(c,w,h)
            1 -> village(c,w,h)
            2 -> forest(c,w,h)
            3 -> clearing(c,w,h)
        }
        p.color = 0x10000000
        c.drawRect(0f,h*.43f,w,h,p)
    }

    private fun sky(c:Canvas,w:Float,h:Float) {
        val top = when(stage){2->0xFF547A83.toInt();3->0xFF7896B0.toInt();else->0xFF8CCAE9.toInt()}
        val bottom = when(stage){2->0xFFB6C493.toInt();3->0xFFE5CA99.toInt();else->0xFFFFDFAB.toInt()}
        p.shader=LinearGradient(0f,0f,0f,h*.72f,top,bottom,Shader.TileMode.CLAMP)
        c.drawRect(0f,0f,w,h,p); p.shader=null
        // clouds / sun
        p.color=0xCFFFFFF2.toInt(); c.drawCircle(w*.80f,h*.15f,h*.055f,p)
        if(stage!=2){ c.drawCircle(w*.16f,h*.16f,h*.025f,p); c.drawCircle(w*.20f,h*.15f,h*.04f,p); c.drawCircle(w*.25f,h*.17f,h*.028f,p) }
        polyN(c,0xFF78957A.toInt(),0f,.48f,.16f,.36f,.34f,.45f,.54f,.32f,.76f,.44f,1f,.36f)
        polyN(c,0xFF4F6C58.toInt(),0f,.55f,.18f,.42f,.34f,.54f,.54f,.40f,.76f,.52f,1f,.45f)
    }

    private fun home(c:Canvas,w:Float,h:Float) {
        ground(c,w,h,0xFF7B9D5C)
        polyN(c,0xFFD2B47A, .42f,1f,.58f,1f,.55f,.62f,.45f,.62f)
        fence(c,.08f,.70f,.92f,.82f)
        house(c,w*.50f,h*.45f,1.15f)
        tree(c,w*.10f,h*.53f,1.15f); tree(c,w*.90f,h*.54f,1.0f)
        bush(c,w*.22f,h*.72f); bush(c,w*.78f,h*.72f)
        sign(c,"ДОМ",w*.50f,h*.79f,19f)
    }

    private fun village(c:Canvas,w:Float,h:Float) {
        ground(c,w,h,0xFF759958)
        polyN(c,0xFFB68A55,.0f,.94f,1f,.94f,.63f,.55f,.37f,.55f)
        for(i in 0..6){p.color=0x24FFFFFF; val y=h*(.60f+i*.047f); c.drawRect(w*.31f-i*7,y,w*.69f+i*7,y+2,p)}
        house(c,w*.23f,h*.47f,.75f); house(c,w*.76f,h*.49f,.67f)
        tree(c,w*.07f,h*.53f,.95f); tree(c,w*.93f,h*.54f,.95f); tree(c,w*.50f,h*.40f,.52f)
        well(c,w*.51f,h*.69f)
        fence(c,.05f,.69f,.28f,.79f); fence(c,.72f,.71f,.95f,.80f)
        sign(c,"ДЕРЕВНЯ",w*.50f,h*.83f,18f)
    }

    private fun forest(c:Canvas,w:Float,h:Float) {
        ground(c,w,h,0xFF315B3C)
        polyN(c,0xFFA58A5B,.40f,1f,.60f,1f,.55f,.56f,.45f,.56f)
        polyN(c,0xFFC5A96F,.47f,1f,.53f,1f,.515f,.58f,.485f,.58f)
        tree(c,w*.04f,h*.38f,1.45f); tree(c,w*.18f,h*.40f,1.18f); tree(c,w*.32f,h*.42f,.92f)
        tree(c,w*.68f,h*.42f,1.0f); tree(c,w*.83f,h*.39f,1.25f); tree(c,w*.97f,h*.37f,1.48f)
        tree(c,w*.23f,h*.59f,.70f); tree(c,w*.77f,h*.59f,.72f); tree(c,w*.50f,h*.50f,.60f)
        bush(c,w*.13f,h*.76f); bush(c,w*.87f,h*.75f)
        mushroom(c,w*.25f,h*.78f); mushroom(c,w*.73f,h*.80f)
        sign(c,"ДРЕМУЧИЙ ЛЕС",w*.50f,h*.66f,17f)
    }

    private fun clearing(c:Canvas,w:Float,h:Float) {
        ground(c,w,h,0xFF667F4F)
        // A clearly defined circular encounter arena.
        p.color=if(victory)0xFFB8D68A.toInt() else 0xFFD0B373.toInt()
        c.drawOval(w*.24f,h*.55f,w*.76f,h*.92f,p)
        p.color=0xFF8A744F.toInt(); c.drawOval(w*.31f,h*.63f,w*.69f,h*.84f,p)
        p.color=0xFFB8A474.toInt(); c.drawOval(w*.34f,h*.65f,w*.66f,h*.82f,p)
        for(i in 0 until 9) stone(c,w*(.31f+i*.047f),h*(.72f+(i%2)*.075f),12f+(i%3)*3f)
        tree(c,w*.08f,h*.43f,1.12f); tree(c,w*.92f,h*.44f,1.10f)
        tree(c,w*.18f,h*.58f,.60f); tree(c,w*.82f,h*.59f,.60f)
        torch(c,w*.30f,h*.67f); torch(c,w*.70f,h*.67f)
        if(victory){ sparkle(c,w*.50f,h*.55f); sign(c,"ПОЛЯНА • ПОБЕДА",w*.50f,h*.47f,16f) }
        else sign(c,"ПОЛЯНА МОНСТРА",w*.50f,h*.47f,17f)
    }

    private fun ground(c:Canvas,w:Float,h:Float,color:Int){p.shader=null;p.color=color;c.drawRect(0f,h*.43f,w,h,p)}
    private fun house(c:Canvas,cx:Float,cy:Float,s:Float){
        val bw=150f*s; val bh=95f*s
        p.color=0xFFE1A060.toInt(); c.drawRect(cx-bw/2,cy,cx+bw/2,cy+bh,p)
        polyAbs(c,0xFF754638,cx-bw*.58f,cy,cx,cy-65*s,cx+bw*.58f,cy,cx,cy+34*s)
        p.color=0xFFB9D4D5.toInt()
        c.drawRect(cx-bw*.31f,cy+29*s,cx-bw*.15f,cy+57*s,p)
        c.drawRect(cx+bw*.15f,cy+29*s,cx+bw*.31f,cy+57*s,p)
        p.color=0xFF694333.toInt(); c.drawRect(cx-13*s,cy+48*s,cx+13*s,cy+bh,p)
        p.color=0xFFFFD77B.toInt(); c.drawCircle(cx-bw*.23f,cy+43*s,3*s,p); c.drawCircle(cx+bw*.23f,cy+43*s,3*s,p)
        p.color=0xFF8B5538.toInt(); c.drawRect(cx-bw*.52f,cy-7*s,cx+bw*.52f,cy,p)
    }
    private fun tree(c:Canvas,x:Float,y:Float,s:Float){
        p.color=0xFF5B3F2A.toInt(); c.drawRoundRect(x-8*s,y+28*s,x+8*s,y+120*s,6*s,6*s,p)
        p.color=0xFF214A31.toInt(); c.drawCircle(x,y+25*s,48*s,p)
        p.color=0xFF2F633A.toInt(); c.drawCircle(x-28*s,y+40*s,34*s,p); c.drawCircle(x+28*s,y+42*s,35*s,p)
        p.color=0xFF43804A.toInt(); c.drawCircle(x,y-4*s,27*s,p)
    }
    private fun bush(c:Canvas,x:Float,y:Float){p.color=0xFF376C3D.toInt();c.drawCircle(x,y,28f,p);c.drawCircle(x+22,y+3,22f,p);c.drawCircle(x-21,y+5,20f,p)}
    private fun fence(c:Canvas,x1:Float,y1:Float,x2:Float,y2:Float){
        val yy1=y1*height; val yy2=y2*height
        p.color=0xFF8A633F.toInt(); var x=x1*width
        while(x<=x2*width){c.drawRect(x,yy1-22,x+7,yy2+5,p);x+=34}
        p.color=0xFF9D754B.toInt();c.drawRect(x1*width,yy1-9,x2*width,yy1-3,p);c.drawRect(x1*width,yy1+9,x2*width,yy1+15,p)
    }
    private fun well(c:Canvas,x:Float,y:Float){p.color=0xFF77736A.toInt();c.drawOval(x-38,y-16,x+38,y+18,p);p.color=0xFF4E4A45.toInt();c.drawOval(x-25,y-8,x+25,y+9,p);p.color=0xFF754D31.toInt();c.drawRect(x-4,y-65,x+4,y-15,p);c.drawRect(x-48,y-63,x+48,y-57,p)}
    private fun mushroom(c:Canvas,x:Float,y:Float){p.color=0xFFE4D1A5.toInt();c.drawRect(x-4,y,x+4,y+18,p);p.color=0xFFC75B4B.toInt();c.drawOval(x-13,y-10,x+13,y+6,p)}
    private fun stone(c:Canvas,x:Float,y:Float,r:Float){p.color=0xFF817C70.toInt();c.drawOval(x-r,y-r*.55f,x+r,y+r*.55f,p)}
    private fun torch(c:Canvas,x:Float,y:Float){p.color=0xFF67452C.toInt();c.drawRect(x-4,y,x+4,y+42,p);p.color=0xFFFFA23A.toInt();c.drawCircle(x,y-5,12,p);p.color=0xFFFFE58A.toInt();c.drawCircle(x,y-6,6,p)}
    private fun sparkle(c:Canvas,x:Float,y:Float){p.color=0xFFFFE28A.toInt();for(i in 0 until 8){val a=i*.78f;c.drawCircle(x+cos(a)*wScale(78f),y+sin(a)*28f,4f,p)}}
    private fun wScale(v:Float)=v*width/1080f
    private fun sign(c:Canvas,text:String,x:Float,y:Float,size:Float){p.color=0xFFE0B66E.toInt();c.drawRoundRect(x-150,y-25,x+150,y+25,13f,13f,p);p.color=0xFF3D2B20.toInt();p.textAlign=Paint.Align.CENTER;p.textSize=size;p.isFakeBoldText=true;c.drawText(text,x,y+6,p);p.isFakeBoldText=false}
    private fun polyN(c:Canvas,color:Int,vararg q:Float){p.color=color;path.reset();path.moveTo(q[0]*width,q[1]*height);var i=2;while(i<q.size){path.lineTo(q[i]*width,q[i+1]*height);i+=2};path.close();c.drawPath(path,p)}
    private fun polyAbs(c:Canvas,color:Int,vararg q:Float){p.color=color;path.reset();path.moveTo(q[0],q[1]);var i=2;while(i<q.size){path.lineTo(q[i],q[i+1]);i+=2};path.close();c.drawPath(path,p)}
}
