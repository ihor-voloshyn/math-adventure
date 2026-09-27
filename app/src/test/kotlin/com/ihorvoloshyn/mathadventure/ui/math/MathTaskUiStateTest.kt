package com.ihorvoloshyn.mathadventure.ui.math
import com.mathadventure.core.math.AttemptEvaluation
import com.mathadventure.core.model.*
import kotlin.test.*
class MathTaskUiStateTest {
 private val task=TaskInstance("task-1","ADD_BASIC",TaskMode.DIRECT,1,"BATTLE","7 + 5 = ?",InputType.SELECTION,"12","COUNT ON FROM 7");private val machine=MathTaskStateMachine()
 @Test fun presentStartsInIdle(){val state=machine.present(task,2,5);assertTrue(state is MathTaskUiState.Idle);assertEquals(2,state.taskIndex);assertEquals(5,state.totalTasks)}
 @Test fun selectingAnswerDoesNotEvaluateIt(){assertEquals("12",machine.select(machine.present(task,2,5),"12").selectedAnswer)}
 @Test fun correctEvaluationProducesCorrectState(){val state=machine.resolve(machine.select(machine.present(task,2,5),"12"),AttemptEvaluation(AnswerResult.CORRECT));assertIs<MathTaskUiState.Correct>(state);assertEquals("12",state.selectedAnswer)}
 @Test fun incorrectEvaluationProducesIncorrectState(){val state=machine.resolve(machine.select(machine.present(task,2,5),"11"),AttemptEvaluation(AnswerResult.INCORRECT));assertIs<MathTaskUiState.Incorrect>(state);assertEquals("11",state.selectedAnswer)}
 @Test fun skippedEvaluationProducesSkippedState(){val state=machine.resolve(machine.select(machine.present(task,2,5),"12"),AttemptEvaluation(AnswerResult.SKIPPED));assertIs<MathTaskUiState.Skipped>(state);assertEquals(AnswerResult.SKIPPED,state.evaluation.result)}
}