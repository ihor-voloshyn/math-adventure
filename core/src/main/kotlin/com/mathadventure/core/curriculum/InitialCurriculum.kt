package com.mathadventure.core.curriculum

object InitialCurriculum {
    val graph = CurriculumGraph(
        listOf(
            SkillDefinition("numbers", "numbers"),
            SkillDefinition("addition", "addition", setOf("numbers")),
            SkillDefinition("subtraction", "subtraction", setOf("numbers", "addition")),
            SkillDefinition("multiplication", "multiplication", setOf("addition", "subtraction")),
            SkillDefinition("division", "division", setOf("multiplication", "subtraction")),
            SkillDefinition("fractions", "fractions", setOf("division")),
            SkillDefinition("money", "money", setOf("addition", "subtraction")),
            SkillDefinition("time", "time", setOf("numbers", "addition", "subtraction")),
            SkillDefinition("measurement", "measurement", setOf("numbers", "addition", "subtraction")),
            SkillDefinition("word_problems", "word_problems", setOf("addition", "subtraction")),
            SkillDefinition("logic", "logic"),
            SkillDefinition("data", "data", setOf("numbers")),
            SkillDefinition("pre_algebra", "pre_algebra", setOf("addition", "subtraction", "multiplication", "division"))
        )
    )
}
