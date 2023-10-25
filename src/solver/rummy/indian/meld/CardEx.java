package solver.rummy.indian.meld;

import game.Card;

import java.util.Objects;

public class CardEx {
    private Card original;
    private Card jokerRole;
    public CardEx(Card original) {
        this.original = original;
    }

    public int getRank() {
        if (jokerRole != null)
            return jokerRole.getRank();
        return original.getRank();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CardEx cardEx = (CardEx) o;
        return Objects.equals(original, cardEx.original);
    }

    @Override
    public int hashCode() {
        return Objects.hash(original);
    }

    @Override
    public String toString() {
        return "" + original + (jokerRole == null ? "" : " (" + jokerRole + ")");
    }

    public void setJokerRole(int suit, int rank) {
        jokerRole = Card.getCard(rank, suit);
    }

    public Card getOriginal() {
        return original;
    }
}
