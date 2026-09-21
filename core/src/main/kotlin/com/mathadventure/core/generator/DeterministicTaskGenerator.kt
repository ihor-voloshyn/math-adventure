package com.mathadventure.core.generator

import com.mathadventure.core.contracts.TaskGenerator
import com.mathadventure.core.model.TaskBlueprint
import com.mathadventure.core.model.TaskInstance
import java.security.MessageDigest

class DeterministicTaskGenerator(private val generatorVersion: String = "1.0") : TaskGenerator {
    override fun generate(blueprint: TaskBlueprint): TaskInstance {
        require(blueprint.skillId.isNotBlank()) { "skillId is required" }
        require(blueprint.difficulty >= 1) { "difficulty must be positive" }
        val seed = stableSeed(blueprint)
        return when (blueprint.skillId) {
            "ADD_BASIC" -> additionTask(blueprint, seed, false)
            "ADD_CROSS_TEN" -> additionTask(blueprint, seed, true)
            else -> genericTask(blueprint, seed)
        }
    }
    private fun additionTask(b: TaskBlueprint, seed: Long, crossTen: Boolean): TaskInstance {
        val a = if (crossTen) 27 else 12
        val c = if (crossTen) 18 else 5
        return TaskInstance("task-" + seed.toString(16), b.skillId, b.mode, b.difficulty, b.contextType,
            "$a + $c = ?", b.inputType, (a + c).toString(),
            generationMetadata = mapOf("generatorVersion" to generatorVersion, "seed" to seed.toString()))
    }
    private fun genericTask(b: TaskBlueprint, seed: Long) = TaskInstance(
        "task-" + seed.toString(16), b.skillId, b.mode, b.difficulty, b.contextType,
        "Solve the task for skill " + b.skillId + ".", b.inputType, "UNSUPPORTED",
        generationMetadata = mapOf("generatorVersion" to generatorVersion, "seed" to seed.toString()))
    private fun stableSeed(b: TaskBlueprint): Long {
        val canonical = listOf(b.skillId, b.mode.name, b.difficulty.toString(), b.contextType, b.inputType.name,
            b.constraints.toSortedMap().entries.joinToString("|") { e -> e.key + "=" + e.value }).joinToString("|")
        val digest = MessageDigest.getInstance("SHA-256").digest(canonical.toByteArray())
        var value = 0L
        for (i in 0 until 8) value = (value shl 8) or (digest[i].toLong() and 0xff)
        return value
    }
}
