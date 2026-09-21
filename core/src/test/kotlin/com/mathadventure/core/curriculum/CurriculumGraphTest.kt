package com.mathadventure.core.curriculum

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class CurriculumGraphTest {
    @Test
    fun canonicalFoundationDependenciesArePresent() {
        val curriculum = Curriculum.mvp()

        assertEquals(listOf("NUM_COUNT_FORWARD"), curriculum.requiredPrerequisites("NUM_COMPARE"))
        assertEquals(listOf("NUM_COMPARE"), curriculum.requiredPrerequisites("ADD_BASIC"))
        assertEquals(
            listOf("ADD_BASIC", "SUB_BASIC"),
            curriculum.requiredPrerequisites("WORD_ONE_STEP")
        )
    }

    @Test
    fun unknownSkillDependencyIsRejected() {
        assertFailsWith<IllegalArgumentException> {
            Curriculum(
                skills = listOf(
                    SkillDefinition("A", SkillDomain.NUMBERS, "A")
                ),
                dependencies = listOf(
                    SkillDependency("A", "MISSING", DependencyType.REQUIRED)
                )
            )
        }
    }

    @Test
    fun requiredDependencyCycleIsRejected() {
        assertFailsWith<IllegalArgumentException> {
            Curriculum(
                skills = listOf(
                    SkillDefinition("A", SkillDomain.NUMBERS, "A"),
                    SkillDefinition("B", SkillDomain.NUMBERS, "B")
                ),
                dependencies = listOf(
                    SkillDependency("A", "B", DependencyType.REQUIRED),
                    SkillDependency("B", "A", DependencyType.REQUIRED)
                )
            )
        }
    }
}
