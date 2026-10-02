package com.mathadventure.core.quest

object FirstQuestChain {
    val definitions: List<QuestDefinition> = listOf(
        QuestDefinition(
            id = "story_home_to_village",
            titleKey = "quest.home_to_village.title",
            descriptionKey = "quest.home_to_village.description",
            type = QuestType.STORY,
            objectives = listOf(QuestObjectiveDefinition("visit_village", "quest.objective.visit_village"))
        ),
        QuestDefinition(
            id = "story_village_to_forest",
            titleKey = "quest.village_to_forest.title",
            descriptionKey = "quest.village_to_forest.description",
            type = QuestType.STORY,
            objectives = listOf(
                QuestObjectiveDefinition("talk_to_npc", "quest.objective.talk_to_npc"),
                QuestObjectiveDefinition("reach_forest", "quest.objective.reach_forest")
            ),
            prerequisites = setOf("story_home_to_village")
        ),
        QuestDefinition(
            id = "story_first_battle",
            titleKey = "quest.first_battle.title",
            descriptionKey = "quest.first_battle.description",
            type = QuestType.COMBAT,
            objectives = listOf(QuestObjectiveDefinition("win_first_battle", "quest.objective.win_first_battle"))
        ),
        QuestDefinition(
            id = "story_return_home",
            titleKey = "quest.return_home.title",
            descriptionKey = "quest.return_home.description",
            type = QuestType.HOME_TERRITORY,
            objectives = listOf(QuestObjectiveDefinition("return_home", "quest.objective.return_home")),
            prerequisites = setOf("story_first_battle")
        )
    )
}
