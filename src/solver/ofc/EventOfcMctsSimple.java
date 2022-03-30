package solver.ofc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import game.Card;
import game.EventOfc;

public class EventOfcMctsSimple {

	public int type;
	public List<Card> cardsToBeBoxed;
	public List<Card> front;
	public List<Card> middle;
	public List<Card> back;
	public List<Card> dead;
	
	public EventOfcMctsSimple(int type, Collection<Card> aCardsToBeBoxed) {
		this.type = type;
		this.cardsToBeBoxed = new ArrayList<>(aCardsToBeBoxed);
	}


	public EventOfcMctsSimple(int type, Collection<Card> putToFront, Collection<Card> putToMiddle, Collection<Card> putToBack, Collection<Card> putToDead) {
		this.type = type;
		front = new ArrayList<>(putToFront);
		middle = new ArrayList<>(putToMiddle);
		back = new ArrayList<>(putToBack);
		dead = new ArrayList<>(putToDead);
	}
	
	public EventOfc toEventOfc(String hero) {
		if (type == EventOfc.TYPE_DEAL_CARDS)
			return new EventOfc(type, hero, Card.cards2Mask(cardsToBeBoxed.toArray(new Card[0])));
		return new EventOfc(type, hero, Card.cards2Mask(front.toArray(new Card[0])), Card.cards2Mask(middle.toArray(new Card[0])), Card.cards2Mask(back.toArray(new Card[0])), dead);
	}
	
//	@Override
//	public String toString() {
//		if (type == EventOfc.PUT_CARDS_TO_BOXES)
//			return String.format("F: %s; M: %s; B: %s; D: %s", front, middle, back, dead);
//		if (type == EventOfc.TYPE_DEAL_CARDS)
//			return String.format("DEAL: %s", cardsToBeBoxed);
//		return "";
//	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((back == null) ? 0 : back.hashCode());
		result = prime * result + ((cardsToBeBoxed == null) ? 0 : cardsToBeBoxed.hashCode());
		result = prime * result + ((dead == null) ? 0 : dead.hashCode());
		result = prime * result + ((front == null) ? 0 : front.hashCode());
		result = prime * result + ((middle == null) ? 0 : middle.hashCode());
		result = prime * result + type;
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EventOfcMctsSimple other = (EventOfcMctsSimple) obj;
		if (back == null) {
			if (other.back != null)
				return false;
		} else if (!back.equals(other.back))
			return false;
		if (cardsToBeBoxed == null) {
			if (other.cardsToBeBoxed != null)
				return false;
		} else if (!cardsToBeBoxed.equals(other.cardsToBeBoxed))
			return false;
		if (dead == null) {
			if (other.dead != null)
				return false;
		} else if (!dead.equals(other.dead))
			return false;
		if (front == null) {
			if (other.front != null)
				return false;
		} else if (!front.equals(other.front))
			return false;
		if (middle == null) {
			if (other.middle != null)
				return false;
		} else if (!middle.equals(other.middle))
			return false;
		if (type != other.type)
			return false;
		return true;
	}

}
