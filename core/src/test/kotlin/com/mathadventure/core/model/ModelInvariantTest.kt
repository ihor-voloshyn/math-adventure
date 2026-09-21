package com.mathadventure.core.model

import kotlin.test.Test
import kotlin.test.assertFailsWith

class ModelInvariantTest {
    @Test
    fun masteryMustStayWithinCanonicalRange() {
        assertFailsWith<IllegalArgumentException> {
            SkillState(skillId = "addition", mastery = 6)
        }
    }
}
