package solver.ofc.mcts;

import com.rits.cloning.Cloner;

//import util.Misc;

//import java.util.Collections;
//import java.util.List;
//import java.util.Comparator;
//import java.util.Set;

public class Mcts<StateT extends MctsDomainState<ActionT, AgentT>, ActionT, AgentT extends MctsDomainAgent<StateT>> {

    private static final double NO_EXPLORATION = 0;

    private final int numberOfIterations;
    private int iterationsCount;
    private double explorationParameter;
    private final Cloner cloner;
    
    public int getIterationsCount() {
    	return iterationsCount;
    }

    public static<StateT extends MctsDomainState<ActionT, AgentT>, ActionT, AgentT extends MctsDomainAgent<StateT>>
        Mcts<StateT, ActionT, AgentT> initializeIterations(int numberOfIterations) {
            Cloner cloner = new Cloner();
            return new Mcts<>(numberOfIterations, cloner);
    }

    private Mcts(int numberOfIterations, Cloner cloner) {
        this.numberOfIterations = numberOfIterations;
        this.cloner = cloner;
    }

    public void dontClone(final Class<?>... classes) {
        cloner.dontClone(classes);
    }

    public ActionT uctSearchWithExploration(StateT state, double explorationParameter, long timeDurationMs, long timeLimitMs) {
        setExplorationForSearch(explorationParameter);
        MctsTreeNode<StateT, ActionT, AgentT> rootNode = new MctsTreeNode<>(state, cloner);
        long timeDurationNano = timeDurationMs * 1000000;
        long timeLimitNano = timeLimitMs * 1000000;
        long time = 0;
        long timeStart = System.nanoTime();
        iterationsCount = 0;
        while ((timeDurationMs > 0 && time < timeDurationNano) || (timeDurationMs <= 0 && iterationsCount < numberOfIterations && time < timeLimitNano)) { // this variant as switcher
//        while ((timeLimitMs <= 0 || time < timeLimitNano) && (iterationsCount < numberOfIterations)) { // this variant as preventer
            performMctsIteration(rootNode, state.getCurrentAgent());
            time = System.nanoTime() - timeStart;
            iterationsCount++;
        }
//      LinkedHashMap<String, Integer> sortedMap = new LinkedHashMap<>();
//      rootNode.getChildNodes().stream().sorted(Comparator.comparingInt(MctsTreeNode<StateT, ActionT, AgentT>::getVisitCount).reversed()).forEachOrdered(x -> sortedMap.put(((GameOfcMctsSimple)x.representedState).getStateStr(), x.getVisitCount()));
//      for (Map.Entry<String, Integer> ent : sortedMap.entrySet()) {
//      	System.out.println(String.format("%s: %d", ent.getKey(), ent.getValue()));
//      }
        return getNodesMostPromisingAction(rootNode);
    }

    private void setExplorationForSearch(double explorationParameter) {
        this.explorationParameter = explorationParameter;
    }

    private void performMctsIteration(MctsTreeNode<StateT, ActionT, AgentT> rootNode, AgentT agentInvoking) {
        MctsTreeNode<StateT, ActionT, AgentT> selectedChildNode = treePolicy(rootNode);
        StateT terminalState = getTerminalStateFromDefaultPolicy(selectedChildNode, agentInvoking);
        backPropagate(selectedChildNode, terminalState);
    }

    private MctsTreeNode<StateT, ActionT, AgentT> treePolicy(MctsTreeNode<StateT, ActionT, AgentT> node) {
        while (!node.representsTerminalState()) {
            if (!node.representedStatesCurrentAgentHasAvailableActions())
                return expandWithoutAction(node);
            else if (!node.isFullyExpanded())
                return expandWithAction(node);
            else
                node = getNodesBestChild(node);
        }
        return node;
    }


    private MctsTreeNode<StateT, ActionT, AgentT> expandWithoutAction(MctsTreeNode<StateT, ActionT, AgentT> node) {
        return node.addNewChildWithoutAction();
    }

