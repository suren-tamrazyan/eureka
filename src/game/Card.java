package game;

import java.io.*;
import util.*;
import java.util.*;

public class Card implements Serializable {
	private int index;
	private long mask = -1;
	public final static int DECK_SIZE = 54;
	private static Card[] cards = new Card[DECK_SIZE];
	private static String[] strcards = new String[DECK_SIZE];
	
	static {
		for (int i = 0; i < DECK_SIZE; i++) cards[i] = new Card(i);
		for (int i = 0; i < DECK_SIZE; i++) strcards[i] = cards[i].toString();
	}
	
	public static Card getCard(int index) {
		assert index >= 0 && index < DECK_SIZE;
		return cards[index];
	}
	public static Card getCard(int rank, int suit) {
		int index;
		if (rank == Deck.RANK_JOKER) {
			if (suit == Deck.SUIT_JOKER_RED)
				index = 52;
			else if (suit == Deck.SUIT_JOKER_BLACK)
				index = 53;
			else
				index = -1;
		} else
			index = rank*4+suit;
		return getCard(index);
	}
	public static Card getCard(String s) {
		return getCard(Deck.parseRank(s.substring(0,1)),Deck.parseSuit(s.substring(1,2)));
	}
	
	private Card(int index) {
		assert index >= 0 && index < DECK_SIZE;
		assert cards[index] == null;
		this.index = index;
	}
//	private Card(int rank, int suit) {
//		this.index = rank*4+suit;
//	}
//	private Card(String s) {
//		this(Deck.parseRank(s.substring(0,1)),Deck.parseSuit(s.substring(1,2)));
//	}
	public int getIndex() {
		return this.index;
	}
	public int getRank() {
		if (this.index == 52 || this.index == 53)
			return Deck.RANK_JOKER;
		else
			return this.index/4;
	}
	public int getSuit() {
		switch (this.index) {
		case 52:
			return Deck.SUIT_JOKER_RED;
		case 53:
			return Deck.SUIT_JOKER_BLACK;
		default:
			return this.index%4;
		}		
	}
	public long getMask() {
		if (this.mask == -1)
			this.mask = Deck.createCardMask(this.getRank(), this.getSuit());
		return this.mask;
	}
	public static Card[] join(Card[] ar1, Card[] ar2) {
		Card[] ret = new Card[ar1.length+ar2.length];
		System.arraycopy(ar1, 0, ret, 0, ar1.length);
		System.arraycopy(ar2, 0, ret, ar1.length, ar2.length);
		return ret;
	}
	public static Card[] join(Card[] ar1, Card c) {
		return Card.join(ar1, new Card[]{c});
	}
	public static Card[] cloneCards(Card[] ar) {
		return cloneCards(ar, ar.length);
	}
	public static Card[] cloneCards(Card[] ar, int len) {
		Card[] ret = new Card[len];
		System.arraycopy(ar, 0, ret, 0, len);
		return ret;
	}
	public static String mask2Str(long mask) {
		return mask2Str(mask, "");
	}
	public static String mask2Str(long mask, String delim) {
		if (mask == 0) return "";
		return Deck.cardMaskString(mask, delim);
	}
	public static Card[] mask2Cards_old(long mask) {
		return str2Cards(mask2Str(mask), false);
	}
	public static Card[] mask2Cards(long mask) {
		Card[] ret = new Card[DECK_SIZE];
		int c = 0;
		for (int i = 0; i < DECK_SIZE; i++) {
			if ((mask & Card.getCard(i).getMask()) != 0) ret[c++] = Card.getCard(i);
		}
		Card[] ret2 = new Card[c];
		System.arraycopy(ret, 0, ret2, 0, c);
		return ret2;
	}
	public static String cards2Str(Card[] cards, String delim) {
		if (cards == null) return "null";
		String ret = "";
		for (int i = 0; i < cards.length; i++) {
			if (i != 0) ret += delim;
			ret += cards[i].toString();
		}
		return ret;
	}
	public static String cards2Str(Card[] cards) {
		return cards2Str(cards, "");
	}
	public static String cards2StrPre(Card[] cards) {
		assert cards.length == 2;
		if (cards[0].getIndex() < cards[1].getIndex())
			cards = new Card[]{cards[1], cards[0]};
		String ret = ""+Deck.rankString(cards[0].getRank())+Deck.rankString(cards[1].getRank());
		if (cards[0].getSuit() == cards[1].getSuit()) ret += "s";
		else ret += "o";
		return ret;
	}
	
	public static int[] cards2Ranks(Card[] cards) {
		int[] ranks = new int[cards.length];
		for (int i = 0; i < cards.length; i++) ranks[i] = cards[i].getRank();
		return ranks;
	}
	
