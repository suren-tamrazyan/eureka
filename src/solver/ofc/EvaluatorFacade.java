package solver.ofc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.paukov.combinatorics3.Generator;

import game.Card;
import game.GameOfc;
import game.PlayerOfc;
import solver.ofc.evaluator.Board;
import solver.ofc.evaluator.Deck;
import solver.ofc.evaluator.Evaluator;
import solver.ofc.evaluator.HandRank;

public class EvaluatorFacade {
	
	public static final int THREAD_COUNT = 5;
	public static final int TIME_LIMIT = 10;
	
	public static Board hand2board(PlayerOfc hand, Deck d, Evaluator ev) {
        String strFront = hand.boxFront.toString().replace(".", "");
        String strMiddle = hand.boxMiddle.toString().replace(".", "");
        String strBack = hand.boxBack.toString().replace(".", "");
        Board board = new Board(strFront, strMiddle, strBack, d, ev);
        return board;
	}

	public static Board hand2board(List<Card> front, List<Card> middle, List<Card> back, Deck d, Evaluator ev) {
        String strFront = front.stream().map(Card::toStrDirect).collect(Collectors.joining(""));
        String strMiddle = middle.stream().map(Card::toStrDirect).collect(Collectors.joining(""));
        String strBack = back.stream().map(Card::toStrDirect).collect(Collectors.joining(""));
        Board board = new Board(strFront, strMiddle, strBack, d, ev);
        return board;
	}

	public static int evaluate(PlayerOfc hand) {
		if (!hand.boxesIsFull())
			throw new IllegalArgumentException(String.format("Error: player %s have't full boxes", hand.name));
		
        Deck d = new Deck();
        Evaluator ev = new Evaluator();
		Board board = hand2board(hand, d, ev);
        Board failBoard = new Board("AdAhAd", "2d3cTd7c3d", "9s8s7s6s2h", d, ev);
        int result = board.endResult(failBoard, ev);
        
        return result;
	}

	public static int evaluateByBoard(List<Card> front, List<Card> middle, List<Card> back) {
		if (front.size() != 3 || middle.size() != 5 || back.size() != 5)
			throw new IllegalArgumentException("Error: front/middle/back have't full boxes");
		
        Deck d = new Deck();
        Evaluator ev = new Evaluator();
		Board board = hand2board(front, middle, back, d, ev);
        Board failBoard = new Board("AdAhAd", "2d3cTd7c3d", "9s8s7s6s2h", d, ev);
//        Board failBoard = new Board("5h4h3d", "7d6c5d4c2d", "8s7s6s5s2h", d, ev); minimal not fault
        int result = board.endResult(failBoard, ev);
        
        return result;
	}
	
	public static double[] maxValue(PlayerOfc hand, game.Card aCard, List<game.Card> openCards) throws InterruptedException {
		final long card = Evaluator.encodeCard(aCard.toStrDirect());
		final Deck d = new Deck();
        Evaluator ev = new Evaluator();
		Board board = hand2board(hand, d, ev);
		Board other = new Board(board);
		for (game.Card crd : openCards)
			d.removeCard(Evaluator.encodeCard(crd.toStrDirect()));
		d.removeCard(card);
		
		double[] v = board.maxValueThreads(card, other, d, TIME_LIMIT, THREAD_COUNT, true);
		final double PENALTY = -100000;
		if(board.idx[0] >= 3)
			v[0] = PENALTY;
		if(board.idx[1] >= 5)
			v[1] = PENALTY;
		if(board.idx[2] >= 5)
			v[2] = PENALTY;
		
		return v;
	}

