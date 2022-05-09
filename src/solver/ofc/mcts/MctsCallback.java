package solver.ofc.mcts;

public interface MctsCallback {
	public void onEndSearch(MctsTreeNode<?, ?, ?> root);
	public void onIteration(int iterationsNum, long time, MctsTreeNode<?, ?, ?> root);
}
