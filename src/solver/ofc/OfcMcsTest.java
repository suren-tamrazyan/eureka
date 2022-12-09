package solver.ofc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import game.Card;
import game.EventOfc;
import game.GameOfc;
import game.PlayerOfc;
import game.Game.Nw;
import game.GameOfc.GameMode;
import solver.ofc.mcs.Mcs;

public class OfcMcsTest {

    public void testX() throws Exception {
    	GameOfc game = new GameOfc(Nw.Ppp, 100);
    	game.id = "mcs test";
    	game.addPlayer(new PlayerOfc("pid6365117", 10000));
    	game.addPlayer(new PlayerOfc("pid6258836", 10000));
    	game.addPlayer(new PlayerOfc("pid6281006", 10000));
    	game.heroName = "pid6365117";
    	game.initButtonName("pid6281006");
    	game.gameMode = GameMode.GAME_MODE_REGULAR;
    	
    	EventOfc ev = new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("QsJs6s6d2s")));
    	game.procEvent(ev);
    	System.out.println(game.toString());
    	
    	Config cfg = new Config();
    	GameOfcMctsSimple stateSimple = new GameOfcMctsSimple(game, new NatureSpace(game, cfg));
    	long timeBefore = Utils.getTime();
    	EventOfcMctsSimple decision = Mcs.monteCarloSimulation(stateSimple, 0);
    	System.out.println(Utils.getTime() - timeBefore);
    	System.out.println(cfg.RANDOM_DEAL_COUNT);
    	System.out.println(decision.toEventOfc(game.heroName).toString());
    	
    }
    
    public void testMiddleMove1() throws Exception {
    	GameOfc game = new GameOfc(Nw.Ppp, 100);
    	game.id = "211123230113-37515400-0000015-1";
    	game.addPlayer(new PlayerOfc("pid6691575", 1520));
    	game.addPlayer(new PlayerOfc("pid6691608", 480));
    	game.heroName = "pid6691575";
    	game.initButtonName("pid6691608");
    	game.gameMode = GameMode.GAME_MODE_OFC_PROGRESSIVE;
    	
    	List<Card> emptyList = new ArrayList<>();
    	
    	game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("6d6h5c4d2h"))));
    	game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, "pid6691575", Card.cards2Mask(Card.str2Cards("4d")), Card.cards2Mask(Card.str2Cards("2h5c")), Card.cards2Mask(Card.str2Cards("6h6d")), emptyList));
    	game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, "pid6691608", 0, Card.cards2Mask(Card.str2Cards("9d9c")), Card.cards2Mask(Card.str2Cards("4h5h8h")), emptyList));
    	game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("Kd9h6s"))));
    	game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, "pid6691575", Card.cards2Mask(Card.str2Cards("Kd")), 0, Card.cards2Mask(Card.str2Cards("6s")), new ArrayList<>(Arrays.asList(Card.str2Cards("9h")))));
    	game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, "pid6691608", Card.cards2Mask(Card.str2Cards("5d")), Card.cards2Mask(Card.str2Cards("9s")), 0, emptyList));
    	game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("AsAc8d"))));
    	
    	System.out.println(game.toString());
    	
//    	Config.RANDOM_DEAL_COUNT = 20000;
    	Config cfg = new Config();
    	GameOfcMctsSimple stateSimple = new GameOfcMctsSimple(game, new NatureSpace(game, cfg));
    	long timeBefore = Utils.getTime();
    	EventOfcMctsSimple decision = Mcs.monteCarloSimulation(stateSimple, 0);
    	System.out.println(Utils.getTime() - timeBefore);
    	System.out.println(cfg.RANDOM_DEAL_COUNT);
    	System.out.println(decision.toEventOfc(game.heroName).toString());
    	
    }

    public void testNotGoodMoveOnFourRound() throws Exception {
    	GameOfc game = new GameOfc(Nw.Ppp, 100);
    	game.id = "220212023429-40908329-0000001-1";
    	game.addPlayer(new PlayerOfc("pid7100495", 1520));
    	game.addPlayer(new PlayerOfc("pid6818423", 480));
    	game.heroName = "pid6818423";
    	game.initButtonName("pid6818423");
    	game.gameMode = GameMode.GAME_MODE_OFC_ULTIMATE;
    	
    	List<Card> emptyList = new ArrayList<>();
    	
    	game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("2h3hKdAhAd"))));
    	game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, "pid7100495", 0, Card.cards2Mask(Card.str2Cards("4s7d9h")), Card.cards2Mask(Card.str2Cards("6cQc")), emptyList));
    	game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, "pid6818423", Card.cards2Mask(Card.str2Cards("Kd")), Card.cards2Mask(Card.str2Cards("AhAd")), Card.cards2Mask(Card.str2Cards("2h3h")), emptyList));
    	game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("KhQd3d"))));
    	game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, "pid7100495", 0, 0, Card.cards2Mask(Card.str2Cards("9cTc")), emptyList));
    	game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, "pid6818423", Card.cards2Mask(Card.str2Cards("Kh")), 0, Card.cards2Mask(Card.str2Cards("3d")), new ArrayList<>(Arrays.asList(Card.str2Cards("Qd")))));
    	game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("Jc8s2c"))));
    	game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, "pid7100495", Card.cards2Mask(Card.str2Cards("Th")), Card.cards2Mask(Card.str2Cards("")), Card.cards2Mask(Card.str2Cards("Kc")), emptyList));
    	game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, "pid6818423", 0, Card.cards2Mask(Card.str2Cards("Jc")), Card.cards2Mask(Card.str2Cards("2c")), new ArrayList<>(Arrays.asList(Card.str2Cards("8s")))));
    	game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("QsTd9d"))));
    	game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, "pid7100495", Card.cards2Mask(Card.str2Cards("Qh")), Card.cards2Mask(Card.str2Cards("7h")), Card.cards2Mask(Card.str2Cards("")), emptyList));
    	
    	System.out.println(game.toString());
    	
    	Config cfg = new Config();
    	cfg.RANDOM_DEAL_COUNT = cfg.RANDOM_DEAL_COUNT * 10;
    	GameOfcMctsSimple stateSimple = new GameOfcMctsSimple(game, new NatureSpace(game, cfg));
    	long timeBefore = Utils.getTime();
    	EventOfcMctsSimple decision = Mcs.monteCarloSimulation(stateSimple, 0);
    	System.out.println(Utils.getTime() - timeBefore);
    	System.out.println(cfg.RANDOM_DEAL_COUNT);
    	System.out.println(decision.toEventOfc(game.heroName).toString());
    	
    }

    public void testNotGoodOrGoodMoveOnFourRound() throws Exception {
    	GameOfc game = new GameOfc(Nw.Ppp, 100);
    	game.id = "200528025259-6852666-0000009-1";
    	game.addPlayer(new PlayerOfc("pid2139036", 480));
    	game.addPlayer(new PlayerOfc("pid4009851", 480));
    	game.addPlayer(new PlayerOfc("pid3841225", 1520));
    	game.heroName = "pid4009851";
    	game.initButtonName("pid2139036");
    	game.gameMode = GameMode.GAME_MODE_OFC_ULTIMATE;
    	
    	List<Card> emptyList = new ArrayList<>();
//    	Config.FANTASY_SCORE = 15;
    	    	
    	game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("Qc3s6hThJh"))));
    	game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, game.heroName, Card.cards2Mask(Card.str2Cards("Qc")), Card.cards2Mask(Card.str2Cards("3s")), Card.cards2Mask(Card.str2Cards("6hThJh")), emptyList));
    	game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, "pid3841225", Card.cards2Mask(Card.str2Cards("")), Card.cards2Mask(Card.str2Cards("2h7d")), Card.cards2Mask(Card.str2Cards("8c8sQd")), emptyList));
    	game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, "pid2139036", Card.cards2Mask(Card.str2Cards("Td")), Card.cards2Mask(Card.str2Cards("4hAd")), Card.cards2Mask(Card.str2Cards("5c9c")), emptyList));
    	game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("5hKhJd"))));
    	game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, game.heroName, Card.cards2Mask(Card.str2Cards("")), Card.cards2Mask(Card.str2Cards("")), Card.cards2Mask(Card.str2Cards("5hKh")), new ArrayList<>(Arrays.asList(Card.str2Cards("Jd")))));
    	game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, "pid3841225", Card.cards2Mask(Card.str2Cards("AcAs")), Card.cards2Mask(Card.str2Cards("")), Card.cards2Mask(Card.str2Cards("")), emptyList));
    	game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, "pid2139036", Card.cards2Mask(Card.str2Cards("KdKs")), Card.cards2Mask(Card.str2Cards("")), Card.cards2Mask(Card.str2Cards("")), emptyList));
    	game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("5s6dTc"))));
    	game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, game.heroName, Card.cards2Mask(Card.str2Cards("")), Card.cards2Mask(Card.str2Cards("5s6d")), Card.cards2Mask(Card.str2Cards("")), new ArrayList<>(Arrays.asList(Card.str2Cards("Tc")))));
    	game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, "pid3841225", Card.cards2Mask(Card.str2Cards("")), Card.cards2Mask(Card.str2Cards("9s")), Card.cards2Mask(Card.str2Cards("8h")), emptyList));
    	game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, "pid2139036", Card.cards2Mask(Card.str2Cards("")), Card.cards2Mask(Card.str2Cards("Qs")), Card.cards2Mask(Card.str2Cards("5d")), emptyList));
    	game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("8dAhQh"))));
    	
    	System.out.println(game.toString());
    	
    	Config cfg = new Config();
    	GameOfcMctsSimple stateSimple = new GameOfcMctsSimple(game, new NatureSpace(game, cfg));
    	long timeBefore = Utils.getTime();
    	EventOfcMctsSimple decision = Mcs.monteCarloSimulation(stateSimple, 0);
    	System.out.println(Utils.getTime() - timeBefore);
    	System.out.println(decision.toEventOfc(game.heroName).toString());
    	System.out.println("Nature samples:");
    	for (EventOfcMctsSimple natureSamp : stateSimple.getNatureSpace().natureSamples) {
    		System.out.println(natureSamp.toEventOfc("hero").toString());
    	}
    }

        public void testI() throws Exception {
        	GameOfc game = new GameOfc(Nw.Ppp, 100);
        	game.id = "220309181143-41998571-0000006-1";
        	game.addPlayer(new PlayerOfc("pid7188974", 480));
        	game.addPlayer(new PlayerOfc("pid7190998", 480));
        	game.heroName = "pid7190998";
        	game.initButtonName("pid7188974");
        	game.getPlayer("pid7188974").playFantasy = true;
        	game.gameMode = GameMode.GAME_MODE_REGULAR;
        	
        	List<Card> emptyList = new ArrayList<>();
//        	Config.FANTASY_SCORE = 15;
        	    	
        	game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("2d5c6h9cQh"))));
        	game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, game.heroName, Card.cards2Mask(Card.str2Cards("Qh")), Card.cards2Mask(Card.str2Cards("2d6h")), Card.cards2Mask(Card.str2Cards("5c9c")), emptyList));
        	game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("AdKsTh"))));
        	game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, game.heroName, Card.cards2Mask(Card.str2Cards("Ks")), Card.cards2Mask(Card.str2Cards("Ad")), Card.cards2Mask(Card.str2Cards("")), new ArrayList<>(Arrays.asList(Card.str2Cards("Th")))));
        	game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("Kd8d2s"))));
        	game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, game.heroName, Card.cards2Mask(Card.str2Cards("Kd")), Card.cards2Mask(Card.str2Cards("")), Card.cards2Mask(Card.str2Cards("8d")), new ArrayList<>(Arrays.asList(Card.str2Cards("2s")))));
        	game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("4h3h2c"))));
        	
        	System.out.println(game.toString());
        	
