package solver.rummy.indian.meld;

import game.Card;

import java.util.*;

import static solver.rummy.indian.meld.Utils.*;

public class MeldNode {
    public static final int LEVEL_ROOT = 0;
    public static final int LEVEL_PURE_SEQ = 1;
    public static final int LEVEL_IMPURE_SEQ = 2;
    public List<Card> unassembledCards;
    public List<Integer> statistics;
    public List<Card> meld;

    public MeldType type;
    public MeldNode parent;
    public List<MeldNode> children;
    int level;

    public MeldNode(Collection<Card> meld, MeldType meldType, MeldNode parent) {
        this.unassembledCards = new ArrayList<>(parent.unassembledCards);
        this.unassembledCards.removeAll(meld);
        this.meld = new ArrayList<>(meld);
        this.type = meldType;
        this.parent = parent;
        this.level = parent.level + 1;
    }

    public MeldNode(Collection<Card> rootHandCards) {
        this.unassembledCards = new ArrayList<>(rootHandCards);
        this.statistics = new ArrayList<>(Collections.nCopies(this.unassembledCards.size(), 0));
        meld = Collections.emptyList();
        level = LEVEL_ROOT;
    }

    public void expand(int wildcardRank) {
        children = new ArrayList<>();
        if (level == LEVEL_ROOT) {
            Collection<Collection<Card>> pureSequences = findPureSequences(unassembledCards);
            for (Collection<Card> sequence : pureSequences) {
                children.add(new MeldNode(sequence, MeldType.PURE_SEQ, this));
            }
        }
        if (level == LEVEL_PURE_SEQ) {
            Collection<Collection<Card>> impureSequences = findImpureSequences(unassembledCards, wildcardRank);
            for (Collection<Card> sequence : impureSequences) {
                children.add(new MeldNode(sequence, MeldType.IMPURE_SEQ, this));
            }
        }
        if (level >= LEVEL_IMPURE_SEQ) {
            Collection<Collection<Card>> impureSets = findImpureSets(unassembledCards, wildcardRank);
            for (Collection<Card> set : impureSets) {
                children.add(new MeldNode(set, MeldType.IMPURE_SET, this));
            }
            Collection<Collection<Card>> impureSeq = findImpureSequences(unassembledCards, wildcardRank);
            for (Collection<Card> seq : impureSeq) {
                children.add(new MeldNode(seq, MeldType.IMPURE_SEQ, this));
            }
        }
    }

    public MeldNode depthFirstSearch(int wildcardRank) {
        if (unassembledCards.size() <= 1) {
            return this;
        }

        if (children == null) {
            expand(wildcardRank);
            children.sort(Comparator.comparingInt(node -> node.unassembledCards.size()));
        }

        for (MeldNode child : children) {
            MeldNode leaf = child.depthFirstSearch(wildcardRank);
            if (leaf != null)
                return leaf;
        }

        updateStatistics();
        return null;
    }

    private void updateStatistics() {
        MeldNode root = getRoot();
        for (Card card : this.unassembledCards) {
            int index = root.unassembledCards.indexOf(card);
            root.statistics.set(index, root.statistics.get(index) + 1);
        }
    }

    private MeldNode getRoot() {
        MeldNode node = this;
        while (node.parent != null)
            node = node.parent;
        return node;
    }

    public void breadthFirstSearch(int wildcardRank) {
        Queue<MeldNode> queue = new LinkedList<>();
        queue.add(this);

        while (!queue.isEmpty()) {
            MeldNode currentNode = queue.poll();
            System.out.println(currentNode);

            if (currentNode.children == null)
                currentNode.expand(wildcardRank);

            for (MeldNode child : currentNode.children)
                queue.add(child);
        }
    }

    public List<List<Card>> gatherMelds() {
        List<List<Card>> result = new ArrayList<>();
        MeldNode current = this;
        while (current != null && current.level > LEVEL_ROOT) {
            result.add(current.meld);
            current = current.parent;
        }
        return result;
    }

    public int value(int wildcardRank) {
        return unassembledCards.stream().mapToInt(card -> Utils.value(card, wildcardRank)).sum();
    }

    public MeldNode findMinValueLeaf(int wildcardRank) {
        if (this.children == null || this.children.isEmpty()) {
            return this;
        }

        MeldNode minLeaf = null;
        int minValue = Integer.MAX_VALUE;

        for (MeldNode child : this.children) {
            MeldNode leaf = child.findMinValueLeaf(wildcardRank);
            int childValue = leaf.value(wildcardRank);

            if (childValue < minValue) {
                minValue = childValue;
                minLeaf = leaf;
            }
        }

        return minLeaf;
    }


    public static void main(String[] args) {
        List<Card> hand = Arrays.asList(Card.str2Cards("2c 3s 4c 5c 6c Kd Qc Ac Jd 5d Kc 5c Qd"));
        int wildcardRank = 2;
        MeldNode rootMeldsTree = new MeldNode(hand);
        MeldNode solution = rootMeldsTree.depthFirstSearch(wildcardRank);
        if (solution != null)
            System.out.println(solution.gatherMelds());
        MeldNode minleaf = rootMeldsTree.findMinValueLeaf(wildcardRank);
        System.out.println(minleaf.gatherMelds());
        System.out.println("value: " + minleaf.value(wildcardRank));
    }
}
