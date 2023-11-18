package solver.rummy.indian.meld;

import game.Card;

import java.util.*;
import java.util.stream.Collectors;

public class Utils {

    public static List<List<Card>> findSubsequences(List<Card> sequence) {
        List<List<Card>> subsequences = new ArrayList<>();
        int n = sequence.size();
        for (int len = 3; len <= n; len++) {
            for (int start = 0; start <= n - len; start++) {
                List<Card> subsequence = new ArrayList<>(len);
                for (int i = start; i < start + len; i++) {
                    subsequence.add(sequence.get(i));
                }
                subsequences.add(subsequence);
            }
        }
        return subsequences;
    }

    public static Collection<Collection<Card>> findPureSequences(List<Card> hand) {
        Map<Integer, List<Card>> suitMap = new HashMap<>();
        for (Card card : hand) {
            if (card.getIndex() == 52 || card.getIndex() == 53) continue;
            suitMap.computeIfAbsent(card.getSuit(), k -> new ArrayList<>()).add(card);
        }

        Collection<Collection<Card>> pureSequences = new ArrayList<>();
        List<List<Card>> largestSequences = new ArrayList<>();

        for (List<Card> suitCards : suitMap.values()) {
            suitCards.sort(Comparator.comparing(Card::getRank));
//            LinkedHashSet<Card> currentSequence = new LinkedHashSet<>();
            List<Card> currentSequence = new ArrayList<>();
            for (Card card : suitCards) {
                if (currentSequence.isEmpty() && card.getRank() == 0 && suitCards.get(suitCards.size() - 1).getRank() == 12) { // Ace can be as first and last
                    currentSequence.add(suitCards.get(suitCards.size() - 1));
                    currentSequence.add(card);
                }
                if (!currentSequence.isEmpty() && currentSequence.get(currentSequence.size() - 1).getRank() == card.getRank()) // if two or more same card from different deck
                    continue;
                if (currentSequence.isEmpty() || (currentSequence.get(currentSequence.size() - 1).getRank() + 1 == card.getRank())) {
                    currentSequence.add(card);
                } else {
                    if (currentSequence.size() >= 3)
                        largestSequences.add(new ArrayList<>(currentSequence));
                    currentSequence.clear();
                    currentSequence.add(card);
                }
            }
            if (currentSequence.size() >= 3)
                largestSequences.add(new ArrayList<>(currentSequence));
        }

        for (List<Card> sequence : largestSequences) {
            if (sequence.size() >= 3)
                pureSequences.addAll(findSubsequences(sequence));
        }

        return pureSequences;
    }

