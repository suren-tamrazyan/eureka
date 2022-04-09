package solver.ofc.scoring;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import game.EventOfc;
import game.Game.Nw;
import game.GameOfc;
import game.GameOfc.GameMode;
import solver.ofc.EurekaRunner;
import solver.ofc.EvaluatorFacade;
import solver.ofc.Heuristics;
import solver.ofc.Utils;
import game.PlayerOfc;

public class Estimator {
	public enum GameFilter {ONLY_FANTASY, WITHOUT_FANTASY, ALL};
	private final int REPEAT_COUNT = 1;
	
	private long id;
	private String folder;
	private String name;
//	private String description;
	private DbService dbService;
	private GameFilter gameFilter;
	
	private int handCount = 0;
	private double totalValueAI = 0;
	private double totalValueAvg = 0;
	private double totalValueAbs = 0;
	private long totalTime = 0;
	private int distinctSolutionsCount = 0;

	public Estimator(String aFolder, String aName, GameFilter aGameFilter) throws Exception {
		System.out.println("get db connection");
		dbService = new DbService();
		this.id = Utils.getTime(); // simple id as time
		this.folder = aFolder;
		this.gameFilter = aGameFilter;
		this.name = String.format("%s (%s; %s; %s)", aName, folder, Utils.dateFormatIntel(this.id), gameFilter);
//		this.description= description;
		System.out.println("dbService.newEstimate");
		dbService.newEstimate(this.id, this.name);
	}
	
	public void estimate() throws Exception {
		try (Stream<Path> walk = Files.walk(Paths.get(folder))) {
			List<String> files = walk.filter(Files::isRegularFile).map(Path::toString).collect(Collectors.toList());
			for (String file : files) {
				estimateHandFile(file);
			}
			dbService.updateEstimationStats(this.id, this.totalValueAI, this.totalValueAvg, this.totalValueAbs, this.handCount, this.totalTime, (double)this.distinctSolutionsCount/(double)this.handCount);
			System.out.println(String.format("Estimation is complete: totalValueAI = %f, totalValueAvg = %f, totalValueAbs = %f, handCount = %d, totalTime = %d, robustness (from 1 to %d) = %f", this.totalValueAI, this.totalValueAvg, this.totalValueAbs, this.handCount, this.totalTime, REPEAT_COUNT, (double)this.distinctSolutionsCount/(double)this.handCount));
		}
	}
	
	public void estimateHandFile(String filePath) throws Exception {
		System.out.println(filePath);
		JSONArray hhs = fetchHHsFromFile(filePath);
		for (int i = 0; i < hhs.length(); i++)
			try {
				estimateHand(hhs.getJSONObject(i));
			} catch (Exception e) {
				e.printStackTrace();
			}
		System.out.println();
	}
	
	public JSONArray fetchHHsFromFile(String filename) throws Exception {
		ArrayList<String> lst = new ArrayList<String>();
		try (BufferedReader br = new BufferedReader(new FileReader(filename))){
			String str;
			while ((str = br.readLine()) != null) {
				if (!str.isEmpty())
					lst.add(str);
			}
		}
		lst.set(0, "[" + lst.get(0));
		for (int i = 0; i < lst.size(); i++) {
			if ("}".equals(lst.get(i)))
				lst.set(i, "},");
		}
		lst.add("]");
		JSONArray result = new JSONArray(String.join("\n", lst));
		System.out.println(String.format("Parsed %d HHs", result.length()));
		return result;
	}
	
