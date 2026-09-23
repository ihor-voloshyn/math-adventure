package com.mathadventure.core.generator

import com.mathadventure.core.model.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DeterministicTaskGeneratorTest {
    private val generator = DeterministicTaskGenerator()
    private val blueprint = TaskBlueprint("ADD_CROSS_TEN", TaskMode.DIRECT, 3, "SHOP", InputType.NUMERIC)
    @Test fun sameBlueprintProducesSameTask() = assertEquals(generator.generate(blueprint), generator.generate(blueprint))
    @Test fun battleTaskIndexChangesQuestion() {
        val first = generator.generate(blueprint.copy(constraints = mapOf("taskIndex" to "0")))
        val second = generator.generate(blueprint.copy(constraints = mapOf("taskIndex" to "1")))
        assertTrue(first.taskId != second.taskId)
        assertTrue(first.prompt != second.prompt)
        assertTrue(first.answerSpec != second.answerSpec)
    }

    @Test fun generatorPreservesAdaptiveConstraints() {
        val task = generator.generate(blueprint)
        assertEquals("ADD_CROSS_TEN", task.skillId)
        assertEquals(3, task.difficulty)
        assertEquals(TaskMode.DIRECT, task.mode)
        assertEquals("SHOP", task.contextType)
        assertTrue(task.generationMetadata["seed"]!!.isNotBlank())
    }
}
