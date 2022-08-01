package solver.ofc.scoring;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class DbService {
	private volatile Connection con;
	public DbService() throws Exception {
		con = DriverManager.getConnection("jdbc:mysql://144.76.194.210:3306/ofc_solver_scoring?useUnicode=true&characterEncoding=utf-8&serverTimezone=Europe/Moscow", "shelluser", "l7knu7EX3DFM12");
		con.setAutoCommit(true);
		con.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
	}
	
	public void newEstimate(long id, String name) throws Exception {
		try (PreparedStatement stmt = con.prepareStatement("insert into estimation (estimationId, estimationName, estimationDate) values (?, ?, ?)");) {
			stmt.setLong(1, id);
			stmt.setString(2, name);
			stmt.setTimestamp(3, new java.sql.Timestamp(id));
			stmt.executeUpdate();
		}
	}

	public void newHandEstimation(long estid, String handId, double valueAI, double valueSolverAvg, int repeatCount, int distinctSolutionsCount, long timeMsAvg, int valueAIByBoard, double valueSolverAvgByBoard, int scoreFromHH) throws Exception {
		try (PreparedStatement stmt = con.prepareStatement("insert into hand_estimation (handId, estimationId, valueAI, valueSolverAvg, repeatCount, distinctSolutionsCount, timeMsAvg, valueAIByBoard, valueSolverAvgByBoard, scoreFromHH) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");) {
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
			stmt.executeUpdate();
		}
	}

	public void newHandEstimationExample(long estid, String handId, int num, double valueAI, double valueSolver, long timeMs, String solutionSolver, String solutionAI, String[] aiRounds, String[] solverRounds, int valueAIByBoard, int valueSolverByBoard) throws Exception {
		try (PreparedStatement stmt = con.prepareStatement("insert into hand_estimation_example (handId, estimationId, exampleNumber, valueAI, valueSolver, timeMs, solutionSolver, solutionAI, valueAIByBoard, valueSolverByBoard) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");) {
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
			stmt.executeUpdate();
		}
		try (PreparedStatement stmt = con.prepareStatement("insert into hand_estimation_example_move (handId, estimationId, exampleNumber, round, moveAI, moveSolver) values (?, ?, ?, ?, ?, ?)");) {
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
	
	public void updateEstimationStats(long estid, double totalValueAI, double totalValueAvg, double totalValueAbs, int handCount, long totalTimeMs, double robustness, int totalValueAIByBoard, double totalValueAvgByBoard, int totalValueAbsByBoard, int totalScoreFromHH) throws Exception {
		try (PreparedStatement stmt = con.prepareStatement("update estimation set totalValueAI = ?, totalValueAvg = ?, totalValueAbs = ?, handCount = ?, totalTimeMs = ?, robustness = ?, totalValueAIByBoard = ?, totalValueAvgByBoard = ?, totalValueAbsByBoard = ?, totalScoreFromHH = ? where estimationId = ?");) {
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
			stmt.setLong(11, estid);
			stmt.executeUpdate();
		}
	}
}
