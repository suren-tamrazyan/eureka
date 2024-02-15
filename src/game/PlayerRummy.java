package game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import util.Misc;

public class PlayerRummy extends Player {
    public static int UNDEF_SCORE = Integer.MIN_VALUE;

    public boolean isDropped = false;
    public HashMap<Integer, ArrayList<Card>> cardsGroup = new HashMap<>();
    public int score = UNDEF_SCORE;
    public int won = 0;
    public boolean gotCard = false;

    public List<Card> discardedCards = new ArrayList<>();
    public List<Card> takenOpenCards = new ArrayList<>();
	public List<Card> ignoredOpenCards = new ArrayList<>();

    public PlayerRummy(String name, int stack) {
        super(name, stack);
    }

    public PlayerRummy(PlayerRummy clone) {
        super(clone.name, clone.stack);

        cardsGroup = GameRummy.copyCardsGroup(clone.cardsGroup);
        score = clone.score;
        isDropped = clone.isDropped;
        gotCard = clone.gotCard;
    }

    public boolean hasCard(Card cc) {
        for (Integer group : cardsGroup.keySet()) {
            for (Card c : cardsGroup.get(group)) {
                if (c.getMask() == cc.getMask()) return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        String scg = "";
        if (cardsGroup != null) {
            for (Integer group : cardsGroup.keySet()) {
                scg += "[";
                for (Card c : cardsGroup.get(group)) {
                    scg += c + ", ";
                }
                if (scg.length() > 2)
                    scg = scg.substring(0, scg.length() - 2);
                scg += "]";
            }
        }
        return String.format("%-15s %-7s %-5s CG:%s", name, score == UNDEF_SCORE ? "undef" : score, gotCard + "", scg);
    }
}
