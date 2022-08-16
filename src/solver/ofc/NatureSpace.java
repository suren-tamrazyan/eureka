package solver.ofc;

import game.Card;
import game.EventOfc;
import game.GameOfc;
import org.paukov.combinatorics3.Generator;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

public class NatureSpace {
    public List<EventOfcMctsSimple> natureSamples;
    public Map<String, Integer> numberTakesOfNatureSimulations = new HashMap<>();

    public NatureSpace(List<Card> front, List<Card> middle, List<Card> back, List<Card> toBeBoxed, List<Card> otherOpenedCard,
                       GameOfc.GameMode aGameMode, boolean aIsFirstRound, String aHeroName, Config aCfg) {
        //initAvailableActionsForNature; one common space of samples for all state (node) because one deal over algorithm
        int sizeFront = front.size();
        int sizeMiddle = middle.size();
        int sizeBack = back.size();
        int sizeCardsToBeBoxed = toBeBoxed.size();

        int deckSize = GameOfcMctsSimple.calcDeckSize(aGameMode);
        List<Card> availableCards = new ArrayList<>(deckSize);
        for (int i = 0; i < deckSize; i++)
            availableCards.add(Card.getCard(i));
        availableCards.removeAll(otherOpenedCard);
        availableCards.removeAll(front);
        availableCards.removeAll(middle);
        availableCards.removeAll(back);
        availableCards.removeAll(toBeBoxed);

        int dealSize = 13 - (sizeFront + sizeMiddle + sizeBack + (sizeCardsToBeBoxed - (aIsFirstRound?0:1)));
        BigInteger cntCombi = Utils.combinationCount(availableCards.size(), dealSize);
        if (cntCombi.compareTo(BigInteger.valueOf(3 * aCfg.RANDOM_DEAL_COUNT)) > 0) {
            this.natureSamples = new ArrayList<EventOfcMctsSimple>(aCfg.RANDOM_DEAL_COUNT);
            Set<Set<Card>> checkDistinct = new HashSet<>(aCfg.RANDOM_DEAL_COUNT);
            int i = 0;
            while (i < aCfg.RANDOM_DEAL_COUNT) {
                Collections.shuffle(availableCards);
                Set<Card> tmp = new HashSet<>(availableCards.subList(0, dealSize));
                if (checkDistinct.add(tmp)) {
                    this.natureSamples.add(new EventOfcMctsSimple(EventOfc.TYPE_DEAL_CARDS, tmp));
                    i++;
                }
            }
        } else {
            this.natureSamples = Generator.combination(availableCards).simple(dealSize).stream().map(x -> new EventOfcMctsSimple(EventOfc.TYPE_DEAL_CARDS, x)).collect(Collectors.toList());
            Collections.shuffle(this.natureSamples); // very, very important
        }

        this.numberTakesOfNatureSimulations.clear();
    }

    public NatureSpace(GameOfc game, Config aCfg) {
        this(game.getPlayer(game.heroName).boxFront.toList(), game.getPlayer(game.heroName).boxMiddle.toList(), game.getPlayer(game.heroName).boxBack.toList(),
                game.getPlayer(game.heroName).cardsToBeBoxed, GameOfcMctsSimple.mergeToOther(game), game.gameMode, game.isFirstRound(), game.heroName, aCfg);
    }
}
