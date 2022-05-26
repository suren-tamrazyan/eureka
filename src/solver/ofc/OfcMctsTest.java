package solver.ofc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import game.Card;
import game.EventOfc;
import game.Game.Nw;
import game.GameOfc;
import game.GameOfc.GameMode;
import game.PlayerOfc;

public class OfcMctsTest {

    public void test() throws Exception {
    	GameOfcMcts state = new GameOfcMcts(Nw.Ppp, 100);
    	state.id = "mcts test";
    	state.addPlayer(new PlayerOfc("YYDS", 10000));
    	state.addPlayer(new PlayerOfc("ppp535339", 10000));
    	state.addPlayer(new PlayerOfc("hero", 10000));
    	state.heroName = "hero";
    	state.initButtonName("YYDS");
    	state.gameMode = GameMode.GAME_MODE_REGULAR;
    	
    	List<Card> emptyList = new ArrayList<>();
    	EventOfc ev = new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, "ppp535339", 0, Card.cards2Mask(Card.str2Cards("Tc")), Card.cards2Mask(Card.str2Cards("9s8s7s5s")), emptyList);
    	state.procEvent(ev);
    	ev = new EventOfc(EventOfc.TYPE_DEAL_CARDS, state.heroName, Card.cards2Mask(Card.str2Cards("Qd5hKd2cKh")));
    	state.procEvent(ev);
    	System.out.println(state.toString());
    	
