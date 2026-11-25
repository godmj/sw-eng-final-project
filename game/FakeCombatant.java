package game;

import api.ActionContext;
import api.ActionResult;
import api.Combatant;
import api.SkillInfo;
import api.TargetType;

import java.util.ArrayList;
import java.util.List;

/**
 * 정찬호의 캐릭터가 완성되기 전에 전투 엔진을 먼저 테스트하기 위한 가짜 캐릭터.
 * 규칙은 일부러 단순하게 했다. 실제 데미지·치명타 규칙은 characters/ 에서 만든다.
 */
public class FakeCombatant implements Combatant {

    private final String name;
    private final int maxHp;
    private final int speed;
    private final int priority;
    private final int attackPower;
    private int hp;
    private boolean defending;

    public FakeCombatant(String name, int maxHp, int speed, int priority, int attackPower) {
        this.name = name;
        this.maxHp = maxHp;
        this.hp = maxHp;
        this.speed = speed;
        this.priority = priority;
        this.attackPower = attackPower;
    }

    @Override public String getName() { return name; }
    @Override public boolean isAlive() { return hp > 0; }
    @Override public int getHp() { return hp; }
    @Override public int getMaxHp() { return maxHp; }
    @Override public int getSpeed() { return speed; }
    @Override public int getPriority() { return priority; }

    @Override
    public void onTurnStart() {
        defending = false;
    }

    @Override
    public ActionResult attack(Combatant target, ActionContext ctx) {
        // 기본 명중률 90%에 게임이 준 보정값을 더한다.
        boolean hit = ctx.getRandom().nextInt(100) < 90 + ctx.getHitModifier();
        if (!hit) {
            return ActionResult.attack(name, target.getName(), "공격", 0, false, false, null);
        }
        int dealt = target.takeDamage(attackPower);
        return ActionResult.attack(name, target.getName(), "공격", dealt, true, false, null);
    }

    @Override
    public ActionResult defend() {
        defending = true;
        return ActionResult.defend(name);
    }

    @Override
    public List<SkillInfo> getSkills() {
        List<SkillInfo> skills = new ArrayList<>();
        skills.add(new SkillInfo("강타", TargetType.ENEMY, true));
        return skills;
    }

    @Override
    public ActionResult useSkill(int index, Combatant target, ActionContext ctx) {
        int dealt = target.takeDamage(attackPower * 2);
        return ActionResult.attack(name, target.getName(), "강타", dealt, true, false, null);
    }

    @Override
    public int takeDamage(int amount) {
        int real = defending ? amount / 2 : amount;
        return takeTrueDamage(real);
    }

    @Override
    public int takeTrueDamage(int amount) {
        int real = Math.min(amount, hp);
        hp -= real;
        return real;
    }

    @Override
    public int heal(int amount) {
        int real = Math.min(amount, maxHp - hp);
        hp += real;
        return real;
    }
}
