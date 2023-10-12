package solver.ofc;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import game.*;
import org.paukov.combinatorics3.Generator;

import solver.mcts.MctsDomainState;

public class GameOfcMcts extends GameOfc implements MctsDomainState<EventOfcMcts, AgentOfcMcts> {
	private static final long serialVersionUID = 1L;
	
//	private static Map<GameOfcMcts, List<EventOfcMcts>> availableActionsForCurrentStep = new HashMap<>();
	public List<EventOfcMcts> availableActionsForAgent;
	public List<EventOfcMcts> availableActionsForNature;
	private Nature nature;
	private AgentOfcMcts agent;
	boolean currentStepForNature;

	public GameOfcMcts(Nw network, int bb) {
		super(network, bb);
		nature = new Nature();
		agent = new AgentOfcMcts();
		currentStepForNature = false;
	}
	
	public GameOfcMcts(GameOfc source) {
		super(source.network, source.bb);
		nature = new Nature();
		agent = new AgentOfcMcts();
		currentStepForNature = false;
		
		this.type = source.type;
		this.id = source.id;
		this.tableId = source.tableId;
		this.date = source.date;
		this.heroName = source.heroName;
		this.buttonName = source.buttonName;
//		this.curMovePlayerInd = source.curMovePlayerInd;
		this.mcount = source.mcount;
		
		this.gameMode = source.gameMode;
		this.round = source.getRound();
		this.isReviewedPlayers = source.isReviewedPlayers;
		this.isRemoveHero = source.isRemoveHero;
		this.finish = source.isFinished();
		this.timeHeroFantasyDelt = source.timeHeroFantasyDelt;
		this.allowEmptyDeadBox = source.isAllowEmptyDeadBox();
		this.skipCheckFLCardCount = source.isSkipCheckFLCardCount();
		
		this.players = new Player[source.players.length];
		this.allPlayers = new Player[source.players.length];
		for (int i = 0; i < source.players.length; i++) {
			this.players[i] = new PlayerOfc((PlayerOfc) source.players[i]);
			this.allPlayers[i] = this.players[i];
		}
		
		
		this.curMovePlayerInd = getPlayerInd(this.heroName);
//		if (this.curMovePlayerInd != this.getPlayerInd(this.heroName))
//			throw new IllegalArgumentException("Not hero move");
		
	}
	
	private List<EventOfcMcts> getAvailableActionsForCurrentStep() {
//		if (!availableActionsForCurrentStep.containsKey(this))
		
//		if (availableActionsForCurrentStep == null)
//			initAvailableActionsForCurrentStep();
//		return availableActionsForCurrentStep;
		
//		return availableActionsForCurrentStep.get(this);
//		List<EventOfcMcts> tmpavailableActionsForCurrentStep = availableActionsForCurrentStep;
//		availableActionsForCurrentStep = null;
//		return tmpavailableActionsForCurrentStep;
		
		if (currentStepForNature) {
			if (availableActionsForNature == null)
				initAvailableActionsForNature();
			return availableActionsForNature;
		} else {
			if (availableActionsForAgent == null)
				initAvailableActionsForAgent();
			return availableActionsForAgent;
		}
	}
	
	private void resetCache() {
		availableActionsForAgent = null;
		availableActionsForNature = null;
	}
	
	@Override
	public void beforeCloning() {
		resetCache();
	}

	public List<Card> getAvailableCards() {
		int deckSize = (gameMode == GameMode.GAME_MODE_OFC_WILD_CARD_REGULAR || gameMode == GameMode.GAME_MODE_OFC_WILD_CARD_PROGRESSIVE || gameMode == GameMode.GAME_MODE_OFC_WILD_CARD_ULTIMATE) ? 54 : 52;
		List<Card> deck = new ArrayList<>(deckSize);
		for (int i = 0; i < deckSize; i++)
			deck.add(Card.getCard(i));
		for (PlayerOfc p : getPlayers()) {
			deck.removeAll(p.boxBack.toList());
			deck.removeAll(p.boxMiddle.toList());
			deck.removeAll(p.boxFront.toList());
			deck.removeAll(p.boxDead.toList());
			deck.removeAll(p.cardsToBeBoxed);
		}
		
		return deck;
	}
	
