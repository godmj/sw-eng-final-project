package game;

import api.Combatant;

import java.util.List;
import java.util.Random;

/**
 * 스테이지 3개를 이어서 진행하는 게임 흐름.
 * 플레이어 파티는 스테이지 사이에 그대로 이어진다(HP 유지). 한 번이라도 지면 끝.
 * 마지막 스테이지의 적이 왕(드래곤)이다.
 */
public class Game {

    public static final int MAX_ROUNDS = 30;

    private final List<Combatant> players;
    private final List<List<Combatant>> stages;
    private final ActionChooser playerChooser;
    private final ActionChooser enemyChooser;
    private final ConsolePrinter printer;
    private final Random random;

    public Game(List<Combatant> players, List<List<Combatant>> stages,
                ActionChooser playerChooser, ActionChooser enemyChooser,
                ConsolePrinter printer, Random random) {
        this.players = players;
        this.stages = stages;
        this.playerChooser = playerChooser;
        this.enemyChooser = enemyChooser;
        this.printer = printer;
        this.random = random;
    }

    /** 끝까지 진행해서 클리어했으면 true. */
    public boolean run() {
        for (int i = 0; i < stages.size(); i++) {
            List<Combatant> enemies = stages.get(i);
            printer.printStageStart(i + 1, stages.size(), enemies);
            BattleEngine engine = new BattleEngine(players, enemies,
                    playerChooser, enemyChooser, printer, random, MAX_ROUNDS);
            BattleOutcome outcome = engine.run();
            if (outcome != BattleOutcome.PLAYER_WIN) {
                printer.printMessage("\nGAME OVER");
                return false;
            }
        }
        printer.printMessage("\n모든 스테이지 클리어! 왕을 쓰러뜨렸다!");
        return true;
    }
}
