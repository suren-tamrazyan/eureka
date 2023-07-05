package solver.ofc.scoring;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class DbService {
	private volatile Connection con;
	public Connection con() throws Exception {
		if (con == null || !con.isValid(0)) {
			con = DriverManager.getConnection("jdbc:mysql://144.76.194.210:3306/ofc_solver_scoring?useUnicode=true&characterEncoding=utf-8&serverTimezone=Europe/Moscow", "shelluser", "l7knu7EX3DFM12");
			con.setAutoCommit(true);
			con.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
		}
		return con;
	}
	
	public void newEstimate(long id, String name) throws Exception {
		try (PreparedStatement stmt = con().prepareStatement("insert into estimation (estimationId, estimationName, estimationDate) values (?, ?, ?)");) {
			stmt.setLong(1, id);
			stmt.setString(2, name);
			stmt.setTimestamp(3, new java.sql.Timestamp(id));
			stmt.executeUpdate();
		}
	}

	public void newHandEstimation(long estid, String handId, double valueAI, double valueSolverAvg, int repeatCount, int distinctSolutionsCount, long timeMsAvg, int valueAIByBoard, double valueSolverAvgByBoard, int scoreFromHH, int fantasyAICount, int fantasySolverCount) throws Exception {
		try (PreparedStatement stmt = con().prepareStatement("insert into hand_estimation (handId, estimationId, valueAI, valueSolverAvg, repeatCount, distinctSolutionsCount, timeMsAvg, valueAIByBoard, valueSolverAvgByBoard, scoreFromHH, fantasyAICount, fantasySolverCount) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");) {
			stmt.setString(1, handId);
			stmt.setLong(2, estid);
			stmt.setDouble(3, valueAI);
			stmt.setDouble(4, valueSolverAvg);
			stmt.setInt(5, repeatCount);
			stmt.setInt(6, distinctSolutionsCount);
			stmt.setLong(7, timeMsAvg);
			stmt.setInt(8, valueAIByBoard);
			stmt.setDouble(9, valueSolverAvgByBoard);
			stmt.setInt(10, scoreFromHH);
			stmt.setInt(11, fantasyAICount);
			stmt.setInt(12, fantasySolverCount);
			stmt.executeUpdate();
		}
	}

	public void newHandEstimationExample(long estid, String handId, int num, double valueAI, double valueSolver, long timeMs, String solutionSolver, String solutionAI, String[] aiRounds, String[] solverRounds, int valueAIByBoard, int valueSolverByBoard, boolean fantasyAI, boolean fantasySolver) throws Exception {
		try (PreparedStatement stmt = con().prepareStatement("insert into hand_estimation_example (handId, estimationId, exampleNumber, valueAI, valueSolver, timeMs, solutionSolver, solutionAI, valueAIByBoard, valueSolverByBoard, fantasyAI, fantasySolver) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");) {
			stmt.setString(1, handId);
			stmt.setLong(2, estid);
			stmt.setInt(3, num);
			stmt.setDouble(4, valueAI);
			stmt.setDouble(5, valueSolver);
			stmt.setLong(6, timeMs);
			stmt.setString(7, solutionSolver);
			stmt.setString(8, solutionAI);
			stmt.setInt(9, valueAIByBoard);
			stmt.setInt(10, valueSolverByBoard);
			stmt.setBoolean(11, fantasyAI);
			stmt.setBoolean(12, fantasySolver);
			stmt.executeUpdate();
		}
		try (PreparedStatement stmt = con().prepareStatement("insert into hand_estimation_example_move (handId, estimationId, exampleNumber, round, moveAI, moveSolver) values (?, ?, ?, ?, ?, ?)");) {
			for (int round = 0; round < 5; round++) {
				stmt.setString(1, handId);
				stmt.setLong(2, estid);
				stmt.setInt(3, num);
				stmt.setInt(4, round + 1);
				stmt.setString(5, aiRounds[round]);
				stmt.setString(6, solverRounds[round]);
				stmt.executeUpdate();
			}
		}
	}
	
	public void updateEstimationStats(long estid, double totalValueAI, double totalValueAvg, double totalValueAbs, int handCount, long totalTimeMs, double robustness, int totalValueAIByBoard, double totalValueAvgByBoard, int totalValueAbsByBoard, int totalScoreFromHH, int fantasyAICount, int fantasySolverCount) throws Exception {
		try (PreparedStatement stmt = con().prepareStatement("update estimation set totalValueAI = ?, totalValueAvg = ?, totalValueAbs = ?, handCount = ?, totalTimeMs = ?, robustness = ?, totalValueAIByBoard = ?, totalValueAvgByBoard = ?, totalValueAbsByBoard = ?, totalScoreFromHH = ?, fantasyAICount = ?, fantasySolverCount = ? where estimationId = ?");) {
			stmt.setDouble(1, totalValueAI);
			stmt.setDouble(2, totalValueAvg);
			stmt.setDouble(3, totalValueAbs);
			stmt.setInt(4, handCount);
			stmt.setLong(5, totalTimeMs);
			stmt.setDouble(6, robustness);
			stmt.setInt(7, totalValueAIByBoard);
			stmt.setDouble(8, totalValueAvgByBoard);
			stmt.setInt(9, totalValueAbsByBoard);
			stmt.setInt(10, totalScoreFromHH);
			stmt.setInt(11, fantasyAICount);
			stmt.setInt(12, fantasySolverCount);
			stmt.setLong(13, estid);
			stmt.executeUpdate();
		}
	}
	public void newFit(long id, String name) throws Exception {
		try (PreparedStatement stmt = con().prepareStatement("insert into fit (fitId, fitName, fitDate) values (?, ?, ?)");) {
			stmt.setLong(1, id);
			stmt.setString(2, name);
			stmt.setTimestamp(3, new java.sql.Timestamp(id));
			stmt.executeUpdate();
		}
	}

	public void updateFitStats(long fitId, int cntRound1, int cntRound2, int cntRound3, int cntRound4, int cntRound5, int cntFantasy, int totalNonFan, int totalFantasy, double fracRound1, double fracRound2, double fracRound3, double fracRound4, double fracRound5, double fracFantasy, long totalTimeMs) throws Exception {
		try (PreparedStatement stmt = con().prepareStatement("update fit set cntRound1 = ?, cntRound2 = ?, cntRound3 = ?, cntRound4 = ?, cntRound5 = ?, cntFantasy = ?, totalNonFan = ?, totalFantasy = ?, fracRound1 = ?, fracRound2 = ?, fracRound3 = ?, fracRound4 = ?, fracRound5 = ?, fracFantasy = ?, totalTimeMs = ? where fitId = ?");) {
			stmt.setInt(1, cntRound1);
			stmt.setInt(2, cntRound2);
			stmt.setInt(3, cntRound3);
			stmt.setInt(4, cntRound4);
			stmt.setInt(5, cntRound5);
			stmt.setInt(6, cntFantasy);
			stmt.setInt(7, totalNonFan);
			stmt.setInt(8, totalFantasy);
			stmt.setDouble(9, fracRound1);
			stmt.setDouble(10, fracRound2);
			stmt.setDouble(11, fracRound3);
			stmt.setDouble(12, fracRound4);
			stmt.setDouble(13, fracRound5);
			stmt.setDouble(14, Double.isFinite(fracFantasy)?fracFantasy:0);
			stmt.setLong(15, totalTimeMs);
			stmt.setLong(16, fitId);
			stmt.executeUpdate();
		}
	}

	public void newFitMove(long fitId, String handId, int round, String moveAI, String moveSolver, boolean isEquals) throws Exception {
		try (PreparedStatement stmt = con().prepareStatement("insert into fit_move (fitId, handId, round, moveAI, moveSolver, isEquals) values (?, ?, ?, ?, ?, ?)");) {
			stmt.setLong(1, fitId);
			stmt.setString(2, handId);
			stmt.setInt(3, round);
			stmt.setString(4, moveAI);
			stmt.setString(5, moveSolver);
			stmt.setBoolean(6, isEquals);
			stmt.executeUpdate();
		}

	}
}
