package solver.rummy.indian.test;

import game.Card;
import solver.rummy.indian.*;
import util.Misc;

import java.util.Arrays;
import java.util.List;

public class Test2 {
    public static void draw() {
        int times = 5000;
        List<Card> hand = Arrays.asList(Card.str2Cards("4c, Ts, Kh, Js, 2d, Kh, 7h, Xr, 9c, 8c, Ks, Qc, 3d"));
        Card wildcard = Card.getCard("5h");
        Card topDiscardPile = Card.getCard("Js");
        List<Card> knownDiscardedCards = Arrays.asList(Card.str2Cards("Ad Jc Js"));
        DecisionPhase phase = DecisionPhase.DRAW;
        int DECK_COUNT = 2;

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
//        Config.DEPTH_OF_SEARCH = 2;
        List<Card> hand = Arrays.asList(Card.str2Cards("3h, 7h, 7c, Xr, 7s, Kd, 8s, 4h, 7d, Ad, Qd, 2d, 3c, 5s"));
        Card wildcard = Card.getCard("Jc");
        Card topDiscardPile = null;//Card.getCard("");
        List<Card> knownDiscardedCards = Arrays.asList(Card.str2Cards("5d 8c 6c Ac As 3s Kc Td Jh"));
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
