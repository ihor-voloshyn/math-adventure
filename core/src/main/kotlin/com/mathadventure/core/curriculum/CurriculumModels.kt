package com.mathadventure.core.curriculum

data class SkillDefinition(
    val skillId: String,
    val domainId: String,
    val prerequisites: Set<String> = emptySet()
)

class CurriculumGraph(
    definitions: Collection<SkillDefinition>
) {
    private val byId = definitions.associateBy { it.skillId }

    init {
        require(byId.size == definitions.size) { "skillId values must be unique" }
        byId.values.forEach { skill ->
            require(skill.skillId !in skill.prerequisites) {
                "skill cannot depend on itself: ${skill.skillId}"
            }
            skill.prerequisites.forEach { prerequisite ->
                require(prerequisite in byId) {
                    "unknown prerequisite '$prerequisite' for '${skill.skillId}'"
                }
            }
        }
    }

    fun get(skillId: String): SkillDefinition =
        requireNotNull(byId[skillId]) { "unknown skill: $skillId" }

    fun all(): Collection<SkillDefinition> = byId.values

    fun prerequisitesOf(skillId: String): Set<String> = get(skillId).prerequisites
}
