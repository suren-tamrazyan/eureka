package game;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EventOfc extends Event {

	public EventOfc() {
	}

	public EventOfc(int type, String who, long iVal) {
		super(type, who, iVal);
	}

	public EventOfc(int type, String who) {
		super(type, who);
	}
	
	public EventOfc(int type, String who, long putToFront, long putToMiddle, long putToBack, List<Card> putToDead) {
		super(type, who);
		Arrays.asList(Card.mask2Cards(putToFront)).stream().forEach((x) -> card2box.put(x, GameOfc.BOX_LEVEL_FRONT));
		Arrays.asList(Card.mask2Cards(putToMiddle)).stream().forEach((x) -> card2box.put(x, GameOfc.BOX_LEVEL_MIDDLE));
		Arrays.asList(Card.mask2Cards(putToBack)).stream().forEach((x) -> card2box.put(x, GameOfc.BOX_LEVEL_BACK));
		putToDead.stream().forEach((x) -> card2box.put(x, GameOfc.BOX_LEVEL_DEAD));
	}
	
	public EventOfc(String who, long resultChips, Map<String, GameOfc.Score> results) {
		super(EventOfc.PAY_WINS, who, resultChips);
		resultOpponentScore.putAll(results);
	}

	public static final int TYPE_DEAL_CARDS = 1;
	public static final int PUT_CARDS_TO_BOXES = 2;
	public static final int FANTASY_CARDS_TO_BOXES = 3;
	public static final int SHOW_DEAD_CARDS = 4;
	public static final int PAY_WINS = 5;
	public static final int REMOVE_PLAYER = 6;
	
	public Map<Card, Integer> card2box = new LinkedHashMap<Card, Integer>();
	public Map<String, GameOfc.Score> resultOpponentScore = new HashMap<>();

	public static String evType2Str(int evType) {
		switch (evType) {
		case TYPE_DEAL_CARDS:
			return "TYPE_DEAL_CARDS";
		case PUT_CARDS_TO_BOXES:
			return "PUT_CARDS_TO_BOXES";
		case FANTASY_CARDS_TO_BOXES:
			return "FANTASY_CARDS_TO_BOXES";
		case SHOW_DEAD_CARDS:
			return "SHOW_DEAD_CARDS";
		case PAY_WINS:
			return "PAY_WINS";
		case REMOVE_PLAYER:
			return "REMOVE_PLAYER";
		default:
			return "unknown type";
		}
	}

	public boolean equalCardBoxes(Object obj){
		EventOfc other = (EventOfc) obj;
		if (card2box == null) {
			if (other.card2box != null)
				return false;
		} else if (!card2box.equals(other.card2box))
			return false;
		return true;
	}

	@Override
	public String toString() {
		if (type==PAY_WINS) {
			return String.format("EventOFC [type=%s, who=%s, chips=%d, scores=%s]", evType2Str(type), who, iVal, resultOpponentScore);
		} else {
			String strFront = card2box.entrySet().stream().filter((entry) -> entry.getValue() == GameOfc.BOX_LEVEL_FRONT).map((ent) -> ent.getKey().toString()).collect(Collectors.joining(""));
			String strMiddle = card2box.entrySet().stream().filter((entry) -> entry.getValue() == GameOfc.BOX_LEVEL_MIDDLE).map((ent) -> ent.getKey().toString()).collect(Collectors.joining(""));
			String strBack = card2box.entrySet().stream().filter((entry) -> entry.getValue() == GameOfc.BOX_LEVEL_BACK).map((ent) -> ent.getKey().toString()).collect(Collectors.joining(""));
			String strDead = card2box.entrySet().stream().filter((entry) -> entry.getValue() == GameOfc.BOX_LEVEL_DEAD).map((ent) -> ent.getKey().toString()).collect(Collectors.joining(""));
			String strCard2Box = String.format("(F:%s, M:%s, B:%s, D:%s)", strFront, strMiddle, strBack, strDead);
			return String.format("EventOFC [type=%s, who=%s, iVal=%s, card2box=%s]", evType2Str(type), who, Card.mask2Str(iVal), strCard2Box);
		}
	}

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((card2box == null) ? 0 : card2box.hashCode());
        result = prime * result + ((resultOpponentScore == null) ? 0 : resultOpponentScore.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        EventOfc other = (EventOfc) obj;
        if (card2box == null) {
            if (other.card2box != null)
                return false;
        } else if (!card2box.equals(other.card2box))
            return false;
        if (resultOpponentScore == null) {
            if (other.resultOpponentScore != null)
                return false;
        } else if (!resultOpponentScore.equals(other.resultOpponentScore))
            return false;
        return true;
    }
}