	public static Card[] str2Cards(String str) {
		return str2Cards(str, true);
	}
	public static Card[] str2Cards(String str, boolean repl) {
		if (repl) str = str.replaceAll("[ ,_]", "").replaceAll("10", "T");
		Card[] cards = new Card[str.length()/2];
		for (int i = 0; i < str.length()/2; i++) {
			cards[i] = getCard(str.substring(i*2, i*2+2));
		}
		return cards;
	}
	public static long cards2Mask(Card[] cards) {
		long m = 0;
		for (Card c: cards) m |= c.getMask();
		return m;
	}
	public static long cards2Mask(List<Card> cards) {
		long m = 0;
		for (Card c: cards) m |= c.getMask();
		return m;
	}
	public static Card[] ar2Cards(int[] ranks, int[] suits) {
		assert ranks.length == suits.length;
		Card[] ret = new Card[ranks.length];
		for (int i = 0; i < ranks.length; i++) {
			ret[i] = getCard(ranks[i], suits[i]);
		}
		return ret;
	}

	public static boolean checkNoRepeat(Card[] cards) {
		long m = 0;
		for (Card c: cards) {
			if ((m & c.getMask()) != 0) return false;
			m |= c.getMask();
		}
		return true;
	}
	public static Card[] orderByIndex(Card[] cards) {
		Card[] ret = new Card[cards.length];
		System.arraycopy(cards, 0, ret, 0, cards.length);
		for (int i = 0; i < cards.length-1; i++) {
			for (int j = 1; j < cards.length; j++) {
				if (ret[j].getIndex() < ret[j-1].getIndex()) {
					Card tmp = ret[j];
					ret[j] = ret[j-1];
					ret[j-1] = tmp;
				}
			}
		}
		return ret;
	}
	
	public static Card[] getRandomCards(int len, long deadMask) {
		Card[] cards = new Card[len];
		long m = 0;
		for (int i = 0; i < len; i++) {
			Card c = null;
			do {
				c = Card.getCard(Misc.rand.nextInt(DECK_SIZE));
			} while ((m & c.getMask()) != 0 || (deadMask & c.getMask()) != 0);
			m |= c.getMask();
			cards[i] = c;
		}
		return cards;
	}
	
	public static Card[] getRandomCardsWithoutJoker(int len, long deadMask) {
		Card[] cards = new Card[len];
		long m = 0;
		for (int i = 0; i < len; i++) {
			Card c = null;
			do {
				c = Card.getCard(Misc.rand.nextInt(DECK_SIZE-2));
			} while ((m & c.getMask()) != 0 || (deadMask & c.getMask()) != 0);
			m |= c.getMask();
			cards[i] = c;
		}
		return cards;
	}
	
	private static Card[][][] seqLength2Seq = new Card[2][][];

	public static Card[][] getAllOrderedCardSeq(int seqLength) {
		
		assert seqLength >=0 && seqLength <= 2 : "Incorrect segLength="+seqLength;
		
		if (seqLength == 0) return new Card[0][2];
		
		if (seqLength2Seq[seqLength-1] == null) {

			int retLength = 1;
			for (int i = 0; i < seqLength; i++) retLength *= DECK_SIZE-i;
			Card[][] ret = new Card[retLength][seqLength];
			int curSeqCount = 0;
			
			//int rank = 0, suit = 0;
			int card = 0;
			Card[] curSeq = new Card[seqLength];
			for (int c1 = 0; c1 < DECK_SIZE; c1++) {
				/*rank = c1 / 4;
				suit = c1 % 4;
				card = Deck.createCardMask(rank, suit);*/
				curSeq[0] = Card.getCard(c1);
				if (seqLength == 1) {
					ret[curSeqCount++] = curSeq.clone();
					continue;
				}
				for (int c2 = 0; c2 < DECK_SIZE; c2++) {
					if (c2 == c1) continue;
					/*rank = c2 / 4;
					suit = c2 % 4;
					card = Deck.createCardMask(rank, suit);*/
					curSeq[1] = Card.getCard(c2);
					if (seqLength == 2) {
						ret[curSeqCount++] = curSeq.clone();
						continue;
					}
				}
			}
			seqLength2Seq[seqLength-1] = ret;
		}
		return seqLength2Seq[seqLength-1];
	}

	@Override
	public String toString() {
		return Deck.cardMaskString(this.getMask());
	}

	public String toStrDirect() {
		return strcards[index];
	}
	
	@Override
	public boolean equals(Object obj) {
		if (! (obj instanceof Card)) return false;
		Card card = (Card)obj;
		return this.index == card.index;
	}

	public String toGraphicString() {
		return Deck.cardGraphic(getRank(), getSuit());
	}
	
	
	public static void main(String[] args) throws Exception {
		for (int r = 0; r < Deck.RANK_COUNT; r++)
			for (int s = 0; s < Deck.SUIT_COUNT; s++)
				System.out.println(getCard(r, s));
	}

	@Override
	public int hashCode() {
		return index;
	}
}
