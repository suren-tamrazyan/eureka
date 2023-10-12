package solver.mcts;

import com.rits.cloning.Cloner;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class MctsTreeNode<StateT extends MctsDomainState<ActionT, AgentT>, ActionT, AgentT extends MctsDomainAgent> {

    private final MctsTreeNode<StateT, ActionT, AgentT> parentNode;
    private final ActionT incomingAction;
    private final StateT representedState;
    private int visitCount;
    private double totalReward;
    private List<MctsTreeNode<StateT, ActionT, AgentT>> childNodes;
    private final Cloner cloner;

    protected MctsTreeNode(StateT representedState, Cloner cloner) {
        this(null, null, representedState, cloner);
    }

    private MctsTreeNode(MctsTreeNode<StateT, ActionT, AgentT> parentNode, ActionT incomingAction,
                         StateT representedState, Cloner cloner) {
        this.parentNode = parentNode;
        this.incomingAction = incomingAction;
        this.representedState = representedState;
        this.visitCount = 0;
        this.totalReward = 0.0;
        this.childNodes = new ArrayList<>();
        this.cloner = cloner;
    }

    protected MctsTreeNode<StateT, ActionT, AgentT> getParentNode() {
        return parentNode;
    }

    protected ActionT getIncomingAction() {
        return incomingAction;
    }

    public int getVisitCount() {
        return visitCount;
    }

    protected int getParentsVisitCount() {
        return parentNode.getVisitCount();
    }

    public List<MctsTreeNode<StateT, ActionT, AgentT>> getChildNodes() {
        return childNodes;
    }

    protected boolean hasChildNodes() {
        return childNodes.size() > 0;
    }

    protected boolean representsTerminalState() {
        return representedState.isTerminal();
    }

    protected AgentT getRepresentedStatesPreviousAgent() {
        return representedState.getPreviousAgent();
    }

    protected boolean representedStatesCurrentAgentHasAvailableActions() {
        return representedState.getNumberOfAvailableActionsForCurrentAgent() > 0;
    }

    protected boolean isFullyExpanded() {
        return representedState.getNumberOfAvailableActionsForCurrentAgent() == childNodes.size();
    }

    protected boolean hasUnvisitedChild () {
        return childNodes.stream()
                .anyMatch(MctsTreeNode::isUnvisited);
    }

    private boolean isUnvisited() {
        return visitCount == 0;
    }

    protected MctsTreeNode<StateT, ActionT, AgentT> addNewChildWithoutAction() {
        StateT childNodeState = getDeepCloneOfRepresentedState();
        childNodeState.skipCurrentAgent();
        return appendNewChildInstance(childNodeState, null);
    }

    protected MctsTreeNode<StateT, ActionT, AgentT> addNewChildFromAction(ActionT action) {
        if (!isUntriedAction(action))
            throw new IllegalArgumentException("Error: invalid action passed as function parameter");
        else
            return addNewChildFromUntriedAction(action);
    }

    private boolean isUntriedAction(ActionT action) {
//        return getUntriedActionsForCurrentAgent().contains(action);
    	return childNodes.stream().map(MctsTreeNode::getIncomingAction).noneMatch(act -> act.equals(action));
    }

    protected ActionT getRandomActionFromUntriedActions() {
    	List<ActionT> availableActions = representedState.getAvailableActionsForCurrentAgent();
    	Set<ActionT> triedActions = new HashSet<>(getTriedActionsForCurrentAgent());
    	if (this.representedStateCurrentAgentActionsIsOrderedMode()) {
    		for (ActionT act : availableActions)
    			if (!triedActions.contains(act))
    				return act;
    	} else {
    		Set<ActionT> untriedActions = new HashSet<>(availableActions);
    		untriedActions.removeAll(triedActions);
    		
            int idx = 0;
            int rnd = ThreadLocalRandom.current().nextInt(untriedActions.size());
            for (ActionT act : untriedActions) {
            	if (idx == rnd)
            		return act;
            	idx++;
            }
    	}
    	System.out.println("the end");
    	return null;
    }
    protected Set<ActionT> getUntriedActionsForCurrentAgent() {
        List<ActionT> availableActions = representedState.getAvailableActionsForCurrentAgent();
        
        if (this.representedStateCurrentAgentActionsIsOrderedMode()) {
        	Set<ActionT> res = new HashSet<>();
        	Set<ActionT> tried = new HashSet<>(getTriedActionsForCurrentAgent());
        	for (ActionT act : availableActions) {
        		if (!tried.contains(act)) {
        			res.add(act);
        			return res;
        		}
        	}
        }
        
//        List<ActionT> untriedActions = new ArrayList<>(availableActions);
//        long time = Misc.getTime();
        Set<ActionT> untriedActions = new HashSet<>(availableActions);
//        OfcMctsTest.untriedTime1 += (Misc.getTime() - time);
//        time = Misc.getTime();
        List<ActionT> triedActions = getTriedActionsForCurrentAgent();
//        OfcMctsTest.untriedTime2 += (Misc.getTime() - time);
//        time = Misc.getTime();
        untriedActions.removeAll(triedActions);
//        OfcMctsTest.untriedTime3 += (Misc.getTime() - time);
        return untriedActions;
    }

    private List<ActionT> getTriedActionsForCurrentAgent() {
        return childNodes.stream()
                .map(MctsTreeNode::getIncomingAction)
                .collect(Collectors.toList());
    }

    private MctsTreeNode<StateT, ActionT, AgentT> addNewChildFromUntriedAction(ActionT incomingAction) {
        StateT childNodeState = getNewStateFromAction(incomingAction);
        return appendNewChildInstance(childNodeState, incomingAction);
    }

    private StateT getNewStateFromAction(ActionT action) {
        StateT representedStateClone = getDeepCloneOfRepresentedState();
        representedStateClone.performActionForCurrentAgent(action);
        return representedStateClone;
    }

    public StateT getDeepCloneOfRepresentedState() {
//    	return cloner.deepClone(representedState);
    	representedState.beforeCloning();
        return cloner.deepClone(representedState);
    }

    private MctsTreeNode<StateT, ActionT, AgentT> appendNewChildInstance(
            StateT representedState, ActionT incomingAction) {
        MctsTreeNode<StateT, ActionT, AgentT> childNode = new MctsTreeNode<>(
                this, incomingAction, representedState, cloner);
        childNodes.add(childNode);
        return childNode;
    }

    protected void updateDomainTheoreticValue(double rewardAddend) {
        visitCount += 1;
        totalReward += rewardAddend;
    }

    public double getDomainTheoreticValue() {
        return totalReward / visitCount;
    }
    
    public boolean representedStateCurrentAgentActionsIsOrderedMode() {
    	return representedState.currentAgentActionsIsOrderedMode();
    }
    
//    public double getTotalReward() {
//    	return totalReward;
//    }
}