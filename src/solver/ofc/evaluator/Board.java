/*
 * Copyright (C) 2013 Ilkka Kokkarinen

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.
 */
package solver.ofc.evaluator;

import java.util.concurrent.*;
import java.util.*;

/* A board consists of front, middle and back hands, and the methods to work on them. */

public class Board {
   
    // The three hands as arrays of cards, each card a long
    private long[][] hands;
    // How far each hand has been filled so far
    public int[] idx = new int[3];
    // The values of completely filled hands
    // evals[0] = value of front hand against 5-card hand
    // evals[1] = value of middle hand
    // evals[2] = value of back hand
    // evals[3] = value of front hand against 3-card hand
    private short[] evals = new short[4];
    // The cached values of bonuses for completed hands
    private short[] bonuses = new short[3];
    // Count of each rank in each hand
    private short[][] rankCount = new short[13][3];
    // How many samples have been taken during execution
    public static long sampleCount = 0;
    // How many times alpha beta pruning has given us a cutoff
    public static int abCutoffCount = 0;
    // The time the thread owning this board started evaluation
    private long startTime;
    // The value of reaching Fantasyland
    public static int fantasyLand = 15;
    
    private boolean running = true;
    
    // Create a separate copy of another board. Needed for multithreading.
    public Board(Board other) {
        hands = new long[3][];
        hands[0] = new long[3];
        for(int i = 0; i < 3; i++) {
            hands[0][i] = other.hands[0][i];
        }
        hands[1] = new long[5];
        for(int i = 0; i < 5; i++) {
            hands[1][i] = other.hands[1][i];
        }
        hands[2] = new long[5];
        for(int i = 0; i < 5; i++) {
            hands[2][i] = other.hands[2][i];
        }
        evals = new short[4];
        idx = new int[3];
        bonuses = new short[3];
        for(int i = 0; i < 4; i++) {
            evals[i] = other.evals[i];
        }
        for(int i = 0; i < 3; i++) {
            idx[i] = other.idx[i];
            bonuses[i] = other.bonuses[i];
        }
        for(int i = 0; i < 13; i++) {
            for(int j = 0; j < 3; j++) {
                rankCount[i][j] = other.rankCount[i][j];
            }
        }
    }
    
    // Create a board from three strings encoding the cards.
    public Board(String frontS, String middleS, String backS, Evaluator ev, Deck d) {
        hands = new long[3][];
        hands[0] = new long[3];
        hands[1] = new long[5];
        hands[2] = new long[5];
        Evaluator.encodeHand(frontS, hands[0]); 
        Evaluator.encodeHand(middleS, hands[1]);
        Evaluator.encodeHand(backS, hands[2]);
        idx[0] = frontS.length() / 2;
        idx[1] = middleS.length() / 2;
        idx[2] = backS.length() / 2;
        evals[0] = evals[1] = evals[2] = -1;
        for(int h = 0; h < 3; h++) {
            for(int i = 0; i < idx[h]; i++) {
                rankCount[(int)(hands[h][i] >> 8) & 0xF][h]++;
            }
        }
        updateEvals(0, ev, d);
        updateEvals(1, ev, d);
        updateEvals(2, ev, d);
    }
    
    // Create a board and eliminate those cards from the given deck.
    public Board(String frontS, String middleS, String backS, Deck d, Evaluator ev) {
        this(frontS, middleS, backS, ev, d);
        d.removeCards(hands[0]);
        d.removeCards(hands[1]);
        d.removeCards(hands[2]);
    }

    // The total number of cards on this board.
    public int getCount() {
        return idx[0] + idx[1] + idx[2];
    }
    
    // Conversion to readable string
    public String toString() {
        String res = "[";
        for(int j = 0; j < 3; j++) {
            for(int i = 0; i < idx[j]; i++) {
                res += Evaluator.decodeCard(hands[j][i]) + ((i < idx[j] - 1)? " " : "");
            }
            res += (j < 2)? "; " : "]";
        }
        return res;
    }
    
