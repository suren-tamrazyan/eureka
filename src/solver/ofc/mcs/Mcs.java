package solver.ofc.mcs;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import solver.ofc.*;

public class Mcs {
	private static List<String> csv = new ArrayList<>();
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
			int cnt = 0;
			for (EventOfcMctsSimple natureSamp : startState.getOwnerClosure().natureSamples) {
				GameOfcMctsSimple natStateClone = actStateClone.clone();
				natStateClone.performActionForCurrentAgent(natureSamp);
				EventOfcMctsSimple finalAct = Heuristics.completion(natStateClone.boxFront, natStateClone.boxMiddle, natStateClone.boxBack, natStateClone.cardsToBeBoxed);
				natStateClone.performActionForCurrentAgent(finalAct);
				reward += EvaluatorFacade.evaluate(natStateClone.boxFront, natStateClone.boxMiddle, natStateClone.boxBack, false);
				i++;
				if (Config.DEBUG_PRINT) {
					cnt++;
					debugOnEstimateSample(act, natStateClone, natureSamp, cnt, reward);
				}
			}
			if (Config.DEBUG_PRINT)
				System.out.println(String.format("act = %s; count = %d reward = %f", act.toEventOfc("hero").toString(), cnt, reward));
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
		if (Config.DEBUG_PRINT) onEndSearch();
		return bestSolution; 
	}

	public static EventOfcMctsSimple monteCarloSimulation(GameOfcMctsSimple startState, long timeDurationMs, int cpuNums, int elitePercent) {
		List<EventOfcMctsSimple> actions = startState.getAvailableActionsForCurrentAgent();

		int i = 0;
		int epoch = 0;
		Map<EventOfcMctsSimple, Double> rewards = new ConcurrentHashMap<>();
		for (EventOfcMctsSimple act : actions) {
			rewards.put(act, new Double(0.0));
		}
		for (EventOfcMctsSimple natureSamp : startState.getOwnerClosure().natureSamples) {
			epoch++;
//			GameOfcMctsSimple natStateClone = startState.clone();
			for (EventOfcMctsSimple act : actions) {
				GameOfcMctsSimple actStateClone = startState.clone();//natStateClone.clone();
				actStateClone.performActionForCurrentAgent(act);
				actStateClone.performActionForCurrentAgent(natureSamp);
				EventOfcMctsSimple finalAct = Heuristics.completion(actStateClone.boxFront, actStateClone.boxMiddle, actStateClone.boxBack, actStateClone.cardsToBeBoxed);
				actStateClone.performActionForCurrentAgent(finalAct);
				double reward = rewards.get(act) + EvaluatorFacade.evaluate(actStateClone.boxFront, actStateClone.boxMiddle, actStateClone.boxBack, false);
				rewards.put(act, reward);
				i++;

				if (Config.DEBUG_PRINT) {
					debugOnEstimateSample(act, actStateClone, natureSamp, epoch, reward);
				}
			}
		}

		Map.Entry<EventOfcMctsSimple, Double> maxEntry = rewards.entrySet().stream().max(Comparator.comparing(Map.Entry::getValue)).get();

		System.out.println(String.format("iter = %d; actions.count = %d", i, actions.size()));
		if (Config.DEBUG_PRINT) onEndSearch();
		return maxEntry.getKey();
	}

	protected static void debugOnEstimateSample(EventOfcMctsSimple action, GameOfcMctsSimple state, EventOfcMctsSimple sample, int actionIterNum, double actionReward) {
//		System.out.println(String.format("%f %s", EvaluatorFacade.evaluate(state.boxFront, state.boxMiddle, state.boxBack, false), sample));
		if (actionIterNum % 100 == 0) {
			if (csv.isEmpty())
				csv.add("epoch;branch;reward");
			csv.add(String.format(java.util.Locale.US, "%d;%s;%.2f", actionIterNum, action.toString().replaceAll(";", ""), actionReward));
		}
	}

	protected static void onEndSearch() {
		// save csv file
		System.out.println();
		System.out.println("Save csv file");
		String filename = String.format("D:\\develop\\temp\\poker\\Eureka\\debug\\mcs-%s.csv", Utils.dateFormatIntel(Utils.getTime()));
		try {
			FileWriter writer = new FileWriter(filename);
			writer.write(csv.stream().collect(Collectors.joining("\n")));
			writer.close();
			System.out.println(String.format("%s saved.", filename));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
