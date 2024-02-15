package game;

import java.io.Serializable;

public class Player  implements Cloneable, Serializable{
	public String name;
	public int stack;
	public int won = 0;
	public int lost = 0;
	public int rake = 0;
	
	public Player(String name,int stack) {
		this.name = name;
		this.stack = stack;
	}
	
	public Player clone() {
		Player result = new Player(this.name, this.stack);
		result.won = this.won;
		result.lost = this.lost;
		result.rake = this.rake;
		return result;
	}
	public String getName() {
		return name;
	}
}