//        	GameOfcMctsSimple stateSimple = new GameOfcMctsSimple(game, new EurekaRunner(game));
        	
        	
        	Config cfg = new Config();
        	EventOfc result = Mcs.monteCarloSimulation(new GameOfcMctsSimple(game, new NatureSpace(game, cfg)), 0).toEventOfc(game.heroName);//EurekaRunner.run(game, 4000);
        	System.out.println(result.toString());
        	
        	
//        	long timeBefore = Misc.getTime();
//        	EventOfcMctsSimple decision = Mcs.monteCarloSimulation(stateSimple, 0);
//        	System.out.println(Misc.getTime() - timeBefore);
//        	System.out.println(decision.toEventOfc(game.heroName).toString());
//        	System.out.println("Nature samples:");
//        	for (EventOfcMctsSimple natureSamp : stateSimple.getOwnerClosure().natureSamples) {
//        		System.out.println(natureSamp.toEventOfc("hero").toString());
//        	}
    }
    
        
        public void testNotLikeAI1() throws Exception {
        	GameOfc game = new GameOfc(Nw.Ppp, 100);
        	game.id = "211123230113-37515400-0000015-1";
        	game.addPlayer(new PlayerOfc("pid6691575", 1520));
        	game.addPlayer(new PlayerOfc("pid6691608", 480));
        	game.heroName = "pid6691575";
        	game.initButtonName(game.heroName);
        	game.gameMode = GameMode.GAME_MODE_OFC_PROGRESSIVE;
        	
        	List<Card> emptyList = new ArrayList<>();
        	
        	game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, "pid6691608", 0, Card.cards2Mask(Card.str2Cards("")), Card.cards2Mask(Card.str2Cards("Qh3c3d3s4d")), emptyList));
        	game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("6h6d6sTcTd"))));
        	game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, "pid6691575", Card.cards2Mask(Card.str2Cards("")), Card.cards2Mask(Card.str2Cards("TcTd")), Card.cards2Mask(Card.str2Cards("6h6d6s")), emptyList));
        	game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, "pid6691608", Card.cards2Mask(Card.str2Cards("")), Card.cards2Mask(Card.str2Cards("4c9h")), 0, emptyList));
        	game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("Jd2c5h"))));
        	game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, "pid6691575", Card.cards2Mask(Card.str2Cards("Jd")), Card.cards2Mask(Card.str2Cards("2c")), 0, new ArrayList<>(Arrays.asList(Card.str2Cards("5h")))));
        	game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, "pid6691608", Card.cards2Mask(Card.str2Cards("")), Card.cards2Mask(Card.str2Cards("9d7s")), 0, emptyList));
        	game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("2h8hTs"))));
        	
        	System.out.println(game.toString());
        	
        	Config cfg = new Config();
        	cfg.RANDOM_DEAL_COUNT = 12000;
        	GameOfcMctsSimple stateSimple = new GameOfcMctsSimple(game, new NatureSpace(game, cfg));
        	long timeBefore = Utils.getTime();
