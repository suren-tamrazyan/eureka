package game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PlayerOfc extends Player {
	private static final long serialVersionUID = 1L;

	public class CardBox {
		private List<Card> box = new ArrayList<Card>();
		private int limit;
		
		public CardBox(int capacity) {
			this.limit = capacity;
		}
		
		public void addCard(Card card) throws GameException {
			if (limit == box.size()) 
				throw new GameException("Incorrect state");
			box.add(card);
		}
		
		@Override
		public String toString() {
			return box.stream().map(Object::toString).collect(Collectors.joining("")) + String.join("", Collections.nCopies(limit-box.size(), "."));
		}
		
		public List<Card> toList() {
			return new ArrayList<Card>(box);
		}
		
		public boolean isFull() {
			return box.size() == limit;
		}
	}
	
	public PlayerOfc(String name, int stack) {
		super(name, stack);
	}

	public PlayerOfc(String name, int stack, boolean fantasy) {
		super(name, stack);
		playFantasy = fantasy;
	}
	
	public PlayerOfc(PlayerOfc source) {
		super(source.name, source.stack);
		playFantasy = source.playFantasy;
		fantasyCardCount = source.fantasyCardCount;
		cardsToBeBoxed.addAll(source.cardsToBeBoxed);
		resultOppScore.putAll(source.resultOppScore);
		try {
			for (Card card : source.boxFront.toList()) boxFront.addCard(card);
			for (Card card : source.boxMiddle.toList()) boxMiddle.addCard(card);
			for (Card card : source.boxBack.toList()) boxBack.addCard(card);
			for (Card card : source.boxDead.toList()) boxDead.addCard(card);
		} catch (GameException e) {
			e.printStackTrace();
		} 
	}
	
	public CardBox boxFront = new CardBox(3);
	public CardBox boxMiddle = new CardBox(5);
	public CardBox boxBack = new CardBox(5);
	public CardBox boxDead = new CardBox(4);
	public List<Card> cardsToBeBoxed = new ArrayList<Card>();
	public Map<String, GameOfc.Score> resultOppScore = new HashMap<>();
	
	public boolean playFantasy = false;
	public int fantasyCardCount = -1;
	
	public boolean isDealer(String dealerName) {
		return name.equals(dealerName);
	}
	
	public boolean isHero(String heroName) {
		return name.equals(heroName);
	}
	
	public boolean boxesIsFull() {
		return boxFront.isFull() && boxMiddle.isFull() && boxBack.isFull();
	}

	public GameOfc.Score getSumScore() {
		GameOfc.Score sumScore = new GameOfc.Score(0, 0, 0, 0, 0);
		for (GameOfc.Score sc : resultOppScore.values()) {
			sumScore.front += sc.front;
			sumScore.middle += sc.middle;
			sumScore.back += sc.back;
			sumScore.allwin += sc.allwin;
			sumScore.profit += sc.profit;
			sumScore.rake += sc.rake;
		}
		return sumScore;
	}
	
	@Override
	public String toString() {
		String strDeal = cardsToBeBoxed.stream().map(Object::toString).collect(Collectors.joining(""));
		return String.format("%-15s %7d F:%-6s M:%-10s B:%-10s D:%-8s DL:%-10s", name, stack, boxFront, boxMiddle, boxBack, boxDead, strDeal);
	}
}