    // Computes the end result of this board and other, both assumed completed.
    // Uses the 1-6 scoring scheme and the bonuses computed as above.
    public int endResult(Board other, Evaluator ev) {
        short ef1 = this.evals[0];
        short em1 = this.evals[1];
        short eb1 = this.evals[2];
        short ef13 = this.evals[3];
        short ef2 = other.evals[0];
        short em2 = other.evals[1];
        short eb2 = other.evals[2];
        short ef23 = other.evals[3];
        boolean fouled1 = (ef1 < em1 || em1 < eb1);
        boolean fouled2 = (ef2 < em2 || em2 < eb2);
        if(fouled1 && fouled2) { return 0; }
        if(fouled1) {
            return -6 - (other.bonuses[0] + other.bonuses[1] + other.bonuses[2]);
        }           
        if(fouled2) {
            return +6 + (this.bonuses[0] + this.bonuses[1] + this.bonuses[2]); 
        }
        int wins = 0;
        if(ef13 <= 6185 || ef23 <= 6185) { // I think the author was wrong  if(ef13 <= 6685 || ef23 <= 6685) {
            // at least one front is pair or trips, compare straight up
            if(ef13 < ef23) { wins = 1; }
            if(ef13 > ef23) { wins = -1; }
        }
        else {
            // neither front has pair or trips
            wins = ev.compareThrees(this.hands[0], other.hands[0]);
        }
        wins += (em1 < em2) ? 1: 0;
        wins += (eb1 < eb2) ? 1: 0;
        wins -= (em1 > em2) ? 1: 0;
        wins -= (eb1 > eb2) ? 1: 0;
        if(wins == 3) { wins = 6; } // 6 points for winning all three
        if(wins == -3) { wins = -6; }
        return wins + (this.bonuses[0] + this.bonuses[1] + this.bonuses[2])
                    - (other.bonuses[0] + other.bonuses[1] + other.bonuses[2]);
    }
    
    // Updates the hand value evaluation for the completed hand, if any, and returns
    // the probability guesstimate that the board will be fouled.
    private double updateEvals(int h, Evaluator ev, Deck d) {
        if(h == 0 && idx[0] == 3) { // Front hand is complete                       
            evals[0] = ev.evalThree(hands[0], true);
            if(evals[0] <= 6185) { // I think the author was wrong  if(evals[0] <= 6685) {
                evals[3] = evals[0];
                if(Evaluator.getHandRank(evals[0]) == HandRank.TRIPS) {
                    int r1 = (int)(hands[0][0] >> 8) & 0xF;
                    bonuses[0] += (short)(r1 + 10);
                }
                if(Evaluator.getHandRank(evals[0]) == HandRank.ONE_PAIR) {
                    int r1 = (int)(hands[0][0] >> 8) & 0xF;
                    int r2 = (int)(hands[0][1] >> 8) & 0xF;
                    int r3 = (int)(hands[0][2] >> 8) & 0xF;
                    if(r1 == r2 && r1 >= 4) { bonuses[0] = (short)(r1 - 3); }
                    else if(r1 == r3 && r1 >= 4) { bonuses[0] = (short)(r1 - 3); }
                    else if(r2 == r3 && r2 >= 4) { bonuses[0] = (short)(r2 - 3); } 
                }
                // Fantasyland! (QQ2 evaluates to 3985)
                if(evals[0] <= 3985) { bonuses[0] += fantasyLand; }
            }
            else {
                evals[3] = ev.evalThree(hands[0], false);
            }            
            if ((idx[1] == 5 && evals[0] < evals[1]) || (idx[2] == 5 && evals[0] < evals[2])) {
                return 1.0; // already fouled
            }
            if(idx[1] < 5 && idx[2] < 5) {
                double fProb = d.countFoulsGT(hands[1], idx[1], evals[0], ev);
                if(idx[2] < 3) { fProb = fProb / 2; }
                return fProb;
            }
            else { return 0.0; }
        }
        else if(h == 1 && idx[1] == 5) { // Middle hand is complete
            evals[1] = ev.evalFive(hands[1]);
            switch(Evaluator.getHandRank(evals[1])) {
                case TRIPS: bonuses[1] = 2; break;
                case STRAIGHT: bonuses[1] = 4; break;
                case FLUSH: bonuses[1] = 8; break;
                case FULL_HOUSE: bonuses[1] = 12; break;
                case QUADS: bonuses[1] = 20; break;
                case STRAIGHT_FLUSH: bonuses[1] = 30; break;
                case ROYAL_FLUSH: bonuses[1] = 50; break;
            }
            if((idx[0] == 3 && evals[0] < evals[1]) || (idx[2] == 5 && evals[1] < evals[2])) {
                return 1.0;
            }
            if(idx[2] < 5 && idx[0] < 3) {
                double fProb = d.countFoulsGT(hands[2], idx[2], evals[1], ev);
                if(idx[0] < 2) { fProb = fProb / 2; }
                return fProb;
            }
            else { return 0.0; }
        }
        else if(h == 2 && idx[2] == 5) { // Back hand is complete
            evals[2] = ev.evalFive(hands[2]);
            switch(Evaluator.getHandRank(evals[2])) {
                case STRAIGHT: bonuses[2] = 2; break;
                case FLUSH: bonuses[2] = 4; break;
                case FULL_HOUSE: bonuses[2] = 6; break;
                case QUADS: bonuses[2] = 10; break;
                case STRAIGHT_FLUSH: bonuses[2] = 15; break;
                case ROYAL_FLUSH: bonuses[2] = 25; break;
            }
            if((idx[1] == 5 && evals[1] < evals[2]) || (idx[0] == 3 && evals[0] < evals[2])) {
                return 1.0;
            }
            if(idx[1] < 5 && idx[0] < 3) {
                double fProb = d.countFoulsLT(hands[1], idx[1], evals[2], ev);
                if(idx[0] < 2) { fProb = fProb / 2; }
                return fProb;
            }
            else { return 0.0; }
        }
        return 0.0;
    }
    
