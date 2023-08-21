package solver.ofc;

import game.*;
import solver.ofc.mcs.Mcs;
import solver.ofc.mcts.Mcts;
import util.Misc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OfcThirdRoundTest {
    public void testNotLikeAI16() throws Exception {

        GameOfc game = new GameOfc(Game.Nw.Upoker, 100);
        game.id = "21168314-10";
        game.addPlayer(new PlayerOfc("opp1", 1520));
        game.addPlayer(new PlayerOfc("hero", 1520));
        game.heroName = "hero";
        game.initButtonName(game.heroName);
        game.gameMode = GameOfc.GameMode.GAME_MODE_OFC_PROGRESSIVE;

        List<Card> emptyList = new ArrayList<>();

        game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, "opp1", Card.cards2Mask(Card.str2Cards("")), Card.cards2Mask(Card.str2Cards("3s9h")), Card.cards2Mask(Card.str2Cards("TdQdKd")), emptyList));
        game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("Ad2c7h5d5s"))));
        game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, game.heroName, Card.cards2Mask(Card.str2Cards("Ad")), Card.cards2Mask(Card.str2Cards("2c7h")), Card.cards2Mask(Card.str2Cards("5d5s")), emptyList));
        game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, "opp1", Card.cards2Mask(Card.str2Cards("Ks")), Card.cards2Mask(Card.str2Cards("")), Card.cards2Mask(Card.str2Cards("3d")), emptyList));
        game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("Ac4dQh"))));
        game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, game.heroName, Card.cards2Mask(Card.str2Cards("Ac")), Card.cards2Mask(Card.str2Cards("4d")), Card.cards2Mask(Card.str2Cards("")), new ArrayList<>(Arrays.asList(Card.str2Cards("Qh")))));
        game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, "opp1", Card.cards2Mask(Card.str2Cards("")), Card.cards2Mask(Card.str2Cards("3h")), Card.cards2Mask(Card.str2Cards("8d")), emptyList));
        game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("4cJhAs"))));
        game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, game.heroName, Card.cards2Mask(Card.str2Cards("")), Card.cards2Mask(Card.str2Cards("4c")), Card.cards2Mask(Card.str2Cards("Jh")), new ArrayList<>(Arrays.asList(Card.str2Cards("As")))));
        game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, "opp1", Card.cards2Mask(Card.str2Cards("Jc")), Card.cards2Mask(Card.str2Cards("2h")), 0, emptyList));
        game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("6c7c7d"))));

        System.out.println(game);

        Config.EvaluationMethod = Config.EvaluationMethodKind.BOARD_ACROSS;
        Config.DEPTH_OF_SEARCH = 10;
        Config.OPP_RANDOM_DEAL_COUNT = 100;
        Config.DEBUG_PRINT = true;
        Config.FANTASY_SCORE = 7;
        Config.FAIL_PENALTY = -1;

//		EventOfc result = EurekaRunner.run(game, 15000, 17000);
        Config cfg = new Config();
        NatureSpace natureSpace = new NatureSpaceExt(game, cfg);
        GameOfcMctsSimple stateSimple = new GameOfcMctsSimple(game, natureSpace);
        long timeBefore = Utils.getTime();
        EventOfcMctsSimple decision = Mcs.monteCarloSimulation(stateSimple, 0);

        System.out.println(String.format("MCS decision in %d ms: \n%s", Utils.getTime() - timeBefore, decision.toEventOfc(game.heroName).toString()));

    }


    public void testNotLikeAI100() throws Exception {

        GameOfc game = new GameOfc(Game.Nw.Upoker, 100);
        game.id = "21166694-190";
        game.addPlayer(new PlayerOfc("opp1", 1520));
        game.addPlayer(new PlayerOfc("hero", 1520));
        game.heroName = "hero";
        game.initButtonName("opp1");
        game.gameMode = GameOfc.GameMode.GAME_MODE_OFC_PROGRESSIVE;

        List<Card> emptyList = new ArrayList<>();

        game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("Ah3c4dThTc"))));
        game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, game.heroName, Card.cards2Mask(Card.str2Cards("Ah")), Card.cards2Mask(Card.str2Cards("3c4d")), Card.cards2Mask(Card.str2Cards("ThTc")), emptyList));
        game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, "opp1", Card.cards2Mask(Card.str2Cards("Ac")), Card.cards2Mask(Card.str2Cards("5h")), Card.cards2Mask(Card.str2Cards("TsJsKs")), emptyList));
        game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("3sTd6c"))));
        game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, game.heroName, Card.cards2Mask(Card.str2Cards("")), Card.cards2Mask(Card.str2Cards("3s")), Card.cards2Mask(Card.str2Cards("Td")), new ArrayList<>(Arrays.asList(Card.str2Cards("6c")))));
        game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, "opp1", Card.cards2Mask(Card.str2Cards("")), Card.cards2Mask(Card.str2Cards("8cKd")), Card.cards2Mask(Card.str2Cards("")), emptyList));
        game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("6dKcQs"))));

        System.out.println(game);

        Config.EvaluationMethod = Config.EvaluationMethodKind.SINGLE_HERO;
        Config.DEPTH_OF_SEARCH = 10;
        Config.OPP_RANDOM_DEAL_COUNT = 100;
        Config.DEBUG_PRINT = true;
        Config.FANTASY_SCORE = 7;
        Config.FAIL_PENALTY = -3;

        long timeBefore = Utils.getTime();
		EventOfc result = EurekaRunner.run(game, 0, 8000);
