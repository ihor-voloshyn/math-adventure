package com.mathadventure.core.curriculum

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CurriculumTest {
    private val curriculum = Curriculum.mvp()

    @Test
    fun requiredPrerequisitesAreReturned() {
        assertEquals(listOf("ADD_BASIC"), curriculum.requiredPrerequisites("ADD_CROSS_TEN"))
    }

    @Test
    fun requiredGraphIsAcyclic() {
        assertTrue(curriculum.skills.isNotEmpty())
    }

    @Test
    fun relatedDependencyDoesNotBecomePrerequisite() {
        assertEquals(emptyList(), curriculum.requiredPrerequisites("MONEY_BASIC"))
    }
}
