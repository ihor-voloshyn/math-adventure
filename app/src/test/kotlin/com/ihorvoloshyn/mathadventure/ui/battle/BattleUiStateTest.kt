package com.ihorvoloshyn.mathadventure.ui.battle
import com.mathadventure.core.combat.*
import kotlin.test.*
class BattleUiStateTest {
 private val engine=CombatEngine();private val machine=BattleStateMachine()
 @Test fun successfulAttackProducesResolvedState(){val combat=engine.start("combat-1",3,3);val started=machine.attackStarted(machine.ready(combat),1);val outcome=engine.resolveMathAction(combat,CombatAction.ATTACK,true,1);val state=machine.resolve(started,outcome.state,outcome.resolution);assertIs<BattleUiState.AttackResolved>(state);assertEquals(CombatResolution.HIT,state.resolution);assertEquals(2,state.combat.enemyHp)}
 @Test fun victoryCanTransitionToRewarded(){val combat=engine.start("combat-1",3,1);val started=machine.attackStarted(machine.ready(combat),1);val outcome=engine.resolveMathAction(combat,CombatAction.ATTACK,true,1);val resolved=machine.resolve(started,outcome.state,outcome.resolution);val rewarded=machine.reward(resolved);assertIs<BattleUiState.Rewarded>(rewarded);assertEquals(CombatResolution.VICTORY,rewarded.resolution)}
}