//        Config cfg = new Config();
//        NatureSpace natureSpace = new NatureSpaceExt(game, cfg);
//        GameOfcMctsSimple stateSimple = new GameOfcMctsSimple(game, natureSpace);
//        long timeBefore = Utils.getTime();
//        EventOfcMctsSimple decision = Mcs.monteCarloSimulation(stateSimple, 0);

        System.out.println(String.format("MCS decision in %d ms: \n%s", Utils.getTime() - timeBefore, result.toString()));

    }
    public void testNotLikeAI100MctsNotSimple() throws Exception {

        GameOfc game = new GameOfc(Game.Nw.Upoker, 100);
        game.id = "21166694-190";
        game.addPlayer(new PlayerOfc("opp1", 1520));
        game.addPlayer(new PlayerOfc("hero", 1520));
        game.heroName = "hero";
        game.initButtonName("opp1");
        game.gameMode = GameOfc.GameMode.GAME_MODE_OFC_PROGRESSIVE;

        List<Card> emptyList = new ArrayList<>();

        game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("Ah3c4dThTc"))));
        game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, game.heroName, Card.cards2Mask(Card.str2Cards("Ah")), Card.cards2Mask(Card.str2Cards("3c4d")), Card.cards2Mask(Card.str2Cards("ThTc")), emptyList));
        game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, "opp1", Card.cards2Mask(Card.str2Cards("Ac")), Card.cards2Mask(Card.str2Cards("5h")), Card.cards2Mask(Card.str2Cards("TsJsKs")), emptyList));
        game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("3sTd6c"))));
        game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, game.heroName, Card.cards2Mask(Card.str2Cards("")), Card.cards2Mask(Card.str2Cards("3s")), Card.cards2Mask(Card.str2Cards("Td")), new ArrayList<>(Arrays.asList(Card.str2Cards("6c")))));
        game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, "opp1", Card.cards2Mask(Card.str2Cards("")), Card.cards2Mask(Card.str2Cards("8cKd")), Card.cards2Mask(Card.str2Cards("")), emptyList));
        game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("6dKcQs"))));

        System.out.println(game);

        Config.EvaluationMethod = Config.EvaluationMethodKind.BOARD_ACROSS;
        Config.DEPTH_OF_SEARCH = 10;
        Config.OPP_RANDOM_DEAL_COUNT = 100;
        Config.DEBUG_PRINT = false;
        Config.FANTASY_SCORE = 7;
        Config.FAIL_PENALTY = -3;

        GameOfcMcts state = new GameOfcMcts(game);
        Mcts<GameOfcMcts, EventOfcMcts, AgentOfcMcts> mcts = Mcts.initializeIterations(20000, null, new DebugPrinter());
        mcts.dontClone(AgentOfcMcts.class);
        long timeBefore = Misc.getTime();
        EventOfcMcts decision = mcts.uctSearchWithExploration(state, 10, 50000, 60000);
        decision.setTime();
        System.out.println(Misc.sf("MCTS decision in %d ms: \n%s", Misc.getTime() - timeBefore, decision.toString()));
        System.out.println(Misc.sf("IterateCount = %d", mcts.getIterationsCount()));

    }

    public void testNotLikeAI101() throws Exception {

        GameOfc game = new GameOfc(Game.Nw.Upoker, 100);
        game.id = "21168314-2";
        game.addPlayer(new PlayerOfc("opp1", 1520));
        game.addPlayer(new PlayerOfc("hero", 1520));
        game.heroName = "hero";
        game.initButtonName("hero");
        game.gameMode = GameOfc.GameMode.GAME_MODE_OFC_PROGRESSIVE;

        List<Card> emptyList = new ArrayList<>();

        game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, "opp1", Card.cards2Mask(Card.str2Cards("")), Card.cards2Mask(Card.str2Cards("4s6c")), Card.cards2Mask(Card.str2Cards("7d9d9c")), emptyList));
        game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("TdTs4h5h8h"))));
        game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, game.heroName, Card.cards2Mask(Card.str2Cards("")), Card.cards2Mask(Card.str2Cards("TdTs")), Card.cards2Mask(Card.str2Cards("4h5h8h")), emptyList));
        game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, "opp1", Card.cards2Mask(Card.str2Cards("Jh")), Card.cards2Mask(Card.str2Cards("6s")), Card.cards2Mask(Card.str2Cards("")), emptyList));
        game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("Ks3hQd"))));
        game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, game.heroName, Card.cards2Mask(Card.str2Cards("Ks")), Card.cards2Mask(Card.str2Cards("")), Card.cards2Mask(Card.str2Cards("3h")), new ArrayList<>(Arrays.asList(Card.str2Cards("Qd")))));
        game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, "opp1", Card.cards2Mask(Card.str2Cards("Jd")), Card.cards2Mask(Card.str2Cards("4d")), Card.cards2Mask(Card.str2Cards("")), emptyList));
        game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("6hAd2h"))));

        System.out.println(game);

        Config.EvaluationMethod = Config.EvaluationMethodKind.SINGLE_HERO;
        Config.DEPTH_OF_SEARCH = 10;
        Config.OPP_RANDOM_DEAL_COUNT = 100;
        Config.DEBUG_PRINT = true;
        Config.FANTASY_SCORE = 7;
        Config.FAIL_PENALTY = -3;

        long timeBefore = Utils.getTime();
		EventOfc result = EurekaRunner.run(game, 15000, 17000);
