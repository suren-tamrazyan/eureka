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
        
    public static void main(String[] args) throws Exception {
    	OfcMcsTest test = new OfcMcsTest();
    	test.testNotLikeAI1();
    }
}
