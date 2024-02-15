package game;

import util.Misc;

import java.io.Serializable;

public abstract class Game implements Cloneable, Serializable {
    private static final long serialVersionUID = 1L;

    public enum Type {
        NLH,    // No Limit Holdem
        NLH31,    // No Limit Holdem 3-1

        NLHB,    // No Limit Holdem Bomb
        PLOB,    // PLO Bomb
        PLO5B,    // PLO5 Bomb
        PLO6B,    // PLO6 Bomb

        NLR,    // No Limit Holdem Ring
        PLR,    // PLO Ring
        PLR5,    // PLO5 Ring

        PLO,    // Pot Limit Omaha (4 Cards)
        PLO5,    // Pot Limit Omaha (5 Cards)
        PLO6,    // Pot Limit Omaha (6 Cards)

        NLP,    // No Limit Push (all-in or fold)
        CSDNL,    // Short deck No Limit
        CSDNLB,    // Short deck No Limit Bomb

        OFC,    // Open Face Chinese
        RUMMY,    // Rummy
        PUSOY,

        MTT  //workaround: temporary solution, use only in session.types
    }

    public enum Nw {
        PokerMaster, PokerKing, Dollaro, Chico, PokerStars,
        EuropeBet, Ppp, PokerWorld, Upoker, PokerTime,
        PokerBros, RummyCircle, Pocket52, PokerClans,
        JungleRummy, Spartan, RigelPoker, Bovada, InssaPoker, Ipoker,
        Xpoker, CityOfPoker, WePoker, WePokerClub, Peoples,
        PokerWorldEco, KKPoker, Synottip, Wpn,
        Tiger, ActionPoker, PokerHUB, Adda52, ClubGG, IcePoker, Pokerrrr2, Topaz,
        KickRummy, FishPoker, Suncity, GGPoker, MPL
    }

    public Type type;
    public String id;
    public String tableId;
    public long date;
    public String heroName;
    public String buttonName;
    public int curMovePlayerInd = -1;
    public Player[] players = new Player[0];
    public Player[] allPlayers = new Player[0];
    public int mcount = 0;
    public Nw network;

    public abstract String validateEvent(Event event) throws Exception;

    public abstract boolean isFinished();

    public void procEvent(Event event) throws Exception {
        procEvent(event, true);
    }

    public void procEvent(Event event, boolean validate) throws Exception {
        if (validate) {
            String v = this.validateEvent(event);
            if (v != null) {
                throw new GameException(v);
            }
        }

    }

    public void setType(Type type) {
        this.type = type;
    }

    @Override
    public abstract Game clone();

    public Player getPlayer(int uid) {
        return getPlayer(String.valueOf(uid));
    }

    public Player getPlayer(String name) {
        for (Player p : this.players) if (p.name.equals(name)) return p;
        return null;
    }

    public int getPlayerInd(String name) {
        return Game.getElementInd(this.players, this.getPlayer(name));
    }

    public int getPlayerIndAll(String name) {
        return Game.getElementInd(this.allPlayers, this.getPlayerFromAll(name));
    }

    public static Player[] removeElement(Player[] ar, int ind) {
        Player[] newAr = new Player[ar.length - 1];
        System.arraycopy(ar, 0, newAr, 0, ind);
        System.arraycopy(ar, ind + 1, newAr, ind, ar.length - ind - 1);
        return newAr;
    }

    public static Player[] insertElement(Player[] ar, int ind, Player p) {
        Player[] newAr = new Player[ar.length + 1];
        if (ar.length > 0) System.arraycopy(ar, 0, newAr, 0, ind);
        if (ar.length > ind) System.arraycopy(ar, ind, newAr, ind + 1, ar.length - ind);
        newAr[ind] = p;
        return newAr;
    }

    public static int getElementInd(Player[] ar, Player p) {
        for (int i = 0; i < ar.length; i++) if (ar[i] == p) return i;
        return -1;
    }

    public static Player[] setFirst(Player[] ar, Player p) {
        Player[] ret = new Player[ar.length];
        int pInd = Game.getElementInd(ar, p);
        System.arraycopy(ar, pInd, ret, 0, ar.length - pInd);
        System.arraycopy(ar, 0, ret, ar.length - pInd, pInd);
        return ret;
    }

    public Player getCurMovePlayer() {
        if (this.curMovePlayerInd < 0 || this.curMovePlayerInd > this.players.length - 1)
            return null;
        return this.players[this.curMovePlayerInd];
    }

    public boolean hasPlayer(Player p) {
        for (Player pp : this.players)
            if (p == pp) return true;
        return false;
    }

    public Player getPlayerFromAll(String name) {
        for (Player p : this.allPlayers) if (p.name.equals(name)) return p;
        return null;
    }

    public void addPlayer(Player p) {
        this.players = Game.insertElement(this.players, this.players.length, p);
        this.allPlayers = Game.insertElement(this.allPlayers, this.allPlayers.length, p);
    }

    public void setButtonPlayer() {
        Player p = this.getPlayerFromAll(this.buttonName);
        int i = Game.getElementInd(this.players, p);
        i = (i + 1) % this.players.length;
        this.players = Game.setFirst(this.players, this.players[i]);
    }

    public int getPlayerPos(int ind) {
        return this.players.length - ind;
    }

    public int getCurPlayerPos() {
        return getPlayerPos(this.curMovePlayerInd);
    }

    public static boolean isNLHType(Type t) {
        return (t == Type.NLH) || (t == Type.PLO) || (t == Type.PLO5)
                || (t == Type.NLP) || (t == Type.NLH31)// AOF and 31
                || (t == Type.NLR) || (t == Type.PLR) || (t == Type.PLR5)
                || (t == Type.CSDNL) || (t == Type.CSDNLB)
                || (t == Type.NLHB) || (t == Type.PLOB) || (t == Type.PLO5B)
                || (t == Type.PLO6) || (t == Type.PLO6B);
    }

    public static boolean isOFCType(Type t) {
        return t == Type.OFC;
    }

}
