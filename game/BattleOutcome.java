package game;

/** 전투가 어떻게 끝났는지. */
public enum BattleOutcome {
    PLAYER_WIN,   // 적 전멸
    ENEMY_WIN,    // 플레이어 전멸
    DRAW          // 라운드 상한에 도달했고 HP 비율이 같음
}
