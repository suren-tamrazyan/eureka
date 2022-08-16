package solver.ofc.scoring;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import game.Card;
import game.EventOfc;
import game.GameException;
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
	private int totalValueAIByBoard = 0;
	private double totalValueAvgByBoard = 0;
	private int totalValueAbsByBoard = 0;
	private int totalScoreFromHH = 0;
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
			dbService.updateEstimationStats(this.id, this.totalValueAI, this.totalValueAvg, this.totalValueAbs, this.handCount, this.totalTime, (double)this.distinctSolutionsCount/(double)this.handCount, this.totalValueAIByBoard, this.totalValueAvgByBoard, this.totalValueAbsByBoard, totalScoreFromHH);
			System.out.println(String.format("Estimation is complete: totalValueAI = %f, totalValueAvg = %f, totalValueAbs = %f, handCount = %d, totalTime = %d, robustness (from 1 to %d) = %f, totalValueAIByBoard = %d, totalValueAvgByBoard = %f, totalValueAbsByBoard = %d", this.totalValueAI, this.totalValueAvg, this.totalValueAbs, this.handCount, this.totalTime, REPEAT_COUNT, (double)this.distinctSolutionsCount/(double)this.handCount, this.totalValueAIByBoard, this.totalValueAvgByBoard, this.totalValueAbsByBoard));
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
		String[] aiRounds = new String[5];
		for (int round = 0; round < 5; round++) {
			for (PlayerHh pl : lstPlayers) {
				List<EventOfc> evs = pl.getEvents(round, true);
				for (EventOfc ev : evs) {
					gameAI.procEvent(ev);
					if (pl.isHero() && (ev.type == EventOfc.PUT_CARDS_TO_BOXES || ev.type == EventOfc.FANTASY_CARDS_TO_BOXES))
						aiRounds[round] = ev.toString();
				}
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
		int valueAIByBoard = EvaluatorFacade.evaluateGame(gameAI, gameAI.heroName, 0); //EvaluatorFacade.evaluateByBoard(gameAI, gameAI.heroName);
		int scoreFromHH = lstPlayers.stream().filter(PlayerHh::isHero).findFirst().get().getScore();
		System.out.println(String.format("Estimation of AI: %f; by board: %d; from HH: %d", valueAI, valueAIByBoard, scoreFromHH));
		
		System.out.println();
		
		double handValueSolverSum = 0;
		int handValueSolverByBoardSum = 0;
		Set<String> distinctSolutions = new HashSet<>();
		long timeHandTimeSum = 0;
		for (int num = 0; num < REPEAT_COUNT; num++) {
			System.out.println("gameSolver " + num);
			// gameSolver
			GameOfc gameSolver = gameSolverBase.clone();
			long timeBefore = Utils.getTime();
			String[] solverRounds = new String[5];
			for (int round = 0; round < 5; round++) {
				for (PlayerHh pl : lstPlayers) {
					List<EventOfc> evs = pl.getEvents(round, false);
					for (EventOfc ev : evs)
						gameSolver.procEvent(ev);
					if (pl.isHero() && !evs.isEmpty()) {
						EventOfc solverMove = bestMoveMctsSimple(gameSolver);
						gameSolver.procEvent(solverMove);
						solverRounds[round] = solverMove.toString();
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
			int valueSolverByBoard = EvaluatorFacade.evaluateGame(gameSolver, gameSolver.heroName, 0); // EvaluatorFacade.evaluateByBoard(gameSolver, gameSolver.heroName);
			handValueSolverSum += valueSolver;
			handValueSolverByBoardSum += valueSolverByBoard;
			String strSolution = heroSolver.toString();
			distinctSolutions.add(strSolution);
			System.out.println(strSolution);
			System.out.println(String.format("Estimation of Solver %d: %f; by board: %d", num, valueSolver, valueSolverByBoard));
			dbService.newHandEstimationExample(this.id, gameId, num, valueAI, valueSolver, timeExecExample, strSolution, gameAI.getPlayer(gameAI.heroName).toString(), aiRounds, solverRounds, valueAIByBoard, valueSolverByBoard);
		}
		dbService.newHandEstimation(this.id, gameId, valueAI, handValueSolverSum/REPEAT_COUNT, REPEAT_COUNT, distinctSolutions.size(), timeHandTimeSum/REPEAT_COUNT, valueAIByBoard, handValueSolverByBoardSum/REPEAT_COUNT, scoreFromHH);
		
		handCount++;
		totalValueAI += valueAI;
		totalValueAvg += handValueSolverSum/REPEAT_COUNT;
		totalValueAbs += handValueSolverSum;
		totalValueAIByBoard += valueAIByBoard;
		totalValueAvgByBoard += handValueSolverByBoardSum/REPEAT_COUNT;
		totalValueAbsByBoard += handValueSolverByBoardSum;
		totalScoreFromHH += scoreFromHH;
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

	private String httpRequestGet(String params) throws Exception {
		String url = "http://10.211.59.133:8089";
		
		url += params;
        HttpURLConnection httpClient = (HttpURLConnection) new URL(url).openConnection();
        httpClient.setRequestMethod("GET");
        httpClient.setRequestProperty("Connection", "close");
       	System.out.println("*** http (GET) request: " + url);
        int responseCode = httpClient.getResponseCode();
        if (responseCode >= 400)
        	throw new Exception(String.format("Bad AI response (status %d) (%s)", responseCode, url));
        try (BufferedReader in = new BufferedReader(new InputStreamReader(httpClient.getInputStream()))) {
            StringBuilder response = new StringBuilder();
            String line;
            boolean isFirst = true;
            while ((line = in.readLine()) != null) {
            	if (isFirst)
            		response.append(line);
            	else
            		response.append("\n"+line);
                isFirst = false;
            }
            String result = response.toString();
           	System.out.println("*** http (GET) response: " + result);
            return result;
        }
	}

	private boolean unknownCoefSended = false;
	private String makeMainParams(GameOfc game, String tableName, boolean includeButton, boolean expandFantasy) throws UnsupportedEncodingException {
		String paramStakes = "stakes=" + URLEncoder.encode(String.format("%.2f", (double)100/100.0), "UTF-8");
		String paramClubId = "clubId=123";
		String curr = "1USD";
		String paramPrice = "price=" + URLEncoder.encode(curr, "UTF-8");
		
		String paramHero = "hero=";
		String paramNewCard = "newCards=";
		String paramOpp = "opp=";
		String paramDead = "dead=";
		String paramButton = "button=";
		String paramTable = "table=" + URLEncoder.encode(tableName, "UTF-8");
		String paramGameMode = "rules=" + "classic";
		String paramAccount = "account=" + URLEncoder.encode(game.heroName, "UTF-8");
		String paramAppName = "appName=" + "Ppp";
		String paramGameId = "gameId=" + URLEncoder.encode(game.id, "UTF-8");
		String leftPlayer = "", rightPlayer = "";
		boolean afterHero = game.getPlayerInd(game.heroName) == game.players.length - 1;
		for (PlayerOfc p : game.getPlayers()) {
			String strFront = p.boxFront.toList().stream().map(Object::toString).collect(Collectors.joining(" ")).replaceAll("[X].", "??");
			String strMidlle = p.boxMiddle.toList().stream().map(Object::toString).collect(Collectors.joining(" ")).replaceAll("[X].", "??");
			String strBack = p.boxBack.toList().stream().map(Object::toString).collect(Collectors.joining(" ")).replaceAll("[X].", "??");
			if (p.isHero(game.heroName)) {
				paramHero += URLEncoder.encode(String.format("%s/%s/%s", strFront, strMidlle, strBack), "UTF-8");
				paramNewCard += URLEncoder.encode(p.cardsToBeBoxed.stream().map(Object::toString).collect(Collectors.joining(" ")).replaceAll("[X].", "??"), "UTF-8");
				paramDead += URLEncoder.encode(p.boxDead.toList().stream().map(Object::toString).collect(Collectors.joining(" ")).replaceAll("[X].", "??"), "UTF-8");
				afterHero = true;
			} else {
				String curOpp;
				if (p.playFantasy && !expandFantasy)
					curOpp = "fl" + (p.fantasyCardCount == -1 ? 14 : p.fantasyCardCount);
				else
					curOpp = URLEncoder.encode(String.format("%s/%s/%s", strFront, strMidlle, strBack), "UTF-8");
				if (afterHero)
					leftPlayer = curOpp;
				else
					rightPlayer = curOpp;
				afterHero = false;
			}
		}
		paramOpp += leftPlayer;
		if (!"".equals(rightPlayer)) 
			paramOpp += URLEncoder.encode("\t", "UTF-8") + rightPlayer;
		
		int indButton = game.getPlayerInd(game.buttonName);
		int indHero = game.getPlayerInd(game.heroName);
		if (indButton == indHero)
			paramButton += "0";
		else
			if (game.players.length == 2) {
				paramButton += "1";
			} else {
				if ((++indButton % game.players.length) == indHero)
					paramButton += "2";
				else
					paramButton += "1";
			}
		
		 
		return String.format("%s&%s&%s&%s&%s&%s&%s&%s&%s&%s&%s&%s&%s", paramHero, paramNewCard, paramOpp, paramDead, includeButton?paramButton:"", paramTable, paramGameMode, paramAccount, paramAppName, paramClubId, paramStakes, paramPrice, paramGameId);
	}

	private EventOfc bestMoveEurekaServer(GameOfc game) throws Exception {
		String tableName = "estimator"; int timeLimitSec = 18;
		String paramTimeLimit = "";
		if (timeLimitSec > 0)
			paramTimeLimit = String.format("&timeLimit=%d", timeLimitSec);
		String paramFastObvious = "";
		
		String request = String.format("/bestmove?%s%s%s", makeMainParams(game, tableName, true, false), paramTimeLimit, paramFastObvious);
		String response = httpRequestGet(request);
		boolean respFastObvious = false;
		String[] resps = response.split("/", -1);
		if (resps.length != 3)
			throw new GameException("wrong response format from AI");
		String strFront = resps[0].replaceAll("\\s", "");
		String strMiddle = resps[1].replaceAll("\\s", "");
		String strBack = resps[2].replaceAll("\\s", "");
		
		
		//AI sends the full state. must be subtract existing cards
		List<Card> lstFront = new ArrayList<Card>(Arrays.asList(Card.str2Cards(strFront)));
		List<Card> lstMiddle = new ArrayList<Card>(Arrays.asList(Card.str2Cards(strMiddle)));
		List<Card> lstBack = new ArrayList<Card>(Arrays.asList(Card.str2Cards(strBack)));
		lstFront.removeAll(game.getPlayer(game.heroName).boxFront.toList());
		lstMiddle.removeAll(game.getPlayer(game.heroName).boxMiddle.toList());
		lstBack.removeAll(game.getPlayer(game.heroName).boxBack.toList());
		strFront = lstFront.stream().map(Object::toString).collect(Collectors.joining(""));
		strMiddle = lstMiddle.stream().map(Object::toString).collect(Collectors.joining(""));
		strBack = lstBack.stream().map(Object::toString).collect(Collectors.joining(""));
		
		
		// detection dead cards
		List<Card> deads = new ArrayList<Card>(game.getPlayer(game.heroName).cardsToBeBoxed);
		deads.removeAll(Arrays.asList(Card.str2Cards(strFront + strMiddle + strBack)));
		
		int evType = EventOfc.PUT_CARDS_TO_BOXES;
		if (game.getPlayer(game.heroName).playFantasy)
			evType = EventOfc.FANTASY_CARDS_TO_BOXES;
		return new EventOfc(evType, game.heroName, Card.cards2Mask(Card.str2Cards(strFront)), Card.cards2Mask(Card.str2Cards(strMiddle)), Card.cards2Mask(Card.str2Cards(strBack)), deads);
	}

	public static void main(String[] args) throws Exception {
		Estimator estimator = new Estimator("C:\\ofc_mcts_scoring\\hh_nojokers2", "REFACTORING NATURE_SPACE baseline4; FANTASY_SCORE = 15; RANDOMIZED!!!; 3rd round MCS; FAIL_PENALTY = -3; view by board values", GameFilter.ALL);
		estimator.estimate();
	}

}
