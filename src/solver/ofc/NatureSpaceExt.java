package solver.ofc;

import game.Card;
import game.GameOfc;
import game.PlayerOfc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class NatureSpaceExt extends NatureSpace{
    public static class OppHand {
        public List<Card> front;
        public List<Card> middle;
        public List<Card> back;
        public List<Card> cardsToBeBoxed;
        public long mask = 0L;
//        public String strFront;
//        public String strMiddle;
//        public String strBack;
        public boolean oppPlayFantasy;
        public double evalSingle;
        public OppHand(List<Card> aFront, List<Card> aMiddle, List<Card> aBack, List<Card> aCardsToBeBoxed, boolean aOppPlayFantasy) {
            front = aFront;
            middle = aMiddle;
            back = aBack;
            cardsToBeBoxed = aCardsToBeBoxed;
            oppPlayFantasy = aOppPlayFantasy;
        }
        public void completion() {
            if (!cardsToBeBoxed.isEmpty()) {
                EventOfcMctsSimple distrib = Heuristics.completion(front, middle, back, cardsToBeBoxed);
                front.addAll(distrib.front);
                middle.addAll(distrib.middle);
                back.addAll(distrib.back);
            }
            for (Card crd : front)
                mask |= (1L << crd.getIndex());
            for (Card crd : middle)
                mask |= (1L << crd.getIndex());
            for (Card crd : back)
                mask |= (1L << crd.getIndex());
//            strFront = front.stream().map(Card::toStrDirect).collect(Collectors.joining(""));
//            strMiddle = middle.stream().map(Card::toStrDirect).collect(Collectors.joining(""));
//            strBack = back.stream().map(Card::toStrDirect).collect(Collectors.joining(""));
            if (Config.EvaluationMethod == Config.EvaluationMethodKind.BOARD_SINGLE)
                evalSingle = EvaluatorFacade.evaluate(front, middle, back, oppPlayFantasy);
        }
    }

    // OPP_RANDOM_DEAL_COUNT -> opps.size
    public List<List<OppHand>> natureSamplesOpp;

    public NatureSpaceExt(List<Card> front, List<Card> middle, List<Card> back, List<Card> toBeBoxed, List<Card> otherOpenedCard, GameOfc.GameMode aGameMode, boolean aIsFirstRound, String aHeroName, Config aCfg, List<OppHand> opps) {
        super(front, middle, back, toBeBoxed, otherOpenedCard, aGameMode, aIsFirstRound, aHeroName, aCfg);

        natureSamplesOpp = new ArrayList<>(opps.size());

        int deckSize = GameOfcMctsSimple.calcDeckSize(aGameMode);
        List<Card> availableCards = new ArrayList<>(deckSize);
        for (int i = 0; i < deckSize; i++)
            availableCards.add(Card.getCard(i));
        availableCards.removeAll(otherOpenedCard);
        availableCards.removeAll(front);
        availableCards.removeAll(middle);
        availableCards.removeAll(back);
        availableCards.removeAll(toBeBoxed);
        for (OppHand opp : opps) {
            availableCards.removeAll(opp.front);
            availableCards.removeAll(opp.middle);
            availableCards.removeAll(opp.back);
        }


        // TODO case for small space
        for (int i = 0; i < Config.OPP_RANDOM_DEAL_COUNT; i++) {
            Collections.shuffle(availableCards);
            int fromIndex = 0;
            List<OppHand> smplsOpps = new ArrayList<>();
            for (OppHand opp : opps) {
                int dealSize = 13 - (opp.front.size() + opp.middle.size() + opp.back.size());
                List<Card> lstDeal = availableCards.subList(fromIndex, fromIndex + dealSize);
                fromIndex += dealSize;
                smplsOpps.add(new OppHand(new ArrayList<>(opp.front), new ArrayList<>(opp.middle), new ArrayList<>(opp.back), new ArrayList<>(lstDeal), opp.oppPlayFantasy));
            }
            natureSamplesOpp.add(smplsOpps);
        }

        natureSamplesOpp.parallelStream().forEach(x -> x.forEach(OppHand::completion));
    }

    public NatureSpaceExt(GameOfc game, Config aCfg) {
        this(game.getPlayer(game.heroName).boxFront.toList(), game.getPlayer(game.heroName).boxMiddle.toList(), game.getPlayer(game.heroName).boxBack.toList(),
                game.getPlayer(game.heroName).cardsToBeBoxed, GameOfcMctsSimple.mergeToOther(game), game.gameMode, game.isFirstRound(), game.heroName, aCfg,
                Arrays.stream(game.getPlayers()).filter(pl -> !pl.isHero(game.heroName)).map(player -> new OppHand(player.boxFront.toList(), player.boxMiddle.toList(), player.boxBack.toList(), null, player.playFantasy)).collect(Collectors.toList()));
    }

    public int evaluateGameAcross(List<Card> frontHero, List<Card> middleHero, List<Card> backHero, List<OppHand> opps, boolean heroInFantasy, int fantasyScore) {
        int result = 0;
        for (OppHand opp : opps)
            result += EvaluatorFacade.evaluate(frontHero, middleHero, backHero, opp.front, opp.middle, opp.back, heroInFantasy, opp.oppPlayFantasy, fantasyScore);
        return result;
    }

    public int evaluateGameSingle(double heroEval, List<OppHand> opps) {
        int result = 0;
        for (OppHand opp : opps)
            result += (heroEval - opp.evalSingle);
        return result;
    }

    public static class DontPassSpaceException extends Exception{}
    public double evaluateBySpace(List<Card> frontHero, List<Card> middleHero, List<Card> backHero, boolean heroInFantasy) throws DontPassSpaceException {
        long maskHero = 0L;
        for (Card crd : frontHero)
            maskHero |= (1L << crd.getIndex());
        for (Card crd : middleHero)
            maskHero |= (1L << crd.getIndex());
        for (Card crd : backHero)
            maskHero |= (1L << crd.getIndex());
        int sum = 0;
        int cnt = 0;
        double heroEval = 0;
        if (Config.EvaluationMethod == Config.EvaluationMethodKind.BOARD_SINGLE)
            heroEval = EvaluatorFacade.evaluate(frontHero, middleHero, backHero, heroInFantasy);
        for (List<OppHand> opps : natureSamplesOpp) {
            boolean pass = true;
            for (OppHand op : opps)
                pass = pass && (maskHero & op.mask) == 0;
            if (pass) {
                switch (Config.EvaluationMethod) {
                    case BOARD_SINGLE:
                        sum += evaluateGameSingle(heroEval, opps);
                        break;
                    case BOARD_ACROSS:
                        sum += evaluateGameAcross(frontHero, middleHero, backHero, opps, heroInFantasy, Config.FANTASY_SCORE);
                        break;
                    default:
                        throw new IllegalArgumentException("Config.EvaluationMethod is not BOARD_SINGLE and not BOARD_ACROSS");
                }
                cnt++;
            }
        }
        if (cnt == 0)
            throw new DontPassSpaceException();
        return ((double)sum)/((double)cnt);
    }

}