    	long timeBefore = Utils.getTime();
    	EventOfc decision = EurekaRunner.run(state);
    	System.out.println(Utils.getTime() - timeBefore);
    	System.out.println(decision.toString());
    }
    
    public void testVI() throws Exception {
    	GameOfcMcts state = new GameOfcMcts(Nw.Ppp, 100);
    	state.id = "mcts test";
    	state.addPlayer(new PlayerOfc("YYDS", 10000));
    	state.addPlayer(new PlayerOfc("ppp535339", 10000));
    	state.addPlayer(new PlayerOfc("hero", 10000));
    	state.heroName = "hero";
    	state.initButtonName("YYDS");
    	state.gameMode = GameMode.GAME_MODE_REGULAR;
    	
    	List<Card> emptyList = new ArrayList<>();
    	EventOfc ev = new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, "ppp535339", 0, Card.cards2Mask(Card.str2Cards("KhKs")), Card.cards2Mask(Card.str2Cards("2s4sJc")), emptyList);
    	state.procEvent(ev);
    	ev = new EventOfc(EventOfc.TYPE_DEAL_CARDS, state.heroName, Card.cards2Mask(Card.str2Cards("2h5h9dThJh")));
    	state.procEvent(ev);
    	System.out.println(state.toString());
    	
    	long timeBefore = Utils.getTime();
    	EventOfc decision = EurekaRunner.run(state);
    	System.out.println(Utils.getTime() - timeBefore);
    	System.out.println(decision.toString());
    }
    
    public void testVII() throws Exception {
    	GameOfcMcts state = new GameOfcMcts(Nw.Ppp, 100);
    	state.id = "mcts test";
    	state.addPlayer(new PlayerOfc("hero", 10000));
    	state.addPlayer(new PlayerOfc("ppp535339", 10000));
    	state.heroName = "hero";
    	state.initButtonName("hero");
    	state.gameMode = GameMode.GAME_MODE_REGULAR;
    	
    	List<Card> emptyList = new ArrayList<>();
    	EventOfc ev = new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, "ppp535339", Card.cards2Mask(Card.str2Cards("8cTcQh")), 0, Card.cards2Mask(Card.str2Cards("KcAs")), emptyList);
    	state.procEvent(ev);
    	ev = new EventOfc(EventOfc.TYPE_DEAL_CARDS, state.heroName, Card.cards2Mask(Card.str2Cards("6dTdJdQdKd")));
    	state.procEvent(ev);
    	System.out.println(state.toString());
    	
    	long timeBefore = Utils.getTime();
    	EventOfc decision = EurekaRunner.run(state);
    	System.out.println(Utils.getTime() - timeBefore);
    	System.out.println(decision.toString());
    }
   
    
    public void testII() throws Exception {
    	GameOfcMcts state = new GameOfcMcts(Nw.Ppp, 100);
    	state.id = "mcts test II";
    	state.addPlayer(new PlayerOfc("YYDS", 10000));
    	state.addPlayer(new PlayerOfc("ppp535339", 10000));
    	state.addPlayer(new PlayerOfc("hero", 10000));
    	state.heroName = "hero";
    	state.initButtonName("ppp535339");
    	state.gameMode = GameMode.GAME_MODE_REGULAR;
    	
    	EventOfc ev = new EventOfc(EventOfc.TYPE_DEAL_CARDS, state.heroName, Card.cards2Mask(Card.str2Cards("2d4d7dTsKs")));
    	state.procEvent(ev);
    	System.out.println(state);
    	
    	long timeBefore = Utils.getTime();
    	EventOfc decision = EurekaRunner.run(state);
    	System.out.println(Utils.getTime() - timeBefore);
    	System.out.println(decision.toString());
    }
    
    public void testIII() throws Exception {
    	GameOfcMcts state = new GameOfcMcts(Nw.Ppp, 100);
    	state.id = "mcts test II";
    	state.addPlayer(new PlayerOfc("YYDS", 10000));
    	state.addPlayer(new PlayerOfc("ppp535339", 10000));
    	state.addPlayer(new PlayerOfc("hero", 10000));
    	state.heroName = "hero";
    	state.initButtonName("ppp535339");
    	state.gameMode = GameMode.GAME_MODE_REGULAR;
    	
    	EventOfc ev = new EventOfc(EventOfc.TYPE_DEAL_CARDS, state.heroName, Card.cards2Mask(Card.str2Cards("3h3c3s7c8s")));
    	state.procEvent(ev);
    	System.out.println(state);
    	
    	long timeBefore = Utils.getTime();
    	EventOfc decision = EurekaRunner.run(state);
    	System.out.println(Utils.getTime() - timeBefore);
    	System.out.println(decision.toString());
    }
    
    public void testIV() throws Exception {
    	GameOfcMcts game = new GameOfcMcts(Nw.Ppp, 100);
    	game.id = "mcts test";
    	game.addPlayer(new PlayerOfc("YYDS", 10000));
    	game.addPlayer(new PlayerOfc("ppp535339", 10000));
    	game.addPlayer(new PlayerOfc("hero", 10000));
    	game.heroName = "hero";
    	game.initButtonName("ppp535339");
    	game.gameMode = GameMode.GAME_MODE_REGULAR;
    	
    	List<Card> emptyList = new ArrayList<>();
    	EventOfc ev = new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("4s7h9cTdAs")));
    	game.procEvent(ev);
    	ev = new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, "hero", Card.cards2Mask(Card.str2Cards("4s7h9c")), 0, Card.cards2Mask(Card.str2Cards("TdAs")), emptyList);
    	game.procEvent(ev);
    	ev = new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, "YYDS", Card.cards2Mask(Card.str2Cards("2h3c8d")), 0, Card.cards2Mask(Card.str2Cards("7d7s")), emptyList);
    	game.procEvent(ev);
    	ev = new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, "ppp535339", Card.cards2Mask(Card.str2Cards("7cQcAh")), Card.cards2Mask(Card.str2Cards("Ad")), Card.cards2Mask(Card.str2Cards("Ts")), emptyList);
    	game.procEvent(ev);
    	ev = new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("2d3hJs")));
    	game.procEvent(ev);
    	System.out.println(game);
    	
    	long timeBefore = Utils.getTime();
    	EventOfc decision = EurekaRunner.run(game);
    	System.out.println(Utils.getTime() - timeBefore);
    	System.out.println(decision.toString());
    }
    
    public void testV() throws Exception {
    	GameOfcMcts state = new GameOfcMcts(Nw.Ppp, 100);
    	state.id = "mcts test V";
    	state.addPlayer(new PlayerOfc("YYDS", 10000));
    	state.addPlayer(new PlayerOfc("ppp535339", 10000));
    	state.addPlayer(new PlayerOfc("hero", 10000));
    	state.heroName = "hero";
    	state.initButtonName("ppp535339");
    	state.gameMode = GameMode.GAME_MODE_REGULAR;
    	
    	EventOfc ev = new EventOfc(EventOfc.TYPE_DEAL_CARDS, state.heroName, Card.cards2Mask(Card.str2Cards("4s7h9cTdAs")));
    	state.procEvent(ev);
    	System.out.println(state);
    	
    	long timeBefore = Utils.getTime();
    	EventOfc decision = EurekaRunner.run(state);
    	System.out.println(Utils.getTime() - timeBefore);
    	System.out.println(decision.toString());
    }
    
    public void testX() throws Exception {
    	GameOfc game = new GameOfc(Nw.Ppp, 100);
    	game.id = "mcts test";
    	game.addPlayer(new PlayerOfc("pid6365117", 10000));
    	game.addPlayer(new PlayerOfc("pid6258836", 10000));
    	game.addPlayer(new PlayerOfc("pid6281006", 10000));
    	game.heroName = "pid6365117";
    	game.initButtonName("pid6281006");
    	game.gameMode = GameMode.GAME_MODE_REGULAR;
    	
    	EventOfc ev = new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("QsJs6s6d2s")));
    	game.procEvent(ev);
    	System.out.println(game.toString());
    	
    	long timeBefore = Utils.getTime();
    	EventOfc decision = EurekaRunner.run(game);
    	System.out.println(Utils.getTime() - timeBefore);
    	System.out.println(decision.toString());
    }
    
    public void testXX() throws Exception {
    	GameOfc game = new GameOfc(Nw.Ppp, 100);
    	game.id = "mcts test";
    	game.addPlayer(new PlayerOfc("YYDS", 10000));
    	game.addPlayer(new PlayerOfc("ppp535339", 10000));
    	game.addPlayer(new PlayerOfc("hero", 10000));
    	game.heroName = "hero";
    	game.initButtonName("YYDS");
    	game.gameMode = GameMode.GAME_MODE_REGULAR;
    	
    	List<Card> emptyList = new ArrayList<>();
    	EventOfc ev = new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, "ppp535339", 0, Card.cards2Mask(Card.str2Cards("Tc")), Card.cards2Mask(Card.str2Cards("9s8s7s5s")), emptyList);
    	game.procEvent(ev);
    	ev = new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("Qd5hKd2cKh")));
    	game.procEvent(ev);
    	System.out.println(game.toString());
    	
    	long timeBefore = Utils.getTime();
    	EventOfc decision = EurekaRunner.run(game);
    	System.out.println(Utils.getTime() - timeBefore);
    	System.out.println(decision.toString());
    }

    public void testXXX() throws Exception {
    	GameOfc game = new GameOfc(Nw.Ppp, 100);
    	game.id = "mcts test";
    	game.addPlayer(new PlayerOfc("YYDS", 10000));
    	game.addPlayer(new PlayerOfc("hero", 10000));
    	game.heroName = "hero";
    	game.initButtonName("hero");
    	game.gameMode = GameMode.GAME_MODE_REGULAR;
    	
    	List<Card> emptyList = new ArrayList<>();
    	EventOfc ev = new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, "YYDS", 0, Card.cards2Mask(Card.str2Cards("6sAdAc")), Card.cards2Mask(Card.str2Cards("9sQh")), emptyList);
    	game.procEvent(ev);
    	ev = new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("3d4c5s7d7c")));
    	game.procEvent(ev);
    	System.out.println(game.toString());
    	
    	long timeBefore = Utils.getTime();
    	EventOfc decision = EurekaRunner.run(game);
    	System.out.println(Utils.getTime() - timeBefore);
    	System.out.println(decision.toString());
    }

    public void testXXXX() throws Exception {
    	GameOfc game = new GameOfc(Nw.Ppp, 100);
    	game.id = "mcts test";
    	game.addPlayer(new PlayerOfc("pid6365117", 10000));
    	game.addPlayer(new PlayerOfc("pid6258836", 10000));
    	game.addPlayer(new PlayerOfc("pid6281006", 10000));
    	game.heroName = "pid6365117";
    	game.initButtonName("pid6281006");
    	game.gameMode = GameMode.GAME_MODE_REGULAR;
    	
//    	EventOfc ev = new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("3h3s6h9hQs")));
    	EventOfc ev = new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("4h4d8c9dTs")));
    	game.procEvent(ev);
    	System.out.println(game.toString());
    	
    	long timeBefore = Utils.getTime();
    	EventOfc decision = EurekaRunner.run(game);
    	System.out.println(Utils.getTime() - timeBefore);
    	System.out.println(decision.toString());
    }
    
    public void testLastMove() throws Exception {
    	GameOfc game = new GameOfc(Nw.Ppp, 100);
    	game.id = "211115021216-37100219-0000024-1";
    	game.addPlayer(new PlayerOfc("pid6691575", 1520));
    	game.addPlayer(new PlayerOfc("pid6691608", 480));
    	game.heroName = "pid6691575";
    	game.initButtonName("pid6691575");
    	game.gameMode = GameMode.GAME_MODE_OFC_PROGRESSIVE;
    	
    	List<Card> emptyList = new ArrayList<>();
    	
    	game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("QsQdJsJd3d"))));
    	game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, "pid6691608", Card.cards2Mask(Card.str2Cards("9h")), Card.cards2Mask(Card.str2Cards("2d5hTc")), Card.cards2Mask(Card.str2Cards("As")), emptyList));
    	game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, "pid6691575", 0, Card.cards2Mask(Card.str2Cards("3d")), Card.cards2Mask(Card.str2Cards("JdJsQdQs")), emptyList));
    	game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("Qh5d4s"))));
    	game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, "pid6691608", 0, 0, Card.cards2Mask(Card.str2Cards("8c8s")), emptyList));
    	game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, "pid6691575", Card.cards2Mask(Card.str2Cards("")), Card.cards2Mask(Card.str2Cards("5d")), Card.cards2Mask(Card.str2Cards("Qh")), new ArrayList<>(Arrays.asList(Card.str2Cards("4s")))));
    	game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("Ad6s6h"))));
    	game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, "pid6691608", Card.cards2Mask(Card.str2Cards("6d")), Card.cards2Mask(Card.str2Cards("4h")), Card.cards2Mask(Card.str2Cards("")), emptyList));
    	game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, "pid6691575", Card.cards2Mask(Card.str2Cards("6h")), Card.cards2Mask(Card.str2Cards("Ad")), Card.cards2Mask(Card.str2Cards("")), new ArrayList<>(Arrays.asList(Card.str2Cards("6s")))));
    	game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("AcKd5c"))));
    	game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, "pid6691608", Card.cards2Mask(Card.str2Cards("2h")), Card.cards2Mask(Card.str2Cards("2s")), Card.cards2Mask(Card.str2Cards("")), emptyList));
    	game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, "pid6691575", Card.cards2Mask(Card.str2Cards("Ac")), Card.cards2Mask(Card.str2Cards("Kd")), Card.cards2Mask(Card.str2Cards("")), new ArrayList<>(Arrays.asList(Card.str2Cards("5c")))));
    	game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("Ah9s9c"))));
    	game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, "pid6691608", Card.cards2Mask(Card.str2Cards("")), Card.cards2Mask(Card.str2Cards("")), Card.cards2Mask(Card.str2Cards("4dTd")), emptyList));
    	
    	System.out.println(game.toString());
    	
    	long timeBefore = Utils.getTime();
    	EventOfc decision = EurekaRunner.run(game);
    	System.out.println(Utils.getTime() - timeBefore);
    	System.out.println(decision.toString());
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
    	
    	long timeBefore = Utils.getTime();
    	EventOfc decision = EurekaRunner.run(game);
    	System.out.println(Utils.getTime() - timeBefore);
    	System.out.println(decision.toString());
    }
    
    public void testXI() throws Exception {
    	GameOfc game = new GameOfc(Nw.Ppp, 100);
    	game.id = "mcts test";
    	game.addPlayer(new PlayerOfc("YYDS", 10000));
    	game.addPlayer(new PlayerOfc("hero", 10000));
    	game.heroName = "hero";
    	game.initButtonName("hero");
    	game.gameMode = GameMode.GAME_MODE_REGULAR;
    	
    	List<Card> emptyList = new ArrayList<>();
    	EventOfc ev = new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, "YYDS", 0, Card.cards2Mask(Card.str2Cards("4cAd")), Card.cards2Mask(Card.str2Cards("5h5c7h")), emptyList);
    	game.procEvent(ev);
    	ev = new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("2c3s6s9cQc")));
    	game.procEvent(ev);
    	System.out.println(game.toString());
    	
    	long timeBefore = Utils.getTime();
    	EventOfc decision = EurekaRunner.run(game);
    	System.out.println(Utils.getTime() - timeBefore);
    	System.out.println(decision.toString());
    }
    
    public void testXII() throws Exception {
    	GameOfc game = new GameOfc(Nw.Ppp, 100);
    	game.id = "mcts test";
    	game.addPlayer(new PlayerOfc("YYDS", 10000));
    	game.addPlayer(new PlayerOfc("hero", 10000));
    	game.heroName = "hero";
    	game.initButtonName("hero");
    	game.gameMode = GameMode.GAME_MODE_REGULAR;
    	
    	List<Card> emptyList = new ArrayList<>();
    	EventOfc ev = new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, "YYDS", Card.cards2Mask(Card.str2Cards("Kc")), Card.cards2Mask(Card.str2Cards("3h4c")), Card.cards2Mask(Card.str2Cards("9dTs")), emptyList);
    	game.procEvent(ev);
    	ev = new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("4h6c7h9cAc")));
    	game.procEvent(ev);
    	System.out.println(game.toString());
    	
    	long timeBefore = Utils.getTime();
    	EventOfc decision = EurekaRunner.run(game);
    	System.out.println(Utils.getTime() - timeBefore);
    	System.out.println(decision.toString());
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
    	
    	long timeBefore = Utils.getTime();
    	EventOfc decision = EurekaRunner.run(game);
    	System.out.println(Utils.getTime() - timeBefore);
    	System.out.println(decision.toString());
    }

    public void testNotGoodMoveOnFourRoundII() throws Exception { // this all rright
    	GameOfc game = new GameOfc(Nw.Ppp, 100);
    	game.id = "220214170351-41019722-0000003-1";
    	game.addPlayer(new PlayerOfc("pid7057264", 1520));
    	game.addPlayer(new PlayerOfc("pid7016970", 480));
    	game.heroName = "pid7057264";
    	game.initButtonName("pid7016970");
    	game.gameMode = GameMode.GAME_MODE_OFC_PROGRESSIVE;
    	
    	List<Card> emptyList = new ArrayList<>();
    	
    	game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("Qs8h3s3c3d"))));
    	game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, game.heroName, Card.cards2Mask(Card.str2Cards("Qs")), Card.cards2Mask(Card.str2Cards("8h")), Card.cards2Mask(Card.str2Cards("3d3c3s")), emptyList));
    	game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, "pid7016970", Card.cards2Mask(Card.str2Cards("")), Card.cards2Mask(Card.str2Cards("6c7d")), Card.cards2Mask(Card.str2Cards("2cQdQc")), emptyList));
    	game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("AsAd9s"))));
    	game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, game.heroName, Card.cards2Mask(Card.str2Cards("")), Card.cards2Mask(Card.str2Cards("AdAs")), Card.cards2Mask(Card.str2Cards("")), new ArrayList<>(Arrays.asList(Card.str2Cards("9s")))));
    	game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, "pid7016970", Card.cards2Mask(Card.str2Cards("9c")), Card.cards2Mask(Card.str2Cards("5h")), Card.cards2Mask(Card.str2Cards("")), new ArrayList<>(Arrays.asList(Card.str2Cards("")))));
    	game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("5s5c5d"))));
    	game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, game.heroName, Card.cards2Mask(Card.str2Cards("")), Card.cards2Mask(Card.str2Cards("")), Card.cards2Mask(Card.str2Cards("5d5c")), new ArrayList<>(Arrays.asList(Card.str2Cards("5s")))));
    	game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, "pid7016970", Card.cards2Mask(Card.str2Cards("Kh")), Card.cards2Mask(Card.str2Cards("7s")), Card.cards2Mask(Card.str2Cards("")), new ArrayList<>(Arrays.asList(Card.str2Cards("")))));
    	game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("Ah8c8d"))));
    	
    	System.out.println(game.toString());
    	
    	long timeBefore = Utils.getTime();
    	EventOfc decision = EurekaRunner.run(game);
    	System.out.println(Utils.getTime() - timeBefore);
    	System.out.println(decision.toString());
    }

    public void testLastMoveII() throws Exception {
    	/*not enough precision, not use kicker*/
    	GameOfc game = new GameOfc(Nw.Ppp, 100);
    	game.id = "220214021830-40992954-0000006-1";
    	game.addPlayer(new PlayerOfc("pid2733650", 1520));
    	game.addPlayer(new PlayerOfc("pid6849971", 480));
    	game.heroName = "pid6849971";
    	game.initButtonName("pid2733650");
    	game.getPlayer("pid2733650").playFantasy = true;
    	game.gameMode = GameMode.GAME_MODE_OFC_ULTIMATE;
    	
    	List<Card> emptyList = new ArrayList<>();
    	
    	game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("Ks8c7d5s3s"))));
    	game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, game.heroName, Card.cards2Mask(Card.str2Cards("7d")), Card.cards2Mask(Card.str2Cards("8c")), Card.cards2Mask(Card.str2Cards("3s5sKs")), emptyList));
    	game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("Qd3d2s"))));
    	game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, game.heroName, Card.cards2Mask(Card.str2Cards("")), Card.cards2Mask(Card.str2Cards("Qd")), Card.cards2Mask(Card.str2Cards("2s")), new ArrayList<>(Arrays.asList(Card.str2Cards("3d")))));
    	game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("Ts9h6d"))));
    	game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, game.heroName, Card.cards2Mask(Card.str2Cards("")), Card.cards2Mask(Card.str2Cards("9h")), Card.cards2Mask(Card.str2Cards("Ts")), new ArrayList<>(Arrays.asList(Card.str2Cards("6d")))));
    	game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("Jh9s7h"))));
    	game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, game.heroName, Card.cards2Mask(Card.str2Cards("7h")), Card.cards2Mask(Card.str2Cards("9s")), Card.cards2Mask(Card.str2Cards("")), new ArrayList<>(Arrays.asList(Card.str2Cards("Jh")))));
    	game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("KcKd3c"))));
    	
    	System.out.println(game.toString());
    	
    	long timeBefore = Utils.getTime();
    	EventOfc decision = EurekaRunner.run(game);
    	System.out.println(Utils.getTime() - timeBefore);
    	System.out.println(decision.toString());
    }

    public void testCheckMoveOnSecondRound() throws Exception {
    	/*not enough precision, not use kicker*/
    	GameOfc game = new GameOfc(Nw.Ppp, 100);
    	game.id = "220214200108-41024543-0000016-1";
    	game.addPlayer(new PlayerOfc("pid6814040", 1520));
    	game.addPlayer(new PlayerOfc("pid535057", 480));
    	game.heroName = "pid6814040";
    	game.initButtonName("pid535057");
    	game.getPlayer("pid535057").playFantasy = true;
    	game.gameMode = GameMode.GAME_MODE_OFC_ULTIMATE;
    	
    	List<Card> emptyList = new ArrayList<>();
    	
    	game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("KsKdQd6c2c"))));
    	game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, game.heroName, Card.cards2Mask(Card.str2Cards("Qd")), Card.cards2Mask(Card.str2Cards("KdKs")), Card.cards2Mask(Card.str2Cards("2c6c")), emptyList));
    	game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("AhKc4h"))));
    	
    	System.out.println(game.toString());
    	
    	long timeBefore = Utils.getTime();
    	EventOfc decision = EurekaRunner.run(game);
    	System.out.println(Utils.getTime() - timeBefore);
    	System.out.println(decision.toString());
    }

    public void testFantasy() throws Exception {
    	GameOfc game = new GameOfc(Nw.Ppp, 100);
    	game.id = "220308161554-41953321-0000019-1";
    	game.addPlayer(new PlayerOfc("pid7190998", 1520));
    	game.addPlayer(new PlayerOfc("pid7188974", 480));
    	game.heroName = "pid7190998";
    	PlayerOfc hero = game.getPlayer(game.heroName);
    	game.initButtonName("pid7190998");
    	hero.playFantasy = true;
    	hero.fantasyCardCount = 14;
    	game.gameMode = GameMode.GAME_MODE_OFC_ULTIMATE;
    	
    	List<Card> emptyList = new ArrayList<>();
    	
    	game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("4d4c4s5h6s7d7s8h8sJhJdJcQhAh"))));
    	
    	System.out.println(game.toString());
    	
    	long timeBefore = Utils.getTime();
    	EventOfc decision = Heuristics.fantasyCompletion(hero.boxFront.toList(), hero.boxMiddle.toList(), hero.boxBack.toList(), hero.cardsToBeBoxed, 85000).toEventOfc(game.heroName);
    	System.out.println(Utils.getTime() - timeBefore);
    	System.out.println(decision);
    }
    
    public void testNotLikeAI1() {
//    	10 to middle
//    	http://10.211.59.133:8089/bestmove?hero=Jd%2F2c+Tc+Td%2F6h+6d+6s&newCards=2h+8h+Ts&opp=Qh+3c%2F3d+3s+4d+4c%2F9h+9d+7s&dead=5h&button=0&table=43510531&rules=classic&account=pid7339538&appName=Ppp&clubId=3489347&stakes=0.10&price=1USD&gameId=220414023117-43510531-0000224-1&timeLimit=15&fastObvious&partner=crowneco-ufxfyajbxx
//    	Jd / 2c Tc Td Ts / 6h 6d 6s 2h
//
//    	http://13.49.155.94:8000/bestmove?hero=Jd%2F2c+Tc+Td%2F6h+6d+6s&newCards=2h+8h+Ts&opp=Qh+3c%2F3d+3s+4d+4c%2F9h+9d+7s&dead=5h&button=0&table=test&rules=classic&account=pid7339538&appName=Ppp&clubId=3489347&stakes=0.10&price=1USD&gameId=220414023117-43510531-0000224-1&timeLimit=15&fastObvious&partner=crowneco-ufxfyajbxx
//    	Jd / 2c Tc Td 2h / 6h 6d 6s 8h
//
//    	http://nsk.convexbytes.com:15273/bestmove?hero=Jd%2F2c+Tc+Td%2F6h+6d+6s&newCards=2h+8h+Ts&opp=Qh+3c%2F3d+3s+4d+4c%2F9h+9d+7s&dead=5h&button=0&table=test&rules=classic&account=pid7339538&appName=Ppp&clubId=3489347&stakes=0.10&price=1USD&gameId=220414023117-43510531-0000224-1&timeLimit=15&fastObvious&partner=0
//    	Jd / 2c Tc Td 2h / 6h 6d 6s 8h
    	
    	System.out.println("start!");
    	long timeBefore = Utils.getTime();
    	long tsec = 10;
    	EventOfc decision = EurekaRunner.run(Arrays.asList(Card.str2Cards("Jd")), Arrays.asList(Card.str2Cards("2cTcTd")), Arrays.asList(Card.str2Cards("6h6d6s")), Arrays.asList(Card.str2Cards("2h8hTs")),  Arrays.asList(Card.str2Cards("Qh3c3d3s4d4c9h9d7s5h")), GameMode.GAME_MODE_REGULAR, 3, "HeroName", /*60*/tsec*1000, 1700000);
    	System.out.println(Utils.getTime() - timeBefore);
    	System.out.println(decision.toString());
    }
    
    public void testNotLikeAI2() {
//    		http://10.211.59.133:8089/bestmove?hero=Qd%2F8c+Ks%2F6d+Jd+6c+6s&newCards=5s+8h+8d&opp=Td%2F8s+Js+7d%2FQh+Qc+Qs&dead=5h&button=1&table=test&rules=classic&account=pid7339538&appName=Ppp&clubId=3489347&stakes=0.10&price=1USD&gameId=220414023150-43510554-0000103-1&timeLimit=15&fastObvious&partner=crowneco-ufxfyajbxx
//    		Qd / 8c Ks 8h 8d / 6d Jd 6c 6s
//
//    		http://13.49.155.94:8000/bestmove?hero=Qd%2F8c+Ks%2F6d+Jd+6c+6s&newCards=5s+8h+8d&opp=Td%2F8s+Js+7d%2FQh+Qc+Qs&dead=5h&button=1&table=test&rules=classic&account=pid7339538&appName=Ppp&clubId=3489347&stakes=0.10&price=1USD&gameId=220414023150-43510554-0000103-1&timeLimit=15&fastObvious&partner=crowneco-ufxfyajbxx
//    		Qd / 8c Ks 5s 8h / 6d Jd 6c 6s
//
//    		http://nsk.convexbytes.com:15273/bestmove?hero=Qd%2F8c+Ks%2F6d+Jd+6c+6s&newCards=5s+8h+8d&opp=Td%2F8s+Js+7d%2FQh+Qc+Qs&dead=5h&button=1&table=test&rules=classic&account=pid7339538&appName=Ppp&clubId=3489347&stakes=0.10&price=1USD&gameId=220414023150-43510554-0000103-1&timeLimit=15&fastObvious&partner=0
//    		Qd / 8c Ks 5s 8d / 6d Jd 6c 6s
    	
    	System.out.println("start!");
    	long timeBefore = Utils.getTime();
    	long tsec = 60;
    	EventOfc decision = EurekaRunner.run(Arrays.asList(Card.str2Cards("Qd")), Arrays.asList(Card.str2Cards("8cKs")), Arrays.asList(Card.str2Cards("6dJd6c6s")), Arrays.asList(Card.str2Cards("5s8h8d")),  Arrays.asList(Card.str2Cards("Td8sJs7dQhQcQs5h")), GameMode.GAME_MODE_REGULAR, 3, "HeroName", /*60*/tsec*1000, 1700000);
    	System.out.println(Utils.getTime() - timeBefore);
    	System.out.println(decision.toString());
    }
    
    public void testNotLikeAI3() {
//    		http://10.211.59.133:8089/bestmove?hero=Jc+Kd%2F3s+7c+7h%2F8h+8c+Td+4c&newCards=2d+7d+9h&opp=Kc+6c+9s%2F2s+Qs+Ac+Ks%2F5d+6d+Jd+3d&dead=5s+Ah&button=0&table=test&rules=classic&account=pid7339538&appName=Ppp&clubId=3489347&stakes=0.10&price=1USD&gameId=220414023117-43510531-0000140-1&timeLimit=15&fastObvious&partner=crowneco-ufxfyajbxx
//    		Jc Kd / 3s 7c 7h 7d 9h / 8h 8c Td 4c
//
//    		http://13.49.155.94:8000/bestmove?hero=Jc+Kd%2F3s+7c+7h%2F8h+8c+Td+4c&newCards=2d+7d+9h&opp=Kc+6c+9s%2F2s+Qs+Ac+Ks%2F5d+6d+Jd+3d&dead=5s+Ah&button=0&table=test&rules=classic&account=pid7339538&appName=Ppp&clubId=3489347&stakes=0.10&price=1USD&gameId=220414023117-43510531-0000140-1&timeLimit=15&fastObvious&partner=crowneco-ufxfyajbxx
//    		Jc Kd 9h / 3s 7c 7h 2d / 8h 8c Td 4c
//
//    		http://nsk.convexbytes.com:15273/bestmove?hero=Jc+Kd%2F3s+7c+7h%2F8h+8c+Td+4c&newCards=2d+7d+9h&opp=Kc+6c+9s%2F2s+Qs+Ac+Ks%2F5d+6d+Jd+3d&dead=5s+Ah&button=0&table=test&rules=classic&account=pid7339538&appName=Ppp&clubId=3489347&stakes=0.10&price=1USD&gameId=220414023117-43510531-0000140-1&timeLimit=15&fastObvious&partner=0
//    		Jc Kd 9h / 3s 7c 7h 2d / 8h 8c Td 4c
    	
    	System.out.println("start!");
    	long timeBefore = Utils.getTime();
    	long tsec = 10;
    	EventOfc decision = EurekaRunner.run(Arrays.asList(Card.str2Cards("JcKd")), Arrays.asList(Card.str2Cards("3s7c7h")), Arrays.asList(Card.str2Cards("8h8cTd4c")), Arrays.asList(Card.str2Cards("2d7d9h")),  Arrays.asList(Card.str2Cards("Kc6c9s2sQsAcKs5d6dJd3d5sAh")), GameMode.GAME_MODE_REGULAR, 4, "HeroName", /*60*/tsec*1000, 1700000);
    	System.out.println(Utils.getTime() - timeBefore);
    	System.out.println(decision.toString());
    	
    }

	public void testNotLikeAI6() {

		System.out.println("start!");
		long timeBefore = Utils.getTime();
		long tsec = 5;
		EventOfc decision = EurekaRunner.run(Arrays.asList(Card.str2Cards("QsKc")), Arrays.asList(Card.str2Cards("3dAc")), Arrays.asList(Card.str2Cards("8h4hTh")), Arrays.asList(Card.str2Cards("2c5h9h")),  Arrays.asList(Card.str2Cards("As7d8c7h4sJdJsQhQd6sAdKh2d9d9s")), GameMode.GAME_MODE_REGULAR, 3, "HeroName", /*60*/tsec*1000, 1700000);
		System.out.println(Utils.getTime() - timeBefore);
		System.out.println(decision.toString());

	}

	public void testNotLikeAI7() {

		System.out.println("start!");
		long timeBefore = Utils.getTime();
		long tsec = 10;
		EventOfc decision = EurekaRunner.run(Arrays.asList(Card.str2Cards("")), Arrays.asList(Card.str2Cards("6hTh4h")), Arrays.asList(Card.str2Cards("2h2d2c2s")), Arrays.asList(Card.str2Cards("3c9dKs")),  Arrays.asList(Card.str2Cards("6s8h6c5dJdKd8d4c")), GameMode.GAME_MODE_REGULAR, 3, "HeroName", /*60*/tsec*1000, 1700000);
		System.out.println(Utils.getTime() - timeBefore);
		System.out.println(decision.toString());

	}

	public void testNotLikeAI8() {

		System.out.println("start!");
		long timeBefore = Utils.getTime();
		long tsec = 5;
		EventOfc decision = EurekaRunner.run(Arrays.asList(Card.str2Cards("Qd")), Arrays.asList(Card.str2Cards("KsKh")), Arrays.asList(Card.str2Cards("2c3h5s3c")), Arrays.asList(Card.str2Cards("5h9hQc")),  Arrays.asList(Card.str2Cards("AhTs2s8h8d7cJcKdKc7h")), GameMode.GAME_MODE_REGULAR, 3, "HeroName", /*60*/tsec*1000, 1700000);
		System.out.println(Utils.getTime() - timeBefore);
		System.out.println(decision.toString());

	}

	public void testNotLikeAI9_1() {

		System.out.println("start!");
		long timeBefore = Utils.getTime();
		long tsec = 10;
		EventOfc decision = EurekaRunner.run(Arrays.asList(Card.str2Cards("")), Arrays.asList(Card.str2Cards("")), Arrays.asList(Card.str2Cards("")), Arrays.asList(Card.str2Cards("Qs7c4h4c9d")),  Arrays.asList(Card.str2Cards("As7h7d8cJd")), GameMode.GAME_MODE_REGULAR, 1, "HeroName", /*60*/tsec*1000, 1700000);
		System.out.println(Utils.getTime() - timeBefore);
		System.out.println(decision.toString());

	}

	public static void main(String[] args) throws Exception {
		OfcMctsTest test = new OfcMctsTest();
//		Config.RANDOM_DEAL_COUNT = 10000;
//		Config.NUMBER_OF_ITERATIONS = 20000;
//		Config.EXPLORATION_PARAMETER = 30;
		test.testNotLikeAI9_1();
    	
//    	LinkedHashMap<String, Integer> sortedMap = new LinkedHashMap<>();
//    	OfcMctsSimpleRunner.numberTakesOfNatureSimulations.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).forEachOrdered(x -> sortedMap.put(x.getKey(), x.getValue()));
//    	int simulationCount = sortedMap.entrySet().stream().mapToInt(x -> x.getValue() + 1).sum();
//    	System.out.println(Misc.sf("simulationCount (depth) = %d, variationCount (width) = %d", simulationCount, sortedMap.size()));
//    	String strMap = sortedMap.toString();
//    	System.out.println(Misc.sf("Sorted Map numberTakesOfNatureSimulations : %s", strMap));
	}

}