//			EventOfcMctsSimple decision = Mcs.monteCarloSimulation(stateSimple, 0);
//			EventOfcMctsSimple decision = Mcs.monteCarloSimulation(stateSimple);
			EventOfcMctsSimple decision = Mcs.monteCarloSimulation(stateSimple, 0, 4);
        	System.out.println(Utils.getTime() - timeBefore);
        	System.out.println(cfg.RANDOM_DEAL_COUNT);
        	System.out.println(decision.toEventOfc(game.heroName).toString());
        	
        }

	public void testNotLikeAI2() throws Exception {
		GameOfc game = new GameOfc(Nw.Ppp, 100);
		game.id = "220414023150-43510554-0000103-1";
		game.addPlayer(new PlayerOfc("opp1", 1520));
		game.addPlayer(new PlayerOfc("hero", 1520));
		game.heroName = "hero";
		game.initButtonName("opp1");
		game.gameMode = GameMode.GAME_MODE_OFC_PROGRESSIVE;

		List<Card> emptyList = new ArrayList<>();

		game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("Qd8cKs6dJd"))));
		game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, game.heroName, Card.cards2Mask(Card.str2Cards("Qd")), Card.cards2Mask(Card.str2Cards("8cKs")), Card.cards2Mask(Card.str2Cards("6dJd")), emptyList));
		game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, "opp1", Card.cards2Mask(Card.str2Cards("Td")), Card.cards2Mask(Card.str2Cards("8sJs7d")), Card.cards2Mask(Card.str2Cards("Qh")), emptyList));
		game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("6c6s5h"))));
		game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, game.heroName, Card.cards2Mask(Card.str2Cards("")), Card.cards2Mask(Card.str2Cards("")), Card.cards2Mask(Card.str2Cards("6c6s")), new ArrayList<>(Arrays.asList(Card.str2Cards("5h")))));
		game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, "opp1", Card.cards2Mask(Card.str2Cards("")), 0, Card.cards2Mask(Card.str2Cards("QcQs")), emptyList));
		game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("5s8h8d"))));

		System.out.println(game.toString());

		Config cfg = new Config();
		cfg.RANDOM_DEAL_COUNT = 20000;
		GameOfcMctsSimple stateSimple = new GameOfcMctsSimple(game, new NatureSpace(game, cfg));
		long timeBefore = Utils.getTime();
		EventOfcMctsSimple decision = Mcs.monteCarloSimulation(stateSimple, 0);
//		EventOfcMctsSimple decision = Mcs.monteCarloSimulation(stateSimple);
//		EventOfcMctsSimple decision = Mcs.monteCarloSimulation(stateSimple, 0, 4);
		System.out.println(Utils.getTime() - timeBefore);
		System.out.println(String.format("RANDOM_DEAL_COUNT = %d", cfg.RANDOM_DEAL_COUNT));
		System.out.println(decision.toEventOfc(game.heroName).toString());

	}

	public void testNotLikeAI4() throws Exception {
		GameOfc game = new GameOfc(Nw.Ppp, 100);
		game.id = "220425211628-44015685-0000031-1";
		game.addPlayer(new PlayerOfc("opp1", 1520));
		game.addPlayer(new PlayerOfc("hero", 1520));
		game.heroName = "hero";
		game.initButtonName("opp1");
		game.gameMode = GameMode.GAME_MODE_OFC_PROGRESSIVE;

		List<Card> emptyList = new ArrayList<>();

		game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("4hAdAc5c8c"))));
		game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, game.heroName, Card.cards2Mask(Card.str2Cards("")), Card.cards2Mask(Card.str2Cards("4hAdAc")), Card.cards2Mask(Card.str2Cards("5c8c")), emptyList));
		game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, "opp1", Card.cards2Mask(Card.str2Cards("Ah")), Card.cards2Mask(Card.str2Cards("5h7h")), Card.cards2Mask(Card.str2Cards("4sJs")), emptyList));
		game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("3c8sKh"))));

		System.out.println(game.toString());

		Config cfg = new Config();
		cfg.RANDOM_DEAL_COUNT = 10000;
		GameOfcMctsSimple stateSimple = new GameOfcMctsSimple(game, new NatureSpace(game, cfg));
		long timeBefore = Utils.getTime();
		EventOfcMctsSimple decision = Mcs.monteCarloSimulation(stateSimple, 0);
//		EventOfcMctsSimple decision = Mcs.monteCarloSimulation(stateSimple);
//		EventOfcMctsSimple decision = Mcs.monteCarloSimulation(stateSimple, 0, 4);
		System.out.println(Utils.getTime() - timeBefore);
		System.out.println(String.format("RANDOM_DEAL_COUNT = %d", cfg.RANDOM_DEAL_COUNT));
		System.out.println(decision.toEventOfc(game.heroName).toString());

	}

	public void testNotLikeAI5() throws Exception {
		GameOfc game = new GameOfc(Nw.Ppp, 100);
		game.id = "220429010803-44153974-0000031-1";
		game.addPlayer(new PlayerOfc("opp1", 1520));
		game.addPlayer(new PlayerOfc("hero", 1520));
		game.heroName = "hero";
		game.initButtonName("hero");
		game.gameMode = GameMode.GAME_MODE_OFC_PROGRESSIVE;

		List<Card> emptyList = new ArrayList<>();

		game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, "opp1", Card.cards2Mask(Card.str2Cards("AcQd")), Card.cards2Mask(Card.str2Cards("3d4h6c")), Card.cards2Mask(Card.str2Cards("")), emptyList));
		game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("8cQs8s2s4c"))));
		game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, game.heroName, Card.cards2Mask(Card.str2Cards("8cQs8s")), Card.cards2Mask(Card.str2Cards("2s4c")), Card.cards2Mask(Card.str2Cards("")), emptyList));
		game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, "opp1", Card.cards2Mask(Card.str2Cards("")), Card.cards2Mask(Card.str2Cards("2d")), Card.cards2Mask(Card.str2Cards("7s")), emptyList));
		game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("3c6d3s"))));
		game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, game.heroName, Card.cards2Mask(Card.str2Cards("")), Card.cards2Mask(Card.str2Cards("3c")), Card.cards2Mask(Card.str2Cards("6d")), new ArrayList<>(Arrays.asList(Card.str2Cards("3s")))));
		game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, "opp1", Card.cards2Mask(Card.str2Cards("")), 0, Card.cards2Mask(Card.str2Cards("TsJs")), emptyList));
		game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("9d6hAs"))));
		game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, game.heroName, Card.cards2Mask(Card.str2Cards("")), Card.cards2Mask(Card.str2Cards("")), Card.cards2Mask(Card.str2Cards("9d6h")), new ArrayList<>(Arrays.asList(Card.str2Cards("As")))));
		game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, "opp1", Card.cards2Mask(Card.str2Cards("")), 0, Card.cards2Mask(Card.str2Cards("4s5s")), emptyList));
		game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("3h5dAh"))));

		System.out.println(game.toString());

		Config cfg = new Config();
		cfg.RANDOM_DEAL_COUNT = 20000;
		Config.FANTASY_SCORE = 15;
		GameOfcMctsSimple stateSimple = new GameOfcMctsSimple(game, new NatureSpace(game, cfg));
		long timeBefore = Utils.getTime();
		EventOfcMctsSimple decision = Mcs.monteCarloSimulation(stateSimple, 0);
