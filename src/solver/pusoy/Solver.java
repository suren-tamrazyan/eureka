package solver.pusoy;

import game.Card;
import game.EventOfc;
import org.paukov.combinatorics3.Generator;
import solver.ofc.Config;
import solver.ofc.EvaluatorFacade;
import solver.ofc.EventOfcMctsSimple;
import solver.ofc.Utils;
import solver.ofc.evaluator.Evaluator;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

public class Solver {
	
	public static class AsHand implements Comparable<AsHand> {
		private List<Card> evalList;
		private List<Card> baseList;
		private List<Card> completeList;
		private short eval; // less is better 
		private long[] lHand;
		
//		private String strBase;
//		private String strEval;
		
		private long mask = 0L;
		
		public short getEval() {
			return eval;
		}

		public AsHand(List<Card> aEvalList, List<Card> aBaseList) {
			this.evalList = aEvalList;
			this.baseList = aBaseList;
			
			int handSize = evalList.size() + baseList.size(); 
			if (handSize != 5 && handSize != 3)
				throw new IllegalArgumentException("Init Heuristics.AsHand: hand size is not 5 and is not 3");
			
			Evaluator ev = new Evaluator();
//			strBase = baseList.stream().map(Card::toStrDirect).collect(Collectors.joining(""));
//			strEval = evalList.stream().map(Card::toStrDirect).collect(Collectors.joining(""));
//	        lHand = Evaluator.encodeHand(strBase + strEval);
			List<Card> allcard = new ArrayList<>(baseList);
			allcard.addAll(evalList);
			lHand = new long[allcard.size()];
			EvaluatorFacade.encodeHand(allcard, lHand);
	        
	        if(lHand.length == 5)
	        	eval = ev.evalFive(lHand);
	        else
	        	eval = ev.evalThree(lHand, false);
	        
	        // mask
			for (Card crd : baseList)
				mask |= (1L << crd.getIndex());
			for (Card crd : evalList)
				mask |= (1L << crd.getIndex());

			this.completeList = new ArrayList<>(baseList);
			this.completeList.addAll(evalList);
		}
		
		@Override
		public int compareTo(AsHand o) {
			int result = this.eval - o.eval;
			if (!Config.DISTINCT_TREE)
				if (result == 0) // prevent remove equals by eval objects
					result = 1;//this.strEval.compareTo(o.strEval);
			return result;
		}
		
		public List<Card> toCompleteList() {
			return this.completeList;
		}
		
		public long getMask() {
			return mask;
		}
		
		public boolean isOverlay(AsHand other) {
			return (this.getMask() & other.getMask()) != 0;
		}
		
		@Override
		public String toString() {
			return String.format("%s-%s", baseList, evalList);
		}
		
	}
	
	public static class Trinity implements Comparable<Trinity> {
		private AsHand front;
		private AsHand middle;
		private AsHand back;
		private Double eval = null; // more is better
		
		public Trinity(AsHand aFront, AsHand aMiddle, AsHand aBack) {
			front = aFront;
			middle = aMiddle;
			back = aBack;
		}
		
//		public double eval(boolean inFantasy, boolean isPusoy) {
//			if (eval == null) {
//				if (isPusoy)
//					eval = solver.pusoy.Evaluator.evaluate(front.toCompleteList(), middle.toCompleteList(), back.toCompleteList());
//				else
//					eval = EvaluatorFacade.evaluate(front.toCompleteList(), middle.toCompleteList(), back.toCompleteList(), inFantasy);
//			}
//			return eval;
//		}

		@Override
		public int compareTo(Trinity o) {
			return solver.pusoy.Evaluator.compare(this.front.toCompleteList(), this.middle.toCompleteList(), this.back.toCompleteList(), o.front.toCompleteList(), o.middle.toCompleteList(), o.back.toCompleteList());
		}

		//check for overlay cards
		public boolean isValid() {
			long bMask = back.getMask();
			long mMask = middle.getMask();
			long fMask = front.getMask();
			return ((bMask & mMask) == 0) && ((bMask & fMask) == 0) && ((mMask & fMask) == 0);
		}
		
		public EventOfcMctsSimple toEventOfcMctsSimple(Collection<Card> toBeBoxed) {
			int evType = EventOfc.PUT_CARDS_TO_BOXES;
			if (front.baseList.isEmpty() && middle.baseList.isEmpty() && back.baseList.isEmpty())
				evType = EventOfc.FANTASY_CARDS_TO_BOXES;
			List<Card> deads = new ArrayList<>(toBeBoxed);
			deads.removeAll(front.baseList);
			deads.removeAll(front.evalList);
			deads.removeAll(middle.baseList);
			deads.removeAll(middle.evalList);
			deads.removeAll(back.baseList);
			deads.removeAll(back.evalList);
			return new EventOfcMctsSimple(evType, front.evalList, middle.evalList, back.evalList, deads);
		}
	}

