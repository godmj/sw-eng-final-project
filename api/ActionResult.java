package api;

/**
 * 캐릭터가 행동한 결과. 게임은 이 값을 읽어 화면에 출력한다.
 * 캐릭터는 문자열을 만들어 출력하지 않고, 사실(값)만 담아서 돌려준다.
 */
public class ActionResult {

    private final String actorName;
    private final String targetName;     // 대상이 없으면 null (예: 방어)
    private final String actionName;     // "공격", "방어", 스킬 이름 등
    private final int amount;            // 실제 데미지 또는 회복량
    private final boolean heal;          // true면 amount는 회복량
    private final boolean hit;           // 명중 여부
    private final boolean critical;      // 치명타 여부
    private final StatusEffect appliedEffect;  // 걸린 상태이상, 없으면 null

    public ActionResult(String actorName, String targetName, String actionName,
                        int amount, boolean heal, boolean hit, boolean critical,
                        StatusEffect appliedEffect) {
        this.actorName = actorName;
        this.targetName = targetName;
        this.actionName = actionName;
        this.amount = amount;
        this.heal = heal;
        this.hit = hit;
        this.critical = critical;
        this.appliedEffect = appliedEffect;
    }

    /** 공격 결과 (명중했으면 실제 데미지, 빗나갔으면 hit=false, amount=0). */
    public static ActionResult attack(String actor, String target, String action,
                                      int damage, boolean hit, boolean critical,
                                      StatusEffect applied) {
        return new ActionResult(actor, target, action, damage, false, hit, critical, applied);
    }

    /** 회복 결과. */
    public static ActionResult heal(String actor, String target, String action, int healed) {
        return new ActionResult(actor, target, action, healed, true, true, false, null);
    }

    /** 방어 결과. */
    public static ActionResult defend(String actor) {
        return new ActionResult(actor, null, "방어", 0, false, true, false, null);
    }

    public String getActorName() {
        return actorName;
    }

    public String getTargetName() {
        return targetName;
    }

    public String getActionName() {
        return actionName;
    }

    public int getAmount() {
        return amount;
    }

    public boolean isHeal() {
        return heal;
    }

    public boolean isHit() {
        return hit;
    }

    public boolean isCritical() {
        return critical;
    }

    public StatusEffect getAppliedEffect() {
        return appliedEffect;
    }
}
