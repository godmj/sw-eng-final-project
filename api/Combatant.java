package api;

import java.util.List;

/**
 * 게임(신민지)과 캐릭터(정찬호)가 만나는 유일한 약속(인터페이스).
 * 게임은 이 타입으로만 플레이어와 적을 다룬다.
 *
 * 규칙
 *  - 캐릭터는 println 하지 않는다. 결과는 ActionResult 값으로 돌려준다.
 *  - 난수는 ActionContext의 Random만 사용한다 (시드 고정 테스트를 위해).
 *  - 이 파일은 두 사람이 합의한 뒤에만 수정한다 (이슈·PR로 기록).
 */
public interface Combatant {

    // ---- 조회 (화면 출력·턴 순서용) ----
    String getName();
    boolean isAlive();
    int getHp();
    int getMaxHp();
    int getSpeed();        // 턴 순서 2순위
    int getPriority();     // 선공 등급, 높을수록 먼저 (턴 순서 1순위)

    // ---- 턴 진행 ----
    /** 자기 턴이 시작될 때 게임이 호출한다. 방어 해제, 쿨다운 감소 등을 처리한다. */
    void onTurnStart();

    // ---- 행동 ----
    /** 기본 공격. 명중·치명타 판정과 데미지 적용까지 캐릭터가 하고 결과를 돌려준다. */
    ActionResult attack(Combatant target, ActionContext ctx);

    /** 방어. 다음 자기 턴 시작(onTurnStart)에 해제된다. */
    ActionResult defend();

    /** 사용 가능한 스킬 목록 (이름, 대상 종류, 사용 가능 여부). */
    List<SkillInfo> getSkills();

    /** index번째 스킬 사용. 사용 불가면 usable=false인 스킬은 게임이 고르지 않는다. */
    ActionResult useSkill(int index, Combatant target, ActionContext ctx);

    // ---- 피해 받기 ----
    /** 방어를 적용해 피해를 받고, 실제로 깎인 양을 돌려준다. */
    int takeDamage(int amount);

    /** 방어를 무시하는 피해 (독·화상 등 지속 피해). 실제로 깎인 양을 돌려준다. */
    int takeTrueDamage(int amount);
}