	public void estimateHand(JSONObject hh) throws Exception {
		JSONObject handData = hh.getJSONObject("handData");
		String gameId = JSONObject.getNames(handData)[0];
		if (!hh.getBoolean("joined")) {
			System.out.println("not joined " + gameId);
			return;
		}
		JSONArray jaPlayers = handData.getJSONArray(gameId);
		List<PlayerHh> lstPlayers = new ArrayList<>();
		for (int i = 0; i < jaPlayers.length(); i++)
			lstPlayers.add(new PlayerHh(jaPlayers.getJSONObject(i)));
		
		boolean isOurFantasy = lstPlayers.stream().filter(PlayerHh::isHero).findAny().get().isInFantasy();
		if (gameFilter == GameFilter.ONLY_FANTASY && !isOurFantasy){
			System.out.println("skip by filter:" + gameId);
			return;
		}
		if (gameFilter == GameFilter.WITHOUT_FANTASY && isOurFantasy){
			System.out.println("skip by filter:" + gameId);
			return;
		}
		
		System.out.println("Estimation " + gameId);
		
		GameOfc gameAI = new GameOfc(Nw.Ppp, 1);
		GameOfc gameSolverBase = new GameOfc(Nw.Ppp, 1);
		List<GameOfc> lstGames = Stream.of(gameAI, gameSolverBase).collect(Collectors.toList());
		for (GameOfc game : lstGames) {
			game.id = gameId;
			String dealerName = "";
			for (PlayerHh pl : lstPlayers) {
				if (pl.getOrderIndex() == 0)
					dealerName = pl.getPid();
				if (pl.isHero())
					game.heroName = pl.getPid();
				game.addPlayer(new PlayerOfc(pl.getPid(), 1, pl.isInFantasy()));
			}
			game.initButtonName(dealerName);
			game.gameMode = str2GameMode(hh.getString("rules"));
			game.setSkipCheckFLCardCount();
			game.setAllowEmptyDeadBox();
		}
		
		Collections.sort(lstPlayers);
		
		System.out.println("gameAI");
		// gameAI
		for (int round = 0; round < 5; round++) {
			for (PlayerHh pl : lstPlayers) {
				List<EventOfc> evs = pl.getEvents(round, true);
				for (EventOfc ev : evs)
					gameAI.procEvent(ev);
			}
		}
		for (PlayerHh pl : lstPlayers) {
			List<EventOfc> evsDeads = pl.getShowDeadsEvent();
			for (EventOfc ev : evsDeads)
				gameAI.procEvent(ev);
		}
		System.out.println(gameAI);
		PlayerOfc heroAI = gameAI.getPlayer(gameAI.heroName);
		double valueAI = EvaluatorFacade.evaluate(heroAI.boxFront.toList(), heroAI.boxMiddle.toList(), heroAI.boxBack.toList(), isOurFantasy);
		System.out.println(String.format("Estimation of AI: %f", valueAI));
		
		System.out.println();
		
		double handValueSolverSum = 0;
		Set<String> distinctSolutions = new HashSet<>();
		long timeHandTimeSum = 0;
		for (int num = 0; num < REPEAT_COUNT; num++) {
			System.out.println("gameSolver " + num);
			// gameSolver
			GameOfc gameSolver = gameSolverBase.clone();
			long timeBefore = Utils.getTime();
			for (int round = 0; round < 5; round++) {
				for (PlayerHh pl : lstPlayers) {
					List<EventOfc> evs = pl.getEvents(round, false);
					for (EventOfc ev : evs)
						gameSolver.procEvent(ev);
					if (pl.isHero() && !evs.isEmpty()) {
						EventOfc solverMove = bestMoveMctsSimple(gameSolver);
						gameSolver.procEvent(solverMove);
					}
				}
			}
			long timeExecExample = Utils.getTime() - timeBefore;
			timeHandTimeSum += timeExecExample;
			for (PlayerHh pl : lstPlayers) {
				List<EventOfc> evsDeads = pl.getShowDeadsEvent();
				for (EventOfc ev : evsDeads)
					gameSolver.procEvent(ev);
			}
			PlayerOfc heroSolver = gameSolver.getPlayer(gameSolver.heroName);
			double valueSolver = EvaluatorFacade.evaluate(heroSolver.boxFront.toList(), heroSolver.boxMiddle.toList(), heroSolver.boxBack.toList(), isOurFantasy);
			handValueSolverSum += valueSolver;
			String strSolution = heroSolver.toString();
			distinctSolutions.add(strSolution);
			System.out.println(strSolution);
			System.out.println(String.format("Estimation of Solver %d: %f", num, valueSolver));
			dbService.newHandEstimationExample(this.id, gameId, num, valueAI, valueSolver, timeExecExample, strSolution, gameAI.getPlayer(gameAI.heroName).toString());
		}
		dbService.newHandEstimation(this.id, gameId, valueAI, handValueSolverSum/REPEAT_COUNT, REPEAT_COUNT, distinctSolutions.size(), timeHandTimeSum/REPEAT_COUNT);
		
		handCount++;
		totalValueAI += valueAI;
		totalValueAvg += handValueSolverSum/REPEAT_COUNT;
		totalValueAbs += handValueSolverSum;
		totalTime += timeHandTimeSum;
		distinctSolutionsCount += distinctSolutions.size();
		
		System.out.println();
		System.out.println();
	}
	
	public static GameOfc.GameMode str2GameMode(String gameMode) {
		switch (gameMode) {
		case "classic":
			return GameMode.GAME_MODE_REGULAR;
		case "progressive16_refant14_nojokers":
			return GameMode.GAME_MODE_OFC_PROGRESSIVE;
		case "progressive17_nojokers":
			return GameMode.GAME_MODE_OFC_ULTIMATE;
		case "progressive16_refant14_jokers":
			return GameMode.GAME_MODE_OFC_WILD_CARD_PROGRESSIVE;
		case "progressive17_jokers":
			return GameMode.GAME_MODE_OFC_WILD_CARD_ULTIMATE;
		default:
			return GameMode.GAME_MODE_REGULAR;
		}
	}

	private EventOfc bestMoveMctsSimple(GameOfc game) throws Exception {
		PlayerOfc hero = game.getPlayer(game.heroName);
		if (hero.playFantasy)
			return Heuristics.fantasyCompletion(hero.boxFront.toList(), hero.boxMiddle.toList(), hero.boxBack.toList(), hero.cardsToBeBoxed, 85000).toEventOfc(game.heroName);
		
    	long timeBefore = Utils.getTime();
//    	EventOfc result = EurekaRunner.run(game);
    	EventOfc result = EurekaRunner.run(game, delay(game.getRound()), 17000);
    	System.out.println(String.format("MCTS decision in %d ms: \n%s", Utils.getTime() - timeBefore, result.toString()));

    	return result;
	}
	
	private long delay(int round) {
		switch (round) {
		case 1: return 9000;
		case 2: return 5000;
		case 3: return 5000;
		case 4: return 4000;
		case 5: return 2000;
		default:
			return 0;
		}
	}

	public static void main(String[] args) throws Exception {
		Estimator estimator = new Estimator("C:\\ofc_mcts_scoring\\hh_nojokers2", "baseline3; FANTASY_SCORE = 17", GameFilter.WITHOUT_FANTASY);
		estimator.estimate();
	}

}
