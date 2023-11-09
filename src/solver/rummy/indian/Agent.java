package solver.rummy.indian;

import solver.mcts.MctsDomainAgent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Agent implements MctsDomainAgent<State> {
    @Override
    public State getTerminalStateByPerformingSimulationFromState(State state) {
        while (!state.isTerminal()) {
            Action action = getBiasedOrRandomActionFromStatesAvailableActions(state);
            state.performActionForCurrentAgent(action);
        }
        return state;
    }

    private Action getBiasedOrRandomActionFromStatesAvailableActions(State state) {
        List<Action> availableActions = state.getAvailableActionsForCurrentAgent();//.stream().filter(action -> action instanceof DrawMove || !((DiscardMove)action).isJoker(state.wildcardRank)).collect(Collectors.toList());
        int index = ThreadLocalRandom.current().nextInt(availableActions.size());
        if (state.phase == DecisionPhase.DISCARD) {
            index = max(state.rootMeldsTree.statistics); //softmax(state.rootMeldsTree.statistics);
//            switch (Config.GOAL) {
//                case COMPLETABLE_DISTANCE:
//                    index = max(state.rootMeldsTree.statistics); //softmax(state.rootMeldsTree.statistics);
//                    break;
//                case MIN_LEAF_VALUE: {
//                    int minval = Integer.MAX_VALUE;
//                    for (int i = 0; i < availableActions.size(); i++) {
//                        State state1 = state.clone();
//                        state1.performActionForCurrentAgent(availableActions.get(i));
//                        state1.buildMeldsTree();
//                        if (state1.rootMeldsTree.minValue < minval) {
//                            minval = state1.rootMeldsTree.minValue;
//                            index = i;
//                        }
//                    }
//                }
//                case COMBINATION:
//                    List<Integer> production = new ArrayList<>(state.rootMeldsTree.statistics.size());
//                    for (int i = 0; i < state.rootMeldsTree.statistics.size(); i++) {
//                        production.add(state.rootMeldsTree.statistics.get(i) * Utils.value(state.rootMeldsTree.unassembledCards.get(i), state.wildcardRank));
//                    }
//                    index = softmax(production);
//                    break;
//            }

        }
        return availableActions.get(index);
    }

    public static int softmax(List<Integer> statistics) {
        int sumOfStatistics = statistics.stream().mapToInt(Integer::intValue).sum();

        int randomValue = ThreadLocalRandom.current().nextInt(sumOfStatistics);

        int cumulative = 0;
        for (int i = 0; i < statistics.size(); i++) {
            cumulative += statistics.get(i);
            if (randomValue < cumulative) {
                return i;
            }
        }
        // if outbounds
        return statistics.size() - 1;
    }

    public static int max(List<Integer> statistics) {
        // select one of maximum values from statistics
        int max = Collections.max(statistics);
        List<Integer> indexOf = new ArrayList<>();
        for (int i = 0; i < statistics.size(); i++) {
            if (statistics.get(i) == max)
                indexOf.add(i);
        }
        return indexOf.get(ThreadLocalRandom.current().nextInt(indexOf.size()));
    }

    @Override
    public double getRewardFromTerminalState(State terminalState) {
        switch (Config.GOAL) {
            case COMPLETABLE_DISTANCE:
                if (terminalState.solution == null) {
                    return -terminalState.round * 2;
                }
                return -terminalState.round;
            case MIN_LEAF_VALUE:
                return -terminalState.rootMeldsTree.minValue;
            case SUM_MIN_VALUE_OF_PATH:
                if (terminalState.solution == null) {
                    return -terminalState.sumMinValueOfPath * 2;
                }
                return -terminalState.sumMinValueOfPath;
            case SUM_DELTA_MIN_VALUE_OF_PATH:
                if (terminalState.solution == null) {
                    return -terminalState.sumDeltaMinValueOfPath * 2;
                }
                return -terminalState.sumDeltaMinValueOfPath;
        }
        return 0;
    }
}
