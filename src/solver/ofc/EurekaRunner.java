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
import solver.ofc.mcts.MctsCallback;

public class EurekaRunner {
	private GameOfcMctsSimple stateSimple;
	private Config cfg;
//	private GameOfc game;
	private int mctsIterationsCount;
	public int getMctsIterationsCount() {
		return mctsIterationsCount;
	}

	public NatureSpace natureSpace;

	public EurekaRunner(List<Card> front, List<Card> middle, List<Card> back, List<Card> toBeBoxed, List<Card> otherOpenedCard, 
			GameMode aGameMode, boolean aIsFirstRound, String aHeroName, Config aCfg) {
		cfg = aCfg;
		natureSpace = new NatureSpace(front, middle, back, toBeBoxed, otherOpenedCard, aGameMode, aIsFirstRound, aHeroName, cfg);
    	stateSimple = new GameOfcMctsSimple(front, middle, back, toBeBoxed, otherOpenedCard, aGameMode, aIsFirstRound, aHeroName, natureSpace);
	}
	
	public EurekaRunner(GameOfc game, Config aCfg) {
		this(game.getPlayer(game.heroName).boxFront.toList(), game.getPlayer(game.heroName).boxMiddle.toList(), game.getPlayer(game.heroName).boxBack.toList(),
				game.getPlayer(game.heroName).cardsToBeBoxed, GameOfcMctsSimple.mergeToOther(game), game.gameMode, game.isFirstRound(), game.heroName, aCfg);
	}
	
	public EventOfc runMcts(long timeDurationMs) {
		if (stateSimple.boxBack.size()+stateSimple.boxMiddle.size()+stateSimple.boxFront.size() == 11) {
			return stateSimple.getCurrentAgent().getBiasedOrRandomActionFromStatesAvailableActions(stateSimple).toEventOfc(stateSimple.heroName);
		}
		
		MctsCallback callback = null;
		MctsCallback callbackDebug = Config.DEBUG_PRINT ? new DebugPrinter() : null;
    	Mcts<GameOfcMctsSimple, EventOfcMctsSimple, AgentOfcMctsSimple> mcts = Mcts.initializeIterations(cfg.NUMBER_OF_ITERATIONS, callback, callbackDebug);
    	mcts.dontClone(AgentOfcMcts.class, EurekaRunner.class, NatureSpace.class);
    	EventOfcMctsSimple decision = mcts.uctSearchWithExploration(stateSimple, cfg.EXPLORATION_PARAMETER, timeDurationMs, cfg.TIME_LIMIT_MS);
    	System.out.println(String.format("mcts.iterationCount = %d", mcts.getIterationsCount()));
    	this.mctsIterationsCount = mcts.getIterationsCount();
    	return decision.toEventOfc(stateSimple.heroName);
	}

	public static EventOfc run(List<Card> front, List<Card> middle, List<Card> back, List<Card> toBeBoxed, List<Card> otherOpenedCard, 
			GameMode aGameMode, int aRound, String aHeroName, long timeDurationMs, long timeLimitMs) {
		Config cfg = new Config();
		cfg.TIME_LIMIT_MS = timeLimitMs;
		if (aRound == 4 || aRound == 3) { // for 4 and 3 round can run MCS
			return Mcs.monteCarloSimulation(new GameOfcMctsSimple(front, middle, back, toBeBoxed, otherOpenedCard, aGameMode, aRound == 1, aHeroName, new NatureSpace(front, middle, back, toBeBoxed, otherOpenedCard, aGameMode, aRound == 1, aHeroName, cfg)), timeDurationMs > 0 ? timeDurationMs : cfg.TIME_LIMIT_MS, Config.CPU_NUM).toEventOfc(aHeroName);
		} else {
			if (aRound == 1) cfg.EXPLORATION_PARAMETER = 7;
			if (aRound == 2) cfg.EXPLORATION_PARAMETER = 15;
//			if (aRound == 3) cfg.EXPLORATION_PARAMETER = 20;
			
			if (aRound == 1) {
				cfg.NUMBER_OF_ITERATIONS = 10000;
				cfg.RANDOM_DEAL_COUNT = cfg.NUMBER_OF_ITERATIONS;
				timeDurationMs = 0;
			}
			cfg.RANDOM_DEAL_COUNT = cfg.NUMBER_OF_ITERATIONS;
			EurekaRunner runner = new EurekaRunner(front, middle, back, toBeBoxed, otherOpenedCard, aGameMode, aRound == 1, aHeroName, cfg);
			return runner.runMcts(timeDurationMs);
		}
	}
	
	public static EventOfc run(GameOfc game, long timeDurationMs, long timeLimitMs) {
		return run(game.getPlayer(game.heroName).boxFront.toList(), game.getPlayer(game.heroName).boxMiddle.toList(), game.getPlayer(game.heroName).boxBack.toList(),
				game.getPlayer(game.heroName).cardsToBeBoxed, GameOfcMctsSimple.mergeToOther(game), game.gameMode, game.getRound(), game.heroName, timeDurationMs, timeLimitMs);
	}
	
	public static EventOfc run(GameOfc game) {
		return run(game, 0, 17000);
	}
}
