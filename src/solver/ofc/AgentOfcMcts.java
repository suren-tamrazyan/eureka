package solver.ofc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;

import game.Card;
import game.GameException;
import game.GameOfc;
import game.PlayerOfc;
import solver.ofc.mcts.MctsDomainAgent;

public class AgentOfcMcts  implements MctsDomainAgent<GameOfcMcts> {
	
	public static ExecutorService pool = Executors.newCachedThreadPool();

	@Override
	public GameOfcMcts getTerminalStateByPerformingSimulationFromState(GameOfcMcts state) {
		while (!state.isTerminal()) {
			EventOfcMcts action = getBiasedOrRandomActionFromStatesAvailableActions(state);
			state.performActionForCurrentAgent(action);
		}
//		EventOfcMctsSimple heuristic = Heuristics.completion(state.hero().boxFront.toList(), state.hero().boxMiddle.toList(), state.hero().boxBack.toList(), state.hero().cardsToBeBoxed);
//		if (heuristic != null) {
//			try {
//				state.merge(heuristic);
//			} catch (GameException e) {
//				throw new RuntimeException(e);
//			}
//		} else {
//			while (!state.isTerminal()) {
//				EventOfcMcts action = getBiasedOrRandomActionFromStatesAvailableActions(state);
//				state.performActionForCurrentAgent(action);
//			}
//		}
		return state;
	}

	public EventOfcMcts getBiasedOrRandomActionFromStatesAvailableActions(GameOfcMcts state) {
		List<EventOfcMcts> availableActions = state.getAvailableActionsForCurrentAgent();
		if (state.currentStepForNature) {
			return availableActions.get(ThreadLocalRandom.current().nextInt(availableActions.size()));
		} else {
			if (state.isLastRound()) {
				double maxReward = Double.NEGATIVE_INFINITY;
				EventOfcMcts bestAction = availableActions.get(0);
				for (EventOfcMcts act : availableActions) {
					GameOfc clone = state.clone();
					try {
						clone.procEvent(act);
					} catch (Exception e) {
						throw new IllegalArgumentException(String.format("Exception: %s", e.getMessage()), e);
					}
					double currentReward = EvaluatorFacade.evaluate(clone.getPlayer(clone.heroName));
					if (currentReward > maxReward) {
						maxReward = currentReward;
						bestAction = act;
					}
				}
				return bestAction;
//			} else {
//				EventOfcMctsSimple heurRes = Heuristics.completion(state.hero().boxFront.toList(), state.hero().boxMiddle.toList(), state.hero().boxBack.toList(), state.hero().cardsToBeBoxed);
//				if (heurRes == null)
//					return availableActions.get(0);
//				else
//					return (EventOfcMcts) heurRes.toEventOfc(state.heroName);
//			}
			} else if (state.isFirstRound()) {
				return availableActions.get(ThreadLocalRandom.current().nextInt(availableActions.size()));
			} else {
				// heavy play out
				List<Card> openCards = new ArrayList<>();
				for (PlayerOfc p : state.getPlayers()) {
					if (!p.name.equals(state.heroName)) {
						openCards.addAll(p.boxBack.toList());
						openCards.addAll(p.boxMiddle.toList());
						openCards.addAll(p.boxFront.toList());
						openCards.addAll(p.boxDead.toList());
						openCards.addAll(p.cardsToBeBoxed);

					}
				}
//				return getHeuristicMove_maxValue(state, availableActions, openCards);
				return getHeuristicMove_ProbabilityByOneRow(state, availableActions, openCards);
			}
		}
	}

	private EventOfcMcts getHeuristicMove_ProbabilityByOneRow(GameOfcMcts state, List<EventOfcMcts> availableActions, List<Card> openCards) {
		Map<Card, Double> valF = null; // values for front box; indexes is card
		Map<Card, Double> valM = null; // values for middle box; indexes is card
		Map<Card, Double> valB = null; // values for back box; indexes is card
		if (!state.hero().boxFront.isFull())
			valF = EvaluatorFacade.evaluateIncompleteHand(state.hero().boxFront.toList(), GameOfc.BOX_LEVEL_FRONT, state.hero().cardsToBeBoxed, openCards);
		if (!state.hero().boxMiddle.isFull())
			valM = EvaluatorFacade.evaluateIncompleteHand(state.hero().boxMiddle.toList(), GameOfc.BOX_LEVEL_MIDDLE, state.hero().cardsToBeBoxed, openCards);
		if (!state.hero().boxBack.isFull())
			valB = EvaluatorFacade.evaluateIncompleteHand(state.hero().boxBack.toList(), GameOfc.BOX_LEVEL_BACK, state.hero().cardsToBeBoxed, openCards);
		
		double maxVal = Double.NEGATIVE_INFINITY;
		EventOfcMcts bestAction = availableActions.get(0);
		for (EventOfcMcts act : availableActions) {
			double currentVal = 0;

			for (Map.Entry<Card, Integer> entry : act.card2box.entrySet()) {
				int box = entry.getValue();
				switch (box) {
				case GameOfcMcts.BOX_LEVEL_FRONT:
					if (valF != null)
						currentVal += valF.get(entry.getKey());
					break;
				case GameOfcMcts.BOX_LEVEL_MIDDLE:
					if (valM != null)
						currentVal += valM.get(entry.getKey());
					break;
				case GameOfcMcts.BOX_LEVEL_BACK:
					if (valB != null)
						currentVal += valB.get(entry.getKey());
					break;
				default:
					break;
				}
			}
			
			if (currentVal > maxVal) {
				maxVal = currentVal;
				bestAction = act;
			}
		}
		return bestAction;
	}
	
