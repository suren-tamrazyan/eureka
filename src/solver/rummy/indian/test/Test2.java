package solver.rummy.indian.test;

import game.Card;
import solver.rummy.indian.*;
import solver.rummy.indian.meld.MeldNode;
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
            RunResult result = Runner.run(hand, knownDiscardedCards, wildcard, topDiscardPile, phase, DECK_COUNT, times);
            if (((DrawMove)result.decision).drawFromDiscardPile)
                pileCnt++;
            else
                deckCnt++;
            System.out.println(result.decision + " " + (Misc.getTime() - timeBefore));
            System.out.println(result.description);
        }
        System.out.println(String.format("pileCnt = %d, deckCnt = %d", pileCnt, deckCnt));
        // pileCnt = 9, deckCnt = 1; why deckCnt = 1??
    }

    public static void discard() {
        int times = 10000;
//        Config.DEPTH_OF_SEARCH = 2;
        List<Card> hand = Arrays.asList(Card.str2Cards("2d 3d 2c 3c Kh Ks Xr 5d 6d 8d 7s 6c 7c 8c"));
        Card wildcard = Card.getCard("7d");
        Card topDiscardPile = null;//Card.getCard("");
        List<Card> knownDiscardedCards = Arrays.asList(Card.str2Cards("Kd, Jd, 9h, Js, Th, 6s, 7d, Qc, 2s, 5h, Ah"));
        DecisionPhase phase = DecisionPhase.DISCARD;
        int DECK_COUNT = 1;

        for (int i = 0; i < 5; i++) {
            long timeBefore = Misc.getTime();
            AdditionalData ad = new AdditionalData();
            ad.oppDiscardedCards.addAll(Arrays.asList(Card.str2Cards("Kd, 9h, Th, 6c, 8d, Ks")));
            ad.oppTakenOpenCards.addAll(Arrays.asList(Card.str2Cards("Qc, 5h, Ah")));
            RunResult result = Runner.run(hand, knownDiscardedCards, wildcard, topDiscardPile, phase, DECK_COUNT, times, ad);
            System.out.println(result.decision + " " + (Misc.getTime() - timeBefore));
            System.out.println(result.description);
        }
    }

    public static void meld() {
//        List<Card> hand = Arrays.asList(Card.str2Cards("5c, Js, Tc, Xr, Th, Td, Ac, Qs, 9s, 6h, 3c, Ts, Qc"));
        List<Card> hand = Arrays.asList(Card.str2Cards("5c, Js, Tc, Xr, Th, Td, Ac, Qs, 9s, 9c, 3c, Ts, Qc"));
        Card wildcard = Card.getCard("5s");

        long timeBefore = Misc.getTime();
        MeldNode minleaf = Runner.getMinvalueLeaf(hand, wildcard);
        System.out.println("time:  " + (Misc.getTime() - timeBefore));
        System.out.println("Melds: " + minleaf.gatherMelds());
        System.out.println("MinValue: " + minleaf.unassembledCards + " " + minleaf.value(wildcard.getRank()));
    }

    public static void main(String[] args) {
//        draw();
//        discard();
        meld();
    }

}
