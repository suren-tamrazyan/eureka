package game;

import java.util.ArrayList;
import java.util.HashMap;

public class EventRummy extends Event {
	public HashMap<Integer, ArrayList<Card>> cardsGroup;
	public int scores = PlayerRummy.UNDEF_SCORE;
	
	public EventRummy(int type, String who, long iVal) {
		super(type, who, iVal);
	}
	
	public EventRummy(int type, String who, HashMap<Integer, ArrayList<Card>> cardsGroup) {
		super(type, who);
		
		this.cardsGroup = GameRummy.copyCardsGroup(cardsGroup);
	}
	
	public EventRummy(int type, String who, long iVal, HashMap<Integer, ArrayList<Card>> cardsGroup) {
		super(type, who, iVal);
		
		this.cardsGroup = GameRummy.copyCardsGroup(cardsGroup);
	}
	
	public EventRummy(int type, String who, long iVal, int score, HashMap<Integer, ArrayList<Card>> cardsGroup) {
		super(type, who, iVal);
		scores = score;
		this.cardsGroup = GameRummy.copyCardsGroup(cardsGroup);
	}
	
	public EventRummy(int type, String who) {
		super(type, who);
	}
	
	public static final int DEAL_CARDS = 1;
	public static final int PICK_CARD_OPEN = 2;
	public static final int PICK_CARD_CLOSED = 3;
	public static final int DISCARD_CARD = 4;
	public static final int DISCARD_CARD_WITH_DECLARE = 5;
	public static final int SORT_CARD = 6;
	public static final int DROP = 7;
	public static final int DECLARE = 8;
	public static final int SCORES = 9;
	public static final int PLAYER_TURN_TIMEOUT = 10;
	public static final int DEALER_SET = 11;
	public static final int JOKER_SET = 12;
	public static final int OPEN_CARD_SET = 13;
	public static final int PREPARE_OPP = 14;
	public static final int END_GAME = 15;
	public static final int TYPE_PROFIT_VALUE = 72;
	public static final int TYPE_RAKE_VALUE = 73;
	public static final int HIDDEN_CARD = 74;


	public static String evType2Str(int evType) {
		switch (evType) {
		case END_GAME:
			return "END_GAME";
		case DEAL_CARDS:
			return "DEAL_CARDS";
		case PICK_CARD_OPEN:
			return "PICK_CARD_OPEN";
		case PICK_CARD_CLOSED:
			return "PICK_CARD_CLOSED";
		case DISCARD_CARD:
			return "DISCARD_CARD";
		case DISCARD_CARD_WITH_DECLARE:
			return "DISCARD_CARD_WITH_DECLARE";
		case SORT_CARD:
			return "SORT_CARD";
		case DROP:
			return "DROP";
		case DECLARE:
			return "DECLARE";
		case SCORES:
			return "SCORES";
		case PLAYER_TURN_TIMEOUT:
			return "PLAYER_TURN_TIMEOUT";
		case DEALER_SET:
			return "DEALER_SET";
		case JOKER_SET:
			return "JOKER_SET";
		case OPEN_CARD_SET:
			return "OPEN_CARD_SET";
		case PREPARE_OPP:
			return "PREPARE_OPP";
		case TYPE_PROFIT_VALUE:
			return "TYPE_PROFIT_VALUE";
		case TYPE_RAKE_VALUE:
			return "TYPE_RAKE_VALUE";
		case HIDDEN_CARD:
			return "HIDDEN_CARD";
		default:
			return "unknown type";
		}
	}
	@Override
	public String toString() {
		String scg = "";
		if(cardsGroup != null)
		{
			for(Integer group : cardsGroup.keySet())
			{
				scg += "[";
				for(Card c : cardsGroup.get(group))
				{
					scg += c + ", ";
				}
				if(scg.length() > 2)
					scg = scg.substring(0, scg.length()-2);
				scg += "]";
			}
		}
		return String.format("EventRummy [type=%s, who=%s, iVal=%s, cardsGroup=%s]", evType2Str(type), who, (type==PICK_CARD_OPEN||type==PICK_CARD_CLOSED||type==DISCARD_CARD||type==DISCARD_CARD_WITH_DECLARE)?Card.mask2Str(iVal):String.valueOf(iVal), scg);
	}
}
