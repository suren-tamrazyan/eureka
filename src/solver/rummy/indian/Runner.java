package solver.rummy.indian;

import game.Card;
import solver.mcts.Mcts;
import solver.mcts.MctsCallback;
import solver.rummy.indian.meld.MeldNode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Runner {
    public static Action run(Collection<Card> heroHand, Collection<Card> knownDiscardedCards, int round, Card wildcard, Card topDiscardPile, DecisionPhase phase, int deckCount) {
//        final int NUMBER_OF_ITERATIONS = 100000;
//        final double EXPLORATION_PARAMETER = 1.41;
        final int TIME_LIMIT_MS = Integer.MAX_VALUE;
        int timeDurationMs = 10000;
        MctsCallback callback = null;
        MctsCallback callbackDebug = null;//Config.DEBUG_PRINT ? new DebugPrinter() : null;
        Mcts<State, Action, Agent> mcts = Mcts.initializeIterations(Config.NUMBER_OF_ITERATIONS, callback, callbackDebug, true);
        mcts.dontClone(Agent.class, MeldNode.class);
        State state = new State();
        state.init(heroHand, knownDiscardedCards, round, wildcard.getRank(), topDiscardPile, phase, deckCount);
        if (phase == DecisionPhase.DISCARD && state.solution != null && state.solution.unassembledCards.size() == 1) { // unassembledCards.size() == 0 need deep figure out
            Card discard = state.solution.unassembledCards.get(0);
            return new DiscardMoveResult(discard, true);
        }
        Action decision = mcts.uctSearchWithExploration(state, Config.EXPLORATION_PARAMETER, timeDurationMs, TIME_LIMIT_MS);
        if (decision instanceof DiscardMove)
            decision = new DiscardMoveResult(((DiscardMove)decision).discard.card, false);
        System.out.println(String.format("best mcts.iterationCount = %d", mcts.getIterationsCount()));
        return decision;
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
}
