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

    override fun onTouchEvent(event: MotionEvent): Boolean = scene.handleTouch(event)

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
        // Prototype defaults to a cat hero; the same renderer supports a dog hero.
        private val heroKind = HeroKind.CAT
        private var lastTouchY = 0f

        private lateinit var cube: Mesh
        private lateinit var sphere: Mesh
        private lateinit var cylinder: Mesh
        private lateinit var cone: Mesh

        private enum class HeroKind { CAT, DOG }

        override fun onSurfaceCreated(
            gl: javax.microedition.khronos.opengles.GL10?,
            config: javax.microedition.khronos.egl.EGLConfig?
        ) {
            GLES20.glClearColor(0.12f, 0.18f, 0.28f, 1f)
            program = Shader.create()
            cube = Mesh.cube()
            sphere = Mesh.sphere(14, 10)
            cylinder = Mesh.cylinder(14)
            cone = Mesh.cone(14)
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
            angle += 0.08f
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

            val horizontalDistance = 12f
            val cameraX = sin(cameraYaw) * horizontalDistance
            val cameraZ = cos(cameraYaw) * horizontalDistance
            val cameraY = 5.5f + cameraPitch * 2.5f

            Matrix.setLookAtM(
                view, 0,
                cameraX, cameraY, cameraZ,
                0f, 0.9f, 0f,
                0f, 1f, 0f
            )
            drawWorld()
        }

        fun handleTouch(event: MotionEvent): Boolean {
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    lastTouchX = event.x
                    lastTouchY = event.y
                }
                MotionEvent.ACTION_MOVE -> {
                    val dx = event.x - lastTouchX
                    val dy = event.y - lastTouchY
                    lastTouchX = event.x
                    lastTouchY = event.y
                    cameraYaw = (cameraYaw - dx * 0.008f) % (2f * Math.PI.toFloat())
                    cameraPitch = (cameraPitch + dy * 0.006f).coerceIn(0.15f, 1.25f)
                }
            }
            return true
        }

        private fun drawWorld() {
            cube(0f, -0.25f, 0f, 14f, 0.35f, 14f, 0.14f, 0.30f, 0.18f)

            when (stage) {
                0, 4 -> drawHome()
                1 -> drawVillage()
                2 -> drawForest(false)
                3 -> drawForest(true)
            }

            // The hero and pet are always visible so the player has a clear avatar identity.
            drawHero(-1.5f, 0f, 1.4f)
            drawPet(0.0f, 0f, 1.8f)

            when (stage) {
                1 -> drawNpc(1.8f, 0f, -1.2f)
                2 -> drawEnemy(2.2f, 0f, -1.0f)
                3 -> drawCombatEnemy(2.5f, 0f, -1.4f)
            }
        }

        private fun drawHome() {
            // House: walls + pitched roof + door + windows.
            cube(-2.6f, 1.0f, -1.9f, 3.5f, 2.1f, 2.8f, 0.78f, 0.48f, 0.30f)
            cone(-2.6f, 2.65f, -1.9f, 2.55f, 1.25f, 0.86f, 0.18f, 0.12f)
            cube(-2.6f, 0.75f, -0.33f, 0.65f, 1.25f, 0.18f, 0.18f, 0.08f, 0.04f)
            cube(-1.35f, 1.25f, -0.46f, 0.55f, 0.65f, 0.08f, 0.24f, 0.62f, 0.82f)
            cube(-3.85f, 1.25f, -0.46f, 0.55f, 0.65f, 0.08f, 0.24f, 0.62f, 0.82f)
            cylinder(-0.8f, 0.22f, 0.6f, 0.55f, 0.32f, 0.62f, 0.38f, 0.22f)
        }

        private fun drawVillage() {
            cube(2.6f, 0.45f, -2.0f, 2.7f, 0.9f, 2.4f, 0.72f, 0.50f, 0.34f)
            cone(2.6f, 1.35f, -2.0f, 2.15f, 0.85f, 0.78f, 0.18f, 0.12f)
            cylinder(2.6f, 1.0f, -0.35f, 0.7f, 0.95f, 0.25f, 0.25f, 0.32f)
            tree(-3.0f, 0f, -2.8f)
            tree(3.8f, 0f, 2.3f)
            tree(-4.2f, 0f, 1.4f)
        }

        private fun drawForest(combat: Boolean) {
            tree(-3.2f, 0f, -2.0f)
            tree(-1.0f, 0f, -3.2f)
            tree(3.8f, 0f, 1.7f)
            tree(2.8f, 0f, -3.0f)
            tree(-4.0f, 0f, 2.7f)
            if (combat) {
                cylinder(0.8f, 0.04f, -1.2f, 2.8f, 0.08f, 0.35f, 0.52f, 0.28f)
            }
        }

        private fun drawHero(x: Float, y: Float, z: Float) {
            val bob = sin(angle) * 0.025f
            val furR = if (heroKind == HeroKind.CAT) 0.68f else 0.50f
            val furG = if (heroKind == HeroKind.CAT) 0.48f else 0.32f
            val furB = if (heroKind == HeroKind.CAT) 0.30f else 0.18f
            sphere(x, y + 0.92f + bob, z, 0.56f, 0.78f, 0.46f, furR, furG, furB)
            sphere(x, y + 1.66f + bob, z - 0.02f, 0.50f, 0.48f, 0.46f, furR, furG, furB)
            sphere(x, y + 1.56f + bob, z - 0.40f, 0.30f, 0.24f, 0.24f, 0.82f, 0.62f, 0.46f)
            if (heroKind == HeroKind.CAT) {
                cone(x - 0.30f, y + 2.10f + bob, z, 0.22f, 0.52f, furR, furG, furB)
                cone(x + 0.30f, y + 2.10f + bob, z, 0.22f, 0.52f, furR, furG, furB)
                cylinder(x - 0.48f, y + 0.92f + bob, z + 0.05f, 0.10f, 0.65f, furR, furG, furB)
                cone(x - 0.48f, y + 1.30f + bob, z + 0.05f, 0.11f, 0.42f, furR, furG, furB)
            } else {
                sphere(x - 0.43f, y + 1.75f + bob, z, 0.18f, 0.34f, 0.22f, 0.34f, 0.20f, 0.12f)
                sphere(x + 0.43f, y + 1.75f + bob, z, 0.18f, 0.34f, 0.22f, 0.34f, 0.20f, 0.12f)
                sphere(x, y + 1.52f + bob, z - 0.48f, 0.24f, 0.16f, 0.18f, 0.20f, 0.12f, 0.08f)
                cylinder(x + 0.48f, y + 0.98f + bob, z + 0.04f, 0.10f, 0.62f, furR, furG, furB)
            }
            sphere(x - 0.17f, y + 1.72f + bob, z - 0.43f, 0.07f, 0.07f, 0.05f, 0.03f, 0.03f, 0.03f)
            sphere(x + 0.17f, y + 1.72f + bob, z - 0.43f, 0.07f, 0.07f, 0.05f, 0.03f, 0.03f)
            sphere(x, y + 1.58f + bob, z - 0.64f, 0.07f, 0.06f, 0.05f, 0.12f, 0.05f, 0.04f)
            cube(x, y + 1.02f + bob, z - 0.04f, 0.58f, 0.10f, 0.48f, 0.18f, 0.24f, 0.34f)
            cylinder(x - 0.23f, y + 0.24f, z, 0.15f, 0.62f, furR, furG, furB)
            cylinder(x + 0.23f, y + 0.24f, z, 0.15f, 0.62f, furR, furG, furB)
            sphere(x - 0.23f, y + 0.02f, z - 0.02f, 0.18f, 0.10f, 0.28f, 0.12f, 0.12f, 0.14f)
            sphere(x + 0.23f, y + 0.02f, z - 0.02f, 0.18f, 0.10f, 0.28f, 0.12f, 0.12f, 0.14f)
            if (equippedWeaponVisualId == "weapon_sword_sparks") {
                cylinder(x + 0.67f, y + 1.02f, z - 0.02f, 0.07f, 1.15f, 0.78f, 0.80f, 0.86f)
                cylinder(x + 0.67f, y + 0.52f, z - 0.02f, 0.18f, 0.10f, 0.32f, 0.18f, 0.08f)
            } else {
                cylinder(x + 0.55f, y + 1.0f, z, 0.07f, 0.82f, 0.72f, 0.72f, 0.76f)
            }
        }\n\n        private fun drawPet(x: Float, y: Float, z: Float) {
            val bob = sin(angle * 1.5f) * 0.08f
            sphere(x, y + 0.45f + bob, z, 0.62f, 0.46f, 0.78f, 0.22f, 0.54f, 0.66f)
            sphere(x, y + 0.95f + bob, z - 0.02f, 0.44f, 0.42f, 0.48f, 0.30f, 0.66f, 0.72f)
            sphere(x, y + 0.90f + bob, z - 0.40f, 0.24f, 0.20f, 0.18f, 0.72f, 0.82f, 0.78f)
            cone(x - 0.28f, y + 1.30f + bob, z, 0.18f, 0.42f, 0.20f, 0.48f, 0.60f)
            cone(x + 0.28f, y + 1.30f + bob, z, 0.18f, 0.42f, 0.20f, 0.48f, 0.60f)
            sphere(x - 0.15f, y + 1.03f + bob, z - 0.42f, 0.06f, 0.06f, 0.04f, 0.02f, 0.02f, 0.02f)
            sphere(x + 0.15f, y + 1.03f + bob, z - 0.42f, 0.06f, 0.06f, 0.04f, 0.02f, 0.02f, 0.02f)
            cylinder(x + 0.62f, y + 0.62f + bob, z + 0.02f, 0.08f, 0.52f, 0.22f, 0.54f, 0.66f)
        }\n\n        private fun drawNpc(x: Float, y: Float, z: Float) {
            cylinder(x, y + 0.82f, z, 0.58f, 1.25f, 0.30f, 0.58f, 0.38f)
            sphere(x, y + 1.72f, z, 0.48f, 0.88f, 0.72f, 0.55f, 0.38f)
            cone(x, y + 2.20f, z, 0.62f, 0.45f, 0.22f, 0.34f, 0.20f)
        }

        private fun drawEnemy(x: Float, y: Float, z: Float) {
            val bob = sin(angle * 1.2f) * 0.06f
            sphere(x, y + 0.72f + bob, z, 0.88f, 0.72f, 0.66f, 0.34f, 0.20f, 0.12f)
            sphere(x, y + 1.48f + bob, z - 0.02f, 0.66f, 0.58f, 0.58f, 0.40f, 0.26f, 0.16f)
            cone(x - 0.40f, y + 2.12f + bob, z, 0.22f, 0.68f, 0.30f, 0.16f, 0.10f)
            cone(x + 0.40f, y + 2.12f + bob, z, 0.22f, 0.68f, 0.30f, 0.16f, 0.10f)
            cylinder(x - 0.92f, y + 0.78f + bob, z - 0.02f, 0.13f, 0.78f, 0.28f, 0.16f, 0.10f)
            cylinder(x + 0.92f, y + 0.78f + bob, z - 0.02f, 0.13f, 0.78f, 0.28f, 0.16f, 0.10f)
            cylinder(x - 0.32f, y + 0.18f + bob, z, 0.16f, 0.52f, 0.25f, 0.14f, 0.09f)
            cylinder(x + 0.32f, y + 0.18f + bob, z, 0.16f, 0.52f, 0.25f, 0.14f, 0.09f)
            sphere(x - 0.20f, y + 1.58f + bob, z - 0.50f, 0.09f, 0.09f, 0.05f, 0.95f, 0.76f, 0.08f)
            sphere(x + 0.20f, y + 1.58f + bob, z - 0.50f, 0.09f, 0.09f, 0.05f, 0.95f, 0.76f, 0.08f)
            sphere(x, y + 1.38f + bob, z - 0.56f, 0.10f, 0.07f, 0.05f, 0.08f, 0.03f, 0.02f)
        }\n\n        private fun drawCombatEnemy(x: Float, y: Float, z: Float) {
            val bounce = if (victory) -0.45f else sin(angle * 1.5f) * 0.04f
            sphere(x, y + 0.85f + bounce, z, 1.10f, 0.86f, 0.92f, 0.46f, 0.18f, 0.10f)
            sphere(x, y + 1.76f + bounce, z - 0.03f, 0.82f, 0.66f, 0.74f, 0.38f, 0.12f, 0.08f)
            cone(x - 0.52f, y + 2.50f + bounce, z, 0.28f, 0.90f, 0.28f, 0.10f, 0.06f)
            cone(x + 0.52f, y + 2.50f + bounce, z, 0.28f, 0.90f, 0.28f, 0.10f, 0.06f)
            cylinder(x - 1.05f, y + 0.90f + bounce, z - 0.02f, 0.16f, 0.95f, 0.34f, 0.12f, 0.07f)
            cylinder(x + 1.05f, y + 0.90f + bounce, z - 0.02f, 0.16f, 0.95f, 0.34f, 0.12f, 0.07f)
            cylinder(x - 0.40f, y + 0.22f + bounce, z, 0.20f, 0.62f, 0.28f, 0.10f, 0.06f)
            cylinder(x + 0.40f, y + 0.22f + bounce, z, 0.20f, 0.62f, 0.28f, 0.10f, 0.06f)
            sphere(x - 0.28f, y + 1.86f + bounce, z - 0.66f, 0.12f, 0.12f, 0.06f, 1.0f, 0.82f, 0.10f)
            sphere(x + 0.28f, y + 1.86f + bounce, z - 0.66f, 0.12f, 0.12f, 0.06f, 1.0f, 0.82f, 0.10f)
            sphere(x, y + 1.64f + bounce, z - 0.72f, 0.12f, 0.08f, 0.06f, 0.05f, 0.02f, 0.01f)
        }\n\n        private fun tree(x: Float, y: Float, z: Float) {
            cylinder(x, y + 0.9f, z, 0.32f, 1.8f, 0.40f, 0.22f, 0.08f)
            sphere(x, y + 2.05f, z, 1.25f, 0.82f, 0.95f, 0.10f, 0.45f)
            sphere(x, y + 2.75f, z + 0.05f, 0.95f, 0.78f, 0.82f, 0.12f, 0.54f)
        }

        private fun drawMesh(
            mesh: Mesh,
            x: Float, y: Float, z: Float,
            sx: Float, sy: Float, sz: Float,
            r: Float, g: Float, b: Float
        ) {
            Matrix.setIdentityM(model, 0)
            Matrix.translateM(model, 0, x, y, z)
            Matrix.scaleM(model, 0, sx, sy, sz)
            Matrix.multiplyMM(mvp, 0, view, 0, model, 0)
            Matrix.multiplyMM(mvp, 0, projection, 0, mvp, 0)
            mesh.draw(program, mvp, r, g, b)
        }

        private fun cube(x: Float, y: Float, z: Float, sx: Float, sy: Float, sz: Float, r: Float, g: Float, b: Float) =
            drawMesh(cube, x, y, z, sx, sy, sz, r, g, b)

        private fun sphere(x: Float, y: Float, z: Float, sx: Float, sy: Float, sz: Float, r: Float, g: Float, b: Float) =
            drawMesh(sphere, x, y, z, sx, sy, sz, r, g, b)

        private fun cylinder(x: Float, y: Float, z: Float, radius: Float, height: Float, r: Float, g: Float, b: Float) =
            drawMesh(cylinder, x, y, z, radius, height, radius, r, g, b)

        private fun cone(x: Float, y: Float, z: Float, radius: Float, height: Float, r: Float, g: Float, b: Float) =
            drawMesh(cone, x, y, z, radius, height, radius, r, g, b)
    }

    private class Mesh(
        vertices: FloatArray,
        indices: ShortArray
    ) {
        private val vertexBuffer = java.nio.ByteBuffer.allocateDirect(vertices.size * 4)
            .order(java.nio.ByteOrder.nativeOrder()).asFloatBuffer()
        private val indexBuffer = java.nio.ByteBuffer.allocateDirect(indices.size * 2)
            .order(java.nio.ByteOrder.nativeOrder()).asShortBuffer()
        private val indexCount = indices.size

        init {
            vertexBuffer.put(vertices).position(0)
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
            GLES20.glVertexAttribPointer(pos, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer)
            GLES20.glDrawElements(GLES20.GL_TRIANGLES, indexCount, GLES20.GL_UNSIGNED_SHORT, indexBuffer)
            GLES20.glDisableVertexAttribArray(pos)
        }

        companion object {
            fun cube() = Mesh(
                floatArrayOf(
                    -1f,-1f,-1f, 1f,-1f,-1f, 1f,1f,-1f, -1f,1f,-1f,
                    -1f,-1f,1f, 1f,-1f,1f, 1f,1f,1f, -1f,1f,1f
                ),
                shortArrayOf(
                    0,1,2, 2,3,0, 1,5,6, 6,2,1, 5,4,7, 7,6,5,
                    4,0,3, 3,7,4, 3,2,6, 6,7,3, 4,5,1, 1,0,4
                )
            )

            fun sphere(segments: Int, rings: Int): Mesh {
                val vertices = mutableListOf<Float>()
                val indices = mutableListOf<Short>()
                for (ring in 0..rings) {
                    val v = ring.toFloat() / rings
                    val phi = Math.PI.toFloat() * (v - 0.5f)
                    val y = sin(phi)
                    val radius = cos(phi)
                    for (segment in 0 until segments) {
                        val u = segment.toFloat() / segments
                        val theta = 2f * Math.PI.toFloat() * u
                        vertices += (cos(theta) * radius)
                        vertices += y
                        vertices += (sin(theta) * radius)
                    }
                }
                for (ring in 0 until rings) {
                    for (segment in 0 until segments) {
                        val next = (segment + 1) % segments
                        val a = (ring * segments + segment).toShort()
                        val b = (ring * segments + next).toShort()
                        val c = ((ring + 1) * segments + next).toShort()
                        val d = ((ring + 1) * segments + segment).toShort()
                        indices += a; indices += b; indices += c
                        indices += c; indices += d; indices += a
                    }
                }
                return Mesh(vertices.toFloatArray(), indices.toShortArray())
            }

            fun cylinder(segments: Int): Mesh {
                val vertices = mutableListOf<Float>()
                val indices = mutableListOf<Short>()
                vertices += 0f; vertices += -1f; vertices += 0f
                vertices += 0f; vertices += 1f; vertices += 0f
                for (segment in 0 until segments) {
                    val a = 2 + segment * 2
                    val theta = 2f * Math.PI.toFloat() * segment / segments
                    val x = cos(theta)
                    val z = sin(theta)
                    vertices += x; vertices += -1f; vertices += z
                    vertices += x; vertices += 1f; vertices += z
                }
                for (segment in 0 until segments) {
                    val next = (segment + 1) % segments
                    val botA = (2 + segment * 2).toShort()
                    val topA = (3 + segment * 2).toShort()
                    val botB = (2 + next * 2).toShort()
                    val topB = (3 + next * 2).toShort()
                    indices += 0; indices += botB; indices += botA
                    indices += 1; indices += topA; indices += topB
                    indices += botA; indices += botB; indices += topB
                    indices += topB; indices += topA; indices += botA
                }
                return Mesh(vertices.toFloatArray(), indices.toShortArray())
            }

            fun cone(segments: Int): Mesh {
                val vertices = mutableListOf<Float>()
                val indices = mutableListOf<Short>()
                vertices += 0f; vertices += 1f; vertices += 0f
                vertices += 0f; vertices += -1f; vertices += 0f
                for (segment in 0 until segments) {
                    val theta = 2f * Math.PI.toFloat() * segment / segments
                    vertices += cos(theta); vertices += -1f; vertices += sin(theta)
                }
                for (segment in 0 until segments) {
                    val next = (segment + 1) % segments
                    val rimA = (2 + segment).toShort()
                    val rimB = (2 + next).toShort()
                    indices += 0; indices += rimA; indices += rimB
                    indices += 1; indices += rimB; indices += rimA
                }
                return Mesh(vertices.toFloatArray(), indices.toShortArray())
            }
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

        private fun compile(type: Int, source: String): Int =
            GLES20.glCreateShader(type).also {
                GLES20.glShaderSource(it, source)
                GLES20.glCompileShader(it)
            }
    }
}
