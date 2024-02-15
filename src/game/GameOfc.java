package game;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import util.Misc;


public class GameOfc extends Game {
	public static final int ROUND_STARTER = 1;
	public static final int ROUND_LAST = 5;
	
	public static final int DEAL_COUNT_STARTER = 5;  
	public static final int DEAL_COUNT_NONSTARTER = 3;  
	//public static final int DEAL_COUNT_FANTASY = 14;  
	
	public static final int BOX_LEVEL_DEAD = 0; 
	public static final int BOX_LEVEL_FRONT = 1; 
	public static final int BOX_LEVEL_MIDDLE = 2; 
	public static final int BOX_LEVEL_BACK = 3;

	public enum GameMode { UNKNOWN,
		GAME_MODE_REGULAR, GAME_MODE_OFC_PROGRESSIVE, GAME_MODE_OFC_PROGRESSIVE_REFANTASY16, GAME_MODE_OFC_ULTIMATE,
		GAME_MODE_OFC_WILD_CARD_REGULAR, GAME_MODE_OFC_WILD_CARD_PROGRESSIVE, GAME_MODE_OFC_WILD_CARD_PROGRESSIVE_REFANTASY16, GAME_MODE_OFC_WILD_CARD_ULTIMATE,
		GAME_MODE_OFC_ULTIMATE_REFANTASY14, GAME_MODE_OFC_WILD_CARD_ULTIMATE_REFANTASY14, GAME_MODE_OFC_WILD_CARD_ULTIMATE_REFANTASY14_1JOKER}
	
	public static class Score {
		public int front;
		public int middle;
		public int back;
		public int allwin;
		public long profit;
		public long rake;
		public Score(int front, int middle, int back, int allwin, long profit) {
			super();
			this.front = front;
			this.middle = middle;
			this.back = back;
			this.allwin = allwin;
			this.profit = profit;
		}
		@Override
		public String toString() {
			return Misc.sf("(f: %d; m: %d; b: %d; a: %d; p: %d)", front, middle, back, allwin, profit);
		}
	}
	
	protected int round = ROUND_STARTER;
	private long checkDeck = 0;
	protected boolean finish = false;
	protected boolean allowEmptyDeadBox = false;
	protected boolean skipCheckFLCardCount = false;
	public boolean canParallelMoves = false;
	
	public GameMode gameMode = GameMode.UNKNOWN;
	public EventOfc tempAnswerEv;
	public boolean isReviewedPlayers = false;
	public boolean isRemoveHero = false;
	public long timeHeroFantasyDelt;
	public int bb;
	public boolean isSequential = false;
	
	public void setAllowEmptyDeadBox() {
		allowEmptyDeadBox = true;
	}
	public boolean isAllowEmptyDeadBox() {
		return allowEmptyDeadBox;
	}
	
	public void setSkipCheckFLCardCount() {
		skipCheckFLCardCount = true;
	}
	public boolean isSkipCheckFLCardCount() {
		return skipCheckFLCardCount;
	}
	
	public GameOfc(Nw network, int bb) {
//		super();
		this.network = network;
		this.type = Type.OFC;
		this.bb = bb;
		this.date = Misc.getTime();
	}

	private static final long serialVersionUID = 1L;

