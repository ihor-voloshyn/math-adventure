package com.mathadventure.core.curriculum

enum class SkillDomain {
    NUMBERS, ADDITION, SUBTRACTION, MULTIPLICATION, DIVISION, FRACTIONS,
    MONEY, TIME, MEASUREMENT, WORD_PROBLEMS, LOGIC, DATA, PREALGEBRA
}

enum class DependencyType { REQUIRED, RELATED }

data class SkillDefinition(
    val id: String,
    val domain: SkillDomain,
    val name: String
)

data class SkillDependency(
    val fromSkillId: String,
    val toSkillId: String,
    val type: DependencyType
)

class Curriculum(
    val skills: List<SkillDefinition>,
    val dependencies: List<SkillDependency>
) {
    private val byId = skills.associateBy { it.id }

    init {
        require(skills.map { it.id }.distinct().size == skills.size) { "Skill IDs must be unique" }
        dependencies.forEach {
            require(byId.containsKey(it.fromSkillId) && byId.containsKey(it.toSkillId)) {
                "Dependency references unknown skill"
            }
        }
        require(!hasRequiredCycle()) { "REQUIRED dependency graph must be acyclic" }
    }

    fun getSkill(skillId: String): SkillDefinition? = byId[skillId]

    fun requiredPrerequisites(skillId: String): List<String> =
        dependencies.filter { it.toSkillId == skillId && it.type == DependencyType.REQUIRED }
            .map { it.fromSkillId }

    fun relatedSkills(skillId: String): List<String> =
        dependencies.filter {
            it.type == DependencyType.RELATED &&
                (it.fromSkillId == skillId || it.toSkillId == skillId)
        }.map { if (it.fromSkillId == skillId) it.toSkillId else it.fromSkillId }

    private fun hasRequiredCycle(): Boolean {
        val state = mutableMapOf<String, Int>()
        fun visit(id: String): Boolean {
            when (state[id]) {
                1 -> return true
                2 -> return false
            }
            state[id] = 1
            requiredPrerequisites(id).forEach { if (visit(it)) return true }
            state[id] = 2
            return false
        }
        return skills.any { visit(it.id) }
    }

    companion object {
        fun mvp(): Curriculum {
            val s = listOf(
                SkillDefinition("NUM_COUNT_FORWARD", SkillDomain.NUMBERS, "Прямой счёт"),
                SkillDefinition("NUM_COUNT_BACKWARD", SkillDomain.NUMBERS, "Обратный счёт"),
                SkillDefinition("NUM_COMPARE", SkillDomain.NUMBERS, "Сравнение чисел"),
                SkillDefinition("NUM_NUMBER_LINE", SkillDomain.NUMBERS, "Числовая прямая"),
                SkillDefinition("ADD_BASIC", SkillDomain.ADDITION, "Базовое сложение"),
                SkillDefinition("ADD_CROSS_TEN", SkillDomain.ADDITION, "Сложение с переходом через десяток"),
                SkillDefinition("ADD_MULTI_DIGIT", SkillDomain.ADDITION, "Многозначное сложение"),
                SkillDefinition("ADD_UNKNOWN_ADDEND", SkillDomain.ADDITION, "Неизвестное слагаемое"),
                SkillDefinition("SUB_BASIC", SkillDomain.SUBTRACTION, "Базовое вычитание"),
                SkillDefinition("SUB_CROSS_TEN", SkillDomain.SUBTRACTION, "Вычитание с переходом через десяток"),
                SkillDefinition("SUB_MULTI_DIGIT", SkillDomain.SUBTRACTION, "Многозначное вычитание"),
                SkillDefinition("MUL_EQUAL_GROUPS", SkillDomain.MULTIPLICATION, "Равные группы"),
                SkillDefinition("MUL_TABLE", SkillDomain.MULTIPLICATION, "Таблица умножения 2–12"),
                SkillDefinition("MUL_MULTI_DIGIT", SkillDomain.MULTIPLICATION, "Умножение многозначного числа"),
                SkillDefinition("DIV_EXACT", SkillDomain.DIVISION, "Точное деление"),
                SkillDefinition("DIV_REMAINDER", SkillDomain.DIVISION, "Деление с остатком"),
                SkillDefinition("DIV_MULTI_DIGIT", SkillDomain.DIVISION, "Многозначное деление"),
                SkillDefinition("FRACTION_PART_OF_WHOLE", SkillDomain.FRACTIONS, "Часть целого"),
                SkillDefinition("FRACTION_SIMPLE", SkillDomain.FRACTIONS, "Простые дроби"),
                SkillDefinition("MONEY_BASIC", SkillDomain.MONEY, "Денежные значения"),
                SkillDefinition("MONEY_PURCHASE", SkillDomain.MONEY, "Покупка"),
                SkillDefinition("MONEY_CHANGE", SkillDomain.MONEY, "Сдача"),
                SkillDefinition("TIME_BASIC", SkillDomain.TIME, "Единицы и определение времени"),
                SkillDefinition("TIME_DURATION", SkillDomain.TIME, "Продолжительность"),
                SkillDefinition("MEASURE_LENGTH", SkillDomain.MEASUREMENT, "Длина"),
                SkillDefinition("MEASURE_MASS", SkillDomain.MEASUREMENT, "Масса"),
                SkillDefinition("MEASURE_VOLUME", SkillDomain.MEASUREMENT, "Объём"),
                SkillDefinition("WORD_ONE_STEP", SkillDomain.WORD_PROBLEMS, "Одношаговые текстовые задачи"),
                SkillDefinition("WORD_TWO_STEP", SkillDomain.WORD_PROBLEMS, "Двухшаговые текстовые задачи"),
                SkillDefinition("LOGIC_SEQUENCE", SkillDomain.LOGIC, "Закономерности и последовательности")
            )
            val d = listOf(
                req("NUM_COUNT_FORWARD", "NUM_COMPARE"),
                req("NUM_COMPARE", "ADD_BASIC"),
                req("ADD_BASIC", "ADD_CROSS_TEN"),
                req("ADD_CROSS_TEN", "ADD_MULTI_DIGIT"),
                req("ADD_BASIC", "ADD_UNKNOWN_ADDEND"),
                req("ADD_BASIC", "SUB_BASIC"),
                req("SUB_BASIC", "SUB_CROSS_TEN"),
                req("SUB_CROSS_TEN", "SUB_MULTI_DIGIT"),
                req("ADD_BASIC", "MUL_EQUAL_GROUPS"),
                req("MUL_EQUAL_GROUPS", "MUL_TABLE"),
                req("MUL_TABLE", "MUL_MULTI_DIGIT"),
                req("MUL_TABLE", "DIV_EXACT"),
                req("DIV_EXACT", "DIV_REMAINDER"),
                req("DIV_REMAINDER", "DIV_MULTI_DIGIT"),
                req("DIV_EXACT", "FRACTION_SIMPLE"),
                req("FRACTION_PART_OF_WHOLE", "FRACTION_SIMPLE"),
                req("ADD_BASIC", "MONEY_PURCHASE"),
                req("MONEY_PURCHASE", "MONEY_CHANGE"),
                req("TIME_BASIC", "TIME_DURATION"),
                req("WORD_ONE_STEP", "WORD_TWO_STEP"),
                req("ADD_BASIC", "WORD_ONE_STEP"),
                req("SUB_BASIC", "WORD_ONE_STEP")
            )
            return Curriculum(s, d + listOf(
                SkillDependency("MONEY_BASIC", "ADD_BASIC", DependencyType.RELATED),
                SkillDependency("TIME_BASIC", "MEASURE_LENGTH", DependencyType.RELATED),
                SkillDependency("MUL_TABLE", "DIV_EXACT", DependencyType.RELATED)
            ))
        }

        private fun req(from: String, to: String) =
            SkillDependency(from, to, DependencyType.REQUIRED)
    }
}