//		EventOfcMctsSimple decision = Mcs.monteCarloSimulation(stateSimple);
//		EventOfcMctsSimple decision = Mcs.monteCarloSimulation(stateSimple, 0, 4);
		System.out.println(Utils.getTime() - timeBefore);
		System.out.println(String.format("RANDOM_DEAL_COUNT = %d", cfg.RANDOM_DEAL_COUNT));
		System.out.println(decision.toEventOfc(game.heroName).toString());

	}

	public void testNotLikeAI6() throws Exception {
		GameOfc game = new GameOfc(Nw.Ppp, 100);
		game.id = "220505003911-44411055-0000004-1";
		game.addPlayer(new PlayerOfc("opp1", 1520));
		game.addPlayer(new PlayerOfc("opp2", 1520));
		game.addPlayer(new PlayerOfc("hero", 1520));
		game.heroName = "hero";
		game.initButtonName("opp2");
		game.gameMode = GameMode.GAME_MODE_OFC_PROGRESSIVE;

		List<Card> emptyList = new ArrayList<>();

		game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("QsKc3dAc8h"))));
		game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, game.heroName, Card.cards2Mask(Card.str2Cards("QsKc")), Card.cards2Mask(Card.str2Cards("3dAc8")), Card.cards2Mask(Card.str2Cards("8h")), emptyList));
		game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, "opp1", Card.cards2Mask(Card.str2Cards("As")), Card.cards2Mask(Card.str2Cards("7d8c7h")), Card.cards2Mask(Card.str2Cards("4s")), emptyList));
		game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, "opp2", Card.cards2Mask(Card.str2Cards("QhQd")), Card.cards2Mask(Card.str2Cards("6sAdKh")), Card.cards2Mask(Card.str2Cards("")), emptyList));
		game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("4hTh9s"))));
		game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, game.heroName, Card.cards2Mask(Card.str2Cards("")), Card.cards2Mask(Card.str2Cards("")), Card.cards2Mask(Card.str2Cards("4hTh")), new ArrayList<>(Arrays.asList(Card.str2Cards("9s")))));
		game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, "opp1", Card.cards2Mask(Card.str2Cards("")), Card.cards2Mask(Card.str2Cards("")), Card.cards2Mask(Card.str2Cards("JdJs")), emptyList));
		game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, "opp2", Card.cards2Mask(Card.str2Cards("")), Card.cards2Mask(Card.str2Cards("")), Card.cards2Mask(Card.str2Cards("2d9d")), emptyList));
		game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("2c5h9h"))));

		System.out.println(game.toString());

		Config cfg = new Config();
		cfg.RANDOM_DEAL_COUNT = 15000;
		Config.FANTASY_SCORE = 15;
		Config.EvaluationMethod = Config.EvaluationMethodKind.BOARD_ACROSS;
		GameOfcMctsSimple stateSimple = new GameOfcMctsSimple(game, new NatureSpaceExt(game, cfg));
		long timeBefore = Utils.getTime();
		EventOfcMctsSimple decision = Mcs.monteCarloSimulation(stateSimple, 0);
//		EventOfcMctsSimple decision = Mcs.monteCarloSimulation(stateSimple);
//		EventOfcMctsSimple decision = Mcs.monteCarloSimulation(stateSimple, 0, 4);
		System.out.println(Utils.getTime() - timeBefore);
		System.out.println(String.format("RANDOM_DEAL_COUNT = %d", cfg.RANDOM_DEAL_COUNT));
		System.out.println(decision.toEventOfc(game.heroName).toString());

	}

	public void testNotLikeAI7() throws Exception {
		GameOfc game = new GameOfc(Nw.Ppp, 100);
		game.id = "220505095551-44429199-0000035-1";
		game.addPlayer(new PlayerOfc("opp1", 1520));
		game.addPlayer(new PlayerOfc("hero", 1520));
		game.heroName = "hero";
		game.initButtonName("opp1");
		game.gameMode = GameMode.GAME_MODE_OFC_PROGRESSIVE;

		List<Card> emptyList = new ArrayList<>();

		game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("6hTh4h2h2d"))));
		game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, game.heroName, Card.cards2Mask(Card.str2Cards("")), Card.cards2Mask(Card.str2Cards("6hTh4h")), Card.cards2Mask(Card.str2Cards("2h2d")), emptyList));
		game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, "opp1", Card.cards2Mask(Card.str2Cards("")), Card.cards2Mask(Card.str2Cards("6s8h6c")), Card.cards2Mask(Card.str2Cards("5dJd")), emptyList));
		game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("2c2s4c"))));
		game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, game.heroName, Card.cards2Mask(Card.str2Cards("")), Card.cards2Mask(Card.str2Cards("")), Card.cards2Mask(Card.str2Cards("2c2s")), new ArrayList<>(Arrays.asList(Card.str2Cards("4c")))));
		game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, "opp1", Card.cards2Mask(Card.str2Cards("")), 0, Card.cards2Mask(Card.str2Cards("Kd8d")), emptyList));
		game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("3c9dKs"))));

		System.out.println(game.toString());

		Config cfg = new Config();
		cfg.RANDOM_DEAL_COUNT = 20000;
		Config.FANTASY_SCORE = 15;
//		Config.FAIL_PENALTY = -3 -3;
		GameOfcMctsSimple stateSimple = new GameOfcMctsSimple(game, new NatureSpace(game, cfg));
		long timeBefore = Utils.getTime();
//		EventOfcMctsSimple decision = Mcs.monteCarloSimulation(stateSimple, 0);
//		EventOfcMctsSimple decision = Mcs.monteCarloSimulation(stateSimple);
		EventOfcMctsSimple decision = Mcs.monteCarloSimulation(stateSimple, 0, 4);
		System.out.println(Utils.getTime() - timeBefore);
		System.out.println(String.format("RANDOM_DEAL_COUNT = %d", cfg.RANDOM_DEAL_COUNT));
		System.out.println(decision.toEventOfc(game.heroName).toString());

	}

	public void testNotLikeAI9_1() throws Exception {
		GameOfc game = new GameOfc(Nw.Ppp, 100);
		game.id = "21177423-5";
		game.addPlayer(new PlayerOfc("opp1", 1520));
		game.addPlayer(new PlayerOfc("hero", 1520));
		game.heroName = "hero";
		game.initButtonName("hero");
		game.gameMode = GameMode.GAME_MODE_OFC_PROGRESSIVE;

		List<Card> emptyList = new ArrayList<>();

		game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, "opp1", Card.cards2Mask(Card.str2Cards("As")), Card.cards2Mask(Card.str2Cards("7h7d")), Card.cards2Mask(Card.str2Cards("8cJd")), emptyList));
		game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("Qs7c4h4c9d"))));

		System.out.println(game.toString());

		Config cfg = new Config();
		cfg.RANDOM_DEAL_COUNT = 10000;
		Config.FANTASY_SCORE = 15;
		GameOfcMctsSimple stateSimple = new GameOfcMctsSimple(game, new NatureSpace(game, cfg));
		long timeBefore = Utils.getTime();
		EventOfcMctsSimple decision = Mcs.monteCarloSimulation(stateSimple, 0);
