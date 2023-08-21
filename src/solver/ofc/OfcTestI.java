package solver.ofc;

import game.*;
import solver.ofc.mcts.Mcts;
import util.Misc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OfcTestI {
    public static void test1_1() throws Exception {
/*
  old variant http://10.211.59.133:8089/bestmove?hero=%2F%2F&newCards=6c+7h+9s+Qs+Ac&opp=%2F%2F&dead=&button=1&table=test&rules=progressive16_refant14_nojokers&account=pid7568847&appName=Ppp&clubId=3109882&stakes=0.50&price=0.001USD&gameId=221106145621-52798420-0000004-1&timeLimit=15&fastObvious&partner=altai-zxgsejynkd
              Qs / 9s Ac / 6c 7h

        http://13.49.160.164:8000/bestmove?hero=%2F%2F&newCards=6c+7h+9s+Qs+Ac&opp=%2F%2F&dead=&button=1&table=test&rules=progressive16_refant14_nojokers&account=pid7568847&appName=Ppp&clubId=3109882&stakes=0.50&price=0.001USD&gameId=221106145621-52798420-0000004-1&timeLimit=15&fastObvious&partner=altai-zxgsejynkd
        Ac / 6c 7h / 9s Qs

        http://nsk.convexbytes.com:15273/bestmove?hero=%2F%2F&newCards=6c+7h+9s+Qs+Ac&opp=%2F%2F&dead=&button=1&table=test&rules=progressive16_refant14_nojokers&account=pid7568847&appName=Ppp&clubId=3109882&stakes=0.50&price=0.001USD&gameId=221106145621-52798420-0000004-1&timeLimit=15&fastObvious&partner=altai-zxgsejynkd
        Ac / 6c 7h / 9s Qs

        self
        Ac / 6c 7h / 9s Qs
        ok, right
*/
        GameOfc game = new GameOfc(Game.Nw.Spartan, 100);
        game.id = "414960953";
        game.addPlayer(new PlayerOfc("opp1", 1520));
        game.addPlayer(new PlayerOfc("hero", 1520));
        game.heroName = "hero";
        game.initButtonName("opp1");
        game.gameMode = GameOfc.GameMode.GAME_MODE_REGULAR;

        List<Card> emptyList = new ArrayList<>();

        game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("6c7h9sQsAc"))));

        System.out.println(game);

        Config.EvaluationMethod = Config.EvaluationMethodKind.SINGLE_HERO;
        Config.DEBUG_PRINT = false;
        Config.FANTASY_SCORE = 7;
        Config.FAIL_PENALTY = -3;

        long timeBefore = Utils.getTime();
        EventOfc result = EurekaRunner.run(game, 0, 8000);

        System.out.println(String.format("MCTS decision in %d ms: \n%s", Utils.getTime() - timeBefore, result.toString()));
    }

    public static void test1_2() throws Exception {
/*
  old variant http://10.211.59.133:8089/bestmove?hero=Ac%2F6c+7h%2F9s+Qs&newCards=6h+8c+9d&opp=Kd%2FAs%2F4d+Jh+Js&dead=&button=1&table=test&rules=progressive16_refant14_nojokers&account=pid7568847&appName=Ppp&clubId=3109882&stakes=0.50&price=0.001USD&gameId=221106145621-52798420-0000004-1&timeLimit=15&fastObvious&partner=altai-zxgsejynkd
              Ac / 6c 7h 6h / 9s Qs 9d

        http://13.49.160.164:8000/bestmove?hero=Ac%2F6c+7h%2F9s+Qs&newCards=6h+8c+9d&opp=Kd%2FAs%2F4d+Jh+Js&dead=&button=1&table=test&rules=progressive16_refant14_nojokers&account=pid7568847&appName=Ppp&clubId=3109882&stakes=0.50&price=0.001USD&gameId=221106145621-52798420-0000004-1&timeLimit=15&fastObvious&partner=altai-zxgsejynkd
        Ac / 6c 7h 6h / 9s Qs 9d

        http://nsk.convexbytes.com:15273/bestmove?hero=Ac%2F6c+7h%2F9s+Qs&newCards=6h+8c+9d&opp=Kd%2FAs%2F4d+Jh+Js&dead=&button=1&table=test&rules=progressive16_refant14_nojokers&account=pid7568847&appName=Ppp&clubId=3109882&stakes=0.50&price=0.001USD&gameId=221106145621-52798420-0000004-1&timeLimit=15&fastObvious&partner=altai-zxgsejynkd
        Ac / 6c 7h 6h / 9s Qs 9d*

        self
        Ac / 6c 7h 6h / 9s Qs 9d
        ok, right
*/
        GameOfc game = new GameOfc(Game.Nw.Spartan, 100);
        game.id = "414960953";
        game.addPlayer(new PlayerOfc("opp1", 1520));
        game.addPlayer(new PlayerOfc("hero", 1520));
        game.heroName = "hero";
        game.initButtonName("opp1");
        game.gameMode = GameOfc.GameMode.GAME_MODE_REGULAR;

        List<Card> emptyList = new ArrayList<>();

        game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("6c7h9sQsAc"))));
        game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, game.heroName, Card.cards2Mask(Card.str2Cards("Ac")), Card.cards2Mask(Card.str2Cards("6c7h")), Card.cards2Mask(Card.str2Cards("9sQs")), emptyList));
        game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, "opp1", Card.cards2Mask(Card.str2Cards("Kd")), Card.cards2Mask(Card.str2Cards("As")), Card.cards2Mask(Card.str2Cards("4dJhJs")), emptyList));
        game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("6h8c9d"))));


        System.out.println(game);

        Config.EvaluationMethod = Config.EvaluationMethodKind.SINGLE_HERO;
        Config.DEBUG_PRINT = false;
        Config.FANTASY_SCORE = 7;
        Config.FAIL_PENALTY = -3;

        long timeBefore = Utils.getTime();
        EventOfc result = EurekaRunner.run(game, 0, 8000);

        System.out.println(String.format("MCTS decision in %d ms: \n%s", Utils.getTime() - timeBefore, result.toString()));
    }

    public static void test2() throws Exception {
/*
  old variant http://10.211.59.133:8089/bestmove?hero=%2F%2F&newCards=2h+3c+5d+8c+Kc&opp=%2F%2F&dead=&button=1&table=test&rules=progressive16_refant14_nojokers&account=pid7568847&appName=Ppp&clubId=3109882&stakes=0.50&price=0.001USD&gameId=221106145621-52798420-0000004-1&timeLimit=15&fastObvious&partner=altai-zxgsejynkd
                / 2h 5d / 3c 8c Kc

        http://13.49.160.164:8000/bestmove?hero=%2F%2F&newCards=2h+3c+5d+8c+Kc&opp=%2F%2F&dead=&button=1&table=test&rules=progressive16_refant14_nojokers&account=pid7568847&appName=Ppp&clubId=3109882&stakes=0.50&price=0.001USD&gameId=221106145621-52798420-0000004-1&timeLimit=15&fastObvious&partner=altai-zxgsejynkd
        / 5d 2h / 3c 8c Kc

        http://nsk.convexbytes.com:15273/bestmove?hero=%2F%2F&newCards=2h+3c+5d+8c+Kc&opp=%2F%2F&dead=&button=1&table=test&rules=progressive16_refant14_nojokers&account=pid7568847&appName=Ppp&clubId=3109882&stakes=0.50&price=0.001USD&gameId=221106145621-52798420-0000004-1&timeLimit=15&fastObvious&partner=altai-zxgsejynkd
         / 2h 5d / 3c 8c Kc

        self
        (F:Kc, M:2h3c5d, B:8c, D:)
        BAD
*/
        GameOfc game = new GameOfc(Game.Nw.Spartan, 100);
        game.id = "414961267";
        game.addPlayer(new PlayerOfc("opp1", 1520));
        game.addPlayer(new PlayerOfc("hero", 1520));
        game.heroName = "hero";
        game.initButtonName("opp1");
        game.gameMode = GameOfc.GameMode.GAME_MODE_REGULAR;

        List<Card> emptyList = new ArrayList<>();

        game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("2h3c5d8cKc"))));

        System.out.println(game);

        Config.EvaluationMethod = Config.EvaluationMethodKind.SINGLE_HERO;
        Config.DEBUG_PRINT = false;
        Config.FANTASY_SCORE = 7;
        Config.FAIL_PENALTY = -3;

        long timeBefore = Utils.getTime();
        EventOfc result = EurekaRunner.run(game, 0, 8000);

        System.out.println(String.format("MCTS decision in %d ms: \n%s", Utils.getTime() - timeBefore, result.toString()));
    }

    public static void test3() throws Exception {
/*
  old variant http://10.211.59.133:8089/bestmove?hero=%2F%2F&newCards=5c+8c+Qd+Ks+Ac&opp=%2F3c%2F8s+9d+Ts+Jd&dead=&button=0&table=test&rules=progressive16_refant14_nojokers&account=pid7568847&appName=Ppp&clubId=3109882&stakes=0.50&price=0.001USD&gameId=221106145621-52798420-0000004-1&timeLimit=15&fastObvious&partner=altai-zxgsejynkd
                Qd / Ks / 5c 8c Ac

        http://13.49.160.164:8000/bestmove?hero=%2F%2F&newCards=5c+8c+Qd+Ks+Ac&opp=%2F3c%2F8s+9d+Ts+Jd&dead=&button=0&table=test&rules=progressive16_refant14_nojokers&account=pid7568847&appName=Ppp&clubId=3109882&stakes=0.50&price=0.001USD&gameId=221106145621-52798420-0000004-1&timeLimit=15&fastObvious&partner=altai-zxgsejynkd
        Ac / 5c 8c / Ks Qd

        http://nsk.convexbytes.com:15273/bestmove?hero=%2F%2F&newCards=5c+8c+Qd+Ks+Ac&opp=%2F3c%2F8s+9d+Ts+Jd&dead=&button=0&table=test&rules=progressive16_refant14_nojokers&account=pid7568847&appName=Ppp&clubId=3109882&stakes=0.50&price=0.001USD&gameId=221106145621-52798420-0000004-1&timeLimit=15&fastObvious&partner=altai-zxgsejynkd
         Qd / Ks / 5c 8c Ac

        self
        (F:KsAc, M:5c8c, B:Qd, D:)
        BAD
*/
        GameOfc game = new GameOfc(Game.Nw.Spartan, 100);
        game.id = "414962846";
        game.addPlayer(new PlayerOfc("opp1", 1520));
        game.addPlayer(new PlayerOfc("hero", 1520));
        game.heroName = "hero";
        game.initButtonName("hero");
        game.gameMode = GameOfc.GameMode.GAME_MODE_REGULAR;

        List<Card> emptyList = new ArrayList<>();

        game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, "opp1", Card.cards2Mask(Card.str2Cards("")), Card.cards2Mask(Card.str2Cards("3c")), Card.cards2Mask(Card.str2Cards("8s9dTsJd")), emptyList));
        game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("5c8cQdKsAc"))));

        System.out.println(game);

        Config.EvaluationMethod = Config.EvaluationMethodKind.SINGLE_HERO;
        Config.DEBUG_PRINT = false;
        Config.FANTASY_SCORE = 7;
        Config.FAIL_PENALTY = -3;

        long timeBefore = Utils.getTime();
        EventOfc result = EurekaRunner.run(game, 0, 8000);

        System.out.println(String.format("MCTS decision in %d ms: \n%s", Utils.getTime() - timeBefore, result.toString()));
    }

    public static void test4() throws Exception {
/*
  old variant http://10.211.59.133:8089/bestmove?hero=%2F%2F&newCards=5s+6c+9c+Tc+Ac&opp=%2F%2F&dead=&button=1&table=test&rules=progressive16_refant14_nojokers&account=pid7568847&appName=Ppp&clubId=3109882&stakes=0.50&price=0.001USD&gameId=221106145621-52798420-0000004-1&timeLimit=15&fastObvious&partner=altai-zxgsejynkd
                 / 5s / 6c 9c Tc Ac

        http://13.49.160.164:8000/bestmove?hero=%2F%2F&newCards=5s+6c+9c+Tc+Ac&opp=%2F%2F&dead=&button=1&table=test&rules=progressive16_refant14_nojokers&account=pid7568847&appName=Ppp&clubId=3109882&stakes=0.50&price=0.001USD&gameId=221106145621-52798420-0000004-1&timeLimit=15&fastObvious&partner=altai-zxgsejynkd
        Ac / 5s / 6c 9c Tc

        http://nsk.convexbytes.com:15273/bestmove?hero=%2F%2F&newCards=5s+6c+9c+Tc+Ac&opp=%2F%2F&dead=&button=1&table=test&rules=progressive16_refant14_nojokers&account=pid7568847&appName=Ppp&clubId=3109882&stakes=0.50&price=0.001USD&gameId=221106145621-52798420-0000004-1&timeLimit=15&fastObvious&partner=altai-zxgsejynkd
          / 5s / 6c 9c Tc Ac

        self
        F:Ac, M:5s6c, B:9cTc, D:)
        BAD
*/
        GameOfc game = new GameOfc(Game.Nw.Spartan, 100);
        game.id = "414965543";
        game.addPlayer(new PlayerOfc("opp1", 1520));
        game.addPlayer(new PlayerOfc("hero", 1520));
        game.heroName = "hero";
        game.initButtonName("opp1");
        game.gameMode = GameOfc.GameMode.GAME_MODE_REGULAR;

        List<Card> emptyList = new ArrayList<>();

        game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("5s6c9cTcAc"))));

        System.out.println(game);

        Config.EvaluationMethod = Config.EvaluationMethodKind.SINGLE_HERO;
        Config.DEBUG_PRINT = false;
        Config.FANTASY_SCORE = 7;
        Config.FAIL_PENALTY = -3;

        long timeBefore = Utils.getTime();
        EventOfc result = EurekaRunner.run(game, 0, 8000);

        System.out.println(String.format("MCTS decision in %d ms: \n%s", Utils.getTime() - timeBefore, result.toString()));
    }

    public static void test5_1() throws Exception {
/*
  old variant http://10.211.59.133:8089/bestmove?hero=%2F%2F&newCards=2d+3d+3c+8d+Kd&opp=fl14&dead=&button=1&table=test&rules=classic&account=pid7568847&appName=Ppp&clubId=3109882&stakes=0.50&price=0.001USD&gameId=221106145621-52798420-0000004-1&timeLimit=15&fastObvious&partner=altai-zxgsejynkd
                  / 3c / 2d 3d 8d Kd

        http://13.49.160.164:8000/bestmove?hero=%2F%2F&newCards=2d+3d+3c+8d+Kd&opp=fl14&dead=&button=1&table=test&rules=classic&account=pid7568847&appName=Ppp&clubId=3109882&stakes=0.50&price=0.001USD&gameId=221106145621-52798420-0000004-1&timeLimit=15&fastObvious&partner=altai-zxgsejynkd
         / 3c / 2d 3d 8d Kd

        http://nsk.convexbytes.com:15273/bestmove?hero=%2F%2F&newCards=2d+3d+3c+8d+Kd&opp=fl14&dead=&button=1&table=test&rules=classic&account=pid7568847&appName=Ppp&clubId=3109882&stakes=0.50&price=0.001USD&gameId=221106145621-52798420-0000004-1&timeLimit=15&fastObvious&partner=altai-zxgsejynkd
         / 3c / 2d 3d 8d Kd

        self
        (F:Kd, M:2d8d, B:3d3c, D:)
        BAD
*/
        GameOfc game = new GameOfc(Game.Nw.Spartan, 100);
        game.id = "414965786";
        game.addPlayer(new PlayerOfc("opp1", 1520));
        game.addPlayer(new PlayerOfc("hero", 1520));
        game.heroName = "hero";
        game.initButtonName("opp1");
        game.getPlayer("opp1").playFantasy = true;
        game.gameMode = GameOfc.GameMode.GAME_MODE_REGULAR;

        List<Card> emptyList = new ArrayList<>();

        game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("2d3d3c8dKd"))));

        System.out.println(game);

        Config.EvaluationMethod = Config.EvaluationMethodKind.SINGLE_HERO;
        Config.DEBUG_PRINT = false;
        Config.FANTASY_SCORE = 7;
        Config.FAIL_PENALTY = -3;

        long timeBefore = Utils.getTime();
        EventOfc result = EurekaRunner.run(game, 0, 8000);

        System.out.println(String.format("MCTS decision in %d ms: \n%s", Utils.getTime() - timeBefore, result.toString()));
    }

    public static void test5_2() throws Exception {
/*
  old variant http://10.211.59.133:8089/bestmove?hero=Kd%2F2d+8d%2F3d+3c&newCards=7c+8h+Jd&opp=fl14&dead=&button=1&table=test&rules=classic&account=pid7568847&appName=Ppp&clubId=3109882&stakes=0.50&price=0.001USD&gameId=221106145621-52798420-0000004-1&timeLimit=15&fastObvious&partner=altai-zxgsejynkd
                  Kd / 2d 8d 8h / 3d 3c Jd

        http://13.49.160.164:8000/bestmove?hero=Kd%2F2d+8d%2F3d+3c&newCards=7c+8h+Jd&opp=fl14&dead=&button=1&table=test&rules=classic&account=pid7568847&appName=Ppp&clubId=3109882&stakes=0.50&price=0.001USD&gameId=221106145621-52798420-0000004-1&timeLimit=15&fastObvious&partner=altai-zxgsejynkd
         Kd / 2d 8d 8h / 3d 3c Jd

        http://nsk.convexbytes.com:15273/bestmove?hero=Kd%2F2d+8d%2F3d+3c&newCards=7c+8h+Jd&opp=fl14&dead=&button=1&table=test&rules=classic&account=pid7568847&appName=Ppp&clubId=3109882&stakes=0.50&price=0.001USD&gameId=221106145621-52798420-0000004-1&timeLimit=15&fastObvious&partner=altai-zxgsejynkd
         Kd / 2d 8d 8h / 3d 3c Jd

        self
        (F:, M:8h, B:Jd, D:7c)
        so ok
*/
        GameOfc game = new GameOfc(Game.Nw.Spartan, 100);
        game.id = "414965786";
        game.addPlayer(new PlayerOfc("opp1", 1520));
        game.addPlayer(new PlayerOfc("hero", 1520));
        game.heroName = "hero";
        game.initButtonName("opp1");
        game.getPlayer("opp1").playFantasy = true;
        game.gameMode = GameOfc.GameMode.GAME_MODE_REGULAR;

        List<Card> emptyList = new ArrayList<>();

        game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("2d3d3c8dKd"))));
        game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, game.heroName, Card.cards2Mask(Card.str2Cards("Kd")), Card.cards2Mask(Card.str2Cards("2d8d")), Card.cards2Mask(Card.str2Cards("3d3c")), emptyList));
        game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("7c8hJd"))));
        System.out.println(game);

        Config.EvaluationMethod = Config.EvaluationMethodKind.SINGLE_HERO;
        Config.DEBUG_PRINT = false;
        Config.FANTASY_SCORE = 7;
        Config.FAIL_PENALTY = -3;

        long timeBefore = Utils.getTime();
        EventOfc result = EurekaRunner.run(game, 0, 8000);

        System.out.println(String.format("MCTS decision in %d ms: \n%s", Utils.getTime() - timeBefore, result.toString()));
    }

    public static void test5_3() throws Exception {
/*
  old variant http://10.211.59.133:8089/bestmove?hero=Kd%2F2d+8d+8h%2F3d+3c+Jd&newCards=2h+Ad+As&opp=fl14&dead=7c&button=1&table=test&rules=classic&account=pid7568847&appName=Ppp&clubId=3109882&stakes=0.50&price=0.001USD&gameId=221106145621-52798420-0000004-1&timeLimit=15&fastObvious&partner=altai-zxgsejynkd
                  Kd Ad / 2d 8d 8h 2h / 3d 3c Jd

        http://13.49.160.164:8000/bestmove?hero=Kd%2F2d+8d+8h%2F3d+3c+Jd&newCards=2h+Ad+As&opp=fl14&dead=7c&button=1&table=test&rules=classic&account=pid7568847&appName=Ppp&clubId=3109882&stakes=0.50&price=0.001USD&gameId=221106145621-52798420-0000004-1&timeLimit=15&fastObvious&partner=altai-zxgsejynkd
         Kd Ad As / 2d 8d 8h / 3d 3c Jd

        http://nsk.convexbytes.com:15273/bestmove?hero=Kd%2F2d+8d+8h%2F3d+3c+Jd&newCards=2h+Ad+As&opp=fl14&dead=7c&button=1&table=test&rules=classic&account=pid7568847&appName=Ppp&clubId=3109882&stakes=0.50&price=0.001USD&gameId=221106145621-52798420-0000004-1&timeLimit=15&fastObvious&partner=altai-zxgsejynkd
         Kd As / 2d 8d 8h 2h / 3d 3c Jd

        self
        (F:Ad, M:2h, B:, D:As)
        so so
*/
        GameOfc game = new GameOfc(Game.Nw.Spartan, 100);
        game.id = "414965786";
        game.addPlayer(new PlayerOfc("opp1", 1520));
        game.addPlayer(new PlayerOfc("hero", 1520));
        game.heroName = "hero";
        game.initButtonName("opp1");
        game.getPlayer("opp1").playFantasy = true;
        game.gameMode = GameOfc.GameMode.GAME_MODE_REGULAR;

        List<Card> emptyList = new ArrayList<>();

        game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("2d3d3c8dKd"))));
        game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, game.heroName, Card.cards2Mask(Card.str2Cards("Kd")), Card.cards2Mask(Card.str2Cards("2d8d")), Card.cards2Mask(Card.str2Cards("3d3c")), emptyList));
        game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("7c8hJd"))));
        game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, game.heroName, Card.cards2Mask(Card.str2Cards("")), Card.cards2Mask(Card.str2Cards("8h")), Card.cards2Mask(Card.str2Cards("Jd")), Arrays.asList(Card.str2Cards("7c"))));
        game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("2hAdAs"))));
        System.out.println(game);

        Config.EvaluationMethod = Config.EvaluationMethodKind.SINGLE_HERO;
        Config.DEBUG_PRINT = false;
        Config.FANTASY_SCORE = 7;
        Config.FAIL_PENALTY = -3;

        long timeBefore = Utils.getTime();
        EventOfc result = EurekaRunner.run(game, 0, 8000);

        System.out.println(String.format("MCTS decision in %d ms: \n%s", Utils.getTime() - timeBefore, result.toString()));
    }

    public static void test5_2_2() throws Exception {
/*
  old variant http://10.211.59.133:8089/bestmove?hero=%2F3c%2F3d+2d+8d+Kd&newCards=7c+8h+Jd&opp=fl14&dead=&button=1&table=test&rules=classic&account=pid7568847&appName=Ppp&clubId=3109882&stakes=0.50&price=0.001USD&gameId=221106145621-52798420-0000004-1&timeLimit=15&fastObvious&partner=altai-zxgsejynkd
                  / 3c 7c / 3d 2d 8d Kd Jd

        http://13.49.160.164:8000/bestmove?hero=%2F3c%2F3d+2d+8d+Kd&newCards=7c+8h+Jd&opp=fl14&dead=&button=1&table=test&rules=classic&account=pid7568847&appName=Ppp&clubId=3109882&stakes=0.50&price=0.001USD&gameId=221106145621-52798420-0000004-1&timeLimit=15&fastObvious&partner=altai-zxgsejynkd
          / 3c 7c / 3d 2d 8d Kd Jd

        http://nsk.convexbytes.com:15273/bestmove?hero=%2F3c%2F3d+2d+8d+Kd&newCards=7c+8h+Jd&opp=fl14&dead=&button=1&table=test&rules=classic&account=pid7568847&appName=Ppp&clubId=3109882&stakes=0.50&price=0.001USD&gameId=221106145621-52798420-0000004-1&timeLimit=15&fastObvious&partner=altai-zxgsejynkd
          / 3c 7c / 3d 2d 8d Kd Jd*

        self
        (F:, M:7c, B:Jd, D:8h)
        so ok
*/
        GameOfc game = new GameOfc(Game.Nw.Spartan, 100);
        game.id = "414965786";
        game.addPlayer(new PlayerOfc("opp1", 1520));
        game.addPlayer(new PlayerOfc("hero", 1520));
        game.heroName = "hero";
        game.initButtonName("opp1");
        game.getPlayer("opp1").playFantasy = true;
        game.gameMode = GameOfc.GameMode.GAME_MODE_REGULAR;

        List<Card> emptyList = new ArrayList<>();

        game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("2d3d3c8dKd"))));
        game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, game.heroName, Card.cards2Mask(Card.str2Cards("")), Card.cards2Mask(Card.str2Cards("3c")), Card.cards2Mask(Card.str2Cards("2d3d8dKd")), emptyList));
        game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("7c8hJd"))));
        System.out.println(game);

        Config.EvaluationMethod = Config.EvaluationMethodKind.SINGLE_HERO;
        Config.DEBUG_PRINT = false;
        Config.FANTASY_SCORE = 7;
        Config.FAIL_PENALTY = -3;

        long timeBefore = Utils.getTime();
        EventOfc result = EurekaRunner.run(game, 0, 8000);

        System.out.println(String.format("MCTS decision in %d ms: \n%s", Utils.getTime() - timeBefore, result.toString()));
    }

    public static void test5_3_2() throws Exception {
/*
  old variant http://10.211.59.133:8089/bestmove?hero=%2F3c+7c%2F3d+2d+8d+Kd+Jd&newCards=2h+Ad+As&opp=fl14&dead=8h&button=1&table=test&rules=classic&account=pid7568847&appName=Ppp&clubId=3109882&stakes=0.50&price=0.001USD&gameId=221106145621-52798420-0000004-1&timeLimit=15&fastObvious&partner=altai-zxgsejynkd
                   / 3c 7c Ad As / 3d 2d 8d Kd Jd

        http://13.49.160.164:8000/bestmove?hero=%2F3c+7c%2F3d+2d+8d+Kd+Jd&newCards=2h+Ad+As&opp=fl14&dead=8h&button=1&table=test&rules=classic&account=pid7568847&appName=Ppp&clubId=3109882&stakes=0.50&price=0.001USD&gameId=221106145621-52798420-0000004-1&timeLimit=15&fastObvious&partner=altai-zxgsejynkd
         Ad As / 3c 7c / 3d 2d 8d Kd Jd

        http://nsk.convexbytes.com:15273/bestmove?hero=%2F3c+7c%2F3d+2d+8d+Kd+Jd&newCards=2h+Ad+As&opp=fl14&dead=8h&button=1&table=test&rules=classic&account=pid7568847&appName=Ppp&clubId=3109882&stakes=0.50&price=0.001USD&gameId=221106145621-52798420-0000004-1&timeLimit=15&fastObvious&partner=altai-zxgsejynkd
          Ad As / 3c 7c / 3d 2d 8d Kd Jd*

        self
        (F:AdAs, M:, B:, D:2h)
        so ok
*/
        GameOfc game = new GameOfc(Game.Nw.Spartan, 100);
        game.id = "414965786";
        game.addPlayer(new PlayerOfc("opp1", 1520));
        game.addPlayer(new PlayerOfc("hero", 1520));
        game.heroName = "hero";
        game.initButtonName("opp1");
        game.getPlayer("opp1").playFantasy = true;
        game.gameMode = GameOfc.GameMode.GAME_MODE_REGULAR;

        List<Card> emptyList = new ArrayList<>();

        game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("2d3d3c8dKd"))));
        game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, game.heroName, Card.cards2Mask(Card.str2Cards("")), Card.cards2Mask(Card.str2Cards("3c")), Card.cards2Mask(Card.str2Cards("2d3d8dKd")), emptyList));
        game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("7c8hJd"))));
        game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, game.heroName, Card.cards2Mask(Card.str2Cards("")), Card.cards2Mask(Card.str2Cards("7c")), Card.cards2Mask(Card.str2Cards("Jd")), Arrays.asList(Card.str2Cards("8h"))));
        game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("2hAdAs"))));
        System.out.println(game);

        Config.EvaluationMethod = Config.EvaluationMethodKind.SINGLE_HERO;
        Config.DEBUG_PRINT = false;
        Config.FANTASY_SCORE = 7;
        Config.FAIL_PENALTY = -3;

        long timeBefore = Utils.getTime();
        EventOfc result = EurekaRunner.run(game, 0, 8000);

        System.out.println(String.format("MCTS decision in %d ms: \n%s", Utils.getTime() - timeBefore, result.toString()));
    }

    public static void test6() throws Exception {
/*
  old variant http://10.211.59.133:8089/bestmove?hero=Kh+Kd%2F3d+7d%2F9c&newCards=5h+9s+Jc&opp=fl14&dead=&button=1&table=test&rules=classic&account=pid7568847&appName=Ppp&clubId=3109882&stakes=0.50&price=0.001USD&gameId=221106145621-52798420-0000004-1&timeLimit=15&fastObvious&partner=altai-zxgsejynkd
                 Kh Kd / 3d 7d / 9c 9s Jc

        http://13.49.160.164:8000/bestmove?hero=Kh+Kd%2F3d+7d%2F9c&newCards=5h+9s+Jc&opp=fl14&dead=&button=1&table=test&rules=classic&account=pid7568847&appName=Ppp&clubId=3109882&stakes=0.50&price=0.001USD&gameId=221106145621-52798420-0000004-1&timeLimit=15&fastObvious&partner=altai-zxgsejynkd
          / 3c 7c / 3d 2d 8d Kd Jd

        http://nsk.convexbytes.com:15273/bestmove?hero=Kh+Kd%2F3d+7d%2F9c&newCards=5h+9s+Jc&opp=fl14&dead=&button=1&table=test&rules=classic&account=pid7568847&appName=Ppp&clubId=3109882&stakes=0.50&price=0.001USD&gameId=221106145621-52798420-0000004-1&timeLimit=15&fastObvious&partner=altai-zxgsejynkd
          / 3c 7c / 3d 2d 8d Kd Jd*

        self
        (F:, M:7c, B:Jd, D:8h)
        so ok
*/
        GameOfc game = new GameOfc(Game.Nw.Spartan, 100);
        game.id = "414967050";
        game.addPlayer(new PlayerOfc("opp1", 1520));
        game.addPlayer(new PlayerOfc("hero", 1520));
        game.heroName = "hero";
        game.initButtonName("opp1");
        game.getPlayer("opp1").playFantasy = true;
        game.gameMode = GameOfc.GameMode.GAME_MODE_REGULAR;

        List<Card> emptyList = new ArrayList<>();

        game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("KhKd3d7d9c"))));
        game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, game.heroName, Card.cards2Mask(Card.str2Cards("KhKd")), Card.cards2Mask(Card.str2Cards("3d7d")), Card.cards2Mask(Card.str2Cards("9c")), emptyList));
        game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("5h9sJc"))));
        System.out.println(game);

        Config.EvaluationMethod = Config.EvaluationMethodKind.SINGLE_HERO;
        Config.DEBUG_PRINT = false;
        Config.FANTASY_SCORE = 7;
        Config.FAIL_PENALTY = -3;

        long timeBefore = Utils.getTime();
        EventOfc result = EurekaRunner.run(game, 0, 8000);

        System.out.println(String.format("MCTS decision in %d ms: \n%s", Utils.getTime() - timeBefore, result.toString()));
    }

    public static void test7() throws Exception {
/*
  old variant http://10.211.59.133:8089/bestmove?hero=Kc+Kd%2F6h+7h+5h%2F8c+Jc&newCards=4h+Td+Ks&opp=fl14&dead=3h&button=1&table=test&rules=classic&account=pid7568847&appName=Ppp&clubId=3109882&stakes=0.50&price=0.001USD&gameId=221106145621-52798420-0000004-1&timeLimit=15&fastObvious&partner=altai-zxgsejynkd
                 Kc Kd / 6h 7h 5h 4h / 8c Jc Td

        http://13.49.160.164:8000/bestmove?hero=Kc+Kd%2F6h+7h+5h%2F8c+Jc&newCards=4h+Td+Ks&opp=fl14&dead=3h&button=1&table=test&rules=classic&account=pid7568847&appName=Ppp&clubId=3109882&stakes=0.50&price=0.001USD&gameId=221106145621-52798420-0000004-1&timeLimit=15&fastObvious&partner=altai-zxgsejynkd
          Kc Kd Ks / 6h 7h 5h 4h / 8c Jc

        http://nsk.convexbytes.com:15273/bestmove?hero=Kc+Kd%2F6h+7h+5h%2F8c+Jc&newCards=4h+Td+Ks&opp=fl14&dead=3h&button=1&table=test&rules=classic&account=pid7568847&appName=Ppp&clubId=3109882&stakes=0.50&price=0.001USD&gameId=221106145621-52798420-0000004-1&timeLimit=15&fastObvious&partner=altai-zxgsejynkd
          Kc Kd Ks / 6h 7h 5h 4h / 8c Jc

        self
        (F:Ks, M:4h, B:, D:Td)
        right
*/
        GameOfc game = new GameOfc(Game.Nw.Spartan, 100);
        game.id = "414966771";
        game.addPlayer(new PlayerOfc("opp1", 1520));
        game.addPlayer(new PlayerOfc("hero", 1520));
        game.heroName = "hero";
        game.initButtonName("opp1");
        game.getPlayer("opp1").playFantasy = true;
        game.gameMode = GameOfc.GameMode.GAME_MODE_REGULAR;

        List<Card> emptyList = new ArrayList<>();

        game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("KcKd6h7h5h"))));
        game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, game.heroName, Card.cards2Mask(Card.str2Cards("KcKd")), Card.cards2Mask(Card.str2Cards("6h7h5h")), Card.cards2Mask(Card.str2Cards("")), emptyList));
        game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("8cJc3h"))));
        game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, game.heroName, Card.cards2Mask(Card.str2Cards("")), Card.cards2Mask(Card.str2Cards("")), Card.cards2Mask(Card.str2Cards("8cJc")), Arrays.asList(Card.str2Cards("3h"))));
        game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("4hTdKs"))));
        System.out.println(game);

        Config.EvaluationMethod = Config.EvaluationMethodKind.SINGLE_HERO;
        Config.DEBUG_PRINT = false;
        Config.FANTASY_SCORE = 7;
        Config.FAIL_PENALTY = -3;

        long timeBefore = Utils.getTime();
        EventOfc result = EurekaRunner.run(game, 0, 8000);

        System.out.println(String.format("MCTS decision in %d ms: \n%s", Utils.getTime() - timeBefore, result.toString()));
    }

    public static void test8() throws Exception {
/*
  old variant http://10.211.59.133:8089/bestmove?hero=%2F%2F&newCards=6h+7h+8c+Jc+Kc&opp=%2F%2F&dead=&button=1&table=test&rules=progressive16_refant14_nojokers&account=pid7568847&appName=Ppp&clubId=3109882&stakes=0.50&price=0.001USD&gameId=221106145621-52798420-0000004-1&timeLimit=15&fastObvious&partner=altai-zxgsejynkd
                  / 6h 7h / 8c Jc Kc

        http://13.49.160.164:8000/bestmove?hero=%2F%2F&newCards=6h+7h+8c+Jc+Kc&opp=%2F%2F&dead=&button=1&table=test&rules=progressive16_refant14_nojokers&account=pid7568847&appName=Ppp&clubId=3109882&stakes=0.50&price=0.001USD&gameId=221106145621-52798420-0000004-1&timeLimit=15&fastObvious&partner=altai-zxgsejynkd
        Ac / 5s / 6c 9c Tc

        http://nsk.convexbytes.com:15273/bestmove?hero=%2F%2F&newCards=6h+7h+8c+Jc+Kc&opp=%2F%2F&dead=&button=1&table=test&rules=progressive16_refant14_nojokers&account=pid7568847&appName=Ppp&clubId=3109882&stakes=0.50&price=0.001USD&gameId=221106145621-52798420-0000004-1&timeLimit=15&fastObvious&partner=altai-zxgsejynkd
          / 5s / 6c 9c Tc Ac

        self new
        (F:, M:6h7h, B:8cJcKc, D:)
        ok
*/
        GameOfc game = new GameOfc(Game.Nw.Spartan, 100);
        game.id = "414966771";
        game.addPlayer(new PlayerOfc("opp1", 1520));
        game.addPlayer(new PlayerOfc("hero", 1520));
        game.heroName = "hero";
        game.initButtonName("opp1");
        game.gameMode = GameOfc.GameMode.GAME_MODE_REGULAR;

        List<Card> emptyList = new ArrayList<>();

        game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("6h7h8cJcKc"))));

        System.out.println(game);

        Config.EvaluationMethod = Config.EvaluationMethodKind.SINGLE_HERO;
        Config.DEBUG_PRINT = false;
        Config.FANTASY_SCORE = 7;
        Config.FAIL_PENALTY = -3;

        long timeBefore = Utils.getTime();
        EventOfc result = EurekaRunner.run(game, 0, 8000);

        System.out.println(String.format("MCTS decision in %d ms: \n%s", Utils.getTime() - timeBefore, result.toString()));
    }

    public static void test9() throws Exception {
/*
  old variant http://10.211.59.133:8089/bestmove?hero=%2F%2F&newCards=2s+7h+9s+Qs+Kc&opp=Qd%2F5c%2F8s+9c+Js&dead=&button=0&table=test&rules=progressive16_refant14_nojokers&account=pid7568847&appName=Ppp&clubId=3109882&stakes=0.50&price=0.001USD&gameId=221106145621-52798420-0000004-1&timeLimit=15&fastObvious&partner=altai-zxgsejynkd
                  Kc / 7h / 2s 9s Qs

        http://13.49.160.164:8000/bestmove?hero=%2F%2F&newCards=2s+7h+9s+Qs+Kc&opp=Qd%2F5c%2F8s+9c+Js&dead=&button=0&table=test&rules=progressive16_refant14_nojokers&account=pid7568847&appName=Ppp&clubId=3109882&stakes=0.50&price=0.001USD&gameId=221106145621-52798420-0000004-1&timeLimit=15&fastObvious&partner=altai-zxgsejynkd
        Kc / 7h / 2s 9s Qs

        http://nsk.convexbytes.com:15273/bestmove?hero=%2F%2F&newCards=2s+7h+9s+Qs+Kc&opp=Qd%2F5c%2F8s+9c+Js&dead=&button=0&table=test&rules=progressive16_refant14_nojokers&account=pid7568847&appName=Ppp&clubId=3109882&stakes=0.50&price=0.001USD&gameId=221106145621-52798420-0000004-1&timeLimit=15&fastObvious&partner=altai-zxgsejynkd
          Kc / 7h / 2s 9s Qs

        self new
        (F:Kc, M:7h, B:2s9sQs, D:)
        ok
*/
        GameOfc game = new GameOfc(Game.Nw.Spartan, 100);
        game.id = "414966825";
        game.addPlayer(new PlayerOfc("opp1", 1520));
        game.addPlayer(new PlayerOfc("hero", 1520));
        game.heroName = "hero";
        game.initButtonName("hero");
        game.gameMode = GameOfc.GameMode.GAME_MODE_REGULAR;

        List<Card> emptyList = new ArrayList<>();

        game.procEvent(new EventOfc(EventOfc.PUT_CARDS_TO_BOXES, "opp1", Card.cards2Mask(Card.str2Cards("Qd")), Card.cards2Mask(Card.str2Cards("5c")), Card.cards2Mask(Card.str2Cards("8s9cJs")), emptyList));
        game.procEvent(new EventOfc(EventOfc.TYPE_DEAL_CARDS, game.heroName, Card.cards2Mask(Card.str2Cards("2s7h9sQsKc"))));

        System.out.println(game);

        Config.EvaluationMethod = Config.EvaluationMethodKind.SINGLE_HERO;
        Config.DEBUG_PRINT = false;
        Config.FANTASY_SCORE = 7;
        Config.FAIL_PENALTY = -3;

        long timeBefore = Utils.getTime();
        EventOfc result = EurekaRunner.run(game, 0, 8000);

        System.out.println(String.format("MCTS decision in %d ms: \n%s", Utils.getTime() - timeBefore, result.toString()));
    }

    public static void main(String[] args) throws Exception {
        test9();
    }

}