	@Override
	public String validateEvent(Event event) throws Exception {
		EventOfc ev;
		if ((event instanceof EventOfc))
			ev = (EventOfc) event;
		else
			return "The event is not compatible with the game.";
		
		if (ev.type == EventOfc.PUT_CARDS_TO_BOXES 
				|| ev.type == EventOfc.FANTASY_CARDS_TO_BOXES
				|| ev.type == EventOfc.SHOW_DEAD_CARDS) {
			for (Card card : ev.card2box.keySet()) {
				if ((checkDeck & card.getMask()) == card.getMask())
					return String.format("card %s has already been used in the game", card.toString());
				else
					checkDeck |= card.getMask();
			}
		}
		
		PlayerOfc who = getPlayer(ev.who);
		int cntCards = 0;
		switch (ev.type) {
		case (EventOfc.TYPE_DEAL_CARDS):
			cntCards = Card.mask2Cards(ev.iVal).length;
		
			if (!who.cardsToBeBoxed.isEmpty())
				return "cardToBeBoxes is't empty; who = " + who;
			if ((!who.playFantasy && ((round == ROUND_STARTER && cntCards != DEAL_COUNT_STARTER) || (round != ROUND_STARTER && cntCards != DEAL_COUNT_NONSTARTER)))
					|| (!skipCheckFLCardCount && who.playFantasy && cntCards != who.fantasyCardCount))
				return "wrong number of cards (TYPE_DEAL_CARDS) " + ev + "\nInvalid game state:\n" + this;
			
			break;
		case (EventOfc.PUT_CARDS_TO_BOXES):
			if (ev.card2box == null)
				return "card2box is null";
		
			cntCards = ev.card2box.size();
			if (!canParallelMoves && who != getCurMovePlayer())
				return String.format("current player (%s) is different from the target player (%s) %s", getCurMovePlayer().name, who.name, "\nInvalid game state:\n" + this);
			
			if (who.isHero(heroName)) {
				if ((!who.playFantasy && 
						((round == ROUND_STARTER && cntCards != DEAL_COUNT_STARTER) 
						|| (round != ROUND_STARTER && cntCards != DEAL_COUNT_NONSTARTER))))
					return "wrong number of cards (PUT_CARDS_TO_BOXES0) " + ev + "\nInvalid game state:\n" + this;
			} else {
				if ((!who.playFantasy && 
						((round == ROUND_STARTER && cntCards != DEAL_COUNT_STARTER) 
						|| (round != ROUND_STARTER && cntCards != DEAL_COUNT_NONSTARTER - 1))))
					return "wrong number of cards (PUT_CARDS_TO_BOXES1) " + ev + "\nInvalid game state:\n" + this;
			}
			
			if ((who.isHero(heroName)) && (!who.cardsToBeBoxed.containsAll(ev.card2box.keySet()) || !ev.card2box.keySet().containsAll(who.cardsToBeBoxed)))
				return String.format("different card set (who.cardsToBeBoxed <-> ev.card2box); who = %s; ev = %s", who, ev);
			
			break;
		case (EventOfc.FANTASY_CARDS_TO_BOXES):
			if (ev.card2box == null)
				return "card2box is null";
		
			cntCards = ev.card2box.size();
//			if (!isLastRound() && !Arrays.asList(this.getPlayers()).stream().allMatch(p -> p.playFantasy))
//				return "FANTASY_CARDS_TO_BOXES sould be in the last round" + "\nInvalid game state:\n" + this;
			
			if (who.playFantasy) {
				long fantasyDeadCard = ev.card2box.values().stream().filter((val) -> val == BOX_LEVEL_DEAD).count();
				if (who.isHero(heroName)) {
//					if (cntCards != who.fantasyCardCount)
//						return "wrong number of cards (FANTASY_CARDS_TO_BOXES1) " + ev;
//					if (fantasyDeadCard != 1)
//						return "only one card should be in BOX_LEVEL_DEAD (FANTASY_CARDS_TO_BOXES) " + ev;
				} else {
					if (cntCards != 13)
						return "wrong number of cards (FANTASY_CARDS_TO_BOXES2) " + ev + "\nInvalid game state:\n" + this;
					if (fantasyDeadCard != 0)
						return "BOX_LEVEL_DEAD should be empty (FANTASY_CARDS_TO_BOXES) " + ev + "\nInvalid game state:\n" + this;
				}

				
			} else {
				return "only fantasy player should be FANTASY_CARDS_TO_BOXES; who = " + who.name + "\nInvalid game state:\n" + this;
			}
			
			break;
		case (EventOfc.SHOW_DEAD_CARDS):
			if (ev.card2box == null)
				return "card2box is null";
		
			cntCards = ev.card2box.size();
			
			if (!who.playFantasy && !allowEmptyDeadBox && (cntCards != 4 || ev.card2box.values().stream().filter((val) -> val == BOX_LEVEL_DEAD).count() != 4))
				return "there should be only 4 cards and only in BOX_LEVEL_DEAD " + ev;
			
			break;
		case (EventOfc.PAY_WINS):
			
			break;
		case (EventOfc.REMOVE_PLAYER):

			break;
		default:
			return "Unknown event!";
		}
		return null;
	}