//		EventOfcMctsSimple decision = Mcs.monteCarloSimulation(stateSimple);
//		EventOfcMctsSimple decision = Mcs.monteCarloSimulation(stateSimple, 0, 4);
		System.out.println(Utils.getTime() - timeBefore);
		System.out.println(String.format("RANDOM_DEAL_COUNT = %d", cfg.RANDOM_DEAL_COUNT));
		System.out.println(decision.toEventOfc(game.heroName).toString());

	}

	public void testNotLikeAI10() throws Exception {
		GameOfc game = new GameOfc(Nw.Ppp, 100);
		game.id = "220601204400-45623273-0000016-1";
		game.addPlayer(new PlayerOfc("opp1", 1520));
		game.addPlayer(new PlayerOfc("hero", 1520));
		game.heroName = "hero";
		game.initButtonName("opp1");
		game.gameMode = GameMode.GAME_MODE_OFC_PROGRESSIVE;

		List<Card> emptyList = new ArrayList<>();

		game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("9hAc7d8h5c"))));
		game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, game.heroName, Card.cards2Mask(Card.str2Cards("")), Card.cards2Mask(Card.str2Cards("9hAc7d8h")), Card.cards2Mask(Card.str2Cards("5c")), emptyList));
		game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, "opp1", Card.cards2Mask(Card.str2Cards("")), Card.cards2Mask(Card.str2Cards("6s4d6c6d")), Card.cards2Mask(Card.str2Cards("9d")), emptyList));
		game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("KhQc9c"))));
		game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, game.heroName, Card.cards2Mask(Card.str2Cards("KhQc")), Card.cards2Mask(Card.str2Cards("")), Card.cards2Mask(Card.str2Cards("")), new ArrayList<>(Arrays.asList(Card.str2Cards("9c")))));
		game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, "opp1", Card.cards2Mask(Card.str2Cards("KcKd")), 0, Card.cards2Mask(Card.str2Cards("")), emptyList));
		game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("5s2h9s"))));
		game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, game.heroName, Card.cards2Mask(Card.str2Cards("")), Card.cards2Mask(Card.str2Cards("")), Card.cards2Mask(Card.str2Cards("5s2h")), new ArrayList<>(Arrays.asList(Card.str2Cards("9s")))));
		game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, "opp1", Card.cards2Mask(Card.str2Cards("")), Card.cards2Mask(Card.str2Cards("")), Card.cards2Mask(Card.str2Cards("TcJh")), emptyList));
		game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("TdTsQd"))));

		System.out.println(game.toString());

		Config cfg = new Config();
		cfg.RANDOM_DEAL_COUNT = 20000;
		Config.FANTASY_SCORE = 15;
		Config.FAIL_PENALTY = -1;
		GameOfcMctsSimple stateSimple = new GameOfcMctsSimple(game, new NatureSpace(game, cfg));
		long timeBefore = Utils.getTime();
		EventOfcMctsSimple decision = Mcs.monteCarloSimulation(stateSimple, 0);
//		EventOfcMctsSimple decision = Mcs.monteCarloSimulation(stateSimple);
//		EventOfcMctsSimple decision = Mcs.monteCarloSimulation(stateSimple, 0, 4);
		System.out.println(Utils.getTime() - timeBefore);
		System.out.println(String.format("RANDOM_DEAL_COUNT = %d", cfg.RANDOM_DEAL_COUNT));
		System.out.println(decision.toEventOfc(game.heroName).toString());

	}

	public void testNotLikeAI3() throws Exception {
		GameOfc game = new GameOfc(Nw.Ppp, 100);
		game.id = "220414023117-43510531-0000140-1";
		game.addPlayer(new PlayerOfc("opp1", 1520));
		game.addPlayer(new PlayerOfc("hero", 1520));
		game.heroName = "hero";
		game.initButtonName("hero");
		game.gameMode = GameMode.GAME_MODE_OFC_PROGRESSIVE;

		List<Card> emptyList = new ArrayList<>();

		game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, "opp1", Card.cards2Mask(Card.str2Cards("Kc6c9s")), Card.cards2Mask(Card.str2Cards("2sQs")), Card.cards2Mask(Card.str2Cards("")), emptyList));
		game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("JcKd3s7c7h"))));
		game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, game.heroName, Card.cards2Mask(Card.str2Cards("JcKd")), Card.cards2Mask(Card.str2Cards("3s7c7h")), Card.cards2Mask(Card.str2Cards("")), emptyList));
		game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, "opp1", Card.cards2Mask(Card.str2Cards("")), Card.cards2Mask(Card.str2Cards("AcKs")), Card.cards2Mask(Card.str2Cards("")), emptyList));
		game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("8h8c5s"))));
		game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, game.heroName, Card.cards2Mask(Card.str2Cards("")), Card.cards2Mask(Card.str2Cards("")), Card.cards2Mask(Card.str2Cards("8h8c")), new ArrayList<>(Arrays.asList(Card.str2Cards("5s")))));
		game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, "opp1", Card.cards2Mask(Card.str2Cards("")), 0, Card.cards2Mask(Card.str2Cards("5d6d")), emptyList));
		game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("Td4cAh"))));
		game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, game.heroName, Card.cards2Mask(Card.str2Cards("")), Card.cards2Mask(Card.str2Cards("")), Card.cards2Mask(Card.str2Cards("Td4c")), new ArrayList<>(Arrays.asList(Card.str2Cards("Ah")))));
		game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, "opp1", Card.cards2Mask(Card.str2Cards("")), Card.cards2Mask(Card.str2Cards("")), Card.cards2Mask(Card.str2Cards("Jd3d")), emptyList));
		game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("2d7d9h"))));

		System.out.println(game.toString());

		Config cfg = new Config();
		cfg.RANDOM_DEAL_COUNT = 20000;
		Config.FANTASY_SCORE = 15;
//		Config.FAIL_PENALTY = -3 -3;
		GameOfcMctsSimple stateSimple = new GameOfcMctsSimple(game, new NatureSpace(game, cfg));
		long timeBefore = Utils.getTime();
		EventOfcMctsSimple decision = Mcs.monteCarloSimulation(stateSimple, 0);
//		EventOfcMctsSimple decision = Mcs.monteCarloSimulation(stateSimple);
//		EventOfcMctsSimple decision = Mcs.monteCarloSimulation(stateSimple, 0, 4);
		System.out.println(Utils.getTime() - timeBefore);
		System.out.println(String.format("RANDOM_DEAL_COUNT = %d", cfg.RANDOM_DEAL_COUNT));
		System.out.println(decision.toEventOfc(game.heroName).toString());

	}

	public void testNotLikeAI13() throws Exception {
		GameOfc game = new GameOfc(Nw.Ppp, 100);
		game.id = "334648317";
		game.addPlayer(new PlayerOfc("opp1", 1520));
		game.addPlayer(new PlayerOfc("hero", 1520));
		game.heroName = "hero";
		game.initButtonName("hero");
		game.gameMode = GameMode.GAME_MODE_OFC_PROGRESSIVE;

		List<Card> emptyList = new ArrayList<>();

		game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, "opp1", Card.cards2Mask(Card.str2Cards("QcQh9h")), Card.cards2Mask(Card.str2Cards("AdAc")), Card.cards2Mask(Card.str2Cards("")), emptyList));
		game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("QsQdKhKd9c"))));
		game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, game.heroName, Card.cards2Mask(Card.str2Cards("QsQd")), Card.cards2Mask(Card.str2Cards("KhKd9c")), Card.cards2Mask(Card.str2Cards("")), emptyList));
		game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, "opp1", Card.cards2Mask(Card.str2Cards("")), Card.cards2Mask(Card.str2Cards("4h5c")), Card.cards2Mask(Card.str2Cards("")), emptyList));
		game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("Ah2sTc"))));
		game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, game.heroName, Card.cards2Mask(Card.str2Cards("")), Card.cards2Mask(Card.str2Cards("Ah")), Card.cards2Mask(Card.str2Cards("2s")), new ArrayList<>(Arrays.asList(Card.str2Cards("Tc")))));
		game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, "opp1", Card.cards2Mask(Card.str2Cards("")), 0, Card.cards2Mask(Card.str2Cards("6sTs")), emptyList));
		game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("5s7sJc"))));
		game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, game.heroName, Card.cards2Mask(Card.str2Cards("")), Card.cards2Mask(Card.str2Cards("")), Card.cards2Mask(Card.str2Cards("5s7s")), new ArrayList<>(Arrays.asList(Card.str2Cards("Jc")))));
		game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, "opp1", Card.cards2Mask(Card.str2Cards("")), Card.cards2Mask(Card.str2Cards("")), Card.cards2Mask(Card.str2Cards("9sJs")), emptyList));
		game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("3h7dAs"))));

		System.out.println(game.toString());

		Config cfg = new Config();
		cfg.RANDOM_DEAL_COUNT = 20000;
		Config.FANTASY_SCORE = 15;
