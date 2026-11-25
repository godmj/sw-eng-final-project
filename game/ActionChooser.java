package game;

import api.Combatant;

import java.util.List;

/**
 * 행동을 고르는 역할. 플레이어는 콘솔 입력, 적은 간단한 AI, 테스트는 자동으로 구현한다.
 * 엔진은 누가 고르는지 모르고 이 인터페이스만 부른다.
 */
public interface ActionChooser {
    Decision choose(Combatant actor, List<Combatant> allies, List<Combatant> enemies);
}
