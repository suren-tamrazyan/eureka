package solver.ofc;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.paukov.combinatorics3.Generator;

import game.Card;
import game.EventOfc;
import game.GameOfc;
import game.PlayerOfc;
import game.GameOfc.GameMode;
import solver.mcts.MctsDomainState;

public class GameOfcMctsSimple implements MctsDomainState<EventOfcMctsSimple, AgentOfcMctsSimple> {
	public List<Card> boxFront = new ArrayList<Card>(3);
	public List<Card> boxMiddle = new ArrayList<Card>(5);
	public List<Card> boxBack = new ArrayList<Card>(5);
	public List<Card> cardsToBeBoxed = new ArrayList<Card>();
	private AgentOfcMctsSimple agent = new AgentOfcMctsSimple();
	public boolean currentStepForNature = false;
	public List<EventOfcMctsSimple> availableActionsForAgent;
//	public List<EventOfcMcts> availableActionsForNature;
	public GameMode gameMode;
	private List<Card> deck;
	public boolean isFirstRound;
	public String heroName;
	private Config cfg;
	
	// common space
	private NatureSpace natureSpace;
	public NatureSpace getNatureSpace() {
		return natureSpace;
	}

	public static int calcDeckSize(GameMode aGameMode) {
		return (aGameMode == GameMode.GAME_MODE_OFC_WILD_CARD_REGULAR || aGameMode == GameMode.GAME_MODE_OFC_WILD_CARD_PROGRESSIVE || aGameMode == GameMode.GAME_MODE_OFC_WILD_CARD_ULTIMATE) ? 54 : 52;
	}
	public GameOfcMctsSimple(List<Card> front, List<Card> middle, List<Card> back, List<Card> toBeBoxed, List<Card> otherOpenedCard, 
			GameMode aGameMode, boolean aIsFirstRound, String aHeroName, NatureSpace aNatureSpace, Config aCfg) {
		boxFront.addAll(front);
		boxMiddle.addAll(middle);
		boxBack.addAll(back);
		cardsToBeBoxed.addAll(toBeBoxed);
		this.gameMode = aGameMode;
		this.isFirstRound = aIsFirstRound;
		heroName = aHeroName;
		this.cfg = aCfg;
		
		this.natureSpace = aNatureSpace;
		
		int deckSize = calcDeckSize(gameMode);
		deck = new ArrayList<>(deckSize);
		for (int i = 0; i < deckSize; i++)
			deck.add(Card.getCard(i));
		deck.removeAll(otherOpenedCard);
		deck.removeAll(front);
		deck.removeAll(middle);
		deck.removeAll(back);
		deck.removeAll(toBeBoxed);
	}
	
	public static List<Card> mergeToOther(GameOfc source) {
		List<Card> other = new ArrayList<>();
		for (PlayerOfc p : source.getPlayers()) {
			other.addAll(p.boxBack.toList());
			other.addAll(p.boxMiddle.toList());
			other.addAll(p.boxFront.toList());
			other.addAll(p.boxDead.toList());
			other.addAll(p.cardsToBeBoxed);
		}
		return other;
	}
	public GameOfcMctsSimple(GameOfc source, NatureSpace aNatureSpace) {
		this(source.getPlayer(source.heroName).boxFront.toList(), source.getPlayer(source.heroName).boxMiddle.toList(), source.getPlayer(source.heroName).boxBack.toList(),
				source.getPlayer(source.heroName).cardsToBeBoxed, mergeToOther(source), source.gameMode, source.isFirstRound(), source.heroName, aNatureSpace, new Config());
	}

	public GameOfcMctsSimple() {
	}

	@Override
	public GameOfcMctsSimple clone() {
		GameOfcMctsSimple result = new GameOfcMctsSimple();
		result.boxFront.addAll(boxFront);
		result.boxMiddle.addAll(boxMiddle);
		result.boxBack.addAll(boxBack);
		result.cardsToBeBoxed.addAll(cardsToBeBoxed);
//		agent = new AgentOfcMctsSimple();
		result.currentStepForNature = currentStepForNature;
		if (availableActionsForAgent == null)
			result.availableActionsForAgent = null;
		else 
			result.availableActionsForAgent = new ArrayList<>(availableActionsForAgent);
		result.gameMode = gameMode;
		result.deck = new ArrayList<>(deck);
		result.isFirstRound = isFirstRound;
		result.heroName = heroName;
		result.natureSpace = natureSpace;
		return result;
	}

	private List<EventOfcMctsSimple> getAvailableActionsForCurrentStep() {
		if (currentStepForNature) {
//			if (availableActionsForNature == null)
//				initAvailableActionsForNature();
//			return availableActionsForNature;
			return getNatureSpace().natureSamples;
		} else {
			if (availableActionsForAgent == null)
				initAvailableActionsForAgent();
			return availableActionsForAgent;
		}
	}
	