    /*
    // Basic minimax algorithm with alpha beta cutoffs. Not used any more.
    private int maxValueSingle(long card, Board other, Deck d, int alpha, int beta, Evaluator ev) {
        if(this.getCount() == 13) {
            return this.endResult(other, ev);
        }
        // Try the possible moves from front to back. Should think up some better way here.            
        for(int h = 0; h < 3; h++) {  
            if(h == 0 && idx[h] < 3 || h > 0 && idx[h] < 5) { // is this move possible?
                this.hands[h][idx[h]++] = card; // if yes, try it
                double fProb = updateEvals(h, ev, d); // see how this affects current hand values
                int result = -1000;
                if(!(fProb == 1.0 && alpha > -6)) {
                    // Evaluate next state recursively from opponent's point of view                    
                    long nextCard = d.draw();                    
                    result = -other.maxValueSingle(nextCard, this, d, -beta, -alpha, ev);
                    d.putBack(nextCard);
                }
                idx[h]--; // take out the card we just put in
                this.evals[h] = -1; this.bonuses[h] = 0;
                if(alpha < result) {
                    alpha = result;
                    if(alpha >= beta) { abCutoffCount++; return alpha; }
                }
            }
        }
        return alpha;
    }
    */
   
    // MTDF minimax evaluation, slower than negascout, not useful in this game
    /*
    private int maxValueMTDF(long card, Board other, Deck d, int alpha, int beta, Evaluator ev) {
        int g = (alpha + beta) / 2;
        while(alpha < beta) {
            int g2 = (g == alpha) ? g + 1: g;
            g = maxValueSingle(card, other, d, g2 - 1, g2, ev);
            if(g < g2) { beta = g; } else { alpha = g; }
        }
        return g;
    }
    */
        
    // Negascout evaluation, a bit faster than regular old minimax
    private double negaScoutRank(long card, Board other, Deck d, double alpha, double beta, Evaluator ev) {
        if(this.getCount() == 13) {
            return this.endResult(other, ev);
        }
        double a = alpha, b = beta;
        boolean first = true;
        int h = 0; // which hand to try first to put the card in
        int r = (int)(card >> 8) & 0xF; // rank of the card
        if(rankCount[r][2] > 0) { h = 2; } // so, try back first
        else if(rankCount[r][1] > 0) { h = 1; } // ok, middle 
        for(int loop = 0; loop < 3; loop++) {  
            if(h == 0 && idx[h] < 3 || h > 0 && idx[h] < 5) { // is this move possible?
                this.hands[h][idx[h]++] = card; // if yes, try it
                rankCount[r][h]++;
                double fProb = updateEvals(h, ev, d); // see how this affects current hand values
                double result = -1000;
                if(!(fProb == 1.0 && alpha > -6)) {
                    // evaluate next state recursively from opponent's point of view                    
                    long nextCard = d.draw();                    
                    result = -other.negaScoutRank(nextCard, this, d, -b, -a, ev);
                    if(result > -0) { result = -6 * fProb + result * (1 - fProb); }
                    if(result > a && result < beta && !first) {
                        // evaluate for reals
                        a = -other.negaScoutRank(nextCard, this, d, -beta, -result, ev);
                        if(a > 0) { a = -6 * fProb + a * (1 - fProb); }
                    }
                    d.putBack(nextCard);
                    if(a < result) { a = result; }
                    first = false;
                }
                idx[h]--; // take out the card we just put in
                this.evals[h] = -1;
                this.bonuses[h] = 0;
                rankCount[r][h]--;
                if(a >= beta) { return a; }
                b = a + 1;
            }
            h = (h + 1) % 3;
        }
        return a;
    }
    