	@Override
	public void procEvent(Event event, boolean validate) throws GameException, Exception {
		EventOfc ev;
		if ((event instanceof EventOfc))
			ev = (EventOfc) event;
		else
			throw new IllegalArgumentException("The event is not compatible with the game.");
		super.procEvent(event, validate);
		
		PlayerOfc who = getPlayer(ev.who);
		switch (ev.type) {
		case EventOfc.TYPE_DEAL_CARDS:
			procDealCards(who, Arrays.asList(Card.mask2Cards(ev.iVal)));
			if (who.name.equals(heroName) && who.playFantasy)
				timeHeroFantasyDelt = ev.createTime;
			break;
		case EventOfc.PUT_CARDS_TO_BOXES:
			procCards2Boxes(who, ev.card2box);
			break;
		case EventOfc.FANTASY_CARDS_TO_BOXES:
			procShowdown(who, ev.card2box);
			if (isLastRound() && Arrays.asList(getPlayers()).stream().filter(pl -> !pl.playFantasy).allMatch(pl -> pl.boxesIsFull()))
				nextCurMovePlayer(true);
			break;
		case EventOfc.SHOW_DEAD_CARDS:
			procShowdown(who, ev.card2box);
			break;
		case EventOfc.PAY_WINS:
			procPayWins(who, ev.iVal, ev.resultOpponentScore);
			break;
		case EventOfc.REMOVE_PLAYER:
			procRemovePlayer(who);
			break;
		default:
			throw new GameException("Unknown event!");
		}
		mcount++;
	}

	private void procRemovePlayer(PlayerOfc who) throws GameException {
		int ind = getPlayerInd(who.name);
		players = Game.removeElement(players, ind);
		ind = Game.getElementInd(allPlayers, who);
		allPlayers = Game.removeElement(allPlayers, ind);
		if (who.name.equals(heroName)) {
			heroName = null;
			isRemoveHero = true;
		}
		initButtonName(buttonName);
		isReviewedPlayers = true;
	}

	@Override
	public boolean isFinished() {
		return finish;
	}

	@Override
	public GameOfc clone() {
		GameOfc result = new GameOfc(network, bb);
		result.type = type;
		result.id = id;
		result.tableId = tableId;
		result.date = date;
		result.heroName = heroName;
		result.buttonName = buttonName;
		result.curMovePlayerInd = curMovePlayerInd;
		result.mcount = mcount;
		
		result.gameMode = gameMode;
		result.round = round;
		result.checkDeck = checkDeck;
		result.isReviewedPlayers = isReviewedPlayers;
		result.isRemoveHero = isRemoveHero;
		result.finish = finish;
		result.timeHeroFantasyDelt = timeHeroFantasyDelt;
		result.allowEmptyDeadBox = allowEmptyDeadBox;
		result.skipCheckFLCardCount = skipCheckFLCardCount;
		result.canParallelMoves = canParallelMoves;
//		result.bb = bb;
		
		result.players = new Player[players.length];
		result.allPlayers = new Player[players.length];
		for (int i = 0; i < players.length; i++) {
			result.players[i] = new PlayerOfc((PlayerOfc) players[i]);
			result.allPlayers[i] = result.players[i];
		}

		return result;
	}

	@Override
	public PlayerOfc getCurMovePlayer() {
		return (PlayerOfc) super.getCurMovePlayer();
	}
	
	@Override
	public PlayerOfc getPlayer(String name) {
		return (PlayerOfc) super.getPlayer(name);
	}
	
	public PlayerOfc getPlayer(int ind) {
		return (PlayerOfc) players[ind];
	}
	
	public int getRound() {
		return round;
	}
	
	public boolean isFirstRound() {
		return round == ROUND_STARTER;
	}
	
	public boolean isLastRound() {
		return round >= ROUND_LAST;
	}

	public void setRound(int round) {
		this.round = round;
	}
	
	protected void nextCurMovePlayer(boolean fantasyMode) {
		int curInd = curMovePlayerInd;
		if (fantasyMode) {
			do {
				curMovePlayerInd = ++curMovePlayerInd % players.length;
			} while (!getCurMovePlayer().playFantasy && curInd != curMovePlayerInd);
		} else {
			do {
				curMovePlayerInd = ++curMovePlayerInd % players.length;
			} while (getCurMovePlayer().playFantasy && curInd != curMovePlayerInd);
		}
	}
	
	protected int nextRound() throws GameException {
		if (isLastRound())
			throw new GameException("Illegal OFC state");
		return ++round;
	}
	
	protected void procDealCards(PlayerOfc player, List<Card> newCards) {
		player.cardsToBeBoxed.addAll(newCards);
	}
	
