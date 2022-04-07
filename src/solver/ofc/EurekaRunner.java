package solver.ofc;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.paukov.combinatorics3.Generator;

import game.Card;
import game.EventOfc;
import game.GameOfc;
import game.GameOfc.GameMode;
import solver.ofc.mcs.Mcs;
import solver.ofc.mcts.Mcts;

public class EurekaRunner {
	private GameOfcMctsSimple stateSimple;
//	private GameOfc game;
	private int mctsIterationsCount;
	public int getMctsIterationsCount() {
		return mctsIterationsCount;
	}

	public List<EventOfcMctsSimple> natureSamples;
	public Map<String, Integer> numberTakesOfNatureSimulations = new HashMap<>();

	public EurekaRunner(List<Card> front, List<Card> middle, List<Card> back, List<Card> toBeBoxed, List<Card> otherOpenedCard, 
			GameMode aGameMode, boolean aIsFirstRound, String aHeroName) {
    	stateSimple = new GameOfcMctsSimple(front, middle, back, toBeBoxed, otherOpenedCard, aGameMode, aIsFirstRound, aHeroName, this);
    	
		//initAvailableActionsForNature; one common space of samples for all state (node) because one deal over algorithm
    	int sizeFront = front.size();
    	int sizeMiddle = middle.size();
    	int sizeBack = back.size();
    	int sizeCardsToBeBoxed = toBeBoxed.size();

		List<Card> availableCards = stateSimple.getAvailableCards();
		int dealSize = 13 - (sizeFront + sizeMiddle + sizeBack + (sizeCardsToBeBoxed - (aIsFirstRound?0:1)));
		BigInteger cntCombi = Utils.combinationCount(availableCards.size(), dealSize);
		if (cntCombi.compareTo(BigInteger.valueOf(3 * Config.RANDOM_DEAL_COUNT)) > 0) {
			this.natureSamples = new ArrayList<EventOfcMctsSimple>(Config.RANDOM_DEAL_COUNT);
			Set<Set<Card>> checkDistinct = new HashSet<>(Config.RANDOM_DEAL_COUNT);
			int i = 0;
			while (i < Config.RANDOM_DEAL_COUNT) {
				Collections.shuffle(availableCards);
				Set<Card> tmp = new HashSet<>(availableCards.subList(0, dealSize));
				if (checkDistinct.add(tmp)) {
					this.natureSamples.add(new EventOfcMctsSimple(EventOfc.TYPE_DEAL_CARDS, tmp));
					i++;
				}
			}
		} else
			this.natureSamples = Generator.combination(availableCards).simple(dealSize).stream().map(x -> new EventOfcMctsSimple(EventOfc.TYPE_DEAL_CARDS, x)).collect(Collectors.toList());
		
		this.numberTakesOfNatureSimulations.clear();
	}
	
	public EurekaRunner(GameOfc game) {
		this(game.getPlayer(game.heroName).boxFront.toList(), game.getPlayer(game.heroName).boxMiddle.toList(), game.getPlayer(game.heroName).boxBack.toList(),
				game.getPlayer(game.heroName).cardsToBeBoxed, GameOfcMctsSimple.mergeToOther(game), game.gameMode, game.isFirstRound(), game.heroName);
	}
	
	public EventOfc runMcts(long timeDurationMs) {
		if (stateSimple.boxBack.size()+stateSimple.boxMiddle.size()+stateSimple.boxFront.size() == 11) {
			return stateSimple.getCurrentAgent().getBiasedOrRandomActionFromStatesAvailableActions(stateSimple).toEventOfc(stateSimple.heroName);
		}
		
    	Mcts<GameOfcMctsSimple, EventOfcMctsSimple, AgentOfcMctsSimple> mcts = Mcts.initializeIterations(Config.NUMBER_OF_ITERATIONS);
    	mcts.dontClone(AgentOfcMcts.class, EurekaRunner.class);
    	EventOfcMctsSimple decision = mcts.uctSearchWithExploration(stateSimple, Config.EXPLORATION_PARAMETER, timeDurationMs, Config.TIME_LIMIT_MS);
    	System.out.println(String.format("mcts.iterationCount = %d", mcts.getIterationsCount()));
    	this.mctsIterationsCount = mcts.getIterationsCount();
    	return decision.toEventOfc(stateSimple.heroName);
	}

	public static EventOfc run(List<Card> front, List<Card> middle, List<Card> back, List<Card> toBeBoxed, List<Card> otherOpenedCard, 
			GameMode aGameMode, int aRound, String aHeroName, long timeDurationMs) {
		if (aRound == 4) { // for 4 round can run MCS
			return Mcs.monteCarloSimulation(new GameOfcMctsSimple(front, middle, back, toBeBoxed, otherOpenedCard, aGameMode, aRound == 1, aHeroName, new EurekaRunner(front, middle, back, toBeBoxed, otherOpenedCard, aGameMode, aRound == 1, aHeroName)), 0).toEventOfc(aHeroName);
		} else {
			if (aRound == 1) Config.EXPLORATION_PARAMETER = 7;
			if (aRound == 2) Config.EXPLORATION_PARAMETER = 15;
			if (aRound == 3) Config.EXPLORATION_PARAMETER = 20;
			if (aRound == 1) {
				Config.NUMBER_OF_ITERATIONS = 10000;
				Config.RANDOM_DEAL_COUNT = Config.NUMBER_OF_ITERATIONS;
				timeDurationMs = 0;
			}
			EurekaRunner runner = new EurekaRunner(front, middle, back, toBeBoxed, otherOpenedCard, aGameMode, aRound == 1, aHeroName);
			return runner.runMcts(timeDurationMs);
		}
	}
	
	public static EventOfc run(GameOfc game, long timeDurationMs) {
		return run(game.getPlayer(game.heroName).boxFront.toList(), game.getPlayer(game.heroName).boxMiddle.toList(), game.getPlayer(game.heroName).boxBack.toList(),
				game.getPlayer(game.heroName).cardsToBeBoxed, GameOfcMctsSimple.mergeToOther(game), game.gameMode, game.getRound(), game.heroName, timeDurationMs);
	}
	
	public static EventOfc run(GameOfc game) {
		return run(game, 0);
	}
}
