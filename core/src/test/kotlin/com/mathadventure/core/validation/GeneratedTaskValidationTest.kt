package com.mathadventure.core.validation

import com.mathadventure.core.contracts.*
import com.mathadventure.core.generator.DeterministicTaskGenerator
import com.mathadventure.core.model.*
import kotlin.test.Test
import kotlin.test.assertEquals

class GeneratedTaskValidationTest {
    @Test fun generatedAdditionTaskPassesAllLayers() {
        val task = DeterministicTaskGenerator().generate(TaskBlueprint("ADD_CROSS_TEN", TaskMode.DIRECT, 3, "SHOP", InputType.NUMERIC))
        assertEquals(ValidationResult.Valid, TaskValidationPipeline(StructuralTaskValidator(), LogicalTaskValidator(), TaskValidator { ValidationResult.Valid }).validate(task))
    }
    @Test fun malformedTaskStopsAtStructuralLayer() {
        val task = TaskInstance("", "ADD_BASIC", TaskMode.DIRECT, 1, "TEST", "2+3=?", InputType.NUMERIC, "5")
        val result = TaskValidationPipeline(StructuralTaskValidator(), LogicalTaskValidator(), TaskValidator { ValidationResult.Valid }).validate(task)
        assertEquals(ValidationLayer.STRUCTURAL, (result as ValidationResult.Invalid).layer)
    }
}
