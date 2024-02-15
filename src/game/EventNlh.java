package game;

import util.Misc;

import java.io.Serializable;
import java.util.Arrays;

public class EventNlh extends Event implements Cloneable, Serializable {

    public static final int TYPE_UNKNOWN = 0;

    public static final int TYPE_HOLES = 1;

    public static final int TYPE_FLOP = 2;
    public static final int TYPE_TURN = 3;
    public static final int TYPE_RIVER = 4;

    public static final int TYPE_SB = 5;
    public static final int TYPE_BB = 6;
    public static final int TYPE_SBB = 7;
    public static final int TYPE_ANTE = 8;

    public static final int TYPE_MOVE_FIRST = 9;

    public static final int TYPE_FOLD = 9;
    public static final int TYPE_CALL = 10;
    public static final int TYPE_CALL_ALLIN = 11;
    public static final int TYPE_RAISE = 12;
    public static final int TYPE_RAISE_ALLIN = 13;
    public static final int TYPE_BET = 14;
    public static final int TYPE_BET_ALLIN = 15;
    public static final int TYPE_CHECK = 16;

    public static final int TYPE_MOVE_LAST = 16;

    public static final int TYPE_RETURN_UNCALLED = 18;
    public static final int TYPE_WIN_POT = 19;
    public static final int TYPE_LEAVE = 25;
    public static final int TYPE_NOT_SHOW = 27;
    public static final int TYPE_JOIN = 29;
    public static final int TYPE_SHOW = 31;
    public static final int TYPE_SHOWDOWN = 32;
    public static final int TYPE_MUCK = 33;
    public static final int TYPE_SIT_OUT = 35;
    public static final int TYPE_SITS_OUT = 36;
    public static final int TYPE_MISC = 44;
    public static final int TYPE_BB_ALLIN = 45;
    public static final int TYPE_HAND_CANCELED = 46;

    // From IP
    public static final int TYPE_PREFLOP = 50;
    public static final int TYPE_ALL_IN = 52;

    public static final int TYPE_FLOP2 = 54;
    public static final int TYPE_FLOP3 = 55;
    public static final int TYPE_TURN2 = 56;
    public static final int TYPE_TURN3 = 57;
    public static final int TYPE_RIVER2 = 58;
    public static final int TYPE_RIVER3 = 59;
    public static final int TYPE_SHOWDOWN2 = 60;
    public static final int TYPE_SHOWDOWN3 = 61;

    public static final int TYPE_WIN_SIDE_POT = 62;
    public static final int TYPE_GAME_FINISHED = 63;
    public static final int TYPE_BLIND_SB_TO_MID = 64;
    public static final int TYPE_STRADDLE = 65;

    public static final int TYPE_CONTRIB_BUYIN = 66;
    public static final int TYPE_CONTRIB_REBUY = 67;
    public static final int TYPE_ANTE_ALLIN = 68;
    public static final int TYPE_SB_ALLIN = 69;
    public static final int TYPE_STRADDLE_ALLIN = 70;
    public static final int TYPE_INSURANCE = 71;

    public static final int TYPE_PROFIT_VALUE = 72;
    public static final int TYPE_RAKE_VALUE = 73;
    public static final int TYPE_WIN_VALUE = 74;
    public static final int TYPE_PREFOLD = 75;

    public static final int TYPE_JACKPOT_ACT = 76;
    public static final int TYPE_BLIND_BB_TO_MID = 77;
    public String fromString = null;
    public double weight = Double.NaN;
    public Card[] holes;

    public EventNlh() {
    }

    public EventNlh(int type, String who) {
        super(type, who);
    }

    public EventNlh(int type, long iVal) {
        super(type, iVal);
    }

    public EventNlh(int type, String who, long iVal) {
        super(type, who, iVal);
    }

    /**
     * new TYPE_HOLES event
     */
    public EventNlh(String who, Card[] holes) {
        super(EventNlh.TYPE_HOLES, who, Card.cards2Mask(holes));
        this.holes = Arrays.copyOf(holes, holes.length);
    }

    /*public Event(int type, Player player, long iVal) {
        this.type = type;
        this.iVal = iVal;
        this.who = player.name;
        this.player = player;
    }*/
    public EventNlh(double weight, int type, String who, long iVal) {
        this(type, who, iVal);
        this.weight = weight;
        this.createTime = Misc.getTime();
    }

    public EventNlh(double weight, int type, String who) {
        this(type, who);
        this.weight = weight;
        this.createTime = Misc.getTime();
    }

    public EventNlh(int type, String who, String sVal) {
        super(type, who, sVal);
    }