//		Config.FAIL_PENALTY = 0;
		GameOfcMctsSimple stateSimple = new GameOfcMctsSimple(game, new NatureSpace(game, cfg));
		long timeBefore = Utils.getTime();
		EventOfcMctsSimple decision = Mcs.monteCarloSimulation(stateSimple, 0);
//		EventOfcMctsSimple decision = Mcs.monteCarloSimulation(stateSimple);
//		EventOfcMctsSimple decision = Mcs.monteCarloSimulation(stateSimple, 0, 4);
		System.out.println(Utils.getTime() - timeBefore);
		System.out.println(String.format("RANDOM_DEAL_COUNT = %d", cfg.RANDOM_DEAL_COUNT));
		System.out.println(decision.toEventOfc(game.heroName).toString());

	}

	public void testNotLikeAI14() throws Exception {
//		http://10.211.59.133:8089/bestmove?hero=6s%2F9c+8c+Ts+Qs%2F7d+8d+Td+Ad&newCards=2s+7c+Kc&opp=Ks+2h+9d%2F3d+Jd+As+6d%2F2c+3c+6c+4c&dead=3s+Ac&button=0&table=47033135&rules=progressive16_refant14_nojokers&account=pid7568880&appName=Ppp&clubId=3109882&stakes=0.50&price=1USD&gameId=220703065557-47033135-0000027-1&timeLimit=15&fastObvious&partner=crowneco-ufxfyajbxx
//		6s 2s / 9c 8c Ts Qs Kc / 7d 8d Td Ad
//		http://13.49.155.94:8000/bestmove?hero=6s%2F9c+8c+Ts+Qs%2F7d+8d+Td+Ad&newCards=2s+7c+Kc&opp=Ks+2h+9d%2F3d+Jd+As+6d%2F2c+3c+6c+4c&dead=3s+Ac&button=0&table=test&rules=progressive16_refant14_nojokers&account=pid7568880&appName=Ppp&clubId=3109882&stakes=0.50&price=1USD&gameId=220703065557-47033135-0000027-1&timeLimit=15&fastObvious&partner=crowneco-ufxfyajbxx
//		6s 2s 7c / 9c 8c Ts Qs / 7d 8d Td Ad
//		http://nsk.convexbytes.com:15273/bestmove?hero=6s%2F9c+8c+Ts+Qs%2F7d+8d+Td+Ad&newCards=2s+7c+Kc&opp=Ks+2h+9d%2F3d+Jd+As+6d%2F2c+3c+6c+4c&dead=3s+Ac&button=0&table=test&rules=progressive16_refant14_nojokers&account=pid7568880&appName=Ppp&clubId=3109882&stakes=0.50&price=1USD&gameId=220703065557-47033135-0000027-1&timeLimit=15&fastObvious&partner=0
//		6s 2s 7c / 9c 8c Ts Qs / 7d 8d Td Ad

		GameOfc game = new GameOfc(Nw.Ppp, 100);
		game.id = "220703065557-47033135-0000027-1";
		game.addPlayer(new PlayerOfc("opp1", 1520));
		game.addPlayer(new PlayerOfc("hero", 1520));
		game.heroName = "hero";
		game.initButtonName("hero");
		game.gameMode = GameMode.GAME_MODE_OFC_PROGRESSIVE;

		List<Card> emptyList = new ArrayList<>();

		game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, "opp1", Card.cards2Mask(Card.str2Cards("")), Card.cards2Mask(Card.str2Cards("As")), Card.cards2Mask(Card.str2Cards("2c3c6c4c")), emptyList));
		game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("6s9c8cTsQs"))));
		game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, game.heroName, Card.cards2Mask(Card.str2Cards("6s")), Card.cards2Mask(Card.str2Cards("9c8cTsQs")), Card.cards2Mask(Card.str2Cards("")), emptyList));
		game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, "opp1", Card.cards2Mask(Card.str2Cards("")), Card.cards2Mask(Card.str2Cards("3dJd")), Card.cards2Mask(Card.str2Cards("")), emptyList));
		game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("7d8d3s"))));
		game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, game.heroName, Card.cards2Mask(Card.str2Cards("")), Card.cards2Mask(Card.str2Cards("")), Card.cards2Mask(Card.str2Cards("7d8d")), new ArrayList<>(Arrays.asList(Card.str2Cards("3s")))));
		game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, "opp1", Card.cards2Mask(Card.str2Cards("Ks2h")), 0, Card.cards2Mask(Card.str2Cards("")), emptyList));
		game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("TdAdAc"))));
		game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, game.heroName, Card.cards2Mask(Card.str2Cards("")), Card.cards2Mask(Card.str2Cards("")), Card.cards2Mask(Card.str2Cards("TdAd")), new ArrayList<>(Arrays.asList(Card.str2Cards("Ac")))));
		game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, "opp1", Card.cards2Mask(Card.str2Cards("9d")), Card.cards2Mask(Card.str2Cards("6d")), Card.cards2Mask(Card.str2Cards("")), emptyList));
		game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("2s7cKc"))));

		System.out.println(game.toString());

		Config cfg = new Config();
		cfg.RANDOM_DEAL_COUNT = 20000;
		Config.FANTASY_SCORE = 15;
		Config.FAIL_PENALTY = -1; // -1 better than -3 in this case
//		NatureSpace natureSpace = new NatureSpace(game, cfg);
		NatureSpace natureSpace = new NatureSpaceExt(game, cfg);
		GameOfcMctsSimple stateSimple = new GameOfcMctsSimple(game, natureSpace);
		long timeBefore = Utils.getTime();
		EventOfcMctsSimple decision = Mcs.monteCarloSimulation(stateSimple, 0);
