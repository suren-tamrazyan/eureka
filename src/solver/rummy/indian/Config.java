package solver.rummy.indian;

public class Config {
    public enum Goal {COMPLETABLE_DISTANCE, MIN_LEAF_VALUE, SUM_MIN_VALUE_OF_PATH, SUM_DELTA_MIN_VALUE_OF_PATH}
    public static double EXPLORATION_PARAMETER = 1000;//1.8;//1.41;
    public static int NUMBER_OF_ITERATIONS = 10000;
    public static Goal GOAL = Goal.SUM_MIN_VALUE_OF_PATH;
    public static int DEPTH_OF_SEARCH = 4;
}
