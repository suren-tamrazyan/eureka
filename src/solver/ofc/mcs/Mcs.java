package solver.ofc.mcs;

import java.util.List;

import solver.ofc.EvaluatorFacade;
import solver.ofc.EventOfcMctsSimple;
import solver.ofc.GameOfcMctsSimple;
import solver.ofc.Heuristics;

public class Mcs {

	public Mcs() {
	}

	public static EventOfcMctsSimple monteCarloSimulation(GameOfcMctsSimple startState, int simDepth) {
		List<EventOfcMctsSimple> actions = startState.getAvailableActionsForCurrentAgent();
		
		int i = 0;
		double maxReward = 0;
		EventOfcMctsSimple bestSolution = null;
		for (EventOfcMctsSimple act : actions) {
			GameOfcMctsSimple actStateClone = startState.clone();
			actStateClone.performActionForCurrentAgent(act);
			double reward = 0;
//			int cnt = 0;
			for (EventOfcMctsSimple natureSamp : startState.getOwnerClosure().natureSamples) {
				GameOfcMctsSimple natStateClone = actStateClone.clone();
				natStateClone.performActionForCurrentAgent(natureSamp);
				EventOfcMctsSimple finalAct = Heuristics.completion(natStateClone.boxFront, natStateClone.boxMiddle, natStateClone.boxBack, natStateClone.cardsToBeBoxed);
				natStateClone.performActionForCurrentAgent(finalAct);
				reward += EvaluatorFacade.evaluate(natStateClone.boxFront, natStateClone.boxMiddle, natStateClone.boxBack, false);
				i++;
//				cnt++;
//				System.out.println(String.format("%f %s", EvaluatorFacade.evaluate(natStateClone.boxFront, natStateClone.boxMiddle, natStateClone.boxBack, false), natureSamp));
			}
//			System.out.println(String.format("act = %s; count = %d reward = %f", act.toEventOfc("hero").toString(), cnt, reward));
			if (reward > maxReward) {
				maxReward = reward;
				bestSolution = act;
			}
		}
		System.out.println(String.format("iter = %d; actions.count = %d", i, actions.size()));
		if (bestSolution == null) {
			System.out.println("monteCarloSimulation: no solution, first action chosen");
			bestSolution = actions.get(0);
		}
		return bestSolution; 
	}
}