	public static Map<Card, Double> evaluateIncompleteHand(List<Card> lstBox, int boxLevel, List<Card> cardsToBeBoxed, List<Card> openCards) {
		Map<Card, Double> result = new HashMap<>(cardsToBeBoxed.size());
		Map<Card, Long> scores = new HashMap<>(cardsToBeBoxed.size());
		final int DECK_SIZE = 52;
		int boxSize = boxLevel == GameOfc.BOX_LEVEL_FRONT ? 3 : 5;
		if (lstBox.size() == boxSize)
			throw new IllegalArgumentException("box is completed in evaluateIncompleteHand()");
		
		//prepare
		List<Long> longBox = new ArrayList<>(lstBox.size());
		for (Card crd : lstBox)
			longBox.add(Evaluator.encodeCard(crd.toStrDirect()));
		List<Long> longCardsToBeBoxed = new ArrayList<>(cardsToBeBoxed.size());
		for (Card crd : cardsToBeBoxed)
			longCardsToBeBoxed.add(Evaluator.encodeCard(crd.toStrDirect()));
		List<Long> deck = new ArrayList<>(DECK_SIZE);
		for(int i = 0; i < 52; i++)
			deck.add(Evaluator.encodeCard(i / 13, i % 13));
		deck.removeAll(openCards.stream().map(crd -> Evaluator.encodeCard(crd.toStrDirect())).collect(Collectors.toList()));
		deck.removeAll(longCardsToBeBoxed);
		deck.removeAll(longBox);
        Evaluator ev = new Evaluator();
        
        List<List<Long>> combo = Generator.combination(deck).simple(boxSize - 1 - lstBox.size()).stream().collect(Collectors.toList());
        for (List<Long> lst : combo) {
        	for (int i = 0; i < cardsToBeBoxed.size(); i++) {
            	List<Long> comboBox = new ArrayList<>(boxSize);
            	comboBox.addAll(longBox);
            	comboBox.addAll(lst);
        		comboBox.add(longCardsToBeBoxed.get(i));
        		long[] hand = comboBox.stream().mapToLong(l -> l).toArray();
        		short cardEv = evalBonus(boxLevel, hand, ev);
        		Long val = scores.get(cardsToBeBoxed.get(i));
        		if (val == null) val = 0L;
        		scores.put(cardsToBeBoxed.get(i), val+cardEv);
        		
//        		System.out.println(Arrays.toString(hand) + " " + cardEv);
        	}
        }
        
        int n = combo.size();
        // may be more scalable?
        for (Map.Entry<Card, Long> entry : scores.entrySet()) {
        	result.put(entry.getKey(), (double) ((double) entry.getValue()/(double) n));
        }
        
        return result;
	}
	public static short evalBonus(int boxLevel, long[] hand, Evaluator ev) {
		short handEv;
		short fantasyLand = 20;
		switch (boxLevel) {
		case GameOfc.BOX_LEVEL_FRONT:
			handEv = ev.evalThree(hand, true);
			short bonus = 0;
            if(handEv <= 6685) {
                if(Evaluator.getHandRank(handEv) == HandRank.TRIPS) {
                    int r1 = (int)(hand[0] >> 8) & 0xF;
                    bonus += (short)(r1 + 10);
                }
                if(Evaluator.getHandRank(handEv) == HandRank.ONE_PAIR) {
                    int r1 = (int)(hand[0] >> 8) & 0xF;
                    int r2 = (int)(hand[1] >> 8) & 0xF;
                    int r3 = (int)(hand[2] >> 8) & 0xF;
                    if(r1 == r2 && r1 >= 4) { bonus = (short)(r1 - 3); }
                    else if(r1 == r3 && r1 >= 4) { bonus = (short)(r1 - 3); }
                    else if(r2 == r3 && r2 >= 4) { bonus = (short)(r2 - 3); } 
                }
                // Fantasyland! (QQ2 evaluates to 3985)
                if(handEv <= 3985) { bonus += fantasyLand; }
            }
            return bonus;
		case GameOfc.BOX_LEVEL_MIDDLE:
		case GameOfc.BOX_LEVEL_BACK:
			handEv = ev.evalFive(hand);
            switch(Evaluator.getHandRank(handEv)) {
	            case ONE_PAIR: return 1;
	            case TWO_PAIR: return 2;
	            case TRIPS: return 4;
	            
	            case STRAIGHT: return 8;
	            case FLUSH: return 16;
	            case FULL_HOUSE: return 24;
	            case QUADS: return 32;
	            case STRAIGHT_FLUSH: return 40;
				default:
					return 0;
            }
		default:
			throw new IllegalArgumentException("Wrong boxLevel in evalBonus()");
		}
	}
	