    private MctsTreeNode<StateT, ActionT, AgentT> expandWithAction(MctsTreeNode<StateT, ActionT, AgentT> node) {
        ActionT randomUntriedAction = getRandomActionFromNodesUntriedActions(node);
        return node.addNewChildFromAction(randomUntriedAction);
    }

    private ActionT getRandomActionFromNodesUntriedActions(MctsTreeNode<StateT, ActionT, AgentT> node) {
    	return node.getRandomActionFromUntriedActions();
/*    	
        Set<ActionT> untriedActions = node.getUntriedActionsForCurrentAgent();
        
//    	if (node.representedStateCurrentAgentActionsIsOrderedMode()) {
//    		// toString is any attribute for ordering; Order for robust 
//    		return untriedActions.stream().min(Comparator.comparing(Object::toString)).orElse(null);
//    	}

        int idx = 0;
        int rnd = Misc.rand.nextInt(untriedActions.size());
        for (ActionT act : untriedActions) {
        	if (idx == rnd)
        		return act;
        	idx++;
        }
        return null;
        
//        return untriedActions.get(Misc.rand.nextInt(untriedActions.size()));
//        Collections.shuffle(untriedActions);
//        return untriedActions.get(0);
 * 
 */
    }

    private MctsTreeNode<StateT, ActionT, AgentT> getNodesBestChild(MctsTreeNode<StateT, ActionT, AgentT> node) {
        validateBestChildComputable(node);
        return getNodesBestChildConfidentlyWithExploration(node, explorationParameter);
    }

    private void validateBestChildComputable(MctsTreeNode<StateT, ActionT, AgentT> node) {
        if (!node.hasChildNodes())
            throw new UnsupportedOperationException("Error: operation not supported if child nodes empty");
        else if (!node.isFullyExpanded())
            throw new UnsupportedOperationException("Error: operation not supported if node not fully expanded");
        else if (node.hasUnvisitedChild())
            throw new UnsupportedOperationException(
                    "Error: operation not supported if node contains an unvisited child");
    }

    private ActionT getNodesMostPromisingAction(MctsTreeNode<StateT, ActionT, AgentT> node) {
        validateBestChildComputable(node);
        MctsTreeNode<StateT, ActionT, AgentT> bestChildWithoutExploration =
                getNodesBestChildConfidentlyWithExploration(node, NO_EXPLORATION);
        return bestChildWithoutExploration.getIncomingAction();
    }

    private MctsTreeNode<StateT, ActionT, AgentT> getNodesBestChildConfidentlyWithExploration(
            MctsTreeNode<StateT, ActionT, AgentT> node, double explorationParameter) {
        return node.getChildNodes().stream()
                .max((node1, node2) -> Double.compare(
                        calculateUctValue(node1, explorationParameter),
                        calculateUctValue(node2, explorationParameter))).get();
    }

    private double calculateUctValue(MctsTreeNode<StateT, ActionT, AgentT> node, double explorationParameter) {
        return node.getDomainTheoreticValue()
                + explorationParameter
                * (Math.sqrt((2 * Math.log(node.getParentsVisitCount())) / node.getVisitCount()));
    }

    private StateT getTerminalStateFromDefaultPolicy(
            MctsTreeNode<StateT, ActionT, AgentT> node, AgentT agentInvoking) {
        StateT nodesStateClone = node.getDeepCloneOfRepresentedState();
        return agentInvoking.getTerminalStateByPerformingSimulationFromState(nodesStateClone);
    }

    private void backPropagate(MctsTreeNode<StateT, ActionT, AgentT> node, StateT terminalState) {
        while (node != null) {
            updateNodesDomainTheoreticValue(node, terminalState);
            node = node.getParentNode();
        }
    }

    private void updateNodesDomainTheoreticValue(MctsTreeNode<StateT, ActionT, AgentT> node, StateT terminalState) {
        // violation of the law of demeter
        AgentT parentsStatesCurrentAgent = node.getRepresentedStatesPreviousAgent();
        double reward = parentsStatesCurrentAgent.getRewardFromTerminalState(terminalState);
        node.updateDomainTheoreticValue(reward);
    }
}