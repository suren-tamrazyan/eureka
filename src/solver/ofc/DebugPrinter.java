package solver.ofc;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import solver.ofc.mcts.MctsCallback;
import solver.ofc.mcts.MctsTreeNode;

public class DebugPrinter implements MctsCallback {
	private List<String> csv = new ArrayList<>();  

	@Override
	public void onEndSearch(MctsTreeNode<?, ?, ?> root) {
		
		System.out.println("VisitCount");
		LinkedHashMap<String, Integer> sortedMapByCount = new LinkedHashMap<>();
		root.getChildNodes().stream().sorted(Comparator.comparingInt(MctsTreeNode<?, ?, ?>::getVisitCount).reversed()).forEachOrdered(x -> sortedMapByCount.put(((solver.ofc.GameOfcMctsSimple)x.getDeepCloneOfRepresentedState()).getStateStr(), x.getVisitCount()));
		for (Map.Entry<String, Integer> ent : sortedMapByCount.entrySet())
			System.out.println(String.format("%s: %d", ent.getKey(), ent.getValue()));
      
		System.out.println();
      
      	System.out.println("Reward");
      	LinkedHashMap<String, Double> sortedMapByReward = new LinkedHashMap<>();
      	root.getChildNodes().stream().sorted(Comparator.comparingDouble(MctsTreeNode<?, ?, ?>::getDomainTheoreticValue).reversed()).forEachOrdered(x -> sortedMapByReward.put(((solver.ofc.GameOfcMctsSimple)x.getDeepCloneOfRepresentedState()).getStateStr(), x.getDomainTheoreticValue()));
      	for (Map.Entry<String, Double> ent : sortedMapByReward.entrySet())
      		System.out.println(String.format("%s: %f", ent.getKey(), ent.getValue()));
      
      
      	// save csv file
      	System.out.println();
      	System.out.println("Save csv file");
      	String filename = String.format("D:\\develop\\temp\\poker\\Eureka\\debug\\hist-%s.csv", Utils.dateFormatIntel(Utils.getTime()));
      	try {
      		FileWriter writer = new FileWriter(filename);
      		writer.write(csv.stream().collect(Collectors.joining("\n")));
      		writer.close();
      		System.out.println(String.format("%s saved.", filename));
      	} catch (IOException e) {
      		e.printStackTrace();
      	}
      	
      	System.out.println();
	}

	@Override
	public void onIteration(int iterationsNum, long time, MctsTreeNode<?, ?, ?> root) {
		if (iterationsNum % 100 == 0) {
			if (csv.isEmpty())
				csv.add("epoch;branch;count;reward");
			root.getChildNodes().stream().forEach(x -> csv.add( String.format(java.util.Locale.US, "%d;%s;%d;%.2f", iterationsNum, ((solver.ofc.GameOfcMctsSimple)x.getDeepCloneOfRepresentedState()).getStateStr(), x.getVisitCount(), x.getDomainTheoreticValue()) ));
		}
	}

}