    public static int toAllInType(int type) {
        switch (type) {
            case EventNlh.TYPE_CALL:
                return EventNlh.TYPE_CALL_ALLIN;
            case EventNlh.TYPE_RAISE:
                return EventNlh.TYPE_RAISE_ALLIN;
            case EventNlh.TYPE_BET:
                return EventNlh.TYPE_BET_ALLIN;
            case EventNlh.TYPE_BB:
                return EventNlh.TYPE_BB_ALLIN;
            case EventNlh.TYPE_SB:
                return EventNlh.TYPE_SB_ALLIN;
            case EventNlh.TYPE_ANTE:
                return EventNlh.TYPE_ANTE_ALLIN;
            case EventNlh.TYPE_STRADDLE:
                return EventNlh.TYPE_STRADDLE_ALLIN;
        }

        return type;
    }


    public static boolean isPutToPot(int t) {
        return t == TYPE_SBB || t == TYPE_SB || t == TYPE_BB || t == TYPE_BB_ALLIN || t == TYPE_ANTE || t == TYPE_ANTE_ALLIN || t == TYPE_STRADDLE || t == TYPE_RAISE || t == TYPE_RAISE_ALLIN || t == TYPE_CALL || t == TYPE_CALL_ALLIN || t == TYPE_BET || t == TYPE_BET_ALLIN || t == TYPE_SB_ALLIN || t == TYPE_STRADDLE_ALLIN;
    }

    public boolean isPutToPot() {
        return EventNlh.isPutToPot(this.type);
    }

    public boolean isBoard() {
        int t = this.type;
        return t == TYPE_FLOP || t == TYPE_TURN || t == TYPE_RIVER;
    }

    public boolean isBoard2() {
        int t = this.type;
        return t == TYPE_FLOP2 || t == TYPE_TURN2 || t == TYPE_RIVER2;
    }

    public boolean isBoard3() {
        int t = this.type;
        return t ==  TYPE_FLOP3 || t == TYPE_TURN3 || t == TYPE_RIVER3;
    }

    public boolean isAddBoard() {
        return isBoard2() || isBoard3();
    }

    public static boolean isMove(int t) {
        return t >= TYPE_MOVE_FIRST && t <= TYPE_MOVE_LAST;
    }

    public boolean isMove() {
        return EventNlh.isMove(this.type);
    }

    public boolean isFold() {
        return this.type == EventNlh.TYPE_FOLD || this.type == EventNlh.TYPE_PREFOLD;
    }

    public boolean isCheck() {
        return this.type == EventNlh.TYPE_CHECK;
    }

    public boolean isCall() {
        return this.type == EventNlh.TYPE_CALL || this.type == EventNlh.TYPE_CALL_ALLIN;
    }

    public boolean isStraddle() {
        return this.type == EventNlh.TYPE_STRADDLE || this.type == EventNlh.TYPE_STRADDLE_ALLIN;
    }

    public static boolean isBetOrRaise(int t) {
        return isBet(t) || isRaise(t);
    }

    public boolean isBetOrRaise() {
        return isBetOrRaise(this.type);
    }

    public static boolean isBet(int t) {
        return t == TYPE_BET || t == TYPE_BET_ALLIN;
    }

    public boolean isBet() {
        return isBet(this.type);
    }

    public static boolean isRaise(int t) {
        return t == TYPE_RAISE || t == TYPE_RAISE_ALLIN;
    }

    public boolean isRaise() {
        return isRaise(this.type);
    }

    public static boolean isCallOrCheck(int t) {
        return t == TYPE_CALL || t == TYPE_CALL_ALLIN || t == TYPE_CHECK;
    }

    public boolean isCallOrCheck() {
        return isCallOrCheck(this.type);
    }

    public boolean isAllIn() {
        int t = this.type;
        return t == TYPE_RAISE_ALLIN || t == TYPE_BET_ALLIN || t == TYPE_CALL_ALLIN || t == TYPE_BB_ALLIN || t == TYPE_ANTE_ALLIN
                || t == TYPE_SB_ALLIN || t == TYPE_STRADDLE_ALLIN;
    }

    public boolean isBlind() {
        int t = this.type;
        return t == TYPE_SB || t == TYPE_SB_ALLIN || t == TYPE_BB || t == TYPE_BB_ALLIN || t == TYPE_SBB || t == TYPE_STRADDLE || t == TYPE_STRADDLE_ALLIN;
    }

    public boolean isAnte() {
        int t = this.type;
        return t == TYPE_ANTE || t == TYPE_ANTE_ALLIN;
    }

    public boolean is2Showdowns() {
        int t = this.type;
        return t == TYPE_FLOP2 || t == TYPE_FLOP3 || t == TYPE_TURN2 || t == TYPE_TURN3
                || t == TYPE_RIVER2 || t == TYPE_RIVER3 || t == TYPE_SHOWDOWN2 || t == TYPE_SHOWDOWN3;

    }

    public boolean isWin() {
        int t = this.type;
        return t == TYPE_WIN_POT || t == TYPE_WIN_SIDE_POT /*|| t==TYPE_TIE_POT*/;
    }

    public boolean isCards() {
        int t = this.type;
        return t == TYPE_HOLES || this.isBoard() || t == TYPE_SHOW || t == TYPE_MUCK;
    }

