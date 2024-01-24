package solver.rummy.indian.test;

import game.Card;
import solver.rummy.indian.Action;
import solver.rummy.indian.DecisionPhase;
import solver.rummy.indian.DrawMove;
import solver.rummy.indian.Runner;
import solver.rummy.indian.meld.MeldNode;
import util.Misc;

import java.util.Arrays;
import java.util.List;

public class HiddenCardTest1 {

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
        Card hiddenCard = Card.getCard("4d");
        List<Card> knownDiscardedCards = Arrays.asList(Card.str2Cards("3h"));
        DecisionPhase phase = DecisionPhase.DRAW;
        int DECK_COUNT = 1;

        int pileCnt = 0, deckCnt = 0;
        for (int i = 0; i < 10; i++) {
            long timeBefore = Misc.getTime();
            Action decision = Runner.run(hand, knownDiscardedCards, wildcard, topDiscardPile, phase, DECK_COUNT, timems, hiddenCard);
            if (((DrawMove)decision).drawFromDiscardPile)
                pileCnt++;
            else
                deckCnt++;
            System.out.println(decision + " " + (Misc.getTime() - timeBefore));
        }
        System.out.println(String.format("pileCnt = %d, deckCnt = %d", pileCnt, deckCnt));

        MeldNode minleaf = Runner.getMinvalueLeaf(hand, wildcard);
        System.out.println("Melds: " + minleaf.gatherMelds());

    }

    public static void test3WrongDecision() {
        List<Card> hand = Arrays.asList(Card.str2Cards("2c, 7h, 5h, 2h, Ah, 9s, 6h, Ac, As, 8s, Ts, 7s, 7c"));
        Card wildcard = Card.getCard("6s");
        Card topDiscardPile = Card.getCard("9d");
        Card hiddenCard = Card.getCard("2d");
        List<Card> knownDiscardedCards = Arrays.asList(Card.str2Cards("9d, Qc, Qs, 8h, 4s, 9c, 9h, 5s, 6s, Kc, Jd, 3c"));
        DecisionPhase phase = DecisionPhase.DRAW;
        int DECK_COUNT = 1;

        int pileCnt = 0, deckCnt = 0;
        for (int i = 0; i < 10; i++) {
            long timeBefore = Misc.getTime();
            Action decision = Runner.run(hand, knownDiscardedCards, wildcard, topDiscardPile, phase, DECK_COUNT, timems, hiddenCard);
            if (((DrawMove)decision).drawFromDiscardPile)
                pileCnt++;
            else
                deckCnt++;
            System.out.println(decision + " " + (Misc.getTime() - timeBefore));
        }
        System.out.println(String.format("pileCnt = %d, deckCnt = %d", pileCnt, deckCnt));

        MeldNode minleaf = Runner.getMinvalueLeaf(hand, wildcard);
        System.out.println("Melds: " + minleaf.gatherMelds());

    }

    public static void test4WrongDecision() {
        List<Card> hand = Arrays.asList(Card.str2Cards("As, Qs, Qh, 6s, 2c, 2h, Js, Kh, Jh, 5s, 2d, Ks, Ad"));
        Card wildcard = Card.getCard("6h");
        Card topDiscardPile = Card.getCard("7s");
        Card hiddenCard = Card.getCard("7d");
        List<Card> knownDiscardedCards = Arrays.asList(Card.str2Cards("7s, 6h, Kc, Tc, Jd"));
        DecisionPhase phase = DecisionPhase.DRAW;
        int DECK_COUNT = 1;

        int pileCnt = 0, deckCnt = 0;
        for (int i = 0; i < 10; i++) {
            long timeBefore = Misc.getTime();
            Action decision = Runner.run(hand, knownDiscardedCards, wildcard, topDiscardPile, phase, DECK_COUNT, timems, hiddenCard);
            if (((DrawMove)decision).drawFromDiscardPile)
                pileCnt++;
            else
                deckCnt++;
            System.out.println(decision + " " + (Misc.getTime() - timeBefore));
        }
        System.out.println(String.format("pileCnt = %d, deckCnt = %d", pileCnt, deckCnt));

        MeldNode minleaf = Runner.getMinvalueLeaf(hand, wildcard);
        System.out.println("Melds: " + minleaf.gatherMelds());

    }

    public static void test4WrongDecision_() {
        List<Card> hand = Arrays.asList(Card.str2Cards("As, Qs, Qh, 6s, 2c, 2h, Js, Kh, Jh, 5s, 2d, Ks, Ad, 7s"));
        Card wildcard = Card.getCard("6h");
        Card topDiscardPile = null;
        Card hiddenCard = Card.getCard("7d");
        List<Card> knownDiscardedCards = Arrays.asList(Card.str2Cards("6h, Kc, Tc, Jd"));
        DecisionPhase phase = DecisionPhase.DISCARD;
        int DECK_COUNT = 1;

        for (int i = 0; i < 1; i++) {
            long timeBefore = Misc.getTime();
            Action decision = Runner.run(hand, knownDiscardedCards, wildcard, topDiscardPile, phase, DECK_COUNT, timems, hiddenCard);
            System.out.println(decision + " " + (Misc.getTime() - timeBefore));
        }

        MeldNode minleaf = Runner.getMinvalueLeaf(hand, wildcard);
        System.out.println("Melds: " + minleaf.gatherMelds());

    }

    public static void main(String[] args) {
        timems = 5000;
        test4WrongDecision_();
    }
}
