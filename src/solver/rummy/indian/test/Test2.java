package solver.rummy.indian.test;

import game.Card;
import solver.rummy.indian.Action;
import solver.rummy.indian.DecisionPhase;
import solver.rummy.indian.DrawMove;
import solver.rummy.indian.Runner;
import util.Misc;

import java.util.Arrays;
import java.util.List;

public class Test2 {
    public static void draw() {
        int times = 5000;
        List<Card> hand = Arrays.asList(Card.str2Cards("2s, As, Ah, Ad, Jc, Ks, 2h, Js, Th, 5d, 6d, Qs, 3d"));
        Card wildcard = Card.getCard("Ts");
        Card topDiscardPile = Card.getCard("2d");
        List<Card> knownDiscardedCards = Arrays.asList(Card.str2Cards("5s, 7c, 8s, 9h, 9c, Kd, Kc, 2d"));
        DecisionPhase phase = DecisionPhase.DRAW;
        int DECK_COUNT = 1;

        int pileCnt = 0, deckCnt = 0;
        for (int i = 0; i < 10; i++) {
            long timeBefore = Misc.getTime();
            Action decision = Runner.run(hand, knownDiscardedCards, wildcard, topDiscardPile, phase, DECK_COUNT, times);
            if (((DrawMove)decision).drawFromDiscardPile)
                pileCnt++;
            else
                deckCnt++;
            System.out.println(decision + " " + (Misc.getTime() - timeBefore));
        }
        System.out.println(String.format("pileCnt = %d, deckCnt = %d", pileCnt, deckCnt));
        // pileCnt = 9, deckCnt = 1; why deckCnt = 1??
    }

    public static void discard() {
        int times = 10000;
        List<Card> hand = Arrays.asList(Card.str2Cards("2s, As, Ah, Ad, Jc, Ks, 2h, Js, Th, 5d, 6d, Qs, 3d, 2d"));
        Card wildcard = Card.getCard("Ts");
        Card topDiscardPile = Card.getCard("2d");
        List<Card> knownDiscardedCards = Arrays.asList(Card.str2Cards("5s, 9h, 7c, 8s, 9c, Kd, Kc"));
        DecisionPhase phase = DecisionPhase.DISCARD;
        int DECK_COUNT = 1;

        for (int i = 0; i < 10; i++) {
            long timeBefore = Misc.getTime();
            Action decision = Runner.run(hand, knownDiscardedCards, wildcard, topDiscardPile, phase, DECK_COUNT, times);
            System.out.println(decision + " " + (Misc.getTime() - timeBefore));
        }
    }

    public static void main(String[] args) {
//        draw();
        discard();
    }

}
