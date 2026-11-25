package game;

import api.Combatant;

import java.util.List;
import java.util.Random;

/** 적 행동 선택. 살아 있는 상대 중 무작위로 하나를 기본 공격한다. */
public class SimpleEnemyAI implements ActionChooser {

    private final Random random;

    public SimpleEnemyAI(Random random) {
        this.random = random;
    }

    @Override
    public Decision choose(Combatant actor, List<Combatant> allies, List<Combatant> enemies) {
        List<Combatant> alive = ConsoleChooser.aliveOf(enemies);
        return Decision.attack(alive.get(random.nextInt(alive.size())));
    }
}
