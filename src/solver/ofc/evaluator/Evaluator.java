/* Poker 5-card hand rank evaluator based on Cactus Kev's poker hand evaluator at
 * http://www.suffecool.net/poker/evaluator.html
 * with additional three card hand ranks for Open Face Chinese Poker
 */
package solver.ofc.evaluator;

import java.util.concurrent.*;

public class Evaluator {

    private static final int primes[] = { 2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41 };
    private static final long[] suitOffsets = { 0x8000, 0x4000, 0x2000, 0x1000 };
    private static final String ranks = "23456789TJQKA";
    private static final String suits = "CDHS";

    public static long evalThreeCount = 0;
    public static long evalFiveCount = 0;
    
    private Semaphore mutex = new Semaphore(1);
    
    private void myLittleSort(int[] a) {
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
    
    public static long encodeCard(int suit, int rank) {
        long result = primes[rank] | (rank << 8) | suitOffsets[suit] | (1 << (16 + rank));
        return result;
    }

    public static long encodeCard(String cs) {
        cs = cs.toUpperCase();
        char rank = cs.charAt(0);
        char suit = cs.charAt(1);
        return encodeCard(suits.indexOf(suit), ranks.indexOf(rank));
    }

    public static long[] encodeHand(String cs) {
        long[] hand = new long[cs.length() / 2];
        return encodeHand(cs, hand);
    }
    
    public static long[] encodeHand(String cs, long[] hand) {
        cs = cs.toUpperCase();
        int i = 0;
        while(i < cs.length()) {
            hand[i / 2] = encodeCard(cs.substring(i, i+2));
            i += 2;
        }
        return hand;
    }

    public static HandRank getHandRank(short val) {
        if(val == -1) { System.out.println("Undefined evaluation!!!"); }
        if (val > 6185) return(HandRank.HIGH_CARD);        // 1277 high card
        if (val > 3325) return(HandRank.ONE_PAIR);         // 2860 one pair
        if (val > 2467) return(HandRank.TWO_PAIR);         //  858 two pair
        if (val > 1609) return(HandRank.TRIPS);            //  858 three-kind
        if (val > 1599) return(HandRank.STRAIGHT);         //   10 straights
        if (val > 322)  return(HandRank.FLUSH);            // 1277 flushes
        if (val > 166)  return(HandRank.FULL_HOUSE);       //  156 full house
        if (val > 10)   return(HandRank.QUADS);            //  156 four-kind
        return(HandRank.STRAIGHT_FLUSH);                   //   10 straight-flushes
    }

    public static String decodeCard(long card) {
        long r = (card >> 8) & 0xF;
        char suit;

        r = (card >> 8) & 0xF;
        if ( 0 < (card & 0x8000) )
            suit = 'c';
        else if ( 0 < (card & 0x4000) )
            suit = 'd';
        else if ( 0 < (card & 0x2000) )
            suit = 'h';
        else
            suit = 's';
            
        return ranks.charAt((int)r) + "" + suit;
    }
    
    // Assumes that neither hand is pair or trips
    public int compareThrees(long[] h1, long[] h2) {
        int[] r1 = { (int)(h1[0] >> 8) & 0xF,
                     (int)(h1[1] >> 8) & 0xF,
                     (int)(h1[2] >> 8) & 0xF
                    };
        int[] r2 = { (int)(h2[0] >> 8) & 0xF,
                     (int)(h2[1] >> 8) & 0xF,
                     (int)(h2[2] >> 8) & 0xF
                    };
        myLittleSort(r1);
        myLittleSort(r2);
        if(r1[2] > r2[2]) return +1;
        if(r1[2] < r2[2]) return -1;
        if(r1[1] > r2[1]) return +1;
        if(r1[1] < r2[1]) return -1;
        if(r1[0] > r2[0]) return +1;
        if(r1[0] < r2[0]) return -1;
        return 0;
    }
    
    
    public short evalFive(long[] hand) {
        
        ++evalFiveCount;
        long c1 = hand[0];
        long c2 = hand[1];
        long c3 = hand[2];
        long c4 = hand[3];
        long c5 = hand[4];
        long q;
        short s;

        q = (c1|c2|c3|c4|c5) >> 16;

        /* check for Flushes and StraightFlushes
         */
        if ( (c1 & c2 & c3 & c4 & c5 & 0xF000) != 0)
            return( Flushes.flushes[(int)q] );

        /* check for Straights and HighCard hands
         */
        s = Unique5.unique5[(int)q];
        if ( s != 0 )  return ( s );

        /* let's do it the hard way
         */
        q = (c1&0xFF) * (c2&0xFF) * (c3&0xFF) * (c4&0xFF) * (c5&0xFF);
        q = findit( q );
        return( Values.values[(int)q] );
    }

    private final boolean[] suitIn = new boolean[4];
    private final long[] extend = new long[5];
    
    public short evalThree(long[] hand, boolean compareToFive) { 
        ++evalThreeCount;
        int r1 = (int)(hand[0] >> 8) & 0xF;
        int r2 = (int)(hand[1] >> 8) & 0xF;
        int r3 = (int)(hand[2] >> 8) & 0xF;
        
        boolean isPair = (r1 == r2) || (r1 == r3) || (r2 == r3);
        // trips and pairs
        if(isPair || compareToFive) {
            // find an unused suit to use for filler cards
            suitIn[0] = suitIn[1] = suitIn[2] = suitIn[3] = false;
            for(int i = 0; i < 3; i++) {
                if((hand[0] & 0x8000) > 0) suitIn[0] = true;
                else if((hand[0] & 0x4000) > 0) suitIn[1] = true;
                else if((hand[0] & 0x2000) > 0) suitIn[2] = true;
                else suitIn[3] = true;
            }
            int s = 0;
            while(suitIn[s]) s++;

            int[] p = {r1, r2, r3, 100, 100};
            myLittleSort(p);
            p[3] = p[4] = 0;
            // find two lowest unused cards
            while(p[3] == p[2] || p[3] == p[1] || p[3] == p[0]) { p[3]++; }
            p[4] = p[3] + 1;
            while(p[4] == p[2] || p[4] == p[1] || p[4] == p[0]) { p[4]++; }
            // extend the lowest five card poker hand that this hand is equivalent to
            extend[0] = hand[0];
            extend[1] = hand[1];
            extend[2] = hand[2];
            extend[3] = encodeCard(s, p[3]);
            extend[4] = encodeCard(s, p[4]);            
            short result = evalFive(extend);
            if(result <= 1609) { // oops, made a straight
                int maxi = 0; // find largest non-ace
                for(int i = 1; i < 5; i++) {
                    if(p[i] != 12 && p[i] > p[maxi]) { maxi = i; }
                }
                p[maxi]++; // and increment that
                extend[0] = encodeCard(0, p[0]);
                extend[1] = encodeCard(1, p[1]);
                extend[2] = encodeCard(1, p[2]);
                extend[3] = encodeCard(0, p[3]);
                extend[4] = encodeCard(0, p[4]);
                result = evalFive(extend);
            }
            return result;
        }
        
        // high cards lookup from triangular numbers lookup tables
        int[] p = {r1, r2, r3};
        myLittleSort(p);
        int tally = triAcc[p[2] - 2] + tri[p[1] - 1] + (p[0] - 1);
        int top = triAcc[11];
        int result = 7461 + (top - tally);
        return (short)(result);
        
    }

    private static final int[] tri =
      { 0, 1, 3, 6, 10, 15, 21, 28, 36, 45, 55, 66, 78, 91, 105, 120 };
    private static final int[] triAcc;
    static {
        // accumulate the triangular numbers table
        triAcc = new int[tri.length];
        triAcc[0] = 0;
        for(int i = 1; i < triAcc.length; i++) {
            triAcc[i] = triAcc[i-1] + tri[i];
        }
    }
    
    private static long findit( long key )
    {
        int low = 0, high = 4887, mid;
        while ( low <= high )
        {
            mid = (high + low) / 2;  
            if ( key < Products.products[mid] )
                high = mid - 1;
            else if ( key > Products.products[mid] )
                low = mid + 1;
            else
                return( mid );
        }
        System.out.println("Findit Error! Should not happen!!!!111ELEVENTY");
        return( -1 );
    }

    
}
