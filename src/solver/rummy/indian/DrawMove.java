package solver.rummy.indian;

import java.util.Objects;

public class DrawMove extends Action {
    public boolean drawFromDiscardPile; // true - draw from DiscardPile, false - draw from Deck
    public DrawMove(boolean drawFromDiscardPile) {
        this.drawFromDiscardPile = drawFromDiscardPile;
    }
    @Override
    public String toString() {
        return "Draw " + (drawFromDiscardPile ? "from DiscardPile" : "from Deck");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DrawMove drawMove = (DrawMove) o;
        return drawFromDiscardPile == drawMove.drawFromDiscardPile;
    }

    @Override
    public int hashCode() {
        return Objects.hash(drawFromDiscardPile);
    }
}