//        Config cfg = new Config();
//        NatureSpace natureSpace = new NatureSpaceExt(game, cfg);
//        GameOfcMctsSimple stateSimple = new GameOfcMctsSimple(game, natureSpace);
//        long timeBefore = Utils.getTime();
//        EventOfcMctsSimple decision = Mcs.monteCarloSimulation(stateSimple, 0);

        System.out.println(String.format("MCS decision in %d ms: \n%s", Utils.getTime() - timeBefore, result.toString()));

    }

    public void testNotLikeAI101MctsNotSimple() throws Exception {

        GameOfc game = new GameOfc(Game.Nw.Upoker, 100);
        game.id = "21168314-2";
        game.addPlayer(new PlayerOfc("opp1", 1520));
        game.addPlayer(new PlayerOfc("hero", 1520));
        game.heroName = "hero";
        game.initButtonName("hero");
        game.gameMode = GameOfc.GameMode.GAME_MODE_OFC_PROGRESSIVE;

        List<Card> emptyList = new ArrayList<>();

        game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, "opp1", Card.cards2Mask(Card.str2Cards("")), Card.cards2Mask(Card.str2Cards("4s6c")), Card.cards2Mask(Card.str2Cards("7d9d9c")), emptyList));
        game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("TdTs4h5h8h"))));
        game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, game.heroName, Card.cards2Mask(Card.str2Cards("")), Card.cards2Mask(Card.str2Cards("TdTs")), Card.cards2Mask(Card.str2Cards("4h5h8h")), emptyList));
        game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, "opp1", Card.cards2Mask(Card.str2Cards("Jh")), Card.cards2Mask(Card.str2Cards("6s")), Card.cards2Mask(Card.str2Cards("")), emptyList));
        game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("Ks3hQd"))));
        game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, game.heroName, Card.cards2Mask(Card.str2Cards("Ks")), Card.cards2Mask(Card.str2Cards("")), Card.cards2Mask(Card.str2Cards("3h")), new ArrayList<>(Arrays.asList(Card.str2Cards("Qd")))));
        game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, "opp1", Card.cards2Mask(Card.str2Cards("Jd")), Card.cards2Mask(Card.str2Cards("4d")), Card.cards2Mask(Card.str2Cards("")), emptyList));
        game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("6hAd2h"))));

        System.out.println(game);

        Config.EvaluationMethod = Config.EvaluationMethodKind.SINGLE_HERO;
        Config.DEPTH_OF_SEARCH = 10;
        Config.OPP_RANDOM_DEAL_COUNT = 100;
        Config.DEBUG_PRINT = true;
        Config.FANTASY_SCORE = 7;
        Config.FAIL_PENALTY = -3;

        GameOfcMcts state = new GameOfcMcts(game);
        Mcts<GameOfcMcts, EventOfcMcts, AgentOfcMcts> mcts = Mcts.initializeIterations(20000, null, new DebugPrinter());
        mcts.dontClone(AgentOfcMcts.class);
        long timeBefore = Misc.getTime();
        EventOfcMcts decision = mcts.uctSearchWithExploration(state, 10, 50000, 60000);
        decision.setTime();
        System.out.println(Misc.sf("MCTS decision in %d ms: \n%s", Misc.getTime() - timeBefore, decision.toString()));
        System.out.println(Misc.sf("IterateCount = %d", mcts.getIterationsCount()));

    }

    public void testNotLikeAI102() throws Exception {

        GameOfc game = new GameOfc(Game.Nw.Upoker, 100);
        game.id = "21168314-27";
        game.addPlayer(new PlayerOfc("opp1", 1520));
        game.addPlayer(new PlayerOfc("hero", 1520));
        game.heroName = "hero";
        game.initButtonName("hero");
        game.gameMode = GameOfc.GameMode.GAME_MODE_OFC_PROGRESSIVE;

        List<Card> emptyList = new ArrayList<>();

        game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, "opp1", Card.cards2Mask(Card.str2Cards("Ks")), Card.cards2Mask(Card.str2Cards("")), Card.cards2Mask(Card.str2Cards("2h3c4d5c")), emptyList));
        game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("3d5d7cTsJs"))));
        game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, game.heroName, Card.cards2Mask(Card.str2Cards("")), Card.cards2Mask(Card.str2Cards("3d5d7c")), Card.cards2Mask(Card.str2Cards("TsJs")), emptyList));
        game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, "opp1", Card.cards2Mask(Card.str2Cards("")), Card.cards2Mask(Card.str2Cards("9dAs")), Card.cards2Mask(Card.str2Cards("")), emptyList));
        game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("Jc6d2c"))));
        game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, game.heroName, Card.cards2Mask(Card.str2Cards("6d")), Card.cards2Mask(Card.str2Cards("")), Card.cards2Mask(Card.str2Cards("Jc")), new ArrayList<>(Arrays.asList(Card.str2Cards("2c")))));
        game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, "opp1", Card.cards2Mask(Card.str2Cards("5h")), Card.cards2Mask(Card.str2Cards("3h")), Card.cards2Mask(Card.str2Cards("")), emptyList));
        game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("8dQh6h"))));

        System.out.println(game);

        Config.EvaluationMethod = Config.EvaluationMethodKind.SINGLE_HERO;
        Config.DEPTH_OF_SEARCH = 10;
        Config.OPP_RANDOM_DEAL_COUNT = 100;
        Config.DEBUG_PRINT = true;
        Config.FANTASY_SCORE = 7;
        Config.FAIL_PENALTY = -3;

        long timeBefore = Utils.getTime();
		EventOfc result = EurekaRunner.run(game, 0, 8000);
