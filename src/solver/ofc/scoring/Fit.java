package solver.ofc.scoring;

import game.EventOfc;
import game.Game;
import game.GameOfc;
import game.PlayerOfc;
import org.json.JSONArray;
import org.json.JSONObject;
import solver.ofc.Config;
import solver.ofc.Utils;
import solver.ofc.scoring.Estimator.GameFilter;
import util.Misc;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Fit {
    private long id;
    private String folder;
    private String name;
    //	private String description;
    private DbService dbService;
	private GameFilter gameFilter;


    private int[] cntRound = new int[5];
    private int cntFantasy = 0;
    private int totalNonFan = 0;
    private int totalFantasy = 0;
    private long totalTimeMs = 0;

    public Fit(String aFolder, String aName, GameFilter aGameFilter) throws Exception {
        System.out.println("get db connection");
        dbService = new DbService();
        this.id = Utils.getTime(); // simple id as time
        this.folder = aFolder;
        this.gameFilter = aGameFilter;
        this.name = String.format("%s (%s; %s; %s)", aName, folder, Utils.dateFormatIntel(this.id), this.gameFilter);
        System.out.println("dbService.newEstimate");
        dbService.newFit(this.id, this.name);
    }

    public void fit() throws Exception {
        try (Stream<Path> walk = Files.walk(Paths.get(folder))) {
            List<String> files = walk.filter(Files::isRegularFile).map(Path::toString).collect(Collectors.toList());
            for (String file : files) {
                fitHandFile(file);
            }
            double fracRound1 = (double)cntRound[0]*100.0/(double)totalNonFan;
            double fracRound2 = (double)cntRound[1]*100.0/(double)totalNonFan;
            double fracRound3 = (double)cntRound[2]*100.0/(double)totalNonFan;
            double fracRound4 = (double)cntRound[3]*100.0/(double)totalNonFan;
            double fracRound5 = (double)cntRound[4]*100.0/(double)totalNonFan;
            double fracFantasy = (double)cntFantasy*100.0/(double)totalFantasy;
            dbService.updateFitStats(this.id, cntRound[0], cntRound[1], cntRound[2], cntRound[3], cntRound[4], cntFantasy, totalNonFan, totalFantasy, fracRound1, fracRound2, fracRound3, fracRound4, fracRound5, fracFantasy, totalTimeMs);
            System.out.println(String.format("Fit is complete: Round1Count = %d, Round2Count = %d, Round3Count = %d, Round4Count = %d, Round5Count = %d, FantasyCount = %d, NonFantasyTotal = %d, FantasyTotal = %d, fracRound1 = %f, fracRound2 = %f, fracRound3 = %f, fracRound4 = %f, fracRound5 = %f, fracFantasy = %f, totalTime = %d", cntRound[0], cntRound[1], cntRound[2], cntRound[3], cntRound[4], cntFantasy, totalNonFan, totalFantasy, fracRound1, fracRound2, fracRound3, fracRound4, fracRound5, fracFantasy, totalTimeMs));
        }
    }

    public void fitHandFile(String filePath) throws Exception {
        System.out.println(filePath);
        JSONArray hhs = Estimator.fetchHHsFromFile(filePath);
        for (int i = 0; i < hhs.length(); i++)
            try {
                fitHand(hhs.getJSONObject(i));
            } catch (Exception e) {
                e.printStackTrace();
            }
        System.out.println();
    }

    public void fitHand(JSONObject hh) throws Exception {
        JSONObject handData = hh.getJSONObject("handData");
        String gameId = JSONObject.getNames(handData)[0];
        if (!hh.getBoolean("joined")) {
            System.out.println("not joined " + gameId);
            return;
        }
        boolean isSpartan = "Spartan".equals(hh.getString("appName"));
        JSONArray jaPlayers = handData.getJSONArray(gameId);
        List<PlayerHh> lstPlayers = new ArrayList<>();
        for (int i = 0; i < jaPlayers.length(); i++)
            lstPlayers.add(new PlayerHh(jaPlayers.getJSONObject(i), isSpartan));

        long heroCount = lstPlayers.stream().filter(PlayerHh::isHero).count();
        if (heroCount != 1) {
            System.out.println(Misc.sf("hero count = %d", heroCount));
            return;
        }

        boolean isOurFantasy = lstPlayers.stream().filter(PlayerHh::isHero).findAny().get().isInFantasy();
		if (gameFilter == GameFilter.ONLY_FANTASY && !isOurFantasy){
			System.out.println("skip by filter:" + gameId);
			return;
		}
		if (gameFilter == GameFilter.WITHOUT_FANTASY && isOurFantasy){
			System.out.println("skip by filter:" + gameId);
			return;
		}

        System.out.println("Fit " + gameId);

        Collections.sort(lstPlayers);

        GameOfc game = new GameOfc(Game.Nw.Ppp, 1);
        game.id = gameId;
        String dealerName = "";
        for (PlayerHh pl : lstPlayers) {
//            if (pl.getOrderIndex() == 0)
//                dealerName = pl.getPid();
            if (pl.isHero())
                game.heroName = pl.getPid();
            game.addPlayer(new PlayerOfc(pl.getPid(), 1, pl.isInFantasy()));
        }
        dealerName = lstPlayers.get(lstPlayers.size() - 1).getPid();
        game.initButtonName(dealerName);
        game.gameMode = Estimator.str2GameMode(hh.getString("rules"));
        game.setSkipCheckFLCardCount();
        game.setAllowEmptyDeadBox();

        System.out.println("gameAI");
        // gameAI
        long timeBefore = Utils.getTime();
//        EventOfc[] aiRounds = new EventOfc[5];
        for (int round = 0; round < 5; round++) {
            for (PlayerHh pl : lstPlayers) {
                List<EventOfc> evs = pl.getEvents(round, true);
                for (EventOfc ev : evs) {
                    if (pl.isHero() && (ev.type == EventOfc.PUT_CARDS_TO_BOXES || ev.type == EventOfc.FANTASY_CARDS_TO_BOXES)) {
//                        aiRounds[round] = ev;
                        EventOfc solverMove = Estimator.bestMoveMctsSimple(game);
//                        EventOfc solverMove = Estimator.bestMoveMctsHard(game);
                        boolean isEquals = false;
                        if (ev.equalCardBoxes(solverMove)) {
                            if (pl.isInFantasy())
                                cntFantasy++;
                            else
                                cntRound[round]++;
                            isEquals = true;
                        }
                        String strEv = ev.toString();
                        String strSolverMove = solverMove.toString();
                        int dbRound = pl.isInFantasy() ? 0 : round+1;
                        dbService.newFitMove(id, gameId, dbRound, strEv, strSolverMove, isEquals);
                        System.out.println(Misc.sf("Round %d", dbRound));
                        if (isEquals)
                        	System.out.println("******* EQUALS *******");
                        System.out.println("AI move: " + strEv);
                        System.out.println("Solver move: " + strSolverMove);
                        System.out.println();
                    }
                    game.procEvent(ev);
                }
            }
        }
        if (isOurFantasy)
            totalFantasy++;
        else
            totalNonFan++;
        totalTimeMs += ( Utils.getTime() - timeBefore);
        for (PlayerHh pl : lstPlayers) {
            List<EventOfc> evsDeads = pl.getShowDeadsEvent();
            for (EventOfc ev : evsDeads)
                game.procEvent(ev);
        }

        System.out.println(game);
        System.out.println();
        System.out.println();

    }

    public static void main(String[] args) throws Exception {
    	String path = args[0];
    	String evalMethod = args[1];
    	String oppDealCount = args[2];
    	String depthSearch = args[3];
    	String fantasyScore = args[4];
    	String failPenalty = args[5];
        Config.EvaluationMethod = Config.EvaluationMethodKind.valueOf(evalMethod);
        Config.OPP_RANDOM_DEAL_COUNT = Integer.parseInt(oppDealCount);
        Config.DEPTH_OF_SEARCH = Integer.parseInt(depthSearch);
        Config.FANTASY_SCORE = Integer.parseInt(fantasyScore);
        Config.FAIL_PENALTY = Integer.parseInt(failPenalty);
        String name = String.format("%s; OPP_RANDOM_DEAL_COUNT = %d; DEPTH_OF_SEARCH = %d; FANTASY_SCORE = %d; FAIL_PENALTY = %d", Config.EvaluationMethod, Config.OPP_RANDOM_DEAL_COUNT, Config.DEPTH_OF_SEARCH, Config.FANTASY_SCORE, Config.FAIL_PENALTY);

        Fit fit = new Fit(path, name, GameFilter.ALL);
        fit.fit();
    }
}
