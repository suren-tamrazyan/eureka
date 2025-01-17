package solver.rummy.indian;

import game.Card;
import solver.mcts.MctsDomainState;
import solver.rummy.indian.meld.MeldNode;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class State implements MctsDomainState<Action, Agent> {
    public static final int HAND_SIZE = 13;
    public static final int DECK_SIZE = 54;

    public static Agent agent = new Agent();
//    public int DECK_COUNT;
    public int round;
    public List<DeckCard> heroHand;
    public List<DeckCard> knownDiscardedCards;
    public ArrayList<DeckCard> deck;
    public int wildcardRank;
    public DeckCard topDiscardPile; // contain in knownDiscardedCards
    public DeckCard hiddenCard; // for KickRummy we know hiddenCard
    public boolean isOriginal; // true - state from game, false - state from simulation process; can mean as isRoot
    public DecisionPhase phase;
    public boolean isCompletable() {
        return solution != null;
    };
    public MeldNode rootMeldsTree;
    public MeldNode solution;
    public int sumMinValueOfPath;
    public int sumDeltaMinValueOfPath;
    public List<Action> availableActionsForAgent; // cash


    public void init(Collection<Card> heroHand, Collection<Card> knownDiscardedCards, int wildcardRank, Card topDiscardPile, DecisionPhase phase, int DECK_COUNT, Card hiddenCard) {
        this.round = 1;
        this.wildcardRank = wildcardRank;
        this.topDiscardPile = null;
        this.hiddenCard = null;
        this.deck = new ArrayList<>();
        this.isOriginal = true;
        this.phase = phase;
        this.heroHand = new ArrayList<>(heroHand.size());
        this.knownDiscardedCards = new ArrayList<>(knownDiscardedCards.size());
        List<Card> tmpHeroHand = new ArrayList<>(heroHand);
        List<Card> tmpKnownDiscardedCards = new ArrayList<>(knownDiscardedCards);
        for (int i = 0; i < DECK_COUNT; i++) {
            for (int j = 0; j < DECK_SIZE; j++) {
                DeckCard deckCard = new DeckCard(i, Card.getCard(j));
                if (tmpHeroHand.remove(deckCard.card)) {
                    this.heroHand.add(deckCard);
                } else {
                    if (this.topDiscardPile == null && deckCard.card.equals(topDiscardPile)) {
                        this.topDiscardPile = deckCard;
                    }
                    if (this.hiddenCard == null && deckCard.card.equals(hiddenCard)) {
                        this.hiddenCard = deckCard;
                    }
                    if (tmpKnownDiscardedCards.remove(deckCard.card)) {
                        this.knownDiscardedCards.add(deckCard);
                    } else {
                        this.deck.add(deckCard);
                    }
                }
            }
        }
        Collections.shuffle(this.deck);
        this.buildMeldsTree();
        if (!tmpHeroHand.isEmpty())
            throw new IllegalArgumentException("Invalid hero hand: has excess cards " + tmpHeroHand);
        if (!tmpKnownDiscardedCards.isEmpty())
            throw new IllegalArgumentException("Invalid KnownDiscardedCards: has excess cards " + tmpKnownDiscardedCards);
    }

    @Override
    public State clone() {
        State result = new State();
        result.heroHand = new ArrayList<>(heroHand);
        result.knownDiscardedCards = new ArrayList<>(knownDiscardedCards);
        result.deck = new ArrayList<>(deck);
        result.round = round;
        result.wildcardRank = wildcardRank;
        result.topDiscardPile = topDiscardPile;
        result.hiddenCard = hiddenCard;
//        result.DECK_COUNT = DECK_COUNT;
        result.isOriginal = isOriginal;
        result.phase = phase;
        result.sumMinValueOfPath = sumMinValueOfPath;
        return result;
    }

    public DeckCard pickCard() {
//        if (deck.size() == 0) {
//            reshuffleDeck();
//        }
        int randomIndex = ThreadLocalRandom.current().nextInt(deck.size());
        DeckCard result = deck.get(randomIndex);
        deck.set(randomIndex, deck.get(deck.size() - 1));
        deck.remove(deck.size() - 1);
        return result;
    }

    public void drawCard(DrawMove action) {
        assert phase == DecisionPhase.DRAW;
        DeckCard drawnCard;
        if (action.drawFromDiscardPile) {
            drawnCard = topDiscardPile;
            topDiscardPile = null;
        } else {
            if (hiddenCard != null) {
                drawnCard = hiddenCard;
                hiddenCard = null;
                deck.remove(drawnCard);
            } else
                drawnCard = pickCard();
        }
        heroHand.add(drawnCard);
        phase = DecisionPhase.DISCARD;
        round++;
        buildMeldsTree();
    }

    public void drawCard(DrawRandomCardMove action) {
        assert phase == DecisionPhase.DRAW;
        deck.remove(action.drawRandomCard);
        heroHand.add(action.drawRandomCard);
        phase = DecisionPhase.DISCARD;
        round++;
        buildMeldsTree();
    }

    public void discard(DiscardMove action) {
        assert phase == DecisionPhase.DISCARD;
        boolean succDiscarded = heroHand.remove(action.discard);
        assert succDiscarded;
        knownDiscardedCards.add(action.discard);
        phase = DecisionPhase.DRAW;
    }

    public void buildMeldsTree() {
//        if (rootMeldsTree != null) return;
//        rootMeldsTree = new MeldNode(heroHand.stream().map(dc -> dc.card).collect(Collectors.toList()));
        // replaced stream to loop
        List<Card> cards = new ArrayList<>(heroHand.size());
        for (DeckCard dc : heroHand) {
            cards.add(dc.card);
        }
        int prevMinVal = 0;
        if (rootMeldsTree != null)
            prevMinVal = rootMeldsTree.minValue;
        rootMeldsTree = new MeldNode(cards);
        solution = rootMeldsTree.depthFirstSearch(wildcardRank);
        sumMinValueOfPath += rootMeldsTree.minValue;
        sumDeltaMinValueOfPath = rootMeldsTree.minValue - prevMinVal;
    }

    @Override
    public boolean isTerminal() {
        switch (Config.GOAL) {
            case COMPLETABLE_DISTANCE:
                return isCompletable() || this.deck.size() == 0;
            case MIN_LEAF_VALUE:
                return this.round > Config.DEPTH_OF_SEARCH;
            case SUM_MIN_VALUE_OF_PATH:
            case SUM_DELTA_MIN_VALUE_OF_PATH:
                if (Config.DEPTH_OF_SEARCH > 0)
                    return this.round > Config.DEPTH_OF_SEARCH || (solution != null && phase == DecisionPhase.DISCARD && solution.unassembledCards.size() <= 1);
                else
                    return isCompletable() || this.deck.size() == 0;
        }
        return false;
    }

    @Override
    public Agent getCurrentAgent() {
        return agent;
    }

    @Override
    public Agent getPreviousAgent() {
        return agent;
    }

/*
    @Override
    public int getNumberOfAvailableActionsForCurrentAgent() {
//        if (availableActionsForAgent == null) {
            if (phase == DecisionPhase.DRAW) {
                if (isOriginal)
                    return 2;
                else
                    return 1;
            }
            if (phase == DecisionPhase.DISCARD) return heroHand.size();
            assert false;
            return 0;
//        } else
//            return availableActionsForAgent.size();
    }

    @Override
    public List<Action> getAvailableActionsForCurrentAgent() {
//        if (availableActionsForAgent == null) {
//            if (phase == DecisionPhase.DRAW) {
//                if (isOriginal)
//                    availableActionsForAgent = Arrays.asList(new DrawMove(true), new DrawMove(false));
//                else
//                    availableActionsForAgent = Collections.singletonList(new DrawMove(false));
//            }
//            if (phase == DecisionPhase.DISCARD)
//                availableActionsForAgent = heroHand.stream().map(DiscardMove::new).collect(Collectors.toList());
//        }
//        return availableActionsForAgent;

        if (phase == DecisionPhase.DRAW) {
            if (isOriginal)
                return Arrays.asList(new DrawMove(true), new DrawMove(false));
            else
                return Collections.singletonList(new DrawMove(false));
        }
        if (phase == DecisionPhase.DISCARD) return heroHand.stream().map(DiscardMove::new).collect(Collectors.toList());
        assert false;
        return null;
    }
*/

    @Override
    public int getNumberOfAvailableActionsForCurrentAgent() {
        if (phase == DecisionPhase.DRAW) {
            if (isOriginal)
                return 2;
            else
                return deck.size();
        }
        if (phase == DecisionPhase.DISCARD) return heroHand.size();
        assert false;
        return 0;
    }

    @Override
    public List<Action> getAvailableActionsForCurrentAgent() {
        if (phase == DecisionPhase.DRAW) {
            if (isOriginal)
                return Arrays.asList(new DrawMove(true), new DrawMove(false));
            else {
//                return deck.stream().map(DrawRandomCardMove::new).collect(Collectors.toList());
                // replaced stream to loop
                List<Action> moves = new ArrayList<>(deck.size());
                for (DeckCard card : deck) {
                    moves.add(new DrawRandomCardMove(card));
                }
                return moves;
            }
        }
        if (phase == DecisionPhase.DISCARD) {
//            return heroHand.stream().map(DiscardMove::new).collect(Collectors.toList());
            // replaced stream to loop
            List<Action> moves = new ArrayList<>(heroHand.size());
            for (DeckCard card : heroHand) {
                moves.add(new DiscardMove(card));
            }
            return moves;
        }
        assert false;
        return null;
    }

    @Override
    public MctsDomainState performActionForCurrentAgent(Action action) {
        switch (phase) {
            case DRAW:
                if (isOriginal)
                    drawCard((DrawMove) action);
                else
                    drawCard((DrawRandomCardMove) action);
                break;
            case DISCARD:
                discard((DiscardMove) action);
                break;
        }
        isOriginal = false;
        return this;
    }

    @Override
    public MctsDomainState skipCurrentAgent() {
        return this;
    }

    @Override
    public boolean currentAgentActionsIsOrderedMode() {
        return false;
    }

    @Override
    public void beforeCloning() {

    }

    @Override
    public double getExplorationParameter() {
        switch (phase) {
            case DRAW:
                return 1000; // random
            case DISCARD:
                return Config.EXPLORATION_PARAMETER;
        }
        assert false;
        return 1.41;
    }
}