	private void resetCache() {
		availableActionsForAgent = null;
//		availableActionsForNature = null;
	}
	
	@Override
	public void beforeCloning() {
		resetCache();
	}

	@Override
	public double getExplorationParameter() {
		return 0;
	}

//	public List<Card> getAvailableCards() {
//		List<Card> result = new ArrayList<>(this.deck);
//		result.removeAll(boxBack);
//		result.removeAll(boxMiddle);
//		result.removeAll(boxFront);
//		result.removeAll(cardsToBeBoxed);
//
//		return result;
//	}
	
//	protected void initAvailableActionsForNature() {
//		long timeBefore = Misc.getTime();
//		OfcMctsTest.availableInitCount++;
//		OfcMctsTest.availableInitCountNature++;
//		
//		List<Card> availableCards = getAvailableCards();
//		int dealSize = 13 - (boxFront.size() + boxMiddle.size() + boxBack.size());
//		if (dealSize > 2) {
//			availableActionsForNature = new ArrayList<EventOfcMcts>(RANDOM_DEAL_COUNT);
//			for (int i = 0; i < RANDOM_DEAL_COUNT; i++) {
//				Collections.shuffle(availableCards);
//				availableActionsForNature.add(new EventOfcMcts(EventOfc.TYPE_DEAL_CARDS, this.heroName, Card.cards2Mask(availableCards.subList(0, dealSize).toArray(new Card[0]))));
//			}
//		} else
//			availableActionsForNature = Generator.combination(availableCards).simple(dealSize).stream().map(x -> new EventOfcMcts(EventOfc.TYPE_DEAL_CARDS, this.heroName, Card.cards2Mask(x.toArray(new Card[0])))).collect(Collectors.toList());
//		
//		long timeSpend = Misc.getTime() - timeBefore;
//		OfcMctsTest.availableInitTime += timeSpend;
//		OfcMctsTest.availableInitTimeNature += timeSpend;
//	}
	
