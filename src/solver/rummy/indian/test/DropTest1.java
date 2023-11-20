package solver.rummy.indian.test;

import game.Card;
import solver.rummy.indian.Runner;
import solver.rummy.indian.meld.MeldNode;
import util.Misc;

import java.util.Arrays;
import java.util.List;

public class DropTest1 {

    public static void test1() {
        List<Card> hand = Arrays.asList(Card.str2Cards("6d, Tc, Qc, 9c, 2c, 5h, 6c, As, Td, Kc, Qd, 9h, Qh"));
        Card wildcard = Card.getCard("3d");

        for (int i = 0; i < 1; i++) {
            long timeBefore = Misc.getTime();
            MeldNode minleaf = Runner.getMinvalueLeaf(hand, wildcard);
            System.out.println("time:  " + (Misc.getTime() - timeBefore));
            System.out.println("Melds: " + minleaf.gatherMelds());
            System.out.println("MinValue: " + minleaf.unassembledCards + " " + minleaf.value(wildcard.getRank()));
        }
    }

    public static void test2() {
        List<Card> hand = Arrays.asList(Card.str2Cards("2c, Ah, 2d, 6h, 6d, 9h, 5s, 6s, Js, 3s, 8d, Ts, Tc"));
        Card wildcard = Card.getCard("Qh");

        for (int i = 0; i < 1; i++) {
            long timeBefore = Misc.getTime();
            MeldNode minleaf = Runner.getMinvalueLeaf(hand, wildcard);
            System.out.println("time:  " + (Misc.getTime() - timeBefore));
            System.out.println("Melds: " + minleaf.gatherMelds());
            System.out.println("MinValue: " + minleaf.unassembledCards + " " + minleaf.value(wildcard.getRank()));
        }
    }

    public static void test3() {
        List<Card> hand = Arrays.asList(Card.str2Cards("7d, Ac, 2h, Ts, As, Qd, 3c, 6s, 4d, Tc, 8s, Kh, 5s"));
        Card wildcard = Card.getCard("2c");

        for (int i = 0; i < 1; i++) {
            long timeBefore = Misc.getTime();
            MeldNode minleaf = Runner.getMinvalueLeaf(hand, wildcard);
            System.out.println("time:  " + (Misc.getTime() - timeBefore));
            System.out.println("Melds: " + minleaf.gatherMelds());
            System.out.println("MinValue: " + minleaf.unassembledCards + " " + minleaf.value(wildcard.getRank()));
        }
    }

    public static void test4() {
        List<Card> hand = Arrays.asList(Card.str2Cards("7d, Xr, 5s, 3h, 8s, Kh, Th, 6c, Qs, Ah, 7h, 5h, Qd"));
        Card wildcard = Card.getCard("2h");

        for (int i = 0; i < 1; i++) {
            long timeBefore = Misc.getTime();
            MeldNode minleaf = Runner.getMinvalueLeaf(hand, wildcard);
            System.out.println("time:  " + (Misc.getTime() - timeBefore));
            System.out.println("Melds: " + minleaf.gatherMelds());
            System.out.println("MinValue: " + minleaf.unassembledCards + " " + minleaf.value(wildcard.getRank()));
        }
    }

    public static void test5() {
        List<Card> hand = Arrays.asList(Card.str2Cards("Ah, Tc, Js, Ad, 6h, 5d, Qs, Kd, Kc, 4s, 7c, 8h, 3s"));
        Card wildcard = Card.getCard("Qh");

        for (int i = 0; i < 1; i++) {
            long timeBefore = Misc.getTime();
            MeldNode minleaf = Runner.getMinvalueLeaf(hand, wildcard);
            System.out.println("time:  " + (Misc.getTime() - timeBefore));
            System.out.println("Melds: " + minleaf.gatherMelds());
            System.out.println("MinValue: " + minleaf.unassembledCards + " " + minleaf.value(wildcard.getRank()));
        }
    }

    public static void test6() {
        List<Card> hand = Arrays.asList(Card.str2Cards("8c, 6d, Tc, Kh, 2c, Ks, 9d, 3h, Ac, Ts, 4d, 7c, 9h"));
        Card wildcard = Card.getCard("6s");

        for (int i = 0; i < 1; i++) {
            long timeBefore = Misc.getTime();
            MeldNode minleaf = Runner.getMinvalueLeaf(hand, wildcard);
            System.out.println("time:  " + (Misc.getTime() - timeBefore));
            System.out.println("Melds: " + minleaf.gatherMelds());
            System.out.println("MinValue: " + minleaf.unassembledCards + " " + minleaf.value(wildcard.getRank()));
        }
    }

    public static void test7() {
        List<Card> hand = Arrays.asList(Card.str2Cards("3c, 6s, Ks, 6c, 3d, Tc, 8c, Ac, 5d, Kd, Jh, 7s, 5h"));
        Card wildcard = Card.getCard("8h");

        for (int i = 0; i < 1; i++) {
            long timeBefore = Misc.getTime();
            MeldNode minleaf = Runner.getMinvalueLeaf(hand, wildcard);
            System.out.println("time:  " + (Misc.getTime() - timeBefore));
            System.out.println("Melds: " + minleaf.gatherMelds());
            System.out.println("MinValue: " + minleaf.unassembledCards + " " + minleaf.value(wildcard.getRank()));
        }
    }