//        Config cfg = new Config();
//        NatureSpace natureSpace = new NatureSpaceExt(game, cfg);
//        GameOfcMctsSimple stateSimple = new GameOfcMctsSimple(game, natureSpace);
//        long timeBefore = Utils.getTime();
//        EventOfcMctsSimple decision = Mcs.monteCarloSimulation(stateSimple, 0);

        System.out.println(String.format("MCS decision in %d ms: \n%s", Utils.getTime() - timeBefore, result.toString()));

    }

    public void testNotLikeAI102MctsNotSimple() throws Exception {

        GameOfc game = new GameOfc(Game.Nw.Upoker, 100);
        game.id = "21168314-27";
        game.addPlayer(new PlayerOfc("opp1", 1520));
        game.addPlayer(new PlayerOfc("hero", 1520));
        game.heroName = "hero";
        game.initButtonName("hero");
        game.gameMode = GameOfc.GameMode.GAME_MODE_OFC_PROGRESSIVE;

        List<Card> emptyList = new ArrayList<>();

        game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, "opp1", Card.cards2Mask(Card.str2Cards("Ks")), Card.cards2Mask(Card.str2Cards("")), Card.cards2Mask(Card.str2Cards("2h3c4d5c")), emptyList));
        game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("3d5d7cTsJs"))));
        game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, game.heroName, Card.cards2Mask(Card.str2Cards("")), Card.cards2Mask(Card.str2Cards("3d5d7c")), Card.cards2Mask(Card.str2Cards("TsJs")), emptyList));
        game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, "opp1", Card.cards2Mask(Card.str2Cards("")), Card.cards2Mask(Card.str2Cards("9dAs")), Card.cards2Mask(Card.str2Cards("")), emptyList));
        game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("Jc6d2c"))));
        game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, game.heroName, Card.cards2Mask(Card.str2Cards("6d")), Card.cards2Mask(Card.str2Cards("")), Card.cards2Mask(Card.str2Cards("Jc")), new ArrayList<>(Arrays.asList(Card.str2Cards("2c")))));
        game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, "opp1", Card.cards2Mask(Card.str2Cards("5h")), Card.cards2Mask(Card.str2Cards("3h")), Card.cards2Mask(Card.str2Cards("")), emptyList));
        game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("8dQh6h"))));

        System.out.println(game);

        Config.EvaluationMethod = Config.EvaluationMethodKind.BOARD_ACROSS;
        Config.DEPTH_OF_SEARCH = 10;
        Config.OPP_RANDOM_DEAL_COUNT = 100;
        Config.DEBUG_PRINT = true;
        Config.FANTASY_SCORE = 7;
        Config.FAIL_PENALTY = -1;

        GameOfcMcts state = new GameOfcMcts(game);
        Mcts<GameOfcMcts, EventOfcMcts, AgentOfcMcts> mcts = Mcts.initializeIterations(20000, null, new DebugPrinter());
        mcts.dontClone(AgentOfcMcts.class);
        long timeBefore = Misc.getTime();
        EventOfcMcts decision = mcts.uctSearchWithExploration(state, 10, 50000, 60000);
        decision.setTime();
        System.out.println(Misc.sf("MCTS decision in %d ms: \n%s", Misc.getTime() - timeBefore, decision.toString()));
        System.out.println(Misc.sf("IterateCount = %d", mcts.getIterationsCount()));

    }

    public void testNotLikeAI103() throws Exception {

        GameOfc game = new GameOfc(Game.Nw.Upoker, 100);
        game.id = "21168314-12";
        game.addPlayer(new PlayerOfc("opp1", 1520));
        game.addPlayer(new PlayerOfc("hero", 1520));
        game.getPlayer("opp1").playFantasy = true;
        game.heroName = "hero";
        game.initButtonName("hero");
        game.gameMode = GameOfc.GameMode.GAME_MODE_OFC_PROGRESSIVE;

        List<Card> emptyList = new ArrayList<>();

        game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("Kd4cJs7hQh"))));
        game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, game.heroName, Card.cards2Mask(Card.str2Cards("Kd")), Card.cards2Mask(Card.str2Cards("4cJs")), Card.cards2Mask(Card.str2Cards("7hQh")), emptyList));
        game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("Kh4hTc"))));

        System.out.println(game);

        Config.EvaluationMethod = Config.EvaluationMethodKind.SINGLE_HERO;
        Config.DEPTH_OF_SEARCH = 10;
        Config.OPP_RANDOM_DEAL_COUNT = 100;
        Config.DEBUG_PRINT = true;
        Config.FANTASY_SCORE = 7;
        Config.FAIL_PENALTY = -3;

