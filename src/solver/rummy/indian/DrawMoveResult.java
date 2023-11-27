package solver.rummy.indian;

public class DrawMoveResult extends DrawMove {
    public double value;
    public DrawMoveResult(boolean drawFromDiscardPile) {
        super(drawFromDiscardPile);
    }

    public DrawMoveResult(boolean drawFromDiscardPile, double value) {
        super(drawFromDiscardPile);
        this.value = value;
    }
}