    public static void test8() {
        List<Card> hand = Arrays.asList(Card.str2Cards("Jc, 2s, 4c, As, Qh, 8d, 6d, Jh, 5c, Td, Ts, 6h, 5h"));
        Card wildcard = Card.getCard("Ks");

        for (int i = 0; i < 1; i++) {
            long timeBefore = Misc.getTime();
            MeldNode minleaf = Runner.getMinvalueLeaf(hand, wildcard);
            System.out.println("time:  " + (Misc.getTime() - timeBefore));
            System.out.println("Melds: " + minleaf.gatherMelds());
            System.out.println("MinValue: " + minleaf.unassembledCards + " " + minleaf.value(wildcard.getRank()));
        }
    }

    public static void test9() {
        List<Card> hand = Arrays.asList(Card.str2Cards("Qd, 5s, 2s, 2d, 5h, 9d, Kd, 8h, Kh, 2h, 7c, 6s, 9s"));
        Card wildcard = Card.getCard("Ts");

        for (int i = 0; i < 1; i++) {
            long timeBefore = Misc.getTime();
            MeldNode minleaf = Runner.getMinvalueLeaf(hand, wildcard);
            System.out.println("time:  " + (Misc.getTime() - timeBefore));
            System.out.println("Melds: " + minleaf.gatherMelds());
            System.out.println("MinValue: " + minleaf.unassembledCards + " " + minleaf.value(wildcard.getRank()));
        }
    }

    public static void test10() {
        List<Card> hand = Arrays.asList(Card.str2Cards("6h, 4s, Td, 9h, 7d, 6c, Jc, 3d, 7h, Ah, Jd, 8c, Ad"));
        Card wildcard = Card.getCard("5d");

        for (int i = 0; i < 1; i++) {
            long timeBefore = Misc.getTime();
            MeldNode minleaf = Runner.getMinvalueLeaf(hand, wildcard);
            System.out.println("time:  " + (Misc.getTime() - timeBefore));
            System.out.println("Melds: " + minleaf.gatherMelds());
            System.out.println("MinValue: " + minleaf.unassembledCards + " " + minleaf.value(wildcard.getRank()));
        }
    }

    public static void test11() {
        List<Card> hand = Arrays.asList(Card.str2Cards("4s, As, 9c, Ah, 6h, Js, 6d, 6s, 9d, 8h, Jh, 5c, Ac"));
        Card wildcard = Card.getCard("Qh");

        for (int i = 0; i < 1; i++) {
            long timeBefore = Misc.getTime();
            MeldNode minleaf = Runner.getMinvalueLeaf(hand, wildcard);
            System.out.println("time:  " + (Misc.getTime() - timeBefore));
            System.out.println("Melds: " + minleaf.gatherMelds());
            System.out.println("MinValue: " + minleaf.unassembledCards + " " + minleaf.value(wildcard.getRank()));
        }
    }

    public static void test() {
        List<Card> hand = Arrays.asList(Card.str2Cards("9d, 8d, 5c, 6s, 8h, Kd, As, 2s, Qc, Qs, 9s, 5h, Th, 8c"));
        Card wildcard = Card.getCard("6d");

        for (int i = 0; i < 1; i++) {
            long timeBefore = Misc.getTime();
            MeldNode minleaf = Runner.getMinvalueLeaf(hand, wildcard);
            System.out.println("time:  " + (Misc.getTime() - timeBefore));
            System.out.println("Melds: " + minleaf.gatherMelds());
            System.out.println("MinValue: " + minleaf.unassembledCards + " " + minleaf.value(wildcard.getRank()));
        }
    }

    public static void main(String[] args) {
        test();
//        System.out.println("test");
//        test1();
//        System.out.println("---------------------------------------------");
//        System.out.println();
//        System.out.println("test2");
//        test2();
//        System.out.println("---------------------------------------------");
//        System.out.println();
//        System.out.println("test3");
//        test3();
//        System.out.println("---------------------------------------------");
//        System.out.println();
//        System.out.println("test4");
//        test4();
//        System.out.println("---------------------------------------------");
//        System.out.println();
//        System.out.println("test5");
//        test5();
//        System.out.println("---------------------------------------------");
//        System.out.println();
//        System.out.println("test6");
//        test6();
//        System.out.println("---------------------------------------------");
//        System.out.println();
//        System.out.println("test7");
//        test7();
//        System.out.println("---------------------------------------------");
//        System.out.println();
//        System.out.println("test8");
//        test8();
//        System.out.println("---------------------------------------------");
//        System.out.println();
//        System.out.println("test9");
//        test9();
//        System.out.println("---------------------------------------------");
//        System.out.println();
//        System.out.println("test10");
//        test10();
//        System.out.println("---------------------------------------------");
//        System.out.println();
//        System.out.println("test11");
//        test11();
//        System.out.println("---------------------------------------------");
//        System.out.println();
    }
}
