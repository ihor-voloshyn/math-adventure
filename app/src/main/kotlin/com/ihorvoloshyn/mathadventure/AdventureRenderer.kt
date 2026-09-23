package com.ihorvoloshyn.mathadventure

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.view.MotionEvent
import kotlin.math.cos
import kotlin.math.sin

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
        private lateinit var gableRoof: GableRoof

        override fun onSurfaceCreated(
            gl: javax.microedition.khronos.opengles.GL10?,
            config: javax.microedition.khronos.egl.EGLConfig?
        ) {
            GLES20.glClearColor(0.38f, 0.62f, 0.86f, 1f)
            program = Shader.create()
            cube = Cube()
            gableRoof = GableRoof()
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
            // Warm cottage silhouette: wall mass + gabled roof + readable door/windows.
            cube(-2.5f, 1.15f, -1.8f, 3.35f, 2.35f, 2.75f, 0.68f, 0.42f, 0.24f)
            roof(-2.5f, 3.05f, -1.8f, 3.85f, 1.35f, 3.15f, 0.48f, 0.18f, 0.12f)
            cube(-2.5f, 0.82f, -0.28f, 0.72f, 1.18f, 0.16f, 0.22f, 0.11f, 0.05f)
            cube(-3.45f, 1.45f, -0.34f, 0.72f, 0.72f, 0.10f, 0.34f, 0.64f, 0.82f)
            cube(-1.55f, 1.45f, -0.34f, 0.72f, 0.72f, 0.10f, 0.34f, 0.64f, 0.82f)
            cube(-3.85f, 0.35f, -0.05f, 0.32f, 0.18f, 0.32f, 0.84f, 0.68f, 0.28f)
            cube(-1.15f, 0.35f, -0.05f, 0.32f, 0.18f, 0.32f, 0.84f, 0.68f, 0.28f)
            cube(-1.25f, 3.45f, -2.35f, 0.28f, 0.65f, 0.28f, 0.38f, 0.25f, 0.20f)
            // Small pet-bed marker in the yard.
            cube(-3.25f, 0.20f, 0.25f, 0.72f, 0.18f, 0.48f, 0.70f, 0.38f, 0.20f)
        }

        private fun drawVillage() {
            cube(2.6f, 0.45f, -2.0f, 2.7f, 0.9f, 2.4f, 0.72f, 0.50f, 0.34f)
            cube(2.6f, 1.05f, -2.0f, 3.0f, 0.3f, 2.7f, 0.78f, 0.18f, 0.14f)
            tree(-3.0f, 0f, -2.8f)
            tree(3.8f, 0f, 2.3f)
        }

        private fun drawForest(combat: Boolean = false) {
            tree(-3.2f, 0f, -2.0f)
            tree(-1.0f, 0f, -3.2f)
            tree(3.8f, 0f, 1.7f)
            tree(2.8f, 0f, -3.0f)
            if (combat) cube(0.8f, 0.12f, -1.2f, 2.8f, 0.08f, 2.0f, 0.35f, 0.52f, 0.28f)
        }

        private fun drawHero(x: Float, y: Float, z: Float) {
            cube(x, y + 1.0f, z, 0.75f, 1.5f, 0.55f, 0.25f, 0.42f, 0.78f)
            cube(x, y + 2.0f, z, 0.7f, 0.7f, 0.7f, 0.95f, 0.78f, 0.58f)
            cube(x - 0.24f, y + 0.2f, z, 0.2f, 0.7f, 0.3f, 0.15f, 0.16f, 0.25f)
            cube(x + 0.24f, y + 0.2f, z, 0.2f, 0.7f, 0.3f, 0.15f, 0.16f, 0.25f)
            if (equippedWeaponVisualId == "weapon_sword_sparks") {
                cube(x + 0.72f, y + 1.12f, z, 0.12f, 1.15f, 0.12f, 0.78f, 0.78f, 0.82f)
                cube(x + 0.72f, y + 0.55f, z, 0.35f, 0.10f, 0.14f, 0.32f, 0.18f, 0.08f)
            } else {
                cube(x + 0.55f, y + 1.05f, z, 0.18f, 0.9f, 0.18f, 0.72f, 0.72f, 0.78f)
            }
        }

        private fun drawPet(x: Float, y: Float, z: Float) {
            val bob = sin(angle * 0.04f) * 0.08f
            cube(x, y + 0.38f + bob, z, 0.75f, 0.55f, 0.7f, 0.82f, 0.62f, 0.22f)
            cube(x, y + 0.75f + bob, z, 0.52f, 0.45f, 0.52f, 0.94f, 0.76f, 0.38f)
            cube(x - 0.2f, y + 0.92f + bob, z, 0.12f, 0.18f, 0.12f, 0.94f, 0.76f, 0.38f)
            cube(x + 0.2f, y + 0.92f + bob, z, 0.12f, 0.18f, 0.12f, 0.94f, 0.76f, 0.38f)
        }

        private fun drawNpc(x: Float, y: Float, z: Float) {
            cube(x, y + 1.0f, z, 0.75f, 1.5f, 0.55f, 0.32f, 0.62f, 0.38f)
            cube(x, y + 2.0f, z, 0.7f, 0.7f, 0.7f, 0.95f, 0.78f, 0.58f)
        }

        private fun drawEnemy(x: Float, y: Float, z: Float) {
            val bounce = if (victory) -0.35f else 0f
            cube(x, y + 0.8f + bounce, z, 1.1f, 1.2f, 0.9f, 0.44f, 0.62f, 0.24f)
            cube(x, y + 1.7f + bounce, z, 0.9f, 0.75f, 0.9f, 0.38f, 0.55f, 0.20f)
            cube(x - 0.38f, y + 2.25f + bounce, z, 0.18f, 0.55f, 0.18f, 0.38f, 0.55f, 0.20f)
            cube(x + 0.38f, y + 2.25f + bounce, z, 0.18f, 0.55f, 0.18f, 0.38f, 0.55f, 0.20f)
        }

        private fun tree(x: Float, y: Float, z: Float) {
            cube(x, y + 0.9f, z, 0.35f, 1.8f, 0.35f, 0.42f, 0.24f, 0.10f)
            cube(x, y + 2.1f, z, 1.6f, 1.6f, 1.6f, 0.16f, 0.52f, 0.22f)
            cube(x, y + 2.85f, z, 1.2f, 1.1f, 1.2f, 0.18f, 0.62f, 0.25f)
        }

        private fun roof(
            x: Float, y: Float, z: Float,
            sx: Float, sy: Float, sz: Float,
            r: Float, g: Float, b: Float
        ) {
            Matrix.setIdentityM(model, 0)
            Matrix.translateM(model, 0, x, y, z)
            Matrix.scaleM(model, 0, sx, sy, sz)
            Matrix.multiplyMM(mvp, 0, view, 0, model, 0)
            Matrix.multiplyMM(mvp, 0, projection, 0, mvp, 0)
            gableRoof.draw(program, mvp, r, g, b)
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

    private class GableRoof {
        private val vertices = floatArrayOf(
            -1f,0f,-1f, 1f,0f,-1f, 0f,1f,-1f,
            -1f,0f,1f, 1f,0f,1f, 0f,1f,1f
        )
        private val indices = shortArrayOf(
            0,1,2, 3,5,4,
            0,3,4, 4,1,0,
            1,4,5, 5,2,1,
            2,5,3, 3,0,2
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
