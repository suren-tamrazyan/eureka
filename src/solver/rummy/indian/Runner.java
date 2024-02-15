package solver.rummy.indian;

import game.Card;
import solver.mcts.Mcts;
import solver.mcts.MctsCallback;
import solver.mcts.MctsTreeNode;
import solver.rummy.indian.meld.MeldNode;
import solver.rummy.indian.meld.Utils;

import java.util.*;
import java.util.stream.Collectors;

public class Runner {

    public static RunResult run(Collection<Card> heroHand, Collection<Card> knownDiscardedCards, Card wildcard, Card topDiscardPile, DecisionPhase phase, int deckCount, int timeDurationMs) {
        AdditionalData additionalData = new AdditionalData();
        return run(heroHand, knownDiscardedCards, wildcard, topDiscardPile, phase, deckCount, timeDurationMs, null, additionalData);
    }
    public static RunResult run(Collection<Card> heroHand, Collection<Card> knownDiscardedCards, Card wildcard, Card topDiscardPile, DecisionPhase phase, int deckCount, int timeDurationMs, Card hiddenCard) {
        AdditionalData additionalData = new AdditionalData();
        return run(heroHand, knownDiscardedCards, wildcard, topDiscardPile, phase, deckCount, timeDurationMs, hiddenCard, additionalData);
    }
    public static RunResult run(Collection<Card> heroHand, Collection<Card> knownDiscardedCards, Card wildcard, Card topDiscardPile, DecisionPhase phase, int deckCount, int timeDurationMs, AdditionalData additionalData) {
        return run(heroHand, knownDiscardedCards, wildcard, topDiscardPile, phase, deckCount, timeDurationMs, null, additionalData);
    }
    public static RunResult run(Collection<Card> heroHand, Collection<Card> knownDiscardedCards, Card wildcard, Card topDiscardPile, DecisionPhase phase, int deckCount, int timeDurationMs, Card hiddenCard, AdditionalData additionalData) {
//        final int NUMBER_OF_ITERATIONS = 100000;
//        final double EXPLORATION_PARAMETER = 1.41;
        final int TIME_LIMIT_MS = Integer.MAX_VALUE;
        MctsCallback callback = null;
        MctsCallback callbackDebug = null;//Config.DEBUG_PRINT ? new DebugPrinter() : null;
        Mcts<State, Action, Agent> mcts = Mcts.initializeIterations(Config.NUMBER_OF_ITERATIONS, callback, callbackDebug, true);
        mcts.dontClone(Agent.class, MeldNode.class);
        State state = new State();
        state.init(heroHand, knownDiscardedCards, wildcard.getRank(), topDiscardPile, phase, deckCount, hiddenCard);
        if (phase == DecisionPhase.DISCARD && state.solution != null && state.solution.unassembledCards.size() == 1) { // unassembledCards.size() == 0 need deep figure out
            Card discard = state.solution.unassembledCards.get(0);
            return new RunResult(new DiscardMoveResult(discard, true), "Direct solving.");
        }
        Action decision = mcts.uctSearchWithExploration(state, Config.EXPLORATION_PARAMETER, timeDurationMs, TIME_LIMIT_MS);
        String description = String.format("Hand: %s\nKnownDiscardedCards: %s\nMCTS: (mcts.iterationCount = %d)\n", heroHand, knownDiscardedCards, mcts.getIterationsCount());
        description += mcts.getRootNode().getChildNodes().stream().sorted(((o1, o2) -> Double.compare(o2.getDomainTheoreticValue(), o1.getDomainTheoreticValue()))).map(x -> String.format("- %s, visit = %d, val = %f", x.getIncomingAction(), x.getVisitCount(), x.getDomainTheoreticValue())).collect(Collectors.joining("\n"));
        if (decision instanceof DiscardMove) {
            RunResult discardResult = Heuristics.handleDiscardMove(additionalData, mcts, wildcard, description);
            if (((DiscardMoveResult)discardResult.decision).discard != ((DiscardMove) decision).discard.card)
                discardResult.description += String.format("\nDecision was changed by heuristics. First decision: %s, actual discard: %s\n---------------------\n", ((DiscardMove) decision).discard.card, ((DiscardMoveResult)discardResult.decision).discard);
            return discardResult;
//            decision = new DiscardMoveResult(((DiscardMove) decision).discard.card, false);
        }
        if (decision instanceof DrawMove)
            decision = new DrawMoveResult(((DrawMove)decision).drawFromDiscardPile, mcts.getRootNode().getDomainTheoreticValue());
        return new RunResult(decision, description);
    }

    public static MeldNode getMinvalueLeaf(Collection<Card> heroHand, Card wildcard) {
        List<Card> hand = new ArrayList<>(heroHand);
        int wildcardRank = wildcard.getRank();
        MeldNode rootMeldsTree = new MeldNode(hand);
        MeldNode solution = rootMeldsTree.depthFirstSearch(wildcardRank); // build tree
        if (solution != null && 1!=1)
            System.out.println(solution.gatherMelds());
        return rootMeldsTree.findMinValueLeaf(wildcardRank);
    }
    
    public static void main(String[] atgs) {
//    	List<Card> hand = Arrays.asList(Card.str2Cards("Kc 5s 2c 8d Ks Qc 6s 8s 3d 7s 3h 7c Kd"));
//    	Card wildcard = Card.getCard("Qd");
//    	MeldNode minleaf = Runner.getMinvalueLeaf(hand, wildcard);
//    	System.out.println(minleaf.gatherMelds());

        List<Card> hand = Arrays.asList(Card.str2Cards("5h 7h 9d Td 2s 6d 6c 6s 9h Th Xr 2h 3h Ah"));
        Card wildcard = Card.getCard("8s");
        MeldNode minleaf = Runner.getMinvalueLeaf(hand, wildcard);
        System.out.println("Melds: " + minleaf.gatherMelds());
        // List<Card> hand = Arrays.asList(Card.str2Cards("5h 7h 9d Td 2s 6d 6c 6s 9h Th Xr 2h 3h Ah"));
        // actual Melds: [[5h, 7h, 9d, Td, 2s], [6d, 6c, 6s], [9h, Th, Xr], [Ah, 2h, 3h]]
        // best Melds: [[5h, 9d, Td, 2s], [6d, 6c, 6s], [7h, 9h, Th, Xr], [Ah, 2h, 3h]]

        System.out.println();
        List<Card> unassembledCards = Arrays.asList(Card.str2Cards("5h 7h 9d Td 2s 6d 6c 6s 9h Th Xr"));
        Collection<Collection<Card>> impureSequences = Utils.findImpureSequences(unassembledCards, wildcard.getRank());
        System.out.println("New:");
        System.out.println(impureSequences);
        impureSequences = solver.rummy.indian.meld.Utils.old_findImpureSequences(unassembledCards, wildcard.getRank());
        System.out.println("Old:");
        System.out.println(impureSequences);
    }
}