//		EventOfcMctsSimple decision = Mcs.monteCarloSimulation(stateSimple);
//		EventOfcMctsSimple decision = Mcs.monteCarloSimulation(stateSimple, 0, 4);
		System.out.println(Utils.getTime() - timeBefore);
		System.out.println(String.format("RANDOM_DEAL_COUNT = %d", cfg.RANDOM_DEAL_COUNT));
		System.out.println(decision.toEventOfc(game.heroName).toString());

	}

	public void testNotLikeAI15() throws Exception {
//		http://10.211.59.133:8089/bestmove?hero=Qc%2FJd+3s+Ks%2F2h+5h+7h&newCards=2d+3c+8d&opp=Ac+Ah%2F4c+3d%2F9h+Th+Jh%09As%2F2c+5s+5c+8s%2F6d+7d+9d+4d&dead=7s&button=1&table=443-1c426558&rules=progressive16_refant14_nojokers&account=Wifi_onZone01&appName=Spartan&clubId=0&stakes=5.00&price=1INR&gameId=356860800&timeLimit=15&fastObvious&partner=altai-zxgsejynkd&timeDurationMs=4408
//		Qc 8d / Jd 3s Ks / 2h 5h 7h 2d
//		Qc 8d / Jd 3s Ks / 2h 5h 7h 2d
//
//		http://13.49.155.94:8000/bestmove?hero=Qc%2FJd+3s+Ks%2F2h+5h+7h&newCards=2d+3c+8d&opp=Ac+Ah%2F4c+3d%2F9h+Th+Jh%09As%2F2c+5s+5c+8s%2F6d+7d+9d+4d&dead=7s&button=1&table=test&rules=progressive16_refant14_nojokers&account=Wifi_onZone01&appName=Spartan&clubId=0&stakes=5.00&price=1INR&gameId=356860800&timeLimit=15&fastObvious&partner=altai-zxgsejynkd
//		Qc 2d / Jd 3s Ks 3c / 2h 5h 7h
//
//		http://nsk.convexbytes.com:15273/bestmove?hero=Qc%2FJd+3s+Ks%2F2h+5h+7h&newCards=2d+3c+8d&opp=Ac+Ah%2F4c+3d%2F9h+Th+Jh%09As%2F2c+5s+5c+8s%2F6d+7d+9d+4d&dead=7s&button=1&table=test&rules=progressive16_refant14_nojokers&account=Wifi_onZone01&appName=Spartan&clubId=0&stakes=5.00&price=1INR&gameId=356860800&timeLimit=15&fastObvious&partner=0
//		Qc 2d / Jd 3s Ks 3c / 2h 5h 7h

		GameOfc game = new GameOfc(Nw.Spartan, 100);
		game.id = "356860800";
		game.addPlayer(new PlayerOfc("opp1", 1520));
		game.addPlayer(new PlayerOfc("hero", 1520));
		game.addPlayer(new PlayerOfc("opp2", 1520));
		game.heroName = "hero";
		game.initButtonName("opp2");
		game.gameMode = GameMode.GAME_MODE_OFC_PROGRESSIVE;

		List<Card> emptyList = new ArrayList<>();

		game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, "opp1", Card.cards2Mask(Card.str2Cards("As")), Card.cards2Mask(Card.str2Cards("2c5s5c8s")), Card.cards2Mask(Card.str2Cards("")), emptyList));
		game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("QcJd3sKs2h"))));
		game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, game.heroName, Card.cards2Mask(Card.str2Cards("Qc")), Card.cards2Mask(Card.str2Cards("Jd3sKs")), Card.cards2Mask(Card.str2Cards("2h")), emptyList));
		game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, "opp2", Card.cards2Mask(Card.str2Cards("AcAh")), Card.cards2Mask(Card.str2Cards("4c3d")), Card.cards2Mask(Card.str2Cards("9h")), emptyList));
		game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, "opp1", Card.cards2Mask(Card.str2Cards("")), Card.cards2Mask(Card.str2Cards("")), Card.cards2Mask(Card.str2Cards("6d7d")), emptyList));
		game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("5h7h7s"))));
		game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, game.heroName, Card.cards2Mask(Card.str2Cards("")), Card.cards2Mask(Card.str2Cards("")), Card.cards2Mask(Card.str2Cards("5h7h")), new ArrayList<>(Arrays.asList(Card.str2Cards("7s")))));
		game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, "opp2", Card.cards2Mask(Card.str2Cards("")), 0, Card.cards2Mask(Card.str2Cards("ThJh")), emptyList));
		game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, "opp1", Card.cards2Mask(Card.str2Cards("")), 0, Card.cards2Mask(Card.str2Cards("9d4d")), emptyList));
		game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("2d3c8d"))));

		System.out.println(game.toString());

		Config.EvaluationMethod = Config.EvaluationMethodKind.SINGLE_HERO;
		Config.DEPTH_OF_SEARCH = 10;
		Config.OPP_RANDOM_DEAL_COUNT = 1000;
		Config.DEBUG_PRINT = true;
		Config.FAIL_PENALTY = -3;

//		EventOfc result = EurekaRunner.run(game, 15000, 17000);
		Config cfg = new Config();
		NatureSpace natureSpace = new NatureSpaceExt(game, cfg);
		GameOfcMctsSimple stateSimple = new GameOfcMctsSimple(game, natureSpace);
		long timeBefore = Utils.getTime();
		EventOfcMctsSimple decision = Mcs.monteCarloSimulation(stateSimple, 0);

		System.out.println(String.format("MCS decision in %d ms: \n%s", Utils.getTime() - timeBefore, decision.toEventOfc(game.heroName).toString()));

	}

	public void testNotLikeAI16() throws Exception {

		GameOfc game = new GameOfc(Nw.Upoker, 100);
		game.id = "21168314-10";
		game.addPlayer(new PlayerOfc("opp1", 1520));
		game.addPlayer(new PlayerOfc("hero", 1520));
		game.heroName = "hero";
		game.initButtonName(game.heroName);
		game.gameMode = GameMode.GAME_MODE_OFC_PROGRESSIVE;

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

		System.out.println(game.toString());

		Config.EvaluationMethod = Config.EvaluationMethodKind.SINGLE_HERO;
		Config.DEPTH_OF_SEARCH = 10;
		Config.OPP_RANDOM_DEAL_COUNT = 100;
		Config.DEBUG_PRINT = true;
		Config.FANTASY_SCORE = 0;
		Config.FAIL_PENALTY = 0;

//		EventOfc result = EurekaRunner.run(game, 15000, 17000);
		Config cfg = new Config();
		NatureSpace natureSpace = new NatureSpaceExt(game, cfg);
		GameOfcMctsSimple stateSimple = new GameOfcMctsSimple(game, natureSpace);
		long timeBefore = Utils.getTime();
		EventOfcMctsSimple decision = Mcs.monteCarloSimulation(stateSimple, 0);

		System.out.println(String.format("MCS decision in %d ms: \n%s", Utils.getTime() - timeBefore, decision.toEventOfc(game.heroName).toString()));

	}

	public void testNotLikeAI17() throws Exception {

		GameOfc game = new GameOfc(Nw.Upoker, 100);
		game.id = "21173170-13";
		game.addPlayer(new PlayerOfc("opp1", 1520));
		game.addPlayer(new PlayerOfc("hero", 1520));
		game.heroName = "hero";
		game.initButtonName(game.heroName);
		game.gameMode = GameMode.GAME_MODE_OFC_PROGRESSIVE;

		List<Card> emptyList = new ArrayList<>();

		game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, "opp1", Card.cards2Mask(Card.str2Cards("As")), Card.cards2Mask(Card.str2Cards("9sQc")), Card.cards2Mask(Card.str2Cards("KdKc")), emptyList));
		game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("Qh8d9d9cTh"))));
		game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, game.heroName, Card.cards2Mask(Card.str2Cards("Qh")), Card.cards2Mask(Card.str2Cards("8d")), Card.cards2Mask(Card.str2Cards("9d9cTh")), emptyList));
		game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, "opp1", Card.cards2Mask(Card.str2Cards("")), Card.cards2Mask(Card.str2Cards("Qs")), Card.cards2Mask(Card.str2Cards("Kh")), emptyList));
		game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("3s6s4s"))));
		game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, game.heroName, Card.cards2Mask(Card.str2Cards("")), Card.cards2Mask(Card.str2Cards("3s6s")), Card.cards2Mask(Card.str2Cards("")), new ArrayList<>(Arrays.asList(Card.str2Cards("4s")))));
		game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, "opp1", Card.cards2Mask(Card.str2Cards("")), Card.cards2Mask(Card.str2Cards("Jd")), Card.cards2Mask(Card.str2Cards("7s")), emptyList));
		game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("3hTcJs"))));
		game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, game.heroName, Card.cards2Mask(Card.str2Cards("")), Card.cards2Mask(Card.str2Cards("3h")), Card.cards2Mask(Card.str2Cards("Tc")), new ArrayList<>(Arrays.asList(Card.str2Cards("Js")))));
		game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, "opp1", Card.cards2Mask(Card.str2Cards("2c")), 0, Card.cards2Mask(Card.str2Cards("Ks")), emptyList));
		game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("Jc2s5s"))));

		System.out.println(game.toString());

		Config.EvaluationMethod = Config.EvaluationMethodKind.SINGLE_HERO;
		Config.DEPTH_OF_SEARCH = 10;
		Config.OPP_RANDOM_DEAL_COUNT = 100;
		Config.DEBUG_PRINT = true;
		Config.FANTASY_SCORE = 15;
		Config.FAIL_PENALTY = -3;

