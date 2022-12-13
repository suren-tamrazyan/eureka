package solver.pusoy;

import game.Card;
import solver.ofc.Config;
import solver.ofc.EvaluatorFacade;
import solver.ofc.evaluator.HandRank;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static solver.ofc.EvaluatorFacade.encodeHand;

public class Evaluator {
    public static double evaluate(List<Card> front, List<Card> middle, List<Card> back) {
//    	final int MULTIPLIER = 1;//10000;
        final double MAX_EVAL = 7748 + 1;
        final double MAX_BONUS = 20;
        final int REGULARIZATION_PARAM = 3;
        solver.ofc.evaluator.Evaluator ev = new solver.ofc.evaluator.Evaluator();
        // The values of completely filled hands
        // evals[0] = value of front hand against 5-card hand
        // evals[1] = value of middle hand
        // evals[2] = value of back hand
        // evals[3] = value of front hand against 3-card hand
        short[] evals = new short[4];
        evals[0] = evals[1] = evals[2] = -1;
        // The cached values of bonuses for completed hands
        int[] bonuses = new int[3];
        // The three hands as arrays of cards, each card a long
        long[][] hands = new long[3][];
        hands[0] = new long[3];
        hands[1] = new long[5];
        hands[2] = new long[5];
        encodeHand(front, hands[0]);
        encodeHand(middle, hands[1]);
        encodeHand(back, hands[2]);

        // Front hand
        evals[0] = ev.evalThree(hands[0], true);
        if(evals[0] <= 6185) {
            evals[3] = evals[0];
            if(solver.ofc.evaluator.Evaluator.getHandRank(evals[0]) == HandRank.TRIPS) {
                bonuses[0] += 3;
            }
        } else {
            evals[3] = ev.evalThree(hands[0], false);
        }
        double norm_eval_kicker0 = (double)(MAX_EVAL - evals[3]) / MAX_EVAL;

        // Middle hand
        evals[1] = ev.evalFive(hands[1]);
        double norm_eval_kicker1 = (double)(MAX_EVAL - evals[1]) / MAX_EVAL;
        switch(solver.ofc.evaluator.Evaluator.getHandRank(evals[1])) {
            case FULL_HOUSE: bonuses[1] = 2; break;
            case QUADS: bonuses[1] = 8; break;
            case STRAIGHT_FLUSH: bonuses[1] = 10; break;
            case ROYAL_FLUSH: bonuses[1] = 20; break;
            default: bonuses[1] = 0; break;
        }

        // Back hand
        evals[2] = ev.evalFive(hands[2]);
        double norm_eval_kicker2 = (double)(MAX_EVAL - evals[2]) / MAX_EVAL;
        switch(solver.ofc.evaluator.Evaluator.getHandRank(evals[2])) {
            case QUADS: bonuses[2] = 4; break;
            case STRAIGHT_FLUSH: bonuses[2] = 5; break;
            case ROYAL_FLUSH: bonuses[2] = 10; break;
            default: bonuses[2] = 0; break;
        }


        // fouled
        if (evals[0] < evals[1] || evals[1] < evals[2])
            return 0;

        // six pair
        if (solver.ofc.evaluator.Evaluator.getHandRank(evals[2]) == HandRank.TWO_PAIR
                && solver.ofc.evaluator.Evaluator.getHandRank(evals[1]) == HandRank.TWO_PAIR
                && solver.ofc.evaluator.Evaluator.getHandRank(evals[0]) == HandRank.ONE_PAIR) {

            List<Integer> acc = new ArrayList<>(13);
            for (long h : hands[0])
                acc.add((int)(h >> 8) & 0xF);
            for (long h : hands[1])
                acc.add((int)(h >> 8) & 0xF);
            for (long h : hands[2])
                acc.add((int)(h >> 8) & 0xF);
            boolean isSixPair = acc.stream().collect(Collectors.groupingBy(e -> e)).entrySet().stream().filter(e -> e.getValue().size() == 2).map(Map.Entry::getKey).count() == 6;
            if (isSixPair)
                return 100 + 3;
        }

        // dragon & three straights
        if (solver.ofc.evaluator.Evaluator.getHandRank(evals[2]) == HandRank.STRAIGHT
                && solver.ofc.evaluator.Evaluator.getHandRank(evals[1]) == HandRank.STRAIGHT) {
            int r00 = (int)(hands[0][0] >> 8) & 0xF;
            int r01 = (int)(hands[0][1] >> 8) & 0xF;
            int r02 = (int)(hands[0][2] >> 8) & 0xF;
            int[] p0 = {r00, r01, r02};
            myLittleSort(p0);
            if (p0[0]+1 == p0[1] && p0[1]+1 == p0[2]) {
                if (p0[0] == 0) {
                    int r10 = (int) (hands[1][0] >> 8) & 0xF;
                    int r11 = (int) (hands[1][1] >> 8) & 0xF;
                    int r12 = (int) (hands[1][2] >> 8) & 0xF;
                    int r13 = (int) (hands[1][3] >> 8) & 0xF;
                    int r14 = (int) (hands[1][4] >> 8) & 0xF;
                    int[] p1 = {r10, r11, r12, r13, r14};
                    myLittleSort(p1);
                    if (p1[0] == 3) {
                        int r20 = (int) (hands[2][0] >> 8) & 0xF;
                        int r21 = (int) (hands[2][1] >> 8) & 0xF;
                        int r22 = (int) (hands[2][2] >> 8) & 0xF;
                        int r23 = (int) (hands[2][3] >> 8) & 0xF;
                        int r24 = (int) (hands[2][4] >> 8) & 0xF;
                        int[] p2 = {r20, r21, r22, r23, r24};
                        myLittleSort(p2);
                        if (p2[0] == 8)
                            return 100 + 13; // dragon
                    }
                }

                return 100 + 3; // straight by three card
            }
        }

        // three flashes
        if (solver.ofc.evaluator.Evaluator.getHandRank(evals[2]) == HandRank.FLUSH
                && solver.ofc.evaluator.Evaluator.getHandRank(evals[1]) == HandRank.FLUSH) {
            long c1 = hands[0][0];
            long c2 = hands[0][1];
            long c3 = hands[0][2];
            long q;
            short s;
            q = (c1|c2|c3) >> 16;
            if ( (c1 & c2 & c3 & 0xF000) != 0) // flush by three card
                return 100 + 3;
        }


        double bonus = ((double)bonuses[0]/MAX_BONUS + (double)bonuses[1]/MAX_BONUS + (double)bonuses[2]/MAX_BONUS);
        return bonus + (norm_eval_kicker0 + norm_eval_kicker1 + norm_eval_kicker2)/REGULARIZATION_PARAM;
        // may be also use weight for row?
    }
    private static void myLittleSort(int[] a) {
        for(int i = 1; i < a.length; i++) {
            int x = a[i];
            int j = i;
            while(j > 0 && a[j-1] > x) {
                a[j] = a[j-1];
                j--;
            }
            a[j] = x;
        }
    }

    public static void main(String[] args) {
        System.out.println(evaluate(Arrays.asList(Card.str2Cards("ThTdAh")), Arrays.asList(Card.str2Cards("4d4c6d6c9c")), Arrays.asList(Card.str2Cards("2h2c3d3c3s"))));
        System.out.println(evaluate(Arrays.asList(Card.str2Cards("ThTdAh")), Arrays.asList(Card.str2Cards("2h2c6d6c9c")), Arrays.asList(Card.str2Cards("4d4c3d3c3s"))));
//        System.out.println(evaluate(Arrays.asList(Card.str2Cards("KcAsKh")), Arrays.asList(Card.str2Cards("4h4s4c9c9d")), Arrays.asList(Card.str2Cards("ThTdTsQsQd"))));
//        System.out.println(evaluate(Arrays.asList(Card.str2Cards("KcAs3h")), Arrays.asList(Card.str2Cards("4h8c4c9c2s")), Arrays.asList(Card.str2Cards("ThTd3sQsQd"))));
//        System.out.println(evaluate(Arrays.asList(Card.str2Cards("KcAs3h")), Arrays.asList(Card.str2Cards("4h8c4cAc2s")), Arrays.asList(Card.str2Cards("ThTd3sQsQd"))));
    }
}