	private EventOfcMcts getHeuristicMove_ProbabilityByOneRow_multithread(GameOfcMcts state, List<EventOfcMcts> availableActions, List<Card> openCards) {
		Map<Card, Double> valF = null; // values for front box; indexes is card
		Map<Card, Double> valM = null; // values for middle box; indexes is card
		Map<Card, Double> valB = null; // values for back box; indexes is card
		
		Future<Map<Card, Double>> futF = null; // async result for front box;
		Future<Map<Card, Double>> futM = null; // async result for middle box;
		Future<Map<Card, Double>> futB = null; // async result for back box;
		
		if (!state.hero().boxBack.isFull())
			futB = pool.submit(new Callable<Map<Card, Double>>() {
													@Override
													public Map<Card, Double> call( ) {
														System.out.println("start Back");
														Map<Card, Double> res = EvaluatorFacade.evaluateIncompleteHand(state.hero().boxBack.toList(), GameOfc.BOX_LEVEL_BACK, state.hero().cardsToBeBoxed, openCards);
														System.out.println("end Back");
														return res;
													}
												});
		if (!state.hero().boxMiddle.isFull())
			futM = pool.submit(new Callable<Map<Card, Double>>() {
													@Override
													public Map<Card, Double> call( ) {
														System.out.println("start Middle");
														Map<Card, Double> res = EvaluatorFacade.evaluateIncompleteHand(state.hero().boxMiddle.toList(), GameOfc.BOX_LEVEL_MIDDLE, state.hero().cardsToBeBoxed, openCards);
														System.out.println("end Middle");
														return res;
													}
												});
		if (!state.hero().boxFront.isFull())
			futF = pool.submit(new Callable<Map<Card, Double>>() {
													@Override
													public Map<Card, Double> call( ) {
														System.out.println("start Front");
														Map<Card, Double> res = EvaluatorFacade.evaluateIncompleteHand(state.hero().boxFront.toList(), GameOfc.BOX_LEVEL_FRONT, state.hero().cardsToBeBoxed, openCards);
														System.out.println("end Front");
														return res;
													}
												});
		
		try {
			if (futF != null)
				valF = futF.get();
			if (futM != null)
				valM = futM.get();
			if (futB != null)
				valB = futB.get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		
		double maxVal = Double.NEGATIVE_INFINITY;
		EventOfcMcts bestAction = availableActions.get(0);
		for (EventOfcMcts act : availableActions) {
			double currentVal = 0;

			for (Map.Entry<Card, Integer> entry : act.card2box.entrySet()) {
				int box = entry.getValue();
				switch (box) {
				case GameOfcMcts.BOX_LEVEL_FRONT:
					if (valF != null)
						currentVal += valF.get(entry.getKey());
					break;
				case GameOfcMcts.BOX_LEVEL_MIDDLE:
					if (valM != null)
						currentVal += valM.get(entry.getKey());
					break;
				case GameOfcMcts.BOX_LEVEL_BACK:
					if (valB != null)
						currentVal += valB.get(entry.getKey());
					break;
				default:
					break;
				}
			}
			
			if (currentVal > maxVal) {
				maxVal = currentVal;
				bestAction = act;
			}
		}
		return bestAction;
	}
	
	private EventOfcMcts getHeuristicMove_maxValue(GameOfcMcts state, List<EventOfcMcts> availableActions, List<Card> openCards) {
		double[] val0; // values for card0; indexes is box
		double[] val1; // values for card1; indexes is box
		double[] val2; // values for card2; indexes is box
		try {
			val0 = EvaluatorFacade.maxValue(state.hero(), state.hero().cardsToBeBoxed.get(0), openCards);
			val1 = EvaluatorFacade.maxValue(state.hero(), state.hero().cardsToBeBoxed.get(1), openCards);
			val2 = EvaluatorFacade.maxValue(state.hero(), state.hero().cardsToBeBoxed.get(2), openCards);
		} catch (Exception e) {
			return availableActions.get(ThreadLocalRandom.current().nextInt(availableActions.size()));
		}
		
		double maxVal = Double.NEGATIVE_INFINITY;
		EventOfcMcts bestAction = availableActions.get(0);
		for (EventOfcMcts act : availableActions) {
			double currentVal = 0;
			
			int ind0 = act.card2box.get(state.hero().cardsToBeBoxed.get(0)) - 1;
			if (ind0 >= 0)
				currentVal += val0[ind0];
			int ind1 = act.card2box.get(state.hero().cardsToBeBoxed.get(1)) - 1;
			if (ind1 >= 0)
				currentVal += val1[ind1];
			int ind2 = act.card2box.get(state.hero().cardsToBeBoxed.get(2)) - 1;
			if (ind2 >= 0)
				currentVal += val2[ind2];
			
			
			if (currentVal > maxVal) {
				maxVal = currentVal;
				bestAction = act;
			}
		}
		return bestAction;
	}

	@Override
	public double getRewardFromTerminalState(GameOfcMcts terminalState) {
		return getReward(terminalState.hero());
	}
	
	public static double getReward(PlayerOfc player) {
		if (!player.boxesIsFull())
			throw new IllegalArgumentException(String.format("Error: player %s not in final state", player.name));
		
		return EvaluatorFacade.evaluate(player);
	}

}
