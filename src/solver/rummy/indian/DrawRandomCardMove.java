package solver.rummy.indian;

import game.Card;

import java.util.Objects;

public class DrawRandomCardMove extends Action {
    public Card drawRandomCard;

    public DrawRandomCardMove(Card drawRandomCard) {
        this.drawRandomCard = drawRandomCard;
    }

    @Override
    public String toString() {
        return "Draw Card " + drawRandomCard;
    }

    // commented because can be double card
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DrawRandomCardMove that = (DrawRandomCardMove) o;
        return Objects.equals(drawRandomCard, that.drawRandomCard);
    }

    @Override
    public int hashCode() {
        return Objects.hash(drawRandomCard);
    }
}
