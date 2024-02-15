package solver.rummy.indian;

import game.Card;
import solver.mcts.Mcts;
import solver.mcts.MctsTreeNode;
import solver.rummy.indian.meld.MeldNode;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Heuristics {
    public static class ActionValueScore {
        public Action action;
        public double value;
        public int score;
        public ActionValueScore(Action action, double value, int score) {
            this.action = action;
            this.value = value;
            this.score = score;
        }
    }
    private final static double SELECT_THRESHOLD_PERC = 0.2;
    private final static int NEIGHBORS_DISTANCE = 2;
    public static RunResult handleDiscardMove(AdditionalData additionalData, Mcts<State, Action, Agent> mcts, Card wildcard, String description) {
        String heuristicsDescr = "";
        List<Card> hand = mcts.getRootNode().getChildNodes().stream().map(x -> ((DiscardMove) x.getIncomingAction()).discard.card).collect(Collectors.toList());
        MeldNode minleaf = Runner.getMinvalueLeaf(hand, wildcard);
        heuristicsDescr += String.format("\n---------------------\nMinLeaf: %s\n",  minleaf.gatherMelds());
        List<MctsTreeNode<State, Action, Agent>> selectedNodes = selectNodes(mcts.getRootNode().getChildNodes(), minleaf.unassembledCards);
        heuristicsDescr += String.format("\n---------------------\nSelectedNodes (threshold: %f):\n", SELECT_THRESHOLD_PERC) + selectedNodes.stream().map(x -> String.format("- %s, visit = %d, val = %f", x.getIncomingAction(), x.getVisitCount(), x.getDomainTheoreticValue())).collect(Collectors.joining("\n"));
        List<ActionValueScore> lstScored = selectedNodes.stream().map(x -> new ActionValueScore(x.getIncomingAction(), x.getDomainTheoreticValue(), score(x.getIncomingAction(), additionalData, wildcard))).collect(Collectors.toList());
        heuristicsDescr += "\n---------------------\nScoredNodes:\n" + lstScored.stream().map(x -> String.format("- %s, score = %d, val = %f", x.action, x.score, x.value)).collect(Collectors.joining("\n"));
        heuristicsDescr += String.format("\n---------------------\nOppTakenOpenCards: %s\nOppDiscardedCards: %s\nOppIgnoredOpenCards: %s\n---------------------\n", additionalData.oppTakenOpenCards, additionalData.oppDiscardedCards, additionalData.oppIgnoredOpenCards);
        Action decision = lstScored.stream().sorted(Comparator.comparing((ActionValueScore x) -> x.score, Comparator.reverseOrder()).thenComparing((ActionValueScore x) -> x.value, Comparator.reverseOrder())).map(x -> x.action).findFirst().get();
        return new RunResult(new DiscardMoveResult(((DiscardMove) decision).discard.card, false), description + heuristicsDescr);
    }

    private static boolean isNeighbour(Card card1, Card card2, int distance) {
        return card1.getSuit() == card2.getSuit()
                && (Math.abs(card1.getRank() - card2.getRank()) <= distance
                    || (card1.getRank() == 0 && card2.getRank() == 12)
                    || (card1.getRank() == 12 && card2.getRank() == 0));
    }
    private static int score(Action action, AdditionalData additionalData, Card wildcard) {
        int result = 0;
        Card card = ((DiscardMove) action).discard.card;
        // case #1
        if (isNeighbour(card, wildcard, NEIGHBORS_DISTANCE))
            result++;
        // case #2
        for (Card card2 : additionalData.oppDiscardedCards) { // TODO use position in list for evaluation
            if (isNeighbour(card, card2, NEIGHBORS_DISTANCE))
                result++;
        }
        // case #3
        for (Card card2 : additionalData.oppTakenOpenCards) { // TODO use position in list for evaluation
            if (isNeighbour(card, card2, NEIGHBORS_DISTANCE))
                result--;
        }
        // case 4
        for (Card card2 : additionalData.oppIgnoredOpenCards) { // TODO use position in list for evaluation
            if (isNeighbour(card, card2, 1))
                result++;
        }
        return result;
    }
    private static List<MctsTreeNode<State, Action, Agent>> selectNodes(List<MctsTreeNode<State, Action, Agent>> lst, List<Card> unassembled) {
        List<MctsTreeNode<State, Action, Agent>> sortedLst = new ArrayList<>(lst);
        sortedLst.sort(((o1, o2) -> Double.compare(o2.getDomainTheoreticValue(), o1.getDomainTheoreticValue())));
        double totalValue = 0;
        for (MctsTreeNode<State, Action, Agent> node : sortedLst) {
            totalValue += node.getDomainTheoreticValue();
        }
        double threshold = SELECT_THRESHOLD_PERC * totalValue;
        double selectedValue = 0;
        List<MctsTreeNode<State, Action, Agent>> selectedActions = new ArrayList<>();
        boolean isFirst = true;
        for (MctsTreeNode<State, Action, Agent> node : sortedLst) {
            if (isFirst) {
                selectedValue += Math.abs(node.getDomainTheoreticValue());
                selectedActions.add(node);
                isFirst = false;
                continue;
            }

            if (!unassembled.contains(((DiscardMove) node.getIncomingAction()).discard.card))
                continue;

            if (selectedValue + Math.abs(node.getDomainTheoreticValue()) <= Math.abs(threshold)) {
                selectedValue += Math.abs(node.getDomainTheoreticValue());
                selectedActions.add(node);
            } else {
                break;
            }
        }

        return selectedActions;
    }
}