	public void initButtonName(String btnName) throws GameException {
		int indButton = getPlayerInd(btnName);
		if (indButton == -1)
			throw new GameException("Failed to init buttonName");
		buttonName = btnName;
		curMovePlayerInd = indButton;
		nextCurMovePlayer(false);
		// all play fantasy
		if (heroName != null && Arrays.asList(getPlayers()).stream().allMatch(p -> p.playFantasy))
			curMovePlayerInd = getPlayerInd(heroName);
	}
	
	protected void procCards2Boxes(PlayerOfc player, Map<Card, Integer> card2box) throws GameException {
		PlayerOfc.CardBox[] boxes = {player.boxDead, player.boxFront, player.boxMiddle, player.boxBack};
		//card2box.entrySet().stream().forEach((entry) -> boxes[entry.getValue()].addCard(entry.getKey()));
		for (Map.Entry<Card, Integer> entry : card2box.entrySet()) {
			boxes[entry.getValue()].addCard(entry.getKey());
		};
		player.cardsToBeBoxed.clear();
		
		//condition for the transition of the round
		boolean needTransitRound = false;
		int ind = Game.getElementInd(players, player);
		do {
			needTransitRound = getPlayer(ind).isDealer(buttonName);
			ind = ++ind % players.length;
		} while (getPlayer(ind).playFantasy && !needTransitRound);
		
		if (needTransitRound) {
			if (isLastRound()) { 
				if (heroName != null && getPlayer(heroName).playFantasy) {
					//during Fantasy mode, the hero is the first to play
					curMovePlayerInd = getPlayerInd(heroName);
				}
				else {
					nextCurMovePlayer(true);
				}
			} else {
				nextCurMovePlayer(false);
				nextRound();
			}
		} else
			nextCurMovePlayer(false);
	}

	private void setFinish() {
		finish = (allowEmptyDeadBox || Arrays.asList(this.getPlayers()).stream().allMatch(pl -> !pl.boxDead.toList().isEmpty())) // all dead cards are known
				&& Arrays.asList(this.getPlayers()).stream().allMatch(pl -> !pl.resultOppScore.isEmpty()); // all result are known
	}

	/**
	 * rake calculation. rake must calc only for negative value
	 */
	private int calcRake(long profit) {
		if (profit > 0) // only for negative value
			return 0;
		switch (network) {
			case Ppp:
			case FishPoker:
				return (int) (0.03 * -profit);
			default:
				return (int) (0.03 * -profit);
		}
	}

	private void procPayWins(PlayerOfc player, long resultChips, Map<String, Score> resultOpponentScore) {
		player.stack = (int) resultChips;
		player.resultOppScore.putAll(resultOpponentScore);
		// calc rake
		Score plScore = player.getSumScore();
		if (plScore.rake > 0)
			player.rake = (int) plScore.rake;
		else
			player.rake = calcRake(plScore.profit);
		// determine the finish
		setFinish();
	}

	private void procShowdown(PlayerOfc player, Map<Card, Integer> card2box) throws GameException {
		PlayerOfc.CardBox[] boxes = {player.boxDead, player.boxFront, player.boxMiddle, player.boxBack};
		for (Map.Entry<Card, Integer> entry : card2box.entrySet()) {boxes[entry.getValue()].addCard(entry.getKey());};
		player.cardsToBeBoxed.clear();
		// determine the finish
		setFinish();
	}
	
	// cast players to PlayerOFC[]
	public PlayerOfc[] getPlayers() {
		PlayerOfc[] ret = new PlayerOfc[this.players.length];
		for (int i = 0; i < this.players.length; i++) ret[i] = (PlayerOfc) this.players[i];
		return ret;
	}

	public PlayerOfc getUtgPlayer() {
		int indBtn = getPlayerIndAll(buttonName);
		if (indBtn == -1) return null;
		int indUtg = ++indBtn % allPlayers.length;
		return (PlayerOfc) this.allPlayers[indUtg];
	}
	
