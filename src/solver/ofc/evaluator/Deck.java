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

/* A utility class to represent a deck of cards. */
package solver.ofc.evaluator;

import java.util.*;

public class Deck {
    
    public long[] cards = new long[52];
    private Random rng;
    private int size;
    
    private static long seed = System.currentTimeMillis();
    
    // Create a new deck with the 52 cards in sorted order.
    public Deck() {
        size = 52;
        for(int suit = 0; suit < 4; suit++) {
            for(int rank = 0; rank < 13; rank++) {
                cards[13*suit + rank] = Evaluator.encodeCard(suit, rank);
            }
        }
        rng = new Random((int)(seed++));
    }
    
    // Create a separate copy of another existing deck.
    public Deck(Deck other) {
        this.cards = new long[52];
        this.size = other.size;
        for(int i = 0; i < 52; i++) {
            this.cards[i] = other.cards[i];
        }
        this.rng = new Random((int)(seed++));
    }
    
    public int getSize() { return size; }
    
    public String toString() {
        String res = "(" + size + ": ";
        for(int i = 0; i < size; i++) {
            res += Evaluator.decodeCard(cards[i]) + " ";
        }
        return res + ")";
    }
    
    // Shuffle all of the remaining cards.
    public void shuffle() {
        for(int i = size - 1; i > 0; i--) {
            int j = rng.nextInt(i + 1);
            long tmp = cards[i];
            cards[i] = cards[j];
            cards[j] = tmp;
        }
    }
    
    // Shuffle so that top k cards are randomly drawn from the remaining deck.
    public void shuffle(int k) {
        for(int i = size - 1; i >= size - k; i--) {
            int j = rng.nextInt(i + 1);
            long tmp = cards[i];
            cards[i] = cards[j];
            cards[j] = tmp;
        }
    }
    
    // Draw the card in the given location.
    private long draw(int idx) {
        long card = cards[idx];
        for(int i = idx; i < size - 1; i++) {
            cards[i] = cards[i+1];
        }
        size = size - 1;
        return card;
    }
    
    // Draw the topmost remaining card from the deck.
    public long draw() {
        return cards[--size];
    }
    
    // Put back a previously drawn card.
    public void putBack(long card) {
        cards[size++] = card;
    }
    
    // Remove the given card from the deck.
    public void removeCard(long card) {
        for(int j = 0; j < size; j++) {
            if(cards[j] == card) {
                this.draw(j); break;
            }
        }
    }
    
    // Remove an array of cards from the deck.
    public void removeCards(long[] dead) {
        for(int i = 0; i < dead.length; i++) {
            if(dead[i] > 0) {
                for(int j = 0; j < size; j++) {
                    if(cards[j] == dead[i]) {                        
                        this.draw(j); break;
                    }
                }
            }
        }
    }
    
    // Just experimenting here. These methods are not yet used for anything.
    
    private int[] indices;
    
    public void startPermuting(int k) {
        indices = new int[k];
        for(int i = 0; i < k; i++) {
            indices[i] = i;
        }
    }
    
    public void nextPermutation(int k) {
        int i = k - 2;
        while(i > 0 && indices[i] > indices[i+1]) { i--; }
        if(i == 0 && indices[0] > indices[1]) { restorePermutation(k); return; }
        int j = k - 1;
        while(indices[j] < indices[i]) { j--; }
        long tmpCard = cards[size - 1 - i];
        cards[size - 1 - i] = cards[size - 1 - j];
        cards[size - 1 - j] = tmpCard;
        int tmpIdx = indices[i];
        indices[i] = indices[j];
        indices[j] = tmpIdx;
        i++;
        int l = k - 1;
        while(i < l) {
            tmpIdx = indices[i]; indices[i] = indices[l]; indices[l] = tmpIdx;
            tmpCard = cards[size - 1 - i]; cards[size - i - 1] = cards[size - l - 1]; cards[size - l - 1] = tmpCard;
            i++; l--;
        }
    }
    
    public void restorePermutation(int k) {
        int i = 0;
        int l = k - 1;
        while(i < l) {
            long tmpCard = cards[size - 1 - i];
            cards[size - i - 1] = cards[size - l - 1];
            cards[size - l - 1] = tmpCard;
            i++; l--;
        }
    }
    
    // More experimental stuff
     
    private static final int SAMPLES = 20;
    
    public double countFoulsGT(long[] hand, int idx, short thres, Evaluator ev) {
        int total = 0;
        for(int i = 0; i < SAMPLES; i++) {
            for(int j = idx; j < hand.length; j++) {
                hand[j] = cards[(i + j) % size];
            }
            if(ev.evalFive(hand) > thres) { total++; }
        }
        return total / (double)SAMPLES;
    }
    
    public double countFoulsLT(long[] hand, int idx, short thres, Evaluator ev) {
        int total = 0;
        for(int i = 0; i < SAMPLES; i++) {
            for(int j = idx; j < hand.length; j++) {
                hand[j] = cards[(i + j) % size];
            }
            if(ev.evalFive(hand) < thres) { total++; }
        }
        return total / (double)SAMPLES;
    }
    
}