	public static class Completor {
		private ThreadPoolExecutor pool = (ThreadPoolExecutor) Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() - 1);
		private Trinity best = null;

		public EventOfcMctsSimple pusoyCompletion(List<Card> front, List<Card> middle, List<Card> back, Collection<Card> toBeBoxed, long timeLimitMs) {
			long timeStart = System.nanoTime();
			long timeLimitNano = timeLimitMs * 1000000;
			
			TreeSet<AsHand> treeBack = new TreeSet<>(Generator.combination(toBeBoxed).simple(5 - back.size()).stream().map(x -> new AsHand(x, back)).collect(Collectors.toList()));
			TreeSet<AsHand> treeMiddle = new TreeSet<>(Generator.combination(toBeBoxed).simple(5 - middle.size()).stream().map(x -> new AsHand(x, middle)).collect(Collectors.toList()));
			TreeSet<AsHand> treeFront = new TreeSet<>(Generator.combination(toBeBoxed).simple(3 - front.size()).stream().map(x -> new AsHand(x, front)).collect(Collectors.toList()));
			
			Object lock = new Object();

			for (AsHand hFront : treeFront) {
					pool.submit(() -> {
						for (AsHand hMiddle : treeMiddle) {
							//preventive check for overlap cards
							if (hMiddle.isOverlay(hFront))
								continue;
							
							for (AsHand hBack : treeBack) { //TreeSet are safe when read, even concurrently, by multiple threads.
								Trinity current = new Trinity(hFront, hMiddle, hBack);
								if (current.isValid()) {
									if (best == null)
										best = current;
									boolean fouled = (hFront.getEval() < hMiddle.getEval() || hMiddle.getEval() < hBack.getEval());
									if (!fouled) {
										if (current.compareTo(best) <= 0)
											continue;
										synchronized (lock) {
											if (current.compareTo(best) > 0)
												best = current;
										}
									}
								}
							}
						}
					});
			}
			
			long time = 0;
			do {
				Utils.sleep(10);
//				System.out.println(String.format("Queue.Count = %d; Active.Count = %d", pool.getQueue().size(), pool.getActiveCount()));
				time = System.nanoTime() - timeStart;
			} while ((!pool.getQueue().isEmpty() || pool.getActiveCount() > 0) && (time) < timeLimitNano);
//			System.out.println(String.format("Shoutdown: Solutions = %d; BestValue = %s; Queue.Count = %d; Active.Count = %d", solutionsCount, bestEval, pool.getQueue().size(), pool.getActiveCount()));
			pool.shutdown();
				
			return best.toEventOfcMctsSimple(toBeBoxed);
		}
	}

	public static EventOfcMctsSimple pusoyCompletionSync(List<Card> front, List<Card> middle, List<Card> back, Collection<Card> toBeBoxed) {

		TreeSet<AsHand> treeBack = new TreeSet<>(Generator.combination(toBeBoxed).simple(5 - back.size()).stream().map(x -> new AsHand(x, back)).collect(Collectors.toList()));
		TreeSet<AsHand> treeMiddle = new TreeSet<>(Generator.combination(toBeBoxed).simple(5 - middle.size()).stream().map(x -> new AsHand(x, middle)).collect(Collectors.toList()));
		TreeSet<AsHand> treeFront = new TreeSet<>(Generator.combination(toBeBoxed).simple(3 - front.size()).stream().map(x -> new AsHand(x, front)).collect(Collectors.toList()));

		Trinity best = null;

		for (AsHand hFront : treeFront) {
			for (AsHand hMiddle : treeMiddle) {
				//preventive check for overlap cards
				if (hMiddle.isOverlay(hFront))
					continue;

				for (AsHand hBack : treeBack) { //TreeSet are safe when read, even concurrently, by multiple threads.
					Trinity current = new Trinity(hFront, hMiddle, hBack);
					if (current.isValid()) {
						if (best == null)
							best = current;
						boolean fouled = (hFront.getEval() < hMiddle.getEval() || hMiddle.getEval() < hBack.getEval());
						if (!fouled) {
							if (current.compareTo(best) <= 0)
								continue;
							if (current.compareTo(best) > 0)
								best = current;
						}
					}
				}
			}
		}

		return best.toEventOfcMctsSimple(toBeBoxed);
	}

	public static EventOfcMctsSimple pusoyCompletion(Collection<Card> toBeBoxed, long timeLimitMs) {
		Completor completor = new Completor();
		return completor.pusoyCompletion(Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), toBeBoxed, timeLimitMs);
	}

	public static void main(String[] args) {
		long time = Utils.getTime();
//		System.out.println(pusoyCompletion(Arrays.asList(Card.str2Cards("TdThAh6d6c2h2c9c3c3d3s4c4d")), 10000).toEventOfc("hero"));
//		System.out.println(pusoyCompletion(Arrays.asList(Card.str2Cards("AhAdAs6d7h8d9dTd4c5c7c9cQc7s9s")), 20000).toEventOfc("hero"));
//		System.out.println(pusoyCompletion(Arrays.asList(Card.str2Cards("TdTsKh8d8h6d6c7dAdAcAhQh9s")), 10000).toEventOfc("hero"));
//		System.out.println(pusoyCompletion(Arrays.asList(Card.str2Cards("2h 2s 3s 4h 5h 5c 5s 7d 9s Th Ts Qh Ac")), 10000).toEventOfc("hero"));
		System.out.println(pusoyCompletionSync(Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Arrays.asList(Card.str2Cards("9h Js Qc Kc Ah 4h 4s Th 5d 6s 7c 2d 7d"))).toEventOfc("hero"));
		System.out.println(String.format("time = %d", Utils.getTime() - time));

		testCompare(Arrays.asList(Card.str2Cards("2d 4h 4s")), Arrays.asList(Card.str2Cards("5d 6s 7d 7c Ah")), Arrays.asList(Card.str2Cards("9h Th Js Qc Kc")),
				Arrays.asList(Card.str2Cards("5d 6s 9h")), Arrays.asList(Card.str2Cards("2d 4h 4s 7d 7c")), Arrays.asList(Card.str2Cards("Th Js Qc Kc Ah")),
				Arrays.asList(Card.str2Cards("2d 5d Ah")), Arrays.asList(Card.str2Cards("4h 4s 6s 7d 7c")), Arrays.asList(Card.str2Cards("9h Th Js Qc Kc")));

		testCompare(Arrays.asList(Card.str2Cards("9h 9s Qc")), Arrays.asList(Card.str2Cards("8c Th Jh Jd Kc")), Arrays.asList(Card.str2Cards("2d 3d 3c 5h 5s")),
				Arrays.asList(Card.str2Cards("8c 9h Jh")), Arrays.asList(Card.str2Cards("2d 3d 3c 5h 5s")), Arrays.asList(Card.str2Cards("9s Th Jd Qc Kc")),
				Arrays.asList(Card.str2Cards("9h Jh Kc")), Arrays.asList(Card.str2Cards("2d 3d 3c 5h 5s")), Arrays.asList(Card.str2Cards("8c 9s Th Jd Qc")));

		testCompare(Arrays.asList(Card.str2Cards("7d Td Kh")), Arrays.asList(Card.str2Cards("2d 8d Jh Jc Qh")), Arrays.asList(Card.str2Cards("4c 5h 6s 7s 8s")),
				Arrays.asList(Card.str2Cards("7d Td Kh")), Arrays.asList(Card.str2Cards("2d 8d Jh Jc Qh")), Arrays.asList(Card.str2Cards("4c 5h 6s 7s 8s")),
				Arrays.asList(Card.str2Cards("Td Qh Kh")), Arrays.asList(Card.str2Cards("2d 7d 8d Jh Jc")), Arrays.asList(Card.str2Cards("4c 5h 6s 7s 8s")));

//		testCompare(Arrays.asList(Card.str2Cards("")), Arrays.asList(Card.str2Cards("")), Arrays.asList(Card.str2Cards("")),
//				Arrays.asList(Card.str2Cards("")), Arrays.asList(Card.str2Cards("")), Arrays.asList(Card.str2Cards("")),
//				Arrays.asList(Card.str2Cards("")), Arrays.asList(Card.str2Cards("")), Arrays.asList(Card.str2Cards("")));
	}

	public static void testCompare(List<Card> frontOur1, List<Card> middleOur1, List<Card> backOur1, List<Card> frontOur2, List<Card> middleOur2, List<Card> backOur2, List<Card> frontForeign, List<Card> middleForeign, List<Card> backForeign) {
		double evalOur1 = solver.pusoy.Evaluator.evaluate(frontOur1, middleOur1, backOur1);
		double evalOur2 = solver.pusoy.Evaluator.evaluate(frontOur2, middleOur2, backOur2);
		double evalForeign = solver.pusoy.Evaluator.evaluate(frontForeign, middleForeign, backForeign);
		System.out.println(String.format("evalOur1 = %f; evalOur2 = %f; evalForeign = %f", evalOur1, evalOur2, evalForeign));
		System.out.println(String.format("compare our1 and foreign = %d", solver.pusoy.Evaluator.compare(frontOur1, middleOur1, backOur1, frontForeign, middleForeign, backForeign)));
		System.out.println(String.format("compare our2 and foreign = %d", solver.pusoy.Evaluator.compare(frontOur2, middleOur2, backOur2, frontForeign, middleForeign, backForeign)));
		System.out.println(String.format("compare our2 and our1 = %d", solver.pusoy.Evaluator.compare(frontOur2, middleOur2, backOur2, frontOur1, middleOur1, backOur1)));
		System.out.println();
	}
}