//		EventOfc result = EurekaRunner.run(game, 15000, 17000);
        Config cfg = new Config();
        NatureSpace natureSpace = new NatureSpaceExt(game, cfg);
        GameOfcMctsSimple stateSimple = new GameOfcMctsSimple(game, natureSpace);
        long timeBefore = Utils.getTime();
        EventOfcMctsSimple decision = Mcs.monteCarloSimulation(stateSimple, 0);

        System.out.println(String.format("MCS decision in %d ms: \n%s", Utils.getTime() - timeBefore, decision.toEventOfc(game.heroName).toString()));

    }

    public void testNotLikeAI103MctsNotSimple() throws Exception {

        GameOfc game = new GameOfc(Game.Nw.Upoker, 100);
        game.id = "21168314-12";
        game.addPlayer(new PlayerOfc("opp1", 1520));
        game.addPlayer(new PlayerOfc("hero", 1520));
        game.getPlayer("opp1").playFantasy = true;
        game.heroName = "hero";
        game.initButtonName("hero");
        game.gameMode = GameOfc.GameMode.GAME_MODE_OFC_PROGRESSIVE;

        List<Card> emptyList = new ArrayList<>();

        game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("Kd4cJs7hQh"))));
        game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, game.heroName, Card.cards2Mask(Card.str2Cards("Kd")), Card.cards2Mask(Card.str2Cards("4cJs")), Card.cards2Mask(Card.str2Cards("7hQh")), emptyList));
        game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("Kh4hTc"))));

        System.out.println(game);

        Config.EvaluationMethod = Config.EvaluationMethodKind.SINGLE_HERO;
        Config.DEPTH_OF_SEARCH = 10;
        Config.OPP_RANDOM_DEAL_COUNT = 100;
        Config.DEBUG_PRINT = true;
        Config.FANTASY_SCORE = 7;
        Config.FAIL_PENALTY = -3;

        GameOfcMcts state = new GameOfcMcts(game);
        Mcts<GameOfcMcts, EventOfcMcts, AgentOfcMcts> mcts = Mcts.initializeIterations(20000, null, new DebugPrinter());
        mcts.dontClone(AgentOfcMcts.class);
        long timeBefore = Misc.getTime();
        EventOfcMcts decision = mcts.uctSearchWithExploration(state, 10, 50000, 60000);
        decision.setTime();
        System.out.println(Misc.sf("MCTS decision in %d ms: \n%s", Misc.getTime() - timeBefore, decision.toString()));
        System.out.println(Misc.sf("IterateCount = %d", mcts.getIterationsCount()));

    }

    public void testNotLikeAI104() throws Exception {

        GameOfc game = new GameOfc(Game.Nw.Upoker, 100);
        game.id = "21169926-1";
        game.addPlayer(new PlayerOfc("opp1", 1520));
        game.addPlayer(new PlayerOfc("hero", 1520));
        game.heroName = "hero";
        game.initButtonName("opp1");
        game.gameMode = GameOfc.GameMode.GAME_MODE_OFC_PROGRESSIVE;

        List<Card> emptyList = new ArrayList<>();

        game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("2dJs5c8c9c"))));
        game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, game.heroName, Card.cards2Mask(Card.str2Cards("")), Card.cards2Mask(Card.str2Cards("2dJs")), Card.cards2Mask(Card.str2Cards("5c8c9c")), emptyList));
        game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, "opp1", Card.cards2Mask(Card.str2Cards("Kc")), Card.cards2Mask(Card.str2Cards("2s3h6d")), Card.cards2Mask(Card.str2Cards("Ts")), emptyList));
        game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("7s2h2c"))));

        System.out.println(game);

        Config.EvaluationMethod = Config.EvaluationMethodKind.SINGLE_HERO;
        Config.DEPTH_OF_SEARCH = 10;
        Config.OPP_RANDOM_DEAL_COUNT = 200;
        Config.DEBUG_PRINT = false;
        Config.FANTASY_SCORE = 7;
        Config.FAIL_PENALTY = -3;

        long timeBefore = Utils.getTime();
		EventOfc result = EurekaRunner.run(game, 0, 8000);
