package com.ihorvoloshyn.mathadventure.ui.math

import com.mathadventure.core.math.AttemptEvaluation
import com.mathadventure.core.model.AnswerResult
import com.mathadventure.core.model.TaskInstance

sealed interface MathTaskUiState {
    val task: TaskInstance
    val taskIndex: Int
    val totalTasks: Int

    data class Idle(
        override val task: TaskInstance,
        override val taskIndex: Int,
        override val totalTasks: Int
    ) : MathTaskUiState

    data class Selected(
        override val task: TaskInstance,
        override val taskIndex: Int,
        override val totalTasks: Int,
        val selectedAnswer: String
    ) : MathTaskUiState

    data class Correct(
        override val task: TaskInstance,
        override val taskIndex: Int,
        override val totalTasks: Int,
        val selectedAnswer: String,
        val evaluation: AttemptEvaluation
    ) : MathTaskUiState {
        init {
            require(evaluation.result == AnswerResult.CORRECT) {
                "Correct state requires a CORRECT evaluation"
            }
        }
    }

    data class Incorrect(
        override val task: TaskInstance,
        override val taskIndex: Int,
        override val totalTasks: Int,
        val selectedAnswer: String,
        val evaluation: AttemptEvaluation
    ) : MathTaskUiState {
        init {
            require(evaluation.result == AnswerResult.INCORRECT) {
                "Incorrect state requires an INCORRECT evaluation"
            }
        }
    }

    data class Skipped(
        override val task: TaskInstance,
        override val taskIndex: Int,
        override val totalTasks: Int,
        val evaluation: AttemptEvaluation
    ) : MathTaskUiState {
        init {
            require(evaluation.result == AnswerResult.SKIPPED) {
                "Skipped state requires a SKIPPED evaluation"
            }
        }
    }
}

class MathTaskStateMachine {
    fun present(task: TaskInstance, taskIndex: Int, totalTasks: Int): MathTaskUiState =
        MathTaskUiState.Idle(task, taskIndex, totalTasks)

    fun select(state: MathTaskUiState, answer: String): MathTaskUiState.Selected {
        require(answer.isNotBlank()) { "answer is required" }
        return MathTaskUiState.Selected(
            task = state.task,
            taskIndex = state.taskIndex,
            totalTasks = state.totalTasks,
            selectedAnswer = answer
        )
    }

    fun resolve(
        state: MathTaskUiState.Selected,
        evaluation: AttemptEvaluation
    ): MathTaskUiState {
        return when (evaluation.result) {
            AnswerResult.CORRECT -> MathTaskUiState.Correct(
                task = state.task,
                taskIndex = state.taskIndex,
                totalTasks = state.totalTasks,
                selectedAnswer = state.selectedAnswer,
                evaluation = evaluation
            )
            AnswerResult.INCORRECT -> MathTaskUiState.Incorrect(
                task = state.task,
                taskIndex = state.taskIndex,
                totalTasks = state.totalTasks,
                selectedAnswer = state.selectedAnswer,
                evaluation = evaluation
            )
            AnswerResult.SKIPPED -> MathTaskUiState.Skipped(
                task = state.task,
                taskIndex = state.taskIndex,
                totalTasks = state.totalTasks,
                evaluation = evaluation
            )
        }
    }
}
