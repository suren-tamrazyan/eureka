package solver.ofc.mcs;

import game.Card;
import game.EventOfc;
import org.paukov.combinatorics3.Generator;
import solver.ofc.*;

import java.util.List;
import java.util.stream.Collectors;

public class FullTraversal {
    public static class Hand {

    }
    /**
     * the class makes a complete search of the tree for possible solutions to the GameOfcMcts and summarizes the result of the evaluations in the parent level
     */
    public static double evalPerspective(GameOfcMcts state, boolean print) {
        if (print)
            System.out.println(state.toString());

        double result = 0;

        List<EventOfcMcts> actions = state.getAvailableActionsForCurrentAgent();
        int dealSize = 3;
        List<Card> availableCards = state.getAvailableCards();
        List<EventOfcMcts> natureSamples = Generator.combination(availableCards).simple(dealSize).stream().map(x -> new EventOfcMcts(EventOfc.TYPE_DEAL_CARDS, state.heroName, Card.cards2Mask(x.toArray(new Card[0])))).collect(Collectors.toList());
//        List<EventOfcMcts> natureSamples = state.getAvailableActionsForCurrentAgent();
//        System.out.println("natureSamples: " + natureSamples.size());
        for (EventOfcMcts act : actions) {
            double actEval = 0;
            GameOfcMcts actStateClone = state.copy();
            actStateClone.performActionForCurrentAgent(act);
            if (actStateClone.hero().boxesIsFull())
                actEval += EvaluatorFacade.evaluate(actStateClone.hero().boxFront.toList(), actStateClone.hero().boxMiddle.toList(), actStateClone.hero().boxBack.toList(), false);
            else {
                for (EventOfcMcts natureSamp : natureSamples) {
                    GameOfcMcts natStateClone = actStateClone.copy();
                    natStateClone.performActionForCurrentAgent(natureSamp);
                    actEval += evalPerspective(natStateClone, false);
                }
            }
            result += actEval;
            if (print)
                System.out.println(String.format("act = %s, eval = %f", act.toString(), actEval));
        }
        return result;
    }
}
