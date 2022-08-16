package solver.ofc.mcs;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.DoubleAdder;
import java.util.stream.Collectors;

import solver.ofc.*;

public class Mcs {
	public static class QueueItem {
		public EventOfcMctsSimple act;
		public GameOfcMctsSimple state;
		public QueueItem (EventOfcMctsSimple act, GameOfcMctsSimple state) {
			this.act = act;
			this.state = state;
		}
	}

	private static List<String> csv = new ArrayList<>();
	//	private Set<String> csv = ConcurrentHashMap.newKeySet();
	private final int QUEUE_SIZE_PER_THREAD = 200;
	private BlockingQueue<QueueItem> queue;
	private ThreadPoolExecutor pool;
	private Map<EventOfcMctsSimple, DoubleAdder> rewards = new ConcurrentHashMap<>();
	private volatile boolean theEnd;
	private int cpuNum;
	public Mcs(int cpuNum) {
		this.cpuNum = cpuNum;
		queue = new LinkedBlockingQueue<>(cpuNum * QUEUE_SIZE_PER_THREAD);
		pool = (ThreadPoolExecutor) Executors.newFixedThreadPool(cpuNum);

		startWorkers();
	}

	private void startWorkers() {
		for (int i = 0; i < cpuNum; i++) {
			pool.submit(() -> {
				while (!(theEnd && queue.isEmpty()) && !Thread.currentThread().isInterrupted()) {
					QueueItem estState = queue.poll();
					if (estState == null) {
						Utils.sleep(1);
//						System.out.println("Skipping empty queue");
						continue;
					}
					EventOfcMctsSimple finalAct = Heuristics.completion(estState.state.boxFront, estState.state.boxMiddle, estState.state.boxBack, estState.state.cardsToBeBoxed);
					estState.state.performActionForCurrentAgent(finalAct);

					double reward = EvaluatorFacade.evaluate(estState.state.boxFront, estState.state.boxMiddle, estState.state.boxBack, false);
//					if (reward > 0)
						rewards.get(estState.act).add(reward);
				}
			});
		}
	}
	public EventOfcMctsSimple monteCarloSimulation(GameOfcMctsSimple startState, long timeDurationMs) {
		long timeDurationNano = timeDurationMs * 1000000;
		long time = 0;
		long timeStart = System.nanoTime();

		List<EventOfcMctsSimple> actions = startState.getAvailableActionsForCurrentAgent();

		int i = 0;
		int epoch = 0;

		for (EventOfcMctsSimple act : actions) {
			rewards.put(act, new DoubleAdder());
		}

		outerloop:
		for (EventOfcMctsSimple natureSamp : startState.getNatureSpace().natureSamples) {
			epoch++;
			for (EventOfcMctsSimple act : actions) {
				GameOfcMctsSimple actStateClone = startState.clone();
				actStateClone.performActionForCurrentAgent(act);
				actStateClone.performActionForCurrentAgent(natureSamp);

				try {
					queue.put(new QueueItem(act, actStateClone));
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}

				i++;
				time = System.nanoTime() - timeStart;
				if (timeDurationMs > 0 && time > timeDurationNano)
					break outerloop;

			}
		}

		theEnd = true;
		do {
			Utils.sleep(10);
//			System.out.println("Waiting in main threads");
		} while (!pool.getQueue().isEmpty() || pool.getActiveCount() > 0);
		pool.shutdown();

		Map.Entry<EventOfcMctsSimple, DoubleAdder> maxEntry = rewards.entrySet().stream().max(Comparator.comparing(x -> x.getValue().sum())).get();
		System.out.println(String.format("epoch = %d; iter = %d; actions.count = %d", epoch, i, actions.size()));
		return maxEntry.getKey();
	}

	public static EventOfcMctsSimple monteCarloSimulation(GameOfcMctsSimple startState, long timeDurationMs, int cpuNum) {
		Mcs mcs = new Mcs(cpuNum);
		return mcs.monteCarloSimulation(startState, timeDurationMs);
	}

