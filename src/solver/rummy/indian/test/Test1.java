package solver.rummy.indian.test;

import game.Card;
import solver.rummy.indian.*;

import java.util.Arrays;
import java.util.List;

public class Test1 {
    public static void test1() {
        List<Card> hand = Arrays.asList(Card.str2Cards("Jc 3d Th 8c Qs 5s Kd 9c 4c As 6s 4s Ad"));
        Card wildcard = Card.getCard("2d");
        Card topDiscardPile = Card.getCard("4d");
        List<Card> knownDiscardedCards = Arrays.asList(Card.str2Cards("4d"));
        int round = 1;
        DecisionPhase phase = DecisionPhase.DRAW;
        int DECK_COUNT = 2;

        Action decision = Runner.run(hand, knownDiscardedCards, round, wildcard, topDiscardPile, phase, DECK_COUNT);
        System.out.println(decision);
    }

    public static void test2() {
        List<Card> hand = Arrays.asList(Card.str2Cards("Ac Qs Xr 4h 9d 5d As 3c 7c Jc 2h Qd 6d"));
        Card wildcard = Card.getCard("9s");
//        Card topDiscardPile = Card.getCard("7h");
        Card topDiscardPile = Card.getCard("3h");
        List<Card> knownDiscardedCards = Arrays.asList(Card.str2Cards("3h"));
        int round = 1;
        DecisionPhase phase = DecisionPhase.DRAW;
        int DECK_COUNT = 1;

        int pileCnt = 0, deckCnt = 0;
        for (int i = 0; i < 10; i++) {
            Action decision = Runner.run(hand, knownDiscardedCards, round, wildcard, topDiscardPile, phase, DECK_COUNT);
            if (((DrawMove)decision).drawFromDiscardPile)
                pileCnt++;
            else
                deckCnt++;
            System.out.println(decision);
        }
        System.out.println(String.format("pileCnt = %d, deckCnt = %d", pileCnt, deckCnt));
    }

    public static void test3() {
        List<Card> hand = Arrays.asList(Card.str2Cards("7d 3h Ah 4s 8d 9h Ts Qh Jc 9c 9d 4h Ad Td"));
        Card wildcard = Card.getCard("2d");
        Card topDiscardPile = Card.getCard("5s");
        List<Card> knownDiscardedCards = Arrays.asList(Card.str2Cards("5s"));
        int round = 1;
        DecisionPhase phase = DecisionPhase.DISCARD;
        int DECK_COUNT = 1;

        int pileCnt = 0, deckCnt = 0;
        for (int i = 0; i < 10; i++) {
            Action decision = Runner.run(hand, knownDiscardedCards, round, wildcard, topDiscardPile, phase, DECK_COUNT);
            System.out.println(decision);
        }
        // TS
    }

    public static void test4() {
        List<Card> hand = Arrays.asList(Card.str2Cards("7d 3h Ah 4s 8d 9h Qh Jc 9c 9d 4h Td 4d"));
        Card wildcard = Card.getCard("2d");
        Card topDiscardPile = Card.getCard("6d");
        List<Card> knownDiscardedCards = Arrays.asList(Card.str2Cards("5s Ts Qd Kd 8h Ad 6d"));
        int round = 1;
        DecisionPhase phase = DecisionPhase.DRAW;
        int DECK_COUNT = 1;

        int pileCnt = 0, deckCnt = 0;
        for (int i = 0; i < 10; i++) {
            Action decision = Runner.run(hand, knownDiscardedCards, round, wildcard, topDiscardPile, phase, DECK_COUNT);
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
        int round = 1;
        DecisionPhase phase = DecisionPhase.DISCARD;
        int DECK_COUNT = 1;

        int pileCnt = 0, deckCnt = 0;
        for (int i = 0; i < 10; i++) {
            Action decision = Runner.run(hand, knownDiscardedCards, round, wildcard, topDiscardPile, phase, DECK_COUNT);
            System.out.println(decision);
        }
        // Qh
    }

    public static void main(String[] args) {
        test5();
    }
}