	protected void initAvailableActionsForAgent() {
		List<EventOfcMctsSimple> lAvailableActionsForCurrentStep = new ArrayList<>();
		int frontCount = boxFront.size();
		int middleCount = boxMiddle.size();
		int backCount = boxBack.size();
		int deadCount = 0;
		int[] counter = {frontCount, middleCount, backCount, deadCount};
		int[] limits = {3, 5, 5, 1};
		int boxesNum = this.isFirstRound ? 3: 4; // for first round without dead box
		int sizeDead = Utils.deadCardsCount(frontCount + middleCount + backCount);
		if (this.cfg.NOT_SAMPLED_DEADS)
			sizeDead = 0;
		int totalCount = 13 + (this.cfg.NOT_SAMPLED_DEADS ? 0 : 4);
		
		if (frontCount + middleCount + backCount + sizeDead + cardsToBeBoxed.size() == totalCount) {
			if (Config.HEURISTIC_COMPLETE) {
				lAvailableActionsForCurrentStep.add(Heuristics.completion(boxFront, boxMiddle, boxBack, cardsToBeBoxed));
			} else {
				List<List<Card>> permutation = Generator.permutation(cardsToBeBoxed).simple().stream().collect(Collectors.toList());
				List<Card> lstFront = new ArrayList<>();
				List<Card> lstMiddle = new ArrayList<>();
				List<Card> lstBack = new ArrayList<>();
				List<Card> lstDead = new ArrayList<>();
				for (List<Card> ctb : permutation) {
					int index = 0;
					frontCount = boxFront.size();
					middleCount = boxMiddle.size();
					backCount = boxBack.size();
					while (frontCount < 3) {
						lstFront.add(ctb.get(index));
						frontCount++;
						index++;
					}
					while (middleCount < 5) {
						lstMiddle.add(ctb.get(index));
						middleCount++;
						index++;
					}
					while (backCount < 5) {
						lstBack.add(ctb.get(index));
						backCount++;
						index++;
					}
					lAvailableActionsForCurrentStep.add(new EventOfcMctsSimple(EventOfc.PUT_CARDS_TO_BOXES, lstFront, lstMiddle, lstBack, lstDead));
					lstFront.clear();
					lstMiddle.clear();
					lstBack.clear();
				}
			}
			
		} else {
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
						
						if (this.isFirstRound) {
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
									
									((List<Card>) arr[c0]).add(cardsToBeBoxed.get(0));
									((List<Card>) arr[c1]).add(cardsToBeBoxed.get(1));
									((List<Card>) arr[c2]).add(cardsToBeBoxed.get(2));
									((List<Card>) arr[c3]).add(cardsToBeBoxed.get(3));
									((List<Card>) arr[c4]).add(cardsToBeBoxed.get(4));
									
									lAvailableActionsForCurrentStep.add(new EventOfcMctsSimple(EventOfc.PUT_CARDS_TO_BOXES, lstFront, lstMiddle, lstBack, lstDead));
									
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
							
							((List<Card>) arr[c0]).add(cardsToBeBoxed.get(0));
							((List<Card>) arr[c1]).add(cardsToBeBoxed.get(1));
							((List<Card>) arr[c2]).add(cardsToBeBoxed.get(2));
							
							if (!lstDead.isEmpty()) {
								lAvailableActionsForCurrentStep.add(new EventOfcMctsSimple(EventOfc.PUT_CARDS_TO_BOXES, lstFront, lstMiddle, lstBack, lstDead));
							}
						}
						
						counter[c2]--;
					}
					counter[c1]--;
				}
				counter[c0]--;
			}
			
			if (Config.INIT_HEURISTIC_PRUNING) {
				if (frontCount == 0 && middleCount == 0 && backCount == 0) {
					List<EventOfcMctsSimple> pruning = lAvailableActionsForCurrentStep.stream().filter(x -> x.front.size() == 3 || x.middle.size() == 5).collect(Collectors.toList());
//					System.out.println(Misc.sf("Init pruning size: %d", pruning.size()));
					lAvailableActionsForCurrentStep.removeAll(pruning);
				}
			}
		}
		
		availableActionsForAgent = lAvailableActionsForCurrentStep;
		
	}

	@Override
	public boolean isTerminal() {
		return boxFront.size() == 3 && boxMiddle.size() == 5 && boxBack.size() == 5;
	}

	@Override
	public AgentOfcMctsSimple getCurrentAgent() {
		return agent;
	}

	@Override
	public AgentOfcMctsSimple getPreviousAgent() {
		return agent;
	}

	@Override
	public int getNumberOfAvailableActionsForCurrentAgent() {
		return getAvailableActionsForCurrentStep().size();
	}

	@Override
	public List<EventOfcMctsSimple> getAvailableActionsForCurrentAgent() {
//		return new ArrayList<>(getAvailableActionsForCurrentStep());
		return getAvailableActionsForCurrentStep();
	}

	@Override
	public MctsDomainState performActionForCurrentAgent(EventOfcMctsSimple action) {
		resetCache();
		if (action.type == EventOfc.TYPE_DEAL_CARDS) {
			cardsToBeBoxed.clear();
			cardsToBeBoxed.addAll(action.cardsToBeBoxed);
		}
		if (action.type == EventOfc.PUT_CARDS_TO_BOXES) {
			boxFront.addAll(action.front);
			boxMiddle.addAll(action.middle);
			boxBack.addAll(action.back);
			cardsToBeBoxed.clear();

			isFirstRound = false;
		}
		currentStepForNature = !currentStepForNature;
		return this;
	}

	@Override
	public MctsDomainState skipCurrentAgent() {
		return this;
	}
	
	@Override
	public boolean currentAgentActionsIsOrderedMode() {
		return Config.NATURE_ORDERED_MODE && currentStepForNature;
	}

/*	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((boxBack == null) ? 0 : boxBack.hashCode());
		result = prime * result + ((boxFront == null) ? 0 : boxFront.hashCode());
		result = prime * result + ((boxMiddle == null) ? 0 : boxMiddle.hashCode());
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
		GameOfcMctsSimple other = (GameOfcMctsSimple) obj;
		if (boxBack == null) {
			if (other.boxBack != null)
				return false;
		} else if (!boxBack.equals(other.boxBack))
			return false;
		if (boxFront == null) {
			if (other.boxFront != null)
				return false;
		} else if (!boxFront.equals(other.boxFront))
			return false;
		if (boxMiddle == null) {
			if (other.boxMiddle != null)
				return false;
		} else if (!boxMiddle.equals(other.boxMiddle))
			return false;
		return true;
	}
*/	
	public String getStateStr() {
		return boxFront + "-" + boxMiddle + "-" + boxBack;
	}

	public double evaluate(boolean inFantasy) {
		if (Config.EvaluationMethod == Config.EvaluationMethodKind.BOARD_SINGLE || Config.EvaluationMethod == Config.EvaluationMethodKind.BOARD_ACROSS) {  //if (natureSpace instanceof NatureSpaceExt) {
			try {
				return ((NatureSpaceExt) natureSpace).evaluateBySpace(boxFront, boxMiddle, boxBack, inFantasy);
			} catch (NatureSpaceExt.DontPassSpaceException e) {
				System.out.println("DontPassSpaceException");
				return EvaluatorFacade.evaluate(boxFront, boxMiddle, boxBack, inFantasy);
			}
		} else
			return EvaluatorFacade.evaluate(boxFront, boxMiddle, boxBack, inFantasy);
	}
}
