package game;

import api.ActionResult;
import api.Combatant;
import api.StatusEffect;

/** 전투 진행을 화면에 알리는 쪽. 엔진은 출력하지 않고 이 인터페이스만 호출한다. */
public interface BattleListener {
    void onRoundStart(int round);
    void onTurnStart(Combatant actor);
    void onAction(ActionResult result);
    void onEffectDamage(Combatant victim, StatusEffect effect, int dealt);
    void onEffectExpired(Combatant owner, StatusEffect effect);
    void onDeath(Combatant dead);
    void onBattleEnd(BattleOutcome outcome, int rounds);
}
