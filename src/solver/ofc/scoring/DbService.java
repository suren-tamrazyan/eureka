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

	public void newHandEstimation(long estid, String handId, double valueAI, double valueSolverAvg, int repeatCount, int distinctSolutionsCount, long timeMsAvg) throws Exception {
		try (PreparedStatement stmt = con.prepareStatement("insert into hand_estimation (handId, estimationId, valueAI, valueSolverAvg, repeatCount, distinctSolutionsCount, timeMsAvg) values (?, ?, ?, ?, ?, ?, ?)");) {
			stmt.setString(1, handId);
			stmt.setLong(2, estid);
			stmt.setDouble(3, valueAI);
			stmt.setDouble(4, valueSolverAvg);
			stmt.setInt(5, repeatCount);
			stmt.setInt(6, distinctSolutionsCount);
			stmt.setLong(7, timeMsAvg);
			stmt.executeUpdate();
		}
	}

	public void newHandEstimationExample(long estid, String handId, int num, double valueAI, double valueSolver, long timeMs, String solutionSolver, String solutionAI) throws Exception {
		try (PreparedStatement stmt = con.prepareStatement("insert into hand_estimation_example (handId, estimationId, exampleNumber, valueAI, valueSolver, timeMs, solutionSolver, solutionAI) values (?, ?, ?, ?, ?, ?, ?, ?)");) {
			stmt.setString(1, handId);
			stmt.setLong(2, estid);
			stmt.setInt(3, num);
			stmt.setDouble(4, valueAI);
			stmt.setDouble(5, valueSolver);
			stmt.setLong(6, timeMs);
			stmt.setString(7, solutionSolver);
			stmt.setString(8, solutionAI);
			stmt.executeUpdate();
		}
	}
	
	public void updateEstimationStats(long estid, double totalValueAI, double totalValueAvg, double totalValueAbs, int handCount, long totalTimeMs, double robustness) throws Exception {
		try (PreparedStatement stmt = con.prepareStatement("update estimation set totalValueAI = ?, totalValueAvg = ?, totalValueAbs = ?, handCount = ?, totalTimeMs = ?, robustness = ? where estimationId = ?");) {
			stmt.setDouble(1, totalValueAI);
			stmt.setDouble(2, totalValueAvg);
			stmt.setDouble(3, totalValueAbs);
			stmt.setInt(4, handCount);
			stmt.setLong(5, totalTimeMs);
			stmt.setDouble(6, robustness);
			stmt.setLong(7, estid);
			stmt.executeUpdate();
		}
	}
}
