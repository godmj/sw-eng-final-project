package game;

import api.Combatant;
import api.SkillInfo;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

/** 플레이어 행동을 콘솔에서 입력받는 ActionChooser. 잘못 입력하면 다시 묻는다. */
public class ConsoleChooser implements ActionChooser {

    private final Scanner in;
    private final PrintStream out;

    public ConsoleChooser(Scanner in, PrintStream out) {
        this.in = in;
        this.out = out;
    }

    @Override
    public Decision choose(Combatant actor, List<Combatant> allies, List<Combatant> enemies) {
        try {
            while (true) {
                out.println("행동 선택: 1) 공격  2) 방어  3) 스킬");
                int pick = readInt(1, 3);
                if (pick == 2) return Decision.defend();
                if (pick == 1) {
                    Combatant t = pickTarget("공격 대상", aliveOf(enemies));
                    if (t != null) return Decision.attack(t);
                } else {
                    Decision d = chooseSkill(actor, allies, enemies);
                    if (d != null) return d;
                }
            }
        } catch (NoSuchElementException e) {
            // 입력이 끝났으면(테스트·파이프) 방어로 넘긴다.
            return Decision.defend();
        }
    }

    private Decision chooseSkill(Combatant actor, List<Combatant> allies, List<Combatant> enemies) {
        List<SkillInfo> skills = actor.getSkills();
        if (skills.isEmpty()) {
            out.println("사용할 수 있는 스킬이 없습니다.");
            return null;
        }
        for (int i = 0; i < skills.size(); i++) {
            SkillInfo s = skills.get(i);
            out.println("  " + (i + 1) + ") " + s.getName() + (s.isUsable() ? "" : " (사용 불가)"));
        }
        out.println("  0) 뒤로");
        int pick = readInt(0, skills.size());
        if (pick == 0) return null;
        SkillInfo skill = skills.get(pick - 1);
        if (!skill.isUsable()) {
            out.println("지금은 사용할 수 없는 스킬입니다.");
            return null;
        }
        Combatant target;
        switch (skill.getTargetType()) {
            case SELF:
                target = actor;
                break;
            case ALLY:
                target = pickTarget("대상", aliveOf(allies));
                break;
            case ENEMY:
            default:
                target = pickTarget("대상", aliveOf(enemies));
        }
        return target == null ? null : Decision.skill(pick - 1, target);
    }

    private Combatant pickTarget(String label, List<Combatant> candidates) {
        if (candidates.isEmpty()) return null;
        if (candidates.size() == 1) return candidates.get(0);
        out.println(label + " 선택:");
        for (int i = 0; i < candidates.size(); i++) {
            Combatant c = candidates.get(i);
            out.println("  " + (i + 1) + ") " + c.getName() + " (HP " + c.getHp() + "/" + c.getMaxHp() + ")");
        }
        return candidates.get(readInt(1, candidates.size()) - 1);
    }

    private int readInt(int min, int max) {
        while (true) {
            String line = in.nextLine().trim();
            try {
                int v = Integer.parseInt(line);
                if (v >= min && v <= max) return v;
            } catch (NumberFormatException ignored) {
                // 아래에서 다시 묻는다.
            }
            out.println(min + "~" + max + " 사이의 숫자를 입력하세요.");
        }
    }

    static List<Combatant> aliveOf(List<Combatant> team) {
        List<Combatant> alive = new ArrayList<>();
        for (Combatant c : team) if (c.isAlive()) alive.add(c);
        return alive;
    }
}
