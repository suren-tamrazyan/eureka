package solver.rummy.indian;

import game.Card;

import java.util.Objects;

public class DiscardMove extends Action {
    public DeckCard discard;

    public DiscardMove(DeckCard discardCard) {
        discard = discardCard;
    }

    @Override
    public String toString() {
        return "Discard " + discard;
    }

    public boolean isJoker(int wildcardRank) {
        return discard.card.getIndex() == 52 || discard.card.getIndex() == 53 || discard.card.getRank() == wildcardRank;
    }

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
