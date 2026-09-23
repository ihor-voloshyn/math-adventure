package com.ihorvoloshyn.mathadventure

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.view.MotionEvent
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class AdventureRenderer(context: Context) : GLSurfaceView(context) {
    private val scene = SceneRenderer()

    init {
        setEGLContextClientVersion(2)
        setRenderer(scene)
        renderMode = RENDERMODE_CONTINUOUSLY
    }

    fun setStage(stage: Int) { scene.stage = stage }
    fun setVictory(value: Boolean) { scene.victory = value }
    fun setEquippedWeapon(visualId: String?) { scene.equippedWeaponVisualId = visualId }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return scene.handleTouch(event)
    }

    private class SceneRenderer : Renderer {
        var stage = 0
        var victory = false
        var equippedWeaponVisualId: String? = null
        private var program = 0
        private val projection = FloatArray(16)
        private val view = FloatArray(16)
        private val model = FloatArray(16)
        private val mvp = FloatArray(16)
        private var angle = 0f
        private var cameraYaw = 0f
        private var cameraPitch = 0.62f
        private var lastTouchX = 0f
        private var lastTouchY = 0f
        private lateinit var cube: Cube
        private lateinit var sphere: Mesh
        private lateinit var cylinder: Mesh
        private lateinit var cone: Mesh
        private lateinit var roof: Mesh

        override fun onSurfaceCreated(
            gl: javax.microedition.khronos.opengles.GL10?,
            config: javax.microedition.khronos.egl.EGLConfig?
        ) {
            GLES20.glClearColor(0.38f, 0.62f, 0.86f, 1f)
            program = Shader.create()
            cube = Cube()
            sphere = Mesh.sphere()
            cylinder = Mesh.cylinder()
            cone = Mesh.cone()
            roof = Mesh.roof()
            GLES20.glEnable(GLES20.GL_DEPTH_TEST)
        }

        override fun onSurfaceChanged(
            gl: javax.microedition.khronos.opengles.GL10?,
            width: Int,
            height: Int
        ) {
            GLES20.glViewport(0, 0, width, height)
            val ratio = width.toFloat() / height.coerceAtLeast(1)
            Matrix.frustumM(projection, 0, -ratio, ratio, -1f, 1f, 2f, 40f)
        }

        override fun onDrawFrame(gl: javax.microedition.khronos.opengles.GL10?) {
            angle += 0.15f
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

            val horizontalDistance = 12f
            val cameraX = sin(cameraYaw) * horizontalDistance
            val cameraZ = cos(cameraYaw) * horizontalDistance
            val cameraY = 5.5f + cameraPitch * 2.5f

            Matrix.setLookAtM(
                view, 0,
                cameraX, cameraY, cameraZ,
                0f, 0.8f, 0f,
                0f, 1f, 0f
            )
            drawWorld()
        }

        fun handleTouch(event: MotionEvent): Boolean {
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    lastTouchX = event.x
                    lastTouchY = event.y
                    return true
                }

                MotionEvent.ACTION_MOVE -> {
                    val dx = event.x - lastTouchX
                    val dy = event.y - lastTouchY
                    lastTouchX = event.x
                    lastTouchY = event.y

                    cameraYaw = (cameraYaw - dx * 0.008f) % (2f * Math.PI.toFloat())
                    cameraPitch = (cameraPitch + dy * 0.006f).coerceIn(0.15f, 1.25f)
                    return true
                }
            }
            return true
        }

        private fun drawWorld() {
            cube(0f, -0.25f, 0f, 14f, 0.35f, 14f, 0.22f, 0.58f, 0.28f)
            when (stage) {
                0, 4 -> drawHome()
                1 -> drawVillage()
                2 -> drawForest()
                3 -> drawForest(true)
            }
            drawHero(-1.4f, 0f, 1.2f)
            drawPet(0.0f, 0f, 1.6f)
            if (stage == 1) drawNpc(1.8f, 0f, -1.2f)
            if (stage == 2 || stage == 3) drawEnemy(2.2f, 0f, -1.0f)
        }

        private fun drawHome() {
            cube(-2.5f, 1.0f, -1.8f, 3.5f, 2.0f, 2.7f, 0.72f, 0.45f, 0.28f)
            roof(-2.5f, 2.45f, -1.8f, 3.95f, 1.35f, 3.1f, 0.55f, 0.12f, 0.10f)
            cube(-2.5f, 0.75f, -0.35f, 0.75f, 1.35f, 0.16f, 0.18f, 0.09f, 0.05f)
            cube(-3.25f, 0.18f, -0.15f, 0.8f, 0.18f, 1.0f, 0.55f, 0.36f, 0.18f)
            sphere(-3.25f, 0.52f, -0.15f, 0.38f, 0.28f, 0.12f, 0.05f)
        }

        private fun drawVillage() {
            cube(2.6f, 0.45f, -2.0f, 2.7f, 0.9f, 2.4f, 0.72f, 0.50f, 0.34f)
            roof(2.6f, 1.25f, -2.0f, 3.1f, 0.9f, 2.8f, 0.48f, 0.18f, 0.12f)
            tree(-3.0f, 0f, -2.8f)
            tree(3.8f, 0f, 2.3f)
            tree(0.0f, 0f, -4.0f)
        }

        private fun drawForest(combat: Boolean = false) {
            tree(-3.2f, 0f, -2.0f)
            tree(-1.0f, 0f, -3.2f)
            tree(3.8f, 0f, 1.7f)
            tree(2.8f, 0f, -3.0f)
            if (combat) cube(0.8f, 0.12f, -1.2f, 2.8f, 0.08f, 2.0f, 0.35f, 0.52f, 0.28f)
        }

        private fun drawHero(x: Float, y: Float, z: Float) {
            cylinder(x, y + 0.85f, z, 0.58f, 1.45f, 0.22f, 0.42f, 0.78f)
            sphere(x, y + 1.9f, z, 0.52f, 0.95f, 0.78f, 0.58f)
            cylinder(x - 0.28f, y + 0.25f, z, 0.18f, 0.65f, 0.12f, 0.15f, 0.16f, 0.25f)
            cylinder(x + 0.28f, y + 0.25f, z, 0.18f, 0.65f, 0.12f, 0.15f, 0.16f, 0.25f)
            sphere(x - 0.18f, y + 2.02f, z - 0.45f, 0.10f, 0.10f, 0.10f, 0.05f, 0.05f)
            if (equippedWeaponVisualId == "weapon_sword_sparks") {
                cylinder(x + 0.68f, y + 1.15f, z, 0.10f, 1.25f, 0.08f, 0.78f, 0.80f, 0.86f)
                cube(x + 0.68f, y + 0.53f, z, 0.38f, 0.09f, 0.12f, 0.32f, 0.18f, 0.08f)
            }
        }

        private fun drawPet(x: Float, y: Float, z: Float) {
            val bob = sin(angle * 0.04f) * 0.08f
            sphere(x, y + 0.40f + bob, z, 0.58f, 0.52f, 0.20f, 0.62f, 0.18f)
            sphere(x, y + 0.82f + bob, z, 0.45f, 0.76f, 0.45f, 0.82f, 0.55f, 0.22f)
            cone(x - 0.20f, y + 1.10f + bob, z, 0.18f, 0.38f, 0.82f, 0.55f, 0.22f)
            cone(x + 0.20f, y + 1.10f + bob, z, 0.18f, 0.38f, 0.82f, 0.55f, 0.22f)
            sphere(x - 0.15f, y + 0.84f + bob, z - 0.40f, 0.06f, 0.06f, 0.06f, 0.05f, 0.04f)
            sphere(x + 0.15f, y + 0.84f + bob, z - 0.40f, 0.06f, 0.06f, 0.06f, 0.05f, 0.04f)
        }

        private fun drawNpc(x: Float, y: Float, z: Float) {
            cylinder(x, y + 0.85f, z, 0.58f, 1.45f, 0.32f, 0.62f, 0.38f)
            sphere(x, y + 1.9f, z, 0.50f, 0.92f, 0.72f, 0.58f)
            cone(x, y + 2.35f, z, 0.58f, 0.55f, 0.12f, 0.22f, 0.38f)
        }

        private fun drawEnemy(x: Float, y: Float, z: Float) {
            val bounce = if (victory) -0.35f else 0f
            sphere(x, y + 0.78f + bounce, z, 0.78f, 0.66f, 0.62f, 0.62f, 0.24f)
            sphere(x, y + 1.58f + bounce, z, 0.58f, 0.44f, 0.52f, 0.55f, 0.20f)
            cone(x - 0.38f, y + 2.05f + bounce, z, 0.20f, 0.58f, 0.55f, 0.20f, 0.12f)
            cone(x + 0.38f, y + 2.05f + bounce, z, 0.20f, 0.58f, 0.55f, 0.20f, 0.12f)
        }

        private fun tree(x: Float, y: Float, z: Float) {
            cylinder(x, y + 0.9f, z, 0.22f, 1.8f, 0.42f, 0.24f, 0.10f)
            cone(x, y + 2.05f, z, 0.95f, 1.65f, 0.12f, 0.48f, 0.18f)
            cone(x, y + 2.75f, z, 0.72f, 1.35f, 0.15f, 0.58f, 0.22f)
        }

        private fun sphere(x: Float, y: Float, z: Float, sx: Float, sy: Float, sz: Float, r: Float, g: Float, b: Float) =
            drawMesh(sphere, x, y, z, sx, sy, sz, r, g, b)

        private fun cylinder(x: Float, y: Float, z: Float, radius: Float, height: Float, r: Float, g: Float, b: Float) =
            drawMesh(cylinder, x, y, z, radius, height, radius, r, g, b)

        private fun cone(x: Float, y: Float, z: Float, radius: Float, height: Float, r: Float, g: Float, b: Float) =
            drawMesh(cone, x, y, z, radius, height, radius, r, g, b)

        private fun roof(x: Float, y: Float, z: Float, sx: Float, sy: Float, sz: Float, r: Float, g: Float, b: Float) =
            drawMesh(roof, x, y, z, sx, sy, sz, r, g, b)

        private fun drawMesh(mesh: Mesh, x: Float, y: Float, z: Float, sx: Float, sy: Float, sz: Float, r: Float, g: Float, b: Float) {
            Matrix.setIdentityM(model, 0)
            Matrix.translateM(model, 0, x, y, z)
            Matrix.scaleM(model, 0, sx, sy, sz)
            Matrix.multiplyMM(mvp, 0, view, 0, model, 0)
            Matrix.multiplyMM(mvp, 0, projection, 0, mvp, 0)
            mesh.draw(program, mvp, r, g, b)
        }

        private fun cube(
            x: Float, y: Float, z: Float,
            sx: Float, sy: Float, sz: Float,
            r: Float, g: Float, b: Float
        ) {
            Matrix.setIdentityM(model, 0)
            Matrix.translateM(model, 0, x, y, z)
            Matrix.scaleM(model, 0, sx, sy, sz)
            Matrix.multiplyMM(mvp, 0, view, 0, model, 0)
            Matrix.multiplyMM(mvp, 0, projection, 0, mvp, 0)
            cube.draw(program, mvp, r, g, b)
        }
    }

    private class Mesh(private val vertices: FloatArray, private val indices: ShortArray) {
        private val buffer = java.nio.ByteBuffer.allocateDirect(vertices.size * 4).order(java.nio.ByteOrder.nativeOrder()).asFloatBuffer().apply { put(vertices).position(0) }
        private val indexBuffer = java.nio.ByteBuffer.allocateDirect(indices.size * 2).order(java.nio.ByteOrder.nativeOrder()).asShortBuffer().apply { put(indices).position(0) }
        fun draw(program: Int, mvp: FloatArray, r: Float, g: Float, b: Float) {
            val pos = GLES20.glGetAttribLocation(program, "aPosition")
            val matrix = GLES20.glGetUniformLocation(program, "uMvp")
            val color = GLES20.glGetUniformLocation(program, "uColor")
            GLES20.glUseProgram(program)
            GLES20.glUniformMatrix4fv(matrix, 1, false, mvp, 0)
            GLES20.glUniform4f(color, r, g, b, 1f)
            GLES20.glEnableVertexAttribArray(pos)
            GLES20.glVertexAttribPointer(pos, 3, GLES20.GL_FLOAT, false, 0, buffer)
            GLES20.glDrawElements(GLES20.GL_TRIANGLES, indices.size, GLES20.GL_UNSIGNED_SHORT, indexBuffer)
            GLES20.glDisableVertexAttribArray(pos)
        }
        companion object {
            fun sphere(): Mesh {
                val v=mutableListOf<Float>(); val ind=mutableListOf<Short>(); val rings=8; val seg=12
                for(i in 0..rings){ val p=Math.PI*i/rings; for(j in 0 until seg){ val t=2*Math.PI*j/seg; v += cos(t).toFloat()*sin(p).toFloat(); v += cos(p).toFloat(); v += sin(t).toFloat()*sin(p).toFloat() } }
                for(i in 0 until rings) for(j in 0 until seg){ val a=(i*seg+j).toShort(); val b=(i*seg+(j+1)%seg).toShort(); val c=((i+1)*seg+j).toShort(); val d=((i+1)*seg+(j+1)%seg).toShort(); ind += a; ind += c; ind += b; ind += b; ind += c; ind += d }
                return Mesh(v.toFloatArray(),ind.toShortArray())
            }
            fun cylinder(): Mesh { return prism(12, 1f, 1f) }
            fun cone(): Mesh { return coneMesh(12) }
            fun roof(): Mesh { return prism(4, 1f, 0.72f) }
            private fun prism(seg:Int, top:Float, bottom:Float): Mesh { val v=mutableListOf<Float>(); val ind=mutableListOf<Short>(); v += -1f;v += -1f;v += -1f;v += 1f;v += -1f;v += -1f;v += 1f;v += -1f;v += 1f;v += -1f;v += -1f;v += 1f; return cubeLike(v,ind) }
            private fun cubeLike(v:MutableList<Float>,i:MutableList<Short>):Mesh { i += 0;i += 1;i += 2;i += 2;i += 3;i += 0; return Mesh(v.toFloatArray(),i.toShortArray()) }
            private fun coneMesh(seg:Int): Mesh { val v=mutableListOf<Float>(); val i=mutableListOf<Short>(); v += 0f;v += 1f;v += 0f; for(j in 0 until seg){ val t=2*Math.PI*j/seg; v += cos(t).toFloat();v += -1f;v += sin(t).toFloat() }; for(j in 0 until seg){ i += 0;i += (1+j).toShort();i += (1+(j+1)%seg).toShort() }; return Mesh(v.toFloatArray(),i.toShortArray()) }
        }
    }

    private class Cube {
        private val vertices = floatArrayOf(
            -1f,-1f,-1f, 1f,-1f,-1f, 1f,1f,-1f, -1f,1f,-1f,
            -1f,-1f,1f, 1f,-1f,1f, 1f,1f,1f, -1f,1f,1f
        )
        private val indices = shortArrayOf(
            0,1,2, 2,3,0, 1,5,6, 6,2,1, 5,4,7, 7,6,5,
            4,0,3, 3,7,4, 3,2,6, 6,7,3, 4,5,1, 1,0,4
        )
        private val buffer = java.nio.ByteBuffer.allocateDirect(vertices.size * 4)
            .order(java.nio.ByteOrder.nativeOrder()).asFloatBuffer()
        private val indexBuffer = java.nio.ByteBuffer.allocateDirect(indices.size * 2)
            .order(java.nio.ByteOrder.nativeOrder()).asShortBuffer()

        init {
            buffer.put(vertices).position(0)
            indexBuffer.put(indices).position(0)
        }

        fun draw(program: Int, mvp: FloatArray, r: Float, g: Float, b: Float) {
            val pos = GLES20.glGetAttribLocation(program, "aPosition")
            val matrix = GLES20.glGetUniformLocation(program, "uMvp")
            val color = GLES20.glGetUniformLocation(program, "uColor")
            GLES20.glUseProgram(program)
            GLES20.glUniformMatrix4fv(matrix, 1, false, mvp, 0)
            GLES20.glUniform4f(color, r, g, b, 1f)
            GLES20.glEnableVertexAttribArray(pos)
            GLES20.glVertexAttribPointer(pos, 3, GLES20.GL_FLOAT, false, 0, buffer)
            GLES20.glDrawElements(GLES20.GL_TRIANGLES, indices.size, GLES20.GL_UNSIGNED_SHORT, indexBuffer)
            GLES20.glDisableVertexAttribArray(pos)
        }
    }

    private object Shader {
        fun create(): Int {
            val vertex = compile(
                GLES20.GL_VERTEX_SHADER,
                "attribute vec4 aPosition; uniform mat4 uMvp; void main(){gl_Position=uMvp*aPosition;}"
            )
            val fragment = compile(
                GLES20.GL_FRAGMENT_SHADER,
                "precision mediump float; uniform vec4 uColor; void main(){gl_FragColor=uColor;}"
            )
            return GLES20.glCreateProgram().also {
                GLES20.glAttachShader(it, vertex)
                GLES20.glAttachShader(it, fragment)
                GLES20.glLinkProgram(it)
            }
        }

        private fun compile(type: Int, source: String): Int {
            return GLES20.glCreateShader(type).also {
                GLES20.glShaderSource(it, source)
                GLES20.glCompileShader(it)
            }
        }
    }
}
