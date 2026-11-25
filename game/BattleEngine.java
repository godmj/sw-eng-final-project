package game;

import api.ActionContext;
import api.ActionResult;
import api.Combatant;
import api.StatusEffect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * 한 번의 전투(스테이지 하나)를 진행하는 엔진.
 *
 *  - 턴 순서: priority 내림차순 → speed 내림차순 → 파티 순서. 전투 시작 때 한 번만 정렬한다.
 *  - 모든 참가자를 Combatant 하나로 다룬다 (플레이어/적 구분 없이 한 루프).
 *  - 상태이상은 엔진이 보관하고 턴 시작 때 적용·감소시킨다.
 *  - 출력은 하지 않는다. BattleListener로 알리기만 한다.
 *  - 라운드 상한에 도달하면 양 팀 HP 합 비율을 비교해 승자를 정한다.
 */
public class BattleEngine {

    private final List<Combatant> players;
    private final List<Combatant> enemies;
    private final ActionChooser playerChooser;
    private final ActionChooser enemyChooser;
    private final BattleListener listener;
    private final Random random;
    private final int maxRounds;

    private final List<Combatant> order = new ArrayList<>();
    private final Map<Combatant, List<StatusEffect>> effects = new IdentityHashMap<>();
    private final Set<Combatant> announcedDead = Collections.newSetFromMap(new IdentityHashMap<>());

    public BattleEngine(List<Combatant> players, List<Combatant> enemies,
                        ActionChooser playerChooser, ActionChooser enemyChooser,
                        BattleListener listener, Random random, int maxRounds) {
        this.players = players;
        this.enemies = enemies;
        this.playerChooser = playerChooser;
        this.enemyChooser = enemyChooser;
        this.listener = listener;
        this.random = random;
        this.maxRounds = maxRounds;
    }

    /** 전투를 끝까지 진행하고 결과를 돌려준다. */
    public BattleOutcome run() {
        buildOrder();
        int round = 0;
        while (round < maxRounds) {
            round++;
            listener.onRoundStart(round);
            for (Combatant actor : order) {
                if (!actor.isAlive()) continue;
                takeTurn(actor);
                BattleOutcome done = checkWipeOut();
                if (done != null) {
                    listener.onBattleEnd(done, round);
                    return done;
                }
            }
        }
        BattleOutcome outcome = decideByHpRatio();
        listener.onBattleEnd(outcome, round);
        return outcome;
    }

    // ---- 순서 ----

    private void buildOrder() {
        order.clear();
        order.addAll(players);
        order.addAll(enemies);
        // List.sort는 안정 정렬이라 같은 값이면 위에서 넣은 파티 순서가 유지된다.
        order.sort(Comparator
                .comparingInt(Combatant::getPriority).reversed()
                .thenComparing(Comparator.comparingInt(Combatant::getSpeed).reversed()));
    }

    /** 정렬된 턴 순서 (테스트·화면 표시용 복사본). */
    public List<Combatant> getTurnOrder() {
        if (order.isEmpty()) buildOrder();
        return new ArrayList<>(order);
    }

    // ---- 한 턴 ----

    private void takeTurn(Combatant actor) {
        listener.onTurnStart(actor);
        actor.onTurnStart();

        applyStatusEffects(actor);
        if (!actor.isAlive()) {
            announceDeaths();
            return;
        }

        boolean isPlayer = players.contains(actor);
        List<Combatant> allies = isPlayer ? players : enemies;
        List<Combatant> foes = isPlayer ? enemies : players;
        ActionChooser chooser = isPlayer ? playerChooser : enemyChooser;

        Decision decision = chooser.choose(actor, allies, foes);
        ActionContext ctx = new ActionContext(hitModifierOf(actor), random);

        ActionResult result = execute(actor, decision, ctx);
        listener.onAction(result);
        registerEffect(decision.getTarget(), result);
        announceDeaths();
    }

    private void announceDeaths() {
        for (Combatant c : order) {
            if (!c.isAlive() && announcedDead.add(c)) {
                effects.remove(c);
                listener.onDeath(c);
            }
        }
    }

    private ActionResult execute(Combatant actor, Decision d, ActionContext ctx) {
        switch (d.getType()) {
            case DEFEND:
                return actor.defend();
            case SKILL:
                return actor.useSkill(d.getSkillIndex(), d.getTarget(), ctx);
            case ATTACK:
            default:
                return actor.attack(d.getTarget(), ctx);
        }
    }

    // ---- 상태이상 ----

    private void registerEffect(Combatant target, ActionResult result) {
        StatusEffect applied = result.getAppliedEffect();
        if (applied == null || target == null) return;
        List<StatusEffect> list = effects.computeIfAbsent(target, k -> new ArrayList<>());
        for (StatusEffect existing : list) {
            if (existing.getName().equals(applied.getName())) {
                existing.refresh(applied.getRemainingTurns());   // 중첩 없이 지속시간만 갱신
                return;
            }
        }
        list.add(applied);
    }

    private void applyStatusEffects(Combatant owner) {
        List<StatusEffect> list = effects.get(owner);
        if (list == null) return;
        Iterator<StatusEffect> it = list.iterator();
        while (it.hasNext() && owner.isAlive()) {
            StatusEffect e = it.next();
            int dmg = e.getDamagePerTurn();
            if (dmg > 0) {
                int dealt = owner.takeTrueDamage(dmg);
                listener.onEffectDamage(owner, e, dealt);
            }
            e.tick();
            if (e.isExpired()) {
                it.remove();
                listener.onEffectExpired(owner, e);
            }
        }
    }

    private int hitModifierOf(Combatant c) {
        List<StatusEffect> list = effects.get(c);
        if (list == null) return 0;
        int sum = 0;
        for (StatusEffect e : list) sum += e.getHitModifier();
        return sum;
    }

    // ---- 종료 판정 ----

    private BattleOutcome checkWipeOut() {
        boolean playersAlive = anyAlive(players);
        boolean enemiesAlive = anyAlive(enemies);
        if (!enemiesAlive) return BattleOutcome.PLAYER_WIN;
        if (!playersAlive) return BattleOutcome.ENEMY_WIN;
        return null;
    }

    private BattleOutcome decideByHpRatio() {
        double p = hpRatio(players);
        double e = hpRatio(enemies);
        if (p > e) return BattleOutcome.PLAYER_WIN;
        if (p < e) return BattleOutcome.ENEMY_WIN;
        return BattleOutcome.DRAW;
    }

    private static boolean anyAlive(List<Combatant> team) {
        for (Combatant c : team) if (c.isAlive()) return true;
        return false;
    }

    /** 팀의 (현재 HP 합) / (최대 HP 합). */
    private static double hpRatio(List<Combatant> team) {
        int hp = 0, max = 0;
        for (Combatant c : team) {
            hp += c.getHp();
            max += c.getMaxHp();
        }
        return max == 0 ? 0 : (double) hp / max;
    }
}
