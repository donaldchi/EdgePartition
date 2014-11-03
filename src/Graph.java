/**
 * Graph.java
 *
 * Reads an instance of a weighted graph and provides a calculation of the
 * fitness of a given partitioning.
 */

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class Graph
{
	private int numNodes;
	private ArrayList<HashMap<Integer,Double>> edgeList;

	/**
	 * read an instance of the graph problem from a given data file
	 *
	 * @param filename The name of the data file to read
	 *
	 * The format of the file is simple. The first line is just the number of
	 * nodes in the graph (n). After that, there are n lines, each consisting
	 * of the following format:
	 *
	 *     nodeNum numEdges d1 w1 d2 w2 ... dk wk
	 *
	 * That is, the first number is the node number. The next number is the
	 * number of edges connected to this node (k). Then, there are k pairs
	 * of numbers, the first being the number of the node it's connected to
	 * and the second the weight.
	 *
	 * All weights are positive.
	 */
	public void readGraphFile(String filename) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(filename));

		// the first line contains the number of nodes
		String line = br.readLine();
		numNodes = Integer.parseInt(line);
		edgeList = new ArrayList<HashMap<Integer,Double>>(numNodes);
		for(int i=0; i<numNodes; i++) {
			edgeList.add(i, new HashMap<Integer,Double>());
		}

		while((line = br.readLine()) != null) {
			String[] tokens = line.split("\\s");

			// skip blank lines
			if(tokens.length <= 1) {
				continue;
			}

			// first token is node number; second is number of edges for this node
			int nodeNum = Integer.parseInt(tokens[0]);
			int numEdges = Integer.parseInt(tokens[1]);

			// then for each edge, we have a tuple of destination node and weight
			for(int i=0; i<numEdges; i++) {
				int destNode = Integer.parseInt(tokens[2*i+2]);
				double weight = Double.parseDouble(tokens[2*i+3]);
				edgeList.get(nodeNum).put(destNode, weight);
			}
		}
	}



	/**
	 * return the number of nodes in the graph
	 */
	public int numberOfNodes() {
		return numNodes;
	}



	/**
	 * return a set of nodes connected to the given node
	 *
	 * @param node The number of the node to get connected edges from
	 */
	public Set<Integer> getEdgesFrom(int node) {
		return edgeList.get(node).keySet();
	}




    public ArrayList<HashMap<Integer,Double>> getNodeList(){
        return edgeList;
    }


	/**
	 * returns true if the graph has an edge from node src to node dest
	 */
	public boolean hasEdge(int src, int dest) {
		return edgeList.get(src).containsKey(dest);
	}



	/**
	 * return the weight of the edge from node src to node dest
	 */
	public double getWeight(int src, int dest) {
		return edgeList.get(src).get(dest);
	}


	/**
	 * calculate the total cost of a partitioning of the graph
	 *
	 * @param partitions array of partition numbers for each node
	 *
	 * You should feel free to adapt this code as needed. You can come up with a completely
	 * different representation if you prefer.
	 */

	public double partitionCost(ArrayList<Integer> partitions) {
		double cost = 0.0;
		// iterate over all edges in the graph, and if the edges connect nodes in
		// different partitions, add the edge weight to the total cost
		for(int src = 0; src < numNodes; src++) {
			for(Integer dest : getEdgesFrom(src)) {
				if(partitions.get(src) != partitions.get(dest)) {
					cost += getWeight(src, dest);
				}
			}
		}
		return cost/2.0; 		// we counted each edge twice 
	}
}

			
			
