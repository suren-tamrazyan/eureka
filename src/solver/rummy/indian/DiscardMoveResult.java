package solver.rummy.indian;

import game.Card;

public class DiscardMoveResult extends Action {
    public Card discard;
    public boolean declare;
    public DiscardMoveResult(Card discardCard, boolean declare) {
        this.discard = discardCard;
        this.declare = declare;
    }

    @Override
    public String toString() {
        return "Discard " + discard + (declare ? " [declare]" : "");
    }
}