	protected void initAvailableActionsForNature() {
		List<Card> availableCards = getAvailableCards();
//			availableActionsForCurrentStep.put(this, Generator.combination(availableCards).simple(3).stream().map(x -> new EventOfcMcts(EventOfc.TYPE_DEAL_CARDS, this.heroName, Card.cards2Mask(x.toArray(new Card[0])))).collect(Collectors.toList()));
		availableActionsForNature = Generator.combination(availableCards).simple(3).stream().map(x -> new EventOfcMcts(EventOfc.TYPE_DEAL_CARDS, this.heroName, Card.cards2Mask(x.toArray(new Card[0])))).collect(Collectors.toList());
	}
	
	protected void initAvailableActionsForAgent() {
		List<EventOfcMcts> lAvailableActionsForCurrentStep = new ArrayList<>();
		int frontCount = hero().boxFront.toList().size();
		int middleCount = hero().boxMiddle.toList().size();
		int backCount = hero().boxBack.toList().size();
		int deadCount = 0;
		int[] counter = {frontCount, middleCount, backCount, deadCount};
		int[] limits = {3, 5, 5, 1};
		int boxesNum = this.isFirstRound() ? 3: 4; // for first round without dead box
		// 0 - front, 1 - middle; 2 - back, 3 - dead;
		for (int c0 = 0; c0 < boxesNum; c0++) {
			if (counter[c0] >= limits[c0]) continue;
			counter[c0]++;
			for (int c1 = 0; c1 < boxesNum; c1++) {
				if (counter[c1] >= limits[c1]) continue;
				counter[c1]++;
				for (int c2 = 0; c2 < boxesNum; c2++) {
					if (counter[c2] >= limits[c2]) continue;
					counter[c2]++;
					
					if (this.isFirstRound()) {
						for (int c3 = 0; c3 < boxesNum; c3++) {
							if (counter[c3] >= limits[c3]) continue;
							counter[c3]++;
							for (int c4 = 0; c4 < boxesNum; c4++) {
								if (counter[c4] >= limits[c4]) continue;
								counter[c4]++;
								
								List<Card> lstFront = new ArrayList<>();
								List<Card> lstMiddle = new ArrayList<>();
								List<Card> lstBack = new ArrayList<>();
								List<Card> lstDead = new ArrayList<>();
								Object[] arr = new Object[4];
								arr[0] = lstFront;
								arr[1] = lstMiddle;
								arr[2] = lstBack;
								arr[3] = lstDead;
								
								((List<Card>) arr[c0]).add(hero().cardsToBeBoxed.get(0));
								((List<Card>) arr[c1]).add(hero().cardsToBeBoxed.get(1));
								((List<Card>) arr[c2]).add(hero().cardsToBeBoxed.get(2));
								((List<Card>) arr[c3]).add(hero().cardsToBeBoxed.get(3));
								((List<Card>) arr[c4]).add(hero().cardsToBeBoxed.get(4));
								
								lAvailableActionsForCurrentStep.add(new EventOfcMcts(EventOfc.PUT_CARDS_TO_BOXES, this.heroName, lstFront, lstMiddle, lstBack, lstDead));
								
								counter[c4]--;
							}
							counter[c3]--;
						}
					} else {
						List<Card> lstFront = new ArrayList<>();
						List<Card> lstMiddle = new ArrayList<>();
						List<Card> lstBack = new ArrayList<>();
						List<Card> lstDead = new ArrayList<>();
						Object[] arr = new Object[4];
						arr[0] = lstFront;
						arr[1] = lstMiddle;
						arr[2] = lstBack;
						arr[3] = lstDead;
						
						((List<Card>) arr[c0]).add(hero().cardsToBeBoxed.get(0));
						((List<Card>) arr[c1]).add(hero().cardsToBeBoxed.get(1));
						((List<Card>) arr[c2]).add(hero().cardsToBeBoxed.get(2));
						
						if (!lstDead.isEmpty()) {
							lAvailableActionsForCurrentStep.add(new EventOfcMcts(EventOfc.PUT_CARDS_TO_BOXES, this.heroName, lstFront, lstMiddle, lstBack, lstDead));
						}
					}
					
					counter[c2]--;
				}
				counter[c1]--;
			}
			counter[c0]--;
		}
		availableActionsForAgent = lAvailableActionsForCurrentStep;
	}
	
	public PlayerOfc hero() {
		return getPlayer(heroName);
	}

	@Override
	public boolean isTerminal() {
		return hero().boxesIsFull();
	}

	@Override
	public AgentOfcMcts getCurrentAgent() {
		if (currentStepForNature)
			return nature;
		else
			return agent;
	}

