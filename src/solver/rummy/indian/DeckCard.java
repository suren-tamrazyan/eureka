package solver.rummy.indian;

import game.Card;

import java.util.Objects;

public class DeckCard {
    public int deck;
    public Card card;

    public DeckCard(int deck, Card card) {
        this.deck = deck;
        this.card = card;
    }

    @Override
    public String toString() {
        return card + "(" + deck + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeckCard deckCard = (DeckCard) o;
        return deck == deckCard.deck && Objects.equals(card, deckCard.card);
    }

    @Override
    public int hashCode() {
        return Objects.hash(deck, card);
    }
}
