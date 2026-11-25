package characters;

import java.util.Random;
import api.*;

public class CharacterTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Warrior w = new Warrior();
		Dragon d = new Dragon();
		
		ActionContext ctx = new ActionContext(0, new Random(42));
		
		ActionResult r = w.attack(d, ctx);
		System.out.println(w.getName() + "->" + d.getName() + " : " + r.getAmount() + "데미지, 남은HP" + " " + d.getHp());
		
		r = d.attack(w, ctx);
		System.out.println(d.getName() + "->" + w.getName() + " : " + r.getAmount() + "데미지, 남은HP" + " " + w.getHp());

	}

}
