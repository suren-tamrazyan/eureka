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
    	GameOfcMctsSimple stateSimple = new GameOfcMctsSimple(game, new EurekaRunner(game, cfg));
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
    	GameOfcMctsSimple stateSimple = new GameOfcMctsSimple(game, new EurekaRunner(game, cfg));
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
    	GameOfcMctsSimple stateSimple = new GameOfcMctsSimple(game, new EurekaRunner(game, cfg));
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
    	GameOfcMctsSimple stateSimple = new GameOfcMctsSimple(game, new EurekaRunner(game, cfg));
    	long timeBefore = Utils.getTime();
    	EventOfcMctsSimple decision = Mcs.monteCarloSimulation(stateSimple, 0);
    	System.out.println(Utils.getTime() - timeBefore);
    	System.out.println(decision.toEventOfc(game.heroName).toString());
    	System.out.println("Nature samples:");
    	for (EventOfcMctsSimple natureSamp : stateSimple.getOwnerClosure().natureSamples) {
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
        	EventOfc result = Mcs.monteCarloSimulation(new GameOfcMctsSimple(game, new EurekaRunner(game, cfg)), 0).toEventOfc(game.heroName);//EurekaRunner.run(game, 4000);
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
        	GameOfcMctsSimple stateSimple = new GameOfcMctsSimple(game, new EurekaRunner(game, cfg));
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
		GameOfcMctsSimple stateSimple = new GameOfcMctsSimple(game, new EurekaRunner(game, cfg));
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
		GameOfcMctsSimple stateSimple = new GameOfcMctsSimple(game, new EurekaRunner(game, cfg));
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
		GameOfcMctsSimple stateSimple = new GameOfcMctsSimple(game, new EurekaRunner(game, cfg));
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
		GameOfcMctsSimple stateSimple = new GameOfcMctsSimple(game, new EurekaRunner(game, cfg));
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
		GameOfcMctsSimple stateSimple = new GameOfcMctsSimple(game, new EurekaRunner(game, cfg));
		long timeBefore = Utils.getTime();
		EventOfcMctsSimple decision = Mcs.monteCarloSimulation(stateSimple, 0);
//		EventOfcMctsSimple decision = Mcs.monteCarloSimulation(stateSimple);
//		EventOfcMctsSimple decision = Mcs.monteCarloSimulation(stateSimple, 0, 4);
		System.out.println(Utils.getTime() - timeBefore);
		System.out.println(String.format("RANDOM_DEAL_COUNT = %d", cfg.RANDOM_DEAL_COUNT));
		System.out.println(decision.toEventOfc(game.heroName).toString());

	}

	public static void main(String[] args) throws Exception {
    	OfcMcsTest test = new OfcMcsTest();
    	test.testNotLikeAI7();
    }
}
