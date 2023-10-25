package solver.rummy.indian;

import com.rits.cloning.IFastCloner;
import solver.mcts.MctsDomainAgent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

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
        if (state.phase == DecisionPhase.DISCARD)
            index = max(state.rootMeldsTree.statistics);;//softmax(state.rootMeldsTree.statistics);
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
        if (terminalState.solution == null) {
            System.out.println("terminalState.solution == null");
            return -terminalState.round * 2;
        }
        return -terminalState.round;
    }
}