    // Samples the values of three moves until time limit, and returns their values.
    public void maxValueSampling(long card, Board other, Deck d, Evaluator ev, double[] v, int[] count, List<List<Double>> averages) {
        while(running) {
            d.shuffle();
            long nextCard = d.draw();
            int r = (int)(nextCard >> 8) & 0xF;
            // one sample of each of three possible moves
            for(int h = 0; h < 3; h++) { 
                if(averages.get(h) != null) {
                    sampleCount++;
                    this.hands[h][this.idx[h]++] = card; // put the card in hand h
                    this.updateEvals(h, ev, d);
                    rankCount[r][h]++;
                    double result = -other.negaScoutRank(nextCard, this, d, -1000, +1000, ev);
                    // uncomment next two lines to test equivalence of two minimax evaluators
                    //int result2 = -other.maxValueSingle(nextCard, this, d, -1000, +1000, ev);
                    //if(result != result2) { System.out.println("Evaluation error!"); }
                    if(running) {
                        try { // update the estimate for the move v
                            mutex.acquire();
                            v[h] += result;
                            count[h]++;
                            List<Double> a = averages.get(h);
                            if(a != null && (count[h] & (count[h] - 1)) == 0) {
                                a.add(v[h] / count[h]);
                                
                                if(a.size() > 12 && Math.abs(a.get(a.size() - 1) - a.get(a.size() - 2)) < 0.001) {
                                    averages.set(h, null);
                                }
                                
                            }
                        }
                        catch(InterruptedException e) { }
                        finally { mutex.release(); }
                    }
                    this.idx[h]--; // take out the card we just put in
                    this.evals[h] = -1;
                    rankCount[r][h]--;
                    this.bonuses[h] = 0;
                }
            }
            d.putBack(nextCard);
        } while(running);
    } 
    
    private static final ExecutorService es = Executors.newCachedThreadPool();
    private static final Semaphore mutex = new Semaphore(1);
    
    // Launches the given number of threads to evaluate moves concurrently, and waits for termination condition to occur.
    public double[] maxValueThreads(final long card, final Board other, final Deck d, long timeLimit, int threadCount, boolean onlyBest)
    throws InterruptedException {
        final double[] v = new double[3];
        final int[] count = new int[3];
        Board[] boards = new Board[threadCount];
        final List<List<Double>> averages = new ArrayList<List<Double>>();
        averages.add(new ArrayList<Double>());
        averages.add(new ArrayList<Double>());
        averages.add(new ArrayList<Double>());
        if(idx[0] == 3) { averages.set(0, null); }
        if(idx[1] == 5) { averages.set(1, null); }
        if(idx[2] == 5) { averages.set(2, null); }
        
        long startTime = System.currentTimeMillis();
        for(int i = 0; i < threadCount; i++) {
            final Board b = boards[i] = new Board(this);
            es.submit(new Runnable() {
                public void run() {
                    b.maxValueSampling(card, new Board(other), new Deck(d), new Evaluator(), v, count, averages);
                }                
            });
        }
        while((averages.get(0) != null || averages.get(1) != null || averages.get(2) != null)
               && System.currentTimeMillis() - startTime < timeLimit) {
            try { Thread.sleep(100); } catch(InterruptedException e) { }
            mutex.acquire(); // safety first
            List<Double> amax = null, amin = null;
            int min = -1;
            for(int h = 0; h < 3; h++) {
                List<Double> a = averages.get(h);
                if(a != null) {
                    // test if this move is minimum or maximum so far
                    if(amin == null || a.get(a.size() - 1) < amin.get(amin.size() - 1)) {
                        amin = a; min = h;
                    }
                    if(amax == null || a.get(a.size() - 1) > amax.get(amax.size() - 1)) {
                        amax = a;
                    }

                }
            }
            // check if we can give up evaluating the current worst move
            if(onlyBest && amax != amin && amin.size() > 12 && amax.size() > 12) {
                double changeMin = Math.abs(amin.get(amin.size() - 1) - amin.get(amin.size() - 2));
                double changeMax = Math.abs(amax.get(amax.size() - 1) - amax.get(amax.size() - 2));
                if(amax.get(amax.size() - 1) - amin.get(amin.size() - 1) > 20 * (changeMin + changeMax)) {
                    // give up evaluating the move that is current worst
                    averages.set(min, null);
                }
            }
            mutex.release();
        }
        for(Board b : boards) { b.running = false; }
        double[] result = new double[3];
        mutex.acquire();
        if(count[0] > 0) { result[0] = v[0] / count[0]; }
        if(count[1] > 0) { result[1] = v[1] / count[1]; }
        if(count[2] > 0) { result[2] = v[2] / count[2]; }
        mutex.release();
        System.out.printf("Front: %.3f %d\n", result[0], count[0]);
        System.out.printf("Middle: %.3f %d\n", result[1], count[1]);
        System.out.printf("Back: %.3f %d\n", result[2], count[2]);
        return result;
    }
}