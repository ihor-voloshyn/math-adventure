package com.mathadventure.core.validation

import com.mathadventure.core.contracts.*
import com.mathadventure.core.generator.DeterministicTaskGenerator
import com.mathadventure.core.math.BasicMathEngine
import com.mathadventure.core.model.*
import kotlin.test.Test
import kotlin.test.assertEquals

class GeneratedTaskValidationTest {
    private val mathEngine = BasicMathEngine()

    @Test
    fun generatedAdditionTaskPassesAllLayers() {
        val task = DeterministicTaskGenerator().generate(
            TaskBlueprint("ADD_CROSS_TEN", TaskMode.DIRECT, 3, "SHOP", InputType.NUMERIC)
        )
        assertEquals(
            ValidationResult.Valid,
            TaskValidationPipeline(
                StructuralTaskValidator(),
                LogicalTaskValidator(),
                MathematicalTaskValidator(mathEngine)
            ).validate(task)
        )
    }

    @Test
    fun mathematicallyIncorrectAnswerFailsAtMathematicalLayer() {
        val task = TaskInstance(
            "task-invalid-answer",
            "ADD_BASIC",
            TaskMode.DIRECT,
            1,
            "TEST",
            "12 + 5 = ?",
            InputType.NUMERIC,
            "18"
        )
        val result = TaskValidationPipeline(
            StructuralTaskValidator(),
            LogicalTaskValidator(),
            MathematicalTaskValidator(mathEngine)
        ).validate(task)

        assertEquals(ValidationLayer.MATHEMATICAL, (result as ValidationResult.Invalid).layer)
        assertEquals("INCORRECT_EXPECTED_ANSWER", result.reason)
    }

    @Test
    fun malformedTaskStopsAtStructuralLayer() {
        val task = TaskInstance("", "ADD_BASIC", TaskMode.DIRECT, 1, "TEST", "2+3=?", InputType.NUMERIC, "5")
        val result = TaskValidationPipeline(
            StructuralTaskValidator(),
            LogicalTaskValidator(),
            MathematicalTaskValidator(mathEngine)
        ).validate(task)
        assertEquals(ValidationLayer.STRUCTURAL, (result as ValidationResult.Invalid).layer)
    }

    @Test
    fun unsupportedMathFormFailsAtMathematicalLayer() {
        val task = TaskInstance(
            "task-unsupported",
            "ADD_BASIC",
            TaskMode.DIRECT,
            1,
            "TEST",
            "Solve this task.",
            InputType.NUMERIC,
            "5"
        )
        val result = TaskValidationPipeline(
            StructuralTaskValidator(),
            LogicalTaskValidator(),
            MathematicalTaskValidator(mathEngine)
        ).validate(task)

        assertEquals(ValidationLayer.MATHEMATICAL, (result as ValidationResult.Invalid).layer)
        assertEquals("UNSUPPORTED_MATHEMATICAL_FORM", result.reason)
    }
}
