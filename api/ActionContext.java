package api;

import java.util.Random;

/**
 * 게임이 행동할 때마다 캐릭터에게 넘기는 정보 묶음.
 * 단계가 늘어 필요한 값이 생겨도 Combatant 메서드를 고치지 않고 여기에 항목만 추가한다.
 */
public class ActionContext {

    private final int hitModifier;   // 명중 보정값 (퍼센트 정수, 음수면 명중 감소)
    private final Random random;     // 게임이 만든 난수 (시드 고정 가능)

    public ActionContext(int hitModifier, Random random) {
        this.hitModifier = hitModifier;
        this.random = random;
    }

    public int getHitModifier() {
        return hitModifier;
    }

    public Random getRandom() {
        return random;
    }
}
