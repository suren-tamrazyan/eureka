package game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import util.Misc;

public class GameRummy extends Game {
    public int bb = 0;
    public String matchId = "";
    public Card open;
    public Card wild;
    public boolean declared = false;
    public boolean scored = false;
    public boolean finished = false;
    public HashMap<Integer, ArrayList<Card>> cardsToGroup = null;
    public String plrDeclared;
    public final List<Card> discardClosePile = new ArrayList<>();

    public GameRummy(Nw network) {
//		super();
        this.network = network;
        this.type = Type.RUMMY;
        this.date = Misc.getTime();
    }

    @Override
    public void procEvent(Event event) throws GameException, Exception {
        procEvent(event, true);
    }

    public static HashMap<Integer, ArrayList<Card>> copyCardsGroup(HashMap<Integer, ArrayList<Card>> cardsGroup) {
        HashMap<Integer, ArrayList<Card>> cardsGroup_ = new HashMap<>();
        for (Integer group : cardsGroup.keySet()) {
            ArrayList<Card> gr = cardsGroup.get(group);
            ArrayList<Card> copy = new ArrayList<>();
            for (Card c : gr) {
                copy.add(c);
            }
            cardsGroup_.put(group, copy);
        }
        return cardsGroup_;
    }

    @Override
    public void procEvent(Event ev, boolean validate) throws GameException, Exception {
        EventRummy event = (EventRummy) ev;
        if (validate) {
            String v = validateEvent(ev);
            if (v != null) {
                throw new GameException(v);
            }
        }
        PlayerRummy who = getPlayer(event.who);
        switch (event.type) {
            case EventRummy.END_GAME:
                finished = true;
                break;
            case EventRummy.DEAL_CARDS:
                who.cardsGroup = copyCardsGroup(event.cardsGroup);
                break;
            case EventRummy.PLAYER_TURN_TIMEOUT:
                nextCurMovePlayer();
                break;
            case EventRummy.PICK_CARD_OPEN:
                who.takenOpenCards.add(Card.mask2Cards(event.iVal)[0]);
                open = null;
            case EventRummy.PICK_CARD_CLOSED:
                //boolean FromClosedDeck = event.FromClosedDeck;

                if (!who.cardsGroup.containsKey(0))
                    who.cardsGroup.put(0, new ArrayList<>());
                if (event.iVal != 0)
                    who.cardsGroup.get(0).add(Card.mask2Cards(event.iVal)[0]);
                who.gotCard = true;

                if (open != null && !who.ignoredOpenCards.contains(open))
                    who.ignoredOpenCards.add(open);

                break;
            case EventRummy.DISCARD_CARD_WITH_DECLARE:
                declared = true;
                plrDeclared = event.who;
            case EventRummy.DISCARD_CARD:
                Card c = Card.mask2Cards(event.iVal)[0];

                l0:
                for (Integer group : who.cardsGroup.keySet()) {
                    ArrayList<Card> cards = who.cardsGroup.get(group);
                    for (int i = 0; i < cards.size(); i++) {
                        if (cards.get(i) == c) {
                            cards.remove(i);
                            break l0;
                        }
                    }
                }

                who.gotCard = false;
                who.discardedCards.add(c);
                if (open != null)
                    discardClosePile.add(open);
                open = c;
                if (!who.name.equals(heroName) || event.type != EventRummy.DISCARD_CARD_WITH_DECLARE)
                    nextCurMovePlayer();

                break;

            case EventRummy.DROP:
                who.isDropped = true;
                nextCurMovePlayer();
                break;

            case EventRummy.SORT_CARD:
                if (event.cardsGroup.size() > 0)
                    who.cardsGroup = copyCardsGroup(event.cardsGroup);
                mcount--;
                break;

            case EventRummy.DECLARE:
                declared = true;
                plrDeclared = event.who;
                if (!who.name.equals(heroName))
                    nextCurMovePlayer();

                break;
            case EventRummy.DEALER_SET:
                this.buttonName = who.name;
                curMovePlayerInd = 0;
                setButtonPlayer();
                break;
            case EventRummy.JOKER_SET:
                wild = Card.mask2Cards(event.iVal)[0];
                break;
            case EventRummy.OPEN_CARD_SET:
                if (event.iVal == 0)
                    open = null;
                else
                    open = Card.mask2Cards(event.iVal)[0];
                break;
            case EventRummy.SCORES:
                declared = true;
                who.won = (int) ev.iVal;
                who.score = ((EventRummy) ev).scores;
                if (event.cardsGroup.size() > 0)
                    who.cardsGroup = copyCardsGroup(event.cardsGroup);

                boolean allScored = true;
                for (Player p : allPlayers) {
                    if (((PlayerRummy) p).score == PlayerRummy.UNDEF_SCORE) {
                        allScored = false;
                    }
                }
                scored = allScored;

                break;
            case EventRummy.TYPE_PROFIT_VALUE:
                who.won = (int) ev.iVal;
                break;
            case EventRummy.TYPE_RAKE_VALUE:
                who.rake = (int) ev.iVal;
                break;
            case EventRummy.HIDDEN_CARD:
                break;
            default:
                throw new GameException("Unknown event!");
        }
        mcount++;
    }

    private void nextCurMovePlayer() {
        int curInd = curMovePlayerInd;
        do {
            curMovePlayerInd = ++curMovePlayerInd % players.length;
        } while (getCurMovePlayer().isDropped && curInd != curMovePlayerInd);
    }

