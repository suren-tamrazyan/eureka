package game;

import java.io.*;

public class GameEvs implements Serializable {
	public Game game;
	public Event[] evs;
	
	public GameEvs() {
		this(null, new Event[0]);
	}
	
	public GameEvs(Game game, Event[] evs) {
		this.game = game;
		this.evs = evs;
	}
	
	public String toString() {
		if(game==null)
			return null;
		return this.game.toString();
	}
	
	public EventNlh[] getEventsNLH () {
		return castEventsNLH(evs);
	}
	
	public static EventNlh[] castEventsNLH (Event[] events) {
		EventNlh[] ret = new EventNlh[events.length];
		for (int i = 0; i < events.length; i++) ret[i] = (EventNlh) events[i];
		return ret;
	}
	
	public EventOfc[] getEventsOfc () {
		EventOfc[] ret = new EventOfc[evs.length];
		for (int i = 0; i < evs.length; i++) ret[i] = (EventOfc) evs[i];
		return ret;
	}
	
	public static EventRummy[] castEventsRummy (Event[] events) {
		EventRummy[] ret = new EventRummy[events.length];
		for (int i = 0; i < events.length; i++) ret[i] = (EventRummy) events[i];
		return ret;
	}
}
