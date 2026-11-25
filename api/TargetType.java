package api;

/** 스킬이 누구를 대상으로 하는지. 게임이 대상 선택 메뉴를 만들 때 쓴다. */
public enum TargetType {
    ENEMY,   // 적 1명
    ALLY,    // 아군 1명
    SELF     // 자신
}
