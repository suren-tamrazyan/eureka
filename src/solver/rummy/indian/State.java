package solver.rummy.indian;

import game.Card;
import solver.mcts.MctsDomainState;
import solver.rummy.indian.meld.MeldNode;
import util.Misc;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class State implements MctsDomainState<Action, Agent> {
    public static final int HAND_SIZE = 13;
    public static final int DECK_SIZE = 54;

    public static Agent agent = new Agent();
//    public int DECK_COUNT;
    public int round;
    public List<Card> heroHand;
    public List<Card> knownDiscardedCards;
    public ArrayList<Card> deck;
    public int wildcardRank;
    public Card topDiscardPile; // contain in knownDiscardedCards
    public boolean isOriginal; // true - state from game, false - state from simulation process; can mean as isRoot
    public DecisionPhase phase;
    public boolean isCompletable() {
        return solution != null;
    };
    public MeldNode rootMeldsTree;
    public MeldNode solution;


    public void init(Collection<Card> heroHand, Collection<Card> knownDiscardedCards, int round, int wildcardRank, Card topDiscardPile, DecisionPhase phase, int DECK_COUNT) {
        this.heroHand = new ArrayList<>(heroHand);
        this.knownDiscardedCards = new ArrayList<>(knownDiscardedCards);
        this.round = round;
        this.wildcardRank = wildcardRank;
        this.topDiscardPile = topDiscardPile;
        this.deck = new ArrayList<>();
        this.isOriginal = true;
        this.phase = phase;
        List<Card> allKnownCards = new ArrayList<>(this.heroHand);
        allKnownCards.addAll(this.knownDiscardedCards);
        for (int i = 0; i < DECK_COUNT; i++) {
            for (int j = 0; j < DECK_SIZE; j++) {
                if (!allKnownCards.remove(Card.getCard(j)))
                    this.deck.add(Card.getCard(j));
            }
        }
        Collections.shuffle(this.deck);
        this.buildMeldsTree();
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
//        result.DECK_COUNT = DECK_COUNT;
        result.isOriginal = isOriginal;
        result.phase = phase;
        return result;
    }

    public Card pickCard() {
//        if (deck.size() == 0) {
//            reshuffleDeck();
//        }
        int randomIndex = ThreadLocalRandom.current().nextInt(deck.size());
        Card result = deck.get(randomIndex);
        deck.set(randomIndex, deck.get(deck.size() - 1));
        deck.remove(deck.size() - 1);
        return result;
    }

    public void drawCard(DrawMove action) {
        assert phase == DecisionPhase.DRAW;
        Card drawnCard;
        if (action.drawFromDiscardPile) {
            drawnCard = topDiscardPile;
            topDiscardPile = null;
        } else
            drawnCard= pickCard();
        heroHand.add(drawnCard);
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
//    public void applyAction(Action action) {
//        isOriginal = false;
//        buildMeldsForest();
//    }

    public void buildMeldsTree() {
//        if (rootMeldsTree != null) return;
        rootMeldsTree = new MeldNode(heroHand);
        solution = rootMeldsTree.depthFirstSearch(wildcardRank);
    }

    @Override
    public boolean isTerminal() {
        return isCompletable() || this.deck.size() == 0;
    }

    @Override
    public Agent getCurrentAgent() {
        return agent;
    }

    @Override
    public Agent getPreviousAgent() {
        return agent;
    }

    @Override
    public int getNumberOfAvailableActionsForCurrentAgent() {
        if (phase == DecisionPhase.DRAW) {
            if (isOriginal)
                return 2;
            else
                return 1;
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
            else
                return Collections.singletonList(new DrawMove(false));
        }
        if (phase == DecisionPhase.DISCARD) return heroHand.stream().map(DiscardMove::new).collect(Collectors.toList());
        assert false;
        return null;
    }

    @Override
    public MctsDomainState performActionForCurrentAgent(Action action) {
        switch (phase) {
            case DRAW:
                drawCard((DrawMove) action);
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
}
