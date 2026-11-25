package game;

import api.ActionResult;
import api.Combatant;
import api.StatusEffect;

import java.io.PrintStream;
import java.util.List;

/** BattleListener의 콘솔 구현. 화면 출력은 전부 여기서만 한다. */
public class ConsolePrinter implements BattleListener {

    private final PrintStream out;

    public ConsolePrinter(PrintStream out) {
        this.out = out;
    }

    @Override
    public void onRoundStart(int round) {
        out.println();
        out.println("===== 라운드 " + round + " =====");
    }

    @Override
    public void onTurnStart(Combatant actor) {
        out.println("-- " + actor.getName() + "의 턴 (HP " + actor.getHp() + "/" + actor.getMaxHp() + ")");
    }

    @Override
    public void onAction(ActionResult r) {
        if ("방어".equals(r.getActionName())) {
            out.println(r.getActorName() + ": 방어 자세를 취했다.");
            return;
        }
        if (r.isHeal()) {
            out.println(r.getActorName() + "의 " + r.getActionName() + "! "
                    + r.getTargetName() + " HP +" + r.getAmount());
            return;
        }
        if (!r.isHit()) {
            out.println(r.getActorName() + "의 " + r.getActionName() + "! 빗나갔다.");
            return;
        }
        String crit = r.isCritical() ? " (치명타!)" : "";
        out.println(r.getActorName() + "의 " + r.getActionName() + "! "
                + r.getTargetName() + "에게 " + r.getAmount() + " 피해" + crit);
        if (r.getAppliedEffect() != null) {
            out.println("  → " + r.getTargetName() + "에게 [" + r.getAppliedEffect().getName() + "] 상태이상!");
        }
    }

    @Override
    public void onEffectDamage(Combatant victim, StatusEffect effect, int dealt) {
        out.println("  [" + effect.getName() + "] " + victim.getName() + " " + dealt + " 피해");
    }

    @Override
    public void onEffectExpired(Combatant owner, StatusEffect effect) {
        out.println("  [" + effect.getName() + "] " + owner.getName() + "에게서 효과가 사라졌다.");
    }

    @Override
    public void onDeath(Combatant dead) {
        out.println("*** " + dead.getName() + " 쓰러졌다! ***");
    }

    @Override
    public void onBattleEnd(BattleOutcome outcome, int rounds) {
        out.println();
        switch (outcome) {
            case PLAYER_WIN: out.println("전투 승리! (" + rounds + "라운드)"); break;
            case ENEMY_WIN:  out.println("전투 패배... (" + rounds + "라운드)"); break;
            default:         out.println("무승부 (" + rounds + "라운드)");
        }
    }

    /** 스테이지 시작 안내 (엔진 밖, 게임 흐름에서 호출). */
    public void printStageStart(int stage, int total, List<Combatant> enemies) {
        out.println();
        out.println("########## 스테이지 " + stage + " / " + total + " ##########");
        StringBuilder sb = new StringBuilder("적: ");
        for (int i = 0; i < enemies.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(enemies.get(i).getName()).append("(HP ").append(enemies.get(i).getMaxHp()).append(")");
        }
        out.println(sb);
    }

    public void printMessage(String msg) {
        out.println(msg);
    }
}