    @Override
    public PlayerRummy getCurMovePlayer() {
        return (PlayerRummy) super.getCurMovePlayer();
    }

    public PlayerRummy getPlayer(int ind) {
        return (PlayerRummy) players[ind];
    }

    @Override
    public PlayerRummy getPlayer(String name) {
        return (PlayerRummy) super.getPlayer(name);
    }

    @Override
    public String validateEvent(Event ev) throws Exception {
        EventRummy event = (EventRummy) ev;
        PlayerRummy who = getPlayer(event.who);

        switch (event.type) {
            case EventRummy.DEAL_CARDS:
                if (buttonName.isEmpty())
                    return "Dealer isn't set";
                if (heroName.isEmpty())
                    return "Hero isn't set";
                if (event.cardsGroup == null)
                    return "Cards in hand is null";
                if (who.cardsGroup != null && who.cardsGroup.size() != 0)
                    return "Player already have a cards";
                break;

            case EventRummy.PLAYER_TURN_TIMEOUT:
                if (who != getCurMovePlayer())
                    return "wrong turn";
                break;

            case EventRummy.PICK_CARD_OPEN:
                if (event.iVal == 0)
                    return "Empty card";

            case EventRummy.PICK_CARD_CLOSED:
                if (who != getCurMovePlayer())
                    return "wrong turn";
                if (who.gotCard)
                    return "Card got but dont discarded";

                if (this.heroName.equals(who.name)) {
                    int cCount1 = 0;
                    for (Integer group : who.cardsGroup.keySet()) {
                        ArrayList<Card> cards = who.cardsGroup.get(group);
                        cCount1 += cards.size();
                    }

                    if (cCount1 != 13)
                        return "Card count wrong on pick";
                }
                break;

            case EventRummy.DISCARD_CARD_WITH_DECLARE:
            case EventRummy.DISCARD_CARD:
                if (who != getCurMovePlayer())
                    return "wrong turn";

                if (!who.gotCard)
                    return "Card discarded but no pickup";

                if (!this.heroName.equals(who.name))
                    return null;

                Card c = Card.mask2Cards(event.iVal)[0];
                boolean haveCard = false;
                int cCount = 0;

                for (Integer group : who.cardsGroup.keySet()) {
                    ArrayList<Card> cards = who.cardsGroup.get(group);
                    cCount += cards.size();

                    for (int i = 0; i < cards.size(); i++) {
                        if (cards.get(i) == c) {
                            haveCard = true;
                        }
                    }
                }

                if (!haveCard)
                    return "Player don't have this card";

                if (cCount != 14)
                    return "Card count wrong on discard";

                break;

            case EventRummy.END_GAME:
                break;
            case EventRummy.SORT_CARD:
                if (!who.name.equals(heroName))
                    break;

                break;

            case EventRummy.DROP:
                if (who.isDropped)
                    return "Player already dropped";
                break;
            case EventRummy.DEALER_SET:
                if (who == null)
                    return "Need Dealer";
                break;
            case EventRummy.JOKER_SET:
                if (event.iVal == 0)
                    return "Card must be not zero";
                break;
            case EventRummy.OPEN_CARD_SET:
                //if(event.iVal == 0)
                //	return "Card must be not zero";
                break;
            case EventRummy.DECLARE:
                if (declared)
                    return "Already declared";
                if (event.who.isEmpty())
                    return "Need who call declcare";
                break;

            case EventRummy.SCORES:
                for (Player p : players) {
                    if (!((PlayerRummy) p).isDropped && ((PlayerRummy) p).gotCard) {
                        return "Not all players discard a card: " + p;
                    }
                }
                break;
            case EventRummy.TYPE_PROFIT_VALUE:
                break;
            case EventRummy.TYPE_RAKE_VALUE:
                break;
            case EventRummy.HIDDEN_CARD:
                break;
            default:
                throw new GameException("Unknown event!");
        }

        return null;
    }

    @Override
    public boolean isFinished() {
        return (declared && scored) || finished;
    }

    @Override
    public GameRummy clone() {
        GameRummy result = new GameRummy(network);
        result.type = type;
        result.id = id;
        result.tableId = tableId;
        result.matchId = matchId;
        result.date = date;
        result.heroName = heroName;
        result.buttonName = buttonName;
        result.curMovePlayerInd = curMovePlayerInd;
        result.mcount = mcount;

        result.open = open;
        result.wild = wild;
        result.declared = declared;
        result.scored = scored;

        result.players = new Player[players.length];
        result.allPlayers = new Player[players.length];
        for (int i = 0; i < players.length; i++) {
            result.players[i] = new PlayerRummy((PlayerRummy) players[i]);
            result.allPlayers[i] = result.players[i];
        }

        return result;
    }

    @Override
    public String toString() {
        String result = Misc.sf("---------------- Game #%s; Wild: %s; Open: %s; Finished: %s; Scored: %s; Declared: %s -----------------\n", this.id, wild, open, isFinished() + "", scored + "", declared + "");
        for (Player p : players) {
            PlayerRummy player = (PlayerRummy) p;
            String strLabel = (player.name.equals(buttonName) ? "b" : "") + (player.name.equals(heroName) ? "h" : "") + (player.isDropped ? "D" : "");
            if (!"".equals(strLabel.trim()))
                strLabel = String.format("(%s)", strLabel);
            result += (player == getCurMovePlayer() ? "*" : " ") + String.format("%-5s", strLabel) + player + "\n";
        }
        return result;
    }

}
