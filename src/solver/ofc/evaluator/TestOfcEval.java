package solver.ofc.evaluator;

public class TestOfcEval {
    public static void main(String[] args) {
        Deck d = new Deck();
        Evaluator ev = new Evaluator();
        Board board = new Board("AhQsJs", "Qd8c8d2c2d", "KhKd5h5c3s", d, ev);
        Board other = new Board("AdAhAd", "2d3cTd7c3d", "9s8s7s6s5s", d, ev);
//        Board other = new Board("", "", "", d, ev);

        Board boardAlt = new Board("AhKsKc", "Qd8c8d2c2d", "KhKd5h5cJs", d, ev);

        System.out.println(board.endResult(other, ev));
        System.out.println(other.endResult(board, ev));

        System.out.println(boardAlt.endResult(other, ev));
        System.out.println(other.endResult(boardAlt, ev));
    }
}