	/*
	public static Map<Card, Double> evaluateIncompleteHand(CardBox box, boolean isFrontBox, List<Card> cardsToBeBoxed,	List<Card> openCards) {
		Map<Card, Double> result = new HashMap<>(cardsToBeBoxed.size());
		Map<Card, Long> scores = new HashMap<>(cardsToBeBoxed.size());
		
		//prepare
		List<Card> lstBox = box.toList();
		List<Long> longBox = new ArrayList<>(lstBox.size());
		for (Card crd : lstBox)
			longBox.add(Evaluator.encodeCard(crd.toString()));
		List<Long> longCardsToBeBoxed = new ArrayList<>(cardsToBeBoxed.size());
		for (Card crd : cardsToBeBoxed)
			longCardsToBeBoxed.add(Evaluator.encodeCard(crd.toString()));
		final Deck deck = new Deck();
		for (game.Card crd : openCards)
			deck.removeCard(Evaluator.encodeCard(crd.toString()));
		for (Long crd : longCardsToBeBoxed)
			deck.removeCard(crd);
        Evaluator ev = new Evaluator();
        
        int n = 0;
        final int MULTIPLIER = 7000; // may be more scalable value?
        if (isFrontBox) {
        	
        } else {
        	long[] hand = new long[5];
        	int a, b, c, d, e;
            // loop over every possible five-card hand
        	if (lstBox.size() == 4) {
    			hand[0] = longBox.get(0); hand[1] = longBox.get(1); hand[2] = longBox.get(2); hand[3] = longBox.get(3);
        		for (int i = 0; i < cardsToBeBoxed.size(); i++) {
        			hand[4] = longCardsToBeBoxed.get(i);
        			short cardEv = ev.evalFive(hand);
        			scores.put(cardsToBeBoxed.get(i), scores.get(cardsToBeBoxed.get(i))+cardEv);
        		}
    			n++;
        	} else {
	            for(a=0; a < deck.getSize() - 4; a++) {
	            	if (lstBox.size() == 3) {
	        			hand[0] = longBox.get(0); hand[1] = longBox.get(1); hand[2] = longBox.get(2); hand[3] = longBox.get(3);
	            		for (int i = 0; i < cardsToBeBoxed.size(); i++) {
	            			hand[4] = longCardsToBeBoxed.get(i);
	            			short cardEv = ev.evalFive(hand);
	            			scores.put(cardsToBeBoxed.get(i), scores.get(cardsToBeBoxed.get(i))+cardEv);
	            		}
	        			n++;
	            	} else
			            hand[0] = deck.cards[a];
			            for(b=a+1; b < deck.getSize() - 3; b++) {
			                hand[1] = deck.cards[b];
			                for(c=b+1; c < deck.getSize() - 2; c++) {
			                	hand[2] = deck.cards[c];
				                for(d=c+1; d< deck.getSize() - 1; d++) {
				                    hand[3] = deck.cards[d];
				                    for(e=d+1; e < deck.getSize(); e++) {
				                    	hand[4] = deck.cards[e];
				                    	
				                    }
				                }
			                }
			            }
	            }
        	}
        }
        
		return result;
	}
	*/
	
	public static double evaluate(List<Card> front, List<Card> middle, List<Card> back, boolean inFantasy) {
//		return evaluateByBoard(front, middle, back);
		
		if (front.size() != 3 || middle.size() != 5 || back.size() != 5)
			throw new IllegalArgumentException("Error: front/middle/back have't full boxes");
		
        String strFront = front.stream().map(Card::toStrDirect).collect(Collectors.joining(""));
        String strMiddle = middle.stream().map(Card::toStrDirect).collect(Collectors.joining(""));
        String strBack = back.stream().map(Card::toStrDirect).collect(Collectors.joining(""));
        
        return evaluate(strFront, strMiddle, strBack, inFantasy);
    }
        
