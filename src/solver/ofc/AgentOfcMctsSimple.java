package solver.ofc;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import game.Card;
import solver.ofc.mcts.MctsDomainAgent;

public class AgentOfcMctsSimple implements MctsDomainAgent<GameOfcMctsSimple> {

	public AgentOfcMctsSimple() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public GameOfcMctsSimple getTerminalStateByPerformingSimulationFromState(GameOfcMctsSimple state) {
        while (!state.isTerminal()) {
            EventOfcMctsSimple action = getBiasedOrRandomActionFromStatesAvailableActions(state);
            state.performActionForCurrentAgent(action);
        }
        return state;
	}

	public EventOfcMctsSimple getBiasedOrRandomActionFromStatesAvailableActions(GameOfcMctsSimple state) {
		List<EventOfcMctsSimple> availableActions = state.getAvailableActionsForCurrentAgent();
		if (state.currentStepForNature) {
//			return availableActions.get(Misc.rand.nextInt(availableActions.size())); this case don't take robust result, because take different random case for other node
			int num = -1; 
			String stateStr = state.getStateStr();
			if (state.getNatureSpace().numberTakesOfNatureSimulations.containsKey(stateStr))
				num = state.getNatureSpace().numberTakesOfNatureSimulations.get(stateStr);
			num++;
			state.getNatureSpace().numberTakesOfNatureSimulations.put(stateStr, num);
			return availableActions.get(num);
		} else {
			if (state.boxFront.size() + state.boxMiddle.size() + state.boxBack.size() == 11) { //state.isLastRound()
				double maxReward = Double.NEGATIVE_INFINITY;
				EventOfcMctsSimple bestAction = availableActions.get(0);
				for (EventOfcMctsSimple act : availableActions) {
					ArrayList<Card> lstFront = new ArrayList<>(state.boxFront);
					ArrayList<Card> lstMiddle = new ArrayList<>(state.boxMiddle);
					ArrayList<Card> lstBack = new ArrayList<>(state.boxBack);
					lstFront.addAll(act.front);
					lstMiddle.addAll(act.middle);
					lstBack.addAll(act.back);
					double currentReward = EvaluatorFacade.evaluate(lstFront, lstMiddle, lstBack, false);
					if (currentReward > maxReward) {
						maxReward = currentReward;
						bestAction = act;
					}
				}
				return bestAction;
			} else
				return availableActions.get(ThreadLocalRandom.current().nextInt(availableActions.size()));
		}
	}

	@Override
	public double getRewardFromTerminalState(GameOfcMctsSimple terminalState) {
		return EvaluatorFacade.evaluate(terminalState.boxFront, terminalState.boxMiddle, terminalState.boxBack, false);
	}

}
