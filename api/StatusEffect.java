package api;

/**
 * 상태이상.
 * 효과의 내용(얼마나 아픈지, 명중을 얼마나 깎는지)은 캐릭터가 구현하고,
 * 누구에게 걸렸는지 보관하고 턴마다 적용·감소시키는 것은 게임이 한다.
 *
 *  - 독·화상: getDamagePerTurn() > 0 → 게임이 턴 시작 시 takeTrueDamage로 적용
 *  - 실명·어지러움: getHitModifier() < 0 → 게임이 대상의 ActionContext 명중 보정값에 더함
 *  - 속도를 바꾸는 효과는 두지 않는다 (턴 순서 재정렬 방지).
 */
public interface StatusEffect {

    /** 종류를 구분하는 이름. 같은 이름이면 중첩하지 않고 지속시간만 갱신한다. */
    String getName();

    /** 남은 턴 수. */
    int getRemainingTurns();

    /** 턴이 하나 지났을 때 게임이 호출한다. */
    void tick();

    /** 같은 종류가 다시 걸리면 게임이 호출해 지속시간을 갱신한다. */
    void refresh(int turns);

    /** 매 턴 받는 방어 무시 피해. 없으면 0. */
    int getDamagePerTurn();

    /** 명중 보정값(퍼센트 정수). 명중을 깎는 효과면 음수, 없으면 0. */
    int getHitModifier();

    default boolean isExpired() {
        return getRemainingTurns() <= 0;
    }
}