//		EventOfc result = EurekaRunner.run(game, 15000, 17000);
		Config cfg = new Config();
		NatureSpace natureSpace = new NatureSpaceExt(game, cfg);
		GameOfcMctsSimple stateSimple = new GameOfcMctsSimple(game, natureSpace);
		long timeBefore = Utils.getTime();
		EventOfcMctsSimple decision = Mcs.monteCarloSimulation(stateSimple, 0);

		System.out.println(String.format("MCS decision in %d ms: \n%s", Utils.getTime() - timeBefore, decision.toEventOfc(game.heroName).toString()));

	}

	public void testNotLikeAI18() throws Exception {

		GameOfc game = new GameOfc(Nw.Upoker, 100);
		game.id = "21173170-16";
		game.addPlayer(new PlayerOfc("opp1", 1520));
		game.addPlayer(new PlayerOfc("hero", 1520));
		game.heroName = "hero";
		game.initButtonName("opp1");
		game.gameMode = GameMode.GAME_MODE_OFC_PROGRESSIVE;

		List<Card> emptyList = new ArrayList<>();

		game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("Ad9s5h5d5s"))));
		game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, game.heroName, Card.cards2Mask(Card.str2Cards("Ad")), Card.cards2Mask(Card.str2Cards("9s")), Card.cards2Mask(Card.str2Cards("5h5d5s")), emptyList));
		game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, "opp1", Card.cards2Mask(Card.str2Cards("As")), Card.cards2Mask(Card.str2Cards("7c9c")), Card.cards2Mask(Card.str2Cards("TcTs")), emptyList));
		game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("2s5cKh"))));
		game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, game.heroName, Card.cards2Mask(Card.str2Cards("")), Card.cards2Mask(Card.str2Cards("2s")), Card.cards2Mask(Card.str2Cards("5c")), new ArrayList<>(Arrays.asList(Card.str2Cards("Kh")))));
		game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, "opp1", Card.cards2Mask(Card.str2Cards("Ah")), Card.cards2Mask(Card.str2Cards("")), Card.cards2Mask(Card.str2Cards("Js")), emptyList));
		game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("Kd7hTd"))));
		game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, game.heroName, Card.cards2Mask(Card.str2Cards("Kd")), Card.cards2Mask(Card.str2Cards("")), Card.cards2Mask(Card.str2Cards("7h")), new ArrayList<>(Arrays.asList(Card.str2Cards("Td")))));
		game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, "opp1", Card.cards2Mask(Card.str2Cards("")), Card.cards2Mask(Card.str2Cards("9d")), Card.cards2Mask(Card.str2Cards("Th")), emptyList));
		game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("4h3h8s"))));

		System.out.println(game.toString());

		Config.EvaluationMethod = Config.EvaluationMethodKind.SINGLE_HERO;
		Config.DEPTH_OF_SEARCH = 10;
		Config.OPP_RANDOM_DEAL_COUNT = 100;
		Config.DEBUG_PRINT = true;
		Config.FANTASY_SCORE = 15;
		Config.FAIL_PENALTY = -3;

//		EventOfc result = EurekaRunner.run(game, 15000, 17000);
		Config cfg = new Config();
		NatureSpace natureSpace = new NatureSpaceExt(game, cfg);
		GameOfcMctsSimple stateSimple = new GameOfcMctsSimple(game, natureSpace);
		long timeBefore = Utils.getTime();
		EventOfcMctsSimple decision = Mcs.monteCarloSimulation(stateSimple, 0);

		System.out.println(String.format("MCS decision in %d ms: \n%s", Utils.getTime() - timeBefore, decision.toEventOfc(game.heroName).toString()));

	}

	public void testNotLikeAI19() throws Exception {

		GameOfc game = new GameOfc(Nw.Upoker, 100);
		game.id = "21178407-12";
		game.addPlayer(new PlayerOfc("opp1", 1520));
		game.addPlayer(new PlayerOfc("hero", 1520));
		game.heroName = "hero";
		game.initButtonName("opp1");
		game.gameMode = GameMode.GAME_MODE_OFC_PROGRESSIVE;

		List<Card> emptyList = new ArrayList<>();

		game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("Qs6s7dJhJd"))));
		game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, game.heroName, Card.cards2Mask(Card.str2Cards("Qs")), Card.cards2Mask(Card.str2Cards("6s")), Card.cards2Mask(Card.str2Cards("7dJhJd")), emptyList));
		game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, "opp1", Card.cards2Mask(Card.str2Cards("")), Card.cards2Mask(Card.str2Cards("3d3s9h")), Card.cards2Mask(Card.str2Cards("4cKc")), emptyList));
		game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("2h8s4h"))));
		game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, game.heroName, Card.cards2Mask(Card.str2Cards("")), Card.cards2Mask(Card.str2Cards("2h8s")), Card.cards2Mask(Card.str2Cards("")), new ArrayList<>(Arrays.asList(Card.str2Cards("4h")))));
		game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, "opp1", Card.cards2Mask(Card.str2Cards("")), Card.cards2Mask(Card.str2Cards("")), Card.cards2Mask(Card.str2Cards("7cJc")), emptyList));
		game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("8d5h8c"))));
		game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, game.heroName, Card.cards2Mask(Card.str2Cards("")), Card.cards2Mask(Card.str2Cards("8d")), Card.cards2Mask(Card.str2Cards("5h")), new ArrayList<>(Arrays.asList(Card.str2Cards("8c")))));
		game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, "opp1", Card.cards2Mask(Card.str2Cards("Ac")), Card.cards2Mask(Card.str2Cards("8h")), Card.cards2Mask(Card.str2Cards("")), emptyList));
		game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("9sQhAh"))));

		System.out.println(game.toString());

		Config.EvaluationMethod = Config.EvaluationMethodKind.BOARD_ACROSS;
		Config.DEPTH_OF_SEARCH = 10;
		Config.OPP_RANDOM_DEAL_COUNT = 100;
		Config.DEBUG_PRINT = true;
		Config.FANTASY_SCORE = 15;
		Config.FAIL_PENALTY = -1;

//		EventOfc result = EurekaRunner.run(game, 15000, 17000);
		Config cfg = new Config();
		NatureSpace natureSpace = new NatureSpaceExt(game, cfg);
		GameOfcMctsSimple stateSimple = new GameOfcMctsSimple(game, natureSpace);
		long timeBefore = Utils.getTime();
		EventOfcMctsSimple decision = Mcs.monteCarloSimulation(stateSimple, 0);

		System.out.println(String.format("MCS decision in %d ms: \n%s", Utils.getTime() - timeBefore, decision.toEventOfc(game.heroName).toString()));

	}

	public static void main(String[] args) throws Exception {
    	OfcMcsTest test = new OfcMcsTest();
    	test.testNotLikeAI19();
    }
}