//        Config cfg = new Config();
//        NatureSpace natureSpace = new NatureSpaceExt(game, cfg);
//        GameOfcMctsSimple stateSimple = new GameOfcMctsSimple(game, natureSpace);
//        long timeBefore = Utils.getTime();
//        EventOfcMctsSimple decision = Mcs.monteCarloSimulation(stateSimple, 0);

        System.out.println(String.format("MCS decision in %d ms: \n%s", Utils.getTime() - timeBefore, result.toString()));

    }

    public void testNotLikeAI104MctsNotSimple() throws Exception {

        GameOfc game = new GameOfc(Game.Nw.Upoker, 100);
        game.id = "21169926-1";
        game.addPlayer(new PlayerOfc("opp1", 1520));
        game.addPlayer(new PlayerOfc("hero", 1520));
        game.heroName = "hero";
        game.initButtonName("opp1");
        game.gameMode = GameOfc.GameMode.GAME_MODE_OFC_PROGRESSIVE;

        List<Card> emptyList = new ArrayList<>();

        game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("2dJs5c8c9c"))));
        game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, game.heroName, Card.cards2Mask(Card.str2Cards("")), Card.cards2Mask(Card.str2Cards("2dJs")), Card.cards2Mask(Card.str2Cards("5c8c9c")), emptyList));
        game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, "opp1", Card.cards2Mask(Card.str2Cards("Kc")), Card.cards2Mask(Card.str2Cards("2s3h6d")), Card.cards2Mask(Card.str2Cards("Ts")), emptyList));
        game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("7s2h2c"))));

        System.out.println(game);

        Config.EvaluationMethod = Config.EvaluationMethodKind.SINGLE_HERO;
        Config.DEPTH_OF_SEARCH = 10;
        Config.OPP_RANDOM_DEAL_COUNT = 200;
        Config.DEBUG_PRINT = true;
        Config.FANTASY_SCORE = 7;
        Config.FAIL_PENALTY = 0;

        GameOfcMcts state = new GameOfcMcts(game);
        Mcts<GameOfcMcts, EventOfcMcts, AgentOfcMcts> mcts = Mcts.initializeIterations(20000, null, new DebugPrinter());
        mcts.dontClone(AgentOfcMcts.class);
        long timeBefore = Misc.getTime();
        EventOfcMcts decision = mcts.uctSearchWithExploration(state, 150, 500000, 600000);
        decision.setTime();
        System.out.println(Misc.sf("MCTS decision in %d ms: \n%s", Misc.getTime() - timeBefore, decision.toString()));
        System.out.println(Misc.sf("IterateCount = %d", mcts.getIterationsCount()));

    }

    public void testNotLikeAI105() throws Exception {

        GameOfc game = new GameOfc(Game.Nw.Upoker, 100);
        game.id = "21169926-15";
        game.addPlayer(new PlayerOfc("opp1", 1520));
        game.addPlayer(new PlayerOfc("hero", 1520));
        game.getPlayer("opp1").playFantasy = true;
        game.heroName = "hero";
        game.initButtonName("opp1");
        game.gameMode = GameOfc.GameMode.GAME_MODE_OFC_PROGRESSIVE;

        List<Card> emptyList = new ArrayList<>();

        game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("7hJdJsQdQc"))));
        game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, game.heroName, Card.cards2Mask(Card.str2Cards("")), Card.cards2Mask(Card.str2Cards("7h")), Card.cards2Mask(Card.str2Cards("JdJsQdQc")), emptyList));
        game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("2h9dTd"))));

        System.out.println(game);

        Config.EvaluationMethod = Config.EvaluationMethodKind.SINGLE_HERO;
        Config.DEPTH_OF_SEARCH = 10;
        Config.OPP_RANDOM_DEAL_COUNT = 200;
        Config.DEBUG_PRINT = false;
        Config.FANTASY_SCORE = 7;
        Config.FAIL_PENALTY = -3;

        long timeBefore = Utils.getTime();
        EventOfc decision = EurekaRunner.run(game, 50000, 60000);
