package game;

import java.util.Arrays;
import java.util.Map;

import util.Misc;

public class GamePusoy extends Game {

	boolean wins_payed = false;
	public int royalty = -1;
	public int scoop = -1;
	public int naturals = -1;
	public int bb = -1;
	public GamePusoy(Nw nw, int royalty, int scoop, int naturals)
	{
		this.network = nw;
		this.royalty = royalty;
		this.scoop = scoop;
		this.naturals = naturals;
		this.type = Type.PUSOY;
	}
	
	@Override
	public String validateEvent(Event event) throws Exception {
		if(!(event instanceof EventOfc))
			return "Only EventOFC supported";
		
		EventOfc ev = (EventOfc) event;
		PlayerOfc plr = (PlayerOfc) getPlayer(event.who);
		if(plr == null && event.type != EventOfc.PAY_WINS)
			return "Player not found";
		
		if(event.type == EventOfc.TYPE_DEAL_CARDS)
		{
			if(!event.who.equals(this.heroName))
				return "DEAL cards can only hero";
			else if(Card.mask2Cards(ev.iVal).length != 13)
				return "Wrong cards number";
			else if(!plr.cardsToBeBoxed.isEmpty())
				return "Cards already dealt";
		} 
		else if(event.type == EventOfc.PUT_CARDS_TO_BOXES)
		{
			if (ev.card2box == null)
				return "card2box is null";
			else if(ev.card2box.size() != 13)
				return "Wrong card number";
			else if(event.who.equals(this.heroName) && plr.cardsToBeBoxed.isEmpty())
				return "Player hasn't a cards";
			else if(event.who.equals(this.heroName) && !ev.card2box.keySet().containsAll(plr.cardsToBeBoxed))
				return "Different cards dealt";
		}
		else if(event.type == EventOfc.PAY_WINS)
		{
			if(!allShown())
				return "First get cards for all players";
			
			else if(ev.resultOpponentScore == null)
				return "Profit not set";
			
			for(String name : ev.resultOpponentScore.keySet())
			{
				if(getPlayer(name) == null)
					return Misc.sf("Player %s not found", name);
			}
		}
		else 
		{
			return "Not supported event";
		}
		
		return null;
	}
	
	@Override
	public void procEvent(Event event, boolean validate) throws GameException, Exception {
		super.procEvent(event, validate);
		
		EventOfc ev = (EventOfc) event;
		PlayerOfc plr = (PlayerOfc) getPlayer(event.who);
		if(event.type == EventOfc.TYPE_DEAL_CARDS)
		{
			plr.cardsToBeBoxed.addAll(Arrays.asList(Card.mask2Cards(ev.iVal)));
			curMovePlayerInd = 0;
			mcount++;
		}
		else if(event.type == EventOfc.PUT_CARDS_TO_BOXES)
		{
			PlayerOfc.CardBox[] boxes = {plr.boxDead, plr.boxFront, plr.boxMiddle, plr.boxBack};
			for (Map.Entry<Card, Integer> entry : ev.card2box.entrySet()) {
				boxes[entry.getValue()].addCard(entry.getKey());
			};
			plr.cardsToBeBoxed.clear();
			mcount++;
		}
		else if(event.type == EventOfc.PAY_WINS)
		{
			for(String name : ev.resultOpponentScore.keySet())
			{
				PlayerOfc plr2 = (PlayerOfc) getPlayer(name);
				plr2.won += ev.resultOpponentScore.get(name).profit;
			}
			wins_payed = true;
		}
	}
	
	public boolean allShown()
	{
		for(int i = 0; i < this.allPlayers.length; i++)
		{
			if(!((PlayerOfc)allPlayers[i]).boxesIsFull())
				return false;
		}
		
		return true;
	}

	@Override
	public String toString() {
		String result = "---------------- Game #"+this.id + "; -----------------\n";
		for (Player p : players) {
			PlayerOfc player = (PlayerOfc) p;
			String strLabel = (player.isDealer(buttonName)?"b":"") + (player.isHero(heroName)?"h":"");
			if (!"".equals(strLabel.trim()))
				strLabel = String.format("(%s)", strLabel);
			result += (player==getCurMovePlayer()?"*":" ") + String.format("%-5s", strLabel) + player + "\n";
		}
		return result;
	}
	
	@Override
	public boolean isFinished() {
		return allShown() && wins_payed;
	}

	@Override
	public Game clone() {
		GamePusoy result = new GamePusoy(this.network, this.royalty, this.scoop, this.naturals);
		result.type = type;
		result.id = id;
		result.tableId = tableId;
		result.date = date;
		result.heroName = heroName;
		result.buttonName = buttonName;
		result.curMovePlayerInd = curMovePlayerInd;
		result.mcount = mcount;
		result.bb = bb;
				
		result.players = new Player[players.length];
		result.allPlayers = new Player[players.length];
		for (int i = 0; i < players.length; i++) {
			result.players[i] = new PlayerOfc((PlayerOfc) players[i]);
			result.allPlayers[i] = result.players[i];
		}

		return result;
	}


	@Override
	public PlayerOfc getPlayer(String name) {
		return (PlayerOfc) super.getPlayer(name);
	}

}
