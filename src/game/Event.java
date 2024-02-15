package game;

import util.Misc;

public class Event {
	public int type;
	public String who;
	public String sVal;
	public long iVal;
	public long createTime;
	public static final int TYPE_END_GAME = 99;

	public Event() {
	}
	public Event(int type, String who) {
		this.type = type;
		this.who = who;
		this.createTime = Misc.getTime();
	}
	public Event(int type, long iVal) {
		this.type = type;
		this.iVal = iVal;
		this.createTime = Misc.getTime();
	}
	public Event(int type, String who, long iVal) {
		this.type = type;
		this.iVal = iVal;
		this.who = who;
		this.createTime = Misc.getTime();
	}
//	public Event(double weight, int type, String who, long iVal) {
//		this(type,who,iVal);
//		this.weight = weight;
//		this.createTime = Misc.getTime();
//	}
//	public Event(double weight, int type, String who) {
//		this(type,who);
//		this.weight = weight;
//		this.createTime = Misc.getTime();
//	}
	public Event(int type, String who, String sVal) {
		this.type = type;
		this.sVal = sVal;
		this.who = who;
		this.createTime = Misc.getTime();
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (iVal ^ (iVal >>> 32));
		result = prime * result + ((sVal == null) ? 0 : sVal.hashCode());
		result = prime * result + type;
		result = prime * result + ((who == null) ? 0 : who.hashCode());
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
		Event other = (Event) obj;
		if (iVal != other.iVal)
			return false;
		if (sVal == null) {
			if (other.sVal != null)
				return false;
		} else if (!sVal.equals(other.sVal))
			return false;
		if (type != other.type)
			return false;
		if (who == null) {
			if (other.who != null)
				return false;
		} else if (!who.equals(other.who))
			return false;
		return true;
	}
}
