package solver.rummy.indian.test;

import game.Card;
import solver.rummy.indian.*;
import solver.rummy.indian.meld.MeldNode;
import util.Misc;

import java.util.Arrays;
import java.util.List;

public class Test1 {
    public static int timems = 10000;
    public static void test1() {
        List<Card> hand = Arrays.asList(Card.str2Cards("Jc 3d Th 8c Qs 5s Kd 9c 4c As 6s 4s Ad"));
        Card wildcard = Card.getCard("2d");
        Card topDiscardPile = Card.getCard("4d");
        List<Card> knownDiscardedCards = Arrays.asList(Card.str2Cards("4d"));
        DecisionPhase phase = DecisionPhase.DRAW;
        int DECK_COUNT = 1;

        int pileCnt = 0, deckCnt = 0;
        for (int i = 0; i < 10; i++) {
            long timeBefore = Misc.getTime();
            Action decision = Runner.run(hand, knownDiscardedCards, wildcard, topDiscardPile, phase, DECK_COUNT, timems);
            if (((DrawMove)decision).drawFromDiscardPile)
                pileCnt++;
            else
                deckCnt++;
            System.out.println(decision + " " + (Misc.getTime() - timeBefore));
        }
        System.out.println(String.format("pileCnt = %d, deckCnt = %d", pileCnt, deckCnt));
    }

    public static void test2() {
        List<Card> hand = Arrays.asList(Card.str2Cards("Ac Qs Xr 4h 9d 5d As 3c 7c Jc 2h Qd 6d"));
        Card wildcard = Card.getCard("9s");
//        Card topDiscardPile = Card.getCard("7h");
        Card topDiscardPile = Card.getCard("3h");
        List<Card> knownDiscardedCards = Arrays.asList(Card.str2Cards("3h"));
        DecisionPhase phase = DecisionPhase.DRAW;
        int DECK_COUNT = 1;

        int pileCnt = 0, deckCnt = 0;
        for (int i = 0; i < 10; i++) {
            long timeBefore = Misc.getTime();
            Action decision = Runner.run(hand, knownDiscardedCards, wildcard, topDiscardPile, phase, DECK_COUNT, timems);
            if (((DrawMove)decision).drawFromDiscardPile)
                pileCnt++;
            else
                deckCnt++;
            System.out.println(decision + " " + (Misc.getTime() - timeBefore));
        }
        System.out.println(String.format("pileCnt = %d, deckCnt = %d", pileCnt, deckCnt));
    }

    public static void test3() {
        List<Card> hand = Arrays.asList(Card.str2Cards("7d 3h Ah 4s 8d 9h Ts Qh Jc 9c 9d 4h Ad Td"));
        Card wildcard = Card.getCard("2d");
        Card topDiscardPile = Card.getCard("5s");
        List<Card> knownDiscardedCards = Arrays.asList(Card.str2Cards("5s"));
        DecisionPhase phase = DecisionPhase.DISCARD;
        int DECK_COUNT = 1;

        int pileCnt = 0, deckCnt = 0;
        for (int i = 0; i < 10; i++) {
            long timeBefore = Misc.getTime();
            Action decision = Runner.run(hand, knownDiscardedCards, wildcard, topDiscardPile, phase, DECK_COUNT, timems);
            System.out.println(decision + " " + (Misc.getTime() - timeBefore));
        }
        // TS
    }

    public static void test4() {
        List<Card> hand = Arrays.asList(Card.str2Cards("7d 3h Ah 4s 8d 9h Qh Jc 9c 9d 4h Td 4d"));
        Card wildcard = Card.getCard("2d");
        Card topDiscardPile = Card.getCard("6d");
        List<Card> knownDiscardedCards = Arrays.asList(Card.str2Cards("5s Ts Qd Kd 8h Ad 6d"));
        DecisionPhase phase = DecisionPhase.DRAW;
        int DECK_COUNT = 1;

        int pileCnt = 0, deckCnt = 0;
        for (int i = 0; i < 10; i++) {
            Action decision = Runner.run(hand, knownDiscardedCards, wildcard, topDiscardPile, phase, DECK_COUNT, timems);
            if (((DrawMove)decision).drawFromDiscardPile)
                pileCnt++;
            else
                deckCnt++;
            System.out.println(decision);
        }
        System.out.println(String.format("pileCnt = %d, deckCnt = %d", pileCnt, deckCnt));
        //
    }

