package solver.ofc;

public class Config {
	public static boolean DEBUG_PRINT = false;
	public static int FAIL_PENALTY = -3;//0;
	public static boolean HEURISTIC_COMPLETE = true;
	public static int DEPTH_OF_SEARCH = 10;
	public static boolean DISTINCT_TREE = false; // on TRUE may be situation where completion not found isValid combination
	public static boolean INIT_HEURISTIC_PRUNING = true;
	public static boolean NATURE_ORDERED_MODE = true;
	public static boolean FANTASY_FAST_SOLVE = false; // FAST but not strict
	public static int FANTASY_SCORE = 15;//20//10;//15;
	public static int CPU_NUM = 4;

	public static boolean ESTIMATE_OPPONENTS = false;
	public static int OPP_RANDOM_DEAL_COUNT = 200;

	public int NUMBER_OF_ITERATIONS = 15000;
	public int RANDOM_DEAL_COUNT = 15000; // == NUMBER_OF_ITERATIONS
	public double EXPLORATION_PARAMETER = 7;//20;//2;//1.41;
	public long TIME_LIMIT_MS = 17000;
}
