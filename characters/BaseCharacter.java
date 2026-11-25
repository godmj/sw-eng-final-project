package characters;

import java.util.ArrayList;
import java.util.List;
import api.*;

public abstract class BaseCharacter implements Combatant {
	private String name;
	private int maxHp;
	private int currentHp;
	private int attackPower;
	private int speed;
	
	public BaseCharacter(String n, int mh, int ap, int s) {
		name = n;
		maxHp = mh;
		currentHp = mh;
		attackPower = ap;
		speed = s;
		
	}
	public String getName() {
		return name;
	}
	public int getHp() {
		return currentHp;
	}
	public int getMaxHp() {
		return maxHp;
	}
	public int getSpeed() {
		return speed;
	}
	public int getPriority() {
		return 0;
	}
	
	public boolean isAlive() {
		return currentHp > 0;
	}
	
    public int takeDamage(int amount) {
        // ① 실제로 깎일 양 정하기: amount와 currentHp 중 작은 쪽
        
    	int actual = Math.min(amount, currentHp);

        // ② HP에서 실제 양만큼 빼기
        currentHp = currentHp - actual;

        // ③ 실제 양 돌려주기
        return actual;
    }
    
    public void onTurnStart() {
        // 2단계(방어 해제), 5단계(쿨다운 감소)에서 채움
    }

    public ActionResult defend() {
        return new ActionResult(0, true);   // 2단계에서 채움
    }

    public List<SkillInfo> getSkills() {
        return new ArrayList<>();           // 빈 목록 = 스킬 없음
    }

    public ActionResult useSkill(int index, Combatant target, ActionContext ctx) {
        return new ActionResult(0, false);  // 2단계에서 채움
    }
    
    public int takeTrueDamage(int amount) {
    	int actual = Math.min(amount, currentHp);
    	
    	currentHp = currentHp - actual;
    	
    	return actual;
    }
    
    public ActionResult attack(Combatant target, ActionContext ctx) {
        // 1) 내 공격력을 데미지로
        int damage = attackPower;

        // 2) 상대에게 데미지를 넣고, 실제로 들어간 양을 받기
        int actual = target.takeDamage(damage);

        // 3) 결과 상자에 담아서 돌려주기 (실제 데미지, 명중했음)
        return new ActionResult(actual, true);
        			
    }
}