//        Config cfg = new Config();
//        cfg.RANDOM_DEAL_COUNT = 30000;
//        NatureSpace natureSpace = new NatureSpace(game, cfg);
//        GameOfcMctsSimple stateSimple = new GameOfcMctsSimple(game, natureSpace);
//        long timeBefore = Utils.getTime();
//        EventOfcMctsSimple decision = Mcs.monteCarloSimulation(stateSimple, 0);
//        EventOfcMctsSimple decision = Mcs.monteCarloSimulation(stateSimple, 0, 4);

        System.out.println(String.format("MCS decision in %d ms: \n%s", Utils.getTime() - timeBefore, decision.toString()));

    }

    public void testNotLikeAI105MctsNotSimple() throws Exception {

        GameOfc game = new GameOfc(Game.Nw.Upoker, 100);
        game.id = "21169926-15";
        game.addPlayer(new PlayerOfc("opp1", 1520));
        game.addPlayer(new PlayerOfc("hero", 1520));
        game.getPlayer("opp1").playFantasy = true;
        game.heroName = "hero";
        game.initButtonName("opp1");
        game.gameMode = GameOfc.GameMode.GAME_MODE_OFC_PROGRESSIVE;

        List<Card> emptyList = new ArrayList<>();

        game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("7hJdJsQdQc"))));
        game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, game.heroName, Card.cards2Mask(Card.str2Cards("")), Card.cards2Mask(Card.str2Cards("7h")), Card.cards2Mask(Card.str2Cards("JdJsQdQc")), emptyList));
        game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("2h9dTd"))));

        System.out.println(game);

        Config.EvaluationMethod = Config.EvaluationMethodKind.SINGLE_HERO;
        Config.DEPTH_OF_SEARCH = 10;
        Config.OPP_RANDOM_DEAL_COUNT = 200;
        Config.DEBUG_PRINT = false;
        Config.FANTASY_SCORE = 15;
        Config.FAIL_PENALTY = -3;

        GameOfcMcts state = new GameOfcMcts(game);
        Mcts<GameOfcMcts, EventOfcMcts, AgentOfcMcts> mcts = Mcts.initializeIterations(20000, null, new DebugPrinter());
        mcts.dontClone(AgentOfcMcts.class);
        long timeBefore = Misc.getTime();
        EventOfcMcts decision = mcts.uctSearchWithExploration(state, 15, 50000, 60000);
        decision.setTime();
        System.out.println(Misc.sf("MCTS decision in %d ms: \n%s", Misc.getTime() - timeBefore, decision.toString()));
        System.out.println(Misc.sf("IterateCount = %d", mcts.getIterationsCount()));

    }


    // first round

    public void testNotLikeAI106() throws Exception {

        GameOfc game = new GameOfc(Game.Nw.Upoker, 100);
        game.id = "21166694-183";
        game.addPlayer(new PlayerOfc("opp1", 1520));
        game.addPlayer(new PlayerOfc("hero", 1520));
        game.heroName = "hero";
        game.initButtonName("hero");
        game.gameMode = GameOfc.GameMode.GAME_MODE_OFC_PROGRESSIVE;

        List<Card> emptyList = new ArrayList<>();

        game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, "opp1", Card.cards2Mask(Card.str2Cards("KhKd")), Card.cards2Mask(Card.str2Cards("7s")), Card.cards2Mask(Card.str2Cards("2dQd")), emptyList));
        game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("6d8sTsJcJs"))));

        System.out.println(game);

        Config.EvaluationMethod = Config.EvaluationMethodKind.SINGLE_HERO;
        Config.DEPTH_OF_SEARCH = 10;
        Config.OPP_RANDOM_DEAL_COUNT = 100;
        Config.DEBUG_PRINT = false;
        Config.FANTASY_SCORE = 7;
        Config.FAIL_PENALTY = -3;

        long timeBefore = Utils.getTime();
        EventOfc result = EurekaRunner.run(game, 0, 8000);
//        Config cfg = new Config();
//        NatureSpace natureSpace = new NatureSpaceExt(game, cfg);
//        GameOfcMctsSimple stateSimple = new GameOfcMctsSimple(game, natureSpace);
//        long timeBefore = Utils.getTime();
//        EventOfcMctsSimple decision = Mcs.monteCarloSimulation(stateSimple, 0);

        System.out.println(String.format("MCS decision in %d ms: \n%s", Utils.getTime() - timeBefore, result.toString()));

    }

    public void testNotLikeAI106MctsNotSimple() throws Exception {

        GameOfc game = new GameOfc(Game.Nw.Upoker, 100);
        game.id = "21166694-183";
        game.addPlayer(new PlayerOfc("opp1", 1520));
        game.addPlayer(new PlayerOfc("hero", 1520));
        game.heroName = "hero";
        game.initButtonName("hero");
        game.gameMode = GameOfc.GameMode.GAME_MODE_OFC_PROGRESSIVE;

        List<Card> emptyList = new ArrayList<>();

        game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, "opp1", Card.cards2Mask(Card.str2Cards("KhKd")), Card.cards2Mask(Card.str2Cards("7s")), Card.cards2Mask(Card.str2Cards("2dQd")), emptyList));
        game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("6d8sTsJcJs"))));

        System.out.println(game);

        Config.EvaluationMethod = Config.EvaluationMethodKind.SINGLE_HERO;
        Config.DEPTH_OF_SEARCH = 10;
        Config.OPP_RANDOM_DEAL_COUNT = 100;
        Config.DEBUG_PRINT = false;
        Config.FANTASY_SCORE = 7;
        Config.FAIL_PENALTY = 0;

        GameOfcMcts state = new GameOfcMcts(game);
        Mcts<GameOfcMcts, EventOfcMcts, AgentOfcMcts> mcts = Mcts.initializeIterations(20000, null, null);
        mcts.dontClone(AgentOfcMcts.class);
        long timeBefore = Misc.getTime();
        EventOfcMcts decision = mcts.uctSearchWithExploration(state, 7, 0, 60000);
        decision.setTime();
        System.out.println(Misc.sf("MCTS decision in %d ms: \n%s", Misc.getTime() - timeBefore, decision.toString()));
        System.out.println(Misc.sf("IterateCount = %d", mcts.getIterationsCount()));

    }



    // another first round
