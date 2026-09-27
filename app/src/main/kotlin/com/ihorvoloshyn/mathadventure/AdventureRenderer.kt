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
    fun setEquippedArmor(visualId: String?) { scene.equippedArmorVisualId = visualId }
    fun setAttackActive(value: Boolean) { scene.attackActive = value }
    fun setHitFeedback(value: Boolean) { scene.hitFeedback = value }
    fun setHeroClass(value: String) { scene.heroClass = if (value.equals("MAGE", ignoreCase = true)) SceneRenderer.HeroClass.MAGE else SceneRenderer.HeroClass.KNIGHT }
    fun setHeroKind(value: String) { scene.heroKind = if (value.equals("DOG", ignoreCase = true)) SceneRenderer.HeroKind.DOG else SceneRenderer.HeroKind.CAT }
    fun setPetKind(value: String) { scene.petKind = if (value.equals("PUPPY", ignoreCase = true)) SceneRenderer.PetKind.PUPPY else SceneRenderer.PetKind.KITTEN }

    override fun onTouchEvent(event: MotionEvent): Boolean = scene.handleTouch(event)

    private class SceneRenderer : Renderer {
        var stage = 0
        var victory = false
        var equippedWeaponVisualId: String? = null
        var equippedArmorVisualId: String? = null
        @Volatile var attackActive = false
        @Volatile var hitFeedback = false
        var heroClass = HeroClass.KNIGHT

        private var program = 0
        private val projection = FloatArray(16)
        private val view = FloatArray(16)
        private val model = FloatArray(16)
        private val mvp = FloatArray(16)
        private var angle = 0f
        private var attackClock = 0f
        private var hitClock = 0f
        private var previousAttackActive = false
        private var previousHitFeedback = false
        private var cameraYaw = 0f
        private var cameraPitch = 0.62f
        private var lastTouchX = 0f
        // Prototype defaults to a cat hero; the same renderer supports a dog hero.
        var heroKind = HeroKind.CAT
        var petKind = PetKind.KITTEN
        private var lastTouchY = 0f
        private var characterYaw = 0f

        private lateinit var cube: Mesh
        private lateinit var sphere: Mesh
        private lateinit var cylinder: Mesh
        private lateinit var cone: Mesh

        enum class HeroKind { CAT, DOG }
        enum class PetKind { KITTEN, PUPPY }
        enum class HeroClass { KNIGHT, MAGE }

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

            if (attackActive) {
                if (!previousAttackActive) attackClock = 0f
                attackClock = (attackClock + 0.055f).coerceAtMost(1f)
            } else {
                attackClock = 0f
            }
            previousAttackActive = attackActive

            if (hitFeedback) {
                if (!previousHitFeedback) hitClock = 0f
                hitClock = (hitClock + 0.085f).coerceAtMost(1f)
            } else {
                hitClock = 0f
            }
            previousHitFeedback = hitFeedback

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
            cube(0f, -0.25f, 0f, 14f, 0.35f, 14f, 0.12f, 0.25f, 0.16f)
            stone(-5.0f, 0f, -0.8f, 0.24f, 0.14f, 0.18f)
            stone(5.1f, 0f, -1.8f, 0.30f, 0.16f, 0.22f)
            stone(4.2f, 0f, 3.1f, 0.20f, 0.12f, 0.16f)

            when (stage) {
                0, 4 -> drawHome()
                1 -> drawVillage()
                2 -> drawForest(false)
                3 -> drawForest(true)
            }

            characterYaw = if (stage == 3) Math.PI.toFloat() else 0f
            val attackProgress = if (attackActive && stage == 3) attackClock else 0f
            drawHero(-0.9f + attackLunge(attackProgress), 0f, 1.7f, attackProgress)
            characterYaw = 0f

            when (stage) {
                1 -> drawNpc(1.8f, 0f, -1.2f)
                2 -> drawCombatEnemy(2.5f, 0f, -1.4f)
                3 -> {
                    drawCombatEnemy(2.5f + hitKnockback(hitClock), 0f, -1.4f)
                    drawAttackVfx(-0.9f + attackLunge(attackProgress), 1.35f, 1.62f, attackProgress, hitClock)
                }
            }
        }

        private fun drawAttackVfx(heroX: Float, heroY: Float, heroZ: Float, attackProgress: Float, hitProgress: Float) {
            if (heroClass == HeroClass.MAGE && attackProgress > 0.18f) {
                // Mage: readable magical projectile travelling from staff toward the enemy.
                val t = ((attackProgress - 0.18f) / 0.82f).coerceIn(0f, 1f)
                val eased = t * t * (3f - 2f * t)
                val x = heroX + 0.85f + eased * 1.85f
                val y = heroY + 0.10f + sin(t * Math.PI.toFloat()) * 0.28f
                val z = heroZ - 0.03f
                val pulse = 0.09f + sin(t * Math.PI.toFloat()) * 0.035f
                sphere(x, y, z, pulse, pulse, pulse, 0.30f, 0.78f, 1.0f)
                sphere(x, y, z + 0.025f, pulse * 0.48f, pulse * 0.48f, pulse * 0.48f, 0.82f, 0.96f, 1.0f)
                if (t > 0.18f) {
                    sphere(x - 0.14f, y + 0.08f, z, pulse * 0.35f, pulse * 0.35f, pulse * 0.35f, 0.50f, 0.86f, 1.0f)
                    sphere(x - 0.22f, y - 0.06f, z, pulse * 0.25f, pulse * 0.25f, pulse * 0.25f, 0.72f, 0.92f, 1.0f)
                }
            } else if (heroClass == HeroClass.KNIGHT && attackProgress > 0.58f && attackProgress < 0.88f) {
                // Knight: short physical impact burst at the enemy.
                val t = ((attackProgress - 0.58f) / 0.30f).coerceIn(0f, 1f)
                val radius = 0.10f + t * 0.38f
                val y = 1.28f + t * 0.18f
                sphere(2.35f, y, -1.42f, radius, radius * 0.42f, radius * 0.22f, 0.95f, 0.78f, 0.26f)
                sphere(2.35f - radius * 0.8f, y + radius * 0.45f, -1.40f, radius * 0.16f, radius * 0.16f, radius * 0.16f, 1.0f, 0.92f, 0.50f)
                sphere(2.35f + radius * 0.7f, y - radius * 0.25f, -1.44f, radius * 0.12f, radius * 0.12f, radius * 0.12f, 1.0f, 0.84f, 0.34f)
            }

            if (hitProgress > 0f && hitProgress < 0.82f) {
                // Shared hit feedback: brief impact ring/particles; presentation only.
                val t = (hitProgress / 0.82f).coerceIn(0f, 1f)
                val radius = 0.10f + t * 0.48f
                val y = 1.22f + t * 0.22f
                if (heroClass == HeroClass.MAGE) {
                    sphere(2.48f, y, -1.44f, radius, radius, radius, 0.38f, 0.82f, 1.0f)
                    sphere(2.48f - radius * 0.85f, y + radius * 0.30f, -1.42f, radius * 0.14f, radius * 0.14f, radius * 0.14f, 0.72f, 0.94f, 1.0f)
                    sphere(2.48f + radius * 0.75f, y - radius * 0.20f, -1.46f, radius * 0.11f, radius * 0.11f, radius * 0.11f, 0.56f, 0.88f, 1.0f)
                } else {
                    sphere(2.48f, y, -1.44f, radius, radius * 0.30f, radius * 0.18f, 0.96f, 0.72f, 0.22f)
                    sphere(2.48f - radius * 0.80f, y + radius * 0.40f, -1.42f, radius * 0.13f, radius * 0.13f, radius * 0.13f, 1.0f, 0.92f, 0.48f)
                    sphere(2.48f + radius * 0.72f, y - radius * 0.25f, -1.46f, radius * 0.10f, radius * 0.10f, radius * 0.10f, 1.0f, 0.82f, 0.30f)
                }
            }
        }

        private fun attackLunge(progress: Float): Float {
            if (progress <= 0f) return 0f
            // Anticipation -> forward strike -> recoil. The impact is near 70% of the timeline.
            return when {
                progress < 0.22f -> -0.08f * (progress / 0.22f)
                progress < 0.72f -> {
                    val t = (progress - 0.22f) / 0.50f
                    -0.08f + 0.64f * (t * t * (3f - 2f * t))
                }
                else -> {
                    val t = ((progress - 0.72f) / 0.28f).coerceIn(0f, 1f)
                    0.56f * (1f - t)
                }
            }
        }

        private fun hitKnockback(progress: Float): Float {
            if (progress <= 0f) return 0f
            // Fast impact displacement followed by recovery.
            return 0.18f * sin(progress * Math.PI.toFloat())
        }

        private fun drawHome() {
            cube(-2.6f, 0.95f, -2.2f, 3.2f, 1.9f, 2.2f, 0.78f, 0.48f, 0.30f)
            cone(-2.6f, 2.65f, -2.2f, 2.35f, 1.20f, 0.86f, 0.18f, 0.12f)
            cube(-2.6f, 0.72f, 0.02f, 0.62f, 1.12f, 0.12f, 0.18f, 0.08f, 0.04f)
            cube(-1.35f, 1.25f, 0.04f, 0.55f, 0.65f, 0.08f, 0.24f, 0.62f, 0.82f)
            cube(-3.85f, 1.25f, 0.04f, 0.55f, 0.65f, 0.08f, 0.24f, 0.62f, 0.82f)
            cylinder(-2.6f, 2.85f, -2.2f, 0.18f, 0.70f, 0.32f, 0.32f, 0.34f)
            cube(-2.6f, 2.50f, 0.04f, 2.8f, 0.08f, 0.10f, 0.58f, 0.34f, 0.18f)
            cylinder(-0.8f, 0.22f, 0.6f, 0.55f, 0.32f, 0.62f, 0.38f, 0.22f)
            cylinder(-1.45f, 0.22f, 0.55f, 0.22f, 0.72f, 0.42f, 0.22f, 0.09f)
            cylinder(-1.05f, 0.18f, 0.62f, 0.20f, 0.62f, 0.50f, 0.27f, 0.11f)
            sphere(-1.75f, 0.78f, -0.36f, 0.10f, 0.14f, 0.10f, 0.92f, 0.68f, 0.18f)
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
                cylinder(0.8f, 0.10f, -1.2f, 2.15f, 0.06f, 0.48f, 0.60f, 0.34f)
                cylinder(0.8f, 0.16f, -1.2f, 1.72f, 0.05f, 0.30f, 0.42f, 0.22f)
                stone(-1.15f, 0.10f, -2.25f, 0.26f, 0.12f, 0.20f)
                stone(2.65f, 0.10f, -0.55f, 0.22f, 0.14f, 0.18f)
                stone(0.25f, 0.10f, -2.65f, 0.18f, 0.10f, 0.16f)
                cylinder(-1.15f, 0.58f, -1.20f, 0.08f, 0.92f, 0.22f, 0.12f, 0.06f)
                sphere(-1.15f, 1.12f, -1.20f, 0.18f, 0.20f, 0.18f, 0.88f, 0.42f, 0.08f)
                cylinder(2.75f, 0.58f, -1.20f, 0.08f, 0.92f, 0.22f, 0.12f, 0.06f)
                sphere(2.75f, 1.12f, -1.20f, 0.18f, 0.20f, 0.18f, 0.88f, 0.42f, 0.08f)
                cylinder(0.8f, 0.17f, 1.00f, 0.34f, 0.05f, 0.30f, 0.25f, 0.16f)
                cylinder(0.8f, 0.18f, 0.45f, 0.26f, 0.05f, 0.34f, 0.28f, 0.18f)
            }
        }

        private fun drawHero(x: Float, y: Float, z: Float, attackProgress: Float = 0f) {
            val bob = sin(angle) * 0.025f
            val furR = if (heroKind == HeroKind.CAT) 0.68f else 0.50f
            val furG = if (heroKind == HeroKind.CAT) 0.48f else 0.32f
            val furB = if (heroKind == HeroKind.CAT) 0.30f else 0.18f

            val anticipation = if (attackProgress in 0.01f..0.22f) {
                sin((attackProgress / 0.22f) * Math.PI.toFloat()) * 0.10f
            } else 0f
            val strikeLean = when {
                attackProgress < 0.22f -> 0f
                attackProgress < 0.72f -> (attackProgress - 0.22f) / 0.50f
                else -> (1f - attackProgress) / 0.28f
            }.coerceIn(0f, 1f)

            sphere(x, y + 0.92f + bob - anticipation * 0.15f, z, 0.56f, 0.78f, 0.46f, furR, furG, furB)
            if (equippedArmorVisualId == "armor_guardian_vest") drawGuardianVest(x, y + bob - anticipation * 0.12f, z)
            sphere(x, y + 1.66f + bob - anticipation * 0.10f, z + 0.02f, 0.50f, 0.48f, 0.46f, furR, furG, furB)
            sphere(x, y + 1.56f + bob + anticipation * 0.10f, z + 0.40f, 0.30f, 0.24f, 0.24f, 0.82f, 0.62f, 0.46f)
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
            sphere(x - 0.17f, y + 1.72f + bob, z + 0.43f, 0.07f, 0.07f, 0.05f, 0.03f, 0.03f, 0.03f)
            sphere(x + 0.17f, y + 1.72f + bob, z + 0.43f, 0.07f, 0.07f, 0.05f, 0.03f, 0.03f, 0.03f)
            sphere(x, y + 1.58f + bob, z + 0.64f, 0.07f, 0.06f, 0.05f, 0.12f, 0.05f, 0.04f)
            sphere(x - 0.20f, y + 1.52f + bob, z + 0.60f, 0.12f, 0.10f, 0.08f, 0.72f, 0.50f, 0.36f)
            sphere(x + 0.20f, y + 1.52f + bob, z + 0.60f, 0.12f, 0.10f, 0.08f, 0.72f, 0.50f, 0.36f)
            cylinder(x - 0.30f, y + 1.55f + bob, z + 0.67f, 0.018f, 0.34f, 0.88f, 0.82f, 0.72f)
            cylinder(x + 0.30f, y + 1.55f + bob, z + 0.67f, 0.018f, 0.34f, 0.88f, 0.82f, 0.72f)
            cylinder(x, y + 1.27f + bob, z + 0.02f, 0.31f, 0.08f, 0.82f, 0.64f, 0.12f)
            sphere(x, y + 1.25f + bob, z + 0.34f, 0.06f, 0.06f, 0.04f, 0.95f, 0.72f, 0.10f)
            cube(x, y + 1.02f + bob, z - 0.04f, 0.58f, 0.10f, 0.48f, 0.18f, 0.24f, 0.34f)
            sphere(x - 0.43f, y + 1.12f + bob, z, 0.18f, 0.13f, 0.25f, 0.22f, 0.30f, 0.40f)
            sphere(x + 0.43f, y + 1.12f + bob, z, 0.18f, 0.13f, 0.25f, 0.22f, 0.30f, 0.40f)
            sphere(x, y + 1.05f + bob, z + 0.31f, 0.11f, 0.11f, 0.05f, 0.86f, 0.64f, 0.16f)
            cylinder(x - 0.23f, y + 0.24f, z, 0.15f, 0.62f, furR, furG, furB)
            cylinder(x + 0.23f, y + 0.24f, z, 0.15f, 0.62f, furR, furG, furB)
            sphere(x - 0.23f, y + 0.02f, z + 0.02f, 0.18f, 0.10f, 0.28f, 0.12f, 0.12f, 0.14f)
            sphere(x + 0.23f, y + 0.02f, z + 0.02f, 0.18f, 0.10f, 0.28f, 0.12f, 0.12f, 0.14f)
            sphere(x - 0.52f, y + 1.17f + bob, z + 0.02f, 0.24f, 0.20f, 0.28f, 0.34f, 0.40f, 0.50f)
            sphere(x + 0.52f, y + 1.17f + bob, z + 0.02f, 0.24f, 0.20f, 0.28f, 0.34f, 0.40f, 0.50f)
            cube(x, y + 1.08f + bob, z + 0.50f, 0.20f, 0.22f, 0.06f, 0.70f, 0.58f, 0.18f)
            sphere(x, y + 1.10f + bob, z + 0.57f, 0.08f, 0.08f, 0.04f, 0.92f, 0.78f, 0.22f)
            cylinder(x, y + 0.82f + bob, z + 0.02f, 0.46f, 0.08f, 0.10f, 0.14f, 0.20f)
            sphere(x, y + 0.82f + bob, z + 0.50f, 0.09f, 0.09f, 0.05f, 0.90f, 0.70f, 0.16f)
            sphere(x - 0.24f, y + 0.42f + bob, z - 0.01f, 0.18f, 0.16f, 0.18f, 0.26f, 0.32f, 0.40f)
            sphere(x + 0.24f, y + 0.42f + bob, z - 0.01f, 0.18f, 0.16f, 0.18f, 0.26f, 0.32f, 0.40f)
            if (heroClass == HeroClass.MAGE) {
                drawMageRobe(x, y + bob, z)
                drawMagicStaff(x + 0.68f, y + 0.72f + bob, z - 0.05f, attackProgress)
            } else if (equippedWeaponVisualId != null) {
                drawSword(
                    x + 0.67f + strikeLean * 0.16f,
                    y + 0.96f + bob + anticipation * 0.18f,
                    z - 0.05f,
                    equippedWeaponVisualId == "weapon_sword_sparks",
                    strikeLean
                )
            }
        }

        private fun drawGuardianVest(x: Float, y: Float, z: Float) {
            cube(x, y + 0.98f, z - 0.01f, 0.88f, 0.92f, 0.56f, 0.18f, 0.28f, 0.42f)
            sphere(x - 0.46f, y + 1.02f, z, 0.16f, 0.34f, 0.18f, 0.14f, 0.24f, 0.36f)
            sphere(x + 0.46f, y + 1.02f, z, 0.16f, 0.34f, 0.18f, 0.14f, 0.24f, 0.36f)
            cylinder(x, y + 0.58f, z + 0.30f, 0.055f, 0.48f, 0.72f, 0.56f, 0.18f)
            sphere(x, y + 1.05f, z + 0.31f, 0.12f, 0.14f, 0.08f, 0.82f, 0.66f, 0.22f)
        }

        private fun drawMageRobe(x: Float, y: Float, z: Float) {
            // Presentation-only mage silhouette.
            cone(x, y + 0.72f, z - 0.02f, 0.62f, 0.95f, 0.24f, 0.20f, 0.46f)
            cylinder(x, y + 1.10f, z + 0.02f, 0.34f, 0.08f, 0.72f, 0.52f, 0.16f)
        }

        private fun drawMagicStaff(x: Float, y: Float, z: Float, progress: Float) {
            val cast = if (progress > 0f) sin(progress * Math.PI.toFloat()) else 0f
            val staffLean = 0.18f + cast * 0.12f
            cylinder(x, y + 0.02f, z, 0.055f, 1.55f, 0.36f, 0.20f, 0.08f)
            sphere(x, y + 0.88f + cast * 0.18f, z, 0.14f, 0.14f, 0.14f, 0.42f, 0.78f, 1.0f)
            if (progress > 0.25f && progress < 0.85f) {
                sphere(x + staffLean, y + 0.96f + cast * 0.18f, z + 0.02f, 0.07f, 0.07f, 0.07f, 0.55f, 0.88f, 1.0f)
                sphere(x + staffLean * 1.7f, y + 0.96f + cast * 0.18f, z + 0.02f, 0.045f, 0.045f, 0.045f, 0.75f, 0.94f, 1.0f)
            }
        }

        private fun drawSword(x: Float, y: Float, z: Float, enchanted: Boolean, strikeLean: Float = 0f) {
            // Readable weapon silhouette: grip + guard + blade + pommel.
            val swing = -32f * strikeLean
            cylinder(x, y - 0.36f, z, 0.065f, 0.42f, 0.24f, 0.12f, 0.06f)
            cylinder(x, y - 0.10f, z, 0.16f, 0.09f, 0.76f, 0.58f, 0.16f)
            cone(x, y + 0.50f, z, 0.12f, 0.95f, 0.72f, 0.76f, 0.82f)
            sphere(x, y - 0.58f, z, 0.10f, 0.10f, 0.10f, 0.56f, 0.38f, 0.12f)
            if (enchanted) {
                sphere(x, y + 0.72f, z - 0.03f, 0.07f, 0.07f, 0.07f, 0.45f, 0.78f, 1.0f)
                sphere(x, y + 0.42f, z - 0.03f, 0.045f, 0.045f, 0.045f, 0.75f, 0.92f, 1.0f)
                sphere(x - 0.12f, y + 0.22f, z - 0.03f, 0.035f, 0.06f, 0.035f, 0.40f, 0.82f, 1.0f)
                sphere(x + 0.12f, y + 0.58f, z - 0.03f, 0.035f, 0.06f, 0.035f, 0.55f, 0.90f, 1.0f)
            }
        }

        private fun drawPet(x: Float, y: Float, z: Float) {
            val bob = sin(angle * 1.5f) * 0.08f
            val puppy = petKind == PetKind.PUPPY
            val bodyR = if (puppy) 0.62f else 0.22f
            val bodyG = if (puppy) 0.42f else 0.54f
            val bodyB = if (puppy) 0.24f else 0.66f
            sphere(x, y + 0.45f + bob, z, 0.62f, 0.46f, 0.78f, bodyR, bodyG, bodyB)
            sphere(x, y + 0.95f + bob, z + 0.02f, 0.44f, 0.42f, 0.48f, bodyR + 0.08f, bodyG + 0.08f, bodyB + 0.06f)
            sphere(x, y + 0.90f + bob, z + 0.40f, 0.24f, 0.20f, 0.18f, 0.82f, 0.74f, 0.60f)
            if (puppy) {
                sphere(x - 0.28f, y + 1.16f + bob, z + 0.02f, 0.18f, 0.30f, 0.16f, 0.48f, 0.28f, 0.16f)
                sphere(x + 0.28f, y + 1.16f + bob, z + 0.02f, 0.18f, 0.30f, 0.16f, 0.48f, 0.28f, 0.16f)
            } else {
                cone(x - 0.28f, y + 1.30f + bob, z, 0.18f, 0.42f, bodyR, bodyG, bodyB)
                cone(x + 0.28f, y + 1.30f + bob, z, 0.18f, 0.42f, bodyR, bodyG, bodyB)
            }
            cylinder(x + 0.62f, y + 0.62f + bob, z + 0.02f, 0.08f, 0.52f, bodyR, bodyG, bodyB)
            sphere(x - 0.15f, y + 1.03f + bob, z + 0.42f, 0.06f, 0.06f, 0.04f, 0.02f, 0.02f, 0.02f)
            sphere(x + 0.15f, y + 1.03f + bob, z + 0.42f, 0.06f, 0.06f, 0.04f, 0.02f, 0.02f, 0.02f)
            sphere(x, y + 0.34f + bob, z + 0.48f, 0.045f, 0.05f, 0.035f, 0.55f, 0.88f, 1.0f)
            cylinder(x, y + 0.54f + bob, z + 0.02f, 0.40f, 0.07f, 0.12f, 0.18f, 0.24f)
            sphere(x, y + 0.56f + bob, z + 0.43f, 0.07f, 0.09f, 0.04f, 0.72f, 0.52f, 0.16f)
            cylinder(x, y + 0.82f + bob, z + 0.43f, 0.23f, 0.07f, 0.10f, 0.24f, 0.30f)
            sphere(x, y + 0.72f + bob, z + 0.50f, 0.08f, 0.09f, 0.05f, 0.92f, 0.68f, 0.16f)
            sphere(x + 0.62f, y + 0.36f + bob, z + 0.02f, 0.13f, 0.10f, 0.16f, 0.16f, 0.34f, 0.42f)
            sphere(x - 0.36f, y + 0.08f + bob, z + 0.28f, 0.18f, 0.10f, 0.22f, 0.14f, 0.36f, 0.44f)
            sphere(x + 0.36f, y + 0.08f + bob, z + 0.28f, 0.18f, 0.10f, 0.22f, 0.14f, 0.36f, 0.44f)
            sphere(x - 0.18f, y + 0.82f + bob, z + 0.49f, 0.07f, 0.05f, 0.04f, 0.08f, 0.08f, 0.08f)
            sphere(x + 0.18f, y + 0.82f + bob, z + 0.49f, 0.07f, 0.05f, 0.04f, 0.08f, 0.08f, 0.08f)
        }
        private fun drawNpc(x: Float, y: Float, z: Float) {
            cylinder(x, y + 0.82f, z, 0.58f, 1.25f, 0.30f, 0.58f, 0.38f)
            sphere(x, y + 1.72f, z, 0.48f, 0.88f, 0.72f, 0.55f, 0.38f, 0.28f)
            cone(x, y + 2.20f, z, 0.62f, 0.45f, 0.22f, 0.34f, 0.20f)
            sphere(x - 0.16f, y + 1.80f, z - 0.66f, 0.07f, 0.07f, 0.05f, 0.04f, 0.04f, 0.04f)
            sphere(x + 0.16f, y + 1.80f, z - 0.66f, 0.07f, 0.07f, 0.05f, 0.04f, 0.04f, 0.04f)
            sphere(x, y + 1.60f, z - 0.72f, 0.08f, 0.06f, 0.05f, 0.38f, 0.12f, 0.10f)
            cylinder(x, y + 0.98f, z - 0.02f, 0.62f, 0.08f, 0.18f, 0.24f, 0.12f)
            sphere(x + 0.55f, y + 0.72f, z - 0.08f, 0.20f, 0.25f, 0.14f, 0.20f, 0.12f, 0.07f)
            sphere(x + 0.55f, y + 0.88f, z - 0.20f, 0.07f, 0.07f, 0.04f, 0.82f, 0.62f, 0.16f)
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
            sphere(x - 0.30f, y + 1.40f + bob, z - 0.55f, 0.16f, 0.12f, 0.10f, 0.52f, 0.30f, 0.18f)
            sphere(x + 0.30f, y + 1.40f + bob, z - 0.55f, 0.16f, 0.12f, 0.10f, 0.52f, 0.30f, 0.18f)
            sphere(x, y + 1.38f + bob, z - 0.56f, 0.10f, 0.07f, 0.05f, 0.08f, 0.03f, 0.02f)
            cylinder(x, y + 0.88f + bob, z - 0.52f, 0.07f, 0.92f, 0.20f, 0.10f, 0.05f)
            cylinder(x - 0.92f, y + 0.82f + bob, z - 0.10f, 0.09f, 0.70f, 0.22f, 0.12f, 0.06f)
            sphere(x - 0.92f, y + 1.20f + bob, z - 0.10f, 0.16f, 0.18f, 0.16f, 0.28f, 0.15f, 0.07f)
        }

        private fun drawCombatEnemy(x: Float, y: Float, z: Float) {
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
            cylinder(x + 1.12f, y + 0.98f + bounce, z - 0.12f, 0.11f, 1.15f, 0.22f, 0.11f, 0.05f)
            sphere(x + 1.12f, y + 1.52f + bounce, z - 0.12f, 0.23f, 0.20f, 0.22f, 0.30f, 0.16f, 0.07f)
            sphere(x - 0.58f, y + 1.20f + bounce, z - 0.02f, 0.22f, 0.16f, 0.30f, 0.25f, 0.12f, 0.07f)
            cylinder(x, y + 1.18f + bounce, z - 0.02f, 0.78f, 0.14f, 0.12f, 0.07f, 0.035f)
            sphere(x - 0.78f, y + 1.18f + bounce, z - 0.02f, 0.30f, 0.22f, 0.34f, 0.18f, 0.10f, 0.06f)
            sphere(x + 0.78f, y + 1.18f + bounce, z - 0.02f, 0.30f, 0.22f, 0.34f, 0.18f, 0.10f, 0.06f)
            cylinder(x - 0.52f, y + 2.48f + bounce, z, 0.13f, 0.10f, 0.22f, 0.08f, 0.04f)
            cylinder(x + 0.52f, y + 2.48f + bounce, z, 0.13f, 0.10f, 0.22f, 0.08f, 0.04f)
            sphere(x + 1.12f, y + 1.62f + bounce, z - 0.12f, 0.30f, 0.18f, 0.26f, 0.26f, 0.12f, 0.05f)
            sphere(x + 1.12f, y + 1.78f + bounce, z - 0.12f, 0.12f, 0.08f, 0.10f, 0.38f, 0.20f, 0.08f)
        }

        private fun tree(x: Float, y: Float, z: Float) {
            cylinder(x, y + 0.9f, z, 0.32f, 1.8f, 0.40f, 0.22f, 0.08f)
            sphere(x, y + 2.05f, z, 1.25f, 0.82f, 0.95f, 0.10f, 0.45f, 0.18f)
            sphere(x + 0.18f, y + 2.55f, z - 0.08f, 0.88f, 0.68f, 0.76f, 0.08f, 0.36f, 0.13f)
            sphere(x - 0.28f, y + 2.82f, z + 0.06f, 0.72f, 0.60f, 0.70f, 0.12f, 0.50f, 0.20f)
            sphere(x + 0.28f, y + 2.95f, z + 0.02f, 0.62f, 0.55f, 0.62f, 0.10f, 0.42f, 0.16f)
        }

        private fun stone(x: Float, y: Float, z: Float, sx: Float, sy: Float, sz: Float) {
            sphere(x, y + sy, z, sx, sy, sz, 0.34f, 0.36f, 0.32f)
        }

        private fun drawMesh(
            mesh: Mesh,
            x: Float, y: Float, z: Float,
            sx: Float, sy: Float, sz: Float,
            r: Float, g: Float, b: Float
        ) {
            Matrix.setIdentityM(model, 0)
            Matrix.translateM(model, 0, x, y, z)
            if (characterYaw != 0f) Matrix.rotateM(model, 0, characterYaw * 180f / Math.PI.toFloat(), 0f, 1f, 0f)
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
                "attribute vec4 aPosition; uniform mat4 uMvp; varying float vLight; void main(){vec3 n=normalize(aPosition.xyz); vec3 lightDir=normalize(vec3(-0.45,0.80,0.55)); vLight=0.70+0.30*max(0.0,dot(n,lightDir)); gl_Position=uMvp*aPosition;}"
            )
            val fragment = compile(
                GLES20.GL_FRAGMENT_SHADER,
                "precision mediump float; uniform vec4 uColor; varying float vLight; void main(){gl_FragColor=vec4(uColor.rgb*vLight,uColor.a);}"
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
