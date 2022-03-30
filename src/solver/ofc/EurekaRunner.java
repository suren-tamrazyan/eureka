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
import solver.ofc.mcs.Mcs;
import solver.ofc.mcts.Mcts;

public class EurekaRunner {
	private GameOfcMctsSimple stateSimple;
	private GameOfc game;
	private int mctsIterationsCount;
	public int getMctsIterationsCount() {
		return mctsIterationsCount;
	}

	public List<EventOfcMctsSimple> natureSamples;
	public Map<String, Integer> numberTakesOfNatureSimulations = new HashMap<>();

	public EurekaRunner(GameOfc sourceGame) {
		this.game = sourceGame;
    	stateSimple = new GameOfcMctsSimple(game, this);
    	
		//initAvailableActionsForNature; one common space of samples for all state (node) because one deal over algorithm
    	int sizeFront = game.getPlayer(game.heroName).boxFront.toList().size();
    	int sizeMiddle = game.getPlayer(game.heroName).boxMiddle.toList().size();
    	int sizeBack = game.getPlayer(game.heroName).boxBack.toList().size();
    	int sizeCardsToBeBoxed = game.getPlayer(game.heroName).cardsToBeBoxed.size();

		List<Card> availableCards = stateSimple.getAvailableCards();
		int dealSize = 13 - (sizeFront + sizeMiddle + sizeBack + (sizeCardsToBeBoxed - (game.isFirstRound()?0:1)));
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
	
	public EventOfc runMcts(long timeDurationMs) {
		if (stateSimple.boxBack.size()+stateSimple.boxMiddle.size()+stateSimple.boxFront.size() == 11) {
			return stateSimple.getCurrentAgent().getBiasedOrRandomActionFromStatesAvailableActions(stateSimple).toEventOfc(game.heroName);
		}
		
    	Mcts<GameOfcMctsSimple, EventOfcMctsSimple, AgentOfcMctsSimple> mcts = Mcts.initializeIterations(Config.NUMBER_OF_ITERATIONS);
    	mcts.dontClone(AgentOfcMcts.class, EurekaRunner.class);
    	EventOfcMctsSimple decision = mcts.uctSearchWithExploration(stateSimple, Config.EXPLORATION_PARAMETER, timeDurationMs, Config.TIME_LIMIT_MS);
    	System.out.println(String.format("mcts.iterationCount = %d", mcts.getIterationsCount()));
    	this.mctsIterationsCount = mcts.getIterationsCount();
    	return decision.toEventOfc(game.heroName);
	}

	public static EventOfc run(GameOfc game, long timeDurationMs) {
		if (game.getRound() == 4) { // for 4 round can run MCS
			return Mcs.monteCarloSimulation(new GameOfcMctsSimple(game, new EurekaRunner(game)), 0).toEventOfc(game.heroName);
		} else {
			if (game.getRound() == 1) Config.EXPLORATION_PARAMETER = 7;
			if (game.getRound() == 2) Config.EXPLORATION_PARAMETER = 15;
			if (game.getRound() == 3) Config.EXPLORATION_PARAMETER = 20;
			if (game.getRound() == 1) {
				Config.NUMBER_OF_ITERATIONS = 10000;
				Config.RANDOM_DEAL_COUNT = Config.NUMBER_OF_ITERATIONS;
				timeDurationMs = 0;
			}
			EurekaRunner runner = new EurekaRunner(game);
			return runner.runMcts(timeDurationMs);
		}
	}
	
	public static EventOfc run(GameOfc game) {
		return run(game, 0);
	}
}
