package solver.ofc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

import org.paukov.combinatorics3.Generator;

import game.Card;
import game.EventOfc;
import solver.ofc.evaluator.Evaluator;

public class Heuristics {
	
	private static final boolean DEBUG_OUT = false;
	
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
	
	public static class Trinity {
		private AsHand front;
		private AsHand middle;
		private AsHand back;
		private Double eval = null; // more is better
		
		public Trinity(AsHand aFront, AsHand aMiddle, AsHand aBack) {
			front = aFront;
			middle = aMiddle;
			back = aBack;
		}
		
		public double eval(boolean inFantasy, boolean isPusoy) {
			if (eval == null) {
				if (isPusoy)
					eval = solver.pusoy.Evaluator.evaluate(front.toCompleteList(), middle.toCompleteList(), back.toCompleteList());
				else
					eval = EvaluatorFacade.evaluate(front.toCompleteList(), middle.toCompleteList(), back.toCompleteList(), inFantasy);
			}
			return eval; 
		}
		
		//check for overlay cards
		public boolean isValid() {
//			long bitOverlay = 0;
//			for (AsHand hnd : Arrays.asList(back, middle, front)) {
//				for (Card crd : hnd.toCompleteList()) {
//					if ((bitOverlay & (1L << crd.getIndex())) == 0)
//						bitOverlay |= (1L << crd.getIndex());
//					else
//						return false;
//				}
//			}
//			return true;
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
		private volatile double bestEval = Double.NEGATIVE_INFINITY;
		private int solutionsCount = 0;

		public EventOfcMctsSimple fantasyCompletion(List<Card> front, List<Card> middle, List<Card> back, Collection<Card> toBeBoxed, long timeLimitMs, boolean isPusoy) {
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
	//									if (backupBadSolution == null) backupBadSolution = current;
									boolean fouled = (hFront.getEval() < hMiddle.getEval() || hMiddle.getEval() < hBack.getEval());
									if (!fouled) {
										if (current.eval(true, isPusoy) <= bestEval) {
											if (Config.FANTASY_FAST_SOLVE) // FAST but not strict
												return;
											else
												continue;
										}
										synchronized (lock) {
											if (current.eval(true, isPusoy) > bestEval) {
												best = current;
												bestEval = best.eval(true, isPusoy);
											}
											solutionsCount++;
										}
//										return;
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

	/**
	 * greedy, very greedy search; pseudo-optimal completion
	 */
	public static EventOfcMctsSimple completion(List<Card> front, List<Card> middle, List<Card> back, Collection<Card> toBeBoxed) {
		
		TreeSet<AsHand> treeBack = new TreeSet<>(Generator.combination(toBeBoxed).simple(5 - back.size()).stream().map(x -> new AsHand(x, back)).collect(Collectors.toList()));
		TreeSet<AsHand> treeMiddle = new TreeSet<>(Generator.combination(toBeBoxed).simple(5 - middle.size()).stream().map(x -> new AsHand(x, middle)).collect(Collectors.toList()));
		TreeSet<AsHand> treeFront = new TreeSet<>(Generator.combination(toBeBoxed).simple(3 - front.size()).stream().map(x -> new AsHand(x, front)).collect(Collectors.toList()));
		
		Trinity bestByBack = null;
		Trinity backupBadSolution = null;
		double bestEvalByBack = Double.NEGATIVE_INFINITY;
		int cntBack = 0;
		outerback:
		for (AsHand hBack : treeBack) {
			for (AsHand hMiddle : treeMiddle) {
				
				//preventive check for overlap cards
				if (hBack.isOverlay(hMiddle))
					continue;
				
				for (AsHand hFront : treeFront) {
					Trinity current = new Trinity(hFront, hMiddle, hBack);
					if (current.isValid()) {
						if (backupBadSolution == null) backupBadSolution = current;
						boolean fouled = (hFront.getEval() < hMiddle.getEval() || hMiddle.getEval() < hBack.getEval());
						if (!fouled) {
							if (current.eval(false, false) > bestEvalByBack) {
								bestByBack = current;
								bestEvalByBack = bestByBack.eval(false, false);
							}
							cntBack++;
							if (cntBack >= Config.DEPTH_OF_SEARCH)
								break outerback;
						}
					}
				}
			}
		}
		
		//a little less greed 
		Trinity bestByFront = null;
		double bestEvalByFront = Double.NEGATIVE_INFINITY;
		int cntFront = 0;
		outerfront:
		for (AsHand hFront : treeFront) {
			for (AsHand hMiddle : treeMiddle) {
				
				//preventive check for overlap cards
				if (hMiddle.isOverlay(hFront))
					continue;
				
				for (AsHand hBack : treeBack) {
					Trinity current = new Trinity(hFront, hMiddle, hBack);
					if (current.isValid()) {
						if (backupBadSolution == null) backupBadSolution = current;
						boolean fouled = (hFront.getEval() < hMiddle.getEval() || hMiddle.getEval() < hBack.getEval());
						if (!fouled) {
							if (current.eval(false, false) > bestEvalByFront) {
								bestByFront = current;
								bestEvalByFront = bestByFront.eval(false, false);
							}
							cntFront++;
							if (cntFront >= Config.DEPTH_OF_SEARCH)
								break outerfront;
						}
					}
				}
			}
		}
		
		Trinity best;
		if (bestEvalByBack > bestEvalByFront)
			best = bestByBack;
		else {
			best = bestByFront;
			if (DEBUG_OUT)
				System.out.println("bestEvalByFront better than bestByBack");
		}
		
		if (best == null)
			best = backupBadSolution;
		if (best == null)
			return null;

		return best.toEventOfcMctsSimple(toBeBoxed);
	}
	
	public static EventOfcMctsSimple fantasyCompletion(List<Card> front, List<Card> middle, List<Card> back, Collection<Card> toBeBoxed, long timeLimitMs) {
		Completor completor = new Completor();
		return completor.fantasyCompletion(front, middle, back, toBeBoxed, timeLimitMs, false);
	}

	public static EventOfcMctsSimple pusoyCompletion(Collection<Card> toBeBoxed, long timeLimitMs) {
		Completor completor = new Completor();
		return completor.fantasyCompletion(Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), toBeBoxed, timeLimitMs, true);
	}

	public static void main(String[] args) {
//		System.out.println(Misc.sf("Oleg = %d", EvaluatorFacade.evaluate(Arrays.asList(Card.str2Cards("5d5sJs")), Arrays.asList(Card.str2Cards("4c7d7s8c9c")), Arrays.asList(Card.str2Cards("3h8h9hThQh")))));
//		System.out.println(Misc.sf("Suren = %d", EvaluatorFacade.evaluate(Arrays.asList(Card.str2Cards("4c8c9c")), Arrays.asList(Card.str2Cards("5d5s7d7sJs")), Arrays.asList(Card.str2Cards("3h8h9hThQh")))));
		long time = Utils.getTime();
//		for (int i=0; i<100; i++)
//		System.out.println(completion(Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Arrays.asList(Card.str2Cards("5d5sJs4c7d7s8c9c3h8h9hThQh"))).toEventOfc("hero"));
//		System.out.println(completion(Arrays.asList(Card.str2Cards("5d5s")), Collections.emptyList(), Collections.emptyList(), Arrays.asList(Card.str2Cards("Js4c7d7s8c9c3h8h9hThQh"))).toEventOfc("hero"));
//		System.out.println(completion(Arrays.asList(Card.str2Cards("5d5s")), Arrays.asList(Card.str2Cards("4c")), Arrays.asList(Card.str2Cards("3h8h")), Arrays.asList(Card.str2Cards("Js7d7s8c9c9hThQh"))).toEventOfc("hero"));
//		System.out.println(completion(Arrays.asList(Card.str2Cards("5hKhKd")), Arrays.asList(Card.str2Cards("")), Arrays.asList(Card.str2Cards("2cQd")), Arrays.asList(Card.str2Cards("ThTd6dAc8d4d8cQc"))).toEventOfc("hero"));
//		System.out.println(completion(Arrays.asList(Card.str2Cards("Qd")), Arrays.asList(Card.str2Cards("5hKhKd")), Arrays.asList(Card.str2Cards("2c")), Arrays.asList(Card.str2Cards("AdTdAcAsJhJcJsQc"))).toEventOfc("hero"));
		
//		System.out.println(completion(Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Arrays.asList(Card.str2Cards("9hQsKh2d2s6s7h8s4c6c7c8cTc4h"))).toEventOfc("hero"));		
//		System.out.println(completion(Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Arrays.asList(Card.str2Cards("2h2d2c3h3d3c5c6c7h9hJdQsKsAs"))).toEventOfc("hero"));		
//		System.out.println(completion(Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Arrays.asList(Card.str2Cards("2h3d5c6h6c6s7d8dTdTcJsQdKhAc"))).toEventOfc("hero"));		
//		System.out.println(completion(Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Arrays.asList(Card.str2Cards("2h2s4s5d6s7h7c8h9h9d9cTdJhAc")), true).toEventOfc("hero"));
//		System.out.println("Core count: " + Runtime.getRuntime().availableProcessors());
//		System.out.println(completion(Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Arrays.asList(Card.str2Cards("8d8sTh2c3h4d5h6d9dTcJhQcKd4s")), true).toEventOfc("hero"));
//		System.out.println(completion(Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Arrays.asList(Card.str2Cards("7sQhKdAs3h4d4s5d8h9h9d9c9sJs")), true).toEventOfc("hero"));
		//System.out.println(fantasyCompletion(Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Arrays.asList(Card.str2Cards("9hAdAs5c6s7s8d9d3c4c8cTcJc4dJd")), 10000).toEventOfc("hero"));
//		System.out.println(fantasyCompletion(Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Arrays.asList(Card.str2Cards("3h4c4s5h5s6d7d7s8h8d9h9c9sThTdAh")), 10000).toEventOfc("hero"));
//		System.out.println(EvaluatorFacade.evaluate(Arrays.asList(Card.str2Cards("KcAsKh")), Arrays.asList(Card.str2Cards("4h8c4c9cAc")), Arrays.asList(Card.str2Cards("ThTdTsQsQd")), false));
//		System.out.println(EvaluatorFacade.evaluate(Arrays.asList(Card.str2Cards("KcAs3h")), Arrays.asList(Card.str2Cards("4h8c4c9c2s")), Arrays.asList(Card.str2Cards("ThTd3sQsQd")), false));
//		System.out.println(EvaluatorFacade.evaluate(Arrays.asList(Card.str2Cards("KcAs3h")), Arrays.asList(Card.str2Cards("4h8c4cAc2s")), Arrays.asList(Card.str2Cards("ThTd3sQsQd")), false));
//		System.out.println(fantasyCompletion(Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Arrays.asList(Card.str2Cards("9hAdAs5c6s7s8d9d3c4c8cTcJc4dJd")), 10000).toEventOfc("hero"));
//		System.out.println(pusoyCompletion(Arrays.asList(Card.str2Cards("TdThAh6d6c2h2c9c3c3d3s4c4d")), 10000).toEventOfc("hero"));
//		System.out.println(pusoyCompletion(Arrays.asList(Card.str2Cards("AhAdAs6d7h8d9dTd4c5c7c9cQc7s9s")), 20000).toEventOfc("hero"));
//		System.out.println(pusoyCompletion(Arrays.asList(Card.str2Cards("TdTsKh8d8h6d6c7dAdAcAhQh9s")), 10000).toEventOfc("hero"));
//		System.out.println(pusoyCompletion(Arrays.asList(Card.str2Cards("AdJs3sTdTcQc6d2dAhKh6h5h3h")), 10000).toEventOfc("hero"));
//		System.out.println(pusoyCompletion(Arrays.asList(Card.str2Cards("Tc6c5h3c3hAdQh8cAsKsQs7s2s")), 10000).toEventOfc("hero"));
//		System.out.println(pusoyCompletion(Arrays.asList(Card.str2Cards("QdJc5cQcJsTd9c8sTh9h7h6h3h")), 10000).toEventOfc("hero"));
//		System.out.println(pusoyCompletion(Arrays.asList(Card.str2Cards("Tc8h4dKhQc9d5h3cKsTs6s4s3s")), 10000).toEventOfc("hero"));
		System.out.println(completion(Arrays.asList(Card.str2Cards("5d5s")), Arrays.asList(Card.str2Cards("4c")), Arrays.asList(Card.str2Cards("3h8h")), Arrays.asList(Card.str2Cards("Js5c7s8c9c9hThQhAcAhAsAd"))).toEventOfc("hero"));
		System.out.println(String.format("time = %d", Utils.getTime() - time));
//		time = Utils.getTime();
//		Config.FANTASY_SCORE = 5;
////		System.out.println(fantasyCompletion(Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Arrays.asList(Card.str2Cards("AhAdAs6d7h8d9dTd4c5c7c9cQc7s9s")), 20000).toEventOfc("hero"));
//		System.out.println(String.format("time = %d", Utils.getTime() - time));
//
	}
}