    public static Collection<Collection<Card>> findImpureSequences(List<Card> hand, int wildcardRank) {
        Map<Integer, List<Card>> suitMap = new HashMap<>();
        List<Card> jokersFirst = new ArrayList<>();
        for (Card card : hand) {
            if (card.getIndex() == 52 || card.getIndex() == 53) {
                jokersFirst.add(card);
                continue;
            }
            if (card.getRank() == wildcardRank) {
                jokersFirst.add(card);
            }
            suitMap.computeIfAbsent(card.getSuit(), k -> new ArrayList<>()).add(card);
        }

        Collection<Collection<Card>> impureSequences = new HashSet<>();
        List<List<Card>> largestSequences = new ArrayList<>();

        for (List<Card> suitCards : suitMap.values()) {
            suitCards.sort(Comparator.comparing(Card::getRank));
            List<CardEx> currentSequence = new ArrayList<>();
//            List<CardEx> jokers = new ArrayList<>(jokersFirst.stream().map(CardEx::new).collect(Collectors.toList())); replaced stream to loop
            List<CardEx> jokers = new ArrayList<>(jokersFirst.size());
            for (Card jokerCard : jokersFirst)
                jokers.add(new CardEx(jokerCard));

            for (Card card : suitCards) {
                if (currentSequence.isEmpty() && card.getRank() == 0 && suitCards.get(suitCards.size() - 1).getRank() == 12) { // Ace can be as first and last
                    currentSequence.add(new CardEx(suitCards.get(suitCards.size() - 1)));
                    currentSequence.add(new CardEx(card));
                }
                if (!currentSequence.isEmpty() && currentSequence.get(currentSequence.size() - 1).getOriginal().getRank() == card.getRank()) // if two or more same card from different deck
                    continue;
//                if (currentSequence.isEmpty() && card.getRank() == wildcardRank)
//                    continue;
                while (!(currentSequence.isEmpty() || (currentSequence.get(currentSequence.size() - 1).getRank() + 1 == card.getRank())) && jokers.size() > 0) {
                    int lastRank = currentSequence.get(currentSequence.size() - 1).getRank();
                    CardEx jokerAsCard = jokers.remove(jokers.size() - 1);
                    if (lastRank < 12) {
                        jokerAsCard.setJokerRole(card.getSuit(), lastRank + 1);
                        currentSequence.add(jokerAsCard);
                    }
                }
                boolean cardExistsInSequenceAsJoker = false;
                if (card.getRank() == wildcardRank) {
                    for (CardEx cardEx : currentSequence) {
                        if (cardEx.getOriginal().getIndex() == card.getIndex()) {
                            cardExistsInSequenceAsJoker = true;
                            break;
                        }
                    }
                }
                if (currentSequence.isEmpty() || (currentSequence.get(currentSequence.size() - 1).getRank() + 1 == card.getRank() && !cardExistsInSequenceAsJoker)) {
                    currentSequence.add(new CardEx(card));
                    if (card.getRank() == wildcardRank)
                        jokers.remove(new CardEx(card));
                } else {
                    fillEndsByJokers(currentSequence, jokers);
                    if (currentSequence.size() >= 3) {
//                        largestSequences.add(new ArrayList<>(currentSequence.stream().map(CardEx::getOriginal)./*sorted(Comparator.comparing(Card::getRank)).jokers disorder*/collect(Collectors.toList()))); replaced stream to loop
                        List<Card> newLargestSequence = new ArrayList<>(currentSequence.size());
                        for (CardEx cardEx : currentSequence)
                            newLargestSequence.add(cardEx.getOriginal());
                        largestSequences.add(newLargestSequence);
                    }
                    currentSequence.clear();
                    jokers.clear();
//                    jokers.addAll(jokersFirst.stream().map(CardEx::new).collect(Collectors.toList())); replaced stream to loop
                    for (Card jokerCard : jokersFirst)
                        jokers.add(new CardEx(jokerCard));

                    currentSequence.add(new CardEx(card));
                }
            }
            fillEndsByJokers(currentSequence, jokers);
            if (currentSequence.size() >= 3) {
//                largestSequences.add(new ArrayList<>(currentSequence.stream().map(CardEx::getOriginal)./*sorted(Comparator.comparing(Card::getRank)).jokers disorder*/collect(Collectors.toList()))); replaced stream to loop
                List<Card> newLargestSequence = new ArrayList<>(currentSequence.size());
                for (CardEx cardEx : currentSequence)
                    newLargestSequence.add(cardEx.getOriginal());
                largestSequences.add(newLargestSequence);
            }
        }

        for (List<Card> sequence : largestSequences) {
            if (sequence.size() >= 3)
                impureSequences.addAll(findSubsequences(sequence));
        }

        return impureSequences;
    }

    public static Collection<Collection<Card>> findImpureSets(List<Card> hand, int wildcardRank) {
        Map<Integer, Set<Card>> rankMap = new HashMap<>();
        List<Card> jokersFirst = new ArrayList<>();
        for (Card card : hand) {
            if (card.getIndex() == 52 || card.getIndex() == 53) {
                jokersFirst.add(card);
                continue;
            }
            if (card.getRank() == wildcardRank) {
                jokersFirst.add(card);
            }
            rankMap.computeIfAbsent(card.getRank(), k -> new HashSet<>()).add(card);
        }

        Collection<Collection<Card>> impureSets = new HashSet<>();

        if (jokersFirst.size() >= 3)
            impureSets.add(new ArrayList<>(jokersFirst));
        if (jokersFirst.size() > 3)
            impureSets.add(new ArrayList<>(jokersFirst.subList(0, 3)));

        for (Set<Card> rankCards : rankMap.values()) {
            if (rankCards.size() + jokersFirst.size() >= 3) {
                List<Card> set = new ArrayList<>(rankCards);
                if (set.size() >= 3)
                    impureSets.add(new ArrayList<>(set));
                if (set.size() > 3) {
                    impureSets.add(new ArrayList<>(set.subList(0, 3)));
                    impureSets.add(new ArrayList<>(set.subList(1, 4)));
                    List<Card> tmp = new ArrayList<>(set.subList(2, 4));
                    tmp.add(set.get(0));
                    impureSets.add(tmp);
                }
                List<Card> jokers = new ArrayList<>(jokersFirst);
                while (set.size() < 4 && !jokers.isEmpty()) {
                    set.add(jokers.remove(jokers.size() - 1));
                    if (set.size() >= 3)
                        impureSets.add(new ArrayList<>(set));
                }
            }
        }

        return impureSets;
    }

