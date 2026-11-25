package game;

import api.Combatant;
import api.SkillInfo;
import api.TargetType;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 적 행동 선택. 쓸 수 있는 공격 스킬이 있으면 그것을 쓰고,
 * 없으면 살아 있는 상대 중 무작위로 하나를 기본 공격한다. 적은 방어하지 않는다.
 */
public class SimpleEnemyAI implements ActionChooser {

    private final Random random;

    public SimpleEnemyAI(Random random) {
        this.random = random;
    }

    @Override
    public Decision choose(Combatant actor, List<Combatant> allies, List<Combatant> enemies) {
        List<Combatant> alive = ConsoleChooser.aliveOf(enemies);
        Combatant target = alive.get(random.nextInt(alive.size()));

        List<SkillInfo> skills = actor.getSkills();
        List<Integer> usable = new ArrayList<>();
        for (int i = 0; i < skills.size(); i++) {
            SkillInfo s = skills.get(i);
            if (s.isUsable() && s.getTargetType() == TargetType.ENEMY) {
                usable.add(i);
            }
        }
        if (!usable.isEmpty()) {
            return Decision.skill(usable.get(random.nextInt(usable.size())), target);
        }
        return Decision.attack(target);
    }
}