	@Override
	public AgentOfcMcts getPreviousAgent() {
		if (currentStepForNature)
			return agent;
		else
			return nature;
	}

	@Override
	public int getNumberOfAvailableActionsForCurrentAgent() {
		return getAvailableActionsForCurrentStep().size();
	}

	@Override
	public List<EventOfcMcts> getAvailableActionsForCurrentAgent() {
		return new ArrayList<>(getAvailableActionsForCurrentStep());
	}

	@Override
	public MctsDomainState<EventOfcMcts, AgentOfcMcts> performActionForCurrentAgent(EventOfcMcts action) {
		resetCache();
		int curround = getRound();
		try {
			procEvent(action);
			// always a hero's move
			curMovePlayerInd = getPlayerInd(heroName);
			if (!isLastRound() && curround == getRound() && action.type == EventOfc.PUT_CARDS_TO_BOXES)
				nextRound();
		} catch (Exception e) {
			throw new IllegalArgumentException(String.format("Exception: %s", e.getMessage()), e);
		}
		currentStepForNature = !currentStepForNature;
		return this;
	}

	@Override
	public MctsDomainState<EventOfcMcts, AgentOfcMcts> skipCurrentAgent() {
		return this;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (currentStepForNature ? 1231 : 1237);
		result = prime * result + round;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GameOfcMcts other = (GameOfcMcts) obj;
		if (currentStepForNature != other.currentStepForNature)
			return false;
		if (round != other.round)
			return false;
		return true;
	}

	public static void main(String[] args) {
//		int deckSize = 52;
//		List<Card> deck = new ArrayList<>(deckSize);
//		for (int i = 0; i < deckSize; i++)
//			deck.add(Card.getCard(i));
//
//		List<Card> blacklist = new ArrayList<>(6);
//		for (int i = 0; i < 6; i++)
//			blacklist.add(Card.getCard(i));
//		
//		Stream<List<Card>> all = Generator.combination(deck).simple(3).stream();//.collect(Collectors.toList());
//		long timeBefor = Misc.getTime();
//		List<EventOfcMcts> result = all.filter(x -> {for (Card c : blacklist) {if (x.contains(c)) return false;} return true;}).map(x -> new EventOfcMcts(EventOfc.TYPE_DEAL_CARDS, "xxx", Card.cards2Mask(x.toArray(new Card[0])))).collect(Collectors.toList());
//    	System.out.println(Misc.getTime() - timeBefor);
//    	System.out.println(result.size());
		
		int deckSize = 52;
		List<Card> deck = new ArrayList<>(deckSize);
		for (int i = 0; i < deckSize; i++)
			deck.add(Card.getCard(i));

		List<Card> blacklist = new ArrayList<>(6);
		for (int i = 0; i < 6; i++)
			blacklist.add(Card.getCard(i));
		
		List<Set<Card>> all = Generator.combination(deck).simple(3).stream().map(x -> new HashSet<Card>(x)).filter(x -> {for (Card c : blacklist) {if (x.contains(c)) return false;} return true;}).collect(Collectors.toList());
		long timeBefor = Utils.getTime();
//		List<EventOfcMcts> result = new ArrayList<>();
//		for ()
		List<EventOfcMcts> result = all.stream().map(x -> new EventOfcMcts(EventOfc.TYPE_DEAL_CARDS, "xxx", Card.cards2Mask(x.toArray(new Card[0])))).collect(Collectors.toList());
    	System.out.println(Utils.getTime() - timeBefor);
    	System.out.println(result.size());
		
	}

	@Override
	public boolean currentAgentActionsIsOrderedMode() {
		return false;
	}

	public String getStateStr() {
		PlayerOfc hero = getPlayer(heroName);
		return hero.boxFront + "-" + hero.boxMiddle + "-" + hero.boxBack;
	}



	public void merge(EventOfcMctsSimple eventToFinal) throws GameException {
		PlayerOfc hero = hero();
		for (Card card : eventToFinal.front)
			hero.boxFront.addCard(card);
		for (Card card : eventToFinal.middle)
			hero.boxMiddle.addCard(card);
		for (Card card : eventToFinal.back)
			hero.boxBack.addCard(card);
		round = 5;
		finish = true;
	}

	public GameOfcMcts copy() {
		GameOfcMcts clone = new GameOfcMcts(this);
		clone.currentStepForNature = this.currentStepForNature;
		return clone;
	}
}
