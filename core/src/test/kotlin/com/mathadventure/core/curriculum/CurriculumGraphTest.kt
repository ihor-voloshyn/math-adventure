package com.mathadventure.core.curriculum

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class CurriculumGraphTest {
    @Test
    fun canonicalFoundationDependenciesArePresent() {
        assertEquals(setOf("numbers"), InitialCurriculum.graph.prerequisitesOf("addition"))
        assertEquals(
            setOf("addition", "subtraction"),
            InitialCurriculum.graph.prerequisitesOf("multiplication")
        )
        assertEquals(
            setOf("multiplication", "subtraction"),
            InitialCurriculum.graph.prerequisitesOf("division")
        )
    }

    @Test
    fun unknownPrerequisiteIsRejected() {
        assertFailsWith<IllegalArgumentException> {
            CurriculumGraph(
                listOf(
                    SkillDefinition("a", "numbers", setOf("missing"))
                )
            )
        }
    }
}
