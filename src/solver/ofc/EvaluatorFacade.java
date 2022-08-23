package solver.ofc;

import java.util.*;
import java.util.stream.Collectors;

import org.paukov.combinatorics3.Generator;

import game.Card;
import game.GameOfc;
import game.PlayerOfc;
import solver.ofc.evaluator.Board;
import solver.ofc.evaluator.Deck;
import solver.ofc.evaluator.Evaluator;
import solver.ofc.evaluator.HandRank;
import util.Misc;

public class EvaluatorFacade {
	
	public static final int THREAD_COUNT = 5;
	public static final int TIME_LIMIT = 10;

    private static long[] card2code = new long[52];
    static {
        for (int i = 0; i < 52; i++) card2code[i] =  Evaluator.encodeCard(Card.getCard(i).toString());
    }
    public static void encodeHand(Collection<Card> cards, long[] hand) {
        int i = 0;
        for (Card card : cards) {
            hand[i] = card2code[card.getIndex()];
            i++;
        }
    }
	
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
                case ROYAL_FLUSH: return 50;
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
	
//	public static double evaluate(List<Card> front, List<Card> middle, List<Card> back, boolean inFantasy) {
////		return evaluateByBoard(front, middle, back);
//
//		if (front.size() != 3 || middle.size() != 5 || back.size() != 5)
//			throw new IllegalArgumentException("Error: front/middle/back have't full boxes");
//
//        String strFront = front.stream().map(Card::toStrDirect).collect(Collectors.joining(""));
//        String strMiddle = middle.stream().map(Card::toStrDirect).collect(Collectors.joining(""));
//        String strBack = back.stream().map(Card::toStrDirect).collect(Collectors.joining(""));
//
//        return evaluate(strFront, strMiddle, strBack, inFantasy);
//    }
        
    public static double evaluate(List<Card> front, List<Card> middle, List<Card> back, boolean inFantasy) {
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
        encodeHand(front, hands[0]);
        encodeHand(middle, hands[1]);
        encodeHand(back, hands[2]);
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
            case ROYAL_FLUSH: bonuses[1] = 50; break;
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
            case ROYAL_FLUSH: bonuses[2] = 25; break;
            default: bonuses[2] = 0; break;
        }
        
        // Fantasyland! 
        if (inFantasy)
        	if(Evaluator.getHandRank(evals[0]) == HandRank.TRIPS || Evaluator.getHandRank(evals[2]) == HandRank.QUADS || Evaluator.getHandRank(evals[2]) == HandRank.STRAIGHT_FLUSH || Evaluator.getHandRank(evals[2]) == HandRank.ROYAL_FLUSH) { bonuses[0] += fantasyLand; }


        // fouled
        if (evals[0] < evals[1] || evals[1] < evals[2])
        	return Config.FAIL_PENALTY;//return 0;
        