	@Override
	public String toString() {
		String result = "---------------- Game #"+this.id + "; round " + this.round + "; mode: " + this.gameMode.name() + (isFinished()?"; FINISH":"") + " -----------------\n";
		for (Player p : players) {
			PlayerOfc player = (PlayerOfc) p;
			String strLabel = (player.isDealer(buttonName)?"b":"") + (player.isHero(heroName)?"h":"") + (player.playFantasy?"f":"");
			if (!"".equals(strLabel.trim()))
				strLabel = String.format("(%s)", strLabel);
			result += (player==getCurMovePlayer()?"*":" ") + String.format("%-5s", strLabel) + player + "\n";
		}
		return result;
	}
	
/*	
	public static void testSuccessGame1() {
		GameOfc game = new GameOfc();
		try {
			// init game
			game.id = "2332";
			String hero = "Bruce Lee";
			game.addPlayer(new PlayerOfc("Rambo", 1001));
			game.addPlayer(new PlayerOfc("Van Dam", 1002));
			game.addPlayer(new PlayerOfc(hero, 1003));
			game.initButtonName("Rambo");
			game.heroName = hero;
			// create events
			List<EventOfc> eventList = new ArrayList<EventOfc>();
			eventList.add(new EventOfc(EventOfc.TYPE_DEAL_CARDS, hero, Card.cards2Mask(Card.str2Cards("Ah9s2sJc6c"))));
			eventList.add(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, "Van Dam", Card.cards2Mask(Card.str2Cards("6d")),
																		 Card.cards2Mask(Card.str2Cards("4s4d")),
																		 Card.cards2Mask(Card.str2Cards("Tc2c")), 0));
			eventList.add(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, hero, Card.cards2Mask(Card.str2Cards("2s")),
																	Card.cards2Mask(Card.str2Cards("6c9s")),
																	Card.cards2Mask(Card.str2Cards("AhJc")), 0));
			eventList.add(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, "Rambo", Card.cards2Mask(Card.str2Cards("8h")),
																	   Card.cards2Mask(Card.str2Cards("JsTs")),
																	   Card.cards2Mask(Card.str2Cards("Qc5c")), 0));
			eventList.add(new EventOfc(EventOfc.TYPE_DEAL_CARDS, hero, Card.cards2Mask(Card.str2Cards("Td3dAs"))));
			eventList.add(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, "Van Dam", 0, Card.cards2Mask(Card.str2Cards("Kd9d")), 0, 0));
			eventList.add(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, hero, 0, Card.cards2Mask(Card.str2Cards("Td")), Card.cards2Mask(Card.str2Cards("As")), Card.cards2Mask(Card.str2Cards("3d"))));
			eventList.add(new EventOfc(EventOfc.TYPE_DEAL_CARDS, hero, Card.cards2Mask(Card.str2Cards("9hQs8d"))));
			eventList.add(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, "Rambo", 0, Card.cards2Mask(Card.str2Cards("Kh")), Card.cards2Mask(Card.str2Cards("5d")), 0));
			eventList.add(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, "Van Dam", 0, 0, Card.cards2Mask(Card.str2Cards("AdTh")), 0));
			eventList.add(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, hero, 0, Card.cards2Mask(Card.str2Cards("9h")), Card.cards2Mask(Card.str2Cards("Qs")), Card.cards2Mask(Card.str2Cards("8d"))));
			eventList.add(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, "Rambo", Card.cards2Mask(Card.str2Cards("6h")), Card.cards2Mask(Card.str2Cards("Qd")), 0, 0));
			eventList.add(new EventOfc(EventOfc.TYPE_DEAL_CARDS, hero, Card.cards2Mask(Card.str2Cards("Ac4c3s"))));
			eventList.add(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, "Van Dam", Card.cards2Mask(Card.str2Cards("8s8c")), 0, 0, 0));
			eventList.add(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, hero, Card.cards2Mask(Card.str2Cards("4c")), 0, Card.cards2Mask(Card.str2Cards("Ac")), Card.cards2Mask(Card.str2Cards("3s"))));
			eventList.add(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, "Rambo", 0, Card.cards2Mask(Card.str2Cards("Qh")), Card.cards2Mask(Card.str2Cards("5h")), 0));
			eventList.add(new EventOfc(EventOfc.TYPE_DEAL_CARDS, hero, Card.cards2Mask(Card.str2Cards("3h4h5s"))));
			eventList.add(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, "Van Dam", 0, Card.cards2Mask(Card.str2Cards("2h")), Card.cards2Mask(Card.str2Cards("2d")), 0));
			eventList.add(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, hero, Card.cards2Mask(Card.str2Cards("5s")), Card.cards2Mask(Card.str2Cards("4h")), 0, Card.cards2Mask(Card.str2Cards("3h"))));
			eventList.add(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, "Rambo", Card.cards2Mask(Card.str2Cards("6s")), 0, Card.cards2Mask(Card.str2Cards("7s")), 0));
			
//			PtcOfc ptc = new PtcOfc();
			boolean sendedShowdown = false;
			for (int i = 0; i < eventList.size(); i++) {
				EventOfc ev = eventList.get(i);
				if (ev.type == EventOfc.PUT_CARDS_TO_BOXES && ev.who.equals(hero)) {
					System.out.println("request AI answer...");
					//ptc.sendButtons(null, game);
					//ev = game.tempAnswerEv;
				}
				game.procEvent(ev);
				System.out.println(ev);
				System.out.println(game);
				if (!sendedShowdown && ev.type == EventOfc.SHOW_DEAD_CARDS) {
					System.out.println("sending SHOWDOWN to AI server ...");
					//Event[] evs = {ev};
					//ptc.sendEvents(null, game, evs);
					sendedShowdown = true;
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void testSuccessFantasyGame1() {
		GameOfc game = new GameOfc();
		try {
			// init game
			game.id = "4334";
			String hero = "Bruce Lee";
			game.addPlayer(new PlayerOfc("Rambo", 1001));
			game.addPlayer(new PlayerOfc("Van Dam", 1002, true));
			game.addPlayer(new PlayerOfc(hero, 1003));
			game.initButtonName(hero);
			game.heroName = hero;
			// create events
			List<EventOfc> eventList = new ArrayList<EventOfc>();
			eventList.add(new EventOfc(EventOfc.TYPE_DEAL_CARDS, hero, Card.cards2Mask(Card.str2Cards("9h3c8d5c3h"))));
			eventList.add(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, "Rambo", 0, Card.cards2Mask(Card.str2Cards("As8s")), Card.cards2Mask(Card.str2Cards("Qh6h2h")), 0));
			eventList.add(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, hero, Card.cards2Mask(Card.str2Cards("3c3h")), Card.cards2Mask(Card.str2Cards("5c")), Card.cards2Mask(Card.str2Cards("9h8d")), 0));
			eventList.add(new EventOfc(EventOfc.TYPE_DEAL_CARDS, hero, Card.cards2Mask(Card.str2Cards("3sTcJd"))));
			eventList.add(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, "Rambo", 0, Card.cards2Mask(Card.str2Cards("Qs9s")), 0, 0));
			eventList.add(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, hero, 0, Card.cards2Mask(Card.str2Cards("Tc")), Card.cards2Mask(Card.str2Cards("Jd")), Card.cards2Mask(Card.str2Cards("3s"))));
			eventList.add(new EventOfc(EventOfc.TYPE_DEAL_CARDS, hero, Card.cards2Mask(Card.str2Cards("Ac7h9c"))));
			eventList.add(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, "Rambo", 0, Card.cards2Mask(Card.str2Cards("Js")), Card.cards2Mask(Card.str2Cards("Kh")), 0));
			eventList.add(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, hero, 0, 0, Card.cards2Mask(Card.str2Cards("Ac9c")), Card.cards2Mask(Card.str2Cards("7h"))));
			eventList.add(new EventOfc(EventOfc.TYPE_DEAL_CARDS, hero, Card.cards2Mask(Card.str2Cards("Kc4d7c"))));
			eventList.add(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, "Rambo", Card.cards2Mask(Card.str2Cards("5h")), 0, Card.cards2Mask(Card.str2Cards("Ah")), 0));
			eventList.add(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, hero, Card.cards2Mask(Card.str2Cards("Kc")), Card.cards2Mask(Card.str2Cards("7c")), 0, Card.cards2Mask(Card.str2Cards("4d"))));
			eventList.add(new EventOfc(EventOfc.TYPE_DEAL_CARDS, hero, Card.cards2Mask(Card.str2Cards("Ad5d6c"))));
			eventList.add(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, "Rambo", Card.cards2Mask(Card.str2Cards("Jc8c")), 0, 0, 0));
			eventList.add(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, hero, 0, Card.cards2Mask(Card.str2Cards("5dAd")), 0, Card.cards2Mask(Card.str2Cards("6c"))));
			eventList.add(new EventOfc(EventOfc.FANTASY_CARDS_TO_BOXES, "Van Dam", Card.cards2Mask(Card.str2Cards("8h4h3d")), Card.cards2Mask(Card.str2Cards("Ts7s6s5s4s")), Card.cards2Mask(Card.str2Cards("KdQdTd9d2d")), 0));
			eventList.add(new EventOfc(EventOfc.SHOW_DEAD_CARDS, "Van Dam", 0, 0, 0, Card.cards2Mask(Card.str2Cards("2c"))));
			eventList.add(new EventOfc(EventOfc.SHOW_DEAD_CARDS, "Rambo", 0, 0, 0, Card.cards2Mask(Card.str2Cards("2sQc4c6d"))));
			
			for (EventOfc ev : eventList) {
				game.procEvent(ev);
				System.out.println(ev);
				System.out.println(game);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void testSuccessFantasyGame2() {
		GameOfc game = new GameOfc();
		try {
			//PtcOfc ptc = new PtcOfc();
			// init game
			game.id = "5555";
			game.gameMode = GameMode.GAME_MODE_OFC_PROGRESSIVE;
			String hero = "Bruce Lee";
			game.addPlayer(new PlayerOfc("Rambo", 1001));
			game.addPlayer(new PlayerOfc("Van Dam", 1002, true));
			game.getPlayer("Van Dam").fantasyCardCount = 14;
			game.addPlayer(new PlayerOfc(hero, 1003));
			game.initButtonName(hero);
			game.heroName = hero;
			// create events
			List<EventOfc> eventList = new ArrayList<EventOfc>();
			eventList.add(new EventOfc(EventOfc.TYPE_DEAL_CARDS, hero, Card.cards2Mask(Card.str2Cards("9h3c8d5c3h"))));
			eventList.add(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, "Rambo", 0, Card.cards2Mask(Card.str2Cards("As8s")), Card.cards2Mask(Card.str2Cards("Qh6h2h")), 0));
			eventList.add(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, hero, Card.cards2Mask(Card.str2Cards("3c3h")), Card.cards2Mask(Card.str2Cards("5c")), Card.cards2Mask(Card.str2Cards("9h8d")), 0));
			eventList.add(new EventOfc(EventOfc.TYPE_DEAL_CARDS, hero, Card.cards2Mask(Card.str2Cards("3sTcJd"))));
			eventList.add(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, "Rambo", 0, Card.cards2Mask(Card.str2Cards("Qs9s")), 0, 0));
			eventList.add(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, hero, 0, Card.cards2Mask(Card.str2Cards("Tc")), Card.cards2Mask(Card.str2Cards("Jd")), Card.cards2Mask(Card.str2Cards("3s"))));
			eventList.add(new EventOfc(EventOfc.TYPE_DEAL_CARDS, hero, Card.cards2Mask(Card.str2Cards("Ac7h9c"))));
			eventList.add(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, "Rambo", 0, Card.cards2Mask(Card.str2Cards("Js")), Card.cards2Mask(Card.str2Cards("Kh")), 0));
			eventList.add(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, hero, 0, 0, Card.cards2Mask(Card.str2Cards("Ac9c")), Card.cards2Mask(Card.str2Cards("7h"))));
			eventList.add(new EventOfc(EventOfc.TYPE_DEAL_CARDS, hero, Card.cards2Mask(Card.str2Cards("Kc4d7c"))));
			eventList.add(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, "Rambo", Card.cards2Mask(Card.str2Cards("5h")), 0, Card.cards2Mask(Card.str2Cards("Ah")), 0));
			eventList.add(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, hero, Card.cards2Mask(Card.str2Cards("Kc")), Card.cards2Mask(Card.str2Cards("7c")), 0, Card.cards2Mask(Card.str2Cards("4d"))));
			eventList.add(new EventOfc(EventOfc.TYPE_DEAL_CARDS, hero, Card.cards2Mask(Card.str2Cards("Ad5d6c"))));
			eventList.add(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, "Rambo", Card.cards2Mask(Card.str2Cards("Jc8c")), 0, 0, 0));
			eventList.add(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, hero, 0, Card.cards2Mask(Card.str2Cards("5dAd")), 0, Card.cards2Mask(Card.str2Cards("6c"))));
			eventList.add(new EventOfc(EventOfc.FANTASY_CARDS_TO_BOXES, "Van Dam", Card.cards2Mask(Card.str2Cards("8h4h3d")), Card.cards2Mask(Card.str2Cards("Ts7s6s5s4s")), Card.cards2Mask(Card.str2Cards("KdQdTd9d2d")), 0));
			eventList.add(new EventOfc(EventOfc.SHOW_DEAD_CARDS, "Van Dam", 0, 0, 0, Card.cards2Mask(Card.str2Cards("2c"))));
			eventList.add(new EventOfc(EventOfc.SHOW_DEAD_CARDS, "Rambo", 0, 0, 0, Card.cards2Mask(Card.str2Cards("2sQc4c6d"))));
			
			boolean sendedShowdown = false;
			for (int i = 0; i < eventList.size(); i++) {
				EventOfc ev = eventList.get(i);
				if (ev.type == EventOfc.PUT_CARDS_TO_BOXES && ev.who.equals(hero)) {
					System.out.println("request AI answer...");
					//ptc.sendButtons(null, game);
					//ev = game.tempAnswerEv;
				}
				game.procEvent(ev);
				System.out.println(ev);
				System.out.println(game);
				if (!sendedShowdown && ev.type == EventOfc.SHOW_DEAD_CARDS) {
					System.out.println("sending SHOWDOWN to AI server ...");
					//Event[] evs = {ev};
					//ptc.sendEvents(null, game, evs);
					sendedShowdown = true;
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void testButtonOrder() {
		GameOfc game = new GameOfc();
		try {
			// init game
			game.id = "2332";
			String hero = "pid2717127";
			game.addPlayer(new PlayerOfc("pid939064", 1001));
			game.addPlayer(new PlayerOfc(hero, 1003));
			game.addPlayer(new PlayerOfc("pid2658962", 1002));
			game.initButtonName("pid2658962");
			game.heroName = hero;
			// create events
			List<EventOfc> eventList = new ArrayList<EventOfc>();
			eventList.add(new EventOfc(EventOfc.TYPE_DEAL_CARDS, hero, Card.cards2Mask(Card.str2Cards("Jh3c3d3h2s"))));
			eventList.add(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, "pid939064", 0, Card.cards2Mask(Card.str2Cards("2h7d")), Card.cards2Mask(Card.str2Cards("Js9c9h")), 0));
			eventList.add(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, hero, 0, 0, 0, 0));
			
			PtcOfc ptc = new PtcOfc();
			boolean sendedShowdown = false;
			for (int i = 0; i < eventList.size(); i++) {
				EventOfc ev = eventList.get(i);
				if (ev.type == EventOfc.PUT_CARDS_TO_BOXES && ev.who.equals(hero)) {
					System.out.println("request AI answer...");
					ptc.sendButtons(null, game);
					ev = game.tempAnswerEv;
				}
				game.procEvent(ev);
				System.out.println(ev);
				System.out.println(game);
				if (!sendedShowdown && ev.type == EventOfc.SHOW_DEAD_CARDS) {
					System.out.println("sending SHOWDOWN to AI server ...");
					Event[] evs = {ev};
					ptc.sendEvents(null, game, evs);
					sendedShowdown = true;
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	public static void testButtonOrderTwoPlayer() {
		GameOfc game = new GameOfc();
		try {
			// init game
			game.id = "2332";
			String hero = "pid2717127";
			game.addPlayer(new PlayerOfc("pid471377", 1001));
			game.addPlayer(new PlayerOfc(hero, 1003));
			game.initButtonName("pid471377");
			game.heroName = hero;
			// create events
			List<EventOfc> eventList = new ArrayList<EventOfc>();
			eventList.add(new EventOfc(EventOfc.TYPE_DEAL_CARDS, hero, Card.cards2Mask(Card.str2Cards("7d7h6h2c2h"))));
			eventList.add(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, hero, 0, 0, 0, 0));
			
			PtcOfc ptc = new PtcOfc();
			boolean sendedShowdown = false;
			for (int i = 0; i < eventList.size(); i++) {
				EventOfc ev = eventList.get(i);
				if (ev.type == EventOfc.PUT_CARDS_TO_BOXES && ev.who.equals(hero)) {
					System.out.println("request AI answer...");
					ptc.sendButtons(null, game);
					ev = game.tempAnswerEv;
				}
				game.procEvent(ev);
				System.out.println(ev);
				System.out.println(game);
				if (!sendedShowdown && ev.type == EventOfc.SHOW_DEAD_CARDS) {
					System.out.println("sending SHOWDOWN to AI server ...");
					Event[] evs = {ev};
					ptc.sendEvents(null, game, evs);
					sendedShowdown = true;
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
*/	
	public static void main (String[] args) {
//		testSuccessGame1();
//		testSuccessFantasyGame1();
//		testSuccessFantasyGame2();
		//testButtonOrder();
		//testButtonOrderTwoPlayer();
	}
}
