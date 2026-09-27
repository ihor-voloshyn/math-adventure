package com.ihorvoloshyn.mathadventure.ui.math

import com.mathadventure.core.math.AttemptEvaluation
import com.mathadventure.core.model.AnswerResult
import com.mathadventure.core.model.InputType
import com.mathadventure.core.model.TaskInstance
import com.mathadventure.core.model.TaskMode
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.Test

class MathTaskUiStateTest {
    private val task = TaskInstance(
        taskId = "task-1",
        skillId = "ADD_BASIC",
        mode = TaskMode.DIRECT,
        difficulty = 1,
        contextType = "BATTLE",
        prompt = "7 + 5 = ?",
        inputType = InputType.SELECTION,
        answerSpec = "12",
        hintSpec = "COUNT ON FROM 7"
    )

    private val machine = MathTaskStateMachine()

    @Test
    fun presentStartsInIdle() {
        val state = assertInstanceOf<MathTaskUiState.Idle>(machine.present(task, 2, 5))
        assertEquals(2, state.taskIndex)
        assertEquals(5, state.totalTasks)
    }

    @Test
    fun selectingAnswerDoesNotEvaluateIt() {
        val state = machine.select(machine.present(task, 2, 5), "12")
        assertEquals("12", state.selectedAnswer)
    }

    @Test
    fun correctEvaluationProducesCorrectState() {
        val selected = machine.select(machine.present(task, 2, 5), "12")
        val state = assertInstanceOf<MathTaskUiState.Correct>(
            machine.resolve(selected, AttemptEvaluation(AnswerResult.CORRECT))
        )
        assertEquals("12", state.selectedAnswer)
    }

    @Test
    fun incorrectEvaluationProducesIncorrectState() {
        val selected = machine.select(machine.present(task, 2, 5), "11")
        val state = assertInstanceOf<MathTaskUiState.Incorrect>(
            machine.resolve(selected, AttemptEvaluation(AnswerResult.INCORRECT))
        )
        assertEquals("11", state.selectedAnswer)
    }

    @Test
    fun skippedEvaluationProducesSkippedState() {
        val selected = machine.select(machine.present(task, 2, 5), "12")
        val state = assertInstanceOf<MathTaskUiState.Skipped>(
            machine.resolve(selected, AttemptEvaluation(AnswerResult.SKIPPED))
        )
        assertEquals(AnswerResult.SKIPPED, state.evaluation.result)
    }
}