	public static EventOfcMctsSimple monteCarloSimulation(GameOfcMctsSimple startState, int simDepth) {
		List<EventOfcMctsSimple> actions = startState.getAvailableActionsForCurrentAgent();
		
		int i = 0;
		double maxReward = Double.NEGATIVE_INFINITY;
		EventOfcMctsSimple bestSolution = null;
		for (EventOfcMctsSimple act : actions) {
			GameOfcMctsSimple actStateClone = startState.clone();
			actStateClone.performActionForCurrentAgent(act);
			double rewardAcc = 0;
			int cnt = 0;
			for (EventOfcMctsSimple natureSamp : startState.getNatureSpace().natureSamples) {
				GameOfcMctsSimple natStateClone = actStateClone.clone();
				natStateClone.performActionForCurrentAgent(natureSamp);
				EventOfcMctsSimple finalAct = Heuristics.completion(natStateClone.boxFront, natStateClone.boxMiddle, natStateClone.boxBack, natStateClone.cardsToBeBoxed);
				natStateClone.performActionForCurrentAgent(finalAct);
				double rwrd = EvaluatorFacade.evaluate(natStateClone.boxFront, natStateClone.boxMiddle, natStateClone.boxBack, false);
				rewardAcc += rwrd;
				i++;
				if (Config.DEBUG_PRINT) {
					cnt++;
					debugOnEstimateSample(act, natStateClone, natureSamp, cnt, rewardAcc, rwrd);
				}
			}
			if (Config.DEBUG_PRINT)
				System.out.println(String.format("act = %s; count = %d rewardAcc = %f", act.toEventOfc("hero").toString(), cnt, rewardAcc));
			if (rewardAcc > maxReward) {
				maxReward = rewardAcc;
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

	public static EventOfcMctsSimple monteCarloSimulation(GameOfcMctsSimple startState) {
		List<EventOfcMctsSimple> actions = startState.getAvailableActionsForCurrentAgent();

		int i = 0;
		int epoch = 0;
		Map<EventOfcMctsSimple, Double> rewards = new ConcurrentHashMap<>();
		for (EventOfcMctsSimple act : actions) {
			rewards.put(act, new Double(0.0));
		}
		for (EventOfcMctsSimple natureSamp : startState.getNatureSpace().natureSamples) {
			epoch++;
//			GameOfcMctsSimple natStateClone = startState.clone();
			for (EventOfcMctsSimple act : actions) {
				GameOfcMctsSimple actStateClone = startState.clone();//natStateClone.clone();
				actStateClone.performActionForCurrentAgent(act);
				actStateClone.performActionForCurrentAgent(natureSamp);
				EventOfcMctsSimple finalAct = Heuristics.completion(actStateClone.boxFront, actStateClone.boxMiddle, actStateClone.boxBack, actStateClone.cardsToBeBoxed);
				actStateClone.performActionForCurrentAgent(finalAct);
				double rwrd = EvaluatorFacade.evaluate(actStateClone.boxFront, actStateClone.boxMiddle, actStateClone.boxBack, false);
				double rewardAcc = rewards.get(act) + rwrd;
				rewards.put(act, rewardAcc);
				i++;

				if (Config.DEBUG_PRINT) {
					debugOnEstimateSample(act, actStateClone, natureSamp, epoch, rewardAcc, rwrd);
				}
			}
		}

		Map.Entry<EventOfcMctsSimple, Double> maxEntry = rewards.entrySet().stream().max(Comparator.comparing(Map.Entry::getValue)).get();

		System.out.println(String.format("iter = %d; actions.count = %d", i, actions.size()));
		if (Config.DEBUG_PRINT) onEndSearch();
		return maxEntry.getKey();
	}

	protected static void debugOnEstimateSample(EventOfcMctsSimple action, GameOfcMctsSimple state, EventOfcMctsSimple sample, int actionIterNum, double actionRewardAcc, double actionReward) {
//		System.out.println(String.format("%f %s", EvaluatorFacade.evaluate(state.boxFront, state.boxMiddle, state.boxBack, false), sample));
//		if (actionIterNum % 100 == 0) {
			if (csv.isEmpty())
				csv.add("epoch;branch;rewardAcc;state;reward");
			csv.add(String.format(java.util.Locale.US, "%d;%s;%.4f;%s;%.4f", actionIterNum, action.toString().replaceAll(";", ""), actionRewardAcc, state.getStateStr().replaceAll(";", ""), actionReward));
//		}
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
