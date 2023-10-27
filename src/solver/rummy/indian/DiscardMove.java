package solver.rummy.indian;

import game.Card;

import java.util.Objects;

public class DiscardMove extends Action {
    public Card discard;

    public DiscardMove(Card discardCard) {
        discard = discardCard;
    }

    @Override
    public String toString() {
        return "Discard " + discard;
    }

    public boolean isJoker(int wildcardRank) {
        return discard.getIndex() == 52 || discard.getIndex() == 53 || discard.getRank() == wildcardRank;
    }

    // commented because can be double card
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DiscardMove that = (DiscardMove) o;
        return Objects.equals(discard, that.discard);
    }

    @Override
    public int hashCode() {
        return Objects.hash(discard);
    }
}
