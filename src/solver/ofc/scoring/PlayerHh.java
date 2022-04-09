package solver.ofc.scoring;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.json.JSONObject;

import game.Card;
import game.EventOfc;
import game.GameOfc;

public class PlayerHh implements Comparable<PlayerHh> {

	private String pid;
	private boolean inFantasy;
	private int orderIndex;
	private boolean hero;
	private List<Map<Card, Integer>> roundCards;
	
	public String getPid() {
		return pid;
	}

	public boolean isInFantasy() {
		return inFantasy;
	}

	public int getOrderIndex() {
		return orderIndex;
	}

	public boolean isHero() {
		return hero;
	}

	public PlayerHh(JSONObject jo) throws Exception {
		pid = jo.getString("playerId");
		inFantasy = jo.getBoolean("inFantasy");
		orderIndex = jo.getInt("orderIndex");
		hero = jo.getBoolean("hero");
		roundCards = new ArrayList<>(inFantasy?1:5);
		roundCards.add(new HashMap<>());
		if (!inFantasy)
			for (int i = 1; i < 5; i++)
				roundCards.add(new HashMap<>());
		String strDead = jo.getString("dead");
		String[] rows = jo.getString("rows").split("/");
		
		if (strDead != null && !"".equals(strDead))
			Arrays.stream(strDead.split("\\s")).forEach(s -> roundCards.get(getRound(s)).put(getCard(s), GameOfc.BOX_LEVEL_DEAD));
		Arrays.stream(rows[0].split("\\s")).forEach(s -> roundCards.get(getRound(s)).put(getCard(s), GameOfc.BOX_LEVEL_FRONT));
		Arrays.stream(rows[1].split("\\s")).forEach(s -> roundCards.get(getRound(s)).put(getCard(s), GameOfc.BOX_LEVEL_MIDDLE));
		Arrays.stream(rows[2].split("\\s")).forEach(s -> roundCards.get(getRound(s)).put(getCard(s), GameOfc.BOX_LEVEL_BACK));
	}
	
	private int getRound(String str) {
		if (inFantasy) return 0;
		return Integer.parseInt(Character.toString(str.charAt(2)));
	}
	
	private Card getCard(String str) {
		return Card.str2Cards( str.substring(0, 2))[0];
	}

	@Override
	public int compareTo(PlayerHh o) {
		if (this.getOrderIndex() == 0)
			return 1;
		if (o.getOrderIndex() == 0)
			return -1;
		return this.getOrderIndex() - o.getOrderIndex();
	}
	
	public List<EventOfc> getEvents(int round, boolean includeHeroMove) {
		return getEvents(round, includeHeroMove, hero);
	}
	public List<EventOfc> getEvents(int round, boolean includeHeroMove, boolean asHiro) {
		List<EventOfc> result = new ArrayList<>();
		if (inFantasy && round > 0) return result;
		if (asHiro)
			result.add(new EventOfc(EventOfc.TYPE_DEAL_CARDS, pid, Card.cards2Mask(roundCards.get(round).keySet().toArray(new Card[0]))));
		
		int evType = EventOfc.PUT_CARDS_TO_BOXES;
		if (inFantasy)
			evType = EventOfc.FANTASY_CARDS_TO_BOXES;
		if (!asHiro || includeHeroMove) {
			Card[] cardsToFront = roundCards.get(round).entrySet().stream().filter(entry -> entry.getValue() == GameOfc.BOX_LEVEL_FRONT).map(ent -> ent.getKey()).toArray(Card[]::new);
			Card[] cardsToMiddle = roundCards.get(round).entrySet().stream().filter(entry -> entry.getValue() == GameOfc.BOX_LEVEL_MIDDLE).map(ent -> ent.getKey()).toArray(Card[]::new);
			Card[] cardsToBack = roundCards.get(round).entrySet().stream().filter(entry -> entry.getValue() == GameOfc.BOX_LEVEL_BACK).map(ent -> ent.getKey()).toArray(Card[]::new);
			long maskToFront = Card.cards2Mask(cardsToFront);
			long maskToMiddle = Card.cards2Mask(cardsToMiddle);
			long maskToBack = Card.cards2Mask(cardsToBack);
			List<Card> lstToDead = roundCards.get(round).entrySet().stream().filter(entry -> entry.getValue() == GameOfc.BOX_LEVEL_DEAD).map(ent -> ent.getKey()).collect(Collectors.toList());
			if (!asHiro)
				lstToDead = new ArrayList<>(); // empty list, for game rules
			result.add(new EventOfc(evType, pid, maskToFront, maskToMiddle, maskToBack, lstToDead));
		}
		
		return result;
	}
	
	public List<EventOfc> getShowDeadsEvent() {
		return getShowDeadsEvent(hero);
	}
	public List<EventOfc> getShowDeadsEvent(boolean asHero) {
		List<EventOfc> result = new ArrayList<>();
		if (asHero) return result;
		List<Card> lstToDead = roundCards.stream().flatMap(map -> map.entrySet().stream().filter(entry -> entry.getValue() == GameOfc.BOX_LEVEL_DEAD).map(ent -> ent.getKey())).collect(Collectors.toList());
		result.add(new EventOfc(EventOfc.SHOW_DEAD_CARDS, pid, 0, 0, 0, lstToDead));
		return result;
	}
	
	public static void main(String[] args) {
		String str = Arrays.toString("8d0 8s0 Th0/2c0 3h0 4d0 5h0 6d0/9d0 Tc0 Jh0 Qc0 Kd0".split("/"));
		System.out.println(str);
	}
}
