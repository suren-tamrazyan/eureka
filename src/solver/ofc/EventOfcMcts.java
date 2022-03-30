package solver.ofc;

import java.util.List;
import game.Card;
import game.EventOfc;
import game.GameOfc;

public class EventOfcMcts extends EventOfc {

	public EventOfcMcts(int type, String who, long iVal) {
		super();
		this.type = type;
		this.iVal = iVal;
		this.who = who;
	}


	public EventOfcMcts(int type, String who, List<Card> putToFront, List<Card> putToMiddle, List<Card> putToBack, List<Card> putToDead) {
//		putToFront.stream().forEach((x) -> card2box.put(x, GameOfc.BOX_LEVEL_FRONT));
//		putToMiddle.stream().forEach((x) -> card2box.put(x, GameOfc.BOX_LEVEL_MIDDLE));
//		putToBack.stream().forEach((x) -> card2box.put(x, GameOfc.BOX_LEVEL_BACK));
//		putToDead.stream().forEach((x) -> card2box.put(x, GameOfc.BOX_LEVEL_DEAD));
		for (Card x : putToFront)  card2box.put(x, GameOfc.BOX_LEVEL_FRONT);
		for (Card x : putToMiddle) card2box.put(x, GameOfc.BOX_LEVEL_MIDDLE);
		for (Card x : putToBack) card2box.put(x, GameOfc.BOX_LEVEL_BACK);
		for (Card x : putToDead) card2box.put(x, GameOfc.BOX_LEVEL_DEAD);
		this.type = type;
		this.who = who;
	
	}
	
	public void setTime() {
		createTime = Utils.getTime();
	}

}