//    http://nsk.convexbytes.com:15273/bestmove?hero=%2F%2F&newCards=6s+8h+Qh+Kh+Kc&opp=%2F%2F&dead=&button=0&table=test&rules=progressive16_refant14_nojokers&account=pid7568847&appName=Ppp&clubId=3109882&stakes=0.50&price=0.001USD&gameId=221106145621-52798420-0000004-1&timeLimit=15&fastObvious&partner=0
//    Qh / Kh Kc / 6s 8h
//    http://13.49.160.164:8000/bestmove?hero=%2F%2F&newCards=6s+8h+Qh+Kh+Kc&opp=%2F%2F&dead=&button=1&table=test&rules=progressive16_refant14_nojokers&account=pid7568847&appName=Ppp&clubId=3109882&stakes=0.50&price=0.001USD&gameId=221106145621-52798420-0000004-1&timeLimit=15&fastObvious&partner=altai-zxgsejynkd
//    Kc Kh / 6s / 8h Qh

    public void testNotLikeAI107() throws Exception {

        GameOfc game = new GameOfc(Game.Nw.Upoker, 100);
        game.id = "221106145621-52798420-0000004-1";
        game.addPlayer(new PlayerOfc("opp1", 1520));
        game.addPlayer(new PlayerOfc("hero", 1520));
        game.heroName = "hero";
        game.initButtonName("opp1");
        game.gameMode = GameOfc.GameMode.GAME_MODE_REGULAR;

        List<Card> emptyList = new ArrayList<>();

        game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("6s8hQhKhKc"))));

        System.out.println(game);

        Config.EvaluationMethod = Config.EvaluationMethodKind.SINGLE_HERO;
        Config.DEPTH_OF_SEARCH = 10;
        Config.OPP_RANDOM_DEAL_COUNT = 100;
        Config.DEBUG_PRINT = false;
        Config.FANTASY_SCORE = 7;
        Config.FAIL_PENALTY = -3;

        long timeBefore = Utils.getTime();
        EventOfc result = EurekaRunner.run(game, 0, 8000);
//        Config cfg = new Config();
//        NatureSpace natureSpace = new NatureSpace(game, cfg);
//        GameOfcMctsSimple stateSimple = new GameOfcMctsSimple(game, natureSpace);
//        long timeBefore = Utils.getTime();
//        EventOfcMctsSimple result = Mcs.monteCarloSimulation(stateSimple, 10000, 4);

        System.out.println(String.format("MCS decision in %d ms: \n%s", Utils.getTime() - timeBefore, result.toString()));

    }

    public void testNotLikeAI107MctsNotSimple() throws Exception {

        GameOfc game = new GameOfc(Game.Nw.Upoker, 100);
        game.id = "221106145621-52798420-0000004-1";
        game.addPlayer(new PlayerOfc("opp1", 1520));
        game.addPlayer(new PlayerOfc("hero", 1520));
        game.heroName = "hero";
        game.initButtonName("opp1");
        game.gameMode = GameOfc.GameMode.GAME_MODE_REGULAR;

        List<Card> emptyList = new ArrayList<>();

        game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("6s8hQhKhKc"))));

        System.out.println(game);

        Config.EvaluationMethod = Config.EvaluationMethodKind.SINGLE_HERO;
        Config.DEPTH_OF_SEARCH = 10;
        Config.OPP_RANDOM_DEAL_COUNT = 100;
        Config.DEBUG_PRINT = false;
        Config.FANTASY_SCORE = 7;
        Config.FAIL_PENALTY = -3;

        GameOfcMcts state = new GameOfcMcts(game);
        Mcts<GameOfcMcts, EventOfcMcts, AgentOfcMcts> mcts = Mcts.initializeIterations(100000, null, null);
        mcts.dontClone(AgentOfcMcts.class);
        long timeBefore = Misc.getTime();
        EventOfcMcts decision = mcts.uctSearchWithExploration(state, 7, 0, 600000);
        decision.setTime();
        System.out.println(Misc.sf("MCTS decision in %d ms: \n%s", Misc.getTime() - timeBefore, decision.toString()));
        System.out.println(Misc.sf("IterateCount = %d", mcts.getIterationsCount()));

    }


    // second round
    public void testFail1() throws Exception {

        GameOfc game = new GameOfc(Game.Nw.Upoker, 100);
        game.id = "221106145621-52798420-0000004-1";
        game.addPlayer(new PlayerOfc("opp1", 1520));
        game.addPlayer(new PlayerOfc("hero", 1520));
        game.heroName = "hero";
        game.initButtonName("opp1");
        game.gameMode = GameOfc.GameMode.GAME_MODE_REGULAR;

        List<Card> emptyList = new ArrayList<>();

        game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("2h3c5d8cKc"))));

        System.out.println(game);

        Config.EvaluationMethod = Config.EvaluationMethodKind.SINGLE_HERO;
        Config.DEPTH_OF_SEARCH = 10;
        Config.OPP_RANDOM_DEAL_COUNT = 100;
        Config.DEBUG_PRINT = false;
        Config.FANTASY_SCORE = 7;
        Config.FAIL_PENALTY = -3;

        long timeBefore = Utils.getTime();
        EventOfc result = EurekaRunner.run(game, 0, 16000);

        System.out.println(String.format("MCS decision in %d ms: \n%s", Utils.getTime() - timeBefore, result.toString()));

    }

    public static void main(String[] args) throws Exception {
        OfcThirdRoundTest test = new OfcThirdRoundTest();
        test.testFail1();
    }
}
