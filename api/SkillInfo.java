package api;

/** 게임이 스킬 메뉴를 그릴 때 필요한 정보. 스킬의 실제 효과는 캐릭터 안에 있다. */
public class SkillInfo {

    private final String name;
    private final TargetType targetType;
    private final boolean usable;   // MP 부족, 쿨다운 중이면 false

    public SkillInfo(String name, TargetType targetType, boolean usable) {
        this.name = name;
        this.targetType = targetType;
        this.usable = usable;
    }

    public String getName() {
        return name;
    }

    public TargetType getTargetType() {
        return targetType;
    }

    public boolean isUsable() {
        return usable;
    }
}
