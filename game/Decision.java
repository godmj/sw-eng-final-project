package game;

import api.Combatant;

/** 한 캐릭터가 이번 턴에 무엇을 할지 고른 결과. 입력 담당(콘솔/자동)이 만든다. */
public class Decision {

    public enum Type { ATTACK, DEFEND, SKILL }

    private final Type type;
    private final int skillIndex;
    private final Combatant target;

    private Decision(Type type, int skillIndex, Combatant target) {
        this.type = type;
        this.skillIndex = skillIndex;
        this.target = target;
    }

    public static Decision attack(Combatant target) {
        return new Decision(Type.ATTACK, -1, target);
    }

    public static Decision defend() {
        return new Decision(Type.DEFEND, -1, null);
    }

    public static Decision skill(int skillIndex, Combatant target) {
        return new Decision(Type.SKILL, skillIndex, target);
    }

    public Type getType() { return type; }
    public int getSkillIndex() { return skillIndex; }
    public Combatant getTarget() { return target; }
}