    private static void fillEndsByJokers(List<CardEx> currentSequence, List<CardEx> jokers) {
//        if (currentSequence.size() + jokers.size() >= 3) {
//            int firstRank = currentSequence.get(0).getRank();
//            if (firstRank == 12) // Ace
//                firstRank = 1;
//            int firstAddable = firstRank - 1;
//            int lastAddable = 12 - currentSequence.get(currentSequence.size() - 1).getRank();
//            int additionalJokersCount = Math.min(lastAddable + firstAddable, jokers.size()); // additional jokers for adding first and last ends
//            currentSequence.addAll(jokers.subList(0, additionalJokersCount));
//        }
        // last End
        while (jokers.size() > 0) {
            int lastRank = currentSequence.get(currentSequence.size() - 1).getRank();
            if (lastRank < 12) {
                CardEx jokerAsCard = jokers.remove(jokers.size() - 1);
                jokerAsCard.setJokerRole(currentSequence.get(0).getOriginal().getSuit(), lastRank + 1);
                boolean jokerCardExistsInSeq = false;
                for (CardEx cardEx : currentSequence) {
                    if (cardEx.getOriginal().getIndex() == jokerAsCard.getOriginal().getIndex()) {
                        jokerCardExistsInSeq = true;
                        break;
                    }
                }
                if (!jokerCardExistsInSeq)
                    currentSequence.add(jokerAsCard);
            } else
                break;
        }
        // first End
        while (jokers.size() > 0) {
            int firstRank = currentSequence.get(0).getRank();
            if (firstRank > 2) {
                CardEx jokerAsCard = jokers.remove(jokers.size() - 1);
                jokerAsCard.setJokerRole(currentSequence.get(0).getOriginal().getSuit(), firstRank - 1);
                boolean jokerCardExistsInSeq = false;
                for (CardEx cardEx : currentSequence) {
                    if (cardEx.getOriginal().getIndex() == jokerAsCard.getOriginal().getIndex()) {
                        jokerCardExistsInSeq = true;
                        break;
                    }
                }
                if (!jokerCardExistsInSeq)
                    currentSequence.add(0, jokerAsCard);
            } else
                break;
        }
    }

    public static boolean isJoker(Card card, int wildcardRank) {
        return card.getIndex() == 52 || card.getIndex() == 53 || card.getRank() == wildcardRank;
    }

    public static int value(Card card, int wildcardRank) {
        if (isJoker(card, wildcardRank)) // don't count joker
            return 0;
        if (card.getRank() >= 9) // Ace, King, Queen and Jack each hold 10 points.
            return 10;
        else
            return card.getRank() + 2; // The other remaining cards have value equal to their face value
    }

    public static void main(String[] args) {
        List<Card> lst = new ArrayList<>();
//        lst.addAll(Arrays.asList(Card.str2Cards("4s 8d 9d Td Qd Ad Xr")));
        lst.addAll(Arrays.asList(Card.str2Cards("8d 9d Td 4s Qd Xr Ad")));
        List<List<Card>> subsec = findSubsequences(lst);
        System.out.println(subsec);
        List<Card> hand = new ArrayList<>();
//        hand.addAll(Arrays.asList(Card.str2Cards("2c 3c 4c 5s 6c Kd Qc Ac Jd 5d Kc 5d Qd")));
//        hand.addAll(Arrays.asList(Card.str2Cards("3s 3c 5s 5c Ks Kd Qc Xr Jd 5d Kc 5c Kh")));
        hand.addAll(Arrays.asList(Card.str2Cards("3s 8d 9d Td Qd Ad Ac Xr")));
        int wildcardRank = 2;
        Collection<Collection<Card>> pureSequences = findPureSequences(hand);
        Collection<Collection<Card>> impureSequences = findImpureSequences(hand, wildcardRank);
        Collection<Collection<Card>> impureSets = findImpureSets(hand, wildcardRank);
        for (Collection<Card> sequence : pureSequences) {
            System.out.println("Pure Sequence: " + sequence);
        }
        System.out.println();
        for (Collection<Card> sequence : impureSequences) {
            System.out.println("Impure Sequence: " + sequence);
        }
        System.out.println();
        for (Collection<Card> set : impureSets) {
            System.out.println("Impure Set: " + set);
        }

        for (int i = 0; i < 52; i++) {
            System.out.println(Card.getCard(i) + " " + Card.getCard(i).getRank());
        }
    }
}