    public String toStringNoWeight() {
        if (this.type == EventNlh.TYPE_UNKNOWN) {
            String ret = "UNKNOWN EVENT";
            if (this.fromString != null) ret += ": " + this.fromString;
            return ret;
        }
        if (this.fromString != null && !this.fromString.contains("No value"))
            return this.fromString;
        String ret = "";

        if (this.type == EventNlh.TYPE_HOLES) ret += "holes";

        else if (this.type == EventNlh.TYPE_FLOP) ret += "flop";
        else if (this.type == EventNlh.TYPE_TURN) ret += "turn";
        else if (this.type == EventNlh.TYPE_RIVER) ret += "river";
        else if (this.type == EventNlh.TYPE_FLOP2) ret += "flop2";
        else if (this.type == EventNlh.TYPE_TURN2) ret += "turn2";
        else if (this.type == EventNlh.TYPE_RIVER2) ret += "river2";
        else if (this.type == EventNlh.TYPE_FLOP3) ret += "flop3";
        else if (this.type == EventNlh.TYPE_TURN3) ret += "turn3";
        else if (this.type == EventNlh.TYPE_RIVER3) ret += "river3";

        else if (this.type == EventNlh.TYPE_SB || this.type == EventNlh.TYPE_SB_ALLIN) ret += "sb";
        else if (this.type == EventNlh.TYPE_BLIND_SB_TO_MID) ret += "small blind to mid";
        else if (this.type == EventNlh.TYPE_BLIND_BB_TO_MID) ret += "big blind to mid";
        else if (this.type == EventNlh.TYPE_SBB) ret += "sbb";
        else if (this.type == EventNlh.TYPE_BB || this.type == EventNlh.TYPE_BB_ALLIN) ret += "bb";
        else if (this.type == EventNlh.TYPE_STRADDLE || this.type == EventNlh.TYPE_STRADDLE_ALLIN) ret += "straddle";
        else if (this.type == EventNlh.TYPE_ANTE || this.type == EventNlh.TYPE_ANTE_ALLIN) ret += "ante";
        else if (this.type == EventNlh.TYPE_PREFLOP) ret += "preflop";

        else if (this.type == EventNlh.TYPE_FOLD) ret += "fold";
        else if (this.type == EventNlh.TYPE_PREFOLD) ret += "prefold";
        else if (this.type == EventNlh.TYPE_CHECK) ret += "check";
        else if (this.type == EventNlh.TYPE_BET || this.type == EventNlh.TYPE_BET_ALLIN) ret += "bet";
        else if (this.type == EventNlh.TYPE_RAISE || this.type == EventNlh.TYPE_RAISE_ALLIN) ret += "raise";
        else if (this.type == EventNlh.TYPE_CALL || this.type == EventNlh.TYPE_CALL_ALLIN) ret += "call";

        else if (this.type == EventNlh.TYPE_SHOW) ret += "show " + Card.mask2Str(this.iVal);
        else if (this.type == EventNlh.TYPE_MUCK) ret += "muck " + Card.mask2Str(this.iVal);
        else if (this.type == EventNlh.TYPE_JOIN) ret += "join table";
        else if (this.type == EventNlh.TYPE_LEAVE) ret += "leave table";
        else if (this.type == EventNlh.TYPE_SHOWDOWN) ret += "showdown";
        else if (this.type == EventNlh.TYPE_INSURANCE) ret += "insurance " + this.iVal;


        else if (this.type == EventNlh.TYPE_PROFIT_VALUE) ret += "profit " + this.iVal;
        else if (this.type == EventNlh.TYPE_WIN_VALUE) ret += "win " + this.iVal;
        else if (this.type == EventNlh.TYPE_RAKE_VALUE) ret += "rake " + this.iVal;
        else if (this.type == EventNlh.TYPE_JACKPOT_ACT) ret += "got bonus " + this.iVal;


        else ret += "event type=" + this.type;

        if (this.isAllIn()) ret += " all-in";

        if (this.who != null) ret = this.who + " " + ret;
        if (this.isPutToPot()) ret += " " + iVal;
        if (this.type == EventNlh.TYPE_BLIND_SB_TO_MID) ret += " " + iVal;
        if (this.type == EventNlh.TYPE_BLIND_BB_TO_MID) ret += " " + iVal;

        //if (this.isNewStage() && this.sVal != null) ret += " " + this.sVal;
        if (this.isBoard() || this.isAddBoard() || (this.type == EventNlh.TYPE_HOLES && this.iVal != 0))
            ret += " " + Card.mask2Str(this.iVal);

        return ret;
    }

    public String toString() {
        String ret = this.toStringNoWeight();
        //FIXME add time ?
        if (!Double.isNaN(this.weight)) ret += String.format(" (%1.1f%%)", this.weight * 100);
        return ret;
    }

    public boolean isWinRakeProfit() {
        return this.type == EventNlh.TYPE_WIN_VALUE ||
                this.type == EventNlh.TYPE_RAKE_VALUE ||
                this.type == EventNlh.TYPE_PROFIT_VALUE;
    }
}

	