    public static double evaluate(String strFront, String strMiddle, String strBack, boolean inFantasy) {
//    	final int MULTIPLIER = 1;//10000;
    	final short MAX_EVAL = 7748 + 1;
    	final int REGULARIZATION_PARAM = 3;
        Evaluator ev = new Evaluator();
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
        Evaluator.encodeHand(strFront, hands[0]); 
        Evaluator.encodeHand(strMiddle, hands[1]);
        Evaluator.encodeHand(strBack, hands[2]);
        // The value of reaching Fantasyland
        final int fantasyLand = Config.FANTASY_SCORE;

        // Front hand
        evals[0] = ev.evalThree(hands[0], true);
        if(evals[0] <= 6185) {
            evals[3] = evals[0];
            if(Evaluator.getHandRank(evals[0]) == HandRank.TRIPS) {
                int r1 = (int)(hands[0][0] >> 8) & 0xF;
                bonuses[0] += (r1 + 10);
            }
            if(Evaluator.getHandRank(evals[0]) == HandRank.ONE_PAIR) {
                int r1 = (int)(hands[0][0] >> 8) & 0xF;
                int r2 = (int)(hands[0][1] >> 8) & 0xF;
                int r3 = (int)(hands[0][2] >> 8) & 0xF;
                if(r1 == r2 && r1 >= 4) { bonuses[0] = (r1 - 3); }
                else if(r1 == r3 && r1 >= 4) { bonuses[0] = (r1 - 3); }
                else if(r2 == r3 && r2 >= 4) { bonuses[0] = (r2 - 3); } 
            }
            // Fantasyland! (QQ2 evaluates to 3985) 
            if (!inFantasy)
            	if(evals[0] <= 3985) { bonuses[0] += fantasyLand; }
        } else {
            evals[3] = ev.evalThree(hands[0], false);
        }        
        double norm_eval_kicker0 = (double)(MAX_EVAL - evals[3]) / MAX_EVAL;
        
        // Middle hand
        evals[1] = ev.evalFive(hands[1]);
        double norm_eval_kicker1 = (double)(MAX_EVAL - evals[1]) / MAX_EVAL;
        switch(Evaluator.getHandRank(evals[1])) {
        	case TRIPS: bonuses[1] = 2; break;
            case STRAIGHT: bonuses[1] = 4; break;
            case FLUSH: bonuses[1] = 8; break;
            case FULL_HOUSE: bonuses[1] = 12; break;
            case QUADS: bonuses[1] = 20; break;
            case STRAIGHT_FLUSH: bonuses[1] = 30; break;
            default: bonuses[1] = 0; break;  
        }

        // Back hand
        evals[2] = ev.evalFive(hands[2]);
        double norm_eval_kicker2 = (double)(MAX_EVAL - evals[2]) / MAX_EVAL;
        switch(Evaluator.getHandRank(evals[2])) {
            case STRAIGHT: bonuses[2] = 2; break;
            case FLUSH: bonuses[2] = 4; break;
            case FULL_HOUSE: bonuses[2] = 6; break;
            case QUADS: bonuses[2] = 10; break;
            case STRAIGHT_FLUSH: bonuses[2] = 15; break;
            default: bonuses[2] = 0; break;
        }
        
        // Fantasyland! 
        if (inFantasy)
        	if(Evaluator.getHandRank(evals[0]) == HandRank.TRIPS || Evaluator.getHandRank(evals[2]) == HandRank.QUADS || Evaluator.getHandRank(evals[2]) == HandRank.STRAIGHT_FLUSH) { bonuses[0] += fantasyLand; }


        // fouled
        if (evals[0] < evals[1] || evals[1] < evals[2])
        	return Config.FAIL_PENALTY;//return 0;
        
        double bonus = (double)(bonuses[0] + bonuses[1] + bonuses[2]);
        return bonus + (norm_eval_kicker0 + norm_eval_kicker1 + norm_eval_kicker2)/REGULARIZATION_PARAM;
	}
	
	public static void main(String[] args) {
/*		List<Card> lstBox = new ArrayList<>();
		lstBox.add(game.Card.getCard("2h"));
		lstBox.add(game.Card.getCard("5h"));
		lstBox.add(game.Card.getCard("Th"));
		lstBox.add(game.Card.getCard("Qh"));
//		lstBox.add(game.Card.getCard("Ah"));
		
		List<Card> cardToBeBoxed = new ArrayList<>();
		cardToBeBoxed.add(game.Card.getCard("Jh"));
		cardToBeBoxed.add(game.Card.getCard("9d"));
		cardToBeBoxed.add(game.Card.getCard("Ts"));
		
		List<Card> openCards = new ArrayList<>();
		openCards.add(game.Card.getCard("Kh"));
		openCards.add(game.Card.getCard("Ks"));
		openCards.add(game.Card.getCard("2s"));
		openCards.add(game.Card.getCard("4s"));
		openCards.add(game.Card.getCard("Jc"));
		
		System.out.println(evaluateIncompleteHand(lstBox, GameOfc.BOX_LEVEL_BACK, cardToBeBoxed, openCards));
*/
		System.out.println(evaluateByBoard(Arrays.asList(Card.str2Cards("4dKdJs")), Arrays.asList(Card.str2Cards("2h5c6c3h7d")), Arrays.asList(Card.str2Cards("6h6d6sAcAs"))));
	}
}
