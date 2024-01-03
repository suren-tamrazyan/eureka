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

    public static void main(String[] args) {
        timems = 5000;
        test2();
    }
}
