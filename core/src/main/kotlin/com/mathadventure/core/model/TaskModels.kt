package com.mathadventure.core.model

enum class TaskMode {
    DIRECT,
    WORD_PROBLEM,
    UNKNOWN_COMPONENT,
    INVERSE,
    REPRESENTATION
}

enum class InputType {
    NUMERIC,
    SELECTION
}

data class AdaptiveDecision(
    val skillId: String,
    val mode: TaskMode,
    val difficulty: Int,
    val contextType: String,
    val constraints: Map<String, String> = emptyMap()
)

data class TaskBlueprint(
    val skillId: String,
    val mode: TaskMode,
    val difficulty: Int,
    val contextType: String,
    val inputType: InputType,
    val constraints: Map<String, String> = emptyMap()
)

data class TaskInstance(
    val taskId: String,
    val skillId: String,
    val mode: TaskMode,
    val difficulty: Int,
    val contextType: String,
    val prompt: String,
    val inputType: InputType,
    val answerSpec: String,
    val hintSpec: String? = null,
    val generationMetadata: Map<String, String> = emptyMap()
)
