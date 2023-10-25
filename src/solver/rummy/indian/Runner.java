package solver.rummy.indian;

import game.Card;
import solver.mcts.Mcts;
import solver.mcts.MctsCallback;
import solver.rummy.indian.meld.MeldNode;

import java.util.Collection;

public class Runner {
    public static Action run(Collection<Card> heroHand, Collection<Card> knownDiscardedCards, int round, Card wildcard, Card topDiscardPile, DecisionPhase phase, int deckCount) {
//        final int NUMBER_OF_ITERATIONS = 100000;
//        final double EXPLORATION_PARAMETER = 1.41;
        final int TIME_LIMIT_MS = Integer.MAX_VALUE;
        int timeDurationMs = 0;
        MctsCallback callback = null;
        MctsCallback callbackDebug = null;//Config.DEBUG_PRINT ? new DebugPrinter() : null;
        Mcts<State, Action, Agent> mcts = Mcts.initializeIterations(Config.NUMBER_OF_ITERATIONS, callback, callbackDebug);
        mcts.dontClone(Agent.class, MeldNode.class);
        State state = new State();
        state.init(heroHand, knownDiscardedCards, round, wildcard.getRank(), topDiscardPile, phase, deckCount);
        Action decision = mcts.uctSearchWithExploration(state, Config.EXPLORATION_PARAMETER, timeDurationMs, TIME_LIMIT_MS);
        System.out.println(String.format("best mcts.iterationCount = %d", mcts.getIterationsCount()));
        return decision;
    }
}