        double bonus = (double)(bonuses[0] + bonuses[1] + bonuses[2]);
        return bonus + (norm_eval_kicker0 + norm_eval_kicker1 + norm_eval_kicker2)/REGULARIZATION_PARAM;
	}

    private static class Struct {
        public long[][] hands;
        public short[] evals;
        public int[] bonuses;
//        public boolean willFantasy = false;
        public Struct() {
            evals = new short[4];
            evals[0] = evals[1] = evals[2] = -1;
            // The cached values of bonuses for completed hands
            bonuses = new int[3];
            // The three hands as arrays of cards, each card a long
            hands = new long[3][];
            hands[0] = new long[3];
            hands[1] = new long[5];
            hands[2] = new long[5];
        }
    }
    private static Struct prepare(List<Card> front, List<Card> middle, List<Card> back, boolean inFantasy, Evaluator ev, int fantasyLand) {
        Struct result = new Struct();
        encodeHand(front, result.hands[0]);
        encodeHand(middle, result.hands[1]);
        encodeHand(back, result.hands[2]);
        // The value of reaching Fantasyland
//        final int fantasyLand = Config.FANTASY_SCORE;

        // Front hand
        result.evals[0] = ev.evalThree(result.hands[0], true);
        if(result.evals[0] <= 6185) {
            result.evals[3] = result.evals[0];
            if(Evaluator.getHandRank(result.evals[0]) == HandRank.TRIPS) {
                int r1 = (int)(result.hands[0][0] >> 8) & 0xF;
                result.bonuses[0] += (r1 + 10);
            }
            if(Evaluator.getHandRank(result.evals[0]) == HandRank.ONE_PAIR) {
                int r1 = (int)(result.hands[0][0] >> 8) & 0xF;
                int r2 = (int)(result.hands[0][1] >> 8) & 0xF;
                int r3 = (int)(result.hands[0][2] >> 8) & 0xF;
                if(r1 == r2 && r1 >= 4) { result.bonuses[0] = (r1 - 3); }
                else if(r1 == r3 && r1 >= 4) { result.bonuses[0] = (r1 - 3); }
                else if(r2 == r3 && r2 >= 4) { result.bonuses[0] = (r2 - 3); }
            }
            // Fantasyland! (QQ2 evaluates to 3985)
            if (!inFantasy)
                if(result.evals[0] <= 3985) { result.bonuses[0] += fantasyLand; /*result.willFantasy = true;*/}
        } else {
            result.evals[3] = ev.evalThree(result.hands[0], false);
        }

        // Middle hand
        result.evals[1] = ev.evalFive(result.hands[1]);
        switch(Evaluator.getHandRank(result.evals[1])) {
            case TRIPS: result.bonuses[1] = 2; break;
            case STRAIGHT: result.bonuses[1] = 4; break;
            case FLUSH: result.bonuses[1] = 8; break;
            case FULL_HOUSE: result.bonuses[1] = 12; break;
            case QUADS: result.bonuses[1] = 20; break;
            case STRAIGHT_FLUSH: result.bonuses[1] = 30; break;
            case ROYAL_FLUSH: result.bonuses[1] = 50; break;
            default: result.bonuses[1] = 0; break;
        }

        // Back hand
        result.evals[2] = ev.evalFive(result.hands[2]);
        switch(Evaluator.getHandRank(result.evals[2])) {
            case STRAIGHT: result.bonuses[2] = 2; break;
            case FLUSH: result.bonuses[2] = 4; break;
            case FULL_HOUSE: result.bonuses[2] = 6; break;
            case QUADS: result.bonuses[2] = 10; break;
            case STRAIGHT_FLUSH: result.bonuses[2] = 15; break;
            case ROYAL_FLUSH: result.bonuses[2] = 25; break;
            default: result.bonuses[2] = 0; break;
        }

        // Fantasyland!
        if (inFantasy)
            if(Evaluator.getHandRank(result.evals[0]) == HandRank.TRIPS || Evaluator.getHandRank(result.evals[2]) == HandRank.QUADS || Evaluator.getHandRank(result.evals[2]) == HandRank.STRAIGHT_FLUSH || Evaluator.getHandRank(result.evals[2]) == HandRank.ROYAL_FLUSH) { result.bonuses[0] += fantasyLand; /*result.willFantasy = true;*/}

        return result;
    }
    public static int evaluate(List<Card> frontHero, List<Card> middleHero, List<Card> backHero, List<Card> frontOpp, List<Card> middleOpp, List<Card> backOpp, boolean inFantasyHero, boolean inFantasyOpp, int fantasyScore) {
        Evaluator ev = new Evaluator();
        Struct hero = prepare(frontHero, middleHero, backHero, inFantasyHero, ev, fantasyScore);
        Struct opp = prepare(frontOpp, middleOpp, backOpp, inFantasyOpp, ev, fantasyScore);
        short ef1 = hero.evals[0];
        short em1 = hero.evals[1];
        short eb1 = hero.evals[2];
        short ef13 = hero.evals[3];
        short ef2 = opp.evals[0];
        short em2 = opp.evals[1];
        short eb2 = opp.evals[2];
        short ef23 = opp.evals[3];
        boolean fouled1 = (ef1 < em1 || em1 < eb1);
        boolean fouled2 = (ef2 < em2 || em2 < eb2);
        if(fouled1 && fouled2) { return 0; }
        if(fouled1) {
            return -6 - (opp.bonuses[0] + opp.bonuses[1] + opp.bonuses[2]);
        }
        if(fouled2) {
            return +6 + (hero.bonuses[0] + hero.bonuses[1] + hero.bonuses[2]);
        }
        int wins = 0;
        if(ef13 <= 6185 || ef23 <= 6185) { // I think the author was wrong  if(ef13 <= 6685 || ef23 <= 6685) {
            // at least one front is pair or trips, compare straight up
            if(ef13 < ef23) { wins = 1; }
            if(ef13 > ef23) { wins = -1; }
        }
        else {
            // neither front has pair or trips
            wins = ev.compareThrees(hero.hands[0], opp.hands[0]);
        }
        wins += (em1 < em2) ? 1: 0;
        wins += (eb1 < eb2) ? 1: 0;
        wins -= (em1 > em2) ? 1: 0;
        wins -= (eb1 > eb2) ? 1: 0;
        if(wins == 3) { wins = 6; } // 6 points for winning all three
        if(wins == -3) { wins = -6; }
        return wins + (hero.bonuses[0] + hero.bonuses[1] + hero.bonuses[2])
                - (opp.bonuses[0] + opp.bonuses[1] + opp.bonuses[2]);
    }

    /**
     * evaluate game by board with all players
     * @param game
     * @param heroName who is the hero in this evaluation case
     * @return sum of score between all players
     */
    public static int evaluateGame(GameOfc game, String heroName, int fantasyScore) {
        int result = 0;
        PlayerOfc hero = game.getPlayer(heroName);
        if (hero == null) return result;
//        Evaluator ev = new Evaluator();
        for (PlayerOfc player : game.getPlayers()) {
            if (player.isHero(heroName)) continue;
            result += evaluate(hero.boxFront.toList(), hero.boxMiddle.toList(), hero.boxBack.toList(), player.boxFront.toList(), player.boxMiddle.toList(), player.boxBack.toList(), hero.playFantasy, player.playFantasy, fantasyScore);
        }
        return result;
    }


    /**
     * evaluate game by board with all players
     * @param game
     * @param heroName who is the hero in this evaluation case
     * @return sum of score between all players
     */
    public static int evaluateByBoard(GameOfc game, String heroName) {
        int result = 0;
        PlayerOfc hero = game.getPlayer(heroName);
        if (hero == null) return result;
        Deck d = new Deck();
        Evaluator ev = new Evaluator();
        Board heroBoard = hand2board(hero, d, ev);
        for (PlayerOfc player : game.getPlayers()) {
            if (player.isHero(heroName)) continue;
            Board otherBoard = hand2board(player, d, ev);
            result += heroBoard.endResult(otherBoard, ev);
        }
        return result;
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
//		System.out.println(evaluateByBoard(Arrays.asList(Card.str2Cards("4dKdJs")), Arrays.asList(Card.str2Cards("2h5c6c3h7d")), Arrays.asList(Card.str2Cards("6h6d6sAcAs"))));

        // STRAIGHT_FLUSH
        List<String> sfHands = Arrays.asList("2h3h4h5hAh", "2h3h4h5h6h", "3h4h5h6h7h", "4h5h6h7h8h", "5h6h7h8h9h", "6h7h8h9hTh", "7h8h9hThJh", "8h9hThJhQh", "9hThJhQhKh", "ThJhQhKhAh",
                                            "2d3d4d5dAd", "2d3d4d5d6d", "3d4d5d6d7d", "4d5d6d7d8d", "5d6d7d8d9d", "6d7d8d9dTd", "7d8d9dTdJd", "8d9dTdJdQd", "9dTdJdQdKd", "TdJdQdKdAd",
                                            "2s3s4s5sAs", "2s3s4s5s6s", "3s4s5s6s7s", "4s5s6s7s8s", "5s6s7s8s9s", "6s7s8s9sTs", "7s8s9sTsJs", "8s9sTsJsQs", "9sTsJsQsKs", "TsJsQsKsAs",
                                            "2c3c4c5cAc", "2c3c4c5c6c", "3c4c5c6c7c", "4c5c6c7c8c", "5c6c7c8c9c", "6c7c8c9cTc", "7c8c9cTcJc", "8c9cTcJcQc", "9cTcJcQcKc", "TcJcQcKcAc");
        Evaluator ev = new Evaluator();
        for (String sfHand : sfHands) {
            long[] hand = new long[5];
            Evaluator.encodeHand(sfHand, hand);
            short eval = ev.evalFive(hand);
            System.out.println(Misc.sf("%s %d %s", sfHand, eval, Evaluator.getHandRank(eval)));
        }
	}
}