    public static void test5() {
        List<Card> hand = Arrays.asList(Card.str2Cards("7d 3h Ah 4s 8d Qh 9c 9d 4h Td 4d 6d 3d 5h"));
        Card wildcard = Card.getCard("2d");
        Card topDiscardPile = Card.getCard("9h");
        List<Card> knownDiscardedCards = Arrays.asList(Card.str2Cards("5s Ts Qd Kd 8h Ad Jc Th 9h"));
        DecisionPhase phase = DecisionPhase.DISCARD;
        int DECK_COUNT = 1;

        int pileCnt = 0, deckCnt = 0;
        for (int i = 0; i < 10; i++) {
            long timeBefore = Misc.getTime();
            Action decision = Runner.run(hand, knownDiscardedCards, wildcard, topDiscardPile, phase, DECK_COUNT, timems);
            System.out.println(decision + " " + (Misc.getTime() - timeBefore));
        }
        // Qh
    }

    public static void test6() {
        List<Card> hand = Arrays.asList(Card.str2Cards("Jh 3h Ah 4s 8d Qh 4c 9d 4h Td 4d 6d 3d 5h"));
        Card wildcard = Card.getCard("3s");
        Card topDiscardPile = Card.getCard("9h");
        List<Card> knownDiscardedCards = Arrays.asList(Card.str2Cards("5s Ts Qd Kd 8h Ad Jc Th 9h"));
        DecisionPhase phase = DecisionPhase.DISCARD;
        int DECK_COUNT = 1;

        int pileCnt = 0, deckCnt = 0;
        for (int i = 0; i < 10; i++) {
            long timeBefore = Misc.getTime();
            Action decision = Runner.run(hand, knownDiscardedCards, wildcard, topDiscardPile, phase, DECK_COUNT, timems);
            System.out.println(decision + " " + (Misc.getTime() - timeBefore));
        }
    }

    public static void test7() {
        List<Card> hand = Arrays.asList(Card.str2Cards("Jh 3h Ah 4s 8d Qh 4c 9d 4h Td 4d 3d 5h"));
        Card wildcard = Card.getCard("3s");

        for (int i = 0; i < 1; i++) {
            long timeBefore = Misc.getTime();
            MeldNode minleaf = Runner.getMinvalueLeaf(hand, wildcard);
            System.out.println("time:  " + (Misc.getTime() - timeBefore));
            System.out.println("Melds: " + minleaf.gatherMelds());
            System.out.println("MinValue: " + minleaf.unassembledCards + " " + minleaf.value(wildcard.getRank()));
        }
    }

    public static void test8() {
        List<Card> hand = Arrays.asList(Card.str2Cards("8d 3d Qd 9d Td 9s Xr Ad 4s 3s Ac Js Ts"));
        Card wildcard = Card.getCard("4d");

        for (int i = 0; i < 1; i++) {
            long timeBefore = Misc.getTime();
            MeldNode minleaf = Runner.getMinvalueLeaf(hand, wildcard);
            System.out.println("time:  " + (Misc.getTime() - timeBefore));
            System.out.println("Melds: " + minleaf.gatherMelds());
            System.out.println("MinValue: " + minleaf.unassembledCards + " " + minleaf.value(wildcard.getRank()));
        }
    }

    public static void test9() {
        List<Card> hand = Arrays.asList(Card.str2Cards("8d 3d Qd 9d Td 9s Xr Ad 4s 3s Ac Js Ts 5d"));
        Card wildcard = Card.getCard("4d");
        Card topDiscardPile = Card.getCard("Kc");
        List<Card> knownDiscardedCards = Arrays.asList(Card.str2Cards("Kc"));
        DecisionPhase phase = DecisionPhase.DISCARD;
        int DECK_COUNT = 1;

        for (int i = 0; i < 10; i++) {
            long timeBefore = Misc.getTime();
            Action decision = Runner.run(hand, knownDiscardedCards, wildcard, topDiscardPile, phase, DECK_COUNT, timems);
            System.out.println(decision + " " + (Misc.getTime() - timeBefore));
        }
    }

    public static void test10() {
        List<Card> hand = Arrays.asList(Card.str2Cards("5s 6s 8s Ts Ah 2h 4h 5h 7h 8h 2c Qc 3d Jd"));
        Card wildcard = Card.getCard("Jh");
        Card topDiscardPile = Card.getCard("Kc");
        List<Card> knownDiscardedCards = Arrays.asList(Card.str2Cards("As"));
        DecisionPhase phase = DecisionPhase.DISCARD;
        int DECK_COUNT = 1;

        for (int i = 0; i < 10; i++) {
            long timeBefore = Misc.getTime();
            Action decision = Runner.run(hand, knownDiscardedCards, wildcard, topDiscardPile, phase, DECK_COUNT, timems);
            System.out.println(decision + " " + (Misc.getTime() - timeBefore));
        }
    }

    public static void main(String[] args) {
//        timems = 5000;
//        test1();
        System.out.println("test10");
        test10();
        System.out.println("test3");
        test3();
        System.out.println("test5");
        test5();
    }
}
